package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.ManagerInvitationController;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.session.AppSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.List;

public class StaffDetailsPage {

    private final ManagerInvitationController controller = new ManagerInvitationController();

    public VBox getContent(BorderPane rightPane) {
        VBox root = new VBox(20);

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox nameBox = new VBox(4, OwnerUi.heading("Space Manager Directory", 20),
                OwnerUi.body("Manage assigned managers, view invitation codes, and control space assignments.", 12, OwnerUi.TEXT_MUTED));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        var addBtn = OwnerUi.primaryButton("+ Invite Space Manager");
        addBtn.setMaxWidth(200);
        addBtn.setOnAction(event -> {
            Runnable ran = () -> OwnerUi.navigate(OwnerShell.getScene());
            OwnerUi.navigate(new AddManagerPage().getScene(ran));
        });
        header.getChildren().addAll(nameBox, spacer, addBtn);

        // Active Managers List Container
        VBox managersCard = OwnerUi.card();
        managersCard.setSpacing(12);
        managersCard.getChildren().add(OwnerUi.heading("Active Space Managers", 16));

        Label loadingMgrs = new Label("Loading Managers...");
        managersCard.getChildren().add(loadingMgrs);

        // Sent Invitations List Container
        VBox inviteCard = OwnerUi.card();
        inviteCard.setSpacing(12);
        inviteCard.getChildren().add(OwnerUi.heading("Sent Manager Invitations & Codes", 16));

        Label loadingInvites = new Label("Loading Invitations...");
        inviteCard.getChildren().add(loadingInvites);

        root.getChildren().addAll(header, managersCard, inviteCard);

        if (AppSession.isAuthenticated()) {
            // Load Active Managers
            controller.getOwnerManagers().thenAccept(res -> Platform.runLater(() -> {
                managersCard.getChildren().clear();
                managersCard.getChildren().add(OwnerUi.heading("Active Space Managers", 16));

                if (res.isFailure() || res.getData().isEmpty()) {
                    managersCard.getChildren().add(OwnerUi.body("No active Managers assigned.", 13, OwnerUi.TEXT_MUTED));
                } else {
                    for (Manager mgr : res.getData()) {
                        HBox row = new HBox(16);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setStyle("-fx-background-color:#F8FAFC; -fx-padding:12; -fx-background-radius:10;");

                        String statusText = mgr.isActive() ? "ACTIVE" : "DEACTIVATED";
                        String statusColor = mgr.isActive() ? "#16A34A" : "#DC2626";

                        VBox details = new VBox(4,
                                OwnerUi.heading(mgr.getName() + " (" + statusText + ")", 14),
                                OwnerUi.body("Email: " + mgr.getEmail() + " | Phone: " + mgr.getPhone() + " | Assigned Spaces: " + mgr.getAssignedSpaceIds().size(), 12, OwnerUi.TEXT_MUTED)
                        );

                        Region spc = new Region();
                        HBox.setHgrow(spc, Priority.ALWAYS);

                        Button toggleBtn = OwnerUi.secondaryButton(mgr.isActive() ? "Deactivate" : "Reactivate");
                        toggleBtn.setOnAction(e -> {
                            toggleBtn.setDisable(true);
                            controller.setManagerActiveStatus(mgr.getManagerId(), !mgr.isActive())
                                    .thenRun(() -> Platform.runLater(OwnerShell::refresh));
                        });

                        row.getChildren().addAll(details, spc, toggleBtn);
                        managersCard.getChildren().add(row);
                    }
                }
            }));

            // Load Invitations
            controller.getOwnerInvitations().thenAccept(res -> Platform.runLater(() -> {
                inviteCard.getChildren().clear();
                inviteCard.getChildren().add(OwnerUi.heading("Sent Manager Invitations & Codes", 16));

                if (res.isFailure() || res.getData().isEmpty()) {
                    inviteCard.getChildren().add(OwnerUi.body("No pending invitations sent.", 13, OwnerUi.TEXT_MUTED));
                } else {
                    for (ManagerInvitation inv : res.getData()) {
                        HBox row = new HBox(16);
                        row.setAlignment(Pos.CENTER_LEFT);
                        row.setStyle("-fx-background-color:#FFFBEB; -fx-padding:10; -fx-background-radius:8;");

                        VBox invDetails = new VBox(4,
                                OwnerUi.heading("Recipient: " + inv.getManagerEmail() + " | Status: " + inv.getStatus(), 13),
                                OwnerUi.body("Invite Code: " + inv.getInvitationCode() + " | Assigned Spaces: " + inv.getAssignedSpaceIds().size(), 12, OwnerUi.PRIMARY_CONTAINER)
                        );

                        Region spc = new Region();
                        HBox.setHgrow(spc, Priority.ALWAYS);

                        Button copyBtn = OwnerUi.secondaryButton("Copy Code");
                        copyBtn.setOnAction(e -> {
                            ClipboardContent content = new ClipboardContent();
                            content.putString(inv.getInvitationCode());
                            Clipboard.getSystemClipboard().setContent(content);
                            copyBtn.setText("Copied!");
                        });

                        Button revokeBtn = OwnerUi.secondaryButton("Revoke");
                        revokeBtn.setOnAction(e -> {
                            revokeBtn.setDisable(true);
                            controller.revokeInvitation(inv.getInvitationCode())
                                    .thenRun(() -> Platform.runLater(OwnerShell::refresh));
                        });

                        row.getChildren().addAll(invDetails, spc, copyBtn, revokeBtn);
                        inviteCard.getChildren().add(row);
                    }
                }
            }));
        }

        return root;
    }
}
