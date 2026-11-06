package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class ManagersView {

    public VBox getContent() {
        Label title = heading("Space Manager Governance", 24);
        Label subtitle = muted("Governance rule: One Manager account per Space. Admin may view or disable a Manager. Disabling blocks login & preserves history without altering assignment.");

        TextField search = searchField("🔍 Search by Manager name, assigned Space, email, or ID...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Managers (512)"),
                outlineChip("Active (498)"),
                outlineChip("Disabled (14)")
        );

        VBox managerList = new VBox(12,
                createManagerRow("MGR-301", "Kavya Patel", "kavya@spotfit.com", "Pixel Photography Studio (Space #101)", "Vikram Shah (Owner)", "ACTIVE", GREEN_BG, GREEN_TEXT),
                createManagerRow("MGR-302", "Amit Sharma", "amit@spotfit.com", "Pixel Podcast Studio B (Space #102)", "Vikram Shah (Owner)", "ACTIVE", GREEN_BG, GREEN_TEXT),
                createManagerRow("MGR-303", "Rohan Joshi", "rohan@spotfit.com", "UrbanFit Gym - Drive-In (Space #201)", "Rajesh Sharma (Owner)", "DISABLED", RED_BG, RED_TEXT),
                createManagerRow("MGR-304", "Meera Desai", "meera@spotfit.com", "Aura Yoga Studio - Bodakdev (Space #301)", "Ananya Malhotra (Owner)", "ACTIVE", GREEN_BG, GREEN_TEXT)
        );

        VBox contentCard = card(filtersRow, managerList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createManagerRow(String id, String name, String email, String space, String owner, String status, String bg, String fg) {
        Label avatarLbl = avatar(initialsOf(name), 42);

        VBox nameBox = new VBox(2,
                heading(name + " (" + id + ")", 15),
                muted(email + " · Owner: " + owner)
        );

        VBox spaceBox = new VBox(2,
                heading("Assigned Space:", 12),
                muted(space)
        );

        Label statusChip = chip(status, bg, fg);

        Button actionBtn = secondaryButton(status.equals("DISABLED") ? "Enable Login" : "Disable Account");
        actionBtn.setOnAction(e -> showManagerGovernanceModal(id, name, space, owner, status));

        HBox row = new HBox(16, avatarLbl, nameBox, hgrow(), spaceBox, statusChip, actionBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showManagerGovernanceModal(String id, String name, String space, String owner, String currentStatus) {
        Label info = muted("Manager ID: " + id + " | Space: " + space + " | Owner: " + owner);

        VBox ruleBox = new VBox(8,
                heading("Platform Manager Rules:", 14),
                muted("1. One Manager account is strictly assigned to one Space."),
                muted("2. Disabling a Manager blocks login and preserves all historical check-in/booking records."),
                muted("3. Disabling does NOT alter Manager's assignment (only Owner reassigns)."),
                muted("4. Disabling does NOT suspend the associated Space.")
        );
        ruleBox.setPadding(new Insets(12));
        ruleBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Button disableBtn = dangerButton("Disable Manager Account");
        Button enableBtn = primaryButton("Enable Manager Account");

        HBox actionRow = currentStatus.equals("DISABLED") ? new HBox(enableBtn) : new HBox(disableBtn);

        VBox body = new VBox(16, info, ruleBox, actionRow);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Manager Governance - " + name, body);

        disableBtn.setOnAction(e -> {
            dialog.close();
            showNotice("Manager Disabled", "Manager " + name + " account disabled. Login blocked; historical records and space assignment preserved.");
        });

        enableBtn.setOnAction(e -> {
            dialog.close();
            showNotice("Manager Enabled", "Manager " + name + " account reactivated.");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Governance Completed", notice);
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String init = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) init += parts[1].substring(0, 1).toUpperCase();
        return init;
    }
}

