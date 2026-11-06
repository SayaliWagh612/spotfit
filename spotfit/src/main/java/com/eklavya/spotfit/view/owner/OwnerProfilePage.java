package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerProfileController;
import com.eklavya.spotfit.model.Owner;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class OwnerProfilePage {
    private final OwnerProfileController controller = new OwnerProfileController();

    public VBox getContent(BorderPane rightPane) {
        Owner owner = controller.getCurrentOwner();
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Personal Profile", personalTab(owner)),
                new Tab("Business Profile", businessTab(owner)),
                new Tab("Security", securityTab()),
                new Tab("Account Overview", overviewTab(owner))
        );
        tabs.setPrefHeight(620);
        VBox content = new VBox(18, profileHeader(owner), tabs);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }

    private HBox profileHeader(Owner owner) {
        Label avatar = new Label(initials(owner.getName()));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(74, 74);
        avatar.setStyle("-fx-background-color:" + OwnerUi.PRIMARY_LIGHT + "; -fx-background-radius:999; -fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + OwnerUi.PRIMARY + ";");
        VBox info = new VBox(4, OwnerUi.heading(owner.getName(), 20),
                OwnerUi.body(owner.getBusinessName(), 13, OwnerUi.TEXT_MUTED), OwnerUi.badge(owner.getVerificationStatus()));
        HBox row = new HBox(16, avatar, info);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox personalTab(Owner owner) {
        TextField name = field(owner.getName());
        TextField email = field(owner.getEmail());
        TextField phone = field(owner.getPhone());
        TextField city = field(owner.getCity());
        ComboBox<String> language = new ComboBox<>();
        language.getItems().addAll("English", "Hindi", "Marathi", "Gujarati");
        language.setValue(owner.getPreferredLanguage());
        language.setMaxWidth(Double.MAX_VALUE);

        Button save = OwnerUi.primaryButton("Save Personal Profile");
        Button cancel = OwnerUi.secondaryButton("Reset Changes");
        cancel.setOnAction(e -> { name.setText(owner.getName()); email.setText(owner.getEmail()); phone.setText(owner.getPhone()); city.setText(owner.getCity()); language.setValue(owner.getPreferredLanguage()); });
        save.setOnAction(e -> {
            String result = controller.updatePersonalProfile(name.getText(), email.getText(), phone.getText(), city.getText(), language.getValue());
            message(result, "Personal profile updated. Dashboard and profile headers will now use the new details.");
        });
        return formCard("Personal Information", row("Full Name", name, "Email Address", email),
                row("Phone Number", phone, "City", city), labelled("Preferred Language", language), new HBox(10, cancel, save));
    }

    private VBox businessTab(Owner owner) {
        TextField name = field(owner.getBusinessName());
        TextField email = field(owner.getBusinessEmail());
        TextField phone = field(owner.getBusinessPhone());
        TextArea address = OwnerUi.textArea("Registered business address", 3); address.setText(owner.getBusinessAddress());
        Button save = OwnerUi.primaryButton("Save Business Profile");
        save.setOnAction(e -> message(controller.updateBusinessProfile(name.getText(), email.getText(), phone.getText(), address.getText()), "Business profile updated."));
        return formCard("Business Information", row("Business Name", name, "Business Email", email),
                labelled("Business Phone", phone), labelled("Registered Address", address), save);
    }

    private VBox securityTab() {
        PasswordField current = OwnerUi.passwordField("Current password");
        PasswordField next = OwnerUi.passwordField("New password");
        PasswordField confirm = OwnerUi.passwordField("Confirm new password");
        Button update = OwnerUi.primaryButton("Update Password");
        update.setOnAction(e -> {
            String result = controller.updatePassword(current.getText(), next.getText(), confirm.getText());
            message(result, "Password updated for this mock session.");
            if ("SUCCESS".equals(result)) { current.clear(); next.clear(); confirm.clear(); }
        });
        return formCard("Password & Security", labelled("Current Password", current),
                row("New Password", next, "Confirm Password", confirm), update);
    }

    private VBox overviewTab(Owner owner) {
        return formCard("Account Overview",
                info("Owner ID", owner.getOwnerId()), info("Account Status", owner.getAccountStatus()),
                info("Verification", owner.getVerificationStatus()), info("Member Since", owner.getJoinedDate()),
                OwnerUi.body("Authentication and email verification will be connected when Firebase is implemented.", 12, OwnerUi.TEXT_MUTED));
    }

    private VBox formCard(String title, javafx.scene.Node... nodes) {
        VBox card = OwnerUi.card(); card.setMaxWidth(820); card.getChildren().add(OwnerUi.heading(title, 17)); card.getChildren().addAll(nodes); return card;
    }
    private TextField field(String value) { TextField f = OwnerUi.textField(""); f.setText(value == null ? "" : value); return f; }
    private VBox labelled(String label, javafx.scene.Node field) { return new VBox(6, OwnerUi.fieldLabel(label), field); }
    private HBox row(String l1, javafx.scene.Node f1, String l2, javafx.scene.Node f2) { HBox row = new HBox(14, labelled(l1, f1), labelled(l2, f2)); row.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS)); return row; }
    private HBox info(String label, String value) { HBox row = new HBox(OwnerUi.body(label, 12, OwnerUi.TEXT_MUTED), spacer(), OwnerUi.body(value == null ? "—" : value, 13, OwnerUi.TEXT_DARK)); return row; }
    private Region spacer() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }
    private String initials(String name) { if (name == null || name.isBlank()) return "O"; String[] p = name.trim().split("\\s+"); return p[0].substring(0,1).toUpperCase() + (p.length > 1 ? p[1].substring(0,1).toUpperCase() : ""); }
    private void message(String result, String success) { Alert a = new Alert("SUCCESS".equals(result) ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR); a.setHeaderText("SUCCESS".equals(result) ? "Saved successfully" : "Please correct the form"); a.setContentText("SUCCESS".equals(result) ? success : result); a.showAndWait(); }
}

