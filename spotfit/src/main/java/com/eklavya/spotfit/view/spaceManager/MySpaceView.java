package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.constants.ManagerPermission;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.constants.SpaceApprovalStatus;
import com.eklavya.spotfit.constants.SpaceOperationalStatus;
import com.eklavya.spotfit.controller.manager.ManagerSpaceOperationsController;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.OperationResult;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.session.AppSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class MySpaceView {

    private BorderPane rightPane;
    private VBox tabContentContainer;
    private final List<Button> tabButtons = new ArrayList<>();
    private Space targetSpace;
    private Manager cachedManager;
    private final ManagerSpaceOperationsController opsController = new ManagerSpaceOperationsController();

    public MySpaceView() {
        initManagerProfile();
    }

    public MySpaceView(Space targetSpace) {
        this.targetSpace = targetSpace;
        initManagerProfile();
    }

    private void initManagerProfile() {
        if (AppSession.isAuthenticated() && AppSession.isManager()) {
            new com.eklavya.spotfit.service.ManagerService()
                    .getManagerProfile(AppSession.getUserId(), AppSession.getIdToken())
                    .thenAccept(res -> {
                        if (res.isSuccess()) {
                            this.cachedManager = res.getData();
                        }
                    });
        }
    }

    private boolean hasPermission(String perm) {
        if (cachedManager != null && cachedManager.getPermissions() != null) {
            return cachedManager.getPermissions().contains(perm);
        }
        return true;
    }

    public VBox getContent(BorderPane rightPane) {
        this.rightPane = rightPane;

        String spaceName = (targetSpace != null && targetSpace.getSpaceName() != null) ? targetSpace.getSpaceName()
                : (targetSpace != null && targetSpace.getName() != null) ? targetSpace.getName() : "Pixel Photography Studio";
        String locationText = (targetSpace != null && targetSpace.getCategory() != null) ? "📍 " + spaceName + " · " + targetSpace.getCategory()
                : "📍 Pixel Photography Studio · Ahmedabad, Gujarat";

        Label title = heading(spaceName + " Management", 30);
        Label subtitle = muted("Manage operational details, availability, media, facilities, and team for your assigned Space.");
        Label locationChip = outlineChip(locationText);

        HBox header = new HBox(new VBox(4, title, subtitle), hgrow(), locationChip);
        header.setAlignment(Pos.CENTER_LEFT);

        boolean isV2 = (targetSpace != null && targetSpace.getSchemaVersion() == 2);

        HBox tabsRow;
        if (isV2) {
            tabsRow = new HBox(8,
                    createTabButton("V2 Operational Control", 0),
                    createTabButton("Space Information", 1),
                    createTabButton("Photos & Videos", 2),
                    createTabButton("Amenities", 3),
                    createTabButton("Facilities", 4),
                    createTabButton("Team", 5)
            );
        } else {
            tabsRow = new HBox(8,
                    createTabButton("Space Information", 0),
                    createTabButton("Photos & Videos", 1),
                    createTabButton("Amenities", 2),
                    createTabButton("Facilities", 3),
                    createTabButton("Resources (V1)", 4),
                    createTabButton("Offerings (V1)", 5),
                    createTabButton("Team", 6)
            );
        }
        tabsRow.setStyle("-fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1px;");

        tabContentContainer = new VBox(20);

        selectTab(0, isV2);

        VBox content = new VBox(20, header, tabsRow, tabContentContainer);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private Button createTabButton(String label, int index) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 16; -fx-cursor:hand;");
        boolean isV2 = (targetSpace != null && targetSpace.getSchemaVersion() == 2);
        btn.setOnAction(e -> selectTab(index, isV2));
        tabButtons.add(btn);
        return btn;
    }

    private void selectTab(int index, boolean isV2) {
        for (int i = 0; i < tabButtons.size(); i++) {
            Button btn = tabButtons.get(i);
            if (i == index) {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + PRIMARY + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-border-color:transparent transparent " + PRIMARY + " transparent; -fx-border-width:0 0 3px 0; -fx-padding:10 16; -fx-cursor:hand;");
            } else {
                btn.setStyle("-fx-background-color:transparent; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-font-size:14px; -fx-font-weight:bold; -fx-padding:10 16; -fx-cursor:hand;");
            }
        }

        tabContentContainer.getChildren().clear();
        if (isV2) {
            switch (index) {
                case 0 -> tabContentContainer.getChildren().add(createV2OperationalControlTab());
                case 1 -> tabContentContainer.getChildren().add(createSpaceInfoTab());
                case 2 -> tabContentContainer.getChildren().add(createPhotosVideosTab());
                case 3 -> tabContentContainer.getChildren().add(createAmenitiesTab());
                case 4 -> tabContentContainer.getChildren().add(createFacilitiesTab());
                case 5 -> tabContentContainer.getChildren().add(createTeamTab());
            }
        } else {
            switch (index) {
                case 0 -> tabContentContainer.getChildren().add(createSpaceInfoTab());
                case 1 -> tabContentContainer.getChildren().add(createPhotosVideosTab());
                case 2 -> tabContentContainer.getChildren().add(createAmenitiesTab());
                case 3 -> tabContentContainer.getChildren().add(createFacilitiesTab());
                case 4 -> tabContentContainer.getChildren().add(createResourcesTab());
                case 5 -> tabContentContainer.getChildren().add(createOfferingsTab());
                case 6 -> tabContentContainer.getChildren().add(createTeamTab());
            }
        }
    }

    // =========================================================
    // V2 OPERATIONAL CONTROL TAB
    // =========================================================
    private VBox createV2OperationalControlTab() {
        Label title = heading("V2 Space Operational Control & Management", 20);
        Label subtitle = muted("Execute Manager operational tasks for assigned V2 Space within security boundaries.");

        VBox cards = new VBox(20, title, subtitle);

        // 1. Operational Status Control
        cards.getChildren().add(createOperationalStatusCard());

        // 2. Booking Type Specific Operational Card
        String bType = (targetSpace != null && targetSpace.getBookingType() != null) ? targetSpace.getBookingType().toUpperCase() : "SLOT";
        if ("SEAT".equals(bType)) {
            cards.getChildren().add(createSeatControlCard());
        } else if ("WORKSHOP".equals(bType)) {
            cards.getChildren().add(createWorkshopControlCard());
        } else { // SLOT, VENUE, PRACTICE_ROOM
            cards.getChildren().add(createAvailabilityControlCard());
        }

        return cards;
    }

    private VBox createOperationalStatusCard() {
        Label secTitle = heading("1. Operational Status Control", 18);

        String currentOpStatus = (targetSpace != null && targetSpace.getOperationalStatus() != null)
                ? targetSpace.getOperationalStatus().toUpperCase() : "INACTIVE";
        String approvalStatus = (targetSpace != null && targetSpace.getApprovalStatus() != null)
                ? targetSpace.getApprovalStatus().toUpperCase() : "DRAFT";
        boolean isApproved = "APPROVED".equalsIgnoreCase(approvalStatus);

        Label currentStatusLabel = chip("Current Operational Status: " + currentOpStatus,
                "ACTIVE".equals(currentOpStatus) ? GREEN_BG : RED_BG,
                "ACTIVE".equals(currentOpStatus) ? GREEN_TEXT : RED_TEXT);

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        VBox contentBox = new VBox(12, new HBox(10, currentStatusLabel, chip("Approval: " + approvalStatus, CHIP_BG, TEXT)));

        if (!isApproved) {
            Label warn = new Label("🔒 Operational status alteration is prohibited for unapproved Spaces (Current status: " + approvalStatus + "). Owner/Developer approval required.");
            warn.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px; -fx-font-weight:bold;");
            contentBox.getChildren().add(warn);
        } else if (!hasPermission(ManagerPermission.MANAGE_SPACE_STATUS)) {
            Label warn = new Label("🔒 Permission 'MANAGE_SPACE_STATUS' required to alter operational status.");
            warn.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px; -fx-font-weight:bold;");
            contentBox.getChildren().add(warn);
        } else {
            HBox statusBtns = new HBox(10);
            for (String targetStatus : List.of(SpaceOperationalStatus.ACTIVE, SpaceOperationalStatus.INACTIVE, SpaceOperationalStatus.MAINTENANCE, SpaceOperationalStatus.CLOSED)) {
                Button btn = secondaryButton(targetStatus);
                if (targetStatus.equalsIgnoreCase(currentOpStatus)) {
                    btn.setStyle(btn.getStyle() + " -fx-border-color:" + PRIMARY + "; -fx-border-width:2px;");
                }
                btn.setOnAction(e -> {
                    if (targetSpace == null) return;
                    msgLabel.setText("Updating operational status to " + targetStatus + "...");
                    msgLabel.setStyle("-fx-text-fill:" + PRIMARY + ";");

                    opsController.setV2OperationalStatus(targetSpace.getSpaceId(), targetStatus)
                            .thenAccept(res -> Platform.runLater(() -> {
                                if (res.isFailure()) {
                                    msgLabel.setText("Failed: " + res.getMessage() + " (" + res.getErrorCode() + ")");
                                    msgLabel.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                                } else {
                                    targetSpace = res.getData();
                                    msgLabel.setText("SUCCESS! Operational status set to " + targetStatus);
                                    msgLabel.setStyle("-fx-text-fill:" + GREEN_TEXT + ";");
                                }
                            }));
                });
                statusBtns.getChildren().add(btn);
            }
            contentBox.getChildren().addAll(new VBox(6, muted("Set Operational State:"), statusBtns), msgLabel);
        }

        return card(secTitle, contentBox);
    }

    private VBox createAvailabilityControlCard() {
        Label secTitle = heading("2. Availability & Block Control (SLOT/VENUE/PRACTICE_ROOM)", 18);
        VBox contentBox = new VBox(12);

        if (!hasPermission(ManagerPermission.MANAGE_AVAILABILITY)) {
            Label warn = new Label("🔒 Permission 'MANAGE_AVAILABILITY' required to set weekly rules or block slots.");
            warn.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px; -fx-font-weight:bold;");
            contentBox.getChildren().add(warn);
            return card(secTitle, contentBox);
        }

        Label opHoursNote = chip("ℹ Operating Hours Read-Only", CHIP_BG, SECONDARY_TEXT);
        Label desc = muted("Configure weekly availability rules and manage slot blocks for assigned space.");

        TextField tfDay = searchField("Day of Week (e.g. MONDAY)");
        tfDay.setText("MONDAY");
        TextField tfStart = searchField("Start Time (e.g. 09:00)");
        tfStart.setText("09:00");
        TextField tfEnd = searchField("End Time (e.g. 17:00)");
        tfEnd.setText("17:00");

        Label ruleMsg = new Label();
        ruleMsg.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        Button saveRuleBtn = primaryButton("Save Weekly Availability Rule");
        saveRuleBtn.setOnAction(e -> {
            if (targetSpace == null) return;
            ruleMsg.setText("Saving availability rule...");
            ruleMsg.setStyle("-fx-text-fill:" + PRIMARY + ";");

            opsController.saveWeeklyRule(targetSpace.getSpaceId(), tfDay.getText().trim(), tfStart.getText().trim(), tfEnd.getText().trim(), 60, 60, 240, true)
                    .thenAccept(res -> Platform.runLater(() -> {
                        if (res.isFailure()) {
                            ruleMsg.setText("Failed: " + res.getMessage() + " (" + res.getErrorCode() + ")");
                            ruleMsg.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                        } else {
                            ruleMsg.setText("SUCCESS! Availability rule saved for " + tfDay.getText());
                            ruleMsg.setStyle("-fx-text-fill:" + GREEN_TEXT + ";");
                        }
                    }));
        });

        HBox ruleForm = new HBox(10, tfDay, tfStart, tfEnd, saveRuleBtn);
        ruleForm.setAlignment(Pos.CENTER_LEFT);

        contentBox.getChildren().addAll(opHoursNote, desc, ruleForm, ruleMsg);
        return card(secTitle, contentBox);
    }

    private VBox createSeatControlCard() {
        Label secTitle = heading("2. Seat Operational Status Management (SEAT Space)", 18);
        VBox contentBox = new VBox(12);

        if (!hasPermission(ManagerPermission.MANAGE_SEATS)) {
            Label warn = new Label("🔒 Permission 'MANAGE_SEATS' required to update seat status.");
            warn.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px; -fx-font-weight:bold;");
            contentBox.getChildren().add(warn);
            return card(secTitle, contentBox);
        }

        Label notice = muted("Manager can update seat operational status (AVAILABLE, BLOCKED, MAINTENANCE). Seat layout, code, capacity, and pricing are locked.");
        TextField tfSeatId = searchField("Seat ID (e.g. seat-1)");
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll(SeatStatus.AVAILABLE, SeatStatus.BLOCKED, SeatStatus.MAINTENANCE);
        cbStatus.setValue(SeatStatus.AVAILABLE);

        Label seatMsg = new Label();
        seatMsg.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        Button updateSeatBtn = primaryButton("Update Seat Operational Status");
        updateSeatBtn.setOnAction(e -> {
            if (targetSpace == null) return;
            String seatId = tfSeatId.getText().trim();
            if (seatId.isEmpty()) {
                seatMsg.setText("Please enter a valid Seat ID.");
                seatMsg.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            seatMsg.setText("Updating seat status...");
            seatMsg.setStyle("-fx-text-fill:" + PRIMARY + ";");

            opsController.updateSeatStatus(targetSpace.getSpaceId(), seatId, cbStatus.getValue())
                    .thenAccept(res -> Platform.runLater(() -> {
                        if (res.isFailure()) {
                            seatMsg.setText("Failed: " + res.getMessage() + " (" + res.getErrorCode() + ")");
                            seatMsg.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                        } else {
                            seatMsg.setText("SUCCESS! Seat " + seatId + " status updated to " + cbStatus.getValue());
                            seatMsg.setStyle("-fx-text-fill:" + GREEN_TEXT + ";");
                        }
                    }));
        });

        HBox seatForm = new HBox(10, tfSeatId, cbStatus, updateSeatBtn);
        seatForm.setAlignment(Pos.CENTER_LEFT);

        contentBox.getChildren().addAll(notice, seatForm, seatMsg);
        return card(secTitle, contentBox);
    }

    private VBox createWorkshopControlCard() {
        Label secTitle = heading("2. Workshop Session Control (WORKSHOP Space)", 18);
        VBox contentBox = new VBox(12);

        if (!hasPermission(ManagerPermission.MANAGE_SESSIONS)) {
            Label warn = new Label("🔒 Permission 'MANAGE_SESSIONS' required to cancel workshop sessions.");
            warn.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px; -fx-font-weight:bold;");
            contentBox.getChildren().add(warn);
            return card(secTitle, contentBox);
        }

        Label notice = muted("Manager can view scheduled workshop sessions and cancel sessions. Pricing and booked counts remain read-only.");
        TextField tfSessId = searchField("Session ID (e.g. sess-101)");

        Label sessMsg = new Label();
        sessMsg.setStyle("-fx-font-size:13px; -fx-font-weight:bold;");

        Button cancelSessBtn = secondaryButton("✕ Cancel Workshop Session");
        cancelSessBtn.setStyle(cancelSessBtn.getStyle() + " -fx-text-fill:" + RED_TEXT + ";");
        cancelSessBtn.setOnAction(e -> {
            if (targetSpace == null) return;
            String sessId = tfSessId.getText().trim();
            if (sessId.isEmpty()) {
                sessMsg.setText("Please enter a valid Session ID.");
                sessMsg.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                return;
            }
            sessMsg.setText("Cancelling workshop session...");
            sessMsg.setStyle("-fx-text-fill:" + PRIMARY + ";");

            opsController.cancelWorkshopSession(targetSpace.getSpaceId(), sessId)
                    .thenAccept(res -> Platform.runLater(() -> {
                        if (res.isFailure()) {
                            sessMsg.setText("Failed: " + res.getMessage() + " (" + res.getErrorCode() + ")");
                            sessMsg.setStyle("-fx-text-fill:" + RED_TEXT + ";");
                        } else {
                            sessMsg.setText("SUCCESS! Session " + sessId + " cancelled.");
                            sessMsg.setStyle("-fx-text-fill:" + GREEN_TEXT + ";");
                        }
                    }));
        });

        HBox sessForm = new HBox(10, tfSessId, cancelSessBtn);
        sessForm.setAlignment(Pos.CENTER_LEFT);

        contentBox.getChildren().addAll(notice, sessForm, sessMsg);
        return card(secTitle, contentBox);
    }

    // =========================================================
    // 1. SPACE INFORMATION TAB
    // =========================================================
    private VBox createSpaceInfoTab() {
        Label sec1Title = heading("Operational Space Details (Editable)", 18);

        TextField tfShortDesc = searchField("A premier photography & podcast production studio in Ahmedabad");
        tfShortDesc.setText("A premier photography & podcast production studio equipped with high-grade lighting and soundproofing.");

        TextArea taFullDesc = new TextArea("Pixel Photography Studio offers full-service production suites for photography, content creation, and audio/video podcast recording. Located centrally in SG Highway, Ahmedabad.");
        taFullDesc.setWrapText(true);
        taFullDesc.setPrefRowCount(3);
        taFullDesc.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px; -fx-background-radius:12px;");

        TextArea taRules = new TextArea("1. No smoking inside studios.\n2. Handle lighting equipment with care.\n3. Wrap sessions on time.");
        taRules.setWrapText(true);
        taRules.setPrefRowCount(3);
        taRules.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px; -fx-background-radius:12px;");

        TextField tfHours = searchField("Opening & Closing Hours");
        tfHours.setText("Mon - Sun: 09:00 AM - 09:00 PM");

        Button btnSaveInfo = primaryButton("Save Space Information");
        btnSaveInfo.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Updated Space Info", "Saved updated description, rules & operational details", "SPACE");
            showModal("Information Saved", new VBox(16, heading("✔ Space details updated successfully", 16), muted("Changes reflect on customer-facing SpotFit listing.")));
        });

        VBox editableCard = card(sec1Title,
                new VBox(6, muted("SHORT DESCRIPTION"), tfShortDesc),
                new VBox(6, muted("FULL DESCRIPTION"), taFullDesc),
                new VBox(6, muted("SPACE RULES & INSTRUCTIONS"), taRules),
                new VBox(6, muted("OPERATIONAL HOURS"), tfHours),
                btnSaveInfo
        );

        Label sec2Title = heading("Owner-Controlled Fields (Read-Only)", 18);
        Label lockNotice = chip("🔒 Protected by Space Owner", CHIP_BG, PRIMARY);

        VBox lockedGrid = new VBox(12,
                readOnlyField("OFFICIAL SPACE NAME", "Pixel Photography Studio (Owner controlled)"),
                readOnlyField("BUSINESS OWNER", "Vikram Shah Enterprises Ltd."),
                readOnlyField("LEGAL REGISTRATION / GST", "24AAAAA0000A1Z5"),
                readOnlyField("VERIFICATION DOCUMENTS", "Verified & Approved on SpotFit"),
                readOnlyField("SETTLEMENT BANK ACCOUNT", "HDFC Bank ··· 8821")
        );

        VBox lockedCard = card(new HBox(sec2Title, hgrow(), lockNotice), lockedGrid);

        return new VBox(20, editableCard, lockedCard);
    }

    private VBox readOnlyField(String label, String val) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label v = new Label(val);
        v.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + "; -fx-background-color:" + BACKGROUND + "; -fx-padding:8 12; -fx-background-radius:8px;");
        return new VBox(4, l, v);
    }

    // =========================================================
    // 2. PHOTOS & VIDEOS TAB
    // =========================================================
    private VBox createPhotosVideosTab() {
        Label title = heading("Space Gallery & Media", 18);
        Button uploadBtn = primaryButton("+ Upload New Photo/Video");

        uploadBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Uploaded Photo", "Added new main hall studio photograph", "SPACE");
            showModal("Upload Media", new VBox(16, heading("Upload Photo or Video", 16), muted("Selected file: studio_main_hall.jpg"), primaryButton("Confirm Upload")));
        });

        HBox header = new HBox(title, hgrow(), uploadBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox mediaGrid = new HBox(16,
                mediaCard("Main Studio View", "COVER IMAGE", "📷 Studio_Front.jpg"),
                mediaCard("Podcast Booth A", "GALLERY", "📷 Podcast_A.jpg"),
                mediaCard("Editing Suite", "GALLERY", "📷 Edit_Suite.jpg"),
                mediaCard("Space Tour", "VIDEO", "🎥 Space_Tour.mp4")
        );
        mediaGrid.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));

        return card(header, mediaGrid);
    }

    private VBox mediaCard(String name, String tag, String file) {
        Label icon = new Label("🖼");
        icon.setStyle("-fx-font-size:32px;");
        VBox imgBox = new VBox(icon);
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setPrefHeight(120);
        imgBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px;");

        Label tagChip = chip(tag, PRIMARY, "white");
        Button deleteBtn = secondaryButton("Delete");
        deleteBtn.setOnAction(e -> AuditLogManager.addLog("Rahul Sharma", "Deleted Media", "Removed media " + file, "SPACE"));

        HBox actions = new HBox(10, tagChip, hgrow(), deleteBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        return new VBox(10, imgBox, heading(name, 15), muted(file), actions);
    }

    // =========================================================
    // 3. AMENITIES TAB
    // =========================================================
    private VBox createAmenitiesTab() {
        Label title = heading("Space Amenities", 18);
        Label subtitle = muted("Enable or disable customer amenities available at Pixel Photography Studio.");

        VBox grid = new VBox(12,
                amenityRow("📶 High-Speed Wi-Fi", "500 Mbps Fiber connection", true),
                amenityRow("❄ Air Conditioning", "Climate controlled units in all rooms", true),
                amenityRow("🅿 Dedicated Parking", "5 car spots available", true),
                amenityRow("⚡ Power Backup", "UPS & Generator backup available", true),
                amenityRow("🚻 Washroom Facilities", "Clean private rest rooms", true),
                amenityRow("🔌 Charging Stations", "Multi-pin surge protected strips", true),
                amenityRow("☕ Drinking Water & Coffee", "Filtered water & espresso machine", true),
                amenityRow("♿ Wheelchair Access", "Ramp and wide door entry", false)
        );

        return card(title, subtitle, grid);
    }

    private HBox amenityRow(String name, String desc, boolean active) {
        Label n = heading(name, 15);
        Label d = muted(desc);
        VBox text = new VBox(2, n, d);

        Label toggle = chip(active ? "✓ ENABLED" : "✕ DISABLED", active ? GREEN_BG : RED_BG, active ? GREEN_TEXT : RED_TEXT);
        toggle.setStyle(toggle.getStyle() + " -fx-cursor:hand;");
        toggle.setOnMouseClicked(e -> AuditLogManager.addLog("Rahul Sharma", "Toggled Amenity", name + " toggled", "SPACE"));

        HBox row = new HBox(text, hgrow(), toggle);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    // =========================================================
    // 4. FACILITIES TAB
    // =========================================================
    private VBox createFacilitiesTab() {
        Label title = heading("Operational Facilities", 18);
        Label subtitle = muted("Manage room layouts and space facility availability.");

        VBox list = new VBox(12,
                facilityCard("Reception & Lounge", "Ground Floor", "Capacity: 10 people", "OPERATIONAL"),
                facilityCard("Studio A - Photo Floor", "Main Floor", "Capacity: 8 people", "OPERATIONAL"),
                facilityCard("Studio B - Podcast Room", "Soundproofed Room 2", "Capacity: 4 people", "OPERATIONAL"),
                facilityCard("Editing & Color Grading Suite", "Room 3", "Capacity: 2 people", "UNDER MAINTENANCE")
        );

        return card(title, subtitle, list);
    }

    private HBox facilityCard(String name, String loc, String cap, String status) {
        boolean op = status.equals("OPERATIONAL");
        Label st = chip(status, op ? GREEN_BG : RED_BG, op ? GREEN_TEXT : RED_TEXT);

        VBox textBox = new VBox(4, heading(name, 16), muted(loc + " · " + cap));

        Button updateBtn = secondaryButton("Update Facility");
        updateBtn.setOnAction(e -> showModal("Update Facility", new VBox(16, heading("Facility Status", 16), muted("Updating status for " + name), primaryButton("Save Status"))));

        HBox row = new HBox(textBox, hgrow(), st, updateBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    // =========================================================
    // 5. RESOURCES TAB
    // =========================================================
    private VBox createResourcesTab() {
        Label title = heading("Bookable Resources & Asset Management", 18);
        Button addResourceBtn = primaryButton("+ Add Resource");

        addResourceBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Added Resource", "Created new resource kit", "RESOURCE");
            showModal("Add Resource", new VBox(16, heading("New Resource Asset", 16), searchField("Resource Name (e.g. Sony A7IV Camera)"), primaryButton("Save Resource")));
        });

        HBox header = new HBox(title, hgrow(), addResourceBtn);

        VBox resourcesList = new VBox(12,
                resourceRow("Studio A (Photo Floor)", "Physical Space", "AVAILABLE"),
                resourceRow("Studio B (Podcast Room)", "Physical Space", "AVAILABLE"),
                resourceRow("4K Video Camera Kit", "Equipment", "AVAILABLE"),
                resourceRow("Godox Studio Lighting Set", "Equipment", "MAINTENANCE"),
                resourceRow("Podcast Mic & Mixer Set", "Equipment", "AVAILABLE")
        );

        return card(header, resourcesList);
    }

    private HBox resourceRow(String name, String type, String status) {
        boolean avail = status.equals("AVAILABLE");
        Label stChip = chip(status, avail ? GREEN_BG : RED_BG, avail ? GREEN_TEXT : RED_TEXT);

        Button toggleMaint = secondaryButton(avail ? "Mark Maintenance" : "Set Available");
        toggleMaint.setOnAction(e -> {
            String nStatus = avail ? "MAINTENANCE" : "AVAILABLE";
            AuditLogManager.addLog("Rahul Sharma", "Changed Resource Status", name + " set to " + nStatus, "RESOURCE");
            showModal("Resource Updated", new VBox(16, heading("Resource status changed", 16), muted(name + " is now " + nStatus)));
        });

        HBox row = new HBox(new VBox(4, heading(name, 16), muted("Type: " + type)), hgrow(), stChip, toggleMaint);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    // =========================================================
    // 6. OFFERINGS TAB
    // =========================================================
    private VBox createOfferingsTab() {
        Label title = heading("Space Offerings", 18);
        Button addOfferingBtn = primaryButton("+ Create New Offering");

        addOfferingBtn.setOnAction(e -> showModal("Create Offering", new VBox(16,
                heading("New Space Offering", 16),
                searchField("Offering Name (e.g. Fashion Shoot)"),
                searchField("Duration (e.g. 2 Hours)"),
                muted("Price is set by Space Owner upon approval."),
                primaryButton("Submit Offering")
        )));

        HBox header = new HBox(title, hgrow(), addOfferingBtn);

        VBox lockNotice = new VBox(8,
                chip("ℹ Commercial Pricing Policy", BLUE_BG, BLUE_TEXT),
                muted("Managers can create & edit operational parameters (name, description, duration, required resources), but CANNOT change price or disable offerings directly. To halt operations, mark resources as maintenance.")
        );
        lockNotice.setPadding(new Insets(12));
        lockNotice.setStyle("-fx-background-color:" + BLUE_BG + "; -fx-background-radius:12px;");

        VBox offeringsList = new VBox(12,
                offeringRow("Video Podcast Suite", "Studio B + Mic Set", "2 Hours", "₹2,500 (Owner Set)"),
                offeringRow("Commercial Photography", "Studio A + Light Set", "3 Hours", "₹4,000 (Owner Set)"),
                offeringRow("Audio Recording", "Studio B", "1 Hour", "₹1,200 (Owner Set)"),
                offeringRow("4K Video Editing Room", "Editing Suite", "2 Hours", "₹1,800 (Owner Set)")
        );

        return card(header, lockNotice, offeringsList);
    }

    private HBox offeringRow(String name, String resource, String duration, String price) {
        VBox info = new VBox(4, heading(name, 16), muted("Assigned Resource: " + resource + " · Duration: " + duration));

        Label priceTag = chip(price, CHIP_BG, TEXT);

        Button editBtn = secondaryButton("Edit Details");
        editBtn.setOnAction(e -> showModal("Edit Offering", new VBox(16,
                heading("Edit Offering Details", 16),
                searchField(name),
                muted("Price: " + price + " (Locked - Owner Controlled)"),
                primaryButton("Save Changes")
        )));

        HBox row = new HBox(info, hgrow(), priceTag, editBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(14);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    // =========================================================
    // 7. TEAM TAB
    // =========================================================
    private VBox createTeamTab() {
        Label title = heading("Space Operational Team", 18);
        Button addTeamBtn = primaryButton("+ Add Team Member");

        addTeamBtn.setOnAction(e -> showModal("Add Team Member", new VBox(16,
                heading("New Operational Team Member", 16),
                searchField("Full Name"),
                searchField("Role (e.g. Technician, Receptionist)"),
                searchField("Email"),
                primaryButton("Save Record")
        )));

        HBox header = new HBox(title, hgrow(), addTeamBtn);

        VBox loginNote = new VBox(8,
                chip("ℹ Operational Record Only", CHIP_BG, PRIMARY),
                muted("Team members listed here are managed for attendance and space operations. Team members DO NOT receive Manager portal login credentials.")
        );
        loginNote.setPadding(new Insets(12));
        loginNote.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px;");

        VBox teamList = new VBox(12,
                teamMemberRow("Rahul Sharma", "Receptionist / Front Desk", "rahul@spotfit.com", "PRESENT"),
                teamMemberRow("Priya Patel", "Studio Instructor", "priya@spotfit.com", "PRESENT"),
                teamMemberRow("Aman Verma", "Audio/Lighting Technician", "aman@spotfit.com", "ABSENT")
        );

        return card(header, loginNote, teamList);
    }

    private HBox teamMemberRow(String name, String role, String email, String status) {
        boolean present = status.equals("PRESENT");
        Label avatarLabel = avatar(initialsOf(name), 40);

        VBox info = new VBox(4, heading(name, 16), muted(role + " · ✉ " + email));

        Label attChip = chip(status, present ? GREEN_BG : RED_BG, present ? GREEN_TEXT : RED_TEXT);
        attChip.setStyle(attChip.getStyle() + " -fx-cursor:hand;");
        attChip.setOnMouseClicked(e -> AuditLogManager.addLog("Rahul Sharma", "Updated Team Attendance", name + " attendance updated", "TEAM"));

        HBox row = new HBox(avatarLabel, info, hgrow(), attChip);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(14);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
        return initials;
    }
}

