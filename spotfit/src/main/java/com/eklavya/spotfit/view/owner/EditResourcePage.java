package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.ResourceStatus;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.controller.owner.ResourceController;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Form for editing an existing physical Resource.
 */
public class EditResourcePage {

    private final Space space;
    private final Resource resource;
    private final ResourceController resourceController;
    private final VBox container;

    private TextField nameField;
    private TextField codeField;
    private ComboBox<String> typeCombo;
    private TextArea descriptionArea;
    private TextField capacityField;
    private TextField equipmentField;
    private TextField amenitiesField;
    private ComboBox<String> statusCombo;
    private Label errorLabel;
    private Button submitButton;

    public EditResourcePage(Space space, Resource resource) {
        this.space = space;
        this.resource = resource;
        this.resourceController = new ResourceController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
    }

    public VBox getContent() {
        renderForm();
        return container;
    }

    private void renderForm() {
        container.getChildren().clear();

        if (space == null || resource == null) {
            container.getChildren().add(OwnerUi.body("No resource or space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title
        Label titleLbl = OwnerUi.heading("Edit Physical Resource", 22);
        Label subtitleLbl = OwnerUi.body("Resource Code: " + resource.getResourceCode() + " | Space: " + space.getSpaceName(), 13, OwnerUi.TEXT_MUTED);
        VBox headerBox = new VBox(4, titleLbl, subtitleLbl);

        // Form Card
        VBox card = OwnerUi.card();
        card.setSpacing(16);

        nameField = OwnerUi.textField("e.g. Podcast Room A");
        nameField.setText(resource.getResourceName() != null ? resource.getResourceName() : "");

        codeField = OwnerUi.textField("e.g. ROOM-A");
        codeField.setText(resource.getResourceCode() != null ? resource.getResourceCode() : "");

        typeCombo = OwnerUi.comboBox("Select Resource Type");
        typeCombo.getItems().addAll(
                ResourceType.ROOM,
                ResourceType.SEAT,
                ResourceType.EQUIPMENT,
                ResourceType.VENUE_AREA,
                ResourceType.WORKSHOP_AREA
        );
        typeCombo.setValue(resource.getResourceType() != null ? resource.getResourceType() : ResourceType.ROOM);

        descriptionArea = OwnerUi.textArea("Brief description...", 3);
        descriptionArea.setText(resource.getDescription() != null ? resource.getDescription() : "");

        capacityField = OwnerUi.textField("e.g. 4");
        capacityField.setText(String.valueOf(resource.getCapacity() > 0 ? resource.getCapacity() : 1));

        equipmentField = OwnerUi.textField("Comma-separated equipment");
        equipmentField.setText(resource.getEquipment() != null ? String.join(", ", resource.getEquipment()) : "");

        amenitiesField = OwnerUi.textField("Comma-separated amenities");
        amenitiesField.setText(resource.getAmenities() != null ? String.join(", ", resource.getAmenities()) : "");

        statusCombo = OwnerUi.comboBox("Operational Status");
        statusCombo.getItems().addAll(
                ResourceStatus.AVAILABLE,
                ResourceStatus.MAINTENANCE,
                ResourceStatus.BLOCKED
        );
        statusCombo.setValue(
                (ResourceStatus.MAINTENANCE.equalsIgnoreCase(resource.getStatus()) || ResourceStatus.BLOCKED.equalsIgnoreCase(resource.getStatus()))
                        ? resource.getStatus() : ResourceStatus.AVAILABLE
        );

        errorLabel = OwnerUi.body("", 13, OwnerUi.ERROR);
        errorLabel.setWrapText(true);

        submitButton = OwnerUi.primaryButton("Save Changes");
        Button cancelBtn = OwnerUi.secondaryButton("Cancel");

        submitButton.setOnAction(e -> handleUpdate());
        cancelBtn.setOnAction(e -> ResourceFlowRouter.goToSpaceDetails(space));

        HBox actions = new HBox(12, submitButton, cancelBtn);

        card.getChildren().addAll(
                OwnerUi.fieldGroup("Resource Name *", nameField),
                OwnerUi.fieldGroup("Resource Code *", codeField),
                OwnerUi.fieldGroup("Resource Type *", typeCombo),
                OwnerUi.fieldGroup("Capacity *", capacityField),
                OwnerUi.fieldGroup("Operational Status *", statusCombo),
                OwnerUi.fieldGroup("Description", descriptionArea),
                OwnerUi.fieldGroup("Equipment (Comma-separated)", equipmentField),
                OwnerUi.fieldGroup("Amenities (Comma-separated)", amenitiesField),
                errorLabel,
                actions
        );

        container.getChildren().addAll(headerBox, card);
    }

    private void handleUpdate() {
        errorLabel.setText("");

        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String code = codeField.getText() != null ? codeField.getText().trim() : "";
        String type = typeCombo.getValue();
        String desc = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String capText = capacityField.getText() != null ? capacityField.getText().trim() : "";
        String status = statusCombo.getValue();

        if (name.isBlank()) {
            errorLabel.setText("Resource name is required.");
            return;
        }
        if (code.isBlank()) {
            errorLabel.setText("Resource code is required.");
            return;
        }
        if (type == null || type.isBlank()) {
            errorLabel.setText("Please select a resource type.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capText);
            if (capacity <= 0) {
                errorLabel.setText("Capacity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter a valid numeric capacity.");
            return;
        }

        List<String> equipment = Arrays.stream(equipmentField.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        List<String> amenities = Arrays.stream(amenitiesField.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        Resource updated = new Resource();
        updated.setResourceId(resource.getResourceId());
        updated.setSpaceId(resource.getSpaceId());
        updated.setBusinessId(resource.getBusinessId());
        updated.setOwnerId(resource.getOwnerId());
        updated.setResourceName(name);
        updated.setResourceCode(code);
        updated.setResourceType(type);
        updated.setDescription(desc);
        updated.setCapacity(capacity);
        updated.setEquipment(equipment);
        updated.setAmenities(amenities);
        updated.setStatus(status);
        updated.setActive(resource.isActive());
        updated.setCreatedAt(resource.getCreatedAt());

        submitButton.setDisable(true);
        errorLabel.setText("Updating resource...");
        errorLabel.setStyle("-fx-text-fill: #1D5C32;");

        resourceController.updateResource(updated).thenAccept(result -> Platform.runLater(() -> {
            submitButton.setDisable(false);
            if (result.isSuccess()) {
                ResourceFlowRouter.goToSpaceDetails(space);
            } else {
                errorLabel.setStyle("-fx-text-fill: #B91C1C;");
                errorLabel.setText(result.getMessage());
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                submitButton.setDisable(false);
                errorLabel.setStyle("-fx-text-fill: #B91C1C;");
                errorLabel.setText("System error: " + ex.getMessage());
            });
            return null;
        });
    }
}
