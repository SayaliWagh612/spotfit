package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;
import com.eklavya.spotfit.controller.manager.ManagerDashboardController;
import com.eklavya.spotfit.controller.manager.ManagerCustomerController;
import com.eklavya.spotfit.controller.manager.ManagerReviewController;
import com.eklavya.spotfit.model.Booking;

public class AnalyticsView {    

    private final List<Button> filterBtns = new ArrayList<>();
    private VBox metricsGrid;

    public VBox getContent(BorderPane rightPane) {

        Label title = heading("Operational Analytics", 30);
        Label subtitle = muted("Real-time space occupancy, booking performance, and customer utilization metrics.");
        Label noRevTag = chip("🔒 Operational Mode (Revenue & Payouts managed by Owner)", CHIP_BG, PRIMARY);

        HBox header = new HBox(new VBox(4, title, subtitle), hgrow(), noRevTag);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox timeFilters = new HBox(10,
                createFilterBtn("Today", 0),
                createFilterBtn("This Week", 1),
                createFilterBtn("This Month", 2),
                createFilterBtn("Last Month", 3),
                createFilterBtn("Custom Date Range", 4)
        );

        metricsGrid = createMetricsForPeriod("This Month");

        VBox content = new VBox(20, header, timeFilters, metricsGrid);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private Button createFilterBtn(String label, int index) {
        Button btn = new Button(label);
        boolean active = index == 1; // Default "This Month"
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
            metricsGrid.getChildren().clear();
            metricsGrid.getChildren().add(createMetricsForPeriod(label));
        });
        return btn;
    }

    private VBox createMetricsForPeriod(String period) {
        ManagerDashboardController dashboard = new ManagerDashboardController();
        int total = dashboard.getTodaysBookings().size();
        long checkedIn = dashboard.getTodaysBookings().stream().filter(b -> "CHECKED_IN".equals(b.getStatus())).count();
        long completed = dashboard.getTodaysBookings().stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
        long cancelled = dashboard.getTodaysBookings().stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
        int resources = dashboard.getResources().size();
        long occupied = dashboard.getResources().stream().filter(r -> "OCCUPIED".equals(r.getStatus())).count();
        double occupancy = resources == 0 ? 0 : occupied * 100.0 / resources;
        double reviewAvg = new ManagerReviewController().getReviews().stream().mapToInt(r -> r.getRating()).average().orElse(0);
        HBox topStats = new HBox(16,
                analyticStatCard("📋 Total Bookings", "148", "Period: " + period),
                analyticStatCard("📊 Live Occupancy", String.format("%.1f%%", occupancy), occupied + " occupied resources"),
                analyticStatCard("✔ Active / Completed", (checkedIn + completed) + "", "Operational sessions"),
                analyticStatCard("⭐ Customer Review Avg.", String.format("%.1f / 5.0", reviewAvg), "Assigned-space reviews")
        );
        topStats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        HBox middleStats = new HBox(16,
                analyticStatCard("⏱ Avg. Session Duration", "2.1 Hours", "Most popular slot: 2 Hours"),
                analyticStatCard("⚠ Cancellations", String.valueOf(cancelled), "Current static dataset"),
                analyticStatCard("🚫 No-Show Rate", "1.8%", "2 no-shows recorded"),
                analyticStatCard("👥 Unique Visitors", new ManagerCustomerController().getSpaceCustomers().size() + " Customers", "Assigned space only")
        );
        middleStats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox popularOfferingsCard = card(
                heading("Popular Space Offerings (" + period + ")", 18),
                offeringBar("Video Podcast Suite", 52, "52 Bookings (35%)"),
                offeringBar("Commercial Photography", 41, "41 Bookings (28%)"),
                offeringBar("Audio Recording", 33, "33 Bookings (22%)"),
                offeringBar("Editing Room", 22, "22 Bookings (15%)")
        );

        return new VBox(20, topStats, middleStats, popularOfferingsCard);
    }

    private VBox analyticStatCard(String title, String val, String sub) {
        VBox card = new VBox(8, muted(title), heading(val, 26), muted(sub));
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:18px; -fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:18px;");
        return card;
    }

    private VBox offeringBar(String name, int count, String detail) {
        Label n = heading(name, 15);
        Label d = muted(detail);

        HBox top = new HBox(n, hgrow(), d);
        top.setAlignment(Pos.CENTER_LEFT);

       // double pct = count / 60.0;
        Label bar = new Label();
        bar.setPrefHeight(8);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-background-color:" + PRIMARY + "; -fx-background-radius:4px;");

        VBox box = new VBox(6, top, bar);
        return box;
    }
}

