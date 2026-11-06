// package com.eklavya.spotfit.service;

// import com.eklavya.spotfit.constants.BookingStatus;
// import com.eklavya.spotfit.constants.OperationCode;
// import com.eklavya.spotfit.constants.PaymentStatus;
// import com.eklavya.spotfit.dao.PaymentDao;
// import com.eklavya.spotfit.model.Booking;
// import com.eklavya.spotfit.model.OperationResult;
// import com.eklavya.spotfit.model.Payment;
// import com.eklavya.spotfit.config.AppConfig;
// import com.eklavya.spotfit.util.ValidationUtil;

// import java.util.UUID;
// import java.util.concurrent.CompletableFuture;

// public class PaymentService {

//     private final PaymentDao paymentDao;
//     private final BookingService bookingService;

//     public PaymentService() {
//         this.paymentDao = new PaymentDao();
//         this.bookingService = new BookingService();
//     }

//     public CompletableFuture<OperationResult<Payment>> createPaymentOrder(Booking booking, String idToken) {
//         if (booking == null || ValidationUtil.isBlank(booking.getBookingId())) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Booking is required."));
//         }

//         if (BookingStatus.EXPIRED.equalsIgnoreCase(booking.getStatus())) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_STATE, "Booking intent has expired."));
//         }

//         if (!BookingStatus.PENDING_PAYMENT.equalsIgnoreCase(booking.getStatus())) {
//             return CompletableFuture.completedFuture(
//                     OperationResult.failure(OperationCode.INVALID_STATE, "Booking is not in PENDING_PAYMENT state."));
//         }

//         String mode = AppConfig.getPaymentMode();
//         long amountPaise = booking.getTotalAmountPaise() > 0 ? booking.getTotalAmountPaise() : Math.round(booking.getAmount() * 100);

//         Payment payment = new Payment(
//                 UUID.randomUUID().toString(),
//                 booking.getBookingId(),
//                 booking.getCustomerId() != null ? booking.getCustomerId() : com.eklavya.spotfit.session.AppSession.getUserId(),
//                 booking.getOwnerId(),
//                 booking.getSpaceId(),
//                 amountPaise,
//                 mode,
//                 PaymentStatus.PENDING
//         );

//         if ("DEMO".equalsIgnoreCase(mode)) {
//             payment.setProvider("DEMO_PROVIDER");
//             payment.setProviderOrderId("demo_order_" + UUID.randomUUID().toString().substring(0, 8));
//         }

//         return paymentDao.createPayment(payment, idToken)
//                 .thenApply(res -> {
//                     if (res.isSuccess()) {
//                         return OperationResult.success(payment);
//                     } else {
//                         return OperationResult.failure(res.getErrorCode(), res.getMessage());
//                     }
//                 });
//     }

//     public CompletableFuture<OperationResult<Payment>> verifyDemoPayment(Payment payment) {
//         if (payment == null) {
//             return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Payment is required."));
//         }

//         if (!"DEMO".equalsIgnoreCase(payment.getMode())) {
//             return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_STATE, "Cannot verify non-DEMO payment in this mode."));
//         }

//         // In DEMO mode without a backend, we cannot update Firestore rules for Payment and Booking (Customer cannot write them).
//         // Therefore, we just simulate the success in-memory. The real booking remains PENDING_PAYMENT.
//         payment.setStatus(PaymentStatus.SUCCESSFUL);
//         payment.setProviderPaymentId("demo_pay_" + UUID.randomUUID().toString().substring(0, 8));
//         payment.setUpdatedAt(System.currentTimeMillis());

//         return CompletableFuture.completedFuture(OperationResult.success(payment));
//     }
// }
package com.eklavya.spotfit.service;

import com.eklavya.spotfit.config.AppConfig;
import com.eklavya.spotfit.constants.BookingStatus;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.PaymentStatus;
import com.eklavya.spotfit.dao.BookingDao;
import com.eklavya.spotfit.dao.PaymentDao;
import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Payment;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PaymentService {

    private final PaymentDao paymentDao;
    private final BookingService bookingService;
    private final BookingDao bookingDao;

    public PaymentService() {
        this.paymentDao = new PaymentDao();
        this.bookingService = new BookingService();
        this.bookingDao = new BookingDao();
    }

    public CompletableFuture<OperationResult<Payment>> createPaymentOrder(
            Booking booking,
            String idToken) {

        if (booking == null
                || ValidationUtil.isBlank(booking.getBookingId())) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Booking is required."));
        }

        if (BookingStatus.EXPIRED.equalsIgnoreCase(
                booking.getStatus())) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_STATE,
                            "Booking intent has expired."));
        }

        if (!BookingStatus.PENDING_PAYMENT.equalsIgnoreCase(
                booking.getStatus())) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_STATE,
                            "Booking is not in PENDING_PAYMENT state."));
        }

        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "Authentication is required."));
        }

        String mode = AppConfig.getPaymentMode();

        long amountPaise = booking.getTotalAmountPaise() > 0
                ? booking.getTotalAmountPaise()
                : Math.round(booking.getAmount() * 100);

        String customerId = booking.getCustomerId() != null
                ? booking.getCustomerId()
                : AppSession.getUserId();

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                booking.getBookingId(),
                customerId,
                booking.getOwnerId(),
                booking.getSpaceId(),
                amountPaise,
                mode,
                PaymentStatus.PENDING);

        if ("DEMO".equalsIgnoreCase(mode)) {
            payment.setProvider("DEMO_PROVIDER");

            payment.setProviderOrderId(
                    "demo_order_"
                            + UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8));
        }

        return paymentDao
                .createPayment(payment, idToken)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return OperationResult.success(payment);
                    }

                    return OperationResult.failure(
                            result.getErrorCode(),
                            result.getMessage());
                });
    }

    /**
     * Temporary demonstration payment confirmation.
     *
     * No real money is charged. The customer-owned Firestore
     * booking is changed from PENDING_PAYMENT to CONFIRMED.
     */
    public CompletableFuture<OperationResult<Payment>> verifyDemoPayment(
            Payment payment,
            Booking booking,
            String idToken) {

        if (payment == null || booking == null) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Payment and booking are required."));
        }

        if (ValidationUtil.isBlank(payment.getBookingId())
                || ValidationUtil.isBlank(booking.getBookingId())
                || !payment.getBookingId().equals(
                        booking.getBookingId())) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_ARGUMENT,
                            "Payment does not belong to this booking."));
        }

        if (!"DEMO".equalsIgnoreCase(payment.getMode())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_STATE,
                            "Cannot verify a non-demo payment."));
        }

        if (ValidationUtil.isBlank(idToken)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.AUTHENTICATION_REQUIRED,
                            "Authentication is required."));
        }

        if (!BookingStatus.PENDING_PAYMENT.equalsIgnoreCase(
                booking.getStatus())) {

            return CompletableFuture.completedFuture(
                    OperationResult.failure(
                            OperationCode.INVALID_STATE,
                            "Only a pending booking can be confirmed."));
        }

        return bookingDao
                .confirmDemoPayment(booking, idToken)
                .thenApply(bookingResult -> {
                    if (bookingResult.isFailure()) {
                        return OperationResult.failure(
                                bookingResult.getErrorCode(),
                                bookingResult.getMessage());
                    }

                    payment.setStatus(PaymentStatus.SUCCESSFUL);

                    payment.setProviderPaymentId(
                            "demo_pay_"
                                    + UUID.randomUUID()
                                            .toString()
                                            .substring(0, 8));

                    payment.setUpdatedAt(
                            System.currentTimeMillis());

                    return OperationResult.success(
                            payment,
                            "Demo payment approved and booking confirmed.");
                });
    }
}