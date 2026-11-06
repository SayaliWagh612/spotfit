package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.dao.WorkshopSessionDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class WorkshopSessionServiceTest {

    private WorkshopSessionService sessionService;
    private MockWorkshopSessionDao mockDao;

    @BeforeEach
    public void setUp() {
        mockDao = new MockWorkshopSessionDao();
        sessionService = new WorkshopSessionService(mockDao);
    }

    @Test
    @DisplayName("createSession rejects past start time or endAt <= startAt")
    void testCreateSessionInvalidTimes() {
        Space space = createSampleV2WorkshopSpace();
        long now = System.currentTimeMillis();

        WorkshopSession pastSession = new WorkshopSession("s1", space.getSpaceId(), now - 10000, now + 10000, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, now, now);
        OperationResult<WorkshopSession> res1 = sessionService.createSession(space, pastSession, "token").join();
        assertTrue(res1.isFailure());

        WorkshopSession invalidDuration = new WorkshopSession("s2", space.getSpaceId(), now + 10000, now + 5000, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, now, now);
        OperationResult<WorkshopSession> res2 = sessionService.createSession(space, invalidDuration, "token").join();
        assertTrue(res2.isFailure());
    }

    @Test
    @DisplayName("createSession rejects overlapping scheduled session")
    void testCreateSessionOverlap() {
        Space space = createSampleV2WorkshopSpace();
        long start1 = System.currentTimeMillis() + 100000;
        long end1 = start1 + 3600000;

        WorkshopSession existing = new WorkshopSession("s1", space.getSpaceId(), start1, end1, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        mockDao.storedSessions.add(existing);

        // Overlapping session (starts during existing)
        WorkshopSession overlapping = new WorkshopSession("s2", space.getSpaceId(), start1 + 1000, end1 + 1000, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        OperationResult<WorkshopSession> res = sessionService.createSession(space, overlapping, "token").join();

        assertTrue(res.isFailure());
        assertEquals("OVERLAPPING_SESSION", res.getErrorCode());
    }

    @Test
    @DisplayName("createSession succeeds for non-overlapping future session")
    void testCreateSessionSuccess() {
        Space space = createSampleV2WorkshopSpace();
        long start1 = System.currentTimeMillis() + 100000;
        long end1 = start1 + 3600000;

        WorkshopSession session = new WorkshopSession("s1", space.getSpaceId(), start1, end1, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        OperationResult<WorkshopSession> res = sessionService.createSession(space, session, "token").join();

        assertTrue(res.isSuccess());
        assertEquals(WorkshopSessionStatus.SCHEDULED, res.getData().getStatus());
        assertEquals(0, res.getData().getBookedCount());
    }

    private Space createSampleV2WorkshopSpace() {
        Space space = new Space();
        space.setSchemaVersion(2);
        space.setSpaceId("space-v2-workshop");
        space.setBookingType(BookingType.WORKSHOP);
        space.setBookingConfig(new WorkshopBookingConfig(100000L, 1, 20, 24, 24, "All ages", List.of(), "Instructor"));
        space.setOwnerId("owner1");
        return space;
    }

    private static class MockWorkshopSessionDao extends WorkshopSessionDao {
        final List<WorkshopSession> storedSessions = new ArrayList<>();

        @Override
        public CompletableFuture<OperationResult<WorkshopSession>> createSession(String spaceId, WorkshopSession session, String idToken) {
            storedSessions.add(session);
            return CompletableFuture.completedFuture(OperationResult.success(session));
        }

        @Override
        public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>(storedSessions)));
        }
    }
}
