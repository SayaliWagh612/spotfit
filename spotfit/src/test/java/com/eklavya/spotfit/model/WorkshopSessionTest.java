package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkshopSessionTest {

    @Test
    @DisplayName("WorkshopSession model constructor and field getters/setters work correctly")
    void testWorkshopSessionModel() {
        long start = System.currentTimeMillis() + 3600000;
        long end = start + 7200000;
        WorkshopSession session = new WorkshopSession("sess123", "space123", start, end, 20, 0, 15000L, WorkshopSessionStatus.SCHEDULED, true, 1000L, 2000L);

        assertEquals("sess123", session.getSessionId());
        assertEquals("space123", session.getSpaceId());
        assertEquals(start, session.getStartAt());
        assertEquals(end, session.getEndAt());
        assertEquals(20, session.getCapacity());
        assertEquals(0, session.getBookedCount());
        assertEquals(15000L, session.getPriceOverridePaise());
        assertEquals(WorkshopSessionStatus.SCHEDULED, session.getStatus());
        assertTrue(session.isActive());
        assertEquals(1000L, session.getCreatedAt());
        assertEquals(2000L, session.getUpdatedAt());
    }

    @Test
    @DisplayName("WorkshopSessionStatus validation works for valid and invalid values")
    void testWorkshopSessionStatusValidation() {
        assertTrue(WorkshopSessionStatus.isValid("SCHEDULED"));
        assertTrue(WorkshopSessionStatus.isValid("CANCELLED"));
        assertTrue(WorkshopSessionStatus.isValid("COMPLETED"));
        assertFalse(WorkshopSessionStatus.isValid("UNKNOWN"));
        assertFalse(WorkshopSessionStatus.isValid(null));
    }
}
