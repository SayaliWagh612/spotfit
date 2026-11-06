# SpotFit Owner V2 — Phase 4 Backend Implementation Report

## Overview
This report documents the completion of **Phase 4: Space DAO, Service, and Owner Controller Backend** for the SpotFit Owner V2 architecture. 

Phase 4 establishes the authoritative V2 backend persistence pipeline:
$$\text{Owner Controller} \longrightarrow \text{SpaceService} \longrightarrow \text{SpaceDao} \longrightarrow \text{FirestoreRestClient}$$

All operations enforce strict schema version isolation (`schemaVersion == 2`), owner session validation, authoritative business loading, deep configuration matrix validation, and defensive update-mask sanitization. Legacy V1 documents (`schemaVersion == 1`) remain 100% backward compatible and are never silently mutated or auto-upgraded.

---

## 1. Architectural Changes Summary

### 1.1 `SpaceController.java` (Controller Layer)
- **Session & Role Validation**: Every V2 entry point (`createV2Space`, `loadV2Space`, `updateV2Space`) validates `AppSession.isAuthenticated()` and `AppSession.isOwner()`. Returns `OperationCode.AUTHENTICATION_REQUIRED` or `ROLE_MISMATCH` immediately on failure.
- **`createV2Space(businessId, submittedSpace)`**: Validates inputs, extracts `authenticatedOwnerId = AppSession.getUserId()` and `idToken = AppSession.getIdToken()`, delegating to `SpaceService.createV2Space(...)`.
- **`loadV2Space(spaceId)`**: Fetches the space asynchronously, verifies that `ownerId` matches `AppSession.getUserId()` (returning `PERMISSION_DENIED` on mismatch) and that `schemaVersion == 2` (returning `SCHEMA_VERSION_MISMATCH` for V1 documents).
- **`updateV2Space(spaceId, submittedSpace)`**: Authoritatively fetches the existing space, verifies owner matching and `schemaVersion == 2`, and delegates to `SpaceService.updateV2Space(...)`.

### 1.2 `SpaceService.java` (Business Domain Layer)
- **Authoritative Business Loading**: `createV2Space` loads the parent `Business` from `BusinessDao` using the submitted `businessId` and active `idToken`. Rejects creation if the parent business is missing, not V2, not owned by the authenticated owner, not in `APPROVED` verification status, or not in the `CREATIVITY` category.
- **Deep Domain Validation (`validateV2Space`)**: Validates space location (6-digit postal code), operating hours (24h format `HH:mm`, opening before closing), capacity (> 0), timezone, and category/activity-group alignment with the parent business.
- **6 V2 Booking Configuration Matrix**:
  1. `SEAT`: Validates `SeatBookingConfig` (min/max duration, guests, `SeatSelectionMode`, pricing unit `SEAT`/`HOUR`/`FLAT`).
  2. `SLOT`: Validates `SlotBookingConfig` (min/max duration, slot interval, buffer before/after, max people, pricing unit `HOUR`/`SESSION`/`FLAT`).
  3. `WORKSHOP`: Validates `WorkshopBookingConfig` (`pricePerPersonPaise == basePricePaise`, min/max participants, registration deadline hours, pricing unit `PERSON`/`SESSION`/`FLAT`).
  4. `RENTAL`: Validates `RentalBookingConfig` (quantity > 0, min/max rental hours, delivery radius/charge, pricing unit `DAY`/`HOUR`/`FLAT`).
  5. `VENUE`: Validates `VenueBookingConfig` (min booking hours, setup/cleanup buffer, allowed event types, pricing unit `EVENT`/`DAY`/`HOUR`/`FLAT`).
  6. `PRACTICE_ROOM`: Validates `PracticeRoomBookingConfig` (min/max duration, equipment options, pricing unit `HOUR`/`SESSION`/`FLAT`).
  - Rejects `BookingType.RESOURCE` with an explicit error.
- **Defensive Lifecycle State Machine & Resets**:
  - Editing a `SUBMITTED` or `UNDER_REVIEW` space is rejected (`PERMISSION_DENIED`).
  - Editing an `APPROVED` space resets `approvalStatus` to `DRAFT`, `operationalStatus` to `INACTIVE`, and `status` to `DRAFT` if material fields (`spaceName`, `description`, `location`, `capacity`, `pricing`, `bookingConfig`, `operatingHours`) are changed.
  - Editing a `REJECTED` space resets `approvalStatus` to `DRAFT` and clears `rejectionReason`.

### 1.3 `SpaceDao.java` (Persistence Layer)
- **Firestore Key Mapping**: Serializes V2 documents using canonical Firestore key `activityType` (never `v2ActivityType`).
- **Integer Currency**: Serializes all currency fields (`basePricePaise`, `securityDepositPaise`, `pricePerPersonPaise`, `deliveryChargePaise`, `cleaningFeePaise`) strictly as `integerValue` (avoiding floating-point representation).
- **Dual-Schema Deserialization**:
  - `schemaVersion` missing or `1` $\rightarrow$ Deserialized as V1 `Space` object (preserving all V1 fields and defaulting `schemaVersion` to `1`).
  - `schemaVersion == 2` $\rightarrow$ Deserialized as V2 `Space` object with full typed value models (`SpaceLocation`, `OperatingDayConfig`, polymorphic `BookingConfig`).
- **Update Mask Allowlist & Sanitization**: `updateV2Space` strictly enforces an update mask allowlist. Rejects path-like fields (e.g. `location/addressLine`) and immutable system fields (`spaceId`, `businessId`, `ownerId`, `schemaVersion`, `createdAt`) with `INVALID_ARGUMENT`.

---

## 2. Regression Test Suite & Verification Results

### 2.1 Test Execution Summary
- **Total Executed Tests**: **253**
- **Failures**: **0**
- **Errors**: **0**
- **Skipped**: **0**
- **Build Status**: `BUILD SUCCESS`

### 2.2 Test Breakdown
| Module / Component | Test File | Test Count | Result |
| :--- | :--- | :---: | :---: |
| Owner V1/V2 Base Models | `OwnerV2Phase1Test` | 7 | PASS |
| Business V2 Foundation | `BusinessV2Phase2Test` | 30 | PASS |
| Space V2 Domain Model | `SpaceV2Phase3Test` | 44 | PASS |
| **Space V2 DAO Persistence** | `SpaceDaoV2Phase4Test` | **15** | PASS |
| **Space V2 Domain Service** | `SpaceServiceV2Phase4Test` | **15** | PASS |
| **Space V2 Owner Controller** | `SpaceControllerV2Phase4Test` | **16** | PASS |
| Existing System Regression Suite | *All Legacy Tests* | 126 | PASS |
| **Total Test Suite** | | **253** | **PASS** |

---

## 3. Key Safety & Architectural Rules Enforced
1. **Zero Silent Upgrades**: Reading or saving a V1 document never alters its `schemaVersion` or converts it into V2.
2. **Authoritative Session Boundaries**: Client-submitted `ownerId` and `businessId` are ignored; owner identity is strictly extracted from `AppSession.getUserId()` and `BusinessDao` lookup.
3. **No Unchecked Maps**: All V2 configurations rely on typed Java value objects (`SlotBookingConfig`, `SeatBookingConfig`, `SpaceLocation`, `OperatingDayConfig`).
4. **Synchronous Timestamps**: Application-captured single `currentTimeMillis` values are used for atomic creation/update tracking (`createdAt`, `updatedAt`).

---

## 4. Verification Checklist
- [x] All 6 V2 booking types tested for round-trip serialization and domain validation.
- [x] V1 documents default to `schemaVersion == 1` and preserve existing fields.
- [x] Authoritative Business validation rejects unapproved, non-V2, or mismatched parent businesses.
- [x] Defensive reset machine reverts `APPROVED` spaces to `DRAFT` upon material edits.
- [x] Full test suite of 253 tests passes cleanly via `mvn clean test`.
