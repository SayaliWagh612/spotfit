package com.eklavya.spotfit.model;

import com.eklavya.spotfit.constants.SeatStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Model representing a Seat unit within a V2 SEAT Space.
 * Direct path: /spaces/{spaceId}/seats/{seatId}
 */
public class Seat {

    private String seatId;
    private String spaceId;
    private String seatName;
    private String seatCode;
    private String section;
    private String rowLabel;
    private int columnNumber;
    private int capacity;
    private long priceOverridePaise;
    private String status;
    private boolean active;
    private long createdAt;
    private long updatedAt;

    public Seat() {
        this.seatId = UUID.randomUUID().toString();
        this.spaceId = "";
        this.seatName = "";
        this.seatCode = "";
        this.section = "";
        this.rowLabel = "";
        this.columnNumber = 0;
        this.capacity = 1;
        this.priceOverridePaise = 0L;
        this.status = SeatStatus.AVAILABLE;
        this.active = true;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Seat(String seatId, String spaceId, String seatName, String seatCode,
                String section, String rowLabel, int columnNumber, int capacity,
                long priceOverridePaise, String status, boolean active,
                long createdAt, long updatedAt) {

        this.seatId = (seatId != null && !seatId.isBlank()) ? seatId.trim() : UUID.randomUUID().toString();
        this.spaceId = (spaceId != null) ? spaceId.trim() : "";
        this.seatName = (seatName != null) ? seatName.trim() : "";
        this.seatCode = (seatCode != null) ? seatCode.trim() : "";
        this.section = (section != null) ? section.trim() : "";
        this.rowLabel = (rowLabel != null) ? rowLabel.trim() : "";
        this.columnNumber = Math.max(0, columnNumber);
        this.capacity = Math.max(1, capacity);
        this.priceOverridePaise = Math.max(0L, priceOverridePaise);
        this.status = SeatStatus.isValid(status) ? status.trim().toUpperCase() : SeatStatus.AVAILABLE;
        this.active = active;
        long now = System.currentTimeMillis();
        this.createdAt = createdAt > 0 ? createdAt : now;
        this.updatedAt = updatedAt > 0 ? updatedAt : this.createdAt;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        if (seatId != null && !seatId.isBlank()) {
            this.seatId = seatId.trim();
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

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = (seatName != null) ? seatName.trim() : "";
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = (seatCode != null) ? seatCode.trim() : "";
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = (section != null) ? section.trim() : "";
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = (rowLabel != null) ? rowLabel.trim() : "";
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = Math.max(0, columnNumber);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
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
        if (SeatStatus.isValid(status)) {
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
        Seat seat = (Seat) o;
        return Objects.equals(seatId, seat.seatId) && Objects.equals(spaceId, seat.spaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, spaceId);
    }
}
