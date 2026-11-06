package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class AdminAnalyticsView {

    public VBox getContent() {
        Label title = heading("Platform Insights & Analytics", 24);
        Label subtitle = muted("Platform-wide trends, user acquisition growth, space category distribution, and booking metrics.");

        HBox statsRow = new HBox(16,
                analyticCard("📈 Active Users", "14,280", "+18.4% YoY"),
                analyticCard("🏬 Active Spaces", "620", "+12.1% YoY"),
                analyticCard("🔁 Repeat Booking Rate", "64.2%", "+4.2% YoY"),
                analyticCard("⭐ Avg Platform Rating", "4.82 ★", "High Satisfaction")
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox distributionCard = card(
                heading("Top Performing Categories by Booking Volume", 18),
                categoryRow("Photography & Film Studios", "38% of total bookings", "18,590 bookings"),
                categoryRow("Podcast & Audio Suites", "26% of total bookings", "12,720 bookings"),
                categoryRow("Fitness & Gym Spaces", "20% of total bookings", "9,780 bookings"),
                categoryRow("Yoga & Mindfulness Halls", "16% of total bookings", "7,830 bookings")
        );

        VBox root = new VBox(24, title, subtitle, statsRow, distributionCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private VBox analyticCard(String label, String value, String sub) {
        VBox card = card(
                muted(label),
                heading(value, 26),
                chip(sub, GREEN_BG, GREEN_TEXT)
        );
        return card;
    }

    private HBox categoryRow(String catName, String percent, String volume) {
        Label name = heading(catName, 15);
        Label p = muted(percent);
        Label v = heading(volume, 14);

        HBox row = new HBox(14, new VBox(2, name, p), hgrow(), v);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return row;
    }
}

