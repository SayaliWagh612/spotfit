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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dynamic form for creating a new bookable Offering in a Space.
 * Money input is in Rupees (₹) and parsed into paise using BigDecimal.
 */
public class CreateOfferingPage {

    private final Space space;
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

    public CreateOfferingPage(Space space) {
        this.space = space;
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

        if (space == null) {
            container.getChildren().add(OwnerUi.body("No space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title
        Label titleLbl = OwnerUi.heading("Add Bookable Offering", 22);
        Label subtitleLbl = OwnerUi.body("Space: " + space.getSpaceName() + " (" + space.getCity() + ")", 13, OwnerUi.TEXT_MUTED);
        VBox headerBox = new VBox(4, titleLbl, subtitleLbl);

        // Form Card
        VBox card = OwnerUi.card();
        card.setSpacing(16);

        nameField = OwnerUi.textField("e.g. Podcast Recording, Hot Desk Day Pass");
        descriptionArea = OwnerUi.textArea("Detailed package/service description...", 3);

        bookingTypeCombo = OwnerUi.comboBox("Select Booking Type");
        bookingTypeCombo.getItems().addAll(
                BookingType.SLOT,
                BookingType.SEAT,
                BookingType.WORKSHOP,
                BookingType.RENTAL,
                BookingType.VENUE,
                BookingType.RESOURCE
        );
        bookingTypeCombo.setValue(BookingType.SLOT);

        pricingTypeCombo = OwnerUi.comboBox("Select Pricing Model");
        pricingTypeCombo.getItems().addAll(
                PricingType.PER_HOUR,
                PricingType.PER_PERSON,
                PricingType.PER_SESSION,
                PricingType.PER_DAY,
                PricingType.FLAT
        );
        pricingTypeCombo.setValue(PricingType.PER_HOUR);

        priceRupeesField = OwnerUi.textField("e.g. 1000 or 999.50");

        requiredResourceTypeCombo = OwnerUi.comboBox("Select Compatible Physical Resource");
        requiredResourceTypeCombo.getItems().addAll(
                ResourceType.ROOM,
                ResourceType.SEAT,
                ResourceType.EQUIPMENT,
                ResourceType.VENUE_AREA,
                ResourceType.WORKSHOP_AREA
        );
        requiredResourceTypeCombo.setValue(ResourceType.ROOM);

        // Auto update pricing & resource defaults based on booking type
        bookingTypeCombo.setOnAction(e -> applyBookingTypeDefaults());
        requiredResourceTypeCombo.setOnAction(e -> checkCompatibleResources());

        defaultDurationField = OwnerUi.textField("e.g. 60");
        defaultDurationField.setText("60");

        minDurationField = OwnerUi.textField("e.g. 60");
        minDurationField.setText("60");

        maxDurationField = OwnerUi.textField("e.g. 480");
        maxDurationField.setText("480");

        intervalField = OwnerUi.textField("e.g. 30");
        intervalField.setText("30");

        capacityField = OwnerUi.textField("Max concurrent capacity");
        capacityField.setText("1");

        minParticipantsField = OwnerUi.textField("Min participants for workshop");
        minParticipantsField.setText("1");

        maxParticipantsField = OwnerUi.textField("Max participants for workshop");
        maxParticipantsField.setText("10");

        workshopBox = new VBox(12,
                OwnerUi.fieldGroup("Minimum Participants", minParticipantsField),
                OwnerUi.fieldGroup("Maximum Participants", maxParticipantsField)
        );
        workshopBox.setVisible(false);
        workshopBox.setManaged(false);

        includedItemsField = OwnerUi.textField("Comma-separated items (e.g. 2 Mics, Engineer, Refreshments)");
        rulesField = OwnerUi.textField("Comma-separated rules (e.g. No food inside booth)");

        resourceCheckNotice = OwnerUi.body("Checking space resource compatibility...", 13, OwnerUi.ACCENT);
        resourceCheckNotice.setWrapText(true);

        errorLabel = OwnerUi.body("", 13, OwnerUi.ERROR);
        errorLabel.setWrapText(true);

        submitButton = OwnerUi.primaryButton("Save Offering");
        Button cancelBtn = OwnerUi.secondaryButton("Cancel");

        submitButton.setOnAction(e -> handleCreate());
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

    private void applyBookingTypeDefaults() {
        String bt = bookingTypeCombo.getValue();
        if (BookingType.SEAT.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.SEAT);
            pricingTypeCombo.setValue(PricingType.PER_PERSON);
            workshopBox.setVisible(false);
            workshopBox.setManaged(false);
        } else if (BookingType.SLOT.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.ROOM);
            pricingTypeCombo.setValue(PricingType.PER_HOUR);
            workshopBox.setVisible(false);
            workshopBox.setManaged(false);
        } else if (BookingType.WORKSHOP.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.WORKSHOP_AREA);
            pricingTypeCombo.setValue(PricingType.PER_PERSON);
            workshopBox.setVisible(true);
            workshopBox.setManaged(true);
        } else if (BookingType.RENTAL.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.EQUIPMENT);
            pricingTypeCombo.setValue(PricingType.PER_HOUR);
            workshopBox.setVisible(false);
            workshopBox.setManaged(false);
        } else if (BookingType.VENUE.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.VENUE_AREA);
            pricingTypeCombo.setValue(PricingType.PER_HOUR);
            workshopBox.setVisible(false);
            workshopBox.setManaged(false);
        } else if (BookingType.RESOURCE.equalsIgnoreCase(bt)) {
            requiredResourceTypeCombo.setValue(ResourceType.ROOM);
            pricingTypeCombo.setValue(PricingType.PER_HOUR);
            workshopBox.setVisible(false);
            workshopBox.setManaged(false);
        }
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

    private void handleCreate() {
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

        submitButton.setDisable(true);
        errorLabel.setText("Saving offering...");
        errorLabel.setStyle("-fx-text-fill: #1D5C32;");

        offeringController.createOffering(
                space.getSpaceId(), name, desc, bt, pt, priceText, reqType,
                defaultDur, minDur, maxDur, interval, cap, minPart, maxPart,
                items, rules
        ).thenAccept(result -> Platform.runLater(() -> {
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
