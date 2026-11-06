package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Owner Staff Controller routing legacy & view calls to the asynchronous ManagerInvitationController.
 */
public class OwnerManagerController {

    private final ManagerInvitationController invitationController;

    public OwnerManagerController() {
        this.invitationController = new ManagerInvitationController();
    }

    public OwnerManagerController(ManagerInvitationController invitationController) {
        this.invitationController = (invitationController != null) ? invitationController : new ManagerInvitationController();
    }

    public CompletableFuture<OperationResult<List<Manager>>> getManagers() {
        return invitationController.getOwnerManagers();
    }

    public CompletableFuture<OperationResult<List<ManagerInvitation>>> getInvitations() {
        return invitationController.getOwnerInvitations();
    }

    public CompletableFuture<OperationResult<ManagerInvitation>> inviteManager(
            String managerEmail, List<String> assignedSpaceIds, List<String> permissions) {
        return invitationController.createInvitation(managerEmail, assignedSpaceIds, permissions);
    }

    public CompletableFuture<OperationResult<Void>> revokeInvitation(String invitationCode) {
        return invitationController.revokeInvitation(invitationCode);
    }

    public CompletableFuture<OperationResult<Void>> updateManagerAssignments(
            String managerId, List<String> newSpaceIds, List<String> newPermissions) {
        return invitationController.updateManagerAssignments(managerId, newSpaceIds, newPermissions);
    }

    public CompletableFuture<OperationResult<Void>> setManagerActiveStatus(String managerId, boolean active) {
        return invitationController.setManagerActiveStatus(managerId, active);
    }
}
