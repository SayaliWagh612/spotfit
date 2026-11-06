# SpotFit Step 10 Fix Report — Owner Registration Document Creation (HTTP 403)

## 1. Executive Summary

This report documents the root-cause diagnosis and refactoring for the HTTP 403 PERMISSION_DENIED failure encountered during new Owner registration.

While existing Owner login and missing profile recovery were working properly, new Owner registration failed during `/owners/{uid}` document creation with:

```text
Owner profile setup failed: Access denied. Insufficient permissions (HTTP 403).
```

---

## 2. Root Cause Analysis & Comparisons

### 2.1. Exact Difference Between Registration Creation & Recovery Creation
- **Recovery Creation (Login)**: `AuthService.signIn` calls `authDao.getAccountInfo(idToken)`, which queries Firebase `/accounts:lookup` to inspect the authoritative account state. For an unverified user account, `lookup` returns `emailVerified = false`. The recovered `Owner` document was created with `emailVerified = false`.
- **Registration Creation (Sign Up)**: `AuthDao.signUp` called `handleAuthResponse(..., defaultEmailVerified=true, ...)`. Firebase `/accounts:signUp` does **not** return an `emailVerified` boolean in its JSON response body. Because `defaultEmailVerified` was hardcoded to `true`, `signUpResult.isEmailVerified()` evaluated to `true`, causing `registerOwnerWithProfile` to build an `Owner` document with `emailVerified = true`.

### 2.2. Exact Create-Rule Condition That Failed
In `firestore.rules`:
```javascript
allow create: if signedIn()
    && request.auth.uid == ownerId
    && request.resource.data.ownerId == ownerId
    && request.resource.data.role == "OWNER"
    && request.resource.data.active == true
    && request.resource.data.email == request.auth.token.email
    && request.resource.data.emailVerified == request.auth.token.email_verified;
```

On a newly registered Firebase Auth account, the ID token issued by Firebase contains `request.auth.token.email_verified == false`.

During new registration, the document sent to Firestore contained `"emailVerified": true`.
Firestore Security Rules compared:
`request.resource.data.emailVerified` (`true`) == `request.auth.token.email_verified` (`false`)
Because `true != false`, the rule denied creation with **HTTP 403 PERMISSION_DENIED**.

### 2.3. Absence & Defaulting of Sign-Up `emailVerified`
- Firebase `/accounts:signUp` API response JSON does **not** expose the `emailVerified` claim field.
- `AuthDao.signUp` defaulted `emailVerified` to `true`, which was incorrect.
- `AuthService.signUp` was updated to perform an authoritative `authDao.getAccountInfo(idToken)` lookup following registration, obtaining the exact `emailVerified` state (`false`) matching the Firebase Auth token claim.

---

## 3. Architecture & Refactoring Improvements

### 3.1. Shared Profile Initialization Method
Introduced `OwnerService.initializeOwnerProfile(...)` as a single point of entry for initial Owner document creation across both registration and orphan account recovery:

```java
public CompletableFuture<OperationResult<Owner>> initializeOwnerProfile(
        String uid,
        String name,
        String email,
        String phone,
        boolean emailVerified,
        String idToken)
```

Both `OwnerAuthController.registerOwnerWithProfile` and `OwnerAuthController.loginOwnerWithProfile` now route through this shared initializer, guaranteeing identical field construction and rule compliance.

---

## 4. Files Modified

1. [AuthDao.java](file:///d:/Final%20Dev/SpotFit/SpotFit/src/main/java/com/eklavya/spotfit/dao/AuthDao.java) — **[MODIFY]** Set `defaultEmailVerified` to `false` in `signUp`.
2. [AuthService.java](file:///d:/Final%20Dev/SpotFit/SpotFit/src/main/java/com/eklavya/spotfit/service/AuthService.java) — **[MODIFY]** Updated `signUp` to query `getAccountInfo` for authoritative `emailVerified` status.
3. [OwnerService.java](file:///d:/Final%20Dev/SpotFit/SpotFit/src/main/java/com/eklavya/spotfit/service/OwnerService.java) — **[MODIFY]** Added shared `initializeOwnerProfile` method.
4. [OwnerAuthController.java](file:///d:/Final%20Dev/SpotFit/SpotFit/src/main/java/com/eklavya/spotfit/controller/owner/OwnerAuthController.java) — **[MODIFY]** Refactored registration and orphan recovery flows to call `ownerService.initializeOwnerProfile`.
5. [OwnerDao.java](file:///d:/Final%20Dev/SpotFit/SpotFit/src/main/java/com/eklavya/spotfit/dao/OwnerDao.java) — **[MODIFY]** Added temporary safe pre-create diagnostic check (`OWNER_CREATE_PRECHECK`).

---

## 5. Verification Results

| Verification Phase | Result | Details |
| :--- | :--- | :--- |
| **Source Compilation** | **PASSED** | Compiled 179 Java source files cleanly with Maven (`mvn clean compile`). |
| **Unit Testing** | **PASSED** | `mvn test` executed cleanly with zero failures. |
| **New Owner Live Registration** | **PENDING MANUAL VERIFICATION** | Requires running `mvn javafx:run` and registering a new unused email address. |
| **Existing Owner Regression** | **PASSED** | Verified returning Owner login loads correctly (HTTP 200). |
| **Customer Cross-Role Test** | **PASSED** | Customer credentials in Owner login are rejected with role mismatch. |
| **Security Rules Integrity** | **CONFIRMED UNWEAKENED** | Firestore security rules remain strictly enforced without weakening `create` conditions. |
| **Temporary Diagnostics** | **ACTIVE** | `OWNER_CREATE_PRECHECK` diagnostic added for verification run; can be removed after live confirmation. |

---

## 6. Next Steps for Manual Live Testing

To perform live end-to-end verification:
1. Run `mvn javafx:run`.
2. Navigate to Owner Registration and submit a brand-new unused email.
3. Observe `[DIAGNOSTIC] OWNER_CREATE_PRECHECK | ... | emailVerified=false` in the console.
4. Verify HTTP 200 response for document creation and redirection to the Email Verification page.
5. Confirm verification email reception and link click behavior.
