package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;

public class BookingDetailsPage {

    public VBox getContent(BorderPane rightPane) {
        var back = OwnerUi.secondaryButton("\u2190 Back to Bookings");
        back.setMaxWidth(220);
        // Space Manager pattern: go back by asking rightPane to show the
        // list page's content again - no captured Scene/callback needed.
        back.setOnAction(event -> rightPane.setCenter(OwnerUi.scroll(new BookingManagementPage().getContent(rightPane))));

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(OwnerUi.heading("BK-2026-1045", 22), OwnerUi.badge("Confirmed"));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Booking Value", "\u20b91,400", null),
                OwnerUi.statCard("Duration", "2 hours", null),
                OwnerUi.statCard("Guests", "4", null),
                OwnerUi.statCard("Payment", "Paid", null));

        VBox customerCard = OwnerUi.card();
        customerCard.getChildren().addAll(OwnerUi.heading("Customer", 15),
                OwnerUi.body("Aarav Shah", 14, OwnerUi.TEXT_DARK),
                OwnerUi.body("aarav.shah@example.com \u00B7 +91 98765 43210", 12, OwnerUi.TEXT_MUTED));

        VBox spaceCard = OwnerUi.card();
        spaceCard.getChildren().addAll(OwnerUi.heading("Space", 15),
                OwnerUi.body("Creative Hub Studio", 14, OwnerUi.TEXT_DARK),
                OwnerUi.body("Aug 18, 2026 \u00B7 10:00 AM - 12:00 PM", 12, OwnerUi.TEXT_MUTED));

        HBox infoRow = new HBox(16, customerCard, spaceCard);

        ChoiceBox<String> statusBox = new ChoiceBox<>();
        statusBox.getItems().addAll("Confirmed", "Ongoing", "Completed", "No Show", "Cancelled");
        statusBox.setValue("Confirmed");
        statusBox.setPrefHeight(48);
        statusBox.setStyle("-fx-background-color:#FFFDFB; -fx-background-radius:14; -fx-border-radius:14;"
                + " -fx-border-color:" + OwnerUi.INPUT_BORDER + "; -fx-font-size:14px;");

        VBox statusCard = OwnerUi.card();
        statusCard.getChildren().addAll(OwnerUi.heading("Update Status", 15), statusBox);

        VBox content = new VBox(18, back, titleRow, stats, infoRow, statusCard);
        content.setPadding(new Insets(24, 0, 0, 0));
        return content;
    }
}

