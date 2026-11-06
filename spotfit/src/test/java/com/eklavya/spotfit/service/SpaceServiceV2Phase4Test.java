package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.*;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.model.bookingconfig.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceServiceV2Phase4Test {

    private SpaceService spaceService;
    private DummySpaceDao spaceDao;
    private DummyBusinessDao businessDao;

    @BeforeEach
    public void setUp() {
        spaceDao = new DummySpaceDao();
        businessDao = new DummyBusinessDao();
        spaceService = new SpaceService(spaceDao, businessDao);
    }

    @Test
    public void testValidateV2SpaceReturnsReadyForValidPayload() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        Space space = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        SubmissionReadinessResult readiness = spaceService.validateV2Space(bus, space, "owner-1");

        assertTrue(readiness.isReady(), "Expected ready but missing: " + readiness.getMissingRequirements());
        assertTrue(readiness.getMissingRequirements().isEmpty());
    }

    @Test
    public void testValidateV2SpaceDetectsInvalidPostalCode() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        Space space = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        SpaceLocation loc = space.getSpaceLocation();
        loc.setPostalCode("123");
        space.setSpaceLocation(loc);

        SubmissionReadinessResult readiness = spaceService.validateV2Space(bus, space, "owner-1");

        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(m -> m.contains("6-digit postal code")));
    }

    @Test
    public void testValidateV2SpaceEnforcesWorkshopPriceMatch() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        WorkshopBookingConfig cfg = new WorkshopBookingConfig(150000L, 5, 20, 24, 48, "NONE", List.of(), "Instructor");
        Space space = createSampleV2Space("owner-1", BookingType.WORKSHOP, cfg);
        space.setPricingUnit(PricingUnit.PERSON);
        space.setBasePricePaise(100000L); // mismatch! base is 100000, pricePerPerson is 150000

        SubmissionReadinessResult readiness = spaceService.validateV2Space(bus, space, "owner-1");

        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(m -> m.contains("Workshop pricePerPersonPaise must equal basePricePaise")));
    }

    @Test
    public void testValidateV2SpaceValidatesOperatingHoursTimes() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        Space space = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        Map<String, OperatingDayConfig> oh = new LinkedHashMap<>();
        oh.put("MONDAY", new OperatingDayConfig("18:00", "09:00", true)); // invalid, closing before opening
        space.setOperatingHours(oh);

        SubmissionReadinessResult readiness = spaceService.validateV2Space(bus, space, "owner-1");

        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(m -> m.contains("Opening time must precede closing time")));
    }

    @Test
    public void testValidateV2SpaceRejectsResourceBookingType() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        Space space = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        space.setBookingType(BookingType.RESOURCE);

        SubmissionReadinessResult readiness = spaceService.validateV2Space(bus, space, "owner-1");

        assertFalse(readiness.isReady());
        assertTrue(readiness.getMissingRequirements().stream().anyMatch(m -> m.contains("Valid V2 booking type is required")));
    }

    @Test
    public void testCreateV2SpaceLoadsBusinessAuthoritatively() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "bus-1", submitted, "token-1").join();

        assertTrue(res.isSuccess(), "createV2Space failed: " + res.getMessage());
        assertNotNull(res.getData());
        assertEquals("bus-1", res.getData().getBusinessId());
        assertEquals("owner-1", res.getData().getOwnerId());
    }

    @Test
    public void testCreateV2SpaceFailsWhenBusinessNotFound() {
        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "non-existent-bus", submitted, "token-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.NOT_FOUND, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceFailsWhenOwnerMismatchOnBusiness() {
        Business bus = createSampleV2Business("bus-1", "other-owner", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "bus-1", submitted, "token-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceFailsWhenBusinessNotApproved() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.DRAFT);
        businessDao.setMockBusiness("bus-1", bus);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "bus-1", submitted, "token-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
    }

    @Test
    public void testCreateV2SpaceAssignsAuthoritativeDefaults() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "bus-1", submitted, "token-1").join();

        assertTrue(res.isSuccess());
        Space space = res.getData();
        assertEquals(2, space.getSchemaVersion());
        assertEquals(SpaceStatus.DRAFT, space.getStatus());
        assertEquals(SpaceApprovalStatus.DRAFT, space.getApprovalStatus());
        assertEquals(SpaceOperationalStatus.INACTIVE, space.getOperationalStatus());
        assertTrue(space.getCreatedAt() > 0L);
        assertEquals(space.getCreatedAt(), space.getUpdatedAt());
    }

    @Test
    public void testUpdateV2SpaceRejectsSubmittedOrUnderReviewSpace() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space existing = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        existing.setApprovalStatus(SpaceApprovalStatus.SUBMITTED);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        submitted.setSpaceName("New Name Request");

        OperationResult<Space> res = spaceService.updateV2Space("owner-1", existing, submitted, "token-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.PERMISSION_DENIED, res.getErrorCode());
        assertTrue(res.getMessage().contains("Cannot edit Space while SUBMITTED or UNDER_REVIEW"));
    }

    @Test
    public void testUpdateV2SpaceResetsApprovedStatusOnMaterialEdit() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space existing = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        existing.setApprovalStatus(SpaceApprovalStatus.APPROVED);
        existing.setOperationalStatus(SpaceOperationalStatus.ACTIVE);
        existing.setStatus(SpaceStatus.LIVE);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        submitted.setSpaceName("Changed Pottery Studio Name"); // material change!

        OperationResult<Space> res = spaceService.updateV2Space("owner-1", existing, submitted, "token-1").join();

        assertTrue(res.isSuccess());
        Space updated = res.getData();
        assertEquals(SpaceApprovalStatus.DRAFT, updated.getApprovalStatus());
        assertEquals(SpaceOperationalStatus.INACTIVE, updated.getOperationalStatus());
        assertEquals(SpaceStatus.DRAFT, updated.getStatus());
    }

    @Test
    public void testUpdateV2SpaceDoesNotResetApprovedStatusWhenUnchanged() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space existing = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        existing.setApprovalStatus(SpaceApprovalStatus.APPROVED);
        existing.setOperationalStatus(SpaceOperationalStatus.ACTIVE);
        existing.setStatus(SpaceStatus.LIVE);

        // Identical submitted space object
        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));

        OperationResult<Space> res = spaceService.updateV2Space("owner-1", existing, submitted, "token-1").join();

        assertTrue(res.isSuccess());
        Space updated = res.getData();
        assertEquals(SpaceApprovalStatus.APPROVED, updated.getApprovalStatus());
        assertEquals(SpaceOperationalStatus.ACTIVE, updated.getOperationalStatus());
        assertEquals(SpaceStatus.LIVE, updated.getStatus());
    }

    @Test
    public void testUpdateV2SpaceClearsRejectionMetadataOnEdit() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space existing = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        existing.setApprovalStatus(SpaceApprovalStatus.REJECTED);
        existing.setRejectionReason("Inadequate description");

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        submitted.setDescription("Now updated with rich detailed studio information");

        OperationResult<Space> res = spaceService.updateV2Space("owner-1", existing, submitted, "token-1").join();

        assertTrue(res.isSuccess());
        Space updated = res.getData();
        assertEquals(SpaceApprovalStatus.DRAFT, updated.getApprovalStatus());
        assertEquals("", updated.getRejectionReason());
    }

    @Test
    public void testCreateV2SpaceSeparatesValidationFailureResult() {
        Business bus = createSampleV2Business("bus-1", "owner-1", BusinessStatus.APPROVED);
        businessDao.setMockBusiness("bus-1", bus);

        Space submitted = createSampleV2Space("owner-1", BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        submitted.setSpaceName(""); // validation error!

        OperationResult<Space> res = spaceService.createV2Space("owner-1", "bus-1", submitted, "token-1").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
        assertTrue(res.getMessage().contains("V2 Space validation failed"));
    }

    private Business createSampleV2Business(String businessId, String ownerId, String status) {
        return Business.createV2(businessId, ownerId, "Craft Studio", "Legal Name",
                "email@test.com", "9999999999", "Desc", "Address", "Landmark", "City", "State", "400001",
                "REG123", SpaceCategory.CREATIVITY, List.of(CreativityActivityGroup.ART), "http://logo.png", "http://web.com",
                status, true, 0L, 0L, "", 1000L, 1000L);
    }

    private Space createSampleV2Space(String ownerId, String bookingType, BookingConfig bookingConfig) {
        SpaceLocation loc = new SpaceLocation("123 Craft Lane", "Studio Zone", "Mumbai", "Maharashtra", "400001", "Near Hub", 19.0, 72.8);
        Map<String, OperatingDayConfig> oh = new LinkedHashMap<>();
        oh.put("MONDAY", new OperatingDayConfig("09:00", "18:00", true));

        Space space = Space.createV2(
                "v2-space-1", "bus-1", ownerId, "Pottery Studio", "Studio for wheel spinning",
                SpaceCategory.CREATIVITY, CreativityActivityGroup.ART, "POTTERY_WHEEL", loc,
                "http://img.png", List.of("http://img1.png"), List.of("WiFi"), List.of("Wheel"),
                10, bookingType, 100000L, PricingUnit.HOUR,
                bookingConfig, oh, List.of("Clean up"), "Flexible"
        );
        space.setTimeZoneId("Asia/Kolkata");
        return space;
    }

    private static class DummySpaceDao extends SpaceDao {
        @Override
        public CompletableFuture<OperationResult<Space>> createV2Space(Space space, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(space));
        }

        @Override
        public CompletableFuture<OperationResult<Space>> updateV2Space(Space space, List<String> updateMask, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(space));
        }
    }

    private static class DummyBusinessDao extends BusinessDao {
        private final Map<String, Business> store = new HashMap<>();

        public void setMockBusiness(String id, Business bus) {
            store.put(id, bus);
        }

        @Override
        public CompletableFuture<OperationResult<Business>> getBusiness(String businessId, String idToken) {
            if (store.containsKey(businessId)) {
                return CompletableFuture.completedFuture(OperationResult.success(store.get(businessId)));
            }
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Business not found"));
        }
    }
}
