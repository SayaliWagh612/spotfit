// package com.eklavya.spotfit.view.user;

// import com.eklavya.spotfit.view.common.WelcomePage;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.GridPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.VBox;

// import javafx.scene.image.ImageView;
// import javafx.scene.layout.StackPane;
// import javafx.scene.paint.Color;
// import javafx.scene.paint.LinearGradient;
// import javafx.scene.paint.Stop;
// import javafx.scene.shape.Rectangle;

// import static com.eklavya.spotfit.util.StyleConstants.*;
// import static com.eklavya.spotfit.view.user.CustomerUi.*;

// public class DomainPage {

//         // ============================================================
//         // MAIN SCENE
//         // ============================================================

//         public static Scene getDomainScene(
//                         String domainName,
//                         Runnable backAction) {

//                 BorderPane root = new BorderPane();

//                 root.setStyle(
//                                 "-fx-background-color: " + BG_PAGE + ";");

//                 // --------------------------------------------------------
//                 // TOP BAR
//                 // --------------------------------------------------------

//                 HBox topBar = buildTopBar(
//                                 domainName,
//                                 backAction);

//                 // --------------------------------------------------------
//                 // CONTENT
//                 // --------------------------------------------------------

//                 VBox content = buildContent(domainName);

//                 ScrollPane scroll = new ScrollPane(content);

//                 scroll.setFitToWidth(true);

//                 scroll.setHbarPolicy(
//                                 ScrollPane.ScrollBarPolicy.NEVER);

//                 scroll.setVbarPolicy(
//                                 ScrollPane.ScrollBarPolicy.AS_NEEDED);

//                 scroll.setStyle(
//                                 "-fx-background-color: transparent;" +
//                                                 "-fx-background: transparent;" +
//                                                 "-fx-border-color: transparent;");

//                 root.setTop(topBar);
//                 root.setCenter(scroll);

//                 return new Scene(root, 1280, 800);
//         }

//         // ============================================================
//         // TOP BAR
//         // ============================================================

//         private static HBox buildTopBar(
//                         String domainName,
//                         Runnable backAction) {

//                 HBox bar = new HBox(20);

//                 bar.setAlignment(
//                                 Pos.CENTER_LEFT);

//                 bar.setPadding(
//                                 new Insets(15, 42, 15, 42));

//                 bar.setStyle(
//                                 "-fx-background-color: " + CARD_WHITE + ";" +
//                                                 "-fx-border-color: " + BORDER + ";" +
//                                                 "-fx-border-width: 0 0 1 0;");

//                 // --------------------------------------------------------
//                 // BACK
//                 // --------------------------------------------------------

//                 Button back = new Button("←  Back");

//                 back.setStyle(
//                                 "-fx-background-color: transparent;" +
//                                                 "-fx-text-fill: " + TEXT_MUTED + ";" +
//                                                 "-fx-font-family: " + FONT + ";" +
//                                                 "-fx-font-size: 12px;" +
//                                                 "-fx-cursor: hand;");

//                 back.setOnAction(
//                                 e -> backAction.run());

//                 // --------------------------------------------------------
//                 // TITLE
//                 // --------------------------------------------------------

//                 Label title = new Label(
//                                 domainName);

//                 title.setStyle(
//                                 "-fx-font-family: " + FONT + ";" +
//                                                 "-fx-font-size: 20px;" +
//                                                 "-fx-font-weight: bold;" +
//                                                 "-fx-text-fill: " + TEXT_DARK + ";");

//                 // --------------------------------------------------------
//                 // SPACER
//                 // --------------------------------------------------------

//                 Region spacer = new Region();

//                 HBox.setHgrow(
//                                 spacer,
//                                 Priority.ALWAYS);

//                 // --------------------------------------------------------
//                 // PROFILE
//                 // --------------------------------------------------------

//                 // Button profile = new Button("●");

//                 // profile.setStyle(
//                 //                 "-fx-background-color: " + PILL_BG + ";" +
//                 //                                 "-fx-background-radius: 20;" +
//                 //                                 "-fx-font-size: 13px;" +
//                 //                                 "-fx-text-fill: " + TEXT_DARK + ";" +
//                 //                                 "-fx-cursor: hand;");

//                 bar.getChildren().addAll(
//                                 back,
//                                 title,
//                                 spacer);

//                 return bar;
//         }

//         // ============================================================
//         // CONTENT
//         // ============================================================

//         private static VBox buildContent(
//                         String domainName) {

//                 VBox content = new VBox(28);

//                 content.setFillWidth(true);

//                 content.setPadding(
//                                 new Insets(35, 55, 60, 55));

//                 // --------------------------------------------------------
//                 // HERO
//                 // --------------------------------------------------------

//                 VBox hero = new VBox(7);

//                 Label title = new Label(
//                                 "Explore " + domainName);

//                 title.setStyle(
//                                 "-fx-font-family: " + FONT + ";" +
//                                                 "-fx-font-size: 29px;" +
//                                                 "-fx-font-weight: bold;" +
//                                                 "-fx-text-fill: " + TEXT_DARK + ";");

//                 Label subtitle = new Label(
//                                 "Choose what you want to do.");

//                 subtitle.setStyle(
//                                 "-fx-font-family: " + FONT + ";" +
//                                                 "-fx-font-size: 13px;" +
//                                                 "-fx-text-fill: " + TEXT_MUTED + ";");

//                 hero.getChildren().addAll(
//                                 title,
//                                 subtitle);

//                 // --------------------------------------------------------
//                 // SECTION TITLE
//                 // --------------------------------------------------------

//                 Label sectionTitle = new Label(
//                                 "Explore by activity");

//                 sectionTitle.setStyle(
//                                 heading(17));

//                 // --------------------------------------------------------
//                 // ACTIVITY GRID
//                 // --------------------------------------------------------

//                 GridPane grid = buildActivityGrid(
//                                 domainName);

//                 content.getChildren().addAll(
//                                 hero,
//                                 sectionTitle,
//                                 grid);

//                 return content;
//         }

//         // ============================================================
//         // ACTIVITY GRID
//         // ============================================================

//         private static GridPane buildActivityGrid(
//                         String domainName) {

//                 GridPane grid = new GridPane();

//                 grid.setHgap(18);

//                 grid.setVgap(18);

//                 grid.setAlignment(
//                                 Pos.CENTER);

//                 /*
//                  * For now we use mock/static data.
//                  *
//                  * Later this will come from Firebase.
//                  */

//                 String[] activities = getActivitiesForDomain(domainName);

//                 for (int i = 0; i < activities.length; i++) {

//                         String activity = activities[i];

//                         StackPane card = activityCard(domainName, activity);

//                         int column = i % 2;

//                         int row = i / 2;

//                         grid.add(
//                                         card,
//                                         column,
//                                         row);

//                         GridPane.setHgrow(
//                                         card,
//                                         Priority.ALWAYS);
//                 }

//                 return grid;
//         }

//         // ============================================================
//         // ACTIVITY CARD
//         // ============================================================

//         private static StackPane activityCard(String domainName, String activity) {
//         StackPane card = new StackPane();
//         card.setMinHeight(160);
//         card.setPrefHeight(180);
//         card.setMaxWidth(Double.MAX_VALUE);
//         card.setStyle("-fx-background-radius: 16; -fx-cursor: hand;");

//         ImageView bg = resolveSpaceCover(null, "Creativity".equals(domainName) ? activity : null);
//         bg.setFitHeight(180);
//         bg.setPreserveRatio(false);

//         // Clip rounded corners
//         Rectangle clip = new Rectangle(0, 0, 400, 180);
//         clip.widthProperty().bind(card.widthProperty());
//         clip.setArcWidth(32);
//         clip.setArcHeight(32);
//         card.setClip(clip);

//         // Bind image width to fill
//         bg.fitWidthProperty().bind(card.widthProperty());

//         Rectangle gradient = new Rectangle();
//         gradient.widthProperty().bind(card.widthProperty());
//         gradient.heightProperty().bind(card.heightProperty());
//         gradient.setFill(new LinearGradient(0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
//                 new Stop(0, Color.TRANSPARENT),
//                 new Stop(0.5, Color.color(0, 0, 0, 0.4)),
//                 new Stop(1, Color.color(0, 0, 0, 0.8))));

//         VBox content = new VBox(6);
//         content.setAlignment(Pos.BOTTOM_LEFT);
//         content.setPadding(new Insets(20));

//         Label title = new Label(activity);
//         title.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

//         Label subtitle = new Label("Explore spaces →");
//         subtitle.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: white;");

//         content.getChildren().addAll(title, subtitle);

//         card.getChildren().addAll(bg, gradient, content);

//         // Subtle hover
//         card.setOnMouseEntered(e -> {
//             card.setTranslateY(-4);
//             card.setStyle("-fx-background-radius: 16; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);");
//         });
//         card.setOnMouseExited(e -> {
//             card.setTranslateY(0);
//             card.setStyle("-fx-background-radius: 16; -fx-cursor: hand;");
//         });

//                 // --------------------------------------------------------
//                 // CLICK
//                 // --------------------------------------------------------

//                 card.setOnMouseClicked(
//                                 e -> openActivityPage(
//                                                 domainName,
//                                                 activity));

//                 return card;
//         }

//         // ============================================================
//         // TEMPORARY ACTIVITY DATA
//         // ============================================================

//         private static String[] getActivitiesForDomain(
//                         String domainName) {

//         switch (domainName) {
//             case "Creativity":
//                 return new String[] {
//                         "Art",
//                         "Music",
//                         "Podcasts",
//                         "Content Creation"
//                 };

//                         case "Sports":

//                                 return new String[] {
//                                                 "Turfs",
//                                                 "Courts",
//                                                 "Gaming Zones",
//                                                 "Adventure Activities"
//                                 };

//                         case "Wellness":

//                                 return new String[] {
//                                                 "Yoga",
//                                                 "Zumba",
//                                                 "Gyms",
//                                                 "Meditation",
//                                                 "Spas"
//                                 };

//                         case "Workspaces":

//                                 return new String[] {
//                                                 "Study Libraries",
//                                                 "Work Cafes",
//                                                 "Silent Working Areas",
//                                                 "Co-working Spaces"
//                                 };

//                         default:

//                                 return new String[] {};
//                 }
//         }

//         // ============================================================
//         // ACTIVITY NAVIGATION
//         // ============================================================

//         private static void openActivityPage(
//                         String domainName,
//                         String activity) {

//                 com.eklavya.spotfit.navigation.AppNavigator.setScene(
//                                 ActivityPage.getActivityScene(
//                                                 domainName,
//                                                 activity,
//                                                 () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
//                                                                 DomainPage.getDomainScene(
//                                                                                 domainName,
//                                                                                 () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
//                                                                                                 Explore.getExploreScene(
//                                                                                                                 () -> com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage()
//                                                                                                                                 .setScene(
//                                                                                                                                                 UserDash.getUserDashScene())))))));
//         }
// }
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.user.CustomerUi.resolveSpaceCover;

public class DomainPage {

        private static final double CARD_WIDTH = 500;
        private static final double CARD_HEIGHT = 250;
        private static final double CONTENT_WIDTH = 1060;

        public static Scene getDomainScene(String domainName, Runnable backAction) {
                BorderPane root = new BorderPane();
                root.setStyle("-fx-background-color: " + BG_PAGE + ";");
                root.setTop(buildTopBar(domainName, backAction));

                VBox content = buildContent(domainName);
                StackPane contentWrapper = new StackPane(content);
                contentWrapper.setAlignment(Pos.TOP_CENTER);
                contentWrapper.setStyle("-fx-background-color: transparent;");

                ScrollPane scroll = new ScrollPane(contentWrapper);
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

        private static HBox buildTopBar(String domainName, Runnable backAction) {
                HBox bar = new HBox(20);
                bar.setAlignment(Pos.CENTER_LEFT);
                bar.setPadding(new Insets(15, 42, 15, 42));
                bar.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-width: 0 0 1 0;");

                Button back = new Button("←  Back");
                String normalBackStyle = "-fx-background-color: transparent;" +
                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                "-fx-font-family: " + FONT + ";" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 8 12;" +
                                "-fx-cursor: hand;";
                String hoverBackStyle = "-fx-background-color: " + PILL_BG + ";" +
                                "-fx-background-radius: 10;" +
                                "-fx-text-fill: " + PRIMARY + ";" +
                                "-fx-font-family: " + FONT + ";" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 8 12;" +
                                "-fx-cursor: hand;";
                back.setStyle(normalBackStyle);
                back.setOnMouseEntered(e -> back.setStyle(hoverBackStyle));
                back.setOnMouseExited(e -> back.setStyle(normalBackStyle));
                back.setOnAction(e -> backAction.run());

                Label title = new Label(domainName);
                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 21px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                bar.getChildren().addAll(back, title, spacer);
                return bar;
        }

        private static VBox buildContent(String domainName) {
                VBox content = new VBox(28);
                content.setMaxWidth(CONTENT_WIDTH);
                content.setFillWidth(true);
                content.setPadding(new Insets(36, 0, 60, 0));

                VBox hero = new VBox(7);
                Label title = new Label("Explore " + domainName);
                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 31px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label subtitle = new Label(getDomainDescription(domainName));
                subtitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 14px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");
                hero.getChildren().addAll(title, subtitle);

                HBox sectionHeader = new HBox();
                sectionHeader.setAlignment(Pos.CENTER_LEFT);
                Label sectionTitle = new Label("Choose an activity");
                sectionTitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 19px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label count = new Label(getActivitiesForDomain(domainName).length + " activities");
                count.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 20;" +
                                                "-fx-padding: 7 12;");
                sectionHeader.getChildren().addAll(sectionTitle, spacer, count);

                content.getChildren().addAll(hero, sectionHeader, buildActivityGrid(domainName));
                return content;
        }

        private static GridPane buildActivityGrid(String domainName) {
                GridPane grid = new GridPane();
                grid.setHgap(22);
                grid.setVgap(22);
                grid.setAlignment(Pos.TOP_CENTER);

                ColumnConstraints left = fixedColumn();
                ColumnConstraints right = fixedColumn();
                grid.getColumnConstraints().addAll(left, right);

                String[] activities = getActivitiesForDomain(domainName);
                for (int i = 0; i < activities.length; i++) {
                        grid.add(activityCard(domainName, activities[i]), i % 2, i / 2);
                }
                return grid;
        }

        private static ColumnConstraints fixedColumn() {
                ColumnConstraints column = new ColumnConstraints();
                column.setMinWidth(CARD_WIDTH);
                column.setPrefWidth(CARD_WIDTH);
                column.setMaxWidth(CARD_WIDTH);
                return column;
        }

        private static StackPane activityCard(String domainName, String activity) {
                StackPane card = new StackPane();
                card.setMinSize(CARD_WIDTH, CARD_HEIGHT);
                card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
                card.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
                card.setStyle("-fx-background-color: #E8D8CA; -fx-background-radius: 20; -fx-cursor: hand;");

                ImageView imageView = getLocalActivityImage(domainName, activity);
                imageView.setFitWidth(CARD_WIDTH);
                imageView.setFitHeight(CARD_HEIGHT);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);

                Rectangle clip = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
                clip.setArcWidth(40);
                clip.setArcHeight(40);
                card.setClip(clip);

                Rectangle gradient = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
                gradient.setArcWidth(40);
                gradient.setArcHeight(40);
                gradient.setFill(new LinearGradient(
                                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                                new Stop(0, Color.color(0, 0, 0, 0.04)),
                                new Stop(0.45, Color.color(0, 0, 0, 0.12)),
                                new Stop(1, Color.color(0, 0, 0, 0.82))));

                Label badge = new Label(getActivityBadge(activity));
                badge.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.94);" +
                                                "-fx-background-radius: 20;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 7 12;");
                StackPane.setAlignment(badge, Pos.TOP_LEFT);
                StackPane.setMargin(badge, new Insets(15));

                VBox text = new VBox(5);
                text.setAlignment(Pos.BOTTOM_LEFT);
                text.setPadding(new Insets(20));
                Label cardTitle = new Label(activity);
                cardTitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 24px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: white;");
                Label cardSubtitle = new Label(getActivitySubtitle(activity));
                cardSubtitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-text-fill: rgba(255,255,255,0.92);");
                Label explore = new Label("Explore spaces  →");
                explore.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-padding: 5 0 0 0;");
                text.getChildren().addAll(cardTitle, cardSubtitle, explore);
                StackPane.setAlignment(text, Pos.BOTTOM_LEFT);

                card.getChildren().addAll(imageView, gradient, badge, text);
                addShadow(card, 0.15);

                card.setOnMouseEntered(e -> animateCard(card, 1.025, 0.25));
                card.setOnMouseExited(e -> animateCard(card, 1.0, 0.15));
                card.setOnMouseClicked(e -> openActivityPage(domainName, activity));
                return card;
        }

        private static void animateCard(StackPane card, double scale, double shadowOpacity) {
                ScaleTransition transition = new ScaleTransition(Duration.millis(150), card);
                transition.setToX(scale);
                transition.setToY(scale);
                transition.play();
                addShadow(card, shadowOpacity);
        }

        private static void addShadow(Region node, double opacity) {
                DropShadow shadow = new DropShadow();
                shadow.setRadius(18);
                shadow.setOffsetY(6);
                shadow.setColor(Color.color(0.23, 0.14, 0.09, opacity));
                node.setEffect(shadow);
        }

        private static ImageView getLocalActivityImage(String domainName, String activity) {
                if ("Creativity".equalsIgnoreCase(domainName)) {
                        return resolveSpaceCover(null, activity.replace(' ', '_'));
                }

                String path = switch (domainName) {
                        case "Sports" -> "/images/UserDashImg/Sports.png";
                        case "Wellness" -> "/images/UserDashImg/Wellness.png";
                        case "Workspaces" -> "/images/UserDashImg/Workplaces.png";
                        default -> "/images/UserDashImg/Creativity.png";
                };

                try (InputStream stream = DomainPage.class.getResourceAsStream(path)) {
                        if (stream != null) {
                                return new ImageView(new Image(stream));
                        }
                } catch (Exception ignored) {
                        // CustomerUi provides a safe local fallback below.
                }
                return resolveSpaceCover(null, null);
        }

        private static String getDomainDescription(String domainName) {
                return switch (domainName) {
                        case "Creativity" -> "Studios, workshops and spaces that help ideas come alive.";
                        case "Sports" -> "Discover turfs, courts and activity spaces for your next game.";
                        case "Wellness" -> "Find spaces designed for movement, recovery and calm.";
                        case "Workspaces" -> "Flexible study spots and professional workspaces.";
                        default -> "Discover spaces and experiences made for you.";
                };
        }

        private static String getActivityBadge(String activity) {
                return switch (activity) {
                        case "Art", "Music", "Podcasts", "Content Creation" -> "CREATIVITY";
                        case "Turfs", "Courts", "Gaming Zones", "Adventure Activities" -> "SPORTS · PROTOTYPE";
                        case "Yoga", "Zumba", "Gyms", "Meditation", "Spas" -> "WELLNESS · PROTOTYPE";
                        default -> "WORKSPACES · PROTOTYPE";
                };
        }

        private static String getActivitySubtitle(String activity) {
                return switch (activity) {
                        case "Art" -> "Studios for painting, pottery and creative workshops";
                        case "Music" -> "Practice rooms, recording and music workshops";
                        case "Podcasts" -> "Recording-ready podcast studios and equipment";
                        case "Content Creation" -> "Photography, video and creator studios";
                        case "Turfs" -> "Outdoor spaces for team games and practice";
                        case "Courts" -> "Indoor and outdoor courts for active play";
                        case "Gaming Zones" -> "Social gaming spaces and experiences";
                        case "Adventure Activities" -> "Spaces for active outdoor experiences";
                        case "Yoga" -> "Studios for guided practice and mindful movement";
                        case "Zumba" -> "Energetic group dance and fitness studios";
                        case "Gyms" -> "Fitness spaces with training equipment";
                        case "Meditation" -> "Quiet spaces for focus and relaxation";
                        case "Spas" -> "Comfortable spaces for wellness and recovery";
                        case "Study Libraries" -> "Quiet reading and focused study environments";
                        case "Work Cafes" -> "Casual workspaces for productive sessions";
                        case "Silent Working Areas" -> "Distraction-free spaces for deep work";
                        case "Co-working Spaces" -> "Flexible desks and collaborative workplaces";
                        default -> "Discover spaces suited to this activity";
                };
        }

        private static String[] getActivitiesForDomain(String domainName) {
                return switch (domainName) {
                        case "Creativity" -> new String[] { "Art", "Music", "Podcasts", "Content Creation" };
                        case "Sports" -> new String[] { "Turfs", "Courts", "Gaming Zones", "Adventure Activities" };
                        case "Wellness" -> new String[] { "Yoga", "Zumba", "Gyms", "Meditation", "Spas" };
                        case "Workspaces" -> new String[] {
                                        "Study Libraries", "Work Cafes", "Silent Working Areas", "Co-working Spaces"
                        };
                        default -> new String[] {};
                };
        }

        private static void openActivityPage(String domainName, String activity) {
                AppNavigator.setScene(
                                ActivityPage.getActivityScene(
                                                domainName,
                                                activity,
                                                () -> AppNavigator.setScene(
                                                                DomainPage.getDomainScene(
                                                                                domainName,
                                                                                () -> AppNavigator.setScene(
                                                                                                Explore.getExploreScene(
                                                                                                                () -> AppNavigator
                                                                                                                                .setScene(
                                                                                                                                                UserDash.getUserDashScene())))))));
        }
}