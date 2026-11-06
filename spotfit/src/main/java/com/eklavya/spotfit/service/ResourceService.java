package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.ResourceStatus;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.dao.ResourceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service enforcing domain validation and business rules for Resource management.
 */
public class ResourceService {

    private final ResourceDao resourceDao;

    public ResourceService() {
        this.resourceDao = new ResourceDao();
    }

    public ResourceService(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
    }

    public CompletableFuture<OperationResult<Resource>> createResource(
            String ownerId,
            String businessId,
            String spaceId,
            String resourceName,
            String resourceCode,
            String resourceType,
            String description,
            int capacity,
            List<String> equipment,
            List<String> amenities,
            String idToken) {

        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Owner ID is required."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        OperationResult<Void> validation = validateResourceInputs(
                resourceName, resourceCode, resourceType, capacity);
        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        int finalCap = capacity;
        if (ResourceType.SEAT.equalsIgnoreCase(resourceType) && capacity <= 0) {
            finalCap = 1;
        }

        String resourceId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        Resource resource = new Resource(
                resourceId,
                spaceId.trim(),
                businessId.trim(),
                ownerId.trim(),
                ValidationUtil.sanitizeText(resourceName),
                ValidationUtil.sanitizeText(description),
                resourceType.trim(),
                resourceCode.trim().toUpperCase(),
                finalCap,
                equipment != null ? equipment : new ArrayList<>(),
                amenities != null ? amenities : new ArrayList<>(),
                ResourceStatus.AVAILABLE,
                true,
                now,
                now
        );

        return resourceDao.createResource(spaceId.trim(), resource, idToken);
    }

    public CompletableFuture<OperationResult<Resource>> getResource(String spaceId, String resourceId, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        return resourceDao.getResource(spaceId.trim(), resourceId, idToken);
    }

    public CompletableFuture<OperationResult<List<Resource>>> getResourcesForSpace(String spaceId, String ownerId, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return resourceDao.getResourcesForSpace(spaceId.trim(), idToken);
    }

    public CompletableFuture<OperationResult<Resource>> updateResource(String spaceId, Resource resource, String idToken) {
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (resource == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource object cannot be null."));
        }
        if (ValidationUtil.isBlank(resource.getSpaceId())) {
            resource.setSpaceId(spaceId.trim());
        } else if (!spaceId.trim().equals(resource.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Cannot move Resource to a different Space."));
        }

        OperationResult<Void> validation = validateResourceInputs(
                resource.getResourceName(),
                resource.getResourceCode(),
                resource.getResourceType(),
                resource.getCapacity());

        if (validation.isFailure()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(validation.getErrorCode(), validation.getMessage()));
        }

        String st = resource.getStatus();
        if (ResourceStatus.RESERVED.equalsIgnoreCase(st) || ResourceStatus.OCCUPIED.equalsIgnoreCase(st)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.PERMISSION_DENIED,
                            "Cannot manually set status to RESERVED or OCCUPIED."));
        }

        resource.setResourceName(ValidationUtil.sanitizeText(resource.getResourceName()));
        resource.setResourceCode(resource.getResourceCode().trim().toUpperCase());
        resource.setDescription(ValidationUtil.sanitizeText(resource.getDescription()));
        resource.setUpdatedAt(System.currentTimeMillis());

        return resourceDao.updateResource(spaceId.trim(), resource, idToken);
    }

    public CompletableFuture<OperationResult<Resource>> updateResource(Resource resource, String idToken) {
        if (resource == null || ValidationUtil.isBlank(resource.getSpaceId())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Resource and spaceId are required."));
        }
        return updateResource(resource.getSpaceId(), resource, idToken);
    }

    public OperationResult<Void> validateResourceInputs(
            String resourceName,
            String resourceCode,
            String resourceType,
            int capacity) {

        if (ValidationUtil.isBlank(resourceName)) {
            return OperationResult.failure("RESOURCE_NAME_REQUIRED", "Resource name is required.");
        }
        if (ValidationUtil.isBlank(resourceCode)) {
            return OperationResult.failure("RESOURCE_CODE_REQUIRED", "Resource code is required.");
        }
        if (ValidationUtil.isBlank(resourceType) || !ResourceType.isValid(resourceType)) {
            return OperationResult.failure("RESOURCE_TYPE_INVALID", "Please select a valid resource type.");
        }
        if (capacity <= 0) {
            return OperationResult.failure("CAPACITY_INVALID", "Resource capacity must be greater than zero.");
        }

        return OperationResult.success(null, "Resource inputs are valid.");
    }
}
