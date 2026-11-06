package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.WeekDay;

/**
 * Domain model representing a recurring weekly Availability Rule for a Resource.
 * Rule ID is deterministic and corresponds to the uppercase WeekDay (e.g. MONDAY).
 * Times are stored in strict HH:mm 24-hour format.
 */
public class AvailabilityRule {

    private String ruleId;
    private String spaceId;
    private String resourceId;
    private String ownerId;

    private String dayOfWeek;
    private String startTime;
    private String endTime;

    private int slotIntervalMinutes;
    private int minimumBookingMinutes;
    private int maximumBookingMinutes;

    private boolean active;

    private long createdAt;
    private long updatedAt;

    public AvailabilityRule() {
        this.slotIntervalMinutes = 60;
        this.minimumBookingMinutes = 60;
        this.maximumBookingMinutes = 240;
        this.active = true;
    }

    public AvailabilityRule(String ruleId, String spaceId, String resourceId, String ownerId,
                            String dayOfWeek, String startTime, String endTime,
                            int slotIntervalMinutes, int minimumBookingMinutes, int maximumBookingMinutes,
                            boolean active, long createdAt, long updatedAt) {

        this.dayOfWeek = dayOfWeek != null ? dayOfWeek.trim().toUpperCase() : null;
        this.ruleId = (ruleId != null && !ruleId.isBlank()) ? ruleId.trim().toUpperCase() : this.dayOfWeek;
        this.spaceId = spaceId;
        this.resourceId = resourceId;
        this.ownerId = ownerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotIntervalMinutes = slotIntervalMinutes;
        this.minimumBookingMinutes = minimumBookingMinutes;
        this.maximumBookingMinutes = maximumBookingMinutes;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek != null ? dayOfWeek.trim().toUpperCase() : null;
        if (this.ruleId == null || this.ruleId.isBlank()) {
            this.ruleId = this.dayOfWeek;
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getSlotIntervalMinutes() {
        return slotIntervalMinutes;
    }

    public void setSlotIntervalMinutes(int slotIntervalMinutes) {
        this.slotIntervalMinutes = slotIntervalMinutes;
    }

    public int getMinimumBookingMinutes() {
        return minimumBookingMinutes;
    }

    public void setMinimumBookingMinutes(int minimumBookingMinutes) {
        this.minimumBookingMinutes = minimumBookingMinutes;
    }

    public int getMaximumBookingMinutes() {
        return maximumBookingMinutes;
    }

    public void setMaximumBookingMinutes(int maximumBookingMinutes) {
        this.maximumBookingMinutes = maximumBookingMinutes;
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
