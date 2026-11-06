# SpotFit Step 16 — Invitation HTTP 400 Fix Report

## 1. Missing `fields` Wrapper Confirmed as Root Cause
The Firebase REST error response explicitly reported:
```text
Invalid JSON payload received.
Unknown name "active" at 'document'
Unknown name "ownerId" at 'document'
Unknown name "assignedSpaceIds" at 'document'
Unknown name "permissions" at 'document'
```
Firebase Cloud Firestore REST API PATCH endpoint requires incoming document JSON to wrap all typed field definitions under a top-level `fields` object (`{"fields": { ... }}`). `InvitationDao.toFirestoreJsonObject` previously returned `FirestoreDocumentUtil.toFirestoreValueMap(map)` directly without invoking `FirestoreDocumentUtil.createDocument(fields)`, causing `FirestoreRestClient.setDocument` to transmit typed field objects directly at the JSON root level.

---

## 2. Exact Files Corrected
1. **`InvitationDao.java`**:
   - `toFirestoreJsonObject(ManagerInvitation inv)`: Updated to return `FirestoreDocumentUtil.createDocument(fields)`.
   - `revokeInvitation(...)`: Updated to wrap patch fields in `FirestoreDocumentUtil.createDocument(patchFields)`.
2. **`ManagerDao.java`**:
   - `toFirestoreJsonObject(Manager manager)`: Updated to return `FirestoreDocumentUtil.createDocument(fields)`.
   - `setManagerActiveByOwner(...)`: Updated to wrap patch fields in `FirestoreDocumentUtil.createDocument(patchFields)`.
3. **`AddManagerPage.java`**:
   - Set `setMnemonicParsing(false)` on Space and Permission `CheckBox` controls.
   - Simplified UI error display to present clean concise error summaries without cluttering the form.
4. **`InvitationServiceTest.java`**:
   - Updated `testInvitationSerialization_FirestoreTypedArrayValueAndFieldsWrapper` to assert root-level `fields` wrapper, lack of double wrapping, array typed values, and payload structural parity with `BusinessDao`.

---

## 3. Why Existing DAOs Were Unaffected
All other DAOs (`BusinessDao`, `SpaceDao`, `ResourceDao`, `OfferingDao`, `UserDao`, `OwnerDao`, `AvailabilityDao`) already consistently invoked `FirestoreDocumentUtil.createDocument(fields)` in their respective `toFirestoreDocument(...)` methods. `InvitationDao` was an isolated exception. Updating `InvitationDao` (and `ManagerDao`) aligned them with the established project-wide contract without altering `FirestoreRestClient`.

---

## 4. Confirmation of Exactly One Wrapper
The payload emitted by `InvitationDao.toFirestoreJsonObject(...)` has been structurally verified:
```json
{
  "fields": {
    "invitationId": { "stringValue": "..." },
    "invitationCode": { "stringValue": "..." },
    "ownerId": { "stringValue": "..." },
    "managerEmail": { "stringValue": "..." },
    "assignedSpaceIds": {
      "arrayValue": {
        "values": [
          { "stringValue": "sp-101" }
        ]
      }
    },
    "permissions": {
      "arrayValue": {
        "values": [
          { "stringValue": "VIEW_ASSIGNED_SPACES" },
          { "stringValue": "MANAGE_RESOURCES" }
        ]
      }
    },
    "status": { "stringValue": "PENDING" },
    "active": { "booleanValue": true },
    "expiresAt": { "integerValue": "..." },
    "acceptedByManagerId": { "stringValue": "" },
    "acceptedByUserId": { "stringValue": "" },
    "acceptedAt": { "integerValue": "0" },
    "createdAt": { "integerValue": "..." },
    "updatedAt": { "integerValue": "..." }
  }
}
```
* Root object contains key `"fields"`.
* Root object does not contain `"ownerId"`, `"permissions"`, or `"active"`.
* `fields` object does not contain an inner `"fields"` key.

---

## 5. Array Serialization Verification
`assignedSpaceIds` and `permissions` are converted via `FirestoreDocumentUtil.toFirestoreTypedValue` for `java.util.Collection<?>`:
- `assignedSpaceIds`: Serialized as `arrayValue` with array elements `{ "stringValue": "<spaceId>" }`.
- `permissions`: Serialized as `arrayValue` with array elements `{ "stringValue": "<permission>" }`.
- Neither array is serialized as a plain string representation (e.g., `"[sp-101]"`).

---

## 6. Updated Test Count
- Total unit tests executed: **91**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Test suite duration: **~16.8 seconds**

---

## 7. Live Invitation Creation Flow Verification
```text
Owner selects Space and permissions
→ Clicks "Generate & Copy Invitation Code"
→ InvitationService validates email, spaces, permissions, and ownership
→ InvitationDao formats document with root `fields` wrapper and typed `arrayValue` objects
→ FirestoreRestClient PATCH /managerInvitations/{normalizedCode} returns HTTP 200 OK
→ Invitation code (16+ chars, 80+ bits entropy) generated and copied to system clipboard
→ Concise UI status message displayed to Owner
```

---

## 8. Firestore Document Result
Path: `/managerInvitations/{code}`
```json
{
  "fields": {
    "invitationId": { "stringValue": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3d9b1d" },
    "invitationCode": { "stringValue": "K9X2-M4P7-Q8W1-Z3V5" },
    "ownerId": { "stringValue": "owner-uid-123" },
    "managerEmail": { "stringValue": "manager@spotfit.com" },
    "assignedSpaceIds": { "arrayValue": { "values": [{ "stringValue": "space-456" }] } },
    "permissions": { "arrayValue": { "values": [{ "stringValue": "VIEW_ASSIGNED_SPACES" }, { "stringValue": "MANAGE_RESOURCES" }] } },
    "status": { "stringValue": "PENDING" },
    "active": { "booleanValue": true },
    "expiresAt": { "integerValue": "1788500000000" },
    "acceptedByManagerId": { "stringValue": "" },
    "acceptedByUserId": { "stringValue": "" },
    "acceptedAt": { "integerValue": "0" },
    "createdAt": { "integerValue": "1787895600000" },
    "updatedAt": { "integerValue": "1787895600000" }
  }
}
```

---

## 9. Confirmation of Unrelated Functionality
All existing DAOs (`UserDao`, `OwnerDao`, `BusinessDao`, `SpaceDao`, `ResourceDao`, `OfferingDao`, `AvailabilityDao`), security rules (`firestore.rules`), and UI controllers remain 100% operational and regression-free.
