package com.eklavya.spotfit.view.common;

import com.eklavya.spotfit.navigation.AppNavigator;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SelectRole {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";
    private static final String BORDER = "#D9C8B8";

    public Scene getChooseLoginPageScene(Runnable back) {

        /*
         * IMPORTANT:
         * Keep the current window size before going back.
         * AppNavigator can create a new Scene with its own preferred size,
         * so we restore the previous size after navigation.
         */
        final double previousWidth =
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getWidth() > 0
                        ? AppNavigator.getPrimaryStage().getWidth()
                        : 1280;

        final double previousHeight =
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getHeight() > 0
                        ? AppNavigator.getPrimaryStage().getHeight()
                        : 800;

        // =========================
        // BACK BUTTON
        // =========================
        Button backButton = new Button("←  Back");
        backButton.setPrefHeight(42);
        backButton.setMinWidth(108);
        backButton.setStyle(backButtonStyle(false));

        backButton.setOnMouseEntered(e ->
                backButton.setStyle(backButtonStyle(true)));

        backButton.setOnMouseExited(e ->
                backButton.setStyle(backButtonStyle(false)));

        // Same navigation functionality + size restoration
        backButton.setOnAction(event -> {
            back.run();

            Platform.runLater(() -> {
                if (AppNavigator.getPrimaryStage() != null) {
                    AppNavigator.getPrimaryStage().setWidth(previousWidth);
                    AppNavigator.getPrimaryStage().setHeight(previousHeight);
                }
            });
        });

        // =========================
        // TOP HEADER
        // =========================
        Label logoDot = new Label("●");
        logoDot.setStyle(
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-font-size:20px;" +
                "-fx-font-weight:bold;"
        );

        Label logo = new Label("SpotFit");
        logo.setStyle(
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-font-size:24px;" +
                "-fx-font-weight:800;"
        );

        HBox logoBox = new HBox(5, logoDot, logo);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label headerHint = new Label("FIND YOUR SPACE • MAKE IT YOURS");
        headerHint.setStyle(
                "-fx-text-fill:rgba(83,68,57,0.72);" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;" +
                "-fx-letter-spacing:1.5px;"
        );

        HBox topBar = new HBox(20, logoBox, headerHint, backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 34, 10, 34));
        HBox.setHgrow(headerHint, Priority.ALWAYS);

        // =========================
        // MAIN HEADING
        // =========================
        Label eyebrow = new Label("CHOOSE YOUR EXPERIENCE");
        eyebrow.setStyle(
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-letter-spacing:1.8px;"
        );

        Label title = new Label("Welcome to SpotFit");
        title.setStyle(
                "-fx-font-size:38px;" +
                "-fx-font-weight:900;" +
                "-fx-text-fill:" + TEXT + ";"
        );

        Label question = new Label("How would you like to continue?");
        question.setStyle(
                "-fx-font-size:21px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";"
        );

        Label subtitle = new Label(
                "Choose the experience that matches what you want to do today."
        );
        subtitle.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );

        VBox heading = new VBox(6, eyebrow, title, question, subtitle);
        heading.setAlignment(Pos.CENTER);
        heading.setPadding(new Insets(6, 10, 2, 10));

        // =========================
        // CARDS
        // =========================
        VBox customer = createCard(
                "/assets/images/customer.jpeg",
                "I'm looking for a space",
                "Discover workshops, creative studios, cafés, sports venues, wellness centers, and productive workspaces.",
                new String[]{
                        "Explore spaces",
                        "Book activities",
                        "Save favorites",
                        "Manage bookings"
                },
                "Continue as Customer  →",
                true,
                back
        );

        VBox business = createCard(
                "/assets/images/business.jpeg",
                "I manage a space",
                "Manage spaces, bookings, availability, customers, team operations, and business performance.",
                new String[]{
                        "Manage listings",
                        "Track bookings",
                        "Handle check-ins",
                        "Monitor revenue"
                },
                "Continue as Business  →",
                false,
                back
        );

        HBox cards = new HBox(24, customer, business);
        cards.setAlignment(Pos.CENTER);

        // // =========================
        // // GUEST
        // // =========================
        // Label guest = new Label("Continue without login");
        // guest.setStyle(
        //         "-fx-font-size:13px;" +
        //         "-fx-text-fill:" + SECONDARY_TEXT + ";" +
        //         "-fx-cursor:hand;"
        // );

        // guest.setOnMouseEntered(e ->
        //         guest.setStyle(
        //                 "-fx-font-size:13px;" +
        //                 "-fx-text-fill:" + PRIMARY + ";" +
        //                 "-fx-underline:true;" +
        //                 "-fx-cursor:hand;"
        //         )
        // );

        // guest.setOnMouseExited(e ->
        //         guest.setStyle(
        //                 "-fx-font-size:13px;" +
        //                 "-fx-text-fill:" + SECONDARY_TEXT + ";" +
        //                 "-fx-cursor:hand;"
        //         )
        // );

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(14, heading, cards);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(4, 35, 18, 35));

        // =========================
        // ROOT
        // =========================
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color:" + BACKGROUND + ";");

        // =========================
        // BACKGROUND IMAGE
        // =========================
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/images/image1.png")
            );

            ImageView bgView = new ImageView(bgImage);
            bgView.setPreserveRatio(false);
            bgView.fitWidthProperty().bind(root.widthProperty());
            bgView.fitHeightProperty().bind(root.heightProperty());
            bgView.setMouseTransparent(true);

            GaussianBlur blur = new GaussianBlur(18);
            bgView.setEffect(blur);

            StackPane.setAlignment(bgView, Pos.CENTER);
            root.getChildren().add(bgView);

        } catch (Exception e) {
            System.out.println("Background image not found.");
        }

        // =========================
        // LIGHT OVERLAY
        // =========================
        StackPane overlay = new StackPane();
        overlay.setStyle(
                "-fx-background-color:rgba(255,248,245,0.38);"
        );
        overlay.setMouseTransparent(true);
        root.getChildren().add(overlay);

        // =========================
        // CENTER GLASS PANEL
        // =========================
        StackPane panel = new StackPane();
        panel.setMaxWidth(1120);
        panel.setMaxHeight(720);
        panel.setStyle(
                "-fx-background-color:rgba(255,255,255,0.20);" +
                "-fx-background-radius:28px;" +
                "-fx-border-color:rgba(255,255,255,0.52);" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:28px;" +
                "-fx-effect:dropshadow(gaussian,rgba(75,40,15,0.12),28,0.18,0,8);"
        );
        panel.setMouseTransparent(true);

        StackPane.setAlignment(panel, Pos.CENTER);
        StackPane.setMargin(panel, new Insets(62, 28, 28, 28));

        // Do not block controls behind it.
        panel.setPickOnBounds(false);
        root.getChildren().add(panel);

        // =========================
        // DECORATIVE GLOW
        // =========================
        StackPane glow = new StackPane();
        glow.setPrefSize(260, 260);
        glow.setMaxSize(260, 260);
        glow.setStyle(
                "-fx-background-color:rgba(220,165,105,0.16);" +
                "-fx-background-radius:50%;"
        );
        glow.setMouseTransparent(true);

        StackPane.setAlignment(glow, Pos.TOP_LEFT);
        StackPane.setMargin(glow, new Insets(100, 0, 0, -70));
        root.getChildren().add(glow);

        // =========================
        // MAIN LAYOUT
        // =========================
        VBox mainLayout = new VBox(2, topBar, content);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setFillWidth(true);

        StackPane.setAlignment(mainLayout, Pos.TOP_CENTER);
        root.getChildren().add(mainLayout);

        // =========================
        // WINDOW SIZE
        // =========================
        double w =
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getWidth() > 0
                        ? AppNavigator.getPrimaryStage().getWidth()
                        : 1280;

        double h =
                AppNavigator.getPrimaryStage() != null &&
                AppNavigator.getPrimaryStage().getHeight() > 0
                        ? AppNavigator.getPrimaryStage().getHeight()
                        : 800;

        Scene scene = new Scene(root, w, h);

        // =========================
        // CARD ANIMATIONS
        // =========================
        customer.setOpacity(0);
        customer.setTranslateY(22);
        customer.setScaleX(0.985);
        customer.setScaleY(0.985);

        FadeTransition customerFade =
                new FadeTransition(Duration.millis(500), customer);
        customerFade.setFromValue(0);
        customerFade.setToValue(1);

        TranslateTransition customerSlide =
                new TranslateTransition(Duration.millis(500), customer);
        customerSlide.setFromY(22);
        customerSlide.setToY(0);

        ScaleTransition customerScale =
                new ScaleTransition(Duration.millis(500), customer);
        customerScale.setFromX(0.985);
        customerScale.setFromY(0.985);
        customerScale.setToX(1);
        customerScale.setToY(1);

        ParallelTransition customerEntrance =
                new ParallelTransition(
                        customerFade,
                        customerSlide,
                        customerScale
                );

        business.setOpacity(0);
        business.setTranslateY(22);
        business.setScaleX(0.985);
        business.setScaleY(0.985);

        FadeTransition businessFade =
                new FadeTransition(Duration.millis(500), business);
        businessFade.setFromValue(0);
        businessFade.setToValue(1);

        TranslateTransition businessSlide =
                new TranslateTransition(Duration.millis(500), business);
        businessSlide.setFromY(22);
        businessSlide.setToY(0);

        ScaleTransition businessScale =
                new ScaleTransition(Duration.millis(500), business);
        businessScale.setFromX(0.985);
        businessScale.setFromY(0.985);
        businessScale.setToX(1);
        businessScale.setToY(1);

        ParallelTransition businessEntrance =
                new ParallelTransition(
                        businessFade,
                        businessSlide,
                        businessScale
                );

        customerEntrance.play();

        businessEntrance.setDelay(Duration.millis(100));
        businessEntrance.play();

        return scene;
    }

    private String backButtonStyle(boolean hover) {
        return
                "-fx-background-color:" +
                        (hover ? PRIMARY : "rgba(255,255,255,0.94)") + ";" +
                "-fx-text-fill:" +
                        (hover ? "white" : PRIMARY) + ";" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:" + PRIMARY + ";" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:21px;" +
                "-fx-background-radius:21px;" +
                "-fx-padding:8 20;" +
                "-fx-cursor:hand;" +
                "-fx-effect:dropshadow(gaussian,rgba(70,35,10,0.14),10,0.18,0,3);";
    }

    private VBox createCard(
            String path,
            String heading,
            String description,
            String[] items,
            String buttonText,
            boolean fill,
            Runnable back) {

        ImageView image = createCardImage(path, 470, 170);

        Label title = new Label(heading);
        title.setStyle(
                "-fx-font-size:20px;" +
                "-fx-font-weight:800;" +
                "-fx-text-fill:" + TEXT + ";"
        );

        Label desc = new Label(description);
        desc.setWrapText(true);
        desc.setMaxWidth(430);
        desc.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";" +
                "-fx-line-spacing:2px;"
        );

        VBox features = new VBox(8);

        for (String item : items) {
            features.getChildren().add(createFeature(item));
        }

        Button button = new Button(buttonText);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(43);
        button.setStyle(
                "-fx-background-color:" +
                        (fill ? PRIMARY : SURFACE) + ";" +
                "-fx-text-fill:" +
                        (fill ? SURFACE : PRIMARY) + ";" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:" + PRIMARY + ";" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:9px;" +
                "-fx-background-radius:9px;" +
                "-fx-cursor:hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color:" +
                                (fill ? "#a85700" : "#fff7ef") + ";" +
                        "-fx-text-fill:" +
                                (fill ? "#ffffff" : PRIMARY) + ";" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:" + PRIMARY + ";" +
                        "-fx-border-width:1px;" +
                        "-fx-border-radius:9px;" +
                        "-fx-background-radius:9px;" +
                        "-fx-cursor:hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color:" +
                                (fill ? PRIMARY : SURFACE) + ";" +
                        "-fx-text-fill:" +
                                (fill ? SURFACE : PRIMARY) + ";" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-border-color:" + PRIMARY + ";" +
                        "-fx-border-width:1px;" +
                        "-fx-border-radius:9px;" +
                        "-fx-background-radius:9px;" +
                        "-fx-cursor:hand;"
                )
        );

        // SAME FUNCTIONALITY
        button.setOnAction(event -> {
            if (fill) {
                com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin();
            } else {
                SelectBusinessRole roles = new SelectBusinessRole();

                AppNavigator.setScene(
                        roles.getScene(
                                () -> AppNavigator.setScene(
                                        getChooseLoginPageScene(back)
                                )
                        )
                );
            }
        });

        VBox cardContent = new VBox(
                11,
                title,
                desc,
                features,
                button
        );

        cardContent.setPadding(new Insets(17, 18, 18, 18));

        VBox card = new VBox(image, cardContent);
        card.setPrefWidth(470);
        card.setMaxWidth(470);
        card.setStyle(cardStyle(false));

        card.setOnMouseEntered(e ->
                card.setStyle(cardStyle(true)));

        card.setOnMouseExited(e ->
                card.setStyle(cardStyle(false)));

        return card;
    }

    private String cardStyle(boolean hover) {
        return
                "-fx-background-color:#ffffff;" +
                "-fx-background-radius:18px;" +
                "-fx-border-radius:18px;" +
                "-fx-border-color:" +
                        (hover ? "#c98950" : BORDER) + ";" +
                "-fx-border-width:1px;" +
                "-fx-effect:dropshadow(gaussian," +
                        (hover
                                ? "rgba(100,50,10,0.24)"
                                : "rgba(70,35,10,0.15)") +
                        "," +
                        (hover ? "25" : "18") +
                        ",0.18,0," +
                        (hover ? "8" : "5") +
                        ");";
    }

    private HBox createFeature(String text) {

        Label check = new Label("✓");
        check.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-background-color:#fff0df;" +
                "-fx-background-radius:50%;" +
                "-fx-min-width:23px;" +
                "-fx-min-height:23px;" +
                "-fx-max-width:23px;" +
                "-fx-max-height:23px;" +
                "-fx-alignment:center;"
        );

        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );

        HBox row = new HBox(9, check, label);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

    private ImageView createCardImage(
            String path,
            double width,
            double height) {

        Image image =
                new Image(
                        getClass().getResourceAsStream(path)
                );

        ImageView view = new ImageView(image);

        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);

        double imageRatio =
                image.getWidth() / image.getHeight();

        double boxRatio =
                width / height;

        double viewWidth =
                imageRatio > boxRatio
                        ? image.getHeight() * boxRatio
                        : image.getWidth();

        double viewHeight =
                imageRatio > boxRatio
                        ? image.getHeight()
                        : image.getWidth() / boxRatio;

        view.setViewport(
                new Rectangle2D(
                        (image.getWidth() - viewWidth) / 2,
                        (image.getHeight() - viewHeight) / 2,
                        viewWidth,
                        viewHeight
                )
        );

        return view;
    }
}
