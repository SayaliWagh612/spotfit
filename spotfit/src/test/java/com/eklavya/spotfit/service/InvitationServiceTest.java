package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationServiceTest {

    private InvitationService invitationService;
    private Space validSpace;

    @BeforeEach
    public void setUp() {
        invitationService = new InvitationService();
        validSpace = new Space();
        validSpace.setSpaceId("sp-101");
        validSpace.setOwnerId("owner-001");
        validSpace.setName("Test Space");
        validSpace.setActive(true);
    }

    @Test
    public void testCreateInvitation_SuccessValidation() throws ExecutionException, InterruptedException {
        List<String> assignedSpaces = List.of("sp-101");
        List<String> perms = ManagerPermission.getDefaultPermissions();

        OperationResult<ManagerInvitation> res = invitationService.createInvitation(
                "owner-001",
                "manager@example.com",
                assignedSpaces,
                perms,
                List.of(validSpace),
                "mock-token"
        ).get();

        assertNotNull(res);
        assertNotEquals("INVALID_EMAIL", res.getErrorCode());
        assertNotEquals("ZERO_SPACES", res.getErrorCode());
    }

    @Test
    public void testCreateInvitation_InvalidEmail_ReturnsError() throws ExecutionException, InterruptedException {
        OperationResult<ManagerInvitation> res = invitationService.createInvitation(
                "owner-001",
                "invalid-email",
                List.of("sp-101"),
                ManagerPermission.getDefaultPermissions(),
                List.of(validSpace),
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("INVALID_EMAIL", res.getErrorCode());
    }

    @Test
    public void testCreateInvitation_EmptySpaces_ReturnsError() throws ExecutionException, InterruptedException {
        OperationResult<ManagerInvitation> res = invitationService.createInvitation(
                "owner-001",
                "manager@example.com",
                List.of(),
                ManagerPermission.getDefaultPermissions(),
                List.of(validSpace),
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("ZERO_SPACES", res.getErrorCode());
    }

    @Test
    public void testCreateInvitation_UnownedSpace_ReturnsError() throws ExecutionException, InterruptedException {
        OperationResult<ManagerInvitation> res = invitationService.createInvitation(
                "owner-001",
                "manager@example.com",
                List.of("sp-999"), // Unowned space ID
                ManagerPermission.getDefaultPermissions(),
                List.of(validSpace),
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("FOREIGN_SPACE_REJECTED", res.getErrorCode());
    }

    @Test
    public void testCreateInvitation_EmptyPermissions_ReturnsError() throws ExecutionException, InterruptedException {
        OperationResult<ManagerInvitation> res = invitationService.createInvitation(
                "owner-001",
                "manager@example.com",
                List.of("sp-101"),
                List.of(),
                List.of(validSpace),
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("ZERO_PERMISSIONS", res.getErrorCode());
    }

    @Test
    public void testInvitationSerialization_FirestoreTypedArrayValueAndFieldsWrapper() {
        ManagerInvitation invitation = ManagerInvitation.createDefault(
                "owner-001",
                "manager@example.com",
                List.of("sp-101"),
                ManagerPermission.getDefaultPermissions()
        );

        org.json.JSONObject payload = com.eklavya.spotfit.dao.InvitationDao.toFirestoreJsonObject(invitation);
        assertNotNull(payload);

        // 1. Assert exactly one 'fields' wrapper at root level
        assertTrue(payload.has("fields"), "Payload must contain root 'fields' wrapper");
        assertFalse(payload.has("ownerId"), "Root payload must not contain document fields directly");
        assertFalse(payload.has("permissions"), "Root payload must not contain document fields directly");

        org.json.JSONObject fields = payload.getJSONObject("fields");
        assertFalse(fields.has("fields"), "Payload must not contain double 'fields' wrapper");
        assertTrue(fields.has("ownerId"));
        assertTrue(fields.has("permissions"));
        assertTrue(fields.has("assignedSpaceIds"));
        assertTrue(fields.has("active"));

        // 2. Assert permissions and assignedSpaceIds are serialized as arrayValue
        org.json.JSONObject spaceIdsObj = fields.getJSONObject("assignedSpaceIds");
        assertTrue(spaceIdsObj.has("arrayValue"), "assignedSpaceIds must be serialized as arrayValue");
        
        org.json.JSONArray values = spaceIdsObj.getJSONObject("arrayValue").getJSONArray("values");
        assertEquals(1, values.length());
        assertEquals("sp-101", values.getJSONObject(0).getString("stringValue"));

        org.json.JSONObject permsObj = fields.getJSONObject("permissions");
        assertTrue(permsObj.has("arrayValue"), "permissions must be serialized as arrayValue");

        // 3. Compare payload shape against Business model payload shape
        com.eklavya.spotfit.model.Business business = new com.eklavya.spotfit.model.Business();
        business.setBusinessId("biz-101");
        business.setOwnerId("owner-001");

        org.json.JSONObject businessPayload = com.eklavya.spotfit.util.FirestoreDocumentUtil.createDocument(
                com.eklavya.spotfit.util.FirestoreDocumentUtil.toFirestoreValueMap(
                        new org.json.JSONObject()
                                .put("businessId", business.getBusinessId())
                                .put("ownerId", business.getOwnerId())
                )
        );

        assertEquals(payload.keySet(), businessPayload.keySet(), "Invitation payload shape must match Business payload structure");
    }

    @Test
    public void testPermissionDisplayLabels() {
        assertEquals("View Assigned Spaces", ManagerPermission.getDisplayLabel(ManagerPermission.VIEW_ASSIGNED_SPACES));
        assertEquals("Manage Resources", ManagerPermission.getDisplayLabel(ManagerPermission.MANAGE_RESOURCES));
    }

    @Test
    public void testInvitationModel_CodeNormalizationAndEntropy() {
        ManagerInvitation invitation = ManagerInvitation.createDefault(
                "owner-001",
                "manager@example.com",
                List.of("sp-101"),
                ManagerPermission.getDefaultPermissions()
        );

        assertNotNull(invitation.getInvitationId());
        assertNotNull(invitation.getInvitationCode());
        assertTrue(invitation.getInvitationCode().length() >= 16);
        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        assertTrue(invitation.isActive());
        assertTrue(invitation.getExpiresAt() > System.currentTimeMillis());
        assertFalse(invitation.isExpired());
    }
}
