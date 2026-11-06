package com.eklavya.spotfit.constants;

/**
 * Constants for V2 Space Workshop Session status.
 * String constants only, enums avoided for future-proofing and schema evolution.
 */
public final class WorkshopSessionStatus {

    public static final String SCHEDULED = "SCHEDULED";
    public static final String CANCELLED = "CANCELLED";
    public static final String COMPLETED = "COMPLETED";

    private WorkshopSessionStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        String s = status.trim().toUpperCase();
        return SCHEDULED.equals(s) || CANCELLED.equals(s) || COMPLETED.equals(s);
    }
}
