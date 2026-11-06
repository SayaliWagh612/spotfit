package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.OfferingService;
import com.eklavya.spotfit.service.ResourceService;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Controller enforcing Owner session security, compatible resource checks, and cross-document validation for Offerings.
 */
public class OfferingController {

    private final OfferingService offeringService;
    private final ResourceService resourceService;
    private final SpaceService spaceService;

    public OfferingController() {
        this.offeringService = new OfferingService();
        this.resourceService = new ResourceService();
        this.spaceService = new SpaceService();
    }

    public OfferingController(OfferingService offeringService, ResourceService resourceService, SpaceService spaceService) {
        this.offeringService = offeringService;
        this.resourceService = resourceService;
        this.spaceService = spaceService;
    }

    public CompletableFuture<OperationResult<Offering>> createOffering(
            String spaceId,
            String offeringName,
            String description,
            String bookingType,
            String pricingType,
            String priceRupeesText,
            String requiredResourceType,
            int defaultDurationMinutes,
            int minimumDurationMinutes,
            int maximumDurationMinutes,
            int bookingIntervalMinutes,
            int capacity,
            int minimumParticipants,
            int maximumParticipants,
            List<String> includedItems,
            List<String> rules) {

        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceService.getSpace(spaceId, idToken).thenCompose(spaceRes -> {
                if (spaceRes.isFailure() || spaceRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NOT_FOUND, "Target Space not found."));
                }

                Space space = spaceRes.getData();
                if (!ownerId.equals(space.getOwnerId())) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.PERMISSION_DENIED, "Space does not belong to current Owner."));
                }

                return resourceService.getResourcesForSpace(spaceId, ownerId, idToken).thenCompose(resResult -> {
                    if (resResult.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(resResult.getErrorCode(), resResult.getMessage()));
                    }

                    List<Resource> resources = resResult.getData();
                    boolean hasCompatibleResource = resources != null && resources.stream()
                            .anyMatch(r -> r.isActive() && requiredResourceType != null && requiredResourceType.equalsIgnoreCase(r.getResourceType()));

                    if (!hasCompatibleResource) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("COMPATIBLE_RESOURCE_REQUIRED",
                                        "Create at least one compatible Resource before creating this Offering."));
                    }

                    String businessId = space.getBusinessId();
                    return offeringService.createOffering(
                            ownerId, businessId, spaceId,
                            offeringName, description, bookingType, pricingType,
                            priceRupeesText, requiredResourceType,
                            defaultDurationMinutes, minimumDurationMinutes, maximumDurationMinutes,
                            bookingIntervalMinutes, capacity, minimumParticipants, maximumParticipants,
                            includedItems, rules, idToken);
                });
            })
        );
    }

    public CompletableFuture<OperationResult<List<Resource>>> loadCompatibleResources(String spaceId, String requiredResourceType) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            resourceService.getResourcesForSpace(spaceId, ownerId, idToken)
                    .thenApply(res -> {
                        if (res.isSuccess() && res.getData() != null) {
                            List<Resource> compatible = res.getData().stream()
                                    .filter(r -> r.isActive() && requiredResourceType != null && requiredResourceType.equalsIgnoreCase(r.getResourceType()))
                                    .collect(Collectors.toList());
                            return OperationResult.success(compatible);
                        } else {
                            return OperationResult.success(List.of());
                        }
                    })
        );
    }

    public CompletableFuture<OperationResult<List<Offering>>> loadOfferingsForSpace(String spaceId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            offeringService.getOfferingsForSpace(spaceId, ownerId, idToken)
        );
    }

    public CompletableFuture<OperationResult<Offering>> updateOffering(Offering submittedOffering) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (submittedOffering == null || submittedOffering.getOfferingId() == null || submittedOffering.getSpaceId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Offering object and spaceId are required."));
        }

        String ownerId = AppSession.getUserId();
        String spaceId = submittedOffering.getSpaceId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            offeringService.getOffering(spaceId, submittedOffering.getOfferingId(), idToken).thenCompose(existingRes -> {
                if (existingRes.isFailure() || existingRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NOT_FOUND, "Existing Offering not found."));
                }

                Offering existing = existingRes.getData();
                if (!ownerId.equals(existing.getOwnerId())) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.PERMISSION_DENIED, "Offering does not belong to current Owner."));
                }

                return spaceService.getSpace(existing.getSpaceId(), idToken).thenCompose(spaceRes -> {
                    if (spaceRes.isFailure() || spaceRes.getData() == null) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.NOT_FOUND, "Target Space not found."));
                    }

                    Space space = spaceRes.getData();
                    if (!ownerId.equals(space.getOwnerId())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.PERMISSION_DENIED, "Parent Space does not belong to current Owner."));
                    }

                    return resourceService.getResourcesForSpace(existing.getSpaceId(), ownerId, idToken).thenCompose(resResult -> {
                        if (resResult.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(resResult.getErrorCode(), resResult.getMessage()));
                        }

                        List<Resource> resources = resResult.getData();
                        String reqType = submittedOffering.getRequiredResourceType();
                        boolean hasCompatibleResource = resources != null && resources.stream()
                                .anyMatch(r -> r.isActive() && reqType != null && reqType.equalsIgnoreCase(r.getResourceType()));

                        if (!hasCompatibleResource) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure("COMPATIBLE_RESOURCE_REQUIRED",
                                            "Create at least one compatible Resource before updating this Offering to target " + reqType + "."));
                        }

                        Offering updated = new Offering();
                        updated.setOfferingId(existing.getOfferingId());
                        updated.setOwnerId(existing.getOwnerId());
                        updated.setBusinessId(existing.getBusinessId());
                        updated.setSpaceId(existing.getSpaceId());
                        updated.setCreatedAt(existing.getCreatedAt());
                        updated.setActive(existing.isActive());
                        updated.setStatus(existing.getStatus());

                        updated.setOfferingName(submittedOffering.getOfferingName());
                        updated.setDescription(submittedOffering.getDescription());
                        updated.setBookingType(submittedOffering.getBookingType());
                        updated.setPricingType(submittedOffering.getPricingType());
                        updated.setPricePaise(submittedOffering.getPricePaise());
                        updated.setRequiredResourceType(submittedOffering.getRequiredResourceType());
                        updated.setDefaultDurationMinutes(submittedOffering.getDefaultDurationMinutes());
                        updated.setMinimumDurationMinutes(submittedOffering.getMinimumDurationMinutes());
                        updated.setMaximumDurationMinutes(submittedOffering.getMaximumDurationMinutes());
                        updated.setBookingIntervalMinutes(submittedOffering.getBookingIntervalMinutes());
                        updated.setCapacity(submittedOffering.getCapacity());
                        updated.setMinimumParticipants(submittedOffering.getMinimumParticipants());
                        updated.setMaximumParticipants(submittedOffering.getMaximumParticipants());
                        updated.setIncludedItems(submittedOffering.getIncludedItems());
                        updated.setRules(submittedOffering.getRules());

                        return offeringService.updateOffering(existing.getSpaceId(), updated, idToken);
                    });
                });
            })
        );
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
