package com.eklavya.spotfit.constants;

/**
 * Controlled seat price mode constants for SEAT booking type configuration.
 */
public final class SeatPriceMode {

    public static final String UNIFORM = "UNIFORM";
    public static final String TIERED = "TIERED";
    public static final String INDIVIDUAL = "INDIVIDUAL";

    private SeatPriceMode() {
    }

    public static boolean isValid(String mode) {
        if (mode == null || mode.isBlank()) {
            return false;
        }

        String trimmed = mode.trim().toUpperCase();
        return UNIFORM.equals(trimmed)
                || TIERED.equals(trimmed)
                || INDIVIDUAL.equals(trimmed);
    }
}
