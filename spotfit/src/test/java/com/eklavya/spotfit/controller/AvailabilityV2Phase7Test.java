package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.AvailabilityController;
import com.eklavya.spotfit.dao.AvailabilityDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.service.AvailabilityService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityV2Phase7Test {

    private AvailabilityDaoStub stubDao;
    private SpaceDaoStub stubSpaceDao;
    private AvailabilityService service;
    private AvailabilityController controller;

    @BeforeEach
    void setUp() {
        stubDao = new AvailabilityDaoStub();
        stubSpaceDao = new SpaceDaoStub();
        service = new AvailabilityService(stubDao);
        controller = new AvailabilityController(service, stubSpaceDao, null);

        AppSession.clearSession();
    }

    @AfterEach
    void tearDown() {
        AppSession.clearSession();
    }

    @Test
    @DisplayName("V2 Availability: Rejects non-V2 Space (schemaVersion == 1)")
    void testV2Availability_RejectsV1Space() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        OperationResult<AvailabilityRule> res = controller.saveSpaceWeeklyRule(
                "sp-v1", "MONDAY", "09:00", "18:00", 60, 60, 240, true).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.SCHEMA_VERSION_MISMATCH, res.getErrorCode());
    }

    @Test
    @DisplayName("V2 Availability: Rejects write when approvalStatus is SUBMITTED or UNDER_REVIEW")
    void testV2Availability_RejectsLockedStatus() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        // Test SUBMITTED
        OperationResult<AvailabilityRule> resSubmitted = controller.saveSpaceWeeklyRule(
                "sp-submitted", "MONDAY", "09:00", "18:00", 60, 60, 240, true).join();

        assertTrue(resSubmitted.isFailure());
        assertEquals(OperationCode.LIFECYCLE_WRITE_DENIED, resSubmitted.getErrorCode());

        // Test UNDER_REVIEW
        OperationResult<AvailabilityBlock> resBlock = controller.createSpaceAvailabilityBlock(
                "sp-under-review", System.currentTimeMillis(), System.currentTimeMillis() + 3600000, "Maintenance").join();

        assertTrue(resBlock.isFailure());
        assertEquals(OperationCode.LIFECYCLE_WRITE_DENIED, resBlock.getErrorCode());
    }

    @Test
    @DisplayName("V2 Availability: Saves space weekly rule with resourceId == '' for DRAFT, REJECTED, APPROVED")
    void testV2Availability_SaveRuleSuccess() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        OperationResult<AvailabilityRule> res = controller.saveSpaceWeeklyRule(
                "sp-v2-draft", "MONDAY", "09:00", "18:00", 60, 60, 240, true).join();

        assertTrue(res.isSuccess());
        AvailabilityRule savedRule = res.getData();
        assertEquals("MONDAY", savedRule.getDayOfWeek());
        assertEquals("", savedRule.getResourceId());
        assertEquals("sp-v2-draft", savedRule.getSpaceId());
        assertEquals("owner-1", savedRule.getOwnerId());
    }

    @Test
    @DisplayName("V2 Availability: Applies space operating hours with resourceId == ''")
    void testV2Availability_ApplyOperatingHours() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        OperationResult<List<AvailabilityRule>> res = controller.applyOperatingHoursToSpaceAvailability("sp-v2-draft").join();

        assertTrue(res.isSuccess());
        List<AvailabilityRule> rules = res.getData();
        assertEquals(2, rules.size());
        for (AvailabilityRule r : rules) {
            assertEquals("", r.getResourceId());
            assertEquals("sp-v2-draft", r.getSpaceId());
        }
    }

    @Test
    @DisplayName("V2 Availability: Load space availability and preview slots")
    void testV2Availability_LoadAndPreview() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        controller.saveSpaceWeeklyRule("sp-v2-draft", "MONDAY", "09:00", "17:00", 60, 60, 240, true).join();

        OperationResult<Map<String, Object>> loadRes = controller.loadSpaceAvailability("sp-v2-draft").join();
        assertTrue(loadRes.isSuccess());
        @SuppressWarnings("unchecked")
        List<AvailabilityRule> rules = (List<AvailabilityRule>) loadRes.getData().get("rules");
        assertEquals(1, rules.size());

        OperationResult<List<AvailableSlot>> previewRes = controller.previewSpaceSlots("sp-v2-draft", LocalDate.of(2026, 9, 7)).join();
        assertTrue(previewRes.isSuccess());
        assertNotNull(previewRes.getData());
    }

    private static class SpaceDaoStub extends SpaceDao {
        @Override
        public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
            if ("sp-v1".equals(spaceId)) {
                Space sp = new Space("sp-v1", "biz-1", "owner-1", "V1 Space", "Desc",
                        "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                        "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                        "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", "DRAFT", true, 0L, 0L);
                sp.setSchemaVersion(1);
                return CompletableFuture.completedFuture(OperationResult.success(sp));
            } else if ("sp-submitted".equals(spaceId)) {
                Space sp = new Space("sp-submitted", "biz-1", "owner-1", "Submitted V2 Space", "Desc",
                        "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                        "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                        "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", "DRAFT", true, 0L, 0L);
                sp.setSchemaVersion(2);
                sp.setApprovalStatus("SUBMITTED");
                sp.setBookingType("SLOT");
                return CompletableFuture.completedFuture(OperationResult.success(sp));
            } else if ("sp-under-review".equals(spaceId)) {
                Space sp = new Space("sp-under-review", "biz-1", "owner-1", "Under Review V2 Space", "Desc",
                        "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                        "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                        "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", "DRAFT", true, 0L, 0L);
                sp.setSchemaVersion(2);
                sp.setApprovalStatus("UNDER_REVIEW");
                sp.setBookingType("SLOT");
                return CompletableFuture.completedFuture(OperationResult.success(sp));
            } else {
                Space sp = new Space("sp-v2-draft", "biz-1", "owner-1", "Draft V2 Space", "Desc",
                        "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                        "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                        "09:00", "18:00", List.of("MONDAY", "TUESDAY"), "Asia/Kolkata", "DRAFT", true, 0L, 0L);
                sp.setSchemaVersion(2);
                sp.setApprovalStatus("DRAFT");
                sp.setBookingType("SLOT");
                return CompletableFuture.completedFuture(OperationResult.success(sp));
            }
        }
    }

    private static class AvailabilityDaoStub extends AvailabilityDao {
        private final List<AvailabilityRule> rulesStore = new ArrayList<>();
        private final List<AvailabilityBlock> blocksStore = new ArrayList<>();

        @Override
        public CompletableFuture<OperationResult<AvailabilityRule>> setSpaceAvailabilityRule(String spaceId, AvailabilityRule rule, String idToken) {
            rulesStore.removeIf(r -> r.getDayOfWeek().equalsIgnoreCase(rule.getDayOfWeek()));
            rulesStore.add(rule);
            return CompletableFuture.completedFuture(OperationResult.success(rule));
        }

        @Override
        public CompletableFuture<OperationResult<AvailabilityRule>> getSpaceAvailabilityRule(String spaceId, String dayOfWeek, String idToken) {
            for (AvailabilityRule r : rulesStore) {
                if (r.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                    return CompletableFuture.completedFuture(OperationResult.success(r));
                }
            }
            return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Not found"));
        }

        @Override
        public CompletableFuture<OperationResult<List<AvailabilityRule>>> getSpaceAvailabilityRules(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(rulesStore)));
        }

        @Override
        public CompletableFuture<OperationResult<AvailabilityBlock>> createSpaceAvailabilityBlock(String spaceId, AvailabilityBlock block, String idToken) {
            blocksStore.add(block);
            return CompletableFuture.completedFuture(OperationResult.success(block));
        }

        @Override
        public CompletableFuture<OperationResult<AvailabilityBlock>> updateSpaceAvailabilityBlock(String spaceId, AvailabilityBlock block, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(block));
        }

        @Override
        public CompletableFuture<OperationResult<AvailabilityBlock>> getSpaceAvailabilityBlock(String spaceId, String blockId, String idToken) {
            for (AvailabilityBlock b : blocksStore) {
                if (b.getBlockId().equals(blockId)) {
                    return CompletableFuture.completedFuture(OperationResult.success(b));
                }
            }
            return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Not found"));
        }

        @Override
        public CompletableFuture<OperationResult<List<AvailabilityBlock>>> getSpaceAvailabilityBlocks(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(blocksStore)));
        }
    }
}
