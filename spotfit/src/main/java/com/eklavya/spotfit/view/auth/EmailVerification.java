package com.eklavya.spotfit.view.auth;

import com.eklavya.spotfit.controller.auth.AuthController;
import com.eklavya.spotfit.controller.user.UserProfileController;
import com.eklavya.spotfit.session.AppSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.util.ResponsiveUtil.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

/**
 * Real Firebase email link verification screen for SpotFit.
 */
public class EmailVerification {

    public static Scene getVerificationScene(String email, Runnable onVerified, Runnable onBackToRegister) {

        HBox root = new HBox();
        root.setStyle("-fx-background-color: " + BG_PANEL_GRADIENT_TOP + ";");
        root.setAlignment(Pos.CENTER);

        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox verifyCard = buildVerifyCard(email, onVerified, onBackToRegister);

        VBox rightWrapper = new VBox(verifyCard);
        rightWrapper.setAlignment(Pos.CENTER);
        rightWrapper.setPadding(new Insets(40));
        rightWrapper.setStyle("-fx-background-color: " + BG_PAGE + ";");

        root.getChildren().addAll(leftPanel, rightWrapper);

        ScrollPane scrollRoot = new ScrollPane(root);
        scrollRoot.setFitToWidth(true);
        scrollRoot.setFitToHeight(true);
        scrollRoot.setStyle("-fx-background: " + BG_PANEL_GRADIENT_TOP + "; -fx-background-color: " + BG_PANEL_GRADIENT_TOP + ";");

        Rectangle2D screen = screenBounds();
        Scene scene = new Scene(scrollRoot, screen.getWidth(), screen.getHeight());

        bindWidthPercent(rightWrapper, scene, 0.34, 440, 580);
        verifyCard.prefWidthProperty().bind(rightWrapper.widthProperty().subtract(80));

        return scene;
    }

    private static VBox buildLeftPanel() {
        Label badge = pillBadge("🔒  SECURE ACCOUNT VERIFICATION");

        Text heading = new Text("Almost There!\nLet's verify your email.");
        heading.setStyle(heading(24));

        Text body = new Text("We sent a verification link to your email address. "
                + "Open the email, click the link to verify your account, then return to SpotFit to continue.");
        body.setStyle(body(15));
        body.setWrappingWidth(560);

        Region illustration = new Region();
        illustration.setPrefSize(600, 380);
        illustration.setMaxWidth(600);
        illustration.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;"
        );
        Label illustrationLabel = new Label("✉ Click the link in your email to verify!");
        illustrationLabel.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane illustrationStack = new StackPane(illustration, illustrationLabel);

        HBox trust = new HBox(50, trustItem("🛡", "Secure Protection", "Industry-standard encryption for your data."),
                trustItem("✓", "Verified Access", "Ensuring a trusted community platform."));

        VBox content = new VBox(24, badge, heading, body, illustrationStack, trust);
        content.setMaxWidth(660);
        content.setPadding(new Insets(50, 60, 50, 60));

        VBox wrapper = new VBox(content);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        return wrapper;
    }

    private static HBox trustItem(String icon, String title, String desc) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-padding: 10 12 10 12;" +
                "-fx-font-size: 14px;"
        );
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label descLabel = new Label(desc);
        descLabel.setStyle(body(12));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);
        VBox textCol = new VBox(2, titleLabel, descLabel);
        HBox row = new HBox(12, iconLabel, textCol);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    private static VBox buildVerifyCard(String emailParam, Runnable onVerified, Runnable onBackToRegister) {
        AuthController authController = new AuthController();

        VBox card = card(460);
        card.setAlignment(Pos.CENTER);

        Label envelope = new Label("✉");
        envelope.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-padding: 18 22 18 22;" +
                "-fx-font-size: 22px;"
        );

        Text title = new Text("Verify Email Address");
        title.setStyle(heading(22));

        Text subtitle = new Text("We sent a verification link to:");
        subtitle.setStyle(body(13));

        String displayEmail = (emailParam != null && !emailParam.isBlank()) ? emailParam : AppSession.getEmail();
        if (displayEmail == null || displayEmail.isBlank()) {
            displayEmail = "your registered email";
        }

        Label emailLabel = new Label(displayEmail);
        emailLabel.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        Text instructions = new Text("Please open your inbox, click the verification link in the email from Firebase, and then click below.");
        instructions.setStyle(body(13));
        instructions.setWrappingWidth(380);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(380);
        statusLabel.setStyle("-fx-font-size: 13px;");

        Button verifyBtn = primaryButton("I’ve Verified My Email  →");
        verifyBtn.setOnAction(e -> {
            statusLabel.setText("");
            verifyBtn.setDisable(true);
            verifyBtn.setText("Checking Verification...");

            authController.checkCurrentUserEmailVerification()
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        verifyBtn.setDisable(false);
                        verifyBtn.setText("I’ve Verified My Email  →");

                        if (throwable != null) {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Something went wrong while contacting Firebase. Please try again.");
                            return;
                        }

                        if (result.isSuccess() && result.isEmailVerified()) {
                            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 13px;");
                            statusLabel.setText("Email verified successfully!");

                            UserProfileController profileController = new UserProfileController();
                            profileController.markCurrentUserEmailVerified()
                                    .whenComplete((pResult, pThrowable) -> Platform.runLater(onVerified::run));
                        } else if (result.isSuccess() && !result.isEmailVerified()) {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Your email is not verified yet. Click the link in your email and try again.");
                        } else {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        Button resendBtn = outlineButton("Resend Verification Email");
        resendBtn.setOnAction(e -> {
            statusLabel.setText("");
            resendBtn.setDisable(true);

            authController.sendVerificationEmail()
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        if (throwable != null) {
                            resendBtn.setDisable(false);
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Something went wrong while requesting verification email.");
                            return;
                        }

                        if (result.isSuccess()) {
                            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-size: 13px;");
                            statusLabel.setText("Verification email resent. Please check your inbox.");

                            int[] secondsLeft = {60};
                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                                secondsLeft[0]--;
                                if (secondsLeft[0] <= 0) {
                                    resendBtn.setDisable(false);
                                    resendBtn.setText("Resend Verification Email");
                                } else {
                                    resendBtn.setText("Resend in " + secondsLeft[0] + "s");
                                }
                            }));
                            timeline.setCycleCount(60);
                            timeline.play();
                        } else {
                            resendBtn.setDisable(false);
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        Label backToReg = new Label("Back to Login");
        backToReg.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand;");
        backToReg.setOnMouseClicked(e -> {
            authController.logout();
            onBackToRegister.run();
        });

        HBox linkRow = new HBox(backToReg);
        linkRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(envelope, title, subtitle, emailLabel, instructions, statusLabel, verifyBtn, resendBtn, linkRow);
        return card;
    }
}