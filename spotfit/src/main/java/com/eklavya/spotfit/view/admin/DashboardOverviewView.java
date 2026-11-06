package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class DashboardOverviewView {

    public VBox getContent() {
        Label title = heading("Platform Overview & Governance Dashboard", 24);
        Label subtitle = muted(
                "Real-time metric summary, verification queue status, revenue analytics, and system alerts.");

        // Section 1: Top Metrics Grid (13 Metrics)
        HBox row1 = new HBox(16,
                metricCard("👥", "Users", "14,280", "+12% this mo", BLUE_BG, BLUE_TEXT),
                metricCard("🏢", "Owners", "340", "+5 new", GREEN_BG, GREEN_TEXT),
                metricCard("♟", "Managers", "512", "Active 498", CHIP_BG, TEXT),
                metricCard("🏢", "Businesses", "380", "Multi-branch", PURPLE_BG, PURPLE_TEXT));
        HBox row2 = new HBox(16,
                metricCard("📍", "Spaces", "620", "Active 590", CHIP_BG, TEXT),
                metricCard("📋", "Bookings", "48,920", "840 today", GREEN_BG, GREEN_TEXT),
                metricCard("💰", "Gross GBV", "₹1.42 Cr", "Total value", AMBER_BG, AMBER_TEXT),
                metricCard("⚡", "Platform Revenue", "₹21.30 L", "15% take rate", GREEN_BG, GREEN_TEXT));
        HBox row3 = new HBox(16,
                metricCard("⏳", "Pending Verifications", "14", "Requires action", AMBER_BG, AMBER_TEXT),
                metricCard("⚠️", "Open Complaints", "8", "3 high priority", RED_BG, RED_TEXT),
                metricCard("🎫", "Support Tickets", "12", "Open desk", BLUE_BG, BLUE_TEXT),
                metricCard("🚩", "Reported Reviews", "6", "Moderation queue", AMBER_BG, AMBER_TEXT),
                metricCard("💳", "Payment Disputes", "2", "Requires override", RED_BG, RED_TEXT));

        row1.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        row2.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        row3.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox metricsBox = new VBox(14, row1, row2, row3);

        // Section 2: Verification Queue & Action Alerts
        VBox queueCard = createVerificationQueueSummary();
        VBox alertsCard = createSystemAlertsSummary();

        HBox middleRow = new HBox(20, queueCard, alertsCard);
        HBox.setHgrow(queueCard, Priority.ALWAYS);
        HBox.setHgrow(alertsCard, Priority.ALWAYS);

        // Section 3: Revenue Breakdown & Recent Activity Feed
        VBox revenueCard = createRevenueBreakdownSummary();
        VBox activityCard = createActivityFeedSummary();

        HBox bottomRow = new HBox(20, revenueCard, activityCard);
        HBox.setHgrow(revenueCard, Priority.ALWAYS);
        HBox.setHgrow(activityCard, Priority.ALWAYS);

        VBox root = new VBox(24, title, subtitle, metricsBox, middleRow, bottomRow);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private VBox metricCard(String icon, String label, String value, String badgeText, String badgeBg, String badgeFg) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle(
                "-fx-font-size:16px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px; -fx-padding:8;");

        Label badge = chip(badgeText, badgeBg, badgeFg);

        HBox top = new HBox(iconLbl, hgrow(), badge);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox card = card(
                top,
                muted(label),
                heading(value, 22));
        card.setPadding(new Insets(16));
        card.setMinWidth(140);
        return card;
    }

    private VBox createVerificationQueueSummary() {
        Label title = heading("Verification Queue Summary", 18);
        Label sub = muted("14 pending platform verification requests requiring Admin governance.");

        VBox rows = new VBox(10,
                queueRow("Owner Verification", "Rajesh Sharma (UrbanFit Group)", "Pending Document Audit", "PENDING",
                        AMBER_BG, AMBER_TEXT),
                queueRow("Business Listing", "Nexus Wellness & Fitness Hub", "3 New Branches Submitted", "UNDER_REVIEW",
                        BLUE_BG, BLUE_TEXT),
                queueRow("Space Branch", "Pixel Studio Branch B - Satellite", "Safety & License Inspection", "PENDING",
                        AMBER_BG, AMBER_TEXT));

        Button viewQueueBtn = primaryButton("Go to Verification Center →");

        return card(sectionHeader("Verification Queue", null), sub, rows, viewQueueBtn);
    }

    private HBox queueRow(String type, String title, String detail, String status, String bg, String fg) {
        VBox info = new VBox(2, heading(title, 14), muted(type + " · " + detail));
        Label statusChip = chip(status, bg, fg);

        HBox row = new HBox(info, hgrow(), statusChip);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return row;
    }

    private VBox createSystemAlertsSummary() {
        Label sub = muted("Urgent incidents requiring immediate platform intervention.");

        VBox rows = new VBox(10,
                alertRow("🚨 Direct Safety Complaint", "Unsafe equipment report at FitZone Gym B", "HIGH", RED_BG,
                        RED_TEXT),
                alertRow("💳 Razorpay Dispute Override", "Dispute #DSP-9041 requires admin approval", "CRITICAL",
                        RED_BG, RED_TEXT),
                alertRow("🚩 Flagged Review Spike", "5 negative reported reviews on Studio Apex", "MEDIUM", AMBER_BG,
                        AMBER_TEXT));

        return card(sectionHeader("Platform Action Alerts", null), sub, rows);
    }

    private HBox alertRow(String title, String desc, String priority, String bg, String fg) {
        VBox info = new VBox(2, heading(title, 14), muted(desc));
        Label prioChip = chip(priority, bg, fg);

        HBox row = new HBox(info, hgrow(), prioChip);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return row;
    }

    private VBox createRevenueBreakdownSummary() {
        Label title = heading("Platform Revenue & GBV Breakdown", 18);

        VBox stats = new VBox(12,
                detailRow("Gross Booking Value (GBV):", "₹1,42,80,000"),
                detailRow("Platform Commission (15%):", "₹21,42,000"),
                detailRow("Gateway Processing Fees:", "₹2,85,600"),
                detailRow("Net Platform Earnings:", "₹18,56,400"));
        stats.setPadding(new Insets(16));
        stats.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px;");

        return card(title, stats);
    }

    private HBox detailRow(String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label v = new Label(value);
        v.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        HBox r = new HBox(l, hgrow(), v);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }

    private VBox createActivityFeedSummary() {
        Label title = heading("Recent Platform Activity", 18);

        VBox feed = new VBox(10,
                activityItem("Owner Verified", "Admin approved Vikram Shah (Pixel Group)", "12 mins ago"),
                activityItem("Space Suspended", "Admin suspended PowerGym B due to safety complaint", "45 mins ago"),
                activityItem("Refund Issued", "Admin issued ₹1,500 refund for Booking #SPF-8821", "2 hours ago"),
                activityItem("Taxonomy Added", "Admin added new category 'Yoga & Mindfulness'", "4 hours ago"));

        return card(title, feed);
    }

    private HBox activityItem(String title, String desc, String time) {
        VBox info = new VBox(2, heading(title, 14), muted(desc + " · " + time));
        HBox r = new HBox(info);
        r.setPadding(new Insets(8, 12, 8, 12));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:8px;");
        return r;
    }
}

