package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SeatSelectionMode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.dao.SeatDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import com.eklavya.spotfit.model.bookingconfig.SlotBookingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SeatServiceTest {

    private SeatService seatService;
    private MockSeatDao mockSeatDao;

    @BeforeEach
    public void setUp() {
        mockSeatDao = new MockSeatDao();
        seatService = new SeatService(mockSeatDao);
    }

    @Test
    @DisplayName("validateSeatSpace rejects null space, non-v2 space, non-SEAT space, or non-SeatBookingConfig")
    void testValidateSeatSpace() {
        Space v1Space = new Space();
        v1Space.setSchemaVersion(1);
        v1Space.setBookingType(BookingType.SEAT);
        assertTrue(seatService.validateSeatSpace(v1Space).isFailure());

        Space slotSpace = createSampleV2Space(BookingType.SLOT, new SlotBookingConfig());
        assertTrue(seatService.validateSeatSpace(slotSpace).isFailure());

        Space validSpace = createSampleV2Space(BookingType.SEAT, new SeatBookingConfig());
        assertTrue(seatService.validateSeatSpace(validSpace).isSuccess());
    }

    @Test
    @DisplayName("createSeat rejects duplicate seat code in the same space")
    void testCreateSeatDuplicateCode() {
        Space space = createSampleV2Space(BookingType.SEAT, new SeatBookingConfig(SeatSelectionMode.SEAT_LIST, 60, 240, 30, 1, "UNIFORM", false, 30, 0));

        Seat seat1 = new Seat("seat1", space.getSpaceId(), "Seat 1", "A-1", "A", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        mockSeatDao.storedSeats.add(seat1);

        Seat seat2 = new Seat("seat2", space.getSpaceId(), "Seat 2", "A-1", "A", "A", 2, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        OperationResult<Seat> res = seatService.createSeat(space, seat2, "token").join();

        assertTrue(res.isFailure());
        assertEquals("DUPLICATE_SEAT_CODE", res.getErrorCode());
    }

    @Test
    @DisplayName("createSeat allows seat creation with unique code")
    void testCreateSeatSuccess() {
        Space space = createSampleV2Space(BookingType.SEAT, new SeatBookingConfig(SeatSelectionMode.SEAT_LIST, 60, 240, 30, 1, "UNIFORM", false, 30, 0));

        Seat seat1 = new Seat("seat1", space.getSpaceId(), "Seat 1", "A-1", "A", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        OperationResult<Seat> res = seatService.createSeat(space, seat1, "token").join();

        assertTrue(res.isSuccess());
        assertEquals("seat1", res.getData().getSeatId());
    }

    private Space createSampleV2Space(String bookingType, com.eklavya.spotfit.model.bookingconfig.BookingConfig bookingConfig) {
        Space space = new Space();
        space.setSchemaVersion(2);
        space.setSpaceId("space-v2-seat");
        space.setBookingType(bookingType);
        space.setBookingConfig(bookingConfig);
        space.setOwnerId("owner1");
        return space;
    }

    private static class MockSeatDao extends SeatDao {
        final List<Seat> storedSeats = new ArrayList<>();

        @Override
        public CompletableFuture<OperationResult<Seat>> createSeat(String spaceId, Seat seat, String idToken) {
            storedSeats.add(seat);
            return CompletableFuture.completedFuture(OperationResult.success(seat));
        }

        @Override
        public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(storedSeats)));
        }
    }
}
