package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;
import com.eklavya.spotfit.controller.manager.ManagerBookingController;
import com.eklavya.spotfit.model.Booking;
import javafx.application.Platform;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;

public class BookingManagement {

    private final List<VBox> filterCards = new ArrayList<>();
    private VBox bookingsListContainer;
    private BorderPane rightPane;
    private final ManagerBookingController controller = new ManagerBookingController();
    private String currentStatusFilter = "All Bookings";
    private String currentSearch = "";

    public VBox getContent(BorderPane rightPane) {
        this.rightPane = rightPane;

        Label title = heading("Booking Management", 30);
        Label subtitle = muted(
                "View, monitor, reschedule, cancel, or extend customer bookings for Pixel Photography Studio.");

        HBox metaRow = new HBox(12, outlineChip("📍 Pixel Photography Studio · Ahmedabad, Gujarat"),
                outlineChip("📅 Thursday, 13 August 2026"));

        HBox filterCardsRow = new HBox(14,
                createFilterCard("All Bookings", "48", 0, true),
                createFilterCard("Today", "18", 1, false),
                createFilterCard("Upcoming", "23", 2, false),
                createFilterCard("Active", "3", 3, false),
                createFilterCard("Completed", "6", 4, false),
                createFilterCard("Cancelled", "1", 5, false),
                createFilterCard("No Show", "1", 6, false));
        filterCardsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        TextField searchField = searchField("Search by customer name or booking ID...");
        searchField.textProperty().addListener((obs, oldV, newV) -> filterBookings(newV.trim()));

        HBox dropdownRow = new HBox(10, outlineChip("Date: Today ⌄"), outlineChip("Status: All ⌄"),
                outlineChip("Resource: All ⌄"), outlineChip("Time: All ⌄"), hgrow(), link("Clear Filters"));
        dropdownRow.setAlignment(Pos.CENTER_LEFT);

        HBox quickFiltersRow = new HBox(10, quickFilterChip("Today", true), quickFilterChip("Tomorrow", false),
                quickFilterChip("This Week", false), quickFilterChip("Custom Date", false));

        VBox findBookingCard = card(heading("Find a Booking", 18), searchField, dropdownRow, quickFiltersRow);

        bookingsListContainer = new VBox(14);
        loadBookings();

        VBox listCard = card(heading("Booking Operations List", 18), bookingsListContainer);

        VBox content = new VBox(20, title, subtitle, metaRow, filterCardsRow, findBookingCard, listCard);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private VBox createFilterCard(String label, String value, int index, boolean active) {
        VBox card = new VBox(8, muted(label), heading(value, 26));
        card.setPadding(new Insets(16));
        card.setStyle(active
                ? "-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + PRIMARY
                        + "; -fx-border-width:2px; -fx-border-radius:16px; -fx-cursor:hand;"
                : "-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + BORDER
                        + "; -fx-border-width:1px; -fx-border-radius:16px; -fx-cursor:hand;");

        filterCards.add(card);
        card.setOnMouseClicked(e -> {
            for (int i = 0; i < filterCards.size(); i++) {
                VBox c = filterCards.get(i);
                boolean isIt = i == index;
                c.setStyle(isIt
                        ? "-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:"
                                + PRIMARY + "; -fx-border-width:2px; -fx-border-radius:16px; -fx-cursor:hand;"
                        : "-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + BORDER
                                + "; -fx-border-width:1px; -fx-border-radius:16px; -fx-cursor:hand;");
            }
            String filter = label.equals("All Bookings") ? "All" : label.toUpperCase().replace(" ", "_");
            currentStatusFilter = filter;
            loadBookings();
        });
        return card;
    }

    private Label quickFilterChip(String text, boolean active) {
        return active ? chip(text, PRIMARY, "white") : chip(text, CHIP_BG, TEXT);
    }

    private void filterBookings(String q) {
        currentSearch = q;
        loadBookings();
    }

    private void loadBookings() {
        controller.getBookings(null, "All".equals(currentStatusFilter) ? null : currentStatusFilter, currentSearch)
            .thenAccept(bookings -> Platform.runLater(() -> {
                bookingsListContainer.getChildren().clear();
                if (bookings.isEmpty()) {
                    bookingsListContainer.getChildren().add(muted("No bookings found."));
                    return;
                }
                for (Booking b : bookings) {
                    bookingsListContainer.getChildren().add(bookingRow(b));
                }
            }));
    }

    private HBox bookingRow(Booking booking) {
        String dateStr = "N/A";
        String timeStr = "N/A";
        if (booking.getStartAt() > 0) {
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(booking.getStartAt()), ZoneId.systemDefault());
            dateStr = dt.format(DateTimeFormatter.ofPattern("dd\nMMM"));
            timeStr = dt.format(DateTimeFormatter.ofPattern("h:mm a"));
        }

        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle(
                "-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + "; -fx-background-color:" + CHIP_BG
                        + "; -fx-background-radius:10px; -fx-padding:10 14; -fx-text-alignment:center;");

        Label idLabel = new Label(booking.getBookingId());
        idLabel.setStyle("-fx-font-size:12px; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-border-color:" + BORDER
                + "; -fx-border-radius:6px; -fx-padding:2 8;");

        String payStatus = booking.getPaymentStatus();
        Label payChip = chip("Payment: " + payStatus, "SUCCESS".equals(payStatus) ? GREEN_BG : CHIP_BG,
                "SUCCESS".equals(payStatus) ? GREEN_TEXT : TEXT);

        String bStatus = booking.getStatus();
        Label statusChip = chip(bStatus, bStatus.equals("CHECKED_IN") ? GREEN_BG : CHIP_BG, bStatus.equals("CHECKED_IN") ? GREEN_TEXT : TEXT);

        HBox nameRow = new HBox(10, heading(booking.getCustomerName() != null ? booking.getCustomerName() : "Unknown", 16), idLabel, statusChip, payChip);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label detailsLabel = muted((booking.getOfferingName() != null ? booking.getOfferingName() : "Space") + "  ·  🕐 " + timeStr + "  ·  👤 " + booking.getParticipantCount() + " guests");

        VBox infoBox = new VBox(4, nameRow, detailsLabel);

        Button viewDetailsButton = secondaryButton("View Details →");
        viewDetailsButton.setOnAction(e -> rightPane.setCenter(scroll(new BookingDetails().getContent(rightPane))));

        Button actionsBtn = primaryButton("Actions ▾");
        actionsBtn.setOnAction(e -> openBookingActionModal(booking));

        HBox buttonsBox = new HBox(10, actionsBtn, viewDetailsButton);

        HBox row = new HBox(dateLabel, infoBox, hgrow(), buttonsBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(20);
        row.setPadding(new Insets(16));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:16px;");

        return row;
    }

    private void openBookingActionModal(Booking booking) {
        String id = booking.getBookingId();
        String name = booking.getCustomerName() != null ? booking.getCustomerName() : "Unknown";
        
        VBox options = new VBox(12,
                heading("Operational Actions for Booking #" + id, 18),
                muted("Customer: " + name),
                actionBtn("✓ Check-In Customer", () -> {
                    controller.checkIn(id, booking.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                        if (res.isSuccess()) {
                            AuditLogManager.addLog("Current Manager", "Checked In", "Checked in " + name + " for #" + id, "CHECK_IN");
                            showModal("Check-In", new VBox(16, heading("✔ Customer Checked In", 16), muted("Booking status set to CHECKED_IN.")));
                            loadBookings();
                        } else {
                            showModal("Error", new VBox(16, heading("Check-In Failed", 16), muted(res.getMessage())));
                        }
                    }));
                }),
                actionBtn("🏁 Check-Out Customer", () -> {
                    controller.checkOut(id, booking.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                        if (res.isSuccess()) {
                            showModal("Check-Out", new VBox(16, heading("✔ Customer Checked Out", 16), muted("Booking status set to COMPLETED.")));
                            loadBookings();
                        } else {
                            showModal("Error", new VBox(16, heading("Check-Out Failed", 16), muted(res.getMessage())));
                        }
                    }));
                }),
                actionBtn("⏱ Extend Booking Time", () -> openExtendModal(id, name)),
                actionBtn("📅 Reschedule Booking", () -> openRescheduleModal(id, name)),
                actionBtn("🚫 Mark No Show", () -> {
                    AuditLogManager.addLog("Current Manager", "Marked No Show", "Marked #" + id + " as No Show",
                            "BOOKING");
                    showModal("No Show Logged", new VBox(16, heading("Booking marked No-Show", 16)));
                }),
                actionBtn("💬 Contact Customer (SpotFit)", () -> openContactModal(name, "customer@example.com")),
                dangerActionBtn("✕ Cancel Booking", () -> openCancelModal(id, name)));
        options.setPadding(new Insets(20));
        showModal("Booking Operations #" + id, options);
    }

    private Button actionBtn(String text, Runnable action) {
        Button b = secondaryButton(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    private Button dangerActionBtn(String text, Runnable action) {
        Button b = secondaryButton(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(b.getStyle() + " -fx-text-fill:" + RED_TEXT + "; -fx-border-color:" + RED_TEXT + ";");
        b.setOnAction(e -> action.run());
        return b;
    }

    private void openExtendModal(String id, String name) {
        VBox body = new VBox(14,
                heading("Extend Booking #" + id, 18),
                muted("Customer: " + name),
                secondaryButton("+ 30 Minutes Extension"),
                secondaryButton("+ 1 Hour Extension"),
                secondaryButton("+ 2 Hours Extension"),
                primaryButton("Confirm Time Extension"));
        body.setPadding(new Insets(20));
        ModalDialog dialog = showModal("Extend Booking", body);
        Button confirm = (Button) body.getChildren().get(5);
        confirm.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Extended Booking", "Extended booking #" + id + " by 1 hour",
                    "BOOKING");
            dialog.close();
            showModal("Booking Extended",
                    new VBox(16, heading("✔ Extension Confirmed", 16), muted("Notification sent to customer.")));
        });
    }

    private void openRescheduleModal(String id, String name) {
        TextField newDate = searchField("New Date (e.g. 14 Aug 2026)");
        TextField newTime = searchField("New Time Slot (e.g. 3:00 PM - 5:00 PM)");

        VBox body = new VBox(14,
                heading("Reschedule Booking #" + id, 18),
                muted("Customer: " + name),
                new VBox(4, muted("NEW DATE"), newDate),
                new VBox(4, muted("NEW TIME SLOT"), newTime),
                primaryButton("Confirm Reschedule & Notify Customer"));
        body.setPadding(new Insets(20));
        ModalDialog dialog = showModal("Reschedule Booking", body);
        Button confirm = (Button) body.getChildren().get(4);
        confirm.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Rescheduled Booking", "Moved #" + id + " to new slot", "BOOKING");
            dialog.close();
            showModal("Rescheduled",
                    new VBox(16, heading("✔ Booking Rescheduled", 16), muted("Customer notified via SpotFit app.")));
        });
    }

    private void openCancelModal(String id, String name) {
        VBox body = new VBox(14,
                heading("Cancel Booking #" + id, 18),
                muted("Select cancellation reason for policy evaluation:"),
                chip("○ Customer request", CHIP_BG, TEXT),
                chip("○ Space unavailable", CHIP_BG, TEXT),
                chip("○ Maintenance issue", CHIP_BG, TEXT),
                chip("○ Emergency / Safety", CHIP_BG, TEXT),
                chip("○ Operational issue", CHIP_BG, TEXT),
                chip("○ Other", CHIP_BG, TEXT),
                muted("System Policy Evaluation: Refund calculated automatically according to SpotFit cancellation terms. Manager does not manually set refund amount."),
                dangerActionBtn("Confirm Cancellation", () -> {
                }));
        body.setPadding(new Insets(20));
        ModalDialog dialog = showModal("Cancellation Reason", body);
        Button confirm = (Button) body.getChildren().get(8);
        confirm.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Cancelled Booking",
                    "Cancelled #" + id + " (Reason: Customer request)", "BOOKING");
            dialog.close();
            showModal("Booking Cancelled", new VBox(16, heading("✔ Booking Cancelled", 16),
                    muted("SpotFit refund policy executed automatically.")));
        });
    }

    private void openContactModal(String name, String email) {
        TextArea msgArea = new TextArea();
        msgArea.setPromptText("Write in-app SpotFit message to " + name + "...");
        msgArea.setPrefRowCount(4);
        msgArea.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER
                + "; -fx-border-radius:12px;");

        Button sendBtn = primaryButton("Send SpotFit Message");
        sendBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Sent Customer Message",
                    "Sent message to " + name + " regarding booking", "CUSTOMER");
            showModal("Message Sent",
                    new VBox(16, heading("✔ Message Sent", 16), muted("Customer notified via SpotFit app.")));
        });

        VBox body = new VBox(14,
                heading("Contact Customer: " + name, 18),
                muted("Recipient Email: " + email + " (Direct phone numbers hidden for privacy)"),
                msgArea,
                sendBtn);
        body.setPadding(new Insets(20));
        showModal("SpotFit Messaging Channel", body);
    }
}
