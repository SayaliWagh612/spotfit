package com.eklavya.spotfit.model;

import java.util.UUID;

public class Payment {
    private String paymentId;
    private String bookingId;
    private String customerId;
    private String ownerId;
    private String spaceId;
    private String provider;
    private String providerOrderId;
    private String providerPaymentId;
    private long amountPaise;
    private String currency = "INR";
    private String status;
    private String mode;
    private String failureCode;
    private String failureMessage;
    private long createdAt;
    private long updatedAt;

    public Payment() {
        this.paymentId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public Payment(String paymentId, String bookingId, String customerId, String ownerId, String spaceId, long amountPaise, String mode, String status) {
        this.paymentId = paymentId != null ? paymentId : UUID.randomUUID().toString();
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.ownerId = ownerId;
        this.spaceId = spaceId;
        this.amountPaise = amountPaise;
        this.mode = mode;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public Payment(String paymentId, String bookingId, String spaceId, double amount, String paymentMethod, String status, String transactionDate, String razorpayPaymentIdMock) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.spaceId = spaceId;
        this.amountPaise = (long)(amount * 100);
        this.provider = paymentMethod;
        this.status = status;
        this.providerPaymentId = razorpayPaymentIdMock;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getPaymentMethod() { return provider; }
    public void setPaymentMethod(String paymentMethod) { this.provider = paymentMethod; }

    public String getProviderOrderId() { return providerOrderId; }
    public void setProviderOrderId(String providerOrderId) { this.providerOrderId = providerOrderId; }

    public String getProviderPaymentId() { return providerPaymentId; }
    public void setProviderPaymentId(String providerPaymentId) { this.providerPaymentId = providerPaymentId; }

    public long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }

    public double getAmount() { return (double) amountPaise / 100.0; }
    public void setAmount(double amount) { this.amountPaise = (long) (amount * 100); }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getFailureCode() { return failureCode; }
    public void setFailureCode(String failureCode) { this.failureCode = failureCode; }

    public String getFailureMessage() { return failureMessage; }
    public void setFailureMessage(String failureMessage) { this.failureMessage = failureMessage; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
