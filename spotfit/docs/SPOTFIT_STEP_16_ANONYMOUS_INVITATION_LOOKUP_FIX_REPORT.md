# SpotFit Step 16 — Anonymous Pre-Registration Invitation Lookup Fix Report

## Executive Summary
This report documents the resolution of the pre-registration error (`ID token is required for Firestore operation`) when validating Manager invitations before Firebase Authentication account creation.

---

## 1. Root Cause Analysis
- **Circular Dependency**: `ManagerAuthController.registerManager(...)` attempts to validate invitation readiness before creating a Firebase Auth user.
- **Defect**: Pre-signup validation previously called `invitationService.getInvitationByCode(code, null)`, which invoked `FirestoreRestClient.getDocument(...)`. This method calls `validateAndBuildUrl(...)`, enforcing `idToken != null && !idToken.isBlank()`.
- **Failure**: Passing `null` resulted in immediate failure with the error `"ID token is required for Firestore operation."` before any HTTP request was made.

---

## 2. Technical Solution Overview

### A. Low-Level Anonymous GET (`FirestoreRestClient.java`)
- Implemented package-private method:
  `CompletableFuture<OperationResult<JSONObject>> getPublicManagerInvitation(String invitationCode)`
- Hard-restricted collection name to `CollectionName.MANAGER_INVITATIONS`.
- Omits `Authorization` header completely (no `Bearer` header sent).
- URL-encodes path segments and validates input against path traversal characters via `validatePathSegment`.
- Never logs invitation codes, emails, UIDs, tokens, API keys, or raw documents.

### B. Dedicated DAO Access (`InvitationDao.java`)
- Added:
  `CompletableFuture<OperationResult<ManagerInvitation>> getInvitationForRegistration(String invitationCode)`
- Normalizes invitation code via `ManagerInvitation.normalizeCode(...)`.
- Calls `restClient.getPublicManagerInvitation(docId)`.
- Does NOT accept arbitrary collection names or weaken authenticated methods (`getInvitationByCode(code, idToken)` remains authenticated).

### C. Pre-Signup Validation & Security Rules
- Updated `InvitationService.validateInvitationForRegistration(...)` to invoke `getInvitationForRegistration(normalizedCode)`.
- Enforces 11 pre-signup validation rules:
  1. Invitation document exists
  2. Document code matches requested normalized code
  3. `status == PENDING`
  4. `active == true`
  5. `expiresAt > current time`
  6. Form email matches `managerEmail`
  7. `ownerId` is non-blank
  8. `assignedSpaceIds` is non-empty
  9. `permissions` is non-empty
  10. `acceptedAt == 0`
  11. `acceptedByManagerId` is blank/null
- Maps HTTP 403 / 404 / PERMISSION_DENIED to user-safe invalid invitation message:
  `"This invitation is invalid, expired, already used, or does not match the entered email."`
- Updated `/managerInvitations/{code}` in `firestore.rules` to allow unauthenticated GET **only** for `status == "PENDING" && active == true && expiresAt > request.time.toMillis()`. Authenticated collection listing remains permitted (`allow list: if signedIn();`).

### D. UI Progress Updates (`Registration.java`)
- Register button disables immediately upon click.
- Status text updates sequentially (`Validating invitation...`, `Creating account...`, `Sending verification email...`).
- Re-enables button safely on recoverable error while preserving field inputs.

---

## 3. Automated Test Verification
- **New Unit Test**: `FirestoreRestClientTest.java` (3 test cases verifying header omission, blank code rejection, and path segment validation).
- **Controller Unit Tests**: `ManagerAuthControllerTest.java` (12 test cases verifying pre-signup validation, `EMAIL_EXISTS` mapping, Customer/Owner rejection, unverified email checks, and atomic activation).
- **Total Test Suite Executed**: 106 tests.
- **Failures / Errors**: 0.
- **Build Result**: `BUILD SUCCESS`.
