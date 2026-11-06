package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class ComplaintBoxView {

    private final List<Button> filterBtns = new ArrayList<>();
    private VBox complaintsListContainer;

    public VBox getContent(BorderPane rightPane) {

        Label title = heading("Complaint Box & Incident Reports", 30);
        Label subtitle = muted("Investigate, respond to, and resolve operational customer complaints.");
        Label finNotice = chip("🔒 Financial Action Policy: Manager recommends/escalates · Owner/SpotFit issues refunds", CHIP_BG, PRIMARY);

        HBox header = new HBox(new VBox(4, title, subtitle), hgrow(), finNotice);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox statusFilters = new HBox(10,
                createFilterBtn("ALL", 0),
                createFilterBtn("OPEN", 1),
                createFilterBtn("UNDER REVIEW", 2),
                createFilterBtn("RESOLVED", 3),
                createFilterBtn("ESCALATED", 4)
        );

        complaintsListContainer = new VBox(14);
        loadComplaints("ALL");

        VBox content = new VBox(20, header, statusFilters, card(heading("Submitted Space Complaints", 18), complaintsListContainer));
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private Button createFilterBtn(String label, int index) {
        Button btn = new Button(label);
        boolean active = index == 0;
        btn.setStyle(active
                ? "-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;"
                : "-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT + "; -fx-font-size:13px; -fx-border-color:" + BORDER + "; -fx-border-radius:20px; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");

        filterBtns.add(btn);
        btn.setOnAction(e -> {
            for (int i = 0; i < filterBtns.size(); i++) {
                Button b = filterBtns.get(i);
                if (i == index) {
                    b.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");
                } else {
                    b.setStyle("-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT + "; -fx-font-size:13px; -fx-border-color:" + BORDER + "; -fx-border-radius:20px; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");
                }
            }
            loadComplaints(label);
        });
        return btn;
    }

    private void loadComplaints(String filter) {
        complaintsListContainer.getChildren().clear();

        List<VBox> cards = new ArrayList<>();
        cards.add(complaintCard("CMP-104", "Riya Kapoor", "riya.kapoor@example.com", "SPF-2848", "Air Conditioning noise during podcast recording session.", "OPEN", "OPERATIONAL"));
        cards.add(complaintCard("CMP-102", "Aarav Mehta", "aarav.m@example.com", "SPF-2790", "Overcharged 30 minutes due to clock discrepancy. Requesting partial refund.", "ESCALATED", "FINANCIAL"));
        cards.add(complaintCard("CMP-098", "Dev Patel", "dev.patel@example.com", "SPF-2641", "Microphone stand clamp was loose.", "RESOLVED", "EQUIPMENT"));

        for (VBox c : cards) {
            String cStatus = (String) c.getUserData();
            if (filter.equals("ALL") || filter.equals(cStatus)) {
                complaintsListContainer.getChildren().add(c);
            }
        }
    }

    private VBox complaintCard(String cmpId, String customerName, String email, String bookingId, String complaintText, String status, String category) {

        Label idChip = chip(cmpId, CHIP_BG, TEXT);

        String bg = GREEN_BG, fg = GREEN_TEXT;
        if (status.equals("OPEN")) { bg = CHIP_BG; fg = PRIMARY; }
        else if (status.equals("ESCALATED")) { bg = RED_BG; fg = RED_TEXT; }
        else if (status.equals("UNDER REVIEW")) { bg = BLUE_BG; fg = BLUE_TEXT; }

        Label stChip = chip(status, bg, fg);

        HBox top = new HBox(10, idChip, heading(customerName, 16), muted("· Booking #" + bookingId + " · Category: " + category), hgrow(), stChip);
        top.setAlignment(Pos.CENTER_LEFT);

        Label bodyText = new Label("\"" + complaintText + "\"");
        bodyText.setWrapText(true);
        bodyText.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + ";");

        Button investBtn = secondaryButton("🔍 Investigate & Reply");
        investBtn.setOnAction(e -> openInvestigateModal(cmpId, customerName, complaintText, category));

        Button resolveBtn = primaryButton("✓ Mark Resolved");
        resolveBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Resolved Complaint", "Marked " + cmpId + " resolved", "COMPLAINT");
            showModal("Complaint Resolved", new VBox(16, heading("✔ Complaint " + cmpId + " Marked Resolved", 16), muted("Status updated in system.")));
        });

        Button escalateBtn = secondaryButton("⚡ Escalate to Owner / SpotFit");
        escalateBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Escalated Complaint", "Escalated financial/legal complaint " + cmpId + " to Owner/SpotFit", "COMPLAINT");
            showModal("Escalated to Owner", new VBox(16, heading("⚡ Complaint Escalated to Owner & SpotFit", 16), muted("Financial refund processing assigned to Space Owner.")));
        });

        HBox actions = new HBox(10, investBtn, resolveBtn, escalateBtn);

        VBox cardBox = new VBox(12, top, bodyText, actions);
        cardBox.setPadding(new Insets(16));
        cardBox.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:14px;");
        cardBox.setUserData(status);
        return cardBox;
    }

    private void openInvestigateModal(String cmpId, String name, String text, String category) {
        TextArea noteInput = new TextArea();
        noteInput.setPromptText("Enter investigation notes & resolution message to customer...");
        noteInput.setPrefRowCount(4);
        noteInput.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px;");

        VBox finBox = new VBox();
        if (category.equals("FINANCIAL")) {
            Label fNote = new Label("⚠ Note: This complaint involves financial refund request. Manager cannot process Razorpay refund directly. Recommend refund to Owner / SpotFit.");
            fNote.setWrapText(true);
            fNote.setStyle("-fx-font-size:13px; -fx-text-fill:" + RED_TEXT + "; -fx-background-color:" + RED_BG + "; -fx-padding:10; -fx-background-radius:8px;");
            finBox.getChildren().add(fNote);
        }

        Button saveBtn = primaryButton("Save Resolution Note");
        saveBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Investigated Complaint", "Added investigation note for " + cmpId, "COMPLAINT");
            showModal("Note Saved", new VBox(16, heading("✔ Investigation note saved for " + cmpId, 16), muted("Customer notified via SpotFit app.")));
        });

        VBox body = new VBox(16,
                heading("Investigate Complaint " + cmpId, 18),
                muted("Customer: " + name + " · Complaint: \"" + text + "\""),
                finBox,
                noteInput,
                saveBtn
        );
        body.setPadding(new Insets(20));
        showModal("Investigate Complaint " + cmpId, body);
    }
}

