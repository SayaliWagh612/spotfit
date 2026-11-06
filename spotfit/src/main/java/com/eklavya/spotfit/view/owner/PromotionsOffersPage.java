package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class PromotionsOffersPage {

    public static class Campaign {
        private final String name, type, value, uses, revenue, status;
        public Campaign(String name, String type, String value, String uses, String revenue, String status) {
            this.name = name; this.type = type; this.value = value; this.uses = uses; this.revenue = revenue; this.status = status;
        }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getValue() { return value; }
        public String getUses() { return uses; }
        public String getRevenue() { return revenue; }
        public String getStatus() { return status; }
    }

    public VBox getContent(BorderPane rightPane) {
        VBox note = new VBox(OwnerUi.body("Create promotions, increase bookings and attract more customers.", 13, OwnerUi.TEXT_MUTED));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Active Campaigns", "3", null),
                OwnerUi.statCard("Bookings Generated", "142", null),
                OwnerUi.statCard("Revenue Generated", "\u20b968,400", null),
                OwnerUi.statCard("Coupon Redemptions", "312", null));

        var newCampaignBtn = OwnerUi.primaryButton("+ New Campaign");
        newCampaignBtn.setMaxWidth(200);

        TableView<Campaign> table = new TableView<>();
        table.getItems().addAll(
                new Campaign("Weekend Special", "Percentage Discount", "20% off", "84", "\u20b924,000", "Active"),
                new Campaign("First Booking", "Flat Discount", "\u20b9200 off", "58", "\u20b911,600", "Active")
        );
        TableColumn<Campaign, String> nameCol = new TableColumn<>("Campaign Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Campaign, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Campaign, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<Campaign, String> usesCol = new TableColumn<>("Uses");
        usesCol.setCellValueFactory(new PropertyValueFactory<>("uses"));
        TableColumn<Campaign, String> revCol = new TableColumn<>("Revenue");
        revCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        TableColumn<Campaign, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(nameCol, typeCol, valueCol, usesCol, revCol, statusCol);
        table.setPrefHeight(200);

        VBox tableCard = OwnerUi.card();
        tableCard.getChildren().addAll(OwnerUi.heading("Campaigns", 16), table);

        VBox content = new VBox(20, note, stats, newCampaignBtn, tableCard);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }
}

