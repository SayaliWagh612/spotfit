package com.eklavya.spotfit.view.common;

import com.eklavya.spotfit.navigation.AppNavigator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class WelcomePage {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";

    private Scene homePageScene;

    public Scene getScene() {

        /*
         * If scene is already created, reuse it.
         */
        if (homePageScene != null) {
            return homePageScene;
        }

        // =====================================================
        // LOGO
        // =====================================================

        Text logo = new Text("●  SpotFit");

        logo.setStyle(
                "-fx-fill:" + PRIMARY + ";" +
                " -fx-font-size:26px;" +
                " -fx-font-weight:bold;"
        );

        // =====================================================
        // TOP BUTTONS
        // =====================================================

        Button topStart =
                button("Get Started", true);

        Button topGuest =
                button("Explore as Guest", false);

        /*
         * IMPORTANT:
         * Keep a separate About button reference so we can
         * connect it to AboutUs.
         */
        Button topAbout =
                button("About", false);

                // Button topExplore =
                //         button("Explore", false);

        Button topHelp =
                button("Help", false);

        // =====================================================
        // NAVIGATION
        // =====================================================

        HBox nav =
                new HBox(
                        25,
                     //   topExplore,
                        topAbout,
                        topHelp
                );

        nav.setAlignment(
                Pos.CENTER
        );

        HBox actions =
                new HBox(
                        15,
                        topGuest,
                        topStart
                );

        actions.setAlignment(
                Pos.CENTER
        );

        // =====================================================
        // HEADER SPACERS
        // =====================================================

        Region r1 =
                new Region();

        Region r2 =
                new Region();

        HBox.setHgrow(
                r1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                r2,
                Priority.ALWAYS
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox(
                        20,
                        logo,
                        r1,
                        nav,
                        r2,
                        actions
                );

        header.setAlignment(
                Pos.CENTER
        );

        header.setPadding(
                new Insets(
                        22,
                        40,
                        22,
                        40
                )
        );

        header.setStyle(
                "-fx-background-color:" + BACKGROUND + ";" +
                " -fx-border-color:transparent transparent #2b253c transparent;"
        );

        // =====================================================
        // TOP GUEST BUTTON
        // =====================================================

        Runnable openGuest =
                () -> AppNavigator.setScene(
                        com.eklavya.spotfit.view.user.UserDash
                                .getUserDashScene()
                );

        topGuest.setOnAction(
                event -> openGuest.run()
        );

        // =====================================================
        // TOP GET STARTED
        // =====================================================

        topStart.setOnAction(event -> {

            SelectRole chooseLogin =
                    new SelectRole();

            Runnable back =
                    () -> backToHomePage();

            AppNavigator.setScene(
                    chooseLogin.getChooseLoginPageScene(
                            back
                    )
            );
        });

        // =====================================================
        // ABOUT BUTTON
        // =====================================================
        //
        // Clicking About opens AboutUs.
        //
        // When AboutUs clicks Back, it will execute
        // this callback and return to WelcomePage.
        // =====================================================

        topAbout.setOnAction(event -> {

            AboutUs aboutUs =
                    new AboutUs();

            Runnable backToWelcome =
                    () -> backToHomePage();

            AppNavigator.setScene(
                    aboutUs.getAboutUsScene(
                            backToWelcome
                    )
            );
        });

        // =====================================================
        // HERO CHIP
        // =====================================================

        Text chip =
                new Text(
                        "✦  Premium Spaces Available"
                );

        chip.setStyle(
                "-fx-fill:" + SECONDARY_TEXT + ";" +
                " -fx-font-size:13px;"
        );

        // =====================================================
        // HERO TITLE
        // =====================================================

        Text title =
                new Text(
                        "Find the perfect\n" +
                        "space for what you\n" +
                        "want to do."
                );

        title.setStyle(
                "-fx-fill:" + TEXT + ";" +
                " -fx-font-size:48px;" +
                " -fx-font-weight:bold;"
        );

        // =====================================================
        // HERO DESCRIPTION
        // =====================================================

        Text para =
                new Text(
                        "Whether you're looking for an inspiring studio, a\n" +
                        "serene wellness room, or a professional workspace,\n" +
                        "SpotFit connects you with curated environments\n" +
                        "designed for focus and flow."
                );

        para.setStyle(
                "-fx-fill:" + SECONDARY_TEXT + ";" +
                " -fx-font-size:18px;"
        );

        // =====================================================
        // HERO BUTTONS
        // =====================================================

        Button start =
                button(
                        "Get Started   →",
                        true
                );

        Button guest =
                button(
                        "Explore as Guest",
                        false
                );

        start.setPrefSize(
                220,
                55
        );

        guest.setPrefSize(
                220,
                55
        );

        // =====================================================
        // HERO GUEST
        // =====================================================

        guest.setOnAction(
                event -> openGuest.run()
        );

        // =====================================================
        // HERO GET STARTED
        // =====================================================

        start.setOnAction(event -> {

            SelectRole chooseLogin =
                    new SelectRole();

            Runnable back =
                    () -> backToHomePage();

            AppNavigator.setScene(
                    chooseLogin.getChooseLoginPageScene(
                            back
                    )
            );
        });

        // =====================================================
        // CATEGORIES
        // =====================================================

        Text categories =
                new Text(
                        "◉  Creativity    •    ♨  Wellness    •    " +
                        "◍  Sports    •    ▣  Work"
                );

        categories.setStyle(
                "-fx-fill:" + PRIMARY + ";" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        // =====================================================
        // LEFT HERO SECTION
        // =====================================================

        HBox heroButtons =
                new HBox(
                        18,
                        start,
                        guest
                );

        heroButtons.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox left =
                new VBox(
                        25,
                        chip,
                        title,
                        para,
                        heroButtons,
                        categories
                );

        left.setAlignment(
                Pos.CENTER_LEFT
        );

        left.setPrefWidth(
                570
        );

        // =====================================================
        // IMAGE GRID
        // =====================================================

        GridPane images =
                new GridPane();

        images.setHgap(15);
        images.setVgap(15);

        images.add(
                card(
                        "/assets/images/creative.png",
                        "✦  Creative Studios"
                ),
                0,
                0
        );

        images.add(
                card(
                        "/assets/images/wellness.png",
                        "♨  Wellness Rooms"
                ),
                1,
                0
        );

        images.add(
                card(
                        "/assets/images/sports.png",
                        "◍  Premium Courts"
                ),
                0,
                1
        );

        images.add(
                card(
                        "/assets/images/work.png",
                        "▣  Workspaces"
                ),
                1,
                1
        );

        // =====================================================
        // HERO
        // =====================================================

        HBox hero =
                new HBox(
                        45,
                        left,
                        images
                );

        hero.setAlignment(
                Pos.CENTER
        );

        hero.setPadding(
                new Insets(40)
        );

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root =
                new BorderPane(
                        hero
                );

        root.setTop(
                header
        );

        root.setStyle(
                "-fx-background-color:" +
                BACKGROUND +
                ";"
        );

        // =====================================================
        // SCREEN SIZE
        // =====================================================

        double w =
                (
                        AppNavigator.getPrimaryStage() != null
                        &&
                        AppNavigator.getPrimaryStage().getWidth() > 0
                )
                ?
                AppNavigator.getPrimaryStage().getWidth()
                :
                1280;

        double h =
                (
                        AppNavigator.getPrimaryStage() != null
                        &&
                        AppNavigator.getPrimaryStage().getHeight() > 0
                )
                ?
                AppNavigator.getPrimaryStage().getHeight()
                :
                800;

        // =====================================================
        // SCENE
        // =====================================================

        homePageScene =
                new Scene(
                        root,
                        w,
                        h
                );

        return homePageScene;
    }

    // =========================================================
    // BACK TO HOME
    // =========================================================

    public void backToHomePage() {

        AppNavigator.setScene(
                getScene()
        );
    }

    // =========================================================
    // BUTTON
    // =========================================================

    private Button button(
            String name,
            boolean fill
    ) {

        Button b =
                new Button(name);

        b.setStyle(
                "-fx-background-color:" +
                        (fill
                                ? PRIMARY
                                : SURFACE) +
                        ";" +

                "-fx-text-fill:" +
                        (fill
                                ? "white"
                                : TEXT) +
                        ";" +

                "-fx-font-size:14px;" +

                "-fx-font-weight:bold;" +

                "-fx-background-radius:12px;" +

                "-fx-padding:11px 22px;" +

                "-fx-border-color:" +
                        (fill
                                ? PRIMARY
                                : "#e4d8d1") +
                        ";" +

                "-fx-border-radius:12px;"
        );

        return b;
    }

    // =========================================================
    // IMAGE CARD
    // =========================================================

    private StackPane card(
            String path,
            String name
    ) {

        ImageView img =
                new ImageView();

        try {

            java.net.URL res =
                    WelcomePage.class.getResource(path);

            if (res != null) {

                img.setImage(
                        new Image(
                                res.toExternalForm()
                        )
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "[WelcomePage] Image load error: "
                            + path
            );
        }

        img.setFitWidth(
                315
        );

        img.setFitHeight(
                270
        );

        img.setPreserveRatio(
                false
        );

        // =====================================================
        // IMAGE LABEL
        // =====================================================

        Text label =
                new Text(name);

        label.setStyle(
                "-fx-fill:white;" +
                " -fx-font-size:16px;" +
                " -fx-font-weight:bold;"
        );

        // =====================================================
        // IMAGE BOX
        // =====================================================

        StackPane box =
                new StackPane(
                        img,
                        label
                );

        StackPane.setAlignment(
                label,
                Pos.BOTTOM_LEFT
        );

        StackPane.setMargin(
                label,
                new Insets(18)
        );

        // =====================================================
        // ROUNDED CLIP
        // =====================================================

        Rectangle clip =
                new Rectangle(
                        315,
                        270
                );

        clip.setArcWidth(
                28
        );

        clip.setArcHeight(
                28
        );

        box.setClip(
                clip
        );

        // =====================================================
        // SHADOW
        // =====================================================

        box.setStyle(
                "-fx-effect:dropshadow(" +
                        "gaussian," +
                        "rgba(0,0,0,0.5)," +
                        "20," +
                        "0.2," +
                        "0," +
                        "5);"
        );

        return box;
    }
}