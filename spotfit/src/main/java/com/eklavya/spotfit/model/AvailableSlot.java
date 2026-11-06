package com.eklavya.spotfit.model;

/**
 * Computed in-memory model representing a single calculated Available Slot.
 * Never persisted to Firestore.
 */
public class AvailableSlot {

    private String spaceId;
    private String resourceId;

    private long startAt;
    private long endAt;

    private String localStartTime;
    private String localEndTime;

    private boolean available;

    public AvailableSlot() {
        this.available = true;
    }

    public AvailableSlot(String spaceId, String resourceId, long startAt, long endAt,
                         String localStartTime, String localEndTime, boolean available) {

        this.spaceId = spaceId;
        this.resourceId = resourceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.localStartTime = localStartTime;
        this.localEndTime = localEndTime;
        this.available = available;
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

    public String getLocalStartTime() {
        return localStartTime;
    }

    public void setLocalStartTime(String localStartTime) {
        this.localStartTime = localStartTime;
    }

    public String getLocalEndTime() {
        return localEndTime;
    }

    public void setLocalEndTime(String localEndTime) {
        this.localEndTime = localEndTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
