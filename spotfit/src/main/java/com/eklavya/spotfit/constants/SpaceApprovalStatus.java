package com.eklavya.spotfit.constants;

/**
 * Controlled approval status constants for SpotFit V2 Spaces.
 */
public final class SpaceApprovalStatus {

    public static final String DRAFT = "DRAFT";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String UNDER_REVIEW = "UNDER_REVIEW";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    private SpaceApprovalStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }

        String trimmed = status.trim().toUpperCase();
        return DRAFT.equals(trimmed)
                || SUBMITTED.equals(trimmed)
                || UNDER_REVIEW.equals(trimmed)
                || APPROVED.equals(trimmed)
                || REJECTED.equals(trimmed);
    }
}
