package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingType;
import com.eklavya.spotfit.constants.ResourceType;
import com.eklavya.spotfit.controller.owner.OfferingController;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.service.OfferingService;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Form for editing an existing DRAFT Offering.
 * Pre-fills controls using explicit values and BigDecimal string conversions.
 */
public class EditOfferingPage {

    private final Space space;
    private final Offering offering;
    private final OfferingController offeringController;
    private final VBox container;

    private TextField nameField;
    private TextArea descriptionArea;
    private ComboBox<String> bookingTypeCombo;
    private ComboBox<String> pricingTypeCombo;
    private TextField priceRupeesField;
    private ComboBox<String> requiredResourceTypeCombo;

    private TextField defaultDurationField;
    private TextField minDurationField;
    private TextField maxDurationField;
    private TextField intervalField;

    private TextField capacityField;
    private VBox workshopBox;
    private TextField minParticipantsField;
    private TextField maxParticipantsField;

    private TextField includedItemsField;
    private TextField rulesField;

    private Label resourceCheckNotice;
    private Label errorLabel;
    private Button submitButton;

    public EditOfferingPage(Space space, Offering offering) {
        this.space = space;
        this.offering = offering;
        this.offeringController = new OfferingController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
    }

    public VBox getContent() {
        renderForm();
        return container;
    }

    private void renderForm() {
        container.getChildren().clear();

        if (space == null || offering == null) {
            container.getChildren().add(OwnerUi.body("No offering or space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title
        Label titleLbl = OwnerUi.heading("Edit Offering — " + offering.getOfferingName(), 22);
        Label subtitleLbl = OwnerUi.body("Status: " + offering.getStatus() + " | Space: " + space.getSpaceName(), 13, OwnerUi.TEXT_MUTED);
        VBox headerBox = new VBox(4, titleLbl, subtitleLbl);

        // Form Card
        VBox card = OwnerUi.card();
        card.setSpacing(16);

        nameField = OwnerUi.textField("e.g. Podcast Recording");
        nameField.setText(offering.getOfferingName() != null ? offering.getOfferingName() : "");

        descriptionArea = OwnerUi.textArea("Detailed package description...", 3);
        descriptionArea.setText(offering.getDescription() != null ? offering.getDescription() : "");

        bookingTypeCombo = OwnerUi.comboBox("Select Booking Type");
        bookingTypeCombo.getItems().addAll(
                BookingType.SLOT,
                BookingType.SEAT,
                BookingType.WORKSHOP,
                BookingType.RENTAL,
                BookingType.VENUE,
                BookingType.RESOURCE
        );
        bookingTypeCombo.setValue(offering.getBookingType() != null ? offering.getBookingType() : BookingType.SLOT);

        pricingTypeCombo = OwnerUi.comboBox("Select Pricing Model");
        pricingTypeCombo.getItems().addAll(
                PricingType.PER_HOUR,
                PricingType.PER_PERSON,
                PricingType.PER_SESSION,
                PricingType.PER_DAY,
                PricingType.FLAT
        );
        pricingTypeCombo.setValue(offering.getPricingType() != null ? offering.getPricingType() : PricingType.PER_HOUR);

        priceRupeesField = OwnerUi.textField("Price in Rupees");
        // Pre-fill using BigDecimal formatting for exact plain rupees string
        String priceRupeesPlain = BigDecimal.valueOf(offering.getPricePaise(), 2).toPlainString();
        priceRupeesField.setText(priceRupeesPlain);

        requiredResourceTypeCombo = OwnerUi.comboBox("Select Compatible Physical Resource");
        requiredResourceTypeCombo.getItems().addAll(
                ResourceType.ROOM,
                ResourceType.SEAT,
                ResourceType.EQUIPMENT,
                ResourceType.VENUE_AREA,
                ResourceType.WORKSHOP_AREA
        );
        requiredResourceTypeCombo.setValue(offering.getRequiredResourceType() != null ? offering.getRequiredResourceType() : ResourceType.ROOM);

        requiredResourceTypeCombo.setOnAction(e -> checkCompatibleResources());

        defaultDurationField = OwnerUi.textField("Default Duration");
        defaultDurationField.setText(String.valueOf(offering.getDefaultDurationMinutes() > 0 ? offering.getDefaultDurationMinutes() : 60));

        minDurationField = OwnerUi.textField("Min Duration");
        minDurationField.setText(String.valueOf(offering.getMinimumDurationMinutes() > 0 ? offering.getMinimumDurationMinutes() : 60));

        maxDurationField = OwnerUi.textField("Max Duration");
        maxDurationField.setText(String.valueOf(offering.getMaximumDurationMinutes() > 0 ? offering.getMaximumDurationMinutes() : 480));

        intervalField = OwnerUi.textField("Booking Interval");
        intervalField.setText(String.valueOf(offering.getBookingIntervalMinutes() > 0 ? offering.getBookingIntervalMinutes() : 30));

        capacityField = OwnerUi.textField("Max concurrent capacity");
        capacityField.setText(String.valueOf(offering.getCapacity() > 0 ? offering.getCapacity() : 1));

        minParticipantsField = OwnerUi.textField("Min participants");
        minParticipantsField.setText(String.valueOf(offering.getMinimumParticipants() > 0 ? offering.getMinimumParticipants() : 1));

        maxParticipantsField = OwnerUi.textField("Max participants");
        maxParticipantsField.setText(String.valueOf(offering.getMaximumParticipants() > 0 ? offering.getMaximumParticipants() : 10));

        workshopBox = new VBox(12,
                OwnerUi.fieldGroup("Minimum Participants", minParticipantsField),
                OwnerUi.fieldGroup("Maximum Participants", maxParticipantsField)
        );
        boolean isWorkshop = BookingType.WORKSHOP.equalsIgnoreCase(offering.getBookingType());
        workshopBox.setVisible(isWorkshop);
        workshopBox.setManaged(isWorkshop);

        includedItemsField = OwnerUi.textField("Comma-separated items");
        includedItemsField.setText(offering.getIncludedItems() != null ? String.join(", ", offering.getIncludedItems()) : "");

        rulesField = OwnerUi.textField("Comma-separated rules");
        rulesField.setText(offering.getRules() != null ? String.join(", ", offering.getRules()) : "");

        resourceCheckNotice = OwnerUi.body("Checking space resource compatibility...", 13, OwnerUi.ACCENT);
        resourceCheckNotice.setWrapText(true);

        errorLabel = OwnerUi.body("", 13, OwnerUi.ERROR);
        errorLabel.setWrapText(true);

        submitButton = OwnerUi.primaryButton("Save Changes");
        Button cancelBtn = OwnerUi.secondaryButton("Cancel");

        submitButton.setOnAction(e -> handleUpdate());
        cancelBtn.setOnAction(e -> OfferingFlowRouter.goToSpaceDetails(space));

        HBox actions = new HBox(12, submitButton, cancelBtn);

        card.getChildren().addAll(
                OwnerUi.fieldGroup("Offering Name *", nameField),
                OwnerUi.fieldGroup("Description *", descriptionArea),
                OwnerUi.fieldGroup("Booking Type *", bookingTypeCombo),
                OwnerUi.fieldGroup("Pricing Model *", pricingTypeCombo),
                OwnerUi.fieldGroup("Price (₹ Rupees) *", priceRupeesField),
                OwnerUi.fieldGroup("Required Physical Resource Type *", requiredResourceTypeCombo),
                resourceCheckNotice,
                new Separator(),
                OwnerUi.heading("Duration & Interval (Minutes)", 16),
                new HBox(12,
                        OwnerUi.fieldGroup("Default (Min)", defaultDurationField),
                        OwnerUi.fieldGroup("Minimum (Min)", minDurationField),
                        OwnerUi.fieldGroup("Maximum (Min)", maxDurationField),
                        OwnerUi.fieldGroup("Interval (Min)", intervalField)
                ),
                new Separator(),
                OwnerUi.fieldGroup("Capacity / Max Slots *", capacityField),
                workshopBox,
                OwnerUi.fieldGroup("Included Items (Comma-separated)", includedItemsField),
                OwnerUi.fieldGroup("Offering Rules (Comma-separated)", rulesField),
                errorLabel,
                actions
        );

        container.getChildren().addAll(headerBox, card);

        // Perform initial compatibility check
        checkCompatibleResources();
    }

    private void checkCompatibleResources() {
        String reqType = requiredResourceTypeCombo.getValue();
        if (reqType == null || space == null) return;

        offeringController.loadCompatibleResources(space.getSpaceId(), reqType).thenAccept(resResult -> Platform.runLater(() -> {
            if (resResult.isSuccess() && resResult.getData() != null && !resResult.getData().isEmpty()) {
                resourceCheckNotice.setStyle("-fx-text-fill: #1D5C32; -fx-font-weight: bold;");
                resourceCheckNotice.setText("✔ Found " + resResult.getData().size() + " active compatible resource(s) (" + reqType + ") in this space.");
                submitButton.setDisable(false);
            } else {
                resourceCheckNotice.setStyle("-fx-text-fill: #B91C1C; -fx-font-weight: bold;");
                resourceCheckNotice.setText("⚠ Warning: No active " + reqType + " resource found in this space. Add a compatible resource first.");
                submitButton.setDisable(true);
            }
        }));
    }

    private void handleUpdate() {
        errorLabel.setText("");

        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String desc = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String bt = bookingTypeCombo.getValue();
        String pt = pricingTypeCombo.getValue();
        String priceText = priceRupeesField.getText() != null ? priceRupeesField.getText().trim() : "";
        String reqType = requiredResourceTypeCombo.getValue();

        OperationResult<Long> priceRes = OfferingService.parsePriceRupeesToPaise(priceText);
        if (priceRes.isFailure()) {
            errorLabel.setText(priceRes.getMessage());
            return;
        }

        int defaultDur, minDur, maxDur, interval, cap, minPart = 1, maxPart = 1;
        try {
            defaultDur = Integer.parseInt(defaultDurationField.getText().trim());
            minDur = Integer.parseInt(minDurationField.getText().trim());
            maxDur = Integer.parseInt(maxDurationField.getText().trim());
            interval = Integer.parseInt(intervalField.getText().trim());
            cap = Integer.parseInt(capacityField.getText().trim());

            if (BookingType.WORKSHOP.equalsIgnoreCase(bt)) {
                minPart = Integer.parseInt(minParticipantsField.getText().trim());
                maxPart = Integer.parseInt(maxParticipantsField.getText().trim());
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Please enter valid numeric values for duration and capacity fields.");
            return;
        }

        List<String> items = Arrays.stream(includedItemsField.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        List<String> rules = Arrays.stream(rulesField.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        Offering updated = new Offering();
        updated.setOfferingId(offering.getOfferingId());
        updated.setSpaceId(offering.getSpaceId());
        updated.setBusinessId(offering.getBusinessId());
        updated.setOwnerId(offering.getOwnerId());
        updated.setStatus(offering.getStatus());
        updated.setActive(offering.isActive());
        updated.setCreatedAt(offering.getCreatedAt());

        updated.setOfferingName(name);
        updated.setDescription(desc);
        updated.setBookingType(bt);
        updated.setPricingType(pt);
        updated.setPricePaise(priceRes.getData());
        updated.setRequiredResourceType(reqType);
        updated.setDefaultDurationMinutes(defaultDur);
        updated.setMinimumDurationMinutes(minDur);
        updated.setMaximumDurationMinutes(maxDur);
        updated.setBookingIntervalMinutes(interval);
        updated.setCapacity(cap);
        updated.setMinimumParticipants(minPart);
        updated.setMaximumParticipants(maxPart);
        updated.setIncludedItems(items);
        updated.setRules(rules);

        submitButton.setDisable(true);
        errorLabel.setText("Updating offering...");
        errorLabel.setStyle("-fx-text-fill: #1D5C32;");

        offeringController.updateOffering(updated).thenAccept(result -> Platform.runLater(() -> {
            submitButton.setDisable(false);
            if (result.isSuccess()) {
                OfferingFlowRouter.goToSpaceDetails(space);
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
