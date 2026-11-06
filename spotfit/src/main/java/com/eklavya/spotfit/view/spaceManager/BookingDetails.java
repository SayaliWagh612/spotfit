package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class BookingDetails {

    public VBox getContent(BorderPane rightPane) {

        Label backLink = link("← Back to Bookings");
        backLink.setOnMouseClicked(e -> rightPane.setCenter(scroll(new BookingManagement().getContent(rightPane))));

        HBox titleRow = new HBox(12, heading("Booking Details", 28), chip("UPCOMING", CHIP_BG, TEXT));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Button checkInBtn = primaryButton("✓ Verify OTP Check-In");
        checkInBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Checked In", "Checked in Neha Shah (SPF-2852)", "CHECK_IN");
            showModal("Check-In Confirmed", new VBox(16, heading("✔ Customer Checked In", 16), muted("Status updated to Active Session.")));
        });

        HBox actionsRow = new HBox(10, hgrow(), secondaryButton("✎ Edit Notes"), checkInBtn);
        HBox header = new HBox(new VBox(6, titleRow, muted("# Booking ID: SPF-2852")), hgrow(), actionsRow);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox leftColumn = new VBox(20, createBookingInfoCard(), createCustomerCard(), createNotesCard());
        VBox rightColumn = new VBox(20, createPaymentCard(), createTimelineCard(), createQuickActionsCard());
        rightColumn.setPrefWidth(340);
        rightColumn.setMinWidth(340);

        HBox columns = new HBox(20, leftColumn, rightColumn);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox content = new VBox(20, backLink, header, columns);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private VBox createBookingInfoCard() {
        VBox grid = new VBox(16,
                new HBox(40, infoField("BOOKING OFFERING", "📷 Content Shoot Floor"), infoField("ASSIGNED RESOURCE", "Studio B (Podcast Room)")),
                new HBox(40, infoField("DATE", "📅 Thu, 13 Aug 2026"), infoField("TIME SLOT", "🕐 1:00 PM - 3:00 PM (2 hours)")),
                new HBox(40, infoField("GUESTS", "👤 4 People"), infoField("CHECK-IN STATUS", "Expected Arrival: 12:55 PM"))
        );
        Label createdLabel = muted("Created on 11 Aug 2026, 09:42 AM via SpotFit App");
        return card(heading("ⓘ Booking Details", 18), grid, createdLabel);
    }

    private VBox infoField(String label, String value) {
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        return new VBox(6, labelText, valueText);
    }

    private VBox createCustomerCard() {
        Label viewProfile = link("View Profile →");
        HBox header = sectionHeader("👤 Customer Details", viewProfile);

        Label name = heading("Neha Shah", 16);
        Label email = muted("✉ neha.shah@example.com  (SpotFit Verified Account)");
        Label privacyTag = chip("🔒 Phone number hidden for privacy", CHIP_BG, PRIMARY);
        VBox customerInfo = new VBox(4, name, email, privacyTag);

        HBox customerRow = new HBox(14, avatar("NS", 56), customerInfo);
        customerRow.setAlignment(Pos.CENTER_LEFT);

        VBox loyaltyBox = new VBox(4, muted("SPACE RELATIONSHIP"), heading("7 Visits Here", 16));
        loyaltyBox.setPadding(new Insets(14));
        loyaltyBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:14px;");

        HBox bodyRow = new HBox(20, customerRow, hgrow(), loyaltyBox);
        bodyRow.setAlignment(Pos.CENTER_LEFT);

        return card(header, bodyRow);
    }

    private VBox createNotesCard() {
        Label addNote = link("+ Add Note");
        HBox header = sectionHeader("📝 Customer Instructions", addNote);

        Label noteLabel = new Label("\"Bringing lighting stands and a small backdrop. Need access to power outlets near the window.\"");
        noteLabel.setWrapText(true);
        noteLabel.setStyle("-fx-font-size:14px; -fx-font-style:italic; -fx-text-fill:" + TEXT + ";");

        VBox noteBox = new VBox(8, muted("CUSTOMER NOTE"), noteLabel);
        noteBox.setPadding(new Insets(16));
        noteBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:0 12px 12px 0; -fx-border-color:" + PRIMARY + " transparent transparent transparent; -fx-border-width:0 0 0 4px;");

        return card(header, noteBox);
    }

    private VBox createPaymentCard() {
        VBox paidBox = new VBox(4,
                heading("✓ PAID IN FULL", 16),
                muted("Payment Status: CONFIRMED"),
                chip("🔒 Sensitive Razorpay Details Hidden", BACKGROUND, SECONDARY_TEXT)
        );
        paidBox.setPadding(new Insets(16));
        paidBox.setStyle("-fx-background-color:" + GREEN_BG + "; -fx-background-radius:14px;");
        return card(heading("💳 Payment Status", 17), paidBox);
    }

    private VBox createTimelineCard() {
        VBox timeline = new VBox(16,
                timelineItem("Arrival Pending", "Expected on 13 Aug, 12:55 PM", false),
                timelineItem("OTP Generated", "Ready for customer check-in", true),
                timelineItem("Payment Confirmed", "11 Aug 2026, 09:45 AM", true),
                timelineItem("Booking Created", "11 Aug 2026, 09:42 AM", true)
        );
        return card(heading("🕐 Activity Timeline", 17), timeline);
    }

    private HBox timelineItem(String title, String subtitle, boolean done) {
        Label dot = new Label(done ? "✓" : "●");
        dot.setStyle("-fx-font-size:12px; -fx-text-fill:" + (done ? PRIMARY : SECONDARY_TEXT) + "; -fx-background-color:" + (done ? CHIP_BG : "transparent")
                + "; -fx-background-radius:50%; -fx-min-width:22px; -fx-min-height:22px; -fx-alignment:center; -fx-border-color:" + BORDER + "; -fx-border-radius:50%;");

        VBox textBox = new VBox(2, heading(title, 14), muted(subtitle));
        HBox row = new HBox(12, dot, textBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox createQuickActionsCard() {
        Button contactButton = secondaryButton("💬 SpotFit Message");
        contactButton.setMaxWidth(Double.MAX_VALUE);
        contactButton.setOnAction(e -> openContactModal("Neha Shah", "neha.shah@example.com"));

        Button reportButton = secondaryButton("⚠ Report Incident");
        reportButton.setMaxWidth(Double.MAX_VALUE);
        reportButton.setStyle(reportButton.getStyle() + " -fx-text-fill:" + RED_TEXT + "; -fx-border-color:" + RED_TEXT + ";");
        reportButton.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Reported Incident", "Incident reported for SPF-2852", "COMPLAINT");
            showModal("Incident Reported", new VBox(16, heading("Report Submitted", 16), muted("Logged to incident report file.")));
        });

        return card(heading("⚡ Quick Actions", 17), contactButton, reportButton);
    }

    private void openContactModal(String name, String email) {
        TextArea msgArea = new TextArea();
        msgArea.setPromptText("Write in-app SpotFit message to " + name + "...");
        msgArea.setPrefRowCount(4);
        msgArea.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px;");

        Button sendBtn = primaryButton("Send SpotFit Message");
        sendBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Sent Customer Message", "Sent message to " + name, "CUSTOMER");
            showModal("Message Sent", new VBox(16, heading("✔ Message Sent", 16), muted("Customer notified via SpotFit app.")));
        });

        VBox body = new VBox(14,
                heading("Contact Customer: " + name, 18),
                muted("Recipient Email: " + email + " (Direct phone numbers hidden for privacy)"),
                msgArea,
                sendBtn
        );
        body.setPadding(new Insets(20));
        showModal("SpotFit Messaging Channel", body);
    }
}
