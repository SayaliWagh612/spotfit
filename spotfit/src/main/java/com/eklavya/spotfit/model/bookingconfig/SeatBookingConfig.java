package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.SeatPriceMode;
import com.eklavya.spotfit.constants.SeatSelectionMode;

import java.util.Objects;

/**
 * Booking configuration model for SEAT booking type.
 */
public class SeatBookingConfig implements BookingConfig {

    private String selectionMode;
    private int minimumDurationMinutes;
    private int maximumDurationMinutes;
    private int durationStepMinutes;
    private int maxGuestsPerBooking;
    private String seatPriceMode;
    private boolean allowMultipleSeats;
    private int advanceBookingDays;
    private int minimumAdvanceMinutes;

    public SeatBookingConfig() {
        this.selectionMode = SeatSelectionMode.AUTO_ASSIGN;
        this.minimumDurationMinutes = 60;
        this.maximumDurationMinutes = 480;
        this.durationStepMinutes = 60;
        this.maxGuestsPerBooking = 1;
        this.seatPriceMode = SeatPriceMode.UNIFORM;
        this.allowMultipleSeats = false;
        this.advanceBookingDays = 30;
        this.minimumAdvanceMinutes = 0;
    }

    public SeatBookingConfig(String selectionMode, int minimumDurationMinutes, int maximumDurationMinutes,
                              int durationStepMinutes, int maxGuestsPerBooking, String seatPriceMode,
                              boolean allowMultipleSeats, int advanceBookingDays, int minimumAdvanceMinutes) {
        this.selectionMode = SeatSelectionMode.isValid(selectionMode) ? selectionMode : SeatSelectionMode.AUTO_ASSIGN;
        this.minimumDurationMinutes = minimumDurationMinutes;
        this.maximumDurationMinutes = maximumDurationMinutes;
        this.durationStepMinutes = durationStepMinutes;
        this.maxGuestsPerBooking = maxGuestsPerBooking;
        this.seatPriceMode = SeatPriceMode.isValid(seatPriceMode) ? seatPriceMode : SeatPriceMode.UNIFORM;
        this.allowMultipleSeats = allowMultipleSeats;
        this.advanceBookingDays = advanceBookingDays;
        this.minimumAdvanceMinutes = minimumAdvanceMinutes;
    }

    @Override
    public String getBookingType() {
        return BookingType.SEAT;
    }

    @Override
    public BookingConfig copy() {
        return new SeatBookingConfig(selectionMode, minimumDurationMinutes, maximumDurationMinutes,
                durationStepMinutes, maxGuestsPerBooking, seatPriceMode, allowMultipleSeats,
                advanceBookingDays, minimumAdvanceMinutes);
    }

    public String getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(String selectionMode) {
        this.selectionMode = SeatSelectionMode.isValid(selectionMode) ? selectionMode : SeatSelectionMode.AUTO_ASSIGN;
    }

    public int getMinimumDurationMinutes() {
        return minimumDurationMinutes;
    }

    public void setMinimumDurationMinutes(int minimumDurationMinutes) {
        this.minimumDurationMinutes = minimumDurationMinutes;
    }

    public int getMaximumDurationMinutes() {
        return maximumDurationMinutes;
    }

    public void setMaximumDurationMinutes(int maximumDurationMinutes) {
        this.maximumDurationMinutes = maximumDurationMinutes;
    }

    public int getDurationStepMinutes() {
        return durationStepMinutes;
    }

    public void setDurationStepMinutes(int durationStepMinutes) {
        this.durationStepMinutes = durationStepMinutes;
    }

    public int getMaxGuestsPerBooking() {
        return maxGuestsPerBooking;
    }

    public void setMaxGuestsPerBooking(int maxGuestsPerBooking) {
        this.maxGuestsPerBooking = maxGuestsPerBooking;
    }

    public String getSeatPriceMode() {
        return seatPriceMode;
    }

    public void setSeatPriceMode(String seatPriceMode) {
        this.seatPriceMode = SeatPriceMode.isValid(seatPriceMode) ? seatPriceMode : SeatPriceMode.UNIFORM;
    }

    public boolean isAllowMultipleSeats() {
        return allowMultipleSeats;
    }

    public void setAllowMultipleSeats(boolean allowMultipleSeats) {
        this.allowMultipleSeats = allowMultipleSeats;
    }

    public int getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public void setAdvanceBookingDays(int advanceBookingDays) {
        this.advanceBookingDays = advanceBookingDays;
    }

    public int getMinimumAdvanceMinutes() {
        return minimumAdvanceMinutes;
    }

    public void setMinimumAdvanceMinutes(int minimumAdvanceMinutes) {
        this.minimumAdvanceMinutes = minimumAdvanceMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatBookingConfig that = (SeatBookingConfig) o;
        return minimumDurationMinutes == that.minimumDurationMinutes &&
                maximumDurationMinutes == that.maximumDurationMinutes &&
                durationStepMinutes == that.durationStepMinutes &&
                maxGuestsPerBooking == that.maxGuestsPerBooking &&
                allowMultipleSeats == that.allowMultipleSeats &&
                advanceBookingDays == that.advanceBookingDays &&
                minimumAdvanceMinutes == that.minimumAdvanceMinutes &&
                Objects.equals(selectionMode, that.selectionMode) &&
                Objects.equals(seatPriceMode, that.seatPriceMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectionMode, minimumDurationMinutes, maximumDurationMinutes, durationStepMinutes,
                maxGuestsPerBooking, seatPriceMode, allowMultipleSeats, advanceBookingDays, minimumAdvanceMinutes);
    }
}
