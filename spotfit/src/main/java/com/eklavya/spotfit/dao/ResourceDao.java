package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Firestore DAO for managing Resource subcollection persistence under /spaces/{spaceId}/resources.
 */
public class ResourceDao {

    private final FirestoreRestClient firestoreClient;

    public ResourceDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public ResourceDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient;
    }

    public CompletableFuture<OperationResult<Resource>> createResource(String spaceId, Resource resource, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (resource == null || resource.getResourceId() == null || resource.getResourceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource ID is required."));
        }
        if (!spaceId.trim().equals(resource.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Model spaceId does not match path spaceId."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(resource);
        return firestoreClient.setNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.RESOURCES, resource.getResourceId().trim(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(resource, "Resource created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Resource>> getResource(String spaceId, String resourceId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.getNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.RESOURCES, resourceId.trim(), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Resource r = fromFirestoreDocument(res.getData());
                        if (r != null) {
                            return OperationResult.success(r);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Resource not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Resource>>> getResourcesForSpace(String spaceId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.listNestedDocuments(CollectionName.SPACES, spaceId.trim(), CollectionName.RESOURCES, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Resource> resources = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Resource r = fromFirestoreDocument(doc);
                                if (r != null) {
                                    resources.add(r);
                                }
                            }
                        }
                        return OperationResult.success(resources);
                    } else {
                        return OperationResult.success(new ArrayList<>(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Resource>> updateResource(String spaceId, Resource resource, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (resource == null || resource.getResourceId() == null || resource.getResourceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource ID is required."));
        }
        if (!spaceId.trim().equals(resource.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Model spaceId does not match path spaceId."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(resource);
        return firestoreClient.setNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.RESOURCES, resource.getResourceId().trim(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(resource, "Resource updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    private JSONObject toFirestoreDocument(Resource resource) {
        JSONObject fields = new JSONObject();

        fields.put("resourceId", FirestoreDocumentUtil.stringValue(resource.getResourceId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(resource.getSpaceId()));
        fields.put("businessId", FirestoreDocumentUtil.stringValue(resource.getBusinessId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(resource.getOwnerId()));

        fields.put("resourceName", FirestoreDocumentUtil.stringValue(resource.getResourceName()));
        fields.put("description", FirestoreDocumentUtil.stringValue(resource.getDescription()));
        fields.put("resourceType", FirestoreDocumentUtil.stringValue(resource.getResourceType()));
        fields.put("resourceCode", FirestoreDocumentUtil.stringValue(resource.getResourceCode()));

        fields.put("capacity", FirestoreDocumentUtil.integerValue(resource.getCapacity()));
        fields.put("equipment", FirestoreDocumentUtil.stringArrayValue(resource.getEquipment()));
        fields.put("amenities", FirestoreDocumentUtil.stringArrayValue(resource.getAmenities()));

        fields.put("status", FirestoreDocumentUtil.stringValue(resource.getStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(resource.isActive()));

        fields.put("createdAt", FirestoreDocumentUtil.integerValue(resource.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(resource.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private Resource fromFirestoreDocument(JSONObject document) {
        if (document == null) {
            return null;
        }

        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String resourceId = FirestoreDocumentUtil.getString(fields, "resourceId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String businessId = FirestoreDocumentUtil.getString(fields, "businessId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");

        String resourceName = FirestoreDocumentUtil.getString(fields, "resourceName");
        String description = FirestoreDocumentUtil.getString(fields, "description");
        String resourceType = FirestoreDocumentUtil.getString(fields, "resourceType");
        String resourceCode = FirestoreDocumentUtil.getString(fields, "resourceCode");

        int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 1);
        List<String> equipment = FirestoreDocumentUtil.getStringList(fields, "equipment");
        List<String> amenities = FirestoreDocumentUtil.getStringList(fields, "amenities");

        String status = FirestoreDocumentUtil.getString(fields, "status");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);

        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new Resource(
                resourceId, spaceId, businessId, ownerId,
                resourceName, description, resourceType, resourceCode,
                capacity, equipment, amenities, status, active, createdAt, updatedAt);
    }
}
