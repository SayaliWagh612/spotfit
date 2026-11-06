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

public class VerificationView {

    public VBox getContent() {
        Label title = heading("Trust & Safety - Verification Queue", 24);
        Label subtitle = muted("3-Level Independent Verification (Owner, Business, Space). Note: Verified Owner does NOT auto-approve Business or Space.");

        HBox filtersRow = new HBox(12,
                outlineChip("All Submissions (14)"),
                outlineChip("Level 1: Owner (4)"),
                outlineChip("Level 2: Business (5)"),
                outlineChip("Level 3: Space (5)"),
                hgrow(),
                outlineChip("Status: PENDING")
        );

        VBox queueList = new VBox(12,
                createVerificationItem("SUB-901", "Level 1: Owner Verification", "Rajesh Sharma (UrbanFit Group)", "Submitted GST, PAN, Aadhaar identity documents", "OWNER", "PENDING", AMBER_BG, AMBER_TEXT),
                createVerificationItem("SUB-902", "Level 2: Business Verification", "Nexus Wellness & Fitness Hub", "Business registration, FSSAI, Trade license", "BUSINESS", "UNDER_REVIEW", BLUE_BG, BLUE_TEXT),
                createVerificationItem("SUB-903", "Level 3: Space Branch", "Pixel Studio Branch B - Satellite", "Fire safety certificate, Lease agreement, Photos", "SPACE", "PENDING", AMBER_BG, AMBER_TEXT),
                createVerificationItem("SUB-904", "Level 3: Space Branch", "Aura Yoga Bodakdev Branch", "Building permission & Occupancy certificate", "SPACE", "UNDER_REVIEW", BLUE_BG, BLUE_TEXT)
        );

        VBox contentCard = card(filtersRow, queueList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createVerificationItem(String subId, String levelTitle, String entityName, String docSummary, String levelType, String status, String bg, String fg) {
        Label iconLbl = new Label("🔍");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox info = new VBox(2,
                heading(subId + " · " + levelTitle, 15),
                muted(entityName + " · " + docSummary)
        );

        Label levelChip = chip(levelType, CHIP_BG, TEXT);
        Label statusChip = chip(status, bg, fg);

        Button reviewBtn = primaryButton("Review Submission");
        reviewBtn.setOnAction(e -> showReviewModal(subId, levelTitle, entityName, docSummary, levelType));

        HBox row = new HBox(16, iconLbl, info, hgrow(), levelChip, statusChip, reviewBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showReviewModal(String subId, String levelTitle, String entityName, String docSummary, String levelType) {
        Label header = muted("Submission ID: " + subId + " | Target Entity: " + entityName);

        VBox docsBox = new VBox(8,
                heading("Uploaded Documentation & Compliance Audit", 15),
                muted("• Document Summary: " + docSummary),
                muted("• Acting Admin: Admin User (admin@spotfit.com)"),
                muted("• Identity / License Status: Validated against government registry"),
                muted("• Independent Governance: Approving this level does not auto-approve downstream entities.")
        );
        docsBox.setPadding(new Insets(14));
        docsBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Label noteLbl = heading("Internal Admin Note:", 14);
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Add internal notes visible only to Admin team...");
        noteArea.setPrefRowCount(2);

        Button btnApprove = primaryButton("✔ Approve Submission");
        Button btnRequestChanges = secondaryButton("📝 Request Changes / Action");
        Button btnReject = dangerButton("✕ Reject Submission");

        HBox actionBtns = new HBox(10, btnApprove, btnRequestChanges, btnReject);

        VBox body = new VBox(16, header, docsBox, noteLbl, noteArea, actionBtns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Review Submission - " + subId, body);

        btnApprove.setOnAction(e -> {
            dialog.close();
            showNotice("Submission Approved", levelTitle + " for " + entityName + " approved. Status set to VERIFIED / APPROVED.");
        });

        btnRequestChanges.setOnAction(e -> {
            dialog.close();
            showDetailedRejectionModal(subId, entityName, "REQUEST_CHANGES");
        });

        btnReject.setOnAction(e -> {
            dialog.close();
            showDetailedRejectionModal(subId, entityName, "REJECT");
        });
    }

    private void showDetailedRejectionModal(String subId, String entityName, String type) {
        Label title = heading(type.equals("REJECT") ? "Reject Verification Submission" : "Request Corrective Action", 18);
        Label note = muted("Requirement: Mandatory Reason, Required Corrective Action, Timestamp, Acting Admin, and Affected Submission.");

        Label reasonLbl = heading("Mandatory Reason:", 14);
        TextField reasonField = searchField("e.g. Invalid trade license expiry / Blurred ID proof");

        Label actionLbl = heading("Required Corrective Action (Owner Resubmission instructions):", 14);
        TextArea actionArea = new TextArea();
        actionArea.setPromptText("Detailed instructions for Owner to resubmit corrected documents...");
        actionArea.setPrefRowCount(3);

        Button submitBtn = dangerButton("Submit Decision");

        VBox body = new VBox(14, title, note, reasonLbl, reasonField, actionLbl, actionArea, submitBtn);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Verification Decision - " + subId, body);

        submitBtn.setOnAction(e -> {
            if (reasonField.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required!");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showNotice("Decision Recorded", "Decision recorded for " + subId + ". Notification dispatched to Owner. Resubmission enabled.");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Verification Action Complete", notice);
    }
}

