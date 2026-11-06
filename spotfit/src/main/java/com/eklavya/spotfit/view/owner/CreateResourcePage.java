package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.controller.owner.ResourceController;
import com.eklavya.spotfit.model.Space;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Form for adding a new physical Resource to a Space.
 */
public class CreateResourcePage {

    private final Space space;
    private final ResourceController resourceController;
    private final VBox container;

    private TextField nameField;
    private TextField codeField;
    private ComboBox<String> typeCombo;
    private TextArea descriptionArea;
    private TextField capacityField;
    private TextField equipmentField;
    private TextField amenitiesField;
    private Label errorLabel;
    private Button submitButton;

    public CreateResourcePage(Space space) {
        this.space = space;
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

        if (space == null) {
            container.getChildren().add(OwnerUi.body("No space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title
        Label titleLbl = OwnerUi.heading("Add Physical Resource", 22);
        Label subtitleLbl = OwnerUi.body("Space: " + space.getSpaceName() + " (" + space.getCity() + ")", 13, OwnerUi.TEXT_MUTED);
        VBox headerBox = new VBox(4, titleLbl, subtitleLbl);

        // Form Card
        VBox card = OwnerUi.card();
        card.setSpacing(16);

        nameField = OwnerUi.textField("e.g. Podcast Room A, Seat 1, Main Stage");
        codeField = OwnerUi.textField("e.g. ROOM-A, SEAT-1");

        typeCombo = OwnerUi.comboBox("Select Resource Type");
        typeCombo.getItems().addAll(
                ResourceType.ROOM,
                ResourceType.SEAT,
                ResourceType.EQUIPMENT,
                ResourceType.VENUE_AREA,
                ResourceType.WORKSHOP_AREA
        );
        typeCombo.setValue(ResourceType.ROOM);

        descriptionArea = OwnerUi.textArea("Brief description of the physical unit and features...", 3);
        capacityField = OwnerUi.textField("e.g. 4");
        capacityField.setText("1");

        equipmentField = OwnerUi.textField("Comma-separated equipment (e.g. Microphones, Mixer, Acoustic Panels)");
        amenitiesField = OwnerUi.textField("Comma-separated amenities (e.g. AC, Soundproofing, WiFi)");

        errorLabel = OwnerUi.body("", 13, OwnerUi.ERROR);
        errorLabel.setWrapText(true);

        submitButton = OwnerUi.primaryButton("Save Resource");
        Button cancelBtn = OwnerUi.secondaryButton("Cancel");

        submitButton.setOnAction(e -> handleCreate());
        cancelBtn.setOnAction(e -> ResourceFlowRouter.goToSpaceDetails(space));

        HBox actions = new HBox(12, submitButton, cancelBtn);

        card.getChildren().addAll(
                OwnerUi.fieldGroup("Resource Name *", nameField),
                OwnerUi.fieldGroup("Resource Code * (e.g. ROOM-A)", codeField),
                OwnerUi.fieldGroup("Resource Type *", typeCombo),
                OwnerUi.fieldGroup("Capacity (Persons / Units) *", capacityField),
                OwnerUi.fieldGroup("Description", descriptionArea),
                OwnerUi.fieldGroup("Equipment (Comma-separated)", equipmentField),
                OwnerUi.fieldGroup("Amenities (Comma-separated)", amenitiesField),
                errorLabel,
                actions
        );

        container.getChildren().addAll(headerBox, card);
    }

    private void handleCreate() {
        errorLabel.setText("");

        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String code = codeField.getText() != null ? codeField.getText().trim() : "";
        String type = typeCombo.getValue();
        String desc = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String capText = capacityField.getText() != null ? capacityField.getText().trim() : "";

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

        submitButton.setDisable(true);
        errorLabel.setText("Creating resource...");
        errorLabel.setStyle("-fx-text-fill: #1D5C32;");

        resourceController.createResource(
                space.getSpaceId(), name, code, type, desc, capacity, equipment, amenities
        ).thenAccept(result -> Platform.runLater(() -> {
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
