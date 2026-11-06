package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for Owner profile document operations in Cloud Firestore.
 */
public class OwnerDao {

    private final FirestoreRestClient firestoreClient;

    public OwnerDao() {
        this(new FirestoreRestClient());
    }

    public OwnerDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = (firestoreClient != null) ? firestoreClient : new FirestoreRestClient();
    }

    public CompletableFuture<OperationResult<Owner>> createOwner(Owner owner, String idToken) {
        if (owner == null || owner.getOwnerId() == null || owner.getOwnerId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required for creation."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(owner);
        return firestoreClient.setDocument(CollectionName.OWNERS, owner.getOwnerId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Owner created = fromFirestoreDocument(res.getData());
                        if (created.getOwnerId() == null) {
                            created.setOwnerId(owner.getOwnerId());
                        }
                        return OperationResult.success(created, "Owner profile created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Owner>> getOwner(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        return firestoreClient.getDocument(CollectionName.OWNERS, ownerId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Owner owner = fromFirestoreDocument(res.getData());
                        if (owner.getOwnerId() == null) {
                            owner.setOwnerId(ownerId);
                        }
                        return OperationResult.success(owner);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Owner>> updateBasicProfile(
            String ownerId,
            String name,
            String phone,
            String city,
            String profileImageUrl,
            String idToken) {

        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("name", FirestoreDocumentUtil.stringValue(name));
        fields.put("phone", FirestoreDocumentUtil.stringValue(phone));
        fields.put("city", FirestoreDocumentUtil.stringValue(city));
        fields.put("profileImageUrl", FirestoreDocumentUtil.stringValue(profileImageUrl));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("name", "phone", "city", "profileImageUrl", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.OWNERS, ownerId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getOwner(ownerId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<Owner>> updateEmailVerification(
            String ownerId,
            boolean emailVerified,
            String idToken) {

        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("emailVerified", FirestoreDocumentUtil.booleanValue(emailVerified));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("emailVerified", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.OWNERS, ownerId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getOwner(ownerId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<Owner>> completeProfile(
            String ownerId,
            String idToken) {

        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("OWNER_ID_REQUIRED", "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ID_TOKEN_REQUIRED", "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("profileCompleted", FirestoreDocumentUtil.booleanValue(true));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(System.currentTimeMillis()));

        JSONObject patchDoc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateFields = Arrays.asList("profileCompleted", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.OWNERS, ownerId, patchDoc, updateFields, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getOwner(ownerId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    private JSONObject toFirestoreDocument(Owner owner) {
        JSONObject fields = new JSONObject();
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(owner.getOwnerId()));
        fields.put("name", FirestoreDocumentUtil.stringValue(owner.getName()));
        fields.put("email", FirestoreDocumentUtil.stringValue(owner.getEmail()));
        fields.put("phone", FirestoreDocumentUtil.stringValue(owner.getPhone()));
        fields.put("role", FirestoreDocumentUtil.stringValue(owner.getRole()));
        fields.put("profileImageUrl", FirestoreDocumentUtil.stringValue(owner.getProfileImageUrl()));
        fields.put("city", FirestoreDocumentUtil.stringValue(owner.getCity()));
        fields.put("emailVerified", FirestoreDocumentUtil.booleanValue(owner.isEmailVerified()));
        fields.put("profileCompleted", FirestoreDocumentUtil.booleanValue(owner.isProfileCompleted()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(owner.isActive()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(owner.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(owner.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private Owner fromFirestoreDocument(JSONObject document) {
        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");
        String name = FirestoreDocumentUtil.getString(fields, "name");
        String email = FirestoreDocumentUtil.getString(fields, "email");
        String phone = FirestoreDocumentUtil.getString(fields, "phone");
        String role = FirestoreDocumentUtil.getString(fields, "role");
        String profileImageUrl = FirestoreDocumentUtil.getString(fields, "profileImageUrl");
        String city = FirestoreDocumentUtil.getString(fields, "city");
        boolean emailVerified = FirestoreDocumentUtil.getBoolean(fields, "emailVerified", false);
        boolean profileCompleted = FirestoreDocumentUtil.getBoolean(fields, "profileCompleted", false);
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new Owner(
                ownerId, name, email, phone, role, profileImageUrl, city,
                emailVerified, profileCompleted, active, createdAt, updatedAt);
    }
}
