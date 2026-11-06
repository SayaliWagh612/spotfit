package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.ResourceService;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller enforcing Owner session security and cross-document validation for Resources.
 */
public class ResourceController {

    private final ResourceService resourceService;
    private final SpaceService spaceService;

    public ResourceController() {
        this.resourceService = new ResourceService();
        this.spaceService = new SpaceService();
    }

    public ResourceController(ResourceService resourceService, SpaceService spaceService) {
        this.resourceService = resourceService;
        this.spaceService = spaceService;
    }

    public CompletableFuture<OperationResult<Resource>> createResource(
            String spaceId,
            String resourceName,
            String resourceCode,
            String resourceType,
            String description,
            int capacity,
            List<String> equipment,
            List<String> amenities) {

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

                String businessId = space.getBusinessId();
                return resourceService.createResource(
                        ownerId, businessId, spaceId,
                        resourceName, resourceCode, resourceType,
                        description, capacity, equipment, amenities, idToken);
            })
        );
    }

    public CompletableFuture<OperationResult<List<Resource>>> loadResourcesForSpace(String spaceId) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        String ownerId = AppSession.getUserId();
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            resourceService.getResourcesForSpace(spaceId, ownerId, idToken)
        );
    }

    public CompletableFuture<OperationResult<Resource>> updateResource(Resource submittedResource) {
        OperationResult<Void> authCheck = validateOwnerSession();
        if (authCheck.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage()));
        }

        if (submittedResource == null || submittedResource.getResourceId() == null || submittedResource.getSpaceId() == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource object and spaceId are required."));
        }

        String ownerId = AppSession.getUserId();
        String spaceId = submittedResource.getSpaceId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            resourceService.getResource(spaceId, submittedResource.getResourceId(), idToken).thenCompose(existingRes -> {
                if (existingRes.isFailure() || existingRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NOT_FOUND, "Existing Resource not found."));
                }

                Resource existing = existingRes.getData();
                if (!ownerId.equals(existing.getOwnerId())) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.PERMISSION_DENIED, "Resource does not belong to current Owner."));
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

                    Resource updated = new Resource();
                    updated.setResourceId(existing.getResourceId());
                    updated.setOwnerId(existing.getOwnerId());
                    updated.setBusinessId(existing.getBusinessId());
                    updated.setSpaceId(existing.getSpaceId());
                    updated.setCreatedAt(existing.getCreatedAt());
                    updated.setActive(existing.isActive());

                    updated.setResourceName(submittedResource.getResourceName());
                    updated.setResourceCode(submittedResource.getResourceCode());
                    updated.setResourceType(submittedResource.getResourceType());
                    updated.setDescription(submittedResource.getDescription());
                    updated.setCapacity(submittedResource.getCapacity());
                    updated.setEquipment(submittedResource.getEquipment());
                    updated.setAmenities(submittedResource.getAmenities());
                    updated.setStatus(submittedResource.getStatus());

                    return resourceService.updateResource(existing.getSpaceId(), updated, idToken);
                });
            })
        );
    }

    private OperationResult<Void> validateOwnerSession() {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Action reserved for Owner role.");
        }
        return OperationResult.success(null, "Owner authenticated.");
    }
}
