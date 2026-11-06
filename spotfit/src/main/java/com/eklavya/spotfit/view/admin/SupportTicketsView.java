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

public class SupportTicketsView {

    public VBox getContent() {
        Label title = heading("Platform Support Tickets", 24);
        Label subtitle = muted("Manage account help, technical queries, and navigation requests. Support tickets can be linked or converted into formal complaints.");

        TextField search = searchField("🔍 Search by Ticket ID (TCK-xxxx), User/Owner, or Subject...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Tickets (12)"),
                outlineChip("Open (8)"),
                outlineChip("Converted to Complaint (2)"),
                outlineChip("Closed (180)")
        );

        VBox ticketList = new VBox(12,
                createTicketRow("TCK-501", "Account Access & Password Reset Issue", "User: Riya Kapoor", "Technical / Login Help", "OPEN", AMBER_BG, AMBER_TEXT),
                createTicketRow("TCK-502", "Payout Bank Account Update Delay", "Owner: Vikram Shah", "Account / Finance Help", "OPEN", AMBER_BG, AMBER_TEXT),
                createTicketRow("TCK-503", "Manager Unable to Check-in Booking", "Manager: Kavya Patel", "App Navigation", "RESOLVED", GREEN_BG, GREEN_TEXT),
                createTicketRow("TCK-504", "Reported Safety Overcharge at Facility", "User: Siddharth Rao", "Potential Fraud / Safety", "CONVERTED_TO_COMPLAINT", PURPLE_BG, PURPLE_TEXT)
        );

        VBox contentCard = card(filtersRow, ticketList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createTicketRow(String id, String subject, String requester, String category, String status, String bg, String fg) {
        Label iconLbl = new Label("🎫");
        iconLbl.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");

        VBox info = new VBox(2,
                heading(id + " · " + subject, 15),
                muted("Requester: " + requester + " · Category: " + category)
        );

        Label statusChip = chip(status, bg, fg);

        Button actionBtn = secondaryButton("Handle Ticket");
        actionBtn.setOnAction(e -> showTicketModal(id, subject, requester, category, status));

        HBox row = new HBox(16, iconLbl, info, hgrow(), statusChip, actionBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showTicketModal(String id, String subject, String requester, String category, String status) {
        Label info = muted("Ticket ID: " + id + " | Requester: " + requester + " | Category: " + category);

        Label replyLbl = heading("Admin Support Response:", 14);
        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Enter support resolution message or assistance steps...");
        replyArea.setPrefRowCount(3);

        Button btnReply = primaryButton("Send Response");
        Button btnConvert = dangerButton("Convert into Formal Complaint");
        Button btnClose = secondaryButton("Resolve & Close Ticket");

        HBox btns = new HBox(10, btnReply, btnConvert, btnClose);

        VBox body = new VBox(14, info, replyLbl, replyArea, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Support Ticket - " + id, body);

        btnReply.setOnAction(e -> { dialog.close(); showNotice("Response Sent", "Response dispatched to " + requester + "."); });
        btnConvert.setOnAction(e -> { dialog.close(); showNotice("Converted to Complaint", "Ticket " + id + " converted into Formal Complaint #CMP-905 and routed to complaints desk."); });
        btnClose.setOnAction(e -> { dialog.close(); showNotice("Ticket Resolved", "Ticket " + id + " marked resolved and closed."); });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Support Action Completed", notice);
    }
}

