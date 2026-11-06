package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.controller.user.CustomerMarketplaceController;
import com.eklavya.spotfit.service.SpaceService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerMarketplaceV2Phase11ATest {

    private SpaceDao mockSpaceDao;
    private BusinessDao mockBusinessDao;
    private SeatDao mockSeatDao;
    private WorkshopSessionDao mockWorkshopSessionDao;
    private AvailabilityDao mockAvailabilityDao;
    private UserDao mockUserDao;

    private SpaceService spaceService;
    private CustomerMarketplaceController controller;

    private Space v2Space;
    private Business v2Business;

    @BeforeEach
    public void setUp() {
        AppSession.clearSession();
        AppSession.startSession("user-uid-123", "customer@example.com", "Customer Name", UserRole.USER,
                "mock-id-token-xyz", "mock-refresh-token", 3600);

        v2Business = new Business();
        v2Business.setBusinessId("bus-v2-1");
        v2Business.setSchemaVersion(2);
        v2Business.setVerificationStatus(BusinessStatus.APPROVED);
        v2Business.setActive(true);
        v2Business.setCategory(SpaceCategory.CREATIVITY);
        v2Business.setActivityGroups(List.of(CreativityActivityGroup.ART, CreativityActivityGroup.MUSIC));

        v2Space = new Space();
        v2Space.setSpaceId("space-v2-1");
        v2Space.setBusinessId("bus-v2-1");
        v2Space.setSchemaVersion(2);
        v2Space.setApprovalStatus(SpaceApprovalStatus.APPROVED);
        v2Space.setOperationalStatus(SpaceOperationalStatus.ACTIVE);
        v2Space.setActive(true);
        v2Space.setCategory(SpaceCategory.CREATIVITY);
        v2Space.setActivityGroup(CreativityActivityGroup.ART);
        v2Space.setBookingType(BookingType.SEAT);
        v2Space.setPricingUnit(PricingUnit.HOUR);
        v2Space.setBasePricePaise(50000L);

        mockSpaceDao = new SpaceDao() {
            @Override
            public CompletableFuture<OperationResult<List<Space>>> getVisibleV2CreativitySpaces(String activityGroup,
                    String idToken) {
                if ("FAIL_DAO".equalsIgnoreCase(activityGroup)) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NETWORK_ERROR, "Firestore query failed."));
                }
                if (CreativityActivityGroup.ART.equalsIgnoreCase(activityGroup)) {
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(v2Space)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(Collections.emptyList()));
            }

            @Override
            public CompletableFuture<OperationResult<List<Space>>> getPublicVisibleV2CreativitySpaces(
                    String activityGroup) {
                if ("FAIL_DAO".equalsIgnoreCase(activityGroup)) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure(OperationCode.NETWORK_ERROR, "Firestore query failed."));
                }
                if (CreativityActivityGroup.ART.equalsIgnoreCase(activityGroup)) {
                    return CompletableFuture.completedFuture(OperationResult.success(List.of(v2Space)));
                }
                return CompletableFuture.completedFuture(OperationResult.success(Collections.emptyList()));
            }

            @Override
            public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
                if ("space-v2-1".equals(spaceId)) {
                    return CompletableFuture.completedFuture(OperationResult.success(v2Space));
                } else if ("space-unapproved".equals(spaceId)) {
                    Space s = new Space();
                    s.setSpaceId("space-unapproved");
                    s.setBusinessId("bus-v2-1");
                    s.setSchemaVersion(2);
                    s.setApprovalStatus(SpaceApprovalStatus.DRAFT);
                    s.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
                    s.setActive(true);
                    s.setCategory(SpaceCategory.CREATIVITY);
                    s.setActivityGroup(CreativityActivityGroup.ART);
                    return CompletableFuture.completedFuture(OperationResult.success(s));
                }
                return CompletableFuture
                        .completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Space not found"));
            }
        };

        mockBusinessDao = new BusinessDao() {
            @Override
            public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
                if ("bus-v2-1".equals(businessId)) {
                    return CompletableFuture.completedFuture(OperationResult.success(v2Business));
                } else if ("bus-unapproved".equals(businessId)) {
                    Business b = new Business();
                    b.setBusinessId("bus-unapproved");
                    b.setSchemaVersion(2);
                    b.setVerificationStatus(BusinessStatus.DRAFT);
                    b.setActive(true);
                    b.setCategory(SpaceCategory.CREATIVITY);
                    return CompletableFuture.completedFuture(OperationResult.success(b));
                }
                return CompletableFuture
                        .completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Business not found"));
            }

            @Override
            public CompletableFuture<OperationResult<Business>> getPublicBusiness(String businessId) {
                return getBusiness(businessId, null);
            }
        };

        mockSeatDao = new SeatDao() {
            @Override
            public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId, String idToken) {
                Seat seat1 = new Seat();
                seat1.setSeatId("s1");
                seat1.setSpaceId(spaceId);
                seat1.setSeatName("Seat 1");
                seat1.setStatus(SeatStatus.AVAILABLE);
                seat1.setActive(true);

                Seat seat2 = new Seat();
                seat2.setSeatId("s2");
                seat2.setSpaceId(spaceId);
                seat2.setSeatName("Seat 2");
                seat2.setStatus(SeatStatus.BLOCKED);
                seat2.setActive(true);

                return CompletableFuture.completedFuture(OperationResult.success(List.of(seat1, seat2)));
            }
        };

        mockWorkshopSessionDao = new WorkshopSessionDao() {
            @Override
            public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId,
                    String idToken) {
                long future = System.currentTimeMillis() + 86400000L;
                WorkshopSession sess1 = new WorkshopSession();
                sess1.setSessionId("sess1");
                sess1.setSpaceId(spaceId);
                sess1.setStartAt(future);
                sess1.setEndAt(future + 7200000L);
                sess1.setCapacity(10);
                sess1.setBookedCount(2);
                sess1.setStatus(WorkshopSessionStatus.SCHEDULED);
                sess1.setActive(true);

                return CompletableFuture.completedFuture(OperationResult.success(List.of(sess1)));
            }
        };

        mockAvailabilityDao = new AvailabilityDao() {
            @Override
            public CompletableFuture<OperationResult<List<AvailabilityRule>>> getSpaceAvailabilityRules(String spaceId,
                    String idToken) {
                AvailabilityRule rule = new AvailabilityRule();
                rule.setRuleId("MONDAY");
                rule.setSpaceId(spaceId);
                rule.setDayOfWeek("MONDAY");
                rule.setStartTime("09:00");
                rule.setEndTime("17:00");
                rule.setSlotIntervalMinutes(60);
                rule.setActive(true);

                return CompletableFuture.completedFuture(OperationResult.success(List.of(rule)));
            }

            @Override
            public CompletableFuture<OperationResult<List<AvailabilityBlock>>> getSpaceAvailabilityBlocks(
                    String spaceId, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(Collections.emptyList()));
            }
        };

        mockUserDao = new UserDao() {
            @Override
            public CompletableFuture<OperationResult<User>> getUser(String userId, String idToken) {
                User user = new User();
                user.setUserId(userId);
                user.setRole(UserRole.USER);
                user.setActive(true);
                return CompletableFuture.completedFuture(OperationResult.success(user));
            }
        };

        spaceService = new SpaceService(mockSpaceDao, mockBusinessDao);
        controller = new CustomerMarketplaceController(spaceService, mockSeatDao, mockWorkshopSessionDao,
                mockAvailabilityDao, mockUserDao);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    public void testNavigationGroupMapping() {
        assertEquals(CreativityActivityGroup.ART, controller.mapNavigationGroupToActivityGroup("Art"));
        assertEquals(CreativityActivityGroup.CONTENT_CREATION,
                controller.mapNavigationGroupToActivityGroup("Content Creation"));
        assertEquals(CreativityActivityGroup.PODCASTS, controller.mapNavigationGroupToActivityGroup("Podcasts"));
        assertEquals(CreativityActivityGroup.MUSIC, controller.mapNavigationGroupToActivityGroup("Music"));
        assertNull(controller.mapNavigationGroupToActivityGroup("Fitness"));
        assertNull(controller.mapNavigationGroupToActivityGroup(null));
    }

    @Test
    public void testLoadVisibleV2CreativitySpaces_Success() throws Exception {
        OperationResult<List<Space>> res = controller.loadVisibleV2CreativitySpaces("Art", "Art").get();
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertEquals("space-v2-1", res.getData().get(0).getSpaceId());
    }

    @Test
    public void testLoadVisibleV2CreativitySpaces_Unauthenticated() throws Exception {
        AppSession.clearSession();
        OperationResult<List<Space>> res = controller.loadVisibleV2CreativitySpaces("Art", "Art").get();
        assertTrue(res.isSuccess());
        assertEquals(1, res.getData().size());
        assertEquals("space-v2-1", res.getData().get(0).getSpaceId());
    }

    @Test
    public void testLoadVisibleV2CreativitySpaces_RoleMismatch() throws Exception {
        AppSession.clearSession();
        AppSession.startSession("owner-uid", "owner@example.com", "Owner Name", UserRole.OWNER, "mock-token", "refresh",
                3600);
        OperationResult<List<Space>> res = controller.loadVisibleV2CreativitySpaces("Art", "Art").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.ROLE_MISMATCH, res.getErrorCode());
    }

    @Test
    public void testLoadVisibleV2SpaceDetails_Success() throws Exception {
        OperationResult<Space> res = controller.loadVisibleV2SpaceDetails("space-v2-1").get();
        assertTrue(res.isSuccess());
        assertEquals("space-v2-1", res.getData().getSpaceId());
    }

    @Test
    public void testLoadVisibleV2SpaceDetails_UnapprovedSpace() throws Exception {
        OperationResult<Space> res = controller.loadVisibleV2SpaceDetails("space-unapproved").get();
        assertTrue(res.isFailure());
        assertEquals(OperationCode.NOT_FOUND, res.getErrorCode());
    }

    @Test
    public void testLoadCustomerBookingOptions_SeatManualSelection() throws Exception {
        SeatBookingConfig cfg = new SeatBookingConfig();
        cfg.setSelectionMode(SeatSelectionMode.SEAT_LIST);
        v2Space.setBookingConfig(cfg);
        v2Space.setBookingType(BookingType.SEAT);

        OperationResult<CustomerBookingOptions> res = controller
                .loadCustomerBookingOptions("space-v2-1", LocalDate.now()).get();
        assertTrue(res.isSuccess());
        CustomerBookingOptions opts = res.getData();
        assertNotNull(opts.getSeats());
        assertEquals(1, opts.getSeats().size());
        assertEquals("s1", opts.getSeats().get(0).getSeatId());
    }

    @Test
    public void testLoadCustomerBookingOptions_SeatAutoAssign() throws Exception {
        SeatBookingConfig cfg = new SeatBookingConfig();
        cfg.setSelectionMode(SeatSelectionMode.AUTO_ASSIGN);
        v2Space.setBookingConfig(cfg);
        v2Space.setBookingType(BookingType.SEAT);

        OperationResult<CustomerBookingOptions> res = controller
                .loadCustomerBookingOptions("space-v2-1", LocalDate.now()).get();
        assertTrue(res.isSuccess());
        CustomerBookingOptions opts = res.getData();
        assertEquals("A seat will be assigned automatically during booking.", opts.getSeatSelectionMessage());
    }

    @Test
    public void testLoadCustomerBookingOptions_Workshop() throws Exception {
        WorkshopBookingConfig cfg = new WorkshopBookingConfig();
        v2Space.setBookingConfig(cfg);
        v2Space.setBookingType(BookingType.WORKSHOP);

        OperationResult<CustomerBookingOptions> res = controller
                .loadCustomerBookingOptions("space-v2-1", LocalDate.now()).get();
        assertTrue(res.isSuccess());
        CustomerBookingOptions opts = res.getData();
        assertNotNull(opts.getWorkshopSessions());
        assertEquals(1, opts.getWorkshopSessions().size());
        assertEquals("sess1", opts.getWorkshopSessions().get(0).getSessionId());
    }

    @Test
    public void testLoadCustomerBookingOptions_Slot() throws Exception {
        SlotBookingConfig cfg = new SlotBookingConfig();
        v2Space.setBookingConfig(cfg);
        v2Space.setBookingType(BookingType.SLOT);

        // Monday target date
        LocalDate monday = LocalDate.of(2026, 9, 7);
        OperationResult<CustomerBookingOptions> res = controller.loadCustomerBookingOptions("space-v2-1", monday).get();
        assertTrue(res.isSuccess());
        CustomerBookingOptions opts = res.getData();
        assertNotNull(opts.getAvailableSlots());
        assertFalse(opts.getAvailableSlots().isEmpty());
    }

    @Test
    public void testFirestoreRulesV2CustomerVisibilityContract() throws Exception {
        File rulesFile = new File("firestore.rules");
        assertTrue(rulesFile.exists());
        String rulesContent = Files.readString(rulesFile.toPath());

        assertTrue(rulesContent.contains("function isCustomerVisibleSpace()"));
        assertTrue(rulesContent.contains("function isCustomerVisibleSpaceId(targetSpaceId)"));
        assertTrue(rulesContent.contains("resource.data.schemaVersion == 2"));
        assertTrue(rulesContent.contains("resource.data.approvalStatus == \"APPROVED\""));
        assertTrue(rulesContent.contains("resource.data.operationalStatus == \"ACTIVE\""));
        assertTrue(rulesContent.contains("isCustomerVisibleSpaceId(spaceId)"));
    }
}
