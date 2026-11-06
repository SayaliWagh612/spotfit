package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;

public class HelpSupportPage {

    public VBox getContent(BorderPane rightPane) {
        HBox stats = new HBox(16,
                OwnerUi.statCard("Open Tickets", "2", null),
                OwnerUi.statCard("Resolved", "34", null),
                OwnerUi.statCard("Avg Response", "3.2 hrs", null),
                OwnerUi.statCard("Satisfaction", "96%", null));

        var newTicketBtn = OwnerUi.primaryButton("+ Raise a New Ticket");
        newTicketBtn.setMaxWidth(220);
        newTicketBtn.setOnAction(event -> {
            System.out.println("new ticket");
            OwnerUi.navigate(new SupportTicketDetailsPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });

        VBox ticketsCard = OwnerUi.card();
        ticketsCard.getChildren().add(OwnerUi.heading("Your Tickets", 16));
        ticketsCard.getChildren().add(ticketRow("SUP-2026-1045", "Payout Delay Issue", "Open"));
        ticketsCard.getChildren().add(ticketRow("SUP-2026-1032", "Listing not appearing in search", "Resolved"));

        var kbBtn = OwnerUi.secondaryButton("Browse Knowledge Base & FAQs");
        kbBtn.setOnAction(event -> {
            System.out.println("knowledge base");
            OwnerUi.navigate(new KnowledgeBasePage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });

        VBox content = new VBox(20, stats, newTicketBtn, ticketsCard, kbBtn);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }

    private HBox ticketRow(String id, String subject, String status) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-cursor:hand;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(OwnerUi.body(id, 12.5, OwnerUi.TEXT_MUTED),
                OwnerUi.body(subject, 13, OwnerUi.TEXT_DARK), spacer, OwnerUi.badge(status));
        row.setOnMouseClicked(event -> {
            System.out.println("ticket " + id);
            OwnerUi.navigate(new SupportTicketDetailsPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });
        return row;
    }
}

