package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.WorkshopSession;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class WorkshopSessionDaoTest {

    private WorkshopSessionDao sessionDao;

    @BeforeEach
    public void setUp() {
        sessionDao = new WorkshopSessionDao(new DummyFirestoreRestClient());
    }

    @Test
    @DisplayName("WorkshopSession document serialization and deserialization round trip")
    void testSessionRoundTrip() {
        long start = System.currentTimeMillis() + 10000;
        long end = start + 50000;
        WorkshopSession original = new WorkshopSession("sess100", "space200", start, end, 15, 3, 12000L, WorkshopSessionStatus.SCHEDULED, true, 1000L, 2000L);
        JSONObject doc = sessionDao.sessionToFirestoreDocument(original);

        assertNotNull(doc);
        WorkshopSession restored = sessionDao.sessionFromFirestoreDocument(doc);

        assertNotNull(restored);
        assertEquals("sess100", restored.getSessionId());
        assertEquals("space200", restored.getSpaceId());
        assertEquals(start, restored.getStartAt());
        assertEquals(end, restored.getEndAt());
        assertEquals(15, restored.getCapacity());
        assertEquals(3, restored.getBookedCount());
        assertEquals(12000L, restored.getPriceOverridePaise());
        assertEquals(WorkshopSessionStatus.SCHEDULED, restored.getStatus());
        assertTrue(restored.isActive());
    }

    @Test
    @DisplayName("createSession calls setDocumentBySegments and returns success")
    void testCreateSessionSuccess() {
        long start = System.currentTimeMillis() + 10000;
        long end = start + 50000;
        WorkshopSession session = new WorkshopSession("sess1", "space1", start, end, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
        OperationResult<WorkshopSession> res = sessionDao.createSession("space1", session, "mock-token").join();

        assertTrue(res.isSuccess());
        assertEquals("sess1", res.getData().getSessionId());
    }

    private static class DummyFirestoreRestClient extends FirestoreRestClient {
        @Override
        public CompletableFuture<OperationResult<JSONObject>> setDocumentBySegments(List<String> segments, JSONObject document, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(document));
        }

        @Override
        public CompletableFuture<OperationResult<JSONObject>> getDocumentBySegments(List<String> segments, String idToken) {
            long start = System.currentTimeMillis() + 10000;
            long end = start + 50000;
            WorkshopSession s = new WorkshopSession("sess1", "space1", start, end, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
            WorkshopSessionDao dao = new WorkshopSessionDao();
            return CompletableFuture.completedFuture(OperationResult.success(dao.sessionToFirestoreDocument(s)));
        }

        @Override
        public CompletableFuture<OperationResult<List<JSONObject>>> listDocumentsBySegments(List<String> segments, String idToken) {
            long start = System.currentTimeMillis() + 10000;
            long end = start + 50000;
            WorkshopSession s = new WorkshopSession("sess1", "space1", start, end, 10, 0, 0L, WorkshopSessionStatus.SCHEDULED, true, 100L, 100L);
            WorkshopSessionDao dao = new WorkshopSessionDao();
            return CompletableFuture.completedFuture(OperationResult.success(List.of(dao.sessionToFirestoreDocument(s))));
        }
    }
}
