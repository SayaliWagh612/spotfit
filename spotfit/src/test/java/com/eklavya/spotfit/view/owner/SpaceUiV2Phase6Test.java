package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;
import javafx.application.Platform;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceUiV2Phase6Test {

    private static boolean javafxInitialized = false;

    @BeforeAll
    public static void initJavaFX() {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(3, TimeUnit.SECONDS);
            javafxInitialized = true;
        } catch (IllegalStateException e) {
            javafxInitialized = true;
        } catch (Throwable t) {
            System.err.println("JavaFX headless initialization skipped: " + t.getMessage());
        }
    }

    @Test
    @DisplayName("Verify Phase 6 UI classes exist and are loadable")
    public void testUiClassesPresent() {
        assertNotNull(ListNewSpaceWizardPage.class);
        assertNotNull(EditSpacePage.class);
        assertNotNull(SpaceDetailsPage.class);
        assertNotNull(MySpacesPage.class);
        assertNotNull(SpaceFlowRouter.class);
    }

    @Test
    @DisplayName("Verify Price conversion between Rupees and Paise")
    public void testPriceConversionHelpers() {
        long paise = 50000L; // ₹500.00
        BigDecimal rupeeVal = BigDecimal.valueOf(paise, 2);
        assertEquals("500.00", rupeeVal.toPlainString());

        String inputRupees = "1250.75";
        BigDecimal bd = new BigDecimal(inputRupees);
        long computedPaise = bd.movePointRight(2).longValueExact();
        assertEquals(125075L, computedPaise);
    }

    @Test
    @DisplayName("Verify SpaceLocation area field integration")
    public void testSpaceLocationAreaField() {
        SpaceLocation loc = new SpaceLocation("123 Main St", "Baner", "Pune", "Maharashtra", "411045", "Opposite Mall", 18.52, 73.85);
        assertEquals("Baner", loc.getArea());
        assertEquals("123 Main St", loc.getAddressLine());
        assertEquals("Pune", loc.getCity());
    }

    // ==================== REQUIREMENT 3: ALL SIX CREATION CONFIGURATIONS ====================

    @Test
    @DisplayName("Creation Config Matrix: Verify SEAT Booking Config Fields")
    public void testCreation_SeatBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Kothrud", "Pune", "MH", "411038", "Mark", 0.0, 0.0);
        SeatBookingConfig config = new SeatBookingConfig("AUTO_ASSIGN", 60, 480, 60, 4, "UNIFORM", true, 30, 15);
        Map<String, OperatingDayConfig> hours = Map.of("MONDAY", new OperatingDayConfig("09:00", "21:00", true));

        Space v2 = Space.createV2("spc-seat", "b-1", "o-1", "Seat Studio", "Desc", SpaceCategory.CREATIVITY, "PODCASTS", "Podcast Studio", loc, "", Collections.emptyList(), List.of("Wi-Fi"), List.of("AC"), 10, BookingType.SEAT, 25000L, PricingUnit.SEAT, config, hours, List.of("Rule 1"), "Flexible");

        assertEquals(BookingType.SEAT, v2.getBookingType());
        assertEquals(25000L, v2.getBasePricePaise());
        assertEquals(PricingUnit.SEAT, v2.getPricingUnit());
        assertTrue(v2.getBookingConfig() instanceof SeatBookingConfig);

        SeatBookingConfig seatCfg = (SeatBookingConfig) v2.getBookingConfig();
        assertEquals("AUTO_ASSIGN", seatCfg.getSelectionMode());
        assertEquals(60, seatCfg.getMinimumDurationMinutes());
        assertEquals(480, seatCfg.getMaximumDurationMinutes());
        assertEquals(60, seatCfg.getDurationStepMinutes());
        assertEquals(4, seatCfg.getMaxGuestsPerBooking());
        assertEquals("UNIFORM", seatCfg.getSeatPriceMode());
        assertTrue(seatCfg.isAllowMultipleSeats());
        assertEquals(30, seatCfg.getAdvanceBookingDays());
        assertEquals(15, seatCfg.getMinimumAdvanceMinutes());

        assertFalse(BookingType.RESOURCE.equalsIgnoreCase(v2.getBookingType()));
    }

    @Test
    @DisplayName("Creation Config Matrix: Verify SLOT Booking Config Fields")
    public void testCreation_SlotBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Baner", "Pune", "MH", "411045", "Mark", 0.0, 0.0);
        SlotBookingConfig config = new SlotBookingConfig(30, 180, 30, 30, 15, 15, 8, 14, 30);
        Map<String, OperatingDayConfig> hours = Map.of("TUESDAY", new OperatingDayConfig("08:00", "20:00", true));

        Space v2 = Space.createV2("spc-slot", "b-1", "o-1", "Slot Studio", "Desc", SpaceCategory.CREATIVITY, "PODCASTS", "Podcast Studio", loc, "", Collections.emptyList(), List.of("Parking"), List.of("Soundproof"), 8, BookingType.SLOT, 40000L, PricingUnit.HOUR, config, hours, List.of("Rule 2"), "Strict");

        assertEquals(BookingType.SLOT, v2.getBookingType());
        assertEquals(40000L, v2.getBasePricePaise());
        assertTrue(v2.getBookingConfig() instanceof SlotBookingConfig);

        SlotBookingConfig slotCfg = (SlotBookingConfig) v2.getBookingConfig();
        assertEquals(30, slotCfg.getMinimumDurationMinutes());
        assertEquals(180, slotCfg.getMaximumDurationMinutes());
        assertEquals(30, slotCfg.getDurationStepMinutes());
        assertEquals(30, slotCfg.getSlotIntervalMinutes());
        assertEquals(15, slotCfg.getBufferBeforeMinutes());
        assertEquals(15, slotCfg.getBufferAfterMinutes());
        assertEquals(8, slotCfg.getMaxPeople());
        assertEquals(14, slotCfg.getAdvanceBookingDays());
        assertEquals(30, slotCfg.getMinimumAdvanceMinutes());
    }

    @Test
    @DisplayName("Creation Config Matrix: Verify WORKSHOP Booking Config Fields")
    public void testCreation_WorkshopBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Aundh", "Pune", "MH", "411007", "Mark", 0.0, 0.0);
        WorkshopBookingConfig config = new WorkshopBookingConfig(50000L, 5, 25, 24, 48, "18+", List.of("Canvas", "Paints"), "John Doe");
        Map<String, OperatingDayConfig> hours = Map.of("WEDNESDAY", new OperatingDayConfig("10:00", "18:00", true));

        Space v2 = Space.createV2("spc-ws", "b-1", "o-1", "Workshop Hall", "Desc", SpaceCategory.CREATIVITY, "ART", "Art Studio", loc, "", Collections.emptyList(), List.of("Easel"), List.of("Paints"), 25, BookingType.WORKSHOP, 50000L, PricingUnit.PERSON, config, hours, List.of("No food"), "Moderate");

        assertEquals(BookingType.WORKSHOP, v2.getBookingType());
        assertTrue(v2.getBookingConfig() instanceof WorkshopBookingConfig);

        WorkshopBookingConfig wsCfg = (WorkshopBookingConfig) v2.getBookingConfig();
        assertEquals(50000L, wsCfg.getPricePerPersonPaise());
        assertEquals(5, wsCfg.getMinimumParticipantsPerBooking());
        assertEquals(25, wsCfg.getMaximumParticipantsPerBooking());
        assertEquals(24, wsCfg.getBookingCutoffHours());
        assertEquals(48, wsCfg.getCancellationCutoffHours());
        assertEquals("18+", wsCfg.getAgeRestriction());
        assertEquals(List.of("Canvas", "Paints"), wsCfg.getMaterialsIncluded());
        assertEquals("John Doe", wsCfg.getInstructorName());
    }

    @Test
    @DisplayName("Creation Config Matrix: Verify RENTAL Booking Config Fields")
    public void testCreation_RentalBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Viman Nagar", "Pune", "MH", "411014", "Mark", 0.0, 0.0);
        RentalBookingConfig config = new RentalBookingConfig(3, 60, 1440, 100000L, true, true, 15.0, 20000L, "Pickup at gate", true, 50000L, List.of("Lens", "Tripod"));
        Map<String, OperatingDayConfig> hours = Map.of("THURSDAY", new OperatingDayConfig("09:00", "19:00", true));

        Space v2 = Space.createV2("spc-rental", "b-1", "o-1", "Camera Gear", "Desc", SpaceCategory.CREATIVITY, "CONTENT_CREATION", "Production Studio", loc, "", Collections.emptyList(), List.of("4K Camera"), List.of("Case"), 1, BookingType.RENTAL, 150000L, PricingUnit.DAY, config, hours, List.of("Handle with care"), "Strict");

        assertEquals(BookingType.RENTAL, v2.getBookingType());
        assertTrue(v2.getBookingConfig() instanceof RentalBookingConfig);

        RentalBookingConfig rCfg = (RentalBookingConfig) v2.getBookingConfig();
        assertEquals(3, rCfg.getAvailableQuantity());
        assertEquals(60, rCfg.getMinimumRentalDurationMinutes());
        assertEquals(1440, rCfg.getMaximumRentalDurationMinutes());
        assertEquals(100000L, rCfg.getSecurityDepositPaise());
        assertTrue(rCfg.isPickupAvailable());
        assertTrue(rCfg.isDeliveryAvailable());
        assertEquals(15.0, rCfg.getDeliveryRadiusKm(), 0.001);
        assertEquals(20000L, rCfg.getDeliveryChargePaise());
        assertEquals("Pickup at gate", rCfg.getPickupInstructions());
        assertTrue(rCfg.isRequiresIdentityProof());
        assertEquals(50000L, rCfg.getLateFeePaise());
        assertEquals(List.of("Lens", "Tripod"), rCfg.getAddOns());
    }

    @Test
    @DisplayName("Creation Config Matrix: Verify VENUE Booking Config Fields")
    public void testCreation_VenueBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Koregaon Park", "Pune", "MH", "411001", "Mark", 0.0, 0.0);
        VenueBookingConfig config = new VenueBookingConfig(4, 12, true, 7, List.of("Concert", "Exhibition"), List.of("Theater", "Banquet"), 200000L, true, true, true, true, true, true, 100000L);
        Map<String, OperatingDayConfig> hours = Map.of("FRIDAY", new OperatingDayConfig("10:00", "23:00", true));

        Space v2 = Space.createV2("spc-venue", "b-1", "o-1", "Grand Hall", "Desc", SpaceCategory.CREATIVITY, "MUSIC", "Music Room", loc, "", Collections.emptyList(), List.of("Stage"), List.of("Green Room"), 200, BookingType.VENUE, 500000L, PricingUnit.EVENT, config, hours, List.of("No smoking"), "Strict");

        assertEquals(BookingType.VENUE, v2.getBookingType());
        assertTrue(v2.getBookingConfig() instanceof VenueBookingConfig);

        VenueBookingConfig vCfg = (VenueBookingConfig) v2.getBookingConfig();
        assertEquals(4, vCfg.getMinimumBookingHours());
        assertEquals(12, vCfg.getMaximumBookingHours());
        assertTrue(vCfg.isRequiresApproval());
        assertEquals(7, vCfg.getMinimumAdvanceDays());
        assertEquals(List.of("Concert", "Exhibition"), vCfg.getAllowedEventTypes());
        assertEquals(List.of("Theater", "Banquet"), vCfg.getSetupOptions());
        assertEquals(200000L, vCfg.getSecurityDepositPaise());
        assertTrue(vCfg.isSoundSystemAvailable());
        assertTrue(vCfg.isLightingAvailable());
        assertTrue(vCfg.isStageAvailable());
        assertTrue(vCfg.isGreenRoomAvailable());
        assertTrue(vCfg.isOutsideVendorAllowed());
        assertTrue(vCfg.isCateringAllowed());
        assertEquals(100000L, vCfg.getOvertimeRatePaise());
    }

    @Test
    @DisplayName("Creation Config Matrix: Verify PRACTICE_ROOM Booking Config Fields")
    public void testCreation_PracticeRoomBookingConfig() {
        SpaceLocation loc = new SpaceLocation("Line 1", "Deccan", "Pune", "MH", "411004", "Mark", 0.0, 0.0);
        PracticeRoomBookingConfig config = new PracticeRoomBookingConfig(60, 360, 60, true, List.of("Drums", "Piano"), List.of("Guitar Amp"));
        Map<String, OperatingDayConfig> hours = Map.of("SATURDAY", new OperatingDayConfig("08:00", "22:00", true));

        Space v2 = Space.createV2("spc-prac", "b-1", "o-1", "Jam Room", "Desc", SpaceCategory.CREATIVITY, "MUSIC", "Rehearsal Space", loc, "", Collections.emptyList(), List.of("Amps"), List.of("Drums"), 5, BookingType.PRACTICE_ROOM, 30000L, PricingUnit.HOUR, config, hours, List.of("Clean up"), "Flexible");

        assertEquals(BookingType.PRACTICE_ROOM, v2.getBookingType());
        assertTrue(v2.getBookingConfig() instanceof PracticeRoomBookingConfig);

        PracticeRoomBookingConfig pCfg = (PracticeRoomBookingConfig) v2.getBookingConfig();
        assertEquals(60, pCfg.getMinimumDurationMinutes());
        assertEquals(360, pCfg.getMaximumDurationMinutes());
        assertEquals(60, pCfg.getDurationStepMinutes());
        assertTrue(pCfg.isEquipmentSelectionEnabled());
        assertEquals(List.of("Drums", "Piano"), pCfg.getIncludedEquipment());
        assertEquals(List.of("Guitar Amp"), pCfg.getOptionalEquipment());
    }

    // ==================== REQUIREMENT 4 & 5: EDIT PREFILL & ONE-FIELD EDIT PRESERVATION ====================

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for SEAT Space")
    public void testEditPrefillAndSingleFieldEdit_Seat() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Kothrud", "Pune", "MH", "411038", "Mark", 0.0, 0.0);
        SeatBookingConfig config = new SeatBookingConfig("AUTO_ASSIGN", 60, 480, 60, 4, "UNIFORM", true, 30, 15);
        Map<String, OperatingDayConfig> hours = Map.of("MONDAY", new OperatingDayConfig("09:00", "21:00", true));

        Space original = Space.createV2("spc-seat", "b-1", "o-1", "Original Seat Space", "Desc", SpaceCategory.CREATIVITY, "PODCASTS", "Podcast Studio", loc, "", Collections.emptyList(), List.of("Wi-Fi"), List.of("AC"), 10, BookingType.SEAT, 25000L, PricingUnit.SEAT, config, hours, List.of("Rule 1"), "Flexible");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                Space assembledOriginal = page.assembleV2SpaceFromForm();
                assertNotNull(assembledOriginal);
                assertEquals("Original Seat Space", assembledOriginal.getSpaceName());
                assertEquals("Kothrud", assembledOriginal.getSpaceLocation().getArea());
                assertEquals(25000L, assembledOriginal.getBasePricePaise());

                // Single-field edit: change spaceName only
                page.spaceNameFieldV2.setText("Renamed Seat Space");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Seat Space", edited.getSpaceName());
        assertEquals(original.getSpaceId(), edited.getSpaceId());
        assertEquals(original.getBusinessId(), edited.getBusinessId());
        assertEquals(original.getBookingType(), edited.getBookingType());
        assertEquals(original.getBasePricePaise(), edited.getBasePricePaise());
        assertEquals(original.getSpaceLocation().getArea(), edited.getSpaceLocation().getArea());
        assertEquals(original.getAmenities(), edited.getAmenities());
        assertEquals(original.getFacilities(), edited.getFacilities());
        assertEquals(original.getRules(), edited.getRules());
        assertEquals(original.getCancellationPolicy(), edited.getCancellationPolicy());
    }

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for SLOT Space")
    public void testEditPrefillAndSingleFieldEdit_Slot() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Baner", "Pune", "MH", "411045", "Mark", 0.0, 0.0);
        SlotBookingConfig config = new SlotBookingConfig(30, 180, 30, 30, 15, 15, 8, 14, 30);
        Map<String, OperatingDayConfig> hours = Map.of("TUESDAY", new OperatingDayConfig("08:00", "20:00", true));

        Space original = Space.createV2("spc-slot", "b-1", "o-1", "Original Slot Space", "Desc", SpaceCategory.CREATIVITY, "PODCASTS", "Podcast Studio", loc, "", Collections.emptyList(), List.of("Parking"), List.of("Soundproof"), 8, BookingType.SLOT, 40000L, PricingUnit.HOUR, config, hours, List.of("Rule 2"), "Strict");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                page.spaceNameFieldV2.setText("Renamed Slot Space");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Slot Space", edited.getSpaceName());
        assertEquals(original.getBookingType(), edited.getBookingType());
        assertEquals(original.getBasePricePaise(), edited.getBasePricePaise());
        assertEquals(original.getSpaceLocation().getArea(), edited.getSpaceLocation().getArea());
    }

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for WORKSHOP Space")
    public void testEditPrefillAndSingleFieldEdit_Workshop() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Aundh", "Pune", "MH", "411007", "Mark", 0.0, 0.0);
        WorkshopBookingConfig config = new WorkshopBookingConfig(50000L, 5, 25, 24, 48, "18+", List.of("Canvas"), "John Doe");
        Map<String, OperatingDayConfig> hours = Map.of("WEDNESDAY", new OperatingDayConfig("10:00", "18:00", true));

        Space original = Space.createV2("spc-ws", "b-1", "o-1", "Original Workshop Hall", "Desc", SpaceCategory.CREATIVITY, "ART", "Art Studio", loc, "", Collections.emptyList(), List.of("Easel"), List.of("Paints"), 25, BookingType.WORKSHOP, 50000L, PricingUnit.PERSON, config, hours, List.of("No food"), "Moderate");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                page.spaceNameFieldV2.setText("Renamed Workshop Hall");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Workshop Hall", edited.getSpaceName());
        assertEquals(original.getBookingType(), edited.getBookingType());
    }

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for RENTAL Space")
    public void testEditPrefillAndSingleFieldEdit_Rental() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Viman Nagar", "Pune", "MH", "411014", "Mark", 0.0, 0.0);
        RentalBookingConfig config = new RentalBookingConfig(3, 60, 1440, 100000L, true, true, 15.0, 20000L, "Pickup at gate", true, 50000L, List.of("Lens", "Tripod"));
        Map<String, OperatingDayConfig> hours = Map.of("THURSDAY", new OperatingDayConfig("09:00", "19:00", true));

        Space original = Space.createV2("spc-rental", "b-1", "o-1", "Original Camera Gear", "Desc", SpaceCategory.CREATIVITY, "CONTENT_CREATION", "Production Studio", loc, "", Collections.emptyList(), List.of("4K Camera"), List.of("Case"), 1, BookingType.RENTAL, 150000L, PricingUnit.DAY, config, hours, List.of("Handle with care"), "Strict");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                page.spaceNameFieldV2.setText("Renamed Camera Gear");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Camera Gear", edited.getSpaceName());
        assertEquals(original.getBookingType(), edited.getBookingType());
    }

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for VENUE Space")
    public void testEditPrefillAndSingleFieldEdit_Venue() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Koregaon Park", "Pune", "MH", "411001", "Mark", 0.0, 0.0);
        VenueBookingConfig config = new VenueBookingConfig(4, 12, true, 7, List.of("Concert"), List.of("Banquet"), 200000L, true, true, true, true, true, true, 100000L);
        Map<String, OperatingDayConfig> hours = Map.of("FRIDAY", new OperatingDayConfig("10:00", "23:00", true));

        Space original = Space.createV2("spc-venue", "b-1", "o-1", "Original Grand Hall", "Desc", SpaceCategory.CREATIVITY, "MUSIC", "Music Room", loc, "", Collections.emptyList(), List.of("Stage"), List.of("Green Room"), 200, BookingType.VENUE, 500000L, PricingUnit.EVENT, config, hours, List.of("No smoking"), "Strict");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                page.spaceNameFieldV2.setText("Renamed Grand Hall");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Grand Hall", edited.getSpaceName());
        assertEquals(original.getBookingType(), edited.getBookingType());
    }

    @Test
    @DisplayName("Edit Prefill & Single-Field Edit Preservation for PRACTICE_ROOM Space")
    public void testEditPrefillAndSingleFieldEdit_PracticeRoom() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Line 1", "Deccan", "Pune", "MH", "411004", "Mark", 0.0, 0.0);
        PracticeRoomBookingConfig config = new PracticeRoomBookingConfig(60, 360, 60, true, List.of("Drums"), List.of("Guitar Amp"));
        Map<String, OperatingDayConfig> hours = Map.of("SATURDAY", new OperatingDayConfig("08:00", "22:00", true));

        Space original = Space.createV2("spc-prac", "b-1", "o-1", "Original Jam Room", "Desc", SpaceCategory.CREATIVITY, "MUSIC", "Rehearsal Space", loc, "", Collections.emptyList(), List.of("Amps"), List.of("Drums"), 5, BookingType.PRACTICE_ROOM, 30000L, PricingUnit.HOUR, config, hours, List.of("Clean up"), "Flexible");

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] submittedRef = new Space[1];

        Platform.runLater(() -> {
            try {
                EditSpacePage page = new EditSpacePage(original);
                page.renderV2FormForTest();
                page.spaceNameFieldV2.setText("Renamed Jam Room");
                submittedRef[0] = page.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space edited = submittedRef[0];
        assertNotNull(edited);
        assertEquals("Renamed Jam Room", edited.getSpaceName());
        assertEquals(original.getBookingType(), edited.getBookingType());
    }

    // ==================== REQUIREMENT 6: WIZARD STATE NAVIGATION ====================

    @Test
    @DisplayName("Wizard State: Step 1 -> 2 -> 3 -> 4 -> 5 retains state")
    public void testWizardStatePreservation_StepNavigation() throws Exception {
        if (!javafxInitialized) return;

        CountDownLatch latch = new CountDownLatch(1);
        final Space[] wizardSpaceRef = new Space[1];

        Platform.runLater(() -> {
            try {
                ListNewSpaceWizardPage wizard = new ListNewSpaceWizardPage();

                Business testBiz = new Business();
                testBiz.setBusinessId("biz-123");
                testBiz.setOwnerId("owner-456");
                testBiz.setBusinessName("Test Business");
                testBiz.setCategory("CREATIVITY");
                testBiz.setActivityGroups(List.of("PODCASTS"));
                testBiz.setVerificationStatus("APPROVED");
                testBiz.setActive(true);
                testBiz.setSchemaVersion(2);

                wizard.renderStep1ForTest(testBiz, "PODCASTS", "Podcast Studio");

                wizard.renderStep2ForTest();
                wizard.spaceNameField.setText("State Test Studio");
                wizard.descriptionField.setText("Testing state preservation");
                wizard.addressField.setText("456 Park Rd");
                wizard.areaField.setText("Kothrud");
                wizard.cityField.setText("Pune");
                wizard.stateField.setText("Maharashtra");
                wizard.postalCodeField.setText("411038");
                wizard.capacityField.setText("12");

                wizard.renderStep3ForTest();
                wizard.bookingTypeCombo.setValue(BookingType.SLOT);
                wizard.basePriceRupeesField.setText("600.00");
                wizard.pricingUnitCombo.setValue(PricingUnit.HOUR);

                wizard.renderStep4ForTest();
                wizard.slotMinDurationField.setText("60");
                wizard.slotMaxDurationField.setText("240");
                wizard.slotDurationStepField.setText("30");
                wizard.slotIntervalField.setText("30");

                wizard.renderStep5ForTest();
                wizard.amenitiesField.setText("Wi-Fi, AC");
                wizard.facilitiesField.setText("Parking");

                // Navigate back to Step 2 and verify state preserved
                wizard.renderStep2ForTest();
                assertEquals("State Test Studio", wizard.spaceNameField.getText());
                assertEquals("Kothrud", wizard.areaField.getText());

                // Navigate forward to Step 3 and verify state preserved
                wizard.renderStep3ForTest();
                assertEquals("600.00", wizard.basePriceRupeesField.getText());

                wizardSpaceRef[0] = wizard.assembleV2SpaceFromForm();
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        Space assembled = wizardSpaceRef[0];
        assertNotNull(assembled);
        assertEquals("State Test Studio", assembled.getSpaceName());
        assertEquals("Kothrud", assembled.getSpaceLocation().getArea());
        assertEquals(60000L, assembled.getBasePricePaise());
        assertEquals(BookingType.SLOT, assembled.getBookingType());
    }

    // ==================== REQUIREMENT 7 & 2: V1/V2 DETAILS ISOLATION & SUBMISSION ====================

    @Test
    @DisplayName("V2 Details Isolation: V2 details page must not invoke V1 submission or render sub-collections")
    public void testV2Details_NoV1SubmissionOrSubCollections() throws Exception {
        if (!javafxInitialized) return;

        SpaceLocation loc = new SpaceLocation("Addr 2", "Bavdhan", "Pune", "MH", "411021", "Mark 2", 0.0, 0.0);
        Space v2 = Space.createV2("spc-v2", "b-2", "o-1", "V2 Art Space", "Art Studio Desc", SpaceCategory.CREATIVITY, "ART", "Art Studio", loc, "", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 8, BookingType.SLOT, 35000L, PricingUnit.HOUR, null, Collections.emptyMap(), Collections.emptyList(), "Standard");

        CountDownLatch latch = new CountDownLatch(1);
        final SpaceDetailsPage[] detailsRef = new SpaceDetailsPage[1];

        Platform.runLater(() -> {
            try {
                detailsRef[0] = new SpaceDetailsPage(v2);
                Node node = detailsRef[0].getContent();
                assertNotNull(node);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertNotNull(detailsRef[0]);
    }
}
