package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class OwnersView {

    public VBox getContent() {
        Label title = heading("Owner Governance & Verification", 24);
        Label subtitle = muted("Verify business owners, inspect compliance history, suspend/restore owners, and explicitly select affected spaces for suspension.");

        TextField search = searchField("🔍 Search by Owner name, Business entity, email, or ID...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Owners (340)"),
                outlineChip("Verified (310)"),
                outlineChip("Pending (14)"),
                outlineChip("Suspended (16)")
        );

        VBox ownerList = new VBox(12,
                createOwnerRow("OWN-201", "Vikram Shah", "Pixel Media Enterprises", "vikram@pixelmedia.com", "2 Businesses · 5 Spaces", "Compliance Score: 98%", "VERIFIED", GREEN_BG, GREEN_TEXT),
                createOwnerRow("OWN-202", "Rajesh Sharma", "UrbanFit Group", "rajesh@urbanfit.com", "1 Business · 3 Spaces", "Pending Audit", "PENDING", AMBER_BG, AMBER_TEXT),
                createOwnerRow("OWN-203", "Ananya Malhotra", "Aura Wellness Co.", "ananya@aurawellness.com", "1 Business · 2 Spaces", "Under Verification Review", "UNDER_REVIEW", BLUE_BG, BLUE_TEXT),
                createOwnerRow("OWN-204", "Suresh Patel", "PowerGym Network", "suresh@powergym.com", "1 Business · 4 Spaces", "Safety Incident Flagged", "SUSPENDED", RED_BG, RED_TEXT)
        );

        VBox contentCard = card(filtersRow, ownerList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createOwnerRow(String id, String name, String business, String email, String summary, String compliance, String status, String bg, String fg) {
        Label avatarLbl = avatar(initialsOf(name), 42);

        VBox nameBox = new VBox(2,
                heading(name + " (" + id + ")", 15),
                muted(business + " · " + email)
        );

        VBox detailBox = new VBox(2,
                heading(summary, 13),
                muted(compliance)
        );

        Label statusChip = chip(status, bg, fg);

        Button manageBtn = secondaryButton("Govern & Verify");
        manageBtn.setOnAction(e -> showOwnerGovernanceModal(id, name, business, email, status));

        HBox row = new HBox(16, avatarLbl, nameBox, hgrow(), detailBox, statusChip, manageBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showOwnerGovernanceModal(String id, String name, String business, String email, String status) {
        Label info = muted("Owner ID: " + id + " | Entity: " + business + " | Current Status: " + status);

        Button btnApprove = primaryButton("✔ Set VERIFIED");
        Button btnReject = secondaryButton("✕ Reject Verification");
        Button btnSuspend = dangerButton("🔒 Suspend Owner");
        Button btnRestore = secondaryButton("↺ Restore Owner");

        HBox actionBtns = new HBox(10, btnApprove, btnReject, btnSuspend, btnRestore);

        VBox body = new VBox(16, info, heading("Verification & Status Actions", 16), actionBtns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Owner Governance - " + name, body);

        btnApprove.setOnAction(e -> {
            dialog.close();
            showReasonNotice("Owner Verified", "Owner " + name + " verified successfully. Audit entry created.");
        });

        btnReject.setOnAction(e -> {
            dialog.close();
            showReasonNotice("Owner Verification Rejected", "Verification for " + name + " rejected. Corrective notification dispatched.");
        });

        btnSuspend.setOnAction(e -> {
            dialog.close();
            showOwnerSuspensionSpaceSelectionModal(id, name, business);
        });

        btnRestore.setOnAction(e -> {
            dialog.close();
            showReasonNotice("Owner Restored", "Owner " + name + " restored to ACTIVE/VERIFIED state.");
        });
    }

    private void showOwnerSuspensionSpaceSelectionModal(String id, String name, String business) {
        Label title = heading("Owner Suspension & Space Selection", 18);
        Label note = muted("Locked Rule: Owner suspension does NOT automatically suspend every space. Explicitly select affected spaces.");

        CheckBox cbSpace1 = new CheckBox("Branch 1: Pixel Photography Studio - Satellite (Active)");
        CheckBox cbSpace2 = new CheckBox("Branch 2: Pixel Podcast Studio B - CG Road (Active)");
        CheckBox cbSpace3 = new CheckBox("Branch 3: Pixel Meeting Suites - Vastrapur (Active)");
        cbSpace1.setSelected(true);

        VBox spaceBox = new VBox(8, heading("Select Affected Spaces for Suspension:", 14), cbSpace1, cbSpace2, cbSpace3);
        spaceBox.setPadding(new Insets(12));
        spaceBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Label reasonLbl = heading("Mandatory Reason & Audit Note:", 14);
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter explicit reason for owner suspension and space selection...");
        reasonArea.setPrefRowCount(3);

        Button confirmBtn = dangerButton("Confirm Owner & Space Suspension");

        VBox body = new VBox(16, title, note, spaceBox, reasonLbl, reasonArea, confirmBtn);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Owner Suspension - " + name, body);

        confirmBtn.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required! Please fill below:");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showReasonNotice("Suspension Executed", "Owner " + name + " suspended. Selected spaces flagged. Cancellation and refund processing triggered if future bookings exist. Immutable audit entry logged.");
        });
    }

    private void showReasonNotice(String title, String msg) {
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

