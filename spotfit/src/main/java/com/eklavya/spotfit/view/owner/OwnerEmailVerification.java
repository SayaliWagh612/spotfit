package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerAuthController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Owner Email Verification page for SpotFit.
 */
public class OwnerEmailVerification {

    private static int secondsRemaining = 0;
    private static Timeline cooldownTimeline;

    public static Scene getVerificationScene(String email, Runnable onVerified, Runnable onBack) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: " + OwnerUi.BACKGROUND + ";");

        Label badge = OwnerUi.badge("EMAIL VERIFICATION REQUIRED");

        Label title = OwnerUi.heading("Verify Your Owner Email", 28);

        Label subtitle = OwnerUi.body(
                "We sent a verification link to:\n" + (email != null ? email : "your email address"),
                14,
                OwnerUi.TEXT_MUTED
        );
        subtitle.setAlignment(Pos.CENTER);

        Label statusLabel = OwnerUi.body("", 13, OwnerUi.TEXT_DARK);
        statusLabel.setWrapText(true);

        Button verifiedBtn = OwnerUi.primaryButton("I've Verified My Email  \u2192");
        verifiedBtn.setMaxWidth(360);

        Button resendBtn = OwnerUi.secondaryButton("Resend Verification Email");
        resendBtn.setMaxWidth(360);

        Button backBtn = OwnerUi.secondaryButton("\u2190 Back to Login");
        backBtn.setMaxWidth(360);

        OwnerAuthController ownerAuthController = new OwnerAuthController();

        verifiedBtn.setOnAction(e -> {
            verifiedBtn.setDisable(true);
            statusLabel.setText("Checking verification status...");
            statusLabel.setStyle("-fx-text-fill: " + OwnerUi.TEXT_MUTED + ";");

            ownerAuthController.checkOwnerEmailVerification()
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        verifiedBtn.setDisable(false);
                        if (throwable != null) {
                            statusLabel.setText("Verification check failed: " + throwable.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isSuccess() && result.isEmailVerified()) {
                            statusLabel.setText("Email verified successfully! Continuing...");
                            statusLabel.setStyle("-fx-text-fill: #388E3C;");
                            onVerified.run();
                        } else {
                            statusLabel.setText("Email not verified yet. Please click the link sent to your email.");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        }
                    }));
        });

        resendBtn.setOnAction(e -> {
            resendBtn.setDisable(true);
            statusLabel.setText("Sending verification email...");

            ownerAuthController.sendVerificationEmail()
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        if (throwable != null) {
                            resendBtn.setDisable(false);
                            statusLabel.setText("Failed to send email: " + throwable.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isSuccess()) {
                            statusLabel.setText("Verification email sent! Check your inbox.");
                            statusLabel.setStyle("-fx-text-fill: #388E3C;");
                            startCooldown(resendBtn);
                        } else {
                            resendBtn.setDisable(false);
                            statusLabel.setText(result != null ? result.getMessage() : "Could not send verification email.");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        }
                    }));
        });

        backBtn.setOnAction(e -> onBack.run());

        VBox card = new VBox(16, badge, title, subtitle, statusLabel, verifiedBtn, resendBtn, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(480);
        card.setStyle("-fx-background-color: " + OwnerUi.SURFACE + ";" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 4);");

        root.getChildren().add(card);
        return OwnerUi.sizedScene(root);
    }

    private static void startCooldown(Button button) {
        secondsRemaining = 60;
        button.setDisable(true);
        button.setText("Resend Email (" + secondsRemaining + "s)");

        if (cooldownTimeline != null) {
            cooldownTimeline.stop();
        }

        cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            if (secondsRemaining > 0) {
                button.setText("Resend Email (" + secondsRemaining + "s)");
            } else {
                button.setDisable(false);
                button.setText("Resend Verification Email");
                cooldownTimeline.stop();
            }
        }));
        cooldownTimeline.setCycleCount(Timeline.INDEFINITE);
        cooldownTimeline.play();
    }
}
