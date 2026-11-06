package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class SupportTicketDetailsPage {

    Scene getScene(Runnable callback) {
        var back = OwnerUi.secondaryButton("\u2190 Back to Help Center");
        back.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(OwnerUi.heading("SUP-2026-1045", 20), OwnerUi.badge("Open"));

        VBox overview = OwnerUi.card();
        overview.getChildren().addAll(OwnerUi.heading("Case Overview", 15),
                infoRow("Subject", "Payout Delay Issue"),
                infoRow("Category", "Payments"),
                infoRow("Expected Resolution", "Within 24 Hours"));

        VBox thread = OwnerUi.card();
        thread.getChildren().add(OwnerUi.heading("Conversation Thread", 15));
        thread.getChildren().add(messageBubble("You", "Oct 10, 09:15 AM",
                "My payout for last week hasn't arrived yet, can you check?"));
        thread.getChildren().add(messageBubble("Priya Sharma (Support Agent)", "Oct 10, 09:50 AM",
                "Thanks for flagging this - I'm checking with our finance team now."));

        var reply = OwnerUi.textField("Type your reply...");
        var sendBtn = OwnerUi.primaryButton("Send");
        HBox replyRow = new HBox(8, reply, sendBtn);
        HBox.setHgrow(reply, Priority.ALWAYS);

        VBox content = new VBox(16, back, titleRow, overview, thread, replyRow);
        content.setPadding(new Insets(24, 0, 0, 0));
        content.setMaxWidth(720);

        return OwnerUi.pageShell("help", "Dashboard \u203A Help & Support \u203A SUP-2026-1045", "Support Ticket Details", null, content, null);
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(OwnerUi.fieldLabel(label), spacer, OwnerUi.body(value, 12.5, OwnerUi.TEXT_DARK));
        return row;
    }

    private VBox messageBubble(String author, String time, String message) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle("-fx-background-color:" + OwnerUi.SURFACE_LOW + "; -fx-background-radius:14;");
        HBox top = new HBox(8);
        top.getChildren().addAll(OwnerUi.body(author, 12, OwnerUi.TEXT_DARK), OwnerUi.body(time, 10.5, OwnerUi.TEXT_MUTED));
        Label msg = OwnerUi.body(message, 12.5, OwnerUi.TEXT_DARK);
        msg.setWrapText(true);
        box.getChildren().addAll(top, msg);
        return box;
    }
}

