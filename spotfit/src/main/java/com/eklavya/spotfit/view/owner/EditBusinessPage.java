package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BusinessStatus;
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
 * View form for editing an existing DRAFT or REJECTED Business profile.
 */
public class EditBusinessPage {

    private final Business existingBusiness;
    private final BusinessController businessController;

    public EditBusinessPage(Business existingBusiness) {
        this.existingBusiness = existingBusiness;
        this.businessController = new BusinessController();
    }

    public Node getContent() {
        VBox card = OwnerUi.card();
        card.setSpacing(20);

        boolean isV2 = existingBusiness != null && existingBusiness.getSchemaVersion() == 2;

        Label heading = OwnerUi.heading("Edit Business Profile", 22);
        Label sub = OwnerUi.body(isV2
                ? "Update editable information for your V2 Business organization."
                : "Update editable information for your organization (Legacy Business profile).", 13, OwnerUi.TEXT_MUTED);

        // Pre-fill editable fields
        TextField businessName = OwnerUi.textField("");
        businessName.setText(val(existingBusiness.getBusinessName()));

        TextField legalName = OwnerUi.textField("");
        legalName.setText(val(existingBusiness.getLegalName()));

        TextField businessEmail = OwnerUi.textField("");
        businessEmail.setText(val(existingBusiness.getBusinessEmail()));

        TextField businessPhone = OwnerUi.textField("");
        businessPhone.setText(val(existingBusiness.getBusinessPhone()));

        TextArea description = OwnerUi.textArea("", 3);
        description.setText(val(existingBusiness.getDescription()));

        TextField addressLine = OwnerUi.textField("");
        addressLine.setText(val(existingBusiness.getAddressLine()));

        TextField landmark = OwnerUi.textField("");
        landmark.setText(val(existingBusiness.getLandmark()));

        TextField city = OwnerUi.textField("");
        city.setText(val(existingBusiness.getCity()));

        TextField state = OwnerUi.textField("");
        state.setText(val(existingBusiness.getState()));

        TextField postalCode = OwnerUi.textField("");
        postalCode.setText(val(existingBusiness.getPostalCode()));

        TextField registrationNumber = OwnerUi.textField("");
        registrationNumber.setText(val(existingBusiness.getRegistrationNumber()));

        // V2-specific controls
        Label categoryValueLabel = OwnerUi.body("Creativity", 14, OwnerUi.TEXT_DARK);
        categoryValueLabel.setStyle("-fx-font-weight: bold; -fx-padding: 6 12; -fx-background-color: #F5F5F5; -fx-background-radius: 4;");
        Label categoryNote = OwnerUi.body("More SpotFit categories will be enabled in a future phase.", 12, OwnerUi.TEXT_MUTED);
        VBox categoryBox = new VBox(4, categoryValueLabel, categoryNote);

        List<String> currentGroups = (existingBusiness != null && existingBusiness.getActivityGroups() != null)
                ? existingBusiness.getActivityGroups()
                : new ArrayList<>();

        CheckBox cbArt = new CheckBox("Art");
        cbArt.setMnemonicParsing(false);
        cbArt.setSelected(currentGroups.contains(CreativityActivityGroup.ART));

        CheckBox cbContent = new CheckBox("Content Creation");
        cbContent.setMnemonicParsing(false);
        cbContent.setSelected(currentGroups.contains(CreativityActivityGroup.CONTENT_CREATION));

        CheckBox cbPodcasts = new CheckBox("Podcasts");
        cbPodcasts.setMnemonicParsing(false);
        cbPodcasts.setSelected(currentGroups.contains(CreativityActivityGroup.PODCASTS));

        CheckBox cbMusic = new CheckBox("Music");
        cbMusic.setMnemonicParsing(false);
        cbMusic.setSelected(currentGroups.contains(CreativityActivityGroup.MUSIC));

        HBox activityGroupsBox = new HBox(16, cbArt, cbContent, cbPodcasts, cbMusic);

        TextField website = OwnerUi.textField("");
        if (isV2) {
            website.setText(val(existingBusiness.getWebsite()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        int row = 0;

        if (isV2) {
            grid.add(OwnerUi.fieldLabel("Business Category *"), 0, row);
            grid.add(categoryBox, 0, row + 1, 2, 1);
            row += 2;

            grid.add(OwnerUi.fieldLabel("Activity Groups * (Select at least one)"), 0, row);
            grid.add(activityGroupsBox, 0, row + 1, 2, 1);
            row += 2;
        }

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

        if (isV2) {
            grid.add(OwnerUi.fieldLabel("Website"), 0, row);
            grid.add(website, 0, row + 1);
            grid.add(OwnerUi.fieldLabel("Registration No."), 1, row);
            grid.add(registrationNumber, 1, row + 1);
            row += 2;
        } else {
            grid.add(OwnerUi.fieldLabel("Registration No."), 0, row);
            grid.add(registrationNumber, 0, row + 1);
            row += 2;
        }

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

        // Warning banner if REJECTED
        VBox rejectionNotice = new VBox();
        if (existingBusiness != null && BusinessStatus.REJECTED.equalsIgnoreCase(existingBusiness.getVerificationStatus())) {
            Label warnLbl = OwnerUi.body("Note: Updating this Business profile will reset its status from REJECTED to DRAFT for resubmission.", 13, "#E65100");
            warnLbl.setStyle("-fx-font-weight: bold; -fx-padding: 8 12; -fx-background-color: #FFF3E0; -fx-background-radius: 6;");
            rejectionNotice.getChildren().add(warnLbl);
        }

        Label statusLabel = OwnerUi.body("", 13, OwnerUi.TEXT_DARK);
        statusLabel.setWrapText(true);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = OwnerUi.secondaryButton("Cancel");
        cancelBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessDetails(existingBusiness));

        Button updateBtn = OwnerUi.primaryButton("Update Business");
        updateBtn.setOnAction(e -> {
            if (isV2) {
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

                existingBusiness.setCategory(SpaceCategory.CREATIVITY);
                existingBusiness.setActivityGroups(selectedGroups);
                existingBusiness.setWebsite(webStr);
                existingBusiness.setLogoUrl("");
            }

            existingBusiness.setBusinessName(businessName.getText().trim());
            existingBusiness.setLegalName(legalName.getText().trim());
            existingBusiness.setBusinessEmail(businessEmail.getText().trim());
            existingBusiness.setBusinessPhone(businessPhone.getText().trim());
            existingBusiness.setDescription(description.getText().trim());
            existingBusiness.setAddressLine(addressLine.getText().trim());
            existingBusiness.setLandmark(landmark.getText().trim());
            existingBusiness.setCity(city.getText().trim());
            existingBusiness.setState(state.getText().trim());
            existingBusiness.setPostalCode(postalCode.getText().trim());
            existingBusiness.setRegistrationNumber(registrationNumber.getText().trim());

            updateBtn.setDisable(true);
            updateBtn.setText("Updating Business...");
            statusLabel.setText("");

            businessController.updateBusiness(existingBusiness)
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        updateBtn.setDisable(false);
                        updateBtn.setText("Update Business");

                        if (throwable != null) {
                            statusLabel.setText("Update failed: " + throwable.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isFailure()) {
                            statusLabel.setText(result.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #D32F2F;");
                        } else if (result != null && result.isSuccess()) {
                            BusinessFlowRouter.goToBusinessList();
                        }
                    }));
        });

        actions.getChildren().addAll(cancelBtn, updateBtn);

        card.getChildren().addAll(heading, sub, rejectionNotice, grid, descBox, statusLabel, actions);
        return card;
    }

    private String val(String str) {
        return str != null ? str : "";
    }
}
