package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.Main;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;


public class AmenitiesFacilitiesPage {

    private Scene amenitiesScene;

    Scene getScene(Runnable callback) {
        Label sub = OwnerUi.body("Select the amenities and facilities available at your space.", 13, OwnerUi.TEXT_MUTED);

        Label essentialTitle = OwnerUi.heading("Essential Facilities", 16);
        FlowPane essentials = amenityGrid(new String[]{
                "High-Speed Wi-Fi", "Air Conditioning", "Power Backup", "Parking Available",
                "Restrooms", "Drinking Water"
        });

        Label comfortTitle = OwnerUi.heading("Comfort & Convenience", 16);
        FlowPane comfort = amenityGrid(new String[]{
                "Seating Area", "Sound System", "Projector / Screen", "Whiteboard",
                "Changing Room", "First Aid Kit"
        });

        Label refreshTitle = OwnerUi.heading("Food & Refreshments", 16);
        FlowPane refresh = amenityGrid(new String[]{"Caf\u00e9 / Snacks", "Water Dispenser", "Microwave"});

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            System.out.println("back");
            callback.run();
        });

        var continueBtn = OwnerUi.primaryButton("Continue  \u2192");
        continueBtn.setOnAction(event -> {
            System.out.println("amenities");
            Runnable ran = new Runnable() {
                public void run() {
                    OwnerUi.navigate(amenitiesScene);
                }
            };
            GalleryUploadPage next = new GalleryUploadPage();
            OwnerUi.navigate(next.getScene(ran));
        });

        HBox actions = new HBox(12, backBtn, continueBtn);
        HBox.setHgrow(continueBtn, Priority.ALWAYS);
        HBox.setHgrow(backBtn, Priority.ALWAYS);

        VBox form = new VBox(18, sub, essentialTitle, essentials, comfortTitle, comfort,
                refreshTitle, refresh, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox tip = OwnerUi.card();
        tip.setStyle(tip.getStyle() + " -fx-background-color:" + OwnerUi.PRIMARY_LIGHT + ";");
        tip.getChildren().addAll(OwnerUi.heading("Can't find what you're looking for?", 14),
                OwnerUi.body("You can add a custom amenity from the wizard on your dashboard later.", 12, OwnerUi.TEXT_MUTED));

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(2), tip);

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(2, formCard, sidePanel));
        amenitiesScene = sc;
        return amenitiesScene;
    }

    private FlowPane amenityGrid(String[] items) {
        FlowPane pane = new FlowPane();
        pane.setHgap(12);
        pane.setVgap(12);
        for (String item : items) {
            CheckBox cb = new CheckBox(item);
            cb.setPrefWidth(190);
            cb.setStyle("-fx-font-size:13px; -fx-text-fill:" + OwnerUi.TEXT_DARK + "; -fx-background-color:"
                    + OwnerUi.SURFACE_LOW + "; -fx-background-radius:14; -fx-padding:12 14;");
            pane.getChildren().add(cb);
        }
        return pane;
    }
}

