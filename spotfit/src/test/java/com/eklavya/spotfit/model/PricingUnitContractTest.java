package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.service.SpaceService;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PricingUnitContractTest {

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @Test
    @DisplayName("Verify frozen PricingUnit constants contain 7 valid values and reject SLOT or NIGHT")
    void testPricingUnitValidity() {
        assertTrue(PricingUnit.isValid("HOUR"));
        assertTrue(PricingUnit.isValid("DAY"));
        assertTrue(PricingUnit.isValid("PERSON"));
        assertTrue(PricingUnit.isValid("SESSION"));
        assertTrue(PricingUnit.isValid("EVENT"));
        assertTrue(PricingUnit.isValid("SEAT"));
        assertTrue(PricingUnit.isValid("FLAT"));

        assertFalse(PricingUnit.isValid("SLOT"), "PricingUnit SLOT must be invalid");
        assertFalse(PricingUnit.isValid("NIGHT"), "PricingUnit NIGHT must be invalid");
        assertFalse(PricingUnit.isValid("INVALID"));
    }

    @Test
    @DisplayName("Verify exact allowed pricing units matrix in SpaceService")
    void testSpaceServicePricingUnitMatrix() {
        SpaceService spaceService = new SpaceService();

        // SEAT: SEAT, PERSON, HOUR, SESSION, FLAT
        Set<String> seatExpected = Set.of("SEAT", "PERSON", "HOUR", "SESSION", "FLAT");
        for (String unit : seatExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }

        // SLOT: HOUR, SESSION, FLAT
        Set<String> slotExpected = Set.of("HOUR", "SESSION", "FLAT");
        for (String unit : slotExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }

        // WORKSHOP: PERSON
        Set<String> workshopExpected = Set.of("PERSON");
        for (String unit : workshopExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }

        // RENTAL: HOUR, DAY, FLAT
        Set<String> rentalExpected = Set.of("HOUR", "DAY", "FLAT");
        for (String unit : rentalExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }

        // VENUE: HOUR, EVENT, SESSION, FLAT
        Set<String> venueExpected = Set.of("HOUR", "EVENT", "SESSION", "FLAT");
        for (String unit : venueExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }

        // PRACTICE_ROOM: HOUR, SESSION, FLAT
        Set<String> prExpected = Set.of("HOUR", "SESSION", "FLAT");
        for (String unit : prExpected) {
            assertTrue(PricingUnit.isValid(unit));
        }
    }

    @Test
    @DisplayName("Verify UI updatePricingUnitsForType mapping parity and Workshop locking in ListNewSpaceWizardPage")
    void testListNewSpaceWizardPagePricingUnitMapping() throws Exception {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final Throwable[] err = new Throwable[1];

        Platform.runLater(() -> {
            try {
                ComboBox<String> unitCombo = new ComboBox<>();
                Method updateMethod = com.eklavya.spotfit.view.owner.ListNewSpaceWizardPage.class
                        .getDeclaredMethod("updatePricingUnitsForType", String.class, ComboBox.class);
                updateMethod.setAccessible(true);
                com.eklavya.spotfit.view.owner.ListNewSpaceWizardPage page = new com.eklavya.spotfit.view.owner.ListNewSpaceWizardPage();

                // Test SEAT
                updateMethod.invoke(page, BookingType.SEAT, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("SEAT", "PERSON", "HOUR", "SESSION", "FLAT"), unitCombo.getItems());
                assertFalse(unitCombo.getItems().contains("SLOT"));
                assertFalse(unitCombo.getItems().contains("NIGHT"));

                // Test SLOT
                updateMethod.invoke(page, BookingType.SLOT, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "SESSION", "FLAT"), unitCombo.getItems());
                assertFalse(unitCombo.getItems().contains("SLOT"));
                assertFalse(unitCombo.getItems().contains("NIGHT"));

                // Test WORKSHOP (Must be locked to PERSON)
                updateMethod.invoke(page, BookingType.WORKSHOP, unitCombo);
                assertTrue(unitCombo.isDisabled(), "Workshop pricing unit combo must be locked/disabled");
                assertEquals(List.of("PERSON"), unitCombo.getItems());
                assertEquals("PERSON", unitCombo.getValue());

                // Test RENTAL
                updateMethod.invoke(page, BookingType.RENTAL, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "DAY", "FLAT"), unitCombo.getItems());

                // Test VENUE
                updateMethod.invoke(page, BookingType.VENUE, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "EVENT", "SESSION", "FLAT"), unitCombo.getItems());

                // Test PRACTICE_ROOM
                updateMethod.invoke(page, BookingType.PRACTICE_ROOM, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "SESSION", "FLAT"), unitCombo.getItems());
            } catch (Throwable t) {
                err[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        if (err[0] != null) throw new RuntimeException(err[0]);
    }

    @Test
    @DisplayName("Verify UI updatePricingUnitsForTypeV2 create/edit mapping parity in EditSpacePage")
    void testEditSpacePagePricingUnitMapping() throws Exception {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        final Throwable[] err = new Throwable[1];

        Platform.runLater(() -> {
            try {
                ComboBox<String> unitCombo = new ComboBox<>();
                Method updateMethod = com.eklavya.spotfit.view.owner.EditSpacePage.class
                        .getDeclaredMethod("updatePricingUnitsForTypeV2", String.class, ComboBox.class);
                updateMethod.setAccessible(true);
                com.eklavya.spotfit.view.owner.EditSpacePage page = new com.eklavya.spotfit.view.owner.EditSpacePage(new Space());

                // Test SEAT
                updateMethod.invoke(page, BookingType.SEAT, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("SEAT", "PERSON", "HOUR", "SESSION", "FLAT"), unitCombo.getItems());

                // Test SLOT
                updateMethod.invoke(page, BookingType.SLOT, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "SESSION", "FLAT"), unitCombo.getItems());

                // Test WORKSHOP (Must be locked to PERSON)
                updateMethod.invoke(page, BookingType.WORKSHOP, unitCombo);
                assertTrue(unitCombo.isDisabled(), "Workshop pricing unit combo must be locked/disabled");
                assertEquals(List.of("PERSON"), unitCombo.getItems());
                assertEquals("PERSON", unitCombo.getValue());

                // Test RENTAL
                updateMethod.invoke(page, BookingType.RENTAL, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "DAY", "FLAT"), unitCombo.getItems());

                // Test VENUE
                updateMethod.invoke(page, BookingType.VENUE, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "EVENT", "SESSION", "FLAT"), unitCombo.getItems());

                // Test PRACTICE_ROOM
                updateMethod.invoke(page, BookingType.PRACTICE_ROOM, unitCombo);
                assertFalse(unitCombo.isDisabled());
                assertEquals(List.of("HOUR", "SESSION", "FLAT"), unitCombo.getItems());
            } catch (Throwable t) {
                err[0] = t;
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        if (err[0] != null) throw new RuntimeException(err[0]);
    }
}
