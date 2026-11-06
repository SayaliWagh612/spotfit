package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessControllerV2Phase5Test {

    private BusinessController controller;
    private MockBusinessDao mockDao;
    private BusinessService service;

    @BeforeEach
    public void setUp() {
        AppSession.clearSession();
        mockDao = new MockBusinessDao();
        service = new BusinessService(mockDao);
        controller = new BusinessController(service);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    @DisplayName("createV2Business fails when unauthenticated")
    public void testCreateV2BusinessUnauthenticated() {
        Business input = Business.createV2("", "", "Test Biz", "", "test@biz.com", "1234567890",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", "DRAFT", true, 0, 0, "", 0, 0);

        OperationResult<Business> res = controller.createV2Business(input).join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    @DisplayName("createV2Business fails for non-OWNER role")
    public void testCreateV2BusinessRoleMismatch() {
        AppSession.startSession("user-123", "user@test.com", "User Test", UserRole.USER, "mock-token", "mock-refresh", 3600);

        Business input = Business.createV2("", "", "Test Biz", "", "test@biz.com", "1234567890",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", "DRAFT", true, 0, 0, "", 0, 0);

        OperationResult<Business> res = controller.createV2Business(input).join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    @DisplayName("createV2Business succeeds for authenticated Owner")
    public void testCreateV2BusinessSuccess() {
        AppSession.startSession("owner-777", "owner@test.com", "Owner Test", UserRole.OWNER, "mock-token-777", "mock-refresh-777", 3600);

        Business input = Business.createV2("", "", "Rhythm Studios", "Rhythm LLC", "info@rhythm.com", "9876543210",
                "A creative music and podcast space.", "Main Street 10", "Near Central Park", "Pune", "Maharashtra", "411001",
                "GST123", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.MUSIC, CreativityActivityGroup.PODCASTS),
                "", "https://rhythm.com", "DRAFT", true, 0, 0, "", 0, 0);

        OperationResult<Business> res = controller.createV2Business(input).join();
        assertTrue(res.isSuccess(), "Expected success but got: " + res.getMessage());
        assertNotNull(res.getData());

        Business b = res.getData();
        assertEquals(2, b.getSchemaVersion());
        assertEquals("owner-777", b.getOwnerId());
        assertEquals("Rhythm Studios", b.getBusinessName());
        assertEquals(SpaceCategory.CREATIVITY, b.getCategory());
        assertEquals(2, b.getActivityGroups().size());
        assertEquals(BusinessStatus.DRAFT, b.getVerificationStatus());
        assertTrue(b.getCreatedAt() > 0);
    }

    @Test
    @DisplayName("updateV2Business rejects non-owner edit")
    public void testUpdateV2BusinessNonOwnerDenied() {
        AppSession.startSession("owner-777", "owner@test.com", "Owner Test", UserRole.OWNER, "mock-token-777", "mock-refresh-777", 3600);

        Business existing = Business.createV2("biz-100", "owner-999", "Other Owner Biz", "", "other@biz.com", "9876543210",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", "DRAFT", true, 0, 0, "", 1000, 1000);
        mockDao.savedBusinesses.add(existing);

        Business editInput = Business.createV2("biz-100", "owner-777", "Modified Biz", "", "other@biz.com", "9876543210",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", "DRAFT", true, 0, 0, "", 1000, 1000);

        OperationResult<Business> res = controller.updateV2Business("biz-100", editInput).join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    @DisplayName("updateV2Business rejects edit when status is SUBMITTED or APPROVED")
    public void testUpdateV2BusinessSubmittedDenied() {
        AppSession.startSession("owner-777", "owner@test.com", "Owner Test", UserRole.OWNER, "mock-token-777", "mock-refresh-777", 3600);

        Business existing = Business.createV2("biz-100", "owner-777", "Submitted Biz", "", "owner@biz.com", "9876543210",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", BusinessStatus.SUBMITTED, true, 5000, 0, "", 1000, 1000);
        mockDao.savedBusinesses.add(existing);

        Business editInput = Business.createV2("biz-100", "owner-777", "Modified Biz Name", "", "owner@biz.com", "9876543210",
                "Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", BusinessStatus.SUBMITTED, true, 5000, 0, "", 1000, 1000);

        OperationResult<Business> res = controller.updateV2Business("biz-100", editInput).join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    @DisplayName("updateV2Business resets REJECTED status to DRAFT and clears review metadata")
    public void testUpdateV2BusinessRejectedResetsToDraft() {
        AppSession.startSession("owner-777", "owner@test.com", "Owner Test", UserRole.OWNER, "mock-token-777", "mock-refresh-777", 3600);

        Business existing = Business.createV2("biz-100", "owner-777", "Rejected Biz", "", "owner@biz.com", "9876543210",
                "Old Desc", "Addr", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART), "", "", BusinessStatus.REJECTED, true, 5000, 9000, "Fix address details", 1000, 1000);
        mockDao.savedBusinesses.add(existing);

        Business editInput = Business.createV2("biz-100", "owner-777", "Corrected Biz Name", "", "owner@biz.com", "9876543210",
                "New Updated Description", "Addr 2", "", "City", "State", "411001", "", SpaceCategory.CREATIVITY,
                List.of(CreativityActivityGroup.ART, CreativityActivityGroup.MUSIC), "", "https://newsite.com", BusinessStatus.REJECTED, true, 5000, 9000, "Fix address details", 1000, 1000);

        OperationResult<Business> res = controller.updateV2Business("biz-100", editInput).join();
        assertTrue(res.isSuccess(), "Expected success: " + res.getMessage());

        Business updated = res.getData();
        assertEquals(BusinessStatus.DRAFT, updated.getVerificationStatus(), "Status must be reset to DRAFT");
        assertEquals(0L, updated.getReviewedAt(), "reviewedAt must be reset to 0");
        assertEquals("", updated.getRejectionReason(), "rejectionReason must be cleared");
        assertEquals("Corrected Biz Name", updated.getBusinessName());
        assertEquals("New Updated Description", updated.getDescription());
        assertEquals("https://newsite.com", updated.getWebsite());
    }

    private static class MockBusinessDao extends BusinessDao {
        public final java.util.List<Business> savedBusinesses = new java.util.ArrayList<>();

        @Override
        public CompletableFuture<OperationResult<Business>> createBusiness(Business business, String idToken) {
            savedBusinesses.add(business);
            return CompletableFuture.completedFuture(OperationResult.success(business));
        }

        @Override
        public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
            for (Business b : savedBusinesses) {
                if (b.getBusinessId().equals(businessId)) {
                    return CompletableFuture.completedFuture(OperationResult.success(b));
                }
            }
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Not found"));
        }

        @Override
        public CompletableFuture<OperationResult<Business>> updateBusiness(Business business, String idToken) {
            for (int i = 0; i < savedBusinesses.size(); i++) {
                if (savedBusinesses.get(i).getBusinessId().equals(business.getBusinessId())) {
                    savedBusinesses.set(i, business);
                    return CompletableFuture.completedFuture(OperationResult.success(business));
                }
            }
            savedBusinesses.add(business);
            return CompletableFuture.completedFuture(OperationResult.success(business));
        }
    }
}
