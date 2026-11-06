package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.model.Booking;
import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.eklavya.spotfit.util.StyleConstants.*;

/** SpotFit-themed dialogs used by the real Workshop booking flow. */
public final class CustomerBookingDialogs {

    private CustomerBookingDialogs() {
    }

    public static boolean confirmBooking(double quoteRupees) {
        return showDialog(
                "₹",
                "Confirm your booking",
                "Review the provisional quote before creating your booking request.",
                "Provisional quote",
                "₹" + String.format("%.2f", quoteRupees),
                null,
                "Confirm Booking",
                "Cancel",
                false);
    }

    public static boolean showBookingCreated(Booking booking) {
        double amount = booking.getTotalAmountPaise() > 0
                ? booking.getTotalAmountPaise() / 100.0
                : booking.getAmount();

        return showDialog(
                "✓",
                "Booking request created",
                "Your booking intent is ready and waiting for payment.",
                "Amount due",
                "₹" + String.format("%.2f", amount),
                "Booking ID: " + booking.getBookingId()
                        + "\nStatus: PENDING_PAYMENT\nAvailability is not reserved until payment is verified.",
                "View My Bookings",
                "Close",
                true);
    }

    /** Shows the standard SpotFit-themed confirmation before ending a session. */
    public static boolean confirmLogout(String accountName) {
        String displayName = accountName == null || accountName.isBlank()
                ? "Current customer"
                : accountName.trim();

        return showDialog(
                "↪",
                "Log out of SpotFit?",
                "You will be returned to the sign-in page.",
                "Signed in as",
                displayName,
                "Your bookings and saved spaces will remain safely stored in your account.",
                "Logout",
                "Stay signed in",
                false);
    }

    private static boolean showDialog(
            String iconText,
            String titleText,
            String subtitleText,
            String amountCaption,
            String amountText,
            String noticeText,
            String primaryText,
            String secondaryText,
            boolean success) {

        AtomicBoolean primarySelected = new AtomicBoolean(false);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        Stage owner = AppNavigator.getPrimaryStage();
        if (owner != null) {
            dialog.initOwner(owner);
        }

        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(58, 58);
        iconCircle.setPrefSize(58, 58);
        iconCircle.setMaxSize(58, 58);
        iconCircle.setStyle(
                "-fx-background-color: " + (success ? "#E8F7EE" : PILL_BG) + ";" +
                "-fx-background-radius: 29;" +
                "-fx-border-color: " + (success ? "#B8E2C7" : BORDER_STRONG) + ";" +
                "-fx-border-radius: 29;");

        Label icon = new Label(iconText);
        icon.setStyle(
                "-fx-font-family: 'Segoe UI Symbol';" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + (success ? "#27864A" : PRIMARY) + ";");
        iconCircle.getChildren().add(icon);

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT_DARK + ";");

        Label subtitle = new Label(subtitleText);
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(390);
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-text-alignment: center;");

        VBox amountBox = new VBox(4);
        amountBox.setAlignment(Pos.CENTER);
        amountBox.setPadding(new Insets(13));
        amountBox.setMaxWidth(Double.MAX_VALUE);
        amountBox.setStyle(
                "-fx-background-color: " + BG_PAGE + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;");

        Label amountLabel = new Label(amountCaption);
        amountLabel.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";");
        Label amountValue = new Label(amountText);
        amountValue.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";");
        amountBox.getChildren().addAll(amountLabel, amountValue);

        VBox card = new VBox(13, iconCircle, title, subtitle, amountBox);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(27));
        card.setPrefWidth(460);
        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + BORDER_STRONG + ";" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(59,36,23,0.24), 28, 0.18, 0, 8);");

        if (noticeText != null && !noticeText.isBlank()) {
            Label notice = new Label(noticeText);
            notice.setWrapText(true);
            notice.setMaxWidth(Double.MAX_VALUE);
            notice.setStyle(
                    "-fx-font-family: " + FONT + ";" +
                    "-fx-font-size: 11px;" +
                    "-fx-text-fill: #856404;" +
                    "-fx-background-color: #FFF7DB;" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 11;");
            card.getChildren().add(notice);
        }

        Button secondary = new Button(secondaryText);
        secondary.setPrefHeight(40);
        secondary.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + BORDER_STRONG + ";" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;");
        secondary.setOnAction(e -> dialog.close());

        Button primary = new Button(primaryText);
        primary.setPrefHeight(40);
        primary.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(primary, Priority.ALWAYS);
        primary.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-background-radius: 20;" +
                "-fx-text-fill: white;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 20;" +
                "-fx-cursor: hand;");
        primary.setOnAction(e -> {
            primarySelected.set(true);
            dialog.close();
        });

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox actions = new HBox(10, secondary, gap, primary);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().add(actions);

        StackPane overlay = new StackPane(card);
        overlay.setPadding(new Insets(24));
        overlay.setStyle("-fx-background-color: rgba(59,36,23,0.18);");

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);
        dialog.showAndWait();

        return primarySelected.get();
    }
}
