package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.InvitationService;
import com.eklavya.spotfit.service.ManagerService;
import com.eklavya.spotfit.session.AppSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller handling Owner operations for Manager Invitations and Staff Management.
 */
public class ManagerInvitationController {

    private final InvitationService invitationService;
    private final ManagerService managerService;
    private final SpaceDao spaceDao;

    public ManagerInvitationController() {
        this.invitationService = new InvitationService();
        this.managerService = new ManagerService();
        this.spaceDao = new SpaceDao();
    }

    public ManagerInvitationController(InvitationService invitationService, ManagerService managerService, SpaceDao spaceDao) {
        this.invitationService = (invitationService != null) ? invitationService : new InvitationService();
        this.managerService = (managerService != null) ? managerService : new ManagerService();
        this.spaceDao = (spaceDao != null) ? spaceDao : new SpaceDao();
    }

    public CompletableFuture<OperationResult<ManagerInvitation>> createInvitation(
            String managerEmail, List<String> assignedSpaceIds, List<String> permissions) {

        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        String ownerId = AppSession.getUserId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceDao.getOwnerSpaces(ownerId, idToken)
                    .thenCompose(spaceRes -> {
                        if (spaceRes.isFailure()) {
                            return CompletableFuture.completedFuture(
                                    OperationResult.failure(spaceRes.getErrorCode(), "Failed to load Owner spaces: " + spaceRes.getMessage()));
                        }
                        List<Space> ownerSpaces = spaceRes.getData();
                        return invitationService.createInvitation(ownerId, managerEmail, assignedSpaceIds, permissions, ownerSpaces, idToken);
                    })
        );
    }

    public CompletableFuture<OperationResult<List<ManagerInvitation>>> getOwnerInvitations() {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            invitationService.getOwnerInvitations(AppSession.getUserId(), idToken)
        );
    }

    public CompletableFuture<OperationResult<List<Manager>>> getOwnerManagers() {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            managerService.getOwnerManagers(AppSession.getUserId(), idToken)
        );
    }

    public CompletableFuture<OperationResult<Void>> revokeInvitation(String invitationCode) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            invitationService.revokeInvitation(invitationCode, AppSession.getUserId(), idToken)
        );
    }

    public CompletableFuture<OperationResult<Void>> updateManagerAssignments(
            String managerId, List<String> newSpaceIds, List<String> newPermissions) {

        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        String ownerId = AppSession.getUserId();

        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            spaceDao.getOwnerSpaces(ownerId, idToken)
                    .thenCompose(spaceRes -> {
                        List<Space> ownerSpaces = spaceRes.isSuccess() ? spaceRes.getData() : List.of();
                        return managerService.updateManagerAssignments(managerId, ownerId, newSpaceIds, newPermissions, ownerSpaces, idToken);
                    })
        );
    }

    public CompletableFuture<OperationResult<Void>> setManagerActiveStatus(String managerId, boolean active) {
        if (!AppSession.isAuthenticated()) {
            return CompletableFuture.completedFuture(
                    OperationResult.failure("AUTH_REQUIRED", "Owner authentication is required."));
        }
        return com.eklavya.spotfit.util.AuthenticatedExecutor.execute(idToken ->
            managerService.setManagerActiveStatus(managerId, AppSession.getUserId(), active, idToken)
        );
    }
}
