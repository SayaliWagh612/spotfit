package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.view.owner.SignInPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HomePage {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";
    private static final String CHIP_BG = "#ffeade";

    private Scene homePageScene;

    public Scene getScene() {
        if (homePageScene != null) {
            return homePageScene;
        }

        Label logo = new Label("SpotFit");
        logo.setStyle("-fx-font-size:24px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");

        Label signInLink = new Label("Already have access?  Sign In");
        signInLink.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + "; -fx-underline:true; -fx-cursor:hand;");
        signInLink.setOnMouseClicked(event -> goToLoginPage());

        Label adminLink = new Label("🛡 Platform Admin Portal");
        adminLink.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#0f172a; -fx-underline:true; -fx-cursor:hand; -fx-padding:0 0 0 20;");
        adminLink.setOnMouseClicked(event -> goToAdminLoginPage());

        HBox header = new HBox(logo, spacer(), signInLink, adminLink);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 64, 24, 64));

        Label label = new Label("SPACE MANAGEMENT PLATFORM");
        label.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");

        Label headline = new Label("Manage Your Space.\nDeliver Better Experiences.");
        headline.setStyle("-fx-font-size:38px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        Label description = new Label(
                "You are a Space Manager. Space Manager accounts are created through an invitation "
                + "from a SpotFit business owner. Once activated, you can manage bookings, "
                + "check-ins, and daily operations for your assigned space."
        );
        description.setWrapText(true);
        description.setStyle("-fx-font-size:15px; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-line-spacing:3px;");

        VBox checklist = new VBox(10,
                createChecklistItem("Operate your assigned spaces"),
                createChecklistItem("Review and manage bookings"),
                createChecklistItem("Track customers and check-ins"),
                createChecklistItem("Work alongside your team")
        );

        VBox leftColumn = new VBox(16, label, headline, description, checklist);
        leftColumn.setMaxWidth(520);

        VBox ctaCard = createCtaCard();

        HBox body = new HBox(56, leftColumn, ctaCard);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(24, 64, 64, 64));

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox contentBox = new VBox(header, body);
        contentBox.setStyle("-fx-background-color:" + BACKGROUND + ";");

        ScrollPane root = new ScrollPane(contentBox);
        root.setFitToWidth(true);
        root.setFitToHeight(true);
        root.setStyle("-fx-background-color:" + BACKGROUND + "; -fx-background:" + BACKGROUND + ";");

        Stage stage = AppNavigator.getPrimaryStage();
        double w = stage != null ? stage.getWidth() : 1280;
        double h = stage != null ? stage.getHeight() : 800;

        homePageScene = new Scene(root, w, h);
        return homePageScene;
    }

    private VBox createCtaCard() {
        Label title = new Label("Welcome, Manager");
        title.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        Label body = new Label(
                "Sign in to manage your assigned SpotFit spaces, or register using the invitation link sent to you by a space owner."
        );
        body.setWrapText(true);
        body.setStyle("-fx-font-size:14px; -fx-text-fill:" + SECONDARY_TEXT + ";");

        Button registerButton = new Button("Register with Invitation");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(46);
        registerButton.setStyle(
                "-fx-background-color:" + PRIMARY + "; -fx-text-fill:white; -fx-font-size:14px; "
                + "-fx-font-weight:bold; -fx-background-radius:12px;"
        );
        registerButton.setOnAction(event -> goToRegistrationPage());

        Button loginButton = new Button("Existing Manager? Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(44);
        loginButton.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:" + PRIMARY + "; -fx-font-size:14px; "
                + "-fx-font-weight:bold; -fx-border-color:" + PRIMARY + "; -fx-border-width:1.5px; -fx-border-radius:12px;"
        );
        loginButton.setOnAction(event -> goToLoginPage());

        Label infoTitle = new Label("Invitation Only");
        infoTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        Label infoBody = new Label("You must have an active invitation from a space owner to register here.");
        infoBody.setWrapText(true);
        infoBody.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");

        VBox infoBox = new VBox(4, infoTitle, infoBody);
        infoBox.setPadding(new Insets(16));
        infoBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:14px;");

        VBox card = new VBox(18, title, body, registerButton, loginButton, infoBox);
        card.setPadding(new Insets(32));
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color:" + SURFACE + "; -fx-background-radius:24px; "
                + "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 25, 0, 0, 8);"
        );

        return card;
    }

    private Label createChecklistItem(String text) {
        Label label = new Label("✔  " + text);
        label.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + ";");
        return label;
    }

    private Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private void forceFullScreen() {
        Stage stage = AppNavigator.getPrimaryStage();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        boolean alreadyFullSize = stage.isMaximized()
                && Math.abs(stage.getWidth() - bounds.getWidth()) < 2
                && Math.abs(stage.getHeight() - bounds.getHeight()) < 2;

        if (!alreadyFullSize) {
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            AppNavigator.setMaximized(true);
        }
    }

    private void goToRegistrationPage() {
        Registration registrationPage = new Registration();
        Runnable callbackHome = () -> backToHomePage();
        AppNavigator.setScene(registrationPage.getRegistrationPageScene(callbackHome));
        forceFullScreen();
    }

    private void goToLoginPage() {
        LoginPage loginPage = new LoginPage();
        Runnable callbackHome = () -> backToHomePage();
        AppNavigator.setScene(loginPage.getLoginPageScene(callbackHome));
        forceFullScreen();
    }

    private void goToAdminLoginPage() {
        com.eklavya.spotfit.view.admin.AdminLoginPage adminLoginPage =
                new com.eklavya.spotfit.view.admin.AdminLoginPage();

        Runnable callbackHome = () -> backToHomePage();

        AppNavigator.setScene(
                adminLoginPage.getAdminLoginPageScene(callbackHome)
        );

        forceFullScreen();
    }

    public void backToHomePage() {
        AppNavigator.setScene(getScene());
        forceFullScreen();
    }
}
