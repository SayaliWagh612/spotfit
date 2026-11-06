package com.eklavya.spotfit.view.owner;

import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CustomerProfilePage {

    Scene getScene(Runnable callback) {
        var back = OwnerUi.secondaryButton("\u2190 Back to Customers");
        back.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        // Paste an image link here - shows a placeholder box until you do.
        String CUSTOMER_AVATAR_URL = "";
        Node avatar = OwnerUi.imageOrPlaceholder(CUSTOMER_AVATAR_URL, 64, 64, 32);
        VBox nameBox = new VBox(4, OwnerUi.heading("Aarav Shah", 20), OwnerUi.badge("Gold Tier"));
        header.getChildren().addAll(avatar, nameBox);

        HBox stats = new HBox(16,
                OwnerUi.statCard("Total Bookings", "12", null),
                OwnerUi.statCard("Total Spend", "\u20b918,400", null),
                OwnerUi.statCard("Avg Rating Given", "4.9 \u2605", null),
                OwnerUi.statCard("Last Booking", "Aug 14, 2026", null));

        VBox infoCard = OwnerUi.card();
        infoCard.getChildren().addAll(OwnerUi.heading("Information", 15),
                infoRow("Full Name", "Aarav Shah"),
                infoRow("Mobile", "+91 98765 43210"),
                infoRow("Email", "aarav.shah@example.com"),
                infoRow("VIP Type", "Gold Tier"),
                infoRow("Preferred Category", "Creativity"));

        VBox behaviorCard = OwnerUi.card();
        behaviorCard.getChildren().addAll(OwnerUi.heading("Behavior", 15),
                infoRow("Repeat Rate", "83%"),
                infoRow("Favorite Space", "Creative Hub Studio"));

        HBox cardsRow = new HBox(16, infoCard, behaviorCard);

        VBox content = new VBox(18, back, header, stats, cardsRow);
        content.setPadding(new Insets(24, 0, 0, 0));

        return OwnerUi.pageShell("crm", "Dashboard \u203A Customers \u203A Aarav Shah", "Customer Profile", null, content, null);
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(OwnerUi.fieldLabel(label), spacer, OwnerUi.body(value, 12.5, OwnerUi.TEXT_DARK));
        return row;
    }
}

