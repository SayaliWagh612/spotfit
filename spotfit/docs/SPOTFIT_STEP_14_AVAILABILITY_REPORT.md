# SpotFit Step 14 — Technical Report: Resource Availability Rules, Blocks, and Slot Generation

## Executive Summary
SpotFit Step 14 implements a complete resource availability and scheduling engine operating directly on top of nested subcollections under `/spaces/{spaceId}/resources/{resourceId}`.

The engine computes available time slots dynamically in memory from weekly recurring rules minus one-time exception blocks. Computed slots are **never persisted** to Firestore.

---

## 1. Subcollection Persistence Schema

```text
/spaces/{spaceId}
└── /resources/{resourceId}
    ├── /availabilityRules/{ruleId}   (ruleId = uppercase weekday e.g. MONDAY)
    └── /availabilityBlocks/{blockId} (blockId = random UUID)
```

### 1.1 AvailabilityRule Model (`/availabilityRules/{ruleId}`)
* `ruleId` (`String`): uppercase weekday (`MONDAY`, `TUESDAY`, ..., `SUNDAY`)
* `spaceId` (`String`): parent Space ID
* `resourceId` (`String`): parent Resource ID
* `ownerId` (`String`): authenticated Owner UID
* `dayOfWeek` (`String`): uppercase weekday
* `startTime` (`String`): strict `HH:mm` format (e.g., `"09:00"`)
* `endTime` (`String`): strict `HH:mm` format (e.g., `"18:00"`)
* `slotIntervalMinutes` (`int`): e.g., `60`
* `minimumBookingMinutes` (`int`): e.g., `60`
* `maximumBookingMinutes` (`int`): e.g., `240`
* `active` (`boolean`): true/false
* `createdAt` (`long`): epoch ms
* `updatedAt` (`long`): epoch ms

### 1.2 AvailabilityBlock Model (`/availabilityBlocks/{blockId}`)
* `blockId` (`String`): UUID
* `spaceId` (`String`): parent Space ID
* `resourceId` (`String`): parent Resource ID
* `ownerId` (`String`): authenticated Owner UID
* `startAt` (`long`): epoch ms
* `endAt` (`long`): epoch ms
* `reason` (`String`): non-blank reason description
* `status` (`String`): `ACTIVE` or `CANCELLED` (soft cancellation)
* `createdAt` (`long`): epoch ms
* `updatedAt` (`long`): epoch ms

### 1.3 AvailableSlot Model (In-Memory Computed)
* `spaceId` (`String`)
* `resourceId` (`String`)
* `startAt` (`long`): epoch ms
* `endAt` (`long`): epoch ms
* `localStartTime` (`String`): `HH:mm`
* `localEndTime` (`String`): `HH:mm`
* `available` (`boolean`): true

---

## 2. Dynamic Slot Generation Algorithm

1. Retrieve target `Space`, `Resource`, `LocalDate`, `AvailabilityRule` for that weekday, and list of `AvailabilityBlock` objects.
2. Determine `ZoneId` from `Space.getTimeZoneId()` (defaults to `Asia/Kolkata`).
3. If rule is `null` or `!rule.isActive()`, return empty slot list.
4. Construct `ZonedDateTime` for `startTime` and `endTime` on `LocalDate`.
5. Step forward from `ruleStartEpoch` to `ruleEndEpoch` by `slotIntervalMinutes`.
6. Skip any candidate slot where `candidateStartEpoch < nowEpochMs` (past slots omitted).
7. For remaining candidate slots, check overlap against all `ACTIVE` blocks using interval overlap formula:
   `candidateStart < blockEnd && candidateEnd > blockStart`
   *Boundary-touching blocks (`candidateEnd == blockStart` or `candidateStart == blockEnd`) are NOT overlaps.*
8. Collect available slots and sort ascending by `startAt`.

---

## 3. Timezone Support (`Space.timeZoneId`)
* Added `timeZoneId` field to `Space` model and `SpaceDao` serialization.
* Defaults to `Asia/Kolkata` if missing or blank.
* Validated via `ZoneId.of(...)`.
* Slot boundary calculation interprets local time strings in the Space's timezone.

---

## 4. UI Architecture & Navigation

* **Entry Point**: `SpaceDetailsPage` → Resource card → `[ Manage Availability ]` button.
* **ManageAvailabilityPage**:
  * Read-only Context Header: Space Name, Resource Name, Code, Timezone.
  * **Tab 1 — Weekly Schedule**: Monday–Sunday rule editors + `[ Use Space Operating Hours ]` convenience button.
  * **Tab 2 — Blocked Periods**: Block creation form (Date Pickers + HH:mm input + Reason) + real blocks table with `Cancel Block` action.
  * **Tab 3 — Slot Preview**: Date Picker + computed slot list.

---

## 5. Security & Verification

* `firestore.rules` updated with subcollection rules under `/spaces/{spaceId}/resources/{resourceId}/availabilityRules/{ruleId}` and `/availabilityBlocks/{blockId}`.
* Ownership verified via `ownsResource()` requiring authenticated Owner UID matching parent Space and parent Resource `ownerId`.
* **Automated Unit Tests**: 61 total unit tests passing in Maven test execution (18 Availability tests covering rule validation, block conflicts, timezone handling, day normalization, non-operating day rejection, bulk operations, and partial failure detection).
* **Static Analysis**: Verified single `Application` class (`Main.java`), zero `new Stage()` instantiations, zero blocking calls, and zero Firebase Admin SDK imports.

---

## 6. Step 14 Bug Fix — Schedule Initialization & Operating Days

### Root Cause
1. **Generic Weekday Assumption**: "Use Space Operating Hours" previously assumed Monday–Friday, causing validation failures for Spaces configured with custom operating days (e.g. Wednesday–Friday).
2. **Unchecked Day Validation**: Unchecked days triggered active rule validation ("outside operating days") instead of being treated as inactive or unconfigured.
3. **Day Value Formatting**: Unnormalized mixed-case string comparisons (e.g. `Monday` vs `MONDAY`) led to day matching mismatches.
4. **Sidebar Navigation Key**: Navigating to `ManageAvailabilityPage` passed no custom sidebar key, defaulting to `businesses` instead of `myspaces`.

### Resolutions Applied
1. **Actual Space Operating Days**:
   - `space.getOperatingDays()`, `space.getOpeningTime()`, and `space.getClosingTime()` are dynamically read and normalized.
   - Bulk save creates rules ONLY for valid Space operating days.
2. **Day Value Normalization**:
   - Implemented `WeekDay.normalizeDay(String value)` converting `Monday`, `monday`, `MON`, `MONDAY` to `MONDAY`.
   - Applied in `Space.getOperatingDays()`, `AvailabilityService.saveWeeklyRule()`, `validateWeeklyRuleInputs()`, and `ManageAvailabilityPage`.
3. **Unchecked Day & Deactivation Behavior**:
   - Unchecked days do not construct active rules, display no error messages, and trigger no writes unless deactivating an existing active rule (`active = false`).
   - If no rule exists in Firestore for an unchecked day, no write is attempted and no document is created.
4. **Bulk Save & Partial Failure Handling**:
   - `applySpaceOperatingHoursToResource()` tracks individual day write futures.
   - Disables the bulk save button during execution, reports progress, and returns `BULK_SAVE_PARTIAL_FAILURE` if any day write fails.
5. **Sidebar Key Correction**:
   - Updated `ResourceFlowRouter` and `OwnerShell.showCustomPage` to pass `"myspaces"` as the active sidebar key for all Resource availability views.

