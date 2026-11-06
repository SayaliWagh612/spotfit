package com.eklavya.spotfit.model;

import com.eklavya.spotfit.model.bookingconfig.BookingConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Computed in-memory model holding customer booking choices and authoritative space configurations.
 * Never persisted directly to Firestore.
 */
public class CustomerBookingOptions {

    private String spaceId;
    private String bookingType;
    private LocalDate selectedDate;
    private BookingConfig bookingConfig;

    private List<Seat> seats;
    private String seatSelectionMessage;

    private List<AvailableSlot> availableSlots;
    private List<WorkshopSession> workshopSessions;

    public CustomerBookingOptions() {
        this.seats = new ArrayList<>();
        this.availableSlots = new ArrayList<>();
        this.workshopSessions = new ArrayList<>();
    }

    public CustomerBookingOptions(String spaceId, String bookingType, LocalDate selectedDate) {
        this();
        this.spaceId = spaceId;
        this.bookingType = bookingType;
        this.selectedDate = selectedDate;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public BookingConfig getBookingConfig() {
        return bookingConfig;
    }

    public void setBookingConfig(BookingConfig bookingConfig) {
        this.bookingConfig = bookingConfig;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats != null ? seats : new ArrayList<>();
    }

    public String getSeatSelectionMessage() {
        return seatSelectionMessage;
    }

    public void setSeatSelectionMessage(String seatSelectionMessage) {
        this.seatSelectionMessage = seatSelectionMessage;
    }

    public List<AvailableSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<AvailableSlot> availableSlots) {
        this.availableSlots = availableSlots != null ? availableSlots : new ArrayList<>();
    }

    public List<WorkshopSession> getWorkshopSessions() {
        return workshopSessions;
    }

    public void setWorkshopSessions(List<WorkshopSession> workshopSessions) {
        this.workshopSessions = workshopSessions != null ? workshopSessions : new ArrayList<>();
    }
}
