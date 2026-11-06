package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SeatPriceMode;
import com.eklavya.spotfit.constants.SeatSelectionMode;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.model.bookingconfig.PracticeRoomBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.RentalBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.VenueBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerV2Phase1Test {

    @Test
    @DisplayName("BookingType - Validates V2 types and retains RESOURCE legacy compatibility")
    public void testBookingTypeValidation() {
        assertTrue(BookingType.isValid(BookingType.SEAT));
        assertTrue(BookingType.isValid(BookingType.SLOT));
        assertTrue(BookingType.isValid(BookingType.WORKSHOP));
        assertTrue(BookingType.isValid(BookingType.RENTAL));
        assertTrue(BookingType.isValid(BookingType.VENUE));
        assertTrue(BookingType.isValid(BookingType.PRACTICE_ROOM));
        assertTrue(BookingType.isValid(BookingType.RESOURCE), "Legacy RESOURCE must remain valid");

        assertFalse(BookingType.isValid(null));
        assertFalse(BookingType.isValid(""));
        assertFalse(BookingType.isValid("INVALID_TYPE"));
    }

    @Test
    @DisplayName("PricingUnit - Validates valid, invalid, null, and blank inputs")
    public void testPricingUnitValidation() {
        assertTrue(PricingUnit.isValid("HOUR"));
        assertTrue(PricingUnit.isValid("day"));
        assertTrue(PricingUnit.isValid("PERSON"));
        assertTrue(PricingUnit.isValid("session"));
        assertTrue(PricingUnit.isValid("EVENT"));
        assertTrue(PricingUnit.isValid("seat"));
        assertTrue(PricingUnit.isValid("FLAT"));

        assertFalse(PricingUnit.isValid(null));
        assertFalse(PricingUnit.isValid("  "));
        assertFalse(PricingUnit.isValid("BITCOIN"));
    }

    @Test
    @DisplayName("Status Constants - Validates SpaceApprovalStatus, SpaceOperationalStatus, and Seat modes")
    public void testStatusAndModeValidation() {
        assertTrue(SpaceApprovalStatus.isValid("DRAFT"));
        assertTrue(SpaceApprovalStatus.isValid("submitted"));
        assertTrue(SpaceApprovalStatus.isValid("APPROVED"));
        assertFalse(SpaceApprovalStatus.isValid("UNKNOWN"));

        assertTrue(SpaceOperationalStatus.isValid("INACTIVE"));
        assertTrue(SpaceOperationalStatus.isValid("active"));
        assertTrue(SpaceOperationalStatus.isValid("MAINTENANCE"));
        assertFalse(SpaceOperationalStatus.isValid("OFFLINE"));

        assertTrue(SeatSelectionMode.isValid("VISUAL_MAP"));
        assertTrue(SeatSelectionMode.isValid("auto_assign"));
        assertFalse(SeatSelectionMode.isValid("RANDOM"));

        assertTrue(SeatPriceMode.isValid("UNIFORM"));
        assertTrue(SeatPriceMode.isValid("tiered"));
        assertFalse(SeatPriceMode.isValid("NEGOTIABLE"));
    }

    @Test
    @DisplayName("SpaceLocation - String fields default to empty string and no nulls")
    public void testSpaceLocationDefaults() {
        SpaceLocation location = new SpaceLocation();
        assertEquals("", location.getAddressLine());
        assertEquals("", location.getArea());
        assertEquals("", location.getCity());
        assertEquals("", location.getState());
        assertEquals("", location.getPostalCode());
        assertEquals("", location.getLandmark());
        assertEquals(0.0, location.getLatitude());
        assertEquals(0.0, location.getLongitude());

        location.setAddressLine(null);
        assertEquals("", location.getAddressLine());
    }

    @Test
    @DisplayName("OperatingDayConfig - Canonical HH:mm format contract")
    public void testOperatingDayConfig() {
        OperatingDayConfig config = new OperatingDayConfig();
        assertEquals("08:00", config.getOpeningTime());
        assertEquals("20:00", config.getClosingTime());
        assertTrue(config.isActive());

        config.setOpeningTime("09:30");
        config.setClosingTime("18:00");
        assertEquals("09:30", config.getOpeningTime());
        assertEquals("18:00", config.getClosingTime());
    }

    @Test
    @DisplayName("VenueBookingConfig - Defaults requiresApproval to true and handles list fields safely")
    public void testVenueBookingConfig() {
        VenueBookingConfig config = new VenueBookingConfig();
        assertTrue(config.isRequiresApproval(), "requiresApproval must default to true");
        assertNotNull(config.getAllowedEventTypes());
        assertTrue(config.getAllowedEventTypes().isEmpty());
        assertNotNull(config.getSetupOptions());
        assertTrue(config.getSetupOptions().isEmpty());

        List<String> events = new ArrayList<>();
        events.add("CONCERT");
        config.setAllowedEventTypes(events);

        assertEquals(1, config.getAllowedEventTypes().size());
        assertEquals("CONCERT", config.getAllowedEventTypes().get(0));

        // Test defensive immutability on returned list
        assertThrows(UnsupportedOperationException.class, () -> config.getAllowedEventTypes().add("PARTY"));
    }

    @Test
    @DisplayName("WorkshopBookingConfig & RentalBookingConfig - Verify long paise fields and non-null lists")
    public void testMonetaryAndListFields() {
        WorkshopBookingConfig workshop = new WorkshopBookingConfig();
        assertEquals(0L, workshop.getPricePerPersonPaise());
        assertNotNull(workshop.getMaterialsIncluded());

        RentalBookingConfig rental = new RentalBookingConfig();
        assertEquals(0L, rental.getSecurityDepositPaise());
        assertEquals(0L, rental.getDeliveryChargePaise());
        assertEquals(0L, rental.getLateFeePaise());
        assertNotNull(rental.getAddOns());

        SeatBookingConfig seat = new SeatBookingConfig();
        assertEquals(SeatSelectionMode.AUTO_ASSIGN, seat.getSelectionMode());
        assertEquals(SeatPriceMode.UNIFORM, seat.getSeatPriceMode());

        SlotBookingConfig slot = new SlotBookingConfig();
        assertEquals(60, slot.getMinimumDurationMinutes());

        PracticeRoomBookingConfig practice = new PracticeRoomBookingConfig();
        assertNotNull(practice.getIncludedEquipment());
        assertNotNull(practice.getOptionalEquipment());
    }
}
