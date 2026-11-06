package com.eklavya.spotfit;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;

import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.stage.Stage;

import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class SplashScreen extends Application {

    // =========================================================
    // IMAGE PATHS
    // =========================================================

    private static final String BACKGROUND =
            "/images/spotfit_atrium.png";

    private static final String LOGO =
            "/images/spotfit_logo.png";


    // =========================================================
    // WINDOW
    // =========================================================

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 720;

    private static final int SPLASH_DURATION = 10;


    // =========================================================
    // CENTER / ORBIT
    // =========================================================

    private static final double CENTER = 300;

    private static final double ORBIT_RADIUS = 215;


    // =========================================================
    // ROOT
    // =========================================================

    private final StackPane root = new StackPane();


    // =========================================================
    // ANIMATION OBJECTS
    // =========================================================

    private final List<Circle> dots =
            new ArrayList<>();

    private final List<StackPane> featureNodes =
            new ArrayList<>();


    private Text countdownText;

    private Pane rotatingRings;

    private Pane particles;

    private Pane featurePane;

    private StackPane logoContainer;


    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        launch(args);

    }


    // =========================================================
    // START
    // =========================================================

    @Override
    public void start(Stage stage) {

        stage.setTitle("SpotFit");

        stage.setResizable(true);


        // =====================================================
        // BACKGROUND
        // =====================================================

        Image backgroundImage =
                loadImage(BACKGROUND);

        ImageView background =
                new ImageView(backgroundImage);

        background.setPreserveRatio(false);

        background.fitWidthProperty()
                .bind(root.widthProperty());

        background.fitHeightProperty()
                .bind(root.heightProperty());


        // =====================================================
        // OVERLAY
        // =====================================================

        Pane overlay = new Pane();

        overlay.setMouseTransparent(true);

        overlay.setStyle(
                "-fx-background-color: rgba(255,255,255,0.10);"
        );

        overlay.prefWidthProperty()
                .bind(root.widthProperty());

        overlay.prefHeightProperty()
                .bind(root.heightProperty());


        // =====================================================
        // MAIN LAYOUT
        // =====================================================

        BorderPane layout =
                new BorderPane();

        layout.setPickOnBounds(false);


        // =====================================================
        // HEADER
        // =====================================================

        VBox header =
                createHeader();

        BorderPane.setAlignment(
                header,
                Pos.TOP_CENTER
        );

        BorderPane.setMargin(
                header,
                new Insets(15, 0, 0, 0)
        );


        // =====================================================
        // CENTER
        // =====================================================

        StackPane center =
                createCenter();

        BorderPane.setAlignment(
                center,
                Pos.CENTER
        );


        // =====================================================
        // BOTTOM
        // =====================================================

        VBox bottom =
                createBottom();

        BorderPane.setAlignment(
                bottom,
                Pos.BOTTOM_CENTER
        );

        BorderPane.setMargin(
                bottom,
                new Insets(0, 0, 18, 0)
        );


        // =====================================================
        // ADD TO LAYOUT
        // =====================================================

        layout.setTop(header);

        layout.setCenter(center);

        layout.setBottom(bottom);


        // =====================================================
        // ROOT
        // =====================================================

        root.getChildren().clear();

        root.getChildren().addAll(
                background,
                overlay,
                layout
        );


        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        WIDTH,
                        HEIGHT
                );

        scene.setFill(Color.WHITE);


        stage.setScene(scene);

        stage.show();


        // =====================================================
        // ANIMATIONS
        // =====================================================

        animateBackground(background);

        animateHeader(header);

        animateCenter(center);

        animateRings();

        animateParticles();

        animateLogoAndFeatures();

        startSplashTimer(stage);
    }


    // =========================================================
    // HEADER
    // =========================================================

    private VBox createHeader() {

        Text title =
                new Text("SPOTFIT");

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        40
                )
        );

        title.setFill(
                Color.rgb(15, 100, 200)
        );

        title.setEffect(
                new DropShadow(
                        6,
                        Color.rgb(
                                255,
                                255,
                                255,
                                0.8
                        )
                )
        );


        Text subtitle =
                new Text("Workspaces");

        subtitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        18
                )
        );

        subtitle.setFill(
                Color.rgb(20, 70, 120)
        );


        VBox header =
                new VBox(
                        0,
                        title,
                        subtitle
                );

        header.setAlignment(
                Pos.CENTER
        );

        header.setMouseTransparent(
                true
        );


        return header;
    }


    // =========================================================
    // CENTER
    // =========================================================

    private StackPane createCenter() {

        StackPane center =
                new StackPane();

        center.setPrefSize(
                600,
                600
        );

        center.setMinSize(
                600,
                600
        );

        center.setMaxSize(
                600,
                600
        );


        // -----------------------------------------------------
        // RINGS
        // -----------------------------------------------------

        rotatingRings =
                createRings();

        center.getChildren().add(
                rotatingRings
        );


        // -----------------------------------------------------
        // PARTICLES
        // -----------------------------------------------------

        particles =
                createParticles();

        center.getChildren().add(
                particles
        );


        // -----------------------------------------------------
        // LINES
        // -----------------------------------------------------

        center.getChildren().add(
                createLines()
        );


        // -----------------------------------------------------
        // FEATURES
        // -----------------------------------------------------

        featurePane =
                createFeatures();

        center.getChildren().add(
                featurePane
        );


        // -----------------------------------------------------
        // PLATFORM
        // -----------------------------------------------------

        center.getChildren().add(
                createPlatform()
        );


        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        logoContainer =
                createLogo();

        center.getChildren().add(
                logoContainer
        );


        return center;
    }


    // =========================================================
    // RINGS
    // =========================================================

    private Pane createRings() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                600,
                600
        );


        Circle outer =
                new Circle(
                        CENTER,
                        CENTER,
                        ORBIT_RADIUS + 18
                );

        outer.setFill(
                Color.TRANSPARENT
        );

        outer.setStroke(
                Color.rgb(
                        0,
                        245,
                        255,
                        0.18
                )
        );

        outer.setStrokeWidth(1);


        Circle main =
                new Circle(
                        CENTER,
                        CENTER,
                        ORBIT_RADIUS
                );

        main.setFill(
                Color.TRANSPARENT
        );

        main.setStroke(
                Color.rgb(
                        0,
                        245,
                        255,
                        0.50
                )
        );

        main.setStrokeWidth(1.4);

        main.getStrokeDashArray().addAll(
                2.0,
                8.0
        );

        main.setEffect(
                new Glow(0.7)
        );


        Circle second =
                new Circle(
                        CENTER,
                        CENTER,
                        170
                );

        second.setFill(
                Color.TRANSPARENT
        );

        second.setStroke(
                Color.rgb(
                        0,
                        230,
                        255,
                        0.22
                )
        );

        second.setStrokeWidth(1);


        Circle inner =
                new Circle(
                        CENTER,
                        CENTER,
                        125
                );

        inner.setFill(
                Color.TRANSPARENT
        );

        inner.setStroke(
                Color.rgb(
                        0,
                        245,
                        240,
                        0.32
                )
        );

        inner.setStrokeWidth(1.2);


        pane.getChildren().addAll(
                outer,
                main,
                second,
                inner
        );


        pane.setOpacity(0);


        return pane;
    }


    // =========================================================
    // PARTICLES
    // =========================================================

    private Pane createParticles() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                600,
                600
        );


        for (int i = 0; i < 36; i++) {

            double angle =
                    Math.toRadians(
                            i * 10
                    );

            double radius =
                    ORBIT_RADIUS + 14;


            double x =
                    CENTER +
                    radius *
                    Math.cos(angle);


            double y =
                    CENTER +
                    radius *
                    Math.sin(angle);


            Circle particle =
                    new Circle(
                            x,
                            y,
                            i % 4 == 0
                                    ? 2.5
                                    : 1.2
                    );


            particle.setFill(
                    Color.rgb(
                            0,
                            245,
                            240,
                            i % 4 == 0
                                    ? 0.9
                                    : 0.4
                    )
            );


            particle.setEffect(
                    new Glow(0.9)
            );


            pane.getChildren().add(
                    particle
            );
        }


        pane.setOpacity(0);


        return pane;
    }


    // =========================================================
    // LINES
    // =========================================================

    private Pane createLines() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                600,
                600
        );


        for (int i = 0; i < 8; i++) {

            double angle =
                    Math.toRadians(
                            -90 + i * 45
                    );


            double x =
                    CENTER +
                    ORBIT_RADIUS *
                    Math.cos(angle);


            double y =
                    CENTER +
                    ORBIT_RADIUS *
                    Math.sin(angle);


            Line line =
                    new Line(
                            CENTER,
                            CENTER,
                            x,
                            y
                    );


            line.setStroke(
                    Color.rgb(
                            0,
                            235,
                            240,
                            0.08
                    )
            );


            line.setStrokeWidth(0.7);


            line.setMouseTransparent(
                    true
            );


            pane.getChildren().add(
                    line
            );
        }


        pane.setOpacity(0);


        return pane;
    }


    // =========================================================
    // FEATURES
    // =========================================================

    private Pane createFeatures() {

        Pane pane =
                new Pane();

        pane.setPrefSize(
                600,
                600
        );


        String[] names = {

                "Cafe",
                "Wi-Fi",
                "Desk",
                "Quiet",
                "Space",
                "Team",
                "Seat",
                "People"

        };


        String[] icons = {

                "☕",
                "⌁",
                "▣",
                "♬",
                "⌂",
                "♧",
                "▱",
                "♙"

        };


        for (int i = 0; i < 8; i++) {

            double angle =
                    Math.toRadians(
                            -90 + i * 45
                    );


            double x =
                    CENTER +
                    ORBIT_RADIUS *
                    Math.cos(angle);


            double y =
                    CENTER +
                    ORBIT_RADIUS *
                    Math.sin(angle);


            StackPane feature =
                    createFeature(
                            names[i],
                            icons[i]
                    );


            feature.setLayoutX(
                    x - 35
            );

            feature.setLayoutY(
                    y - 35
            );


            feature.setTranslateX(
                    CENTER - x
            );

            feature.setTranslateY(
                    CENTER - y
            );


            feature.setScaleX(0.2);

            feature.setScaleY(0.2);

            feature.setOpacity(0);


            pane.getChildren().add(
                    feature
            );


            featureNodes.add(
                    feature
            );
        }


        return pane;
    }


    // =========================================================
    // FEATURE CARD
    // =========================================================

    private StackPane createFeature(
            String name,
            String iconText) {

        StackPane node =
                new StackPane();


        node.setPrefSize(
                70,
                70
        );

        node.setMaxSize(
                70,
                70
        );


        node.setMouseTransparent(
                true
        );


        Circle glow =
                new Circle(34);

        glow.setFill(
                Color.rgb(
                        0,
                        255,
                        240,
                        0.07
                )
        );

        glow.setEffect(
                new Glow(1)
        );


        Circle circle =
                new Circle(28);

        circle.setFill(
                Color.rgb(
                        3,
                        35,
                        44,
                        0.95
                )
        );

        circle.setStroke(
                Color.rgb(
                        90,
                        255,
                        240,
                        0.95
                )
        );

        circle.setStrokeWidth(1.7);


        circle.setEffect(
                new DropShadow(
                        12,
                        Color.rgb(
                                0,
                                255,
                                240,
                                0.7
                        )
                )
        );


        Text icon =
                new Text(iconText);

        icon.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        icon.setFill(
                Color.WHITE
        );


        Text label =
                new Text(name);

        label.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        12
                )
        );

        label.setFill(
                Color.WHITE
        );

        label.setTranslateY(42);


        node.getChildren().addAll(
                glow,
                circle,
                icon,
                label
        );


        return node;
    }


    // =========================================================
    // PLATFORM
    // =========================================================

    private StackPane createPlatform() {

        StackPane platform =
                new StackPane();


        platform.setPrefSize(
                140,
                140
        );

        platform.setMaxSize(
                140,
                140
        );


        platform.setMouseTransparent(
                true
        );


        Circle glow =
                new Circle(62);

        glow.setFill(
                Color.rgb(
                        0,
                        255,
                        230,
                        0.07
                )
        );

        glow.setEffect(
                new Glow(1)
        );


        Circle outer =
                new Circle(55);

        outer.setFill(
                Color.TRANSPARENT
        );

        outer.setStroke(
                Color.rgb(
                        0,
                        255,
                        230,
                        0.9
                )
        );

        outer.setStrokeWidth(3);

        outer.setEffect(
                new Glow(0.9)
        );


        Circle inner =
                new Circle(43);

        inner.setFill(
                Color.rgb(
                        0,
                        255,
                        230,
                        0.08
                )
        );

        inner.setStroke(
                Color.rgb(
                        140,
                        255,
                        245,
                        0.75
                )
        );

        inner.setStrokeWidth(1.5);


        platform.getChildren().addAll(
                glow,
                outer,
                inner
        );


        platform.setOpacity(0);


        return platform;
    }


    // =========================================================
    // LOGO
    // =========================================================

    private StackPane createLogo() {

        StackPane container =
                new StackPane();


        container.setPrefSize(
                180,
                170
        );

        container.setMaxSize(
                180,
                170
        );


        container.setMouseTransparent(
                true
        );


        // -----------------------------------------------------
        // LOAD LOGO
        // -----------------------------------------------------

        Image original =
                loadImage(LOGO);


        ImageView logo =
                new ImageView(original);


        logo.setFitWidth(105);

        logo.setFitHeight(105);

        logo.setPreserveRatio(true);

        logo.setSmooth(true);


        logo.setEffect(
                new DropShadow(
                        18,
                        Color.rgb(
                                255,
                                180,
                                70,
                                0.55
                        )
                )
        );


        // -----------------------------------------------------
        // TAGLINE
        // -----------------------------------------------------

        Text tagline =
                new Text(
                        "FIND  •  FOCUS  •  FLOW"
                );


        tagline.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        11
                )
        );


        tagline.setFill(
                Color.rgb(
                        20,
                        85,
                        120
                )
        );


        tagline.setOpacity(0);

        tagline.setTranslateY(62);


        // -----------------------------------------------------
        // ACCENT
        // -----------------------------------------------------

        Text accent =
                new Text(
                        "Your space. Your way."
                );


        accent.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        10
                )
        );


        accent.setFill(
                Color.rgb(
                        60,
                        100,
                        120,
                        0.85
                )
        );


        accent.setOpacity(0);

        accent.setTranslateY(80);


        container.getChildren().addAll(
                logo,
                tagline,
                accent
        );


        container.setOpacity(0);

        container.setScaleX(0.55);

        container.setScaleY(0.55);


        return container;
    }


    // =========================================================
    // BACKGROUND ANIMATION
    // =========================================================

    private void animateBackground(
            ImageView background) {

        background.setOpacity(0);


        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1.2),
                        background
                );


        fade.setFromValue(0);

        fade.setToValue(1);

        fade.play();
    }


    // =========================================================
    // HEADER ANIMATION
    // =========================================================

    private void animateHeader(
            Node header) {

        header.setOpacity(0);

        header.setTranslateY(-20);


        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1),
                        header
                );


        fade.setFromValue(0);

        fade.setToValue(1);


        TranslateTransition move =
                new TranslateTransition(
                        Duration.seconds(1),
                        header
                );


        move.setFromY(-20);

        move.setToY(0);


        new ParallelTransition(
                fade,
                move
        ).play();
    }


    // =========================================================
    // CENTER ANIMATION
    // =========================================================

    private void animateCenter(
            Node center) {

        center.setOpacity(1);
    }


    // =========================================================
    // LOGO + FEATURES
    // =========================================================

    private void animateLogoAndFeatures() {

        FadeTransition logoFade =
                new FadeTransition(
                        Duration.millis(700),
                        logoContainer
                );


        logoFade.setFromValue(0);

        logoFade.setToValue(1);


        ScaleTransition logoScale =
                new ScaleTransition(
                        Duration.millis(900),
                        logoContainer
                );


        logoScale.setFromX(0.55);

        logoScale.setFromY(0.55);

        logoScale.setToX(1);

        logoScale.setToY(1);


        logoScale.setInterpolator(
                Interpolator.EASE_OUT
        );


        ParallelTransition logoIntro =
                new ParallelTransition(
                        logoFade,
                        logoScale
                );


        Text tagline =
                (Text) logoContainer
                        .getChildren()
                        .get(1);


        Text accent =
                (Text) logoContainer
                        .getChildren()
                        .get(2);


        FadeTransition taglineFade =
                new FadeTransition(
                        Duration.millis(500),
                        tagline
                );


        taglineFade.setFromValue(0);

        taglineFade.setToValue(1);


        FadeTransition accentFade =
                new FadeTransition(
                        Duration.millis(500),
                        accent
                );


        accentFade.setFromValue(0);

        accentFade.setToValue(1);


        FadeTransition ringsFade =
                new FadeTransition(
                        Duration.millis(700),
                        rotatingRings
                );


        ringsFade.setFromValue(0);

        ringsFade.setToValue(1);


        FadeTransition particlesFade =
                new FadeTransition(
                        Duration.millis(600),
                        particles
                );


        particlesFade.setFromValue(0);

        particlesFade.setToValue(1);


        SequentialTransition featuresSequence =
                new SequentialTransition();


        for (StackPane feature :
                featureNodes) {


            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(350),
                            feature
                    );


            fade.setFromValue(0);

            fade.setToValue(1);


            TranslateTransition move =
                    new TranslateTransition(
                            Duration.millis(650),
                            feature
                    );


            move.setFromX(
                    feature.getTranslateX()
            );

            move.setFromY(
                    feature.getTranslateY()
            );


            move.setToX(0);

            move.setToY(0);


            move.setInterpolator(
                    Interpolator.EASE_OUT
            );


            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(600),
                            feature
                    );


            scale.setFromX(0.2);

            scale.setFromY(0.2);

            scale.setToX(1);

            scale.setToY(1);


            scale.setInterpolator(
                    Interpolator.EASE_OUT
            );


            ParallelTransition appear =
                    new ParallelTransition(
                            fade,
                            move,
                            scale
                    );


            featuresSequence
                    .getChildren()
                    .add(appear);


            featuresSequence
                    .getChildren()
                    .add(
                            new PauseTransition(
                                    Duration.millis(100)
                            )
                    );
        }


        SequentialTransition fullSequence =
                new SequentialTransition(

                        logoIntro,

                        new PauseTransition(
                                Duration.millis(250)
                        ),

                        taglineFade,

                        accentFade,

                        new PauseTransition(
                                Duration.millis(100)
                        ),

                        ringsFade,

                        particlesFade,

                        new PauseTransition(
                                Duration.millis(150)
                        ),

                        featuresSequence
                );


        fullSequence.play();


        fullSequence.setOnFinished(
                event -> {

                    ScaleTransition pulse =
                            new ScaleTransition(
                                    Duration.seconds(2),
                                    logoContainer
                            );


                    pulse.setFromX(1);

                    pulse.setFromY(1);

                    pulse.setToX(1.035);

                    pulse.setToY(1.035);


                    pulse.setAutoReverse(true);


                    pulse.setCycleCount(
                            Animation.INDEFINITE
                    );


                    pulse.play();
                }
        );
    }


    // =========================================================
    // RING ANIMATION
    // =========================================================

    private void animateRings() {

        if (rotatingRings == null) {
            return;
        }


        RotateTransition rotate =
                new RotateTransition(
                        Duration.seconds(18),
                        rotatingRings
                );


        rotate.setFromAngle(0);

        rotate.setToAngle(360);


        rotate.setCycleCount(
                Animation.INDEFINITE
        );


        rotate.setInterpolator(
                Interpolator.LINEAR
        );


        rotate.play();
    }


    // =========================================================
    // PARTICLE ANIMATION
    // =========================================================

    private void animateParticles() {

        if (particles == null) {
            return;
        }


        for (Node node :
                particles.getChildren()) {


            ScaleTransition pulse =
                    new ScaleTransition(
                            Duration.seconds(
                                    1 +
                                    Math.random()
                            ),
                            node
                    );


            pulse.setFromX(0.4);

            pulse.setFromY(0.4);

            pulse.setToX(1.5);

            pulse.setToY(1.5);


            pulse.setAutoReverse(true);


            pulse.setCycleCount(
                    Animation.INDEFINITE
            );


            pulse.play();
        }
    }


    // =========================================================
    // BOTTOM
    // =========================================================

    private VBox createBottom() {

        Text loading =
                new Text(
                        "Finding the best workspaces for you..."
                );


        loading.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        13
                )
        );


        loading.setFill(
                Color.rgb(
                        10,
                        70,
                        120
                )
        );


        // -----------------------------------------------------
        // DOTS
        // -----------------------------------------------------

        Pane dotPane =
                new Pane();


        dotPane.setPrefSize(
                100,
                18
        );


        for (int i = 0; i < 5; i++) {

            Circle dot =
                    new Circle(3.5);


            dot.setCenterX(
                    25 + i * 12
            );


            dot.setCenterY(9);


            dot.setFill(
                    Color.rgb(
                            40,
                            125,
                            200,
                            0.35
                    )
            );


            dotPane.getChildren().add(
                    dot
            );


            dots.add(dot);
        }


        // -----------------------------------------------------
        // COUNTDOWN
        // -----------------------------------------------------

        countdownText =
                new Text(
                        "↻  Redirecting in "
                                + SPLASH_DURATION
                                + " seconds"
                );


        countdownText.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        12
                )
        );


        countdownText.setFill(
                Color.rgb(
                        10,
                        75,
                        125
                )
        );


        VBox bottom =
                new VBox(
                        3,
                        loading,
                        dotPane,
                        countdownText
                );


        bottom.setAlignment(
                Pos.CENTER
        );


        bottom.setMouseTransparent(
                true
        );


        return bottom;
    }


    // =========================================================
    // SPLASH TIMER
    // =========================================================

    private void startSplashTimer(
            Stage stage) {


        // -----------------------------------------------------
        // DOT ANIMATION
        // -----------------------------------------------------

        Timeline dotAnimation =
                new Timeline();


        for (int i = 0; i < dots.size(); i++) {

            Circle dot =
                    dots.get(i);


            double time =
                    i * 0.4;


            dotAnimation.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(time),
                            event -> {

                                dot.setOpacity(1);

                                dot.setScaleX(1.4);

                                dot.setScaleY(1.4);

                            }
                    )
            );


            dotAnimation.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(
                                    time + 0.35
                            ),
                            event -> {

                                dot.setOpacity(0.35);

                                dot.setScaleX(1);

                                dot.setScaleY(1);

                            }
                    )
            );
        }


        dotAnimation.setCycleCount(
                Animation.INDEFINITE
        );


        dotAnimation.play();


        // -----------------------------------------------------
        // COUNTDOWN
        // -----------------------------------------------------

        Timeline countdown =
                new Timeline();


        for (
                int second = SPLASH_DURATION;
                second >= 1;
                second--
        ) {


            final int remaining =
                    second;


            countdown.getKeyFrames().add(
                    new KeyFrame(
                            Duration.seconds(
                                    SPLASH_DURATION - second
                            ),
                            event -> {

                                countdownText.setText(
                                        "↻  Redirecting in "
                                                + remaining
                                                + " seconds"
                                );

                            }
                    )
            );
        }


        countdown.play();


        // -----------------------------------------------------
        // FINISH
        // -----------------------------------------------------

        Timeline finish =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(
                                        SPLASH_DURATION
                                )
                        )
                );


        finish.setOnFinished(
                event -> {

                    dotAnimation.stop();

                    countdown.stop();

                    openWelcomeScreen(stage);

                }
        );


        finish.play();
    }


    // =========================================================
    // STANDALONE WELCOME SCREEN
    // =========================================================

    private void openWelcomeScreen(
            Stage stage) {


        StackPane welcomeRoot =
                new StackPane();


        welcomeRoot.setStyle(
                "-fx-background-color: #FFF8F2;"
        );


        VBox box =
                new VBox(15);


        box.setAlignment(
                Pos.CENTER
        );


        Text title =
                new Text(
                        "Welcome to SpotFit"
                );


        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        36
                )
        );


        title.setFill(
                Color.rgb(
                        15,
                        100,
                        200
                )
        );


        Text subtitle =
                new Text(
                        "Find Your Space. Fit Your Purpose."
                );


        subtitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        18
                )
        );


        subtitle.setFill(
                Color.rgb(
                        60,
                        80,
                        100
                )
        );


        box.getChildren().addAll(
                title,
                subtitle
        );


        welcomeRoot.getChildren().add(
                box
        );


        Scene welcomeScene =
                new Scene(
                        welcomeRoot,
                        WIDTH,
                        HEIGHT
                );


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(500),
                        root
                );


        fade.setFromValue(1);

        fade.setToValue(0);


        fade.setOnFinished(
                event -> {

                    stage.setScene(
                            welcomeScene
                    );

                    stage.setMaximized(false);

                    stage.centerOnScreen();

                    stage.show();

                }
        );


        fade.play();
    }


    // =========================================================
    // LOAD IMAGE SAFELY
    // =========================================================

    private Image loadImage(
            String path) {


        URL resource =
                SplashScreen.class
                        .getResource(path);


        if (resource == null) {

            System.out.println(
                    "Image not found: " + path
            );


            // Return a tiny transparent image
            // instead of crashing the application.

            return new Image(
                    "data:image/png;base64,"
                            + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAAB"
                            + "CAQAAAC1HAwCAAAAC0lEQVR42mNk"
                            + "YAAAAAYAAjCB0C8AAAAASUVORK5CYII="
            );
        }


        return new Image(
                resource.toExternalForm()
        );
    }
}