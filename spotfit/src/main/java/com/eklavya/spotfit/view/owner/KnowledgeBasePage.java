package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;

public class KnowledgeBasePage {

    Scene getScene(Runnable callback) {
        var search = OwnerUi.textField("Search our knowledge base or browse categories below...");

        FlowPane grid = new FlowPane();
        grid.setHgap(16);
        grid.setVgap(16);
        String[][] topics = {
                {"Space Management", "Update listings, manage amenities, and set house rules."},
                {"Booking Management", "Approvals, cancellations, and calendar syncing."},
                {"Payouts & Finance", "Bank accounts, tax documents, and payout schedules."},
                {"Team Management", "Add staff, assign roles, and manage permissions."}
        };
        for (String[] t : topics) grid.getChildren().add(topicCard(t[0], t[1]));

        var backBtn = OwnerUi.secondaryButton("\u2190 Back to Help Center");
        backBtn.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        VBox content = new VBox(20, backBtn, search, grid);
        content.setPadding(new Insets(24, 0, 0, 0));

        return OwnerUi.pageShell("help", "Dashboard \u203A Help & Support", "Knowledge Base & FAQs",
                "How can we help you today?", content, null);
    }

    private VBox topicCard(String title, String desc) {
        VBox card = OwnerUi.card();
        card.setPrefWidth(300);
        card.getChildren().addAll(OwnerUi.heading(title, 15), OwnerUi.body(desc, 12, OwnerUi.TEXT_MUTED));
        return card;
    }
}

