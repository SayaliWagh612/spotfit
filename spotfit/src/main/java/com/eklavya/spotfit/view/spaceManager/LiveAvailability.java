package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

// Live Availability - reached via the "My Space" sidebar item (the reference design had no
// dedicated nav entry for this screen, so this is the closest fit).
public class LiveAvailability {

    VBox getContent() {

        Label title = heading("Live Availability", 30);
        Label subtitle = muted("See what's available and what's happening at your space right now.");

        HBox metaRow = new HBox(12, outlineChip("📍 Pixel Photography Studio · Ahmedabad, Gujarat"), hgrow(), chip("●  Live · Updated just now  ↻", GREEN_BG, GREEN_TEXT));
        metaRow.setAlignment(Pos.CENTER_LEFT);

        VBox statusCard = createStatusCard();

        HBox miniStatsRow = new HBox(16,
                miniStat("✓", "AVAILABLE NOW", "8"),
                miniStat("👥", "OCCUPIED", "12"),
                miniStat("🕐", "ARRIVING SOON", "3 (next 1h)"),
                miniStat("⏳", "ENDING SOON", "2 (next 1h)")
        );
        miniStatsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        VBox activeBookings = card(heading("📱  Active Bookings", 18),
                activeBookingRow("Aarav Mehta", "Photography Session · 09:00 AM - 11:00 AM", "4 Guests"),
                activeBookingRow("Riya Kapoor", "Portrait Session · 11:30 AM - 01:00 PM", "2 Guests")
        );

        VBox todaysFlow = card(heading("Today's Flow", 18),
                flowItem("09:00 AM", "Space Open", true),
                flowItem("09:00-11:00 AM", "Occupied (Aarav Mehta)", false)
        );
        todaysFlow.setPrefWidth(340);
        todaysFlow.setMinWidth(340);

        HBox lowerRow = new HBox(20, activeBookings, todaysFlow);
        HBox.setHgrow(activeBookings, Priority.ALWAYS);

        VBox content = new VBox(20, title, subtitle, metaRow, statusCard, miniStatsRow, lowerRow);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private VBox createStatusCard() {
        Label statusLabel = new Label("SPACE STATUS");
        statusLabel.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label openLabel = heading("●  Open  (9:00 AM - 9:00 PM)", 22);
        Label occupancyLabel = muted("Current Occupancy: 12 / 20");
        Label percentLabel = muted("60% occupied");
        HBox occupancyRow = new HBox(occupancyLabel, hgrow(), percentLabel);

        ProgressBar bar = new ProgressBar(0.6);
        bar.setPrefWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent:" + PRIMARY + ";");

        VBox leftBox = new VBox(10, statusLabel, openLabel, occupancyRow, bar);

        Label bigNumber = heading("8", 44);
        Label bigNumberLabel = muted("spots currently available");
        VBox rightBox = new VBox(4, bigNumber, bigNumberLabel);
        rightBox.setAlignment(Pos.CENTER);

        HBox row = new HBox(30, leftBox, new Separator(javafx.geometry.Orientation.VERTICAL), rightBox);
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER_LEFT);

        return card(row);
    }

    private VBox miniStat(String icon, String label, String value) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:16px; -fx-text-fill:" + PRIMARY + ";");
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");
        HBox topRow = new HBox(8, iconLabel, labelText);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(10, topRow, heading(value, 24));
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + BORDER + "; -fx-border-radius:16px; -fx-border-width:1px;");
        return box;
    }

    private HBox activeBookingRow(String name, String details, String guests) {
        HBox row = new HBox(14, avatar(initialsOf(name), 44), new VBox(2, heading(name, 15), muted(details)));
        row.setAlignment(Pos.CENTER_LEFT);
        HBox full = new HBox(row, hgrow(), muted(guests), chip("● Checked In", GREEN_BG, GREEN_TEXT));
        full.setSpacing(14);
        full.setAlignment(Pos.CENTER_LEFT);
        full.setPadding(new Insets(14));
        full.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:14px;");
        return full;
    }

    private HBox flowItem(String time, String label, boolean start) {
        Label dot = new Label("●");
        dot.setStyle("-fx-font-size:12px; -fx-text-fill:" + (start ? GREEN_TEXT : PRIMARY) + ";");
        VBox textBox = new VBox(2, muted(time), heading(label, 14));
        HBox row = new HBox(10, dot, textBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private String initialsOf(String name) {
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
        return initials;
    }
}
