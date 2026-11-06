package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class CheckIn {

    private VBox activeSessionContainer;

    public VBox getContent() {

        HBox topRow = new HBox(chip("● Space Open (09:00 AM - 09:00 PM)", GREEN_BG, GREEN_TEXT),
                outlineChip("📍 Pixel Photography Studio · Ahmedabad, Gujarat"));
        topRow.setSpacing(12);

        Label title = heading("Customer OTP Check-In & Verification", 30);
        Label subtitle = muted(
                "Enter customer booking ID or OTP code to complete operational check-in for Pixel Photography Studio.");

        VBox otpCard = createOtpVerificationCard();
        VBox manualCard = createManualCheckInCard();
        VBox leftColumn = new VBox(20, otpCard, manualCard);

        HBox statsGrid = new HBox(16, statBox("Checked In", "11", GREEN_TEXT), statBox("Expected", "7", TEXT));
        HBox statsGrid2 = new HBox(16, statBox("Late", "2", RED_TEXT), statBox("Remaining", "5", PRIMARY));
        VBox statsColumn = new VBox(16, statsGrid, statsGrid2);
        statsGrid.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        statsGrid2.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        activeSessionContainer = createActiveSessionsCard();
        VBox rightColumn = new VBox(20, statsColumn, activeSessionContainer);
        rightColumn.setPrefWidth(360);
        rightColumn.setMinWidth(360);

        HBox mainRow = new HBox(20, leftColumn, rightColumn);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox content = new VBox(16, topRow, title, subtitle, mainRow);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private VBox createOtpVerificationCard() {
        Label cardTitle = heading("🔑 Standard OTP Verification Check-In", 20);
        Label desc = muted("Customer provides 4-digit OTP generated in their SpotFit App upon arrival.");

        TextField tfBookingId = searchField("Enter Booking ID (e.g. SPF-2852)");
        TextField tfOtp = searchField("Enter 4-Digit Customer OTP (e.g. 4821)");

        Button verifyBtn = primaryButton("Verify OTP & Complete Check-In");
        verifyBtn.setMaxWidth(Double.MAX_VALUE);
        verifyBtn.setOnAction(e -> {
            String bId = tfBookingId.getText().trim().isEmpty() ? "SPF-2852" : tfBookingId.getText().trim();
            String otp = tfOtp.getText().trim().isEmpty() ? "285201" : tfOtp.getText().trim();

            com.eklavya.spotfit.model.Booking b = com.eklavya.spotfit.controller.admin.MockDataStore.getInstance().findBookingById(bId);

            if (b == null) {
                showModal("Check-In Failed",
                        new VBox(16, heading("❌ Invalid Booking ID", 18), muted("Could not find booking #" + bId)));
                return;
            }

            if (!otp.equals(b.getVerificationCode())) {
                showModal("Check-In Failed", new VBox(16, heading("❌ Invalid OTP", 18),
                        muted("OTP " + otp + " is not valid for booking #" + bId)));
                return;
            }

            AuditLogManager.addLog("Manager", "Checked In Customer",
                    "Booking #" + bId + " verified with OTP [" + otp + "]", "CHECK_IN");
            showModal("Check-In Confirmed", new VBox(16,
                    heading("✔ OTP Verified - Check-In Successful!", 18),
                    muted("Customer: " + (b.getCustomerName() != null ? b.getCustomerName() : "Guest")),
                    muted("Booking ID: #" + bId),
                    muted("Assigned Resource: "
                            + (b.getResourceName() != null ? b.getResourceName() : "General Access")),
                    chip("STATUS: ACTIVE SESSION", GREEN_BG, GREEN_TEXT)));
        });

        VBox box = card(cardTitle, desc, tfBookingId, tfOtp, verifyBtn);
        return box;
    }

    private VBox createManualCheckInCard() {
        Label title = heading("Can't verify OTP? (Manual Check-In)", 18);
        Label subtitle = muted("If the customer's phone is unavailable or tech issues occur, perform manual override.");

        TextField tfBookingId = searchField("Enter Booking ID for Manual Override");

        Button manualBtn = secondaryButton("Manual Check-In Override");
        manualBtn.setOnAction(e -> {
            String bId = tfBookingId.getText().trim().isEmpty() ? "SPF-2852" : tfBookingId.getText().trim();

            VBox reasonForm = new VBox(12,
                    heading("Manual Check-In Reason Required", 16),
                    muted("Select override reason for audit log compliance:"),
                    chip("○ Customer OTP unavailable", CHIP_BG, TEXT),
                    chip("○ Technical issue", CHIP_BG, TEXT),
                    chip("○ Identity verified manually", CHIP_BG, TEXT),
                    chip("○ Other", CHIP_BG, TEXT),
                    primaryButton("Confirm Manual Check-In"));

            ModalDialog dialog = showModal("Manual Check-In Audit Record", reasonForm);
            Button confirm = (Button) reasonForm.getChildren().get(6);
            confirm.setOnAction(ev -> {
                AuditLogManager.addLog("Rahul Sharma", "Manual Check-In Override",
                        "Manual check-in for #" + bId + " (Reason: Identity verified manually)", "AUDIT");
                dialog.close();
                showModal("Override Logged", new VBox(16, heading("✔ Manual Check-In Recorded", 16),
                        muted("Action saved to space activity log.")));
            });
        });

        HBox searchRow = new HBox(10, tfBookingId, manualBtn);
        return card(title, subtitle, searchRow);
    }

    private VBox statBox(String label, String value, String valueColor) {
        VBox box = new VBox(8, muted(label), heading(value, 28));
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + valueColor
                + "; -fx-border-width:0 0 0 3px; -fx-border-radius:16px;");
        return box;
    }

    private VBox createActiveSessionsCard() {
        VBox entries = new VBox(0,
                activeSessionRow("AM", "Aarav Mehta", "SPF-2841", "Studio A", "09:00 AM - 11:00 AM"),
                activeSessionRow("RK", "Riya Kapoor", "SPF-2848", "Studio B", "11:30 AM - 01:00 PM"),
                activeSessionRow("NS", "Neha Shah", "SPF-2852", "Editing Suite", "01:00 PM - 03:00 PM"));

        return card(heading("Active checked-in sessions", 18), entries);
    }

    private HBox activeSessionRow(String initials, String name, String bookingId, String resource, String time) {
        Label av = avatar(initials, 40);
        VBox info = new VBox(2, heading(name + " (" + bookingId + ")", 14), muted(resource + " · " + time));

        Button checkoutBtn = secondaryButton("Check-Out");
        checkoutBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Completed Session", "Checked out " + name + " (" + bookingId + ")",
                    "CHECK_IN");
            showModal("Session Completed", new VBox(16, heading("✔ Session Completed for " + name, 16),
                    muted("Resource status set back to Available.")));
        });

        HBox row = new HBox(12, av, info, hgrow(), checkoutBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));
        row.setStyle("-fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1px;");
        return row;
    }
}
