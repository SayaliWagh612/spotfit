package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.AvailabilityController;
import com.eklavya.spotfit.dao.AvailabilityDao;
import com.eklavya.spotfit.dao.ResourceDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.AvailabilityService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityControllerTest {

    private AvailabilityService stubService;
    private SpaceDao stubSpaceDao;
    private ResourceDao stubResourceDao;
    private AvailabilityController controller;

    @BeforeEach
    void setUp() {
        stubService = new AvailabilityService(new AvailabilityDaoStub());
        stubSpaceDao = new SpaceDaoStub();
        stubResourceDao = new ResourceDaoStub();

        controller = new AvailabilityController(stubService, stubSpaceDao, stubResourceDao);

        AppSession.clearSession();
    }

    @AfterEach
    void tearDown() {
        AppSession.clearSession();
    }

    @Test
    @DisplayName("loadAvailabilityForResource: Unauthenticated user rejected")
    void testLoadAvailability_Unauthenticated() {
        OperationResult<?> res = controller.loadAvailabilityForResource("sp-1", "res-1").join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    @DisplayName("loadAvailabilityForResource: Non-owner role rejected")
    void testLoadAvailability_NonOwnerRole() {
        AppSession.startSession("user-1", "user@test.com", "User", UserRole.MANAGER, "token-1", "refresh-1", 3600);
        OperationResult<?> res = controller.loadAvailabilityForResource("sp-1", "res-1").join();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    @DisplayName("saveWeeklyRule: Ownership mismatch rejected")
    void testSaveWeeklyRule_SpaceOwnershipMismatch() {
        AppSession.startSession("owner-123", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        OperationResult<?> res = controller.saveWeeklyRule(
                "sp-1", "res-1", "MONDAY", "09:00", "18:00", 60, 60, 240, true).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    @DisplayName("applySpaceOperatingHoursToResource: Space operates Wed-Fri creates rules only for Wed, Thu, Fri")
    void testApplySpaceOperatingHours_WedToFriOnly() {
        AppSession.startSession("owner-100", "owner@test.com", "Owner", UserRole.OWNER, "token-1", "refresh-1", 3600);

        OperationResult<List<AvailabilityRule>> res = controller.applySpaceOperatingHoursToResource("sp-wed", "res-wed").join();
        assertTrue(res.isSuccess());
        List<AvailabilityRule> rules = res.getData();
        assertEquals(3, rules.size());
        assertTrue(rules.stream().anyMatch(r -> "WEDNESDAY".equals(r.getDayOfWeek())));
        assertTrue(rules.stream().anyMatch(r -> "THURSDAY".equals(r.getDayOfWeek())));
        assertTrue(rules.stream().anyMatch(r -> "FRIDAY".equals(r.getDayOfWeek())));
        assertFalse(rules.stream().anyMatch(r -> "MONDAY".equals(r.getDayOfWeek())));
    }

    private static class SpaceDaoStub extends SpaceDao {
        @Override
        public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
            if ("sp-wed".equals(spaceId)) {
                Space wedSpace = new Space(
                        "sp-wed", "biz-1", "owner-100", "Wed-Fri Space", "Desc",
                        "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                        "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                        "09:00", "18:00", List.of("WEDNESDAY", "THURSDAY", "FRIDAY"), "Asia/Kolkata",
                        "DRAFT", true, 0L, 0L
                );
                return CompletableFuture.completedFuture(OperationResult.success(wedSpace));
            }
            Space otherSpace = new Space(
                    "sp-1", "biz-1", "other-owner-999", "Space Name", "Desc",
                    "CREATIVITY", "PODCASTING", "STUDIO", "Address", "Landmark",
                    "City", "State", "400001", 10, List.of(), List.of(), List.of(),
                    "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", "DRAFT", true, 0L, 0L
            );
            return CompletableFuture.completedFuture(OperationResult.success(otherSpace));
        }
    }

    private static class ResourceDaoStub extends ResourceDao {
        @Override
        public CompletableFuture<OperationResult<Resource>> getResource(String spaceId, String resourceId, String idToken) {
            if ("res-wed".equals(resourceId)) {
                Resource res = new Resource(
                        "res-wed", "sp-wed", "biz-1", "owner-100", "Wed Res", "Desc",
                        "ROOM", "RES-WED", 5, List.of(), List.of(), "AVAILABLE", true, 0L, 0L
                );
                return CompletableFuture.completedFuture(OperationResult.success(res));
            }
            Resource res = new Resource(
                    "res-1", "sp-1", "biz-1", "other-owner-999", "Res Name", "Desc",
                    "ROOM", "RES-1", 5, List.of(), List.of(), "AVAILABLE", true, 0L, 0L
            );
            return CompletableFuture.completedFuture(OperationResult.success(res));
        }
    }

    private static class AvailabilityDaoStub extends AvailabilityDao {
        private final List<AvailabilityRule> store = new ArrayList<>();

        @Override
        public CompletableFuture<OperationResult<AvailabilityRule>> setAvailabilityRule(String spaceId, String resourceId, AvailabilityRule rule, String idToken) {
            store.removeIf(r -> r.getDayOfWeek().equals(rule.getDayOfWeek()));
            store.add(rule);
            return CompletableFuture.completedFuture(OperationResult.success(rule));
        }

        @Override
        public CompletableFuture<OperationResult<AvailabilityRule>> getAvailabilityRule(String spaceId, String resourceId, String dayOfWeek, String idToken) {
            for (AvailabilityRule r : store) {
                if (r.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                    return CompletableFuture.completedFuture(OperationResult.success(r));
                }
            }
            return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Not found"));
        }

        @Override
        public CompletableFuture<OperationResult<List<AvailabilityRule>>> getAvailabilityRules(String spaceId, String resourceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(store)));
        }

        @Override
        public CompletableFuture<OperationResult<List<com.eklavya.spotfit.model.AvailabilityBlock>>> getAvailabilityBlocks(String spaceId, String resourceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
        }
    }
}
