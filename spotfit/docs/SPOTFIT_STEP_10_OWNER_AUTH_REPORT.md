# SpotFit Step 10 — Owner Authentication & Profile Integration Report

## Executive Summary

Step 10 completes the end-to-end asynchronous authentication, profile setup, and security foundation for **Space Owners** in SpotFit. All owner-related backend components integrate seamlessly with Firebase Authentication REST API and Cloud Firestore REST API while maintaining single-Stage JavaFX navigation and zero-blocking execution.

---

## 1. Domain Model (`Owner.java`)

The canonical `Owner` model represents space owner accounts in the `/owners/{ownerId}` collection:

* **Fields**: `ownerId`, `name`, `email`, `phone`, `role` (`"OWNER"`), `profileImageUrl`, `city`, `emailVerified`, `profileCompleted`, `active`, `createdAt`, `updatedAt`.
* **Immutability & Safety**: Default constructors set `role = UserRole.OWNER`, `active = true`, and timestamps.
* **Compatibility**: Provides helper methods and compatibility getters for seamless UI rendering and legacy settings interoperability.

---

## 2. Firestore Data Access & Service Layer

* **`OwnerDao.java`**: Implements non-blocking Firestore REST operations targeting `/owners/{ownerId}`:
  * `createOwner(Owner owner, String idToken)`
  * `getOwner(String ownerId, String idToken)`
  * `updateOwner(Owner owner, String idToken)`
  * `markEmailVerified(String ownerId, String idToken)`
  * `completeProfile(String ownerId, String idToken)`
* **`OwnerService.java`**: Coordinates business logic and validation:
  * Ensures `ownerId` is non-blank and matches session UID.
  * Validates email formatting and phone number bounds.
  * Guarantees role persistence as `"OWNER"`.

---

## 3. Owner Authentication Controller (`OwnerAuthController.java`)

Provides the central asynchronous workflow for Owner authentication:

```text
Space Owner View
       ↓
OwnerAuthController
       ↓
AuthService / OwnerService
       ↓
AuthDao / OwnerDao
       ↓
Firebase Auth REST / Firestore REST
```

### Key Capabilities

1. **Owner Registration**: Calls `signUp`, checks cross-role isolation, initializes `/owners/{uid}` document, and sends email verification link.
2. **Owner Login**: Calls `signIn`, verifies `/owners/{uid}` document, enforces active status and `"OWNER"` role, and synchronizes email verification state.
3. **Email Verification Refresh**: When email verification is confirmed, executes `authService.refreshToken()` to acquire a refreshed ID token containing `email_verified: true`, then updates Firestore and `AppSession`.
4. **Profile Completion**: Updates location/image details and flags `profileCompleted: true`.

---

## 4. Cross-Role Protection & Role Isolation

Strict architectural boundaries prevent role conversion between **Customer** (`/users`) and **Owner** (`/owners`):

### Protection Mechanisms

* **In `AuthController.java` (Customer Flow)**:
  * Registration checks if `/owners/{uid}` exists. If found, returns error: `"This account is registered as a SpotFit Owner. Please use Owner Login."`
  * Login missing-document recovery checks `/owners/{uid}` before creating a `/users/{uid}` profile, preventing owner account hijacking.
* **In `OwnerAuthController.java` (Owner Flow)**:
  * Registration checks if `/users/{uid}` exists. If found, returns error: `"This account is registered as a SpotFit Customer. Please use Customer Login."`
  * Login checks if `/users/{uid}` exists when `/owners/{uid}` is missing, preventing customer accounts from accessing the Owner Portal.

---

## 5. UI Integration & Single-Stage Navigation

* **`OwnerFlowRouter.java`**: Manages view navigation on `AppNavigator`'s single stage without creating secondary stages.
* **`OwnerEmailVerification.java`**: Interactive verification view featuring a 60-second cooldown timer for resend requests, manual verification status checking, and automatic token refresh upon link confirmation.
* **Views Connected**:
  * `SignInPage.java` -> Uses `OwnerAuthController.loginOwnerWithProfile()` and `OwnerFlowRouter`.
  * `SpaceOwnerRegistrationPage.java` -> Uses `OwnerAuthController.registerOwnerWithProfile()` and `OwnerFlowRouter`.
  * `OwnerProfileSetupPage.java` -> Pre-fills current profile state and finishes onboarding via `OwnerAuthController.completeOwnerProfile()`.
  * `OwnerShell.java` -> Displays live `AppSession.getName()` and supports session logout.

---

## 6. Security Rules (`firestore.rules`)

The updated local `firestore.rules` enforces UID ownership and field immutability:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /owners/{ownerId} {
      allow read: if request.auth != null && request.auth.uid == ownerId;
      allow create: if request.auth != null 
                    && request.auth.uid == ownerId 
                    && request.resource.data.role == "OWNER";
      allow update: if request.auth != null 
                    && request.auth.uid == ownerId
                    && request.resource.data.role == resource.data.role
                    && request.resource.data.ownerId == resource.data.ownerId;
      allow delete: if false;
    }
  }
}
```

> **Mandatory Developer Action**: Copy the published security rules above and publish them in the Firebase Console under **Firestore Database > Rules** before executing live production tests.

---

## 7. Verification Summary

* **Build Verification**: `mvn compile` successfully compiled 178 Java source files with zero errors.
* **Architecture Compliance**:
  * Single Stage maintained (`Main` -> `AppNavigator`).
  * Non-blocking execution preserved (all network operations return `CompletableFuture`).
  * Cross-role account conversion blocked.
