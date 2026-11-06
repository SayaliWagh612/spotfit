package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class ReportsView {

    public VBox getContent() {
        Label title = heading("Platform Financial & Operational Reports", 24);
        Label subtitle = muted("Exportable platform reports for Gross Booking Value (GBV), platform commission, tax compliance, and business performance.");

        HBox reportCards = new HBox(16,
                reportTile("📊 Platform Revenue Report", "Monthly breakdown of platform commission earnings, Razorpay fee subtractions, and net revenue.", "Export PDF"),
                reportTile("💰 GBV & Transaction Report", "Complete log of Gross Booking Value, refunded bookings, and user booking volumes.", "Export CSV"),
                reportTile("⚖ Tax & Compliance Audit", "GST breakdown, Owner payout GST withholdings, and platform compliance audit file.", "Export Excel")
        );
        reportCards.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox summaryBox = card(
                heading("Monthly Revenue & GBV Summary (August 2026)", 18),
                detailRow("Gross Booking Value (GBV):", "₹1,42,80,000"),
                detailRow("Platform Revenue (15%):", "₹21,42,000"),
                detailRow("Total Completed Bookings:", "48,920"),
                detailRow("Average Booking Value:", "₹292"),
                detailRow("Total Refunds Issued:", "₹42,500 (120 refunds)")
        );

        VBox root = new VBox(24, title, subtitle, reportCards, summaryBox);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private VBox reportTile(String title, String desc, String btnText) {
        Button btn = primaryButton(btnText);
        btn.setOnAction(e -> showNotice("Report Downloaded", title + " has been generated and exported successfully."));

        VBox tile = card(
                heading(title, 17),
                muted(desc),
                btn
        );
        return tile;
    }

    private HBox detailRow(String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:14px; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label v = new Label(value);
        v.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        HBox r = new HBox(l, hgrow(), v);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Report Export", notice);
    }
}

