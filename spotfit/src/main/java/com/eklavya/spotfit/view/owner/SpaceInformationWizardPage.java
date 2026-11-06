package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerSpaceController;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;

public class SpaceInformationWizardPage {

    private final OwnerSpaceController controller;
    private Scene wizardScene;

    public SpaceInformationWizardPage() {
        this(new OwnerSpaceController());
    }

    public SpaceInformationWizardPage(OwnerSpaceController controller) {
        this.controller = controller;
    }

    Scene getScene(Runnable callback) {
        Label headerTitle = OwnerUi.heading("Step 4: Space Details & Operating Schedule", 18);
        Label sub = OwnerUi.body("Provide the basic operational and contact details for this space.", 13, OwnerUi.TEXT_MUTED);

        TextField spaceName = OwnerUi.textField("e.g. Rhythm Podcast Studio");
        TextArea description = OwnerUi.textArea("Describe what makes your space special...", 3);

        TextField address = OwnerUi.textField("Building & Street Address");
        TextField city = OwnerUi.textField("City (e.g. Ahmedabad)");

        TextField capacity = OwnerUi.textField("Capacity (e.g. 15)");
        TextField contactEmail = OwnerUi.textField("contact@yourspace.com");
        TextField contactPhone = OwnerUi.textField("+91 98765 00000");

        TextField operatingDays = OwnerUi.textField("e.g. Mon - Sat");
        TextField openTime = OwnerUi.textField("08:00 AM");
        TextField closeTime = OwnerUi.textField("09:00 PM");

        TextField rules = OwnerUi.textField("Rules (e.g. No smoking, quiet area)");
        TextField cancellation = OwnerUi.textField("Cancellation Policy (e.g. 24h refund)");

        CheckBox cbWifi = new CheckBox("Wi-Fi");
        CheckBox cbAC = new CheckBox("Air Conditioning");
        CheckBox cbParking = new CheckBox("Parking");
        CheckBox cbSoundproof = new CheckBox("Soundproofing");

        HBox amenitiesBox = new HBox(16, cbWifi, cbAC, cbParking, cbSoundproof);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        grid.add(OwnerUi.fieldLabel("Address *"), 0, 0);
        grid.add(address, 0, 1);
        grid.add(OwnerUi.fieldLabel("City *"), 1, 0);
        grid.add(city, 1, 1);

        grid.add(OwnerUi.fieldLabel("Capacity *"), 0, 2);
        grid.add(capacity, 0, 3);
        grid.add(OwnerUi.fieldLabel("Contact Email *"), 1, 2);
        grid.add(contactEmail, 1, 3);

        grid.add(OwnerUi.fieldLabel("Operating Days"), 0, 4);
        grid.add(operatingDays, 0, 5);
        grid.add(OwnerUi.fieldLabel("Hours (Open - Close)"), 1, 4);
        HBox hoursBox = new HBox(8, openTime, closeTime);
        grid.add(hoursBox, 1, 5);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            if (callback != null) callback.run();
        });

        var continueBtn = OwnerUi.primaryButton("Continue to Offerings \u2192");
        continueBtn.setOnAction(event -> {
            int cap = 20;
            try { cap = Integer.parseInt(capacity.getText().trim()); } catch (Exception ignored) {}

            List<String> amenities = Arrays.asList("Wi-Fi", "Air Conditioning");
            controller.setSpaceDetails(
                    spaceName.getText(), description.getText(), address.getText(), city.getText(),
                    cap, contactEmail.getText(), contactPhone.getText(),
                    operatingDays.getText(), openTime.getText(), closeTime.getText(),
                    rules.getText(), cancellation.getText(), amenities
            );

            Runnable ran = () -> OwnerUi.navigate(wizardScene);
            AmenitiesFacilitiesPage next = new AmenitiesFacilitiesPage();
            OwnerUi.navigate(next.getScene(ran));
        });

        HBox actions = new HBox(12, backBtn, continueBtn);
        HBox.setHgrow(continueBtn, Priority.ALWAYS);
        HBox.setHgrow(backBtn, Priority.ALWAYS);

        VBox form = new VBox(14, headerTitle, sub,
                OwnerUi.fieldLabel("Space Name *"), spaceName,
                OwnerUi.fieldLabel("Description *"), description,
                grid,
                OwnerUi.fieldLabel("Amenities"), amenitiesBox,
                OwnerUi.fieldLabel("Rules & Cancellation"), rules, cancellation,
                actions);

        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(1));

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(1, formCard, sidePanel));
        wizardScene = sc;
        return wizardScene;
    }
}

