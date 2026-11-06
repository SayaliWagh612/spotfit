package com.eklavya.spotfit.constants;

/**
 * Controlled payment status constants for SpotFit.
 */
public final class PaymentStatus {

    public static final String NOT_STARTED = "NOT_STARTED";
    public static final String PENDING = "PENDING";
    public static final String PROCESSING = "PROCESSING";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String FAILED = "FAILED";
    public static final String REFUND_PENDING = "REFUND_PENDING";
    public static final String PARTIALLY_REFUNDED = "PARTIALLY_REFUNDED";
    public static final String REFUNDED = "REFUNDED";
    public static final String CANCELLED = "CANCELLED";

    private PaymentStatus() {
    }

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }

        return NOT_STARTED.equals(status)
                || PENDING.equals(status)
                || PROCESSING.equals(status)
                || SUCCESSFUL.equals(status)
                || FAILED.equals(status)
                || REFUND_PENDING.equals(status)
                || PARTIALLY_REFUNDED.equals(status)
                || REFUNDED.equals(status)
                || CANCELLED.equals(status);
    }

    public static boolean isSuccessful(String status) {
        if (status == null) {
            return false;
        }

        return SUCCESSFUL.equals(status);
    }
}
