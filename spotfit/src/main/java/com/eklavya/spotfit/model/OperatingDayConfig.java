package com.eklavya.spotfit.model;

import java.util.Objects;

/**
 * Value model representing operating hours configuration for a specific weekday.
 * Canonical time contract: 24-hour HH:mm format (e.g., "08:00", "20:00").
 */
public class OperatingDayConfig {

    private String openingTime;
    private String closingTime;
    private boolean active;

    public OperatingDayConfig() {
        this.openingTime = "08:00";
        this.closingTime = "20:00";
        this.active = true;
    }

    public OperatingDayConfig(String openingTime, String closingTime, boolean active) {
        this.openingTime = openingTime != null && !openingTime.isBlank() ? openingTime.trim() : "08:00";
        this.closingTime = closingTime != null && !closingTime.isBlank() ? closingTime.trim() : "20:00";
        this.active = active;
    }

    public OperatingDayConfig copy() {
        return new OperatingDayConfig(openingTime, closingTime, active);
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime != null && !openingTime.isBlank() ? openingTime.trim() : "08:00";
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime != null && !closingTime.isBlank() ? closingTime.trim() : "20:00";
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatingDayConfig that = (OperatingDayConfig) o;
        return active == that.active &&
                Objects.equals(openingTime, that.openingTime) &&
                Objects.equals(closingTime, that.closingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(openingTime, closingTime, active);
    }
}
