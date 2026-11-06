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

public class TransactionsView {

    public VBox getContent() {
        Label title = heading("Finance - Platform Transactions & Refunds", 24);
        Label subtitle = muted("Governs platform transactions, Razorpay payment reference records, owner payouts, and Admin refund override authority.");

        TextField search = searchField("🔍 Search by Transaction ID (TXN-xxxx), Razorpay Ref, Booking ID, or User...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Transactions (48,920)"),
                outlineChip("Successful (46,800)"),
                outlineChip("Disputed (2)"),
                outlineChip("Refunded (1,200)"),
                outlineChip("Payouts On Hold (4)")
        );

        VBox txnList = new VBox(12,
                createTxnRow("TXN-9841", "pay_M8k2N1p9L4", "SPF-2852", "Neha Shah", "Pixel Media", "₹3,500", "SUCCESSFUL", GREEN_BG, GREEN_TEXT, "PAID", GREEN_BG, GREEN_TEXT),
                createTxnRow("TXN-9840", "pay_L7j1M0o8K3", "SPF-2841", "Aarav Mehta", "Pixel Media", "₹2,200", "SUCCESSFUL", GREEN_BG, GREEN_TEXT, "PROCESSING", BLUE_BG, BLUE_TEXT),
                createTxnRow("TXN-9720", "pay_K6i0L9n7J2", "SPF-2790", "Karan Verma", "FitZone Group", "₹1,800", "DISPUTED", RED_BG, RED_TEXT, "ON_HOLD", AMBER_BG, AMBER_TEXT),
                createTxnRow("TXN-9610", "pay_J5h9K8m6I1", "SPF-2650", "Siddharth Rao", "Aura Wellness", "₹900", "REFUNDED", PURPLE_BG, PURPLE_TEXT, "CANCELLED", CHIP_BG, TEXT)
        );

        VBox contentCard = card(filtersRow, txnList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createTxnRow(String id, String rzpRef, String bookingId, String user, String ownerEntity, String amount, String txnStatus, String tBg, String tFg, String payoutStatus, String pBg, String pFg) {
        Label iconLbl = new Label("💳");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox info = new VBox(2,
                heading(id + " · " + rzpRef, 15),
                muted("Booking: " + bookingId + " · User: " + user + " · Entity: " + ownerEntity)
        );

        VBox statusBox = new VBox(4,
                chip("TXN: " + txnStatus, tBg, tFg),
                chip("Payout: " + payoutStatus, pBg, pFg)
        );

        Label amtLbl = heading(amount, 16);

        Button refundOverrideBtn = primaryButton("Admin Refund Override");
        refundOverrideBtn.setOnAction(e -> showRefundOverrideModal(id, rzpRef, bookingId, user, ownerEntity, amount));

        HBox row = new HBox(16, iconLbl, info, hgrow(), amtLbl, statusBox, refundOverrideBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showRefundOverrideModal(String id, String rzpRef, String bookingId, String user, String ownerEntity, String amount) {
        Label header = muted("Transaction ID: " + id + " | Razorpay Ref: " + rzpRef + " | Booking: " + bookingId);

        VBox details = new VBox(6,
                heading("Financial Refund Authority Record:", 13),
                muted("• User: " + user + " | Business: " + ownerEntity),
                muted("• Amount: " + amount),
                muted("• Security Protocol: JavaFX application never exposes raw Razorpay secret keys."),
                muted("• Execution: Backend payment service executes Razorpay gateway refund API.")
        );
        details.setPadding(new Insets(12));
        details.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Label reasonLbl = heading("Mandatory Admin Override Reason:", 14);
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Specify dispute resolution or policy intervention reason for refund...");
        reasonArea.setPrefRowCount(3);

        Button executeRefundBtn = dangerButton("Execute Gateway Refund via Payment Service");
        Button holdPayoutBtn = secondaryButton("Place Owner Payout ON_HOLD");

        HBox btns = new HBox(10, executeRefundBtn, holdPayoutBtn);

        VBox body = new VBox(14, header, details, reasonLbl, reasonArea, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Admin Refund Override - " + id, body);

        executeRefundBtn.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                reasonLbl.setText("Mandatory Reason Required!");
                reasonLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            dialog.close();
            showNotice("Refund Dispatched", "Refund of " + amount + " for " + bookingId + " executed via backend payment service. Status updated to REFUNDED. Audit log entry recorded.");
        });

        holdPayoutBtn.setOnAction(e -> {
            dialog.close();
            showNotice("Payout Placed ON_HOLD", "Owner payout for " + ownerEntity + " placed ON_HOLD pending dispute investigation.");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Financial Action Complete", notice);
    }
}

