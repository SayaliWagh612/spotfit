package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;

import java.util.Objects;

/**
 * Booking configuration model for SLOT booking type.
 */
public class SlotBookingConfig implements BookingConfig {

    private int minimumDurationMinutes;
    private int maximumDurationMinutes;
    private int durationStepMinutes;
    private int slotIntervalMinutes;
    private int bufferBeforeMinutes;
    private int bufferAfterMinutes;
    private int maxPeople;
    private int advanceBookingDays;
    private int minimumAdvanceMinutes;

    public SlotBookingConfig() {
        this.minimumDurationMinutes = 60;
        this.maximumDurationMinutes = 240;
        this.durationStepMinutes = 30;
        this.slotIntervalMinutes = 30;
        this.bufferBeforeMinutes = 0;
        this.bufferAfterMinutes = 0;
        this.maxPeople = 1;
        this.advanceBookingDays = 30;
        this.minimumAdvanceMinutes = 0;
    }

    public SlotBookingConfig(int minimumDurationMinutes, int maximumDurationMinutes, int durationStepMinutes,
                             int slotIntervalMinutes, int bufferBeforeMinutes, int bufferAfterMinutes,
                             int maxPeople, int advanceBookingDays, int minimumAdvanceMinutes) {
        this.minimumDurationMinutes = minimumDurationMinutes;
        this.maximumDurationMinutes = maximumDurationMinutes;
        this.durationStepMinutes = durationStepMinutes;
        this.slotIntervalMinutes = slotIntervalMinutes;
        this.bufferBeforeMinutes = bufferBeforeMinutes;
        this.bufferAfterMinutes = bufferAfterMinutes;
        this.maxPeople = maxPeople;
        this.advanceBookingDays = advanceBookingDays;
        this.minimumAdvanceMinutes = minimumAdvanceMinutes;
    }

    @Override
    public String getBookingType() {
        return BookingType.SLOT;
    }

    @Override
    public BookingConfig copy() {
        return new SlotBookingConfig(minimumDurationMinutes, maximumDurationMinutes, durationStepMinutes,
                slotIntervalMinutes, bufferBeforeMinutes, bufferAfterMinutes, maxPeople,
                advanceBookingDays, minimumAdvanceMinutes);
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

    public int getSlotIntervalMinutes() {
        return slotIntervalMinutes;
    }

    public void setSlotIntervalMinutes(int slotIntervalMinutes) {
        this.slotIntervalMinutes = slotIntervalMinutes;
    }

    public int getBufferBeforeMinutes() {
        return bufferBeforeMinutes;
    }

    public void setBufferBeforeMinutes(int bufferBeforeMinutes) {
        this.bufferBeforeMinutes = bufferBeforeMinutes;
    }

    public int getBufferAfterMinutes() {
        return bufferAfterMinutes;
    }

    public void setBufferAfterMinutes(int bufferAfterMinutes) {
        this.bufferAfterMinutes = bufferAfterMinutes;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
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
        SlotBookingConfig that = (SlotBookingConfig) o;
        return minimumDurationMinutes == that.minimumDurationMinutes &&
                maximumDurationMinutes == that.maximumDurationMinutes &&
                durationStepMinutes == that.durationStepMinutes &&
                slotIntervalMinutes == that.slotIntervalMinutes &&
                bufferBeforeMinutes == that.bufferBeforeMinutes &&
                bufferAfterMinutes == that.bufferAfterMinutes &&
                maxPeople == that.maxPeople &&
                advanceBookingDays == that.advanceBookingDays &&
                minimumAdvanceMinutes == that.minimumAdvanceMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumDurationMinutes, maximumDurationMinutes, durationStepMinutes,
                slotIntervalMinutes, bufferBeforeMinutes, bufferAfterMinutes, maxPeople, advanceBookingDays, minimumAdvanceMinutes);
    }
}
