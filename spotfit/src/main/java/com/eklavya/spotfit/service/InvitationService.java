package com.eklavya.spotfit.service;

import com.eklavya.spotfit.constants.InvitationStatus;
import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.dao.InvitationDao;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.util.ValidationUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service encapsulating business logic for Manager Invitations.
 */
public class InvitationService {

    private final InvitationDao invitationDao;

    public InvitationService() {
        this.invitationDao = new InvitationDao();
    }

    public InvitationService(InvitationDao invitationDao) {
        this.invitationDao = (invitationDao != null) ? invitationDao : new InvitationDao();
    }

    /**
     * Validates Owner invitation request and saves the invitation under /managerInvitations/{code}.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> createInvitation(
            String ownerId,
            String managerEmail,
            List<String> assignedSpaceIds,
            List<String> permissions,
            List<Space> ownerSpaces,
            String idToken) {

        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_OWNER_ID", "Owner ID cannot be blank."));
        }
        if (ValidationUtil.isBlank(managerEmail) || !ValidationUtil.isValidEmail(managerEmail)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_EMAIL", "A valid Manager email address is required."));
        }
        if (assignedSpaceIds == null || assignedSpaceIds.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ZERO_SPACES", "At least one assigned Space must be selected for the Manager."));
        }
        if (permissions == null || permissions.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("ZERO_PERMISSIONS", "At least one operational permission must be selected for the Manager."));
        }

        // Verify every assigned Space belongs to the inviting Owner
        if (ownerSpaces == null || ownerSpaces.isEmpty()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("NO_OWNER_SPACES", "Owner has no valid Spaces to assign."));
        }

        for (String spaceId : assignedSpaceIds) {
            boolean found = false;
            for (Space space : ownerSpaces) {
                if (space != null && spaceId.equals(space.getSpaceId()) && ownerId.equals(space.getOwnerId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return CompletableFuture.completedFuture(
                        OperationResult.failure("FOREIGN_SPACE_REJECTED", "Space ID '" + spaceId + "' does not belong to the inviting Owner."));
            }
        }

        ManagerInvitation invitation = new ManagerInvitation(ownerId, managerEmail, assignedSpaceIds, permissions);

        if (ValidationUtil.isBlank(invitation.getInvitationCode())) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_CODE", "Generated invitation code cannot be blank."));
        }
        if (!invitation.isPending()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_STATUS", "Initial invitation status must be PENDING."));
        }
        if (!invitation.isActive()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INACTIVE_INVITATION", "Newly created invitation must be active."));
        }
        if (invitation.getCreatedAt() <= 0) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_TIMESTAMP", "Invitation createdAt timestamp is invalid."));
        }
        if (invitation.getExpiresAt() <= invitation.getCreatedAt()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_EXPIRATION", "Invitation expiration timestamp must be strictly after creation timestamp."));
        }

        return invitationDao.createInvitation(invitation, idToken);
    }

    /**
     * Performs direct lookup for /managerInvitations/{normalizedCode}.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> getInvitationByCode(String rawCode, String idToken) {
        String normalizedCode = ManagerInvitation.normalizeCode(rawCode);
        if (ValidationUtil.isBlank(normalizedCode)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        return invitationDao.getInvitationByCode(normalizedCode, idToken)
                .thenApply(res -> {
                    if (res.isSuccess()) {
                        ManagerInvitation inv = res.getData();
                        if (!InvitationStatus.PENDING.equals(inv.getStatus())) {
                            return OperationResult.failure("INVITATION_NOT_PENDING", "Invitation is no longer active (status: " + inv.getStatus() + ").");
                        }
                        if (inv.isExpired()) {
                            return OperationResult.failure("INVITATION_EXPIRED", "Invitation code has expired.");
                        }
                        return OperationResult.success(inv);
                    } else {
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }
                });
    }

    /**
     * Lists invitations for an Owner.
     */
    public CompletableFuture<OperationResult<List<ManagerInvitation>>> getOwnerInvitations(String ownerId, String idToken) {
        if (ValidationUtil.isBlank(ownerId)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_OWNER_ID", "Owner ID cannot be blank."));
        }
        return invitationDao.getOwnerInvitations(ownerId, idToken);
    }

    /**
     * Revokes an invitation created by the Owner.
     */
    public CompletableFuture<OperationResult<Void>> revokeInvitation(String rawCode, String ownerId, String idToken) {
        String normalizedCode = ManagerInvitation.normalizeCode(rawCode);
        if (ValidationUtil.isBlank(normalizedCode)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_CODE", "Invitation code cannot be blank."));
        }

        return invitationDao.getInvitationByCode(normalizedCode, idToken)
                .thenCompose(res -> {
                    if (res.isFailure()) {
                        return CompletableFuture.completedFuture(OperationResult.failure(res.getErrorCode(), res.getMessage()));
                    }
                    ManagerInvitation inv = res.getData();
                    if (!ownerId.equals(inv.getOwnerId())) {
                        return CompletableFuture.completedFuture(OperationResult.failure("UNAUTHORIZED_REVOKE", "You can only revoke your own invitations."));
                    }
                    if (InvitationStatus.ACCEPTED.equals(inv.getStatus())) {
                        return CompletableFuture.completedFuture(OperationResult.failure("CANNOT_REVOKE_ACCEPTED", "Cannot revoke an already accepted invitation."));
                    }
                    return invitationDao.revokeInvitation(normalizedCode, idToken);
                });
    }

    /**
     * Validates invitation readiness for Manager registration before Auth user creation.
     */
    public CompletableFuture<OperationResult<ManagerInvitation>> validateInvitationForRegistration(String rawCode, String formEmail) {
        String normalizedCode = ManagerInvitation.normalizeCode(rawCode);
        if (ValidationUtil.isBlank(normalizedCode)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_INVITATION_CODE", "A valid invitation code is required."));
        }
        if (ValidationUtil.isBlank(formEmail) || !ValidationUtil.isValidEmail(formEmail)) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("INVALID_EMAIL", "A valid Manager email address is required."));
        }

        String targetEmail = formEmail.trim().toLowerCase();
        String safeInvalidMsg = "This invitation is invalid, expired, already used, or does not match the entered email.";

        return invitationDao.getInvitationForRegistration(normalizedCode)
                .thenApply(res -> {
                    if (res.isFailure()) {
                        String errCode = res.getErrorCode();
                        if (OperationCode.NOT_FOUND.equals(errCode) ||
                                OperationCode.PERMISSION_DENIED.equals(errCode) ||
                                OperationCode.AUTHENTICATION_REQUIRED.equals(errCode) ||
                                "NOT_FOUND".equals(errCode) || "PERMISSION_DENIED".equals(errCode)) {
                            return OperationResult.failure("INVITATION_NOT_FOUND", safeInvalidMsg);
                        }
                        return OperationResult.failure(res.getErrorCode(), res.getMessage());
                    }

                    ManagerInvitation inv = res.getData();
                    if (inv == null) {
                        return OperationResult.failure("INVITATION_NOT_FOUND", safeInvalidMsg);
                    }
                    if (inv.isExpired() || inv.getExpiresAt() <= System.currentTimeMillis()) {
                        return OperationResult.failure("INVITATION_EXPIRED", "Invitation code has expired.");
                    }
                    if (!InvitationStatus.PENDING.equalsIgnoreCase(inv.getStatus())) {
                        return OperationResult.failure("INVITATION_NOT_PENDING", "Invitation is no longer PENDING (status: " + inv.getStatus() + ").");
                    }
                    if (!inv.isActive()) {
                        return OperationResult.failure("INACTIVE_INVITATION", "Invitation code is inactive.");
                    }
                    if (inv.getAcceptedAt() > 0 || (inv.getAcceptedByManagerId() != null && !inv.getAcceptedByManagerId().isBlank())) {
                        return OperationResult.failure("INVITATION_ALREADY_ACCEPTED", "Invitation has already been accepted.");
                    }
                    if (inv.getManagerEmail() == null || !inv.getManagerEmail().trim().equalsIgnoreCase(targetEmail)) {
                        return OperationResult.failure("EMAIL_MISMATCH", "Entered email address does not match the invitation recipient.");
                    }
                    if (inv.getOwnerId() == null || inv.getOwnerId().isBlank()) {
                        return OperationResult.failure("INVALID_OWNER", "Invitation is missing owner details.");
                    }
                    if (inv.getAssignedSpaceIds() == null || inv.getAssignedSpaceIds().isEmpty()) {
                        return OperationResult.failure("ZERO_SPACES", "Invitation contains no assigned spaces.");
                    }
                    if (inv.getPermissions() == null || inv.getPermissions().isEmpty()) {
                        return OperationResult.failure("ZERO_PERMISSIONS", "Invitation contains no operational permissions.");
                    }

                    return OperationResult.success(inv, "Invitation is valid and ready for registration.");
                });
    }
}
