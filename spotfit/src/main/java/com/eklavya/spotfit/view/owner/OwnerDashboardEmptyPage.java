package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.Main;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class OwnerDashboardEmptyPage {

    private Scene dashboardEmptyScene;

    public Scene getScene() {
        Label welcome = OwnerUi.heading("Welcome to SpotFit, Rahul \uD83D\uDC4B", 24);
        Label sub = OwnerUi.body("Let's get your first space listed and ready for bookings.", 13, OwnerUi.TEXT_MUTED);

        VBox emptyState = OwnerUi.card();
        emptyState.setAlignment(Pos.CENTER);
        Label icon = new Label("\uD83C\uDFE2");
        icon.setStyle("-fx-font-size:44px;");
        var createBtn = OwnerUi.primaryButton("+ Create Space");
        createBtn.setOnAction(event -> SpaceFlowRouter.goToCreateSpace());
        createBtn.setMaxWidth(220);
        emptyState.getChildren().addAll(icon, OwnerUi.heading("No Spaces Added Yet", 18), createBtn);

        Label guideTitle = OwnerUi.heading("Quick Start Guide", 16);
        HBox guideCards = new HBox(14,
                guideCard("Create Space", "Define your space type and amenities."),
                guideCard("Upload Photos", "Showcase your space with high-quality images."),
                guideCard("Set Pricing", "Determine hourly or daily rates."));

        VBox mainCol = new VBox(20, welcome, sub, emptyState, guideTitle, guideCards);
        mainCol.setPadding(new Insets(28, 20, 28, 32));
        HBox.setHgrow(mainCol, Priority.ALWAYS);

        VBox progressCard = OwnerUi.card();
        ProgressBar bar = new ProgressBar(0.6);
        bar.setPrefWidth(260);
        progressCard.getChildren().addAll(OwnerUi.heading("Onboarding Progress", 15),
                OwnerUi.body("3 of 5 \u00B7 60% Complete", 12, OwnerUi.TEXT_MUTED), bar,
                OwnerUi.body("\u2713 Account Created", 12.5, OwnerUi.SUCCESS),
                OwnerUi.body("\u2713 Business Registration", 12.5, OwnerUi.SUCCESS),
                OwnerUi.body("\u2713 Profile Setup", 12.5, OwnerUi.SUCCESS),
                OwnerUi.body("\u25CB Add First Space", 12.5, OwnerUi.TEXT_MUTED),
                OwnerUi.body("\u25CB Upload Documents", 12.5, OwnerUi.TEXT_MUTED),
                OwnerUi.body("\u25CB Go Live", 12.5, OwnerUi.TEXT_MUTED));

        VBox rightCol = new VBox(20, progressCard);
        rightCol.setPadding(new Insets(28, 32, 28, 20));
        rightCol.setPrefWidth(300);
        rightCol.setMinWidth(300);

        HBox body = new HBox(mainCol, rightCol);
        VBox.setVgrow(body, Priority.ALWAYS);

        ScrollPane scroller = new ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + OwnerUi.BACKGROUND + ";");

        VBox center = new VBox(scroller);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        center.setStyle("-fx-background-color:" + OwnerUi.BACKGROUND + ";");
        HBox.setHgrow(center, Priority.ALWAYS);

        HBox root = new HBox(OwnerUi.sidebar("dashboard"), center);
        dashboardEmptyScene = OwnerUi.sizedScene(root);
        return dashboardEmptyScene;
    }

    private VBox guideCard(String title, String desc) {
        VBox card = OwnerUi.card();
        card.getChildren().addAll(OwnerUi.heading(title, 14), OwnerUi.body(desc, 11.5, OwnerUi.TEXT_MUTED));
        card.setPrefWidth(200);
        return card;
    }
}

