package com.eklavya.spotfit.constants;

/**
 * Controlled invitation status constants for SpotFit.
 */
public final class InvitationStatus {

    public static final String PENDING = "PENDING";
    public static final String VERIFIED = "VERIFIED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String EXPIRED = "EXPIRED";
    public static final String CANCELLED = "CANCELLED";
    public static final String REVOKED = "REVOKED";

    private InvitationStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return PENDING.equals(status)
                || VERIFIED.equals(status)
                || ACCEPTED.equals(status)
                || EXPIRED.equals(status)
                || CANCELLED.equals(status)
                || REVOKED.equals(status);
    }

    public static boolean isUsable(String status) {
        if (status == null) {
            return false;
        }

        return PENDING.equals(status) || VERIFIED.equals(status);
    }
}
