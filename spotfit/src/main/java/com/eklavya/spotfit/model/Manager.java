package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Space Manager in SpotFit.
 * Path: /managers/{managerId}
 */
public class Manager {

    private String managerId;
    private String ownerId;

    private String name;
    private String email;
    private String phone;
    private String role;

    private List<String> assignedSpaceIds = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    private boolean emailVerified;
    private boolean profileCompleted;
    private boolean active;

    private long createdAt;
    private long updatedAt;

    public Manager() {
        this.role = UserRole.MANAGER;
        this.active = true;
        this.emailVerified = false;
        this.profileCompleted = true;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Manager(String managerId, String ownerId, String name, String email, String phone,
                   List<String> assignedSpaceIds, List<String> permissions,
                   boolean emailVerified, boolean profileCompleted, boolean active,
                   long createdAt, long updatedAt) {
        this.managerId = managerId;
        this.ownerId = ownerId;
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : "";
        this.phone = phone;
        this.role = UserRole.MANAGER;
        this.assignedSpaceIds = assignedSpaceIds != null ? new ArrayList<>(assignedSpaceIds) : new ArrayList<>();
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
        this.emailVerified = emailVerified;
        this.profileCompleted = profileCompleted;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Manager(String managerId, String spaceId, String name, String email, String phone, String role, String status) {
        this.managerId = managerId;
        this.ownerId = "";
        this.assignedSpaceIds = (spaceId != null && !spaceId.isBlank()) ? List.of(spaceId) : new ArrayList<>();
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : "";
        this.phone = phone;
        this.role = UserRole.MANAGER;
        this.active = "ACTIVE".equalsIgnoreCase(status);
        this.emailVerified = true;
        this.profileCompleted = true;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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
        this.email = email != null ? email.trim().toLowerCase() : "";
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
        this.role = UserRole.MANAGER; // Always force MANAGER role
    }

    public List<String> getAssignedSpaceIds() {
        return assignedSpaceIds;
    }

    public void setAssignedSpaceIds(List<String> assignedSpaceIds) {
        this.assignedSpaceIds = assignedSpaceIds != null ? new ArrayList<>(assignedSpaceIds) : new ArrayList<>();
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
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

    public String getSpaceId() {
        return (assignedSpaceIds != null && !assignedSpaceIds.isEmpty()) ? assignedSpaceIds.get(0) : "";
    }

    public String getStatus() {
        return active ? "ACTIVE" : "DEACTIVATED";
    }

    public void setStatus(String status) {
        this.active = "ACTIVE".equalsIgnoreCase(status);
    }
}
