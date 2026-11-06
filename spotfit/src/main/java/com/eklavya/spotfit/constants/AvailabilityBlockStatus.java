package com.eklavya.spotfit.constants;

/**
 * Controlled status constants for Availability Blocks in SpotFit.
 */
public final class AvailabilityBlockStatus {

    public static final String ACTIVE = "ACTIVE";
    public static final String CANCELLED = "CANCELLED";

    private AvailabilityBlockStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        String upper = status.trim().toUpperCase();
        return ACTIVE.equals(upper) || CANCELLED.equals(upper);
    }
}
