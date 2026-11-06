package com.eklavya.spotfit.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object capturing Customer booking inputs from UI forms.
 */
public class BookingRequest {

    private String spaceId;
    private String bookingType;

    private LocalDate selectedDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int durationHours = 1;
    private int durationMinutes = 60;

    // SEAT type fields
    private String seatId;
    private String seatName;
    private int seatCount = 1;

    // WORKSHOP type fields
    private String sessionId;
    private int participantCount = 1;

    // RENTAL type fields
    private int rentalDays = 1;
    private int quantity = 1;
    private String deliveryOption; // "PICKUP" or "DELIVERY"
    private String deliveryAddress;
    private List<String> selectedAddOnIds;

    // VENUE type fields
    private int guestCount = 1;
    private String requirements;

    // PRACTICE_ROOM type fields
    private List<String> selectedEquipmentIds;

    public BookingRequest() {
        this.selectedAddOnIds = new ArrayList<>();
        this.selectedEquipmentIds = new ArrayList<>();
    }

    public BookingRequest(String spaceId, String bookingType) {
        this();
        this.spaceId = spaceId;
        this.bookingType = bookingType;
    }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public LocalDate getSelectedDate() { return selectedDate; }
    public void setSelectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public String getSeatName() { return seatName; }
    public void setSeatName(String seatName) { this.seatName = seatName; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public int getRentalDays() { return rentalDays; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public List<String> getSelectedAddOnIds() { return selectedAddOnIds; }
    public void setSelectedAddOnIds(List<String> selectedAddOnIds) { this.selectedAddOnIds = selectedAddOnIds != null ? selectedAddOnIds : new ArrayList<>(); }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public List<String> getSelectedEquipmentIds() { return selectedEquipmentIds; }
    public void setSelectedEquipmentIds(List<String> selectedEquipmentIds) { this.selectedEquipmentIds = selectedEquipmentIds != null ? selectedEquipmentIds : new ArrayList<>(); }
}
