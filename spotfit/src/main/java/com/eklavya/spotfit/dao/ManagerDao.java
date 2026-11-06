package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.config.FirebaseConfig;
import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for Manager profile documents stored at:
 * /managers/{managerId}
 */
public class ManagerDao {

    private final FirestoreRestClient restClient;

    public ManagerDao() {
        this.restClient = new FirestoreRestClient();
    }

    public ManagerDao(FirestoreRestClient restClient) {
        this.restClient = (restClient != null) ? restClient : new FirestoreRestClient();
    }

    /**
     * Atomically creates the /managers/{uid} document AND updates the /managerInvitations/{code} status
     * to ACCEPTED in a single Firestore commit.
     */
    public CompletableFuture<OperationResult<Manager>> createManagerAndAcceptInvitationAtomically(
            Manager manager,
            ManagerInvitation invitation,
            String idToken) {

        if (manager == null || manager.getManagerId() == null || manager.getManagerId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER", "Manager and Manager ID cannot be null or blank."));
        }
        if (invitation == null || invitation.getInvitationCode() == null || invitation.getInvitationCode().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION", "Invitation and Invitation Code cannot be null or blank."));
        }

        String projectId = FirebaseConfig.getProjectId();
        String basePath = "projects/" + projectId + "/databases/(default)/documents/";

        String managerDocPath = basePath + CollectionName.MANAGERS + "/" + manager.getManagerId();
        String invDocId = ManagerInvitation.normalizeCode(invitation.getInvitationCode());
        String invitationDocPath = basePath + CollectionName.MANAGER_INVITATIONS + "/" + invDocId;

        long now = System.currentTimeMillis();

        // 1. Create Manager write payload with exists=false precondition
        JSONObject managerFields = toFirestoreJsonObject(manager).getJSONObject("fields");
        JSONObject managerWrite = new JSONObject()
                .put("update", new JSONObject()
                        .put("name", managerDocPath)
                        .put("fields", managerFields))
                .put("currentDocument", new JSONObject().put("exists", false));

        // 2. Update Invitation write payload (PENDING -> ACCEPTED)
        JSONObject invUpdates = new JSONObject()
                .put("status", InvitationStatus.ACCEPTED)
                .put("active", false)
                .put("acceptedByManagerId", manager.getManagerId())
                .put("acceptedByUserId", manager.getManagerId())
                .put("acceptedAt", now)
                .put("updatedAt", now);

        JSONObject invFields = FirestoreDocumentUtil.toFirestoreValueMap(invUpdates);

        JSONObject invitationWrite = new JSONObject()
                .put("update", new JSONObject()
                        .put("name", invitationDocPath)
                        .put("fields", invFields))
                .put("updateMask", new JSONObject().put("fieldPaths", new JSONArray()
                        .put("status")
                        .put("active")
                        .put("acceptedByManagerId")
                        .put("acceptedByUserId")
                        .put("acceptedAt")
                        .put("updatedAt")))
                .put("currentDocument", new JSONObject().put("exists", true));

        JSONArray writes = new JSONArray()
                .put(managerWrite)
                .put(invitationWrite);

        return restClient.commitBatchWrites(writes, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(manager, "Manager account activated and invitation accepted atomically.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), "Atomic Manager activation failed: " + res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Manager>> createManager(Manager manager, String idToken) {
        if (manager == null || manager.getManagerId() == null || manager.getManagerId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER", "Manager and Manager ID cannot be null or blank."));
        }
        JSONObject doc = toFirestoreJsonObject(manager);
        return restClient.setDocument(CollectionName.MANAGERS, manager.getManagerId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(manager, "Manager profile created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Gets Manager profile by manager ID.
     */
    public CompletableFuture<OperationResult<Manager>> getManager(String managerId, String idToken) {
        if (managerId == null || managerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }

        return restClient.getDocument(CollectionName.MANAGERS, managerId.trim(), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Manager mgr = fromFirestoreJsonObject(res.getData());
                        if (mgr != null) {
                            return OperationResult.success(mgr);
                        } else {
                            return OperationResult.failure("PARSE_ERROR", "Failed to parse Manager document.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Queries /managers for all Managers belonging to an Owner.
     */
    public CompletableFuture<OperationResult<List<Manager>>> getOwnerManagers(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_OWNER_ID", "Owner ID cannot be blank."));
        }

        return restClient.queryDocuments(CollectionName.MANAGERS, "ownerId", ownerId.trim(), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Manager> list = new ArrayList<>();
                        for (JSONObject docJson : res.getData()) {
                            Manager mgr = fromFirestoreJsonObject(docJson);
                            if (mgr != null) {
                                list.add(mgr);
                            }
                        }
                        return OperationResult.success(list);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Manager self-update for personal profile (name, phone).
     */
    public CompletableFuture<OperationResult<Void>> updatePersonalProfile(String managerId, String name, String phone, String idToken) {
        if (managerId == null || managerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }

        JSONObject updates = new JSONObject()
                .put("name", name != null ? name.trim() : "")
                .put("phone", phone != null ? phone.trim() : "")
                .put("updatedAt", System.currentTimeMillis());

        JSONObject patchDoc = FirestoreDocumentUtil.toFirestoreValueMap(updates);

        return restClient.updateDocument(CollectionName.MANAGERS, managerId.trim(), patchDoc, List.of("name", "phone", "updatedAt"), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(null, "Personal profile updated.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Owner update for Manager space assignments and permissions.
     */
    public CompletableFuture<OperationResult<Void>> updateAssignmentsByOwner(
            String managerId, List<String> assignedSpaceIds, List<String> permissions, String idToken) {

        if (managerId == null || managerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }

        JSONObject updates = new JSONObject()
                .put("assignedSpaceIds", assignedSpaceIds != null ? assignedSpaceIds : List.of())
                .put("permissions", permissions != null ? permissions : List.of())
                .put("updatedAt", System.currentTimeMillis());

        JSONObject patchDoc = FirestoreDocumentUtil.toFirestoreValueMap(updates);

        return restClient.updateDocument(CollectionName.MANAGERS, managerId.trim(), patchDoc, List.of("assignedSpaceIds", "permissions", "updatedAt"), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(null, "Manager assignments updated.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Owner update for Manager active status.
     */
    public CompletableFuture<OperationResult<Void>> setManagerActiveByOwner(String managerId, boolean active, String idToken) {
        if (managerId == null || managerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }

        JSONObject updates = new JSONObject()
                .put("active", active)
                .put("updatedAt", System.currentTimeMillis());

        JSONObject patchFields = FirestoreDocumentUtil.toFirestoreValueMap(updates);
        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(patchFields);

        return restClient.updateDocument(CollectionName.MANAGERS, managerId.trim(), patchDoc, List.of("active", "updatedAt"), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(null, "Manager active status updated.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public static JSONObject toFirestoreJsonObject(Manager manager) {
        JSONObject map = new JSONObject();
        map.put("managerId", manager.getManagerId() != null ? manager.getManagerId() : "");
        map.put("ownerId", manager.getOwnerId() != null ? manager.getOwnerId() : "");
        map.put("name", manager.getName() != null ? manager.getName() : "");
        map.put("email", manager.getEmail() != null ? manager.getEmail() : "");
        map.put("phone", manager.getPhone() != null ? manager.getPhone() : "");
        map.put("role", manager.getRole() != null ? manager.getRole() : "MANAGER");
        map.put("assignedSpaceIds", manager.getAssignedSpaceIds() != null ? manager.getAssignedSpaceIds() : List.of());
        map.put("permissions", manager.getPermissions() != null ? manager.getPermissions() : List.of());
        map.put("emailVerified", manager.isEmailVerified());
        map.put("profileCompleted", manager.isProfileCompleted());
        map.put("active", manager.isActive());
        map.put("createdAt", manager.getCreatedAt());
        map.put("updatedAt", manager.getUpdatedAt());

        JSONObject fields = FirestoreDocumentUtil.toFirestoreValueMap(map);
        return FirestoreDocumentUtil.createDocument(fields);
    }

    public static Manager fromFirestoreJsonObject(JSONObject docJson) {
        if (docJson == null || !docJson.has("fields")) {
            return null;
        }

        JSONObject fields = docJson.optJSONObject("fields");
        if (fields == null) {
            return null;
        }

        String managerId = getStringField(fields, "managerId");
        String ownerId = getStringField(fields, "ownerId");
        String name = getStringField(fields, "name");
        String email = getStringField(fields, "email");
        String phone = getStringField(fields, "phone");

        List<String> assignedSpaceIds = getArrayField(fields, "assignedSpaceIds");
        List<String> permissions = getArrayField(fields, "permissions");

        boolean emailVerified = getBooleanField(fields, "emailVerified");
        boolean profileCompleted = getBooleanField(fields, "profileCompleted");
        boolean active = getBooleanField(fields, "active");

        long createdAt = getLongField(fields, "createdAt");
        long updatedAt = getLongField(fields, "updatedAt");

        return new Manager(
                managerId, ownerId, name, email, phone,
                assignedSpaceIds, permissions, emailVerified, profileCompleted, active,
                createdAt, updatedAt
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

    private static boolean getBooleanField(JSONObject fields, String name) {
        if (fields.has(name)) {
            JSONObject obj = fields.getJSONObject(name);
            if (obj.has("booleanValue")) {
                return obj.getBoolean("booleanValue");
            }
        }
        return false;
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
