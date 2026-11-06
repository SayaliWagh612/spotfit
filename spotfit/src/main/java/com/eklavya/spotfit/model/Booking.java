package com.eklavya.spotfit.model;

import java.util.UUID;

public class Booking {
    private String bookingId;
    
    // Object references from USER_SIDE
    private Space space;
    private Offering offering;
    
    // IDs and attributes from OWNER_MANAGER_SIDE
    private String spaceId;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String offeringName;
    private String resourceId;
    private String resourceName;
    private String otpCode;
    
    // Unified Date/Time/Type fields
    private String bookingType;
    private String date; // corresponds to bookingDate
    private String time; // corresponds to slotTime
    
    private double amount;
    private String verificationCode; // matches otpCode conceptually
    
    // Statuses
    private VerificationStatus verificationStatus;
    private String paymentStatus;
    private String status;
    private String checkInTime;
    private String checkOutTime;

    // Additive V2 fields
    private int schemaVersion = 1;
    private String businessId;
    private String ownerId;
    private long startAt;
    private long endAt;
    private int participantCount = 1;
    private int quantity = 1;
    private String seatId;
    private String seatName;
    private String sessionId;
    private long baseAmountPaise;
    private long additionalAmountPaise;
    private long totalAmountPaise;
    private long quotedTotalAmountPaise;
    private String pricingUnit;
    private String businessName;
    private String spaceName;
    private String deliveryAddress;
    private String requirements;
    private boolean requiresApproval;
    private long createdAt;
    private long updatedAt;


    public Booking() {
        this.bookingId = UUID.randomUUID().toString();
    }

    // USER_SIDE Constructor
    public Booking(Space space, Offering offering, String date, String time, double amount, String verificationCode) {
        this.bookingId = UUID.randomUUID().toString();
        this.space = space;
        this.offering = offering;
        this.bookingType = offering != null ? offering.getBookingType() : null;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.verificationCode = verificationCode;
        this.otpCode = verificationCode;
        this.verificationStatus = VerificationStatus.PENDING;
        if (space != null) this.spaceId = space.getSpaceId();
        if (offering != null) this.offeringName = offering.getName();
    }

    // OWNER_MANAGER_SIDE Constructor
    public Booking(String bookingId, String spaceId, String customerId, String customerName, String customerEmail,
                   String customerPhone, String offeringName, String resourceId, String resourceName, String otpCode,
                   String bookingDate, String slotTime, double amount, String paymentStatus, String status,
                   String checkInTime, String checkOutTime) {
        this.bookingId = bookingId;
        this.spaceId = spaceId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.offeringName = offeringName;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.otpCode = otpCode;
        this.verificationCode = otpCode;
        this.date = bookingDate;
        this.time = slotTime;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public Space getSpace() { return space; }
    public void setSpace(Space space) { this.space = space; }

    public Offering getOffering() { return offering; }
    public void setOffering(Offering offering) { this.offering = offering; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getBookingDate() { return date; }
    public void setBookingDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getSlotTime() { return time; }
    public void setSlotTime(String slotTime) { this.time = slotTime; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; this.otpCode = verificationCode; }

    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus status) { this.verificationStatus = status; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getOfferingName() { return offeringName; }
    public void setOfferingName(String offeringName) { this.offeringName = offeringName; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; this.verificationCode = otpCode; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public int getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(int schemaVersion) { this.schemaVersion = schemaVersion; }

    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public long getStartAt() { return startAt; }
    public void setStartAt(long startAt) { this.startAt = startAt; }

    public long getEndAt() { return endAt; }
    public void setEndAt(long endAt) { this.endAt = endAt; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public String getSeatName() { return seatName; }
    public void setSeatName(String seatName) { this.seatName = seatName; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public long getBaseAmountPaise() { return baseAmountPaise; }
    public void setBaseAmountPaise(long baseAmountPaise) { this.baseAmountPaise = baseAmountPaise; }

    public long getAdditionalAmountPaise() { return additionalAmountPaise; }
    public void setAdditionalAmountPaise(long additionalAmountPaise) { this.additionalAmountPaise = additionalAmountPaise; }

    public long getTotalAmountPaise() { return totalAmountPaise; }
    public void setTotalAmountPaise(long totalAmountPaise) {
        this.totalAmountPaise = totalAmountPaise;
        this.quotedTotalAmountPaise = totalAmountPaise;
        this.amount = totalAmountPaise / 100.0;
    }

    public long getQuotedTotalAmountPaise() { return quotedTotalAmountPaise; }
    public void setQuotedTotalAmountPaise(long quotedTotalAmountPaise) {
        this.quotedTotalAmountPaise = quotedTotalAmountPaise;
        this.totalAmountPaise = quotedTotalAmountPaise;
        this.amount = quotedTotalAmountPaise / 100.0;
    }

    public String getPricingUnit() { return pricingUnit; }
    public void setPricingUnit(String pricingUnit) { this.pricingUnit = pricingUnit; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getSpaceName() { return spaceName; }
    public void setSpaceName(String spaceName) { this.spaceName = spaceName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
