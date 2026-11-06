# SpotFit Step 8 — Firestore User Backend Report

## 1. Objective
Implement a reusable, asynchronous Cloud Firestore REST foundation for SpotFit and create the complete backend layers (`User`, `FirestoreDocumentUtil`, `FirestoreRestClient`, `UserDao`, `UserService`, `firestore.rules`) for managing customer User profiles without requiring Firebase Admin SDK, service-account credentials, or blocking network calls.

## 2. User Model
The canonical `User` class (`com.eklavya.spotfit.model.User`) encapsulates all Customer profile attributes:
- **Core Identity**: `userId`, `name`, `email`, `phone`, `role` (defaults to `UserRole.USER`), `profileImageUrl`, `active` (defaults to `true`).
- **Preferences & Onboarding**: `city`, `preferredLanguage`, `personas` (`List<String>`), `interests` (`List<String>`), `budgetPreferencePaise`, `preferredDistanceKm`, `notificationsEnabled` (defaults to `true`), `emailVerified`, `onboardingCompleted`.
- **Metadata**: `createdAt`, `updatedAt`.
- **Factory & Helpers**: Includes `User.createNewUser(...)` convenience factory and `hasCompletedOnboarding()` helper method. `personas` and `interests` lists are defensively initialized to non-null lists.

## 3. Firestore Typed-Value Conversion
`FirestoreDocumentUtil` (`com.eklavya.spotfit.util.FirestoreDocumentUtil`) converts standard Java types to and from Cloud Firestore REST API typed JSON (`stringValue`, `integerValue`, `doubleValue`, `booleanValue`, `arrayValue`):
- **Write Helpers**: `stringValue()`, `integerValue()`, `doubleValue()`, `booleanValue()`, `stringArrayValue()`.
- **Read Helpers**: `getString()`, `getLong()`, `getDouble()`, `getBoolean()`, `getStringList()`.
- **Document Wrappers**: `createDocument()` and `getFields()` handle top-level `"fields"` JSON structure defensively without throwing `NullPointerException`.

## 4. FirestoreRestClient
`FirestoreRestClient` (`com.eklavya.spotfit.dao.FirestoreRestClient`) provides non-blocking, asynchronous HTTP REST communication using Java 17 `HttpClient`:
- **Operations**: `getDocument`, `setDocument` (PUT/PATCH upsert), `updateDocument` (PATCH with `updateMask.fieldPaths`), `deleteDocument`.
- **Authentication**: Sends `Authorization: Bearer {ID_TOKEN}` header with every request.
- **Error Handling**: Maps HTTP status codes (401, 403, 404, 409, 429, 5xx) and transport exceptions (timeouts, connection failures) to clean `OperationResult<T>` failure codes without exposing raw tokens or response bodies.

## 5. UserDao
`UserDao` (`com.eklavya.spotfit.dao.UserDao`) handles persistence logic for the `/users/{userId}` collection (`CollectionName.USERS`):
- **Methods**: `createUser`, `getUser`, `updateBasicProfile`, `updatePersonas`, `updateInterests`, `updateLocationPreferences`, `updateEmailVerification`, `completeOnboarding`.
- **Document Path**: `/users/{Firebase UID}` (strict identity mapping).
- **Serialization**: `toFirestoreDocument` and `fromFirestoreDocument` map model fields using `FirestoreDocumentUtil`.

## 6. UserService
`UserService` (`com.eklavya.spotfit.service.UserService`) provides business-level validation and orchestration before delegating to `UserDao`:
- **Validations**: Enforces non-blank user ID, valid ID token, name, email regex format, phone format, non-empty persona/interest lists, city, positive distance, and non-negative budget.
- **Methods**: `createInitialProfile`, `getUser`, `updateBasicProfile`, `savePersonas`, `saveInterests`, `saveLocationPreferences`, `markEmailVerified`, `completeOnboarding`.

## 7. Security Rules
Project-root `firestore.rules` defines Firestore Rules Version 2:
- **Default Deny**: All unspecified paths default to `allow read, write: if false;`.
- **User Document Policy** (`/users/{userId}`):
  - Read: Allowed if `request.auth.uid == userId`.
  - Create: Allowed if `request.auth.uid == userId`, `role == "USER"`, `active == true`, and `userId` matches path.
  - Update: Allowed if `request.auth.uid == userId` and immutable fields (`userId`, `role`, `createdAt`, `active`) are unchanged.
  - Delete: Denied.

## 8. Threading and Security
- **Threading**: Zero synchronous HTTP calls (`httpClient.sendAsync()` used exclusively). No `future.get()` or `future.join()` calls.
- **Security**: No tokens, authorization headers, passwords, or full private documents are printed or logged. Zero JavaFX imports in model, utility, DAO, or service layers.

## 9. Files Created
1. `src/main/java/com/eklavya/spotfit/model/User.java`
2. `src/main/java/com/eklavya/spotfit/util/FirestoreDocumentUtil.java`
3. `src/main/java/com/eklavya/spotfit/dao/FirestoreRestClient.java`
4. `src/main/java/com/eklavya/spotfit/dao/UserDao.java`
5. `src/main/java/com/eklavya/spotfit/service/UserService.java`
6. `firestore.rules`
7. `docs/SPOTFIT_STEP_8_FIRESTORE_USER_BACKEND_REPORT.md`

## 10. Existing Files Modified
None.

## 11. Build Result
- **Command**: `mvn clean compile`
- **Result**: `BUILD SUCCESS`
- **Source Count**: 171 Java source files compiled with 0 errors.

## 12. Runtime Regression
- **Command**: `mvn javafx:run`
- **Result**: Application launches cleanly via `Main`, primary single-Stage navigation remains functional, and existing UI flows operate without issue.

## 13. Live-Test Status
- **Status**: Structural verification complete. Live CRUD integration against Firestore will be exercised when onboarding Views are connected in Step 9.

## 14. Manual Rule-Publication Instructions
To activate the security rules in Firebase:
1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Navigate to **Build → Firestore Database → Rules**.
3. Copy the contents of `firestore.rules` from the project root.
4. Paste into the Rules editor and click **Publish**.
*(Alternatively, deploy via Firebase CLI: `firebase deploy --only firestore:rules`)*.

## 15. Deferred UI Integration
User UI views (`Register`, `PersonaSelection`, `LocationPreferences`, `OnboardingComplete`, `UserDash`) remain unchanged in Step 8 and will be wired to `UserService` in Step 9.

## 16. Final Verdict
STEP 8 VERIFIED STRUCTURALLY
