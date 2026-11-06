package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class SettingsView {

    private final List<Button> tabButtons = new ArrayList<>();
    private VBox tabContainer;
    private String managerName = "Rahul Sharma";

    public VBox getContent(BorderPane rightPane, String managerName) {
        if (managerName != null && !managerName.isEmpty()) {
            this.managerName = managerName;
        }

        Label title = heading("Manager Settings & Audit Log", 30);
        Label subtitle = muted("Manage your manager profile, assigned space credentials, and review your operational Activity Log.");

        HBox tabsRow = new HBox(8,
                createTabBtn("Profile", 0),
                createTabBtn("Security & Password", 1),
                createTabBtn("Notifications", 2),
                createTabBtn("Assigned Space", 3),
                createTabBtn("Activity Log", 4)
        );
        tabsRow.setStyle("-fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1px;");

        tabContainer = new VBox(20);
        selectTab(0);

        VBox content = new VBox(20, title, subtitle, tabsRow, tabContainer);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private Button createTabBtn(String label, int index) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 16; -fx-cursor:hand;");
        btn.setOnAction(e -> selectTab(index));
        tabButtons.add(btn);
        return btn;
    }

    private void selectTab(int index) {
        for (int i = 0; i < tabButtons.size(); i++) {
            Button btn = tabButtons.get(i);
            if (i == index) {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + PRIMARY + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-border-color:transparent transparent " + PRIMARY + " transparent; -fx-border-width:0 0 3px 0; -fx-padding:10 16; -fx-cursor:hand;");
            } else {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 16; -fx-cursor:hand;");
            }
        }

        tabContainer.getChildren().clear();
        switch (index) {
            case 0 -> tabContainer.getChildren().add(createProfileTab());
            case 1 -> tabContainer.getChildren().add(createSecurityTab());
            case 2 -> tabContainer.getChildren().add(createNotificationsTab());
            case 3 -> tabContainer.getChildren().add(createAssignedSpaceTab());
            case 4 -> tabContainer.getChildren().add(createActivityLogTab());
        }
    }

    private VBox createProfileTab() {
        Label avatarLabel = avatar(initialsOf(managerName), 64);
        Button changePhotoBtn = secondaryButton("Change Photo");

        HBox avatarRow = new HBox(16, avatarLabel, new VBox(6, heading(managerName, 18), muted("Space Operational Manager"), changePhotoBtn));
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        TextField tfName = searchField(managerName);
        tfName.setText(managerName);

        TextField tfEmail = searchField("rahul.sharma@spotfit.com");
        tfEmail.setText("rahul.sharma@spotfit.com");

        TextField tfPhone = searchField("+91 98765 00112");
        tfPhone.setText("+91 98765 00112");

        Button saveBtn = primaryButton("Save Profile Changes");
        saveBtn.setOnAction(e -> {
            AuditLogManager.addLog(managerName, "Updated Profile", "Updated manager name & contact details", "PROFILE");
            showModal("Profile Updated", new VBox(16, heading("✔ Profile Saved", 16), muted("Your manager account details have been updated.")));
        });

        return card(heading("Manager Profile Settings", 18), avatarRow,
                new VBox(6, muted("FULL NAME"), tfName),
                new VBox(6, muted("EMAIL ADDRESS"), tfEmail),
                new VBox(6, muted("PHONE NUMBER"), tfPhone),
                saveBtn
        );
    }

    private VBox createSecurityTab() {
        TextField oldPass = searchField("Current Password");
        TextField newPass = searchField("New Password");
        TextField confirmPass = searchField("Confirm New Password");

        Button updatePassBtn = primaryButton("Update Password");
        updatePassBtn.setOnAction(e -> {
            AuditLogManager.addLog(managerName, "Changed Password", "Manager account password updated", "SECURITY");
            showModal("Password Updated", new VBox(16, heading("✔ Password Changed Successfully", 16)));
        });

        return card(heading("Security & Password", 18),
                new VBox(6, muted("CURRENT PASSWORD"), oldPass),
                new VBox(6, muted("NEW PASSWORD"), newPass),
                new VBox(6, muted("CONFIRM NEW PASSWORD"), confirmPass),
                updatePassBtn
        );
    }

    private VBox createNotificationsTab() {
        return card(heading("Notification Preferences", 18),
                notifRow("New Booking Alert", "Receive in-app & bell notification when a customer books", true),
                notifRow("Cancellation Alert", "Receive immediate alert on customer or owner cancellation", true),
                notifRow("Customer Arrival & Check-In", "Notify when customer verifies OTP", true),
                notifRow("New Review Submitted", "Alert when a review is posted for Pixel Photography Studio", true),
                notifRow("Owner Direct Messages", "Alert on space owner communications", true)
        );
    }

    private HBox notifRow(String title, String desc, boolean on) {
        Label toggle = chip(on ? "ON" : "OFF", on ? GREEN_BG : CHIP_BG, on ? GREEN_TEXT : TEXT);
        toggle.setStyle(toggle.getStyle() + " -fx-cursor:hand;");

        HBox row = new HBox(new VBox(4, heading(title, 15), muted(desc)), hgrow(), toggle);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private VBox createAssignedSpaceTab() {
        return card(heading("Assigned Space Information", 18),
                readOnlyField("ASSIGNED SPACE", "Pixel Photography Studio"),
                readOnlyField("LOCATION", "SG Highway, Ahmedabad, Gujarat"),
                readOnlyField("SPACE OWNER", "Vikram Shah (Owner ID: OWN-9921)"),
                readOnlyField("ASSIGNMENT DATE", "15 January 2026"),
                readOnlyField("MANAGER ROLE STATUS", "ACTIVE - Full Operational Access"),
                muted("Note: Only the Space Owner can assign or reassign Managers to physical spaces.")
        );
    }

    private VBox readOnlyField(String label, String val) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label v = new Label(val);
        v.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + "; -fx-background-color:" + BACKGROUND + "; -fx-padding:8 12; -fx-background-radius:8px;");
        return new VBox(4, l, v);
    }

    // =========================================================
    // ACTIVITY / AUDIT LOG TAB
    // =========================================================
    private VBox createActivityLogTab() {
        Label logTitle = heading("Activity & Audit Log", 18);
        Label subtitle = muted("Complete audit trail of operational actions performed by managers for this space.");

        VBox logList = new VBox(12);

        List<AuditLogManager.LogEntry> logs = AuditLogManager.getLogs();
        for (AuditLogManager.LogEntry log : logs) {
            logList.getChildren().add(auditRow(log.timestamp, log.managerName, log.action, log.details, log.category));
        }

        return card(new HBox(logTitle, hgrow(), chip(logs.size() + " Recorded Actions", CHIP_BG, PRIMARY)), subtitle, logList);
    }

    private HBox auditRow(String timestamp, String manager, String action, String details, String category) {
        Label catChip = chip(category, CHIP_BG, PRIMARY);

        VBox info = new VBox(4,
                new HBox(8, heading(action, 15), catChip),
                muted(details),
                muted("By: " + manager + " · " + timestamp)
        );

        HBox row = new HBox(info);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
        return initials;
    }
}

