package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

public class NotificationsCenterPage {

    public VBox getContent(BorderPane rightPane) {
        HBox stats = new HBox(16,
                OwnerUi.statCard("Total Notifications", "86", null),
                OwnerUi.statCard("Unread", "12", null),
                OwnerUi.statCard("Action Needed", "3", null),
                OwnerUi.statCard("Direct Messages", "5", null));

        VBox list = new VBox(2,
                notifRow("New Booking Request: Studio A", "Review Request", "Today, 9:15 AM", true),
                notifRow("Payment Successful: \u20b9450.00", "View Receipt", "Today, 8:02 AM", true),
                notifRow("New Message from Michael T.", "Yesterday, 4:30 PM", "Yesterday", false),
                notifRow("Review Received on Creative Hub Studio", "5 stars from Aarav Shah", "2 days ago", false));

        VBox listCard = OwnerUi.card();
        listCard.getChildren().addAll(OwnerUi.heading("Recent Notifications", 16), list);

        VBox content = new VBox(20, stats, listCard);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }

    private HBox notifRow(String title, String detail, String when, boolean unread) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 4, 12, 4));
        if (unread) {
            row.setStyle("-fx-background-color:" + OwnerUi.PRIMARY_LIGHT + "; -fx-background-radius:12;");
        }
        VBox text = new VBox(2, OwnerUi.body(title, 13, OwnerUi.TEXT_DARK), OwnerUi.body(detail, 11.5, OwnerUi.TEXT_MUTED));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(text, spacer, OwnerUi.body(when, 11, OwnerUi.TEXT_MUTED));
        return row;
    }
}

