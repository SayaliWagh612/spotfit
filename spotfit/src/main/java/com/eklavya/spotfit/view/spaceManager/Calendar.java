package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class Calendar {

    private final List<Button> viewButtons = new ArrayList<>();
    private VBox calendarGridContainer;
    private BorderPane rightPane;

    public VBox getContent(BorderPane rightPane) {
        this.rightPane = rightPane;

        Label title = heading("Resource & Operational Calendar", 30);
        Label subtitle = muted("Real-time grid schedule for physical resources at Pixel Photography Studio.");
        Label dateChip = outlineChip("📅 Thursday, 13 August 2026");

        HBox titleRow = new HBox(new VBox(6, title, subtitle), hgrow(), dateChip);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label locationChip = outlineChip("📍 Pixel Photography Studio · Ahmedabad, Gujarat");

        HBox viewTabs = new HBox(10,
                createViewBtn("Day View", 0, true),
                createViewBtn("Week View", 1, false),
                createViewBtn("Month View", 2, false));

        Button liveAvailBtn = primaryButton("⚡ Live Availability View");
        liveAvailBtn.setOnAction(e -> rightPane.setCenter(scroll(new LiveAvailability().getContent())));

        HBox controlsRow = new HBox(10,
                secondaryButton("‹ Previous"), secondaryButton("Today"), secondaryButton("Next ›"),
                viewTabs,
                hgrow(),
                liveAvailBtn);
        controlsRow.setAlignment(Pos.CENTER_LEFT);

        HBox legendRow = new HBox(12,
                chip("AVAILABLE", SURFACE, TEXT),
                chip("BOOKED", CHIP_BG, PRIMARY),
                chip("ACTIVE / CHECKED-IN", GREEN_BG, GREEN_TEXT),
                chip("FROZEN / BLOCKED", RED_BG, RED_TEXT),
                chip("MAINTENANCE", BLUE_BG, BLUE_TEXT),
                chip("PENDING", BACKGROUND, SECONDARY_TEXT));
        legendRow.setAlignment(Pos.CENTER_LEFT);

        calendarGridContainer = new VBox(14);
        loadCalendarView("Day View");

        VBox content = new VBox(16, titleRow, locationChip, controlsRow, legendRow,
                card(heading("Resource Schedule Grid", 18), calendarGridContainer));
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private Button createViewBtn(String label, int index, boolean active) {
        Button btn = new Button(label);
        btn.setStyle(active
                ? "-fx-background-color:" + PRIMARY
                        + "; -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;"
                : "-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT
                        + "; -fx-font-size:13px; -fx-border-color:" + BORDER
                        + "; -fx-border-radius:20px; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");

        viewButtons.add(btn);
        btn.setOnAction(e -> {
            for (int i = 0; i < viewButtons.size(); i++) {
                Button b = viewButtons.get(i);
                if (i == index) {
                    b.setStyle("-fx-background-color:" + PRIMARY
                            + "; -fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");
                } else {
                    b.setStyle("-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT
                            + "; -fx-font-size:13px; -fx-border-color:" + BORDER
                            + "; -fx-border-radius:20px; -fx-background-radius:20px; -fx-padding:8 16; -fx-cursor:hand;");
                }
            }
            loadCalendarView(label);
        });
        return btn;
    }

    private void loadCalendarView(String viewName) {
        calendarGridContainer.getChildren().clear();

        // Resource rows: Studio A, Studio B, Recording Room, Editing Room, Camera Kit,
        // Lighting Kit
        calendarGridContainer.getChildren().addAll(
                resourceTimelineRow("Studio A (Photo Floor)",
                        slotBox("09:00 - 11:00 AM", "Aarav Mehta", "Photography", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("11:00 - 01:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("01:00 - 03:00 PM", "Arjun Mehta", "Video Shoot", "BOOKED", CHIP_BG, PRIMARY, true),
                        slotBox("03:00 - 05:00 PM", "Maintenance", "Floor Repainting", "MAINTENANCE", BLUE_BG,
                                BLUE_TEXT, false)),
                resourceTimelineRow("Studio B (Podcast Suite)",
                        slotBox("09:00 - 11:00 AM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("11:30 - 01:00 PM", "Riya Kapoor", "Portrait Session", "BOOKED", CHIP_BG, PRIMARY,
                                true),
                        slotBox("01:00 - 03:00 PM", "Neha Shah", "Content Shoot", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("03:00 - 05:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false)),
                resourceTimelineRow("Recording Room",
                        slotBox("09:00 - 11:00 AM", "Frozen / Blocked", "Private Studio Maintenance", "FROZEN", RED_BG,
                                RED_TEXT, false),
                        slotBox("11:00 - 01:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("01:00 - 03:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("03:30 - 05:00 PM", "Kabir Jain", "Voiceover Session", "BOOKED", CHIP_BG, PRIMARY,
                                true)),
                resourceTimelineRow("Editing Room",
                        slotBox("09:00 - 11:00 AM", "Dev Patel", "Color Grading", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("11:00 - 01:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("01:00 - 03:00 PM", "Pending Booking", "Awaiting confirmation", "PENDING", BACKGROUND,
                                SECONDARY_TEXT, false),
                        slotBox("03:00 - 05:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false)),
                resourceTimelineRow("4K Camera Kit",
                        slotBox("09:00 - 11:00 AM", "Aarav Mehta", "In Use", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("11:00 - 01:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("01:00 - 03:00 PM", "Neha Shah", "In Use", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("03:00 - 05:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false)),
                resourceTimelineRow("Lighting Equipment Kit",
                        slotBox("09:00 - 11:00 AM", "Aarav Mehta", "In Use", "ACTIVE", GREEN_BG, GREEN_TEXT, true),
                        slotBox("11:00 - 01:00 PM", "Maintenance", "Bulb Replacement", "MAINTENANCE", BLUE_BG,
                                BLUE_TEXT, false),
                        slotBox("01:00 - 03:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false),
                        slotBox("03:00 - 05:00 PM", "Available", "Click to Block", "AVAILABLE", SURFACE, TEXT, false)));
    }

    private VBox resourceTimelineRow(String resourceName, VBox... slots) {
        Label nameLabel = heading(resourceName, 15);
        nameLabel.setPrefWidth(180);

        HBox slotsRow = new HBox(12);
        for (VBox slot : slots) {
            slotsRow.getChildren().add(slot);
            HBox.setHgrow(slot, Priority.ALWAYS);
        }

        HBox row = new HBox(16, nameLabel, slotsRow);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return new VBox(row);
    }

    private VBox slotBox(String time, String title, String sub, String state, String bg, String fg, boolean isBooked) {
        Label t = new Label(time);
        t.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");

        Label h = heading(title, 13);
        Label s = muted(sub);

        Label stChip = chip(state, bg, fg);

        VBox box = new VBox(4, t, h, s, stChip);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:10px; -fx-border-color:" + BORDER
                + "; -fx-border-width:1px; -fx-border-radius:10px; -fx-cursor:hand;");

        box.setOnMouseClicked(e -> handleSlotClick(time, title, sub, state, isBooked));
        return box;
    }

    private void handleSlotClick(String time, String title, String sub, String state, boolean isBooked) {
        if (state.equals("BOOKED") || state.equals("ACTIVE")) {
            VBox body = new VBox(14,
                    heading("Booking Details: " + title, 18),
                    muted("Slot: " + time + " · Status: " + state),
                    muted("Offering: " + sub),
                    primaryButton("View Full Booking"),
                    secondaryButton("Extend Session Time"));
            body.setPadding(new Insets(20));
            ModalDialog dialog = showModal("Booked Slot - " + title, body);
            Button viewBtn = (Button) body.getChildren().get(3);
            viewBtn.setOnAction(ev -> {
                dialog.close();
                rightPane.setCenter(scroll(new BookingDetails().getContent(rightPane)));
            });
        } else if (state.equals("AVAILABLE")) {
            TextField reasonInput = searchField("Reason for freezing slot (e.g. Maintenance, Private event)");

            Button blockBtn = primaryButton("Block / Freeze Slot");

            VBox body = new VBox(14,
                    heading("Block Available Slot (" + time + ")", 18),
                    muted("State: AVAILABLE"),
                    reasonInput,
                    blockBtn);
            body.setPadding(new Insets(20));
            ModalDialog dialog = showModal("Freeze Slot", body);

            blockBtn.setOnAction(ev -> {
                String reason = reasonInput.getText().trim().isEmpty() ? "Maintenance" : reasonInput.getText().trim();
                AuditLogManager.addLog("Rahul Sharma", "Blocked Slot",
                        "Blocked slot " + time + " (Reason: " + reason + ")", "CALENDAR");
                dialog.close();
                showModal("Slot Frozen", new VBox(16, heading("✔ Slot Frozen Successfully", 16),
                        muted("State updated to FROZEN / BLOCKED.")));
            });
        } else if (state.equals("MAINTENANCE") || state.equals("FROZEN")) {
            showModal("Slot Status Information", new VBox(16,
                    heading("Status: " + state, 18),
                    muted("Slot: " + time + " · Details: " + sub),
                    secondaryButton("Unblock / Set Available")));
        }
    }
}
