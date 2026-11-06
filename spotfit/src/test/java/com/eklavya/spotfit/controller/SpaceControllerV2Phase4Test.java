package com.eklavya.spotfit.controller;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceControllerV2Phase4Test {

    private SpaceController spaceController;
    private DummySpaceService spaceService;

    @BeforeEach
    public void setUp() {
        spaceService = new DummySpaceService();
        spaceController = new SpaceController(spaceService, new BusinessService());
        AppSession.clearSession();
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testCreateV2SpaceFailsWhenUnauthenticated() {
        OperationResult<Space> res = spaceController.createV2Space("bus-1", createSampleV2Space("owner-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceFailsWhenUserRoleIsNotOwner() {
        AppSession.startSession("user-1", "user@test.com", "User Name", UserRole.USER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.createV2Space("bus-1", createSampleV2Space("user-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceFailsWhenBusinessIdBlank() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.createV2Space("", createSampleV2Space("owner-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceFailsWhenSubmittedSpaceNull() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.createV2Space("bus-1", null).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceDelegatesToServiceWhenOwnerSessionValid() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space submitted = createSampleV2Space("owner-1");

        OperationResult<Space> res = spaceController.createV2Space("bus-1", submitted).join();

        assertTrue(res.isSuccess());
        assertNotNull(res.getData());
        assertEquals("v2-space-1", res.getData().getSpaceId());
    }

    @Test
    public void testLoadV2SpaceFailsWhenUnauthenticated() {
        OperationResult<Space> res = spaceController.loadV2Space("v2-space-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    public void testLoadV2SpaceFailsWhenRoleNotOwner() {
        AppSession.startSession("mgr-1", "mgr@test.com", "Manager Name", UserRole.MANAGER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.loadV2Space("v2-space-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testLoadV2SpaceFailsWhenSpaceIdBlank() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.loadV2Space("   ").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
    }

    @Test
    public void testLoadV2SpaceFailsWhenOwnedByOtherOwner() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space otherSpace = createSampleV2Space("other-owner");
        spaceService.setMockSpace("space-other", otherSpace);

        OperationResult<Space> res = spaceController.loadV2Space("space-other").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    public void testLoadV2SpaceFailsWhenSpaceIsV1Schema() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space v1Space = new Space("v1-space-1", "bus-1", "owner-1", "V1 Name", "V1 Desc",
                SpaceCategory.CREATIVITY, "MUSIC", "ROOM", "Addr", "Mark", "City", "State", "400001",
                10, List.of(), List.of(), List.of(), "09:00", "18:00", List.of("MONDAY"), "DRAFT", true, 1000L, 1000L);
        spaceService.setMockSpace("v1-space-1", v1Space);

        OperationResult<Space> res = spaceController.loadV2Space("v1-space-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.SCHEMA_VERSION_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testLoadV2SpaceReturnsSuccessWhenOwnerOwnsV2Space() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space v2Space = createSampleV2Space("owner-1");
        spaceService.setMockSpace("v2-space-1", v2Space);

        OperationResult<Space> res = spaceController.loadV2Space("v2-space-1").join();

        assertTrue(res.isSuccess());
        assertEquals(2, res.getData().getSchemaVersion());
        assertEquals("owner-1", res.getData().getOwnerId());
    }

    @Test
    public void testUpdateV2SpaceFailsWhenUnauthenticated() {
        OperationResult<Space> res = spaceController.updateV2Space("v2-space-1", createSampleV2Space("owner-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.AUTHENTICATION_REQUIRED, res.getErrorCode());
    }

    @Test
    public void testUpdateV2SpaceFailsWhenRoleNotOwner() {
        AppSession.startSession("user-1", "user@test.com", "User Name", UserRole.USER, "token-1", "ref-1", 3600);

        OperationResult<Space> res = spaceController.updateV2Space("v2-space-1", createSampleV2Space("user-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testUpdateV2SpaceFailsWhenExistingOwnedByOtherOwner() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space otherSpace = createSampleV2Space("other-owner");
        spaceService.setMockSpace("v2-space-1", otherSpace);

        OperationResult<Space> res = spaceController.updateV2Space("v2-space-1", createSampleV2Space("owner-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    public void testUpdateV2SpaceFailsWhenTargetSpaceIsNotV2() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space v1Space = new Space("v1-space-1", "bus-1", "owner-1", "V1 Name", "V1 Desc",
                SpaceCategory.CREATIVITY, "MUSIC", "ROOM", "Addr", "Mark", "City", "State", "400001",
                10, List.of(), List.of(), List.of(), "09:00", "18:00", List.of("MONDAY"), "DRAFT", true, 1000L, 1000L);
        spaceService.setMockSpace("v1-space-1", v1Space);

        OperationResult<Space> res = spaceController.updateV2Space("v1-space-1", createSampleV2Space("owner-1")).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.SCHEMA_VERSION_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testUpdateV2SpaceDelegatesToServiceWhenOwnerSessionAndV2SpaceValid() {
        AppSession.startSession("owner-1", "owner@test.com", "Owner Name", UserRole.OWNER, "token-1", "ref-1", 3600);
        Space existing = createSampleV2Space("owner-1");
        spaceService.setMockSpace("v2-space-1", existing);

        Space submitted = createSampleV2Space("owner-1");
        submitted.setSpaceName("Updated Name");

        OperationResult<Space> res = spaceController.updateV2Space("v2-space-1", submitted).join();

        assertTrue(res.isSuccess());
        assertEquals("Updated Name", res.getData().getSpaceName());
    }

    private Space createSampleV2Space(String ownerId) {
        SpaceLocation loc = new SpaceLocation("123 Craft Lane", "Studio Zone", "Mumbai", "Maharashtra", "400001", "Near Hub", 19.0, 72.8);
        Map<String, OperatingDayConfig> oh = new LinkedHashMap<>();
        oh.put("MONDAY", new OperatingDayConfig("09:00", "18:00", true));

        Space space = Space.createV2(
                "v2-space-1", "bus-1", ownerId, "Pottery Studio", "Studio for wheel spinning",
                SpaceCategory.CREATIVITY, "POTTERY", "POTTERY_WHEEL", loc,
                "http://img.png", List.of("http://img1.png"), List.of("WiFi"), List.of("Wheel"),
                10, BookingType.SLOT, 100000L, PricingUnit.HOUR,
                new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0), oh, List.of("Clean up"), "Flexible"
        );
        space.setTimeZoneId("Asia/Kolkata");
        return space;
    }

    private static class DummySpaceService extends SpaceService {
        private final Map<String, Space> mockSpaces = new LinkedHashMap<>();

        public void setMockSpace(String id, Space s) {
            mockSpaces.put(id, s);
        }

        @Override
        public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
            if (mockSpaces.containsKey(spaceId)) {
                return CompletableFuture.completedFuture(OperationResult.success(mockSpaces.get(spaceId)));
            }
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Space not found"));
        }

        @Override
        public CompletableFuture<OperationResult<Space>> createV2Space(String authenticatedOwnerId, String businessId, Space submittedSpace, String idToken) {
            submittedSpace.setOwnerId(authenticatedOwnerId);
            submittedSpace.setBusinessId(businessId);
            return CompletableFuture.completedFuture(OperationResult.success(submittedSpace));
        }

        @Override
        public CompletableFuture<OperationResult<Space>> updateV2Space(String authenticatedOwnerId, Space existingSpace, Space submittedSpace, String idToken) {
            existingSpace.setSpaceName(submittedSpace.getSpaceName());
            return CompletableFuture.completedFuture(OperationResult.success(existingSpace));
        }
    }
}
