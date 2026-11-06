package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class CustomersView {

    public VBox getContent(BorderPane rightPane) {

        Label title = heading("Customer Directory", 30);
        Label subtitle = muted("View customer visit history and communicate through SpotFit for Pixel Photography Studio.");
        Label privacyNotice = chip("🔒 Strict Privacy: Phone numbers hidden · Space-only history displayed", CHIP_BG, PRIMARY);

        HBox header = new HBox(new VBox(4, title, subtitle), hgrow(), privacyNotice);
        header.setAlignment(Pos.CENTER_LEFT);

        TextField search = searchField("Search customers by name or email...");

        VBox customerList = new VBox(14,
                customerCard("Neha Shah", "neha.shah@example.com", "7 Visits", "7 Completed", "0 Cancelled", "0 No Shows", "13 Aug 2026", "5 Reviews", "0 Complaints"),
                customerCard("Aarav Mehta", "aarav.m@example.com", "12 Visits", "11 Completed", "1 Cancelled", "0 No Shows", "13 Aug 2026", "8 Reviews", "0 Complaints"),
                customerCard("Riya Kapoor", "riya.kapoor@example.com", "3 Visits", "2 Completed", "0 Cancelled", "1 No Show", "10 Aug 2026", "2 Reviews", "1 Complaint"),
                customerCard("Kabir Jain", "kabir.jain@example.com", "5 Visits", "5 Completed", "0 Cancelled", "0 No Shows", "08 Aug 2026", "4 Reviews", "0 Complaints")
        );

        VBox content = new VBox(20, header, search, card(heading("Customers associated with this space", 18), customerList));
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private HBox customerCard(String name, String email, String visits, String comp, String canc, String noShow, String lastVisit, String reviews, String complaints) {

        Label avatarLabel = avatar(initialsOf(name), 48);

        VBox info = new VBox(4,
                heading(name, 16),
                muted("✉ " + email + "  (SpotFit Verified Customer)"),
                muted("Space Relationship: " + visits + " · Last Visit: " + lastVisit)
        );

        HBox statsPills = new HBox(8,
                chip(comp, GREEN_BG, GREEN_TEXT),
                chip(canc, CHIP_BG, TEXT),
                chip(noShow, RED_BG, RED_TEXT)
        );

        Button msgBtn = primaryButton("💬 SpotFit Message");
        msgBtn.setOnAction(e -> openSpotFitMessageModal(name, email));

        Button historyBtn = secondaryButton("View Space History");
        historyBtn.setOnAction(e -> openCustomerHistoryModal(name, email, visits));

        HBox actions = new HBox(10, msgBtn, historyBtn);

        HBox row = new HBox(avatarLabel, info, hgrow(), statsPills, actions);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16);
        row.setPadding(new Insets(16));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:14px;");
        return row;
    }

    private void openSpotFitMessageModal(String name, String email) {
        TextArea msgArea = new TextArea();
        msgArea.setPromptText("Write in-app message to " + name + "...");
        msgArea.setPrefRowCount(4);
        msgArea.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px;");

        Button sendBtn = primaryButton("Send Message");
        sendBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Sent Customer Message", "Sent SpotFit message to " + name + " (" + email + ")", "CUSTOMER");
            showModal("Message Sent", new VBox(16, heading("✔ Message Sent", 16), muted("Notification delivered to " + name + " via SpotFit app.")));
        });

        VBox body = new VBox(16,
                heading("Contact " + name + " (SpotFit Channel)", 18),
                muted("To protect customer privacy, communication is handled exclusively inside SpotFit."),
                muted("Recipient Email: " + email),
                msgArea,
                sendBtn
        );
        body.setPadding(new Insets(20));
        showModal("SpotFit Customer Message", body);
    }

    private void openCustomerHistoryModal(String name, String email, String visits) {
        VBox historyList = new VBox(10,
                historyRow("SPF-2852", "13 Aug 2026", "Content Shoot", "1:00 PM - 3:00 PM", "UPCOMING"),
                historyRow("SPF-2790", "01 Aug 2026", "Podcast Recording", "11:00 AM - 1:00 PM", "COMPLETED"),
                historyRow("SPF-2641", "15 Jul 2026", "Photo Shoot Floor", "02:00 PM - 5:00 PM", "COMPLETED")
        );

        VBox body = new VBox(16,
                heading(name + "'s History at Pixel Photography Studio", 18),
                muted("Showing customer activity exclusively for this assigned physical space."),
                historyList
        );
        body.setPadding(new Insets(20));
        showModal("Space History - " + name, body);
    }

    private HBox historyRow(String id, String date, String offering, String time, String status) {
        Label idTag = chip(id, CHIP_BG, TEXT);
        VBox info = new VBox(2, heading(offering, 14), muted(date + " · " + time));
        Label st = chip(status, status.equals("COMPLETED") ? GREEN_BG : CHIP_BG, status.equals("COMPLETED") ? GREEN_TEXT : TEXT);

        HBox row = new HBox(12, idTag, info, hgrow(), st);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
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

