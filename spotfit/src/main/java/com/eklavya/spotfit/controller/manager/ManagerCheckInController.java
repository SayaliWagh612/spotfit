package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerCheckInController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }
    private String managerName() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "Space Manager" : m.getName(); }

    public List<Booking> getActiveCheckedInSessions() {
        return store.getBookingsForScope(assignedSpaceId()).stream()
                .filter(b -> "CHECKED_IN".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Booking> getUpcomingBookings() {
        return store.getBookingsForScope(assignedSpaceId()).stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public String verifyAndCheckIn(String bookingId, String otpCode) {
        if (bookingId == null || bookingId.isBlank() || otpCode == null || otpCode.isBlank()) {
            return "Error: Booking ID and 6-digit OTP/Code are required.";
        }

        Booking b = store.findBookingById(bookingId);
        if (b == null) {
            return "Error: Booking ID '" + bookingId + "' not found in system.";
        }

        if (!assignedSpaceId().equals(b.getSpaceId())) {
            return "Error: Booking ID belongs to a different space scope.";
        }

        if (!"SUCCESSFUL".equals(b.getPaymentStatus())) {
            return "Error: Payment for booking '" + bookingId + "' is " + b.getPaymentStatus() + ". Cannot check-in unpaid booking.";
        }

        if ("CHECKED_IN".equals(b.getStatus())) {
            return "Warning: Booking '" + bookingId + "' is ALREADY checked in.";
        }

        if ("COMPLETED".equals(b.getStatus())) {
            return "Warning: Booking '" + bookingId + "' is ALREADY completed.";
        }

        if ("CANCELLED".equals(b.getStatus())) {
            return "Error: Booking '" + bookingId + "' was CANCELLED.";
        }

        if (!otpCode.trim().equalsIgnoreCase(b.getOtpCode())) {
            return "Error: Invalid 6-digit OTP/Code entered for booking " + bookingId + ".";
        }

        // Perform Check-in
        b.setStatus("CHECKED_IN");
        b.setCheckInTime("Just now");

        // Update Resource status
        if (b.getResourceId() != null) {
            store.getResourcesForSpace(assignedSpaceId()).stream()
                    .filter(r -> r.getResourceId().equals(b.getResourceId()))
                    .findFirst()
                    .ifPresent(r -> r.setStatus("OCCUPIED"));
        }

        store.addActivityLog(assignedSpaceId(), "Space Manager", managerName(), "VERIFY_CHECKIN", "Successfully verified OTP and checked in booking " + bookingId + " (" + b.getCustomerName() + ")");
        return "SUCCESS: Checked in " + b.getCustomerName() + " (" + b.getOfferingName() + ")";
    }

    public boolean checkoutBooking(String bookingId) {
        Booking b = store.findBookingById(bookingId);
        if (b != null && assignedSpaceId().equals(b.getSpaceId()) && "CHECKED_IN".equals(b.getStatus())) {
            b.setStatus("COMPLETED");
            b.setCheckOutTime("Just now");

            if (b.getResourceId() != null) {
                store.getResourcesForSpace(assignedSpaceId()).stream()
                        .filter(r -> r.getResourceId().equals(b.getResourceId()))
                        .findFirst()
                        .ifPresent(r -> r.setStatus("AVAILABLE"));
            }

            store.addActivityLog(assignedSpaceId(), "Space Manager", managerName(), "CHECKOUT", "Checked out booking " + bookingId + " (" + b.getCustomerName() + ")");
            return true;
        }
        return false;
    }

    public String manualCheckInOverride(String bookingId, String overrideReason) {
        if (overrideReason == null || overrideReason.isBlank()) {
            return "Error: Reason for manual check-in override is required.";
        }

        Booking b = store.findBookingById(bookingId);
        if (b == null) {
            return "Error: Booking ID not found.";
        }

        if (!assignedSpaceId().equals(b.getSpaceId())) {
            return "Error: Booking belongs to a different space.";
        }

        b.setStatus("CHECKED_IN");
        b.setCheckInTime("Just now (Manual Override)");

        if (b.getResourceId() != null) {
            store.getResourcesForSpace(assignedSpaceId()).stream()
                    .filter(r -> r.getResourceId().equals(b.getResourceId()))
                    .findFirst()
                    .ifPresent(r -> r.setStatus("OCCUPIED"));
        }

        store.addActivityLog(assignedSpaceId(), "Space Manager", managerName(), "MANUAL_OVERRIDE_CHECKIN", "Manual check-in override for " + bookingId + ". Reason: " + overrideReason);
        return "SUCCESS: Manual override completed for " + b.getCustomerName();
    }
}

