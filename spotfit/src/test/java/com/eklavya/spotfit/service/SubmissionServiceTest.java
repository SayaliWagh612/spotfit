package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.OfferingStatus;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionServiceTest {

    private SubmissionService submissionService;
    private String ownerId;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService();
        ownerId = "owner-123";
    }

    @Test
    @DisplayName("Business readiness succeeds for fully populated DRAFT business")
    void testValidateBusinessReadiness_Success() {
        Business biz = new Business(
                "biz-1", ownerId, "FitStudio", "FitStudio LLC",
                "info@fitstudio.com", "9876543210", "A fitness center",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                "REG123", BusinessStatus.DRAFT, true, 1000L, 1000L
        );

        OperationResult<SubmissionReadinessResult> result = submissionService.validateBusinessReadiness(biz, ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().isEmpty());
    }

    @Test
    @DisplayName("Business readiness fails when mandatory profile fields are missing")
    void testValidateBusinessReadiness_MissingFields() {
        Business biz = new Business();
        biz.setBusinessId("biz-1");
        biz.setOwnerId(ownerId);
        biz.setVerificationStatus(BusinessStatus.DRAFT);
        biz.setActive(true);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateBusinessReadiness(biz, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        List<String> missing = result.getData().getMissingRequirements();
        assertTrue(missing.stream().anyMatch(s -> s.contains("Business name")));
        assertTrue(missing.stream().anyMatch(s -> s.contains("email")));
        assertTrue(missing.stream().anyMatch(s -> s.contains("phone")));
        assertTrue(missing.stream().anyMatch(s -> s.contains("postal code")));
    }

    @Test
    @DisplayName("Space readiness fails when parent Business is not APPROVED")
    void testValidateSpaceReadiness_ParentBusinessNotApproved() {
        Business parentBiz = new Business(
                "biz-1", ownerId, "FitStudio", "FitStudio LLC",
                "info@fitstudio.com", "9876543210", "A fitness center",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                "REG123", BusinessStatus.DRAFT, true, 1000L, 1000L
        );

        Space space = createSampleSpace("space-1", "biz-1", ownerId);
        Resource res = createSampleResource("res-1", "space-1", ownerId, "biz-1", ResourceType.ROOM);
        Offering off = createSampleOffering("off-1", "space-1", ownerId, "biz-1", ResourceType.ROOM);

        Map<String, List<AvailabilityRule>> ruleMap = new HashMap<>();
        ruleMap.put("res-1", List.of(new AvailabilityRule("rule-1", "space-1", "res-1", ownerId, "MONDAY", "09:00", "17:00", 60, 60, 240, true, 1000L, 1000L)));

        OperationResult<SubmissionReadinessResult> result = submissionService.validateSpaceReadiness(
                parentBiz, space, List.of(res), List.of(off), ruleMap, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("Parent Business must be APPROVED")));
    }

    @Test
    @DisplayName("Offering-compatible Resource has no schedule -> Space readiness fails")
    void testValidateSpaceReadiness_CompatibleResourceNoSchedule() {
        Business parentBiz = createApprovedBusiness("biz-1", ownerId);
        Space space = createSampleSpace("space-1", "biz-1", ownerId);
        Resource roomRes = createSampleResource("res-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);
        Offering roomOffering = createSampleOffering("off-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);

        // Empty rule map -> no schedule for roomRes
        Map<String, List<AvailabilityRule>> ruleMap = new HashMap<>();

        OperationResult<SubmissionReadinessResult> result = submissionService.validateSpaceReadiness(
                parentBiz, space, List.of(roomRes), List.of(roomOffering), ruleMap, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("requires a compatible active Resource with at least one active Availability Rule")));
    }

    @Test
    @DisplayName("Unrelated Resource has schedule, but offering-compatible Resource has no schedule -> Space readiness fails")
    void testValidateSpaceReadiness_UnrelatedResourceScheduleFails() {
        Business parentBiz = createApprovedBusiness("biz-1", ownerId);
        Space space = createSampleSpace("space-1", "biz-1", ownerId);

        // Space has 2 resources: EQUIPMENT and ROOM
        Resource equipRes = createSampleResource("res-equip", "space-1", ownerId, "biz-1", ResourceType.EQUIPMENT);
        Resource roomRes = createSampleResource("res-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);

        // Offering requires ROOM
        Offering roomOffering = createSampleOffering("off-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);

        // Only EQUIPMENT has an active schedule! ROOM has none.
        Map<String, List<AvailabilityRule>> ruleMap = new HashMap<>();
        ruleMap.put("res-equip", List.of(new AvailabilityRule("rule-equip", "space-1", "res-equip", ownerId, "MONDAY", "09:00", "17:00", 60, 60, 240, true, 1000L, 1000L)));
        ruleMap.put("res-room", Collections.emptyList());

        OperationResult<SubmissionReadinessResult> result = submissionService.validateSpaceReadiness(
                parentBiz, space, List.of(equipRes, roomRes), List.of(roomOffering), ruleMap, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("requires a compatible active Resource with at least one active Availability Rule")));
    }

    @Test
    @DisplayName("Space readiness succeeds when all requirements and compatible resource schedules are present")
    void testValidateSpaceReadiness_FullSuccess() {
        Business parentBiz = createApprovedBusiness("biz-1", ownerId);
        Space space = createSampleSpace("space-1", "biz-1", ownerId);
        Resource roomRes = createSampleResource("res-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);
        Offering roomOffering = createSampleOffering("off-room", "space-1", ownerId, "biz-1", ResourceType.ROOM);

        Map<String, List<AvailabilityRule>> ruleMap = new HashMap<>();
        ruleMap.put("res-room", List.of(new AvailabilityRule("rule-room", "space-1", "res-room", ownerId, "MONDAY", "09:00", "17:00", 60, 60, 240, true, 1000L, 1000L)));

        OperationResult<SubmissionReadinessResult> result = submissionService.validateSpaceReadiness(
                parentBiz, space, List.of(roomRes), List.of(roomOffering), ruleMap, ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().isEmpty());
    }

    @Test
    @DisplayName("V2 Space readiness succeeds for valid SEAT space with active seats")
    void testValidateV2SpaceReadiness_SeatSuccess() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2SeatSpace("space-1", "biz-1", ownerId);

        Seat seat1 = new Seat("seat-1", "space-1", "Seat 1", "S1", "A", "1", 1, 1, 0L, "AVAILABLE", true, 1000L, 1000L);
        Seat seat2 = new Seat("seat-2", "space-1", "Seat 2", "S2", "A", "2", 2, 1, 0L, "AVAILABLE", true, 1000L, 1000L);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, List.of(seat1, seat2), null, null, ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().isEmpty());
    }

    @Test
    @DisplayName("V2 Space readiness fails for SEAT space with no seats")
    void testValidateV2SpaceReadiness_SeatNoSeatsFails() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2SeatSpace("space-1", "biz-1", ownerId);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, Collections.emptyList(), null, null, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("at least one active Seat")));
    }

    @Test
    @DisplayName("V2 Space readiness succeeds for WORKSHOP space with future active sessions")
    void testValidateV2SpaceReadiness_WorkshopSuccess() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2WorkshopSpace("space-1", "biz-1", ownerId);

        long futureStart = System.currentTimeMillis() + 86400000L;
        long futureEnd = futureStart + 7200000L;
        WorkshopSession sess = new WorkshopSession(
                "sess-1", "space-1", futureStart, futureEnd, 20, 0,
                0L, "SCHEDULED", true, 1000L, 1000L
        );

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, null, null, List.of(sess), ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
    }

    @Test
    @DisplayName("V2 Space readiness fails when parent business is not CREATIVITY category")
    void testValidateV2SpaceReadiness_NonCreativityBusinessFails() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        parentBiz.setCategory("FITNESS");
        Space space = createSampleV2SeatSpace("space-1", "biz-1", ownerId);

        Seat seat1 = new Seat("seat-1", "space-1", "Seat 1", "S1", "A", "1", 1, 1, 0L, "AVAILABLE", true, 1000L, 1000L);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, List.of(seat1), null, null, ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().stream()
                .anyMatch(s -> s.contains("CREATIVITY")));
    }

    @Test
    @DisplayName("V2 Space readiness succeeds even when coverPhotoUrl is empty")
    void testValidateV2SpaceReadiness_CoverPhotoNotRequired() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2SeatSpace("space-1", "biz-1", ownerId);
        space.setCoverPhotoUrl("");

        Seat seat1 = new Seat("seat-1", "space-1", "Seat 1", "S1", "A", "1", 1, 1, 0L, "AVAILABLE", true, 1000L, 1000L);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, List.of(seat1), null, null, ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().isEmpty());
    }

    @Test
    @DisplayName("V2 Space readiness does not fail with 'Space is already approved' for APPROVED space")
    void testValidateV2SpaceReadiness_ApprovedSpaceEvaluatesConfigReadiness() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2SeatSpace("space-1", "biz-1", ownerId);
        space.setApprovalStatus(com.eklavya.spotfit.constants.SpaceApprovalStatus.APPROVED);

        Seat seat1 = new Seat("seat-1", "space-1", "Seat 1", "S1", "A", "1", 1, 1, 0L, "AVAILABLE", true, 1000L, 1000L);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, List.of(seat1), null, null, ownerId);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isReady());
        assertFalse(result.getData().getMissingRequirements().contains("Space is already approved."));
    }

    @Test
    @DisplayName("V2 WORKSHOP space readiness returns exact missing session guidance message when missing sessions")
    void testValidateV2SpaceReadiness_WorkshopMissingSessionMessage() {
        Business parentBiz = createApprovedV2Business("biz-1", ownerId);
        Space space = createSampleV2WorkshopSpace("space-1", "biz-1", ownerId);

        OperationResult<SubmissionReadinessResult> result = submissionService.validateV2SpaceReadiness(
                parentBiz, space, null, null, Collections.emptyList(), ownerId);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isReady());
        assertTrue(result.getData().getMissingRequirements().contains("Add at least one future scheduled Workshop Session using Manage Sessions."));
    }

    private Business createApprovedBusiness(String bizId, String ownerId) {
        return new Business(
                bizId, ownerId, "FitStudio", "FitStudio LLC",
                "info@fitstudio.com", "9876543210", "A fitness center",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                "REG123", BusinessStatus.APPROVED, true, 1000L, 1000L
        );
    }

    private Business createApprovedV2Business(String bizId, String ownerId) {
        Business b = new Business(
                bizId, ownerId, "FitStudio", "FitStudio LLC",
                "info@fitstudio.com", "9876543210", "A fitness center",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                "REG123", BusinessStatus.APPROVED, true, 1000L, 1000L
        );
        b.setCategory("CREATIVITY");
        b.setActivityGroups(List.of("DANCE"));
        b.setSchemaVersion(2);
        return b;
    }

    private Space createSampleSpace(String spaceId, String bizId, String ownerId) {
        return new Space(
                spaceId, bizId, ownerId, "Yoga Hall", "Quiet hall",
                "Fitness", "Yoga", "Studio", "123 Main St", "Park",
                "Mumbai", "Maharashtra", "400001", 30, List.of("AC", "Mats"),
                List.of("Yoga"), List.of("No shoes"), "06:00", "22:00",
                List.of("MONDAY", "TUESDAY", "WEDNESDAY"), "Asia/Kolkata",
                SpaceStatus.DRAFT, true, 1000L, 1000L
        );
    }

    private Space createSampleV2SeatSpace(String spaceId, String bizId, String ownerId) {
        com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig cfg =
                new com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig(com.eklavya.spotfit.constants.SeatSelectionMode.SEAT_LIST, 60, 240, 60, 5, "PER_SEAT", true, 30, 0);

        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("MONDAY", new OperatingDayConfig("09:00", "21:00", true));

        Space s = new Space();
        s.setSchemaVersion(2);
        s.setSpaceId(spaceId);
        s.setBusinessId(bizId);
        s.setOwnerId(ownerId);
        s.setSpaceName("Painting Hub");
        s.setDescription("Creative space");
        s.setCategory("CREATIVITY");
        s.setActivityGroup("DANCE");
        s.setActivityType("Ballet");
        s.setCoverPhotoUrl("https://example.com/cover.jpg");
        s.setCapacity(20);
        s.setBasePricePaise(50000L);
        s.setPricingUnit("HOUR");
        s.setBookingType("SEAT");
        s.setBookingConfig(cfg);
        s.setOperatingHours(hours);
        s.setAmenities(List.of("Easel"));
        s.setFacilities(List.of("WIFI"));
        s.setRules(List.of("No loud noise"));
        s.setCancellationPolicy("Flexible cancellation");
        s.setTimeZoneId("Asia/Kolkata");
        s.setApprovalStatus(com.eklavya.spotfit.constants.SpaceApprovalStatus.DRAFT);
        s.setOperationalStatus(com.eklavya.spotfit.constants.SpaceOperationalStatus.INACTIVE);
        s.setActive(true);
        s.setCreatedAt(1000L);
        s.setUpdatedAt(1000L);
        s.setSpaceLocation(new SpaceLocation("123 Art St", "Downtown", "Mumbai", "Maharashtra", "400001", "Near Park", 19.0, 72.0));
        return s;
    }

    private Space createSampleV2WorkshopSpace(String spaceId, String bizId, String ownerId) {
        com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig cfg =
                new com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig(50000L, 1, 20, 2, 24, "NONE", List.of("Canvas"), "Instructor A");

        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("MONDAY", new OperatingDayConfig("09:00", "21:00", true));

        Space s = new Space();
        s.setSchemaVersion(2);
        s.setSpaceId(spaceId);
        s.setBusinessId(bizId);
        s.setOwnerId(ownerId);
        s.setSpaceName("Workshop Room");
        s.setDescription("Creative workshop");
        s.setCategory("CREATIVITY");
        s.setActivityGroup("DANCE");
        s.setActivityType("Ballet");
        s.setCoverPhotoUrl("https://example.com/cover.jpg");
        s.setCapacity(20);
        s.setBasePricePaise(50000L);
        s.setPricingUnit("SESSION");
        s.setBookingType("WORKSHOP");
        s.setBookingConfig(cfg);
        s.setOperatingHours(hours);
        s.setAmenities(List.of("Tools"));
        s.setFacilities(List.of("WIFI"));
        s.setRules(List.of("Safety first"));
        s.setCancellationPolicy("Standard cancellation");
        s.setTimeZoneId("Asia/Kolkata");
        s.setApprovalStatus(com.eklavya.spotfit.constants.SpaceApprovalStatus.DRAFT);
        s.setOperationalStatus(com.eklavya.spotfit.constants.SpaceOperationalStatus.INACTIVE);
        s.setActive(true);
        s.setCreatedAt(1000L);
        s.setUpdatedAt(1000L);
        s.setSpaceLocation(new SpaceLocation("123 Art St", "Downtown", "Mumbai", "Maharashtra", "400001", "Near Park", 19.0, 72.0));
        return s;
    }

    private Resource createSampleResource(String resId, String spaceId, String ownerId, String bizId, String type) {
        return new Resource(
                resId, spaceId, bizId, ownerId, "Main Room", "Spacious room",
                type, "RM-1", 30, List.of("Mats"), List.of("Audio"),
                "AVAILABLE", true, 1000L, 1000L
        );
    }

    private Offering createSampleOffering(String offId, String spaceId, String ownerId, String bizId, String reqType) {
        return new Offering(
                offId, spaceId, bizId, ownerId, "Yoga Class", "1h yoga session",
                "HOURLY", "PER_PERSON", 50000L, reqType, 60, 60, 120, 30,
                20, 1, 30, List.of("Mat"), List.of("Arrive 5m early"),
                OfferingStatus.DRAFT, true, 1000L, 1000L
        );
    }
}
