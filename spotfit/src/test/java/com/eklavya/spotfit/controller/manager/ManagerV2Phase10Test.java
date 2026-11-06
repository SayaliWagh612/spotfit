package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;
import com.eklavya.spotfit.service.AvailabilityService;
import com.eklavya.spotfit.service.ManagerAuthorizationService;
import com.eklavya.spotfit.service.ManagerService;
import com.eklavya.spotfit.service.SeatService;
import com.eklavya.spotfit.service.WorkshopSessionService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ManagerV2Phase10Test {

    @Mock
    private ManagerService managerService;

    @Mock
    private SpaceDao spaceDao;

    @Mock
    private BusinessDao businessDao;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private SeatService seatService;

    @Mock
    private WorkshopSessionService sessionService;

    private ManagerAuthorizationService authService;
    private ManagerSpaceOperationsController controller;

    private AutoCloseable mocksCloseable;

    private static final String MANAGER_ID = "mgr-user-777";
    private static final String TOKEN = "valid-mgr-token";
    private static final String SPACE_ID_1 = "space-v2-slot-101";
    private static final String SPACE_ID_UNASSIGNED = "space-v2-other-999";
    private static final String BUSINESS_ID = "bus-v2-888";

    @BeforeEach
    void setUp() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
        authService = new ManagerAuthorizationService();

        controller = new ManagerSpaceOperationsController(
                managerService,
                authService,
                spaceDao,
                businessDao,
                availabilityService,
                seatService,
                sessionService
        );

        AppSession.clearSession();
        AppSession.startSession(MANAGER_ID, "manager@spotfit.com", "Space Manager", UserRole.MANAGER, TOKEN, "refresh-mgr", 3600);
    }

    @AfterEach
    void tearDown() throws Exception {
        AppSession.clearSession();
        if (mocksCloseable != null) {
            mocksCloseable.close();
        }
    }

    private Manager createActiveManager(List<String> assignedSpaceIds, List<String> permissions) {
        Manager m = new Manager();
        m.setManagerId(MANAGER_ID);
        m.setActive(true);
        m.setAssignedSpaceIds(assignedSpaceIds);
        m.setPermissions(permissions);
        return m;
    }

    private Space createApprovedV2SlotSpace() {
        Space space = new Space();
        space.setSpaceId(SPACE_ID_1);
        space.setBusinessId(BUSINESS_ID);
        space.setOwnerId("owner-uid-123");
        space.setSchemaVersion(2);
        space.setActive(true);
        space.setBookingType("SLOT");
        space.setPricingUnit("HOUR");
        space.setBasePricePaise(50000L);
        space.setApprovalStatus(SpaceApprovalStatus.APPROVED);
        space.setOperationalStatus(SpaceOperationalStatus.INACTIVE);

        SlotBookingConfig sCfg = new SlotBookingConfig();
        sCfg.setSlotIntervalMinutes(60);
        sCfg.setMinimumDurationMinutes(60);
        sCfg.setMaximumDurationMinutes(240);
        space.setBookingConfig(sCfg);

        return space;
    }

    private Business createApprovedBusiness() {
        Business bus = new Business();
        bus.setBusinessId(BUSINESS_ID);
        bus.setOwnerId("owner-uid-123");
        bus.setActive(true);
        bus.setVerificationStatus("APPROVED");
        return bus;
    }

    @Test
    void unassignedSpaceAccess_deniedWithSpaceNotAssigned() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.VIEW_ASSIGNED_SPACES));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space unassignedSpace = createApprovedV2SlotSpace();
        unassignedSpace.setSpaceId(SPACE_ID_UNASSIGNED);
        when(spaceDao.getSpace(SPACE_ID_UNASSIGNED, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(unassignedSpace)));

        OperationResult<Space> res = controller.getV2SpaceDetails(SPACE_ID_UNASSIGNED).join();
        assertTrue(res.isFailure());
        assertEquals("SPACE_NOT_ASSIGNED", res.getErrorCode());
    }

    @Test
    void inactiveManagerAccess_deniedWithManagerInactive() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.VIEW_ASSIGNED_SPACES));
        manager.setActive(false);
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        OperationResult<Space> res = controller.getV2SpaceDetails(SPACE_ID_1).join();
        assertTrue(res.isFailure());
        assertEquals("MANAGER_INACTIVE", res.getErrorCode());
    }

    @Test
    void setOperationalStatus_withoutPermission_denied() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.VIEW_ASSIGNED_SPACES));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        OperationResult<Space> res = controller.setV2OperationalStatus(SPACE_ID_1, SpaceOperationalStatus.ACTIVE).join();
        assertTrue(res.isFailure());
        assertEquals("PERMISSION_DENIED", res.getErrorCode());
    }

    @Test
    void setOperationalStatus_onUnapprovedSpace_denied() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_SPACE_STATUS));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        space.setApprovalStatus(SpaceApprovalStatus.DRAFT);
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        OperationResult<Space> res = controller.setV2OperationalStatus(SPACE_ID_1, SpaceOperationalStatus.ACTIVE).join();
        assertTrue(res.isFailure());
        assertEquals("UNAPPROVED_SPACE_ACTIVATION_DENIED", res.getErrorCode());
    }

    @Test
    void setOperationalStatus_onApprovedSpaceWithPermission_succeeds() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_SPACE_STATUS));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        Business business = createApprovedBusiness();
        when(businessDao.getBusiness(BUSINESS_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(business)));

        Space updatedSpace = createApprovedV2SlotSpace();
        updatedSpace.setOperationalStatus(SpaceOperationalStatus.ACTIVE);
        updatedSpace.setPublishedAt(System.currentTimeMillis());

        when(spaceDao.setV2OperationalStatus(eq(SPACE_ID_1), eq(SpaceOperationalStatus.ACTIVE), anyLong(), anyLong(), eq(TOKEN)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(updatedSpace)));

        OperationResult<Space> res = controller.setV2OperationalStatus(SPACE_ID_1, SpaceOperationalStatus.ACTIVE).join();
        assertTrue(res.isSuccess());
        assertEquals(SpaceOperationalStatus.ACTIVE, res.getData().getOperationalStatus());
    }

    @Test
    void manageAvailability_onSeatSpace_returnsUnsupportedBookingType() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_AVAILABILITY));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        space.setBookingType("SEAT");
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        OperationResult<AvailabilityRule> res = controller.saveWeeklyRule(
                SPACE_ID_1, "MONDAY", "09:00", "17:00", 60, 60, 240, true).join();
        assertTrue(res.isFailure());
        assertEquals("UNSUPPORTED_BOOKING_TYPE", res.getErrorCode());
    }

    @Test
    void manageSeatStatus_withPermission_succeeds() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_SEATS));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        space.setBookingType("SEAT");
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        Seat seat = new Seat("seat-1", SPACE_ID_1, "Desk A", "D1", "Main", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        when(seatService.getSeat(SPACE_ID_1, "seat-1", TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(seat)));

        Seat updatedSeat = new Seat("seat-1", SPACE_ID_1, "Desk A", "D1", "Main", "A", 1, 1, 0L, SeatStatus.MAINTENANCE, true, 100L, 200L);
        when(seatService.updateSeat(eq(space), any(Seat.class), eq(TOKEN)))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(updatedSeat)));

        OperationResult<Seat> res = controller.updateSeatStatus(SPACE_ID_1, "seat-1", SeatStatus.MAINTENANCE).join();
        assertTrue(res.isSuccess());
        assertEquals(SeatStatus.MAINTENANCE, res.getData().getStatus());
    }

    @Test
    void cancelWorkshopSession_withPermission_succeeds() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_SESSIONS));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        space.setBookingType("WORKSHOP");

        WorkshopBookingConfig wCfg = new WorkshopBookingConfig();
        wCfg.setInstructorName("Jane Doe");
        space.setBookingConfig(wCfg);

        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        WorkshopSession session = new WorkshopSession("sess-1", SPACE_ID_1, 1000L, 2000L, 10, 0, 0L, "CANCELLED", true, 100L, 100L);
        when(sessionService.cancelSession(space, "sess-1", TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(session)));

        OperationResult<WorkshopSession> res = controller.cancelWorkshopSession(SPACE_ID_1, "sess-1").join();
        assertTrue(res.isSuccess());
        assertEquals("CANCELLED", res.getData().getStatus());
    }

    @Test
    void getAssignedSpaces_withActiveManager_returnsSpacesList() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.VIEW_ASSIGNED_SPACES));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        when(managerService.getAssignedSpacesForManager(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(List.of(space))));

        OperationResult<List<Space>> res = controller.getAssignedSpaces().join();
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertEquals(SPACE_ID_1, res.getData().get(0).getSpaceId());
    }

    @Test
    void updateSeatStatus_preservesSeatImmutableFields() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), List.of(ManagerPermission.MANAGE_SEATS));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(manager)));

        Space space = createApprovedV2SlotSpace();
        space.setBookingType("SEAT");
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        Seat originalSeat = new Seat("seat-77", SPACE_ID_1, "Fixed Desk 7", "FD-07", "Main Floor", "Row B", 1, 1, 5000L, SeatStatus.AVAILABLE, true, 1000L, 1000L);
        when(seatService.getSeat(SPACE_ID_1, "seat-77", TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(originalSeat)));

        when(seatService.updateSeat(eq(space), any(Seat.class), eq(TOKEN)))
                .thenAnswer(invocation -> {
                    Seat updated = invocation.getArgument(1);
                    // Verify immutable fields were untouched
                    assertEquals("Fixed Desk 7", updated.getSeatName());
                    assertEquals("FD-07", updated.getSeatCode());
                    assertEquals(5000L, updated.getPriceOverridePaise());
                    assertEquals(1, updated.getCapacity());
                    assertEquals(SeatStatus.MAINTENANCE, updated.getStatus());
                    return CompletableFuture.completedFuture(OperationResult.success(updated));
                });

        OperationResult<Seat> res = controller.updateSeatStatus(SPACE_ID_1, "seat-77", SeatStatus.MAINTENANCE).join();
        assertTrue(res.isSuccess());
        assertEquals(SeatStatus.MAINTENANCE, res.getData().getStatus());
        assertEquals("FD-07", res.getData().getSeatCode());
    }

    @Test
    void oldManagerProfile_withoutNewPermissions_isCompatibleButDeniedNewActions() {
        // Legacy manager with only V1 permissions
        Manager legacyManager = createActiveManager(List.of(SPACE_ID_1), List.of(
                ManagerPermission.VIEW_ASSIGNED_SPACES,
                ManagerPermission.MANAGE_RESOURCES
        ));
        when(managerService.getManagerProfile(MANAGER_ID, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(legacyManager)));

        Space space = createApprovedV2SlotSpace();
        space.setBookingType("SEAT");
        when(spaceDao.getSpace(SPACE_ID_1, TOKEN))
                .thenReturn(CompletableFuture.completedFuture(OperationResult.success(space)));

        // Attempting MANAGE_SEATS action with legacy profile returns PERMISSION_DENIED
        OperationResult<Seat> res = controller.updateSeatStatus(SPACE_ID_1, "seat-1", SeatStatus.MAINTENANCE).join();
        assertTrue(res.isFailure());
        assertEquals("PERMISSION_DENIED", res.getErrorCode());
    }

    @Test
    void prohibitedAction_priceChange_alwaysDeniedForManager() {
        Manager manager = createActiveManager(List.of(SPACE_ID_1), ManagerPermission.getDefaultPermissions());
        assertFalse(authService.canPerformPriceChange(manager));
    }
}

