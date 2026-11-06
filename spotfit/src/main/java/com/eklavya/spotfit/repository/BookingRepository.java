package com.eklavya.spotfit.repository;

import com.eklavya.spotfit.model.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private static final List<Booking> bookings = new ArrayList<>();

    public static void save(Booking booking) {
        bookings.add(booking);
    }

    public static Booking findByVerificationCode(String code) {
        for (Booking booking : bookings) {
            if (booking.getVerificationCode().equals(code)) {
                return booking;
            }
        }
        return null;
    }

    public static List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
