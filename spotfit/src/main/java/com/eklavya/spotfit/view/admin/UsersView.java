package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class UsersView {

    public VBox getContent() {
        Label title = heading("User Governance & Accounts", 24);
        Label subtitle = muted("Govern user accounts, inspect booking history, manage status (ACTIVE, DEACTIVATED, SUSPENDED, BLOCKED). Historical records are preserved.");

        TextField search = searchField("🔍 Search by user name, email, phone, or User ID...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("Filter: All Users"),
                outlineChip("Active (14,100)"),
                outlineChip("Suspended (120)"),
                outlineChip("Blocked (60)")
        );
        filtersRow.setAlignment(Pos.CENTER_LEFT);

        VBox userList = new VBox(12,
                createUserRow("USR-1001", "Aarav Mehta", "aarav.mehta@example.com", "+91 98765 43210", "48 Bookings", "0 Complaints", "ACTIVE", GREEN_BG, GREEN_TEXT),
                createUserRow("USR-1002", "Riya Kapoor", "riya.k@example.com", "+91 98765 12345", "12 Bookings", "1 Complaint", "ACTIVE", GREEN_BG, GREEN_TEXT),
                createUserRow("USR-1003", "Karan Verma", "karan.v@example.com", "+91 91234 56789", "5 Bookings", "2 Complaints", "SUSPENDED", AMBER_BG, AMBER_TEXT),
                createUserRow("USR-1004", "Siddharth Rao", "siddharth@example.com", "+91 99887 76655", "2 Bookings", "3 Complaints (Fraud Risk)", "BLOCKED", RED_BG, RED_TEXT),
                createUserRow("USR-1005", "Neha Shah", "neha.shah@example.com", "+91 97654 32109", "24 Bookings", "0 Complaints", "DEACTIVATED", CHIP_BG, TEXT)
        );

        VBox contentCard = card(filtersRow, userList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createUserRow(String id, String name, String email, String phone, String bookings, String complaints, String status, String bg, String fg) {
        Label avatarLbl = avatar(initialsOf(name), 42);

        VBox nameBox = new VBox(2,
                heading(name + " (" + id + ")", 15),
                muted(email + " · " + phone)
        );

        VBox statsBox = new VBox(2,
                heading(bookings, 13),
                muted(complaints)
        );

        Label statusChip = chip(status, bg, fg);

        Button inspectBtn = secondaryButton("Inspect & Govern");
        inspectBtn.setOnAction(e -> showUserInspectModal(id, name, email, phone, bookings, complaints, status));

        HBox row = new HBox(16, avatarLbl, nameBox, hgrow(), statsBox, statusChip, inspectBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showUserInspectModal(String id, String name, String email, String phone, String bookings, String complaints, String currentStatus) {
        Label subTitle = muted("ID: " + id + " | Email: " + email + " | Phone: " + phone);

        VBox historyBox = new VBox(8,
                heading("Platform History & Records", 15),
                muted("• Total Bookings: " + bookings),
                muted("• Complaint History: " + complaints),
                muted("• Average Rating Given: 4.8 ★"),
                muted("• No permanent deletion allowed; historical data remains immutable.")
        );
        historyBox.setPadding(new Insets(12));
        historyBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Label statusHeading = heading("Change Account Governance Status", 15);
        Label statusInfo = muted("Current Status: " + currentStatus);

        Button btnActive = secondaryButton("Set ACTIVE");
        Button btnDeactive = secondaryButton("Set DEACTIVATED");
        Button btnSuspend = secondaryButton("Set SUSPENDED");
        Button btnBlock = dangerButton("Set BLOCKED");

        HBox statusBtns = new HBox(10, btnActive, btnDeactive, btnSuspend, btnBlock);

        VBox body = new VBox(16, subTitle, historyBox, statusHeading, statusInfo, statusBtns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("User Governance - " + name, body);

        btnActive.setOnAction(e -> { updateStatusNotice(dialog, name, "ACTIVE"); });
        btnDeactive.setOnAction(e -> { updateStatusNotice(dialog, name, "DEACTIVATED"); });
        btnSuspend.setOnAction(e -> { updateStatusNotice(dialog, name, "SUSPENDED"); });
        btnBlock.setOnAction(e -> { updateStatusNotice(dialog, name, "BLOCKED"); });
    }

    private void updateStatusNotice(ModalDialog dialog, String name, String newStatus) {
        dialog.close();
        VBox notice = new VBox(14,
                heading("Status Updated", 18),
                muted("User " + name + " governance status has been set to: " + newStatus + ". Event recorded in Audit Log."),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Account Governance Updated", notice);
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String init = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) init += parts[1].substring(0, 1).toUpperCase();
        return init;
    }
}

