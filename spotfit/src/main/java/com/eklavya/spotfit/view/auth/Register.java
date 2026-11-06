package com.eklavya.spotfit.view.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import com.eklavya.spotfit.controller.auth.AuthController;
import javafx.application.Platform;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.util.ResponsiveUtil.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

public class Register {

    public static Scene getRegisterScene(Runnable onAccountCreated, Runnable onGoToLogin) {

        HBox root = new HBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, " + BG_PANEL_GRADIENT_TOP + ", " + BG_PAGE + ");");
        root.setAlignment(Pos.CENTER);

        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox registerCard = buildRegisterCard(onAccountCreated, onGoToLogin);

        VBox rightWrapper = new VBox(registerCard);
        rightWrapper.setAlignment(Pos.CENTER);
        rightWrapper.setPadding(new Insets(40));

        root.getChildren().addAll(leftPanel, rightWrapper);

        ScrollPane scrollRoot = new ScrollPane(root);
        scrollRoot.setFitToWidth(true);
        scrollRoot.setFitToHeight(true);
        scrollRoot.setStyle("-fx-background: " + BG_PAGE + "; -fx-background-color: " + BG_PAGE + ";");

        Rectangle2D screen = screenBounds();
        Scene scene = new Scene(scrollRoot, screen.getWidth(), screen.getHeight());

        bindWidthPercent(rightWrapper, scene, 0.40, 500, 680);
        registerCard.prefWidthProperty().bind(rightWrapper.widthProperty().subtract(80));

        return scene;
    }

    private static VBox buildLeftPanel() {
        var logo = Login.loadLogo(32);
        Label logoText = new Label("SpotFit");
        logoText.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY + ";");
        HBox logoRow = new HBox(10, logo, logoText);
        logoRow.setAlignment(Pos.CENTER_LEFT);

        Label badge = pillBadge("✨  Join Thousands of Creators");

        Text heading = new Text("Start your journey with SpotFit");
        heading.setStyle(heading(34));
        heading.setWrappingWidth(460);

        Text subtext = new Text("Create your account and discover spaces tailored to your interests, goals, and activities.");
        subtext.setStyle(body(15));
        subtext.setWrappingWidth(480);

        VBox benefits = new VBox(12,
                benefitRow("Book spaces instantly"),
                benefitRow("Save favorite places"),
                benefitRow("Receive personalized recommendations"),
                benefitRow("Track bookings and history"),
                benefitRow("Join workshops and events")
        );

        Region photoPlaceholder = new Region();
        photoPlaceholder.setPrefSize(500, 240);
        photoPlaceholder.setMaxWidth(500);
        photoPlaceholder.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #C97C3D, #6B3410);" +
                "-fx-background-radius: 16;"
        );

        VBox content = new VBox(20, logoRow, badge, heading, subtext, benefits, photoPlaceholder);
        content.setMaxWidth(560);
        content.setPadding(new Insets(50, 60, 50, 60));

        VBox wrapper = new VBox(content);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        return wrapper;
    }

    private static HBox benefitRow(String text) {
        Label check = new Label("✓");
        check.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-size: 14px; -fx-font-weight: bold;" +
                "-fx-background-color: " + PILL_BG + "; -fx-background-radius: 50%; -fx-padding: 3 7 3 7;");
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px;");
        HBox row = new HBox(10, check, label);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static VBox buildRegisterCard(Runnable onAccountCreated, Runnable onGoToLogin) {
        VBox card = card(520);

        Text title = new Text("Create Account");
        title.setStyle(heading(26));

        Text subtitle = new Text("Please fill in your details to get started.");
        subtitle.setStyle(body(14));

        TextField nameField = textField("John Doe");
        VBox fullNameBlock = labeledField("Full Name", nameField);
        TextField emailField = textField("john@example.com");
        VBox emailBlock = labeledField("Email Address", emailField);

        Label mobileLabel = new Label("Mobile Number");
        mobileLabel.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        Label countryCode = new Label("+91");
        countryCode.setPrefHeight(48);
        countryCode.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 0 14 0 14;" +
                "-fx-alignment: center;" +
                "-fx-background-radius: 10 0 0 10;" +
                "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 1 1; -fx-border-radius: 10 0 0 10;"
        );
        TextField mobile = textField("00000 00000");
        mobile.setStyle(mobile.getStyle() + "-fx-background-radius: 0 10 10 0; -fx-border-radius: 0 10 10 0;");
        HBox.setHgrow(mobile, Priority.ALWAYS);
        HBox mobileRow = new HBox(countryCode, mobile);
        VBox mobileBlock = new VBox(6, mobileLabel, mobileRow);

        PasswordField passField = passwordField("••••••••");
        VBox passwordBlock = labeledField("Password", passField);
        PasswordField confirmPassField = passwordField("••••••••");
        VBox confirmBlock = labeledField("Confirm Password", confirmPassField);
        HBox passwordRow = new HBox(16, passwordBlock, confirmBlock);
        HBox.setHgrow(passwordBlock, Priority.ALWAYS);
        HBox.setHgrow(confirmBlock, Priority.ALWAYS);
        passwordBlock.setMaxWidth(Double.MAX_VALUE);
        confirmBlock.setMaxWidth(Double.MAX_VALUE);

        CheckBox terms = checkbox("I agree to the Terms & Conditions and Privacy Policy.");

        AuthController authController = new AuthController();

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(460);
        statusLabel.setStyle("-fx-font-size: 13px;");

        var createBtn = primaryButton("Create Account");
        createBtn.setOnAction(e -> {
            String nameStr = nameField.getText().trim();
            String mailStr = emailField.getText().trim();
            String phoneStr = countryCode.getText() + " " + mobile.getText().trim();
            String passStr = passField.getText();
            String confirmPassStr = confirmPassField.getText();
            boolean termsAcc = terms.isSelected();

            statusLabel.setText("");
            createBtn.setDisable(true);
            createBtn.setText("Creating Account...");

            authController.registerUserWithProfile(nameStr, mailStr, phoneStr, passStr, confirmPassStr, termsAcc)
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        if (throwable != null) {
                            createBtn.setDisable(false);
                            createBtn.setText("Create Account");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Something went wrong while contacting Firebase. Please try again.");
                            return;
                        }

                        if (result.isSuccess()) {
                            authController.sendVerificationEmail()
                                    .whenComplete((vResult, vThrowable) -> Platform.runLater(() -> {
                                        createBtn.setDisable(false);
                                        createBtn.setText("Create Account");
                                        onAccountCreated.run();
                                    }));
                        } else {
                            createBtn.setDisable(false);
                            createBtn.setText("Create Account");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        HBox divider = divider("OR");

        var googleBtn = outlineButton("Continue with Google");
        var appleBtn = outlineButton("Continue with Apple");
        googleBtn.setOnAction(ev -> {
            statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;");
            statusLabel.setText("Social sign-in is not available yet.");
        });
        appleBtn.setOnAction(ev -> {
            statusLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;");
            statusLabel.setText("Social sign-in is not available yet.");
        });
        HBox socialRow = new HBox(12, googleBtn, appleBtn);
        HBox.setHgrow(googleBtn, Priority.ALWAYS);
        HBox.setHgrow(appleBtn, Priority.ALWAYS);

        Label loginPrompt = new Label("Already have an account?");
        loginPrompt.setStyle(body(13));
        Label loginLink = new Label(" Login");
        loginLink.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand;");
        loginLink.setOnMouseClicked(e -> onGoToLogin.run());
        HBox loginRow = new HBox(loginPrompt, loginLink);
        loginRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                title, subtitle,
                fullNameBlock, emailBlock, mobileBlock, passwordRow,
                terms, statusLabel, createBtn, divider, socialRow, loginRow
        );

        return card;
    }

    private static VBox labeledField(String labelText, javafx.scene.control.Control field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        return new VBox(6, label, field);
    }
}