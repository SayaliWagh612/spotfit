package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.model.bookingconfig.BookingConfig;
import com.eklavya.spotfit.model.bookingconfig.PracticeRoomBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.RentalBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.VenueBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Unit Test Suite for SpotFit Owner V2 — Phase 3 & 3A Space Domain Model.
 */
class SpaceV2Phase3Test {

    // --- Category 1: V1 Compatibility Tests ---

    @Test
    @DisplayName("1. No-arg Space constructor defaults to schema version 1")
    void testNoArgConstructorSchemaVersion() {
        Space space = new Space();
        assertEquals(1, space.getSchemaVersion());
    }

    @Test
    @DisplayName("2. Every legacy constructor remains available and instantiates successfully")
    void testLegacyConstructorsAvailability() {
        Space s1 = new Space();
        assertNotNull(s1);

        Space s2 = new Space("s1", "b1", "o1", "Studio A", "Desc", "DANCE", "Creativity", "ROOM",
                "Line 1", "Mark", "City", "State", "123456", 10, List.of("Wifi"), List.of("Dance"), List.of("No Smoking"),
                "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", SpaceStatus.APPROVED, true, 100L, 200L);
        assertNotNull(s2);

        Space s3 = new Space("s1", "b1", "o1", "Studio A", "Desc", "DANCE", "Creativity", "ROOM",
                "Line 1", "Mark", "City", "State", "123456", 10, List.of("Wifi"), List.of("Dance"), List.of("No Smoking"),
                "09:00", "18:00", List.of("MONDAY"), "Asia/Kolkata", SpaceStatus.APPROVED, true,
                50L, 60L, "Reason", 70L, 100L, 200L);
        assertNotNull(s3);

        Space s4 = new Space("s1", "o1", "Studio A", "DANCE", "Creativity", "ROOM", "Desc",
                "Line 1", "City", 10, "e@m.com", "999", "MONDAY", "09:00", "18:00",
                "Rules", "Policy", List.of("Wifi"), SpaceStatus.DRAFT, "m1", 4.5, 10, 5000.0);
        assertNotNull(s4);

        Space s5 = new Space("Studio A", "4.5", 10, "City", "2 km", 500, "hour", "Wifi", "img.png", List.of());
        assertNotNull(s5);
    }

    @Test
    @DisplayName("3. Legacy constructors preserve parameter-to-field mapping")
    void testLegacyConstructorParameterMapping() {
        Space space = new Space("sp1", "bus1", "own1", "My Space", "Desc",
                "DANCE", "Creativity", "ROOM", "Address", "Mark", "Pune", "MH", "411001",
                15, List.of("AC"), List.of("Salsa"), List.of("No Shoes"),
                "10:00", "20:00", List.of("MONDAY", "TUESDAY"), "Asia/Kolkata",
                SpaceStatus.LIVE, true, 1000L, 2000L);

        assertEquals("sp1", space.getSpaceId());
        assertEquals("bus1", space.getBusinessId());
        assertEquals("own1", space.getOwnerId());
        assertEquals("My Space", space.getSpaceName());
        assertEquals("DANCE", space.getCategory());
        assertEquals("Creativity", space.getActivityGroup());
        assertEquals("ROOM", space.getSpaceType());
        assertEquals("Address", space.getAddressLine());
        assertEquals("Pune", space.getCity());
        assertEquals(15, space.getCapacity());
        assertEquals(SpaceStatus.LIVE, space.getStatus());
        assertTrue(space.isActive());
    }

    @Test
    @DisplayName("4. Existing V1 getters/setters behave unchanged")
    void testLegacyGettersAndSetters() {
        Space space = new Space();
        space.setSpaceName("New Name");
        assertEquals("New Name", space.getSpaceName());
        assertEquals("New Name", space.getName());

        space.setAddressLine("123 Main St");
        assertEquals("123 Main St", space.getAddressLine());
        assertEquals("123 Main St", space.getAddress());
    }

    @Test
    @DisplayName("5. Existing status field remains independent from V2 lifecycle")
    void testLegacyStatusIndependence() {
        Space space = new Space();
        space.setStatus(SpaceStatus.LIVE);
        assertEquals(SpaceStatus.LIVE, space.getStatus());
        assertEquals("", space.getApprovalStatus());
        assertEquals("", space.getOperationalStatus());
    }

    @Test
    @DisplayName("6. Existing address fields remain independent from V2 location object")
    void testAddressAndLocationIndependence() {
        Space space = new Space();
        space.setAddressLine("123 Street");
        space.setCity("Mumbai");

        SpaceLocation loc = space.getSpaceLocation();
        assertEquals("", loc.getAddressLine());
        assertEquals("", loc.getCity());

        SpaceLocation newLoc = new SpaceLocation("456 Avenue", "Bandra", "Mumbai", "MH", "400050", "Park", 19.0, 72.8);
        space.setSpaceLocation(newLoc);

        assertEquals("123 Street", space.getAddressLine());
        assertEquals("456 Avenue", space.getSpaceLocation().getAddressLine());
    }

    @Test
    @DisplayName("7. Existing operating time fields remain independent of V2 operatingHours")
    void testOperatingFieldsIndependence() {
        Space space = new Space();
        space.setOpeningTime("09:00");
        space.setClosingTime("18:00");
        space.setOperatingDays(List.of("MONDAY"));

        assertTrue(space.getOperatingHours().isEmpty());

        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("TUESDAY", new OperatingDayConfig("10:00", "20:00", true));
        space.setOperatingHours(hours);

        assertEquals("09:00", space.getOpeningTime());
        assertEquals(1, space.getOperatingDays().size());
        assertEquals("MONDAY", space.getOperatingDays().get(0));
        assertNotNull(space.getOperatingHours().get("TUESDAY"));
    }

    @Test
    @DisplayName("8. V1 bookingConfig defaults to null")
    void testV1BookingConfigDefaultsNull() {
        Space space = new Space();
        assertNull(space.getBookingConfig());
    }

    @Test
    @DisplayName("9. Existing Space alias methods preserve legacy UI compatibility")
    void testLegacyAliasMethods() {
        Space space = new Space();
        space.setCity("Delhi");
        assertEquals("Delhi", space.getLocation());
        assertEquals("2.0 km", space.getDistance());
        assertEquals("hour", space.getPricingUnit());
        assertEquals(0.0, space.getRating());
    }

    // --- Category 2: V2 Defaults Tests ---

    @Test
    @DisplayName("10. Explicit V2 factory sets schema version 2")
    void testV2FactorySchemaVersion() {
        Space space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "DANCE_PRACTICE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");

        assertEquals(2, space.getSchemaVersion());
    }

    @Test
    @DisplayName("11. V2 factory approval status defaults to DRAFT")
    void testV2FactoryApprovalStatusDefault() {
        Space space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "DANCE_PRACTICE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");

        assertEquals(SpaceApprovalStatus.DRAFT, space.getApprovalStatus());
    }

    @Test
    @DisplayName("12. V2 factory operational status defaults to INACTIVE")
    void testV2FactoryOperationalStatusDefault() {
        Space space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "DANCE_PRACTICE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");

        assertEquals(SpaceOperationalStatus.INACTIVE, space.getOperationalStatus());
    }

    @Test
    @DisplayName("13. V2 factory active state defaults to true")
    void testV2FactoryActiveDefault() {
        Space space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "DANCE_PRACTICE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");

        assertTrue(space.isActive());
    }

    @Test
    @DisplayName("14. Review/submission/publication timestamps use safe initial values (0L)")
    void testV2TimestampDefaults() {
        Space space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "DANCE_PRACTICE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");

        assertEquals(0L, space.getSubmittedAt());
        assertEquals(0L, space.getReviewedAt());
        assertEquals(0L, space.getApprovedAt());
        assertEquals(0L, space.getPublishedAt());
        assertEquals("", space.getRejectionReason());
    }

    @Test
    @DisplayName("15. New Strings use safe non-null default values")
    void testV2StringDefaults() {
        Space space = new Space();
        assertEquals("", space.getCoverPhotoUrl());
        assertEquals("", space.getBookingType());
        assertEquals("", space.getCancellationPolicy());
        assertEquals("", space.getApprovalStatus());
        assertEquals("", space.getOperationalStatus());
        assertEquals("", space.getActivityType());
    }

    @Test
    @DisplayName("16. Collections are non-null and initially empty")
    void testV2CollectionDefaults() {
        Space space = new Space();
        assertNotNull(space.getPhotoUrls());
        assertTrue(space.getPhotoUrls().isEmpty());
        assertNotNull(space.getFacilities());
        assertTrue(space.getFacilities().isEmpty());
        assertNotNull(space.getOperatingHours());
        assertTrue(space.getOperatingHours().isEmpty());
    }

    @Test
    @DisplayName("17. Separate Space instances do not share mutable default collections or location objects")
    void testNoSharedMutableDefaults() {
        Space s1 = new Space();
        Space s2 = new Space();

        s1.setPhotoUrls(List.of("http://photo1.jpg"));
        s1.getSpaceLocation().setAddressLine("Address 1");

        assertTrue(s2.getPhotoUrls().isEmpty());
        assertEquals("", s2.getSpaceLocation().getAddressLine());
    }

    // --- Category 3: Typed Configuration Contract Tests ---

    @Test
    @DisplayName("18. Each config implements BookingConfig interface")
    void testConfigClassesImplementBookingConfig() {
        assertTrue(BookingConfig.class.isAssignableFrom(SeatBookingConfig.class));
        assertTrue(BookingConfig.class.isAssignableFrom(SlotBookingConfig.class));
        assertTrue(BookingConfig.class.isAssignableFrom(WorkshopBookingConfig.class));
        assertTrue(BookingConfig.class.isAssignableFrom(RentalBookingConfig.class));
        assertTrue(BookingConfig.class.isAssignableFrom(VenueBookingConfig.class));
        assertTrue(BookingConfig.class.isAssignableFrom(PracticeRoomBookingConfig.class));
    }

    @Test
    @DisplayName("19. Each config returns its correct controlled booking type constant")
    void testConfigBookingTypeMapping() {
        assertEquals(BookingType.SEAT, new SeatBookingConfig().getBookingType());
        assertEquals(BookingType.SLOT, new SlotBookingConfig().getBookingType());
        assertEquals(BookingType.WORKSHOP, new WorkshopBookingConfig().getBookingType());
        assertEquals(BookingType.RENTAL, new RentalBookingConfig().getBookingType());
        assertEquals(BookingType.VENUE, new VenueBookingConfig().getBookingType());
        assertEquals(BookingType.PRACTICE_ROOM, new PracticeRoomBookingConfig().getBookingType());
    }

    @Test
    @DisplayName("20. Matching booking type and config succeeds in helper")
    void testMatchingBookingConfig() {
        Space space = new Space();
        space.setBookingType(BookingType.SEAT);
        space.setBookingConfig(new SeatBookingConfig());

        assertTrue(space.hasMatchingBookingConfig());
        assertEquals(BookingType.SEAT, space.getBookingConfigType());
    }

    @Test
    @DisplayName("21. Mismatched booking type and config is detected")
    void testMismatchedBookingConfig() {
        Space space = new Space();
        space.setBookingType(BookingType.SEAT);
        space.setBookingConfig(new SlotBookingConfig());

        assertFalse(space.hasMatchingBookingConfig());
        assertEquals(BookingType.SLOT, space.getBookingConfigType());
    }

    @Test
    @DisplayName("22. Null bookingConfig is handled safely by helpers")
    void testNullBookingConfigHandling() {
        Space space = new Space();
        space.setBookingType(BookingType.SEAT);
        space.setBookingConfig(null);

        assertFalse(space.hasMatchingBookingConfig());
        assertEquals("", space.getBookingConfigType());
    }

    @Test
    @DisplayName("23. Legacy RESOURCE booking type is rejected as a valid V2 match")
    void testResourceBookingTypeRejected() {
        Space space = new Space();
        space.setBookingType(BookingType.RESOURCE);
        space.setBookingConfig(new BookingConfig() {
            @Override
            public String getBookingType() {
                return BookingType.RESOURCE;
            }

            @Override
            public BookingConfig copy() {
                return this;
            }
        });

        assertFalse(space.hasMatchingBookingConfig());
    }

    @Test
    @DisplayName("24. Setting bookingType does not silently delete or overwrite bookingConfig")
    void testSetBookingTypeDoesNotDeleteConfig() {
        Space space = new Space();
        SeatBookingConfig config = new SeatBookingConfig();
        space.setBookingConfig(config);
        space.setBookingType(BookingType.SLOT);

        assertNotNull(space.getBookingConfig());
        assertEquals(config, space.getBookingConfig());
    }

    @Test
    @DisplayName("25. Setting bookingConfig does not silently rewrite bookingType")
    void testSetBookingConfigDoesNotRewriteType() {
        Space space = new Space();
        space.setBookingType(BookingType.SEAT);
        space.setBookingConfig(new SlotBookingConfig());

        assertEquals(BookingType.SEAT, space.getBookingType());
        assertEquals(BookingType.SLOT, space.getBookingConfigType());
    }

    // --- Category 4: Defensive Copying Tests ---

    @Test
    @DisplayName("26. photoUrls cannot be mutated through original input list")
    void testPhotoUrlsDefensiveCopyOnInput() {
        Space space = new Space();
        List<String> input = new ArrayList<>();
        input.add("url1");
        space.setPhotoUrls(input);

        input.add("url2");
        assertEquals(1, space.getPhotoUrls().size());
        assertEquals("url1", space.getPhotoUrls().get(0));
    }

    @Test
    @DisplayName("27. facilities cannot be mutated through original input list")
    void testFacilitiesDefensiveCopyOnInput() {
        Space space = new Space();
        List<String> input = new ArrayList<>();
        input.add("Wifi");
        space.setFacilities(input);

        input.add("Parking");
        assertEquals(1, space.getFacilities().size());
        assertEquals("Wifi", space.getFacilities().get(0));
    }

    @Test
    @DisplayName("28. operatingHours cannot be mutated through original input map")
    void testOperatingHoursDefensiveCopyOnInput() {
        Space space = new Space();
        Map<String, OperatingDayConfig> map = new HashMap<>();
        map.put("MONDAY", new OperatingDayConfig("09:00", "17:00", true));
        space.setOperatingHours(map);

        map.put("TUESDAY", new OperatingDayConfig("09:00", "17:00", true));
        assertEquals(1, space.getOperatingHours().size());
    }

    @Test
    @DisplayName("29. Getter-returned collections throw UnsupportedOperationException on direct mutation")
    void testGetterCollectionsUnmodifiable() {
        Space space = new Space();
        space.setPhotoUrls(List.of("url1"));
        space.setFacilities(List.of("Wifi"));

        assertThrows(UnsupportedOperationException.class, () -> space.getPhotoUrls().add("url2"));
        assertThrows(UnsupportedOperationException.class, () -> space.getFacilities().add("Parking"));
        assertThrows(UnsupportedOperationException.class, () -> space.getOperatingHours().put("MON", new OperatingDayConfig()));
    }

    @Test
    @DisplayName("30. SpaceLocation state is not shared across instances through getter/setter")
    void testSpaceLocationDefensiveCopy() {
        Space space = new Space();
        SpaceLocation loc = new SpaceLocation("Line 1", "Area 1", "City 1", "State 1", "100001", "Landmark 1", 10.0, 20.0);
        space.setSpaceLocation(loc);

        SpaceLocation retrieved = space.getSpaceLocation();
        retrieved.setAddressLine("Modified Line");

        assertEquals("Line 1", space.getSpaceLocation().getAddressLine());
    }

    // --- Category 5: Contracts Tests ---

    @Test
    @DisplayName("31. basePricePaise field is declared as primitive long")
    void testBasePricePaiseDeclaredLong() throws NoSuchFieldException {
        Field field = Space.class.getDeclaredField("basePricePaise");
        assertEquals(long.class, field.getType());
    }

    @Test
    @DisplayName("32. No floating point price field exists for V2 base price")
    void testNoFloatingPointBasePrice() {
        assertThrows(NoSuchFieldException.class, () -> Space.class.getDeclaredField("basePriceRupees"));
        assertThrows(NoSuchFieldException.class, () -> Space.class.getDeclaredField("priceDouble"));
    }

    @Test
    @DisplayName("33. OperatingDayConfig preserves HH:mm time format")
    void testOperatingDayConfigFormat() {
        OperatingDayConfig config = new OperatingDayConfig("09:30", "18:45", true);
        assertEquals("09:30", config.getOpeningTime());
        assertEquals("18:45", config.getClosingTime());
    }

    @Test
    @DisplayName("34. Operating hours map normalizes weekday keys consistently")
    void testOperatingHoursWeekdayNormalization() {
        Space space = new Space();
        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("mon", new OperatingDayConfig("08:00", "16:00", true));
        hours.put("TUESDAY", new OperatingDayConfig("09:00", "17:00", true));
        space.setOperatingHours(hours);

        assertTrue(space.getOperatingHours().containsKey("MONDAY"));
        assertTrue(space.getOperatingHours().containsKey("TUESDAY"));
    }

    @Test
    @DisplayName("35. No V2 field causes a legacy constructor to set schema version to 2")
    void testLegacyConstructorsNeverSetV2SchemaVersion() {
        Space s1 = new Space();
        assertEquals(1, s1.getSchemaVersion());

        Space s2 = new Space("sp1", "b1", "o1", "Name", "Desc", "Cat", "Group", "Type",
                "Line", "Mark", "City", "State", "000000", 10, List.of(), List.of(), List.of(),
                "09:00", "17:00", List.of("MONDAY"), "Asia/Kolkata", SpaceStatus.DRAFT, true, 0L, 0L);
        assertEquals(1, s2.getSchemaVersion());
    }

    // --- Phase 3A Specific Regression Tests ---

    @Test
    @DisplayName("36. Canonical activityType field exists and replaces v2ActivityType")
    void testCanonicalActivityTypeField() throws NoSuchFieldException {
        Field field = Space.class.getDeclaredField("activityType");
        assertEquals(String.class, field.getType());
        assertThrows(NoSuchFieldException.class, () -> Space.class.getDeclaredField("v2ActivityType"));
    }

    @Test
    @DisplayName("37. getActivityType and setActivityType work for both V1 and V2 objects")
    void testActivityTypeAccessors() {
        Space v1Space = new Space();
        v1Space.setActivityGroup("CreativityGroup");
        assertEquals("CreativityGroup", v1Space.getActivityType());

        Space v2Space = Space.createV2("s1", "b1", "o1", "Space V2", "Desc", "DANCE", "Creativity", "SALSADANCE",
                new SpaceLocation(), "cover.jpg", List.of(), List.of(), List.of(), 20, BookingType.PRACTICE_ROOM,
                50000L, "hour", new PracticeRoomBookingConfig(), Map.of(), List.of(), "Strict");
        assertEquals("SALSADANCE", v2Space.getActivityType());
    }

    @Test
    @DisplayName("38. Input SpaceLocation mutation does not affect Space internal state")
    void testInputSpaceLocationMutationIsolation() {
        Space space = new Space();
        SpaceLocation inputLoc = new SpaceLocation("Original Address", "Area", "City", "State", "111111", "Landmark", 1.0, 2.0);
        space.setSpaceLocation(inputLoc);

        inputLoc.setAddressLine("Mutated Address");
        assertEquals("Original Address", space.getSpaceLocation().getAddressLine());
    }

    @Test
    @DisplayName("39. Getter-returned SpaceLocation mutation does not affect Space internal state")
    void testGetterSpaceLocationMutationIsolation() {
        Space space = new Space();
        SpaceLocation loc = new SpaceLocation("Original Address", "Area", "City", "State", "111111", "Landmark", 1.0, 2.0);
        space.setSpaceLocation(loc);

        SpaceLocation fetchedLoc = space.getSpaceLocation();
        fetchedLoc.setAddressLine("Mutated Address");

        assertEquals("Original Address", space.getSpaceLocation().getAddressLine());
    }

    @Test
    @DisplayName("40. Input OperatingDayConfig mutation does not affect Space operatingHours map")
    void testInputOperatingDayConfigMutationIsolation() {
        Space space = new Space();
        OperatingDayConfig config = new OperatingDayConfig("08:00", "20:00", true);
        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("MONDAY", config);
        space.setOperatingHours(hours);

        config.setOpeningTime("12:00");
        assertEquals("08:00", space.getOperatingHours().get("MONDAY").getOpeningTime());
    }

    @Test
    @DisplayName("41. Getter-returned OperatingDayConfig value mutation does not affect Space internal state")
    void testGetterOperatingDayConfigValueMutationIsolation() {
        Space space = new Space();
        Map<String, OperatingDayConfig> hours = new HashMap<>();
        hours.put("MONDAY", new OperatingDayConfig("08:00", "20:00", true));
        space.setOperatingHours(hours);

        OperatingDayConfig fetchedConfig = space.getOperatingHours().get("MONDAY");
        fetchedConfig.setOpeningTime("12:00");

        assertEquals("08:00", space.getOperatingHours().get("MONDAY").getOpeningTime());
    }

    @Test
    @DisplayName("42. Input BookingConfig mutation does not affect Space internal state")
    void testInputBookingConfigMutationIsolation() {
        Space space = new Space();
        WorkshopBookingConfig config = new WorkshopBookingConfig(1000L, 1, 10, 24, 48, "NONE", new ArrayList<>(List.of("Canvas")), "Instructor");
        space.setBookingConfig(config);

        config.setInstructorName("Mutated Instructor");
        assertEquals("Instructor", ((WorkshopBookingConfig) space.getBookingConfig()).getInstructorName());
    }

    @Test
    @DisplayName("43. Getter-returned BookingConfig mutation does not affect Space internal state")
    void testGetterBookingConfigMutationIsolation() {
        Space space = new Space();
        WorkshopBookingConfig config = new WorkshopBookingConfig(1000L, 1, 10, 24, 48, "NONE", new ArrayList<>(List.of("Canvas")), "Instructor");
        space.setBookingConfig(config);

        WorkshopBookingConfig fetchedConfig = (WorkshopBookingConfig) space.getBookingConfig();
        fetchedConfig.setInstructorName("Mutated Instructor");

        assertEquals("Instructor", ((WorkshopBookingConfig) space.getBookingConfig()).getInstructorName());
    }

    @Test
    @DisplayName("44. Every BookingConfig copy implementation deep copies scalar and list fields")
    void testBookingConfigCopyImplementations() {
        SeatBookingConfig seat = new SeatBookingConfig();
        assertEquals(seat, seat.copy());

        SlotBookingConfig slot = new SlotBookingConfig();
        assertEquals(slot, slot.copy());

        WorkshopBookingConfig workshop = new WorkshopBookingConfig(5000L, 2, 20, 12, 24, "18+", List.of("Paint", "Brush"), "Bob");
        WorkshopBookingConfig workshopCopy = (WorkshopBookingConfig) workshop.copy();
        assertEquals(workshop, workshopCopy);
        assertNotSame(workshop.getMaterialsIncluded(), workshopCopy.getMaterialsIncluded());

        RentalBookingConfig rental = new RentalBookingConfig(5, 60, 1440, 1000L, true, false, 5.0, 500L, "Pickup", true, 200L, List.of("Helmet"));
        RentalBookingConfig rentalCopy = (RentalBookingConfig) rental.copy();
        assertEquals(rental, rentalCopy);
        assertNotSame(rental.getAddOns(), rentalCopy.getAddOns());

        VenueBookingConfig venue = new VenueBookingConfig(4, 24, true, 2, List.of("Party"), List.of("Tables"), 2000L, true, true, true, true, true, true, 1000L);
        VenueBookingConfig venueCopy = (VenueBookingConfig) venue.copy();
        assertEquals(venue, venueCopy);
        assertNotSame(venue.getAllowedEventTypes(), venueCopy.getAllowedEventTypes());

        PracticeRoomBookingConfig practice = new PracticeRoomBookingConfig(30, 180, 30, true, List.of("Amp"), List.of("Mic"));
        PracticeRoomBookingConfig practiceCopy = (PracticeRoomBookingConfig) practice.copy();
        assertEquals(practice, practiceCopy);
        assertNotSame(practice.getIncludedEquipment(), practiceCopy.getIncludedEquipment());
    }
}
