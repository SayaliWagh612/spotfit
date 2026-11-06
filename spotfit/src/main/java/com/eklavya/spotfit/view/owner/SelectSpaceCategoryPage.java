package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerSpaceController;
import com.eklavya.spotfit.model.ActivityType;

import com.eklavya.spotfit.model.SpaceType;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.layout.*;

import java.util.List;

public class SelectSpaceCategoryPage {

    private final OwnerSpaceController controller = new OwnerSpaceController();

    private String selectedCategory = "Creativity";
    private String selectedActivityGroup = "Content Creation";
    private String selectedSpaceType = "Creator Studio";

    private Scene categoryScene;
    private Runnable callback;

    Scene getScene(Runnable callback) {
        this.callback = callback;

        Label headerTitle = OwnerUi.heading("Space Classification", 20);
        Label sub = OwnerUi.body("Select category, activity group, and space type for your new spot.", 13, OwnerUi.TEXT_MUTED);

        // Step 1: Category Cards
        Label step1Label = OwnerUi.heading("Step 1: Choose Primary Category", 14);
        HBox categoryGrid = new HBox(12);
        for (String cat : controller.getCategories()) {
            categoryGrid.getChildren().add(createCategoryCard(cat));
        }

        // Step 2: Activity Group Selection
        Label step2Label = OwnerUi.heading("Step 2: Choose Activity Group", 14);
        ComboBox<String> activityCombo = new ComboBox<>();
        activityCombo.setStyle("-fx-font-size:13px; -fx-padding:6;");
        activityCombo.setMaxWidth(Double.MAX_VALUE);
        populateActivityGroups(activityCombo);

        // Step 3: Space Type Selection
        Label step3Label = OwnerUi.heading("Step 3: Choose Space Type", 14);
        ComboBox<String> spaceTypeCombo = new ComboBox<>();
        spaceTypeCombo.setStyle("-fx-font-size:13px; -fx-padding:6;");
        spaceTypeCombo.setMaxWidth(Double.MAX_VALUE);
        populateSpaceTypes(activityCombo, spaceTypeCombo);

        activityCombo.setOnAction(e -> {
            selectedActivityGroup = activityCombo.getValue();
            populateSpaceTypes(activityCombo, spaceTypeCombo);
        });

        spaceTypeCombo.setOnAction(e -> {
            selectedSpaceType = spaceTypeCombo.getValue();
        });

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            if (callback != null) callback.run();
        });

        var continueBtn = OwnerUi.primaryButton("Continue to Space Details \u2192");
        continueBtn.setOnAction(event -> {
            controller.setClassification(selectedCategory, selectedActivityGroup, selectedSpaceType);

            Runnable ran = () -> OwnerUi.navigate(categoryScene);
            SpaceInformationWizardPage next = new SpaceInformationWizardPage(controller);
            OwnerUi.navigate(next.getScene(ran));
        });

        HBox actions = new HBox(12, backBtn, continueBtn);
        HBox.setHgrow(continueBtn, Priority.ALWAYS);
        HBox.setHgrow(backBtn, Priority.ALWAYS);

        VBox form = new VBox(14, headerTitle, sub, step1Label, categoryGrid, step2Label, activityCombo, step3Label, spaceTypeCombo, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox tip = OwnerUi.card();
        tip.setStyle(tip.getStyle() + " -fx-background-color:" + OwnerUi.PRIMARY_LIGHT + ";");
        tip.getChildren().addAll(
                OwnerUi.heading("Category Insight", 14),
                OwnerUi.body("Taxonomy Structure:\nCategory -> Activity Group -> Space Type -> Space -> Offerings -> Resources", 12, OwnerUi.TEXT_MUTED)
        );

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(0), tip);

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(0, formCard, sidePanel));
        categoryScene = sc;
        return sc;
    }

    private VBox createCategoryCard(String cat) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setPrefWidth(140);
        card.setAlignment(Pos.CENTER);
        boolean active = cat.equalsIgnoreCase(selectedCategory);
        card.setStyle("-fx-background-color:" + (active ? OwnerUi.PRIMARY_LIGHT : OwnerUi.SURFACE)
                + "; -fx-background-radius:12; -fx-border-radius:12; -fx-border-width:2; -fx-border-color:"
                + (active ? OwnerUi.ACCENT : OwnerUi.CARD_BORDER) + "; -fx-cursor:hand;");

        Label title = OwnerUi.heading(cat, 13);
        title.setWrapText(true);
        title.setAlignment(Pos.CENTER);

        card.getChildren().add(title);
        card.setOnMouseClicked(event -> {
            selectedCategory = cat;
            OwnerUi.navigate(getScene(callback));
        });
        return card;
    }

    private void populateActivityGroups(ComboBox<String> combo) {
        combo.getItems().clear();
        List<ActivityType> list = controller.getActivityGroupsForCategory(selectedCategory);
        for (ActivityType act : list) {
            combo.getItems().add(act.getName());
        }
        if (!combo.getItems().isEmpty()) {
            combo.getSelectionModel().select(0);
            selectedActivityGroup = combo.getItems().get(0);
        }
    }

    private void populateSpaceTypes(ComboBox<String> actCombo, ComboBox<String> spaceTypeCombo) {
        spaceTypeCombo.getItems().clear();
        String currentActName = actCombo.getValue();
        if (currentActName != null) {
            List<ActivityType> actList = controller.getActivityGroupsForCategory(selectedCategory);
            ActivityType matchedAct = actList.stream().filter(a -> a.getName().equals(currentActName)).findFirst().orElse(null);
            if (matchedAct != null) {
                List<SpaceType> stList = controller.getSpaceTypesForActivityGroup(matchedAct.getActivityTypeId());
                for (SpaceType st : stList) {
                    spaceTypeCombo.getItems().add(st.getName());
                }
            }
        }
        if (spaceTypeCombo.getItems().isEmpty()) {
            spaceTypeCombo.getItems().add(selectedCategory + " Room");
        }
        spaceTypeCombo.getSelectionModel().select(0);
        selectedSpaceType = spaceTypeCombo.getItems().get(0);
    }
}

