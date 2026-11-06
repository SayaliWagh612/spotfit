package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class ComplaintsView {

    public VBox getContent() {
        Label title = heading("Complaints & Incident Governance", 24);
        Label subtitle = muted("Manage formal platform complaints. Direct-to-Admin bypass for Safety, Fraud, Payment Disputes, and Owner misconduct.");

        TextField search = searchField("🔍 Search by Complaint ID (CMP-xxxx), User, Space, or Category...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Complaints (8)"),
                outlineChip("Direct-to-Admin (3)"),
                outlineChip("Escalated (2)"),
                outlineChip("Open (3)"),
                outlineChip("Resolved (240)")
        );

        VBox complaintList = new VBox(12,
                createComplaintRow("CMP-901", "🚨 Safety Incident - Unsafe Gym Equipment", "FitZone Gym Branch B", "User: Siddharth Rao", "DIRECT-TO-ADMIN BYPASS", "ESCALATED", RED_BG, RED_TEXT),
                createComplaintRow("CMP-902", "💳 Payment Dispute - Double Charged", "Pixel Studio Satellite", "User: Neha Shah", "DIRECT-TO-ADMIN BYPASS", "UNDER_REVIEW", BLUE_BG, BLUE_TEXT),
                createComplaintRow("CMP-903", "♟ Manager Misconduct - Unauthorized Refusal", "Aura Yoga Studio", "User: Karan Verma", "STANDARD ROUTING (ESCALATED)", "OPEN", AMBER_BG, AMBER_TEXT),
                createComplaintRow("CMP-904", "🏢 Owner Misconduct - Facility Mismatch", "PowerGym Network", "User: Aarav Mehta", "DIRECT-TO-ADMIN BYPASS", "RESOLVED", GREEN_BG, GREEN_TEXT)
        );

        VBox contentCard = card(filtersRow, complaintList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createComplaintRow(String id, String issueTitle, String target, String user, String routingType, String status, String bg, String fg) {
        Label iconLbl = new Label("⚠️");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox info = new VBox(2,
                heading(id + " · " + issueTitle, 15),
                muted(target + " · " + user + " · " + routingType)
        );

        Label statusChip = chip(status, bg, fg);

        Button actionBtn = primaryButton("Investigate & Action");
        actionBtn.setOnAction(e -> showComplaintModal(id, issueTitle, target, user, routingType, status));

        HBox row = new HBox(16, iconLbl, info, hgrow(), statusChip, actionBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showComplaintModal(String id, String issueTitle, String target, String user, String routingType, String currentStatus) {
        Label info = muted("Complaint ID: " + id + " | Target: " + target + " | Routing: " + routingType);

        VBox detailBox = new VBox(8,
                heading("Incident Investigation Summary", 15),
                muted("• Title: " + issueTitle),
                muted("• Submitted By: " + user),
                muted("• Assigned Admin: Governance Desk (admin@spotfit.com)"),
                muted("• Supporting Evidence: 2 Photo Uploads attached")
        );
        detailBox.setPadding(new Insets(12));
        detailBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Label responseLbl = heading("Admin Response / Resolution Note:", 14);
        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Enter official Admin response or investigation findings...");
        responseArea.setPrefRowCount(3);

        Button btnInvestigate = secondaryButton("Set UNDER_REVIEW");
        Button btnEscalate = secondaryButton("Set ESCALATED");
        Button btnResolve = primaryButton("✔ Resolve Complaint");
        Button btnClose = secondaryButton("Close Complaint");
        Button btnReopen = secondaryButton("Reopen Complaint");

        HBox btns = new HBox(10, btnInvestigate, btnEscalate, btnResolve, btnClose, btnReopen);

        VBox body = new VBox(14, info, detailBox, responseLbl, responseArea, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Investigate Complaint - " + id, body);

        btnInvestigate.setOnAction(e -> { dialog.close(); showNotice("Status Updated", "Complaint " + id + " status set to UNDER_REVIEW."); });
        btnEscalate.setOnAction(e -> { dialog.close(); showNotice("Complaint Escalated", "Complaint " + id + " status set to ESCALATED. Flagged to Senior Admin."); });
        btnResolve.setOnAction(e -> { dialog.close(); showNotice("Complaint Resolved", "Complaint " + id + " resolved. Action response logged to audit trail."); });
        btnClose.setOnAction(e -> { dialog.close(); showNotice("Complaint Closed", "Complaint " + id + " closed."); });
        btnReopen.setOnAction(e -> { dialog.close(); showNotice("Complaint Reopened", "Complaint " + id + " reopened for further review."); });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Complaint Governance Action", notice);
    }
}

