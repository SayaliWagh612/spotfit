package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * DAO handling direct nested path persistence for V2 Space Seats.
 * Path: /spaces/{spaceId}/seats/{seatId}
 */
public class SeatDao {

    private final FirestoreRestClient firestoreClient;

    public SeatDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public SeatDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = (firestoreClient != null) ? firestoreClient : new FirestoreRestClient();
    }

    public CompletableFuture<OperationResult<Seat>> createSeat(String spaceId, Seat seat, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (seat == null || seat.getSeatId() == null || seat.getSeatId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat and Seat ID are required."));
        }
        if (!spaceId.trim().equals(seat.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match Seat model space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SEATS, seat.getSeatId().trim()
        );

        JSONObject doc = seatToFirestoreDocument(seat);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(seat, "Seat created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Seat>> getSeat(String spaceId, String seatId, String idToken) {
        if (spaceId == null || spaceId.isBlank() || seatId == null || seatId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Seat ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SEATS, seatId.trim()
        );

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        Seat seat = seatFromFirestoreDocument(res.getData());
                        if (seat != null) {
                            return OperationResult.success(seat);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Seat not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SEATS
        );

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<Seat> seats = new ArrayList<>();
                        if (res.getData() != null) {
                            for (JSONObject docJson : res.getData()) {
                                Seat seat = seatFromFirestoreDocument(docJson);
                                if (seat != null) {
                                    seats.add(seat);
                                }
                            }
                        }
                        return OperationResult.success(seats);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Seat>> updateSeat(String spaceId, Seat seat, String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (seat == null || seat.getSeatId() == null || seat.getSeatId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Seat and Seat ID are required."));
        }
        if (!spaceId.trim().equals(seat.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Path parameters must match Seat model space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SEATS, seat.getSeatId().trim()
        );

        JSONObject doc = seatToFirestoreDocument(seat);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(seat, "Seat updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<Seat>> setSeatActive(String spaceId, String seatId, boolean active, long updatedAt, String idToken) {
        return getSeat(spaceId, seatId, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                    Seat seat = res.getData();
                    seat.setActive(active);
                    seat.setUpdatedAt(updatedAt > 0 ? updatedAt : System.currentTimeMillis());
                    return updateSeat(spaceId, seat, idToken);
                });
    }

    public JSONObject seatToFirestoreDocument(Seat seat) {
        JSONObject fields = new JSONObject();
        fields.put("seatId", FirestoreDocumentUtil.stringValue(seat.getSeatId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(seat.getSpaceId()));
        fields.put("seatName", FirestoreDocumentUtil.stringValue(seat.getSeatName()));
        fields.put("seatCode", FirestoreDocumentUtil.stringValue(seat.getSeatCode()));
        fields.put("section", FirestoreDocumentUtil.stringValue(seat.getSection()));
        fields.put("rowLabel", FirestoreDocumentUtil.stringValue(seat.getRowLabel()));
        fields.put("columnNumber", FirestoreDocumentUtil.integerValue(seat.getColumnNumber()));
        fields.put("capacity", FirestoreDocumentUtil.integerValue(seat.getCapacity()));
        fields.put("priceOverridePaise", FirestoreDocumentUtil.integerValue(seat.getPriceOverridePaise()));
        fields.put("status", FirestoreDocumentUtil.stringValue(seat.getStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(seat.isActive()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(seat.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(seat.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    public Seat seatFromFirestoreDocument(JSONObject doc) {
        if (doc == null) return null;
        JSONObject fields = FirestoreDocumentUtil.getFields(doc);
        if (fields == null || fields.isEmpty()) return null;

        String seatId = FirestoreDocumentUtil.getString(fields, "seatId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        String seatName = FirestoreDocumentUtil.getString(fields, "seatName");
        String seatCode = FirestoreDocumentUtil.getString(fields, "seatCode");
        String section = FirestoreDocumentUtil.getString(fields, "section");
        String rowLabel = FirestoreDocumentUtil.getString(fields, "rowLabel");
        int columnNumber = FirestoreDocumentUtil.getInt(fields, "columnNumber", 0);
        int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 1);
        long priceOverridePaise = FirestoreDocumentUtil.getLong(fields, "priceOverridePaise", 0L);
        String status = FirestoreDocumentUtil.getString(fields, "status");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        if (seatId == null || seatId.isBlank()) return null;

        return new Seat(
                seatId, spaceId, seatName, seatCode,
                section, rowLabel, columnNumber, capacity,
                priceOverridePaise, status, active, createdAt, updatedAt
        );
    }
}
