package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.controller.manager.ManagerAuthController;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

// SpotFit - Verify Manager Invite & Email Verification
public class VerifyInvite {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";

    private final ManagerAuthController authController;
    private Scene verifyPageScene;
    private boolean isProcessing = false;
    private int cooldownSecondsLeft = 0;
    private Timeline cooldownTimeline;

    public VerifyInvite() {
        this(new ManagerAuthController());
    }

    public VerifyInvite(ManagerAuthController authController) {
        this.authController = (authController != null) ? authController : new ManagerAuthController();
    }

    public Scene getVerifyPageScene(
            AuthResult authResult,
            String invitationCode,
            String managerName,
            String managerPhone,
            String managerEmail,
            Runnable callbackRegistration,
            Runnable callbackHome) {

        Label logo = new Label("SpotFit");
        logo.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");

        HBox header = new HBox(logo);
        header.setPadding(new Insets(24, 48, 24, 48));

        Label title = new Label("Email Verification & Account Activation");
        title.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        String displayEmail = (managerEmail != null && !managerEmail.isBlank())
                ? managerEmail
                : (authResult != null && authResult.getEmail() != null ? authResult.getEmail() : "your email");

        Label description = new Label("A verification link has been sent to " + displayEmail + ".\n\n"
                + "Please click the link in your email to verify your email address, then click 'I Have Verified — Activate Account' below to complete your Manager account setup.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size:14px; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-line-spacing:3px;");

        Label statusLabel = new Label("Check your email for the verification link, then click 'I Have Verified — Activate Account' below.");
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");

        Button verifyButton = new Button("I Have Verified — Activate Account →");
        verifyButton.setMaxWidth(Double.MAX_VALUE);
        verifyButton.setPrefHeight(46);
        verifyButton.setStyle("-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-size:14px; "
                + "-fx-font-weight:bold; -fx-background-radius:12px; -fx-cursor:hand;");

        Button resendButton = new Button("Resend Verification Email");
        resendButton.setMaxWidth(Double.MAX_VALUE);
        resendButton.setPrefHeight(42);
        resendButton.setStyle("-fx-background-color:transparent; -fx-text-fill:" + PRIMARY + "; -fx-font-size:14px; "
                + "-fx-font-weight:bold; -fx-border-color:" + PRIMARY + "; -fx-border-width:1.5px; -fx-border-radius:12px; -fx-cursor:hand;");

        Button backButton = new Button("Return to Manager Login");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle("-fx-background-color:transparent; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-font-size:13px; "
                + "-fx-underline:true; -fx-cursor:hand;");

        backButton.setOnAction(event -> {
            if (callbackRegistration != null) {
                callbackRegistration.run();
            } else {
                LoginPage loginPage = new LoginPage();
                AppNavigator.setScene(loginPage.getLoginPageScene(callbackHome));
                AppNavigator.setMaximized(true);
            }
        });

        boolean missingContext = (authResult == null || authResult.getRefreshToken() == null
                || authResult.getRefreshToken().isBlank() || invitationCode == null || invitationCode.isBlank());

        if (missingContext) {
            verifyButton.setDisable(true);
            resendButton.setDisable(true);
            statusLabel.setText("Your invitation activation session is missing or expired. Please sign in and continue your invitation again.");
            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#ba1a1a; -fx-font-weight:bold;");
        } else {
            verifyButton.setDisable(false);
            resendButton.setDisable(false);
        }

        verifyButton.setOnAction(event -> {
            if (isProcessing) return;
            isProcessing = true;

            setLoading(true, verifyButton, resendButton, statusLabel);
            statusLabel.setText("Checking Firebase verification status...");
            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");

            authController.checkVerificationAndActivateManager(authResult, invitationCode, managerName, managerPhone)
                    .whenComplete((res, throwable) -> Platform.runLater(() -> {
                        isProcessing = false;

                        if (throwable != null) {
                            setLoading(false, verifyButton, resendButton, statusLabel);
                            statusLabel.setText(sanitizeErrorMessage(throwable.getMessage()));
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#ba1a1a; -fx-font-weight:bold;");
                            return;
                        }

                        if (res != null && res.isFailure()) {
                            setLoading(false, verifyButton, resendButton, statusLabel);
                            if ("EMAIL_NOT_VERIFIED".equalsIgnoreCase(res.getErrorCode())) {
                                statusLabel.setText("Your email is not verified yet. Open the link sent to your email and try again.");
                            } else {
                                statusLabel.setText(sanitizeErrorMessage(res.getMessage()));
                            }
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#ba1a1a; -fx-font-weight:bold;");
                        } else if (res != null && res.isSuccess()) {
                            statusLabel.setText("Email verified. Manager account activated successfully! Redirecting...");
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2e7d32; -fx-font-weight:bold;");
                            verifyButton.setDisable(true);

                            Manager manager = res.getData();
                            Dashboard dashboardPage = new Dashboard();
                            Runnable callbackLogout = () -> {
                                LoginPage loginPage = new LoginPage();
                                AppNavigator.setScene(loginPage.getLoginPageScene(callbackHome));
                                AppNavigator.setMaximized(true);
                            };

                            String nameToDisplay = (manager != null && manager.getName() != null && !manager.getName().isBlank())
                                    ? manager.getName() : managerName;

                            AppNavigator.setScene(dashboardPage.getDashboardPageScene(nameToDisplay, callbackLogout));
                            AppNavigator.setMaximized(true);
                        } else {
                            setLoading(false, verifyButton, resendButton, statusLabel);
                            statusLabel.setText("Activation failed: Unknown response from authentication service.");
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#ba1a1a; -fx-font-weight:bold;");
                        }
                    }));
        });

        resendButton.setOnAction(event -> {
            if (resendButton.isDisabled() || isResendCooldownActive()) return;

            resendButton.setDisable(true);
            statusLabel.setText("Sending verification email...");
            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");

            authController.sendVerificationEmail(authResult)
                    .whenComplete((res, throwable) -> Platform.runLater(() -> {
                        if (throwable != null || (res != null && res.isFailure())) {
                            resendButton.setDisable(false);
                            String msg = (throwable != null) ? throwable.getMessage() : (res != null ? res.getMessage() : "Failed to send verification email.");
                            statusLabel.setText("Failed to send email: " + msg);
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#ba1a1a; -fx-font-weight:bold;");
                        } else {
                            statusLabel.setText("Verification email resent. Please check your inbox.");
                            statusLabel.setStyle("-fx-font-size:13px; -fx-text-fill:#2e7d32; -fx-font-weight:bold;");
                            startResendCooldown(resendButton);
                        }
                    }));
        });

        VBox card = new VBox(18, title, description, statusLabel, verifyButton, resendButton, backButton);
        card.setPadding(new Insets(40));
        card.setMaxWidth(560);
        card.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:24px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 30, 0, 0, 10);");

        VBox cardWrapper = new VBox(card);
        cardWrapper.setAlignment(Pos.CENTER);
        cardWrapper.setPadding(new Insets(20, 48, 48, 48));
        VBox.setVgrow(cardWrapper, Priority.ALWAYS);

        VBox contentBox = new VBox(header, cardWrapper);
        contentBox.setStyle("-fx-background-color:" + BACKGROUND + ";");

        ScrollPane root = new ScrollPane(contentBox);
        root.setFitToWidth(true);
        root.setFitToHeight(true);
        root.setStyle("-fx-background-color:" + BACKGROUND + "; -fx-background: " + BACKGROUND + ";");

        double w = (AppNavigator.getPrimaryStage() != null && AppNavigator.getPrimaryStage().getWidth() > 0)
                ? AppNavigator.getPrimaryStage().getWidth() : 1280;
        double h = (AppNavigator.getPrimaryStage() != null && AppNavigator.getPrimaryStage().getHeight() > 0)
                ? AppNavigator.getPrimaryStage().getHeight() : 800;

        verifyPageScene = new Scene(root, w, h);

        return verifyPageScene;
    }

    private void setLoading(boolean loading, Button activateButton, Button resendButton, Label statusLabel) {
        activateButton.setDisable(loading);
        resendButton.setDisable(loading || isResendCooldownActive());

        if (loading) {
            activateButton.setText("Checking Verification...");
        } else {
            activateButton.setText("I Have Verified — Activate Account →");
        }
    }

    private boolean isResendCooldownActive() {
        return cooldownSecondsLeft > 0;
    }

    private void startResendCooldown(Button resendButton) {
        cooldownSecondsLeft = 60;
        resendButton.setDisable(true);
        resendButton.setText("Resend Email (" + cooldownSecondsLeft + "s)");

        if (cooldownTimeline != null) {
            cooldownTimeline.stop();
        }

        cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cooldownSecondsLeft--;
            if (cooldownSecondsLeft > 0) {
                resendButton.setText("Resend Email (" + cooldownSecondsLeft + "s)");
            } else {
                resendButton.setDisable(false);
                resendButton.setText("Resend Verification Email");
                if (cooldownTimeline != null) {
                    cooldownTimeline.stop();
                }
            }
        }));
        cooldownTimeline.setCycleCount(Timeline.INDEFINITE);
        cooldownTimeline.play();
    }

    private String sanitizeErrorMessage(String msg) {
        if (msg == null || msg.isBlank()) {
            return "Email verification succeeded, but Manager activation could not be completed. Please try again.";
        }
        if (msg.contains("JSONException") || msg.contains("JSONObject") || msg.contains("Exception") || msg.contains("{") || msg.contains("[")) {
            return "Email verification succeeded, but Manager activation could not be completed. Please try again.";
        }
        return msg;
    }

    void backToVerifyPage() {
        AppNavigator.setScene(verifyPageScene);
        AppNavigator.setMaximized(true);
    }
}
