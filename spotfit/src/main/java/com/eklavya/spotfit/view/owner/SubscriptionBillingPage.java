package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class SubscriptionBillingPage {

    Scene getScene(Runnable callback) {
        VBox planCard = OwnerUi.card();
        planCard.setStyle(planCard.getStyle() + " -fx-background-color:" + OwnerUi.PRIMARY_LIGHT + ";");
        HBox planRow = new HBox(10);
        planRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var upgradeBtn = OwnerUi.secondaryButton("Upgrade Plan");
        planRow.getChildren().addAll(OwnerUi.heading("Professional Plan", 18), spacer, upgradeBtn);
        planCard.getChildren().addAll(planRow, OwnerUi.body("Next renewal on 20 September 2026", 12.5, OwnerUi.TEXT_MUTED));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Monthly Cost", "\u20b92,499", null),
                OwnerUi.statCard("Invoices Generated", "18", null),
                OwnerUi.statCard("Total Billing", "\u20b944,982", null));

        VBox paymentCard = OwnerUi.card();
        paymentCard.getChildren().addAll(OwnerUi.heading("Payment Method", 15),
                OwnerUi.body("VISA ending in 4589 \u00B7 Expiry 08/29 \u00B7 Primary", 12.5, OwnerUi.TEXT_MUTED));

        VBox content = new VBox(20, planCard, stats, paymentCard);
        content.setPadding(new Insets(24, 0, 0, 0));

        return OwnerUi.pageShell("settings", "Dashboard \u203A Settings", "Subscription & Billing", null, content, callback);
    }
}

