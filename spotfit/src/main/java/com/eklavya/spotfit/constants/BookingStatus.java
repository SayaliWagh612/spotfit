package com.eklavya.spotfit.constants;

/**
 * Controlled booking status constants for SpotFit.
 */
public final class BookingStatus {

    public static final String INITIATED = "INITIATED";
    public static final String PENDING_PAYMENT = "PENDING_PAYMENT";
    public static final String PAYMENT_SUCCESS = "PAYMENT_SUCCESS";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CHECK_IN_PENDING = "CHECK_IN_PENDING";
    public static final String CHECKED_IN = "CHECKED_IN";
    public static final String CHECKED_OUT = "CHECKED_OUT";
    public static final String COMPLETED = "COMPLETED";
    public static final String REVIEWED = "REVIEWED";
    public static final String CANCELLED = "CANCELLED";
    public static final String EXPIRED = "EXPIRED";
    public static final String REFUNDED = "REFUNDED";
    public static final String NO_SHOW = "NO_SHOW";

    private BookingStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return INITIATED.equals(status)
                || PENDING_PAYMENT.equals(status)
                || PAYMENT_SUCCESS.equals(status)
                || CONFIRMED.equals(status)
                || CHECK_IN_PENDING.equals(status)
                || CHECKED_IN.equals(status)
                || CHECKED_OUT.equals(status)
                || COMPLETED.equals(status)
                || REVIEWED.equals(status)
                || CANCELLED.equals(status)
                || EXPIRED.equals(status)
                || REFUNDED.equals(status)
                || NO_SHOW.equals(status);
    }

    public static boolean isFinalStatus(String status) {
        if (status == null) {
            return false;
        }

        return REVIEWED.equals(status)
                || CANCELLED.equals(status)
                || EXPIRED.equals(status)
                || REFUNDED.equals(status)
                || NO_SHOW.equals(status);
    }
}
