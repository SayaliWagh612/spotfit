package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.UserRole;

/**
 * Domain model representing a Space Owner in SpotFit.
 */
public class Owner {

    private String ownerId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profileImageUrl;
    private String city;
    private String preferredLanguage;
    private boolean emailVerified;
    private boolean profileCompleted;
    private boolean active;
    private long createdAt;
    private long updatedAt;

    public Owner() {
        this.role = UserRole.OWNER;
        this.active = true;
        this.emailVerified = false;
        this.profileCompleted = false;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Owner(String ownerId, String name, String email, String phone, String city) {
        this(ownerId, name, email, phone, UserRole.OWNER, "", city, false, false, true, System.currentTimeMillis(), System.currentTimeMillis());
    }

    public Owner(String ownerId, String name, String email, String phone, String role,
                 String profileImageUrl, String city, boolean emailVerified,
                 boolean profileCompleted, boolean active, long createdAt, long updatedAt) {
        this.ownerId = ownerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = (role != null && !role.isBlank()) ? role : UserRole.OWNER;
        this.profileImageUrl = profileImageUrl;
        this.city = city;
        this.emailVerified = emailVerified;
        this.profileCompleted = profileCompleted;
        this.active = active;
        this.createdAt = (createdAt > 0) ? createdAt : System.currentTimeMillis();
        this.updatedAt = (updatedAt > 0) ? updatedAt : System.currentTimeMillis();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPreferredLanguage() {
        return (preferredLanguage != null && !preferredLanguage.isBlank()) ? preferredLanguage : "English";
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public void setBusinessName(String businessName) {
    }

    public void setBusinessEmail(String businessEmail) {
    }

    public void setBusinessPhone(String businessPhone) {
        this.phone = businessPhone;
    }

    public void setBusinessAddress(String businessAddress) {
        this.city = businessAddress;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImageUrl = profileImagePath;
    }

    public String getBusinessName() {
        return (name != null && !name.isBlank()) ? name : "SpotFit Owner";
    }

    public String getBusinessEmail() {
        return email;
    }

    public String getBusinessPhone() {
        return phone;
    }

    public String getBusinessAddress() {
        return city;
    }

    public String getVerificationStatus() {
        return emailVerified ? "Verified" : "Pending Verification";
    }

    public String getAccountStatus() {
        return active ? "Active" : "Inactive";
    }

    public String getJoinedDate() {
        if (createdAt <= 0) {
            return "Recently";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.ENGLISH);
        return sdf.format(new java.util.Date(createdAt));
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
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

    @Override
    public String toString() {
        return "Owner{" +
                "ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", emailVerified=" + emailVerified +
                ", profileCompleted=" + profileCompleted +
                ", active=" + active +
                '}';
    }
}
