package com.eklavya.spotfit.dao;

import com.eklavya.spotfit.constants.CollectionName;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.util.FirestoreDocumentUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * DAO handling direct nested path persistence for V2 Space Workshop Sessions.
 * Path: /spaces/{spaceId}/sessions/{sessionId}
 */
public class WorkshopSessionDao {

    private final FirestoreRestClient firestoreClient;

    public WorkshopSessionDao() {
        this.firestoreClient = new FirestoreRestClient();
    }

    public WorkshopSessionDao(FirestoreRestClient firestoreClient) {
        this.firestoreClient = (firestoreClient != null) ? firestoreClient : new FirestoreRestClient();
    }

    public CompletableFuture<OperationResult<WorkshopSession>> createSession(String spaceId, WorkshopSession session,
            String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (session == null || session.getSessionId() == null || session.getSessionId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session and Session ID are required."));
        }
        if (!spaceId.trim().equals(session.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Path parameters must match WorkshopSession space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SESSIONS, session.getSessionId().trim());

        JSONObject doc = sessionToFirestoreDocument(session);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(session, "Workshop session created successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> getSession(String spaceId, String sessionId,
            String idToken) {
        if (spaceId == null || spaceId.isBlank() || sessionId == null || sessionId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID and Session ID are required."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SESSIONS, sessionId.trim());

        return firestoreClient.getDocumentBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        WorkshopSession session = sessionFromFirestoreDocument(res.getData());
                        if (session != null) {
                            return OperationResult.success(session);
                        } else {
                            return OperationResult.failure(OperationCode.NOT_FOUND, "Workshop session not found.");
                        }
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId,
            String idToken) {
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
                CollectionName.SESSIONS);

        return firestoreClient.listDocumentsBySegments(segments, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<WorkshopSession> sessions = new ArrayList<>();
                        if (res.getData() != null) {
                            for (JSONObject docJson : res.getData()) {
                                WorkshopSession session = sessionFromFirestoreDocument(docJson);
                                if (session != null) {
                                    sessions.add(session);
                                }
                            }
                        }
                        return OperationResult.success(sessions);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<List<WorkshopSession>>> getPublicSessionsForSpace(String spaceId) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SESSIONS);

        return firestoreClient.listPublicDocumentsBySegments(segments)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        List<WorkshopSession> sessions = new ArrayList<>();
                        if (res.getData() != null) {
                            for (JSONObject docJson : res.getData()) {
                                WorkshopSession session = sessionFromFirestoreDocument(docJson);
                                if (session != null) {
                                    sessions.add(session);
                                }
                            }
                        }
                        return OperationResult.success(sessions);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> updateSession(String spaceId, WorkshopSession session,
            String idToken) {
        if (spaceId == null || spaceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (session == null || session.getSessionId() == null || session.getSessionId().isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session and Session ID are required."));
        }
        if (!spaceId.trim().equals(session.getSpaceId().trim())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT,
                            "Path parameters must match WorkshopSession space ID."));
        }
        if (idToken == null || idToken.isBlank()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "ID token is required."));
        }

        List<String> segments = List.of(
                CollectionName.SPACES, spaceId.trim(),
                CollectionName.SESSIONS, session.getSessionId().trim());

        JSONObject doc = sessionToFirestoreDocument(session);
        return firestoreClient.setDocumentBySegments(segments, doc, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        return OperationResult.success(session, "Workshop session updated successfully.");
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> cancelSession(String spaceId, String sessionId,
            long updatedAt, String idToken) {
        return getSession(spaceId, sessionId, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture
                                .completedFuture(OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                    WorkshopSession session = res.getData();
                    session.setStatus(WorkshopSessionStatus.CANCELLED);
                    session.setUpdatedAt(updatedAt > 0 ? updatedAt : System.currentTimeMillis());
                    return updateSession(spaceId, session, idToken);
                });
    }

    public JSONObject sessionToFirestoreDocument(WorkshopSession session) {
        JSONObject fields = new JSONObject();
        fields.put("sessionId", FirestoreDocumentUtil.stringValue(session.getSessionId()));
        fields.put("spaceId", FirestoreDocumentUtil.stringValue(session.getSpaceId()));
        fields.put("startAt", FirestoreDocumentUtil.integerValue(session.getStartAt()));
        fields.put("endAt", FirestoreDocumentUtil.integerValue(session.getEndAt()));
        fields.put("capacity", FirestoreDocumentUtil.integerValue(session.getCapacity()));
        fields.put("bookedCount", FirestoreDocumentUtil.integerValue(session.getBookedCount()));
        fields.put("priceOverridePaise", FirestoreDocumentUtil.integerValue(session.getPriceOverridePaise()));
        fields.put("status", FirestoreDocumentUtil.stringValue(session.getStatus()));
        fields.put("active", FirestoreDocumentUtil.booleanValue(session.isActive()));
        fields.put("createdAt", FirestoreDocumentUtil.integerValue(session.getCreatedAt()));
        fields.put("updatedAt", FirestoreDocumentUtil.integerValue(session.getUpdatedAt()));

        return FirestoreDocumentUtil.createDocument(fields);
    }

    public WorkshopSession sessionFromFirestoreDocument(JSONObject doc) {
        if (doc == null)
            return null;
        JSONObject fields = FirestoreDocumentUtil.getFields(doc);
        if (fields == null || fields.isEmpty())
            return null;

        String sessionId = FirestoreDocumentUtil.getString(fields, "sessionId");
        String spaceId = FirestoreDocumentUtil.getString(fields, "spaceId");
        long startAt = FirestoreDocumentUtil.getLong(fields, "startAt", 0L);
        long endAt = FirestoreDocumentUtil.getLong(fields, "endAt", 0L);
        int capacity = FirestoreDocumentUtil.getInt(fields, "capacity", 1);
        int bookedCount = FirestoreDocumentUtil.getInt(fields, "bookedCount", 0);
        long priceOverridePaise = FirestoreDocumentUtil.getLong(fields, "priceOverridePaise", 0L);
        String status = FirestoreDocumentUtil.getString(fields, "status");
        boolean active = FirestoreDocumentUtil.getBoolean(fields, "active", true);
        long createdAt = FirestoreDocumentUtil.getLong(fields, "createdAt", 0L);
        long updatedAt = FirestoreDocumentUtil.getLong(fields, "updatedAt", 0L);

        if (sessionId == null || sessionId.isBlank())
            return null;

        return new WorkshopSession(
                sessionId, spaceId, startAt, endAt,
                capacity, bookedCount, priceOverridePaise,
                status, active, createdAt, updatedAt);
    }
}
