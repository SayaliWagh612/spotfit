package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.util.StyleConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;

public class CustomerLoginPrompt {

    public static void show(String titleStr, Runnable navigateToCustomerSignIn) {
        Platform.runLater(() -> {
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initStyle(StageStyle.UNDECORATED);

            VBox root = new VBox(20);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(30));
            root.setStyle(
                    "-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #EDD9C3; -fx-border-radius: 12; -fx-border-width: 2;");

            // Reusing SpotFit's standard font
            String font = StyleConstants.FONT;

            Label title = new Label(titleStr != null && !titleStr.isBlank() ? titleStr : "Sign in to continue");
            title.setStyle("-fx-font-family: '" + font
                    + "'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #A8571F;");

            Label message = new Label(
                    "Create an account or sign in to book this space, save favorites and manage your bookings.");
            message.setWrapText(true);
            message.setStyle("-fx-font-family: '" + font
                    + "'; -fx-font-size: 14px; -fx-text-fill: #3B2417; -fx-alignment: center; -fx-text-alignment: center;");
            message.setPrefWidth(280);

            Button signInBtn = new Button("Sign In");
            signInBtn.setStyle("-fx-background-color: #A8571F; -fx-text-fill: white; -fx-font-family: '" + font
                    + "'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;");
            signInBtn.setOnAction(e -> {
                modal.close();
                if (navigateToCustomerSignIn != null) {
                    navigateToCustomerSignIn.run();
                }
            });

            signInBtn.setOnMouseEntered(e -> signInBtn
                    .setStyle("-fx-background-color: #8A4718; -fx-text-fill: white; -fx-font-family: '" + font
                            + "'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;"));
            signInBtn.setOnMouseExited(e -> signInBtn
                    .setStyle("-fx-background-color: #A8571F; -fx-text-fill: white; -fx-font-family: '" + font
                            + "'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;"));

            Button cancelBtn = new Button("Continue browsing");
            cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8A7565; -fx-font-family: '" + font
                    + "'; -fx-font-size: 13px; -fx-cursor: hand;");
            cancelBtn.setOnAction(e -> modal.close());

            cancelBtn.setOnMouseEntered(e -> cancelBtn
                    .setStyle("-fx-background-color: transparent; -fx-text-fill: #3B2417; -fx-font-family: '" + font
                            + "'; -fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true;"));
            cancelBtn.setOnMouseExited(e -> cancelBtn
                    .setStyle("-fx-background-color: transparent; -fx-text-fill: #8A7565; -fx-font-family: '" + font
                            + "'; -fx-font-size: 13px; -fx-cursor: hand;"));

            root.getChildren().addAll(title, message, signInBtn, cancelBtn);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            modal.setScene(scene);

            // To make background slightly dimmed, we use root container inside a larger
            // transparent scene
            VBox overlay = new VBox(root);
            overlay.setAlignment(Pos.CENTER);
            overlay.setStyle("-fx-background-color: rgba(251, 237, 224, 0.7);"); // using the page background #FBEDE0
                                                                                 // with alpha
            overlay.setPadding(new Insets(0));
            scene.setRoot(overlay);

            // Set size similar to application window, or center it
            // Since it's a modal, we just rely on JavaFX centering it on the owner or
            // screen

            if (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null) {
                modal.initOwner(com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage());
            }

            modal.showAndWait();
        });
    }
}
