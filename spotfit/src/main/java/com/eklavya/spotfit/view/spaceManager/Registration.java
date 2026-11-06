package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.constants.OperationCode;
import com.eklavya.spotfit.controller.manager.ManagerAuthController;
import com.eklavya.spotfit.model.AuthResult;
import com.eklavya.spotfit.navigation.AppNavigator;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


// ============================================================
// SpotFit - Manager Registration
// ============================================================

public class Registration {

    private static final String PRIMARY = "#884600";
    private static final String BACKGROUND = "#fff8f5";
    private static final String SURFACE = "#ffffff";
    private static final String TEXT = "#2e1503";
    private static final String SECONDARY_TEXT = "#534439";
    private static final String CHIP_BG = "#ffeade";
    private static final String BORDER = "#d9c2b4";


    private final ManagerAuthController authController =
            new ManagerAuthController();

    private Scene registrationPageScene;


    // =========================================================
    // REGISTRATION PAGE
    // =========================================================

    public Scene getRegistrationPageScene(
            Runnable callbackHome
    ) {


        // -----------------------------------------------------
        // LEFT PANEL - UI ONLY
        // -----------------------------------------------------

        Label logo =
                new Label("SpotFit");

        logo.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;"
        );


        Label subLogo =
                new Label("WORKSPACE MANAGEMENT");

        subLogo.setStyle(
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;" +
                "-fx-opacity:0.88;"
        );


        Label invitationOnly =
                new Label("♙   INVITATION ONLY");

        invitationOnly.setStyle(
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;" +
                "-fx-padding:7 12;" +
                "-fx-border-color:rgba(255,255,255,0.55);" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:18px;" +
                "-fx-background-color:rgba(255,255,255,0.06);" +
                "-fx-background-radius:18px;"
        );


        VBox brandBox =
                new VBox(
                        1,
                        logo,
                        subLogo
                );


        Region brandSpacer =
                new Region();

        HBox.setHgrow(
                brandSpacer,
                Priority.ALWAYS
        );


        HBox leftTop =
                new HBox(
                        12,
                        brandBox,
                        brandSpacer,
                        invitationOnly
                );

        leftTop.setAlignment(
                Pos.CENTER_LEFT
        );


        Label pitchTitle =
                new Label(
                        "Elevate your\nworkspace experience."
                );

        pitchTitle.setWrapText(true);
        pitchTitle.setMaxWidth(290);

        pitchTitle.setStyle(
                "-fx-font-size:26px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:white;"
        );


        Label pitchBody =
                new Label(
                        "You've been invited to join a network of space managers.\n"
                        +
                        "Complete your registration to start managing your assigned space."
                );

        pitchBody.setWrapText(true);
        pitchBody.setMaxWidth(292);

        pitchBody.setStyle(
                "-fx-font-size:12px;" +
                "-fx-text-fill:white;" +
                "-fx-opacity:0.92;"
        );


        Label benefit1 = createBenefitLabel("✓", "Manage your assigned space");
        Label benefit2 = createBenefitLabel("✓", "Track bookings & customers");
        Label benefit3 = createBenefitLabel("✓", "Handle daily check-ins");
        Label benefit4 = createBenefitLabel("✓", "Monitor space activity");


        VBox benefits =
                new VBox(
                        12,
                        benefit1,
                        benefit2,
                        benefit3,
                        benefit4
                );


        VBox leftBottom =
                new VBox(
                        14,
                        pitchTitle,
                        pitchBody,
                        benefits
                );


        VBox leftPanel =
                new VBox(35,
                        leftTop,
                        leftBottom
                );


        leftPanel.setPadding(
                new Insets(28)
        );

        leftPanel.setPrefWidth(350);
        leftPanel.setMinWidth(350);
        leftPanel.setMaxWidth(350);

        leftPanel.setStyle(
                "-fx-background-color:rgba(87,45,13,0.64);" +
                "-fx-background-radius:22px 0 0 22px;" +
                "-fx-border-color:rgba(255,255,255,0.45);" +
                "-fx-border-width:1px 0 1px 1px;" +
                "-fx-border-radius:22px 0 0 22px;"
        );


        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        Label title =
                new Label(
                        "Complete Your Invitation"
                );

        title.setStyle(
                "-fx-font-size:25px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" +
                TEXT +
                ";"
        );


        Label subtitle =
                new Label(
                        "This is an exclusive portal for verified "
                        +
                        "Space Managers with an Owner invitation code."
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size:11px;" +
                "-fx-text-fill:" +
                SECONDARY_TEXT +
                ";"
        );


        // -----------------------------------------------------
        // REQUIRED CHIP
        // -----------------------------------------------------

        Label requiredChip =
                new Label(
                        "REQUIRED FOR ACCESS"
                );

        requiredChip.setStyle(
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" +
                PRIMARY +
                ";" +
                "-fx-background-color:" +
                CHIP_BG +
                ";" +
                "-fx-background-radius:20px;" +
                "-fx-padding:4 12;"
        );


        // -----------------------------------------------------
        // INVITATION CODE
        // -----------------------------------------------------

        TextField tokenField =
                createField(
                        "Enter invitation code (e.g. XXXX-XXXX-XXXX-XXXX)"
                );


        VBox tokenBox =
                new VBox(
                        5,
                        requiredChip,
                        fieldLabel("Invitation Code *"),
                        tokenField
                );

        tokenBox.setPadding(
                new Insets(12)
        );

        tokenBox.setStyle(
                "-fx-background-color:" +
                CHIP_BG +
                ";" +
                "-fx-background-radius:10px;"
        );


        // -----------------------------------------------------
        // OTHER FIELDS
        // -----------------------------------------------------

        TextField nameField =
                createField(
                        "Full Name"
                );


        TextField emailField =
                createField(
                        "Manager Email"
                );


        TextField phoneField =
                createField(
                        "Phone Number (+91...)"
                );


        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Create password"
        );

        stylePasswordField(
                passwordField
        );


        PasswordField confirmPasswordField =
                new PasswordField();

        confirmPasswordField.setPromptText(
                "Confirm password"
        );

        stylePasswordField(
                confirmPasswordField
        );


        // -----------------------------------------------------
        // ERROR LABEL
        // -----------------------------------------------------

        Label errorLabel =
                new Label();

        errorLabel.setStyle(
                "-fx-font-size:13px;" +
                "-fx-text-fill:#ba1a1a;" +
                "-fx-font-weight:bold;"
        );

        errorLabel.setWrapText(true);

        errorLabel.setVisible(false);


        // -----------------------------------------------------
        // RECOVERY BUTTON
        // -----------------------------------------------------

        Button recoveryButton =
                new Button(
                        "Sign In & Continue Invitation →"
                );

        recoveryButton.setMaxWidth(
                Double.MAX_VALUE
        );

        recoveryButton.setPrefHeight(38);

        recoveryButton.setStyle(
                "-fx-background-color:#1E293B;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:12px;"
        );

        recoveryButton.setVisible(false);


        // =====================================================
        // IMPORTANT FIX
        // =====================================================

        recoveryButton.setOnAction(e -> {

            LoginPage loginPage =
                    new LoginPage();

            /*
             * LoginPage now accepts only:
             *
             * getLoginPageScene(Runnable callbackHome)
             *
             * Therefore DO NOT pass email/token here.
             */

            AppNavigator.setScene(
                    loginPage.getLoginPageScene(
                            callbackHome
                    )
            );

            AppNavigator.setMaximized(
                    true
            );
        });


        // -----------------------------------------------------
        // REGISTER BUTTON
        // -----------------------------------------------------

        Button continueButton =
                new Button(
                        "Register & Send Verification Link →"
                );

        continueButton.setMaxWidth(
                Double.MAX_VALUE
        );

        continueButton.setPrefHeight(38);

        continueButton.setStyle(
                "-fx-background-color:" +
                PRIMARY +
                ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:12px;"
        );


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        Button backButton =
                new Button(
                        "Back"
                );

        backButton.setMaxWidth(
                Double.MAX_VALUE
        );

        backButton.setPrefHeight(
                36
        );

        backButton.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:" +
                PRIMARY +
                ";" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-border-color:" +
                PRIMARY +
                ";" +
                "-fx-border-width:1.5px;" +
                "-fx-border-radius:12px;"
        );


        backButton.setOnAction(
                event -> callbackHome.run()
        );


        // =====================================================
        // REGISTRATION ACTION
        // =====================================================

        continueButton.setOnAction(event -> {

            errorLabel.setStyle(
                    "-fx-text-fill:#EF4444;" +
                    "-fx-font-size:13px;" +
                    "-fx-wrap-text:true;"
            );

            errorLabel.setVisible(false);

            recoveryButton.setVisible(false);


            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------

            if (
                    tokenField.getText()
                            .trim()
                            .isEmpty()
                    ||
                    nameField.getText()
                            .trim()
                            .isEmpty()
                    ||
                    emailField.getText()
                            .trim()
                            .isEmpty()
                    ||
                    passwordField.getText()
                            .isEmpty()
            ) {

                errorLabel.setText(
                        "Please fill in all required fields."
                );

                errorLabel.setVisible(true);

                return;
            }


            // -------------------------------------------------
            // START REGISTRATION
            // -------------------------------------------------

            continueButton.setDisable(true);

            continueButton.setText(
                    "Validating invitation..."
            );


            errorLabel.setStyle(
                    "-fx-text-fill:#2563EB;" +
                    "-fx-font-size:13px;" +
                    "-fx-wrap-text:true;"
            );


            errorLabel.setText(
                    "Validating invitation details..."
            );

            errorLabel.setVisible(true);


            // -------------------------------------------------
            // API / CONTROLLER CALL
            // -------------------------------------------------

            authController.registerManager(
                    tokenField.getText().trim(),
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    passwordField.getText(),
                    confirmPasswordField.getText()
            )
            .thenAccept(
                    res ->
                            Platform.runLater(
                                    () -> {

                                        continueButton.setDisable(
                                                false
                                        );

                                        continueButton.setText(
                                                "Register & Send Verification Link →"
                                        );


                                        errorLabel.setStyle(
                                                "-fx-text-fill:#EF4444;" +
                                                "-fx-font-size:13px;" +
                                                "-fx-wrap-text:true;"
                                        );


                                        // -------------------------
                                        // REGISTRATION FAILED
                                        // -------------------------

                                        if (res.isFailure()) {

                                            errorLabel.setText(
                                                    res.getMessage()
                                            );

                                            errorLabel.setVisible(
                                                    true
                                            );


                                            if (
                                                    OperationCode
                                                            .EMAIL_ALREADY_EXISTS
                                                            .equals(
                                                                    res.getErrorCode()
                                                            )
                                                    ||
                                                    (
                                                            res.getMessage()
                                                                    != null
                                                            &&
                                                            res.getMessage()
                                                                    .contains(
                                                                            "already exists"
                                                                    )
                                                    )
                                            ) {

                                                recoveryButton
                                                        .setVisible(
                                                                true
                                                        );
                                            }


                                        }

                                        // -------------------------
                                        // REGISTRATION SUCCESS
                                        // -------------------------

                                        else {

                                            AuthResult authResult =
                                                    res.getData();


                                            VerifyInvite verifyPage =
                                                    new VerifyInvite();


                                            Runnable callbackRegistration =
                                                    this::backToRegistrationPage;


                                            AppNavigator.setScene(
                                                    verifyPage
                                                            .getVerifyPageScene(
                                                                    authResult,
                                                                    tokenField
                                                                            .getText()
                                                                            .trim(),
                                                                    nameField
                                                                            .getText()
                                                                            .trim(),
                                                                    phoneField
                                                                            .getText()
                                                                            .trim(),
                                                                    emailField
                                                                            .getText()
                                                                            .trim(),
                                                                    callbackRegistration,
                                                                    callbackHome
                                                            )
                                            );


                                            AppNavigator.setMaximized(
                                                    true
                                            );
                                        }
                                    }
                            )
            )
            .exceptionally(
                    ex -> {

                        Platform.runLater(
                                () -> {

                                    continueButton.setDisable(
                                            false
                                    );

                                    continueButton.setText(
                                            "Register & Send Verification Link →"
                                    );


                                    errorLabel.setStyle(
                                            "-fx-text-fill:#EF4444;" +
                                            "-fx-font-size:13px;" +
                                            "-fx-wrap-text:true;"
                                    );


                                    errorLabel.setText(
                                            "An unexpected error occurred. Please try again."
                                    );

                                    errorLabel.setVisible(
                                            true
                                    );
                                }
                        );

                        return null;
                    }
            );
        });


        // -----------------------------------------------------
        // FORM - COMPACT REFERENCE LAYOUT
        // -----------------------------------------------------

        VBox nameBox =
                new VBox(
                        5,
                        fieldLabel("Full Name *"),
                        nameField
                );

        VBox emailBox =
                new VBox(
                        5,
                        fieldLabel("Email Address *"),
                        emailField
                );

        VBox phoneBox =
                new VBox(
                        5,
                        fieldLabel("Phone Number"),
                        phoneField
                );


        VBox passwordBox =
                new VBox(
                        5,
                        fieldLabel("Create Password *"),
                        passwordField
                );

        VBox confirmPasswordBox =
                new VBox(
                        5,
                        fieldLabel("Confirm Password *"),
                        confirmPasswordField
                );

        HBox.setHgrow(
                passwordBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                confirmPasswordBox,
                Priority.ALWAYS
        );

        passwordBox.setMaxWidth(
                Double.MAX_VALUE
        );

        confirmPasswordBox.setMaxWidth(
                Double.MAX_VALUE
        );


        HBox passwordRow =
                new HBox(
                        12,
                        passwordBox,
                        confirmPasswordBox
                );


        VBox form =
                new VBox(
                        10,

                        title,
                        subtitle,
                        tokenBox,

                        nameBox,
                        emailBox,
                        phoneBox,
                        passwordRow,

                        errorLabel,
                        recoveryButton,
                        continueButton,
                        backButton
                );


        form.setPadding(
                new Insets(24)
        );

        form.setPrefWidth(
                520
        );

        form.setMinWidth(
                520
        );

        form.setMaxWidth(
                520
        );

        form.setStyle(
                "-fx-background-color:" +
                SURFACE +
                ";" +
                "-fx-background-radius:0 22px 22px 0;"
        );


        HBox.setHgrow(
                form,
                Priority.ALWAYS
        );


        // -----------------------------------------------------
        // CARD
        // -----------------------------------------------------

        HBox card =
                new HBox(
                        leftPanel,
                        form
                );

        card.setAlignment(
                Pos.CENTER
        );

        card.setMaxWidth(
                900
        );

        card.setPrefWidth(
                900
        );

        card.setMinWidth(
                900
        );

        card.setMaxHeight(
                660
        );

        card.setPrefHeight(
                660
        );

        card.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-background-radius:22px;" +
                "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.28), 30, 0.18, 0, 10);"
        );


        // -----------------------------------------------------
        // FULL-SCREEN BACKGROUND - NO SCROLL
        // -----------------------------------------------------

        ImageView backgroundImage =
                new ImageView();

        java.net.URL backgroundUrl =
                getClass().getResource(
                        "/images/owner_background.png"
                );

        if (backgroundUrl == null) {
            backgroundUrl =
                    getClass().getResource(
                            "/assets/images/business.jpeg"
                    );
        }

        if (backgroundUrl != null) {
            backgroundImage.setImage(
                    new Image(
                            backgroundUrl.toExternalForm()
                    )
            );
        }

        backgroundImage.setPreserveRatio(
                false
        );

        backgroundImage.setSmooth(
                true
        );


        Region backgroundTint =
                new Region();

        backgroundTint.setStyle(
                "-fx-background-color:rgba(73,40,16,0.16);"
        );


        StackPane root =
                new StackPane(
                        backgroundImage,
                        backgroundTint,
                        card
                );

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(20)
        );

        root.setStyle(
                "-fx-background-color:" +
                BACKGROUND +
                ";"
        );


        backgroundImage.fitWidthProperty()
                .bind(
                        root.widthProperty()
                );

        backgroundImage.fitHeightProperty()
                .bind(
                        root.heightProperty()
                );

        backgroundTint.prefWidthProperty()
                .bind(
                        root.widthProperty()
                );

        backgroundTint.prefHeightProperty()
                .bind(
                        root.heightProperty()
                );


        // -----------------------------------------------------
        // SCENE SIZE
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


        registrationPageScene =
                new Scene(
                        root,
                        w,
                        h
                );


        return registrationPageScene;
    }


    // =========================================================
    // FIELD LABEL
    // =========================================================

    private Label fieldLabel(
            String text
    ) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" +
                TEXT +
                ";"
        );

        return label;
    }


    // =========================================================
    // CREATE TEXT FIELD
    // =========================================================

    private TextField createField(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                36
        );

        field.setStyle(
                "-fx-background-color:" +
                SURFACE +
                ";" +
                "-fx-font-size:12px;" +
                "-fx-background-radius:9px;" +
                "-fx-border-color:" +
                BORDER +
                ";" +
                "-fx-border-radius:9px;" +
                "-fx-padding:7 12;"
        );

        return field;
    }


    // =========================================================
    // PASSWORD FIELD STYLE
    // =========================================================

    private void stylePasswordField(
            PasswordField field
    ) {

        field.setPrefHeight(
                36
        );

        field.setMaxWidth(
                Double.MAX_VALUE
        );

        field.setStyle(
                "-fx-background-color:" +
                SURFACE +
                ";" +
                "-fx-font-size:12px;" +
                "-fx-background-radius:9px;" +
                "-fx-border-color:" +
                BORDER +
                ";" +
                "-fx-border-radius:9px;" +
                "-fx-padding:7 12;"
        );
    }


    // =========================================================
    // LEFT PANEL BENEFIT ROW - UI ONLY
    // =========================================================

    private Label createBenefitLabel(
            String icon,
            String text
    ) {

        Label label =
                new Label(
                        icon + "   " + text
                );

        label.setStyle(
                "-fx-font-size:12px;" +
                "-fx-text-fill:white;" +
                "-fx-font-weight:500;"
        );

        return label;
    }


    // =========================================================
    // SPACER
    // =========================================================

    private Region spacer() {

        Region region =
                new Region();

        VBox.setVgrow(
                region,
                Priority.ALWAYS
        );

        return region;
    }


    // =========================================================
    // BACK TO REGISTRATION
    // =========================================================

    void backToRegistrationPage() {

        AppNavigator.setScene(
                registrationPageScene
        );

        AppNavigator.setMaximized(
                true
        );
    }
}