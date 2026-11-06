package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Booking configuration model for PRACTICE_ROOM booking type.
 */
public class PracticeRoomBookingConfig implements BookingConfig {

    private int minimumDurationMinutes;
    private int maximumDurationMinutes;
    private int durationStepMinutes;
    private boolean equipmentSelectionEnabled;
    private List<String> includedEquipment;
    private List<String> optionalEquipment;

    public PracticeRoomBookingConfig() {
        this.minimumDurationMinutes = 60;
        this.maximumDurationMinutes = 300;
        this.durationStepMinutes = 30;
        this.equipmentSelectionEnabled = false;
        this.includedEquipment = new ArrayList<>();
        this.optionalEquipment = new ArrayList<>();
    }

    public PracticeRoomBookingConfig(int minimumDurationMinutes, int maximumDurationMinutes, int durationStepMinutes,
                                    boolean equipmentSelectionEnabled, List<String> includedEquipment, List<String> optionalEquipment) {
        this.minimumDurationMinutes = minimumDurationMinutes;
        this.maximumDurationMinutes = maximumDurationMinutes;
        this.durationStepMinutes = durationStepMinutes;
        this.equipmentSelectionEnabled = equipmentSelectionEnabled;
        this.includedEquipment = includedEquipment != null ? new ArrayList<>(includedEquipment) : new ArrayList<>();
        this.optionalEquipment = optionalEquipment != null ? new ArrayList<>(optionalEquipment) : new ArrayList<>();
    }

    @Override
    public String getBookingType() {
        return BookingType.PRACTICE_ROOM;
    }

    @Override
    public BookingConfig copy() {
        return new PracticeRoomBookingConfig(minimumDurationMinutes, maximumDurationMinutes,
                durationStepMinutes, equipmentSelectionEnabled,
                new ArrayList<>(includedEquipment != null ? includedEquipment : List.of()),
                new ArrayList<>(optionalEquipment != null ? optionalEquipment : List.of()));
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

    public boolean isEquipmentSelectionEnabled() {
        return equipmentSelectionEnabled;
    }

    public void setEquipmentSelectionEnabled(boolean equipmentSelectionEnabled) {
        this.equipmentSelectionEnabled = equipmentSelectionEnabled;
    }

    public List<String> getIncludedEquipment() {
        return includedEquipment != null ? Collections.unmodifiableList(includedEquipment) : Collections.emptyList();
    }

    public void setIncludedEquipment(List<String> includedEquipment) {
        this.includedEquipment = includedEquipment != null ? new ArrayList<>(includedEquipment) : new ArrayList<>();
    }

    public List<String> getOptionalEquipment() {
        return optionalEquipment != null ? Collections.unmodifiableList(optionalEquipment) : Collections.emptyList();
    }

    public void setOptionalEquipment(List<String> optionalEquipment) {
        this.optionalEquipment = optionalEquipment != null ? new ArrayList<>(optionalEquipment) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PracticeRoomBookingConfig that = (PracticeRoomBookingConfig) o;
        return minimumDurationMinutes == that.minimumDurationMinutes &&
                maximumDurationMinutes == that.maximumDurationMinutes &&
                durationStepMinutes == that.durationStepMinutes &&
                equipmentSelectionEnabled == that.equipmentSelectionEnabled &&
                Objects.equals(includedEquipment, that.includedEquipment) &&
                Objects.equals(optionalEquipment, that.optionalEquipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumDurationMinutes, maximumDurationMinutes, durationStepMinutes,
                equipmentSelectionEnabled, includedEquipment, optionalEquipment);
    }
}
