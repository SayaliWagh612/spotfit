package com.eklavya.spotfit.constants;

/**
 * Controlled complaint status constants for SpotFit.
 */
public final class ComplaintStatus {

    public static final String OPEN = "OPEN";
    public static final String ACKNOWLEDGED = "ACKNOWLEDGED";
    public static final String UNDER_REVIEW = "UNDER_REVIEW";
    public static final String ESCALATED = "ESCALATED";
    public static final String RESOLVED = "RESOLVED";
    public static final String CLOSED = "CLOSED";
    public static final String REJECTED = "REJECTED";

    private ComplaintStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return OPEN.equals(status)
                || ACKNOWLEDGED.equals(status)
                || UNDER_REVIEW.equals(status)
                || ESCALATED.equals(status)
                || RESOLVED.equals(status)
                || CLOSED.equals(status)
                || REJECTED.equals(status);
    }

    public static boolean isClosed(String status) {
        if (status == null) {
            return false;
        }

        return RESOLVED.equals(status)
                || CLOSED.equals(status)
                || REJECTED.equals(status);
    }
}
