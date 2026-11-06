package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.ResourceController;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceControllerTest {

    private ResourceController resourceController;

    @BeforeEach
    public void setUp() {
        resourceController = new ResourceController();
        AppSession.clearSession();
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testUnauthenticatedSessionRejected() throws Exception {
        OperationResult<List<Resource>> result = resourceController.loadResourcesForSpace("space123").get();
        assertTrue(result.isFailure());
        assertEquals("AUTHENTICATION_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testNonOwnerRoleRejected() throws Exception {
        AppSession.startSession("user123", "user@example.com", "Test User", UserRole.USER, "idToken123", "refresh123", 3600);
        OperationResult<Resource> result = resourceController.createResource(
                "space123", "Room A", "ROOM-A", ResourceType.ROOM, "Desc", 4, List.of(), List.of()
        ).get();
        assertTrue(result.isFailure());
        assertEquals("ROLE_MISMATCH", result.getErrorCode());
    }
}
