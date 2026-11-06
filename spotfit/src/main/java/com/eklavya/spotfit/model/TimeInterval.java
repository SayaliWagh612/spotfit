package com.eklavya.spotfit.model;

/**
 * Clean abstraction representing a time interval [startAt, endAt] in epoch milliseconds.
 * Serves as an extension point for slot generation to subtract unavailable periods
 * (such as Availability Blocks and future Bookings).
 */
public class TimeInterval {

    private final long startAt;
    private final long endAt;

    public TimeInterval(long startAt, long endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public long getStartAt() {
        return startAt;
    }

    public long getEndAt() {
        return endAt;
    }

    /**
     * Overlap condition: candidateStart < endAt AND candidateEnd > startAt.
     * Boundary-touching periods (candidateEnd == startAt or candidateStart == endAt) are NOT overlaps.
     */
    public boolean overlaps(long candidateStart, long candidateEnd) {
        return candidateStart < this.endAt && candidateEnd > this.startAt;
    }
}
