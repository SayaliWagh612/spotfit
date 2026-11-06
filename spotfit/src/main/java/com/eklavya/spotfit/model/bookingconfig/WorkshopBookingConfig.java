package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Booking configuration model for WORKSHOP booking type.
 */
public class WorkshopBookingConfig implements BookingConfig {

    private long pricePerPersonPaise;
    private int minimumParticipantsPerBooking;
    private int maximumParticipantsPerBooking;
    private int bookingCutoffHours;
    private int cancellationCutoffHours;
    private String ageRestriction;
    private List<String> materialsIncluded;
    private String instructorName;

    public WorkshopBookingConfig() {
        this.pricePerPersonPaise = 0L;
        this.minimumParticipantsPerBooking = 1;
        this.maximumParticipantsPerBooking = 20;
        this.bookingCutoffHours = 24;
        this.cancellationCutoffHours = 48;
        this.ageRestriction = "NONE";
        this.materialsIncluded = new ArrayList<>();
        this.instructorName = "";
    }

    public WorkshopBookingConfig(long pricePerPersonPaise, int minimumParticipantsPerBooking,
                                  int maximumParticipantsPerBooking, int bookingCutoffHours,
                                  int cancellationCutoffHours, String ageRestriction,
                                  List<String> materialsIncluded, String instructorName) {
        this.pricePerPersonPaise = pricePerPersonPaise;
        this.minimumParticipantsPerBooking = minimumParticipantsPerBooking;
        this.maximumParticipantsPerBooking = maximumParticipantsPerBooking;
        this.bookingCutoffHours = bookingCutoffHours;
        this.cancellationCutoffHours = cancellationCutoffHours;
        this.ageRestriction = ageRestriction != null ? ageRestriction : "NONE";
        this.materialsIncluded = materialsIncluded != null ? new ArrayList<>(materialsIncluded) : new ArrayList<>();
        this.instructorName = instructorName != null ? instructorName : "";
    }

    @Override
    public String getBookingType() {
        return BookingType.WORKSHOP;
    }

    @Override
    public BookingConfig copy() {
        return new WorkshopBookingConfig(pricePerPersonPaise, minimumParticipantsPerBooking,
                maximumParticipantsPerBooking, bookingCutoffHours, cancellationCutoffHours,
                ageRestriction, new ArrayList<>(materialsIncluded != null ? materialsIncluded : List.of()),
                instructorName);
    }

    public long getPricePerPersonPaise() {
        return pricePerPersonPaise;
    }

    public void setPricePerPersonPaise(long pricePerPersonPaise) {
        this.pricePerPersonPaise = pricePerPersonPaise;
    }

    public int getMinimumParticipantsPerBooking() {
        return minimumParticipantsPerBooking;
    }

    public void setMinimumParticipantsPerBooking(int minimumParticipantsPerBooking) {
        this.minimumParticipantsPerBooking = minimumParticipantsPerBooking;
    }

    public int getMaximumParticipantsPerBooking() {
        return maximumParticipantsPerBooking;
    }

    public void setMaximumParticipantsPerBooking(int maximumParticipantsPerBooking) {
        this.maximumParticipantsPerBooking = maximumParticipantsPerBooking;
    }

    public int getBookingCutoffHours() {
        return bookingCutoffHours;
    }

    public void setBookingCutoffHours(int bookingCutoffHours) {
        this.bookingCutoffHours = bookingCutoffHours;
    }

    public int getCancellationCutoffHours() {
        return cancellationCutoffHours;
    }

    public void setCancellationCutoffHours(int cancellationCutoffHours) {
        this.cancellationCutoffHours = cancellationCutoffHours;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction != null ? ageRestriction : "NONE";
    }

    public List<String> getMaterialsIncluded() {
        return materialsIncluded != null ? Collections.unmodifiableList(materialsIncluded) : Collections.emptyList();
    }

    public void setMaterialsIncluded(List<String> materialsIncluded) {
        this.materialsIncluded = materialsIncluded != null ? new ArrayList<>(materialsIncluded) : new ArrayList<>();
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName != null ? instructorName : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkshopBookingConfig that = (WorkshopBookingConfig) o;
        return pricePerPersonPaise == that.pricePerPersonPaise &&
                minimumParticipantsPerBooking == that.minimumParticipantsPerBooking &&
                maximumParticipantsPerBooking == that.maximumParticipantsPerBooking &&
                bookingCutoffHours == that.bookingCutoffHours &&
                cancellationCutoffHours == that.cancellationCutoffHours &&
                Objects.equals(ageRestriction, that.ageRestriction) &&
                Objects.equals(materialsIncluded, that.materialsIncluded) &&
                Objects.equals(instructorName, that.instructorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pricePerPersonPaise, minimumParticipantsPerBooking, maximumParticipantsPerBooking,
                bookingCutoffHours, cancellationCutoffHours, ageRestriction, materialsIncluded, instructorName);
    }
}
