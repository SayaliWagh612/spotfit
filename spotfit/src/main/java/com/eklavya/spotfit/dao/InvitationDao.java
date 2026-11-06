package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for ManagerInvitation documents stored at:
 * /managerInvitations/{normalizedInvitationCode}
 */
public class InvitationDao {

    private final FirestoreRestClient restClient;

    public InvitationDao() {
        this.restClient = new FirestoreRestClient();
    }

    public InvitationDao(FirestoreRestClient restClient) {
        this.restClient = (restClient != null) ? restClient : new FirestoreRestClient();
    }

    /**
     * Persists a new invitation under /managerInvitations/{normalizedInvitationCode}.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> createInvitation(ManagerInvitation invitation, String idToken) {
        if (invitation == null) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_INVITATION", "Invitation cannot be null."));
        }
        String docId = ManagerInvitation.normalizeCode(invitation.getInvitationCode());
        if (docId.isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        JSONObject docJson = toFirestoreJsonObject(invitation);

        return restClient.setDocument(CollectionName.MANAGER_INVITATIONS, docId, docJson, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(invitation, "Manager invitation created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Performs a direct GET for /managerInvitations/{normalizedInvitationCode}.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationByCode(String invitationCode, String idToken) {
        String docId = ManagerInvitation.normalizeCode(invitationCode);
        if (docId.isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        return restClient.getDocument(CollectionName.MANAGER_INVITATIONS, docId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        ManagerInvitation inv = fromFirestoreJsonObject(res.getData());
                        if (inv != null) {
                            return OperationResult.success(inv);
                        } else {
                            return OperationResult.failure("PARSE_ERROR", "Failed to parse ManagerInvitation.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Performs an anonymous direct GET for /managerInvitations/{normalizedInvitationCode}
     * exclusively for pre-registration validation.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationForRegistration(String invitationCode) {
        String docId = ManagerInvitation.normalizeCode(invitationCode);
        if (docId.isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        return restClient.getPublicManagerInvitation(docId)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        ManagerInvitation inv = fromFirestoreJsonObject(res.getData());
                        if (inv != null) {
                            return OperationResult.success(inv);
                        } else {
                            return OperationResult.failure("PARSE_ERROR", "Failed to parse ManagerInvitation.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Queries /managerInvitations for invitations belonging to an Owner.
     */
    public CompletableFuture<OperationResult<List<ManagerInvitation>>> getOwnerInvitations(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_OWNER_ID", "Owner ID cannot be blank."));
        }

        return restClient.queryDocuments(CollectionName.MANAGER_INVITATIONS, "ownerId", ownerId.trim(), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<ManagerInvitation> list = new ArrayList<>();
                        for (JSONObject docJson : res.getData()) {
                            ManagerInvitation inv = fromFirestoreJsonObject(docJson);
                            if (inv != null) {
                                list.add(inv);
                            }
                        }
                        return OperationResult.success(list);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Revokes an existing invitation by setting its status to REVOKED.
     */
    public CompletableFuture<OperationResult<Void>> revokeInvitation(String invitationCode, String idToken) {
        String docId = ManagerInvitation.normalizeCode(invitationCode);
        if (docId.isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        JSONObject updates = new JSONObject()
                .put("status", InvitationStatus.REVOKED)
                .put("updatedAt", System.currentTimeMillis());

        JSONObject patchFields = FirestoreDocumentUtil.toFirestoreValueMap(updates);
        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(patchFields);

        return restClient.updateDocument(CollectionName.MANAGER_INVITATIONS, docId, patchDoc, List.of("status", "updatedAt"), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(null, "Invitation revoked successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public static JSONObject toFirestoreJsonObject(ManagerInvitation inv) {
        JSONObject map = new JSONObject();
        map.put("invitationId", inv.getInvitationId() != null ? inv.getInvitationId() : "");
        map.put("invitationCode", inv.getInvitationCode() != null ? inv.getInvitationCode() : "");
        map.put("ownerId", inv.getOwnerId() != null ? inv.getOwnerId() : "");
        map.put("managerEmail", inv.getManagerEmail() != null ? inv.getManagerEmail() : "");
        map.put("assignedSpaceIds", inv.getAssignedSpaceIds() != null ? inv.getAssignedSpaceIds() : List.of());
        map.put("permissions", inv.getPermissions() != null ? inv.getPermissions() : List.of());
        map.put("status", inv.getStatus() != null ? inv.getStatus() : InvitationStatus.PENDING);
        map.put("active", inv.isActive());
        map.put("expiresAt", inv.getExpiresAt());
        map.put("acceptedByManagerId", inv.getAcceptedByManagerId() != null ? inv.getAcceptedByManagerId() : "");
        map.put("acceptedByUserId", inv.getAcceptedByUserId());
        map.put("acceptedAt", inv.getAcceptedAt());
        map.put("createdAt", inv.getCreatedAt());
        map.put("updatedAt", inv.getUpdatedAt());

        JSONObject fields = FirestoreDocumentUtil.toFirestoreValueMap(map);
        return FirestoreDocumentUtil.createDocument(fields);
    }

    public static ManagerInvitation fromFirestoreJsonObject(JSONObject docJson) {
        if (docJson == null || !docJson.has("fields")) {
            return null;
        }

        JSONObject fields = docJson.optJSONObject("fields");
        if (fields == null) {
            return null;
        }

        String invitationId = getStringField(fields, "invitationId");
        String invitationCode = getStringField(fields, "invitationCode");
        String ownerId = getStringField(fields, "ownerId");
        String managerEmail = getStringField(fields, "managerEmail");

        List<String> assignedSpaceIds = getArrayField(fields, "assignedSpaceIds");
        List<String> permissions = getArrayField(fields, "permissions");

        String status = getStringField(fields, "status");
        boolean active = getBooleanField(fields, "active", true);
        long expiresAt = getLongField(fields, "expiresAt");
        String acceptedByManagerId = getStringField(fields, "acceptedByManagerId");
        if (acceptedByManagerId.isBlank()) {
            acceptedByManagerId = getStringField(fields, "acceptedByUserId");
        }
        long acceptedAt = getLongField(fields, "acceptedAt");
        long createdAt = getLongField(fields, "createdAt");
        long updatedAt = getLongField(fields, "updatedAt");

        return new ManagerInvitation(
                invitationId, invitationCode, ownerId, managerEmail,
                assignedSpaceIds, permissions, status, active, expiresAt,
                acceptedByManagerId, acceptedAt, createdAt, updatedAt
        );
    }

    private static String getStringField(JSONObject fields, String name) {
        if (fields.has(name)) {
            JSONObject obj = fields.getJSONObject(name);
            if (obj.has("stringValue")) {
                return obj.getString("stringValue");
            }
        }
        return "";
    }

    private static boolean getBooleanField(JSONObject fields, String name, boolean defaultValue) {
        if (fields.has(name)) {
            JSONObject obj = fields.getJSONObject(name);
            if (obj.has("booleanValue")) {
                return obj.getBoolean("booleanValue");
            }
        }
        return defaultValue;
    }

    private static long getLongField(JSONObject fields, String name) {
        if (fields.has(name)) {
            JSONObject obj = fields.getJSONObject(name);
            if (obj.has("integerValue")) {
                try {
                    return Long.parseLong(obj.getString("integerValue"));
                } catch (NumberFormatException e) {
                    return obj.getLong("integerValue");
                }
            }
        }
        return 0L;
    }

    private static List<String> getArrayField(JSONObject fields, String name) {
        List<String> list = new ArrayList<>();
        if (fields.has(name)) {
            JSONObject obj = fields.getJSONObject(name);
            if (obj.has("arrayValue") && obj.getJSONObject("arrayValue").has("values")) {
                JSONArray values = obj.getJSONObject("arrayValue").getJSONArray("values");
                for (int i = 0; i < values.length(); i++) {
                    JSONObject item = values.getJSONObject(i);
                    if (item.has("stringValue")) {
                        list.add(item.getString("stringValue"));
                    }
                }
            }
        }
        return list;
    }
}
