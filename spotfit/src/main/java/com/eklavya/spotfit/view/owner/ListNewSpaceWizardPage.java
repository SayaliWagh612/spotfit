package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BookingType;
import com.eklavya.spotfit.constants.PricingUnit;
import com.eklavya.spotfit.constants.SpaceCategory;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;
import com.eklavya.spotfit.service.BusinessService;
import com.eklavya.spotfit.session.AppSession;

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

/**
 * 6-step multi-page wizard view for creating a new V2 DRAFT Space linked to an approved V2 Creativity Business.
 */
public class ListNewSpaceWizardPage {

    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private final SpaceController spaceController;
    private final BusinessController businessController;
    private final BusinessService businessService;
    private final VBox container;

    private int currentStep = 1;

    // Wizard Form State
    private Business selectedBusiness;
    private String selectedActivityGroup;
    private String selectedActivityType;

    // Step 2 State
    final TextField spaceNameField = OwnerUi.textField("e.g. DAWN Podcast Studio");
    final TextArea descriptionField = OwnerUi.textArea("Describe your physical venue...", 3);
    final TextField addressField = OwnerUi.textField("Address Line / Street");
    final TextField areaField = OwnerUi.textField("Area / Locality");
    final TextField landmarkField = OwnerUi.textField("Landmark / Nearby spot");
    final TextField cityField = OwnerUi.textField("City");
    final TextField stateField = OwnerUi.textField("State");
    final TextField postalCodeField = OwnerUi.textField("6-digit Postal Code (e.g. 411001)");
    final TextField timeZoneField = OwnerUi.textField("Asia/Kolkata");
    final TextField capacityField = OwnerUi.textField("e.g. 15");

    // Step 3 State
    ComboBox<String> bookingTypeCombo;
    TextField basePriceRupeesField = OwnerUi.textField("e.g. 500.00");
    ComboBox<String> pricingUnitCombo;

    // Step 4 Config Controls
    VBox configContainer;
    // Seat Config
    ComboBox<String> seatSelectionModeCombo;
    TextField seatMinDurationField = OwnerUi.textField("60");
    TextField seatMaxDurationField = OwnerUi.textField("480");
    TextField seatDurationStepField = OwnerUi.textField("30");
    TextField seatMaxGuestsField = OwnerUi.textField("1");
    ComboBox<String> seatPriceModeCombo;
    CheckBox seatAllowMultipleCb;
    TextField seatAdvanceDaysField = OwnerUi.textField("30");
    TextField seatMinAdvanceMinField = OwnerUi.textField("0");

    // Slot Config
    TextField slotMinDurationField = OwnerUi.textField("60");
    TextField slotMaxDurationField = OwnerUi.textField("240");
    TextField slotDurationStepField = OwnerUi.textField("30");
    TextField slotIntervalField = OwnerUi.textField("30");
    TextField slotBufferBeforeField = OwnerUi.textField("0");
    TextField slotBufferAfterField = OwnerUi.textField("0");
    TextField slotMaxPeopleField = OwnerUi.textField("10");
    TextField slotAdvanceDaysField = OwnerUi.textField("30");
    TextField slotMinAdvanceMinField = OwnerUi.textField("0");

    // Workshop Config
    TextField workshopMinPartField = OwnerUi.textField("1");
    TextField workshopMaxPartField = OwnerUi.textField("20");
    TextField workshopBookingCutoffField = OwnerUi.textField("24");
    TextField workshopCancelCutoffField = OwnerUi.textField("48");
    TextField workshopAgeRestrField = OwnerUi.textField("e.g. 12+");
    TextField workshopMaterialsField = OwnerUi.textField("Canvas, Paints, Brushes");
    TextField workshopInstructorField = OwnerUi.textField("Instructor Name");

    // Rental Config
    TextField rentalQuantityField = OwnerUi.textField("1");
    TextField rentalMinDurationField = OwnerUi.textField("60");
    TextField rentalMaxDurationField = OwnerUi.textField("1440");
    TextField rentalSecurityDepositField = OwnerUi.textField("0.00");
    CheckBox rentalPickupCb;
    CheckBox rentalDeliveryCb;
    TextField rentalDeliveryRadiusField = OwnerUi.textField("5.0");
    TextField rentalDeliveryChargeField = OwnerUi.textField("0.00");
    TextField rentalPickupInstructionsField = OwnerUi.textField("Pickup from front desk");
    CheckBox rentalIdentityProofCb;
    TextField rentalLateFeeField = OwnerUi.textField("0.00");
    TextField rentalAddOnsField = OwnerUi.textField("Tripod, Memory Card");

    // Venue Config
    TextField venueMinHoursField = OwnerUi.textField("2");
    TextField venueMaxHoursField = OwnerUi.textField("24");
    CheckBox venueRequiresApprovalCb;
    TextField venueMinAdvanceDaysField = OwnerUi.textField("1");
    TextField venueAllowedEventTypesField = OwnerUi.textField("Concert, Jam Session, Workshop");
    TextField venueSetupOptionsField = OwnerUi.textField("Theater, Banquets, Standing");
    TextField venueSecurityDepositField = OwnerUi.textField("0.00");
    CheckBox venueSoundSystemCb;
    CheckBox venueLightingCb;
    CheckBox venueStageCb;
    CheckBox venueGreenRoomCb;
    CheckBox venueOutsideVendorCb;
    CheckBox venueCateringCb;
    TextField venueOvertimeRateField = OwnerUi.textField("0.00");

    // Practice Room Config
    TextField practiceMinDurationField = OwnerUi.textField("60");
    TextField practiceMaxDurationField = OwnerUi.textField("240");
    TextField practiceDurationStepField = OwnerUi.textField("30");
    CheckBox practiceEquipmentSelectionCb;
    TextField practiceIncludedEquipmentField = OwnerUi.textField("Drum kit, Amps, PA System");
    TextField practiceOptionalEquipmentField = OwnerUi.textField("Keyboard, Mics");

    // Step 5 Operations State
    final TextField amenitiesField = OwnerUi.textField("e.g. Wi-Fi, Air Conditioning, Soundproofing");
    final TextField facilitiesField = OwnerUi.textField("e.g. Restrooms, Green Room, Parking");
    final TextField rulesField = OwnerUi.textField("e.g. No food near gear, Quiet after 10 PM");
    final TextField cancellationPolicyField = OwnerUi.textField("e.g. Full refund 48h prior to booking");
    final Map<String, DayControls> operatingDayControls = new LinkedHashMap<>();

    private Label errorLabel;
    private Button submitBtn;
    private boolean isSubmitting = false;

    private static class DayControls {
        CheckBox activeCb;
        TextField openField;
        TextField closeField;

        DayControls(String dayName, boolean activeDefault, String openDefault, String closeDefault) {
            this.activeCb = new CheckBox(dayName);
            this.activeCb.setSelected(activeDefault);
            this.openField = OwnerUi.textField(openDefault);
            this.openField.setPrefWidth(90);
            this.closeField = OwnerUi.textField(closeDefault);
            this.closeField.setPrefWidth(90);
        }
    }

    private String preselectedBusinessId;

    public ListNewSpaceWizardPage() {
        this.spaceController = new SpaceController();
        this.businessController = new BusinessController();
        this.businessService = new BusinessService();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));

        initDayControls();
    }

    public ListNewSpaceWizardPage(String preselectedBusinessId) {
        this();
        this.preselectedBusinessId = preselectedBusinessId;
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

    private void initDayControls() {
        operatingDayControls.clear();
        for (String day : ALL_DAYS) {
            boolean active = !WeekDay.SUNDAY.equalsIgnoreCase(day);
            operatingDayControls.put(day, new DayControls(formatDayName(day), active, "09:00", "21:00"));
        }
    }

    public VBox getContent() {
        loadBusinessesAndRenderStep1();
        return container;
    }

    public Scene getScene(Runnable callback) {
        return OwnerUi.pageShell("myspaces", "My Spaces / List New Space (V2)", "List New Space (V2)",
                "Register a V2 physical space under an approved Creativity Business", getContent(), callback);
    }

    Space assembleV2SpaceFromForm() {
        long basePricePaise = parseRupeesToPaise(basePriceRupeesField.getText().trim());
        int cap = 10;
        try {
            cap = Integer.parseInt(capacityField.getText().trim());
        } catch (Exception ignored) {}

        SpaceLocation location = new SpaceLocation(
                addressField.getText().trim(),
                areaField.getText().trim(),
                cityField.getText().trim(),
                stateField.getText().trim(),
                postalCodeField.getText().trim(),
                landmarkField.getText().trim(),
                0.0,
                0.0
        );

        BookingConfig config = assembleBookingConfig(bookingTypeCombo.getValue(), basePricePaise);
        Map<String, OperatingDayConfig> operatingHours = extractOperatingHours();

        String bId = selectedBusiness != null ? selectedBusiness.getBusinessId() : "test-biz-id";
        String oId = selectedBusiness != null ? selectedBusiness.getOwnerId() : "test-owner-id";

        Space v2Space = Space.createV2(
                "spc-test-id",
                bId,
                oId,
                spaceNameField.getText().trim(),
                descriptionField.getText().trim(),
                SpaceCategory.CREATIVITY,
                selectedActivityGroup != null ? selectedActivityGroup : "PODCASTS",
                selectedActivityType != null ? selectedActivityType : "Podcast Studio",
                location,
                "",
                Collections.emptyList(),
                splitCleanList(amenitiesField.getText()),
                splitCleanList(facilitiesField.getText()),
                cap,
                bookingTypeCombo.getValue(),
                basePricePaise,
                pricingUnitCombo.getValue(),
                config,
                operatingHours,
                splitCleanList(rulesField.getText()),
                cancellationPolicyField.getText().trim()
        );

        v2Space.setTimeZoneId(timeZoneField.getText().trim());
        return v2Space;
    }

    void renderStep1ForTest(Business b, String group, String type) {
        this.selectedBusiness = b;
        this.selectedActivityGroup = group;
        this.selectedActivityType = type;
    }

    void renderStep2ForTest() {
        renderStep2SpaceProfileAndLocation();
    }

    void renderStep3ForTest() {
        renderStep3BookingModelAndPricing();
    }

    void renderStep4ForTest() {
        renderStep4BookingConfiguration();
    }

    void renderStep5ForTest() {
        renderStep5Operations();
    }

    // ==================== STEP 1: BUSINESS & CLASSIFICATION ====================

    private void loadBusinessesAndRenderStep1() {
        container.getChildren().clear();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(36, 36);

        VBox loadingBox = new VBox(12, progress, OwnerUi.body("Loading your approved V2 Creativity businesses...", 13, OwnerUi.TEXT_MUTED));
        loadingBox.setAlignment(Pos.CENTER);
        container.getChildren().add(loadingBox);

        businessController.loadCurrentOwnerBusinesses().thenAccept(res -> Platform.runLater(() -> {
            container.getChildren().clear();
            if (res != null && res.isSuccess() && res.getData() != null) {
                List<Business> allBusinesses = res.getData();
                List<Business> eligible = new ArrayList<>();
                String currentUid = AppSession.getUserId();
                for (Business b : allBusinesses) {
                    if (businessService.isEligibleForV2SpaceCreation(b, currentUid)) {
                        eligible.add(b);
                    }
                }

                if (!eligible.isEmpty()) {
                    if (preselectedBusinessId != null && !preselectedBusinessId.isBlank()) {
                        for (Business b : eligible) {
                            if (preselectedBusinessId.equals(b.getBusinessId())) {
                                selectedBusiness = b;
                                break;
                            }
                        }
                    }
                    renderStep1BusinessSelection(eligible);
                } else {
                    renderNoEligibleBusinessAvailable(allBusinesses, null);
                }
            } else {
                renderNoEligibleBusinessAvailable(Collections.emptyList(), res != null ? res.getMessage() : "Unknown error");
            }
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                container.getChildren().clear();
                renderNoEligibleBusinessAvailable(Collections.emptyList(), ex.getMessage());
            });
            return null;
        });
    }

    private void renderNoEligibleBusinessAvailable(List<Business> allBusinesses, String errorMessage) {
        container.getChildren().clear();
        VBox card = OwnerUi.card();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(14);
        card.setPadding(new Insets(24));

        Label header = OwnerUi.heading("No Eligible Business Available for Space Creation", 18);
        
        StringBuilder explanation = new StringBuilder();
        if (errorMessage != null && !errorMessage.isBlank()) {
            explanation.append("Could not load your businesses due to a backend error: ").append(errorMessage);
        } else if (allBusinesses.isEmpty()) {
            explanation.append("You do not have any registered Business profiles. You must create and register a Business before listing a Space.");
        } else {
            String currentUid = AppSession.getUserId();
            explanation.append("You currently have ").append(allBusinesses.size()).append(" business(es), but none are currently eligible for V2 Space creation:\n");
            for (Business b : allBusinesses) {
                explanation.append("• ").append(b.getBusinessName()).append(": Status=").append(b.getVerificationStatus() != null ? b.getVerificationStatus().trim() : "UNKNOWN");
                List<String> failures = businessService.getV2SpaceEligibilityFailures(b, currentUid);
                if (!failures.isEmpty()) {
                    explanation.append(" (").append(String.join("; ", failures)).append(")");
                }
                explanation.append("\n");
            }
            explanation.append("\nNote: Only APPROVED V2 Creativity Businesses with active status can host new V2 Spaces.");
        }

        Label msg = OwnerUi.body(explanation.toString(), 13, OwnerUi.TEXT_MUTED);
        msg.setWrapText(true);

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER);

        Button createBizBtn = OwnerUi.primaryButton("+ Create Business");
        createBizBtn.setOnAction(e -> BusinessFlowRouter.goToCreateBusiness());

        Button viewBizBtn = OwnerUi.secondaryButton("View My Businesses");
        viewBizBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessList());

        Button retryBtn = OwnerUi.secondaryButton("Retry");
        retryBtn.setOnAction(e -> loadBusinessesAndRenderStep1());

        btnBox.getChildren().addAll(createBizBtn, viewBizBtn, retryBtn);

        card.getChildren().addAll(header, msg, btnBox);
        container.getChildren().add(card);
    }

    private void renderStep1BusinessSelection(List<Business> eligibleBusinesses) {
        currentStep = 1;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 1 of 6: Select Business Profile & Classification", 18);
        Label sub = OwnerUi.body("Choose an approved V2 Business and activity type.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(16);

        ComboBox<Business> businessCombo = new ComboBox<>();
        businessCombo.setMaxWidth(Double.MAX_VALUE);
        businessCombo.getItems().addAll(eligibleBusinesses);

        if (selectedBusiness != null && eligibleBusinesses.contains(selectedBusiness)) {
            businessCombo.setValue(selectedBusiness);
        } else {
            businessCombo.setValue(eligibleBusinesses.get(0));
            selectedBusiness = eligibleBusinesses.get(0);
        }

        ComboBox<String> groupCombo = new ComboBox<>();
        groupCombo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        updateActivityGroupsForBusiness(selectedBusiness, groupCombo, typeCombo);

        businessCombo.setOnAction(e -> {
            selectedBusiness = businessCombo.getValue();
            updateActivityGroupsForBusiness(selectedBusiness, groupCombo, typeCombo);
        });

        groupCombo.setOnAction(e -> {
            selectedActivityGroup = groupCombo.getValue();
            updateActivityTypesForGroup(selectedActivityGroup, typeCombo);
        });

        typeCombo.setOnAction(e -> selectedActivityType = typeCombo.getValue());

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button nextBtn = OwnerUi.primaryButton("Continue to Space Profile & Location →");
        nextBtn.setOnAction(e -> {
            if (selectedBusiness == null) {
                errorLabel.setText("Please select a Business.");
                return;
            }
            if (selectedActivityGroup == null || selectedActivityGroup.isBlank()) {
                errorLabel.setText("Please select an Activity Group.");
                return;
            }
            if (selectedActivityType == null || selectedActivityType.isBlank()) {
                errorLabel.setText("Please select an Activity Type.");
                return;
            }
            renderStep2SpaceProfileAndLocation();
        });

        TextField categoryField = OwnerUi.textField(SpaceCategory.CREATIVITY);
        categoryField.setEditable(false);

        HBox actions = new HBox(12, nextBtn);
        formCard.getChildren().addAll(
                title, sub,
                OwnerUi.fieldLabel("Select Business *"), businessCombo,
                OwnerUi.fieldLabel("Category"), categoryField,
                OwnerUi.fieldLabel("Activity Group *"), groupCombo,
                OwnerUi.fieldLabel("Activity Type *"), typeCombo,
                errorLabel, actions
        );

        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(0), formCard);
    }

    private void updateActivityGroupsForBusiness(Business b, ComboBox<String> groupCombo, ComboBox<String> typeCombo) {
        groupCombo.getItems().clear();
        if (b != null && b.getActivityGroups() != null && !b.getActivityGroups().isEmpty()) {
            groupCombo.getItems().addAll(b.getActivityGroups());
        } else {
            groupCombo.getItems().add("CONTENT_CREATION");
        }
        groupCombo.setValue(groupCombo.getItems().get(0));
        selectedActivityGroup = groupCombo.getValue();
        updateActivityTypesForGroup(selectedActivityGroup, typeCombo);
    }

    private void updateActivityTypesForGroup(String group, ComboBox<String> typeCombo) {
        typeCombo.getItems().clear();
        if ("ART".equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Art Studio", "Pottery Studio", "Craft Workshop", "Cooking Studio");
        } else if ("CONTENT_CREATION".equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Creator Studio", "Photography Studio", "Aesthetic Café", "Shooting Location");
        } else if ("PODCASTS".equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Podcast Studio", "Audio Editing Room", "Recording Booth");
        } else if ("MUSIC".equalsIgnoreCase(group)) {
            typeCombo.getItems().addAll("Recording Studio", "Practice Room", "Rehearsal Hall", "Performance Venue");
        } else {
            typeCombo.getItems().add("Creator Studio");
        }
        typeCombo.setValue(typeCombo.getItems().get(0));
        selectedActivityType = typeCombo.getValue();
    }

    // ==================== STEP 2: SPACE PROFILE & LOCATION ====================

    private void renderStep2SpaceProfileAndLocation() {
        currentStep = 2;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 2 of 6: Space Profile & Location", 18);
        Label sub = OwnerUi.body("Provide basic details, area, address, timezone, and guest capacity.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        if (addressField.getText().isBlank() && selectedBusiness != null) {
            addressField.setText(val(selectedBusiness.getAddressLine()));
            cityField.setText(val(selectedBusiness.getCity()));
            stateField.setText(val(selectedBusiness.getState()));
            postalCodeField.setText(val(selectedBusiness.getPostalCode()));
            landmarkField.setText(val(selectedBusiness.getLandmark()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        grid.add(OwnerUi.fieldLabel("Address Line *"), 0, 0);
        grid.add(addressField, 0, 1);
        grid.add(OwnerUi.fieldLabel("Area / Locality *"), 1, 0);
        grid.add(areaField, 1, 1);

        grid.add(OwnerUi.fieldLabel("Landmark"), 0, 2);
        grid.add(landmarkField, 0, 3);
        grid.add(OwnerUi.fieldLabel("City *"), 1, 2);
        grid.add(cityField, 1, 3);

        grid.add(OwnerUi.fieldLabel("State *"), 0, 4);
        grid.add(stateField, 0, 5);
        grid.add(OwnerUi.fieldLabel("Postal Code (6 digits) *"), 1, 4);
        grid.add(postalCodeField, 1, 5);

        grid.add(OwnerUi.fieldLabel("Time Zone *"), 0, 6);
        grid.add(timeZoneField, 0, 7);
        grid.add(OwnerUi.fieldLabel("Guest Capacity *"), 1, 6);
        grid.add(capacityField, 1, 7);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(50);
            grid.getColumnConstraints().add(cc);
        }

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button backBtn = OwnerUi.secondaryButton("← Back");
        backBtn.setOnAction(e -> loadBusinessesAndRenderStep1());

        Button nextBtn = OwnerUi.primaryButton("Continue to Booking Model & Pricing →");
        nextBtn.setOnAction(e -> {
            if (spaceNameField.getText().trim().isBlank()) {
                errorLabel.setText("Space Name is required.");
                return;
            }
            if (descriptionField.getText().trim().isBlank()) {
                errorLabel.setText("Description is required.");
                return;
            }
            if (addressField.getText().trim().isBlank()) {
                errorLabel.setText("Address Line is required.");
                return;
            }
            if (areaField.getText().trim().isBlank()) {
                errorLabel.setText("Area / Locality is required.");
                return;
            }
            if (cityField.getText().trim().isBlank()) {
                errorLabel.setText("City is required.");
                return;
            }
            if (stateField.getText().trim().isBlank()) {
                errorLabel.setText("State is required.");
                return;
            }
            if (!POSTAL_CODE_PATTERN.matcher(postalCodeField.getText().trim()).matches()) {
                errorLabel.setText("Postal code must be exactly 6 digits.");
                return;
            }
            try {
                ZoneId.of(timeZoneField.getText().trim());
            } catch (Exception ex) {
                errorLabel.setText("Invalid Time Zone ID (e.g. Asia/Kolkata).");
                return;
            }
            try {
                int cap = Integer.parseInt(capacityField.getText().trim());
                if (cap <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                errorLabel.setText("Capacity must be a positive integer.");
                return;
            }

            renderStep3BookingModelAndPricing();
        });

        HBox actions = new HBox(12, backBtn, nextBtn);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(
                title, sub,
                OwnerUi.fieldLabel("Space Name *"), spaceNameField,
                OwnerUi.fieldLabel("Description *"), descriptionField,
                grid, errorLabel, actions
        );

        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(1), formCard);
    }

    // ==================== STEP 3: BOOKING MODEL & PRICING ====================

    private void renderStep3BookingModelAndPricing() {
        currentStep = 3;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 3 of 6: Booking Model & Pricing", 18);
        Label sub = OwnerUi.body("Select the booking model and set your base price.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        bookingTypeCombo = new ComboBox<>();
        bookingTypeCombo.setMaxWidth(Double.MAX_VALUE);
        bookingTypeCombo.getItems().addAll(
                BookingType.SEAT,
                BookingType.SLOT,
                BookingType.WORKSHOP,
                BookingType.RENTAL,
                BookingType.VENUE,
                BookingType.PRACTICE_ROOM
        );
        if (bookingTypeCombo.getValue() == null) {
            bookingTypeCombo.setValue(BookingType.SLOT);
        }

        pricingUnitCombo = new ComboBox<>();
        pricingUnitCombo.setMaxWidth(Double.MAX_VALUE);
        updatePricingUnitsForType(bookingTypeCombo.getValue(), pricingUnitCombo);

        bookingTypeCombo.setOnAction(e -> updatePricingUnitsForType(bookingTypeCombo.getValue(), pricingUnitCombo));

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button backBtn = OwnerUi.secondaryButton("← Back");
        backBtn.setOnAction(e -> renderStep2SpaceProfileAndLocation());

        Button nextBtn = OwnerUi.primaryButton("Continue to Booking Configuration →");
        nextBtn.setOnAction(e -> {
            String rupeeStr = basePriceRupeesField.getText().trim();
            if (rupeeStr.isBlank()) {
                errorLabel.setText("Base Price is required.");
                return;
            }
            try {
                BigDecimal bd = new BigDecimal(rupeeStr);
                if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                    errorLabel.setText("Base Price must be greater than 0.");
                    return;
                }
                if (bd.scale() > 2) {
                    errorLabel.setText("Base Price cannot have more than 2 decimal places.");
                    return;
                }
            } catch (Exception ex) {
                errorLabel.setText("Invalid price format. Enter a valid number (e.g. 500.00).");
                return;
            }

            renderStep4BookingConfiguration();
        });

        HBox actions = new HBox(12, backBtn, nextBtn);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(
                title, sub,
                OwnerUi.fieldLabel("Booking Type *"), bookingTypeCombo,
                OwnerUi.fieldLabel("Base Price (₹ Rupees) *"), basePriceRupeesField,
                OwnerUi.fieldLabel("Pricing Unit *"), pricingUnitCombo,
                errorLabel, actions
        );

        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(2), formCard);
    }

    private void updatePricingUnitsForType(String type, ComboBox<String> unitCombo) {
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

    // ==================== STEP 4: BOOKING CONFIGURATION ====================

    private void renderStep4BookingConfiguration() {
        currentStep = 4;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 4 of 6: " + bookingTypeCombo.getValue() + " Booking Configuration", 18);
        Label sub = OwnerUi.body("Configure specific parameters for " + bookingTypeCombo.getValue() + " bookings.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        configContainer = new VBox(12);
        buildConfigFormControls(bookingTypeCombo.getValue(), configContainer);

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button backBtn = OwnerUi.secondaryButton("← Back");
        backBtn.setOnAction(e -> renderStep3BookingModelAndPricing());

        Button nextBtn = OwnerUi.primaryButton("Continue to Operations & Schedule →");
        nextBtn.setOnAction(e -> {
            if (!validateAndExtractConfig(bookingTypeCombo.getValue(), errorLabel)) {
                return;
            }
            renderStep5Operations();
        });

        HBox actions = new HBox(12, backBtn, nextBtn);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(title, sub, configContainer, errorLabel, actions);
        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(3), formCard);
    }

    private void buildConfigFormControls(String type, VBox container) {
        container.getChildren().clear();
        if (BookingType.SEAT.equalsIgnoreCase(type)) {
            seatSelectionModeCombo = new ComboBox<>();
            seatSelectionModeCombo.getItems().addAll("AUTO_ASSIGN", "MANUAL_SELECT");
            seatSelectionModeCombo.setValue("AUTO_ASSIGN");

            seatPriceModeCombo = new ComboBox<>();
            seatPriceModeCombo.getItems().addAll("FLAT_PER_SEAT", "TIERED");
            seatPriceModeCombo.setValue("FLAT_PER_SEAT");

            seatAllowMultipleCb = new CheckBox("Allow multiple seat selection in single booking");
            seatAllowMultipleCb.setSelected(true);

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Selection Mode"), seatSelectionModeCombo,
                    OwnerUi.body("Note: Visual seat map layout arrives in a future update.", 12, OwnerUi.TEXT_MUTED),
                    OwnerUi.fieldLabel("Minimum Duration (minutes) *"), seatMinDurationField,
                    OwnerUi.fieldLabel("Maximum Duration (minutes) *"), seatMaxDurationField,
                    OwnerUi.fieldLabel("Duration Step (minutes) *"), seatDurationStepField,
                    OwnerUi.fieldLabel("Max Guests Per Booking *"), seatMaxGuestsField,
                    OwnerUi.fieldLabel("Seat Price Mode"), seatPriceModeCombo,
                    seatAllowMultipleCb,
                    OwnerUi.fieldLabel("Advance Booking Days *"), seatAdvanceDaysField,
                    OwnerUi.fieldLabel("Minimum Advance Minutes *"), seatMinAdvanceMinField
            );

        } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Duration (minutes) *"), slotMinDurationField,
                    OwnerUi.fieldLabel("Maximum Duration (minutes) *"), slotMaxDurationField,
                    OwnerUi.fieldLabel("Duration Step (minutes) *"), slotDurationStepField,
                    OwnerUi.fieldLabel("Slot Interval (minutes) *"), slotIntervalField,
                    OwnerUi.fieldLabel("Buffer Before Slot (minutes)"), slotBufferBeforeField,
                    OwnerUi.fieldLabel("Buffer After Slot (minutes)"), slotBufferAfterField,
                    OwnerUi.fieldLabel("Max People Allowed *"), slotMaxPeopleField,
                    OwnerUi.fieldLabel("Advance Booking Days *"), slotAdvanceDaysField,
                    OwnerUi.fieldLabel("Minimum Advance Minutes *"), slotMinAdvanceMinField
            );

        } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
            container.getChildren().addAll(
                    OwnerUi.body("Note: Individual workshop sessions & calendar schedules will be configured after creation.", 12, OwnerUi.TEXT_MUTED),
                    OwnerUi.fieldLabel("Minimum Participants Per Booking *"), workshopMinPartField,
                    OwnerUi.fieldLabel("Maximum Participants Per Booking *"), workshopMaxPartField,
                    OwnerUi.fieldLabel("Booking Cutoff Hours *"), workshopBookingCutoffField,
                    OwnerUi.fieldLabel("Cancellation Cutoff Hours *"), workshopCancelCutoffField,
                    OwnerUi.fieldLabel("Age Restriction"), workshopAgeRestrField,
                    OwnerUi.fieldLabel("Materials Included (comma separated)"), workshopMaterialsField,
                    OwnerUi.fieldLabel("Instructor Name"), workshopInstructorField
            );

        } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
            rentalPickupCb = new CheckBox("Pickup Available");
            rentalPickupCb.setSelected(true);
            rentalDeliveryCb = new CheckBox("Delivery Available");
            rentalIdentityProofCb = new CheckBox("Requires Identity Proof");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Available Quantity *"), rentalQuantityField,
                    OwnerUi.fieldLabel("Minimum Duration (minutes) *"), rentalMinDurationField,
                    OwnerUi.fieldLabel("Maximum Duration (minutes) *"), rentalMaxDurationField,
                    OwnerUi.fieldLabel("Security Deposit (₹ Rupees)"), rentalSecurityDepositField,
                    rentalPickupCb, rentalDeliveryCb,
                    OwnerUi.fieldLabel("Delivery Radius (Km)"), rentalDeliveryRadiusField,
                    OwnerUi.fieldLabel("Delivery Charge (₹ Rupees)"), rentalDeliveryChargeField,
                    OwnerUi.fieldLabel("Pickup Instructions"), rentalPickupInstructionsField,
                    rentalIdentityProofCb,
                    OwnerUi.fieldLabel("Late Fee (₹ Rupees per hour)"), rentalLateFeeField,
                    OwnerUi.fieldLabel("Add-Ons (comma separated)"), rentalAddOnsField
            );

        } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
            venueRequiresApprovalCb = new CheckBox("Requires Owner Approval for Bookings");
            venueRequiresApprovalCb.setSelected(true);
            venueSoundSystemCb = new CheckBox("Sound System Available");
            venueLightingCb = new CheckBox("Lighting Available");
            venueStageCb = new CheckBox("Stage Available");
            venueGreenRoomCb = new CheckBox("Green Room Available");
            venueOutsideVendorCb = new CheckBox("Outside Vendors Allowed");
            venueOutsideVendorCb.setSelected(true);
            venueCateringCb = new CheckBox("Catering Allowed");
            venueCateringCb.setSelected(true);

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Booking Hours *"), venueMinHoursField,
                    OwnerUi.fieldLabel("Maximum Booking Hours *"), venueMaxHoursField,
                    venueRequiresApprovalCb,
                    OwnerUi.fieldLabel("Minimum Advance Days *"), venueMinAdvanceDaysField,
                    OwnerUi.fieldLabel("Allowed Event Types (comma separated)"), venueAllowedEventTypesField,
                    OwnerUi.fieldLabel("Setup Options (comma separated)"), venueSetupOptionsField,
                    OwnerUi.fieldLabel("Security Deposit (₹ Rupees)"), venueSecurityDepositField,
                    venueSoundSystemCb, venueLightingCb, venueStageCb, venueGreenRoomCb,
                    venueOutsideVendorCb, venueCateringCb,
                    OwnerUi.fieldLabel("Overtime Rate (₹ Rupees per hour)"), venueOvertimeRateField
            );

        } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
            practiceEquipmentSelectionCb = new CheckBox("Equipment Selection Enabled for Guests");

            container.getChildren().addAll(
                    OwnerUi.fieldLabel("Minimum Duration (minutes) *"), practiceMinDurationField,
                    OwnerUi.fieldLabel("Maximum Duration (minutes) *"), practiceMaxDurationField,
                    OwnerUi.fieldLabel("Duration Step (minutes) *"), practiceDurationStepField,
                    practiceEquipmentSelectionCb,
                    OwnerUi.fieldLabel("Included Equipment (comma separated)"), practiceIncludedEquipmentField,
                    OwnerUi.fieldLabel("Optional Equipment (comma separated)"), practiceOptionalEquipmentField
            );
        }
    }

    private boolean validateAndExtractConfig(String type, Label errorLbl) {
        try {
            if (BookingType.SEAT.equalsIgnoreCase(type)) {
                int minD = Integer.parseInt(seatMinDurationField.getText().trim());
                int maxD = Integer.parseInt(seatMaxDurationField.getText().trim());
                int step = Integer.parseInt(seatDurationStepField.getText().trim());
                int maxG = Integer.parseInt(seatMaxGuestsField.getText().trim());
                int advD = Integer.parseInt(seatAdvanceDaysField.getText().trim());
                int minAdv = Integer.parseInt(seatMinAdvanceMinField.getText().trim());
                if (minD <= 0 || maxD < minD || step <= 0 || maxG <= 0 || advD < 0 || minAdv < 0) {
                    errorLbl.setText("Seat configuration contains invalid values.");
                    return false;
                }

            } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
                int minD = Integer.parseInt(slotMinDurationField.getText().trim());
                int maxD = Integer.parseInt(slotMaxDurationField.getText().trim());
                int step = Integer.parseInt(slotDurationStepField.getText().trim());
                int interval = Integer.parseInt(slotIntervalField.getText().trim());
                int bufB = Integer.parseInt(slotBufferBeforeField.getText().trim());
                int bufA = Integer.parseInt(slotBufferAfterField.getText().trim());
                int maxP = Integer.parseInt(slotMaxPeopleField.getText().trim());
                int advD = Integer.parseInt(slotAdvanceDaysField.getText().trim());
                int minAdv = Integer.parseInt(slotMinAdvanceMinField.getText().trim());
                if (minD <= 0 || maxD < minD || step <= 0 || interval <= 0 || bufB < 0 || bufA < 0 || maxP <= 0 || advD < 0 || minAdv < 0) {
                    errorLbl.setText("Slot configuration contains invalid values.");
                    return false;
                }

            } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
                int minP = Integer.parseInt(workshopMinPartField.getText().trim());
                int maxP = Integer.parseInt(workshopMaxPartField.getText().trim());
                int bookCut = Integer.parseInt(workshopBookingCutoffField.getText().trim());
                int cancelCut = Integer.parseInt(workshopCancelCutoffField.getText().trim());
                if (minP <= 0 || maxP < minP || bookCut < 0 || cancelCut < 0) {
                    errorLbl.setText("Workshop configuration contains invalid values.");
                    return false;
                }

            } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
                int qty = Integer.parseInt(rentalQuantityField.getText().trim());
                int minD = Integer.parseInt(rentalMinDurationField.getText().trim());
                int maxD = Integer.parseInt(rentalMaxDurationField.getText().trim());
                parseRupeesToPaise(rentalSecurityDepositField.getText().trim());
                parseRupeesToPaise(rentalDeliveryChargeField.getText().trim());
                parseRupeesToPaise(rentalLateFeeField.getText().trim());
                double radius = Double.parseDouble(rentalDeliveryRadiusField.getText().trim());
                if (qty <= 0 || minD <= 0 || maxD < minD || radius < 0) {
                    errorLbl.setText("Rental configuration contains invalid values.");
                    return false;
                }
                if (!rentalPickupCb.isSelected() && !rentalDeliveryCb.isSelected()) {
                    errorLbl.setText("Select at least Pickup or Delivery for rental space.");
                    return false;
                }

            } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
                int minH = Integer.parseInt(venueMinHoursField.getText().trim());
                int maxH = Integer.parseInt(venueMaxHoursField.getText().trim());
                int advD = Integer.parseInt(venueMinAdvanceDaysField.getText().trim());
                parseRupeesToPaise(venueSecurityDepositField.getText().trim());
                parseRupeesToPaise(venueOvertimeRateField.getText().trim());
                if (minH <= 0 || maxH < minH || advD < 0) {
                    errorLbl.setText("Venue configuration contains invalid values.");
                    return false;
                }

            } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
                int minD = Integer.parseInt(practiceMinDurationField.getText().trim());
                int maxD = Integer.parseInt(practiceMaxDurationField.getText().trim());
                int step = Integer.parseInt(practiceDurationStepField.getText().trim());
                if (minD <= 0 || maxD < minD || step <= 0) {
                    errorLbl.setText("Practice Room configuration contains invalid values.");
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            errorLbl.setText("Configuration validation error: check numbers & positive values.");
            return false;
        }
    }

    private int parseIntOrDefault(String val, int def) {
        if (val == null || val.isBlank()) return def;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private BookingConfig assembleBookingConfig(String type, long basePricePaise) {
        if (BookingType.SEAT.equalsIgnoreCase(type)) {
            return new SeatBookingConfig(
                    seatSelectionModeCombo != null ? seatSelectionModeCombo.getValue() : "AUTO_ASSIGN",
                    parseIntOrDefault(seatMinDurationField.getText(), 60),
                    parseIntOrDefault(seatMaxDurationField.getText(), 480),
                    parseIntOrDefault(seatDurationStepField.getText(), 60),
                    parseIntOrDefault(seatMaxGuestsField.getText(), 1),
                    seatPriceModeCombo != null ? seatPriceModeCombo.getValue() : "UNIFORM",
                    seatAllowMultipleCb == null || seatAllowMultipleCb.isSelected(),
                    parseIntOrDefault(seatAdvanceDaysField.getText(), 30),
                    parseIntOrDefault(seatMinAdvanceMinField.getText(), 0)
            );
        } else if (BookingType.SLOT.equalsIgnoreCase(type)) {
            return new SlotBookingConfig(
                    parseIntOrDefault(slotMinDurationField.getText(), 60),
                    parseIntOrDefault(slotMaxDurationField.getText(), 240),
                    parseIntOrDefault(slotDurationStepField.getText(), 30),
                    parseIntOrDefault(slotIntervalField.getText(), 30),
                    parseIntOrDefault(slotBufferBeforeField.getText(), 0),
                    parseIntOrDefault(slotBufferAfterField.getText(), 0),
                    parseIntOrDefault(slotMaxPeopleField.getText(), 1),
                    parseIntOrDefault(slotAdvanceDaysField.getText(), 30),
                    parseIntOrDefault(slotMinAdvanceMinField.getText(), 0)
            );
        } else if (BookingType.WORKSHOP.equalsIgnoreCase(type)) {
            return new WorkshopBookingConfig(
                    basePricePaise,
                    parseIntOrDefault(workshopMinPartField.getText(), 1),
                    parseIntOrDefault(workshopMaxPartField.getText(), 20),
                    parseIntOrDefault(workshopBookingCutoffField.getText(), 24),
                    parseIntOrDefault(workshopCancelCutoffField.getText(), 48),
                    workshopAgeRestrField.getText().trim(),
                    splitCleanList(workshopMaterialsField.getText()),
                    workshopInstructorField.getText().trim()
            );
        } else if (BookingType.RENTAL.equalsIgnoreCase(type)) {
            return new RentalBookingConfig(
                    parseIntOrDefault(rentalQuantityField.getText(), 1),
                    parseIntOrDefault(rentalMinDurationField.getText(), 60),
                    parseIntOrDefault(rentalMaxDurationField.getText(), 1440),
                    parseRupeesToPaise(rentalSecurityDepositField.getText().trim()),
                    rentalPickupCb == null || rentalPickupCb.isSelected(),
                    rentalDeliveryCb != null && rentalDeliveryCb.isSelected(),
                    parseDeliveryRadius(rentalDeliveryRadiusField.getText()),
                    parseRupeesToPaise(rentalDeliveryChargeField.getText().trim()),
                    rentalPickupInstructionsField.getText().trim(),
                    rentalIdentityProofCb == null || rentalIdentityProofCb.isSelected(),
                    parseRupeesToPaise(rentalLateFeeField.getText().trim()),
                    splitCleanList(rentalAddOnsField.getText())
            );
        } else if (BookingType.VENUE.equalsIgnoreCase(type)) {
            return new VenueBookingConfig(
                    parseIntOrDefault(venueMinHoursField.getText(), 2),
                    parseIntOrDefault(venueMaxHoursField.getText(), 24),
                    venueRequiresApprovalCb == null || venueRequiresApprovalCb.isSelected(),
                    parseIntOrDefault(venueMinAdvanceDaysField.getText(), 1),
                    splitCleanList(venueAllowedEventTypesField.getText()),
                    splitCleanList(venueSetupOptionsField.getText()),
                    parseRupeesToPaise(venueSecurityDepositField.getText().trim()),
                    venueSoundSystemCb != null && venueSoundSystemCb.isSelected(),
                    venueLightingCb != null && venueLightingCb.isSelected(),
                    venueStageCb != null && venueStageCb.isSelected(),
                    venueGreenRoomCb != null && venueGreenRoomCb.isSelected(),
                    venueOutsideVendorCb == null || venueOutsideVendorCb.isSelected(),
                    venueCateringCb == null || venueCateringCb.isSelected(),
                    parseRupeesToPaise(venueOvertimeRateField.getText().trim())
            );
        } else if (BookingType.PRACTICE_ROOM.equalsIgnoreCase(type)) {
            return new PracticeRoomBookingConfig(
                    parseIntOrDefault(practiceMinDurationField.getText(), 60),
                    parseIntOrDefault(practiceMaxDurationField.getText(), 240),
                    parseIntOrDefault(practiceDurationStepField.getText(), 30),
                    practiceEquipmentSelectionCb != null && practiceEquipmentSelectionCb.isSelected(),
                    splitCleanList(practiceIncludedEquipmentField.getText()),
                    splitCleanList(practiceOptionalEquipmentField.getText())
            );
        }
        return null;
    }

    private double parseDeliveryRadius(String text) {
        if (text == null || text.isBlank()) return 0.0;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ==================== STEP 5: OPERATIONS & SCHEDULE ====================

    private void renderStep5Operations() {
        currentStep = 5;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 5 of 6: Operations & Operating Hours", 18);
        Label sub = OwnerUi.body("Provide amenities, facilities, rules, cancellation policy, and operating schedule.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(14);

        VBox scheduleBox = new VBox(8);
        for (Map.Entry<String, DayControls> entry : operatingDayControls.entrySet()) {
            DayControls ctrl = entry.getValue();
            HBox row = new HBox(12, ctrl.activeCb, OwnerUi.body("Open:", 12, OwnerUi.TEXT_MUTED), ctrl.openField, OwnerUi.body("Close:", 12, OwnerUi.TEXT_MUTED), ctrl.closeField);
            row.setAlignment(Pos.CENTER_LEFT);
            scheduleBox.getChildren().add(row);
        }

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button backBtn = OwnerUi.secondaryButton("← Back");
        backBtn.setOnAction(e -> renderStep4BookingConfiguration());

        Button nextBtn = OwnerUi.primaryButton("Review & Save Draft →");
        nextBtn.setOnAction(e -> {
            if (!validateOperatingHours(errorLabel)) {
                return;
            }
            renderStep6ReviewAndSave();
        });

        HBox actions = new HBox(12, backBtn, nextBtn);
        HBox.setHgrow(nextBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(
                title, sub,
                OwnerUi.fieldLabel("Amenities (comma separated)"), amenitiesField,
                OwnerUi.fieldLabel("Facilities (comma separated)"), facilitiesField,
                OwnerUi.fieldLabel("House Rules (comma separated)"), rulesField,
                OwnerUi.fieldLabel("Cancellation Policy"), cancellationPolicyField,
                OwnerUi.fieldLabel("Operating Hours (24-hour HH:mm) *"), scheduleBox,
                errorLabel, actions
        );

        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(4), formCard);
    }

    private boolean validateOperatingHours(Label errorLbl) {
        boolean anyActive = false;
        for (Map.Entry<String, DayControls> entry : operatingDayControls.entrySet()) {
            DayControls ctrl = entry.getValue();
            if (ctrl.activeCb.isSelected()) {
                anyActive = true;
                String open = ctrl.openField.getText().trim();
                String close = ctrl.closeField.getText().trim();
                if (!TIME_PATTERN.matcher(open).matches() || !TIME_PATTERN.matcher(close).matches()) {
                    errorLbl.setText("Operating hours for " + ctrl.activeCb.getText() + " must be in HH:mm 24-hour format (e.g. 09:00).");
                    return false;
                }
                if (open.compareTo(close) >= 0) {
                    errorLbl.setText("Opening time must be strictly before closing time for " + ctrl.activeCb.getText() + ".");
                    return false;
                }
            }
        }
        if (!anyActive) {
            errorLbl.setText("At least one operating day must be active.");
            return false;
        }
        return true;
    }

    private Map<String, OperatingDayConfig> extractOperatingHours() {
        Map<String, OperatingDayConfig> hours = new LinkedHashMap<>();
        for (Map.Entry<String, DayControls> entry : operatingDayControls.entrySet()) {
            String normDay = WeekDay.normalizeDay(entry.getKey());
            DayControls ctrl = entry.getValue();
            if (normDay != null) {
                hours.put(normDay, new OperatingDayConfig(
                        ctrl.openField.getText().trim(),
                        ctrl.closeField.getText().trim(),
                        ctrl.activeCb.isSelected()
                ));
            }
        }
        return hours;
    }

    // ==================== STEP 6: REVIEW & SAVE DRAFT ====================

    private void renderStep6ReviewAndSave() {
        currentStep = 6;
        container.getChildren().clear();

        Label title = OwnerUi.heading("Step 6 of 6: Review & Save Draft Space", 18);
        Label sub = OwnerUi.body("Review details before creating your V2 DRAFT Space.", 13, OwnerUi.TEXT_MUTED);

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(12);

        String rupeeStr = basePriceRupeesField.getText().trim();
        long basePricePaise = parseRupeesToPaise(rupeeStr);

        VBox reviewSummary = new VBox(8);
        reviewSummary.getChildren().addAll(
                OwnerUi.body("Business: " + (selectedBusiness != null ? selectedBusiness.getBusinessName() : "N/A"), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Space Name: " + spaceNameField.getText().trim(), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Classification: " + SpaceCategory.CREATIVITY + " > " + selectedActivityGroup + " > " + selectedActivityType, 13, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Location: " + addressField.getText().trim() + ", " + areaField.getText().trim() + ", " + cityField.getText().trim() + ", " + stateField.getText().trim() + " " + postalCodeField.getText().trim(), 13, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Time Zone: " + timeZoneField.getText().trim() + " | Capacity: " + capacityField.getText().trim() + " guests", 13, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Booking Model: " + bookingTypeCombo.getValue() + " | Base Price: ₹" + rupeeStr + " / " + pricingUnitCombo.getValue(), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Amenities: " + String.join(", ", splitCleanList(amenitiesField.getText())), 13, OwnerUi.TEXT_MUTED)
        );

        Label notice = OwnerUi.body("Notice: The Space will be saved as a DRAFT and will not be visible to customers until submitted and approved.", 12, OwnerUi.ACCENT);

        errorLabel = OwnerUi.body("", 12, OwnerUi.ERROR);

        Button backBtn = OwnerUi.secondaryButton("← Back");
        backBtn.setOnAction(e -> renderStep5Operations());

        submitBtn = OwnerUi.primaryButton("Save as Draft Space");
        submitBtn.setOnAction(e -> handleSaveDraft());

        HBox actions = new HBox(12, backBtn, submitBtn);
        HBox.setHgrow(submitBtn, Priority.ALWAYS);

        formCard.getChildren().addAll(title, sub, reviewSummary, notice, errorLabel, actions);
        container.getChildren().addAll(OwnerUi.v2WizardProgressCard(5), formCard);
    }

    private void handleSaveDraft() {
        if (isSubmitting) return;
        isSubmitting = true;
        submitBtn.setDisable(true);
        submitBtn.setText("Saving Draft Space...");
        errorLabel.setText("");

        long basePricePaise = parseRupeesToPaise(basePriceRupeesField.getText().trim());
        int cap = Integer.parseInt(capacityField.getText().trim());

        SpaceLocation location = new SpaceLocation(
                addressField.getText().trim(),
                areaField.getText().trim(),
                cityField.getText().trim(),
                stateField.getText().trim(),
                postalCodeField.getText().trim(),
                landmarkField.getText().trim(),
                0.0,
                0.0
        );

        BookingConfig config = assembleBookingConfig(bookingTypeCombo.getValue(), basePricePaise);
        Map<String, OperatingDayConfig> operatingHours = extractOperatingHours();

        Space v2Space = Space.createV2(
                "spc-" + UUID.randomUUID().toString().substring(0, 8),
                selectedBusiness.getBusinessId(),
                selectedBusiness.getOwnerId(),
                spaceNameField.getText().trim(),
                descriptionField.getText().trim(),
                SpaceCategory.CREATIVITY,
                selectedActivityGroup,
                selectedActivityType,
                location,
                "",
                Collections.emptyList(),
                splitCleanList(amenitiesField.getText()),
                splitCleanList(facilitiesField.getText()),
                cap,
                bookingTypeCombo.getValue(),
                basePricePaise,
                pricingUnitCombo.getValue(),
                config,
                operatingHours,
                splitCleanList(rulesField.getText()),
                cancellationPolicyField.getText().trim()
        );

        v2Space.setTimeZoneId(timeZoneField.getText().trim());

        spaceController.createV2Space(selectedBusiness.getBusinessId(), v2Space)
                .thenAccept(res -> Platform.runLater(() -> {
                    isSubmitting = false;
                    if (res.isSuccess()) {
                        SpaceFlowRouter.goToSpaceList();
                    } else {
                        submitBtn.setDisable(false);
                        submitBtn.setText("Save as Draft Space");
                        errorLabel.setText("Failed to save space draft: " + res.getMessage());
                    }
                })).exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isSubmitting = false;
                        submitBtn.setDisable(false);
                        submitBtn.setText("Save as Draft Space");
                        errorLabel.setText("Error creating space draft: " + ex.getMessage());
                    });
                    return null;
                });
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

    private static String val(String str) {
        return str != null ? str : "";
    }
}
