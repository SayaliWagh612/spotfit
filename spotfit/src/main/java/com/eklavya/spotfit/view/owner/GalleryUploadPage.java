package com.eklavya.spotfit.view.owner;

import javafx.scene.Node;
import com.eklavya.spotfit.Main;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class GalleryUploadPage {

    private Scene galleryScene;

    Scene getScene(Runnable callback) {
        Label sub = OwnerUi.body("Gallery Upload & Space Media", 13, OwnerUi.TEXT_MUTED);

        VBox coverDrop = dropzone("Drag and drop your primary cover photo", "High resolution JPEG or PNG, max 10MB");
        VBox coverBox = new VBox(6, new HBox(6, OwnerUi.fieldLabel("Cover Image"), OwnerUi.badge("Required")), coverDrop);

        Label galleryLabel = OwnerUi.body("Add additional photos to give guests a complete view.", 12.5, OwnerUi.TEXT_MUTED);
        FlowPane gallery = new FlowPane();
        gallery.setHgap(12);
        gallery.setVgap(12);
        // Paste up to 4 image links here - shows placeholder boxes until you do.
        String[] GALLERY_IMAGE_URLS = {"", "", "", ""};
        for (int i = 0; i < 4; i++) {
            Node thumb = OwnerUi.imageOrPlaceholder(GALLERY_IMAGE_URLS[i], 130, 100, 12);
            gallery.getChildren().add(thumb);
        }
        VBox galleryBox = new VBox(6, OwnerUi.fieldLabel("Space Gallery"), galleryLabel, gallery);

        VBox videoDrop = dropzone("Select MP4 Video", "Upload a short, 60-second walk-through to boost engagement.");
        VBox videoBox = new VBox(6, new HBox(6, OwnerUi.fieldLabel("Video Walkthrough"), OwnerUi.badge("Optional")), videoDrop);

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        var continueBtn = OwnerUi.primaryButton("Continue  \u2192");
        continueBtn.setOnAction(event -> {
            System.out.println("gallery");
            Runnable ran = new Runnable() {
                public void run() {
                    OwnerUi.navigate(galleryScene);
                }
            };
            PricingAvailabilityPage next = new PricingAvailabilityPage();
            OwnerUi.navigate(next.getScene(ran));
        });

        HBox actions = new HBox(12, backBtn, continueBtn);
        HBox.setHgrow(continueBtn, Priority.ALWAYS);
        HBox.setHgrow(backBtn, Priority.ALWAYS);

        VBox form = new VBox(18, sub, coverBox, galleryBox, videoBox, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox suggestions = OwnerUi.card();
        suggestions.getChildren().addAll(OwnerUi.heading("Media Quality Suggestions", 14),
                OwnerUi.body("\uD83D\uDCA1 Add an exterior image to help guests find the location.", 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body("\u2713 Great lighting in workspace photos.", 12, OwnerUi.SUCCESS));

        VBox completion = OwnerUi.card();
        completion.getChildren().addAll(OwnerUi.heading("Completion Status", 14),
                OwnerUi.body("Photos Uploaded: 4 / 8 recommended", 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Video Walkthrough: Not uploaded", 12, OwnerUi.TEXT_MUTED));

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(3), suggestions, completion);

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(3, formCard, sidePanel));
        galleryScene = sc;
        return galleryScene;
    }

    private VBox dropzone(String title, String subtitle) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color:" + OwnerUi.SURFACE_LOW + "; -fx-background-radius:16;"
                + " -fx-border-color:" + OwnerUi.CARD_BORDER + "; -fx-border-radius:16; -fx-border-style:dashed; -fx-border-width:2;");
        box.getChildren().addAll(OwnerUi.body(title, 13, OwnerUi.TEXT_DARK), OwnerUi.body(subtitle, 11, OwnerUi.TEXT_MUTED));
        return box;
    }
}

