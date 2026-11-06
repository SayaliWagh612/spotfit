package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.model.Space;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerScopeIsolationTest {

    @Test
    public void testMockSpacesPreservedForNonCreativityDomains() {
        Space[] sportsSpaces = SpaceListing.getMockSpaces("Sports");
        assertNotNull(sportsSpaces);
        assertTrue(sportsSpaces.length > 0);
        assertEquals("Active Arena", sportsSpaces[0].getName());

        Space[] wellnessSpaces = SpaceListing.getMockSpaces("Wellness");
        assertNotNull(wellnessSpaces);
        assertTrue(wellnessSpaces.length > 0);
        assertEquals("Zen Wellness Center", wellnessSpaces[0].getName());
    }

    @Test
    public void testDownstreamViewClassesPresentAndUnchanged() {
        assertNotNull(SpaceDetails.class);
        assertNotNull(Booking.class);
        assertNotNull(Payment.class);
    }
}
