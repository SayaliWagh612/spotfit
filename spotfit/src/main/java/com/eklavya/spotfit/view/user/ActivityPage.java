package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class ActivityPage {

    private static final double CONTENT_MAX_WIDTH = 1120;

    public static Scene getActivityScene(
            String domainName,
            String subdomainName,
            Runnable backAction) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");
        root.setTop(buildTopBar(subdomainName, backAction));

        VBox content = buildContent(domainName, subdomainName);
        StackPane centeredContent = new StackPane(content);
        centeredContent.setAlignment(Pos.TOP_CENTER);
        centeredContent.setStyle("-fx-background-color: transparent;");

        ScrollPane scroll = new ScrollPane(centeredContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);
        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;");

        root.setCenter(scroll);
        return new Scene(root, 1280, 800);
    }

    private static HBox buildTopBar(String subdomainName, Runnable backAction) {
        HBox bar = new HBox(18);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 42, 14, 42));
        bar.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;");

        Button back = new Button("←  Back");
        String normalStyle =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 12;" +
                "-fx-cursor: hand;";
        String hoverStyle =
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 12;" +
                "-fx-cursor: hand;";
        back.setStyle(normalStyle);
        back.setOnMouseEntered(e -> back.setStyle(hoverStyle));
        back.setOnMouseExited(e -> back.setStyle(normalStyle));
        back.setOnAction(e -> backAction.run());

        Label title = new Label(subdomainName);
        title.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + TEXT_DARK + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // No profile/notification control is shown here because this page does
        // not currently own a working action for either control.
        bar.getChildren().addAll(back, title, spacer);
        return bar;
    }

    private static VBox buildContent(String domainName, String subdomainName) {
        VBox content = new VBox(24);
        content.setMaxWidth(CONTENT_MAX_WIDTH);
        content.setFillWidth(true);
        content.setPadding(new Insets(28, 0, 56, 0));

        Label breadcrumb = new Label(domainName + "   /   " + subdomainName);
        breadcrumb.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";");

        StackPane hero = buildHero(domainName, subdomainName);

        HBox sectionHeader = new HBox(16);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);

        VBox sectionCopy = new VBox(4);
        Label sectionTitle = new Label("Choose what you want to do");
        sectionTitle.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + TEXT_DARK + ";");
        Label sectionSubtitle = new Label("Select an activity to see matching spaces.");
        sectionSubtitle.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";");
        sectionCopy.getChildren().addAll(sectionTitle, sectionSubtitle);

        Region sectionSpacer = new Region();
        HBox.setHgrow(sectionSpacer, Priority.ALWAYS);

        int activityCount = getActivities(domainName, subdomainName).length;
        Label count = new Label(activityCount + (activityCount == 1 ? " activity" : " activities"));
        count.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 18;" +
                "-fx-padding: 7 13;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";");

        sectionHeader.getChildren().addAll(sectionCopy, sectionSpacer, count);
        content.getChildren().addAll(
                breadcrumb,
                hero,
                sectionHeader,
                buildActivityGrid(domainName, subdomainName));
        return content;
    }

    private static StackPane buildHero(String domainName, String subdomainName) {
        StackPane hero = new StackPane();
        hero.setMinHeight(190);
        hero.setPrefHeight(205);
        hero.setMaxWidth(Double.MAX_VALUE);

        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(PRIMARY_LIGHT)),
                new Stop(0.52, Color.web(PRIMARY)),
                new Stop(1, Color.web(PRIMARY_DARK)));
        hero.setBackground(new Background(
                new BackgroundFill(gradient, new CornerRadii(22), Insets.EMPTY)));
        addShadow(hero, 0.15, 22);

        Region glowOne = decorativeCircle(170, "rgba(255,255,255,0.08)");
        StackPane.setAlignment(glowOne, Pos.CENTER_RIGHT);
        StackPane.setMargin(glowOne, new Insets(0, 65, 0, 0));

        Region glowTwo = decorativeCircle(90, "rgba(255,255,255,0.10)");
        StackPane.setAlignment(glowTwo, Pos.TOP_RIGHT);
        StackPane.setMargin(glowTwo, new Insets(-12, 210, 0, 0));

        VBox text = new VBox(8);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(30, 38, 30, 38));
        text.setMaxWidth(720);

        Label eyebrow = new Label(domainName.toUpperCase());
        eyebrow.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: rgba(255,255,255,0.76);");

        Label title = new Label("Explore " + subdomainName);
        title.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 31px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: white;");

        Label subtitle = new Label(getHeroDescription(subdomainName));
        subtitle.setWrapText(true);
        subtitle.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-text-fill: rgba(255,255,255,0.90);");

        text.getChildren().addAll(eyebrow, title, subtitle);
        StackPane.setAlignment(text, Pos.CENTER_LEFT);
        hero.getChildren().addAll(glowOne, glowTwo, text);
        return hero;
    }

    private static Region decorativeCircle(double size, String color) {
        Region circle = new Region();
        circle.setMouseTransparent(true);
        circle.setMinSize(size, size);
        circle.setPrefSize(size, size);
        circle.setMaxSize(size, size);
        circle.setStyle("-fx-background-color: " + color + "; -fx-background-radius: " + (size / 2) + ";");
        return circle;
    }

    private static GridPane buildActivityGrid(String domainName, String subdomainName) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(2, 0, 20, 0));
        grid.setMaxWidth(Double.MAX_VALUE);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(33.333);
            column.setHgrow(Priority.ALWAYS);
            column.setFillWidth(true);
            grid.getColumnConstraints().add(column);
        }

        String[] activities = getActivities(domainName, subdomainName);
        for (int i = 0; i < activities.length; i++) {
            VBox card = activityCard(domainName, subdomainName, activities[i], i + 1);
            grid.add(card, i % 3, i / 3);
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setFillWidth(card, true);
        }
        return grid;
    }

    private static VBox activityCard(
            String domainName,
            String subdomainName,
            String activity,
            int number) {

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setMinHeight(168);
        card.setPrefHeight(176);
        card.setMaxWidth(Double.MAX_VALUE);
        applyCardStyle(card, false);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label index = new Label(String.format("%02d", number));
        index.setAlignment(Pos.CENTER);
        index.setMinSize(38, 38);
        index.setPrefSize(38, 38);
        index.setMaxSize(38, 38);
        index.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";");

        Label category = new Label("ACTIVITY");
        category.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT_PLACEHOLDER + ";");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        top.getChildren().addAll(index, topSpacer, category);

        Label name = new Label(activity);
        name.setWrapText(true);
        name.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + TEXT_DARK + ";");

        Label description = new Label(getShortDescription(activity));
        description.setWrapText(true);
        description.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + TEXT_MUTED + ";");

        Region verticalSpacer = new Region();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);

        Label explore = new Label("View matching spaces  →");
        explore.setStyle(
                "-fx-font-family: " + FONT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY + ";");

        card.getChildren().addAll(top, name, description, verticalSpacer, explore);
        addShadow(card, 0.06, 14);

        card.setOnMouseEntered(e -> animateCard(card, true));
        card.setOnMouseExited(e -> animateCard(card, false));
        card.setOnMouseClicked(e -> openSpaces(domainName, subdomainName, activity));
        return card;
    }

    private static void animateCard(VBox card, boolean hovered) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(130), card);
        scale.setToX(hovered ? 1.018 : 1.0);
        scale.setToY(hovered ? 1.018 : 1.0);
        scale.play();
        applyCardStyle(card, hovered);
        addShadow(card, hovered ? 0.13 : 0.06, hovered ? 20 : 14);
    }

    private static void applyCardStyle(Region card, boolean hovered) {
        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-background-radius: 17;" +
                "-fx-border-color: " + (hovered ? PRIMARY_LIGHT : BORDER) + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 17;" +
                "-fx-cursor: hand;");
    }

    private static void addShadow(Region node, double opacity, double radius) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(radius);
        shadow.setOffsetY(5);
        shadow.setColor(Color.color(0.23, 0.14, 0.09, opacity));
        node.setEffect(shadow);
    }

    private static String getHeroDescription(String subdomainName) {
        return switch (subdomainName) {
            case "Art" -> "Create, learn and discover studios built for hands-on imagination.";
            case "Music" -> "From practice to performance, find the right space for your sound.";
            case "Podcasts" -> "Record, edit and develop your next conversation in a focused studio.";
            case "Content Creation" -> "Find polished spaces and equipment for your next shoot or idea.";
            default -> "Pick an activity and explore spaces that fit what you want to do.";
        };
    }

    private static String getShortDescription(String activity) {
        return switch (activity) {
            case "Painting & Drawing" -> "Create and express yourself in an inspiring studio.";
            case "DIY Workshops & Craft" -> "Make something personal with hands-on guidance.";
            case "Pottery, Clay & Resin Art" -> "Shape, pour and bring tactile ideas to life.";
            case "Crochet" -> "Slow down, learn and create stitch by stitch.";
            case "Cooking Workshops" -> "Cook, learn and share a practical experience.";
            case "Other Creative Workshops" -> "Discover more ways to learn and make.";
            case "Aesthetic Cafés" -> "Create content in a warm, styled environment.";
            case "Outdoor Shooting Locations" -> "Find visual settings for photography and video.";
            case "Creator / Photography Studios" -> "Work with controlled lighting and professional setups.";
            case "Equipment Rental" -> "Access the gear required for your next project.";
            case "YouTube / Shooting-Technique Workshops" -> "Improve production skills through guided learning.";
            case "Content-Creation & Monetization Workshops" -> "Learn how to build and grow creative work.";
            case "Podcast Studios" -> "Record conversations in a sound-ready environment.";
            case "Podcast Recording Workshops" -> "Learn recording workflows from preparation to export.";
            case "Editing Spaces" -> "Focus on post-production in a dedicated workspace.";
            case "Podcast Equipment" -> "Find microphones and audio tools for clear recording.";
            case "Music Learning" -> "Learn, practise and improve with the right setup.";
            case "Recording Studios" -> "Capture polished sound in an equipped studio.";
            case "Practice Rooms" -> "Rehearse comfortably without interruptions.";
            case "Music Workshops" -> "Join a guided session and develop your skills.";
            case "Performance Venues" -> "Find a space suited to audiences and live sound.";
            case "Turfs" -> "Play and practise your favourite team sport.";
            case "Courts" -> "Find the right court for your next match.";
            case "Gaming Zones" -> "Play, compete and enjoy a social gaming session.";
            case "Adventure Activities" -> "Try an active experience outside your routine.";
            case "Yoga" -> "Move, stretch and find balance in a calm studio.";
            case "Zumba" -> "Dance and stay active in an energetic class.";
            case "Gyms" -> "Train with practical equipment and focused space.";
            case "Meditation" -> "Pause, focus and refresh in a quiet environment.";
            case "Spas" -> "Relax and recover in a comfortable setting.";
            case "Study Libraries" -> "Read and work with fewer distractions.";
            case "Work Cafes" -> "Stay productive in a relaxed café environment.";
            case "Silent Working Areas" -> "Focus deeply in a distraction-free setting.";
            case "Co-working Spaces" -> "Work independently within a professional community.";
            default -> "Discover a space designed around this activity.";
        };
    }

    private static String[] getActivities(String domainName, String subdomainName) {
        if ("Creativity".equals(domainName)) {
            return switch (subdomainName) {
                case "Art" -> new String[] {
                        "Painting & Drawing",
                        "DIY Workshops & Craft",
                        "Pottery, Clay & Resin Art",
                        "Crochet",
                        "Cooking Workshops",
                        "Other Creative Workshops"
                };
                case "Content Creation" -> new String[] {
                        "Aesthetic Cafés",
                        "Outdoor Shooting Locations",
                        "Creator / Photography Studios",
                        "Equipment Rental",
                        "YouTube / Shooting-Technique Workshops",
                        "Content-Creation & Monetization Workshops"
                };
                case "Podcasts" -> new String[] {
                        "Podcast Studios",
                        "Podcast Recording Workshops",
                        "Editing Spaces",
                        "Podcast Equipment"
                };
                case "Music" -> new String[] {
                        "Music Learning",
                        "Recording Studios",
                        "Practice Rooms",
                        "Music Workshops",
                        "Performance Venues"
                };
                default -> new String[] {};
            };
        }

        return switch (domainName) {
            case "Sports" -> new String[] { "Turfs", "Courts", "Gaming Zones", "Adventure Activities" };
            case "Wellness" -> new String[] { "Yoga", "Zumba", "Gyms", "Meditation", "Spas" };
            case "Workspaces" -> new String[] {
                    "Study Libraries", "Work Cafes", "Silent Working Areas", "Co-working Spaces"
            };
            default -> new String[] {};
        };
    }

    private static void openSpaces(
            String domainName,
            String subdomainName,
            String activityName) {

        AppNavigator.setScene(
                SpaceListing.getSpaceListingScene(
                        domainName,
                        subdomainName,
                        activityName,
                        () -> AppNavigator.setScene(
                                ActivityPage.getActivityScene(
                                        domainName,
                                        subdomainName,
                                        () -> AppNavigator.setScene(
                                                DomainPage.getDomainScene(
                                                        domainName,
                                                        () -> AppNavigator.setScene(
                                                                UserDash.getUserDashScene())))))));
    }
}