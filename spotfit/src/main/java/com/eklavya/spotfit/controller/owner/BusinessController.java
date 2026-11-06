package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.session.AppSession;

import com.eklavya.spotfit.util.AuthenticatedExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller orchestrating Business domain actions for authenticated Space Owners.
 */
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController() {
        this.businessService = new BusinessService();
    }

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    public CompletableFuture<OperationResult<Business>> createBusiness(
            String businessName,
            String legalName,
            String businessEmail,
            String businessPhone,
            String description,
            String addressLine,
            String landmark,
            String city,
            String state,
            String postalCode,
            String registrationNumber) {

        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.createBusiness(
                ownerId, businessName, legalName, businessEmail, businessPhone,
                description, addressLine, landmark, city, state, postalCode,
                registrationNumber, token));
    }

    public CompletableFuture<OperationResult<Business>> createV2Business(Business submitted) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }
        if (submitted == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted business cannot be null."));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.createV2Business(
                ownerId,
                submitted.getBusinessName(),
                submitted.getLegalName(),
                submitted.getBusinessEmail(),
                submitted.getBusinessPhone(),
                submitted.getDescription(),
                submitted.getAddressLine(),
                submitted.getLandmark(),
                submitted.getCity(),
                submitted.getState(),
                submitted.getPostalCode(),
                submitted.getRegistrationNumber(),
                submitted.getCategory(),
                submitted.getActivityGroups(),
                "", // logoUrl is deferred to ""
                submitted.getWebsite(),
                token
        ));
    }

    public CompletableFuture<OperationResult<List<Business>>> loadCurrentOwnerBusinesses() {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.getBusinessesForOwner(ownerId, token));
    }

    public CompletableFuture<OperationResult<Business>> loadBusiness(String businessId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        return AuthenticatedExecutor.execute(token -> businessService.getBusiness(businessId, token))
                .thenApply(res -> {
                    if (res.isSuccess() && res.getData() != null) {
                        Business b = res.getData();
                        if (!AppSession.getUserId().equals(b.getOwnerId())) {
                            return OperationResult.failure(OperationCode.PERMISSION_DENIED,
                                    "Access denied. You do not own this business.");
                        }
                    }
                    return res;
                });
    }

    public CompletableFuture<OperationResult<Business>> updateBusiness(Business submittedBusiness) {
        if (submittedBusiness != null && submittedBusiness.getSchemaVersion() == 2) {
            return updateV2Business(submittedBusiness.getBusinessId(), submittedBusiness);
        }
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (submittedBusiness == null || submittedBusiness.getBusinessId() == null || submittedBusiness.getBusinessId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid business submission."));
        }

        String currentUserId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.getBusiness(submittedBusiness.getBusinessId(), token))
                .thenCompose(loadRes -> {
                    if (loadRes.isFailure() || loadRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(loadRes.getErrorCode(),
                                        "Cannot update business: " + loadRes.getMessage()));
                    }

                    Business existing = loadRes.getData();

                    if (!currentUserId.equals(existing.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. You cannot modify another owner's business."));
                    }

                    if (existing.getSchemaVersion() == 2) {
                        return updateV2Business(existing.getBusinessId(), submittedBusiness);
                    }

                    if (!BusinessStatus.DRAFT.equalsIgnoreCase(existing.getVerificationStatus())
                            && !BusinessStatus.REJECTED.equalsIgnoreCase(existing.getVerificationStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(OperationCode.PERMISSION_DENIED,
                                        "Only DRAFT or REJECTED businesses can be edited."));
                    }

                    existing.setBusinessName(submittedBusiness.getBusinessName());
                    existing.setLegalName(submittedBusiness.getLegalName());
                    existing.setBusinessEmail(submittedBusiness.getBusinessEmail());
                    existing.setBusinessPhone(submittedBusiness.getBusinessPhone());
                    existing.setDescription(submittedBusiness.getDescription());
                    existing.setAddressLine(submittedBusiness.getAddressLine());
                    existing.setLandmark(submittedBusiness.getLandmark());
                    existing.setCity(submittedBusiness.getCity());
                    existing.setState(submittedBusiness.getState());
                    existing.setPostalCode(submittedBusiness.getPostalCode());
                    existing.setRegistrationNumber(submittedBusiness.getRegistrationNumber());
                    existing.setUpdatedAt(System.currentTimeMillis());

                    return AuthenticatedExecutor.execute(token -> businessService.updateBusiness(existing, token));
                });
    }

    public CompletableFuture<OperationResult<Business>> updateV2Business(String businessId, Business submittedBusiness) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (businessId == null || businessId.isBlank() || submittedBusiness == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid business submission."));
        }

        String currentUserId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.getBusiness(businessId.trim(), token))
                .thenCompose(loadRes -> {
                    if (loadRes.isFailure() || loadRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(loadRes.getErrorCode(),
                                        "Cannot update business: " + loadRes.getMessage()));
                    }

                    Business existing = loadRes.getData();

                    if (!currentUserId.equals(existing.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. You cannot modify another owner's business."));
                    }

                    if (existing.getSchemaVersion() == 1) {
                        return AuthenticatedExecutor.execute(token -> businessService.updateBusiness(submittedBusiness, token));
                    }

                    String status = existing.getVerificationStatus();
                    if (!BusinessStatus.DRAFT.equalsIgnoreCase(status) && !BusinessStatus.REJECTED.equalsIgnoreCase(status)) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Business>failure(OperationCode.PERMISSION_DENIED,
                                        "Cannot edit business in status: " + status));
                    }

                    existing.setBusinessName(submittedBusiness.getBusinessName());
                    existing.setLegalName(submittedBusiness.getLegalName());
                    existing.setBusinessEmail(submittedBusiness.getBusinessEmail());
                    existing.setBusinessPhone(submittedBusiness.getBusinessPhone());
                    existing.setDescription(submittedBusiness.getDescription());
                    existing.setAddressLine(submittedBusiness.getAddressLine());
                    existing.setLandmark(submittedBusiness.getLandmark());
                    existing.setCity(submittedBusiness.getCity());
                    existing.setState(submittedBusiness.getState());
                    existing.setPostalCode(submittedBusiness.getPostalCode());
                    existing.setRegistrationNumber(submittedBusiness.getRegistrationNumber());
                    existing.setCategory(submittedBusiness.getCategory());
                    existing.setActivityGroups(submittedBusiness.getActivityGroups());
                    existing.setWebsite(submittedBusiness.getWebsite());
                    existing.setLogoUrl("");

                    if (BusinessStatus.REJECTED.equalsIgnoreCase(status)) {
                        existing.setVerificationStatus(BusinessStatus.DRAFT);
                        existing.setReviewedAt(0L);
                        existing.setRejectionReason("");
                    }

                    existing.setUpdatedAt(System.currentTimeMillis());

                    return AuthenticatedExecutor.execute(token -> businessService.updateV2Business(existing, token));
                });
    }

    private OperationResult<Void> validateOwnerSession() {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only authenticated Space Owners can perform this action.");
        }
        if (AppSession.getUserId() == null || AppSession.getUserId().isBlank() || !AppSession.hasIdToken()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Invalid session tokens.");
        }
        return OperationResult.success(null, "Session valid.");
    }
}
