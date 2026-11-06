package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.BusinessDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.AvailabilityBlock;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import com.eklavya.spotfit.service.AvailabilityService;
import com.eklavya.spotfit.service.ManagerAuthorizationService;
import com.eklavya.spotfit.service.ManagerService;
import com.eklavya.spotfit.service.SeatService;
import com.eklavya.spotfit.service.WorkshopSessionService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller managing operational actions on V2 Spaces assigned to a Space Manager.
 * Enforces strict Manager boundaries, assigned space scope, V2 schema version 2, and permission gating.
 */
public class ManagerSpaceOperationsController {

    private final ManagerService managerService;
    private final ManagerAuthorizationService authService;
    private final SpaceDao spaceDao;
    private final BusinessDao businessDao;
    private final AvailabilityService availabilityService;
    private final SeatService seatService;
    private final WorkshopSessionService sessionService;

    public ManagerSpaceOperationsController() {
        this.managerService = new ManagerService();
        this.authService = new ManagerAuthorizationService();
        this.spaceDao = new SpaceDao();
        this.businessDao = new BusinessDao();
        this.availabilityService = new AvailabilityService();
        this.seatService = new SeatService();
        this.sessionService = new WorkshopSessionService();
    }

    public ManagerSpaceOperationsController(
            ManagerService managerService,
            ManagerAuthorizationService authService,
            SpaceDao spaceDao,
            BusinessDao businessDao,
            AvailabilityService availabilityService,
            SeatService seatService,
            WorkshopSessionService sessionService) {
        this.managerService = (managerService != null) ? managerService : new ManagerService();
        this.authService = (authService != null) ? authService : new ManagerAuthorizationService();
        this.spaceDao = (spaceDao != null) ? spaceDao : new SpaceDao();
        this.businessDao = (businessDao != null) ? businessDao : new BusinessDao();
        this.availabilityService = (availabilityService != null) ? availabilityService : new AvailabilityService();
        this.seatService = (seatService != null) ? seatService : new SeatService();
        this.sessionService = (sessionService != null) ? sessionService : new WorkshopSessionService();
    }

    private CompletableFuture<OperationResult<ManagerAuthContext>> verifyManagerAuthAndSpace(String spaceId, String requiredPermission) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTHENTICATION_REQUIRED", "Manager is not authenticated."));
        }
        if (AppSession.getRole() != UserRole.MANAGER) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ROLE_MISMATCH", "Only authenticated Managers can access Manager operations."));
        }

        String managerId = AppSession.getUserId();
        String idToken = AppSession.getIdToken();

        return managerService.getManagerProfile(managerId, idToken).thenCompose(mgrRes -> {
            if (mgrRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(mgrRes.getErrorCode(), mgrRes.getMessage()));
            }
            Manager manager = mgrRes.getData();
            if (manager == null || !manager.isActive()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("MANAGER_INACTIVE", "Manager profile is missing or inactive."));
            }

            if (spaceId == null || spaceId.isBlank()) {
                return CompletableFuture.completedFuture(
                        OperationResult.success(new ManagerAuthContext(manager, null, idToken)));
            }

            return spaceDao.getSpace(spaceId.trim(), idToken).thenApply(spaceRes -> {
                if (spaceRes.isFailure()) {
                    return OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage());
                }
                Space space = spaceRes.getData();

                OperationResult<Void> authCheck = authService.authorizeAssignedV2Space(manager, space, requiredPermission);
                if (authCheck.isFailure()) {
                    return OperationResult.failure(authCheck.getErrorCode(), authCheck.getMessage());
                }

                return OperationResult.success(new ManagerAuthContext(manager, space, idToken));
            });
        });
    }

    public CompletableFuture<OperationResult<List<Space>>> getAssignedSpaces() {
        return verifyManagerAuthAndSpace(null, null).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            return managerService.getAssignedSpacesForManager(ctx.manager.getManagerId(), ctx.idToken);
        });
    }

    public CompletableFuture<OperationResult<Space>> getV2SpaceDetails(String spaceId) {
        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.VIEW_ASSIGNED_SPACES).thenApply(authRes -> {
            if (authRes.isFailure()) {
                return OperationResult.failure(authRes.getErrorCode(), authRes.getMessage());
            }
            return OperationResult.success(authRes.getData().space);
        });
    }

    public CompletableFuture<OperationResult<Space>> setV2OperationalStatus(String spaceId, String targetOperationalStatus) {
        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_SPACE_STATUS).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_ACTIVATION_DENIED",
                                "Manager cannot alter operational status of an unapproved Space (current status: " + space.getApprovalStatus() + ")."));
            }

            if (!SpaceOperationalStatus.isValid(targetOperationalStatus)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid target operational status: " + targetOperationalStatus));
            }

            // Verify parent business status
            return businessDao.getBusiness(space.getBusinessId(), ctx.idToken).thenCompose(busRes -> {
                if (busRes.isFailure() || busRes.getData() == null) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure("BUSINESS_NOT_FOUND", "Parent Business not found or inactive."));
                }
                Business business = busRes.getData();
                if (!business.isActive() || !"APPROVED".equalsIgnoreCase(business.getVerificationStatus())) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure("PARENT_BUSINESS_INACTIVE", "Parent Business is not approved or active."));
                }

                long now = System.currentTimeMillis();
                long publishedAt = space.getPublishedAt();
                if (publishedAt <= 0 && SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(targetOperationalStatus)) {
                    publishedAt = now;
                }

                return spaceDao.setV2OperationalStatus(spaceId, targetOperationalStatus, publishedAt, now, ctx.idToken);
            });
        });
    }

    public CompletableFuture<OperationResult<AvailabilityRule>> saveWeeklyRule(
            String spaceId,
            String dayOfWeek,
            String startTime,
            String endTime,
            int slotIntervalMinutes,
            int minimumBookingMinutes,
            int maximumBookingMinutes,
            boolean active) {

        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_AVAILABILITY).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_MODIFICATION_DENIED",
                                "Availability rules can only be edited for APPROVED Spaces."));
            }

            String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            if (!("SLOT".equals(bType) || "VENUE".equals(bType) || "PRACTICE_ROOM".equals(bType))) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Direct availability management is only supported for SLOT, VENUE, or PRACTICE_ROOM Spaces."));
            }

            return availabilityService.saveSpaceWeeklyRule(
                    space, dayOfWeek, startTime, endTime,
                    slotIntervalMinutes, minimumBookingMinutes, maximumBookingMinutes, active, ctx.idToken);
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> createAvailabilityBlock(
            String spaceId, String title, String reason, long startAt, long endAt) {

        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_AVAILABILITY).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_MODIFICATION_DENIED",
                                "Availability blocks can only be managed for APPROVED Spaces."));
            }

            String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            if (!("SLOT".equals(bType) || "VENUE".equals(bType) || "PRACTICE_ROOM".equals(bType))) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Direct availability management is only supported for SLOT, VENUE, or PRACTICE_ROOM Spaces."));
            }

            String fullReason = (title != null && !title.isBlank()) ? (title + ": " + (reason != null ? reason : "")) : reason;
            return availabilityService.createSpaceBlock(space, startAt, endAt, fullReason, ctx.idToken);
        });
    }

    public CompletableFuture<OperationResult<AvailabilityBlock>> cancelAvailabilityBlock(String spaceId, String blockId) {
        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_AVAILABILITY).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_MODIFICATION_DENIED",
                                "Availability blocks can only be managed for APPROVED Spaces."));
            }

            return availabilityService.cancelSpaceBlock(spaceId, blockId, ctx.idToken);
        });
    }

    public CompletableFuture<OperationResult<Seat>> updateSeatStatus(String spaceId, String seatId, String targetStatus) {
        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_SEATS).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_MODIFICATION_DENIED",
                                "Seats can only be managed for APPROVED Spaces."));
            }

            if (!"SEAT".equalsIgnoreCase(space.getBookingType())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Seats can only be managed for SEAT booking type Spaces."));
            }

            if (!SeatStatus.isValid(targetStatus)) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid seat status: " + targetStatus));
            }

            return seatService.getSeat(spaceId, seatId, ctx.idToken).thenCompose(seatRes -> {
                if (seatRes.isFailure()) {
                    return CompletableFuture.completedFuture(OperationResult.failure(seatRes.getErrorCode(), seatRes.getMessage()));
                }
                Seat seat = seatRes.getData();
                seat.setStatus(targetStatus);
                seat.setUpdatedAt(System.currentTimeMillis());

                // Use seatService to persist operational status update
                return seatService.updateSeat(space, seat, ctx.idToken);
            });
        });
    }

    public CompletableFuture<OperationResult<WorkshopSession>> cancelWorkshopSession(String spaceId, String sessionId) {
        return verifyManagerAuthAndSpace(spaceId, ManagerPermission.MANAGE_SESSIONS).thenCompose(authRes -> {
            if (authRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(authRes.getErrorCode(), authRes.getMessage()));
            }
            ManagerAuthContext ctx = authRes.getData();
            Space space = ctx.space;

            if (!SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNAPPROVED_SPACE_MODIFICATION_DENIED",
                                "Workshop sessions can only be managed for APPROVED Spaces."));
            }

            if (!"WORKSHOP".equalsIgnoreCase(space.getBookingType())) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("UNSUPPORTED_BOOKING_TYPE", "Workshop sessions can only be managed for WORKSHOP booking type Spaces."));
            }

            return sessionService.cancelSession(space, sessionId, ctx.idToken);
        });
    }

    private static class ManagerAuthContext {
        final Manager manager;
        final Space space;
        final String idToken;

        ManagerAuthContext(Manager manager, Space space, String idToken) {
            this.manager = manager;
            this.space = space;
            this.idToken = idToken;
        }
    }
}
