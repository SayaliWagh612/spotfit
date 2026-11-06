package com.eklavya.spotfit.constants;

/**
 * Controlled operational status constants for SpotFit V2 Spaces.
 */
public final class SpaceOperationalStatus {

    public static final String INACTIVE = "INACTIVE";
    public static final String ACTIVE = "ACTIVE";
    public static final String MAINTENANCE = "MAINTENANCE";
    public static final String CLOSED = "CLOSED";

    private SpaceOperationalStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }

        String trimmed = status.trim().toUpperCase();
        return INACTIVE.equals(trimmed)
                || ACTIVE.equals(trimmed)
                || MAINTENANCE.equals(trimmed)
                || CLOSED.equals(trimmed);
    }
}
