package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.AvailabilityBlockStatus;
import com.eklavya.spotfit.constants.WeekDay;
import com.eklavya.spotfit.controller.owner.AvailabilityController;
import com.eklavya.spotfit.model.AvailabilityBlock;
import com.eklavya.spotfit.model.AvailabilityRule;
import com.eklavya.spotfit.model.AvailableSlot;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manage Availability View displaying weekly schedules, availability blocks, and slot previews for a Resource.
 */
public class ManageAvailabilityPage {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Space space;
    private final Resource resource;
    private final AvailabilityController controller;
    private final VBox container;
    private final Label globalFeedbackLbl;

    private final Map<String, RuleRowControls> ruleControlsMap;
    private final VBox blocksListContainer;
    private final VBox slotsPreviewContainer;
    private Button useSpaceHoursBtn;

    public ManageAvailabilityPage(Space space) {
        this(space, null);
    }

    public ManageAvailabilityPage(Space space, Resource resource) {
        this.space = space;
        this.resource = resource;
        this.controller = new AvailabilityController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
        this.globalFeedbackLbl = OwnerUi.body("", 13, OwnerUi.ERROR);
        this.ruleControlsMap = new LinkedHashMap<>();
        this.blocksListContainer = new VBox(10);
        this.slotsPreviewContainer = new VBox(10);
    }

    public VBox getContent() {
        renderView();
        loadData();
        return container;
    }

    public Scene getScene(Runnable callback) {
        String title = "Manage Availability — " + (resource != null ? resource.getResourceName() : (space != null ? space.getSpaceName() : ""));
        return OwnerUi.pageShell("myspaces", title, title,
                "Configure weekly recurring rules, one-time blocks, and preview slots", getContent(), callback);
    }

    private void renderView() {
        container.getChildren().clear();

        boolean isV2Space = (space != null && resource == null && space.getSchemaVersion() == 2);

        if (space == null || (!isV2Space && resource == null)) {
            container.getChildren().add(OwnerUi.body("Space or Resource selection missing.", 14, OwnerUi.ERROR));
            return;
        }

        if (isV2Space) {
            String bType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "";
            boolean isApplicable = "SLOT".equals(bType) || "VENUE".equals(bType) || "PRACTICE_ROOM".equals(bType);

            if (!isApplicable) {
                VBox card = OwnerUi.card();
                card.setSpacing(12);
                Label header = OwnerUi.heading("Direct Space Availability Not Applicable", 18);
                Label msg = OwnerUi.body("Availability for this booking type will be completed with its dedicated booking structure.", 14, OwnerUi.TEXT_MUTED);
                msg.setWrapText(true);
                Button backBtn = OwnerUi.secondaryButton("← Back to Space Details");
                backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(space));
                card.getChildren().addAll(header, msg, backBtn);
                container.getChildren().add(card);
                return;
            }
        }

        // Header context card
        VBox contextCard = OwnerUi.card();
        contextCard.setSpacing(8);

        HBox topHeaderRow = new HBox(12);
        topHeaderRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = OwnerUi.heading(isV2Space ? "Space Availability Management" : "Availability Management", 20);
        Label badge = OwnerUi.badge(isV2Space ? space.getBookingType() : (resource.getResourceType() != null ? resource.getResourceType() : "ROOM"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = OwnerUi.secondaryButton("← Back to Space Details");
        if (isV2Space) {
            backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(space));
        } else {
            backBtn.setOnAction(e -> ResourceFlowRouter.goToSpaceDetails(space));
        }

        topHeaderRow.getChildren().addAll(titleLbl, badge, spacer, backBtn);

        String tz = space.getTimeZoneId() != null ? space.getTimeZoneId() : Space.DEFAULT_TIMEZONE;
        Label detailsLbl = OwnerUi.body(isV2Space ?
                "Space: " + space.getSpaceName() + "  |  Booking Type: " + space.getBookingType() + "  |  Timezone: " + tz :
                "Space: " + space.getSpaceName() + "  |  Resource: " + resource.getResourceName() + " [" + resource.getResourceCode() + "]  |  Timezone: " + tz,
                13, OwnerUi.TEXT_DARK);

        contextCard.getChildren().addAll(topHeaderRow, detailsLbl, globalFeedbackLbl);

        // TabPane for Schedule, Blocks, Slot Preview
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab scheduleTab = new Tab("Weekly Schedule", buildWeeklyScheduleTab());
        Tab blocksTab = new Tab("Blocked Periods", buildBlockedPeriodsTab());
        Tab previewTab = new Tab("Slot Preview", buildSlotPreviewTab());

        tabPane.getTabs().addAll(scheduleTab, blocksTab, previewTab);

        container.getChildren().addAll(contextCard, tabPane);
    }

    // --- Tab 1: Weekly Schedule ---

    private VBox buildWeeklyScheduleTab() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        HBox topActions = new HBox(12);
        topActions.setAlignment(Pos.CENTER_LEFT);

        Label heading = OwnerUi.heading("Recurring Weekly Operating Hours", 16);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        useSpaceHoursBtn = OwnerUi.secondaryButton("Use Space Operating Hours");
        useSpaceHoursBtn.setOnAction(e -> applySpaceHours());

        topActions.getChildren().addAll(heading, spacer, useSpaceHoursBtn);

        VBox rulesTable = new VBox(10);
        List<String> days = List.of(
                WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY,
                WeekDay.THURSDAY, WeekDay.FRIDAY, WeekDay.SATURDAY, WeekDay.SUNDAY
        );

        List<String> spaceOpDays = space != null ? space.getOperatingDays() : Collections.emptyList();

        for (String day : days) {
            boolean isOpDay = spaceOpDays.contains(day);
            HBox dayRow = buildDayRow(day, isOpDay);
            rulesTable.getChildren().add(dayRow);
        }

        box.getChildren().addAll(topActions, rulesTable);
        return box;
    }

    private HBox buildDayRow(String day, boolean isOpDay) {
        HBox row = new HBox(10);
        row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox activeCb = new CheckBox(day);
        activeCb.setPrefWidth(120);
        activeCb.setSelected(isOpDay);

        String openTime = (space != null && space.getOpeningTime() != null) ? space.getOpeningTime() : "09:00";
        String closeTime = (space != null && space.getClosingTime() != null) ? space.getClosingTime() : "18:00";

        TextField startFld = new TextField(openTime);
        startFld.setPromptText("HH:mm");
        startFld.setPrefWidth(70);

        Label toLbl = OwnerUi.body("to", 12, OwnerUi.TEXT_MUTED);

        TextField endFld = new TextField(closeTime);
        endFld.setPromptText("HH:mm");
        endFld.setPrefWidth(70);

        Label intervalLbl = OwnerUi.body("Interval(m):", 12, OwnerUi.TEXT_MUTED);
        TextField intervalFld = new TextField("60");
        intervalFld.setPrefWidth(55);

        Label minLbl = OwnerUi.body("Min(m):", 12, OwnerUi.TEXT_MUTED);
        TextField minFld = new TextField("60");
        minFld.setPrefWidth(55);

        Label maxLbl = OwnerUi.body("Max(m):", 12, OwnerUi.TEXT_MUTED);
        TextField maxFld = new TextField("240");
        maxFld.setPrefWidth(55);

        Button saveBtn = OwnerUi.primaryButton("Save");
        Label rowFeedback = OwnerUi.body("", 12, OwnerUi.SUCCESS);

        RuleRowControls controls = new RuleRowControls(activeCb, startFld, endFld, intervalFld, minFld, maxFld, saveBtn, rowFeedback);
        ruleControlsMap.put(day, controls);

        updateRowEnabledState(controls, isOpDay);

        activeCb.selectedProperty().addListener((obs, oldV, newV) -> {
            rowFeedback.setText("");
            updateRowEnabledState(controls, isOpDay);
        });

        saveBtn.setOnAction(e -> saveRuleRow(day, controls, isOpDay));

        row.getChildren().addAll(
                activeCb, startFld, toLbl, endFld,
                intervalLbl, intervalFld, minLbl, minFld, maxLbl, maxFld,
                saveBtn, rowFeedback
        );

        return row;
    }

    private void updateRowEnabledState(RuleRowControls controls, boolean isOpDay) {
        boolean active = controls.activeCb.isSelected();
        controls.startFld.setDisable(!active);
        controls.endFld.setDisable(!active);
        controls.intervalFld.setDisable(!active);
        controls.minFld.setDisable(!active);
        controls.maxFld.setDisable(!active);

        if (!isOpDay && !active) {
            controls.saveBtn.setDisable(true);
        } else {
            controls.saveBtn.setDisable(false);
        }
    }

    private void saveRuleRow(String day, RuleRowControls controls, boolean isOpDay) {
        try {
            controls.feedbackLbl.setText("");
            showFeedback("", false);

            boolean active = controls.activeCb.isSelected();
            String startTime = controls.startFld.getText() != null ? controls.startFld.getText().trim() : "";
            String endTime = controls.endFld.getText() != null ? controls.endFld.getText().trim() : "";

            int interval = Integer.parseInt(controls.intervalFld.getText().trim());
            int minDur = Integer.parseInt(controls.minFld.getText().trim());
            int maxDur = Integer.parseInt(controls.maxFld.getText().trim());

            controls.saveBtn.setDisable(true);

            boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
            CompletableFuture<com.eklavya.spotfit.model.OperationResult<AvailabilityRule>> saveFut = isV2 ?
                    controller.saveSpaceWeeklyRule(space.getSpaceId(), day, startTime, endTime, interval, minDur, maxDur, active) :
                    controller.saveWeeklyRule(space.getSpaceId(), resource.getResourceId(), day, startTime, endTime, interval, minDur, maxDur, active);

            saveFut.thenAccept(res -> Platform.runLater(() -> {
                updateRowEnabledState(controls, isOpDay);
                if (res.isSuccess()) {
                    controls.feedbackLbl.setStyle("-fx-text-fill: #16a34a;");
                    controls.feedbackLbl.setText("Saved!");
                } else {
                    controls.feedbackLbl.setStyle("-fx-text-fill: #dc2626;");
                    controls.feedbackLbl.setText(res.getMessage());
                }
            }));
        } catch (NumberFormatException e) {
            controls.feedbackLbl.setStyle("-fx-text-fill: #dc2626;");
            controls.feedbackLbl.setText("Durations must be integers.");
        }
    }

    private void applySpaceHours() {
        showFeedback("Applying Space operating hours...", false);
        if (useSpaceHoursBtn != null) {
            useSpaceHoursBtn.setDisable(true);
        }

        boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
        CompletableFuture<com.eklavya.spotfit.model.OperationResult<List<AvailabilityRule>>> applyFut = isV2 ?
                controller.applyOperatingHoursToSpaceAvailability(space.getSpaceId()) :
                controller.applySpaceOperatingHoursToResource(space.getSpaceId(), resource.getResourceId());

        applyFut.thenAccept(res -> Platform.runLater(() -> {
            if (useSpaceHoursBtn != null) {
                useSpaceHoursBtn.setDisable(false);
            }
            if (res.isSuccess()) {
                showFeedback("Applied space operating hours to schedule.", false);
                populateRules(res.getData());
            } else {
                showFeedback("Failed: " + res.getMessage(), true);
            }
        }));
    }

    // --- Tab 2: Blocked Periods ---

    private VBox buildBlockedPeriodsTab() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        VBox formCard = OwnerUi.card();
        formCard.setSpacing(12);

        boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
        Label formHeading = OwnerUi.heading(isV2 ? "Create One-Time Space Availability Block" : "Create One-Time Availability Block", 16);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        TextField startTimeFld = new TextField("09:00");
        startTimeFld.setPromptText("HH:mm");

        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        TextField endTimeFld = new TextField("18:00");
        endTimeFld.setPromptText("HH:mm");

        TextField reasonFld = new TextField();
        reasonFld.setPromptText("e.g. Maintenance, Private Event, Repairs");

        grid.add(OwnerUi.body("Start Date:", 13, OwnerUi.TEXT_DARK), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(OwnerUi.body("Start Time:", 13, OwnerUi.TEXT_DARK), 2, 0);
        grid.add(startTimeFld, 3, 0);

        grid.add(OwnerUi.body("End Date:", 13, OwnerUi.TEXT_DARK), 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(OwnerUi.body("End Time:", 13, OwnerUi.TEXT_DARK), 2, 1);
        grid.add(endTimeFld, 3, 1);

        grid.add(OwnerUi.body("Reason:", 13, OwnerUi.TEXT_DARK), 0, 2);
        grid.add(reasonFld, 1, 2, 3, 1);

        Button blockBtn = OwnerUi.primaryButton(isV2 ? "Block Space" : "Block Resource");
        Label blockFeedback = OwnerUi.body("", 13, OwnerUi.ERROR);

        blockBtn.setOnAction(e -> {
            blockFeedback.setText("");
            try {
                LocalDate sDate = startDatePicker.getValue();
                LocalDate eDate = endDatePicker.getValue();

                LocalTime sTime = LocalTime.parse(startTimeFld.getText().trim(), TIME_FMT);
                LocalTime eTime = LocalTime.parse(endTimeFld.getText().trim(), TIME_FMT);

                String tz = space.getTimeZoneId() != null ? space.getTimeZoneId() : Space.DEFAULT_TIMEZONE;
                ZoneId zoneId = ZoneId.of(tz);

                long startAt = ZonedDateTime.of(sDate, sTime, zoneId).toInstant().toEpochMilli();
                long endAt = ZonedDateTime.of(eDate, eTime, zoneId).toInstant().toEpochMilli();

                String reason = reasonFld.getText() != null ? reasonFld.getText().trim() : "";

                CompletableFuture<com.eklavya.spotfit.model.OperationResult<AvailabilityBlock>> blockFut = isV2 ?
                        controller.createSpaceAvailabilityBlock(space.getSpaceId(), startAt, endAt, reason) :
                        controller.createAvailabilityBlock(space.getSpaceId(), resource.getResourceId(), startAt, endAt, reason);

                blockFut.thenAccept(res -> Platform.runLater(() -> {
                    if (res.isSuccess()) {
                        reasonFld.clear();
                        loadData();
                    } else {
                        blockFeedback.setText(res.getMessage());
                    }
                }));
            } catch (Exception ex) {
                blockFeedback.setText("Invalid date or time format (HH:mm).");
            }
        });

        formCard.getChildren().addAll(formHeading, grid, blockBtn, blockFeedback);

        VBox listCard = OwnerUi.card();
        Label listHeading = OwnerUi.heading("Existing Availability Blocks", 16);
        listCard.getChildren().addAll(listHeading, blocksListContainer);

        box.getChildren().addAll(formCard, listCard);
        return box;
    }

    // --- Tab 3: Slot Preview ---

    private VBox buildSlotPreviewTab() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));

        HBox filterBox = new HBox(12);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label dateLbl = OwnerUi.body("Select Preview Date:", 13, OwnerUi.TEXT_DARK);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        Button previewBtn = OwnerUi.primaryButton("Preview Slots");

        filterBox.getChildren().addAll(dateLbl, datePicker, previewBtn);

        datePicker.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                computeAndDisplayPreview(newV);
            }
        });

        previewBtn.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                computeAndDisplayPreview(datePicker.getValue());
            }
        });

        VBox previewCard = OwnerUi.card();
        previewCard.getChildren().addAll(OwnerUi.heading("Available Computed Slots", 16), slotsPreviewContainer);

        box.getChildren().addAll(filterBox, previewCard);
        return box;
    }

    private void computeAndDisplayPreview(LocalDate date) {
        slotsPreviewContainer.getChildren().clear();
        slotsPreviewContainer.getChildren().add(OwnerUi.body("Calculating available slots...", 13, OwnerUi.TEXT_MUTED));

        boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
        CompletableFuture<com.eklavya.spotfit.model.OperationResult<List<AvailableSlot>>> previewFut = isV2 ?
                controller.previewSpaceSlots(space.getSpaceId(), date) :
                controller.previewSlots(space.getSpaceId(), resource.getResourceId(), date);

        previewFut.thenAccept(res -> Platform.runLater(() -> {
            slotsPreviewContainer.getChildren().clear();
            if (res.isSuccess()) {
                List<AvailableSlot> slots = res.getData();
                if (slots != null && !slots.isEmpty()) {
                    for (AvailableSlot s : slots) {
                        HBox slotItem = new HBox(12);
                        slotItem.setAlignment(Pos.CENTER_LEFT);
                        slotItem.setStyle("-fx-background-color: #f0fdf4; -fx-padding: 8; -fx-background-radius: 4; -fx-border-color: #bbf7d0; -fx-border-radius: 4;");

                        Label timeLbl = OwnerUi.body("🕒 " + s.getLocalStartTime() + " - " + s.getLocalEndTime(), 13, OwnerUi.TEXT_DARK);
                        Label statusLbl = OwnerUi.body("Available", 12, OwnerUi.SUCCESS);

                        slotItem.getChildren().addAll(timeLbl, statusLbl);
                        slotsPreviewContainer.getChildren().add(slotItem);
                    }
                } else {
                    slotsPreviewContainer.getChildren().add(
                            OwnerUi.body("No available slots for " + date + ". Check weekly schedule and active blocks.", 13, OwnerUi.TEXT_MUTED));
                }
            } else {
                slotsPreviewContainer.getChildren().add(
                        OwnerUi.body("Failed to load slots preview: " + res.getMessage(), 13, OwnerUi.ERROR));
            }
        }));
    }

    // --- Data Loading & UI Population ---

    private void loadData() {
        showFeedback("", false);
        boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
        CompletableFuture<com.eklavya.spotfit.model.OperationResult<Map<String, Object>>> loadFut = isV2 ?
                controller.loadSpaceAvailability(space.getSpaceId()) :
                controller.loadAvailabilityForResource(space.getSpaceId(), resource.getResourceId());

        loadFut.thenAccept(res -> Platform.runLater(() -> {
            if (res.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<AvailabilityRule> rules = (List<AvailabilityRule>) res.getData().get("rules");
                @SuppressWarnings("unchecked")
                List<AvailabilityBlock> blocks = (List<AvailabilityBlock>) res.getData().get("blocks");

                populateRules(rules);
                populateBlocks(blocks);
                computeAndDisplayPreview(LocalDate.now());
            } else {
                showFeedback("Failed to load availability: " + res.getMessage(), true);
            }
        }));
    }

    private void populateRules(List<AvailabilityRule> rules) {
        Map<String, AvailabilityRule> ruleMap = new HashMap<>();
        if (rules != null) {
            for (AvailabilityRule r : rules) {
                if (r != null && r.getDayOfWeek() != null) {
                    String norm = WeekDay.normalizeDay(r.getDayOfWeek());
                    if (norm != null) {
                        ruleMap.put(norm, r);
                    }
                }
            }
        }

        List<String> spaceOpDays = space != null ? space.getOperatingDays() : Collections.emptyList();

        for (Map.Entry<String, RuleRowControls> entry : ruleControlsMap.entrySet()) {
            String day = entry.getKey();
            RuleRowControls controls = entry.getValue();

            controls.feedbackLbl.setText("");

            boolean isOpDay = spaceOpDays.contains(day);
            AvailabilityRule rule = ruleMap.get(day);

            if (rule != null) {
                controls.activeCb.setSelected(rule.isActive());
                if (rule.getStartTime() != null) controls.startFld.setText(rule.getStartTime());
                if (rule.getEndTime() != null) controls.endFld.setText(rule.getEndTime());
                controls.intervalFld.setText(String.valueOf(rule.getSlotIntervalMinutes()));
                controls.minFld.setText(String.valueOf(rule.getMinimumBookingMinutes()));
                controls.maxFld.setText(String.valueOf(rule.getMaximumBookingMinutes()));
            } else {
                controls.activeCb.setSelected(false);
                if (isOpDay) {
                    if (space != null && space.getOpeningTime() != null) controls.startFld.setText(space.getOpeningTime());
                    if (space != null && space.getClosingTime() != null) controls.endFld.setText(space.getClosingTime());
                }
            }

            updateRowEnabledState(controls, isOpDay);
        }
    }

    private void populateBlocks(List<AvailabilityBlock> blocks) {
        blocksListContainer.getChildren().clear();

        if (blocks == null || blocks.isEmpty()) {
            blocksListContainer.getChildren().add(OwnerUi.body("No availability blocks configured.", 13, OwnerUi.TEXT_MUTED));
            return;
        }

        String tz = space.getTimeZoneId() != null ? space.getTimeZoneId() : Space.DEFAULT_TIMEZONE;
        ZoneId zoneId = ZoneId.of(tz);

        for (AvailabilityBlock b : blocks) {
            HBox card = new HBox(12);
            card.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            card.setAlignment(Pos.CENTER_LEFT);

            String sStr = ZonedDateTime.ofInstant(Instant.ofEpochMilli(b.getStartAt()), zoneId).format(DATE_FMT);
            String eStr = ZonedDateTime.ofInstant(Instant.ofEpochMilli(b.getEndAt()), zoneId).format(DATE_FMT);

            VBox infoBox = new VBox(4);
            Label rangeLbl = OwnerUi.heading("🚫 " + sStr + "  to  " + eStr, 14);
            Label reasonLbl = OwnerUi.body("Reason: " + (b.getReason() != null ? b.getReason() : "N/A"), 12, OwnerUi.TEXT_MUTED);

            infoBox.getChildren().addAll(rangeLbl, reasonLbl);

            Label statusBadge = OwnerUi.badge(b.getStatus() != null ? b.getStatus() : "ACTIVE");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            card.getChildren().addAll(infoBox, statusBadge, spacer);

            if (AvailabilityBlockStatus.ACTIVE.equalsIgnoreCase(b.getStatus())) {
                Button cancelBtn = OwnerUi.secondaryButton("Cancel Block");
                cancelBtn.setOnAction(e -> {
                    boolean isV2 = (space != null && resource == null && space.getSchemaVersion() == 2);
                    CompletableFuture<com.eklavya.spotfit.model.OperationResult<AvailabilityBlock>> cancelFut = isV2 ?
                            controller.cancelSpaceAvailabilityBlock(space.getSpaceId(), b.getBlockId()) :
                            controller.cancelAvailabilityBlock(space.getSpaceId(), resource.getResourceId(), b.getBlockId());

                    cancelFut.thenAccept(cRes -> Platform.runLater(this::loadData));
                });
                card.getChildren().add(cancelBtn);
            }

            blocksListContainer.getChildren().add(card);
        }
    }

    private void showFeedback(String msg, boolean isError) {
        globalFeedbackLbl.setText(msg);
        globalFeedbackLbl.setStyle(isError ? "-fx-text-fill: #dc2626;" : "-fx-text-fill: #16a34a;");
    }

    private static class RuleRowControls {
        final CheckBox activeCb;
        final TextField startFld;
        final TextField endFld;
        final TextField intervalFld;
        final TextField minFld;
        final TextField maxFld;
        final Button saveBtn;
        final Label feedbackLbl;

        RuleRowControls(CheckBox activeCb, TextField startFld, TextField endFld, TextField intervalFld,
                        TextField minFld, TextField maxFld, Button saveBtn, Label feedbackLbl) {
            this.activeCb = activeCb;
            this.startFld = startFld;
            this.endFld = endFld;
            this.intervalFld = intervalFld;
            this.minFld = minFld;
            this.maxFld = maxFld;
            this.saveBtn = saveBtn;
            this.feedbackLbl = feedbackLbl;
        }
    }
}
