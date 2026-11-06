package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.controller.user.CustomerPaymentController;
import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.config.AppConfig;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class Payment {

    public static Scene getPaymentScene(Booking booking, Runnable callBackAction) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        root.setTop(buildTopBar(callBackAction));

        VBox content = buildContent(booking, callBackAction);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;");

        root.setCenter(scroll);

        return new Scene(root, 1280, 800);
    }

    private static HBox buildTopBar(Runnable callBackAction) {
        HBox bar = new HBox(18);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 42, 14, 42));
        bar.setStyle("-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;");

        Button back = new Button("←  Back");
        back.setStyle("-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-cursor: hand;");
        back.setOnAction(e -> callBackAction.run());

        Label title = new Label("Payment");
        title.setStyle(heading(19));

        bar.getChildren().addAll(back, title);
        return bar;
    }

    private static VBox buildContent(Booking booking, Runnable callBackAction) {
        VBox content = new VBox(24);
        content.setPadding(new Insets(32, 48, 50, 48));

        String mode = AppConfig.getPaymentMode();
        boolean isDemo = "DEMO".equalsIgnoreCase(mode);

        if (isDemo) {
            Label demoWarning = new Label("PAYMENT");
            demoWarning.setStyle(
                    "-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-padding: 10 15; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 13px;");
            demoWarning.setMaxWidth(850);
            content.getChildren().add(demoWarning);
        }

        content.getChildren().addAll(
                buildHeader(booking),
                buildBookingSummary(booking),
                buildPaymentSection(booking, isDemo, callBackAction));

        return content;
    }

    private static VBox buildHeader(Booking booking) {
        VBox box = new VBox(5);
        Label title = new Label("Complete your booking");
        title.setStyle(heading(27));
        String offeringName = booking.getOfferingName() != null ? booking.getOfferingName() : "Session";
        Label subtitle = new Label("Review your " + offeringName + " details before continuing to payment.");
        subtitle.setStyle(body(12));
        box.getChildren().addAll(title, subtitle);
        return box;
    }

    private static VBox buildBookingSummary(Booking booking) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setMaxWidth(850);
        card.setStyle("-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 16;");

        Label heading = new Label("Booking summary");
        heading.setStyle(heading(17));

        String spaceName = booking.getSpaceName() != null ? booking.getSpaceName()
                : (booking.getSpace() != null ? booking.getSpace().getName() : "SpotFit Space");
        String offeringName = booking.getOfferingName() != null ? booking.getOfferingName() : "Session";
        String date = booking.getDate() != null ? booking.getDate() : "Upcoming";
        String time = booking.getTime() != null ? booking.getTime() : "Scheduled";

        card.getChildren().addAll(
                heading,
                row("Space", spaceName),
                row("Offering", offeringName),
                row("Date", date),
                row("Time", time));

        Separator separator = new Separator();

        double totalAmount = booking.getTotalAmountPaise() > 0 ? booking.getTotalAmountPaise() / 100.0
                : booking.getAmount();

        Label total = new Label("Total     ₹" + String.format("%.2f", totalAmount));
        total.setStyle("-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT_DARK + ";");

        card.getChildren().addAll(separator, total);
        return card;
    }

    private static HBox row(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label left = new Label(label);
        left.setStyle(body(11));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label right = new Label(value);
        right.setStyle("-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT_DARK + ";");

        row.getChildren().addAll(left, spacer, right);
        return row;
    }

    private static VBox buildPaymentSection(Booking booking, boolean isDemo, Runnable callBackAction) {
        VBox section = new VBox(14);
        section.setPadding(new Insets(20));
        section.setMaxWidth(850);
        section.setStyle("-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 16;");

        Label title = new Label("Choose payment method");
        title.setStyle(heading(17));

        ToggleGroup group = new ToggleGroup();
        RadioButton upi = paymentOption("UPI", "Pay using your UPI app", group, true);
        RadioButton card = paymentOption("Card", "Credit or debit card", group, false);
        RadioButton netBanking = paymentOption("Net Banking", "Pay using your bank account", group, false);

        VBox options = new VBox(10);
        options.getChildren().addAll(upi, card, netBanking);

        Label secure = new Label(
                isDemo ? "Transaction will proceed soon." : "🔒 Secure payment • You will be redirected to Razorpay");
        secure.setStyle(body(10));

        double totalAmount = booking.getTotalAmountPaise() > 0 ? booking.getTotalAmountPaise() / 100.0
                : booking.getAmount();

        Button pay = new Button("Pay ₹" + String.format("%.2f", totalAmount));
        pay.setPrefHeight(46);
        pay.setMaxWidth(Double.MAX_VALUE);
        pay.setStyle("-fx-background-color: " + PRIMARY + ";" +
                "-fx-background-radius: 23;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;");

        VBox processingBox = new VBox(10);
        processingBox.setAlignment(Pos.CENTER);
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(24, 24);
        Label processingLabel = new Label("Initializing payment...");
        processingLabel.setStyle(body(11));
        processingBox.getChildren().addAll(spinner, processingLabel);
        processingBox.setVisible(false);
        processingBox.setManaged(false);

        CustomerPaymentController controller = new CustomerPaymentController();

        pay.setOnAction(e -> {
            pay.setDisable(true);
            processingBox.setVisible(true);
            processingBox.setManaged(true);
            processingLabel.setText("Initializing payment intent...");

            controller.initiatePayment(booking).thenAccept(res -> {
                Platform.runLater(() -> {
                    if (res.isSuccess()) {
                        com.eklavya.spotfit.model.Payment paymentAttempt = res.getData();
                        if (isDemo) {
                            processingLabel.setText("Simulating payment verification...");
                            controller.simulateDemoSuccess(paymentAttempt, booking).thenAccept(verRes -> {
                                Platform.runLater(() -> {
                                    processingBox.setVisible(false);
                                    processingBox.setManaged(false);
                                    if (verRes.isSuccess()) {
                                        showDemoSuccess(booking, callBackAction);
                                    } else {
                                        showError("Verification Failed", verRes.getMessage());
                                        pay.setDisable(false);
                                    }
                                });
                            });
                        } else {
                            // In a real flow we'd open Razorpay here
                            processingBox.setVisible(false);
                            processingBox.setManaged(false);
                            showError("Not Implemented", "Live Razorpay checkout requires a trusted backend.");
                            pay.setDisable(false);
                        }
                    } else {
                        processingBox.setVisible(false);
                        processingBox.setManaged(false);
                        showError("Payment Initialization Failed", res.getMessage());
                        pay.setDisable(false);
                    }
                });
            });
        });

        section.getChildren().addAll(title, options, secure, processingBox, pay);
        return section;
    }

    private static RadioButton paymentOption(String title, String description, ToggleGroup group, boolean selected) {
        RadioButton option = new RadioButton();
        option.setToggleGroup(group);
        option.setSelected(selected);
        option.setPrefHeight(58);
        option.setMaxWidth(Double.MAX_VALUE);
        option.setText(title + "    " + description);
        option.setStyle("-fx-background-color: " + BG_PAGE + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 0 15;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-cursor: hand;");
        return option;
    }

    private static void showDemoSuccess(Booking booking, Runnable callBackAction) {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        javafx.stage.Stage owner = com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage();
        if (owner != null) {
            dialog.initOwner(owner);
        }

        StackPane successCircle = new StackPane();
        successCircle.setMinSize(64, 64);
        successCircle.setPrefSize(64, 64);
        successCircle.setMaxSize(64, 64);
        successCircle.setStyle(
                "-fx-background-color: #E8F7EE;" +
                        "-fx-background-radius: 32;" +
                        "-fx-border-color: #B8E2C7;" +
                        "-fx-border-radius: 32;");

        Label check = new Label("✓");
        check.setStyle(
                "-fx-font-family: 'Segoe UI Symbol';" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #27864A;");
        successCircle.getChildren().add(check);

        Label title = new Label("Payment successful");
        title.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + TEXT_DARK + ";");

        Label subtitle = new Label("Your checkout simulation was completed.");
        subtitle.setStyle(body(13));

        double amount = booking.getTotalAmountPaise() > 0
                ? booking.getTotalAmountPaise() / 100.0
                : booking.getAmount();

        VBox summary = new VBox(9);
        summary.setPadding(new Insets(14, 16, 14, 16));
        summary.setMaxWidth(Double.MAX_VALUE);
        summary.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;");

        Label amountLabel = new Label("Amount: ₹" + String.format("%.2f", amount));
        amountLabel.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";");

        String bookingId = booking.getBookingId() != null && !booking.getBookingId().isBlank()
                ? booking.getBookingId()
                : "Generated booking intent";
        Label bookingLabel = new Label("Booking ID: " + bookingId);
        bookingLabel.setWrapText(true);
        bookingLabel.setStyle(body(11));
        summary.getChildren().addAll(amountLabel, bookingLabel);

        Label notice = new Label(
        "Payment approved. Your booking is now confirmed and available in My Bookings.");
        notice.setWrapText(true);
        notice.setMaxWidth(390);
        notice.setStyle(
                "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #856404;" +
                        "-fx-background-color: #FFF7DB;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12;");

        Button done = new Button("Back to My Bookings");
        done.setPrefHeight(42);
        done.setMaxWidth(Double.MAX_VALUE);
        done.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                        "-fx-background-radius: 21;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: " + FONT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");
        done.setOnAction(e -> {
            dialog.close();
            if (callBackAction != null) {
                callBackAction.run();
            }
        });

        VBox card = new VBox(14, successCircle, title, subtitle, summary, notice, done);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28));
        card.setPrefWidth(460);
        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + BORDER_STRONG + ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(59,36,23,0.24), 28, 0.18, 0, 8);");

        StackPane overlay = new StackPane(card);
        overlay.setPadding(new Insets(24));
        overlay.setStyle("-fx-background-color: rgba(59,36,23,0.18);");

        Scene scene = new Scene(overlay, 540, 560);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    private static void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}