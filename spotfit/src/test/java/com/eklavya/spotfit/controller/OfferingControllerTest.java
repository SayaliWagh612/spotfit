package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingType;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.OfferingController;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OfferingControllerTest {

    private OfferingController offeringController;

    @BeforeEach
    public void setUp() {
        offeringController = new OfferingController();
        AppSession.clearSession();
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testUnauthenticatedSessionRejected() throws Exception {
        OperationResult<List<Offering>> result = offeringController.loadOfferingsForSpace("space123").get();
        assertTrue(result.isFailure());
        assertEquals("AUTHENTICATION_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testNonOwnerRoleRejected() throws Exception {
        AppSession.startSession("user123", "user@example.com", "Test User", UserRole.USER, "idToken123", "refresh123", 3600);
        OperationResult<Offering> result = offeringController.createOffering(
                "space123", "Podcast Package", "Desc", BookingType.SLOT, PricingType.PER_HOUR,
                "1000", ResourceType.ROOM, 60, 60, 480, 30, 1, 1, 1, List.of(), List.of()
        ).get();
        assertTrue(result.isFailure());
        assertEquals("ROLE_MISMATCH", result.getErrorCode());
    }
}
