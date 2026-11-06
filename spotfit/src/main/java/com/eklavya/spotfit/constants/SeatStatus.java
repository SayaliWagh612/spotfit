package com.eklavya.spotfit.constants;

/**
 * Constants for V2 Space Seat operational status.
 * String constants only, enums avoided for future-proofing and schema evolution.
 */
public final class SeatStatus {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String BLOCKED = "BLOCKED";
    public static final String MAINTENANCE = "MAINTENANCE";

    private SeatStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        String s = status.trim().toUpperCase();
        return AVAILABLE.equals(s) || BLOCKED.equals(s) || MAINTENANCE.equals(s);
    }
}
