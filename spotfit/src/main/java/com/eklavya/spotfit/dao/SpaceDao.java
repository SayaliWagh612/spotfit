package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Firestore DAO for managing Space document persistence across V1 and V2
 * schemas.
 */
public class SpaceDao {

    private static final Set<String> ALLOWED_V2_UPDATE_MASK_FIELDS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    "spaceName", "description", "category", "activityGroup", "activityType",
                    "location", "coverPhotoUrl", "photoUrls", "amenities", "facilities",
                    "capacity", "bookingType", "basePricePaise", "pricingUnit", "bookingConfig",
                    "operatingHours", "rules", "cancellationPolicy", "approvalStatus", "operationalStatus",
                    "submittedAt", "reviewedAt", "rejectionReason", "approvedAt", "publishedAt",
                    "updatedAt", "timeZoneId")));

    private static final Set<String> FORBIDDEN_V2_UPDATE_MASK_FIELDS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    "schemaVersion", "spaceId", "businessId", "ownerId", "createdAt", "active")));

    private final FirestoreRestClient firestoreClient;

    public SpaceDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public SpaceDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient;
    }

    public CompletableFuture<OperationResult<Space>> createSpace(Space space, String idToken) {
        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(space);
        return firestoreClient.setDocument(CollectionName.SPACES, space.getSpaceId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(space, "Space created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> createV2Space(Space space, String idToken) {
        if (space == null || space.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space must have schemaVersion == 2."));
        }
        return createSpace(space, idToken);
    }

    public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.getDocument(CollectionName.SPACES, spaceId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Space space = fromFirestoreDocument(res.getData());
                        if (space != null) {
                            return OperationResult.success(space);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Space not found or malformed.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> getPublicSpace(String spaceId) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return firestoreClient.getPublicDocumentBySegments(List.of(CollectionName.SPACES, spaceId.trim()))
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Space space = fromFirestoreDocument(res.getData());
                        if (space != null) {
                            return OperationResult.success(space);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Space not found or malformed.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesByOwner(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.queryDocuments(CollectionName.SPACES, "ownerId", ownerId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getOwnerSpaces(String ownerId, String idToken) {
        return getSpacesByOwner(ownerId, idToken);
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesByBusiness(String businessId, String idToken) {
        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.queryDocuments(CollectionName.SPACES, "businessId", businessId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getSpacesByOwnerAndBusiness(
            String ownerId, String businessId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("ownerId", ownerId.trim());
        filters.put("businessId", businessId.trim());

        return firestoreClient.queryDocuments(CollectionName.SPACES, filters, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(String activityGroup,
            String idToken) {
        if (activityGroup == null || activityGroup.isBlank()
                || !com.eklavya.spotfit.constants.CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.success(Collections.emptyList()));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("schemaVersion", 2L);
        filters.put("approvalStatus", SpaceApprovalStatus.APPROVED);
        filters.put("operationalStatus", SpaceOperationalStatus.ACTIVE);
        filters.put("active", Boolean.TRUE);
        filters.put("category", com.eklavya.spotfit.constants.SpaceCategory.CREATIVITY);
        filters.put("activityGroup", activityGroup.trim().toUpperCase());
        filters.put("parentBusinessMarketplaceEligible", Boolean.TRUE);

        return firestoreClient.queryDocuments(CollectionName.SPACES, filters, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getLiveCreativitySpaces(String activityGroup,
            String idToken) {
        if (activityGroup == null || activityGroup.isBlank()
                || !com.eklavya.spotfit.constants.CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("status", SpaceStatus.LIVE);
        filters.put("active", Boolean.TRUE);
        filters.put("category", com.eklavya.spotfit.constants.SpaceCategory.CREATIVITY);
        filters.put("activityGroup", activityGroup.trim().toUpperCase());

        return firestoreClient.queryDocuments(CollectionName.SPACES, filters, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(String activityGroup) {
        if (activityGroup == null || activityGroup.isBlank()
                || !com.eklavya.spotfit.constants.CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.success(Collections.emptyList()));
        }

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("schemaVersion", 2L);
        filters.put("approvalStatus", "APPROVED");
        filters.put("operationalStatus", "ACTIVE");
        filters.put("active", Boolean.TRUE);
        filters.put("category", "CREATIVITY");
        filters.put("activityGroup", activityGroup.trim().toUpperCase());
        filters.put("parentBusinessMarketplaceEligible", Boolean.TRUE);

        return firestoreClient.publicQueryDocuments(CollectionName.SPACES, filters)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> getPublicLiveCreativitySpaces(String activityGroup) {
        if (activityGroup == null || activityGroup.isBlank()
                || !com.eklavya.spotfit.constants.CreativityActivityGroup.isValid(activityGroup)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Valid controlled Creativity activity group is required."));
        }

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("schemaVersion", 2L);
        filters.put("approvalStatus", "APPROVED");
        filters.put("operationalStatus", "ACTIVE");
        filters.put("active", Boolean.TRUE);
        filters.put("category", "CREATIVITY");
        filters.put("activityGroup", activityGroup.trim().toUpperCase());
        filters.put("parentBusinessMarketplaceEligible", Boolean.TRUE);

        return firestoreClient.publicQueryDocuments(CollectionName.SPACES, filters)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Space> spaces = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Space s = fromFirestoreDocument(doc);
                                if (s != null) {
                                    spaces.add(s);
                                }
                            }
                        }
                        return OperationResult.success(spaces);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> updateSpace(Space space, String idToken) {
        if (space == null || space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(space);
        return firestoreClient.setDocument(CollectionName.SPACES, space.getSpaceId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(space, "Space updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> updateV2Space(Space space, List<String> updateMask,
            String idToken) {
        if (space == null || space.getSchemaVersion() != 2) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space must have schemaVersion == 2."));
        }
        if (space.getSpaceId() == null || space.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }
        if (updateMask == null || updateMask.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Update mask cannot be empty."));
        }

        Set<String> seenMaskFields = new HashSet<>();
        for (String field : updateMask) {
            if (field == null || field.isBlank()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Update mask contains blank field."));
            }
            String trimmed = field.trim();
            if (trimmed.contains("/") || trimmed.contains(".")) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                                "Path-like field in update mask rejected: " + trimmed));
            }
            if (FORBIDDEN_V2_UPDATE_MASK_FIELDS.contains(trimmed)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                                "Forbidden field in update mask rejected: " + trimmed));
            }
            if (!ALLOWED_V2_UPDATE_MASK_FIELDS.contains(trimmed)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                                "Unknown field in update mask rejected: " + trimmed));
            }
            if (!seenMaskFields.add(trimmed)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                                "Duplicate field in update mask rejected: " + trimmed));
            }
        }

        JSONObject doc = toFirestoreDocument(space);
        return firestoreClient.updateDocument(CollectionName.SPACES, space.getSpaceId(), doc, updateMask, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(space, "V2 Space updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> submitSpace(String spaceId, long submittedAt, long updatedAt,
            String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("status", FirestoreDocumentUtil.stringValue(SpaceStatus.SUBMITTED));
        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(submittedAt));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(""));
        fields.put("publishedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(updatedAt));

        JSONObject doc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateMask = List.of("status", "submittedAt", "reviewedAt", "rejectionReason", "publishedAt",
                "updatedAt");

        return firestoreClient.updateDocument(CollectionName.SPACES, spaceId, doc, updateMask, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getSpace(spaceId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> submitV2Space(String spaceId, long submittedAt, long updatedAt,
            String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("approvalStatus",
                FirestoreDocumentUtil.stringValue(com.eklavya.spotfit.constants.SpaceApprovalStatus.SUBMITTED));
        fields.put("operationalStatus",
                FirestoreDocumentUtil.stringValue(com.eklavya.spotfit.constants.SpaceOperationalStatus.INACTIVE));
        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(submittedAt));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(""));
        fields.put("approvedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("publishedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(updatedAt));

        JSONObject doc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateMask = List.of("approvalStatus", "operationalStatus", "submittedAt", "reviewedAt",
                "rejectionReason", "approvedAt", "publishedAt", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.SPACES, spaceId, doc, updateMask, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getSpace(spaceId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public CompletableFuture<OperationResult<Space>> setV2OperationalStatus(String spaceId, String operationalStatus,
            long publishedAt, long updatedAt, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (!com.eklavya.spotfit.constants.SpaceOperationalStatus.isValid(operationalStatus)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Invalid operational status: " + operationalStatus));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("operationalStatus", FirestoreDocumentUtil.stringValue(operationalStatus));
        fields.put("publishedAt", FirestoreDocumentUtil.integerValue(publishedAt));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(updatedAt));

        JSONObject doc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateMask = List.of("operationalStatus", "publishedAt", "updatedAt");

        return firestoreClient.updateDocument(CollectionName.SPACES, spaceId, doc, updateMask, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getSpace(spaceId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public JSONObject toFirestoreDocument(Space space) {
        if (space.getSchemaVersion() == 1) {
            return toFirestoreV1Document(space);
        } else {
            return toFirestoreV2Document(space);
        }
    }

    private JSONObject toFirestoreV1Document(Space space) {
        JSONObject fields = new JSONObject();

        fields.put("spaceId", FirestoreDocumentUtil.stringValue(space.getSpaceId()));
        fields.put("businessId", FirestoreDocumentUtil.stringValue(space.getBusinessId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(space.getOwnerId()));

        fields.put("spaceName", FirestoreDocumentUtil.stringValue(space.getSpaceName()));
        fields.put("description", FirestoreDocumentUtil.stringValue(space.getDescription()));

        fields.put("category", FirestoreDocumentUtil.stringValue(space.getCategory()));
        fields.put("activityGroup", FirestoreDocumentUtil.stringValue(space.getActivityGroup()));
        fields.put("spaceType", FirestoreDocumentUtil.stringValue(space.getSpaceType()));

        fields.put("addressLine", FirestoreDocumentUtil.stringValue(space.getAddressLine()));
        fields.put("landmark", FirestoreDocumentUtil.stringValue(space.getLandmark()));
        fields.put("city", FirestoreDocumentUtil.stringValue(space.getCity()));
        fields.put("state", FirestoreDocumentUtil.stringValue(space.getState()));
        fields.put("postalCode", FirestoreDocumentUtil.stringValue(space.getPostalCode()));

        fields.put("capacity", FirestoreDocumentUtil.integerValue(space.getCapacity()));

        fields.put("amenities", FirestoreDocumentUtil.stringArrayValue(space.getAmenities()));
        fields.put("suitableActivities", FirestoreDocumentUtil.stringArrayValue(space.getSuitableActivities()));
        fields.put("rules", FirestoreDocumentUtil.stringArrayValue(space.getRules()));

        fields.put("openingTime", FirestoreDocumentUtil.stringValue(space.getOpeningTime()));
        fields.put("closingTime", FirestoreDocumentUtil.stringValue(space.getClosingTime()));
        fields.put("operatingDays", FirestoreDocumentUtil.stringArrayValue(space.getOperatingDays()));
        fields.put("timeZoneId", FirestoreDocumentUtil.stringValue(space.getTimeZoneId()));

        fields.put("status", FirestoreDocumentUtil.stringValue(space.getStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(space.isActive()));

        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(space.getSubmittedAt()));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(space.getReviewedAt()));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(space.getRejectionReason()));
        fields.put("publishedAt", FirestoreDocumentUtil.integerValue(space.getPublishedAt()));

        fields.put("createdAt", FirestoreDocumentUtil.integerValue(space.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(space.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private JSONObject toFirestoreV2Document(Space space) {
        JSONObject fields = new JSONObject();

        fields.put("schemaVersion", FirestoreDocumentUtil.integerValue(2L));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(space.getSpaceId()));
        fields.put("businessId", FirestoreDocumentUtil.stringValue(space.getBusinessId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(space.getOwnerId()));
        fields.put("spaceName", FirestoreDocumentUtil.stringValue(space.getSpaceName()));
        fields.put("description", FirestoreDocumentUtil.stringValue(space.getDescription()));
        fields.put("category", FirestoreDocumentUtil.stringValue(space.getCategory()));
        fields.put("activityGroup", FirestoreDocumentUtil.stringValue(space.getActivityGroup()));
        fields.put("activityType", FirestoreDocumentUtil.stringValue(space.getActivityType()));

        // location map
        JSONObject locFields = new JSONObject();
        SpaceLocation loc = space.getSpaceLocation();
        if (loc != null) {
            locFields.put("addressLine", FirestoreDocumentUtil.stringValue(loc.getAddressLine()));
            locFields.put("area", FirestoreDocumentUtil.stringValue(loc.getArea()));
            locFields.put("city", FirestoreDocumentUtil.stringValue(loc.getCity()));
            locFields.put("state", FirestoreDocumentUtil.stringValue(loc.getState()));
            locFields.put("postalCode", FirestoreDocumentUtil.stringValue(loc.getPostalCode()));
            locFields.put("landmark", FirestoreDocumentUtil.stringValue(loc.getLandmark()));
            locFields.put("latitude", FirestoreDocumentUtil.doubleValue(loc.getLatitude()));
            locFields.put("longitude", FirestoreDocumentUtil.doubleValue(loc.getLongitude()));
        }
        fields.put("location", FirestoreDocumentUtil.mapValue(locFields));

        fields.put("coverPhotoUrl", FirestoreDocumentUtil.stringValue(space.getCoverPhotoUrl()));
        fields.put("photoUrls", FirestoreDocumentUtil.stringArrayValue(space.getPhotoUrls()));
        fields.put("amenities", FirestoreDocumentUtil.stringArrayValue(space.getAmenities()));
        fields.put("facilities", FirestoreDocumentUtil.stringArrayValue(space.getFacilities()));
        fields.put("capacity", FirestoreDocumentUtil.integerValue(space.getCapacity()));
        fields.put("bookingType", FirestoreDocumentUtil.stringValue(space.getBookingType()));
        fields.put("basePricePaise", FirestoreDocumentUtil.integerValue(space.getBasePricePaise()));
        fields.put("pricingUnit", FirestoreDocumentUtil.stringValue(space.getPricingUnit()));

        // bookingConfig map
        fields.put("bookingConfig", serializeBookingConfig(space.getBookingConfig()));

        // operatingHours map
        JSONObject ohFields = new JSONObject();
        Map<String, OperatingDayConfig> oh = space.getOperatingHours();
        if (oh != null) {
            for (Map.Entry<String, OperatingDayConfig> entry : oh.entrySet()) {
                String normDay = WeekDay.normalizeDay(entry.getKey());
                if (normDay != null && entry.getValue() != null) {
                    JSONObject dayFields = new JSONObject();
                    OperatingDayConfig dayCfg = entry.getValue();
                    dayFields.put("openingTime", FirestoreDocumentUtil.stringValue(dayCfg.getOpeningTime()));
                    dayFields.put("closingTime", FirestoreDocumentUtil.stringValue(dayCfg.getClosingTime()));
                    dayFields.put("active", FirestoreDocumentUtil.booleanValue(dayCfg.isActive()));
                    ohFields.put(normDay, FirestoreDocumentUtil.mapValue(dayFields));
                }
            }
        }
        fields.put("operatingHours", FirestoreDocumentUtil.mapValue(ohFields));

        fields.put("rules", FirestoreDocumentUtil.stringArrayValue(space.getRules()));
        fields.put("cancellationPolicy", FirestoreDocumentUtil.stringValue(space.getCancellationPolicy()));
        fields.put("approvalStatus", FirestoreDocumentUtil.stringValue(space.getApprovalStatus()));
        fields.put("operationalStatus", FirestoreDocumentUtil.stringValue(space.getOperationalStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(space.isActive()));

        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(space.getSubmittedAt()));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(space.getReviewedAt()));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(space.getRejectionReason()));
        fields.put("approvedAt", FirestoreDocumentUtil.integerValue(space.getApprovedAt()));
        fields.put("publishedAt", FirestoreDocumentUtil.integerValue(space.getPublishedAt()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(space.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(space.getUpdatedAt()));
        fields.put("timeZoneId", FirestoreDocumentUtil.stringValue(space.getTimeZoneId()));
        fields.put("parentBusinessMarketplaceEligible",
                FirestoreDocumentUtil.booleanValue(space.isParentBusinessMarketplaceEligible()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    public Space fromFirestoreDocument(JSONObject document) {
        if (document == null) {
            return null;
        }

        JSONObject fields = FirestoreDocumentUtil.getFields(document);
        int schemaVersion = FirestoreDocumentUtil.getInt(fields, "schemaVersion", 1);

        if (schemaVersion == 1) {
            return fromFirestoreV1Document(fields);
        } else if (schemaVersion == 2) {
            return fromFirestoreV2Document(fields);
        }
        return null;
    }

    private Space fromFirestoreV1Document(JSONObject fields) {
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String businessId = FirestoreDocumentUtil.getString(fields, "businessId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");

        String spaceName = FirestoreDocumentUtil.getString(fields, "spaceName");
        String description = FirestoreDocumentUtil.getString(fields, "description");

        String category = FirestoreDocumentUtil.getString(fields, "category");
        String activityGroup = FirestoreDocumentUtil.getString(fields, "activityGroup");
        String spaceType = FirestoreDocumentUtil.getString(fields, "spaceType");

        String addressLine = FirestoreDocumentUtil.getString(fields, "addressLine");
        String landmark = FirestoreDocumentUtil.getString(fields, "landmark");
        String city = FirestoreDocumentUtil.getString(fields, "city");
        String state = FirestoreDocumentUtil.getString(fields, "state");
        String postalCode = FirestoreDocumentUtil.getString(fields, "postalCode");

        int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 0);

        List<String> amenities = FirestoreDocumentUtil.getStringList(fields, "amenities");
        List<String> suitableActivities = FirestoreDocumentUtil.getStringList(fields, "suitableActivities");
        List<String> rules = FirestoreDocumentUtil.getStringList(fields, "rules");

        String openingTime = FirestoreDocumentUtil.getString(fields, "openingTime");
        String closingTime = FirestoreDocumentUtil.getString(fields, "closingTime");
        List<String> operatingDays = FirestoreDocumentUtil.getStringList(fields, "operatingDays");
        String timeZoneId = FirestoreDocumentUtil.getString(fields, "timeZoneId");

        String status = FirestoreDocumentUtil.getString(fields, "status");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);

        long submittedAt = FirestoreDocumentUtil.getLong(fields, "submittedAt", 0L);
        long reviewedAt = FirestoreDocumentUtil.getLong(fields, "reviewedAt", 0L);
        String rejectionReason = FirestoreDocumentUtil.getString(fields, "rejectionReason");
        long publishedAt = FirestoreDocumentUtil.getLong(fields, "publishedAt", 0L);

        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new Space(
                spaceId, businessId, ownerId, spaceName, description,
                category, activityGroup, spaceType,
                addressLine, landmark, city, state, postalCode,
                capacity, amenities, suitableActivities, rules,
                openingTime, closingTime, operatingDays, Space.sanitizeTimeZone(timeZoneId),
                status, active,
                submittedAt, reviewedAt, rejectionReason, publishedAt,
                createdAt, updatedAt);
    }

    private Space fromFirestoreV2Document(JSONObject fields) {
        try {
            String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
            String businessId = FirestoreDocumentUtil.getString(fields, "businessId");
            String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");

            String spaceName = FirestoreDocumentUtil.getString(fields, "spaceName");
            String description = FirestoreDocumentUtil.getString(fields, "description");
            String category = FirestoreDocumentUtil.getString(fields, "category");
            String activityGroup = FirestoreDocumentUtil.getString(fields, "activityGroup");
            String activityType = FirestoreDocumentUtil.getString(fields, "activityType");

            JSONObject locFields = FirestoreDocumentUtil.getMapFields(fields, "location");
            SpaceLocation location = new SpaceLocation(
                    FirestoreDocumentUtil.getString(locFields, "addressLine"),
                    FirestoreDocumentUtil.getString(locFields, "area"),
                    FirestoreDocumentUtil.getString(locFields, "city"),
                    FirestoreDocumentUtil.getString(locFields, "state"),
                    FirestoreDocumentUtil.getString(locFields, "postalCode"),
                    FirestoreDocumentUtil.getString(locFields, "landmark"),
                    FirestoreDocumentUtil.getDouble(locFields, "latitude", 0.0),
                    FirestoreDocumentUtil.getDouble(locFields, "longitude", 0.0));

            String coverPhotoUrl = FirestoreDocumentUtil.getString(fields, "coverPhotoUrl");
            List<String> photoUrls = FirestoreDocumentUtil.getStringList(fields, "photoUrls");
            List<String> amenities = FirestoreDocumentUtil.getStringList(fields, "amenities");
            List<String> facilities = FirestoreDocumentUtil.getStringList(fields, "facilities");

            int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 0);
            String bookingType = FirestoreDocumentUtil.getString(fields, "bookingType");
            long basePricePaise = FirestoreDocumentUtil.getLong(fields, "basePricePaise", 0L);
            String pricingUnit = FirestoreDocumentUtil.getString(fields, "pricingUnit");

            JSONObject cfgFields = FirestoreDocumentUtil.getMapFields(fields, "bookingConfig");
            BookingConfig bookingConfig = deserializeBookingConfig(bookingType, cfgFields);
            if (bookingConfig == null) {
                return null; // Classified failure for unparseable booking config
            }

            JSONObject ohFields = FirestoreDocumentUtil.getMapFields(fields, "operatingHours");
            Map<String, OperatingDayConfig> operatingHours = new LinkedHashMap<>();
            if (ohFields != null) {
                for (String dayKey : ohFields.keySet()) {
                    String normDay = WeekDay.normalizeDay(dayKey);
                    if (normDay != null) {
                        JSONObject dayFields = FirestoreDocumentUtil.getMapFields(ohFields, dayKey);
                        operatingHours.put(normDay, new OperatingDayConfig(
                                FirestoreDocumentUtil.getString(dayFields, "openingTime"),
                                FirestoreDocumentUtil.getString(dayFields, "closingTime"),
                                FirestoreDocumentUtil.getBoolean(dayFields, "active", true)));
                    }
                }
            }

            List<String> rules = FirestoreDocumentUtil.getStringList(fields, "rules");
            String cancellationPolicy = FirestoreDocumentUtil.getString(fields, "cancellationPolicy");
            String approvalStatus = FirestoreDocumentUtil.getString(fields, "approvalStatus");
            String operationalStatus = FirestoreDocumentUtil.getString(fields, "operationalStatus");
            boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);

            long submittedAt = FirestoreDocumentUtil.getLong(fields, "submittedAt", 0L);
            long reviewedAt = FirestoreDocumentUtil.getLong(fields, "reviewedAt", 0L);
            String rejectionReason = FirestoreDocumentUtil.getString(fields, "rejectionReason");
            long approvedAt = FirestoreDocumentUtil.getLong(fields, "approvedAt", 0L);
            long publishedAt = FirestoreDocumentUtil.getLong(fields, "publishedAt", 0L);
            long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
            long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);
            String timeZoneId = FirestoreDocumentUtil.getString(fields, "timeZoneId");

            Space v2Space = Space.createV2(
                    spaceId, businessId, ownerId, spaceName, description,
                    category, activityGroup, activityType, location,
                    coverPhotoUrl, photoUrls, amenities, facilities,
                    capacity, bookingType, basePricePaise, pricingUnit,
                    bookingConfig, operatingHours, rules, cancellationPolicy);

            v2Space.setTimeZoneId(timeZoneId);
            v2Space.setApprovalStatus(approvalStatus);
            v2Space.setOperationalStatus(operationalStatus);
            v2Space.setActive(active);
            v2Space.setSubmittedAt(submittedAt);
            v2Space.setReviewedAt(reviewedAt);
            v2Space.setRejectionReason(rejectionReason);
            v2Space.setApprovedAt(approvedAt);
            v2Space.setPublishedAt(publishedAt);
            v2Space.setCreatedAt(createdAt);
            v2Space.setUpdatedAt(updatedAt);
            v2Space.setParentBusinessMarketplaceEligible(
                    FirestoreDocumentUtil.getBoolean(fields, "parentBusinessMarketplaceEligible", false));

            return v2Space;
        } catch (Exception e) {
            return null;
        }
    }

    private JSONObject serializeBookingConfig(BookingConfig config) {
        if (config == null)
            return new JSONObject();
        JSONObject fields = new JSONObject();

        if (config instanceof SeatBookingConfig) {
            SeatBookingConfig c = (SeatBookingConfig) config;
            fields.put("selectionMode", FirestoreDocumentUtil.stringValue(c.getSelectionMode()));
            fields.put("minimumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMinimumDurationMinutes()));
            fields.put("maximumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMaximumDurationMinutes()));
            fields.put("durationStepMinutes", FirestoreDocumentUtil.integerValue(c.getDurationStepMinutes()));
            fields.put("maxGuestsPerBooking", FirestoreDocumentUtil.integerValue(c.getMaxGuestsPerBooking()));
            fields.put("seatPriceMode", FirestoreDocumentUtil.stringValue(c.getSeatPriceMode()));
            fields.put("allowMultipleSeats", FirestoreDocumentUtil.booleanValue(c.isAllowMultipleSeats()));
            fields.put("advanceBookingDays", FirestoreDocumentUtil.integerValue(c.getAdvanceBookingDays()));
            fields.put("minimumAdvanceMinutes", FirestoreDocumentUtil.integerValue(c.getMinimumAdvanceMinutes()));
        } else if (config instanceof SlotBookingConfig) {
            SlotBookingConfig c = (SlotBookingConfig) config;
            fields.put("minimumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMinimumDurationMinutes()));
            fields.put("maximumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMaximumDurationMinutes()));
            fields.put("durationStepMinutes", FirestoreDocumentUtil.integerValue(c.getDurationStepMinutes()));
            fields.put("slotIntervalMinutes", FirestoreDocumentUtil.integerValue(c.getSlotIntervalMinutes()));
            fields.put("bufferBeforeMinutes", FirestoreDocumentUtil.integerValue(c.getBufferBeforeMinutes()));
            fields.put("bufferAfterMinutes", FirestoreDocumentUtil.integerValue(c.getBufferAfterMinutes()));
            fields.put("maxPeople", FirestoreDocumentUtil.integerValue(c.getMaxPeople()));
            fields.put("advanceBookingDays", FirestoreDocumentUtil.integerValue(c.getAdvanceBookingDays()));
            fields.put("minimumAdvanceMinutes", FirestoreDocumentUtil.integerValue(c.getMinimumAdvanceMinutes()));
        } else if (config instanceof WorkshopBookingConfig) {
            WorkshopBookingConfig c = (WorkshopBookingConfig) config;
            fields.put("pricePerPersonPaise", FirestoreDocumentUtil.integerValue(c.getPricePerPersonPaise()));
            fields.put("minimumParticipantsPerBooking",
                    FirestoreDocumentUtil.integerValue(c.getMinimumParticipantsPerBooking()));
            fields.put("maximumParticipantsPerBooking",
                    FirestoreDocumentUtil.integerValue(c.getMaximumParticipantsPerBooking()));
            fields.put("bookingCutoffHours", FirestoreDocumentUtil.integerValue(c.getBookingCutoffHours()));
            fields.put("cancellationCutoffHours", FirestoreDocumentUtil.integerValue(c.getCancellationCutoffHours()));
            fields.put("ageRestriction", FirestoreDocumentUtil.stringValue(c.getAgeRestriction()));
            fields.put("materialsIncluded", FirestoreDocumentUtil.stringArrayValue(c.getMaterialsIncluded()));
            fields.put("instructorName", FirestoreDocumentUtil.stringValue(c.getInstructorName()));
        } else if (config instanceof RentalBookingConfig) {
            RentalBookingConfig c = (RentalBookingConfig) config;
            fields.put("availableQuantity", FirestoreDocumentUtil.integerValue(c.getAvailableQuantity()));
            fields.put("minimumRentalDurationMinutes",
                    FirestoreDocumentUtil.integerValue(c.getMinimumRentalDurationMinutes()));
            fields.put("maximumRentalDurationMinutes",
                    FirestoreDocumentUtil.integerValue(c.getMaximumRentalDurationMinutes()));
            fields.put("securityDepositPaise", FirestoreDocumentUtil.integerValue(c.getSecurityDepositPaise()));
            fields.put("pickupAvailable", FirestoreDocumentUtil.booleanValue(c.isPickupAvailable()));
            fields.put("deliveryAvailable", FirestoreDocumentUtil.booleanValue(c.isDeliveryAvailable()));
            fields.put("deliveryRadiusKm", FirestoreDocumentUtil.doubleValue(c.getDeliveryRadiusKm()));
            fields.put("deliveryChargePaise", FirestoreDocumentUtil.integerValue(c.getDeliveryChargePaise()));
            fields.put("pickupInstructions", FirestoreDocumentUtil.stringValue(c.getPickupInstructions()));
            fields.put("requiresIdentityProof", FirestoreDocumentUtil.booleanValue(c.isRequiresIdentityProof()));
            fields.put("lateFeePaise", FirestoreDocumentUtil.integerValue(c.getLateFeePaise()));
            fields.put("addOns", FirestoreDocumentUtil.stringArrayValue(c.getAddOns()));
        } else if (config instanceof VenueBookingConfig) {
            VenueBookingConfig c = (VenueBookingConfig) config;
            fields.put("minimumBookingHours", FirestoreDocumentUtil.integerValue(c.getMinimumBookingHours()));
            fields.put("maximumBookingHours", FirestoreDocumentUtil.integerValue(c.getMaximumBookingHours()));
            fields.put("requiresApproval", FirestoreDocumentUtil.booleanValue(c.isRequiresApproval()));
            fields.put("minimumAdvanceDays", FirestoreDocumentUtil.integerValue(c.getMinimumAdvanceDays()));
            fields.put("allowedEventTypes", FirestoreDocumentUtil.stringArrayValue(c.getAllowedEventTypes()));
            fields.put("setupOptions", FirestoreDocumentUtil.stringArrayValue(c.getSetupOptions()));
            fields.put("securityDepositPaise", FirestoreDocumentUtil.integerValue(c.getSecurityDepositPaise()));
            fields.put("soundSystemAvailable", FirestoreDocumentUtil.booleanValue(c.isSoundSystemAvailable()));
            fields.put("lightingAvailable", FirestoreDocumentUtil.booleanValue(c.isLightingAvailable()));
            fields.put("stageAvailable", FirestoreDocumentUtil.booleanValue(c.isStageAvailable()));
            fields.put("greenRoomAvailable", FirestoreDocumentUtil.booleanValue(c.isGreenRoomAvailable()));
            fields.put("outsideVendorAllowed", FirestoreDocumentUtil.booleanValue(c.isOutsideVendorAllowed()));
            fields.put("cateringAllowed", FirestoreDocumentUtil.booleanValue(c.isCateringAllowed()));
            fields.put("overtimeRatePaise", FirestoreDocumentUtil.integerValue(c.getOvertimeRatePaise()));
        } else if (config instanceof PracticeRoomBookingConfig) {
            PracticeRoomBookingConfig c = (PracticeRoomBookingConfig) config;
            fields.put("minimumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMinimumDurationMinutes()));
            fields.put("maximumDurationMinutes", FirestoreDocumentUtil.integerValue(c.getMaximumDurationMinutes()));
            fields.put("durationStepMinutes", FirestoreDocumentUtil.integerValue(c.getDurationStepMinutes()));
            fields.put("equipmentSelectionEnabled",
                    FirestoreDocumentUtil.booleanValue(c.isEquipmentSelectionEnabled()));
            fields.put("includedEquipment", FirestoreDocumentUtil.stringArrayValue(c.getIncludedEquipment()));
            fields.put("optionalEquipment", FirestoreDocumentUtil.stringArrayValue(c.getOptionalEquipment()));
        }
        return FirestoreDocumentUtil.mapValue(fields);
    }

    private BookingConfig deserializeBookingConfig(String bookingType, JSONObject fields) {
        if (bookingType == null || fields == null)
            return null;
        try {
            switch (bookingType.trim().toUpperCase()) {
                case BookingType.SEAT:
                    return new SeatBookingConfig(
                            FirestoreDocumentUtil.getString(fields, "selectionMode"),
                            FirestoreDocumentUtil.getInt(fields, "minimumDurationMinutes", 60),
                            FirestoreDocumentUtil.getInt(fields, "maximumDurationMinutes", 480),
                            FirestoreDocumentUtil.getInt(fields, "durationStepMinutes", 30),
                            FirestoreDocumentUtil.getInt(fields, "maxGuestsPerBooking", 1),
                            FirestoreDocumentUtil.getString(fields, "seatPriceMode"),
                            FirestoreDocumentUtil.getBoolean(fields, "allowMultipleSeats", false),
                            FirestoreDocumentUtil.getInt(fields, "advanceBookingDays", 30),
                            FirestoreDocumentUtil.getInt(fields, "minimumAdvanceMinutes", 0));
                case BookingType.SLOT:
                    return new SlotBookingConfig(
                            FirestoreDocumentUtil.getInt(fields, "minimumDurationMinutes", 60),
                            FirestoreDocumentUtil.getInt(fields, "maximumDurationMinutes", 240),
                            FirestoreDocumentUtil.getInt(fields, "durationStepMinutes", 30),
                            FirestoreDocumentUtil.getInt(fields, "slotIntervalMinutes", 30),
                            FirestoreDocumentUtil.getInt(fields, "bufferBeforeMinutes", 0),
                            FirestoreDocumentUtil.getInt(fields, "bufferAfterMinutes", 0),
                            FirestoreDocumentUtil.getInt(fields, "maxPeople", 10),
                            FirestoreDocumentUtil.getInt(fields, "advanceBookingDays", 30),
                            FirestoreDocumentUtil.getInt(fields, "minimumAdvanceMinutes", 0));
                case BookingType.WORKSHOP:
                    return new WorkshopBookingConfig(
                            FirestoreDocumentUtil.getLong(fields, "pricePerPersonPaise", 0L),
                            FirestoreDocumentUtil.getInt(fields, "minimumParticipantsPerBooking", 1),
                            FirestoreDocumentUtil.getInt(fields, "maximumParticipantsPerBooking", 20),
                            FirestoreDocumentUtil.getInt(fields, "bookingCutoffHours", 24),
                            FirestoreDocumentUtil.getInt(fields, "cancellationCutoffHours", 48),
                            FirestoreDocumentUtil.getString(fields, "ageRestriction"),
                            FirestoreDocumentUtil.getStringList(fields, "materialsIncluded"),
                            FirestoreDocumentUtil.getString(fields, "instructorName"));
                case BookingType.RENTAL:
                    return new RentalBookingConfig(
                            FirestoreDocumentUtil.getInt(fields, "availableQuantity", 1),
                            FirestoreDocumentUtil.getInt(fields, "minimumRentalDurationMinutes", 60),
                            FirestoreDocumentUtil.getInt(fields, "maximumRentalDurationMinutes", 1440),
                            FirestoreDocumentUtil.getLong(fields, "securityDepositPaise", 0L),
                            FirestoreDocumentUtil.getBoolean(fields, "pickupAvailable", true),
                            FirestoreDocumentUtil.getBoolean(fields, "deliveryAvailable", false),
                            FirestoreDocumentUtil.getDouble(fields, "deliveryRadiusKm", 0.0),
                            FirestoreDocumentUtil.getLong(fields, "deliveryChargePaise", 0L),
                            FirestoreDocumentUtil.getString(fields, "pickupInstructions"),
                            FirestoreDocumentUtil.getBoolean(fields, "requiresIdentityProof", false),
                            FirestoreDocumentUtil.getLong(fields, "lateFeePaise", 0L),
                            FirestoreDocumentUtil.getStringList(fields, "addOns"));
                case BookingType.VENUE:
                    return new VenueBookingConfig(
                            FirestoreDocumentUtil.getInt(fields, "minimumBookingHours", 2),
                            FirestoreDocumentUtil.getInt(fields, "maximumBookingHours", 24),
                            FirestoreDocumentUtil.getBoolean(fields, "requiresApproval", true),
                            FirestoreDocumentUtil.getInt(fields, "minimumAdvanceDays", 1),
                            FirestoreDocumentUtil.getStringList(fields, "allowedEventTypes"),
                            FirestoreDocumentUtil.getStringList(fields, "setupOptions"),
                            FirestoreDocumentUtil.getLong(fields, "securityDepositPaise", 0L),
                            FirestoreDocumentUtil.getBoolean(fields, "soundSystemAvailable", false),
                            FirestoreDocumentUtil.getBoolean(fields, "lightingAvailable", false),
                            FirestoreDocumentUtil.getBoolean(fields, "stageAvailable", false),
                            FirestoreDocumentUtil.getBoolean(fields, "greenRoomAvailable", false),
                            FirestoreDocumentUtil.getBoolean(fields, "outsideVendorAllowed", true),
                            FirestoreDocumentUtil.getBoolean(fields, "cateringAllowed", true),
                            FirestoreDocumentUtil.getLong(fields, "overtimeRatePaise", 0L));
                case BookingType.PRACTICE_ROOM:
                    return new PracticeRoomBookingConfig(
                            FirestoreDocumentUtil.getInt(fields, "minimumDurationMinutes", 60),
                            FirestoreDocumentUtil.getInt(fields, "maximumDurationMinutes", 240),
                            FirestoreDocumentUtil.getInt(fields, "durationStepMinutes", 30),
                            FirestoreDocumentUtil.getBoolean(fields, "equipmentSelectionEnabled", false),
                            FirestoreDocumentUtil.getStringList(fields, "includedEquipment"),
                            FirestoreDocumentUtil.getStringList(fields, "optionalEquipment"));
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
