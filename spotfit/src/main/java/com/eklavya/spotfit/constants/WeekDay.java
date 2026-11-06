package com.eklavya.spotfit.constants;

import java.time.DayOfWeek;

/**
 * Controlled uppercase weekday constants for SpotFit Availability Rules.
 */
public final class WeekDay {

    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";

    private WeekDay() {
    }

    public static String normalizeDay(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim().toUpperCase();
        switch (trimmed) {
            case "MON":
            case "MONDAY":
                return MONDAY;
            case "TUE":
            case "TUES":
            case "TUESDAY":
                return TUESDAY;
            case "WED":
            case "WEDNESDAY":
                return WEDNESDAY;
            case "THU":
            case "THUR":
            case "THURS":
            case "THURSDAY":
                return THURSDAY;
            case "FRI":
            case "FRIDAY":
                return FRIDAY;
            case "SAT":
            case "SATURDAY":
                return SATURDAY;
            case "SUN":
            case "SUNDAY":
                return SUNDAY;
            default:
                return trimmed;
        }
    }

    public static boolean isValid(String day) {
        String normalized = normalizeDay(day);
        if (normalized == null) {
            return false;
        }
        return MONDAY.equals(normalized)
                || TUESDAY.equals(normalized)
                || WEDNESDAY.equals(normalized)
                || THURSDAY.equals(normalized)
                || FRIDAY.equals(normalized)
                || SATURDAY.equals(normalized)
                || SUNDAY.equals(normalized);
    }

    public static String fromDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek != null ? dayOfWeek.name() : null;
    }
}
