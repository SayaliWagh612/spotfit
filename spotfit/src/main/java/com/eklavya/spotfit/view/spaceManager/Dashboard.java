package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    private String currentTitle = "Dashboard";
    private Label topBarTitleLabel;
    private BorderPane rightPane;

    public Scene getDashboardPageScene(String managerName, Runnable callbackLogout) {

        rightPane = new BorderPane();
        rightPane.setTop(createTopBar(managerName, callbackLogout));

        ScrollPane centerScroll = scroll(createDashboardContent(managerName));
        centerScroll.setFitToWidth(true);
        rightPane.setCenter(centerScroll);

        VBox sidebar = createSidebar(managerName);
        sidebar.setMinWidth(280);
        sidebar.setMaxWidth(280);

        HBox root = new HBox(sidebar, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        rightPane.prefWidthProperty().bind(root.widthProperty().subtract(sidebar.widthProperty()));

        double w = (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getWidth() > 0)
                ? com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getWidth()
                : 1280;
        double h = (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getHeight() > 0)
                ? com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getHeight()
                : 800;
        return new Scene(root, w, h);
    }

    private ScrollPane createFitScroll(Node content) {
        ScrollPane sp = scroll(content);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        if (content instanceof Region) {
            ((Region) content).setMaxWidth(Double.MAX_VALUE);
        }
        return sp;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(String managerName) {

        Label logoIcon = new Label("🏙");
        logoIcon.setStyle("-fx-font-size:20px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:8 10;");
        Label logoText = new Label("SpotFit");
        logoText.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");
        Label logoSub = new Label("SPACE MANAGER");
        logoSub.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");

        HBox logoRow = new HBox(10, logoIcon, new VBox(2, logoText, logoSub));
        logoRow.setAlignment(Pos.CENTER_LEFT);
        logoRow.setPadding(new Insets(24, 20, 24, 20));

        HBox dashboardItem = createSidebarItem("🏠", "Dashboard");
        HBox bookingsItem = createSidebarItem("📋", "Bookings");
        HBox calendarItem = createSidebarItem("📅", "Calendar");
        HBox customersItem = createSidebarItem("👥", "Customers");
        HBox checkInItem = createSidebarItem("✔", "Check-In");
        HBox mySpaceItem = createSidebarItem("🏢", "My Space");
        HBox analyticsItem = createSidebarItem("📊", "Analytics");
        HBox reviewsItem = createSidebarItem("★", "Reviews");
        HBox complaintItem = createSidebarItem("💬", "Complaint Box");

        List<HBox> navItems = new ArrayList<>(List.of(dashboardItem, bookingsItem, calendarItem,
                customersItem, checkInItem, mySpaceItem, analyticsItem, reviewsItem, complaintItem));

        dashboardItem.setOnMouseClicked(e -> {
            setActiveItem(dashboardItem, navItems);
            updateTitle("Dashboard");
            rightPane.setCenter(createFitScroll(createDashboardContent(managerName)));
        });
        bookingsItem.setOnMouseClicked(e -> {
            setActiveItem(bookingsItem, navItems);
            updateTitle("Bookings");
            rightPane.setCenter(createFitScroll(new BookingManagement().getContent(rightPane)));
        });
        calendarItem.setOnMouseClicked(e -> {
            setActiveItem(calendarItem, navItems);
            updateTitle("Calendar");
            rightPane.setCenter(createFitScroll(new Calendar().getContent(rightPane)));
        });
        customersItem.setOnMouseClicked(e -> {
            setActiveItem(customersItem, navItems);
            updateTitle("Customers");
            rightPane.setCenter(createFitScroll(new CustomersView().getContent(rightPane)));
        });
        checkInItem.setOnMouseClicked(e -> {
            setActiveItem(checkInItem, navItems);
            updateTitle("Check-In");
            rightPane.setCenter(createFitScroll(new CheckIn().getContent()));
        });
        mySpaceItem.setOnMouseClicked(e -> {
            setActiveItem(mySpaceItem, navItems);
            updateTitle("My Space");
            rightPane.setCenter(createFitScroll(new MySpaceView().getContent(rightPane)));
        });
        analyticsItem.setOnMouseClicked(e -> {
            setActiveItem(analyticsItem, navItems);
            updateTitle("Analytics");
            rightPane.setCenter(createFitScroll(new AnalyticsView().getContent(rightPane)));
        });
        reviewsItem.setOnMouseClicked(e -> {
            setActiveItem(reviewsItem, navItems);
            updateTitle("Reviews");
            rightPane.setCenter(createFitScroll(new ReviewsView().getContent(rightPane)));
        });
        complaintItem.setOnMouseClicked(e -> {
            setActiveItem(complaintItem, navItems);
            updateTitle("Complaint Box");
            rightPane.setCenter(createFitScroll(new ComplaintBoxView().getContent(rightPane)));
        });

        setActiveItem(dashboardItem, navItems);

        VBox overviewSection = new VBox(4, sectionLabel("OVERVIEW"), dashboardItem);
        VBox workSection = new VBox(4, sectionLabel("WORK"), bookingsItem, calendarItem, customersItem,
                checkInItem, mySpaceItem, analyticsItem, reviewsItem, complaintItem);
        VBox navSections = new VBox(20, overviewSection, workSection);
        navSections.setPadding(new Insets(0, 12, 0, 12));

        HBox helpItem = createSidebarItem("❓", "Help");
        HBox settingsItem = createSidebarItem("⚙", "Settings");

        helpItem.setOnMouseClicked(e -> showHelpModal());
        settingsItem.setOnMouseClicked(e -> {
            updateTitle("Settings & Activity Log");
            rightPane.setCenter(createFitScroll(new SettingsView().getContent(rightPane, managerName)));
        });

        VBox bottomSection = new VBox(4, helpItem, settingsItem);
        bottomSection.setPadding(new Insets(12, 12, 20, 12));
        bottomSection.setStyle("-fx-border-color:" + BORDER + " transparent transparent transparent; -fx-border-width:1px;");

        VBox sidebar = new VBox(logoRow, navSections, growV(), bottomSection);
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent " + BORDER + " transparent transparent; -fx-border-width:1px;");

        return sidebar;
    }

    private void updateTitle(String newTitle) {
        this.currentTitle = newTitle;
        if (topBarTitleLabel != null) {
            topBarTitleLabel.setText(newTitle);
        }
    }

    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-padding:8 8 4 8;");
        return label;
    }

    private HBox createSidebarItem(String icon, String text) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:15px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + ";");

        HBox item = new HBox(12, iconLabel, textLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.setMaxWidth(Double.MAX_VALUE);
        item.setStyle("-fx-background-color:transparent; -fx-cursor:hand;");
        return item;
    }

    private void setActiveItem(HBox activeItem, List<HBox> allItems) {
        for (HBox item : allItems) {
            boolean active = item == activeItem;
            item.setStyle(active ? "-fx-background-color:" + ACTIVE_BG + "; -fx-background-radius:12px;" : "-fx-background-color:transparent; -fx-cursor:hand;");
            Label textLabel = (Label) item.getChildren().get(1);
            textLabel.setStyle("-fx-font-size:14px; -fx-font-weight:" + (active ? "bold" : "normal") + "; -fx-text-fill:" + (active ? PRIMARY : TEXT) + ";");
        }
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(String managerName, Runnable callbackLogout) {

        topBarTitleLabel = new Label(currentTitle);
        topBarTitleLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search bookings, customers...");
        searchField.setPrefWidth(340);
        searchField.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-font-size:13px; -fx-background-radius:12px; -fx-padding:8 14;");
        searchField.setOnAction(e -> openGlobalSearchModal(searchField.getText().trim()));

        Label bellIcon = iconButton("🔔");
        bellIcon.setOnMouseClicked(e -> openNotificationDrawer());

        Label notifBadge = new Label("3");
        notifBadge.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:white; -fx-background-color:#d64545; -fx-background-radius:20px; -fx-padding:1 5;");
        StackPane bellStack = new StackPane(bellIcon, notifBadge);
        StackPane.setAlignment(notifBadge, Pos.TOP_RIGHT);
        bellStack.setOnMouseClicked(e -> openNotificationDrawer());

        Label helpBtn = iconButton("❓");
        helpBtn.setOnMouseClicked(e -> showHelpModal());

        Label avatar = avatar(initialsOf(managerName), 36);
        Label nameLabel = new Label(managerName + " ▾");
        nameLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + "; -fx-cursor:hand;");
        nameLabel.setOnMouseClicked(event -> callbackLogout.run());
        HBox profileBox = new HBox(10, avatar, nameLabel);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        HBox topBar = new HBox(20, topBarTitleLabel, hgrow(), searchField, bellStack, helpBtn, profileBox);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 40, 16, 40));
        topBar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1px;");
        return topBar;
    }

    private Label iconButton(String icon) {
        Label label = new Label(icon);
        label.setStyle("-fx-font-size:16px; -fx-padding:8; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:50%; -fx-cursor:hand;");
        return label;
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
        return initials;
    }

    private Region growV() {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }

    // =========================================================
    // DASHBOARD CONTENT
    // =========================================================

    private VBox createDashboardContent(String managerName) {

        Label greeting = heading("Good morning, " + firstNameOf(managerName), 30);
        Label subGreeting = muted("Here's what's happening at your space today.");

        HBox chipsRow = new HBox(outlineChip("📍 Pixel Photography Studio, Ahmedabad, Gujarat"), hgrow(), outlineChip("📅 Thursday, 13 August 2026"));
        chipsRow.setAlignment(Pos.CENTER_LEFT);

        // Section 8: Quick Actions
        VBox quickActionsCard = createQuickActionsCard();

        HBox statsRow = new HBox(16,
                statCard("📋", "All Bookings", "48", null, "48 total recorded"),
                statCard("📅", "Today's Bookings", "18", chip("↗ +12%", GREEN_BG, GREEN_TEXT), "18 scheduled today"),
                statCard("⌛", "Upcoming", "23", null, "23 upcoming sessions"),
                statCard("✔", "Completed / Check-In", "6", null, "6 sessions done"),
                statCard("✕", "Cancelled", "1", null, "1 cancellation recorded")
        );
        statsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        statsRow.setAlignment(Pos.CENTER_LEFT);

        Label viewCalendar = link("View Calendar →");
        viewCalendar.setOnMouseClicked(e -> {
            updateTitle("Calendar");
            rightPane.setCenter(createFitScroll(new Calendar().getContent(rightPane)));
        });

        VBox scheduleRows = new VBox(14,
                scheduleRow("09:00\nAM", "AM", "Aarav Mehta", "SPF-2841", "Photography", chip("Checked In", CHIP_BG, TEXT), false, true),
                scheduleRow("11:30\nAM", "RK", "Riya Kapoor", "SPF-2848", "Portrait Session", chip("Arriving Soon", PRIMARY, "white"), true, false),
                scheduleRow("01:00\nPM", "NS", "Neha Shah", "SPF-2852", "Content Shoot", chip("Upcoming", CHIP_BG, TEXT), false, false),
                scheduleRow("03:30\nPM", "KJ", "Kabir Jain", "SPF-2861", "Meeting Suite", chip("Upcoming", CHIP_BG, TEXT), false, false)
        );
        VBox scheduleCard = card(sectionHeader("Today's Schedule", viewCalendar), scheduleRows);

        Button viewBookingButton = secondaryButton("View Booking Details");
        viewBookingButton.setOnAction(e -> {
            updateTitle("Booking Details");
            rightPane.setCenter(createFitScroll(new BookingDetails().getContent(rightPane)));
        });

        VBox nextUpCard = card(
                sectionLabel("NEXT UP"),
                new HBox(14, avatar("NS", 48), new VBox(2, heading("Neha Shah", 17), arrivingLabel("Arriving in 35 min"))),
                detailsBox(),
                viewBookingButton
        );

        VBox sideColumn = new VBox(20, nextUpCard, createSpaceStatusCard());
        sideColumn.setPrefWidth(360);
        sideColumn.setMinWidth(360);

        HBox lowerRow = new HBox(20, scheduleCard, sideColumn);
        HBox.setHgrow(scheduleCard, Priority.ALWAYS);

        VBox assignedSpacesCard = createAssignedSpacesSection();

        VBox content = new VBox(24, greeting, subGreeting, chipsRow, quickActionsCard, assignedSpacesCard, statsRow, lowerRow);
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private VBox createAssignedSpacesSection() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-border-color:" + BORDER + "; -fx-border-radius:16px; -fx-border-width:1px;");

        Label sectionTitle = heading("Assigned Operational Spaces", 18);
        container.getChildren().add(sectionTitle);

        loadAssignedSpaces(container);
        return container;
    }

    private void loadAssignedSpaces(VBox container) {
        // Clear previous state node except title
        while (container.getChildren().size() > 1) {
            container.getChildren().remove(1);
        }

        // State 1: Loading
        Label loadingLabel = muted("Loading assigned operational spaces...");
        container.getChildren().add(loadingLabel);

        com.eklavya.spotfit.controller.manager.ManagerSpaceOperationsController opsController =
                new com.eklavya.spotfit.controller.manager.ManagerSpaceOperationsController();

        opsController.getAssignedSpaces().thenAccept(res -> javafx.application.Platform.runLater(() -> {
            while (container.getChildren().size() > 1) {
                container.getChildren().remove(1);
            }

            if (res.isFailure()) {
                // State 3: Error with Retry
                Label errLabel = new Label("Failed to load assigned spaces: " + res.getMessage() + " (" + res.getErrorCode() + ")");
                errLabel.setStyle("-fx-text-fill:" + RED_TEXT + "; -fx-font-size:13px;");

                Button retryBtn = secondaryButton("🔄 Retry Loading");
                retryBtn.setOnAction(e -> loadAssignedSpaces(container));

                container.getChildren().addAll(errLabel, retryBtn);
                return;
            }

            List<com.eklavya.spotfit.model.Space> spaces = res.getData();
            if (spaces == null || spaces.isEmpty()) {
                // State 2: Empty
                Label emptyLabel = muted("No spaces currently assigned to your manager account.");
                container.getChildren().add(emptyLabel);
                return;
            }

            // State 4: Success
            VBox cardsList = new VBox(12);
            for (com.eklavya.spotfit.model.Space space : spaces) {
                cardsList.getChildren().add(createAssignedSpaceCard(space));
            }
            container.getChildren().add(cardsList);
        }));
    }

    private HBox createAssignedSpaceCard(com.eklavya.spotfit.model.Space space) {
        String spaceName = (space.getSpaceName() != null && !space.getSpaceName().isBlank()) ? space.getSpaceName() : space.getName();
        if (spaceName == null || spaceName.isBlank()) spaceName = "Unnamed Space";

        String bookingType = space.getBookingType() != null ? space.getBookingType().toUpperCase() : "SLOT";
        String approvalStatus = space.getApprovalStatus() != null ? space.getApprovalStatus().toUpperCase() : "DRAFT";
        String operationalStatus = space.getOperationalStatus() != null ? space.getOperationalStatus().toUpperCase() : "INACTIVE";
        boolean isV2 = space.getSchemaVersion() == 2;

        Label bTypeChip = chip("Type: " + bookingType, BLUE_BG, BLUE_TEXT);

        boolean isApproved = "APPROVED".equalsIgnoreCase(approvalStatus);
        Label appChip = chip("Approval: " + approvalStatus, isApproved ? GREEN_BG : CHIP_BG, isApproved ? GREEN_TEXT : TEXT);

        boolean isActive = "ACTIVE".equalsIgnoreCase(operationalStatus);
        Label opChip = chip("Status: " + operationalStatus, isActive ? GREEN_BG : RED_BG, isActive ? GREEN_TEXT : RED_TEXT);

        Label vChip = chip(isV2 ? "V2 Architecture" : "V1 Legacy", CHIP_BG, SECONDARY_TEXT);

        HBox chipsRow = new HBox(8, bTypeChip, appChip, opChip, vChip);

        String catSub = (space.getCategory() != null ? space.getCategory() : "") +
                (space.getActivityGroup() != null ? " · " + space.getActivityGroup() : "");
        VBox infoBox = new VBox(4, heading(spaceName, 16), muted(catSub), chipsRow);

        Button actionBtn = primaryButton(isV2 ? "Open V2 Operational View →" : "Open V1 Space Management →");
        actionBtn.setOnAction(e -> {
            updateTitle("My Space");
            rightPane.setCenter(createFitScroll(new MySpaceView(space).getContent(rightPane)));
        });

        HBox card = new HBox(infoBox, hgrow(), actionBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px;");
        return card;
    }

    private VBox createQuickActionsCard() {
        Label title = heading("Quick Operational Actions", 18);

        Button btnCheckIn = primaryButton("✔ Check In Customer");
        btnCheckIn.setOnAction(e -> {
            updateTitle("Check-In");
            rightPane.setCenter(createFitScroll(new CheckIn().getContent()));
        });

        Button btnFindBooking = secondaryButton("🔍 Find Booking");
        btnFindBooking.setOnAction(e -> {
            updateTitle("Bookings");
            rightPane.setCenter(createFitScroll(new BookingManagement().getContent(rightPane)));
        });

        Button btnBlockSlot = secondaryButton("🔒 Block Slot");
        btnBlockSlot.setOnAction(e -> {
            updateTitle("Calendar");
            rightPane.setCenter(createFitScroll(new Calendar().getContent(rightPane)));
        });

        Button btnUpdateSpace = secondaryButton("🏢 Update Space");
        btnUpdateSpace.setOnAction(e -> {
            updateTitle("My Space");
            rightPane.setCenter(createFitScroll(new MySpaceView().getContent(rightPane)));
        });

        HBox buttonsRow = new HBox(12, btnCheckIn, btnFindBooking, btnBlockSlot, btnUpdateSpace);
        buttonsRow.getChildren().forEach(b -> HBox.setHgrow(b, Priority.ALWAYS));

        return card(title, buttonsRow);
    }

    private String firstNameOf(String name) {
        if (name == null || name.isEmpty()) return "Manager";
        return name.trim().split("\\s+")[0];
    }

    private VBox statCard(String icon, String label, String value, Label badge, String footNote) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:16px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:12px; -fx-padding:10;");
        HBox topRow = badge != null ? new HBox(iconLabel, hgrow(), badge) : new HBox(iconLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(12, topRow, muted(label), heading(value, 26));
        if (footNote != null) {
            Label foot = new Label(footNote);
            foot.setStyle("-fx-font-size:12px; -fx-text-fill:" + SECONDARY_TEXT + ";");
            card.getChildren().add(foot);
        }

        card.setMinWidth(130);
        card.setPrefWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:20px; -fx-border-color:" + BORDER + "; -fx-border-radius:20px; -fx-border-width:1px;");
        return card;
    }

    private HBox scheduleRow(String time, String initials, String name, String bookingId, String activity, Label statusChip, boolean highlighted, boolean checkedIn) {
        Label timeLabel = new Label(time);
        timeLabel.setPrefWidth(56);
        timeLabel.setStyle("-fx-font-size:13px; -fx-font-weight:" + (highlighted ? "bold" : "normal") + "; -fx-text-fill:" + (highlighted ? PRIMARY : SECONDARY_TEXT) + "; -fx-text-alignment:center;");

        Label nameLabel = new Label(name + " (" + bookingId + ")");
        nameLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";" + (checkedIn ? " -fx-strikethrough:true;" : ""));
        VBox nameBox = activity.isEmpty() ? new VBox(nameLabel) : new VBox(2, nameLabel, muted(activity));

        HBox personRow = new HBox(12, avatar(initials, 38), nameBox);
        personRow.setAlignment(Pos.CENTER_LEFT);

        HBox rowContent = new HBox(personRow, hgrow(), statusChip);
        rowContent.setAlignment(Pos.CENTER_LEFT);
        rowContent.setPadding(new Insets(14));
        rowContent.setMaxWidth(Double.MAX_VALUE);
        rowContent.setStyle(highlighted
                ? "-fx-background-color:" + CHIP_BG + "; -fx-background-radius:14px; -fx-border-color:" + PRIMARY + "; -fx-border-width:1.5px; -fx-border-radius:14px;"
                : "-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:14px;");
        HBox.setHgrow(rowContent, Priority.ALWAYS);

        HBox row = new HBox(12, timeLabel, rowContent);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label arrivingLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");
        return label;
    }

    private VBox detailsBox() {
        VBox box = new VBox(10, detailRow("Activity:", "Content Shoot Floor"), detailRow("Time:", "1:00 PM - 3:00 PM"), detailRow("Size:", "4 people"));
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:14px;");
        return box;
    }

    private HBox detailRow(String label, String value) {
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        HBox row = new HBox(labelText, hgrow(), valueText);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox createSpaceStatusCard() {
        VBox titleBox = new VBox(12, heading("Space Status", 17), chip("● Open (09:00 AM - 09:00 PM)", GREEN_BG, GREEN_TEXT));
        titleBox.setAlignment(Pos.CENTER);

        HBox miniStatsRow = new HBox(12, miniStat("12/20", "OCCUPANCY"), miniStat("18", "BOOKINGS"));
        miniStatsRow.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        Button manageBtn = primaryButton("Manage Space");
        manageBtn.setOnAction(e -> {
            updateTitle("My Space");
            rightPane.setCenter(createFitScroll(new MySpaceView().getContent(rightPane)));
        });

        return card(titleBox, miniStatsRow, manageBtn);
    }

    private VBox miniStat(String value, String label) {
        VBox box = new VBox(4, heading(value, 20), muted(label));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:14px;");
        return box;
    }

    // =========================================================
    // MODALS & DRAWER HELPERS
    // =========================================================

    private void openNotificationDrawer() {
        VBox list = new VBox(10,
                notifRow("📋 New Booking Received", "Neha Shah booked Studio B (1:00 PM - 3:00 PM)", "10 mins ago"),
                notifRow("✔ Customer Checked In", "Aarav Mehta checked in for SPF-2841", "45 mins ago"),
                notifRow("⭐ New 5-Star Review", "Aarav Mehta left a review for Video Podcast Suite", "2 hours ago")
        );
        VBox body = new VBox(16, heading("Notifications Drawer", 18), list);
        body.setPadding(new Insets(20));
        showModal("Notifications (3)", body);
    }

    private HBox notifRow(String t, String desc, String time) {
        VBox text = new VBox(2, heading(t, 14), muted(desc + " · " + time));
        HBox r = new HBox(text);
        r.setPadding(new Insets(10));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    private void openGlobalSearchModal(String query) {
        VBox results = new VBox(12,
                heading("Search Results for: \"" + query + "\"", 16),
                searchResultRow("Booking #SPF-2852", "Neha Shah · Content Shoot Floor (1:00 PM - 3:00 PM)"),
                searchResultRow("Customer Profile", "Neha Shah · neha.shah@example.com (7 Visits)"),
                searchResultRow("Resource Asset", "Studio B (Podcast Suite) · Available")
        );
        results.setPadding(new Insets(20));
        showModal("Global Search", results);
    }

    private HBox searchResultRow(String title, String sub) {
        VBox info = new VBox(2, heading(title, 14), muted(sub));
        HBox r = new HBox(info);
        r.setPadding(new Insets(10));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    private void showHelpModal() {
        VBox helpBody = new VBox(14,
                heading("SpotFit Space Manager Help & Support", 18),
                muted("Need assistance operating your physical space? Contact Space Owner or SpotFit platform operational desk."),
                muted("Owner Contact: Vikram Shah (vikram@spotfit.com)"),
                muted("Platform Support Desk: support@spotfit.com"),
                primaryButton("Close Help")
        );
        helpBody.setPadding(new Insets(20));
        showModal("Help & Assistance", helpBody);
    }
}
