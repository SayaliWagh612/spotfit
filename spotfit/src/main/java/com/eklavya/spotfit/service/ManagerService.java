package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.dao.InvitationDao;
import com.eklavya.spotfit.dao.ManagerDao;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service encapsulating business logic for Manager profiles and activation.
 */
public class ManagerService {

    private final ManagerDao managerDao;
    private final InvitationDao invitationDao;
    private final SpaceDao spaceDao;

    public ManagerService() {
        this.managerDao = new ManagerDao();
        this.invitationDao = new InvitationDao();
        this.spaceDao = new SpaceDao();
    }

    public ManagerService(ManagerDao managerDao, InvitationDao invitationDao) {
        this.managerDao = (managerDao != null) ? managerDao : new ManagerDao();
        this.invitationDao = (invitationDao != null) ? invitationDao : new InvitationDao();
        this.spaceDao = new SpaceDao();
    }

    public ManagerService(ManagerDao managerDao, InvitationDao invitationDao, SpaceDao spaceDao) {
        this.managerDao = (managerDao != null) ? managerDao : new ManagerDao();
        this.invitationDao = (invitationDao != null) ? invitationDao : new InvitationDao();
        this.spaceDao = (spaceDao != null) ? spaceDao : new SpaceDao();
    }

    /**
     * Redeems a valid ManagerInvitation and atomically creates the Manager document while updating
     * the invitation status to ACCEPTED.
     */
    public CompletableFuture<OperationResult<Manager>> activateManagerAccountAtomically(
            String managerId,
            String name,
            String email,
            String phone,
            boolean emailVerified,
            String invitationCode,
            String idToken) {

        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager authenticated UID cannot be blank."));
        }
        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_NAME", "Manager name cannot be blank."));
        }
        if (ValidationUtil.isBlank(email) || !ValidationUtil.isValidEmail(email)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_EMAIL", "A valid email address is required."));
        }
        if (!emailVerified) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("EMAIL_NOT_VERIFIED", "Email address must be verified before Manager profile activation."));
        }

        String normalizedCode = ManagerInvitation.normalizeCode(invitationCode);
        if (ValidationUtil.isBlank(normalizedCode)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        // 1. Fetch invitation by code using authenticated ID token
        return invitationDao.getInvitationByCode(normalizedCode, idToken)
                .thenCompose(invRes -> {
                    if (invRes.isFailure()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure(invRes.getErrorCode(), invRes.getMessage()));
                    }

                    ManagerInvitation invitation = invRes.getData();

                    // Check if invitation is already ACCEPTED
                    if (InvitationStatus.ACCEPTED.equalsIgnoreCase(invitation.getStatus())) {
                        if (managerId.equalsIgnoreCase(invitation.getAcceptedByManagerId())) {
                            // Idempotent recovery: Fetch existing manager document
                            return managerDao.getManager(managerId, idToken)
                                    .thenCompose(mgrRes -> {
                                        if (mgrRes.isSuccess()) {
                                            return CompletableFuture.completedFuture(
                                                    OperationResult.success(mgrRes.getData(), "Manager account is already activated."));
                                        } else {
                                            // Interrupted state recovery: Re-create missing /managers/{uid} document
                                            Manager manager = new Manager(
                                                    managerId,
                                                    invitation.getOwnerId(),
                                                    name.trim(),
                                                    email.trim().toLowerCase(),
                                                    phone != null ? phone.trim() : "",
                                                    invitation.getAssignedSpaceIds(),
                                                    invitation.getPermissions(),
                                                    true,
                                                    true,
                                                    true,
                                                    System.currentTimeMillis(),
                                                    System.currentTimeMillis()
                                            );
                                            return managerDao.createManager(manager, idToken);
                                        }
                                    });
                        } else {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure("INVITATION_ALREADY_ACCEPTED",
                                            "This invitation code has already been accepted by another account."));
                        }
                    }

                    // 2. Validate invitation status
                    if (!InvitationStatus.PENDING.equalsIgnoreCase(invitation.getStatus())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("INVITATION_ALREADY_USED",
                                        "Invitation code is no longer active (status: " + invitation.getStatus() + ")."));
                    }
                    if (invitation.isExpired()) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("INVITATION_EXPIRED", "Invitation code has expired."));
                    }

                    // 3. Verify email match
                    if (!invitation.getManagerEmail().equalsIgnoreCase(email.trim())) {
                        return CompletableFuture.completedFuture(
                                OperationResult.failure("EMAIL_MISMATCH",
                                        "Authenticated email '" + email + "' does not match invitation recipient email."));
                    }

                    // 4. Check if /managers/{uid} already exists prior to pending invitation update
                    return managerDao.getManager(managerId, idToken)
                            .thenCompose(existingMgrRes -> {
                                if (existingMgrRes.isSuccess()) {
                                    Manager existingMgr = existingMgrRes.getData();
                                    if (!invitation.getOwnerId().equals(existingMgr.getOwnerId())) {
                                        return CompletableFuture.completedFuture(
                                                OperationResult.failure("MULTIPLE_OWNERS_UNSUPPORTED",
                                                        "This account is already an active Manager for another Owner."));
                                    }
                                }

                                // Construct Manager profile
                                long now = System.currentTimeMillis();
                                Manager manager = new Manager(
                                        managerId,
                                        invitation.getOwnerId(),
                                        name.trim(),
                                        email.trim().toLowerCase(),
                                        phone != null ? phone.trim() : "",
                                        invitation.getAssignedSpaceIds(),
                                        invitation.getPermissions(),
                                        true,
                                        true,
                                        true,
                                        now,
                                        now
                                );

                                // 5. Execute atomic batch write
                                return managerDao.createManagerAndAcceptInvitationAtomically(manager, invitation, idToken);
                            });
                });
    }

    /**
     * Gets Manager profile.
     */
    public CompletableFuture<OperationResult<Manager>> getManagerProfile(String managerId, String idToken) {
        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }
        return managerDao.getManager(managerId, idToken);
    }

    /**
     * Gets all Managers belonging to an Owner.
     */
    public CompletableFuture<OperationResult<List<Manager>>> getOwnerManagers(String ownerId, String idToken) {
        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_OWNER_ID", "Owner ID cannot be blank."));
        }
        return managerDao.getOwnerManagers(ownerId, idToken);
    }

    /**
     * Owner updates Manager assignments and permissions.
     */
    public CompletableFuture<OperationResult<Void>> updateManagerAssignments(
            String managerId, String ownerId, List<String> newSpaceIds, List<String> newPermissions, List<Space> ownerSpaces, String idToken) {

        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }
        if (newSpaceIds == null || newSpaceIds.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ZERO_SPACES", "At least one assigned Space must be selected."));
        }

        // Verify space ownership
        if (ownerSpaces != null) {
            for (String spaceId : newSpaceIds) {
                boolean found = false;
                for (Space space : ownerSpaces) {
                    if (space != null && spaceId.equals(space.getSpaceId()) && ownerId.equals(space.getOwnerId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return CompletableFuture.completedFuture(
                            OperationResult.failure("FOREIGN_SPACE_REJECTED", "Space ID '" + spaceId + "' does not belong to Owner."));
                }
            }
        }

        return managerDao.updateAssignmentsByOwner(managerId, newSpaceIds, newPermissions, idToken);
    }

    /**
     * Owner changes Manager active status.
     */
    public CompletableFuture<OperationResult<Void>> setManagerActiveStatus(String managerId, boolean active, String idToken) {
        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }
        return managerDao.setManagerActiveByOwner(managerId, active, idToken);
    }

    public CompletableFuture<OperationResult<Void>> setManagerActiveStatus(String managerId, String ownerId, boolean active, String idToken) {
        return setManagerActiveStatus(managerId, active, idToken);
    }

    /**
     * Manager self-update for personal profile (name, phone).
     */
    public CompletableFuture<OperationResult<Void>> updatePersonalProfile(String managerId, String name, String phone, String idToken) {
        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_MANAGER_ID", "Manager ID cannot be blank."));
        }
        if (ValidationUtil.isBlank(name)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_NAME", "Name cannot be blank."));
        }
        return managerDao.updatePersonalProfile(managerId, name, phone, idToken);
    }

    /**
     * Loads assigned Spaces by exact assigned Space IDs for a Manager.
     */
    public CompletableFuture<OperationResult<List<Space>>> getAssignedSpacesForManager(String managerId, String idToken) {
        if (ValidationUtil.isBlank(managerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTHENTICATION_REQUIRED", "Manager ID is required."));
        }
        return getManagerProfile(managerId, idToken).thenCompose(mgrRes -> {
            if (mgrRes.isFailure()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure(mgrRes.getErrorCode(), mgrRes.getMessage()));
            }
            Manager manager = mgrRes.getData();
            if (manager == null || !manager.isActive()) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("MANAGER_INACTIVE", "Manager account is inactive or missing."));
            }
            List<String> assignedSpaceIds = manager.getAssignedSpaceIds();
            if (assignedSpaceIds == null || assignedSpaceIds.isEmpty()) {
                return CompletableFuture.completedFuture(
                        OperationResult.success(Collections.emptyList(), "Manager has no assigned spaces."));
            }

            List<CompletableFuture<OperationResult<Space>>> futures = new ArrayList<>();
            for (String spaceId : assignedSpaceIds) {
                if (!ValidationUtil.isBlank(spaceId)) {
                    futures.add(spaceDao.getSpace(spaceId.trim(), idToken));
                }
            }

            if (futures.isEmpty()) {
                return CompletableFuture.completedFuture(
                        OperationResult.success(Collections.emptyList(), "Manager has no valid assigned space IDs."));
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        List<Space> loadedSpaces = new ArrayList<>();
                        for (CompletableFuture<OperationResult<Space>> fut : futures) {
                            try {
                                OperationResult<Space> res = fut.join();
                                if (res != null && res.isSuccess() && res.getData() != null) {
                                    Space space = res.getData();
                                    if (space.isActive()) {
                                        loadedSpaces.add(space);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        return OperationResult.success(loadedSpaces);
                    });
        });
    }
}
