package com.eklavya.spotfit.view.common;

import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.view.owner.SignInPage;
import com.eklavya.spotfit.view.spaceManager.LoginPage;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SelectBusinessRole {

    public Scene getScene(Runnable back) {

        /* =========================
           TITLE
           ========================= */

        Label title = new Label("SpotFit Business Portal");

        title.setStyle(
                "-fx-font-size:42px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#351707;" +
                "-fx-effect:dropshadow(gaussian,rgba(255,255,255,0.7),8,0.2,0,2);"
        );


        /* =========================
           SUBTITLE
           ========================= */

        Label subtitle = new Label(
                "Please select your operational role to proceed to sign in."
        );

        subtitle.setStyle(
                "-fx-font-size:18px;" +
                "-fx-text-fill:#5a3b27;" +
                "-fx-font-weight:normal;"
        );


        /* =========================
           BACK BUTTON
           ========================= */

        Button backButton = roleButton("←  Back", false);

        backButton.setPrefWidth(130);
        backButton.setPrefHeight(48);

        backButton.setStyle(
                "-fx-background-color:rgba(255,255,255,0.92);" +
                "-fx-text-fill:#54270d;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:#a95c19;" +
                "-fx-border-width:1.5px;" +
                "-fx-border-radius:25px;" +
                "-fx-background-radius:25px;" +
                "-fx-padding:10 22;" +
                "-fx-cursor:hand;" +
                "-fx-effect:dropshadow(gaussian,rgba(60,30,10,0.18),10,0.2,0,3);"
        );

        backButton.setOnMouseEntered(e ->
                backButton.setStyle(
                        "-fx-background-color:#884600;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#884600;" +
                        "-fx-border-width:1.5px;" +
                        "-fx-border-radius:25px;" +
                        "-fx-background-radius:25px;" +
                        "-fx-padding:10 22;" +
                        "-fx-cursor:hand;" +
                        "-fx-effect:dropshadow(gaussian,rgba(60,30,10,0.25),12,0.2,0,4);"
                )
        );

        backButton.setOnMouseExited(e ->
                backButton.setStyle(
                        "-fx-background-color:rgba(255,255,255,0.92);" +
                        "-fx-text-fill:#54270d;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:#a95c19;" +
                        "-fx-border-width:1.5px;" +
                        "-fx-border-radius:25px;" +
                        "-fx-background-radius:25px;" +
                        "-fx-padding:10 22;" +
                        "-fx-cursor:hand;" +
                        "-fx-effect:dropshadow(gaussian,rgba(60,30,10,0.18),10,0.2,0,3);"
                )
        );

        // SAME ACTION
        backButton.setOnAction(e -> back.run());


        /* =========================
           OWNER BUTTON
           ========================= */

        Button owner = roleButton(
                "Continue as Space Owner",
                true
        );

        // SAME ACTION
        owner.setOnAction(e ->
                com.eklavya.spotfit.view.owner.OwnerFlowRouter.goToLogin()
        );


        /* =========================
           MANAGER BUTTON
           ========================= */

        Button manager = roleButton(
                "Continue as Space Manager",
                false
        );

        // SAME ACTION
        manager.setOnAction(e -> {

            LoginPage login = new LoginPage();

            AppNavigator.setScene(
                    login.getLoginPageScene(
                            () -> back.run()
                    )
            );
        });


        /* =========================
           OWNER CARD CONTENT
           ========================= */

        Label ownerIcon = new Label("♙");

        ownerIcon.setPrefSize(78, 78);
        ownerIcon.setAlignment(Pos.CENTER);

        ownerIcon.setStyle(
                "-fx-background-color:#fff8ef;" +
                "-fx-background-radius:50%;" +
                "-fx-text-fill:#884600;" +
                "-fx-font-size:38px;" +
                "-fx-font-weight:bold;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.12),8,0.2,0,3);"
        );

        Label ownerSmall = new Label("Continue as");

        ownerSmall.setStyle(
                "-fx-text-fill:#fff5eb;" +
                "-fx-font-size:17px;"
        );

        Label ownerTitle = new Label("Space Owner");

        ownerTitle.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label ownerLine = new Label("—");

        ownerLine.setStyle(
                "-fx-text-fill:#ffe1c2;" +
                "-fx-font-size:25px;" +
                "-fx-font-weight:bold;"
        );

        Label ownerDescription = new Label(
                "Manage your spaces, bookings,\nrevenue and business operations."
        );

        ownerDescription.setAlignment(Pos.CENTER);

        ownerDescription.setTextAlignment(
                javafx.scene.text.TextAlignment.CENTER
        );

        ownerDescription.setStyle(
                "-fx-text-fill:#fff2e5;" +
                "-fx-font-size:15px;" +
                "-fx-line-spacing:6px;"
        );


        /* =========================
           MANAGER CARD CONTENT
           ========================= */

        Label managerIcon = new Label("♙");

        managerIcon.setPrefSize(78, 78);
        managerIcon.setAlignment(Pos.CENTER);

        managerIcon.setStyle(
                "-fx-background-color:#fff0df;" +
                "-fx-background-radius:50%;" +
                "-fx-text-fill:#884600;" +
                "-fx-font-size:38px;" +
                "-fx-font-weight:bold;" +
                "-fx-effect:dropshadow(gaussian,rgba(80,40,10,0.10),8,0.2,0,3);"
        );

        Label managerSmall = new Label("Continue as");

        managerSmall.setStyle(
                "-fx-text-fill:#60432f;" +
                "-fx-font-size:17px;"
        );

        Label managerTitle = new Label("Space Manager");

        managerTitle.setStyle(
                "-fx-text-fill:#3b1d0c;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        Label managerLine = new Label("—");

        managerLine.setStyle(
                "-fx-text-fill:#c15e0b;" +
                "-fx-font-size:25px;" +
                "-fx-font-weight:bold;"
        );

        Label managerDescription = new Label(
                "Handle daily operations,\nbookings and space management."
        );

        managerDescription.setAlignment(Pos.CENTER);

        managerDescription.setTextAlignment(
                javafx.scene.text.TextAlignment.CENTER
        );

        managerDescription.setStyle(
                "-fx-text-fill:#6a5140;" +
                "-fx-font-size:15px;" +
                "-fx-line-spacing:6px;"
        );


        /* =========================
           ARROW BUTTONS
           ========================= */

        owner.setText("→");

        owner.setPrefWidth(68);
        owner.setPrefHeight(68);

        owner.setStyle(
                "-fx-background-color:#fffaf4;" +
                "-fx-text-fill:#884600;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:50%;" +
                "-fx-border-radius:50%;" +
                "-fx-cursor:hand;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.15),8,0.2,0,3);"
        );


        manager.setText("→");

        manager.setPrefWidth(68);
        manager.setPrefHeight(68);

        manager.setStyle(
                "-fx-background-color:#fff0df;" +
                "-fx-text-fill:#a65309;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:50%;" +
                "-fx-border-radius:50%;" +
                "-fx-cursor:hand;" +
                "-fx-effect:dropshadow(gaussian,rgba(100,50,10,0.10),8,0.2,0,3);"
        );


        /* =========================
           OWNER CARD
           ========================= */

        VBox ownerContent = new VBox(
                10,
                ownerIcon,
                ownerSmall,
                ownerTitle,
                ownerLine,
                ownerDescription,
                owner
        );

        ownerContent.setAlignment(Pos.CENTER);

        ownerContent.setPadding(
                new Insets(32, 30, 28, 30)
        );

        VBox ownerCard = new VBox(ownerContent);

        ownerCard.setAlignment(Pos.CENTER);

        ownerCard.setPrefWidth(390);
        ownerCard.setPrefHeight(410);

        ownerCard.setStyle(
                "-fx-background-color:linear-gradient(to bottom right,#8d4700,#6f3500);" +
                "-fx-background-radius:28px;" +
                "-fx-border-radius:28px;" +
                "-fx-border-color:rgba(255,255,255,0.20);" +
                "-fx-border-width:1px;" +
                "-fx-effect:dropshadow(gaussian,rgba(70,30,5,0.35),30,0.25,0,12);"
        );


        /* =========================
           MANAGER CARD
           ========================= */

        VBox managerContent = new VBox(
                10,
                managerIcon,
                managerSmall,
                managerTitle,
                managerLine,
                managerDescription,
                manager
        );

        managerContent.setAlignment(Pos.CENTER);

        managerContent.setPadding(
                new Insets(32, 30, 28, 30)
        );

        VBox managerCard = new VBox(managerContent);

        managerCard.setAlignment(Pos.CENTER);

        managerCard.setPrefWidth(390);
        managerCard.setPrefHeight(410);

        managerCard.setStyle(
                "-fx-background-color:rgba(255,253,249,0.94);" +
                "-fx-background-radius:28px;" +
                "-fx-border-color:#e5a96d;" +
                "-fx-border-width:1.5px;" +
                "-fx-border-radius:28px;" +
                "-fx-effect:dropshadow(gaussian,rgba(70,35,10,0.20),28,0.20,0,10);"
        );


        /* =========================
           CARD HOVER
           ========================= */

        ownerCard.setOnMouseEntered(e ->
                ownerCard.setStyle(
                        "-fx-background-color:linear-gradient(to bottom right,#a95700,#7a3900);" +
                        "-fx-background-radius:28px;" +
                        "-fx-border-radius:28px;" +
                        "-fx-border-color:rgba(255,255,255,0.30);" +
                        "-fx-border-width:1px;" +
                        "-fx-effect:dropshadow(gaussian,rgba(70,30,5,0.45),35,0.25,0,14);"
                )
        );

        ownerCard.setOnMouseExited(e ->
                ownerCard.setStyle(
                        "-fx-background-color:linear-gradient(to bottom right,#8d4700,#6f3500);" +
                        "-fx-background-radius:28px;" +
                        "-fx-border-radius:28px;" +
                        "-fx-border-color:rgba(255,255,255,0.20);" +
                        "-fx-border-width:1px;" +
                        "-fx-effect:dropshadow(gaussian,rgba(70,30,5,0.35),30,0.25,0,12);"
                )
        );

        managerCard.setOnMouseEntered(e ->
                managerCard.setStyle(
                        "-fx-background-color:rgba(255,255,255,0.98);" +
                        "-fx-background-radius:28px;" +
                        "-fx-border-color:#b85b0b;" +
                        "-fx-border-width:1.8px;" +
                        "-fx-border-radius:28px;" +
                        "-fx-effect:dropshadow(gaussian,rgba(90,40,10,0.28),32,0.22,0,12);"
                )
        );

        managerCard.setOnMouseExited(e ->
                managerCard.setStyle(
                        "-fx-background-color:rgba(255,253,249,0.94);" +
                        "-fx-background-radius:28px;" +
                        "-fx-border-color:#e5a96d;" +
                        "-fx-border-width:1.5px;" +
                        "-fx-border-radius:28px;" +
                        "-fx-effect:dropshadow(gaussian,rgba(70,35,10,0.20),28,0.20,0,10);"
                )
        );


        /* =========================
           CHOICES
           ========================= */

        HBox choices = new HBox(
                38,
                ownerCard,
                managerCard
        );

        choices.setAlignment(Pos.CENTER);


        /* =========================
           MAIN CONTENT
           ========================= */

        VBox content = new VBox(
                24,
                title,
                subtitle,
                choices
        );

        content.setAlignment(Pos.CENTER);

        content.setPadding(
                new Insets(35, 30, 80, 30)
        );


        /* =========================
           ROOT
           ========================= */

        StackPane root = new StackPane();

        root.setStyle(
                "-fx-background-color:#fff8f2;"
        );


        /* =========================
           BACKGROUND IMAGE
           ========================= */

        try {

            Image image = new Image(
                    getClass().getResourceAsStream(
                            "/images/image.png"
                    )
            );

            ImageView imageView = new ImageView(image);

            imageView.setPreserveRatio(false);

            imageView.fitWidthProperty().bind(
                    root.widthProperty()
            );

            imageView.fitHeightProperty().bind(
                    root.heightProperty()
            );

            imageView.setMouseTransparent(true);

            StackPane.setAlignment(
                    imageView,
                    Pos.CENTER
            );

            root.getChildren().add(
                    imageView
            );

        } catch (Exception e) {

            System.out.println(
                    "Background image not found."
            );
        }


        /* =========================
           DARK / WARM OVERLAY
           ========================= */

        StackPane overlay = new StackPane();

        overlay.setStyle(
                "-fx-background-color:rgba(45,20,5,0.28);"
        );

        overlay.setMouseTransparent(true);

        root.getChildren().add(overlay);


        /* =========================
           MAIN CONTENT
           ========================= */

        StackPane.setAlignment(
                content,
                Pos.CENTER
        );

        root.getChildren().add(content);


        /* =====================================================
           OWNER + MANAGER CARD ENTRANCE ANIMATION
           ===================================================== */

        // Owner card starts slightly below and invisible
        ownerCard.setOpacity(0);
        ownerCard.setTranslateY(45);

        FadeTransition ownerFade =
                new FadeTransition(
                        Duration.millis(700),
                        ownerCard
                );

        ownerFade.setFromValue(0);
        ownerFade.setToValue(1);

        TranslateTransition ownerSlide =
                new TranslateTransition(
                        Duration.millis(700),
                        ownerCard
                );

        ownerSlide.setFromY(45);
        ownerSlide.setToY(0);

        ParallelTransition ownerEntrance =
                new ParallelTransition(
                        ownerFade,
                        ownerSlide
                );


        // Manager card starts slightly below and invisible
        managerCard.setOpacity(0);
        managerCard.setTranslateY(45);

        FadeTransition managerFade =
                new FadeTransition(
                        Duration.millis(700),
                        managerCard
                );

        managerFade.setFromValue(0);
        managerFade.setToValue(1);

        TranslateTransition managerSlide =
                new TranslateTransition(
                        Duration.millis(700),
                        managerCard
                );

        managerSlide.setFromY(45);
        managerSlide.setToY(0);

        ParallelTransition managerEntrance =
                new ParallelTransition(
                        managerFade,
                        managerSlide
                );


        // Owner appears first
        ownerEntrance.play();


        // Manager appears slightly after Owner
        managerEntrance.setDelay(
                Duration.millis(180)
        );

        managerEntrance.play();


        /* =========================
           WINDOW SIZE
           ========================= */

        double w = (
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getWidth() > 0
        )
                ? AppNavigator.getPrimaryStage().getWidth()
                : 1280;

        double h = (
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getHeight() > 0
        )
                ? AppNavigator.getPrimaryStage().getHeight()
                : 800;


        Scene scene = new Scene(root, w, h);


        /* =========================
           MAXIMIZE WINDOW
           ========================= */

        if (AppNavigator.getPrimaryStage() != null) {
            AppNavigator.getPrimaryStage().setMaximized(true);
        }

        return scene;
    }


    /* =========================================================
       SAME METHOD
       ========================================================= */

    private Button roleButton(String text, boolean fill) {

        Button b = new Button(text);

        b.setStyle(
                "-fx-background-color:" +
                        (fill ? "#884600" : "#ffffff") + ";" +
                "-fx-text-fill:" +
                        (fill ? "#ffffff" : "#2e1503") + ";" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:14 24;" +
                "-fx-border-color:#884600;" +
                "-fx-border-radius:12px;" +
                "-fx-background-radius:12px;" +
                "-fx-cursor:hand;"
        );

        return b;
    }
} 