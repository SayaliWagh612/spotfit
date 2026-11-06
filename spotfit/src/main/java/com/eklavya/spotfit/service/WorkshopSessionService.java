package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.dao.WorkshopSessionDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Domain service for managing Workshop Sessions in V2 WORKSHOP Spaces.
 */
public class WorkshopSessionService {

    private final WorkshopSessionDao sessionDao;

    public WorkshopSessionService() {
        this.sessionDao = new WorkshopSessionDao();
    }

    public WorkshopSessionService(WorkshopSessionDao sessionDao) {
        this.sessionDao = (sessionDao != null) ? sessionDao : new WorkshopSessionDao();
    }

    public OperationResult<Void> validateWorkshopSpace(Space space) {
        if (space == null) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space cannot be null.");
        }
        if (space.getSchemaVersion() != 2) {
            return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH, "Workshop sessions are only supported for V2 Spaces.");
        }
        String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
        if (!"WORKSHOP".equals(bType)) {
            return OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Sessions can only be created for WORKSHOP booking type Spaces.");
        }
        if (!(space.getBookingConfig() instanceof WorkshopBookingConfig)) {
            return OperationResult.failure("INVALID_BOOKING_CONFIG", "Space booking config must be WorkshopBookingConfig.");
        }
        return OperationResult.success(null);
    }

    public CompletableFuture<OperationResult<WorkshopSession>> createSession(Space space, WorkshopSession session, String idToken) {
        OperationResult<Void> valRes = validateWorkshopSpace(space);
        if (valRes.isFailure()) {
            return CompletableFuture.completedFuture(OperationResult.failure(valRes.getErrorCode(), valRes.getMessage()));
        }

        if (session == null) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "WorkshopSession object cannot be null."));
        }
        if (session.getEndAt() <= session.getStartAt()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session end time must be strictly after start time."));
        }
        long now = System.currentTimeMillis();
        if (session.getStartAt() <= now) {
            return CompletableFuture.completedFuture(OperationResult.failure("SESSION_TIME_PAST", "Session start time must be in the future."));
        }
        if (session.getCapacity() <= 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session capacity must be greater than zero."));
        }

        WorkshopBookingConfig config = (WorkshopBookingConfig) space.getBookingConfig();
        if (config.getMaximumParticipantsPerBooking() > 0 && session.getCapacity() > config.getMaximumParticipantsPerBooking()) {
            return CompletableFuture.completedFuture(OperationResult.failure("CAPACITY_EXCEEDS_MAX",
                    "Session capacity (" + session.getCapacity() + ") exceeds maximum participants configured for Workshop (" + config.getMaximumParticipantsPerBooking() + ")."));
        }
        if (config.getMinimumParticipantsPerBooking() > 0 && session.getCapacity() < config.getMinimumParticipantsPerBooking()) {
            return CompletableFuture.completedFuture(OperationResult.failure("CAPACITY_BELOW_MIN",
                    "Session capacity (" + session.getCapacity() + ") is below minimum participants configured for Workshop (" + config.getMinimumParticipantsPerBooking() + ")."));
        }

        if (session.getPriceOverridePaise() < 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price override cannot be negative."));
        }

        // Validate overlapping scheduled sessions
        return sessionDao.getSessionsForSpace(space.getSpaceId(), idToken)
                .thenCompose(existingSessionsRes -> {
                    if (existingSessionsRes.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(existingSessionsRes.getErrorCode(), existingSessionsRes.getMessage()));
                    }
                    List<WorkshopSession> existingSessions = existingSessionsRes.getData();
                    if (existingSessions != null) {
                        for (WorkshopSession s : existingSessions) {
                            if (s.isActive() && WorkshopSessionStatus.SCHEDULED.equalsIgnoreCase(s.getStatus())) {
                                boolean overlaps = session.getStartAt() < s.getEndAt() && session.getEndAt() > s.getStartAt();
                                if (overlaps) {
                                    return CompletableFuture.completedFuture(OperationResult.failure("OVERLAPPING_SESSION",
                                            "Session overlaps with an existing scheduled session in this Space."));
                                }
                            }
                        }
                    }

                    session.setSpaceId(space.getSpaceId());
                    session.setBookedCount(0); // Immutable bookedCount
                    session.setStatus(WorkshopSessionStatus.SCHEDULED);
                    session.setActive(true);
                    session.setCreatedAt(now);
                    session.setUpdatedAt(now);

                    return sessionDao.createSession(space.getSpaceId(), session, idToken);
                });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> updateSession(Space space, WorkshopSession session, String idToken) {
        OperationResult<Void> valRes = validateWorkshopSpace(space);
        if (valRes.isFailure()) {
            return CompletableFuture.completedFuture(OperationResult.failure(valRes.getErrorCode(), valRes.getMessage()));
        }

        if (session == null || session.getSessionId() == null || session.getSessionId().isBlank()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session and Session ID are required."));
        }
        if (session.getEndAt() <= session.getStartAt()) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session end time must be strictly after start time."));
        }
        if (session.getCapacity() <= 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Session capacity must be greater than zero."));
        }

        WorkshopBookingConfig config = (WorkshopBookingConfig) space.getBookingConfig();
        if (config.getMaximumParticipantsPerBooking() > 0 && session.getCapacity() > config.getMaximumParticipantsPerBooking()) {
            return CompletableFuture.completedFuture(OperationResult.failure("CAPACITY_EXCEEDS_MAX",
                    "Session capacity exceeds maximum participants configured for Workshop."));
        }
        if (config.getMinimumParticipantsPerBooking() > 0 && session.getCapacity() < config.getMinimumParticipantsPerBooking()) {
            return CompletableFuture.completedFuture(OperationResult.failure("CAPACITY_BELOW_MIN",
                    "Session capacity is below minimum participants configured for Workshop."));
        }
        if (session.getPriceOverridePaise() < 0) {
            return CompletableFuture.completedFuture(OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Price override cannot be negative."));
        }

        return sessionDao.getSession(space.getSpaceId(), session.getSessionId(), idToken)
                .thenCompose(existingRes -> {
                    if (existingRes.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(existingRes.getErrorCode(), existingRes.getMessage()));
                    }
                    WorkshopSession existing = existingRes.getData();
                    if (WorkshopSessionStatus.CANCELLED.equalsIgnoreCase(existing.getStatus())) {
                        return CompletableFuture.completedFuture(OperationResult.failure("CANCELLED_SESSION_IMMUTABLE",
                                "Cancelled sessions cannot be edited."));
                    }

                    // Validate overlap excluding self
                    return sessionDao.getSessionsForSpace(space.getSpaceId(), idToken)
                            .thenCompose(allSessionsRes -> {
                                if (allSessionsRes.isFailure()) {
                                    return CompletableFuture.completedFuture(OperationResult.failure(allSessionsRes.getErrorCode(), allSessionsRes.getMessage()));
                                }
                                List<WorkshopSession> allSessions = allSessionsRes.getData();
                                if (allSessions != null) {
                                    for (WorkshopSession s : allSessions) {
                                        if (!s.getSessionId().equals(session.getSessionId()) && s.isActive() && WorkshopSessionStatus.SCHEDULED.equalsIgnoreCase(s.getStatus())) {
                                            boolean overlaps = session.getStartAt() < s.getEndAt() && session.getEndAt() > s.getStartAt();
                                            if (overlaps) {
                                                return CompletableFuture.completedFuture(OperationResult.failure("OVERLAPPING_SESSION",
                                                        "Session overlaps with an existing scheduled session in this Space."));
                                            }
                                        }
                                    }
                                }

                                session.setSpaceId(space.getSpaceId());
                                session.setBookedCount(existing.getBookedCount()); // Preserve bookedCount
                                session.setCreatedAt(existing.getCreatedAt()); // Preserve createdAt
                                session.setUpdatedAt(System.currentTimeMillis());

                                return sessionDao.updateSession(space.getSpaceId(), session, idToken);
                            });
                });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> getSession(String spaceId, String sessionId, String idToken) {
        return sessionDao.getSession(spaceId, sessionId, idToken);
    }

    public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId, String idToken) {
        return sessionDao.getSessionsForSpace(spaceId, idToken);
    }

    public CompletableFuture<OperationResult<WorkshopSession>> cancelSession(Space space, String sessionId, String idToken) {
        OperationResult<Void> valRes = validateWorkshopSpace(space);
        if (valRes.isFailure()) {
            return CompletableFuture.completedFuture(OperationResult.failure(valRes.getErrorCode(), valRes.getMessage()));
        }
        return sessionDao.cancelSession(space.getSpaceId(), sessionId, System.currentTimeMillis(), idToken);
    }
}
