package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SeatSelectionMode;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceDaoV2Phase4Test {

    private SpaceDao spaceDao;

    @BeforeEach
    public void setUp() {
        spaceDao = new SpaceDao(new DummyFirestoreRestClient());
    }

    @Test
    public void testV1DocumentDeserializationDefaultsToSchemaVersion1() {
        JSONObject fields = new JSONObject();
        fields.put("spaceId", FirestoreDocumentUtil.stringValue("space-123"));
        fields.put("businessId", FirestoreDocumentUtil.stringValue("bus-123"));
        fields.put("ownerId", FirestoreDocumentUtil.stringValue("owner-123"));
        fields.put("spaceName", FirestoreDocumentUtil.stringValue("V1 Studio"));
        fields.put("description", FirestoreDocumentUtil.stringValue("Legacy Space"));
        fields.put("category", FirestoreDocumentUtil.stringValue(SpaceCategory.CREATIVITY));
        fields.put("activityGroup", FirestoreDocumentUtil.stringValue("MUSIC"));
        fields.put("spaceType", FirestoreDocumentUtil.stringValue("ROOM"));

        JSONObject doc = FirestoreDocumentUtil.createDocument(fields);
        Space space = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(space);
        assertEquals(1, space.getSchemaVersion());
        assertEquals("space-123", space.getSpaceId());
        assertEquals("V1 Studio", space.getSpaceName());
        assertEquals("MUSIC", space.getActivityType());
    }

    @Test
    public void testV1DocumentSerializationFormat() {
        Space v1Space = new Space("space-v1", "bus-1", "owner-1", "V1 Name", "V1 Desc",
                SpaceCategory.CREATIVITY, "MUSIC", "ROOM",
                "123 Main St", "Near Park", "Mumbai", "Maharashtra", "400001",
                10, List.of("WiFi"), List.of("Jamming"), List.of("No Smoking"),
                "09:00", "21:00", List.of("MONDAY"),
                SpaceStatus.DRAFT, true, 1000L, 1000L);

        JSONObject doc = spaceDao.toFirestoreDocument(v1Space);
        JSONObject fields = FirestoreDocumentUtil.getFields(doc);

        assertFalse(fields.has("schemaVersion"));
        assertFalse(fields.has("activityType"));
        assertEquals("space-v1", FirestoreDocumentUtil.getString(fields, "spaceId"));
        assertEquals("V1 Name", FirestoreDocumentUtil.getString(fields, "spaceName"));
    }

    @Test
    public void testV2DocumentSerializationUsesCanonicalKeys() {
        Space v2Space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        JSONObject doc = spaceDao.toFirestoreDocument(v2Space);
        JSONObject fields = FirestoreDocumentUtil.getFields(doc);

        assertEquals(2, FirestoreDocumentUtil.getInt(fields, "schemaVersion", 0));
        assertTrue(fields.has("activityType"));
        assertFalse(fields.has("v2ActivityType"));
        assertEquals("POTTERY_WHEEL", FirestoreDocumentUtil.getString(fields, "activityType"));
        assertEquals("integerValue", fields.getJSONObject("basePricePaise").keySet().iterator().next());
    }

    @Test
    public void testV2SeatBookingConfigRoundTrip() {
        SeatBookingConfig cfg = new SeatBookingConfig(SeatSelectionMode.VISUAL_MAP, 60, 480, 30, 4, "PER_SEAT", true, 30, 15);
        Space space = createSampleV2Space(BookingType.SEAT, cfg);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertEquals(2, restored.getSchemaVersion());
        assertEquals(BookingType.SEAT, restored.getBookingType());
        assertTrue(restored.getBookingConfig() instanceof SeatBookingConfig);
        SeatBookingConfig rCfg = (SeatBookingConfig) restored.getBookingConfig();
        assertEquals(SeatSelectionMode.VISUAL_MAP, rCfg.getSelectionMode());
        assertEquals(4, rCfg.getMaxGuestsPerBooking());
    }

    @Test
    public void testV2SlotBookingConfigRoundTrip() {
        SlotBookingConfig cfg = new SlotBookingConfig(60, 240, 30, 30, 15, 15, 12, 30, 30);
        Space space = createSampleV2Space(BookingType.SLOT, cfg);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertTrue(restored.getBookingConfig() instanceof SlotBookingConfig);
        SlotBookingConfig rCfg = (SlotBookingConfig) restored.getBookingConfig();
        assertEquals(15, rCfg.getBufferBeforeMinutes());
        assertEquals(12, rCfg.getMaxPeople());
    }

    @Test
    public void testV2WorkshopBookingConfigRoundTrip() {
        WorkshopBookingConfig cfg = new WorkshopBookingConfig(150000L, 5, 20, 24, 48, "18+", List.of("Clay", "Wheel"), "Master Artisan");
        Space space = createSampleV2Space(BookingType.WORKSHOP, cfg);
        space.setPricingUnit(PricingUnit.PERSON);
        space.setBasePricePaise(150000L);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertTrue(restored.getBookingConfig() instanceof WorkshopBookingConfig);
        WorkshopBookingConfig rCfg = (WorkshopBookingConfig) restored.getBookingConfig();
        assertEquals(150000L, rCfg.getPricePerPersonPaise());
        assertEquals("18+", rCfg.getAgeRestriction());
        assertEquals("Master Artisan", rCfg.getInstructorName());
        assertEquals(List.of("Clay", "Wheel"), rCfg.getMaterialsIncluded());
    }

    @Test
    public void testV2RentalBookingConfigRoundTrip() {
        RentalBookingConfig cfg = new RentalBookingConfig(5, 120, 1440, 50000L, true, true, 15.5, 2000L, "Front Gate", true, 1000L, List.of("Stand", "Case"));
        Space space = createSampleV2Space(BookingType.RENTAL, cfg);
        space.setPricingUnit(PricingUnit.DAY);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertTrue(restored.getBookingConfig() instanceof RentalBookingConfig);
        RentalBookingConfig rCfg = (RentalBookingConfig) restored.getBookingConfig();
        assertEquals(5, rCfg.getAvailableQuantity());
        assertEquals(15.5, rCfg.getDeliveryRadiusKm(), 0.001);
        assertEquals(2000L, rCfg.getDeliveryChargePaise());
        assertEquals(List.of("Stand", "Case"), rCfg.getAddOns());
    }

    @Test
    public void testV2VenueBookingConfigRoundTrip() {
        VenueBookingConfig cfg = new VenueBookingConfig(4, 24, true, 2, List.of("Concert", "Exhibition"), List.of("Banquet"), 100000L, true, true, true, true, true, true, 5000L);
        Space space = createSampleV2Space(BookingType.VENUE, cfg);
        space.setPricingUnit(PricingUnit.EVENT);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertTrue(restored.getBookingConfig() instanceof VenueBookingConfig);
        VenueBookingConfig rCfg = (VenueBookingConfig) restored.getBookingConfig();
        assertEquals(4, rCfg.getMinimumBookingHours());
        assertEquals(List.of("Concert", "Exhibition"), rCfg.getAllowedEventTypes());
        assertTrue(rCfg.isSoundSystemAvailable());
    }

    @Test
    public void testV2PracticeRoomBookingConfigRoundTrip() {
        PracticeRoomBookingConfig cfg = new PracticeRoomBookingConfig(60, 240, 30, true, List.of("Drumkit"), List.of("Guitar Amp"));
        Space space = createSampleV2Space(BookingType.PRACTICE_ROOM, cfg);
        space.setPricingUnit(PricingUnit.HOUR);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        assertTrue(restored.getBookingConfig() instanceof PracticeRoomBookingConfig);
        PracticeRoomBookingConfig rCfg = (PracticeRoomBookingConfig) restored.getBookingConfig();
        assertTrue(rCfg.isEquipmentSelectionEnabled());
        assertEquals(List.of("Drumkit"), rCfg.getIncludedEquipment());
    }

    @Test
    public void testV2LocationNestedMapRoundTrip() {
        SpaceLocation loc = new SpaceLocation("456 Art Way", "Kala Ghoda", "Mumbai", "Maharashtra", "400001", "Opp Museum", 18.9267, 72.8327);
        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        space.setSpaceLocation(loc);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        SpaceLocation rLoc = restored.getSpaceLocation();
        assertNotNull(rLoc);
        assertEquals("456 Art Way", rLoc.getAddressLine());
        assertEquals("Kala Ghoda", rLoc.getArea());
        assertEquals(18.9267, rLoc.getLatitude(), 0.0001);
        assertEquals(72.8327, rLoc.getLongitude(), 0.0001);
    }

    @Test
    public void testV2OperatingHoursMapRoundTrip() {
        Map<String, OperatingDayConfig> oh = new LinkedHashMap<>();
        oh.put("MONDAY", new OperatingDayConfig("09:00", "18:00", true));
        oh.put("SUNDAY", new OperatingDayConfig("10:00", "16:00", false));

        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        space.setOperatingHours(oh);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        Space restored = spaceDao.fromFirestoreDocument(doc);

        assertNotNull(restored);
        Map<String, OperatingDayConfig> rOh = restored.getOperatingHours();
        assertNotNull(rOh);
        assertTrue(rOh.containsKey("MONDAY"));
        assertEquals("09:00", rOh.get("MONDAY").getOpeningTime());
        assertFalse(rOh.get("SUNDAY").isActive());
    }

    @Test
    public void testUpdateV2SpaceDefensivelyRejectsForbiddenMaskFields() {
        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        List<String> forbiddenMask = List.of("spaceName", "ownerId");

        OperationResult<Space> res = spaceDao.updateV2Space(space, forbiddenMask, "mock-token").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
        assertTrue(res.getMessage().contains("Forbidden field in update mask rejected"));
    }

    @Test
    public void testUpdateV2SpaceDefensivelyRejectsUnknownOrMalformedMaskFields() {
        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        List<String> malformedMask = List.of("spaceName", "location/addressLine");

        OperationResult<Space> res = spaceDao.updateV2Space(space, malformedMask, "mock-token").join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.INVALID_ARGUMENT, res.getErrorCode());
        assertTrue(res.getMessage().contains("Path-like field in update mask rejected"));
    }

    @Test
    public void testUpdateV2SpaceAcceptsValidAllowlistMask() {
        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        List<String> validMask = List.of("spaceName", "description", "capacity", "updatedAt");

        OperationResult<Space> res = spaceDao.updateV2Space(space, validMask, "mock-token").join();

        assertTrue(res.isSuccess());
    }

    @Test
    public void testCurrencyFieldsSerializedAsIntegerValue() {
        Space space = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig(60, 240, 30, 30, 0, 0, 10, 30, 0));
        space.setBasePricePaise(125000L);

        JSONObject doc = spaceDao.toFirestoreDocument(space);
        JSONObject fields = FirestoreDocumentUtil.getFields(doc);

        JSONObject basePriceObj = fields.getJSONObject("basePricePaise");
        assertTrue(basePriceObj.has("integerValue"));
        assertEquals("125000", basePriceObj.getString("integerValue"));
    }

    private Space createSampleV2Space(String bookingType, BookingConfig bookingConfig) {
        SpaceLocation loc = new SpaceLocation("123 Craft Lane", "Studio Zone", "Mumbai", "Maharashtra", "400001", "Near Hub", 19.0, 72.8);
        Map<String, OperatingDayConfig> oh = new LinkedHashMap<>();
        oh.put("MONDAY", new OperatingDayConfig("09:00", "18:00", true));

        Space space = Space.createV2(
                "v2-space-1", "bus-1", "owner-1", "Pottery Studio", "Studio for wheel spinning",
                SpaceCategory.CREATIVITY, "POTTERY", "POTTERY_WHEEL", loc,
                "http://img.png", List.of("http://img1.png"), List.of("WiFi"), List.of("Wheel"),
                10, bookingType, 100000L, PricingUnit.HOUR,
                bookingConfig, oh, List.of("Clean up"), "Flexible"
        );
        space.setTimeZoneId("Asia/Kolkata");
        return space;
    }

    private static class DummyFirestoreRestClient extends FirestoreRestClient {
        @Override
        public CompletableFuture<OperationResult<JSONObject>> setDocument(String collection, String documentId, JSONObject document, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(document));
        }

        @Override
        public CompletableFuture<OperationResult<JSONObject>> updateDocument(String collection, String documentId, JSONObject document, List<String> updateMask, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(document));
        }
    }
}
