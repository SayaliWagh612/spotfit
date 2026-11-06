package com.eklavya.spotfit.view.admin;

import com.eklavya.spotfit.navigation.AppNavigator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

/**
 * INACTIVE PROTOTYPE UI — Unused in production.
 * Platform Admin approval operations are performed manually by developers directly in the Firebase Console.
 * Disconnected from active application navigation.
 */
public class AdminLoginPage {

    private boolean isOtpStep = false;
    private String emailInput = "";
    private VBox mainCardContainer;
    private Runnable onSuccessLogin;

    public Scene getAdminLoginPageScene(Runnable callbackBackHome) {
        this.onSuccessLogin = () -> {
            AdminDashboard dashboard = new AdminDashboard();
            AppNavigator.setScene(dashboard.getAdminDashboardPageScene("Admin", callbackBackHome));
            AppNavigator.setMaximized(true);
        };

        // Header
        Label logo = new Label("SpotFit Admin");
        logo.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + ACCENT + ";");

        Label backLink = new Label("← Back to Welcome");
        backLink.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + ACCENT + "; -fx-cursor:hand;");
        backLink.setOnMouseClicked(e -> callbackBackHome.run());

        HBox header = new HBox(logo, hgrow(), backLink);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 64, 24, 64));

        // Form Card
        mainCardContainer = createCredentialsStepCard();

        VBox centerBox = new VBox(mainCardContainer);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40, 20, 60, 20));
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        VBox contentBox = new VBox(header, centerBox);
        contentBox.setStyle("-fx-background-color:" + BACKGROUND + ";");

        ScrollPane root = scroll(contentBox);
        double w = (AppNavigator.getPrimaryStage() != null && AppNavigator.getPrimaryStage().getWidth() > 0) ? AppNavigator.getPrimaryStage().getWidth() : 1280;
        double h = (AppNavigator.getPrimaryStage() != null && AppNavigator.getPrimaryStage().getHeight() > 0) ? AppNavigator.getPrimaryStage().getHeight() : 800;
        return new Scene(root, w, h);
    }

    private VBox createCredentialsStepCard() {
        Label title = heading("SpotFit Platform Governance", 24);
        Label subtitle = muted("Enter authorized system credentials to proceed with 2FA verification.");

        Label emailLabel = heading("Admin Email Address", 14);
        TextField emailField = searchField("admin@spotfit.com");
        emailField.setText("admin@spotfit.com");

        Label passwordLabel = heading("Password", 14);
        PasswordField passwordField = new PasswordField();
        passwordField.setText("••••••••••••");
        passwordField.setStyle("-fx-background-color:" + SURFACE
                + "; -fx-font-size:14px; -fx-background-radius:10px; -fx-border-color:" + BORDER
                + "; -fx-border-radius:10px; -fx-padding:9 14;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + RED_TEXT + ";");
        errorLabel.setVisible(false);

        Button continueBtn = primaryButton("Send 2FA Security Code →");
        continueBtn.setMaxWidth(Double.MAX_VALUE);
        continueBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                errorLabel.setText("Please enter a valid Admin email.");
                errorLabel.setVisible(true);
                return;
            }
            this.emailInput = email;
            switchToOtpStep();
        });

        VBox card = card(
                title, subtitle,
                new VBox(6, emailLabel, emailField),
                new VBox(6, passwordLabel, passwordField),
                errorLabel,
                continueBtn);
        card.setMaxWidth(420);
        return card;
    }

    private void switchToOtpStep() {
        mainCardContainer.getChildren().clear();

        Label title = heading("Enter 2FA Security Code", 24);
        Label subtitle = muted("A 6-digit verification code has been dispatched to " + emailInput);

        Label otpLabel = heading("Email Verification OTP", 14);
        TextField otpField = searchField("Enter 6-digit OTP (e.g. 583921)");
        otpField.setText("583921");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + RED_TEXT + ";");
        errorLabel.setVisible(false);

        Button verifyBtn = primaryButton("Verify & Enter Admin Console");
        verifyBtn.setMaxWidth(Double.MAX_VALUE);
        verifyBtn.setOnAction(e -> {
            if (otpField.getText().trim().length() < 4) {
                errorLabel.setText("Invalid OTP code. Please enter 6-digit security code.");
                errorLabel.setVisible(true);
                return;
            }
            onSuccessLogin.run();
        });

        Button resendBtn = secondaryButton("Resend Security Code");
        resendBtn.setMaxWidth(Double.MAX_VALUE);
        resendBtn.setOnAction(e -> {
            errorLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + GREEN_TEXT + ";");
            errorLabel.setText("✔ New 6-digit verification code sent to " + emailInput);
            errorLabel.setVisible(true);
        });

        mainCardContainer.getChildren().addAll(
                title, subtitle,
                new VBox(6, otpLabel, otpField),
                errorLabel,
                verifyBtn,
                resendBtn);
    }
}
