package com.eklavya.spotfit.constants;

/**
 * Controlled review moderation status constants for SpotFit.
 */
public final class ReviewStatus {

    public static final String PENDING = "PENDING";
    public static final String PUBLISHED = "PUBLISHED";
    public static final String HIDDEN = "HIDDEN";
    public static final String FLAGGED = "FLAGGED";
    public static final String REMOVED = "REMOVED";

    private ReviewStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return PENDING.equals(status)
                || PUBLISHED.equals(status)
                || HIDDEN.equals(status)
                || FLAGGED.equals(status)
                || REMOVED.equals(status);
    }

    public static boolean isPubliclyVisible(String status) {
        if (status == null) {
            return false;
        }

        return PUBLISHED.equals(status);
    }
}
