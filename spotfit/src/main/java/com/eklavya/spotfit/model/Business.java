package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.BusinessStatus;

/**
 * Domain model representing an Owner's organization in SpotFit.
 */
public class Business {

    private String businessId;
    private String ownerId;

    private String businessName;
    private String legalName;
    private String businessEmail;
    private String businessPhone;

    private String description;

    private String addressLine;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;

    private String registrationNumber;

    private String verificationStatus;
    private boolean active;

    private long submittedAt;
    private long reviewedAt;
    private String rejectionReason;

    private long createdAt;
    private long updatedAt;

    private int schemaVersion;
    private String category;
    private java.util.List<String> activityGroups;
    private String logoUrl;
    private String website;

    public Business() {
        this.schemaVersion = 1;
        this.category = "";
        this.activityGroups = new java.util.ArrayList<>();
        this.logoUrl = "";
        this.website = "";
        this.verificationStatus = BusinessStatus.DRAFT;
        this.active = true;
        this.submittedAt = 0L;
        this.reviewedAt = 0L;
        this.rejectionReason = "";
    }

    public Business(String businessId, String ownerId, String businessName, String legalName,
                    String businessEmail, String businessPhone, String description,
                    String addressLine, String landmark, String city, String state, String postalCode,
                    String registrationNumber, String verificationStatus, boolean active,
                    long createdAt, long updatedAt) {
        this(businessId, ownerId, businessName, legalName, businessEmail, businessPhone, description,
                addressLine, landmark, city, state, postalCode, registrationNumber, verificationStatus,
                active, 0L, 0L, "", createdAt, updatedAt);
    }

    public Business(String businessId, String ownerId, String businessName, String legalName,
                    String businessEmail, String businessPhone, String description,
                    String addressLine, String landmark, String city, String state, String postalCode,
                    String registrationNumber, String verificationStatus, boolean active,
                    long submittedAt, long reviewedAt, String rejectionReason,
                    long createdAt, long updatedAt) {
        this(1, businessId, ownerId, businessName, legalName, businessEmail, businessPhone, description,
                addressLine, landmark, city, state, postalCode, registrationNumber, "", new java.util.ArrayList<>(),
                "", "", verificationStatus, active, submittedAt, reviewedAt, rejectionReason, createdAt, updatedAt);
    }

    public Business(int schemaVersion, String businessId, String ownerId, String businessName, String legalName,
                    String businessEmail, String businessPhone, String description,
                    String addressLine, String landmark, String city, String state, String postalCode,
                    String registrationNumber, String category, java.util.List<String> activityGroups,
                    String logoUrl, String website, String verificationStatus, boolean active,
                    long submittedAt, long reviewedAt, String rejectionReason,
                    long createdAt, long updatedAt) {
        this.schemaVersion = schemaVersion;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.businessName = businessName;
        this.legalName = legalName;
        this.businessEmail = businessEmail;
        this.businessPhone = businessPhone;
        this.description = description;
        this.addressLine = addressLine;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.registrationNumber = registrationNumber;
        this.category = category != null ? category : "";
        this.activityGroups = activityGroups != null ? new java.util.ArrayList<>(activityGroups) : new java.util.ArrayList<>();
        this.logoUrl = logoUrl != null ? logoUrl : "";
        this.website = website != null ? website : "";
        this.verificationStatus = verificationStatus != null ? verificationStatus : BusinessStatus.DRAFT;
        this.active = active;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.rejectionReason = rejectionReason != null ? rejectionReason : "";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Business createV1(String businessId, String ownerId, String businessName, String legalName,
                                    String businessEmail, String businessPhone, String description,
                                    String addressLine, String landmark, String city, String state, String postalCode,
                                    String registrationNumber, String verificationStatus, boolean active,
                                    long submittedAt, long reviewedAt, String rejectionReason,
                                    long createdAt, long updatedAt) {
        return new Business(1, businessId, ownerId, businessName, legalName, businessEmail, businessPhone, description,
                addressLine, landmark, city, state, postalCode, registrationNumber, "", new java.util.ArrayList<>(),
                "", "", verificationStatus, active, submittedAt, reviewedAt, rejectionReason, createdAt, updatedAt);
    }

    public static Business createV2(String businessId, String ownerId, String businessName, String legalName,
                                    String businessEmail, String businessPhone, String description,
                                    String addressLine, String landmark, String city, String state, String postalCode,
                                    String registrationNumber, String category, java.util.List<String> activityGroups,
                                    String logoUrl, String website, String verificationStatus, boolean active,
                                    long submittedAt, long reviewedAt, String rejectionReason,
                                    long createdAt, long updatedAt) {
        return new Business(2, businessId, ownerId, businessName, legalName, businessEmail, businessPhone, description,
                addressLine, landmark, city, state, postalCode, registrationNumber, category, activityGroups,
                logoUrl, website, verificationStatus, active, submittedAt, reviewedAt, rejectionReason, createdAt, updatedAt);
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category != null ? category : "";
    }

    public java.util.List<String> getActivityGroups() {
        return activityGroups != null ? java.util.Collections.unmodifiableList(activityGroups) : java.util.Collections.emptyList();
    }

    public void setActivityGroups(java.util.List<String> activityGroups) {
        this.activityGroups = activityGroups != null ? new java.util.ArrayList<>(activityGroups) : new java.util.ArrayList<>();
    }

    public String getLogoUrl() {
        return logoUrl != null ? logoUrl : "";
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl != null ? logoUrl : "";
    }

    public String getWebsite() {
        return website != null ? website : "";
    }

    public void setWebsite(String website) {
        this.website = website != null ? website : "";
    }

    @Override
    public String toString() {
        return "Business{" +
                "businessId='" + businessId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", businessName='" + businessName + '\'' +
                ", city='" + city + '\'' +
                ", schemaVersion=" + schemaVersion +
                ", category='" + category + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", submittedAt=" + submittedAt +
                ", reviewedAt=" + reviewedAt +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", active=" + active +
                '}';
    }
}
