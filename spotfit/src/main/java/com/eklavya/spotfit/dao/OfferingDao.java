package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Firestore DAO for managing Offering subcollection persistence under /spaces/{spaceId}/offerings.
 */
public class OfferingDao {

    private final FirestoreRestClient firestoreClient;

    public OfferingDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public OfferingDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient;
    }

    public CompletableFuture<OperationResult<Offering>> createOffering(String spaceId, Offering offering, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (offering == null || offering.getOfferingId() == null || offering.getOfferingId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering ID is required."));
        }
        if (!spaceId.trim().equals(offering.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Model spaceId does not match path spaceId."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(offering);
        return firestoreClient.setNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.OFFERINGS, offering.getOfferingId().trim(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(offering, "Offering created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Offering>> getOffering(String spaceId, String offeringId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (offeringId == null || offeringId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.getNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.OFFERINGS, offeringId.trim(), idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Offering o = fromFirestoreDocument(res.getData());
                        if (o != null) {
                            return OperationResult.success(o);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Offering not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Offering>>> getOfferingsForSpace(String spaceId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.listNestedDocuments(CollectionName.SPACES, spaceId.trim(), CollectionName.OFFERINGS, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Offering> offerings = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Offering o = fromFirestoreDocument(doc);
                                if (o != null) {
                                    offerings.add(o);
                                }
                            }
                        }
                        return OperationResult.success(offerings);
                    } else {
                        return OperationResult.success(new ArrayList<>(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Offering>> updateOffering(String spaceId, Offering offering, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (offering == null || offering.getOfferingId() == null || offering.getOfferingId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering ID is required."));
        }
        if (!spaceId.trim().equals(offering.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Model spaceId does not match path spaceId."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(offering);
        return firestoreClient.setNestedDocument(CollectionName.SPACES, spaceId.trim(), CollectionName.OFFERINGS, offering.getOfferingId().trim(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(offering, "Offering updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    private JSONObject toFirestoreDocument(Offering offering) {
        JSONObject fields = new JSONObject();

        fields.put("offeringId", FirestoreDocumentUtil.stringValue(offering.getOfferingId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(offering.getSpaceId()));
        fields.put("businessId", FirestoreDocumentUtil.stringValue(offering.getBusinessId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(offering.getOwnerId()));

        fields.put("offeringName", FirestoreDocumentUtil.stringValue(offering.getOfferingName()));
        fields.put("description", FirestoreDocumentUtil.stringValue(offering.getDescription()));

        fields.put("bookingType", FirestoreDocumentUtil.stringValue(offering.getBookingType()));
        fields.put("pricingType", FirestoreDocumentUtil.stringValue(offering.getPricingType()));
        fields.put("pricePaise", FirestoreDocumentUtil.integerValue(offering.getPricePaise()));

        fields.put("requiredResourceType", FirestoreDocumentUtil.stringValue(offering.getRequiredResourceType()));

        fields.put("defaultDurationMinutes", FirestoreDocumentUtil.integerValue(offering.getDefaultDurationMinutes()));
        fields.put("minimumDurationMinutes", FirestoreDocumentUtil.integerValue(offering.getMinimumDurationMinutes()));
        fields.put("maximumDurationMinutes", FirestoreDocumentUtil.integerValue(offering.getMaximumDurationMinutes()));
        fields.put("bookingIntervalMinutes", FirestoreDocumentUtil.integerValue(offering.getBookingIntervalMinutes()));

        fields.put("capacity", FirestoreDocumentUtil.integerValue(offering.getCapacity()));
        fields.put("minimumParticipants", FirestoreDocumentUtil.integerValue(offering.getMinimumParticipants()));
        fields.put("maximumParticipants", FirestoreDocumentUtil.integerValue(offering.getMaximumParticipants()));

        fields.put("includedItems", FirestoreDocumentUtil.stringArrayValue(offering.getIncludedItems()));
        fields.put("rules", FirestoreDocumentUtil.stringArrayValue(offering.getRules()));

        fields.put("status", FirestoreDocumentUtil.stringValue(offering.getStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(offering.isActive()));

        fields.put("createdAt", FirestoreDocumentUtil.integerValue(offering.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(offering.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    private Offering fromFirestoreDocument(JSONObject document) {
        if (document == null) {
            return null;
        }

        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        String offeringId = FirestoreDocumentUtil.getString(fields, "offeringId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String businessId = FirestoreDocumentUtil.getString(fields, "businessId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");

        String offeringName = FirestoreDocumentUtil.getString(fields, "offeringName");
        String description = FirestoreDocumentUtil.getString(fields, "description");

        String bookingType = FirestoreDocumentUtil.getString(fields, "bookingType");
        String pricingType = FirestoreDocumentUtil.getString(fields, "pricingType");
        long pricePaise = FirestoreDocumentUtil.getLong(fields, "pricePaise", 0L);

        String requiredResourceType = FirestoreDocumentUtil.getString(fields, "requiredResourceType");

        int defaultDurationMinutes = FirestoreDocumentUtil.getInt(fields, "defaultDurationMinutes", 60);
        int minimumDurationMinutes = FirestoreDocumentUtil.getInt(fields, "minimumDurationMinutes", 60);
        int maximumDurationMinutes = FirestoreDocumentUtil.getInt(fields, "maximumDurationMinutes", 480);
        int bookingIntervalMinutes = FirestoreDocumentUtil.getInt(fields, "bookingIntervalMinutes", 30);

        int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 1);
        int minimumParticipants = FirestoreDocumentUtil.getInt(fields, "minimumParticipants", 1);
        int maximumParticipants = FirestoreDocumentUtil.getInt(fields, "maximumParticipants", 1);

        List<String> includedItems = FirestoreDocumentUtil.getStringList(fields, "includedItems");
        List<String> rules = FirestoreDocumentUtil.getStringList(fields, "rules");

        String status = FirestoreDocumentUtil.getString(fields, "status");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);

        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        return new Offering(
                offeringId, spaceId, businessId, ownerId,
                offeringName, description, bookingType, pricingType, pricePaise,
                requiredResourceType, defaultDurationMinutes, minimumDurationMinutes,
                maximumDurationMinutes, bookingIntervalMinutes, capacity,
                minimumParticipants, maximumParticipants, includedItems, rules,
                status, active, createdAt, updatedAt);
    }
}
