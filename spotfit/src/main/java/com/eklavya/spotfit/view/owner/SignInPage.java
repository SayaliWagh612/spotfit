
package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.auth.AuthController;
import com.eklavya.spotfit.controller.owner.OwnerAuthController;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.view.auth.PasswordResetDialog;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

// SpotFit - Owner Sign In
// Existing Owner page size/layout preserved.
// Space Manager style background added.
public class SignInPage {

    private static final String PRIMARY = "#884600";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";
    private static final String BORDER = "#d9c2b4";

    private final OwnerAuthController ownerAuthController =
            new OwnerAuthController();

    private Scene signInScene;

    public static Scene getScene() {
        return getScene(null);
    }

    public static Scene getScene(Runnable back) {
        SignInPage page = new SignInPage();
        return page.createScene(back);
    }

    private Scene createScene(Runnable back) {

        // =========================================================
        // BACKGROUND IMAGE - SAME STYLE AS SPACE MANAGER
        // =========================================================

        ImageView backgroundImage = new ImageView();

        try {
            Image image = new Image(
                    getClass().getResourceAsStream(
                            "/images/manager-background.png"
                    )
            );

            backgroundImage.setImage(image);

        } catch (Exception e) {
            System.out.println(
                    "Owner login background image not found."
            );
        }

        // IMPORTANT:
        // Background fills whatever size the Stage currently has.
        // It does NOT set or force the Stage size.
        backgroundImage.setPreserveRatio(false);
        backgroundImage.setSmooth(true);
        backgroundImage.setCache(true);

        // =========================================================
        // SPACE MANAGER STYLE OVERLAY
        // =========================================================

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

        // =========================================================
        // HEADER
        // =========================================================

        Label logo = new Label("SpotFit");

        logo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;" +
                "-fx-effect:dropshadow(" +
                "gaussian,rgba(0,0,0,0.45),8,0,0,2);"
        );

        Label backLink = new Label("←  Back to Welcome");

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

        backLink.setOnMouseEntered(e ->
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
                )
        );

        backLink.setOnMouseExited(e ->
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
                )
        );

        backLink.setOnMouseClicked(e -> {
            if (back != null) {
                back.run();
            } else {
                OwnerFlowRouter.goToRoleSelection();
            }
        });

        HBox header = new HBox(
                logo,
                spacer(),
                backLink
        );

        header.setAlignment(Pos.CENTER_LEFT);

        // KEEPING EXISTING HEADER SIZE
        header.setPadding(
                new Insets(24, 55, 20, 55)
        );

        // =========================================================
        // OWNER LOGIN CONTENT
        // =========================================================

        Label label = new Label("OWNER PORTAL");

        label.setStyle(
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";"
        );

        Label title = new Label("Welcome Back");

        title.setStyle(
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + TEXT + ";"
        );

        Label subtitle = new Label(
                "Sign in to continue managing your SpotFit spaces and business."
        );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );

        // =========================================================
        // EMAIL
        // =========================================================

        TextField emailField = new TextField();

        emailField.setPromptText("Enter Email");

        emailField.setPrefHeight(46);

        emailField.setStyle(
                "-fx-background-color:rgba(255,255,255,0.96);" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:12px;" +
                "-fx-padding:10 14 10 14;"
        );

        // =========================================================
        // PASSWORD
        // =========================================================

        PasswordField passwordField = new PasswordField();

        passwordField.setPromptText("Enter your password");

        passwordField.setPrefHeight(46);

        passwordField.setStyle(
                "-fx-background-color:rgba(255,255,255,0.96);" +
                "-fx-font-size:14px;" +
                "-fx-background-radius:12px;" +
                "-fx-border-color:" + BORDER + ";" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:12px;" +
                "-fx-padding:10 14 10 14;"
        );

        // =========================================================
        // FIELD FOCUS EFFECT
        // =========================================================

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
                                "-fx-padding:10 14 10 14;"
                        );
                    } else {
                        emailField.setStyle(
                                "-fx-background-color:rgba(255,255,255,0.96);" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + BORDER + ";" +
                                "-fx-border-width:1px;" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14 10 14;"
                        );
                    }
                }
        );

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
                                "-fx-padding:10 14 10 14;"
                        );
                    } else {
                        passwordField.setStyle(
                                "-fx-background-color:rgba(255,255,255,0.96);" +
                                "-fx-font-size:14px;" +
                                "-fx-background-radius:12px;" +
                                "-fx-border-color:" + BORDER + ";" +
                                "-fx-border-width:1px;" +
                                "-fx-border-radius:12px;" +
                                "-fx-padding:10 14 10 14;"
                        );
                    }
                }
        );

        // =========================================================
        // REMEMBER + FORGOT PASSWORD
        // =========================================================

        CheckBox rememberMe =
                new CheckBox("Remember this device");

        rememberMe.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" + SECONDARY_TEXT + ";"
        );

        Label forgotPassword =
                new Label("Forgot Password?");

        forgotPassword.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-cursor:hand;"
        );

        forgotPassword.setOnMouseEntered(e ->
                forgotPassword.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#6d3600;" +
                        "-fx-cursor:hand;"
                )
        );

        forgotPassword.setOnMouseExited(e ->
                forgotPassword.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + PRIMARY + ";" +
                        "-fx-cursor:hand;"
                )
        );

        forgotPassword.setOnMouseClicked(e ->
                PasswordResetDialog.show(
                        new AuthController()
                )
        );

        HBox rememberRow = new HBox(
                rememberMe,
                spacer(),
                forgotPassword
        );

        rememberRow.setAlignment(Pos.CENTER_LEFT);

        // =========================================================
        // ERROR LABEL
        // =========================================================

        Label errorLabel = new Label();

        errorLabel.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#ba1a1a;"
        );

        errorLabel.setWrapText(true);

        errorLabel.setVisible(false);

        // =========================================================
        // SIGN IN BUTTON
        // =========================================================

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
                "gaussian,rgba(0,0,0,0.20),10,0,0,3);"
        );

        signInButton.setOnMouseEntered(e ->
                signInButton.setStyle(
                        "-fx-background-color:#6d3600;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:12px;" +
                        "-fx-cursor:hand;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian,rgba(0,0,0,0.25),12,0,0,4);"
                )
        );

        signInButton.setOnMouseExited(e ->
                signInButton.setStyle(
                        "-fx-background-color:" + PRIMARY + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:12px;" +
                        "-fx-cursor:hand;" +
                        "-fx-effect:dropshadow(" +
                        "gaussian,rgba(0,0,0,0.20),10,0,0,3);"
                )
        );

        // =========================================================
        // LOGIN ACTION
        // =========================================================

        signInButton.setOnAction(e -> {

            errorLabel.setVisible(false);

            String email =
                    emailField.getText().trim();

            String password =
                    passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {

                errorLabel.setText(
                        "Please enter email and password."
                );

                errorLabel.setVisible(true);

                return;
            }

            signInButton.setDisable(true);

            signInButton.setText(
                    "Signing In..."
            );

            ownerAuthController
                    .loginOwnerWithProfile(
                            email,
                            password
                    )
                    .whenComplete((result, throwable) -> {

                        Platform.runLater(() -> {

                            signInButton.setDisable(false);

                            signInButton.setText(
                                    "Sign In →"
                            );

                            if (throwable != null) {

                                errorLabel.setText(
                                        "Login failed: " +
                                        throwable.getMessage()
                                );

                                errorLabel.setVisible(true);

                                return;
                            }

                            if (result != null &&
                                result.isSuccess()) {

                                Owner owner =
                                        result.getData();

                                if (owner != null &&
                                    !owner.isEmailVerified()) {

                                    OwnerFlowRouter
                                            .goToEmailVerification(
                                                    owner.getEmail()
                                            );

                                } else if (
                                        owner != null &&
                                        !owner.isProfileCompleted()) {

                                    OwnerFlowRouter
                                            .goToProfileSetup();

                                } else {

                                    OwnerFlowRouter
                                            .goToDashboard();
                                }

                            } else {

                                errorLabel.setText(
                                        result != null &&
                                        result.getMessage() != null
                                                ? result.getMessage()
                                                : "Authentication failed."
                                );

                                errorLabel.setVisible(true);
                            }
                        });
                    });
        });

        // =========================================================
        // REGISTER LINK
        // =========================================================

        Label registerText =
                new Label(
                        "Don't have an owner account?"
                );

        registerText.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:" +
                SECONDARY_TEXT + ";"
        );

        Label registerLink =
                new Label("Register Your Space");

        registerLink.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + PRIMARY + ";" +
                "-fx-cursor:hand;"
        );

        registerLink.setOnMouseEntered(e ->
                registerLink.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:#6d3600;" +
                        "-fx-cursor:hand;"
                )
        );

        registerLink.setOnMouseExited(e ->
                registerLink.setStyle(
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:" + PRIMARY + ";" +
                        "-fx-cursor:hand;"
                )
        );

        registerLink.setOnMouseClicked(e ->
                OwnerFlowRouter.goToRegister()
        );

        HBox registerLinkRow =
                new HBox(
                        5,
                        registerText,
                        registerLink
                );

        registerLinkRow.setAlignment(
                Pos.CENTER
        );

        // =========================================================
        // EXISTING LOGIN CARD SIZE - DO NOT CHANGE
        // =========================================================

        VBox loginBox = new VBox(
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

                rememberRow,
                errorLabel,
                signInButton,
                registerLinkRow
        );

        // KEEP EXISTING CARD PADDING
        loginBox.setPadding(
                new Insets(36)
        );

        // KEEP EXISTING CARD WIDTH
        loginBox.setMaxWidth(420);

        loginBox.setStyle(
                "-fx-background-color:rgba(255,255,255,0.96);" +
                "-fx-background-radius:24px;" +
                "-fx-border-color:rgba(255,255,255,0.75);" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:24px;" +
                "-fx-effect:dropshadow(" +
                "gaussian,rgba(0,0,0,0.28),30,0,0,10);"
        );

        // =========================================================
        // BODY
        // =========================================================

        HBox body =
                new HBox(loginBox);

        body.setAlignment(
                Pos.CENTER
        );

        // KEEP EXISTING BODY PADDING
        body.setPadding(
                new Insets(15, 60, 40, 60)
        );

        VBox.setVgrow(
                body,
                Priority.ALWAYS
        );

        // =========================================================
        // CONTENT
        // =========================================================

        VBox contentBox =
                new VBox(
                        header,
                        body
                );

        contentBox.setStyle(
                "-fx-background-color:transparent;"
        );

        // =========================================================
        // SCROLL PANE
        // =========================================================

        ScrollPane root =
                new ScrollPane(contentBox);

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
                "-fx-background:transparent;"
        );

        // =========================================================
        // MAIN ROOT
        // =========================================================

        StackPane mainRoot =
                new StackPane(
                        backgroundImage,
                        overlay,
                        root
                );

        // Background follows the actual Stage size.
        backgroundImage.fitWidthProperty()
                .bind(mainRoot.widthProperty());

        backgroundImage.fitHeightProperty()
                .bind(mainRoot.heightProperty());

        overlay.prefWidthProperty()
                .bind(mainRoot.widthProperty());

        overlay.prefHeightProperty()
                .bind(mainRoot.heightProperty());

        // =========================================================
        // ENTRANCE ANIMATION
        // =========================================================

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

        TranslateTransition translate =
                new TranslateTransition(
                        Duration.millis(800),
                        root
                );

        translate.setFromY(22);
        translate.setToY(0);

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
                        translate,
                        scale
                );

        entrance.play();

        // =========================================================
        // IMPORTANT:
        // No stage.setWidth()
        // No stage.setHeight()
        // No stage.setMaximized()
        //
        // The stage size is controlled by AppNavigator.
        // =========================================================

        signInScene =
                new Scene(mainRoot);

        return signInScene;
    }

    // =============================================================
    // FIELD LABEL
    // =============================================================

    private Label fieldLabel(String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + TEXT + ";"
        );

        return label;
    }

    // =============================================================
    // SPACER
    // =============================================================

    private Region spacer() {

        Region region =
                new Region();

        HBox.setHgrow(
                region,
                Priority.ALWAYS
        );

        return region;
    }

    // =============================================================
    // BACK TO ROLE SELECTION
    // =============================================================

    public void backToLoginPage() {

        OwnerFlowRouter
                .goToRoleSelection();
    }
}
