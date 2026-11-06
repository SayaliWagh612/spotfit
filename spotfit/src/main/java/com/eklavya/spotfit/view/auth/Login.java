package com.eklavya.spotfit.view.auth;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.InputStream;

import com.eklavya.spotfit.controller.auth.AuthController;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.view.common.SelectRole;

import static com.eklavya.spotfit.util.ResponsiveUtil.screenBounds;
import static com.eklavya.spotfit.view.common.UIComponents.checkbox;
import static com.eklavya.spotfit.view.common.UIComponents.divider;
import static com.eklavya.spotfit.view.common.UIComponents.outlineButton;
import static com.eklavya.spotfit.view.common.UIComponents.primaryButton;

public class Login {

        /*
         * ============================================================
         * DUMMY BACKGROUND IMAGE
         * ============================================================
         *
         * Replace this URL later with your actual image URL if needed.
         */
        private static final String DUMMY_IMAGE_URL = "https://images.unsplash.com/photo-1497366754035-f200968a6e72"
                        + "?auto=format&fit=crop&w=1920&q=80";

        /*
         * ============================================================
         * GET LOGIN SCENE
         * ============================================================
         */

        public static Scene getLoginScene(
                        Runnable onLoginSuccess,
                        Runnable onGoToRegister) {

                /*
                 * --------------------------------------------------------
                 * ROOT
                 * --------------------------------------------------------
                 */

                StackPane root = new StackPane();

                root.setStyle(
                                "-fx-background-color: #F5EFEA;");

                /*
                 * --------------------------------------------------------
                 * BACKGROUND IMAGE
                 * --------------------------------------------------------
                 */

                Image backgroundImage;

                try {

                        backgroundImage = new Image(
                                        DUMMY_IMAGE_URL,
                                        true);

                } catch (Exception e) {

                        backgroundImage = null;
                }

                ImageView background = new ImageView(backgroundImage);

                /*
                 * Stretch image to entire screen.
                 */
                background.setPreserveRatio(false);

                background.setSmooth(true);
                background.setCache(true);

                background.fitWidthProperty()
                                .bind(root.widthProperty());

                background.fitHeightProperty()
                                .bind(root.heightProperty());

                /*
                 * --------------------------------------------------------
                 * DARK OVERLAY
                 * --------------------------------------------------------
                 */

                Region overlay = new Region();

                overlay.setStyle(
                                "-fx-background-color: "
                                                + "linear-gradient("
                                                + "to bottom right,"
                                                + "rgba(30,18,10,0.25),"
                                                + "rgba(50,25,10,0.35),"
                                                + "rgba(20,12,7,0.30)"
                                                + ");");

                overlay.prefWidthProperty()
                                .bind(root.widthProperty());

                overlay.prefHeightProperty()
                                .bind(root.heightProperty());

                /*
                 * --------------------------------------------------------
                 * LOGIN CARD
                 * --------------------------------------------------------
                 */

                VBox loginCard = buildLoginCard(
                                onLoginSuccess,
                                onGoToRegister);

                loginCard.setMaxWidth(420);

                /*
                 * Keep login card in the center.
                 */
                StackPane.setAlignment(
                                loginCard,
                                Pos.CENTER);

                /*
                 * --------------------------------------------------------
                 * BODY
                 * --------------------------------------------------------
                 */

                StackPane body = new StackPane(loginCard);

                body.setPadding(
                                new Insets(
                                                90,
                                                60,
                                                40,
                                                60));

                body.prefWidthProperty()
                                .bind(root.widthProperty());

                body.prefHeightProperty()
                                .bind(root.heightProperty());

                /*
                 * --------------------------------------------------------
                 * HEADER
                 * --------------------------------------------------------
                 *
                 * AnchorPane is used so that the header is ALWAYS
                 * positioned at the TOP.
                 */

                AnchorPane headerLayer = new AnchorPane();

                HBox header = buildHeader();

                /*
                 * TOP
                 */
                AnchorPane.setTopAnchor(
                                header,
                                0.0);

                /*
                 * LEFT
                 */
                AnchorPane.setLeftAnchor(
                                header,
                                0.0);

                /*
                 * RIGHT
                 */
                AnchorPane.setRightAnchor(
                                header,
                                0.0);

                headerLayer.getChildren()
                                .add(header);

                headerLayer.prefWidthProperty()
                                .bind(root.widthProperty());

                headerLayer.prefHeightProperty()
                                .bind(root.heightProperty());

                /*
                 * Header should not block login card.
                 */
                headerLayer.setPickOnBounds(false);

                /*
                 * --------------------------------------------------------
                 * ADD ALL LAYERS
                 * --------------------------------------------------------
                 *
                 * Order:
                 *
                 * Background
                 * Overlay
                 * Login card
                 * Header
                 */

                root.getChildren().addAll(
                                background,
                                overlay,
                                body,
                                headerLayer);

                /*
                 * --------------------------------------------------------
                 * SCROLL PANE
                 * --------------------------------------------------------
                 */

                ScrollPane scrollRoot = new ScrollPane(root);

                scrollRoot.setFitToWidth(true);
                scrollRoot.setFitToHeight(true);

                scrollRoot.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scrollRoot.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scrollRoot.setPannable(false);

                scrollRoot.setStyle(
                                "-fx-background-color: transparent;"
                                                + "-fx-background: transparent;"
                                                + "-fx-border-color: transparent;");

                /*
                 * --------------------------------------------------------
                 * SCENE
                 * --------------------------------------------------------
                 */

                Rectangle2D screen = screenBounds();

                Scene scene = new Scene(
                                scrollRoot,
                                screen.getWidth(),
                                screen.getHeight());

                /*
                 * --------------------------------------------------------
                 * ENTRANCE ANIMATION
                 * --------------------------------------------------------
                 */

                playEntranceAnimation(loginCard);

                return scene;
        }

        /*
         * ============================================================
         * HEADER
         * ============================================================
         */

        private static HBox buildHeader() {

                /*
                 * --------------------------------------------------------
                 * SPOTFIT
                 * --------------------------------------------------------
                 */

                Text logoText = new Text("SpotFit");

                logoText.setStyle(
                                "-fx-fill: white;"
                                                + "-fx-font-size: 22px;"
                                                + "-fx-font-weight: bold;");

                logoText.setEffect(
                                new DropShadow(
                                                8,
                                                Color.rgb(
                                                                0,
                                                                0,
                                                                0,
                                                                0.45)));

                /*
                 * --------------------------------------------------------
                 * BACK BUTTON
                 * --------------------------------------------------------
                 */

                Button backButton = new Button(
                                "←  Back to Welcome");

                backButton.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.94);"
                                                + "-fx-text-fill: #6F390E;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-background-radius: 20;"
                                                + "-fx-border-radius: 20;"
                                                + "-fx-border-color: rgba(136,70,0,0.25);"
                                                + "-fx-border-width: 1;"
                                                + "-fx-padding: 9 18 9 18;"
                                                + "-fx-cursor: hand;");

                /*
                 * --------------------------------------------------------
                 * BACK BUTTON HOVER
                 * --------------------------------------------------------
                 */

                backButton.setOnMouseEntered(e -> backButton.setStyle(
                                "-fx-background-color: #884600;"
                                                + "-fx-text-fill: white;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-background-radius: 20;"
                                                + "-fx-border-radius: 20;"
                                                + "-fx-border-color: #884600;"
                                                + "-fx-border-width: 1;"
                                                + "-fx-padding: 9 18 9 18;"
                                                + "-fx-cursor: hand;"));

                backButton.setOnMouseExited(e -> backButton.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.94);"
                                                + "-fx-text-fill: #6F390E;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-background-radius: 20;"
                                                + "-fx-border-radius: 20;"
                                                + "-fx-border-color: rgba(136,70,0,0.25);"
                                                + "-fx-border-width: 1;"
                                                + "-fx-padding: 9 18 9 18;"
                                                + "-fx-cursor: hand;"));

                /*
                 * --------------------------------------------------------
                 * BACK NAVIGATION
                 * --------------------------------------------------------
                 *
                 * Existing functionality preserved.
                 */

                backButton.setOnAction(e -> {

                        SelectRole selectRole = new SelectRole();

                        AppNavigator.setScene(
                                        selectRole.getChooseLoginPageScene(
                                                        () -> {
                                                        }));
                });

                /*
                 * --------------------------------------------------------
                 * SPACER
                 * --------------------------------------------------------
                 *
                 * This pushes Back to Welcome to the RIGHT.
                 */

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                /*
                 * --------------------------------------------------------
                 * HEADER
                 * --------------------------------------------------------
                 */

                HBox header = new HBox(
                                logoText,
                                spacer,
                                backButton);

                /*
                 * VERY IMPORTANT:
                 *
                 * CENTER_LEFT means:
                 *
                 * SpotFit -> left
                 * Back button -> right
                 *
                 * But the entire HBox itself is anchored to the TOP.
                 */

                header.setAlignment(
                                Pos.CENTER_LEFT);

                /*
                 * Top = 24
                 * Right = 55
                 * Bottom = 20
                 * Left = 55
                 */

                header.setPadding(
                                new Insets(
                                                24,
                                                55,
                                                20,
                                                55));

                header.setMaxWidth(
                                Double.MAX_VALUE);

                return header;
        }

        /*
         * ============================================================
         * LOGIN CARD
         * ============================================================
         */

        private static VBox buildLoginCard(
                        Runnable onLoginSuccess,
                        Runnable onGoToRegister) {

                /*
                 * --------------------------------------------------------
                 * CARD
                 * --------------------------------------------------------
                 */

                VBox card = new VBox(0);

                card.setMaxWidth(420);

                card.setPadding(
                                new Insets(36));

                card.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.96);"
                                                + "-fx-background-radius: 24;"
                                                + "-fx-border-radius: 24;"
                                                + "-fx-border-color: rgba(217,194,180,0.65);"
                                                + "-fx-border-width: 1;"
                                                + "-fx-effect: dropshadow("
                                                + "gaussian,"
                                                + "rgba(0,0,0,0.28),"
                                                + "25,"
                                                + "0,"
                                                + "0,"
                                                + "10"
                                                + ");");

                /*
                 * --------------------------------------------------------
                 * CUSTOMER PORTAL
                 * --------------------------------------------------------
                 */

                Label portalLabel = new Label(
                                "CUSTOMER PORTAL");

                portalLabel.setStyle(
                                "-fx-text-fill: #884600;"
                                                + "-fx-font-size: 12px;"
                                                + "-fx-font-weight: bold;");

                /*
                 * --------------------------------------------------------
                 * TITLE
                 * --------------------------------------------------------
                 */

                Text title = new Text(
                                "Welcome Back");

                title.setStyle(
                                "-fx-fill: #2D211B;"
                                                + "-fx-font-size: 30px;"
                                                + "-fx-font-weight: bold;");

                /*
                 * --------------------------------------------------------
                 * SUBTITLE
                 * --------------------------------------------------------
                 */

                Text subtitle = new Text(
                                "Login to continue your SpotFit journey.");

                subtitle.setStyle(
                                "-fx-fill: #75675F;"
                                                + "-fx-font-size: 14px;");

                /*
                 * --------------------------------------------------------
                 * EMAIL LABEL
                 * --------------------------------------------------------
                 */

                Label emailLabel = new Label(
                                "Email Address");

                emailLabel.setStyle(
                                "-fx-text-fill: #3F3028;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;");

                /*
                 * --------------------------------------------------------
                 * EMAIL FIELD
                 * --------------------------------------------------------
                 */

                TextField email = new TextField();

                email.setPromptText(
                                "Enter your email");

                styleInputField(email);

                /*
                 * --------------------------------------------------------
                 * PASSWORD LABEL
                 * --------------------------------------------------------
                 */

                Label passwordLabel = new Label(
                                "Password");

                passwordLabel.setStyle(
                                "-fx-text-fill: #3F3028;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;");

                /*
                 * --------------------------------------------------------
                 * PASSWORD
                 * --------------------------------------------------------
                 */

                StackPane passwordStack = passwordFieldWithToggle();

                PasswordField pwdField = (PasswordField) passwordStack.getChildren().get(0);

                /*
                 * --------------------------------------------------------
                 * REMEMBER ME
                 * --------------------------------------------------------
                 */

                var rememberMe = checkbox(
                                "Remember Me");

                /*
                 * --------------------------------------------------------
                 * FORGOT PASSWORD
                 * --------------------------------------------------------
                 */

                Label forgot = new Label(
                                "Forgot Password?");

                forgot.setStyle(
                                "-fx-text-fill: #884600;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-cursor: hand;");

                /*
                 * --------------------------------------------------------
                 * REMEMBER ROW
                 * --------------------------------------------------------
                 */

                HBox rememberRow = new HBox(
                                rememberMe,
                                spacer(),
                                forgot);

                rememberRow.setAlignment(
                                Pos.CENTER_LEFT);

                /*
                 * --------------------------------------------------------
                 * AUTH CONTROLLER
                 * --------------------------------------------------------
                 */

                AuthController authController = new AuthController();

                /*
                 * --------------------------------------------------------
                 * STATUS LABEL
                 * --------------------------------------------------------
                 */

                Label statusLabel = new Label();

                statusLabel.setWrapText(true);

                statusLabel.setMaxWidth(350);

                statusLabel.setStyle(
                                "-fx-font-size: 13px;");

                /*
                 * --------------------------------------------------------
                 * FORGOT PASSWORD ACTION
                 * --------------------------------------------------------
                 */

                forgot.setOnMouseClicked(e -> PasswordResetDialog.show(
                                authController));

                /*
                 * --------------------------------------------------------
                 * LOGIN BUTTON
                 * --------------------------------------------------------
                 */

                var loginBtn = primaryButton(
                                "Sign In  →");

                loginBtn.setPrefHeight(46);

                loginBtn.setMaxWidth(
                                Double.MAX_VALUE);

                loginBtn.setStyle(
                                "-fx-background-color: #884600;"
                                                + "-fx-text-fill: white;"
                                                + "-fx-font-size: 14px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-background-radius: 12;"
                                                + "-fx-border-radius: 12;"
                                                + "-fx-cursor: hand;");

                /*
                 * --------------------------------------------------------
                 * LOGIN BUTTON HOVER
                 * --------------------------------------------------------
                 */

                loginBtn.setOnMouseEntered(e -> {

                        if (!loginBtn.isDisabled()) {

                                loginBtn.setStyle(
                                                "-fx-background-color: #6F390E;"
                                                                + "-fx-text-fill: white;"
                                                                + "-fx-font-size: 14px;"
                                                                + "-fx-font-weight: bold;"
                                                                + "-fx-background-radius: 12;"
                                                                + "-fx-border-radius: 12;"
                                                                + "-fx-cursor: hand;");
                        }
                });

                loginBtn.setOnMouseExited(e -> {

                        if (!loginBtn.isDisabled()) {

                                loginBtn.setStyle(
                                                "-fx-background-color: #884600;"
                                                                + "-fx-text-fill: white;"
                                                                + "-fx-font-size: 14px;"
                                                                + "-fx-font-weight: bold;"
                                                                + "-fx-background-radius: 12;"
                                                                + "-fx-border-radius: 12;"
                                                                + "-fx-cursor: hand;");
                        }
                });

                /*
                 * ========================================================
                 * LOGIN FUNCTIONALITY
                 * ========================================================
                 *
                 * This is your existing authentication flow.
                 * No authentication logic has been changed.
                 */

                loginBtn.setOnAction(e -> {

                        String mail = email.getText().trim();

                        String pwd = pwdField.getText();

                        statusLabel.setText("");

                        loginBtn.setDisable(true);

                        loginBtn.setText(
                                        "Signing In...");

                        authController
                                        .loginUserWithProfile(
                                                        mail,
                                                        pwd)
                                        .whenComplete(
                                                        (result, throwable) -> Platform.runLater(() -> {

                                                                loginBtn.setDisable(
                                                                                false);

                                                                loginBtn.setText(
                                                                                "Sign In  →");

                                                                /*
                                                                 * Firebase error
                                                                 */

                                                                if (throwable != null) {

                                                                        statusLabel.setStyle(
                                                                                        "-fx-text-fill: #D32F2F;"
                                                                                                        + "-fx-font-size: 13px;");

                                                                        statusLabel.setText(
                                                                                        "Something went wrong while contacting Firebase. "
                                                                                                        + "Please try again.");

                                                                        return;
                                                                }

                                                                /*
                                                                 * Successful login
                                                                 */
                                                                if (result.isSuccess()) {
                                                                        User user = result.getData();
                                                                        if (user != null && !user.isEmailVerified()) {
                                                                                com.eklavya.spotfit.view.user.UserFlowRouter
                                                                                                .goToEmailVerification(
                                                                                                                user.getEmail());
                                                                        } else if (user != null && !user
                                                                                        .isOnboardingCompleted()) {
                                                                                com.eklavya.spotfit.view.user.UserFlowRouter
                                                                                                .goToPersonaSelection();
                                                                        } else {
                                                                                onLoginSuccess.run();
                                                                        }
                                                                } else {
                                                                        statusLabel.setStyle(
                                                                                        "-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                                                                        statusLabel.setText(result.getMessage());
                                                                }

                                                                // if (result.isSuccess()) {

                                                                // User user =
                                                                // result.getData();

                                                                // /*
                                                                // * Email verification
                                                                // */

                                                                // if (user != null
                                                                // && !user.isEmailVerified()) {

                                                                // com.eklavya.spotfit.view.user.UserFlowRouter
                                                                // .goToEmailVerification(
                                                                // user.getEmail()
                                                                // );

                                                                // }

                                                                // /*
                                                                // * Onboarding
                                                                // */

                                                                // else if (user != null
                                                                // && !user.isOnboardingCompleted()) {

                                                                // com.eklavya.spotfit.view.user.UserFlowRouter
                                                                // .goToPersonaSelection();

                                                                // }

                                                                // /*
                                                                // * Normal successful login
                                                                // */

                                                                // else {

                                                                // onLoginSuccess.run();
                                                                // }

                                                                // }

                                                                // /*
                                                                // * Login failed
                                                                // */

                                                                // else {

                                                                // statusLabel.setStyle(
                                                                // "-fx-text-fill: #D32F2F;"
                                                                // + "-fx-font-size: 13px;"
                                                                // );

                                                                // statusLabel.setText(
                                                                // result.getMessage()
                                                                // );
                                                                // }
                                                        }));
                });

                /*
                 * --------------------------------------------------------
                 * DIVIDER
                 * --------------------------------------------------------
                 */

                HBox divider = divider("OR");

                /*
                 * --------------------------------------------------------
                 * GOOGLE BUTTON
                 * --------------------------------------------------------
                 */

                var googleBtn = outlineButton(
                                "🔍  Continue with Google");

                /*
                 * --------------------------------------------------------
                 * APPLE BUTTON
                 * --------------------------------------------------------
                 */

                var appleBtn = outlineButton(
                                "🍎  Continue with Apple");

                /*
                 * --------------------------------------------------------
                 * GOOGLE ACTION
                 * --------------------------------------------------------
                 */

                googleBtn.setOnAction(e -> {

                        statusLabel.setStyle(
                                        "-fx-text-fill: #75675F;"
                                                        + "-fx-font-size: 13px;");

                        statusLabel.setText(
                                        "Social sign-in is not available yet.");
                });

                /*
                 * --------------------------------------------------------
                 * APPLE ACTION
                 * --------------------------------------------------------
                 */

                appleBtn.setOnAction(e -> {

                        statusLabel.setStyle(
                                        "-fx-text-fill: #75675F;"
                                                        + "-fx-font-size: 13px;");

                        statusLabel.setText(
                                        "Social sign-in is not available yet.");
                });

                /*
                 * --------------------------------------------------------
                 * SIGNUP PROMPT
                 * --------------------------------------------------------
                 */

                Label signupPrompt = new Label(
                                "Don't have an account?");

                signupPrompt.setStyle(
                                "-fx-text-fill: #75675F;"
                                                + "-fx-font-size: 13px;");

                Label signupLink = new Label(
                                " Create Account");

                signupLink.setStyle(
                                "-fx-text-fill: #884600;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-font-weight: bold;"
                                                + "-fx-cursor: hand;");

                /*
                 * Register navigation
                 */

                signupLink.setOnMouseClicked(
                                e -> onGoToRegister.run());

                HBox signupRow = new HBox(
                                signupPrompt,
                                signupLink);

                signupRow.setAlignment(
                                Pos.CENTER);

                /*
                 * --------------------------------------------------------
                 * GUEST LINK
                 * --------------------------------------------------------
                 */

                Label guestLink = new Label(
                                "Browse as Guest");

                guestLink.setStyle(
                                "-fx-text-fill: #3F3028;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-underline: true;"
                                                + "-fx-cursor: hand;");

                guestLink.setOnMouseClicked(
                                e -> AppNavigator.setScene(
                                                com.eklavya.spotfit.view.user.UserDash
                                                                .getUserDashScene()));

                HBox guestRow = new HBox(
                                guestLink);

                guestRow.setAlignment(
                                Pos.CENTER);

                /*
                 * --------------------------------------------------------
                 * TRUST BADGES
                 * --------------------------------------------------------
                 */

                HBox trustRow = trustBadges();

                /*
                 * --------------------------------------------------------
                 * CARD SECTIONS
                 * --------------------------------------------------------
                 */

                VBox portalBox = new VBox(
                                8,
                                portalLabel,
                                title,
                                subtitle);

                VBox emailBox = new VBox(
                                7,
                                emailLabel,
                                email);

                VBox passwordBox = new VBox(
                                7,
                                passwordLabel,
                                passwordStack);

                VBox fieldsBox = new VBox(
                                15,
                                emailBox,
                                passwordBox);

                VBox socialBox = new VBox(
                                10,
                                googleBtn,
                                appleBtn);

                /*
                 * --------------------------------------------------------
                 * ADD TO CARD
                 * --------------------------------------------------------
                 */

                card.getChildren().addAll(

                                portalBox,

                                createGap(24),

                                fieldsBox,

                                createGap(14),

                                rememberRow,

                                createGap(10),

                                statusLabel,

                                createGap(12),

                                loginBtn,

                                createGap(18),

                                divider,

                                createGap(14),

                                socialBox,

                                createGap(18),

                                signupRow,

                                createGap(10),

                                guestRow,

                                createGap(14),

                                trustRow);

                return card;
        }

        /*
         * ============================================================
         * INPUT FIELD STYLE
         * ============================================================
         */

        private static void styleInputField(
                        TextField field) {

                field.setPrefHeight(46);

                field.setMaxWidth(
                                Double.MAX_VALUE);

                field.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.92);"
                                                + "-fx-background-radius: 12;"
                                                + "-fx-border-radius: 12;"
                                                + "-fx-border-color: #D9C2B4;"
                                                + "-fx-border-width: 1;"
                                                + "-fx-text-fill: #2D211B;"
                                                + "-fx-prompt-text-fill: #A08F84;"
                                                + "-fx-font-size: 13px;"
                                                + "-fx-padding: 0 14 0 14;");

                /*
                 * Focus effect
                 */

                field.focusedProperty()
                                .addListener(
                                                (obs, oldValue, focused) -> {

                                                        if (focused) {

                                                                field.setStyle(
                                                                                "-fx-background-color: white;"
                                                                                                + "-fx-background-radius: 12;"
                                                                                                + "-fx-border-radius: 12;"
                                                                                                + "-fx-border-color: #884600;"
                                                                                                + "-fx-border-width: 1.5;"
                                                                                                + "-fx-text-fill: #2D211B;"
                                                                                                + "-fx-prompt-text-fill: #A08F84;"
                                                                                                + "-fx-font-size: 13px;"
                                                                                                + "-fx-padding: 0 14 0 14;");

                                                        } else {

                                                                field.setStyle(
                                                                                "-fx-background-color: rgba(255,255,255,0.92);"
                                                                                                + "-fx-background-radius: 12;"
                                                                                                + "-fx-border-radius: 12;"
                                                                                                + "-fx-border-color: #D9C2B4;"
                                                                                                + "-fx-border-width: 1;"
                                                                                                + "-fx-text-fill: #2D211B;"
                                                                                                + "-fx-prompt-text-fill: #A08F84;"
                                                                                                + "-fx-font-size: 13px;"
                                                                                                + "-fx-padding: 0 14 0 14;");
                                                        }
                                                });
        }

        /*
         * ============================================================
         * PASSWORD FIELD WITH TOGGLE
         * ============================================================
         */

        private static StackPane passwordFieldWithToggle() {

                /*
                 * Hidden password field
                 */

                PasswordField hidden = new PasswordField();

                hidden.setPromptText(
                                "Enter your password");

                styleInputField(hidden);

                /*
                 * Visible password field
                 */

                TextField shown = new TextField();

                shown.setPromptText(
                                "Enter your password");

                styleInputField(shown);

                /*
                 * Initially hidden
                 */

                shown.setManaged(false);
                shown.setVisible(false);

                /*
                 * Keep text synchronized
                 */

                shown.textProperty()
                                .bindBidirectional(
                                                hidden.textProperty());

                /*
                 * Eye icon
                 */

                Label eye = new Label("👁");

                eye.setStyle(
                                "-fx-cursor: hand;"
                                                + "-fx-text-fill: #75675F;"
                                                + "-fx-font-size: 15px;");

                /*
                 * Toggle
                 */

                eye.setOnMouseClicked(e -> {

                        boolean showingPassword = shown.isVisible();

                        shown.setVisible(
                                        !showingPassword);

                        shown.setManaged(
                                        !showingPassword);

                        hidden.setVisible(
                                        showingPassword);

                        hidden.setManaged(
                                        showingPassword);
                });

                /*
                 * Stack
                 */

                StackPane stack = new StackPane(
                                hidden,
                                shown,
                                eye);

                StackPane.setAlignment(
                                eye,
                                Pos.CENTER_RIGHT);

                StackPane.setMargin(
                                eye,
                                new Insets(
                                                0,
                                                14,
                                                0,
                                                0));

                return stack;
        }

        /*
         * ============================================================
         * TRUST BADGES
         * ============================================================
         */

        private static HBox trustBadges() {

                Label l1 = trustLabel(
                                "🔒 Secure Authentication");

                Label l2 = trustLabel(
                                "✓ Verified Spaces");

                Label l3 = trustLabel(
                                "✓ Trusted Payments");

                Label l4 = trustLabel(
                                "✓ Privacy Protected");

                VBox col1 = new VBox(
                                6,
                                l1,
                                l3);

                VBox col2 = new VBox(
                                6,
                                l2,
                                l4);

                HBox row = new HBox(
                                30,
                                col1,
                                col2);

                row.setAlignment(
                                Pos.CENTER);

                row.setPadding(
                                new Insets(
                                                8,
                                                0,
                                                0,
                                                0));

                return row;
        }

        /*
         * ============================================================
         * TRUST LABEL
         * ============================================================
         */

        private static Label trustLabel(
                        String text) {

                Label label = new Label(text);

                label.setStyle(
                                "-fx-text-fill: #75675F;"
                                                + "-fx-font-size: 11px;");

                return label;
        }

        /*
         * ============================================================
         * SPACER
         * ============================================================
         */

        private static Region spacer() {

                Region region = new Region();

                HBox.setHgrow(
                                region,
                                Priority.ALWAYS);

                return region;
        }

        /*
         * ============================================================
         * GAP
         * ============================================================
         */

        private static Region createGap(
                        double height) {

                Region region = new Region();

                region.setMinHeight(height);

                region.setPrefHeight(height);

                return region;
        }

        /*
         * ============================================================
         * ENTRANCE ANIMATION
         * ============================================================
         */

        private static void playEntranceAnimation(
                        VBox loginCard) {

                /*
                 * Fade
                 */

                FadeTransition fade = new FadeTransition(
                                Duration.millis(800),
                                loginCard);

                fade.setFromValue(0);

                fade.setToValue(1);

                /*
                 * Move upward
                 */

                TranslateTransition translate = new TranslateTransition(
                                Duration.millis(800),
                                loginCard);

                translate.setFromY(25);

                translate.setToY(0);

                /*
                 * Scale
                 */

                ScaleTransition scale = new ScaleTransition(
                                Duration.millis(800),
                                loginCard);

                scale.setFromX(0.97);

                scale.setFromY(0.97);

                scale.setToX(1);

                scale.setToY(1);

                /*
                 * Run together
                 */

                ParallelTransition animation = new ParallelTransition(
                                fade,
                                translate,
                                scale);

                animation.play();
        }

        /*
         * ============================================================
         * LOGO LOADER
         * ============================================================
         *
         * Kept for compatibility with the existing project.
         */

        public static ImageView loadLogo(
                        double height) {

                try {

                        InputStream is = Login.class.getResourceAsStream(
                                        "/images/logo.png");

                        if (is == null) {

                                return new ImageView();
                        }

                        ImageView iv = new ImageView(
                                        new Image(is));

                        iv.setFitHeight(height);

                        iv.setPreserveRatio(true);

                        return iv;

                } catch (Exception e) {

                        return new ImageView();
                }
        }
}