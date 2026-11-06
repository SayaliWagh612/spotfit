package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Booking configuration model for VENUE booking type.
 */
public class VenueBookingConfig implements BookingConfig {

    private int minimumBookingHours;
    private int maximumBookingHours;
    private boolean requiresApproval;
    private int minimumAdvanceDays;
    private List<String> allowedEventTypes;
    private List<String> setupOptions;
    private long securityDepositPaise;
    private boolean soundSystemAvailable;
    private boolean lightingAvailable;
    private boolean stageAvailable;
    private boolean greenRoomAvailable;
    private boolean outsideVendorAllowed;
    private boolean cateringAllowed;
    private long overtimeRatePaise;

    public VenueBookingConfig() {
        this.minimumBookingHours = 2;
        this.maximumBookingHours = 24;
        this.requiresApproval = true;
        this.minimumAdvanceDays = 1;
        this.allowedEventTypes = new ArrayList<>();
        this.setupOptions = new ArrayList<>();
        this.securityDepositPaise = 0L;
        this.soundSystemAvailable = false;
        this.lightingAvailable = false;
        this.stageAvailable = false;
        this.greenRoomAvailable = false;
        this.outsideVendorAllowed = true;
        this.cateringAllowed = true;
        this.overtimeRatePaise = 0L;
    }

    public VenueBookingConfig(int minimumBookingHours, int maximumBookingHours, boolean requiresApproval,
                              int minimumAdvanceDays, List<String> allowedEventTypes, List<String> setupOptions,
                              long securityDepositPaise, boolean soundSystemAvailable, boolean lightingAvailable,
                              boolean stageAvailable, boolean greenRoomAvailable, boolean outsideVendorAllowed,
                              boolean cateringAllowed, long overtimeRatePaise) {
        this.minimumBookingHours = minimumBookingHours;
        this.maximumBookingHours = maximumBookingHours;
        this.requiresApproval = requiresApproval;
        this.minimumAdvanceDays = minimumAdvanceDays;
        this.allowedEventTypes = allowedEventTypes != null ? new ArrayList<>(allowedEventTypes) : new ArrayList<>();
        this.setupOptions = setupOptions != null ? new ArrayList<>(setupOptions) : new ArrayList<>();
        this.securityDepositPaise = securityDepositPaise;
        this.soundSystemAvailable = soundSystemAvailable;
        this.lightingAvailable = lightingAvailable;
        this.stageAvailable = stageAvailable;
        this.greenRoomAvailable = greenRoomAvailable;
        this.outsideVendorAllowed = outsideVendorAllowed;
        this.cateringAllowed = cateringAllowed;
        this.overtimeRatePaise = overtimeRatePaise;
    }

    @Override
    public String getBookingType() {
        return BookingType.VENUE;
    }

    @Override
    public BookingConfig copy() {
        return new VenueBookingConfig(minimumBookingHours, maximumBookingHours, requiresApproval,
                minimumAdvanceDays, new ArrayList<>(allowedEventTypes != null ? allowedEventTypes : List.of()),
                new ArrayList<>(setupOptions != null ? setupOptions : List.of()), securityDepositPaise,
                soundSystemAvailable, lightingAvailable, stageAvailable, greenRoomAvailable,
                outsideVendorAllowed, cateringAllowed, overtimeRatePaise);
    }

    public int getMinimumBookingHours() {
        return minimumBookingHours;
    }

    public void setMinimumBookingHours(int minimumBookingHours) {
        this.minimumBookingHours = minimumBookingHours;
    }

    public int getMaximumBookingHours() {
        return maximumBookingHours;
    }

    public void setMaximumBookingHours(int maximumBookingHours) {
        this.maximumBookingHours = maximumBookingHours;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }

    public int getMinimumAdvanceDays() {
        return minimumAdvanceDays;
    }

    public void setMinimumAdvanceDays(int minimumAdvanceDays) {
        this.minimumAdvanceDays = minimumAdvanceDays;
    }

    public List<String> getAllowedEventTypes() {
        return allowedEventTypes != null ? Collections.unmodifiableList(allowedEventTypes) : Collections.emptyList();
    }

    public void setAllowedEventTypes(List<String> allowedEventTypes) {
        this.allowedEventTypes = allowedEventTypes != null ? new ArrayList<>(allowedEventTypes) : new ArrayList<>();
    }

    public List<String> getSetupOptions() {
        return setupOptions != null ? Collections.unmodifiableList(setupOptions) : Collections.emptyList();
    }

    public void setSetupOptions(List<String> setupOptions) {
        this.setupOptions = setupOptions != null ? new ArrayList<>(setupOptions) : new ArrayList<>();
    }

    public long getSecurityDepositPaise() {
        return securityDepositPaise;
    }

    public void setSecurityDepositPaise(long securityDepositPaise) {
        this.securityDepositPaise = securityDepositPaise;
    }

    public boolean isSoundSystemAvailable() {
        return soundSystemAvailable;
    }

    public void setSoundSystemAvailable(boolean soundSystemAvailable) {
        this.soundSystemAvailable = soundSystemAvailable;
    }

    public boolean isLightingAvailable() {
        return lightingAvailable;
    }

    public void setLightingAvailable(boolean lightingAvailable) {
        this.lightingAvailable = lightingAvailable;
    }

    public boolean isStageAvailable() {
        return stageAvailable;
    }

    public void setStageAvailable(boolean stageAvailable) {
        this.stageAvailable = stageAvailable;
    }

    public boolean isGreenRoomAvailable() {
        return greenRoomAvailable;
    }

    public void setGreenRoomAvailable(boolean greenRoomAvailable) {
        this.greenRoomAvailable = greenRoomAvailable;
    }

    public boolean isOutsideVendorAllowed() {
        return outsideVendorAllowed;
    }

    public void setOutsideVendorAllowed(boolean outsideVendorAllowed) {
        this.outsideVendorAllowed = outsideVendorAllowed;
    }

    public boolean isCateringAllowed() {
        return cateringAllowed;
    }

    public void setCateringAllowed(boolean cateringAllowed) {
        this.cateringAllowed = cateringAllowed;
    }

    public long getOvertimeRatePaise() {
        return overtimeRatePaise;
    }

    public void setOvertimeRatePaise(long overtimeRatePaise) {
        this.overtimeRatePaise = overtimeRatePaise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VenueBookingConfig that = (VenueBookingConfig) o;
        return minimumBookingHours == that.minimumBookingHours &&
                maximumBookingHours == that.maximumBookingHours &&
                requiresApproval == that.requiresApproval &&
                minimumAdvanceDays == that.minimumAdvanceDays &&
                securityDepositPaise == that.securityDepositPaise &&
                soundSystemAvailable == that.soundSystemAvailable &&
                lightingAvailable == that.lightingAvailable &&
                stageAvailable == that.stageAvailable &&
                greenRoomAvailable == that.greenRoomAvailable &&
                outsideVendorAllowed == that.outsideVendorAllowed &&
                cateringAllowed == that.cateringAllowed &&
                overtimeRatePaise == that.overtimeRatePaise &&
                Objects.equals(allowedEventTypes, that.allowedEventTypes) &&
                Objects.equals(setupOptions, that.setupOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumBookingHours, maximumBookingHours, requiresApproval, minimumAdvanceDays,
                allowedEventTypes, setupOptions, securityDepositPaise, soundSystemAvailable, lightingAvailable,
                stageAvailable, greenRoomAvailable, outsideVendorAllowed, cateringAllowed, overtimeRatePaise);
    }
}
