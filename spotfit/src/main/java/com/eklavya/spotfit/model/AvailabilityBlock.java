package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.AvailabilityBlockStatus;

/**
 * Domain model representing a one-time Availability Block for a Resource.
 * Timestamps startAt and endAt are epoch milliseconds.
 */
public class AvailabilityBlock {

    private String blockId;
    private String spaceId;
    private String resourceId;
    private String ownerId;

    private long startAt;
    private long endAt;

    private String reason;
    private String status;

    private long createdAt;
    private long updatedAt;

    public AvailabilityBlock() {
        this.status = AvailabilityBlockStatus.ACTIVE;
    }

    public AvailabilityBlock(String blockId, String spaceId, String resourceId, String ownerId,
                             long startAt, long endAt, String reason, String status,
                             long createdAt, long updatedAt) {

        this.blockId = blockId;
        this.spaceId = spaceId;
        this.resourceId = resourceId;
        this.ownerId = ownerId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.reason = reason;
        this.status = status != null ? status.trim().toUpperCase() : AvailabilityBlockStatus.ACTIVE;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public long getStartAt() {
        return startAt;
    }

    public void setStartAt(long startAt) {
        this.startAt = startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim().toUpperCase() : AvailabilityBlockStatus.ACTIVE;
    }

    public boolean isActive() {
        return AvailabilityBlockStatus.ACTIVE.equalsIgnoreCase(this.status);
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
