package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.constants.BookingStatus;
import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.service.BookingService;
import com.eklavya.spotfit.service.ManagerService;
import com.eklavya.spotfit.service.ManagerAuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerBookingOperationsTest {

    private ManagerBookingController controller;
    private String testManagerId;
    private String testSpaceId;
    private List<Booking> mockBookings;

    @BeforeEach
    void setUp() throws Exception {
        testManagerId = "mgr_test_1";
        testSpaceId = "space_test_1";
        mockBookings = new ArrayList<>();

        Manager manager = new Manager();
        manager.setManagerId(testManagerId);
        manager.setOwnerId("test_owner_id");
        manager.setAssignedSpaceIds(List.of(testSpaceId));
        manager.setPermissions(List.of(ManagerPermission.CHECK_IN_OUT));
        manager.setActive(true);

        ManagerService mockManagerService = new ManagerService() {
            @Override
            public CompletableFuture<OperationResult<Manager>> getManagerProfile(String managerId, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(manager));
            }
        };

        BookingService mockBookingService = new BookingService() {
            @Override
            public CompletableFuture<OperationResult<List<Booking>>> getManagerBookings(String spaceId, String idToken) {
                return CompletableFuture.completedFuture(OperationResult.success(mockBookings));
            }

            @Override
            public CompletableFuture<OperationResult<Booking>> checkInBooking(String bookingId, String managerId, String idToken) {
                for (Booking b : mockBookings) {
                    if (b.getBookingId().equals(bookingId)) {
                        if (BookingStatus.PENDING_PAYMENT.equals(b.getStatus()) || !"SUCCESS".equals(b.getPaymentStatus())) {
                            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_STATE", "Booking cannot be checked in without verified payment."));
                        }
                        b.setStatus(BookingStatus.CHECKED_IN);
                        b.setCheckInTime(Instant.now().atZone(ZoneId.systemDefault()).toString());
                        return CompletableFuture.completedFuture(OperationResult.success(b));
                    }
                }
                return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Booking not found"));
            }

            @Override
            public CompletableFuture<OperationResult<Booking>> checkOutBooking(String bookingId, String managerId, String idToken) {
                for (Booking b : mockBookings) {
                    if (b.getBookingId().equals(bookingId)) {
                        if (!BookingStatus.CHECKED_IN.equals(b.getStatus())) {
                            return CompletableFuture.completedFuture(OperationResult.failure("INVALID_STATE", "Booking must be CHECKED_IN to be checked out."));
                        }
                        b.setStatus(BookingStatus.COMPLETED);
                        b.setCheckOutTime(Instant.now().atZone(ZoneId.systemDefault()).toString());
                        return CompletableFuture.completedFuture(OperationResult.success(b));
                    }
                }
                return CompletableFuture.completedFuture(OperationResult.failure("NOT_FOUND", "Booking not found"));
            }
        };

        ManagerAuthorizationService mockAuthService = new ManagerAuthorizationService();

        controller = new ManagerBookingController(mockBookingService, mockManagerService, mockAuthService);

        AppSession.startSession(testManagerId, "test@manager.com", "Manager", "MANAGER", "mock-token", "mock-refresh", 3600);
    }

    @AfterEach
    void tearDown() {
        AppSession.clearSession();
    }

    private Booking createTestBooking(String status, String paymentStatus) {
        Booking booking = new Booking();
        booking.setSpaceId(testSpaceId);
        booking.setOwnerId("test_owner_id");
        booking.setCustomerId("test_customer_id");
        booking.setStatus(status);
        booking.setPaymentStatus(paymentStatus);
        mockBookings.add(booking);
        return booking;
    }

    @Test
    void testManagerQueryIncludesAssignedSpaceId() throws Exception {
        Booking b1 = createTestBooking(BookingStatus.CONFIRMED, "SUCCESS");
        
        List<Booking> bookings = controller.getBookings(null, null, null).get(10, TimeUnit.SECONDS);
        
        assertNotNull(bookings);
        assertTrue(bookings.stream().anyMatch(b -> b.getBookingId().equals(b1.getBookingId())));
    }

    @Test
    void testPendingPaymentCannotCheckIn() throws Exception {
        Booking b1 = createTestBooking(BookingStatus.PENDING_PAYMENT, "PENDING");

        OperationResult<Booking> res = controller.checkIn(b1.getBookingId(), testSpaceId).get(10, TimeUnit.SECONDS);
        assertFalse(res.isSuccess());
        assertTrue(res.getMessage().contains("without verified payment") || res.getMessage().contains("invalid state") || res.getMessage().contains("verified"));
    }

    @Test
    void testConfirmedSuccessCanCheckIn() throws Exception {
        Booking b1 = createTestBooking(BookingStatus.CONFIRMED, "SUCCESS");

        OperationResult<Booking> res = controller.checkIn(b1.getBookingId(), testSpaceId).get(10, TimeUnit.SECONDS);
        assertTrue(res.isSuccess());
        assertEquals(BookingStatus.CHECKED_IN, res.getData().getStatus());
        assertNotNull(res.getData().getCheckInTime());
    }

    @Test
    void testCheckedInCanCheckOut() throws Exception {
        Booking b1 = createTestBooking(BookingStatus.CHECKED_IN, "SUCCESS");

        OperationResult<Booking> res = controller.checkOut(b1.getBookingId(), testSpaceId).get(10, TimeUnit.SECONDS);
        assertTrue(res.isSuccess());
        assertEquals(BookingStatus.COMPLETED, res.getData().getStatus());
        assertNotNull(res.getData().getCheckOutTime());
    }
}
