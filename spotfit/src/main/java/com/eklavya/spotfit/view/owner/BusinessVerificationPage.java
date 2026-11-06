package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.Main;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class BusinessVerificationPage {

    private Scene verificationScene;

    Scene getScene(Runnable callback) {
        Label badge = OwnerUi.badge("FINAL STEP");
        Label sub = OwnerUi.body("We need a few details to verify your business identity before your space goes live.",
                13, OwnerUi.TEXT_MUTED);

        TextField fullName = OwnerUi.textField("Full legal name");
        ChoiceBox<String> idType = new ChoiceBox<>();
        idType.getItems().addAll("Aadhaar", "PAN", "Passport");
        idType.setValue("Aadhaar");
        idType.setPrefHeight(48);
        idType.setMaxWidth(Double.MAX_VALUE);
        idType.setStyle("-fx-background-color:#FFFDFB; -fx-background-radius:14; -fx-border-radius:14;"
                + " -fx-border-color:" + OwnerUi.INPUT_BORDER + "; -fx-font-size:14px;");

        VBox uploadBox = new VBox(8);
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setPadding(new Insets(28));
        uploadBox.setStyle("-fx-background-color:" + OwnerUi.SURFACE_LOW + "; -fx-background-radius:16;"
                + " -fx-border-color:" + OwnerUi.CARD_BORDER + "; -fx-border-radius:16; -fx-border-style:dashed; -fx-border-width:2;");
        uploadBox.getChildren().addAll(OwnerUi.body("Drag & Drop or Browse", 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("PDF, PNG, JPG up to 5MB", 11, OwnerUi.TEXT_MUTED));

        CheckBox authorize = new CheckBox("I authorize SpotFit to verify these details with relevant authorities.");
        authorize.setWrapText(true);
        authorize.setStyle("-fx-font-size:12.5px; -fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        var draftBtn = OwnerUi.secondaryButton("Save Draft");
        var reviewBtn = OwnerUi.primaryButton("Review & Submit  \u2192");
        reviewBtn.setOnAction(event -> {
            System.out.println("verification");
            Runnable ran = new Runnable() {
                public void run() {
                    OwnerUi.navigate(verificationScene);
                }
            };
            ReviewSubmitPage next = new ReviewSubmitPage();
            OwnerUi.navigate(next.getScene(ran));
        });
        HBox actions = new HBox(12, backBtn, draftBtn, reviewBtn);
        HBox.setHgrow(reviewBtn, Priority.ALWAYS);

        VBox form = new VBox(16, badge, sub,
                OwnerUi.fieldLabel("Full Name"), fullName,
                OwnerUi.fieldLabel("ID Type"), idType,
                OwnerUi.fieldLabel("Upload ID Document"), uploadBox,
                authorize, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox checklist = OwnerUi.card();
        checklist.getChildren().addAll(OwnerUi.heading("Verification Checklist", 14),
                OwnerUi.body("\u25CB ID Document", 12.5, OwnerUi.TEXT_MUTED),
                OwnerUi.body("\u25CB Business Proof", 12.5, OwnerUi.TEXT_MUTED),
                OwnerUi.body("\u25CB Bank Details", 12.5, OwnerUi.TEXT_MUTED));

        VBox benefits = OwnerUi.card();
        benefits.setStyle(benefits.getStyle() + " -fx-background-color:" + OwnerUi.PRIMARY_LIGHT + ";");
        benefits.getChildren().addAll(OwnerUi.heading("Benefits After Verification", 14),
                OwnerUi.body("\u2713 Verified badge on your listing", 12, OwnerUi.TEXT_DARK),
                OwnerUi.body("\u2713 Higher trust & booking rate", 12, OwnerUi.TEXT_DARK),
                OwnerUi.body("Review timeline: 24-48 hours", 12, OwnerUi.TEXT_MUTED));

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(5), checklist, benefits);

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(5, formCard, sidePanel));
        verificationScene = sc;
        return verificationScene;
    }
}

