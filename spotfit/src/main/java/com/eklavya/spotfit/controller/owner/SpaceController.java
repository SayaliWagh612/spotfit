package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;

import com.eklavya.spotfit.util.AuthenticatedExecutor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller orchestrating Space domain actions for authenticated Space Owners across V1 and V2 schemas.
 */
public class SpaceController {

    private final SpaceService spaceService;
    private final BusinessService businessService;

    public SpaceController() {
        this.spaceService = new SpaceService();
        this.businessService = new BusinessService();
    }

    public SpaceController(SpaceService spaceService, BusinessService businessService) {
        this.spaceService = spaceService;
        this.businessService = businessService;
    }

    // ==================== V1 Controller Methods ====================

    public CompletableFuture<OperationResult<List<Business>>> loadBusinessChoices() {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.getBusinessesForOwner(ownerId, token));
    }

    public CompletableFuture<OperationResult<Space>> createSpace(
            String businessId,
            String spaceName,
            String description,
            String category,
            String activityGroup,
            String spaceType,
            String addressLine,
            String landmark,
            String city,
            String state,
            String postalCode,
            int capacity,
            List<String> amenities,
            List<String> suitableActivities,
            List<String> rules,
            String openingTime,
            String closingTime,
            List<String> operatingDays) {

        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Please select a Business."));
        }

        String ownerId = AppSession.getUserId();

        // Validate Business ownership before creating Space
        return AuthenticatedExecutor.execute(token -> businessService.getBusiness(businessId, token))
                .thenCompose(bRes -> {
                    if (bRes.isFailure() || bRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(bRes.getErrorCode(),
                                        "Selected Business was not found."));
                    }

                    Business business = bRes.getData();
                    if (!ownerId.equals(business.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "You can only create a Space for a Business you own."));
                    }

                    if (!business.isActive()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Cannot create a Space for an inactive Business."));
                    }

                    return AuthenticatedExecutor.execute(token -> spaceService.createSpace(
                            ownerId, businessId, spaceName, description, category, activityGroup, spaceType,
                            addressLine, landmark, city, state, postalCode, capacity,
                            amenities, suitableActivities, rules, openingTime, closingTime, operatingDays,
                            token));
                });
    }

    public CompletableFuture<OperationResult<List<Space>>> loadCurrentOwnerSpaces() {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> spaceService.getSpacesForOwner(ownerId, token));
    }

    public CompletableFuture<OperationResult<List<Space>>> loadSpacesForBusiness(String businessId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> businessService.getBusiness(businessId, token))
                .thenCompose(bRes -> {
                    if (bRes.isFailure() || bRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<List<Space>>failure(bRes.getErrorCode(), "Business not found."));
                    }

                    Business b = bRes.getData();
                    if (!ownerId.equals(b.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<List<Space>>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. You do not own this Business."));
                    }

                    return AuthenticatedExecutor.execute(token -> spaceService.getSpacesForOwnerAndBusiness(ownerId, businessId, token));
                });
    }

    public CompletableFuture<OperationResult<Space>> loadSpace(String spaceId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return AuthenticatedExecutor.execute(token -> spaceService.getSpace(spaceId, token))
                .thenApply(res -> {
                    if (res.isSuccess() && res.getData() != null) {
                        Space space = res.getData();
                        if (!AppSession.getUserId().equals(space.getOwnerId())) {
                            return OperationResult.failure(OperationCode.PERMISSION_DENIED,
                                    "Access denied. You do not own this Space.");
                        }
                    }
                    return res;
                });
    }

    public CompletableFuture<OperationResult<Space>> updateSpace(Space submittedSpace) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (submittedSpace == null || submittedSpace.getSpaceId() == null || submittedSpace.getSpaceId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid space submission."));
        }

        String currentUserId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> spaceService.getSpace(submittedSpace.getSpaceId(), token))
                .thenCompose(loadRes -> {
                    if (loadRes.isFailure() || loadRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(loadRes.getErrorCode(),
                                        "Cannot update space: " + loadRes.getMessage()));
                    }

                    Space existing = loadRes.getData();

                    // Confirm ownership
                    if (!currentUserId.equals(existing.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. You cannot modify another owner's space."));
                    }

                    // Confirm DRAFT or REJECTED status
                    if (!SpaceStatus.DRAFT.equalsIgnoreCase(existing.getStatus())
                            && !SpaceStatus.REJECTED.equalsIgnoreCase(existing.getStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Only DRAFT or REJECTED spaces can be edited."));
                    }

                    // Check if businessId is changing
                    CompletableFuture<OperationResult<Void>> businessCheck;
                    if (submittedSpace.getBusinessId() != null && !submittedSpace.getBusinessId().equals(existing.getBusinessId())) {
                        businessCheck = AuthenticatedExecutor.execute(token -> businessService.getBusiness(submittedSpace.getBusinessId(), token))
                                .thenApply(bRes -> {
                                    if (bRes.isFailure() || bRes.getData() == null) {
                                        return OperationResult.<Void>failure(bRes.getErrorCode(), "New Business not found.");
                                    }
                                    Business b = bRes.getData();
                                    if (!currentUserId.equals(b.getOwnerId()) || !b.isActive()) {
                                        return OperationResult.<Void>failure(OperationCode.PERMISSION_DENIED, "Target Business is invalid or not owned by you.");
                                    }
                                    return OperationResult.success(null);
                                });
                    } else {
                        businessCheck = CompletableFuture.completedFuture(OperationResult.success(null));
                    }

                    return businessCheck.thenCompose(bCheck -> {
                        if (bCheck.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.<Space>failure(bCheck.getErrorCode(), bCheck.getMessage()));
                        }

                        // Copy editable fields
                        if (submittedSpace.getBusinessId() != null) {
                            existing.setBusinessId(submittedSpace.getBusinessId());
                        }
                        existing.setSpaceName(submittedSpace.getSpaceName());
                        existing.setDescription(submittedSpace.getDescription());
                        existing.setCategory(submittedSpace.getCategory());
                        existing.setActivityGroup(submittedSpace.getActivityGroup());
                        existing.setSpaceType(submittedSpace.getSpaceType());
                        existing.setAddressLine(submittedSpace.getAddressLine());
                        existing.setLandmark(submittedSpace.getLandmark());
                        existing.setCity(submittedSpace.getCity());
                        existing.setState(submittedSpace.getState());
                        existing.setPostalCode(submittedSpace.getPostalCode());
                        existing.setCapacity(submittedSpace.getCapacity());
                        existing.setAmenities(submittedSpace.getAmenities());
                        existing.setSuitableActivities(submittedSpace.getSuitableActivities());
                        existing.setRules(submittedSpace.getRules());
                        existing.setOpeningTime(submittedSpace.getOpeningTime());
                        existing.setClosingTime(submittedSpace.getClosingTime());
                        existing.setOperatingDays(submittedSpace.getOperatingDays());

                        // Preserve immutable fields: spaceId, ownerId, status, active, createdAt
                        existing.setUpdatedAt(System.currentTimeMillis());

                        return AuthenticatedExecutor.execute(token -> spaceService.updateSpace(existing, token));
                    });
                });
    }

    // ==================== V2 Controller Methods ====================

    public CompletableFuture<OperationResult<Space>> createV2Space(String businessId, Space submittedSpace) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (businessId == null || businessId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (submittedSpace == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted Space cannot be null."));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> spaceService.createV2Space(ownerId, businessId, submittedSpace, token));
    }

    public CompletableFuture<OperationResult<Space>> loadV2Space(String spaceId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> spaceService.getSpace(spaceId, token))
                .thenApply(res -> {
                    if (res.isFailure() || res.getData() == null) {
                        return res;
                    }
                    Space space = res.getData();
                    if (!ownerId.equals(space.getOwnerId())) {
                        return OperationResult.failure(OperationCode.PERMISSION_DENIED,
                                "Access denied. You do not own this Space.");
                    }
                    if (space.getSchemaVersion() != 2) {
                        return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH,
                                "Requested space is V1 schema (schemaVersion != 2).");
                    }
                    return res;
                });
    }

    public CompletableFuture<OperationResult<Space>> updateV2Space(String spaceId, Space submittedSpace) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (submittedSpace == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Submitted Space cannot be null."));
        }

        String ownerId = AppSession.getUserId();

        return AuthenticatedExecutor.execute(token -> spaceService.getSpace(spaceId, token))
                .thenCompose(loadRes -> {
                    if (loadRes.isFailure() || loadRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(loadRes.getErrorCode(),
                                        "Cannot update Space: " + loadRes.getMessage()));
                    }

                    Space existing = loadRes.getData();
                    if (!ownerId.equals(existing.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.PERMISSION_DENIED,
                                        "Access denied. You cannot modify another owner's Space."));
                    }
                    if (existing.getSchemaVersion() != 2) {
                        return CompletableFuture.completedFuture(
                                OperationResult.<Space>failure(OperationCode.SCHEMA_VERSION_MISMATCH,
                                        "Target Space is not V2 schema."));
                    }

                    return AuthenticatedExecutor.execute(token -> spaceService.updateV2Space(ownerId, existing, submittedSpace, token));
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
