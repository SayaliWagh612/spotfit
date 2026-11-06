package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerAuthController;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class SpaceOwnerRegistrationPage {

    private Scene registrationScene;

    public Scene getScene(Runnable callback) {

        // ============================================================
        // BACKGROUND
        // ============================================================

        StackPane root = new StackPane();

        ImageView background = null;

        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream(
                            "/images/owner_background.png"
                    )
            );

            background = new ImageView(bgImage);

            background.setPreserveRatio(false);

            background.fitWidthProperty().bind(
                    root.widthProperty()
            );

            background.fitHeightProperty().bind(
                    root.heightProperty()
            );

            background.setSmooth(true);

        } catch (Exception e) {
            System.out.println(
                    "Owner background image not found. "
                    + "Using fallback background."
            );
        }

        if (background != null) {
            root.getChildren().add(background);
        }

        // ============================================================
        // DARK OVERLAY
        // ============================================================

        StackPane darkOverlay = new StackPane();

        darkOverlay.setStyle(
                "-fx-background-color: rgba(0,0,0,0.48);"
        );

        darkOverlay.setMouseTransparent(true);

        root.getChildren().add(darkOverlay);


        // ============================================================
        // MAIN CONTENT
        // ============================================================

        HBox mainLayout = new HBox();

        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setSpacing(70);

        mainLayout.setPadding(
                new Insets(35, 70, 35, 70)
        );

        // ============================================================
        // LEFT: PROMOTIONAL PANEL
        // ============================================================

        Label badge = OwnerUi.badge("SPACE OWNER");

        badge.setStyle(
                "-fx-background-color: rgba(255,255,255,0.18);" +
                "-fx-text-fill: white;" +
                "-fx-border-color: rgba(255,255,255,0.40);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 7 15;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Label headline = OwnerUi.heading(
                "Start Growing Your\nBusiness With SpotFit",
                30
        );

        headline.setTextFill(Color.WHITE);

        headline.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: 900;"
        );

        Label sub = OwnerUi.body(
                "Join thousands of space owners managing bookings, customers, staff and revenue through one powerful platform.",
                13.5,
                OwnerUi.TEXT_MUTED
        );

        sub.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.88);" +
                "-fx-font-size: 14px;" +
                "-fx-line-spacing: 3px;"
        );

        sub.setWrapText(true);
        sub.setMaxWidth(470);


        // ============================================================
        // BENEFITS
        // ============================================================

        VBox benefits = new VBox(13);

        String[] perks = {
                "Accept Online Bookings",
                "Manage Multiple Spaces",
                "Track Revenue & Payouts",
                "Manage Staff & Managers",
                "View Business Analytics",
                "Grow Your Customer Base"
        };

        for (String p : perks) {

            HBox row = new HBox(12);

            row.setAlignment(Pos.CENTER_LEFT);

            Label check = new Label("\u2713");

            check.setMinWidth(22);
            check.setMinHeight(22);
            check.setMaxWidth(22);
            check.setMaxHeight(22);

            check.setAlignment(Pos.CENTER);

            check.setStyle(
                    "-fx-text-fill:white;" +
                    "-fx-background-color:" + OwnerUi.ACCENT + ";" +
                    "-fx-background-radius:50%;" +
                    "-fx-font-size:12px;" +
                    "-fx-font-weight:900;"
            );

            Label perkText = OwnerUi.body(
                    p,
                    14,
                    OwnerUi.TEXT_DARK
            );

            perkText.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: 500;"
            );

            row.getChildren().addAll(
                    check,
                    perkText
            );

            benefits.getChildren().add(row);
        }


        // ============================================================
        // KEEP ORIGINAL HERO IMAGE VARIABLE
        // ============================================================

        String HERO_IMAGE_URL = "";

        Node heroImage =
                OwnerUi.imageOrPlaceholder(
                        HERO_IMAGE_URL,
                        460,
                        240,
                        20
                );

        /*
         * heroImage is intentionally NOT added to the layout.
         * The background image is now used behind the complete page.
         */


        VBox left = new VBox(
                24,
                badge,
                headline,
                sub,
                benefits
        );

        left.setAlignment(Pos.CENTER_LEFT);

        left.setMaxWidth(500);

        left.setPadding(
                new Insets(25, 0, 25, 0)
        );


        // ============================================================
        // RIGHT: REGISTRATION FORM
        // ============================================================

        Label title = OwnerUi.heading(
                "Create Space Owner Account",
                24
        );

        title.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: 900;" +
                "-fx-text-fill: #5a2b08;"
        );


        Label subtitle = OwnerUi.body(
                "Create your Owner account and start managing your businesses.",
                13,
                OwnerUi.TEXT_MUTED
        );

        subtitle.setWrapText(true);


        // ============================================================
        // FORM FIELDS
        // ============================================================

        TextField ownerName = OwnerUi.textField("");

        ownerName.setPromptText("John Doe");


        TextField email = OwnerUi.textField("");

        email.setPromptText(
                "john@example.com"
        );


        TextField mobile = OwnerUi.textField("");

        mobile.setPromptText(
                "9876543210"
        );


        PasswordField password =
                OwnerUi.passwordField("");

        password.setPromptText(
                "At least 8 characters"
        );


        PasswordField confirm =
                OwnerUi.passwordField("");

        confirm.setPromptText(
                "Confirm Password"
        );


        // ============================================================
        // GRID
        // ============================================================

        GridPane grid = new GridPane();

        grid.setHgap(16);
        grid.setVgap(5);


        // LEFT COLUMN

        grid.add(
                OwnerUi.fieldLabel("Full Name"),
                0,
                0
        );

        grid.add(
                ownerName,
                0,
                1
        );


        grid.add(
                OwnerUi.fieldLabel("Mobile Number"),
                0,
                2
        );

        grid.add(
                mobile,
                0,
                3
        );


        grid.add(
                OwnerUi.fieldLabel("Password"),
                0,
                4
        );

        grid.add(
                password,
                0,
                5
        );


        // RIGHT COLUMN

        grid.add(
                OwnerUi.fieldLabel("Business Email"),
                1,
                0
        );

        grid.add(
                email,
                1,
                1
        );


        grid.add(
                OwnerUi.fieldLabel("Confirm Password"),
                1,
                2
        );

        grid.add(
                confirm,
                1,
                3
        );


        // ============================================================
        // COLUMN WIDTH
        // ============================================================

        for (int i = 0; i < 2; i++) {

            ColumnConstraints cc =
                    new ColumnConstraints();

            cc.setPercentWidth(50);

            grid.getColumnConstraints().add(cc);
        }


        // ============================================================
        // FIELD SPACING
        // ============================================================

        GridPane.setMargin(
                ownerName,
                new Insets(0, 0, 9, 0)
        );

        GridPane.setMargin(
                email,
                new Insets(0, 0, 9, 0)
        );

        GridPane.setMargin(
                mobile,
                new Insets(0, 0, 9, 0)
        );

        GridPane.setMargin(
                confirm,
                new Insets(0, 0, 9, 0)
        );


        // ============================================================
        // PASSWORD REQUIREMENTS
        // ============================================================

        HBox pwReq = new HBox(
                16,
                OwnerUi.body(
                        "\u2713 8+ Characters",
                        11,
                        OwnerUi.TEXT_MUTED
                ),
                OwnerUi.body(
                        "\u2713 1 Number",
                        11,
                        OwnerUi.TEXT_MUTED
                ),
                OwnerUi.body(
                        "\u2713 1 Special Char",
                        11,
                        OwnerUi.TEXT_MUTED
                )
        );

        pwReq.setAlignment(
                Pos.CENTER_LEFT
        );


        // ============================================================
        // TERMS
        // ============================================================

        CheckBox terms =
                new CheckBox(
                        "I agree to the Terms & Conditions and Privacy Policy."
                );

        terms.setWrapText(true);

        terms.setStyle(
                "-fx-font-size:12px;" +
                "-fx-text-fill:" +
                OwnerUi.TEXT_MUTED + ";"
        );


        // ============================================================
        // STATUS
        // ============================================================

        Label statusLabel =
                OwnerUi.body(
                        "",
                        12.5,
                        OwnerUi.TEXT_DARK
                );

        statusLabel.setWrapText(true);


        // ============================================================
        // CREATE BUTTON
        // ============================================================

        var createBtn =
                OwnerUi.primaryButton(
                        "Create Business Account  \u2192"
                );

        createBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        createBtn.setPrefHeight(43);


        // ============================================================
        // ORIGINAL CONTROLLER
        // ============================================================

        OwnerAuthController ownerAuthController =
                new OwnerAuthController();


        // ============================================================
        // ORIGINAL REGISTRATION LOGIC
        // ============================================================

        createBtn.setOnAction(event -> {

            if (!terms.isSelected()) {

                statusLabel.setText(
                        "You must agree to the Terms & Conditions."
                );

                statusLabel.setStyle(
                        "-fx-text-fill: #D32F2F;"
                );

                return;
            }


            String nameStr =
                    ownerName.getText().trim();

            String mailStr =
                    email.getText().trim();

            String phoneStr =
                    mobile.getText().trim();

            String pwdStr =
                    password.getText();

            String confirmStr =
                    confirm.getText();


            createBtn.setDisable(true);

            createBtn.setText(
                    "Creating Account..."
            );

            statusLabel.setText("");


            ownerAuthController
                    .registerOwnerWithProfile(
                            nameStr,
                            mailStr,
                            phoneStr,
                            pwdStr,
                            confirmStr
                    )
                    .whenComplete(
                            (result, throwable) ->
                                    Platform.runLater(() -> {

                                createBtn.setDisable(false);

                                createBtn.setText(
                                        "Create Business Account  \u2192"
                                );


                                if (throwable != null) {

                                    statusLabel.setText(
                                            "Registration failed: "
                                                    + throwable.getMessage()
                                    );

                                    statusLabel.setStyle(
                                            "-fx-text-fill: #D32F2F;"
                                    );

                                } else if (
                                        result != null
                                                && result.isSuccess()
                                ) {

                                    OwnerFlowRouter
                                            .goToEmailVerification(
                                                    mailStr
                                            );

                                } else {

                                    statusLabel.setText(
                                            result != null
                                                    ? result.getMessage()
                                                    : "Registration failed."
                                    );

                                    statusLabel.setStyle(
                                            "-fx-text-fill: #D32F2F;"
                                    );
                                }
                            })
                    );
        });


        // ============================================================
        // SIGN IN
        // ============================================================

        HBox signInRow =
                new HBox(4);

        signInRow.setAlignment(
                Pos.CENTER
        );


        Label already =
                OwnerUi.body(
                        "Already have an account?",
                        12.5,
                        OwnerUi.TEXT_MUTED
                );


        Label signIn =
                new Label("Sign In");

        signIn.setStyle(
                "-fx-font-size:12.5px;" +
                "-fx-font-weight:700;" +
                "-fx-text-fill:" +
                OwnerUi.ACCENT + ";" +
                "-fx-cursor:hand;"
        );


        signIn.setOnMouseClicked(event -> {

            if (callback != null) {

                callback.run();

            } else {

                OwnerFlowRouter.goToLogin();
            }
        });


        signInRow.getChildren().addAll(
                already,
                signIn
        );


        // ============================================================
        // FORM CARD
        // ============================================================

        VBox formCard =
                new VBox(
                        13,
                        title,
                        subtitle,
                        grid,
                        pwReq,
                        terms,
                        statusLabel,
                        createBtn,
                        signInRow
                );


        formCard.setPadding(
                new Insets(
                        30,
                        34,
                        26,
                        34
                )
        );

        formCard.setMaxWidth(510);

        formCard.setPrefWidth(510);

        formCard.setStyle(
                "-fx-background-color: rgba(255,255,255,0.96);" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: rgba(255,255,255,0.75);" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 20px;" +
                "-fx-effect: dropshadow(" +
                    "gaussian," +
                    "rgba(0,0,0,0.28)," +
                    "28," +
                    "0.20," +
                    "0," +
                    "10" +
                ");"
        );


        // ============================================================
        // SCROLLER
        // ============================================================

        ScrollPane scroller =
                new ScrollPane(formCard);

        scroller.setFitToWidth(true);

        scroller.setFitToHeight(false);

        scroller.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroller.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroller.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );


        // ============================================================
        // RIGHT SIDE
        // ============================================================

        VBox right =
                new VBox(scroller);

        right.setAlignment(
                Pos.CENTER
        );

        right.setPrefWidth(550);

        right.setMaxWidth(550);

        VBox.setVgrow(
                scroller,
                Priority.NEVER
        );


        // ============================================================
        // MAIN LAYOUT
        // ============================================================

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        mainLayout.getChildren().addAll(
                left,
                right
        );


        // ============================================================
        // SPOTFIT LOGO
        // ============================================================

        Label spotFitLogo =
                new Label("SpotFit");

        spotFitLogo.setStyle(
                "-fx-text-fill:white;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:900;"
        );


        StackPane.setAlignment(
                spotFitLogo,
                Pos.TOP_LEFT
        );

        StackPane.setMargin(
                spotFitLogo,
                new Insets(
                        18,
                        0,
                        0,
                        35
                )
        );


        // ============================================================
        // ADD CONTENT
        // ============================================================

        root.getChildren().add(
                mainLayout
        );

        root.getChildren().add(
                spotFitLogo
        );


        // ============================================================
        // SCENE
        // ============================================================

        registrationScene =
                OwnerUi.sizedScene(root);

        return registrationScene;
    }
}