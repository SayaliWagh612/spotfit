package com.eklavya.spotfit.constants;

/**
 * Controlled resource availability status constants for SpotFit.
 */
public final class ResourceStatus {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String RESERVED = "RESERVED";
    public static final String OCCUPIED = "OCCUPIED";
    public static final String MAINTENANCE = "MAINTENANCE";
    public static final String BLOCKED = "BLOCKED";
    public static final String INACTIVE = "INACTIVE";

    private ResourceStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return AVAILABLE.equals(status)
                || RESERVED.equals(status)
                || OCCUPIED.equals(status)
                || MAINTENANCE.equals(status)
                || BLOCKED.equals(status)
                || INACTIVE.equals(status);
    }

    public static boolean isBookable(String status) {
        if (status == null) {
            return false;
        }

        return AVAILABLE.equals(status);
    }
}
