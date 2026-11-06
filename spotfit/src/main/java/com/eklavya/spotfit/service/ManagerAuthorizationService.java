package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;

/**
 * Reusable backend authorization logic for Manager operational access checks.
 */
public class ManagerAuthorizationService {

    public boolean isAssignedToSpace(Manager manager, String spaceId) {
        if (manager == null || !manager.isActive()) {
            return false;
        }
        if (spaceId == null || spaceId.isBlank()) {
            return false;
        }
        return manager.getAssignedSpaceIds() != null && manager.getAssignedSpaceIds().contains(spaceId.trim());
    }

    public boolean hasPermission(Manager manager, String permission) {
        if (manager == null || !manager.isActive()) {
            return false;
        }
        if (permission == null || permission.isBlank()) {
            return false;
        }
        return manager.getPermissions() != null && manager.getPermissions().contains(permission.trim());
    }

    public OperationResult<Void> canManageOperations(Manager manager, String spaceId) {
        if (!isAssignedToSpace(manager, spaceId)) {
            return OperationResult.failure("SPACE_NOT_ASSIGNED", "Manager is not assigned to Space '" + spaceId + "'.");
        }
        if (!hasPermission(manager, ManagerPermission.EDIT_SPACE_OPERATIONS)) {
            return OperationResult.failure("PERMISSION_DENIED", "Manager lacks 'EDIT_SPACE_OPERATIONS' permission.");
        }
        return OperationResult.success(null);
    }

    public OperationResult<Void> canManageResources(Manager manager, String spaceId) {
        if (!isAssignedToSpace(manager, spaceId)) {
            return OperationResult.failure("SPACE_NOT_ASSIGNED", "Manager is not assigned to Space '" + spaceId + "'.");
        }
        if (!hasPermission(manager, ManagerPermission.MANAGE_RESOURCES)) {
            return OperationResult.failure("PERMISSION_DENIED", "Manager lacks 'MANAGE_RESOURCES' permission.");
        }
        return OperationResult.success(null);
    }

    public OperationResult<Void> canManageAvailability(Manager manager, String spaceId) {
        if (!isAssignedToSpace(manager, spaceId)) {
            return OperationResult.failure("SPACE_NOT_ASSIGNED", "Manager is not assigned to Space '" + spaceId + "'.");
        }
        if (!hasPermission(manager, ManagerPermission.MANAGE_AVAILABILITY)) {
            return OperationResult.failure("PERMISSION_DENIED", "Manager lacks 'MANAGE_AVAILABILITY' permission.");
        }
        return OperationResult.success(null);
    }

    public OperationResult<Void> authorizeSpaceAccess(Manager manager, String spaceId) {
        if (manager == null) {
            return OperationResult.failure("AUTHENTICATION_REQUIRED", "Manager profile is required.");
        }
        if (!manager.isActive()) {
            return OperationResult.failure("ACCOUNT_DEACTIVATED", "Manager account is inactive.");
        }
        if (!isAssignedToSpace(manager, spaceId)) {
            return OperationResult.failure("SPACE_ACCESS_DENIED", "Manager is not assigned to Space '" + spaceId + "'.");
        }
        return OperationResult.success(null);
    }

    public OperationResult<Void> authorizePermission(Manager manager, String spaceId, String permission) {
        OperationResult<Void> spaceAuth = authorizeSpaceAccess(manager, spaceId);
        if (spaceAuth.isFailure()) {
            return spaceAuth;
        }
        if (!hasPermission(manager, permission)) {
            return OperationResult.failure("PERMISSION_DENIED", "Manager lacks permission '" + permission + "'.");
        }
        return OperationResult.success(null);
    }

    /**
     * Authoritative V2 Manager operation authorization check.
     */
    public OperationResult<Void> authorizeAssignedV2Space(Manager manager, Space space, String requiredPermission) {
        if (manager == null) {
            return OperationResult.failure("AUTHENTICATION_REQUIRED", "Manager profile authentication required.");
        }
        if (!manager.isActive()) {
            return OperationResult.failure("MANAGER_INACTIVE", "Manager account is inactive.");
        }
        if (space == null) {
            return OperationResult.failure("NOT_FOUND", "Requested Space does not exist.");
        }
        if (space.getSchemaVersion() != 2) {
            return OperationResult.failure("SCHEMA_VERSION_MISMATCH", "Target Space is not schema version 2.");
        }
        if (!space.isActive()) {
            return OperationResult.failure("NOT_FOUND", "Space is deactivated.");
        }
        if (!isAssignedToSpace(manager, space.getSpaceId())) {
            return OperationResult.failure("SPACE_NOT_ASSIGNED", "Manager is not assigned to Space '" + space.getSpaceId() + "'.");
        }
        if (requiredPermission != null && !requiredPermission.isBlank()) {
            if (!hasPermission(manager, requiredPermission)) {
                return OperationResult.failure("PERMISSION_DENIED", "Manager lacks required permission '" + requiredPermission + "'.");
            }
        }
        return OperationResult.success(null);
    }

    public boolean canPerformPriceChange(Manager manager) {
        return false; // Price changes are strictly forbidden for Space Managers
    }
}

