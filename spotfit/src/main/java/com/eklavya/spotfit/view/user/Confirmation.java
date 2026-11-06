package com.eklavya.spotfit.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.util.StyleConstants.*;
import com.eklavya.spotfit.view.common.WelcomePage;

public class Confirmation {

        public static Scene getConfirmationScene(
                        com.eklavya.spotfit.model.Booking booking,
                        Runnable callBackAction) {

                com.eklavya.spotfit.model.Space space = booking.getSpace();
                com.eklavya.spotfit.model.Offering offering = booking.getOffering();

                VBox root = new VBox(20);

                root.setAlignment(
                                Pos.CENTER);

                root.setPadding(
                                new Insets(40));

                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                Label icon = new Label("✓");

                icon.setPrefSize(
                                70,
                                70);

                icon.setAlignment(
                                Pos.CENTER);

                icon.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 35;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-size: 34px;" +
                                                "-fx-font-weight: bold;");

                String titleStr = "Booking confirmed!";
                if ("VENUE".equalsIgnoreCase(offering.getBookingType()))
                        titleStr = "Request submitted!";
                else if ("RENTAL".equalsIgnoreCase(offering.getBookingType()))
                        titleStr = "Rental confirmed!";
                Label title = new Label(titleStr);

                title.setStyle(
                                heading(28));

                String actionStr = "booked";
                if ("VENUE".equalsIgnoreCase(offering.getBookingType()))
                        actionStr = "requested";
                if ("RENTAL".equalsIgnoreCase(offering.getBookingType()))
                        actionStr = "rented";

                Label subtitle = new Label(
                                "Your " + space.getName() + " session is " + actionStr + ".");

                subtitle.setStyle(
                                body(12));

                Label details = new Label(
                                booking.getDate() + " • " + booking.getTime() + "\n" +
                                                space.getName() + " • " + space.getLocation());

                details.setAlignment(
                                Pos.CENTER);

                details.setStyle(
                                body(11));

                VBox verifyBox = new VBox(8);
                verifyBox.setAlignment(Pos.CENTER);
                verifyBox.setPadding(new Insets(15));
                verifyBox.setMaxWidth(300);
                verifyBox.setStyle(
                                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

                Label verifyTitle = new Label("Verification Code");
                verifyTitle.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 13px; -fx-text-fill: #94a3b8;");

                Label verifyCode = new Label(booking.getVerificationCode());
                verifyCode.setStyle(
                                "-fx-font-family: 'Monospaced'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: "
                                                + PRIMARY + "; -fx-letter-spacing: 2px;");

                Label verifyInst = new Label("Show this code to the manager\nwhen you arrive.");
                verifyInst.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 11px; -fx-text-fill: #64748b;");
                verifyInst.setAlignment(Pos.CENTER);
                verifyInst.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                verifyBox.getChildren().addAll(verifyTitle, verifyCode, verifyInst);

                String viewBtnStr = "View Booking";
                if ("VENUE".equalsIgnoreCase(offering.getBookingType()))
                        viewBtnStr = "View Request Status";

                Button qr = new Button(viewBtnStr);
                qr.setPrefHeight(44);
                qr.setPrefWidth(220);
                qr.setStyle(
                                "-fx-background-color: " + PRIMARY + ";" +
                                                "-fx-background-radius: 22;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;");
                qr.setOnAction(e -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                        MyBookings.getMyBookingsScene(() -> com.eklavya.spotfit.navigation.AppNavigator.setScene(UserDash.getUserDashScene()))
                ));

                Button homeBtn = new Button("Return to Home");
                homeBtn.setPrefHeight(44);
                homeBtn.setPrefWidth(220);
                homeBtn.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-border-color: " + PRIMARY + ";" +
                                                "-fx-border-radius: 22;" +
                                                "-fx-background-radius: 22;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;");
                homeBtn.setOnAction(e -> com.eklavya.spotfit.navigation.AppNavigator.setScene(UserDash.getUserDashScene()));

                root.getChildren().addAll(
                                icon,
                                title,
                                subtitle,
                                details,
                                verifyBox,
                                qr,
                                homeBtn);

                return new Scene(
                                root,
                                1280,
                                800);
        }
}
