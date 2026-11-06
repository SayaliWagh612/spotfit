package com.eklavya.spotfit.model;

/**
 * Structured DTO returned by previewV2Booking for pre-submission display.
 */
public class BookingPreviewResult {

    private String spaceId;
    private String bookingType;
    private long startAt;
    private long endAt;
    private int participantCountOrQuantity;

    private long baseQuotePaise;
    private long additionalQuotePaise;
    private long totalQuotePaise;

    private String pricingUnit;
    private boolean requiresApproval;
    private String prototypeLimitationMessage;

    public BookingPreviewResult() {}

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public long getStartAt() { return startAt; }
    public void setStartAt(long startAt) { this.startAt = startAt; }

    public long getEndAt() { return endAt; }
    public void setEndAt(long endAt) { this.endAt = endAt; }

    public int getParticipantCountOrQuantity() { return participantCountOrQuantity; }
    public void setParticipantCountOrQuantity(int participantCountOrQuantity) { this.participantCountOrQuantity = participantCountOrQuantity; }

    public long getBaseQuotePaise() { return baseQuotePaise; }
    public void setBaseQuotePaise(long baseQuotePaise) { this.baseQuotePaise = baseQuotePaise; }

    public long getAdditionalQuotePaise() { return additionalQuotePaise; }
    public void setAdditionalQuotePaise(long additionalQuotePaise) { this.additionalQuotePaise = additionalQuotePaise; }

    public long getTotalQuotePaise() { return totalQuotePaise; }
    public void setTotalQuotePaise(long totalQuotePaise) { this.totalQuotePaise = totalQuotePaise; }

    public String getPricingUnit() { return pricingUnit; }
    public void setPricingUnit(String pricingUnit) { this.pricingUnit = pricingUnit; }

    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public String getPrototypeLimitationMessage() { return prototypeLimitationMessage; }
    public void setPrototypeLimitationMessage(String prototypeLimitationMessage) { this.prototypeLimitationMessage = prototypeLimitationMessage; }
}
