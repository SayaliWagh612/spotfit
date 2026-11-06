package com.eklavya.spotfit.model.bookingconfig;

import com.eklavya.spotfit.constants.BookingType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Booking configuration model for RENTAL booking type.
 */
public class RentalBookingConfig implements BookingConfig {

    private int availableQuantity;
    private int minimumRentalDurationMinutes;
    private int maximumRentalDurationMinutes;
    private long securityDepositPaise;
    private boolean pickupAvailable;
    private boolean deliveryAvailable;
    private double deliveryRadiusKm;
    private long deliveryChargePaise;
    private String pickupInstructions;
    private boolean requiresIdentityProof;
    private long lateFeePaise;
    private List<String> addOns;

    public RentalBookingConfig() {
        this.availableQuantity = 1;
        this.minimumRentalDurationMinutes = 60;
        this.maximumRentalDurationMinutes = 1440;
        this.securityDepositPaise = 0L;
        this.pickupAvailable = true;
        this.deliveryAvailable = false;
        this.deliveryRadiusKm = 0.0;
        this.deliveryChargePaise = 0L;
        this.pickupInstructions = "";
        this.requiresIdentityProof = true;
        this.lateFeePaise = 0L;
        this.addOns = new ArrayList<>();
    }

    public RentalBookingConfig(int availableQuantity, int minimumRentalDurationMinutes, int maximumRentalDurationMinutes,
                               long securityDepositPaise, boolean pickupAvailable, boolean deliveryAvailable,
                               double deliveryRadiusKm, long deliveryChargePaise, String pickupInstructions,
                               boolean requiresIdentityProof, long lateFeePaise, List<String> addOns) {
        this.availableQuantity = availableQuantity;
        this.minimumRentalDurationMinutes = minimumRentalDurationMinutes;
        this.maximumRentalDurationMinutes = maximumRentalDurationMinutes;
        this.securityDepositPaise = securityDepositPaise;
        this.pickupAvailable = pickupAvailable;
        this.deliveryAvailable = deliveryAvailable;
        this.deliveryRadiusKm = deliveryRadiusKm;
        this.deliveryChargePaise = deliveryChargePaise;
        this.pickupInstructions = pickupInstructions != null ? pickupInstructions : "";
        this.requiresIdentityProof = requiresIdentityProof;
        this.lateFeePaise = lateFeePaise;
        this.addOns = addOns != null ? new ArrayList<>(addOns) : new ArrayList<>();
    }

    @Override
    public String getBookingType() {
        return BookingType.RENTAL;
    }

    @Override
    public BookingConfig copy() {
        return new RentalBookingConfig(availableQuantity, minimumRentalDurationMinutes,
                maximumRentalDurationMinutes, securityDepositPaise, pickupAvailable, deliveryAvailable,
                deliveryRadiusKm, deliveryChargePaise, pickupInstructions, requiresIdentityProof,
                lateFeePaise, new ArrayList<>(addOns != null ? addOns : List.of()));
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getMinimumRentalDurationMinutes() {
        return minimumRentalDurationMinutes;
    }

    public void setMinimumRentalDurationMinutes(int minimumRentalDurationMinutes) {
        this.minimumRentalDurationMinutes = minimumRentalDurationMinutes;
    }

    public int getMaximumRentalDurationMinutes() {
        return maximumRentalDurationMinutes;
    }

    public void setMaximumRentalDurationMinutes(int maximumRentalDurationMinutes) {
        this.maximumRentalDurationMinutes = maximumRentalDurationMinutes;
    }

    public long getSecurityDepositPaise() {
        return securityDepositPaise;
    }

    public void setSecurityDepositPaise(long securityDepositPaise) {
        this.securityDepositPaise = securityDepositPaise;
    }

    public boolean isPickupAvailable() {
        return pickupAvailable;
    }

    public void setPickupAvailable(boolean pickupAvailable) {
        this.pickupAvailable = pickupAvailable;
    }

    public boolean isDeliveryAvailable() {
        return deliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        this.deliveryAvailable = deliveryAvailable;
    }

    public double getDeliveryRadiusKm() {
        return deliveryRadiusKm;
    }

    public void setDeliveryRadiusKm(double deliveryRadiusKm) {
        this.deliveryRadiusKm = deliveryRadiusKm;
    }

    public long getDeliveryChargePaise() {
        return deliveryChargePaise;
    }

    public void setDeliveryChargePaise(long deliveryChargePaise) {
        this.deliveryChargePaise = deliveryChargePaise;
    }

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions != null ? pickupInstructions : "";
    }

    public boolean isRequiresIdentityProof() {
        return requiresIdentityProof;
    }

    public void setRequiresIdentityProof(boolean requiresIdentityProof) {
        this.requiresIdentityProof = requiresIdentityProof;
    }

    public long getLateFeePaise() {
        return lateFeePaise;
    }

    public void setLateFeePaise(long lateFeePaise) {
        this.lateFeePaise = lateFeePaise;
    }

    public List<String> getAddOns() {
        return addOns != null ? Collections.unmodifiableList(addOns) : Collections.emptyList();
    }

    public void setAddOns(List<String> addOns) {
        this.addOns = addOns != null ? new ArrayList<>(addOns) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalBookingConfig that = (RentalBookingConfig) o;
        return availableQuantity == that.availableQuantity &&
                minimumRentalDurationMinutes == that.minimumRentalDurationMinutes &&
                maximumRentalDurationMinutes == that.maximumRentalDurationMinutes &&
                securityDepositPaise == that.securityDepositPaise &&
                pickupAvailable == that.pickupAvailable &&
                deliveryAvailable == that.deliveryAvailable &&
                Double.compare(that.deliveryRadiusKm, deliveryRadiusKm) == 0 &&
                deliveryChargePaise == that.deliveryChargePaise &&
                requiresIdentityProof == that.requiresIdentityProof &&
                lateFeePaise == that.lateFeePaise &&
                Objects.equals(pickupInstructions, that.pickupInstructions) &&
                Objects.equals(addOns, that.addOns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availableQuantity, minimumRentalDurationMinutes, maximumRentalDurationMinutes,
                securityDepositPaise, pickupAvailable, deliveryAvailable, deliveryRadiusKm, deliveryChargePaise,
                pickupInstructions, requiresIdentityProof, lateFeePaise, addOns);
    }
}
