package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.SeatService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Owner Controller for managing Seats within V2 SEAT Spaces.
 * Enforces authentication, Space ownership, schema version 2, and write lifecycle status rules.
 */
public class SeatController {

    private final SeatService seatService;
    private final SpaceDao spaceDao;

    public SeatController() {
        this.seatService = new SeatService();
        this.spaceDao = new SpaceDao();
    }

    public SeatController(SeatService seatService, SpaceDao spaceDao) {
        this.seatService = (seatService != null) ? seatService : new SeatService();
        this.spaceDao = (spaceDao != null) ? spaceDao : new SpaceDao();
    }

    public CompletableFuture<OperationResult<List<Seat>>> getSeatsForSpace(String spaceId) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, false, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return seatService.getSeatsForSpace(spaceId, token);
        });
    }

    public CompletableFuture<OperationResult<Seat>> createSeat(String spaceId, Seat seat) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return seatService.createSeat(ctx.space, seat, token);
        });
    }

    public CompletableFuture<OperationResult<Seat>> updateSeat(String spaceId, Seat seat) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return seatService.updateSeat(ctx.space, seat, token);
        });
    }

    public CompletableFuture<OperationResult<Seat>> setSeatActive(String spaceId, String seatId, boolean active) {
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
            OperationResult<SpaceAuthContext> authRes = verifySpaceOwnerAndV2Context(spaceId, true, token);
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }

            SpaceAuthContext ctx = authRes.getData();
            return seatService.setSeatActive(spaceId, seatId, active, token);
        });
    }

    private OperationResult<SpaceAuthContext> verifySpaceOwnerAndV2Context(String spaceId, boolean isWriteOperation, String idToken) {
        if (!AppSession.isAuthenticated()) {
            return OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User is not authenticated.");
        }
        if (AppSession.getRole() != UserRole.OWNER) {
            return OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Space Owners can manage seats.");
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
                return OperationResult.failure(OperationCode.SCHEMA_VERSION_MISMATCH, "Seats are only supported for V2 Spaces.");
            }

            String bookingType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            if (!"SEAT".equals(bookingType)) {
                return OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Seats can only be managed for SEAT booking type Spaces.");
            }

            if (isWriteOperation) {
                String status = space.getApprovalStatus() != null ? space.getApprovalStatus().toUpperCase() : "";
                if ("SUBMITTED".equals(status) || "UNDER_REVIEW".equals(status)) {
                    return OperationResult.failure(OperationCode.LIFECYCLE_WRITE_DENIED,
                            "Seat modifications are locked while Space is under review.");
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
