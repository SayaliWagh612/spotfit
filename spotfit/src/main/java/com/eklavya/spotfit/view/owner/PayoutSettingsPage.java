package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class PayoutSettingsPage {

    public static class Settlement {
        private final String id, amount, date;
        public Settlement(String id, String amount, String date) { this.id = id; this.amount = amount; this.date = date; }
        public String getId() { return id; }
        public String getAmount() { return amount; }
        public String getDate() { return date; }
    }

    Scene getScene(Runnable callback) {
        VBox bankCard = OwnerUi.card();
        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var editBtn = OwnerUi.secondaryButton("Edit");
        top.getChildren().addAll(OwnerUi.heading("Primary Bank Account", 15), OwnerUi.badge("Verified"), spacer, editBtn);
        bankCard.getChildren().addAll(top,
                infoRow("Bank Name", "HDFC Bank"),
                infoRow("Account Holder", "Creative Hub Spaces Pvt Ltd"),
                infoRow("Account Number", "XXXX XXXX 4589"),
                infoRow("IFSC Code", "HDFC0001234"));

        TableView<Settlement> table = new TableView<>();
        table.getItems().addAll(
                new Settlement("STL-8801", "\u20b938,500", "Aug 10, 2026"),
                new Settlement("STL-8790", "\u20b929,200", "Aug 3, 2026")
        );
        TableColumn<Settlement, String> idCol = new TableColumn<>("Settlement ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Settlement, String> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Settlement, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        table.getColumns().addAll(idCol, amtCol, dateCol);
        table.setPrefHeight(160);

        VBox settlementsCard = OwnerUi.card();
        settlementsCard.getChildren().addAll(OwnerUi.heading("Recent Settlements", 15), table);

        VBox content = new VBox(20, bankCard, settlementsCard);
        content.setPadding(new Insets(24, 0, 0, 0));

        return OwnerUi.pageShell("settings", "Dashboard \u203A Settings", "Payout Settings",
                "Manage bank accounts, settlements and payment preferences.", content, callback);
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(OwnerUi.fieldLabel(label), spacer, OwnerUi.body(value, 12.5, OwnerUi.TEXT_DARK));
        return row;
    }
}

