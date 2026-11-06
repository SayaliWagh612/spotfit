package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Firestore DAO for managing Business document persistence.
 */
public class BusinessDao {

    private final FirestoreRestClient firestoreClient;

    public BusinessDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public BusinessDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = firestoreClient;
    }

    public CompletableFuture<OperationResult<Business>> createBusiness(Business business, String idToken) {
        if (business == null || business.getBusinessId() == null || business.getBusinessId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(business);
        return firestoreClient.setDocument(CollectionName.BUSINESSES, business.getBusinessId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(business, "Business created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.getDocument(CollectionName.BUSINESSES, businessId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Business business = fromFirestoreDocument(res.getData());
                        return OperationResult.success(business);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Business>> getPublicBusiness(String businessId) {
        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        return firestoreClient.getPublicDocumentBySegments(List.of(CollectionName.BUSINESSES, businessId.trim()))
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Business business = fromFirestoreDocument(res.getData());
                        return OperationResult.success(business);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Business>>> getBusinessesByOwner(String ownerId, String idToken) {
        if (ownerId == null || ownerId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        return firestoreClient.queryDocuments(CollectionName.BUSINESSES, "ownerId", ownerId, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Business> businesses = new ArrayList<>();
                        List<JSONObject> docs = res.getData();
                        if (docs != null) {
                            for (JSONObject doc : docs) {
                                Business b = fromFirestoreDocument(doc);
                                if (b != null) {
                                    businesses.add(b);
                                }
                            }
                        }
                        return OperationResult.success(businesses);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Business>> updateBusiness(Business business, String idToken) {
        if (business == null || business.getBusinessId() == null || business.getBusinessId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject doc = toFirestoreDocument(business);
        return firestoreClient.setDocument(CollectionName.BUSINESSES, business.getBusinessId(), doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(business, "Business updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Business>> submitBusiness(String businessId, long submittedAt,
            long updatedAt, String idToken) {
        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        JSONObject fields = new JSONObject();
        fields.put("verificationStatus", FirestoreDocumentUtil.stringValue(BusinessStatus.SUBMITTED));
        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(submittedAt));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(0L));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(""));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(updatedAt));

        JSONObject doc = FirestoreDocumentUtil.createDocument(fields);
        List<String> updateMask = List.of("verificationStatus", "submittedAt", "reviewedAt", "rejectionReason",
                "updatedAt");

        return firestoreClient.updateDocument(CollectionName.BUSINESSES, businessId, doc, updateMask, idToken)
                .thenCompose(res -> {
                    if (res.isSuccess()) {
                        return getBusiness(businessId, idToken);
                    } else {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                });
    }

    public JSONObject toFirestoreDocument(Business business) {
        JSONObject fields = new JSONObject();
        fields.put("businessId", FirestoreDocumentUtil.stringValue(business.getBusinessId()));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue(business.getOwnerId()));

        fields.put("businessName", FirestoreDocumentUtil.stringValue(business.getBusinessName()));
        fields.put("legalName", FirestoreDocumentUtil.stringValue(business.getLegalName()));
        fields.put("businessEmail", FirestoreDocumentUtil.stringValue(business.getBusinessEmail()));
        fields.put("businessPhone", FirestoreDocumentUtil.stringValue(business.getBusinessPhone()));

        fields.put("description", FirestoreDocumentUtil.stringValue(business.getDescription()));

        fields.put("addressLine", FirestoreDocumentUtil.stringValue(business.getAddressLine()));
        fields.put("landmark", FirestoreDocumentUtil.stringValue(business.getLandmark()));
        fields.put("city", FirestoreDocumentUtil.stringValue(business.getCity()));
        fields.put("state", FirestoreDocumentUtil.stringValue(business.getState()));
        fields.put("postalCode", FirestoreDocumentUtil.stringValue(business.getPostalCode()));

        fields.put("registrationNumber", FirestoreDocumentUtil.stringValue(business.getRegistrationNumber()));

        fields.put("verificationStatus", FirestoreDocumentUtil.stringValue(business.getVerificationStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(business.isActive()));

        fields.put("submittedAt", FirestoreDocumentUtil.integerValue(business.getSubmittedAt()));
        fields.put("reviewedAt", FirestoreDocumentUtil.integerValue(business.getReviewedAt()));
        fields.put("rejectionReason", FirestoreDocumentUtil.stringValue(business.getRejectionReason()));

        fields.put("createdAt", FirestoreDocumentUtil.integerValue(business.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(business.getUpdatedAt()));

        if (business.getSchemaVersion() == 2) {
            fields.put("schemaVersion", FirestoreDocumentUtil.integerValue(2L));
            fields.put("category", FirestoreDocumentUtil.stringValue(business.getCategory()));
            fields.put("activityGroups", FirestoreDocumentUtil.stringArrayValue(business.getActivityGroups()));
            fields.put("logoUrl", FirestoreDocumentUtil.stringValue(business.getLogoUrl()));
            fields.put("website", FirestoreDocumentUtil.stringValue(business.getWebsite()));
        }

        return FirestoreDocumentUtil.createDocument(fields);
    }

    public Business fromFirestoreDocument(JSONObject document) {
        JSONObject fields = FirestoreDocumentUtil.getFields(document);

        int schemaVersion = FirestoreDocumentUtil.getInt(fields, "schemaVersion", 1);
        if (schemaVersion < 1 || schemaVersion > 2) {
            schemaVersion = 1;
        }

        String businessId = FirestoreDocumentUtil.getString(fields, "businessId");
        String ownerId = FirestoreDocumentUtil.getString(fields, "ownerId");

        String businessName = FirestoreDocumentUtil.getString(fields, "businessName");
        String legalName = FirestoreDocumentUtil.getString(fields, "legalName");
        String businessEmail = FirestoreDocumentUtil.getString(fields, "businessEmail");
        String businessPhone = FirestoreDocumentUtil.getString(fields, "businessPhone");

        String description = FirestoreDocumentUtil.getString(fields, "description");

        String addressLine = FirestoreDocumentUtil.getString(fields, "addressLine");
        String landmark = FirestoreDocumentUtil.getString(fields, "landmark");
        String city = FirestoreDocumentUtil.getString(fields, "city");
        String state = FirestoreDocumentUtil.getString(fields, "state");
        String postalCode = FirestoreDocumentUtil.getString(fields, "postalCode");

        String registrationNumber = FirestoreDocumentUtil.getString(fields, "registrationNumber");

        String verificationStatus = FirestoreDocumentUtil.getString(fields, "verificationStatus");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);

        long submittedAt = FirestoreDocumentUtil.getLong(fields, "submittedAt", 0L);
        long reviewedAt = FirestoreDocumentUtil.getLong(fields, "reviewedAt", 0L);
        String rejectionReason = FirestoreDocumentUtil.getString(fields, "rejectionReason");

        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        String category = FirestoreDocumentUtil.getString(fields, "category");
        List<String> activityGroups = FirestoreDocumentUtil.getStringList(fields, "activityGroups");
        String logoUrl = FirestoreDocumentUtil.getString(fields, "logoUrl");
        String website = FirestoreDocumentUtil.getString(fields, "website");

        return new Business(
                schemaVersion, businessId, ownerId, businessName, legalName, businessEmail, businessPhone,
                description, addressLine, landmark, city, state, postalCode,
                registrationNumber, category, activityGroups, logoUrl, website, verificationStatus, active,
                submittedAt, reviewedAt, rejectionReason, createdAt, updatedAt);
    }
}
