package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeatTest {

    @Test
    @DisplayName("Seat model constructor and field getters/setters work correctly")
    void testSeatModel() {
        Seat seat = new Seat("seat1", "space123", "VIP Seat 1", "A-1", "VIP", "A", 1, 1, 5000L, SeatStatus.AVAILABLE, true, 1000L, 2000L);

        assertEquals("seat1", seat.getSeatId());
        assertEquals("space123", seat.getSpaceId());
        assertEquals("VIP Seat 1", seat.getSeatName());
        assertEquals("A-1", seat.getSeatCode());
        assertEquals("VIP", seat.getSection());
        assertEquals("A", seat.getRowLabel());
        assertEquals(1, seat.getColumnNumber());
        assertEquals(1, seat.getCapacity());
        assertEquals(5000L, seat.getPriceOverridePaise());
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertTrue(seat.isActive());
        assertEquals(1000L, seat.getCreatedAt());
        assertEquals(2000L, seat.getUpdatedAt());
    }

    @Test
    @DisplayName("SeatStatus validation works for valid and invalid values")
    void testSeatStatusValidation() {
        assertTrue(SeatStatus.isValid("AVAILABLE"));
        assertTrue(SeatStatus.isValid("BLOCKED"));
        assertTrue(SeatStatus.isValid("MAINTENANCE"));
        assertFalse(SeatStatus.isValid("INVALID_STATUS"));
        assertFalse(SeatStatus.isValid(null));
    }
}
