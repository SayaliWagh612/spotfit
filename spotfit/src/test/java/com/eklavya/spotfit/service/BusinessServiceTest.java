package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessServiceTest {

    private BusinessService businessService;

    @BeforeEach
    public void setUp() {
        businessService = new BusinessService();
    }

    @Test
    public void testValidBusinessInputs() {
        OperationResult<Void> result = businessService.validateBusinessInputs(
                "Rhythm Studios",
                "info@rhythm.com",
                "9876543210",
                "123 Main St",
                "Pune",
                "Maharashtra",
                "411001"
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMissingBusinessName() {
        OperationResult<Void> result = businessService.validateBusinessInputs(
                "",
                "info@rhythm.com",
                "9876543210",
                "123 Main St",
                "Pune",
                "Maharashtra",
                "411001"
        );
        assertTrue(result.isFailure());
        assertEquals("BUSINESS_NAME_REQUIRED", result.getErrorCode());
    }

    @Test
    public void testInvalidBusinessEmail() {
        OperationResult<Void> result = businessService.validateBusinessInputs(
                "Rhythm Studios",
                "invalid-email",
                "9876543210",
                "123 Main St",
                "Pune",
                "Maharashtra",
                "411001"
        );
        assertTrue(result.isFailure());
        assertEquals("EMAIL_INVALID", result.getErrorCode());
    }

    @Test
    public void testInvalidBusinessPhone() {
        OperationResult<Void> result = businessService.validateBusinessInputs(
                "Rhythm Studios",
                "info@rhythm.com",
                "123",
                "123 Main St",
                "Pune",
                "Maharashtra",
                "411001"
        );
        assertTrue(result.isFailure());
        assertEquals("BUSINESS_PHONE_INVALID", result.getErrorCode());
    }

    @Test
    public void testDefaultBusinessModelValues() {
        Business business = new Business();
        assertEquals(BusinessStatus.DRAFT, business.getVerificationStatus());
        assertTrue(business.isActive());
    }
}
