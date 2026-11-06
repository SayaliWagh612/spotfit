package com.eklavya.spotfit.constants;

/**
 * Controlled physical resource type constants for SpotFit spaces.
 */
public final class ResourceType {

    public static final String ROOM = "ROOM";
    public static final String SEAT = "SEAT";
    public static final String EQUIPMENT = "EQUIPMENT";
    public static final String VENUE_AREA = "VENUE_AREA";
    public static final String WORKSHOP_AREA = "WORKSHOP_AREA";

    private ResourceType() {
    }

    public static boolean isValid(String type) {
        if (type == null) {
            return false;
        }

        return ROOM.equals(type)
                || SEAT.equals(type)
                || EQUIPMENT.equals(type)
                || VENUE_AREA.equals(type)
                || WORKSHOP_AREA.equals(type);
    }
}
