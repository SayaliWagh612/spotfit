package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.ResourceStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing an individually usable physical unit inside a Space.
 */
public class Resource {

    private String resourceId;
    private String spaceId;
    private String businessId;
    private String ownerId;

    private String resourceName;
    private String description;
    private String resourceType;
    private String resourceCode;

    private int capacity;
    private List<String> equipment;
    private List<String> amenities;

    private String status;
    private boolean active;

    private long createdAt;
    private long updatedAt;

    public Resource() {
        this.equipment = new ArrayList<>();
        this.amenities = new ArrayList<>();
        this.status = ResourceStatus.AVAILABLE;
        this.active = true;
    }

    public Resource(String resourceId, String spaceId, String ownerId, String resourceName, String resourceCode, String resourceType) {
        this();
        this.resourceId = resourceId;
        this.spaceId = spaceId;
        this.ownerId = ownerId;
        this.resourceName = resourceName;
        this.resourceCode = resourceCode;
        this.resourceType = resourceType;
    }

    public Resource(
            String resourceId,
            String spaceId,
            String businessId,
            String ownerId,
            String resourceName,
            String description,
            String resourceType,
            String resourceCode,
            int capacity,
            List<String> equipment,
            List<String> amenities,
            String status,
            boolean active,
            long createdAt,
            long updatedAt) {

        this.resourceId = resourceId;
        this.spaceId = spaceId;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.resourceName = resourceName;
        this.description = description;
        this.resourceType = resourceType;
        this.resourceCode = resourceCode;
        this.capacity = capacity;
        this.equipment = equipment != null ? equipment : new ArrayList<>();
        this.amenities = amenities != null ? amenities : new ArrayList<>();
        this.status = status != null ? status : ResourceStatus.AVAILABLE;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    // Delegating getter/setter for legacy code
    public String getName() {
        return resourceName;
    }

    public void setName(String name) {
        this.resourceName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    // Delegating getter/setter for legacy code
    public String getType() {
        return resourceType;
    }

    public void setType(String type) {
        this.resourceType = type;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getEquipment() {
        return equipment != null ? equipment : new ArrayList<>();
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment != null ? equipment : new ArrayList<>();
    }

    public List<String> getAmenities() {
        return amenities != null ? amenities : new ArrayList<>();
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? amenities : new ArrayList<>();
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
