package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Canonical User domain model for SpotFit customer profiles.
 */
public class User {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profileImageUrl;
    private String city;
    private String preferredLanguage;
    private List<String> personas;
    private List<String> interests;
    private long budgetPreferencePaise;
    private double preferredDistanceKm;
    private boolean notificationsEnabled;
    private boolean emailVerified;
    private boolean onboardingCompleted;
    private boolean active;
    private long createdAt;
    private long updatedAt;

    public User() {
        this.role = UserRole.USER;
        this.personas = new ArrayList<>();
        this.interests = new ArrayList<>();
        this.active = true;
        this.notificationsEnabled = true;
        this.emailVerified = false;
        this.onboardingCompleted = false;
    }

    public User(
            String userId,
            String name,
            String email,
            String phone,
            String role,
            String profileImageUrl,
            String city,
            String preferredLanguage,
            List<String> personas,
            List<String> interests,
            long budgetPreferencePaise,
            double preferredDistanceKm,
            boolean notificationsEnabled,
            boolean emailVerified,
            boolean onboardingCompleted,
            boolean active,
            long createdAt,
            long updatedAt) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = (role != null && !role.isBlank()) ? role : UserRole.USER;
        this.profileImageUrl = profileImageUrl;
        this.city = city;
        this.preferredLanguage = preferredLanguage;
        this.personas = (personas != null) ? new ArrayList<>(personas) : new ArrayList<>();
        this.interests = (interests != null) ? new ArrayList<>(interests) : new ArrayList<>();
        this.budgetPreferencePaise = budgetPreferencePaise;
        this.preferredDistanceKm = preferredDistanceKm;
        this.notificationsEnabled = notificationsEnabled;
        this.emailVerified = emailVerified;
        this.onboardingCompleted = onboardingCompleted;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User createNewUser(String userId, String name, String email, String phone) {
        long now = System.currentTimeMillis();
        User user = new User();
        user.setUserId((userId != null) ? userId.trim() : null);
        user.setName((name != null) ? name.trim() : null);
        user.setEmail((email != null) ? email.trim() : null);
        user.setPhone((phone != null) ? phone.trim() : null);
        user.setRole(UserRole.USER);
        user.setActive(true);
        user.setNotificationsEnabled(true);
        user.setEmailVerified(false);
        user.setOnboardingCompleted(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public List<String> getPersonas() {
        if (personas == null) {
            personas = new ArrayList<>();
        }
        return personas;
    }

    public void setPersonas(List<String> personas) {
        this.personas = (personas != null) ? new ArrayList<>(personas) : new ArrayList<>();
    }

    public List<String> getInterests() {
        if (interests == null) {
            interests = new ArrayList<>();
        }
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = (interests != null) ? new ArrayList<>(interests) : new ArrayList<>();
    }

    public long getBudgetPreferencePaise() {
        return budgetPreferencePaise;
    }

    public void setBudgetPreferencePaise(long budgetPreferencePaise) {
        this.budgetPreferencePaise = budgetPreferencePaise;
    }

    public double getPreferredDistanceKm() {
        return preferredDistanceKm;
    }

    public void setPreferredDistanceKm(double preferredDistanceKm) {
        this.preferredDistanceKm = preferredDistanceKm;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public boolean hasCompletedOnboarding() {
        return onboardingCompleted;
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
        return "User{userId='" + userId + '\''
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", role='" + role + '\''
                + ", active=" + active
                + ", onboardingCompleted=" + onboardingCompleted
                + ", emailVerified=" + emailVerified + '}';
    }
}
