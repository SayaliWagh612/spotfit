package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerServiceTest {

    private ManagerService managerService;
    private Space validSpace;

    @BeforeEach
    public void setUp() {
        managerService = new ManagerService();
        validSpace = new Space();
        validSpace.setSpaceId("sp-101");
        validSpace.setOwnerId("owner-001");
        validSpace.setName("Test Space");
        validSpace.setActive(true);
    }

    @Test
    public void testActivateManagerAccount_ValidationFailsOnBlankCode() throws ExecutionException, InterruptedException {
        OperationResult<Manager> res = managerService.activateManagerAccountAtomically(
                "mgr-uid-001",
                "Manager Name",
                "manager@example.com",
                "+919876543210",
                true,
                "",
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("INVALID_INVITATION_CODE", res.getErrorCode());
    }

    @Test
    public void testUpdateManagerAssignments_UnownedSpace_ReturnsError() throws ExecutionException, InterruptedException {
        OperationResult<Void> res = managerService.updateManagerAssignments(
                "mgr-uid-001",
                "owner-001",
                List.of("sp-999"), // Unowned space
                ManagerPermission.getDefaultPermissions(),
                List.of(validSpace),
                "mock-token"
        ).get();

        assertTrue(res.isFailure());
        assertEquals("FOREIGN_SPACE_REJECTED", res.getErrorCode());
    }
}
