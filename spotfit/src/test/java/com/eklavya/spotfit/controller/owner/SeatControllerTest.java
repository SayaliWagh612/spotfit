package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.SeatDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import com.eklavya.spotfit.service.SeatService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SeatControllerTest {

    private SeatController controller;
    private MockSpaceDao mockSpaceDao;
    private MockSeatService mockSeatService;

    @BeforeEach
    public void setUp() {
        mockSpaceDao = new MockSpaceDao();
        mockSeatService = new MockSeatService();
        controller = new SeatController(mockSeatService, mockSpaceDao);
        AppSession.startSession("owner1", "owner@spotfit.com", "Owner 1", UserRole.OWNER, "mock-token", "mock-refresh", 3600);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    @DisplayName("createSeat denies write when Space approvalStatus is SUBMITTED")
    void testCreateSeatLifecycleWriteDeniedWhenSubmitted() {
        Space space = createSampleSpace("SUBMITTED");
        mockSpaceDao.spaceToReturn = space;

        Seat seat = new Seat("s1", space.getSpaceId(), "Seat 1", "A-1", "A", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        OperationResult<Seat> res = controller.createSeat(space.getSpaceId(), seat).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.LIFECYCLE_WRITE_DENIED, res.getErrorCode());
    }

    @Test
    @DisplayName("createSeat allows write when Space approvalStatus is DRAFT")
    void testCreateSeatAllowedWhenDraft() {
        Space space = createSampleSpace("DRAFT");
        mockSpaceDao.spaceToReturn = space;

        Seat seat = new Seat("s1", space.getSpaceId(), "Seat 1", "A-1", "A", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        OperationResult<Seat> res = controller.createSeat(space.getSpaceId(), seat).join();

        assertTrue(res.isSuccess());
    }

    @Test
    @DisplayName("getSeatsForSpace allows read when Space approvalStatus is SUBMITTED")
    void testGetSeatsAllowedWhenSubmitted() {
        Space space = createSampleSpace("SUBMITTED");
        mockSpaceDao.spaceToReturn = space;

        OperationResult<List<Seat>> res = controller.getSeatsForSpace(space.getSpaceId()).join();

        assertTrue(res.isSuccess());
    }

    private Space createSampleSpace(String approvalStatus) {
        Space space = new Space();
        space.setSchemaVersion(2);
        space.setSpaceId("space-seat-1");
        space.setOwnerId("owner1");
        space.setBookingType(BookingType.SEAT);
        space.setBookingConfig(new SeatBookingConfig());
        space.setApprovalStatus(approvalStatus);
        return space;
    }

    private static class MockSpaceDao extends SpaceDao {
        Space spaceToReturn;

        @Override
        public CompletableFuture<OperationResult<Space>> getSpace(String spaceId, String idToken) {
            if (spaceToReturn != null && spaceToReturn.getSpaceId().equals(spaceId)) {
                return CompletableFuture.completedFuture(OperationResult.success(spaceToReturn));
            }
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.NOT_FOUND, "Not found"));
        }
    }

    private static class MockSeatService extends SeatService {
        @Override
        public CompletableFuture<OperationResult<Seat>> createSeat(Space space, Seat seat, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(seat));
        }

        @Override
        public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
        }
    }
}
