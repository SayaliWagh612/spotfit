package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.dao.WorkshopSessionDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;
import com.eklavya.spotfit.service.WorkshopSessionService;
import com.eklavya.spotfit.session.AppSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class WorkshopSessionControllerTest {

    private WorkshopSessionController controller;
    private MockSpaceDao mockSpaceDao;
    private MockWorkshopSessionService mockSessionService;

    @BeforeEach
    public void setUp() {
        mockSpaceDao = new MockSpaceDao();
        mockSessionService = new MockWorkshopSessionService();
        controller = new WorkshopSessionController(mockSessionService, mockSpaceDao);
        AppSession.startSession("owner1", "owner@spotfit.com", "Owner 1", UserRole.OWNER, "mock-token", "mock-refresh", 3600);
    }

    @AfterEach
    public void tearDown() {
        AppSession.clearSession();
    }

    @Test
    @DisplayName("createSession denies write when Space approvalStatus is UNDER_REVIEW")
    void testCreateSessionLifecycleWriteDeniedWhenUnderReview() {
        Space space = createSampleSpace("UNDER_REVIEW");
        mockSpaceDao.spaceToReturn = space;

        long start = System.currentTimeMillis() + 100000;
        long end = start + 3600000;
        WorkshopSession session = new WorkshopSession("sess1", space.getSpaceId(), start, end, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        OperationResult<WorkshopSession> res = controller.createSession(space.getSpaceId(), session).join();

        assertTrue(res.isFailure());
        assertEquals(OperationCode.LIFECYCLE_WRITE_DENIED, res.getErrorCode());
    }

    @Test
    @DisplayName("createSession allows write when Space approvalStatus is APPROVED")
    void testCreateSessionAllowedWhenApproved() {
        Space space = createSampleSpace("APPROVED");
        mockSpaceDao.spaceToReturn = space;

        long start = System.currentTimeMillis() + 100000;
        long end = start + 3600000;
        WorkshopSession session = new WorkshopSession("sess1", space.getSpaceId(), start, end, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        OperationResult<WorkshopSession> res = controller.createSession(space.getSpaceId(), session).join();

        assertTrue(res.isSuccess());
    }

    private Space createSampleSpace(String approvalStatus) {
        Space space = new Space();
        space.setSchemaVersion(2);
        space.setSpaceId("space-ws-1");
        space.setOwnerId("owner1");
        space.setBookingType(BookingType.WORKSHOP);
        space.setBookingConfig(new WorkshopBookingConfig());
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

    private static class MockWorkshopSessionService extends WorkshopSessionService {
        @Override
        public CompletableFuture<OperationResult<WorkshopSession>> createSession(Space space, WorkshopSession session, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(session));
        }

        @Override
        public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(new ArrayList<>()));
        }
    }
}
