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

public class SpacesView {

    public VBox getContent() {
        Label title = heading("Space Branches & Platform Moderation", 24);
        Label subtitle = muted("Governs Spaces/Branches under Businesses. Verification, Featured toggles, Moderation labels, and Audited Space Interventions.");

        TextField search = searchField("🔍 Search by Space name, Business, City, or Space ID...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Spaces (620)"),
                outlineChip("Approved (590)"),
                outlineChip("Pending (14)"),
                outlineChip("Suspended (10)"),
                outlineChip("Featured (8)")
        );

        VBox spaceList = new VBox(12,
                createSpaceRow("SPC-101", "Pixel Photography Studio - Satellite", "Pixel Media Enterprises (Business)", "Ahmedabad, Gujarat", "Manager: Kavya Patel", "★ 4.9 (128 reviews)", "APPROVED", GREEN_BG, GREEN_TEXT, true),
                createSpaceRow("SPC-102", "Pixel Podcast Studio B - CG Road", "Pixel Media Enterprises (Business)", "Ahmedabad, Gujarat", "Manager: Amit Sharma", "★ 4.8 (84 reviews)", "APPROVED", GREEN_BG, GREEN_TEXT, false),
                createSpaceRow("SPC-201", "UrbanFit Gym & Fitness Branch A", "UrbanFit Group (Business)", "Surat, Gujarat", "Manager: Rohan Joshi", "Pending License Audit", "PENDING", AMBER_BG, AMBER_TEXT, false),
                createSpaceRow("SPC-301", "PowerGym Fitness Suite - SG Highway", "PowerGym Network (Business)", "Ahmedabad, Gujarat", "Manager: Suresh Patel", "Safety Incident Flagged", "SUSPENDED", RED_BG, RED_TEXT, false)
        );

        VBox contentCard = card(filtersRow, spaceList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createSpaceRow(String id, String name, String business, String location, String managerInfo, String rating, String status, String bg, String fg, boolean isFeatured) {
        Label iconLbl = new Label("📍");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox nameBox = new VBox(2,
                new HBox(8, heading(name + " (" + id + ")", 15), isFeatured ? chip("⭐ FEATURED", PURPLE_BG, PURPLE_TEXT) : new Label("")),
                muted(business + " · " + location + " · " + managerInfo)
        );

        Label statusChip = chip(status, bg, fg);

        Button interveneBtn = secondaryButton("Intervene & Moderate");
        interveneBtn.setOnAction(e -> showSpaceInterventionModal(id, name, business, status, isFeatured));

        HBox row = new HBox(16, iconLbl, nameBox, hgrow(), statusChip, interveneBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showSpaceInterventionModal(String id, String name, String business, String status, boolean isFeatured) {
        Label info = muted("Space ID: " + id + " | Business: " + business + " | Status: " + status);

        VBox boundaryNote = new VBox(6,
                heading("Role Boundary Note:", 13),
                muted("Admin governs platform verification, safety, and moderation. Admin cannot edit Owner-controlled content (photos, pricing, resources) directly.")
        );
        boundaryNote.setPadding(new Insets(10));
        boundaryNote.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:8px;");

        Button btnApprove = primaryButton("✔ Approve Space");
        Button btnRequestChanges = secondaryButton("📝 Request Changes");
        Button btnToggleFeatured = secondaryButton(isFeatured ? "★ Unmark Featured" : "⭐ Mark as Featured");
        Button btnSuspend = dangerButton("🔒 Suspend Space");
        Button btnRestore = secondaryButton("↺ Restore Space");

        HBox actionsRow = new HBox(10, btnApprove, btnRequestChanges, btnToggleFeatured, btnSuspend, btnRestore);

        VBox body = new VBox(16, info, boundaryNote, heading("Admin Intervention Actions", 15), actionsRow);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Space Governance - " + name, body);

        btnApprove.setOnAction(e -> {
            dialog.close();
            showNotice("Space Approved", "Space " + name + " approved for public discovery.");
        });

        btnRequestChanges.setOnAction(e -> {
            dialog.close();
            showRequestChangesModal(id, name);
        });

        btnToggleFeatured.setOnAction(e -> {
            dialog.close();
            showNotice("Featured Status Updated", "Space " + name + " featured status updated.");
        });

        btnSuspend.setOnAction(e -> {
            dialog.close();
            showSpaceSuspensionModal(id, name);
        });

        btnRestore.setOnAction(e -> {
            dialog.close();
            showNotice("Space Restored", "Space " + name + " restored to APPROVED status.");
        });
    }

    private void showRequestChangesModal(String id, String name) {
        Label title = heading("Request Changes for Verification", 18);

        Label reasonLbl = heading("Reason for Requesting Changes:", 14);
        TextField reasonField = searchField("e.g. License document blurred / Incomplete fire safety clearance");

        Label actionLbl = heading("Required Corrective Action:", 14);
        TextArea actionArea = new TextArea();
        actionArea.setPromptText("Specify exact steps Owner must take before resubmitting...");
        actionArea.setPrefRowCount(3);

        Button submitBtn = primaryButton("Submit Change Request to Owner");

        VBox body = new VBox(14, title, reasonLbl, reasonField, actionLbl, actionArea, submitBtn);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Request Changes - " + name, body);

        submitBtn.setOnAction(e -> {
            dialog.close();
            showNotice("Change Request Sent", "Corrective request dispatched to Owner. Submission status set to UNDER_REVIEW.");
        });
    }

    private void showSpaceSuspensionModal(String id, String name) {
        Label title = heading("Audited Space Suspension Workflow", 18);
        Label warning = muted("SUSPENSION PROTOCOL EXECUTION:\n1. Space stops accepting new bookings immediately.\n2. Future affected bookings are cancelled.\n3. Gateway payment refunds executed via payment service.\n4. Notifications dispatched to affected Users & Owner.\n5. Immutable audit entry generated.");

        Label reasonLbl = heading("Mandatory Suspension Reason:", 14);
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter legal / safety / policy reason for suspending this space...");
        reasonArea.setPrefRowCount(3);

        CheckBox cbCancelBookings = new CheckBox("Automatically cancel 14 future bookings and initiate full refunds");
        cbCancelBookings.setSelected(true);

        Button confirmBtn = dangerButton("Execute Audited Space Suspension");

        VBox body = new VBox(14, title, warning, reasonLbl, reasonArea, cbCancelBookings, confirmBtn);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Suspend Space - " + name, body);

        confirmBtn.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required! Please fill below:");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showNotice("Space Suspended & Refunds Executed", "Space " + name + " suspended. 14 future bookings cancelled and refunded via Razorpay payment service. Audit log entry recorded.");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Platform Action Completed", notice);
    }
}

