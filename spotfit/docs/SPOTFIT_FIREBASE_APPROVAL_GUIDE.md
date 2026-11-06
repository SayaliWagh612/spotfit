# SpotFit — Firebase Console Manual Approval Guide (Step 15)

For the SpotFit prototype, platform admin operations are managed directly by project developers using the **Firebase Console**.

> [!IMPORTANT]
> **Console Trusted Writes Notice**: Firebase Console developer writes are trusted administrative console operations performed directly on the Cloud Firestore database using Firebase Console admin credentials or Admin SDKs. They are NOT performed through JavaFX Owner credentials, client REST requests, or client-side code.

---

## 1. Business Review & Approval

### A. Approving a Submitted Business
1. Open [Firebase Console](https://console.firebase.google.com/).
2. Select the SpotFit project and navigate to **Cloud Firestore** -> **`businesses`** collection.
3. Locate the target `businessId` document where `verificationStatus == "SUBMITTED"`.
4. Inspect business details (`businessName`, `businessEmail`, `businessPhone`, `addressLine`, `city`, `postalCode`).
5. Update document fields:
   - `verificationStatus`: `"APPROVED"`
   - `reviewedAt`: Current timestamp in milliseconds (e.g., `1772534400000`)
   - `rejectionReason`: `""` (empty string)
   - `updatedAt`: Current timestamp in milliseconds

### B. Rejecting a Submitted Business
1. Open the target `businessId` document in the **`businesses`** collection.
2. Update document fields:
   - `verificationStatus`: `"REJECTED"`
   - `reviewedAt`: Current timestamp in milliseconds
   - `rejectionReason`: `"Detailed explanation for rejection (e.g. Invalid postal code / incomplete address)."`
   - `updatedAt`: Current timestamp in milliseconds

---

## 2. Space Review & Publication

### A. Publishing a Submitted Space to LIVE
1. Navigate to **Cloud Firestore** -> **`spaces`** collection.
2. Locate the target `spaceId` document where `status == "SUBMITTED"`.
3. Verify that parent Business (`businessId`) is `verificationStatus == "APPROVED"`.
4. Check nested subcollections:
   - `/spaces/{spaceId}/resources` -> At least 1 active Resource.
   - `/spaces/{spaceId}/offerings` -> At least 1 active Offering.
   - `/spaces/{spaceId}/resources/{resourceId}/availabilityRules` -> Active weekly rules configured.
5. Update target Space document fields:
   - `status`: `"LIVE"`
   - `publishedAt`: Current timestamp in milliseconds
   - `reviewedAt`: Current timestamp in milliseconds
   - `rejectionReason`: `""`
   - `updatedAt`: Current timestamp in milliseconds
6. Update associated nested Offerings:
   - Navigate to `/spaces/{spaceId}/offerings/{offeringId}`.
   - Update `status`: `"ACTIVE"`.
   - Update `updatedAt`: Current timestamp in milliseconds.

### B. Rejecting a Submitted Space
1. Open the target `spaceId` document in the **`spaces`** collection.
2. Update document fields:
   - `status`: `"REJECTED"`
   - `reviewedAt`: Current timestamp in milliseconds
   - `rejectionReason`: `"Reason for rejection (e.g. Missing operational hours / no active equipment resource)."`
   - `updatedAt`: Current timestamp in milliseconds

---

## 3. Client Verification
After developers update Firestore documents via Firebase Console, the JavaFX Owner application will instantly display updated status cards and enable or restrict appropriate actions upon page refresh or re-navigation.
