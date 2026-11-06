package com.eklavya.spotfit.constants;

/**
 * Controlled booking type constants for SpotFit offerings.
 */
public final class BookingType {

    public static final String SEAT = "SEAT";
    public static final String SLOT = "SLOT";
    public static final String WORKSHOP = "WORKSHOP";
    public static final String RENTAL = "RENTAL";
    public static final String VENUE = "VENUE";
    public static final String PRACTICE_ROOM = "PRACTICE_ROOM";
    // Legacy support for V1 generic resource offerings
    public static final String RESOURCE = "RESOURCE";

    private BookingType() {
    }

    public static boolean isValid(String type) {
        if (type == null) {
            return false;
        }

        return SEAT.equals(type)
                || SLOT.equals(type)
                || WORKSHOP.equals(type)
                || RENTAL.equals(type)
                || VENUE.equals(type)
                || PRACTICE_ROOM.equals(type)
                || RESOURCE.equals(type);
    }
}
