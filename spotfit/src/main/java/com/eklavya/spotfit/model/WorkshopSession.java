package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.WorkshopSessionStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Model representing a Workshop Session instance within a V2 WORKSHOP Space.
 * Direct path: /spaces/{spaceId}/sessions/{sessionId}
 */
public class WorkshopSession {

    private String sessionId;
    private String spaceId;
    private long startAt;
    private long endAt;
    private int capacity;
    private int bookedCount;
    private long priceOverridePaise;
    private String status;
    private boolean active;
    private long createdAt;
    private long updatedAt;

    public WorkshopSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.spaceId = "";
        long now = System.currentTimeMillis();
        this.startAt = now + 86400000L; // Default tomorrow
        this.endAt = this.startAt + 7200000L; // Default +2 hours
        this.capacity = 10;
        this.bookedCount = 0;
        this.priceOverridePaise = 0L;
        this.status = WorkshopSessionStatus.SCHEDULED;
        this.active = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public WorkshopSession(String sessionId, String spaceId, long startAt, long endAt,
                           int capacity, int bookedCount, long priceOverridePaise,
                           String status, boolean active, long createdAt, long updatedAt) {

        this.sessionId = (sessionId != null && !sessionId.isBlank()) ? sessionId.trim() : UUID.randomUUID().toString();
        this.spaceId = (spaceId != null) ? spaceId.trim() : "";
        this.startAt = startAt;
        this.endAt = endAt;
        this.capacity = Math.max(1, capacity);
        this.bookedCount = Math.max(0, bookedCount);
        this.priceOverridePaise = Math.max(0L, priceOverridePaise);
        this.status = WorkshopSessionStatus.isValid(status) ? status.trim().toUpperCase() : WorkshopSessionStatus.SCHEDULED;
        this.active = active;
        long now = System.currentTimeMillis();
        this.createdAt = createdAt > 0 ? createdAt : now;
        this.updatedAt = updatedAt > 0 ? updatedAt : this.createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            this.sessionId = sessionId.trim();
        }
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        if (spaceId != null) {
            this.spaceId = spaceId.trim();
        }
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = Math.max(0, bookedCount);
    }

    public long getPriceOverridePaise() {
        return priceOverridePaise;
    }

    public void setPriceOverridePaise(long priceOverridePaise) {
        this.priceOverridePaise = Math.max(0L, priceOverridePaise);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (WorkshopSessionStatus.isValid(status)) {
            this.status = status.trim().toUpperCase();
        }
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
        if (createdAt > 0) {
            this.createdAt = createdAt;
        }
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        if (updatedAt > 0) {
            this.updatedAt = updatedAt;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkshopSession that = (WorkshopSession) o;
        return Objects.equals(sessionId, that.sessionId) && Objects.equals(spaceId, that.spaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, spaceId);
    }
}
