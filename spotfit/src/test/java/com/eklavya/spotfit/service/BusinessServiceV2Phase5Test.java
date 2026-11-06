package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.SubmissionReadinessResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessServiceV2Phase5Test {

    private BusinessService businessService;
    private MockDao mockDao;

    @BeforeEach
    public void setUp() {
        mockDao = new MockDao();
        businessService = new BusinessService(mockDao);
    }

    @Test
    @DisplayName("validateV2Business returns ready for valid V2 business")
    public void testValidateV2BusinessSuccess() {
        Business b = Business.createV2("b-1", "o-1", "Valid Biz", "Legal Name", "valid@email.com",
                "9876543210", "Good description", "123 Main St", "Near Bank", "Pune", "Maharashtra", "411001",
                "GST123", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART), "", "https://valid.com",
                "DRAFT", true, 0, 0, "", 1000, 1000);

        SubmissionReadinessResult readiness = businessService.validateV2Business(b, "o-1");
        assertTrue(readiness.isReady(), "Expected ready but got errors: " + readiness.getMissingRequirements());
    }

    @Test
    @DisplayName("validateV2Business rejects non-CREATIVITY category for V2")
    public void testValidateV2BusinessCategoryMismatch() {
        Business b = Business.createV2("b-1", "o-1", "Valid Biz", "Legal Name", "valid@email.com",
                "9876543210", "Good description", "123 Main St", "Near Bank", "Pune", "Maharashtra", "411001",
                "GST123", "SPORTS", List.of(CreativityActivityGroup.ART), "", "https://valid.com",
                "DRAFT", true, 0, 0, "", 1000, 1000);

        SubmissionReadinessResult readiness = businessService.validateV2Business(b, "o-1");
        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(e -> e.toLowerCase().contains("creativity")));
    }

    @Test
    @DisplayName("validateV2Business rejects empty activity groups")
    public void testValidateV2BusinessEmptyActivityGroups() {
        Business b = Business.createV2("b-1", "o-1", "Valid Biz", "Legal Name", "valid@email.com",
                "9876543210", "Good description", "123 Main St", "Near Bank", "Pune", "Maharashtra", "411001",
                "GST123", SpaceCategory.CREATIVITY, List.of(), "", "https://valid.com",
                "DRAFT", true, 0, 0, "", 1000, 1000);

        SubmissionReadinessResult readiness = businessService.validateV2Business(b, "o-1");
        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(e -> e.toLowerCase().contains("activity group")));
    }

    @Test
    @DisplayName("validateV2Business rejects invalid website protocol")
    public void testValidateV2BusinessInvalidWebsite() {
        Business b = Business.createV2("b-1", "o-1", "Valid Biz", "Legal Name", "valid@email.com",
                "9876543210", "Good description", "123 Main St", "Near Bank", "Pune", "Maharashtra", "411001",
                "GST123", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART), "", "ftp://invalid-site.com",
                "DRAFT", true, 0, 0, "", 1000, 1000);

        SubmissionReadinessResult readiness = businessService.validateV2Business(b, "o-1");
        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(e -> e.contains("http://") || e.contains("https://")));
    }

    @Test
    @DisplayName("updateV2Business saves sanitized fields correctly")
    public void testUpdateV2BusinessSanitizesAndSaves() {
        Business b = Business.createV2("b-10", "o-1", " Raw Business Name ", " Legal Name ", "USER@EMAIL.COM ",
                " 9876543210 ", " Description ", " Address 1 ", " Landmark ", " City ", " State ", " 411001 ",
                " REG123 ", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.MUSIC), "", " https://mysite.com ",
                "DRAFT", true, 0, 0, "", 1000, 1000);

        OperationResult<Business> result = businessService.updateV2Business(b, "token-1").join();
        assertTrue(result.isSuccess());

        Business saved = result.getData();
        assertEquals("Raw Business Name", saved.getBusinessName());
        assertEquals("user@email.com", saved.getBusinessEmail());
        assertEquals("https://mysite.com", saved.getWebsite());
        assertEquals("", saved.getLogoUrl());
    }

    private static class MockDao extends BusinessDao {
        public Business lastSaved;

        @Override
        public CompletableFuture<OperationResult<Business>> updateBusiness(Business business, String idToken) {
            this.lastSaved = business;
            return CompletableFuture.completedFuture(OperationResult.success(business));
        }
    }
}
