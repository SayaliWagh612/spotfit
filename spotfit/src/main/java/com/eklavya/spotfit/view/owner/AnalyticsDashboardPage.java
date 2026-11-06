package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerAnalyticsController;
import com.eklavya.spotfit.model.AnalyticsSummary;
import com.eklavya.spotfit.model.Space;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.layout.*;

public class AnalyticsDashboardPage {
    public VBox getContent(BorderPane rightPane) {
        String scope = MockDataStore.getInstance().getSelectedSpaceId();
        Space selected = MockDataStore.getInstance().getSelectedSpace();
        AnalyticsSummary summary = new OwnerAnalyticsController().getAnalyticsSummary(scope);

        VBox scopeLine = new VBox(OwnerUi.body("Current scope · " + (selected == null ? "All Spaces" : selected.getName()), 13, OwnerUi.TEXT_MUTED));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Captured Revenue", money(summary.getTotalRevenue()), "Current scope"),
                OwnerUi.statCard("Bookings", String.valueOf(summary.getTotalBookings()), null),
                OwnerUi.statCard("Occupancy", String.format("%.1f%%", summary.getOccupancyRate()), null),
                OwnerUi.statCard("Satisfaction", String.format("%.1f ★", summary.getCustomerSatisfaction()), null));
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        CategoryAxis x = new CategoryAxis(); NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y); chart.setLegendVisible(false); chart.setPrefHeight(300);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Space space : MockDataStore.getInstance().getSpaces()) {
            if (selected == null || selected.getSpaceId().equals(space.getSpaceId()))
                series.getData().add(new XYChart.Data<>(space.getName(), space.getMonthlyRevenue()));
        }
        chart.getData().add(series);
        VBox chartCard = OwnerUi.card(); chartCard.getChildren().addAll(OwnerUi.heading("Revenue by Space", 17), chart);
        VBox content = new VBox(20, scopeLine, stats, chartCard); content.setPadding(new Insets(0,0,0,0));
        return content;
    }
    private String money(double value) { return "₹" + String.format("%,.0f", value); }
}

