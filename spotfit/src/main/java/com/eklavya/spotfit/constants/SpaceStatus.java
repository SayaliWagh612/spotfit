package com.eklavya.spotfit.constants;

/**
 * Controlled space lifecycle status constants for SpotFit.
 */
public final class SpaceStatus {

    public static final String DRAFT = "DRAFT";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String UNDER_REVIEW = "UNDER_REVIEW";
    public static final String APPROVED = "APPROVED";
    public static final String LIVE = "LIVE";
    public static final String REJECTED = "REJECTED";
    public static final String SUSPENDED = "SUSPENDED";
    public static final String CLOSED = "CLOSED";
    public static final String INACTIVE = "INACTIVE";
    public static final String ARCHIVED = "ARCHIVED";

    private SpaceStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return DRAFT.equals(status)
                || SUBMITTED.equals(status)
                || UNDER_REVIEW.equals(status)
                || APPROVED.equals(status)
                || LIVE.equals(status)
                || REJECTED.equals(status)
                || SUSPENDED.equals(status)
                || CLOSED.equals(status)
                || INACTIVE.equals(status)
                || ARCHIVED.equals(status);
    }

    public static boolean isVisibleToUsers(String status) {
        if (status == null) {
            return false;
        }

        return LIVE.equals(status);
    }
}
