package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.constants.UserRole;
import com.eklavya.spotfit.dao.*;
import com.eklavya.spotfit.model.*;
import com.eklavya.spotfit.service.SubmissionService;
import com.eklavya.spotfit.session.AppSession;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for managing Owner Business and Space submission readiness evaluation and status transitions.
 */
public class SubmissionController {

    private final SubmissionService submissionService;
    private final BusinessDao businessDao;
    private final SpaceDao spaceDao;
    private final ResourceDao resourceDao;
    private final OfferingDao offeringDao;
    private final AvailabilityDao availabilityDao;
    private final SeatDao seatDao;
    private final WorkshopSessionDao workshopSessionDao;

    public SubmissionController() {
        this(new SubmissionService(), new BusinessDao(), new SpaceDao(), new ResourceDao(), new OfferingDao(), new AvailabilityDao(), new SeatDao(), new WorkshopSessionDao());
    }

    public SubmissionController(
            SubmissionService submissionService,
            BusinessDao businessDao,
            SpaceDao spaceDao,
            ResourceDao resourceDao,
            OfferingDao offeringDao,
            AvailabilityDao availabilityDao) {
        this(submissionService, businessDao, spaceDao, resourceDao, offeringDao, availabilityDao, new SeatDao(), new WorkshopSessionDao());
    }

    public SubmissionController(
            SubmissionService submissionService,
            BusinessDao businessDao,
            SpaceDao spaceDao,
            ResourceDao resourceDao,
            OfferingDao offeringDao,
            AvailabilityDao availabilityDao,
            SeatDao seatDao,
            WorkshopSessionDao workshopSessionDao) {
        this.submissionService = submissionService;
        this.businessDao = businessDao;
        this.spaceDao = spaceDao;
        this.resourceDao = resourceDao;
        this.offeringDao = offeringDao;
        this.availabilityDao = availabilityDao;
        this.seatDao = seatDao;
        this.workshopSessionDao = workshopSessionDao;
    }

    public CompletableFuture<OperationResult<SubmissionReadinessResult>> checkBusinessReadiness(String businessId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can check Business readiness."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            businessDao.getBusiness(businessId.trim(), idToken)
                    .thenApply(bizRes -> {
                        if (bizRes.isFailure()) {
                            return OperationResult.failure(bizRes.getErrorCode(), bizRes.getMessage());
                        }
                        Business business = bizRes.getData();
                        return submissionService.validateBusinessReadiness(business, uid);
                    })
        );
    }

    public CompletableFuture<OperationResult<Business>> submitBusiness(String businessId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can submit Businesses."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(businessId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Business ID is required."));
        }

        return checkBusinessReadiness(businessId.trim())
                .thenCompose(readinessRes -> {
                    if (readinessRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(readinessRes.getErrorCode(), readinessRes.getMessage()));
                    }
                    SubmissionReadinessResult readiness = readinessRes.getData();
                    if (!readiness.isReady()) {
                        String msg = "Business is not ready for submission:\n- " + String.join("\n- ", readiness.getMissingRequirements());
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.INVALID_ARGUMENT, msg));
                    }
                    long now = System.currentTimeMillis();
                    return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
                        businessDao.submitBusiness(businessId.trim(), now, now, idToken)
                    );
                });
    }

    public CompletableFuture<OperationResult<SubmissionReadinessResult>> checkSpaceReadiness(String spaceId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can check Space readiness."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        String sid = spaceId.trim();
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceDao.getSpace(sid, idToken)
                    .thenCompose(spaceRes -> {
                        if (spaceRes.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
                        }
                        Space space = spaceRes.getData();
                        if (space == null) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
                        }
                        if (!uid.equals(space.getOwnerId())) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.PERMISSION_DENIED, "You do not own this Space."));
                        }

                        return businessDao.getBusiness(space.getBusinessId(), idToken)
                                .thenCompose(bizRes -> {
                                    Business parentBusiness = bizRes.isSuccess() ? bizRes.getData() : null;

                                    return resourceDao.getResourcesForSpace(sid, idToken)
                                            .thenCompose(resRes -> {
                                                List<Resource> resources = resRes.isSuccess() ? resRes.getData() : new ArrayList<>();

                                                return offeringDao.getOfferingsForSpace(sid, idToken)
                                                        .thenCompose(offRes -> {
                                                            List<Offering> offerings = offRes.isSuccess() ? offRes.getData() : new ArrayList<>();

                                                            return loadAvailabilityRulesForResources(sid, resources, idToken)
                                                                    .thenApply(ruleMap -> submissionService.validateSpaceReadiness(
                                                                            parentBusiness, space, resources, offerings, ruleMap, uid));
                                                        });
                                            });
                                });
                    })
        );
    }

    public CompletableFuture<OperationResult<Space>> submitSpace(String spaceId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can submit Spaces."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return checkSpaceReadiness(spaceId.trim())
                .thenCompose(readinessRes -> {
                    if (readinessRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(readinessRes.getErrorCode(), readinessRes.getMessage()));
                    }
                    SubmissionReadinessResult readiness = readinessRes.getData();
                    if (!readiness.isReady()) {
                        String msg = "Space is not ready for submission:\n- " + String.join("\n- ", readiness.getMissingRequirements());
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.INVALID_ARGUMENT, msg));
                    }
                    long now = System.currentTimeMillis();
                    return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
                        spaceDao.submitSpace(spaceId.trim(), now, now, idToken)
                    );
                });
    }

    public CompletableFuture<OperationResult<SubmissionReadinessResult>> checkV2SpaceReadiness(String spaceId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can check Space readiness."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        String sid = spaceId.trim();
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceDao.getSpace(sid, idToken)
                    .thenCompose(spaceRes -> {
                        if (spaceRes.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
                        }
                        Space space = spaceRes.getData();
                        if (space == null) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
                        }
                        if (!uid.equals(space.getOwnerId())) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.PERMISSION_DENIED, "You do not own this Space."));
                        }

                        return businessDao.getBusiness(space.getBusinessId(), idToken)
                                .thenCompose(bizRes -> {
                                    Business parentBusiness = bizRes.isSuccess() ? bizRes.getData() : null;

                                    String bType = space.getBookingType();
                                    if (com.eklavya.spotfit.constants.BookingType.SEAT.equals(bType)) {
                                        return seatDao.getSeatsForSpace(sid, idToken)
                                                .thenApply(seatRes -> {
                                                    List<Seat> seats = seatRes.isSuccess() ? seatRes.getData() : new ArrayList<>();
                                                    return submissionService.validateV2SpaceReadiness(parentBusiness, space, seats, null, null, uid);
                                                });
                                    } else if (com.eklavya.spotfit.constants.BookingType.SLOT.equals(bType)
                                            || com.eklavya.spotfit.constants.BookingType.PRACTICE_ROOM.equals(bType)
                                            || com.eklavya.spotfit.constants.BookingType.VENUE.equals(bType)) {
                                        return availabilityDao.getSpaceAvailabilityRules(sid, idToken)
                                                .thenApply(ruleRes -> {
                                                    List<AvailabilityRule> rules = ruleRes.isSuccess() ? ruleRes.getData() : new ArrayList<>();
                                                    return submissionService.validateV2SpaceReadiness(parentBusiness, space, null, rules, null, uid);
                                                });
                                    } else if (com.eklavya.spotfit.constants.BookingType.WORKSHOP.equals(bType)) {
                                        return workshopSessionDao.getSessionsForSpace(sid, idToken)
                                                .thenApply(sessRes -> {
                                                    List<WorkshopSession> sessions = sessRes.isSuccess() ? sessRes.getData() : new ArrayList<>();
                                                    return submissionService.validateV2SpaceReadiness(parentBusiness, space, null, null, sessions, uid);
                                                });
                                    } else {
                                        return CompletableFuture.completedFuture(
                                                submissionService.validateV2SpaceReadiness(parentBusiness, space, null, null, null, uid));
                                    }
                                });
                    })
        );
    }

    public CompletableFuture<OperationResult<Space>> submitV2Space(String spaceId) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can submit Spaces."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }

        return checkV2SpaceReadiness(spaceId.trim())
                .thenCompose(readinessRes -> {
                    if (readinessRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(readinessRes.getErrorCode(), readinessRes.getMessage()));
                    }
                    SubmissionReadinessResult readiness = readinessRes.getData();
                    if (!readiness.isReady()) {
                        String msg = "V2 Space is not ready for submission:\n- " + String.join("\n- ", readiness.getMissingRequirements());
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(OperationCode.INVALID_ARGUMENT, msg));
                    }
                    long now = System.currentTimeMillis();
                    return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
                        spaceDao.submitV2Space(spaceId.trim(), now, now, idToken)
                    );
                });
    }

    public CompletableFuture<OperationResult<Space>> activateV2Space(String spaceId) {
        return setV2OperationalStatus(spaceId, com.eklavya.spotfit.constants.SpaceOperationalStatus.ACTIVE);
    }

    public CompletableFuture<OperationResult<Space>> setV2OperationalStatus(String spaceId, String operationalStatus) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "Authentication required."));
        }
        if (!UserRole.OWNER.equals(AppSession.getRole())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.ROLE_MISMATCH, "Only Owners can change operational status."));
        }
        String uid = AppSession.getUserId();
        if (ValidationUtil.isBlank(uid)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.AUTHENTICATION_REQUIRED, "User session token is missing."));
        }
        if (ValidationUtil.isBlank(spaceId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Space ID is required."));
        }
        if (!com.eklavya.spotfit.constants.SpaceOperationalStatus.isValid(operationalStatus)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, "Invalid operational status: " + operationalStatus));
        }

        String sid = spaceId.trim();
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceDao.getSpace(sid, idToken)
                    .thenCompose(spaceRes -> {
                        if (spaceRes.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(spaceRes.getErrorCode(), spaceRes.getMessage()));
                        }
                        Space space = spaceRes.getData();
                        if (space == null) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.NOT_FOUND, "Space not found."));
                        }
                        if (!uid.equals(space.getOwnerId())) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.PERMISSION_DENIED, "You do not own this Space."));
                        }
                        if (!com.eklavya.spotfit.constants.SpaceApprovalStatus.APPROVED.equalsIgnoreCase(space.getApprovalStatus())) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.LIFECYCLE_WRITE_DENIED, "Operational status can only be updated for APPROVED spaces. Current status: " + space.getApprovalStatus()));
                        }
                        if (!space.isActive()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(OperationCode.LIFECYCLE_WRITE_DENIED, "Space is inactive."));
                        }

                        if (com.eklavya.spotfit.constants.SpaceOperationalStatus.ACTIVE.equalsIgnoreCase(operationalStatus)) {
                            return checkV2SpaceReadiness(sid)
                                    .thenCompose(readinessRes -> {
                                        if (readinessRes.isFailure()) {
                                            return CompletableFuture.completedFuture(
                                                    OperationResult.failure(readinessRes.getErrorCode(), readinessRes.getMessage()));
                                        }
                                        if (!readinessRes.getData().isReady()) {
                                            String msg = "Cannot activate Space because readiness requirements are not met:\n- "
                                                    + String.join("\n- ", readinessRes.getData().getMissingRequirements());
                                            return CompletableFuture.completedFuture(
                                                    OperationResult.failure(OperationCode.INVALID_ARGUMENT, msg));
                                        }
                                        long publishedAt = (space.getPublishedAt() > 0) ? space.getPublishedAt() : System.currentTimeMillis();
                                        long now = System.currentTimeMillis();
                                        return spaceDao.setV2OperationalStatus(sid, com.eklavya.spotfit.constants.SpaceOperationalStatus.ACTIVE, publishedAt, now, idToken);
                                    });
                        } else {
                            long publishedAt = space.getPublishedAt();
                            long now = System.currentTimeMillis();
                            return spaceDao.setV2OperationalStatus(sid, operationalStatus, publishedAt, now, idToken);
                        }
                    })
        );
    }

    private CompletableFuture<Map<String, List<AvailabilityRule>>> loadAvailabilityRulesForResources(
            String spaceId, List<Resource> resources, String idToken) {

        Map<String, List<AvailabilityRule>> map = new HashMap<>();
        if (resources == null || resources.isEmpty()) {
            return CompletableFuture.completedFuture(map);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Resource r : resources) {
            if (r != null && r.getResourceId() != null) {
                CompletableFuture<Void> f = availabilityDao.getAvailabilityRules(spaceId, r.getResourceId(), idToken)
                        .thenAccept(res -> {
                            if (res.isSuccess() && res.getData() != null) {
                                map.put(r.getResourceId(), res.getData());
                            } else {
                                map.put(r.getResourceId(), new ArrayList<>());
                            }
                        });
                futures.add(f);
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> map);
    }
}
