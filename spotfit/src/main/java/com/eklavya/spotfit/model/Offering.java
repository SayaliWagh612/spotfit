package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.OfferingStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a bookable service or package offered in a Space.
 * Money is persisted strictly in paise using a long integer.
 */
public class Offering {

    private String offeringId;
    private String spaceId;
    private String businessId;
    private String ownerId;

    private String offeringName;
    private String description;

    private String bookingType;
    private String pricingType;
    private long pricePaise;

    private String requiredResourceType;

    private int defaultDurationMinutes;
    private int minimumDurationMinutes;
    private int maximumDurationMinutes;
    private int bookingIntervalMinutes;

    private int capacity;
    private int minimumParticipants;
    private int maximumParticipants;

    private List<String> includedItems;
    private List<String> rules;

    private String status;
    private boolean active;

    private long createdAt;
    private long updatedAt;

    public Offering() {
        this.includedItems = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.status = OfferingStatus.DRAFT;
        this.active = true;
    }

    public Offering(String offeringId, String offeringName, String description, String bookingType, double priceRupees, String pricingType) {
        this();
        this.offeringId = offeringId;
        this.offeringName = offeringName;
        this.description = description;
        this.bookingType = bookingType;
        this.pricingType = pricingType;
        this.pricePaise = (long) (priceRupees * 100);
    }

    public Offering(String offeringId, String spaceId, String offeringName, String bookingType, String pricingType, double priceRupees, int durationMinutes, int capacity, String description, boolean active) {
        this();
        this.offeringId = offeringId;
        this.spaceId = spaceId;
        this.offeringName = offeringName;
        this.bookingType = bookingType;
        this.pricingType = pricingType;
        this.pricePaise = (long) (priceRupees * 100);
        this.defaultDurationMinutes = durationMinutes;
        this.capacity = capacity;
        this.description = description;
        this.active = active;
    }

    public Offering(
            String offeringId,
            String spaceId,
            String businessId,
            String ownerId,
            String offeringName,
            String description,
            String bookingType,
            String pricingType,
            long pricePaise,
            String requiredResourceType,
            int defaultDurationMinutes,
            int minimumDurationMinutes,
            int maximumDurationMinutes,
            int bookingIntervalMinutes,
            int capacity,
            int minimumParticipants,
            int maximumParticipants,
            List<String> includedItems,
            List<String> rules,
            String status,
            boolean active,
            long createdAt,
            long updatedAt) {

        this.offeringId = offeringId;
        this.spaceId = spaceId;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.offeringName = offeringName;
        this.description = description;
        this.bookingType = bookingType;
        this.pricingType = pricingType;
        this.pricePaise = pricePaise;
        this.requiredResourceType = requiredResourceType;
        this.defaultDurationMinutes = defaultDurationMinutes;
        this.minimumDurationMinutes = minimumDurationMinutes;
        this.maximumDurationMinutes = maximumDurationMinutes;
        this.bookingIntervalMinutes = bookingIntervalMinutes;
        this.capacity = capacity;
        this.minimumParticipants = minimumParticipants;
        this.maximumParticipants = maximumParticipants;
        this.includedItems = includedItems != null ? includedItems : new ArrayList<>();
        this.rules = rules != null ? rules : new ArrayList<>();
        this.status = status != null ? status : OfferingStatus.DRAFT;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    // Delegating getter/setter for legacy code
    public String getId() {
        return offeringId;
    }

    public void setId(String id) {
        this.offeringId = id;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOfferingName() {
        return offeringName;
    }

    public void setOfferingName(String offeringName) {
        this.offeringName = offeringName;
    }

    // Delegating getter/setter for legacy code
    public String getName() {
        return offeringName;
    }

    public void setName(String name) {
        this.offeringName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }

    public long getPricePaise() {
        return pricePaise;
    }

    public void setPricePaise(long pricePaise) {
        this.pricePaise = pricePaise;
    }

    public String getPriceRupeesString() {
        return BigDecimal.valueOf(pricePaise, 2).toPlainString();
    }

    public double getPriceRupeesDouble() {
        return BigDecimal.valueOf(pricePaise, 2).doubleValue();
    }

    // Legacy compatibility getters for existing user view callers
    public double getPrice() {
        return getPriceRupeesDouble();
    }

    public String getPricingUnit() {
        return pricingType != null ? pricingType : "";
    }

    public String getRequiredResourceType() {
        return requiredResourceType;
    }

    public void setRequiredResourceType(String requiredResourceType) {
        this.requiredResourceType = requiredResourceType;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    public void setDefaultDurationMinutes(int defaultDurationMinutes) {
        this.defaultDurationMinutes = defaultDurationMinutes;
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

    public int getBookingIntervalMinutes() {
        return bookingIntervalMinutes;
    }

    public void setBookingIntervalMinutes(int bookingIntervalMinutes) {
        this.bookingIntervalMinutes = bookingIntervalMinutes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMinimumParticipants() {
        return minimumParticipants;
    }

    public void setMinimumParticipants(int minimumParticipants) {
        this.minimumParticipants = minimumParticipants;
    }

    public int getMaximumParticipants() {
        return maximumParticipants;
    }

    public void setMaximumParticipants(int maximumParticipants) {
        this.maximumParticipants = maximumParticipants;
    }

    public List<String> getIncludedItems() {
        return includedItems != null ? includedItems : new ArrayList<>();
    }

    public void setIncludedItems(List<String> includedItems) {
        this.includedItems = includedItems != null ? includedItems : new ArrayList<>();
    }

    public List<String> getRules() {
        return rules != null ? rules : new ArrayList<>();
    }

    public void setRules(List<String> rules) {
        this.rules = rules != null ? rules : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}