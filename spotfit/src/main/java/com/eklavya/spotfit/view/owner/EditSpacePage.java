package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.CreativityActivityGroup;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * View allowing Space Owners to edit an existing DRAFT, REJECTED, or APPROVED Space (V1 or V2).
 * Ensures persisted values are loaded into control text/values.
 */
public class EditSpacePage {

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private final Space initialSpace;
    private Space activeSpace;
    private final SpaceController spaceController;
    private final BusinessController businessController;
    private final VBox container;

    // V1 Controls
    private ComboBox<Business> businessComboV1;
    private TextField spaceNameFieldV1;
    private TextArea descriptionFieldV1;
    private ComboBox<String> categoryComboV1;
    private ComboBox<String> groupComboV1;
    private ComboBox<String> spaceTypeComboV1;
    private TextField addressFieldV1;
    private TextField landmarkFieldV1;
    private TextField cityFieldV1;
    private TextField stateFieldV1;
    private TextField postalCodeFieldV1;
    private TextField capacityFieldV1;
    private List<CheckBox> amenityCheckBoxesV1 = new ArrayList<>();
    private TextField customAmenitiesFieldV1;
    private TextField suitableActivitiesFieldV1;
    private TextArea rulesFieldV1;
    private TextField openingTimeFieldV1;
    private TextField closingTimeFieldV1;
    private List<CheckBox> dayCheckBoxesV1 = new ArrayList<>();

    // V2 Controls
    TextField spaceNameFieldV2;
    TextArea descriptionFieldV2;
    TextField addressFieldV2;
    TextField areaFieldV2;
    TextField landmarkFieldV2;
    TextField cityFieldV2;
    TextField stateFieldV2;
    TextField postalCodeFieldV2;
    private TextField timeZoneFieldV2;
    private TextField capacityFieldV2;
    private ComboBox<String> bookingTypeComboV2;
    private TextField basePriceRupeesFieldV2;
    private ComboBox<String> pricingUnitComboV2;
    private TextField amenitiesFieldV2;
    private TextField facilitiesFieldV2;
    private TextField rulesFieldV2;
    private TextField cancellationPolicyFieldV2;

    private VBox configContainerV2;
    // Seat Config V2
    private ComboBox<String> seatSelectionModeComboV2;
    private TextField seatMinDurationFieldV2;
    private TextField seatMaxDurationFieldV2;
    private TextField seatDurationStepFieldV2;
    private TextField seatMaxGuestsFieldV2;
    private ComboBox<String> seatPriceModeComboV2;
    private CheckBox seatAllowMultipleCbV2;
    private TextField seatAdvanceDaysFieldV2;
    private TextField seatMinAdvanceMinFieldV2;

    // Slot Config V2
    private TextField slotMinDurationFieldV2;
    private TextField slotMaxDurationFieldV2;
    private TextField slotDurationStepFieldV2;
    private TextField slotIntervalFieldV2;
    private TextField slotBufferBeforeFieldV2;
    private TextField slotBufferAfterFieldV2;
    private TextField slotMaxPeopleFieldV2;
    private TextField slotAdvanceDaysFieldV2;
    private TextField slotMinAdvanceMinFieldV2;

    // Workshop Config V2
    private TextField workshopMinPartFieldV2;
    private TextField workshopMaxPartFieldV2;
    private TextField workshopBookingCutoffFieldV2;
    private TextField workshopCancelCutoffFieldV2;
    private TextField workshopAgeRestrFieldV2;
    private TextField workshopMaterialsFieldV2;
    private TextField workshopInstructorFieldV2;

    // Rental Config V2
    private TextField rentalQuantityFieldV2;
    private TextField rentalMinDurationFieldV2;
    private TextField rentalMaxDurationFieldV2;
    private TextField rentalSecurityDepositFieldV2;
    private CheckBox rentalPickupCbV2;
    private CheckBox rentalDeliveryCbV2;
    private TextField rentalDeliveryRadiusFieldV2;
    private TextField rentalDeliveryChargeFieldV2;
    private TextField rentalPickupInstructionsFieldV2;
    private CheckBox rentalIdentityProofCbV2;
    private TextField rentalLateFeeFieldV2;
    private TextField rentalAddOnsFieldV2;

    // Venue Config V2
    private TextField venueMinHoursFieldV2;
    private TextField venueMaxHoursFieldV2;
    private CheckBox venueRequiresApprovalCbV2;
    private TextField venueMinAdvanceDaysFieldV2;
    private TextField venueAllowedEventTypesFieldV2;
    private TextField venueSetupOptionsFieldV2;
    private TextField venueSecurityDepositFieldV2;
    private CheckBox venueSoundSystemCbV2;
    private CheckBox venueLightingCbV2;
    private CheckBox venueStageCbV2;
    private CheckBox venueGreenRoomCbV2;
    private CheckBox venueOutsideVendorCbV2;
    private CheckBox venueCateringCbV2;
    private TextField venueOvertimeRateFieldV2;

    // Practice Room Config V2
    private TextField practiceMinDurationFieldV2;
    private TextField practiceMaxDurationFieldV2;
    private TextField practiceDurationStepFieldV2;
    private CheckBox practiceEquipmentSelectionCbV2;
    private TextField practiceIncludedEquipmentFieldV2;
    private TextField practiceOptionalEquipmentFieldV2;

    private final Map<String, DayControlsV2> operatingDayControlsV2 = new LinkedHashMap<>();

    private Label errorLabel;
    private Button saveBtn;
    private boolean isSaving = false;

    private static class DayControlsV2 {
        CheckBox activeCb;
        TextField openField;
        TextField closeField;

        DayControlsV2(String dayName, boolean active, String open, String close) {
            this.activeCb = new CheckBox(dayName);
            this.activeCb.setSelected(active);
            this.openField = OwnerUi.textField(open != null ? open : "09:00");
            this.openField.setPrefWidth(90);
            this.closeField = OwnerUi.textField(close != null ? close : "21:00");
            this.closeField.setPrefWidth(90);
        }
    }

    public EditSpacePage(Space space) {
        this.initialSpace = space;
        this.activeSpace = space;
        this.spaceController = new SpaceController();
        this.businessController = new BusinessController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
    }

    public VBox getContent() {
        if (initialSpace != null && initialSpace.getSchemaVersion() == 2) {
            loadV2SpaceAndRender();
        } else {
            renderV1Form();
        }
        return container;
    }

    void renderV2FormForTest() {
        this.activeSpace = initialSpace;
        renderV2Form();
    }

    Space assembleV2SpaceFromForm() {
        String name = spaceNameFieldV2.getText().trim();
        String desc = descriptionFieldV2.getText().trim();
        String addr = addressFieldV2.getText().trim();
        String area = areaFieldV2.getText().trim();
        String city = cityFieldV2.getText().trim();
        String state = stateFieldV2.getText().trim();
        String postal = postalCodeFieldV2.getText().trim();
        int cap = Integer.parseInt(capacityFieldV2.getText().trim());

        long basePricePaise = parseRupeesToPaise(basePriceRupeesFieldV2.getText().trim());

        Map<String, OperatingDayConfig> hours = new LinkedHashMap<>();
        for (Map.Entry<String, DayControlsV2> entry : operatingDayControlsV2.entrySet()) {
            DayControlsV2 ctrl = entry.getValue();
            String normDay = WeekDay.normalizeDay(entry.getKey());
            if (normDay != null) {
                hours.put(normDay, new OperatingDayConfig(
                        ctrl.openField.getText().trim(),
                        ctrl.closeField.getText().trim(),
                        ctrl.activeCb.isSelected()
                ));
            }
        }

        BookingConfig config = assembleV2BookingConfig(bookingTypeComboV2.getValue(), basePricePaise);

        SpaceLocation location = new SpaceLocation(
                addr, area, city, state, postal,
                landmarkFieldV2.getText().trim(), 0.0, 0.0
        );

        Space submitted = Space.createV2(
                activeSpace.getSpaceId(),
                activeSpace.getBusinessId(),
                activeSpace.getOwnerId(),
                name,
                desc,
                activeSpace.getCategory(),
                activeSpace.getActivityGroup(),
                activeSpace.getActivityType(),
                location,
                activeSpace.getCoverPhotoUrl(),
                activeSpace.getPhotoUrls(),
                splitCleanList(amenitiesFieldV2.getText()),
                splitCleanList(facilitiesFieldV2.getText()),
                cap,
                bookingTypeComboV2.getValue(),
                basePricePaise,
                pricingUnitComboV2.getValue(),
                config,
                hours,
                splitCleanList(rulesFieldV2.getText()),
                cancellationPolicyFieldV2.getText().trim()
        );

        submitted.setTimeZoneId(timeZoneFieldV2.getText().trim());

        String currentApproval = val(activeSpace.getApprovalStatus()).toUpperCase();
        if ("APPROVED".equals(currentApproval) || "REJECTED".equals(currentApproval)) {
            submitted.setApprovalStatus(SpaceStatus.DRAFT);
            submitted.setOperationalStatus("INACTIVE");
        } else {
            submitted.setApprovalStatus(activeSpace.getApprovalStatus());
            submitted.setOperationalStatus(activeSpace.getOperationalStatus());
        }
        return submitted;
    }

    public Scene getScene(Runnable callback) {
        String titleStr = initialSpace != null ? "Edit " + val(initialSpace.getSpaceName()) : "Edit Space";
        return OwnerUi.pageShell("myspaces", "My Spaces / " + titleStr, titleStr,
                "Modify Space attributes", getContent(), callback);
    }

    // ==================== V2 EDIT FORM ====================

    private void loadV2SpaceAndRender() {
        container.getChildren().clear();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(36, 36);

        VBox loadingBox = new VBox(12, progress, OwnerUi.body("Loading space data...", 13, OwnerUi.TEXT_MUTED));
        loadingBox.setAlignment(Pos.CENTER);
        container.getChildren().add(loadingBox);

        spaceController.loadV2Space(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
            container.getChildren().clear();
            if (res.isSuccess() && res.getData() != null) {
                this.activeSpace = res.getData();
                renderV2Form();
            } else {
                this.activeSpace = initialSpace;
                renderV2Form();
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                container.getChildren().clear();
                this.activeSpace = initialSpace;
                renderV2Form();
            });
            return null;
        });
    }

    private void renderV2Form() {
        container.getChildren().clear();

        if (activeSpace == null) {
            container.getChildren().add(OwnerUi.body("No space selected to edit.", 14, OwnerUi.ERROR));
            return;
        }

        String approvalStatus = val(activeSpace.getApprovalStatus()).toUpperCase();
        if ("SUBMITTED".equals(approvalStatus) || "UNDER_REVIEW".equals(approvalStatus)) {
            VBox card = OwnerUi.card();
            Button backBtn = OwnerUi.secondaryButton("← Back to Details");
            backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(activeSpace));
            card.getChildren().addAll(
                    OwnerUi.heading("Editing Disabled", 18),
                    OwnerUi.body("Space is currently submitted and under review. Editing is disabled.", 13, OwnerUi.TEXT_MUTED),
                    backBtn
            );
            container.getChildren().add(card);
            return;
        }

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(
                OwnerUi.heading("Edit V2 Space: " + val(activeSpace.getSpaceName()), 22),
                OwnerUi.badge(formatV2Status(activeSpace))
        );

        VBox warningBox = new VBox(8);
        if ("APPROVED".equals(approvalStatus)) {
            Label warnLbl = OwnerUi.body("⚠️ WARNING: Changing approved listing information will return this Space to Draft and make it inactive until re-approved.", 13, OwnerUi.ERROR);
            warnLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #C62828;");
            warningBox.getChildren().add(warnLbl);
        } else if ("REJECTED".equals(approvalStatus)) {
            Label infoLbl = OwnerUi.body("Notice: Saving changes will reset approval status to Draft for resubmission.", 13, OwnerUi.ACCENT);
            warningBox.getChildren().add(infoLbl);
        }

        // Prefill text fields
        spaceNameFieldV2 = OwnerUi.textField("");
        spaceNameFieldV2.setText(val(activeSpace.getSpaceName()));

        descriptionFieldV2 = OwnerUi.textArea("", 3);
        descriptionFieldV2.setText(val(activeSpace.getDescription()));

        SpaceLocation loc = activeSpace.getSpaceLocation();
        addressFieldV2 = OwnerUi.textField("");
        addressFieldV2.setText(loc != null ? val(loc.getAddressLine()) : val(activeSpace.getAddressLine()));

        areaFieldV2 = OwnerUi.textField("");
        areaFieldV2.setText(loc != null ? val(loc.getArea()) : "");

        landmarkFieldV2 = OwnerUi.textField("");
        landmarkFieldV2.setText(loc != null ? val(loc.getLandmark()) : val(activeSpace.getLandmark()));

        cityFieldV2 = OwnerUi.textField("");
        cityFieldV2.setText(loc != null ? val(loc.getCity()) : val(activeSpace.getCity()));

        stateFieldV2 = OwnerUi.textField("");
        stateFieldV2.setText(loc != null ? val(loc.getState()) : val(activeSpace.getState()));

        postalCodeFieldV2 = OwnerUi.textField("");
        postalCodeFieldV2.setText(loc != null ? val(loc.getPostalCode()) : val(activeSpace.getPostalCode()));

        timeZoneFieldV2 = OwnerUi.textField("");
        timeZoneFieldV2.setText(activeSpace.getTimeZoneId() != null ? activeSpace.getTimeZoneId() : "Asia/Kolkata");

        capacityFieldV2 = OwnerUi.textField("");
        capacityFieldV2.setText(activeSpace.getCapacity() > 0 ? String.valueOf(activeSpace.getCapacity()) : "1");

        bookingTypeComboV2 = new ComboBox<>();
        bookingTypeComboV2.setMaxWidth(Double.MAX_VALUE);
        bookingTypeComboV2.getItems().addAll(
                BookingType.SEAT, BookingType.SLOT, BookingType.WORKSHOP,
                BookingType.RENTAL, BookingType.VENUE, BookingType.PRACTICE_ROOM
        );
        bookingTypeComboV2.setValue(activeSpace.getBookingType() != null ? activeSpace.getBookingType() : BookingType.SLOT);

        basePriceRupeesFieldV2 = OwnerUi.textField("");
        basePriceRupeesFieldV2.setText(BigDecimal.valueOf(activeSpace.getBasePricePaise(), 2).toPlainString());

        pricingUnitComboV2 = new ComboBox<>();
        pricingUnitComboV2.setMaxWidth(Double.MAX_VALUE);
        updatePricingUnitsForTypeV2(bookingTypeComboV2.getValue(), pricingUnitComboV2);
        if (activeSpace.getPricingUnit() != null && pricingUnitComboV2.getItems().contains(activeSpace.getPricingUnit())) {
            pricingUnitComboV2.setValue(activeSpace.getPricingUnit());
        }

        bookingTypeComboV2.setOnAction(e -> {
            updatePricingUnitsForTypeV2(bookingTypeComboV2.getValue(), pricingUnitComboV2);
            buildV2ConfigFormControls(bookingTypeComboV2.getValue(), configContainerV2, activeSpace.getBookingConfig());
        });

        configContainerV2 = new VBox(12);
        buildV2ConfigFormControls(bookingTypeComboV2.getValue(), configContainerV2, activeSpace.getBookingConfig());

        initOperatingDaysV2(activeSpace.getOperatingHours());

        VBox scheduleBox = new VBox(8);
        for (Map.Entry<String, DayControlsV2> entry : operatingDayControlsV2.entrySet()) {
            DayControlsV2 ctrl = entry.getValue();
            HBox row = new HBox(12, ctrl.activeCb, OwnerUi.body("Open:", 12, OwnerUi.TEXT_MUTED), ctrl.openField, OwnerUi.body("Close:", 12, OwnerUi.TEXT_MUTED), ctrl.closeField);
            row.setAlignment(Pos.CENTER_LEFT);
            scheduleBox.getChildren().add(row);
        }

        amenitiesFieldV2 = OwnerUi.textField("");
        amenitiesFieldV2.setText(activeSpace.getAmenities() != null ? String.join(", ", activeSpace.getAmenities()) : "");

        facilitiesFieldV2 = OwnerUi.textField("");
        facilitiesFieldV2.setText(activeSpace.getFacilities() != null ? String.join(", ", activeSpace.getFacilities()) : "");

        rulesFieldV2 = OwnerUi.textField("");
        rulesFieldV2.setText(activeSpace.getRules() != null ? String.join(", ", activeSpace.getRules()) : "");

        cancellationPolicyFieldV2 = OwnerUi.textField("");
        cancellationPolicyFieldV2.setText(activeSpace.getCancellationPolicy() != null ? activeSpace.getCancellationPolicy() : "");

        saveBtn = OwnerUi.primaryButton("Save V2 Space Changes");
        saveBtn.setOnAction(e -> handleSaveV2Changes());

        Button cancelBtn = OwnerUi.secondaryButton("Cancel");
        cancelBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(activeSpace));

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        HBox actions = new HBox(12, cancelBtn, saveBtn);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        GridPane addressGrid = new GridPane();
        addressGrid.setHgap(12);
        addressGrid.setVgap(10);

        addressGrid.add(OwnerUi.fieldLabel("Address Line *"), 0, 0);
        addressGrid.add(addressFieldV2, 0, 1);
        addressGrid.add(OwnerUi.fieldLabel("Area / Locality *"), 1, 0);
        addressGrid.add(areaFieldV2, 1, 1);

        addressGrid.add(OwnerUi.fieldLabel("Landmark"), 0, 2);
        addressGrid.add(landmarkFieldV2, 0, 3);
        addressGrid.add(OwnerUi.fieldLabel("City *"), 1, 2);
        addressGrid.add(cityFieldV2, 1, 3);

        addressGrid.add(OwnerUi.fieldLabel("State *"), 0, 4);
        addressGrid.add(stateFieldV2, 0, 5);
        addressGrid.add(OwnerUi.fieldLabel("Postal Code *"), 1, 4);
        addressGrid.add(postalCodeFieldV2, 1, 5);

        addressGrid.add(OwnerUi.fieldLabel("Time Zone *"), 0, 6);
        addressGrid.add(timeZoneFieldV2, 0, 7);
        addressGrid.add(OwnerUi.fieldLabel("Guest Capacity *"), 1, 6);
        addressGrid.add(capacityFieldV2, 1, 7);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            addressGrid.getColumnConstraints().add(cc);
        }

        formCard.getChildren().addAll(
                titleRow, warningBox,
                OwnerUi.fieldLabel("Business Profile (Read-only)"), OwnerUi.body("Business ID: " + activeSpace.getBusinessId(), 13, OwnerUi.TEXT_DARK),
                OwnerUi.fieldLabel("Space Name *"), spaceNameFieldV2,
                OwnerUi.fieldLabel("Description *"), descriptionFieldV2,
                addressGrid,
                OwnerUi.fieldLabel("Booking Type *"), bookingTypeComboV2,
                OwnerUi.fieldLabel("Base Price (₹ Rupees) *"), basePriceRupeesFieldV2,
                OwnerUi.fieldLabel("Pricing Unit *"), pricingUnitComboV2,
                OwnerUi.fieldLabel("Booking Configuration"), configContainerV2,
                OwnerUi.fieldLabel("Operating Hours (24-hour HH:mm) *"), scheduleBox,
                OwnerUi.fieldLabel("Amenities (comma separated)"), amenitiesFieldV2,
                OwnerUi.fieldLabel("Facilities (comma separated)"), facilitiesFieldV2,
                OwnerUi.fieldLabel("House Rules (comma separated)"), rulesFieldV2,
                OwnerUi.fieldLabel("Cancellation Policy"), cancellationPolicyFieldV2,
                errorLabel, actions
        );

        container.getChildren().add(formCard);
    }

    private void updatePricingUnitsForTypeV2(String type, ComboBox<String> unitCombo) {
        String curr = unitCombo.getValue();
        unitCombo.getItems().clear();
        if (BookingType.SEAT.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.SEAT, PricingUnit.PERSON, PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT);
            unitCombo.setDisable(false);
        } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT);
            unitCombo.setDisable(false);
        } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.PERSON);
            unitCombo.setValue(PricingUnit.PERSON);
            unitCombo.setDisable(true);
            return;
        } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.HOUR, PricingUnit.DAY, PricingUnit.FLAT);
            unitCombo.setDisable(false);
        } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.HOUR, PricingUnit.EVENT, PricingUnit.SESSION, PricingUnit.FLAT);
            unitCombo.setDisable(false);
        } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
            unitCombo.getItems().addAll(PricingUnit.HOUR, PricingUnit.SESSION, PricingUnit.FLAT);
            unitCombo.setDisable(false);
        } else {
            unitCombo.setDisable(false);
        }
        if (curr != null && unitCombo.getItems().contains(curr)) {
            unitCombo.setValue(curr);
        } else if (!unitCombo.getItems().isEmpty()) {
            unitCombo.setValue(unitCombo.getItems().get(0));
        }
    }

    private static final List<String> ALL_DAYS = List.of(
            WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
            WeekDay.THURSDAY, WeekDay.FRIDAY, WeekDay.SATURDAY, WeekDay.SUNDAY
    );

    private static String formatDayName(String day) {
        if (day == null || day.isBlank()) return "";
        String norm = WeekDay.normalizeDay(day);
        if (norm == null) return day;
        return norm.substring(0, 1).toUpperCase() + norm.substring(1).toLowerCase();
    }

    private void initOperatingDaysV2(Map<String, OperatingDayConfig> existing) {
        operatingDayControlsV2.clear();
        for (String day : ALL_DAYS) {
            String normDay = WeekDay.normalizeDay(day);
            OperatingDayConfig cfg = null;
            if (existing != null && normDay != null) {
                for (Map.Entry<String, OperatingDayConfig> entry : existing.entrySet()) {
                    if (entry.getKey() != null && normDay.equalsIgnoreCase(WeekDay.normalizeDay(entry.getKey()))) {
                        cfg = entry.getValue();
                        break;
                    }
                }
            }
            boolean active = cfg != null ? cfg.isActive() : !WeekDay.SUNDAY.equalsIgnoreCase(day);
            String open = cfg != null && cfg.getOpeningTime() != null && !cfg.getOpeningTime().isBlank() ? cfg.getOpeningTime() : "09:00";
            String close = cfg != null && cfg.getClosingTime() != null && !cfg.getClosingTime().isBlank() ? cfg.getClosingTime() : "21:00";
            operatingDayControlsV2.put(day, new DayControlsV2(formatDayName(day), active, open, close));
        }
    }

    private void buildV2ConfigFormControls(String type, VBox container, BookingConfig existingConfig) {
        container.getChildren().clear();
        if (BookingType.SEAT.equalsIgnoreCase(type)) {
            SeatBookingConfig cfg = (existingConfig instanceof SeatBookingConfig) ? (SeatBookingConfig) existingConfig : null;
            seatSelectionModeComboV2 = new ComboBox<>();
            seatSelectionModeComboV2.getItems().addAll("AUTO_ASSIGN", "MANUAL_SELECT");
            seatSelectionModeComboV2.setValue(cfg != null ? cfg.getSelectionMode() : "AUTO_ASSIGN");

            seatMinDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumDurationMinutes()) : "60");
            seatMaxDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumDurationMinutes()) : "480");
            seatDurationStepFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getDurationStepMinutes()) : "30");
            seatMaxGuestsFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaxGuestsPerBooking()) : "1");

            seatPriceModeComboV2 = new ComboBox<>();
            seatPriceModeComboV2.getItems().addAll("FLAT_PER_SEAT", "TIERED");
            seatPriceModeComboV2.setValue(cfg != null ? cfg.getSeatPriceMode() : "FLAT_PER_SEAT");

            seatAllowMultipleCbV2 = new CheckBox("Allow multiple seat selection");
            seatAllowMultipleCbV2.setSelected(cfg != null && cfg.isAllowMultipleSeats());

            seatAdvanceDaysFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getAdvanceBookingDays()) : "30");
            seatMinAdvanceMinFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumAdvanceMinutes()) : "0");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Selection Mode"), seatSelectionModeComboV2,
                    OwnerUi.fieldLabel("Minimum Duration (minutes)"), seatMinDurationFieldV2,
                    OwnerUi.fieldLabel("Maximum Duration (minutes)"), seatMaxDurationFieldV2,
                    OwnerUi.fieldLabel("Duration Step (minutes)"), seatDurationStepFieldV2,
                    OwnerUi.fieldLabel("Max Guests Per Booking"), seatMaxGuestsFieldV2,
                    OwnerUi.fieldLabel("Seat Price Mode"), seatPriceModeComboV2,
                    seatAllowMultipleCbV2,
                    OwnerUi.fieldLabel("Advance Booking Days"), seatAdvanceDaysFieldV2,
                    OwnerUi.fieldLabel("Minimum Advance Minutes"), seatMinAdvanceMinFieldV2
            );

        } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
            SlotBookingConfig cfg = (existingConfig instanceof SlotBookingConfig) ? (SlotBookingConfig) existingConfig : null;
            slotMinDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumDurationMinutes()) : "60");
            slotMaxDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumDurationMinutes()) : "240");
            slotDurationStepFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getDurationStepMinutes()) : "30");
            slotIntervalFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getSlotIntervalMinutes()) : "30");
            slotBufferBeforeFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getBufferBeforeMinutes()) : "0");
            slotBufferAfterFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getBufferAfterMinutes()) : "0");
            slotMaxPeopleFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaxPeople()) : "10");
            slotAdvanceDaysFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getAdvanceBookingDays()) : "30");
            slotMinAdvanceMinFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumAdvanceMinutes()) : "0");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Duration (minutes)"), slotMinDurationFieldV2,
                    OwnerUi.fieldLabel("Maximum Duration (minutes)"), slotMaxDurationFieldV2,
                    OwnerUi.fieldLabel("Duration Step (minutes)"), slotDurationStepFieldV2,
                    OwnerUi.fieldLabel("Slot Interval (minutes)"), slotIntervalFieldV2,
                    OwnerUi.fieldLabel("Buffer Before Slot (minutes)"), slotBufferBeforeFieldV2,
                    OwnerUi.fieldLabel("Buffer After Slot (minutes)"), slotBufferAfterFieldV2,
                    OwnerUi.fieldLabel("Max People Allowed"), slotMaxPeopleFieldV2,
                    OwnerUi.fieldLabel("Advance Booking Days"), slotAdvanceDaysFieldV2,
                    OwnerUi.fieldLabel("Minimum Advance Minutes"), slotMinAdvanceMinFieldV2
            );

        } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
            WorkshopBookingConfig cfg = (existingConfig instanceof WorkshopBookingConfig) ? (WorkshopBookingConfig) existingConfig : null;
            workshopMinPartFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumParticipantsPerBooking()) : "1");
            workshopMaxPartFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumParticipantsPerBooking()) : "20");
            workshopBookingCutoffFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getBookingCutoffHours()) : "24");
            workshopCancelCutoffFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getCancellationCutoffHours()) : "48");
            workshopAgeRestrFieldV2 = OwnerUi.textField(cfg != null ? val(cfg.getAgeRestriction()) : "");
            workshopMaterialsFieldV2 = OwnerUi.textField(cfg != null && cfg.getMaterialsIncluded() != null ? String.join(", ", cfg.getMaterialsIncluded()) : "");
            workshopInstructorFieldV2 = OwnerUi.textField(cfg != null ? val(cfg.getInstructorName()) : "");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Participants Per Booking"), workshopMinPartFieldV2,
                    OwnerUi.fieldLabel("Maximum Participants Per Booking"), workshopMaxPartFieldV2,
                    OwnerUi.fieldLabel("Booking Cutoff Hours"), workshopBookingCutoffFieldV2,
                    OwnerUi.fieldLabel("Cancellation Cutoff Hours"), workshopCancelCutoffFieldV2,
                    OwnerUi.fieldLabel("Age Restriction"), workshopAgeRestrFieldV2,
                    OwnerUi.fieldLabel("Materials Included (comma separated)"), workshopMaterialsFieldV2,
                    OwnerUi.fieldLabel("Instructor Name"), workshopInstructorFieldV2
            );

        } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
            RentalBookingConfig cfg = (existingConfig instanceof RentalBookingConfig) ? (RentalBookingConfig) existingConfig : null;
            rentalQuantityFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getAvailableQuantity()) : "1");
            rentalMinDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumRentalDurationMinutes()) : "60");
            rentalMaxDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumRentalDurationMinutes()) : "1440");
            rentalSecurityDepositFieldV2 = OwnerUi.textField(cfg != null ? BigDecimal.valueOf(cfg.getSecurityDepositPaise(), 2).toPlainString() : "0.00");
            rentalPickupCbV2 = new CheckBox("Pickup Available");
            rentalPickupCbV2.setSelected(cfg == null || cfg.isPickupAvailable());
            rentalDeliveryCbV2 = new CheckBox("Delivery Available");
            rentalDeliveryCbV2.setSelected(cfg != null && cfg.isDeliveryAvailable());
            rentalDeliveryRadiusFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getDeliveryRadiusKm()) : "5.0");
            rentalDeliveryChargeFieldV2 = OwnerUi.textField(cfg != null ? BigDecimal.valueOf(cfg.getDeliveryChargePaise(), 2).toPlainString() : "0.00");
            rentalPickupInstructionsFieldV2 = OwnerUi.textField(cfg != null ? val(cfg.getPickupInstructions()) : "");
            rentalIdentityProofCbV2 = new CheckBox("Requires Identity Proof");
            rentalIdentityProofCbV2.setSelected(cfg != null && cfg.isRequiresIdentityProof());
            rentalLateFeeFieldV2 = OwnerUi.textField(cfg != null ? BigDecimal.valueOf(cfg.getLateFeePaise(), 2).toPlainString() : "0.00");
            rentalAddOnsFieldV2 = OwnerUi.textField(cfg != null && cfg.getAddOns() != null ? String.join(", ", cfg.getAddOns()) : "");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Available Quantity"), rentalQuantityFieldV2,
                    OwnerUi.fieldLabel("Minimum Duration (minutes)"), rentalMinDurationFieldV2,
                    OwnerUi.fieldLabel("Maximum Duration (minutes)"), rentalMaxDurationFieldV2,
                    OwnerUi.fieldLabel("Security Deposit (₹ Rupees)"), rentalSecurityDepositFieldV2,
                    rentalPickupCbV2, rentalDeliveryCbV2,
                    OwnerUi.fieldLabel("Delivery Radius (Km)"), rentalDeliveryRadiusFieldV2,
                    OwnerUi.fieldLabel("Delivery Charge (₹ Rupees)"), rentalDeliveryChargeFieldV2,
                    OwnerUi.fieldLabel("Pickup Instructions"), rentalPickupInstructionsFieldV2,
                    rentalIdentityProofCbV2,
                    OwnerUi.fieldLabel("Late Fee (₹ Rupees per hour)"), rentalLateFeeFieldV2,
                    OwnerUi.fieldLabel("Add-Ons (comma separated)"), rentalAddOnsFieldV2
            );

        } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
            VenueBookingConfig cfg = (existingConfig instanceof VenueBookingConfig) ? (VenueBookingConfig) existingConfig : null;
            venueMinHoursFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumBookingHours()) : "2");
            venueMaxHoursFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumBookingHours()) : "24");
            venueRequiresApprovalCbV2 = new CheckBox("Requires Approval");
            venueRequiresApprovalCbV2.setSelected(cfg == null || cfg.isRequiresApproval());
            venueMinAdvanceDaysFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumAdvanceDays()) : "1");
            venueAllowedEventTypesFieldV2 = OwnerUi.textField(cfg != null && cfg.getAllowedEventTypes() != null ? String.join(", ", cfg.getAllowedEventTypes()) : "");
            venueSetupOptionsFieldV2 = OwnerUi.textField(cfg != null && cfg.getSetupOptions() != null ? String.join(", ", cfg.getSetupOptions()) : "");
            venueSecurityDepositFieldV2 = OwnerUi.textField(cfg != null ? BigDecimal.valueOf(cfg.getSecurityDepositPaise(), 2).toPlainString() : "0.00");
            venueSoundSystemCbV2 = new CheckBox("Sound System Available");
            venueSoundSystemCbV2.setSelected(cfg != null && cfg.isSoundSystemAvailable());
            venueLightingCbV2 = new CheckBox("Lighting Available");
            venueLightingCbV2.setSelected(cfg != null && cfg.isLightingAvailable());
            venueStageCbV2 = new CheckBox("Stage Available");
            venueStageCbV2.setSelected(cfg != null && cfg.isStageAvailable());
            venueGreenRoomCbV2 = new CheckBox("Green Room Available");
            venueGreenRoomCbV2.setSelected(cfg != null && cfg.isGreenRoomAvailable());
            venueOutsideVendorCbV2 = new CheckBox("Outside Vendors Allowed");
            venueOutsideVendorCbV2.setSelected(cfg == null || cfg.isOutsideVendorAllowed());
            venueCateringCbV2 = new CheckBox("Catering Allowed");
            venueCateringCbV2.setSelected(cfg == null || cfg.isCateringAllowed());
            venueOvertimeRateFieldV2 = OwnerUi.textField(cfg != null ? BigDecimal.valueOf(cfg.getOvertimeRatePaise(), 2).toPlainString() : "0.00");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Booking Hours"), venueMinHoursFieldV2,
                    OwnerUi.fieldLabel("Maximum Booking Hours"), venueMaxHoursFieldV2,
                    venueRequiresApprovalCbV2,
                    OwnerUi.fieldLabel("Minimum Advance Days"), venueMinAdvanceDaysFieldV2,
                    OwnerUi.fieldLabel("Allowed Event Types (comma separated)"), venueAllowedEventTypesFieldV2,
                    OwnerUi.fieldLabel("Setup Options (comma separated)"), venueSetupOptionsFieldV2,
                    OwnerUi.fieldLabel("Security Deposit (₹ Rupees)"), venueSecurityDepositFieldV2,
                    venueSoundSystemCbV2, venueLightingCbV2, venueStageCbV2, venueGreenRoomCbV2,
                    venueOutsideVendorCbV2, venueCateringCbV2,
                    OwnerUi.fieldLabel("Overtime Rate (₹ Rupees per hour)"), venueOvertimeRateFieldV2
            );

        } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
            PracticeRoomBookingConfig cfg = (existingConfig instanceof PracticeRoomBookingConfig) ? (PracticeRoomBookingConfig) existingConfig : null;
            practiceMinDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMinimumDurationMinutes()) : "60");
            practiceMaxDurationFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getMaximumDurationMinutes()) : "240");
            practiceDurationStepFieldV2 = OwnerUi.textField(cfg != null ? String.valueOf(cfg.getDurationStepMinutes()) : "30");
            practiceEquipmentSelectionCbV2 = new CheckBox("Equipment Selection Enabled");
            practiceEquipmentSelectionCbV2.setSelected(cfg != null && cfg.isEquipmentSelectionEnabled());
            practiceIncludedEquipmentFieldV2 = OwnerUi.textField(cfg != null && cfg.getIncludedEquipment() != null ? String.join(", ", cfg.getIncludedEquipment()) : "");
            practiceOptionalEquipmentFieldV2 = OwnerUi.textField(cfg != null && cfg.getOptionalEquipment() != null ? String.join(", ", cfg.getOptionalEquipment()) : "");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Duration (minutes)"), practiceMinDurationFieldV2,
                    OwnerUi.fieldLabel("Maximum Duration (minutes)"), practiceMaxDurationFieldV2,
                    OwnerUi.fieldLabel("Duration Step (minutes)"), practiceDurationStepFieldV2,
                    practiceEquipmentSelectionCbV2,
                    OwnerUi.fieldLabel("Included Equipment (comma separated)"), practiceIncludedEquipmentFieldV2,
                    OwnerUi.fieldLabel("Optional Equipment (comma separated)"), practiceOptionalEquipmentFieldV2
            );
        }
    }

    private void handleSaveV2Changes() {
        if (isSaving) return;
        isSaving = true;
        saveBtn.setDisable(true);
        saveBtn.setText("Saving Changes...");
        errorLabel.setText("");

        String name = spaceNameFieldV2.getText().trim();
        if (name.isBlank()) {
            failSaveV2("Space Name is required.");
            return;
        }

        String desc = descriptionFieldV2.getText().trim();
        if (desc.isBlank()) {
            failSaveV2("Description is required.");
            return;
        }

        String addr = addressFieldV2.getText().trim();
        if (addr.isBlank()) {
            failSaveV2("Address Line is required.");
            return;
        }

        String area = areaFieldV2.getText().trim();
        if (area.isBlank()) {
            failSaveV2("Area / Locality is required.");
            return;
        }

        String city = cityFieldV2.getText().trim();
        if (city.isBlank()) {
            failSaveV2("City is required.");
            return;
        }

        String state = stateFieldV2.getText().trim();
        if (state.isBlank()) {
            failSaveV2("State is required.");
            return;
        }

        String postal = postalCodeFieldV2.getText().trim();
        if (!POSTAL_CODE_PATTERN.matcher(postal).matches()) {
            failSaveV2("Postal Code must be 6 digits.");
            return;
        }

        try {
            ZoneId.of(timeZoneFieldV2.getText().trim());
        } catch (Exception ex) {
            failSaveV2("Invalid Time Zone ID (e.g. Asia/Kolkata).");
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capacityFieldV2.getText().trim());
            if (cap <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            failSaveV2("Capacity must be a positive integer.");
            return;
        }

        long basePricePaise;
        try {
            BigDecimal bd = new BigDecimal(basePriceRupeesFieldV2.getText().trim());
            if (bd.compareTo(BigDecimal.ZERO) <= 0 || bd.scale() > 2) throw new IllegalArgumentException();
            basePricePaise = bd.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (Exception ex) {
            failSaveV2("Invalid Base Price format.");
            return;
        }

        // Validate operating hours
        Map<String, OperatingDayConfig> hours = new LinkedHashMap<>();
        boolean anyActive = false;
        for (Map.Entry<String, DayControlsV2> entry : operatingDayControlsV2.entrySet()) {
            DayControlsV2 ctrl = entry.getValue();
            String normDay = WeekDay.normalizeDay(entry.getKey());
            if (ctrl.activeCb.isSelected()) {
                anyActive = true;
                String open = ctrl.openField.getText().trim();
                String close = ctrl.closeField.getText().trim();
                if (!TIME_PATTERN.matcher(open).matches() || !TIME_PATTERN.matcher(close).matches() || open.compareTo(close) >= 0) {
                    failSaveV2("Invalid operating hours for " + formatDayName(normDay) + ".");
                    return;
                }
                if (normDay != null) {
                    hours.put(normDay, new OperatingDayConfig(open, close, true));
                }
            } else if (normDay != null) {
                String open = ctrl.openField.getText().trim();
                String close = ctrl.closeField.getText().trim();
                if (open.isBlank() || !TIME_PATTERN.matcher(open).matches()) open = "09:00";
                if (close.isBlank() || !TIME_PATTERN.matcher(close).matches()) close = "21:00";
                hours.put(normDay, new OperatingDayConfig(open, close, false));
            }
        }
        if (!anyActive) {
            failSaveV2("Select at least one active operating day.");
            return;
        }

        BookingConfig config = assembleV2BookingConfig(bookingTypeComboV2.getValue(), basePricePaise);
        if (config == null) {
            failSaveV2("Invalid configuration settings.");
            return;
        }

        SpaceLocation location = new SpaceLocation(
                addr, area, city, state, postal,
                landmarkFieldV2.getText().trim(), 0.0, 0.0
        );

        Space submitted = Space.createV2(
                activeSpace.getSpaceId(),
                activeSpace.getBusinessId(),
                activeSpace.getOwnerId(),
                name,
                desc,
                activeSpace.getCategory(),
                activeSpace.getActivityGroup(),
                activeSpace.getActivityType(),
                location,
                activeSpace.getCoverPhotoUrl(),
                activeSpace.getPhotoUrls(),
                splitCleanList(amenitiesFieldV2.getText()),
                splitCleanList(facilitiesFieldV2.getText()),
                cap,
                bookingTypeComboV2.getValue(),
                basePricePaise,
                pricingUnitComboV2.getValue(),
                config,
                hours,
                splitCleanList(rulesFieldV2.getText()),
                cancellationPolicyFieldV2.getText().trim()
        );

        submitted.setTimeZoneId(timeZoneFieldV2.getText().trim());

        // Status reset rules on edit: if APPROVED or REJECTED, reset to DRAFT
        String currentApproval = val(activeSpace.getApprovalStatus()).toUpperCase();
        if ("APPROVED".equals(currentApproval) || "REJECTED".equals(currentApproval)) {
            submitted.setApprovalStatus(SpaceStatus.DRAFT);
            submitted.setOperationalStatus("INACTIVE");
        } else {
            submitted.setApprovalStatus(activeSpace.getApprovalStatus());
            submitted.setOperationalStatus(activeSpace.getOperationalStatus());
        }

        spaceController.updateV2Space(activeSpace.getSpaceId(), submitted)
                .thenAccept(res -> Platform.runLater(() -> {
                    isSaving = false;
                    if (res.isSuccess() && res.getData() != null) {
                        SpaceFlowRouter.goToSpaceDetails(res.getData());
                    } else {
                        saveBtn.setDisable(false);
                        saveBtn.setText("Save V2 Space Changes");
                        errorLabel.setText("Failed to update space: " + res.getMessage());
                    }
                })).exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isSaving = false;
                        saveBtn.setDisable(false);
                        saveBtn.setText("Save V2 Space Changes");
                        errorLabel.setText("Error updating space: " + ex.getMessage());
                    });
                    return null;
                });
    }

    private void failSaveV2(String msg) {
        isSaving = false;
        saveBtn.setDisable(false);
        saveBtn.setText("Save V2 Space Changes");
        errorLabel.setText(msg);
    }

    private BookingConfig assembleV2BookingConfig(String type, long basePricePaise) {
        try {
            if (BookingType.SEAT.equalsIgnoreCase(type)) {
                return new SeatBookingConfig(
                        seatSelectionModeComboV2.getValue(),
                        Integer.parseInt(seatMinDurationFieldV2.getText().trim()),
                        Integer.parseInt(seatMaxDurationFieldV2.getText().trim()),
                        Integer.parseInt(seatDurationStepFieldV2.getText().trim()),
                        Integer.parseInt(seatMaxGuestsFieldV2.getText().trim()),
                        seatPriceModeComboV2.getValue(),
                        seatAllowMultipleCbV2.isSelected(),
                        Integer.parseInt(seatAdvanceDaysFieldV2.getText().trim()),
                        Integer.parseInt(seatMinAdvanceMinFieldV2.getText().trim())
                );
            } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
                return new SlotBookingConfig(
                        Integer.parseInt(slotMinDurationFieldV2.getText().trim()),
                        Integer.parseInt(slotMaxDurationFieldV2.getText().trim()),
                        Integer.parseInt(slotDurationStepFieldV2.getText().trim()),
                        Integer.parseInt(slotIntervalFieldV2.getText().trim()),
                        Integer.parseInt(slotBufferBeforeFieldV2.getText().trim()),
                        Integer.parseInt(slotBufferAfterFieldV2.getText().trim()),
                        Integer.parseInt(slotMaxPeopleFieldV2.getText().trim()),
                        Integer.parseInt(slotAdvanceDaysFieldV2.getText().trim()),
                        Integer.parseInt(slotMinAdvanceMinFieldV2.getText().trim())
                );
            } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
                return new WorkshopBookingConfig(
                        basePricePaise,
                        Integer.parseInt(workshopMinPartFieldV2.getText().trim()),
                        Integer.parseInt(workshopMaxPartFieldV2.getText().trim()),
                        Integer.parseInt(workshopBookingCutoffFieldV2.getText().trim()),
                        Integer.parseInt(workshopCancelCutoffFieldV2.getText().trim()),
                        workshopAgeRestrFieldV2.getText().trim(),
                        splitCleanList(workshopMaterialsFieldV2.getText()),
                        workshopInstructorFieldV2.getText().trim()
                );
            } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
                return new RentalBookingConfig(
                        Integer.parseInt(rentalQuantityFieldV2.getText().trim()),
                        Integer.parseInt(rentalMinDurationFieldV2.getText().trim()),
                        Integer.parseInt(rentalMaxDurationFieldV2.getText().trim()),
                        parseRupeesToPaise(rentalSecurityDepositFieldV2.getText().trim()),
                        rentalPickupCbV2.isSelected(),
                        rentalDeliveryCbV2.isSelected(),
                        Double.parseDouble(rentalDeliveryRadiusFieldV2.getText().trim()),
                        parseRupeesToPaise(rentalDeliveryChargeFieldV2.getText().trim()),
                        rentalPickupInstructionsFieldV2.getText().trim(),
                        rentalIdentityProofCbV2.isSelected(),
                        parseRupeesToPaise(rentalLateFeeFieldV2.getText().trim()),
                        splitCleanList(rentalAddOnsFieldV2.getText())
                );
            } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
                return new VenueBookingConfig(
                        Integer.parseInt(venueMinHoursFieldV2.getText().trim()),
                        Integer.parseInt(venueMaxHoursFieldV2.getText().trim()),
                        venueRequiresApprovalCbV2.isSelected(),
                        Integer.parseInt(venueMinAdvanceDaysFieldV2.getText().trim()),
                        splitCleanList(venueAllowedEventTypesFieldV2.getText()),
                        splitCleanList(venueSetupOptionsFieldV2.getText()),
                        parseRupeesToPaise(venueSecurityDepositFieldV2.getText().trim()),
                        venueSoundSystemCbV2.isSelected(),
                        venueLightingCbV2.isSelected(),
                        venueStageCbV2.isSelected(),
                        venueGreenRoomCbV2.isSelected(),
                        venueOutsideVendorCbV2.isSelected(),
                        venueCateringCbV2.isSelected(),
                        parseRupeesToPaise(venueOvertimeRateFieldV2.getText().trim())
                );
            } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
                return new PracticeRoomBookingConfig(
                        Integer.parseInt(practiceMinDurationFieldV2.getText().trim()),
                        Integer.parseInt(practiceMaxDurationFieldV2.getText().trim()),
                        Integer.parseInt(practiceDurationStepFieldV2.getText().trim()),
                        practiceEquipmentSelectionCbV2.isSelected(),
                        splitCleanList(practiceIncludedEquipmentFieldV2.getText()),
                        splitCleanList(practiceOptionalEquipmentFieldV2.getText())
                );
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    // ==================== V1 EDIT FORM ====================

    private void renderV1Form() {
        container.getChildren().clear();

        if (initialSpace == null) {
            container.getChildren().add(OwnerUi.body("No space selected to edit.", 14, OwnerUi.ERROR));
            return;
        }

        if (!SpaceStatus.DRAFT.equalsIgnoreCase(initialSpace.getStatus())) {
            VBox card = OwnerUi.card();
            Button backDetailsBtn = OwnerUi.secondaryButton("← Back to Details");
            backDetailsBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(initialSpace));
            card.getChildren().addAll(
                    OwnerUi.heading("Editing Disabled", 18),
                    OwnerUi.body("Only DRAFT spaces can be edited. Current status: " + initialSpace.getStatus(), 13, OwnerUi.TEXT_MUTED),
                    backDetailsBtn
            );
            container.getChildren().add(card);
            return;
        }

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(
                OwnerUi.heading("Edit Space: " + val(initialSpace.getSpaceName()), 22),
                OwnerUi.badge(initialSpace.getStatus())
        );

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        saveBtn = OwnerUi.primaryButton("Loading Businesses...");
        saveBtn.setDisable(true);
        saveBtn.setOnAction(e -> handleSaveV1Changes());

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        businessComboV1 = new ComboBox<>();
        businessComboV1.setMaxWidth(Double.MAX_VALUE);
        businessController.loadCurrentOwnerBusinesses().thenAccept(bRes -> Platform.runLater(() -> {
            if (bRes.isSuccess() && bRes.getData() != null && !bRes.getData().isEmpty()) {
                businessComboV1.getItems().clear();
                businessComboV1.getItems().addAll(bRes.getData());
                Business matched = null;
                for (Business b : bRes.getData()) {
                    if (b.getBusinessId() != null && b.getBusinessId().equals(initialSpace.getBusinessId())) {
                        matched = b;
                        break;
                    }
                }
                if (matched != null) {
                    businessComboV1.setValue(matched);
                    saveBtn.setDisable(false);
                    saveBtn.setText("Save V1 Changes");
                } else {
                    errorLabel.setText("The business linked to this space could not be found.");
                    saveBtn.setDisable(true);
                    saveBtn.setText("Save Disabled");
                }
            } else {
                errorLabel.setText("Failed to load owner businesses. Editing disabled.");
                saveBtn.setDisable(true);
                saveBtn.setText("Save Disabled");
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                errorLabel.setText("Error loading businesses: " + ex.getMessage());
                saveBtn.setDisable(true);
                saveBtn.setText("Save Disabled");
            });
            return null;
        });

        spaceNameFieldV1 = OwnerUi.textField("");
        spaceNameFieldV1.setText(val(initialSpace.getSpaceName()));

        descriptionFieldV1 = OwnerUi.textArea("", 3);
        descriptionFieldV1.setText(val(initialSpace.getDescription()));

        categoryComboV1 = new ComboBox<>();
        categoryComboV1.setMaxWidth(Double.MAX_VALUE);
        categoryComboV1.getItems().addAll(SpaceCategory.CREATIVITY, SpaceCategory.WELLNESS, SpaceCategory.SPORTS, SpaceCategory.STUDY_WORKSPACE);
        categoryComboV1.setValue(initialSpace.getCategory() != null ? initialSpace.getCategory() : SpaceCategory.CREATIVITY);

        groupComboV1 = new ComboBox<>();
        groupComboV1.setMaxWidth(Double.MAX_VALUE);
        groupComboV1.getItems().addAll(CreativityActivityGroup.ART, CreativityActivityGroup.CONTENT_CREATION, CreativityActivityGroup.PODCASTS, CreativityActivityGroup.MUSIC);
        groupComboV1.setValue(initialSpace.getActivityGroup() != null ? initialSpace.getActivityGroup() : CreativityActivityGroup.CONTENT_CREATION);

        spaceTypeComboV1 = new ComboBox<>();
        spaceTypeComboV1.setMaxWidth(Double.MAX_VALUE);
        updateSpaceTypesForGroupV1(groupComboV1.getValue(), spaceTypeComboV1);
        if (initialSpace.getSpaceType() != null && spaceTypeComboV1.getItems().contains(initialSpace.getSpaceType())) {
            spaceTypeComboV1.setValue(initialSpace.getSpaceType());
        }

        groupComboV1.setOnAction(e -> updateSpaceTypesForGroupV1(groupComboV1.getValue(), spaceTypeComboV1));

        addressFieldV1 = OwnerUi.textField("");
        addressFieldV1.setText(val(initialSpace.getAddressLine()));

        landmarkFieldV1 = OwnerUi.textField("");
        landmarkFieldV1.setText(val(initialSpace.getLandmark()));

        cityFieldV1 = OwnerUi.textField("");
        cityFieldV1.setText(val(initialSpace.getCity()));

        stateFieldV1 = OwnerUi.textField("");
        stateFieldV1.setText(val(initialSpace.getState()));

        postalCodeFieldV1 = OwnerUi.textField("");
        postalCodeFieldV1.setText(val(initialSpace.getPostalCode()));

        capacityFieldV1 = OwnerUi.textField("");
        capacityFieldV1.setText(initialSpace.getCapacity() > 0 ? String.valueOf(initialSpace.getCapacity()) : "");

        initAmenitiesCheckboxesV1();
        FlowPane amenityGrid = new FlowPane();
        amenityGrid.setHgap(12);
        amenityGrid.setVgap(8);
        amenityGrid.getChildren().addAll(amenityCheckBoxesV1);

        customAmenitiesFieldV1 = OwnerUi.textField("");
        populateCustomAmenitiesV1();

        suitableActivitiesFieldV1 = OwnerUi.textField("");
        suitableActivitiesFieldV1.setText(initialSpace.getSuitableActivities() != null ? String.join(", ", initialSpace.getSuitableActivities()) : "");

        rulesFieldV1 = OwnerUi.textArea("", 2);
        rulesFieldV1.setText(initialSpace.getRules() != null ? String.join("\n", initialSpace.getRules()) : "");

        openingTimeFieldV1 = OwnerUi.textField("");
        openingTimeFieldV1.setText(initialSpace.getOpeningTime() != null ? initialSpace.getOpeningTime() : "08:00 AM");

        closingTimeFieldV1 = OwnerUi.textField("");
        closingTimeFieldV1.setText(initialSpace.getClosingTime() != null ? initialSpace.getClosingTime() : "09:00 PM");

        initDaysCheckboxesV1();
        HBox daysBox = new HBox(12);
        daysBox.getChildren().addAll(dayCheckBoxesV1);

        Button cancelBtn = OwnerUi.secondaryButton("Cancel");
        cancelBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(initialSpace));

        HBox actions = new HBox(12, cancelBtn, saveBtn);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(
                titleRow,
                OwnerUi.fieldLabel("Associated Business *"), businessComboV1,
                OwnerUi.fieldLabel("Space Name *"), spaceNameFieldV1,
                OwnerUi.fieldLabel("Description *"), descriptionFieldV1,
                OwnerUi.fieldLabel("Category *"), categoryComboV1,
                OwnerUi.fieldLabel("Activity Group *"), groupComboV1,
                OwnerUi.fieldLabel("Space Type *"), spaceTypeComboV1,
                OwnerUi.fieldLabel("Capacity (guests) *"), capacityFieldV1,
                OwnerUi.fieldLabel("Address Line *"), addressFieldV1,
                OwnerUi.fieldLabel("Landmark"), landmarkFieldV1,
                OwnerUi.fieldLabel("City *"), cityFieldV1,
                OwnerUi.fieldLabel("State *"), stateFieldV1,
                OwnerUi.fieldLabel("Postal Code *"), postalCodeFieldV1,
                OwnerUi.fieldLabel("Standard Amenities"), amenityGrid,
                OwnerUi.fieldLabel("Custom Amenities"), customAmenitiesFieldV1,
                OwnerUi.fieldLabel("Suitable Activities"), suitableActivitiesFieldV1,
                OwnerUi.fieldLabel("House Rules"), rulesFieldV1,
                OwnerUi.fieldLabel("Operating Days *"), daysBox,
                OwnerUi.fieldLabel("Opening Time *"), openingTimeFieldV1,
                OwnerUi.fieldLabel("Closing Time *"), closingTimeFieldV1,
                errorLabel, actions
        );

        container.getChildren().add(formCard);
    }

    private void initAmenitiesCheckboxesV1() {
        amenityCheckBoxesV1.clear();
        List<String> defaultAmenities = Arrays.asList(
                "Wi-Fi", "Air Conditioning", "Parking", "Power Backup", "Lighting",
                "Sound System", "Microphones", "Cameras", "Tripods", "Editing Workstation", "Washroom"
        );
        List<String> existingList = initialSpace.getAmenities() != null ? initialSpace.getAmenities() : new ArrayList<>();

        for (String a : defaultAmenities) {
            CheckBox cb = new CheckBox(a);
            if (existingList.stream().anyMatch(e -> e.equalsIgnoreCase(a))) {
                cb.setSelected(true);
            }
            amenityCheckBoxesV1.add(cb);
        }
    }

    private void populateCustomAmenitiesV1() {
        List<String> defaultAmenities = Arrays.asList(
                "Wi-Fi", "Air Conditioning", "Parking", "Power Backup", "Lighting",
                "Sound System", "Microphones", "Cameras", "Tripods", "Editing Workstation", "Washroom"
        );
        List<String> existingList = initialSpace.getAmenities() != null ? initialSpace.getAmenities() : new ArrayList<>();
        List<String> customList = existingList.stream()
                .filter(a -> defaultAmenities.stream().noneMatch(d -> d.equalsIgnoreCase(a)))
                .collect(Collectors.toList());

        if (!customList.isEmpty()) {
            customAmenitiesFieldV1.setText(String.join(", ", customList));
        }
    }

    private void initDaysCheckboxesV1() {
        dayCheckBoxesV1.clear();
        List<String> days = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        List<String> existingDays = initialSpace.getOperatingDays() != null ? initialSpace.getOperatingDays() : new ArrayList<>();

        for (String d : days) {
            CheckBox cb = new CheckBox(d);
            boolean matched = existingDays.stream().anyMatch(ed ->
                    ed.equalsIgnoreCase(d)
                            || ed.toUpperCase().startsWith(d.toUpperCase())
                            || d.toUpperCase().startsWith(ed.toUpperCase())
            );
            if (matched) {
                cb.setSelected(true);
            }
            dayCheckBoxesV1.add(cb);
        }
    }

    private void updateSpaceTypesForGroupV1(String group, ComboBox<String> typeCombo) {
        String currentVal = typeCombo.getValue();
        typeCombo.getItems().clear();
        if (CreativityActivityGroup.ART.equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Art Studio", "Pottery Studio", "Craft Workshop", "Cooking Workshop");
        } else if (CreativityActivityGroup.CONTENT_CREATION.equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Creator Studio", "Photography Studio", "Aesthetic Café", "Outdoor Shooting Location");
        } else if (CreativityActivityGroup.PODCASTS.equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Podcast Studio", "Editing Space", "Podcast Workshop");
        } else if (CreativityActivityGroup.MUSIC.equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Recording Studio", "Practice Room", "Music Workshop", "Performance Venue");
        } else {
            typeCombo.getItems().add("Creator Studio");
        }

        if (currentVal != null && typeCombo.getItems().contains(currentVal)) {
            typeCombo.setValue(currentVal);
        } else if (!typeCombo.getItems().isEmpty()) {
            typeCombo.setValue(typeCombo.getItems().get(0));
        }
    }

    private void handleSaveV1Changes() {
        if (isSaving) return;
        isSaving = true;
        saveBtn.setDisable(true);
        saveBtn.setText("Saving Changes...");
        errorLabel.setText("");

        Business selectedB = businessComboV1.getValue();
        if (selectedB == null) {
            failSaveV1("Please select a valid Business.");
            return;
        }

        String nameText = spaceNameFieldV1.getText().trim();
        if (nameText.isBlank()) {
            failSaveV1("Space Name cannot be empty.");
            return;
        }

        String descText = descriptionFieldV1.getText().trim();
        if (descText.isBlank()) {
            failSaveV1("Description cannot be empty.");
            return;
        }

        String addressText = addressFieldV1.getText().trim();
        if (addressText.isBlank()) {
            failSaveV1("Address Line cannot be empty.");
            return;
        }

        String cityText = cityFieldV1.getText().trim();
        if (cityText.isBlank()) {
            failSaveV1("City cannot be empty.");
            return;
        }

        String stateText = stateFieldV1.getText().trim();
        if (stateText.isBlank()) {
            failSaveV1("State cannot be empty.");
            return;
        }

        String postalText = postalCodeFieldV1.getText().trim();
        if (postalText.isBlank()) {
            failSaveV1("Postal Code cannot be empty.");
            return;
        }

        int cap;
        try {
            cap = Integer.parseInt(capacityFieldV1.getText().trim());
            if (cap <= 0) throw new NumberFormatException();
        } catch (Exception ex) {
            failSaveV1("Capacity must be a positive integer.");
            return;
        }

        List<String> selectedDays = new ArrayList<>();
        for (CheckBox cb : dayCheckBoxesV1) {
            if (cb.isSelected()) selectedDays.add(cb.getText());
        }
        if (selectedDays.isEmpty()) {
            failSaveV1("Select at least one operating day.");
            return;
        }

        List<String> amenities = new ArrayList<>();
        for (CheckBox cb : amenityCheckBoxesV1) {
            if (cb.isSelected()) amenities.add(cb.getText());
        }
        String customA = customAmenitiesFieldV1.getText().trim();
        if (!customA.isBlank()) {
            for (String s : customA.split(",")) {
                if (!s.isBlank() && !amenities.contains(s.trim())) {
                    amenities.add(s.trim());
                }
            }
        }

        List<String> suitableActivities = new ArrayList<>();
        String actText = suitableActivitiesFieldV1.getText().trim();
        if (!actText.isBlank()) {
            for (String s : actText.split(",")) {
                if (!s.isBlank()) suitableActivities.add(s.trim());
            }
        }

        List<String> rules = new ArrayList<>();
        String rulesText = rulesFieldV1.getText().trim();
        if (!rulesText.isBlank()) {
            for (String s : rulesText.split("\n")) {
                if (!s.isBlank()) rules.add(s.trim());
            }
        }

        Space updatedSpace = new Space();
        updatedSpace.setSpaceId(initialSpace.getSpaceId());
        updatedSpace.setBusinessId(selectedB.getBusinessId());
        updatedSpace.setOwnerId(initialSpace.getOwnerId());
        updatedSpace.setSpaceName(nameText);
        updatedSpace.setDescription(descText);
        updatedSpace.setCategory(categoryComboV1.getValue());
        updatedSpace.setActivityGroup(groupComboV1.getValue());
        updatedSpace.setSpaceType(spaceTypeComboV1.getValue());
        updatedSpace.setAddressLine(addressText);
        updatedSpace.setLandmark(landmarkFieldV1.getText().trim());
        updatedSpace.setCity(cityText);
        updatedSpace.setState(stateText);
        updatedSpace.setPostalCode(postalText);
        updatedSpace.setCapacity(cap);
        updatedSpace.setAmenities(amenities);
        updatedSpace.setSuitableActivities(suitableActivities);
        updatedSpace.setRules(rules);
        updatedSpace.setOpeningTime(openingTimeFieldV1.getText().trim());
        updatedSpace.setClosingTime(closingTimeFieldV1.getText().trim());
        updatedSpace.setOperatingDays(selectedDays);
        updatedSpace.setStatus(initialSpace.getStatus());
        updatedSpace.setActive(initialSpace.isActive());
        updatedSpace.setCreatedAt(initialSpace.getCreatedAt());

        spaceController.updateSpace(updatedSpace).thenAccept(res -> Platform.runLater(() -> {
            isSaving = false;
            if (res.isSuccess() && res.getData() != null) {
                SpaceFlowRouter.goToSpaceDetails(res.getData());
            } else {
                saveBtn.setDisable(false);
                saveBtn.setText("Save V1 Changes");
                errorLabel.setText("Failed to update space: " + res.getMessage());
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                isSaving = false;
                saveBtn.setDisable(false);
                saveBtn.setText("Save V1 Changes");
                errorLabel.setText("Error saving space: " + ex.getMessage());
            });
            return null;
        });
    }

    private void failSaveV1(String msg) {
        isSaving = false;
        saveBtn.setDisable(false);
        saveBtn.setText("Save V1 Changes");
        errorLabel.setText(msg);
    }

    // ==================== HELPER METHODS ====================

    private static long parseRupeesToPaise(String rupeeStr) {
        if (rupeeStr == null || rupeeStr.isBlank()) return 0L;
        try {
            BigDecimal bd = new BigDecimal(rupeeStr.trim());
            return bd.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static List<String> splitCleanList(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        for (String item : input.split("[,\\n]")) {
            String trimmed = item.trim();
            if (!trimmed.isBlank() && !result.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        return result;
    }

    private static String formatV2Status(Space s) {
        if (s == null) return "DRAFT";
        String app = s.getApprovalStatus();
        if (app == null || app.isBlank()) return s.getStatus() != null ? s.getStatus() : "DRAFT";
        app = app.toUpperCase();
        if ("APPROVED".equals(app)) {
            String op = s.getOperationalStatus() != null ? s.getOperationalStatus().toUpperCase() : "INACTIVE";
            return "ACTIVE".equals(op) ? "APPROVED (ACTIVE)" : "APPROVED (INACTIVE)";
        }
        return app;
    }

    private static String val(String str) {
        return str != null ? str : "";
    }
}
