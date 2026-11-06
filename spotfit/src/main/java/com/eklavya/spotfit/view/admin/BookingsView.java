package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class BookingsView {

    public VBox getContent() {
        Label title = heading("Platform Bookings Governance", 24);
        Label subtitle = muted("Inspect all platform bookings, execute Admin status overrides, trigger refund workflows, or link to complaints.");

        TextField search = searchField("🔍 Search by Booking ID (SPF-xxxx), User, Space, or Payment Ref...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Bookings (48,920)"),
                outlineChip("Upcoming (18,400)"),
                outlineChip("Completed (28,500)"),
                outlineChip("Disputed (120)"),
                outlineChip("Cancelled (1,900)")
        );

        VBox bookingList = new VBox(12,
                createBookingRow("SPF-2852", "Neha Shah", "Pixel Photography Studio - Satellite", "Content Shoot Floor", "₹3,500", "13 Aug 2026 (1:00 PM - 3:00 PM)", "CONFIRMED", GREEN_BG, GREEN_TEXT),
                createBookingRow("SPF-2841", "Aarav Mehta", "Pixel Podcast Studio B - CG Road", "Video Podcast Suite", "₹2,200", "13 Aug 2026 (9:00 AM - 11:00 AM)", "COMPLETED", BLUE_BG, BLUE_TEXT),
                createBookingRow("SPF-2790", "Karan Verma", "FitZone Gym - Branch B", "Personal Training Floor", "₹1,800", "12 Aug 2026 (4:00 PM - 5:00 PM)", "DISPUTED", RED_BG, RED_TEXT),
                createBookingRow("SPF-2650", "Siddharth Rao", "Aura Yoga Studio", "Group Meditation Room", "₹900", "10 Aug 2026 (7:00 AM - 8:00 AM)", "CANCELLED", CHIP_BG, TEXT)
        );

        VBox contentCard = card(filtersRow, bookingList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createBookingRow(String id, String user, String space, String offering, String amount, String timeSlot, String status, String bg, String fg) {
        Label iconLbl = new Label("📋");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox nameBox = new VBox(2,
                heading(id + " · " + user, 15),
                muted(space + " (" + offering + ") · " + timeSlot)
        );

        VBox amountBox = new VBox(2,
                heading(amount, 14),
                muted("Razorpay Paid")
        );

        Label statusChip = chip(status, bg, fg);

        Button actionBtn = secondaryButton("Admin Intervention");
        actionBtn.setOnAction(e -> showBookingInterventionModal(id, user, space, offering, amount, status));

        HBox row = new HBox(16, iconLbl, nameBox, hgrow(), amountBox, statusChip, actionBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showBookingInterventionModal(String id, String user, String space, String offering, String amount, String status) {
        Label info = muted("Booking ID: " + id + " | Customer: " + user + " | Amount: " + amount + " | Status: " + status);

        VBox historyBox = new VBox(6,
                heading("Audit History Requirements:", 13),
                muted("Every intervention requires: Mandatory Reason, Acting Admin, Previous/New Status, and Refund link.")
        );
        historyBox.setPadding(new Insets(10));
        historyBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:8px;");

        Label reasonLbl = heading("Mandatory Intervention Reason:", 14);
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for overriding booking status or triggering refund...");
        reasonArea.setPrefRowCount(3);

        Button btnOverrideStatus = primaryButton("Override Status");
        Button btnCancelRefund = dangerButton("Cancel & Trigger Refund Workflow");
        Button btnLinkComplaint = secondaryButton("Link to Complaint / Dispute");

        HBox btns = new HBox(10, btnOverrideStatus, btnCancelRefund, btnLinkComplaint);

        VBox body = new VBox(14, info, historyBox, reasonLbl, reasonArea, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Booking Admin Intervention - " + id, body);

        btnOverrideStatus.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required!");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showNotice("Status Overridden", "Booking " + id + " status successfully updated. Record saved to audit log.");
        });

        btnCancelRefund.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required!");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showNotice("Booking Cancelled & Refund Initiated", "Booking " + id + " cancelled. Refund request of " + amount + " submitted to Razorpay payment service.");
        });

        btnLinkComplaint.setOnAction(e -> {
            dialog.close();
            showNotice("Complaint Linked", "Booking " + id + " linked to open Complaint #CMP-9021.");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Intervention Recorded", notice);
    }
}

