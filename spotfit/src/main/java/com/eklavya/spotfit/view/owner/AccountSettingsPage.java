package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerProfileController;
import com.eklavya.spotfit.model.Owner;
import javafx.scene.control.Alert;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class AccountSettingsPage {

    Scene getScene(Runnable callback) {
        OwnerProfileController controller = new OwnerProfileController();
        Owner owner = controller.getCurrentOwner();
        TextField fullName = OwnerUi.textField(""); fullName.setText(owner.getName());
        TextField email = OwnerUi.textField(""); email.setText(owner.getEmail());
        TextField mobile = OwnerUi.textField(""); mobile.setText(owner.getPhone());

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(6);
        grid.addColumn(0, OwnerUi.fieldLabel("Full Name"), fullName);
        grid.addColumn(1, OwnerUi.fieldLabel("Email Address"), email);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }

        VBox profileCard = OwnerUi.card();
        var saveProfile = OwnerUi.primaryButton("Save Account Information");
        saveProfile.setOnAction(e -> {
            String result = controller.updatePersonalProfile(fullName.getText(), email.getText(), mobile.getText(), owner.getCity(), owner.getPreferredLanguage());
            Alert alert = new Alert("SUCCESS".equals(result) ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText("SUCCESS".equals(result) ? "Account information updated." : result); alert.showAndWait();
        });
        profileCard.getChildren().addAll(OwnerUi.heading("Profile", 16), grid,
                OwnerUi.fieldLabel("Mobile Number"), mobile, saveProfile);

        PasswordField current = OwnerUi.passwordField("Current Password");
        PasswordField newPw = OwnerUi.passwordField("New Password");
        PasswordField confirm = OwnerUi.passwordField("Confirm New Password");
        var updateBtn = OwnerUi.primaryButton("Update Password");
        updateBtn.setOnAction(e -> {
            String result = controller.updatePassword(current.getText(), newPw.getText(), confirm.getText());
            Alert alert = new Alert("SUCCESS".equals(result) ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText("SUCCESS".equals(result) ? "Password updated for this session." : result); alert.showAndWait();
        });

        VBox securityCard = OwnerUi.card();
        securityCard.getChildren().addAll(OwnerUi.heading("Password & Security", 16),
                OwnerUi.body("Last changed: 3 months ago", 11.5, OwnerUi.TEXT_MUTED),
                current, newPw, confirm, updateBtn);

        VBox content = new VBox(20, profileCard, securityCard);
        content.setPadding(new Insets(24, 0, 0, 0));
        content.setMaxWidth(640);

        return OwnerUi.pageShell("settings", "Dashboard \u203A Settings", "Account Settings & Security",
                "Manage account information, privacy preferences and security controls.", content, callback);
    }
}

