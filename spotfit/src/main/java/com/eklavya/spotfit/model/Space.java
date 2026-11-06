package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.model.bookingconfig.BookingConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical domain model representing a physical Space in SpotFit.
 * A Space belongs to exactly one Business through businessId.
 */
public class Space {

    // --- Legacy V1 Fields ---
    private String spaceId;
    private String businessId;
    private String ownerId;

    private String spaceName;
    private String description;

    private String category;
    private String activityGroup;
    private String spaceType;

    private String addressLine;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;

    private int capacity;

    private List<String> amenities;
    private List<String> suitableActivities;
    private List<String> rules;

    private String openingTime;
    private String closingTime;
    private List<String> operatingDays;
    private String timeZoneId;

    private String status;
    private boolean active;

    private long submittedAt;
    private long reviewedAt;
    private String rejectionReason;
    private long publishedAt;

    private long createdAt;
    private long updatedAt;

    // --- Additive Owner V2 Fields ---
    private int schemaVersion;
    private String activityType;
    private SpaceLocation location;
    private String coverPhotoUrl;
    private List<String> photoUrls;
    private List<String> facilities;
    private String bookingType;
    private long basePricePaise;
    private String pricingUnit;
    private BookingConfig bookingConfig;
    private Map<String, OperatingDayConfig> operatingHours;
    private String cancellationPolicy;
    private String approvalStatus;
    private String operationalStatus;
    private long approvedAt;
    private boolean parentBusinessMarketplaceEligible;

    public static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    public Space() {
        // V1 Initializations
        this.amenities = new ArrayList<>();
        this.suitableActivities = new ArrayList<>();
        this.rules = new ArrayList<>();
        this.operatingDays = new ArrayList<>();
        this.timeZoneId = DEFAULT_TIMEZONE;
        this.status = SpaceStatus.DRAFT;
        this.active = true;
        this.submittedAt = 0L;
        this.reviewedAt = 0L;
        this.rejectionReason = "";
        this.publishedAt = 0L;

        // V2 Defaults (Legacy V1 compatibility)
        this.schemaVersion = 1;
        this.activityType = "";
        this.location = new SpaceLocation();
        this.coverPhotoUrl = "";
        this.photoUrls = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.bookingType = "";
        this.basePricePaise = 0L;
        this.pricingUnit = "";
        this.bookingConfig = null;
        this.operatingHours = new LinkedHashMap<>();
        this.cancellationPolicy = "";
        this.approvalStatus = "";
        this.operationalStatus = "";
        this.approvedAt = 0L;
        this.parentBusinessMarketplaceEligible = false;
    }

    public Space(String spaceId, String businessId, String ownerId, String spaceName, String description,
                 String category, String activityGroup, String spaceType,
                 String addressLine, String landmark, String city, String state, String postalCode,
                 int capacity, List<String> amenities, List<String> suitableActivities, List<String> rules,
                 String openingTime, String closingTime, List<String> operatingDays, String timeZoneId,
                 String status, boolean active, long createdAt, long updatedAt) {

        this(spaceId, businessId, ownerId, spaceName, description, category, activityGroup, spaceType,
                addressLine, landmark, city, state, postalCode, capacity, amenities, suitableActivities, rules,
                openingTime, closingTime, operatingDays, timeZoneId, status, active,
                0L, 0L, "", 0L, createdAt, updatedAt);
    }

    public Space(String spaceId, String businessId, String ownerId, String spaceName, String description,
                 String category, String activityGroup, String spaceType,
                 String addressLine, String landmark, String city, String state, String postalCode,
                 int capacity, List<String> amenities, List<String> suitableActivities, List<String> rules,
                 String openingTime, String closingTime, List<String> operatingDays, String timeZoneId,
                 String status, boolean active,
                 long submittedAt, long reviewedAt, String rejectionReason, long publishedAt,
                 long createdAt, long updatedAt) {

        this();
        this.spaceId = spaceId;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.spaceName = spaceName;
        this.description = description;
        this.category = category;
        this.activityGroup = activityGroup;
        this.spaceType = spaceType;
        this.addressLine = addressLine;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.capacity = capacity;
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
        this.suitableActivities = suitableActivities != null ? new ArrayList<>(suitableActivities) : new ArrayList<>();
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.operatingDays = operatingDays != null ? new ArrayList<>(operatingDays) : new ArrayList<>();
        this.timeZoneId = sanitizeTimeZone(timeZoneId);
        this.status = status != null ? status : SpaceStatus.DRAFT;
        this.active = active;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.rejectionReason = rejectionReason != null ? rejectionReason : "";
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.schemaVersion = 1;
    }

    public Space(String spaceId, String businessId, String ownerId, String spaceName, String description,
                 String category, String activityGroup, String spaceType,
                 String addressLine, String landmark, String city, String state, String postalCode,
                 int capacity, List<String> amenities, List<String> suitableActivities, List<String> rules,
                 String openingTime, String closingTime, List<String> operatingDays,
                 String status, boolean active, long createdAt, long updatedAt) {
        this(spaceId, businessId, ownerId, spaceName, description, category, activityGroup, spaceType,
             addressLine, landmark, city, state, postalCode, capacity, amenities, suitableActivities, rules,
             openingTime, closingTime, operatingDays, DEFAULT_TIMEZONE, status, active, createdAt, updatedAt);
    }

    public static String sanitizeTimeZone(String tz) {
        if (tz == null || tz.isBlank()) {
            return DEFAULT_TIMEZONE;
        }
        try {
            java.time.ZoneId.of(tz.trim());
            return tz.trim();
        } catch (Exception e) {
            return DEFAULT_TIMEZONE;
        }
    }

    // --- Named V2 Factory ---
    public static Space createV2(
            String spaceId, String businessId, String ownerId, String spaceName, String description,
            String category, String activityGroup, String activityType, SpaceLocation location,
            String coverPhotoUrl, List<String> photoUrls, List<String> amenities, List<String> facilities,
            int capacity, String bookingType, long basePricePaise, String pricingUnit,
            BookingConfig bookingConfig, Map<String, OperatingDayConfig> operatingHours,
            List<String> rules, String cancellationPolicy
    ) {
        Space space = new Space();
        space.setSchemaVersion(2);
        space.setSpaceId(spaceId);
        space.setBusinessId(businessId);
        space.setOwnerId(ownerId);
        space.setSpaceName(spaceName);
        space.setDescription(description);
        space.setCategory(category);
        space.setActivityGroup(activityGroup);
        space.setActivityType(activityType);
        space.setSpaceLocation(location);
        space.setCoverPhotoUrl(coverPhotoUrl);
        space.setPhotoUrls(photoUrls);
        space.setAmenities(amenities);
        space.setFacilities(facilities);
        space.setCapacity(capacity);
        space.setBookingType(bookingType);
        space.setBasePricePaise(basePricePaise);
        space.setPricingUnit(pricingUnit);
        space.setBookingConfig(bookingConfig);
        space.setOperatingHours(operatingHours);
        space.setRules(rules);
        space.setCancellationPolicy(cancellationPolicy);

        // Authoritative V2 lifecycle defaults
        space.setStatus(SpaceStatus.DRAFT);
        space.setApprovalStatus(SpaceApprovalStatus.DRAFT);
        space.setOperationalStatus(SpaceOperationalStatus.INACTIVE);
        space.setActive(true);
        space.setSubmittedAt(0L);
        space.setReviewedAt(0L);
        space.setRejectionReason("");
        space.setApprovedAt(0L);
        space.setPublishedAt(0L);
        space.setCreatedAt(System.currentTimeMillis());
        space.setUpdatedAt(space.getCreatedAt());
        space.setParentBusinessMarketplaceEligible(false);

        return space;
    }

    // --- Getters & Setters ---

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

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    // Alias for backward compatibility
    public String getName() {
        return spaceName;
    }

    public void setName(String name) {
        this.spaceName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActivityGroup() {
        return activityGroup;
    }

    public void setActivityGroup(String activityGroup) {
        this.activityGroup = activityGroup;
    }

    public String getActivityType() {
        if (activityType != null && !activityType.isBlank()) {
            return activityType;
        }
        return activityGroup != null ? activityGroup : "";
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType != null ? activityType : "";
        if (schemaVersion == 1 && (this.activityGroup == null || this.activityGroup.isBlank())) {
            this.activityGroup = this.activityType;
        }
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    // Alias for backward compatibility
    public String getAddress() {
        return addressLine;
    }

    public void setAddress(String address) {
        this.addressLine = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        if (amenities == null) {
            amenities = new ArrayList<>();
        }
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
    }

    public List<String> getSuitableActivities() {
        if (suitableActivities == null) {
            suitableActivities = new ArrayList<>();
        }
        return suitableActivities;
    }

    public void setSuitableActivities(List<String> suitableActivities) {
        this.suitableActivities = suitableActivities != null ? new ArrayList<>(suitableActivities) : new ArrayList<>();
    }

    public List<String> getRules() {
        if (rules == null) {
            rules = new ArrayList<>();
        }
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public List<String> getOperatingDays() {
        if (operatingDays == null) {
            operatingDays = new ArrayList<>();
        } else {
            List<String> normalized = new ArrayList<>();
            for (String d : operatingDays) {
                String norm = WeekDay.normalizeDay(d);
                if (norm != null && !normalized.contains(norm)) {
                    normalized.add(norm);
                }
            }
            operatingDays = normalized;
        }
        return operatingDays;
    }

    public void setOperatingDays(List<String> operatingDays) {
        this.operatingDays = new ArrayList<>();
        if (operatingDays != null) {
            for (String d : operatingDays) {
                String norm = WeekDay.normalizeDay(d);
                if (norm != null && !this.operatingDays.contains(norm)) {
                    this.operatingDays.add(norm);
                }
            }
        }
    }

    public String getTimeZoneId() {
        if (timeZoneId == null || timeZoneId.isBlank()) {
            timeZoneId = DEFAULT_TIMEZONE;
        }
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = sanitizeTimeZone(timeZoneId);
    }

    public String getOperatingDaysDisplay() {
        if (operatingDays == null || operatingDays.isEmpty()) {
            return "N/A";
        }
        return String.join(", ", operatingDays);
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

    // --- V2 Getters and Setters ---

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public SpaceLocation getSpaceLocation() {
        if (location == null) {
            location = new SpaceLocation();
        }
        return location.copy();
    }

    public void setSpaceLocation(SpaceLocation location) {
        if (location != null) {
            this.location = location.copy();
        } else {
            this.location = new SpaceLocation();
        }
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl != null ? coverPhotoUrl : "";
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl != null ? coverPhotoUrl : "";
    }

    public List<String> getPhotoUrls() {
        if (photoUrls == null) {
            photoUrls = new ArrayList<>();
        }
        return Collections.unmodifiableList(photoUrls);
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? new ArrayList<>(photoUrls) : new ArrayList<>();
    }

    public List<String> getFacilities() {
        if (facilities == null) {
            facilities = new ArrayList<>();
        }
        return Collections.unmodifiableList(facilities);
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities != null ? new ArrayList<>(facilities) : new ArrayList<>();
    }

    public String getBookingType() {
        return bookingType != null ? bookingType : "";
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType != null ? bookingType : "";
    }

    public long getBasePricePaise() {
        return basePricePaise;
    }

    public void setBasePricePaise(long basePricePaise) {
        this.basePricePaise = basePricePaise;
    }

    public String getPricingUnit() {
        if (pricingUnit != null && !pricingUnit.isBlank()) {
            return pricingUnit;
        }
        return "hour";
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit != null ? pricingUnit : "";
    }

    public BookingConfig getBookingConfig() {
        return bookingConfig != null ? bookingConfig.copy() : null;
    }

    public void setBookingConfig(BookingConfig bookingConfig) {
        this.bookingConfig = bookingConfig != null ? bookingConfig.copy() : null;
    }

    public Map<String, OperatingDayConfig> getOperatingHours() {
        if (operatingHours == null) {
            operatingHours = new LinkedHashMap<>();
        }
        Map<String, OperatingDayConfig> copyMap = new LinkedHashMap<>();
        for (Map.Entry<String, OperatingDayConfig> entry : operatingHours.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                copyMap.put(entry.getKey(), entry.getValue().copy());
            }
        }
        return Collections.unmodifiableMap(copyMap);
    }

    public void setOperatingHours(Map<String, OperatingDayConfig> operatingHours) {
        this.operatingHours = new LinkedHashMap<>();
        if (operatingHours != null) {
            for (Map.Entry<String, OperatingDayConfig> entry : operatingHours.entrySet()) {
                String normDay = WeekDay.normalizeDay(entry.getKey());
                if (normDay != null && entry.getValue() != null) {
                    this.operatingHours.put(normDay, entry.getValue().copy());
                }
            }
        }
    }

    public String getCancellationPolicy() {
        return cancellationPolicy != null ? cancellationPolicy : "";
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy != null ? cancellationPolicy : "";
    }

    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "";
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus != null ? approvalStatus : "";
    }

    public String getOperationalStatus() {
        return operationalStatus != null ? operationalStatus : "";
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus != null ? operationalStatus : "";
    }

    public long getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(long approvedAt) {
        this.approvedAt = approvedAt;
    }

    public boolean isParentBusinessMarketplaceEligible() {
        return parentBusinessMarketplaceEligible;
    }

    public void setParentBusinessMarketplaceEligible(boolean parentBusinessMarketplaceEligible) {
        this.parentBusinessMarketplaceEligible = parentBusinessMarketplaceEligible;
    }

    // --- Null-Safe V2 Helpers ---

    public String getBookingConfigType() {
        return bookingConfig != null ? bookingConfig.getBookingType() : "";
    }

    public boolean hasMatchingBookingConfig() {
        if (bookingConfig == null || bookingType == null || bookingType.isBlank()) {
            return false;
        }
        String configType = bookingConfig.getBookingType();
        if (configType == null || configType.isBlank() || BookingType.RESOURCE.equals(configType)) {
            return false;
        }
        return bookingType.equalsIgnoreCase(configType);
    }

    // Legacy mock constructors preserved for 100% backward compatibility
    public Space(String spaceId, String ownerId, String spaceName, String category, String activityGroup,
                 String spaceType, String description, String addressLine, String city, int capacity,
                 String email, String phone, String operatingDaysStr, String openingTime, String closingTime,
                 String houseRules, String cancellationPolicy, List<String> amenities, String status,
                 String managerId, double rating, int totalBookings, double revenue) {
        this();
        this.spaceId = spaceId;
        this.ownerId = ownerId;
        this.spaceName = spaceName;
        this.category = category;
        this.activityGroup = activityGroup;
        this.spaceType = spaceType;
        this.description = description;
        this.addressLine = addressLine;
        this.city = city;
        this.capacity = capacity;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        if (operatingDaysStr != null) {
            this.operatingDays = Arrays.asList(operatingDaysStr.split(","));
        }
        if (amenities != null) {
            this.amenities = new ArrayList<>(amenities);
        }
        if (houseRules != null && !houseRules.isBlank()) {
            this.rules = Arrays.asList(houseRules);
        }
        this.status = status != null ? status : SpaceStatus.DRAFT;
        this.active = true;
    }

    public Space(String name, String ratingStr, int reviewsCount, String location, String distance,
                 int price, String pricingUnit, String amenitiesStr, String imagePath, List<Offering> offerings) {
        this();
        this.spaceId = UUID.randomUUID().toString();
        this.spaceName = name;
        this.city = location;
        if (amenitiesStr != null) {
            this.amenities = Arrays.asList(amenitiesStr.split(" • "));
        }
        this.status = SpaceStatus.DRAFT;
        this.active = true;
    }

    // Helper compatibility getters for non-persisted user UI components
    public double getRating() {
        return 0.0;
    }

    public int getTotalBookings() {
        return 0;
    }

    public double getMonthlyRevenue() {
        return 0.0;
    }

    public String getImagePath() {
        return "/images/UserDashImg/Studio.png";
    }

    public String getLocation() {
        return city != null ? city : "";
    }

    public String getDistance() {
        return "2.0 km";
    }

    public double getPrice() {
        return 0.0;
    }

    public int getReviews() {
        return 0;
    }

    public void setActiveManagerId(String activeManagerId) {
        // Reserved for future Manager assignment step
    }

    public List<Offering> getOfferings() {
        return new ArrayList<>();
    }

    public long getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(long submittedAt) {
        this.submittedAt = submittedAt;
    }

    public long getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(long reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRejectionReason() {
        return rejectionReason != null ? rejectionReason : "";
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason != null ? rejectionReason : "";
    }

    public long getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(long publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return spaceName != null ? spaceName : "";
    }
}