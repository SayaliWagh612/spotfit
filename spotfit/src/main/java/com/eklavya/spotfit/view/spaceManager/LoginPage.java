package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.controller.manager.ManagerAuthController;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.navigation.AppNavigator;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import javafx.util.Duration;


// ============================================================
// SpotFit - Space Manager Login
// ============================================================

public class LoginPage {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";
    private static final String BORDER = "#d9c2b4";

    private final ManagerAuthController authController =
            new ManagerAuthController();

    private Scene loginPageScene;


    // =========================================================
    // LOGIN PAGE
    // =========================================================

    public Scene getLoginPageScene(Runnable callbackHome) {

        // -----------------------------------------------------
        // Background Image
        // -----------------------------------------------------

        ImageView backgroundImage = new ImageView();

        try {

            Image image = new Image(
                    getClass()
                            .getResourceAsStream(
                                    "/images/manager-background.png"
                            )
            );

            backgroundImage.setImage(image);

        } catch (Exception e) {

            System.out.println(
                    "Manager login background image not found."
            );
        }

        backgroundImage.setPreserveRatio(false);
        backgroundImage.setSmooth(true);
        backgroundImage.setCache(true);


        // -----------------------------------------------------
        // Background Overlay
        // -----------------------------------------------------

        Region overlay = new Region();

        overlay.setStyle(
                "-fx-background-color:" +
                "linear-gradient(" +
                "to bottom right," +
                "rgba(30,18,10,0.25)," +
                "rgba(50,25,10,0.35)," +
                "rgba(20,12,7,0.30)" +
                ");"
        );


        // -----------------------------------------------------
        // Header
        // -----------------------------------------------------

        Label logo = new Label("SpotFit");

        logo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;" +
                "-fx-effect:dropshadow(" +
                "gaussian,rgba(0,0,0,0.45),8,0,0,2);"
        );


        Label backLink =
                new Label("←  Back to Welcome");

        backLink.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;" +
                "-fx-cursor:hand;" +
                "-fx-padding:8 15 8 15;" +
                "-fx-background-color:rgba(255,255,255,0.16);" +
                "-fx-background-radius:20px;" +
                "-fx-border-color:rgba(255,255,255,0.35);" +
                "-fx-border-radius:20px;"
        );


        backLink.setOnMouseEntered(event -> {

            backLink.setStyle(
                    "-fx-font-size:13px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-text-fill:white;" +
                    "-fx-cursor:hand;" +
                    "-fx-padding:8 15 8 15;" +
                    "-fx-background-color:rgba(255,255,255,0.28);" +
                    "-fx-background-radius:20px;" +
                    "-fx-border-color:white;" +
                    "-fx-border-radius:20px;"
            );
        });


        backLink.setOnMouseExited(event -> {

            backLink.setStyle(
                    "-fx-font-size:13px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-text-fill:white;" +
                    "-fx-cursor:hand;" +
                    "-fx-padding:8 15 8 15;" +
                    "-fx-background-color:rgba(255,255,255,0.16);" +
                    "-fx-background-radius:20px;" +
                    "-fx-border-color:rgba(255,255,255,0.35);" +
                    "-fx-border-radius:20px;"
            );
        });


        backLink.setOnMouseClicked(
                event -> callbackHome.run()
        );


        HBox header =
                new HBox(
                        logo,
                        spacer(),
                        backLink
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(24, 55, 20, 55)
        );


        // -----------------------------------------------------
        // Login Labels
        // -----------------------------------------------------

        Label label =
                new Label("SPACE MANAGER");

        label.setStyle(
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";"
        );


        Label title =
                new Label("Welcome Back");

        title.setStyle(
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + TEXT + ";"
        );


        Label subtitle =
                new Label(
                        "Sign in to continue managing your assigned SpotFit spaces."
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );


        // -----------------------------------------------------
        // Email Field
        // -----------------------------------------------------

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "Enter Email"
        );

        emailField.setPrefHeight(46);

        emailField.setStyle(
                "-fx-background-color:#fffdfc;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12px;" +
                "-fx-padding:10 14;"
        );


        // -----------------------------------------------------
        // Password Field
        // -----------------------------------------------------

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Enter your password"
        );

        passwordField.setPrefHeight(46);

        passwordField.setStyle(
                "-fx-background-color:#fffdfc;" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-radius:12px;" +
                "-fx-padding:10 14;"
        );


        // -----------------------------------------------------
        // Email Focus Style
        // -----------------------------------------------------

        emailField.focusedProperty().addListener(
                (obs, oldValue, focused) -> {

                    if (focused) {

                        emailField.setStyle(
                                "-fx-background-color:white;" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + PRIMARY + ";" +
                                "-fx-border-width:1.5px;" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14;"
                        );

                    } else {

                        emailField.setStyle(
                                "-fx-background-color:#fffdfc;" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + BORDER + ";" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14;"
                        );
                    }
                }
        );


        // -----------------------------------------------------
        // Password Focus Style
        // -----------------------------------------------------

        passwordField.focusedProperty().addListener(
                (obs, oldValue, focused) -> {

                    if (focused) {

                        passwordField.setStyle(
                                "-fx-background-color:white;" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + PRIMARY + ";" +
                                "-fx-border-width:1.5px;" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14;"
                        );

                    } else {

                        passwordField.setStyle(
                                "-fx-background-color:#fffdfc;" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + BORDER + ";" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14;"
                        );
                    }
                }
        );


        // -----------------------------------------------------
        // Remember Me
        // -----------------------------------------------------

        CheckBox rememberMe =
                new CheckBox(
                        "Remember this device"
                );

        rememberMe.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );


        // -----------------------------------------------------
        // Error Label
        // -----------------------------------------------------

        Label errorLabel =
                new Label();

        errorLabel.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:#ba1a1a;" +
                "-fx-font-weight:bold;"
        );

        errorLabel.setVisible(false);


        // -----------------------------------------------------
        // Sign In Button
        // -----------------------------------------------------

        Button signInButton =
                new Button("Sign In →");

        signInButton.setMaxWidth(
                Double.MAX_VALUE
        );

        signInButton.setPrefHeight(46);

        signInButton.setStyle(
                "-fx-background-color:" + PRIMARY + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:12px;" +
                "-fx-cursor:hand;" +
                "-fx-effect:dropshadow(" +
                "gaussian,rgba(136,70,0,0.30),12,0,0,4);"
        );


        // -----------------------------------------------------
        // Button Hover
        // -----------------------------------------------------

        signInButton.setOnMouseEntered(event -> {

            signInButton.setStyle(
                    "-fx-background-color:#6d3600;" +
                    "-fx-text-fill:white;" +
                    "-fx-font-size:14px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-radius:12px;" +
                    "-fx-cursor:hand;" +
                    "-fx-effect:dropshadow(" +
                    "gaussian,rgba(136,70,0,0.45),18,0,0,5);"
            );

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(130),
                            signInButton
                    );

            scale.setToX(1.02);
            scale.setToY(1.02);

            scale.play();
        });


        signInButton.setOnMouseExited(event -> {

            signInButton.setStyle(
                    "-fx-background-color:" + PRIMARY + ";" +
                    "-fx-text-fill:white;" +
                    "-fx-font-size:14px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-radius:12px;" +
                    "-fx-cursor:hand;" +
                    "-fx-effect:dropshadow(" +
                    "gaussian,rgba(136,70,0,0.30),12,0,0,4);"
            );

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(130),
                            signInButton
                    );

            scale.setToX(1);
            scale.setToY(1);

            scale.play();
        });


        // -----------------------------------------------------
        // Login Action
        // -----------------------------------------------------

        signInButton.setOnAction(event -> {

            errorLabel.setVisible(false);

            if (
                    emailField.getText()
                            .trim()
                            .isEmpty()
                    ||
                    passwordField.getText()
                            .isEmpty()
            ) {

                errorLabel.setText(
                        "Please enter email and password."
                );

                errorLabel.setVisible(true);

                return;
            }


            signInButton.setDisable(true);


            authController.loginManager(
                    emailField.getText().trim(),
                    passwordField.getText()
            )
            .thenAccept(res ->
                    Platform.runLater(() -> {

                        signInButton.setDisable(false);

                        if (res.isFailure()) {

                            errorLabel.setText(
                                    res.getMessage()
                            );

                            errorLabel.setVisible(true);

                        } else {

                            Manager manager =
                                    res.getData();

                            Dashboard dashboardPage =
                                    new Dashboard();

                            Runnable callbackLogout =
                                    () -> backToLoginPage();


                            AppNavigator.setScene(
                                    dashboardPage
                                            .getDashboardPageScene(
                                                    manager.getName(),
                                                    callbackLogout
                                            )
                            );

                            AppNavigator.setMaximized(
                                    true
                            );
                        }
                    })
            );
        });


        // -----------------------------------------------------
        // Registration Link
        // -----------------------------------------------------

        HBox registerLinkRow =
                new HBox(6);

        registerLinkRow.setAlignment(
                Pos.CENTER
        );


        Label noAccount =
                new Label(
                        "Don't have a manager account?"
                );

        noAccount.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );


        Label registerLink =
                new Label(
                        "Register Here"
                );

        registerLink.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-cursor:hand;"
        );


        registerLink.setOnMouseEntered(
                e -> registerLink.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#6d3600;" +
                        "-fx-cursor:hand;"
                )
        );


        registerLink.setOnMouseExited(
                e -> registerLink.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + PRIMARY + ";" +
                        "-fx-cursor:hand;"
                )
        );


        registerLink.setOnMouseClicked(e -> {

            Registration regPage =
                    new Registration();

            AppNavigator.setScene(
                    regPage.getRegistrationPageScene(
                            () ->
                                    AppNavigator.setScene(
                                            loginPageScene
                                    )
                    )
            );
        });


        registerLinkRow.getChildren().addAll(
                noAccount,
                registerLink
        );


        // -----------------------------------------------------
        // Login Box
        // -----------------------------------------------------

        VBox loginBox =
                new VBox(
                        14,

                        label,
                        title,
                        subtitle,

                        new VBox(
                                6,
                                fieldLabel("Work Email"),
                                emailField
                        ),

                        new VBox(
                                6,
                                fieldLabel("Password"),
                                passwordField
                        ),

                        rememberMe,
                        errorLabel,
                        signInButton,
                        registerLinkRow
                );


        loginBox.setPadding(
                new Insets(36)
        );

        loginBox.setMaxWidth(420);

        loginBox.setStyle(
                "-fx-background-color:" +
                "rgba(255, 255, 255, 0.96);" +
                "-fx-background-radius:24px;" +
                "-fx-border-color:" +
                "rgba(255,255,255,0.75);" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:24px;" +
                "-fx-effect:dropshadow(" +
                "gaussian,rgba(0,0,0,0.28),30,0,0,10);"
        );


        // -----------------------------------------------------
        // Body
        // -----------------------------------------------------

        HBox body =
                new HBox(loginBox);

        body.setAlignment(
                Pos.CENTER
        );

        body.setPadding(
                new Insets(15, 60, 40, 60)
        );

        VBox.setVgrow(
                body,
                Priority.ALWAYS
        );


        // -----------------------------------------------------
        // Content
        // -----------------------------------------------------

        VBox contentBox =
                new VBox(
                        header,
                        body
                );

        contentBox.setStyle(
                "-fx-background-color:transparent;"
        );


        // -----------------------------------------------------
        // ScrollPane
        // -----------------------------------------------------

        ScrollPane root =
                new ScrollPane(
                        contentBox
                );

        root.setFitToWidth(true);
        root.setFitToHeight(true);

        root.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-background:transparent;" +
                "-fx-border-color:transparent;"
        );


        // -----------------------------------------------------
        // Main Root
        // -----------------------------------------------------

        StackPane mainRoot =
                new StackPane(
                        backgroundImage,
                        overlay,
                        root
                );


        backgroundImage.fitWidthProperty()
                .bind(
                        mainRoot.widthProperty()
                );

        backgroundImage.fitHeightProperty()
                .bind(
                        mainRoot.heightProperty()
                );

        overlay.prefWidthProperty()
                .bind(
                        mainRoot.widthProperty()
                );

        overlay.prefHeightProperty()
                .bind(
                        mainRoot.heightProperty()
                );


        // -----------------------------------------------------
        // Entrance Animation
        // -----------------------------------------------------

        root.setOpacity(0);
        root.setTranslateY(22);

        root.setScaleX(0.985);
        root.setScaleY(0.985);


        FadeTransition fade =
                new FadeTransition(
                        Duration.millis(800),
                        root
                );

        fade.setFromValue(0);
        fade.setToValue(1);


        TranslateTransition slide =
                new TranslateTransition(
                        Duration.millis(800),
                        root
                );

        slide.setFromY(22);
        slide.setToY(0);


        ScaleTransition scale =
                new ScaleTransition(
                        Duration.millis(800),
                        root
                );

        scale.setFromX(0.985);
        scale.setFromY(0.985);

        scale.setToX(1);
        scale.setToY(1);


        ParallelTransition entrance =
                new ParallelTransition(
                        fade,
                        slide,
                        scale
                );

        entrance.play();


        // -----------------------------------------------------
        // Scene Size
        // -----------------------------------------------------

        double w =
                (
                        AppNavigator.getPrimaryStage() != null
                        &&
                        AppNavigator
                                .getPrimaryStage()
                                .getWidth() > 0
                )
                ?
                        AppNavigator
                                .getPrimaryStage()
                                .getWidth()
                :
                        1280;


        double h =
                (
                        AppNavigator.getPrimaryStage() != null
                        &&
                        AppNavigator
                                .getPrimaryStage()
                                .getHeight() > 0
                )
                ?
                        AppNavigator
                                .getPrimaryStage()
                                .getHeight()
                :
                        800;


        loginPageScene =
                new Scene(
                        mainRoot,
                        w,
                        h
                );


        return loginPageScene;
    }


    // =========================================================
    // FIELD LABEL
    // =========================================================

    private Label fieldLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + TEXT + ";"
        );

        return label;
    }


    // =========================================================
    // SPACER
    // =========================================================

    private Region spacer() {

        Region region =
                new Region();

        HBox.setHgrow(
                region,
                Priority.ALWAYS
        );

        return region;
    }


    // =========================================================
    // BACK TO LOGIN
    // =========================================================

    void backToLoginPage() {

        AppNavigator.setScene(
                loginPageScene
        );

        AppNavigator.setMaximized(
                true
        );
    }
}