package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.model.Business;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * View form for creating a new V2 Business organization profile.
 */
public class CreateBusinessPage {

    private final BusinessController businessController;

    public CreateBusinessPage() {
        this.businessController = new BusinessController();
    }

    public Node getContent() {
        VBox card = OwnerUi.card();
        card.setSpacing(20);

        Label heading = OwnerUi.heading("Create New Business", 22);
        Label sub = OwnerUi.body("Provide your organization's legal, category, and contact details.", 13, OwnerUi.TEXT_MUTED);

        // Basic Info Fields
        TextField businessName = OwnerUi.textField("");
        businessName.setPromptText("e.g. Rhythm Creative Studios");

        TextField legalName = OwnerUi.textField("");
        legalName.setPromptText("Legal entity name (optional)");

        TextField businessEmail = OwnerUi.textField("");
        businessEmail.setPromptText("official@business.com");

        TextField businessPhone = OwnerUi.textField("");
        businessPhone.setPromptText("10-digit Phone Number");

        TextArea description = OwnerUi.textArea("Brief description of your business organization...", 3);

        // Category & Activity Groups (V2)
        Label categoryValueLabel = OwnerUi.body("Creativity", 14, OwnerUi.TEXT_DARK);
        categoryValueLabel.setStyle("-fx-font-weight: bold; -fx-padding: 6 12; -fx-background-color: #F5F5F5; -fx-background-radius: 4;");
        Label categoryNote = OwnerUi.body("More SpotFit categories will be enabled in a future phase.", 12, OwnerUi.TEXT_MUTED);
        VBox categoryBox = new VBox(4, categoryValueLabel, categoryNote);

        CheckBox cbArt = new CheckBox("Art");
        cbArt.setMnemonicParsing(false);
        CheckBox cbContent = new CheckBox("Content Creation");
        cbContent.setMnemonicParsing(false);
        CheckBox cbPodcasts = new CheckBox("Podcasts");
        cbPodcasts.setMnemonicParsing(false);
        CheckBox cbMusic = new CheckBox("Music");
        cbMusic.setMnemonicParsing(false);

        HBox activityGroupsBox = new HBox(16, cbArt, cbContent, cbPodcasts, cbMusic);

        TextField website = OwnerUi.textField("");
        website.setPromptText("e.g. https://mybusiness.com (optional)");

        // Address Fields
        TextField addressLine = OwnerUi.textField("");
        addressLine.setPromptText("Street Address / Building");

        TextField landmark = OwnerUi.textField("");
        landmark.setPromptText("Landmark (optional)");

        TextField city = OwnerUi.textField("");
        city.setPromptText("e.g. Pune");

        TextField state = OwnerUi.textField("");
        state.setPromptText("e.g. Maharashtra");

        TextField postalCode = OwnerUi.textField("");
        postalCode.setPromptText("e.g. 411001");

        TextField registrationNumber = OwnerUi.textField("");
        registrationNumber.setPromptText("GST / Registration No. (optional)");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        int row = 0;
        grid.add(OwnerUi.fieldLabel("Business Category *"), 0, row);
        grid.add(categoryBox, 0, row + 1, 2, 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Activity Groups * (Select at least one)"), 0, row);
        grid.add(activityGroupsBox, 0, row + 1, 2, 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Business Name *"), 0, row);
        grid.add(businessName, 0, row + 1);
        grid.add(OwnerUi.fieldLabel("Legal Name"), 1, row);
        grid.add(legalName, 1, row + 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Business Email *"), 0, row);
        grid.add(businessEmail, 0, row + 1);
        grid.add(OwnerUi.fieldLabel("Business Phone *"), 1, row);
        grid.add(businessPhone, 1, row + 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Website"), 0, row);
        grid.add(website, 0, row + 1);
        grid.add(OwnerUi.fieldLabel("Registration No."), 1, row);
        grid.add(registrationNumber, 1, row + 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Address Line *"), 0, row);
        grid.add(addressLine, 0, row + 1);
        grid.add(OwnerUi.fieldLabel("Landmark"), 1, row);
        grid.add(landmark, 1, row + 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("City *"), 0, row);
        grid.add(city, 0, row + 1);
        grid.add(OwnerUi.fieldLabel("State *"), 1, row);
        grid.add(state, 1, row + 1);
        row += 2;

        grid.add(OwnerUi.fieldLabel("Postal Code *"), 0, row);
        grid.add(postalCode, 0, row + 1);
        row += 2;

        VBox descBox = new VBox(6, OwnerUi.fieldLabel("Business Description"), description);

        Label statusLabel = OwnerUi.body("", 13, OwnerUi.TEXT_DARK);
        statusLabel.setWrapText(true);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = OwnerUi.secondaryButton("Cancel");
        cancelBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessList());

        Button saveBtn = OwnerUi.primaryButton("Save Business");
        saveBtn.setOnAction(e -> {
            List<String> selectedGroups = new ArrayList<>();
            if (cbArt.isSelected()) selectedGroups.add(CreativityActivityGroup.ART);
            if (cbContent.isSelected()) selectedGroups.add(CreativityActivityGroup.CONTENT_CREATION);
            if (cbPodcasts.isSelected()) selectedGroups.add(CreativityActivityGroup.PODCASTS);
            if (cbMusic.isSelected()) selectedGroups.add(CreativityActivityGroup.MUSIC);

            if (selectedGroups.isEmpty()) {
                statusLabel.setText("Please select at least one Activity Group.");
                statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                return;
            }

            String webStr = website.getText().trim();
            if (!webStr.isBlank() && !webStr.toLowerCase().startsWith("http://") && !webStr.toLowerCase().startsWith("https://")) {
                statusLabel.setText("Website URL must start with http:// or https://");
                statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                return;
            }

            saveBtn.setDisable(true);
            saveBtn.setText("Saving Business...");
            statusLabel.setText("");

            Business v2Business = Business.createV2(
                    "", // Generated backend-side
                    "", // Derived backend-side
                    businessName.getText().trim(),
                    legalName.getText().trim(),
                    businessEmail.getText().trim(),
                    businessPhone.getText().trim(),
                    description.getText().trim(),
                    addressLine.getText().trim(),
                    landmark.getText().trim(),
                    city.getText().trim(),
                    state.getText().trim(),
                    postalCode.getText().trim(),
                    registrationNumber.getText().trim(),
                    SpaceCategory.CREATIVITY,
                    selectedGroups,
                    "", // logoUrl is deferred
                    webStr,
                    "DRAFT",
                    true,
                    0L,
                    0L,
                    "",
                    0L,
                    0L
            );

            businessController.createV2Business(v2Business)
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        saveBtn.setDisable(false);
                        saveBtn.setText("Save Business");

                        if (throwable != null) {
                            statusLabel.setText("Creation failed: " + throwable.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isFailure()) {
                            statusLabel.setText(result.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isSuccess()) {
                            BusinessFlowRouter.goToBusinessList();
                        }
                    }));
        });

        actions.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(heading, sub, grid, descBox, statusLabel, actions);
        return card;
    }
}
