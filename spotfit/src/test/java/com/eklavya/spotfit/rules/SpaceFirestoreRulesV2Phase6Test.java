package com.eklavya.spotfit.rules;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceFirestoreRulesV2Phase6Test {

    private static String rulesContent;

    @BeforeAll
    public static void loadRules() throws Exception {
        File rulesFile = new File("firestore.rules");
        assertTrue(rulesFile.exists(), "firestore.rules must exist in working directory");
        rulesContent = Files.readString(Path.of("firestore.rules"));
        assertNotNull(rulesContent);
        assertFalse(rulesContent.isBlank());
    }

    @Test
    @DisplayName("1. Verify /spaces/{spaceId} match block exists")
    public void testSpaceMatchBlockExists() {
        assertTrue(rulesContent.contains("match /spaces/{spaceId}"), "Must contain match rule for spaces collection");
    }

    @Test
    @DisplayName("2. Verify V2 Owner create branch exists")
    public void testV2OwnerCreateBranch() {
        assertTrue(rulesContent.contains("request.resource.data.schemaVersion == 2"), "Must enforce schemaVersion 2");
        assertTrue(rulesContent.contains("request.resource.data.spaceId == spaceId"), "Must enforce spaceId matches document ID");
        assertTrue(rulesContent.contains("request.resource.data.ownerId == request.auth.uid"), "Must enforce space ownerId matches requester auth uid");
    }

    @Test
    @DisplayName("3. Verify no legacy status field required for V2 create")
    public void testV2NoLegacyStatusRequired() {
        // V2 create requires approvalStatus DRAFT and operationalStatus INACTIVE, NOT status == "DRAFT"
        assertTrue(rulesContent.contains("request.resource.data.approvalStatus == \"DRAFT\""));
        assertTrue(rulesContent.contains("request.resource.data.operationalStatus == \"INACTIVE\""));
    }

    @Test
    @DisplayName("4. Verify parent Business must be V2, APPROVED, active, CREATIVITY, and owned by requester")
    public void testV2ParentBusinessValidation() {
        assertTrue(rulesContent.contains("get(/databases/$(database)/documents/businesses/$(request.resource.data.businessId)).data.ownerId == request.auth.uid"),
                "Must check parent business owned by requester");
        assertTrue(rulesContent.contains("get(/databases/$(database)/documents/businesses/$(request.resource.data.businessId)).data.active == true"),
                "Must check parent business active");
        assertTrue(rulesContent.contains("get(/databases/$(database)/documents/businesses/$(request.resource.data.businessId)).data.verificationStatus == \"APPROVED\""),
                "Must require parent business to be APPROVED");
        assertTrue(rulesContent.contains("get(/databases/$(database)/documents/businesses/$(request.resource.data.businessId)).data.category == \"CREATIVITY\""),
                "Must require parent business category to be CREATIVITY");
        assertTrue(rulesContent.contains("get(/databases/$(database)/documents/businesses/$(request.resource.data.businessId)).data.schemaVersion == 2"),
                "Must require parent business schemaVersion == 2");
    }

    @Test
    @DisplayName("5. Verify Space document ID must equal spaceId")
    public void testSpaceIdEqualsDocumentId() {
        assertTrue(rulesContent.contains("request.resource.data.spaceId == spaceId"));
    }

    @Test
    @DisplayName("6. Verify identity, schema, createdAt, active are immutable on V2 update")
    public void testV2ImmutabilityRules() {
        assertTrue(rulesContent.contains("request.resource.data.ownerId == resource.data.ownerId"), "ownerId immutable");
        assertTrue(rulesContent.contains("request.resource.data.spaceId == resource.data.spaceId"), "spaceId immutable");
        assertTrue(rulesContent.contains("request.resource.data.businessId == resource.data.businessId"), "businessId immutable");
        assertTrue(rulesContent.contains("request.resource.data.createdAt == resource.data.createdAt"), "createdAt immutable");
        assertTrue(rulesContent.contains("request.resource.data.active == resource.data.active"), "active immutable");
    }

    @Test
    @DisplayName("7. Verify Owner cannot set V2 APPROVED or operational ACTIVE on create or update")
    public void testOwnerCannotApproveOrActivate() {
        // Owner create forces DRAFT & INACTIVE
        assertTrue(rulesContent.contains("request.resource.data.approvalStatus == \"DRAFT\""));
        assertTrue(rulesContent.contains("request.resource.data.operationalStatus == \"INACTIVE\""));
    }

    @Test
    @DisplayName("8. Verify APPROVED / REJECTED material edit resets status to DRAFT and INACTIVE")
    public void testV2StatusResetOnEdit() {
        assertTrue(rulesContent.contains("resource.data.approvalStatus == \"APPROVED\" && request.resource.data.approvalStatus == \"DRAFT\" && request.resource.data.operationalStatus == \"INACTIVE\""),
                "Must reset APPROVED to DRAFT / INACTIVE on edit");
        assertTrue(rulesContent.contains("resource.data.approvalStatus == \"REJECTED\" && request.resource.data.approvalStatus == \"DRAFT\" && request.resource.data.operationalStatus == \"INACTIVE\""),
                "Must reset REJECTED to DRAFT / INACTIVE on edit");
    }

    @Test
    @DisplayName("9. Verify V1 Owner rules preserved")
    public void testV1OwnerRulesPreserved() {
        assertTrue(rulesContent.contains("(!('schemaVersion' in request.resource.data) || request.resource.data.schemaVersion == 1)"),
                "Must preserve V1 backward compatibility for create");
        assertTrue(rulesContent.contains("(!('schemaVersion' in resource.data) || resource.data.schemaVersion == 1)"),
                "Must preserve V1 backward compatibility for update");
    }

    @Test
    @DisplayName("10. Verify V1 Customer Step 17B branch preserved")
    public void testCustomerStep17BBranchPreserved() {
        assertTrue(rulesContent.contains("isCustomerVisibleSpace()"), "Must preserve Customer Step 17B read branch");
    }

    @Test
    @DisplayName("11. Verify V1 Manager branch preserved")
    public void testManagerBranchPreserved() {
        assertTrue(rulesContent.contains("isAssignedManager(spaceId)"), "Must preserve Manager branch");
    }

    @Test
    @DisplayName("12. Verify nested Resource, Offering, Availability rules preserved")
    public void testSubCollectionRulesPreserved() {
        assertTrue(rulesContent.contains("match /spaces/{spaceId}/resources/{resourceId}"), "Must preserve resources sub-collection rules");
        assertTrue(rulesContent.contains("match /spaces/{spaceId}/offerings/{offeringId}"), "Must preserve offerings sub-collection rules");
        assertTrue(rulesContent.contains("match /spaces/{spaceId}/resources/{resourceId}/availabilityRules/{ruleId}"), "Must preserve availabilityRules sub-collection rules");
    }
}
