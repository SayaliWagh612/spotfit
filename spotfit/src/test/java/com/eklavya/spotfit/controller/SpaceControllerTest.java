package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceControllerTest {

    private SpaceController spaceController;

    @BeforeEach
    public void setUp() {
        spaceController = new SpaceController();
        AppSession.clearSession();
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testUnauthenticatedSessionRejected() throws Exception {
        OperationResult<List<Space>> result = spaceController.loadCurrentOwnerSpaces().get();
        assertTrue(result.isFailure());
        assertEquals("User is not authenticated.", result.getMessage());
    }

    @Test
    public void testNonOwnerRoleRejected() throws Exception {
        AppSession.startSession("user123", "user@example.com", "Test User", UserRole.USER, "idToken123", "refresh123", 3600);
        OperationResult<List<Space>> result = spaceController.loadCurrentOwnerSpaces().get();
        assertTrue(result.isFailure());
        assertEquals("Only authenticated Space Owners can perform this action.", result.getMessage());
    }

    @Test
    public void testOwnerWithoutTokenRejected() throws Exception {
        AppSession.startSession("owner123", "owner@example.com", "Owner Name", UserRole.OWNER, null, null, 0);
        OperationResult<List<Space>> result = spaceController.loadCurrentOwnerSpaces().get();
        assertTrue(result.isFailure());
        assertEquals("Invalid session tokens.", result.getMessage());
    }
}
