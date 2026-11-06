package com.eklavya.spotfit.constants;

/**
 * Controlled pricing unit constants for SpotFit V2 Spaces.
 */
public final class PricingUnit {

    public static final String HOUR = "HOUR";
    public static final String DAY = "DAY";
    public static final String PERSON = "PERSON";
    public static final String SESSION = "SESSION";
    public static final String EVENT = "EVENT";
    public static final String SEAT = "SEAT";
    public static final String FLAT = "FLAT";

    private PricingUnit() {
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String trimmed = value.trim().toUpperCase();
        return HOUR.equals(trimmed)
                || DAY.equals(trimmed)
                || PERSON.equals(trimmed)
                || SESSION.equals(trimmed)
                || EVENT.equals(trimmed)
                || SEAT.equals(trimmed)
                || FLAT.equals(trimmed);
    }
}
