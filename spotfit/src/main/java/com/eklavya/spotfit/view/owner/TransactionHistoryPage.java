package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class TransactionHistoryPage {

    public static class Txn {
        private final String id, type, date, amount, status;
        public Txn(String id, String type, String date, String amount, String status) {
            this.id = id; this.type = type; this.date = date; this.amount = amount; this.status = status;
        }
        public String getId() { return id; }
        public String getType() { return type; }
        public String getDate() { return date; }
        public String getAmount() { return amount; }
        public String getStatus() { return status; }
    }

    Scene getScene(Runnable callback) {
        HBox stats = new HBox(16,
                OwnerUi.statCard("Total Transactions", "482", null),
                OwnerUi.statCard("Total Processed", "\u20b96,84,000", null),
                OwnerUi.statCard("Pending", "\u20b98,200", null),
                OwnerUi.statCard("Refunds", "\u20b91,400", null));

        TableView<Txn> table = new TableView<>();
        table.getItems().addAll(
                new Txn("TXN-9001", "Payment", "Aug 14, 2026", "\u20b91,400", "Completed"),
                new Txn("TXN-9002", "Payout", "Aug 10, 2026", "\u20b938,500", "Completed"),
                new Txn("TXN-9003", "Refund", "Aug 8, 2026", "\u20b9700", "Completed")
        );
        TableColumn<Txn, String> idCol = new TableColumn<>("Transaction ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Txn, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Txn, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Txn, String> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Txn, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(idCol, typeCol, dateCol, amtCol, statusCol);
        table.setPrefHeight(260);

        VBox tableCard = OwnerUi.card();
        tableCard.getChildren().addAll(OwnerUi.heading("Transaction History", 16), table);

        VBox content = new VBox(20, stats, tableCard);
        content.setPadding(new Insets(24, 0, 0, 0));

        return OwnerUi.pageShell("revenue", "Dashboard \u203A Revenue & Payouts", "Transaction History", null, content, callback);
    }
}

