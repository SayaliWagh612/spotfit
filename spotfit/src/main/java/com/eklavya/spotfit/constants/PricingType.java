package com.eklavya.spotfit.constants;

/**
 * Controlled pricing model constants for SpotFit offerings.
 */
public final class PricingType {

    public static final String PER_HOUR = "PER_HOUR";
    public static final String PER_PERSON = "PER_PERSON";
    public static final String PER_SESSION = "PER_SESSION";
    public static final String PER_DAY = "PER_DAY";
    public static final String PER_EVENT = "PER_EVENT";
    public static final String FIXED = "FIXED";
    public static final String FLAT = "FLAT";

    private PricingType() {
    }

    public static boolean isValid(String type) {
        if (type == null) {
            return false;
        }

        return PER_HOUR.equals(type)
                || PER_PERSON.equals(type)
                || PER_SESSION.equals(type)
                || PER_DAY.equals(type)
                || PER_EVENT.equals(type)
                || FIXED.equals(type)
                || FLAT.equals(type);
    }
}
