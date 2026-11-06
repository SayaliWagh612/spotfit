package com.eklavya.spotfit.view.auth;

import com.eklavya.spotfit.controller.auth.AuthController;
import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

/**
 * Password reset dialog for SpotFit authentication flow.
 */
public class PasswordResetDialog {

    public static void show(AuthController authController) {
        Dialog<Void> dialog = new Dialog<>();
        if (AppNavigator.getPrimaryStage() != null) {
            dialog.initOwner(AppNavigator.getPrimaryStage());
        }
        dialog.setTitle("Reset Password");

        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("-fx-background-color: " + CARD_WHITE + ";");

        Text title = new Text("Forgot Your Password?");
        title.setStyle(heading(20));

        Text subtitle = new Text("Enter your email address and we'll send you instructions to reset your password.");
        subtitle.setStyle(body(13));
        subtitle.setWrappingWidth(360);

        TextField emailField = textField("you@example.com");

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(360);
        statusLabel.setStyle("-fx-font-size: 13px;");

        Button sendBtn = primaryButton("Send Reset Link");
        Button cancelBtn = outlineButton("Cancel");

        HBox btnRow = new HBox(12, cancelBtn, sendBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        cancelBtn.setOnAction(e -> dialog.setResult(null));

        sendBtn.setOnAction(e -> {
            String mail = emailField.getText().trim();
            statusLabel.setText("");
            sendBtn.setDisable(true);
            sendBtn.setText("Sending...");

            authController.sendPasswordReset(mail)
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        sendBtn.setDisable(false);
                        sendBtn.setText("Send Reset Link");

                        if (throwable != null) {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Something went wrong while contacting Firebase. Please try again.");
                            return;
                        }

                        if (result.isSuccess()) {
                            statusLabel.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-font-size: 13px;");
                            statusLabel.setText("If an account is eligible, Firebase has sent password-reset instructions.");
                            sendBtn.setDisable(true);
                        } else {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        VBox content = new VBox(16, title, subtitle, emailField, statusLabel, btnRow);
        content.setPadding(new Insets(24));
        content.setPrefWidth(400);

        pane.setContent(content);
        dialog.showAndWait();
    }
}
