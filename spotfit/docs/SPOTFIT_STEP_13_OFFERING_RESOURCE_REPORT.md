# SpotFit Step 13 Architectural Report — Nested Offering and Resource Backend

## 1. Executive Summary
This report documents the architectural design, implementation details, and verification results for Step 13 of the SpotFit platform: **Nested Offering and Resource Persistence Linked to Spaces**.

Resources and Offerings are structured as Firestore subcollections directly under their parent Space document:
- `/spaces/{spaceId}/resources/{resourceId}`
- `/spaces/{spaceId}/offerings/{offeringId}`

Root collection persistence under `/resources` and `/offerings` has been completely eliminated.

---

## 2. Hierarchy & Domain Structure

```text
Owner (Authenticated UID)
└── Business (businessId)
    └── Space (spaceId)
        ├── Subcollection: /resources/{resourceId}
        └── Subcollection: /offerings/{offeringId}
```

### Key Rules Enforced:
1. **Space Ownership Authorization**: Resources and Offerings cannot exist independently of a Space.
2. **Context Derivation**: `spaceId`, `businessId`, and `ownerId` are automatically derived from the parent Space document and `AppSession`.
3. **Space Immutability**: Service layer prevents moving a Resource or Offering between parent Spaces during updates.
4. **Compatible Resource Requirement**: Creating or updating an Offering requires at least one active compatible Resource with matching `resourceType` in the parent Space.
5. **Exact Paise Price Conversion**: Offering prices are submitted as formatted text strings (e.g. `"1000"` or `"999.50"`), validated with `BigDecimal` with a max scale of 2, and converted to integer paise (`100000` paise).

---

## 3. Class-Level Responsibilities

### Data Access Layer (`com.eklavya.spotfit.dao`)
- **`FirestoreRestClient`**: Generic REST client extended with subcollection helper methods:
  - `getNestedDocument(parentCol, parentId, childCol, childId, idToken)`
  - `setNestedDocument(parentCol, parentId, childCol, childId, doc, idToken)`
  - `updateNestedDocument(parentCol, parentId, childCol, childId, doc, updateFields, idToken)`
  - `listNestedDocuments(parentCol, parentId, childCol, idToken)`
  - Encodes path parameters and enforces validation preventing path traversal or blank segments.
- **`ResourceDao`**: Interacts exclusively with `/spaces/{spaceId}/resources/{resourceId}`.
- **`OfferingDao`**: Interacts exclusively with `/spaces/{spaceId}/offerings/{offeringId}`.

### Service Layer (`com.eklavya.spotfit.service`)
- **`ResourceService`**:
  - Enforces `spaceId` path/model matching and immutability.
  - Sanitizes user input and validates `resourceType`, `resourceCode`, and `capacity`.
  - Rejects manual status changes to `RESERVED` or `OCCUPIED`.
- **`OfferingService`**:
  - Enforces exact paise price conversion and compatibility matrix validation (e.g. `SLOT` -> `ROOM`, `SEAT` -> `SEAT`, `WORKSHOP` -> `WORKSHOP_AREA`/`ROOM`).
  - Ensures duration order constraint: `minimum <= default <= maximum`.
  - Restricts edits to `DRAFT` offerings only.

### Controller Layer (`com.eklavya.spotfit.controller.owner`)
- **`ResourceController`**: Verifies Owner session (`AppSession`) and Space ownership before calling Service methods.
- **`OfferingController`**: Verifies Owner session, parent Space ownership, and compatible resource presence before creating or updating Offerings.

---

## 4. Security Rules (`firestore.rules`)

```javascript
    // Nested Resource Subcollection Security Rules (/spaces/{spaceId}/resources/{resourceId})
    match /spaces/{spaceId}/resources/{resourceId} {
      function parentSpace() {
        return get(/databases/$(database)/documents/spaces/$(spaceId));
      }

      function ownsParentSpace() {
        return signedIn() && parentSpace().data.ownerId == request.auth.uid;
      }

      allow get, list: if ownsParentSpace();

      allow create: if ownsParentSpace()
        && request.resource.data.resourceId == resourceId
        && request.resource.data.spaceId == spaceId
        && request.resource.data.ownerId == parentSpace().data.ownerId
        && request.resource.data.businessId == parentSpace().data.businessId
        && request.resource.data.status == "AVAILABLE"
        && request.resource.data.active == true;

      allow update: if ownsParentSpace()
        && request.resource.data.resourceId == resource.data.resourceId
        && request.resource.data.spaceId == resource.data.spaceId
        && request.resource.data.spaceId == spaceId
        && request.resource.data.ownerId == resource.data.ownerId
        && request.resource.data.ownerId == parentSpace().data.ownerId
        && request.resource.data.businessId == resource.data.businessId
        && request.resource.data.businessId == parentSpace().data.businessId
        && request.resource.data.createdAt == resource.data.createdAt
        && request.resource.data.active == resource.data.active
        && request.resource.data.status in ["AVAILABLE", "MAINTENANCE", "BLOCKED"];

      allow delete: if false;
    }

    // Nested Offering Subcollection Security Rules (/spaces/{spaceId}/offerings/{offeringId})
    match /spaces/{spaceId}/offerings/{offeringId} {
      function parentSpace() {
        return get(/databases/$(database)/documents/spaces/$(spaceId));
      }

      function ownsParentSpace() {
        return signedIn() && parentSpace().data.ownerId == request.auth.uid;
      }

      allow get, list: if ownsParentSpace();

      allow create: if ownsParentSpace()
        && request.resource.data.offeringId == offeringId
        && request.resource.data.spaceId == spaceId
        && request.resource.data.ownerId == parentSpace().data.ownerId
        && request.resource.data.businessId == parentSpace().data.businessId
        && request.resource.data.status == "DRAFT"
        && request.resource.data.active == true;

      allow update: if ownsParentSpace()
        && request.resource.data.offeringId == resource.data.offeringId
        && request.resource.data.spaceId == resource.data.spaceId
        && request.resource.data.spaceId == spaceId
        && request.resource.data.ownerId == resource.data.ownerId
        && request.resource.data.ownerId == parentSpace().data.ownerId
        && request.resource.data.businessId == resource.data.businessId
        && request.resource.data.businessId == parentSpace().data.businessId
        && request.resource.data.createdAt == resource.data.createdAt
        && request.resource.data.active == resource.data.active
        && request.resource.data.status == resource.data.status
        && resource.data.status == "DRAFT";

      allow delete: if false;
    }
```

---

## 5. Automated Verification Results

All 39 unit tests executed cleanly with 0 failures and 0 errors:
- `OfferingControllerTest`: 2 tests passed
- `ResourceControllerTest`: 2 tests passed
- `SpaceControllerTest`: 3 tests passed
- `BusinessServiceTest`: 5 tests passed
- `OfferingServiceTest`: 11 tests passed
- `ResourceServiceTest`: 8 tests passed
- `SpaceServiceTest`: 8 tests passed

---

## 6. Status
**COMPLETED AND VERIFIED**
