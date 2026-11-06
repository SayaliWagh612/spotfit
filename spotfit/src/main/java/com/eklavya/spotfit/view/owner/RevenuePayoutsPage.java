package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerRevenueController;
import com.eklavya.spotfit.model.Payment;
import com.eklavya.spotfit.model.Space;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

public class RevenuePayoutsPage {
    public VBox getContent(BorderPane rightPane) {
        String scope = MockDataStore.getInstance().getSelectedSpaceId();
        Space selected = MockDataStore.getInstance().getSelectedSpace();
        OwnerRevenueController controller = new OwnerRevenueController();
        double earnings = controller.getTotalEarnings(scope);

        VBox scopeLine = new VBox(OwnerUi.body("Current scope · " + (selected == null ? "All Spaces" : selected.getName()), 13, OwnerUi.TEXT_MUTED));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Captured Revenue", money(earnings), "Successful mock payments"),
                OwnerUi.statCard("Pending Payout", money(controller.getPendingPayout(scope)), "Mock settlement"),
                OwnerUi.statCard("Transactions", String.valueOf(controller.getPayments(scope).size()), null),
                OwnerUi.statCard("Integration", "Razorpay", "UI mock only"));
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox transactions = OwnerUi.card();
        HBox head = new HBox(OwnerUi.heading("Recent Transactions", 17), spacer());
        var all = OwnerUi.secondaryButton("View Full History");
        // "View Full History" still opens its own full Scene (it isn't a
        // sidebar destination) - "back" restores the shell's persistent
        // Scene, whose rightPane is still showing this Revenue page.
        all.setOnAction(e -> OwnerUi.navigate(new TransactionHistoryPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene()))));
        head.getChildren().add(all); transactions.getChildren().add(head);
        for (Payment payment : controller.getPayments(scope)) transactions.getChildren().add(row(payment));
        if (controller.getPayments(scope).isEmpty()) transactions.getChildren().add(OwnerUi.body("No transactions in this scope.", 13, OwnerUi.TEXT_MUTED));

        VBox spaces = OwnerUi.card(); spaces.getChildren().add(OwnerUi.heading("Revenue by Space", 17));
        for (Space space : MockDataStore.getInstance().getSpaces()) {
            if (selected == null || selected.getSpaceId().equals(space.getSpaceId()))
                spaces.getChildren().add(info(space.getName(), money(controller.getTotalEarnings(space.getSpaceId()))));
        }
        VBox content = new VBox(20, scopeLine, stats, new HBox(18, transactions, spaces)); content.setPadding(new Insets(0,0,0,0));
        return content;
    }
    private HBox row(Payment p) { return info(p.getPaymentId() + " · " + p.getPaymentMethod(), money(p.getAmount()) + " · " + p.getStatus()); }
    private HBox info(String left, String right) { HBox r = new HBox(OwnerUi.body(left,12.5,OwnerUi.TEXT_DARK), spacer(), OwnerUi.body(right,12,OwnerUi.TEXT_MUTED)); r.setAlignment(Pos.CENTER_LEFT); r.setPadding(new Insets(7,0,7,0)); return r; }
    private Region spacer() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }
    private String money(double value) { return "₹" + String.format("%,.0f", value); }
}

