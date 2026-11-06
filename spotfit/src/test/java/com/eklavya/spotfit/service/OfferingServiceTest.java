package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OfferingStatus;
import com.eklavya.spotfit.constants.PricingType;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferingServiceTest {

    private OfferingService offeringService;

    @BeforeEach
    public void setUp() {
        offeringService = new OfferingService();
    }

    @Test
    public void testPriceConversionValidRupees() {
        OperationResult<Long> res1 = OfferingService.parsePriceRupeesToPaise("1000");
        assertTrue(res1.isSuccess());
        assertEquals(100000L, res1.getData());

        OperationResult<Long> res2 = OfferingService.parsePriceRupeesToPaise("999.50");
        assertTrue(res2.isSuccess());
        assertEquals(99950L, res2.getData());

        OperationResult<Long> res3 = OfferingService.parsePriceRupeesToPaise("0.01");
        assertTrue(res3.isSuccess());
        assertEquals(1L, res3.getData());
    }

    @Test
    public void testPriceConversionInvalidDecimals() {
        OperationResult<Long> res = OfferingService.parsePriceRupeesToPaise("999.505");
        assertTrue(res.isFailure());
        assertEquals("PRICE_DECIMALS_INVALID", res.getErrorCode());
    }

    @Test
    public void testPriceConversionZeroOrNegative() {
        OperationResult<Long> res1 = OfferingService.parsePriceRupeesToPaise("0");
        assertTrue(res1.isFailure());

        OperationResult<Long> res2 = OfferingService.parsePriceRupeesToPaise("-100");
        assertTrue(res2.isFailure());
    }

    @Test
    public void testPriceConversionNonNumeric() {
        OperationResult<Long> res = OfferingService.parsePriceRupeesToPaise("abc");
        assertTrue(res.isFailure());
        assertEquals("PRICE_INVALID", res.getErrorCode());
    }

    @Test
    public void testValidSlotPerHourRoomCombination() {
        OperationResult<Void> result = offeringService.validateOfferingInputs(
                "Podcast Recording",
                "Hourly studio recording package",
                BookingType.SLOT,
                PricingType.PER_HOUR,
                ResourceType.ROOM,
                60, 60, 480, 30, 1, 1, 1
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testValidWorkshopPerPersonCombination() {
        OperationResult<Void> result = offeringService.validateOfferingInputs(
                "Vocal Masterclass",
                "Group vocal workshop",
                BookingType.WORKSHOP,
                PricingType.PER_PERSON,
                ResourceType.WORKSHOP_AREA,
                120, 60, 240, 60, 20, 5, 20
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testValidRentalPerDayEquipmentCombination() {
        OperationResult<Void> result = offeringService.validateOfferingInputs(
                "DSLR Camera Rental",
                "Daily video camera package",
                BookingType.RENTAL,
                PricingType.PER_DAY,
                ResourceType.EQUIPMENT,
                1440, 1440, 4320, 1440, 1, 1, 1
        );
        assertTrue(result.isSuccess());
    }

    @Test
    public void testInvalidCompatibilityMatrixCombination() {
        OperationResult<Void> result = offeringService.validateOfferingInputs(
                "Seat Pass",
                "Invalid combo",
                BookingType.SEAT,
                PricingType.PER_HOUR, // Incompatible pricing for SEAT
                ResourceType.ROOM,    // Incompatible resource for SEAT
                60, 60, 480, 30, 1, 1, 1
        );
        assertTrue(result.isFailure());
        assertEquals("COMPATIBILITY_INVALID", result.getErrorCode());
    }

    @Test
    public void testInvalidDurationOrder() {
        OperationResult<Void> result = offeringService.validateOfferingInputs(
                "Podcast Recording",
                "Invalid duration bounds",
                BookingType.SLOT,
                PricingType.PER_HOUR,
                ResourceType.ROOM,
                30, 60, 480, 30, 1, 1, 1 // Default (30) < Min (60)
        );
        assertTrue(result.isFailure());
        assertEquals("DURATION_ORDER_INVALID", result.getErrorCode());
    }

    @Test
    public void testOfferingDefaultStatus() {
        Offering offering = new Offering();
        assertEquals(OfferingStatus.DRAFT, offering.getStatus());
        assertTrue(offering.isActive());
        assertEquals("0.00", offering.getPriceRupeesString());
    }

    @Test
    public void testSpaceIdImmutability() {
        Offering offering = new Offering();
        offering.setSpaceId("space-A");
        offering.setOfferingName("Test Package");
        offering.setDescription("Test Desc");
        offering.setBookingType(BookingType.SLOT);
        offering.setPricingType(PricingType.PER_HOUR);
        offering.setPricePaise(100000L);
        offering.setRequiredResourceType(ResourceType.ROOM);

        OperationResult<Offering> updateRes = offeringService.updateOffering("space-B", offering, "mock-token").join();
        assertTrue(updateRes.isFailure());
        assertEquals("INVALID_ARGUMENT", updateRes.getErrorCode());
    }
}
