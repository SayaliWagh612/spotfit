package com.eklavya.spotfit.controller.user;

import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Payment;
import com.eklavya.spotfit.service.PaymentService;
import com.eklavya.spotfit.session.AppSession;

import java.util.concurrent.CompletableFuture;

public class CustomerPaymentController {

    private final PaymentService paymentService;

    public CustomerPaymentController() {
        this.paymentService = new PaymentService();
    }

    public CompletableFuture<OperationResult<Payment>> initiatePayment(Booking booking) {
        return paymentService.createPaymentOrder(booking, AppSession.getIdToken());
    }

    public CompletableFuture<OperationResult<Payment>> simulateDemoSuccess(
            Payment payment,
            Booking booking) {

        return paymentService.verifyDemoPayment(
                payment,
                booking,
                AppSession.getIdToken());
    }
}
