package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import com.eklavya.spotfit.service.BookingService;
import com.eklavya.spotfit.session.AppSession;

public class OwnerBookingController {

    private final BookingService bookingService;

    public OwnerBookingController() {
        this.bookingService = new BookingService();
    }

    public OwnerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public CompletableFuture<List<Booking>> getBookings(String spaceIdFilter, String statusFilter, String searchQuery) {
        if (!AppSession.isAuthenticated() || !"OWNER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(List.of());
        }

        return bookingService.getOwnerBookings(AppSession.getUserId(), AppSession.getIdToken())
                .thenApply(res -> {
                    if (res.isFailure() || res.getData() == null) {
                        return List.of();
                    }
                    List<Booking> list = res.getData();

                    return list.stream()
                            .filter(b -> spaceIdFilter == null || spaceIdFilter.isBlank() || spaceIdFilter.equals(b.getSpaceId()))
                            .filter(b -> statusFilter == null || "All".equalsIgnoreCase(statusFilter) || statusFilter.equals(b.getStatus()))
                            .filter(b -> searchQuery == null || searchQuery.isBlank() ||
                                    b.getBookingId().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                    (b.getCustomerName() != null && b.getCustomerName().toLowerCase().contains(searchQuery.toLowerCase())) ||
                                    (b.getOfferingName() != null && b.getOfferingName().toLowerCase().contains(searchQuery.toLowerCase())))
                            .collect(Collectors.toList());
                });
    }

    public CompletableFuture<Booking> getBookingById(String bookingId) {
        if (!AppSession.isAuthenticated() || !"OWNER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(null);
        }
        return bookingService.getBooking(bookingId, AppSession.getIdToken())
                .thenApply(res -> {
                    if (res.isFailure() || res.getData() == null) {
                        return null;
                    }
                    Booking b = res.getData();
                    if (AppSession.getUserId().equals(b.getOwnerId())) {
                        return b;
                    }
                    return null;
                });
    }
}

