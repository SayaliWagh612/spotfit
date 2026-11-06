package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerAuthorizationServiceTest {

    private ManagerAuthorizationService authService;
    private Manager activeManager;

    @BeforeEach
    public void setUp() {
        authService = new ManagerAuthorizationService();
        activeManager = new Manager();
        activeManager.setManagerId("mgr-001");
        activeManager.setRole(UserRole.MANAGER);
        activeManager.setActive(true);
        activeManager.setAssignedSpaceIds(List.of("sp-101", "sp-102"));
        activeManager.setPermissions(List.of(
                ManagerPermission.VIEW_ASSIGNED_SPACES,
                ManagerPermission.EDIT_SPACE_OPERATIONS,
                ManagerPermission.MANAGE_AVAILABILITY
        ));
    }

    @Test
    public void testAuthorizeSpaceAccess_Success() {
        OperationResult<Void> result = authService.authorizeSpaceAccess(activeManager, "sp-101");
        assertTrue(result.isSuccess());
    }

    @Test
    public void testAuthorizeSpaceAccess_UnassignedSpace_ReturnsError() {
        OperationResult<Void> result = authService.authorizeSpaceAccess(activeManager, "sp-999");
        assertTrue(result.isFailure());
        assertEquals("SPACE_ACCESS_DENIED", result.getErrorCode());
    }

    @Test
    public void testAuthorizePermission_Success() {
        OperationResult<Void> result = authService.authorizePermission(
                activeManager, "sp-101", ManagerPermission.EDIT_SPACE_OPERATIONS);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testAuthorizePermission_MissingPermission_ReturnsError() {
        OperationResult<Void> result = authService.authorizePermission(
                activeManager, "sp-101", ManagerPermission.MANAGE_RESOURCES);
        assertTrue(result.isFailure());
        assertEquals("PERMISSION_DENIED", result.getErrorCode());
    }

    @Test
    public void testAuthorizePermission_DeactivatedManager_ReturnsError() {
        activeManager.setActive(false);
        OperationResult<Void> result = authService.authorizePermission(
                activeManager, "sp-101", ManagerPermission.EDIT_SPACE_OPERATIONS);
        assertTrue(result.isFailure());
        assertEquals("ACCOUNT_DEACTIVATED", result.getErrorCode());
    }

    @Test
    public void testCanPerformPriceChange_ReturnsFalse() {
        assertFalse(authService.canPerformPriceChange(activeManager));
    }
}
