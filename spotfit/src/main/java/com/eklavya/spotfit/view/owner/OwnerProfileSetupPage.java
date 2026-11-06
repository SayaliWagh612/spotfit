package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerAuthController;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.session.AppSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OwnerProfileSetupPage {

    private Scene profileScene;

    public Scene getScene(Runnable callback) {
        Label logo = new Label("SpotFit");
        logo.setStyle("-fx-font-size:22px; -fx-font-weight:800; -fx-text-fill:" + OwnerUi.PRIMARY_CONTAINER + ";");

        Button backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setMaxWidth(120);
        backBtn.setOnAction(event -> {
            if (callback != null) {
                callback.run();
            } else {
                OwnerFlowRouter.goToLogin();
            }
        });

        Label title = OwnerUi.heading("Complete Your Owner Profile", 28);
        title.setAlignment(Pos.CENTER);

        String PROFILE_PHOTO_URL = "";
        Node photo = OwnerUi.imageOrPlaceholder(PROFILE_PHOTO_URL, 96, 96, 48);
        Label photoLabel = OwnerUi.body("Owner Profile", 11, OwnerUi.TEXT_MUTED);
        VBox photoBox = new VBox(6, photo, photoLabel);
        photoBox.setAlignment(Pos.CENTER);

        TextField fullName = OwnerUi.textField("");
        fullName.setPromptText("Full Name");
        fullName.setText(AppSession.getName() != null ? AppSession.getName() : "");

        TextField mobile = OwnerUi.textField("");
        mobile.setPromptText("10-digit Mobile Number");

        TextField city = OwnerUi.textField("");
        city.setPromptText("City / Location (e.g. Pune)");

        final String[] existingImageUrl = {""};

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(6);
        grid.addColumn(0, OwnerUi.fieldLabel("Full Name"), fullName, OwnerUi.fieldLabel("City / Location"), city);
        grid.addColumn(1, OwnerUi.fieldLabel("Mobile Number"), mobile);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }
        GridPane.setMargin(fullName, new Insets(0, 0, 12, 0));
        GridPane.setMargin(mobile, new Insets(0, 0, 12, 0));

        Label statusLabel = OwnerUi.body("", 12.5, OwnerUi.TEXT_DARK);
        statusLabel.setWrapText(true);

        OwnerAuthController ownerAuthController = new OwnerAuthController();

        // Async pre-fill known profile values
        ownerAuthController.loadCurrentOwner()
                .whenComplete((res, err) -> Platform.runLater(() -> {
                    if (res != null && res.isSuccess() && res.getData() != null) {
                        Owner o = res.getData();
                        if (o.getName() != null && !o.getName().isBlank()) {
                            fullName.setText(o.getName());
                        }
                        if (o.getPhone() != null && !o.getPhone().isBlank()) {
                            mobile.setText(o.getPhone());
                        }
                        if (o.getCity() != null && !o.getCity().isBlank()) {
                            city.setText(o.getCity());
                        }
                        if (o.getProfileImageUrl() != null) {
                            existingImageUrl[0] = o.getProfileImageUrl();
                        }
                    }
                }));

        Button continueBtn = OwnerUi.primaryButton("Save Profile & Continue  \u2192");
        continueBtn.setOnAction(event -> {
            String nameStr = fullName.getText().trim();
            String phoneStr = mobile.getText().trim();
            String cityStr = city.getText().trim();
            String imageStr = existingImageUrl[0];

            continueBtn.setDisable(true);
            continueBtn.setText("Saving Profile...");
            statusLabel.setText("");

            ownerAuthController.updateOwnerProfile(nameStr, phoneStr, cityStr, imageStr)
                    .thenCompose(updateRes -> {
                        if (updateRes.isFailure()) {
                            return PlatformUtilCompleted.completedFuture(updateRes);
                        }
                        return ownerAuthController.completeOwnerProfile();
                    })
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        continueBtn.setDisable(false);
                        continueBtn.setText("Save Profile & Continue  \u2192");

                        if (throwable != null) {
                            statusLabel.setText("Failed to save profile: " + throwable.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isSuccess()) {
                            OwnerFlowRouter.goToDashboard();
                        } else {
                            statusLabel.setText(result != null ? result.getMessage() : "Could not complete profile.");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        }
                    }));
        });

        VBox form = new VBox(14, photoBox, grid, statusLabel, continueBtn);

        VBox card = OwnerUi.card();
        card.getChildren().add(form);
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(card, new Insets(0, 64, 0, 64));

        VBox content = new VBox(20, logo, backBtn, title, card);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + OwnerUi.BACKGROUND + ";");

        VBox root = new VBox(scroller);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        root.setStyle("-fx-background-color:" + OwnerUi.BACKGROUND + ";");

        profileScene = OwnerUi.sizedScene(root);
        return profileScene;
    }

    private static class PlatformUtilCompleted {
        private static <T> java.util.concurrent.CompletableFuture<com.eklavya.spotfit.model.OperationResult<T>> completedFuture(
                com.eklavya.spotfit.model.OperationResult<T> res) {
            return java.util.concurrent.CompletableFuture.completedFuture(res);
        }
    }
}
