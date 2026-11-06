package com.eklavya.spotfit.constants;

/**
 * Controlled offering lifecycle status constants for SpotFit.
 */
public final class OfferingStatus {

    public static final String DRAFT = "DRAFT";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    private OfferingStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return DRAFT.equals(status)
                || ACTIVE.equals(status)
                || INACTIVE.equals(status);
    }
}
