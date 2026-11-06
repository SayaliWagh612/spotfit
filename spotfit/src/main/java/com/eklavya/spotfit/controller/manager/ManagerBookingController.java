package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.service.BookingService;
import com.eklavya.spotfit.service.ManagerAuthorizationService;
import com.eklavya.spotfit.service.ManagerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ManagerBookingController {

    private final BookingService bookingService;
    private final ManagerService managerService;
    private final ManagerAuthorizationService authService;
    public ManagerBookingController() {
        this.bookingService = new BookingService();
        this.managerService = new ManagerService();
        this.authService = new ManagerAuthorizationService();
    }

    public ManagerBookingController(BookingService bookingService, ManagerService managerService, ManagerAuthorizationService authService) {
        this.bookingService = bookingService;
        this.managerService = managerService;
        this.authService = authService;
    }

    public CompletableFuture<List<Booking>> getBookings(String spaceIdFilter, String statusFilter, String searchQuery) {
        if (!AppSession.isAuthenticated() || !"MANAGER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(List.of());
        }

        return managerService.getManagerProfile(AppSession.getUserId(), AppSession.getIdToken())
                .thenCompose(res -> {
                    if (res.isFailure() || res.getData() == null) {
                        return CompletableFuture.completedFuture(List.of());
                    }
                    Manager manager = res.getData();
                    if (!manager.isActive() || manager.getAssignedSpaceIds() == null || manager.getAssignedSpaceIds().isEmpty()) {
                        return CompletableFuture.completedFuture(List.of());
                    }

                    List<String> spacesToFetch = manager.getAssignedSpaceIds();
                    if (spaceIdFilter != null && !spaceIdFilter.isBlank()) {
                        if (spacesToFetch.contains(spaceIdFilter)) {
                            spacesToFetch = List.of(spaceIdFilter);
                        } else {
                            return CompletableFuture.completedFuture(List.of());
                        }
                    }

                    List<CompletableFuture<List<Booking>>> futures = new ArrayList<>();
                    for (String spaceId : spacesToFetch) {
                        futures.add(bookingService.getManagerBookings(spaceId, AppSession.getIdToken())
                                .thenApply(bRes -> bRes.isSuccess() && bRes.getData() != null ? bRes.getData() : List.<Booking>of()));
                    }

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> futures.stream()
                                    .flatMap(f -> f.join().stream())
                                    .filter(b -> statusFilter == null || "All".equalsIgnoreCase(statusFilter) || statusFilter.equals(b.getStatus()))
                                    .filter(b -> searchQuery == null || searchQuery.isBlank() ||
                                            b.getBookingId().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                            (b.getCustomerName() != null && b.getCustomerName().toLowerCase().contains(searchQuery.toLowerCase())) ||
                                            (b.getOfferingName() != null && b.getOfferingName().toLowerCase().contains(searchQuery.toLowerCase())))
                                    .collect(Collectors.toList()));
                });
    }

    public CompletableFuture<OperationResult<Booking>> checkIn(String bookingId, String spaceId) {
        if (!AppSession.isAuthenticated() || !"MANAGER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(OperationResult.failure("UNAUTHORIZED", "Authentication required."));
        }
        
        return managerService.getManagerProfile(AppSession.getUserId(), AppSession.getIdToken())
            .thenCompose(res -> {
                if (res.isFailure() || res.getData() == null) return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Manager not found."));
                Manager manager = res.getData();
                
                OperationResult<Void> authRes = authService.authorizePermission(manager, spaceId, com.eklavya.spotfit.constants.ManagerPermission.CHECK_IN_OUT);
                if (authRes.isFailure()) {
                    return CompletableFuture.completedFuture(OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
                }

                return bookingService.checkInBooking(bookingId, manager.getManagerId(), AppSession.getIdToken());
            });
    }

    public CompletableFuture<OperationResult<Booking>> checkOut(String bookingId, String spaceId) {
        if (!AppSession.isAuthenticated() || !"MANAGER".equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(OperationResult.failure("UNAUTHORIZED", "Authentication required."));
        }
        
        return managerService.getManagerProfile(AppSession.getUserId(), AppSession.getIdToken())
            .thenCompose(res -> {
                if (res.isFailure() || res.getData() == null) return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Manager not found."));
                Manager manager = res.getData();
                
                OperationResult<Void> authRes = authService.authorizePermission(manager, spaceId, com.eklavya.spotfit.constants.ManagerPermission.CHECK_IN_OUT);
                if (authRes.isFailure()) {
                    return CompletableFuture.completedFuture(OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
                }

                return bookingService.checkOutBooking(bookingId, manager.getManagerId(), AppSession.getIdToken());
            });
    }
}
