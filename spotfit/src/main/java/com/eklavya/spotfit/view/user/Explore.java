package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.view.common.WelcomePage;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class Explore {

        // ============================================================
        // IMAGE PATHS
        // Same images used in UserDash
        // ============================================================

        private static final String CREATIVITY_IMAGE = "/images/UserDashImg/Creativity.png";

        private static final String SPORTS_IMAGE = "/images/UserDashImg/Sports.png";

        private static final String WELLNESS_IMAGE = "/images/UserDashImg/Wellness.png";

        private static final String WORKSPACES_IMAGE = "/images/UserDashImg/Workplaces.png";

        private static final String FALLBACK_IMAGE = "/images/UserDashImg/Sports.png";

        // ============================================================
        // MAIN SCENE
        // ============================================================

        public static Scene getExploreScene(Runnable callBackAction) {

                BorderPane root = new BorderPane();

                // KEEP EXISTING PROJECT BACKGROUND
                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                HBox topBar = buildTopBar(callBackAction);

                VBox content = buildContent();

                ScrollPane scroll = new ScrollPane(content);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scroll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                root.setTop(topBar);
                root.setCenter(scroll);

                return new Scene(root, 1280, 800);
        }

        // ============================================================
        // TOP BAR
        // ============================================================

        private static HBox buildTopBar(Runnable callBackAction) {

                HBox bar = new HBox(20);

                bar.setAlignment(Pos.CENTER_LEFT);

                bar.setPadding(
                                new Insets(15, 42, 15, 42));

                bar.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-width: 0 0 1 0;");

                // --------------------------------------------------------
                // BACK BUTTON
                // --------------------------------------------------------

                Button back = new Button("←  Back");

                back.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;");

                back.setOnMouseEntered(e -> back.setStyle(
                                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                                                "-fx-background-radius: 8;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"));

                back.setOnMouseExited(e -> back.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"));

                back.setOnAction(e -> callBackAction.run());

                // --------------------------------------------------------
                // TITLE
                // --------------------------------------------------------

                Label title = new Label("Explore");

                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 20px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                // --------------------------------------------------------
                // SPACER
                // --------------------------------------------------------

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // --------------------------------------------------------
                // NOTIFICATION
                // --------------------------------------------------------

                // Button notification = new Button("♧");

                // notification.setStyle(
                //                 "-fx-background-color: " + PILL_BG + ";" +
                //                                 "-fx-background-radius: 20;" +
                //                                 "-fx-font-size: 15px;" +
                //                                 "-fx-text-fill: " + TEXT_DARK + ";" +
                //                                 "-fx-cursor: hand;");

                // --------------------------------------------------------
                // PROFILE
                // --------------------------------------------------------

                // Button profile = new Button("●");

                // profile.setStyle(
                //                 "-fx-background-color: " + PILL_BG + ";" +
                //                                 "-fx-background-radius: 20;" +
                //                                 "-fx-font-size: 13px;" +
                //                                 "-fx-text-fill: " + TEXT_DARK + ";" +
                //                                 "-fx-cursor: hand;");

                bar.getChildren().addAll(
                                back,
                                title,
                                spacer);

                return bar;
        }

        // ============================================================
        // MAIN CONTENT
        // ============================================================

        private static VBox buildContent() {

                VBox content = new VBox(30);

                content.setFillWidth(true);

                content.setPadding(
                                new Insets(35, 55, 60, 55));

                content.getChildren().addAll(
                                buildHero(),
                                buildDomainSection());

                return content;
        }

        // ============================================================
        // HERO
        // ============================================================

        private static VBox buildHero() {

                VBox hero = new VBox(7);

                Label title = new Label(
                                "Explore spaces that match your vibe");

                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 29px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label subtitle = new Label(
                                "Choose what you want to explore.");

                subtitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                hero.getChildren().addAll(
                                title,
                                subtitle);

                return hero;
        }

        // ============================================================
        // FOUR MAIN DOMAINS
        // Same concept and design as UserDash
        // ============================================================

        private static VBox buildDomainSection() {

                VBox section = new VBox(15);

                // --------------------------------------------------------
                // SECTION HEADER
                // --------------------------------------------------------

                HBox headingRow = new HBox();

                headingRow.setAlignment(
                                Pos.CENTER_LEFT);

                Label heading = new Label(
                                "Explore by activity");

                heading.setStyle(
                                heading(17));

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Label description = new Label(
                                "Find a space for what you love.");

                description.setStyle(
                                body(10));

                headingRow.getChildren().addAll(
                                heading,
                                spacer,
                                description);

                // --------------------------------------------------------
                // CARD ROW 1
                // --------------------------------------------------------

                HBox rowOne = new HBox(18);

                rowOne.setAlignment(
                                Pos.CENTER);

                rowOne.setFillHeight(true);

                StackPane creativity = domainCard(
                                CREATIVITY_IMAGE,
                                "Creativity",
                                "Art, music, podcasts & more");

                StackPane sports = domainCard(
                                SPORTS_IMAGE,
                                "Sports",
                                "Turfs, courts & gaming zones");

                HBox.setHgrow(
                                creativity,
                                Priority.ALWAYS);

                HBox.setHgrow(
                                sports,
                                Priority.ALWAYS);

                rowOne.getChildren().addAll(
                                creativity,
                                sports);

                // --------------------------------------------------------
                // CARD ROW 2
                // --------------------------------------------------------

                HBox rowTwo = new HBox(18);

                rowTwo.setAlignment(
                                Pos.CENTER);

                rowTwo.setFillHeight(true);

                StackPane wellness = domainCard(
                                WELLNESS_IMAGE,
                                "Wellness",
                                "Yoga, gyms & meditation");

                StackPane workspaces = domainCard(
                                WORKSPACES_IMAGE,
                                "Workspaces",
                                "Study spots & co-working");

                HBox.setHgrow(
                                wellness,
                                Priority.ALWAYS);

                HBox.setHgrow(
                                workspaces,
                                Priority.ALWAYS);

                rowTwo.getChildren().addAll(
                                wellness,
                                workspaces);

                section.getChildren().addAll(
                                headingRow,
                                rowOne,
                                rowTwo);

                return section;
        }

        // ============================================================
        // DOMAIN CARD
        // COPIED FROM USERDASH STYLE
        // ============================================================

        private static StackPane domainCard(
                        String imagePath,
                        String title,
                        String subtitle) {

                double cardHeight = 230;

                StackPane card = new StackPane();

                card.setMinWidth(250);

                card.setPrefWidth(520);

                card.setMaxWidth(Double.MAX_VALUE);

                card.setMinHeight(cardHeight);

                card.setPrefHeight(cardHeight);

                card.setStyle(
                                "-fx-cursor: hand;" +
                                                "-fx-background-radius: 18;");

                // --------------------------------------------------------
                // IMAGE
                // --------------------------------------------------------

                ImageView image = new ImageView(
                                loadImage(imagePath));

                image.setPreserveRatio(false);

                image.setFitHeight(
                                cardHeight);

                image.fitWidthProperty().bind(
                                card.widthProperty());

                // --------------------------------------------------------
                // ROUNDED CLIP
                // --------------------------------------------------------

                Rectangle clip = new Rectangle();

                clip.setArcWidth(18);

                clip.setArcHeight(18);

                clip.widthProperty().bind(
                                card.widthProperty());

                clip.setHeight(
                                cardHeight);

                image.setClip(clip);

                // --------------------------------------------------------
                // DARK GRADIENT OVERLAY
                // Same as UserDash
                // --------------------------------------------------------

                Region overlay = new Region();

                overlay.setPrefHeight(
                                cardHeight);

                overlay.setBackground(
                                new javafx.scene.layout.Background(
                                                new javafx.scene.layout.BackgroundFill(
                                                                new LinearGradient(
                                                                                0,
                                                                                0,
                                                                                0,
                                                                                1,
                                                                                true,
                                                                                CycleMethod.NO_CYCLE,

                                                                                new Stop(
                                                                                                0,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0)),

                                                                                new Stop(
                                                                                                0.55,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0)),

                                                                                new Stop(
                                                                                                1,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0.72))),

                                                                new javafx.scene.layout.CornerRadii(
                                                                                18),

                                                                Insets.EMPTY)));

                // --------------------------------------------------------
                // TEXT
                // --------------------------------------------------------

                VBox textBox = new VBox(4);

                textBox.setAlignment(
                                Pos.BOTTOM_LEFT);

                textBox.setPadding(
                                new Insets(
                                                0,
                                                20,
                                                20,
                                                20));

                Label titleLabel = new Label(
                                title);

                titleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 26px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: white;");

                Label subtitleLabel = new Label(
                                subtitle);

                subtitleLabel.setWrapText(true);

                subtitleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-text-fill: rgba(255,255,255,0.88);");

                textBox.getChildren().addAll(
                                titleLabel,
                                subtitleLabel);

                StackPane.setAlignment(
                                textBox,
                                Pos.BOTTOM_LEFT);

                // --------------------------------------------------------
                // ADD EVERYTHING
                // --------------------------------------------------------

                card.getChildren().addAll(
                                image,
                                overlay,
                                textBox);

                // --------------------------------------------------------
                // SHADOW
                // --------------------------------------------------------

                addShadow(
                                card,
                                0.10);

                // --------------------------------------------------------
                // HOVER
                // --------------------------------------------------------

                card.setOnMouseEntered(
                                e -> liftCard(
                                                card,
                                                true));

                card.setOnMouseExited(
                                e -> liftCard(
                                                card,
                                                false));

                // --------------------------------------------------------
                // CLICK
                // --------------------------------------------------------

                card.setOnMouseClicked(
                                e -> openDomainPage(title));

                return card;
        }

        // ============================================================
        // SPACE domain NAVIGATION
        // ============================================================

        private static void openDomainPage(
                        String domainName) {

                com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                DomainPage.getDomainScene(
                                                domainName,
                                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                                Explore.getExploreScene(
                                                                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                                                                UserDash.getUserDashScene())))));
        }

        // ============================================================
        // IMAGE LOADER
        // ============================================================

        private static Image loadImage(
                        String classpathPath) {

                java.io.InputStream stream = Explore.class.getResourceAsStream(
                                classpathPath);

                if (stream == null) {

                        System.err.println(
                                        "[Explore] Missing image resource: "
                                                        + classpathPath
                                                        + " — using fallback image.");

                        stream = Explore.class.getResourceAsStream(
                                        FALLBACK_IMAGE);

                        if (stream == null) {

                                throw new IllegalStateException(
                                                "Fallback image is also missing: "
                                                                + FALLBACK_IMAGE);
                        }
                }

                return new Image(
                                stream);
        }

        // ============================================================
        // SHADOW
        // ============================================================

        private static void addShadow(
                        Region node,
                        double opacity) {

                DropShadow shadow = new DropShadow();

                shadow.setRadius(14);

                shadow.setOffsetY(4);

                shadow.setColor(
                                Color.rgb(
                                                0,
                                                0,
                                                0,
                                                opacity));

                node.setEffect(
                                shadow);
        }

        // ============================================================
        // HOVER ANIMATION
        // ============================================================

        private static void liftCard(
                        Region card,
                        boolean hovered) {

                ScaleTransition scale = new ScaleTransition(
                                Duration.millis(120),
                                card);

                if (hovered) {

                        scale.setToX(1.015);

                        scale.setToY(1.015);

                        addShadow(
                                        card,
                                        0.13);

                } else {

                        scale.setToX(1.0);

                        scale.setToY(1.0);

                        addShadow(
                                        card,
                                        0.06);
                }

                scale.play();
        }
}
