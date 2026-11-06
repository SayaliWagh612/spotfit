package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.controller.owner.ManagerInvitationController;
import com.eklavya.spotfit.dao.SpaceDao;
import com.eklavya.spotfit.model.ManagerInvitation;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.session.AppSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddManagerPage {

    private final ManagerInvitationController controller = new ManagerInvitationController();
    private final SpaceDao spaceDao = new SpaceDao();

    Scene getScene(Runnable callback) {
        var back = OwnerUi.secondaryButton("\u2190 Back");
        back.setOnAction(event -> {
            if (callback != null) callback.run();
        });

        Label title = OwnerUi.heading("Invite Space Manager", 22);
        Label sub = OwnerUi.body("Generate a cryptographically secure invitation code for a Manager and assign target Spaces & permissions.", 13, OwnerUi.TEXT_MUTED);

        VBox spacesBox = new VBox(6);
        Map<String, CheckBox> spaceCheckBoxes = new HashMap<>();

        Label loadingSpacesLabel = new Label("Loading your owned Spaces...");
        spacesBox.getChildren().add(loadingSpacesLabel);

        // Fetch spaces asynchronously
        if (AppSession.isAuthenticated()) {
            spaceDao.getOwnerSpaces(AppSession.getUserId(), AppSession.getIdToken())
                    .thenAccept(res -> Platform.runLater(() -> {
                        spacesBox.getChildren().clear();
                        if (res.isSuccess() && !res.getData().isEmpty()) {
                            for (Space space : res.getData()) {
                                CheckBox cb = new CheckBox(space.getName() + " (" + space.getCategory() + ")");
                                cb.setMnemonicParsing(false);
                                cb.setStyle("-fx-font-size:13px; -fx-text-fill:#1E293B;");
                                cb.setSelected(true); // Default select
                                spaceCheckBoxes.put(space.getSpaceId(), cb);
                                spacesBox.getChildren().add(cb);
                            }
                        } else {
                            spacesBox.getChildren().add(new Label("No Spaces found. Please create a Space before inviting a Manager."));
                        }
                    }));
        }

        TextField email = OwnerUi.textField("manager@example.com");

        // Permissions checklist
        VBox permissionsBox = new VBox(6);
        Map<String, CheckBox> permCheckBoxes = new HashMap<>();
        List<String> defaultPerms = ManagerPermission.getDefaultPermissions();
        for (String perm : defaultPerms) {
            CheckBox cb = new CheckBox(ManagerPermission.getDisplayLabel(perm));
            cb.setMnemonicParsing(false);
            cb.setStyle("-fx-font-size:12px; -fx-text-fill:#475569;");
            cb.setSelected(true);
            permCheckBoxes.put(perm, cb);
            permissionsBox.getChildren().add(cb);
        }

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        var sendBtn = OwnerUi.primaryButton("Generate & Copy Invitation Code \u2192");
        sendBtn.setOnAction(event -> {
            msgLabel.setText("Generating invitation...");
            msgLabel.setStyle("-fx-text-fill:" + OwnerUi.PRIMARY + ";");
            sendBtn.setDisable(true);

            List<String> selectedSpaceIds = new ArrayList<>();
            for (Map.Entry<String, CheckBox> entry : spaceCheckBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedSpaceIds.add(entry.getKey());
                }
            }

            List<String> selectedPermissions = new ArrayList<>();
            for (Map.Entry<String, CheckBox> entry : permCheckBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedPermissions.add(entry.getKey());
                }
            }

            controller.createInvitation(email.getText().trim(), selectedSpaceIds, selectedPermissions)
                    .thenAccept(res -> Platform.runLater(() -> {
                        sendBtn.setDisable(false);
                        if (res.isFailure()) {
                            String errSummary = res.getErrorCode() != null ? res.getErrorCode() : "UNKNOWN_ERROR";
                            msgLabel.setText("Failed to create invitation (" + errSummary + "). Please try again.");
                            msgLabel.setStyle("-fx-text-fill:" + OwnerUi.ERROR + ";");
                        } else {
                            ManagerInvitation inv = res.getData();
                            String code = inv.getInvitationCode();

                            // Automatically copy code to clipboard
                            ClipboardContent content = new ClipboardContent();
                            content.putString(code);
                            Clipboard.getSystemClipboard().setContent(content);

                            msgLabel.setText("SUCCESS! Code copied to clipboard: " + code);
                            msgLabel.setStyle("-fx-text-fill:#16A34A;");

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Manager Invitation Created");
                            alert.setHeaderText("Invitation Code Generated & Copied!");
                            alert.setContentText("Invitation Code: " + code + "\n\nRecipient: " + inv.getManagerEmail() +
                                    "\nAssigned Spaces: " + inv.getAssignedSpaceIds().size() +
                                    "\n\nThe code has been copied to your clipboard. Share this code securely with the Manager.");
                            alert.showAndWait();

                            if (callback != null) callback.run();
                        }
                    }));
        });

        VBox form = new VBox(14, title, sub,
                OwnerUi.fieldLabel("Manager Email *"), email,
                OwnerUi.fieldLabel("Assigned Spaces *"), spacesBox,
                OwnerUi.fieldLabel("Manager Operational Permissions"), permissionsBox,
                msgLabel, sendBtn);

        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox content = new VBox(14, back, formCard);
        content.setPadding(new Insets(24, 0, 0, 0));
        content.setMaxWidth(680);

        return OwnerUi.pageShell("staff", "Dashboard / Space Managers", "Invite Manager",
                "Generate a high-entropy invitation code for a Manager.", content, null);
    }
}
