package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.service.WorkshopSessionService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Owner Controller for managing Workshop Sessions within V2 WORKSHOP Spaces.
 * Enforces authentication, Space ownership, schema version 2, and write lifecycle status rules.
 */
public class WorkshopSessionController {

    private final WorkshopSessionService sessionService;
    private final SpaceDao spaceDao;

    public WorkshopSessionController() {
        this.sessionService = new WorkshopSessionService();
        this.spaceDao = new SpaceDao();
    }

    public WorkshopSessionController(WorkshopSessionService sessionService, SpaceDao spaceDao) {
        this.sessionService = (sessionService != null) ? sessionService : new WorkshopSessionService();
        this.spaceDao = (spaceDao != null) ? spaceDao : new SpaceDao();
    }

    public CompletableFuture<OperationResult<List<WorkshopSession>>> getSessionsForSpace(String spaceId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, false, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return sessionService.getSessionsForSpace(spaceId, token);
        });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> createSession(String spaceId, WorkshopSession session) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return sessionService.createSession(ctx.space, session, token);
        });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> updateSession(String spaceId, WorkshopSession session) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return sessionService.updateSession(ctx.space, session, token);
        });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> cancelSession(String spaceId, String sessionId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return sessionService.cancelSession(ctx.space, sessionId, token);
        });
    }

    private OperationResult<SpaceAuthContext> verifySpaceOwnerAndV2Context(String spaceId, boolean isWriteOperation, String idToken) {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (AppSession.getRole() != UserRole.OWNER) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Space Owners can manage workshop sessions.");
        }

        String ownerId = AppSession.getUserId();

        if (spaceId == null || spaceId.isBlank()) {
            return OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required.");
        }

        try {
            OperationResult<Space> spaceRes = spaceDao.getSpace(spaceId, idToken).get();
            if (spaceRes.isFailure()) {
                return OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage());
            }
            Space space = spaceRes.getData();
            if (!ownerId.equals(space.getOwnerId())) {
                return OperationResult.failure(OperationCode.PERMISSION_DENIED, "Access denied. Space does not belong to authenticated Owner.");
            }
            if (space.getSchemaVersion() != 2) {
                return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH, "Workshop sessions are only supported for V2 Spaces.");
            }

            String bookingType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            if (!"WORKSHOP".equals(bookingType)) {
                return OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Sessions can only be managed for WORKSHOP booking type Spaces.");
            }

            if (isWriteOperation) {
                String status = space.getApprovalStatus() != null ? space.getApprovalStatus().toUpperCase() : "";
                if ("SUBMITTED".equals(status) || "UNDER_REVIEW".equals(status)) {
                    return OperationResult.failure(OperationCode.LIFECYCLE_WRITE_DENIED,
                            "Session modifications are locked while Space is under review.");
                }
            }

            return OperationResult.success(new SpaceAuthContext(space, idToken));

        } catch (Exception e) {
            return OperationResult.failure(OperationCode.UNKNOWN_ERROR, "Failed to verify V2 Space ownership context.");
        }
    }

    private static class SpaceAuthContext {
        final Space space;
        final String idToken;

        SpaceAuthContext(Space space, String idToken) {
            this.space = space;
            this.idToken = idToken;
        }
    }
}
