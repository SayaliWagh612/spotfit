package com.eklavya.spotfit.constants;

/**
 * Controlled seat selection mode constants for SEAT booking type configuration.
 */
public final class SeatSelectionMode {

    public static final String VISUAL_MAP = "VISUAL_MAP";
    public static final String TABLE_LIST = "TABLE_LIST";
    public static final String SEAT_LIST = "SEAT_LIST";
    public static final String AUTO_ASSIGN = "AUTO_ASSIGN";

    private SeatSelectionMode() {
    }

    public static boolean isValid(String mode) {
        if (mode == null || mode.isBlank()) {
            return false;
        }

        String trimmed = mode.trim().toUpperCase();
        return VISUAL_MAP.equals(trimmed)
                || TABLE_LIST.equals(trimmed)
                || SEAT_LIST.equals(trimmed)
                || AUTO_ASSIGN.equals(trimmed);
    }
}
