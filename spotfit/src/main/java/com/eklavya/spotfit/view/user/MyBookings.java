package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.constants.BookingStatus;
import com.eklavya.spotfit.controller.user.CustomerMarketplaceController;
import com.eklavya.spotfit.model.Booking;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class MyBookings {

    public static Scene getMyBookingsScene(Runnable callBackAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        root.setTop(buildTopBar(callBackAction));

        VBox contentBox = new VBox(28);
        contentBox.setPadding(new Insets(32, 48, 50, 48));
        contentBox.getChildren().add(buildHeader());

        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(40));
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(36, 36);
        Label loadingLabel = new Label("Loading your bookings...");
        loadingLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        loadingBox.getChildren().addAll(spinner, loadingLabel);
        contentBox.getChildren().add(loadingBox);

        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        root.setCenter(scroll);

        CustomerMarketplaceController controller = new CustomerMarketplaceController();
        loadBookings(contentBox, loadingBox, controller);

        return new Scene(root, 1280, 800);
    }

    private static void loadBookings(VBox contentBox, VBox loadingBox,
            CustomerMarketplaceController controller) {
        controller.loadCurrentCustomerBookings().thenAccept(res -> {
            javafx.application.Platform.runLater(() -> {
                contentBox.getChildren().remove(loadingBox);
                if (res.isSuccess()) {
                    List<Booking> bookings = res.getData() != null ? res.getData() : new ArrayList<>();
                    renderBookingsList(contentBox, bookings);
                } else {
                    VBox errorBox = new VBox(12);
                    errorBox.setAlignment(Pos.CENTER);
                    errorBox.setPadding(new Insets(36));
                    errorBox.setStyle("-fx-background-color: " + CARD_WHITE
                            + "; -fx-background-radius: 16; -fx-border-color: " + BORDER
                            + "; -fx-border-radius: 16;");

                    Label errorTitle = new Label("We couldn't load your bookings.");
                    errorTitle.setStyle(heading(17));
                    Label errorMessage = new Label(res.getMessage() != null
                            ? res.getMessage()
                            : "Please check your connection and try again.");
                    errorMessage.setStyle(body(12));
                    errorMessage.setWrapText(true);

                    Button retry = new Button("Retry");
                    retry.setStyle("-fx-background-color: " + PRIMARY
                            + "; -fx-text-fill: white; -fx-font-family: " + FONT
                            + "; -fx-font-size: 12px; -fx-font-weight: bold;"
                            + " -fx-background-radius: 18; -fx-padding: 8 20; -fx-cursor: hand;");
                    retry.setOnAction(e -> {
                        contentBox.getChildren().remove(errorBox);
                        contentBox.getChildren().add(loadingBox);
                        loadBookings(contentBox, loadingBox, controller);
                    });
                    errorBox.getChildren().addAll(errorTitle, errorMessage, retry);
                    contentBox.getChildren().add(errorBox);
                }
            });
        });
    }

    private static HBox buildTopBar(Runnable callBackAction) {
        HBox bar = new HBox(18);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 42, 14, 42));
        bar.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 0 0 1 0;");

        Button back = new Button("←  Home");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 11px; -fx-cursor: hand;");
        back.setOnAction(e -> callBackAction.run());

        Label title = new Label("My Bookings");
        title.setStyle(heading(19));

        bar.getChildren().addAll(back, title);
        return bar;
    }

    private static VBox buildHeader() {
        VBox header = new VBox(6);
        Label title = new Label("Your bookings");
        title.setStyle(heading(28));
        Label subtitle = new Label("Keep track of your upcoming, pending payment, and previous SpotFit sessions.");
        subtitle.setStyle(body(13));
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private static void renderBookingsList(VBox container, List<Booking> bookings) {
        List<Booking> pendingBookings = new ArrayList<>();
        List<Booking> upcomingBookings = new ArrayList<>();
        List<Booking> pastBookings = new ArrayList<>();

        for (Booking b : bookings) {
            String st = b.getStatus() != null ? b.getStatus().toUpperCase() : "";
            if (BookingStatus.PENDING_PAYMENT.equalsIgnoreCase(st) || BookingStatus.INITIATED.equalsIgnoreCase(st)) {
                pendingBookings.add(b);
            } else if (BookingStatus.CONFIRMED.equalsIgnoreCase(st)
                    || BookingStatus.PAYMENT_SUCCESS.equalsIgnoreCase(st)
                    || BookingStatus.CHECK_IN_PENDING.equalsIgnoreCase(st)) {
                upcomingBookings.add(b);
            } else {
                pastBookings.add(b);
            }
        }

        // Section 1: Pending Payment Requests (Pre-Payment Intents)
        VBox pendingSection = new VBox(12);
        Label pendingTitle = new Label("Pending Payment Requests (Availability Not Reserved)");
        pendingTitle.setStyle(heading(17));
        pendingSection.getChildren().add(pendingTitle);

        if (!pendingBookings.isEmpty()) {
            for (Booking b : pendingBookings) {
                pendingSection.getChildren().add(buildBookingCard(b, true));
            }
        } else {
            Label noPending = new Label("No pending payment requests.");
            noPending
                    .setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
            pendingSection.getChildren().add(noPending);
        }
        container.getChildren().add(pendingSection);

        // Section 2: Upcoming Bookings
        VBox upcomingSection = new VBox(12);
        Label upcomingTitle = new Label("Upcoming Confirmed Bookings");
        upcomingTitle.setStyle(heading(17));
        upcomingSection.getChildren().add(upcomingTitle);

        if (!upcomingBookings.isEmpty()) {
            for (Booking b : upcomingBookings) {
                upcomingSection.getChildren().add(buildBookingCard(b, false));
            }
        } else {
            // Fallback for V1 demo repository bookings if empty
            List<Booking> repoBookings = com.eklavya.spotfit.repository.BookingRepository.getAllBookings();
            if (repoBookings != null && !repoBookings.isEmpty()) {
                for (Booking b : repoBookings) {
                    upcomingSection.getChildren().add(buildBookingCard(b, false));
                }
            } else {
                Label noUpcoming = new Label("No upcoming confirmed bookings.");
                noUpcoming.setStyle(
                        "-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
                upcomingSection.getChildren().add(noUpcoming);
            }
        }
        container.getChildren().add(upcomingSection);

        // Section 3: Past Bookings
        VBox pastSection = new VBox(12);
        Label pastTitle = new Label("Past Bookings");
        pastTitle.setStyle(heading(17));
        pastSection.getChildren().add(pastTitle);

        if (!pastBookings.isEmpty()) {
            for (Booking b : pastBookings) {
                pastSection.getChildren().add(buildBookingCard(b, false));
            }
        } else {
            VBox fallbackPast1 = bookingCard("12", "AUG", "Photography Studio", "Creator Studio", "Kothrud",
                    "12 Aug • 2:00 PM – 4:00 PM", "Completed", false, "SPF-1102", null);
            VBox fallbackPast2 = bookingCard("04", "AUG", "Pottery Workshop", "Clay House", "Aundh",
                    "04 Aug • 11:00 AM – 1:00 PM", "Completed", false, "SPF-0841", null);
            pastSection.getChildren().addAll(fallbackPast1, fallbackPast2);
        }
        container.getChildren().add(pastSection);
    }

    private static VBox buildBookingCard(Booking b, boolean isPending) {
        String spaceName = b.getSpaceName() != null ? b.getSpaceName()
                : (b.getSpace() != null ? b.getSpace().getName() : "SpotFit Space");
        String bTitle = b.getOfferingName() != null ? b.getOfferingName()
                : (b.getBookingType() != null ? b.getBookingType() + " Booking" : "Session");
        String dateStr = b.getDate() != null ? b.getDate() : "Upcoming";
        String timeStr = b.getTime() != null ? b.getTime() : "";
        String code = b.getVerificationCode() != null ? b.getVerificationCode() : "N/A";
        String status = isPending ? "PENDING_PAYMENT" : (b.getStatus() != null ? b.getStatus() : "CONFIRMED");

        return bookingCard("NOW", "SESSION", bTitle, spaceName,
                "Provisional Quote: ₹" + String.format("%.2f", b.getAmount()), dateStr + " " + timeStr, status,
                isPending, code, b);
    }

    private static VBox bookingCard(
            String day,
            String month,
            String bookingTitle,
            String venue,
            String location,
            String time,
            String status,
            boolean isPending,
            String vCode,
            Booking b) {

        VBox card = new VBox();
        card.setPadding(new Insets(18));
        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 16;");

        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPrefWidth(64);
        dateBox.setMinWidth(64);
        dateBox.setPrefHeight(64);
        dateBox.setStyle("-fx-background-color: " + (isPending ? "#FFF3CD" : PILL_BG) + "; -fx-background-radius: 12;");

        Label dayLabel = new Label(day);
        dayLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: "
                + (isPending ? "#856404" : PRIMARY) + ";");

        Label monthLabel = new Label(month);
        monthLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_MUTED + ";");

        dateBox.getChildren().addAll(dayLabel, monthLabel);

        VBox details = new VBox(5);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label title = new Label(bookingTitle);
        title.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");

        Label venueLabel = new Label(venue + " • " + location);
        venueLabel.setStyle(body(10));

        Label timeLabel = new Label(time);
        timeLabel.setStyle(body(10));

        details.getChildren().addAll(title, venueLabel, timeLabel);

        VBox right = new VBox(8);
        right.setAlignment(Pos.CENTER_RIGHT);

        Label statusLabel = new Label(status);
        statusLabel.setStyle(
                "-fx-background-color: " + (isPending ? "#FFF3CD" : PILL_BG) + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: " + (isPending ? "#856404" : PRIMARY) + ";" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 10;");

        Button action = new Button(isPending ? "Continue to Payment" : "View pass");
        action.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + PRIMARY + ";" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");

        action.setOnAction(e -> {
            if (isPending && b != null) {
                com.eklavya.spotfit.navigation.AppNavigator.setScene(
                        com.eklavya.spotfit.view.user.Payment.getPaymentScene(b,
                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        MyBookings.getMyBookingsScene(() -> com.eklavya.spotfit.navigation.AppNavigator
                                                .getPrimaryStage().setScene(
                                                        com.eklavya.spotfit.view.user.UserDash.getUserDashScene())))));
            } else {
                showBookingDetails(bookingTitle, venue, time, vCode, isPending);
            }
        });

        right.getChildren().addAll(statusLabel, action);
        row.getChildren().addAll(dateBox, details, right);
        card.getChildren().add(row);

        return card;
    }

    private static void showBookingDetails(
            String bookingTitle,
            String venue,
            String time,
            String vCode,
            boolean isPending) {

        javafx.stage.Stage dialog = new javafx.stage.Stage();

        dialog.initModality(
                javafx.stage.Modality.APPLICATION_MODAL);

        dialog.initStyle(
                javafx.stage.StageStyle.TRANSPARENT);

        javafx.stage.Stage owner = com.eklavya.spotfit.navigation.AppNavigator
                .getPrimaryStage();

        if (owner != null) {
            dialog.initOwner(owner);
        }

        String safeTitle = bookingTitle != null && !bookingTitle.isBlank()
                ? bookingTitle
                : "SpotFit Booking";

        String safeVenue = venue != null && !venue.isBlank()
                ? venue
                : "SpotFit Space";

        String safeTime = time != null && !time.isBlank()
                ? time
                : "Scheduled";

        String safeCode = vCode != null && !vCode.isBlank()
                ? vCode
                : "Not available";

        // ---------------------------------------------------------
        // SUCCESS ICON
        // ---------------------------------------------------------

        javafx.scene.layout.StackPane successCircle = new javafx.scene.layout.StackPane();

        successCircle.setMinSize(64, 64);
        successCircle.setPrefSize(64, 64);
        successCircle.setMaxSize(64, 64);

        successCircle.setStyle(
                "-fx-background-color: #E8F7EE;" +
                        "-fx-background-radius: 32;" +
                        "-fx-border-color: #B8E2C7;" +
                        "-fx-border-radius: 32;");

        Label checkIcon = new Label(isPending ? "!" : "✓");

        checkIcon.setStyle(
                "-fx-font-family: 'Segoe UI Symbol';" +
                        "-fx-font-size: 31px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        (isPending ? "#B66A14;" : "#27864A;"));

        successCircle.getChildren().add(checkIcon);

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Label eyebrow = new Label(
                isPending
                        ? "BOOKING REQUEST"
                        : "SPOTFIT BOOKING PASS");

        eyebrow.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";");

        Label title = new Label(safeTitle);

        title.setWrapText(true);
        title.setMaxWidth(390);

        title.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_DARK + ";");

        Label status = new Label(
                isPending
                        ? "PENDING PAYMENT"
                        : "CONFIRMED");

        status.setStyle(
                "-fx-background-color: " +
                        (isPending ? "#FFF3CD;" : "#E8F7EE;") +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " +
                        (isPending ? "#F0D98B;" : "#B8E2C7;") +
                        "-fx-border-radius: 14;" +
                        "-fx-text-fill: " +
                        (isPending ? "#856404;" : "#27864A;") +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 12;");

        // ---------------------------------------------------------
        // VERIFICATION CODE
        // ---------------------------------------------------------

        VBox codeBox = new VBox(5);
        codeBox.setAlignment(Pos.CENTER);
        codeBox.setPadding(new Insets(15));
        codeBox.setMaxWidth(Double.MAX_VALUE);

        codeBox.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 14;");

        Label codeCaption = new Label("VERIFICATION CODE");

        codeCaption.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";");

        Label code = new Label(safeCode);

        code.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";");

        codeBox.getChildren().addAll(
                codeCaption,
                code);

        codeBox.setVisible(!isPending);
        codeBox.setManaged(!isPending);

        // ---------------------------------------------------------
        // BOOKING DETAILS
        // ---------------------------------------------------------

        VBox details = new VBox(11);
        details.setPadding(new Insets(15, 17, 15, 17));

        details.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-background-radius: 13;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 13;");

        Label venueCaption = new Label("VENUE");
        venueCaption.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";");

        Label venueValue = new Label(safeVenue);
        venueValue.setWrapText(true);
        venueValue.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_DARK + ";");

        Label timeCaption = new Label("DATE & TIME");
        timeCaption.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";");

        Label timeValue = new Label(safeTime);
        timeValue.setWrapText(true);
        timeValue.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_DARK + ";");

        details.getChildren().addAll(
                venueCaption,
                venueValue,
                timeCaption,
                timeValue);

        // ---------------------------------------------------------
        // INFORMATION
        // ---------------------------------------------------------

        Label information = new Label(
                isPending
                        ? "Complete the demo payment to confirm this booking."
                        : "Present this verification code to the space manager when you arrive.");

        information.setWrapText(true);
        information.setMaxWidth(390);
        information.setAlignment(Pos.CENTER);
        information.setTextAlignment(
                javafx.scene.text.TextAlignment.CENTER);

        information.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";");

        // ---------------------------------------------------------
        // CLOSE BUTTON
        // ---------------------------------------------------------

        Button close = new Button("Done");

        close.setPrefHeight(42);
        close.setMaxWidth(Double.MAX_VALUE);

        close.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                        "-fx-background-radius: 21;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");

        close.setOnMouseEntered(e -> close.setStyle(
                "-fx-background-color: derive(" +
                        PRIMARY + ", -12%);" +
                        "-fx-background-radius: 21;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"));

        close.setOnMouseExited(e -> close.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                        "-fx-background-radius: 21;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"));

        close.setOnAction(e -> dialog.close());

        // ---------------------------------------------------------
        // DIALOG CARD
        // ---------------------------------------------------------

        VBox card = new VBox(
                13,
                successCircle,
                eyebrow,
                title,
                status,
                codeBox,
                details,
                information,
                close);

        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26));
        card.setPrefWidth(460);
        card.setMaxWidth(460);

        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + BORDER_STRONG + ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(59,36,23,0.24), 28, 0.18, 0, 8);");

        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane(card);

        overlay.setPadding(new Insets(24));

        overlay.setStyle(
                "-fx-background-color: rgba(59,36,23,0.18);");

        Scene scene = new Scene(
                overlay,
                540,
                isPending ? 500 : 610);

        scene.setFill(
                javafx.scene.paint.Color.TRANSPARENT);

        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }
}
