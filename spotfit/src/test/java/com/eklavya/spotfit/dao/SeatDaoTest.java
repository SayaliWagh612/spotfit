package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class SeatDaoTest {

    private SeatDao seatDao;

    @BeforeEach
    public void setUp() {
        seatDao = new SeatDao(new DummyFirestoreRestClient());
    }

    @Test
    @DisplayName("Seat document serialization and deserialization round trip")
    void testSeatRoundTrip() {
        Seat original = new Seat("seat100", "space200", "Corner Desk 1", "D-1", "Zone A", "D", 1, 2, 8000L, SeatStatus.AVAILABLE, true, 1000L, 2000L);
        JSONObject doc = seatDao.seatToFirestoreDocument(original);

        assertNotNull(doc);
        Seat restored = seatDao.seatFromFirestoreDocument(doc);

        assertNotNull(restored);
        assertEquals("seat100", restored.getSeatId());
        assertEquals("space200", restored.getSpaceId());
        assertEquals("Corner Desk 1", restored.getSeatName());
        assertEquals("D-1", restored.getSeatCode());
        assertEquals("Zone A", restored.getSection());
        assertEquals("D", restored.getRowLabel());
        assertEquals(1, restored.getColumnNumber());
        assertEquals(2, restored.getCapacity());
        assertEquals(8000L, restored.getPriceOverridePaise());
        assertEquals(SeatStatus.AVAILABLE, restored.getStatus());
        assertTrue(restored.isActive());
    }

    @Test
    @DisplayName("createSeat calls setDocumentBySegments and returns success")
    void testCreateSeatSuccess() {
        Seat seat = new Seat("seat1", "space1", "Desk 1", "A-1", "Sec 1", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
        OperationResult<Seat> res = seatDao.createSeat("space1", seat, "mock-token").join();

        assertTrue(res.isSuccess());
        assertEquals("seat1", res.getData().getSeatId());
    }

    private static class DummyFirestoreRestClient extends FirestoreRestClient {
        @Override
        public CompletableFuture<OperationResult<JSONObject>> setDocumentBySegments(List<String> segments, JSONObject document, String idToken) {
            return CompletableFuture.completedFuture(OperationResult.success(document));
        }

        @Override
        public CompletableFuture<OperationResult<JSONObject>> getDocumentBySegments(List<String> segments, String idToken) {
            Seat s = new Seat("seat1", "space1", "Desk 1", "A-1", "Sec 1", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
            SeatDao dao = new SeatDao();
            return CompletableFuture.completedFuture(OperationResult.success(dao.seatToFirestoreDocument(s)));
        }

        @Override
        public CompletableFuture<OperationResult<List<JSONObject>>> listDocumentsBySegments(List<String> segments, String idToken) {
            Seat s = new Seat("seat1", "space1", "Desk 1", "A-1", "Sec 1", "A", 1, 1, 0L, SeatStatus.AVAILABLE, true, 100L, 100L);
            SeatDao dao = new SeatDao();
            return CompletableFuture.completedFuture(OperationResult.success(List.of(dao.seatToFirestoreDocument(s))));
        }
    }
}
