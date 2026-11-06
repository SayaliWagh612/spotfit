package com.eklavya.spotfit.view.admin;

import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class AdminDashboard {

    private String currentTitle = "Dashboard";
    private Label topBarTitleLabel;
    private BorderPane rightPane;

    public Scene getAdminDashboardPageScene(String adminName, Runnable callbackLogout) {
        rightPane = new BorderPane();
        rightPane.setTop(createTopBar(adminName, callbackLogout));

        ScrollPane centerScroll = createFitScroll(new DashboardOverviewView().getContent());
        rightPane.setCenter(centerScroll);

        VBox sidebar = createSidebar(adminName);
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

    private VBox createSidebar(String adminName) {
        Label logoIcon = new Label("🛡");
        logoIcon.setStyle("-fx-font-size:20px; -fx-background-color:" + BLUE_BG
                + "; -fx-background-radius:12px; -fx-padding:8 10;");
        Label logoText = new Label("SpotFit");
        logoText.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + ACCENT + ";");
        Label logoSub = new Label("PLATFORM ADMIN");
        logoSub.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + ";");

        HBox logoRow = new HBox(10, logoIcon, new VBox(2, logoText, logoSub));
        logoRow.setAlignment(Pos.CENTER_LEFT);
        logoRow.setPadding(new Insets(24, 20, 24, 20));

        // OVERVIEW
        HBox dashboardItem = createSidebarItem("🏠", "Dashboard");

        // PLATFORM
        HBox usersItem = createSidebarItem("👥", "Users");
        HBox ownersItem = createSidebarItem("🏢", "Owners");
        HBox managersItem = createSidebarItem("♟", "Managers");
        HBox spacesItem = createSidebarItem("📍", "Spaces");
        HBox bookingsItem = createSidebarItem("📋", "Bookings");

        // TRUST & SUPPORT
        HBox verificationItem = createSidebarItem("⏳", "Verification");
        HBox complaintsItem = createSidebarItem("⚠️", "Complaints");
        HBox supportItem = createSidebarItem("🎫", "Support Tickets");
        HBox reviewsItem = createSidebarItem("⭐", "Reviews");

        // FINANCE & INSIGHTS
        HBox transactionsItem = createSidebarItem("💳", "Transactions");
        HBox reportsItem = createSidebarItem("📊", "Reports");
        HBox analyticsItem = createSidebarItem("📈", "Analytics");

        // CONFIGURATION
        HBox categoriesItem = createSidebarItem("🏷", "Categories");
        HBox activitiesItem = createSidebarItem("⚡", "Activities");
        HBox personasItem = createSidebarItem("🎭", "Personas");
        HBox interestsItem = createSidebarItem("💡", "Interests");

        List<HBox> navItems = new ArrayList<>(List.of(
                dashboardItem, usersItem, ownersItem, managersItem, spacesItem, bookingsItem,
                verificationItem, complaintsItem, supportItem, reviewsItem,
                transactionsItem, reportsItem, analyticsItem,
                categoriesItem, activitiesItem, personasItem, interestsItem));

        // Event Handlers
        dashboardItem.setOnMouseClicked(e -> {
            setActiveItem(dashboardItem, navItems);
            updateTitle("Dashboard");
            rightPane.setCenter(createFitScroll(new DashboardOverviewView().getContent()));
        });
        usersItem.setOnMouseClicked(e -> {
            setActiveItem(usersItem, navItems);
            updateTitle("Users Governance");
            rightPane.setCenter(createFitScroll(new UsersView().getContent()));
        });
        ownersItem.setOnMouseClicked(e -> {
            setActiveItem(ownersItem, navItems);
            updateTitle("Owners Governance");
            rightPane.setCenter(createFitScroll(new OwnersView().getContent()));
        });
        managersItem.setOnMouseClicked(e -> {
            setActiveItem(managersItem, navItems);
            updateTitle("Space Managers");
            rightPane.setCenter(createFitScroll(new ManagersView().getContent()));
        });
        spacesItem.setOnMouseClicked(e -> {
            setActiveItem(spacesItem, navItems);
            updateTitle("Spaces Governance");
            rightPane.setCenter(createFitScroll(new SpacesView().getContent()));
        });
        bookingsItem.setOnMouseClicked(e -> {
            setActiveItem(bookingsItem, navItems);
            updateTitle("Platform Bookings");
            rightPane.setCenter(createFitScroll(new BookingsView().getContent()));
        });

        verificationItem.setOnMouseClicked(e -> {
            setActiveItem(verificationItem, navItems);
            updateTitle("Verification Queue");
            rightPane.setCenter(createFitScroll(new VerificationView().getContent()));
        });
        complaintsItem.setOnMouseClicked(e -> {
            setActiveItem(complaintsItem, navItems);
            updateTitle("Complaints & Incidents");
            rightPane.setCenter(createFitScroll(new ComplaintsView().getContent()));
        });
        supportItem.setOnMouseClicked(e -> {
            setActiveItem(supportItem, navItems);
            updateTitle("Support Tickets");
            rightPane.setCenter(createFitScroll(new SupportTicketsView().getContent()));
        });
        reviewsItem.setOnMouseClicked(e -> {
            setActiveItem(reviewsItem, navItems);
            updateTitle("Review Moderation");
            rightPane.setCenter(createFitScroll(new ReviewsView().getContent()));
        });

        transactionsItem.setOnMouseClicked(e -> {
            setActiveItem(transactionsItem, navItems);
            updateTitle("Transactions & Refunds");
            rightPane.setCenter(createFitScroll(new TransactionsView().getContent()));
        });
        reportsItem.setOnMouseClicked(e -> {
            setActiveItem(reportsItem, navItems);
            updateTitle("Platform Reports");
            rightPane.setCenter(createFitScroll(new ReportsView().getContent()));
        });
        analyticsItem.setOnMouseClicked(e -> {
            setActiveItem(analyticsItem, navItems);
            updateTitle("Platform Analytics");
            rightPane.setCenter(createFitScroll(new AdminAnalyticsView().getContent()));
        });

        categoriesItem.setOnMouseClicked(e -> {
            setActiveItem(categoriesItem, navItems);
            updateTitle("Categories Catalogue");
            rightPane.setCenter(createFitScroll(new TaxonomyView().getContent("Categories")));
        });
        activitiesItem.setOnMouseClicked(e -> {
            setActiveItem(activitiesItem, navItems);
            updateTitle("Activities Catalogue");
            rightPane.setCenter(createFitScroll(new TaxonomyView().getContent("Activities")));
        });
        personasItem.setOnMouseClicked(e -> {
            setActiveItem(personasItem, navItems);
            updateTitle("Personas Catalogue");
            rightPane.setCenter(createFitScroll(new TaxonomyView().getContent("Personas")));
        });
        interestsItem.setOnMouseClicked(e -> {
            setActiveItem(interestsItem, navItems);
            updateTitle("Interests Catalogue");
            rightPane.setCenter(createFitScroll(new TaxonomyView().getContent("Interests")));
        });

        setActiveItem(dashboardItem, navItems);

        VBox overviewSec = new VBox(2, sectionLabel("OVERVIEW"), dashboardItem);
        VBox platformSec = new VBox(2, sectionLabel("PLATFORM"), usersItem, ownersItem, managersItem, spacesItem,
                bookingsItem);
        VBox trustSec = new VBox(2, sectionLabel("TRUST & SUPPORT"), verificationItem, complaintsItem, supportItem,
                reviewsItem);
        VBox financeSec = new VBox(2, sectionLabel("FINANCE & INSIGHTS"), transactionsItem, reportsItem, analyticsItem);
        VBox configSec = new VBox(2, sectionLabel("CONFIGURATION"), categoriesItem, activitiesItem, personasItem,
                interestsItem);

        VBox navSections = new VBox(14, overviewSec, platformSec, trustSec, financeSec, configSec);
        navSections.setPadding(new Insets(0, 12, 0, 12));
        navSections.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent " + BORDER
                + " transparent transparent; -fx-border-width:1px;");

        ScrollPane navScroll = scroll(navSections);

        HBox helpItem = createSidebarItem("❓", "Help");
        HBox settingsItem = createSidebarItem("⚙", "Settings");

        helpItem.setOnMouseClicked(e -> showHelpModal());
        settingsItem.setOnMouseClicked(e -> {
            updateTitle("Admin Settings & Configuration");
            rightPane.setCenter(createFitScroll(new AdminSettingsView().getContent("Audit Log")));
        });

        VBox bottomSection = new VBox(4, helpItem, settingsItem);
        bottomSection.setPadding(new Insets(10, 12, 16, 12));
        bottomSection
                .setStyle("-fx-border-color:" + BORDER + " transparent transparent transparent; -fx-border-width:1px;");

        VBox sidebar = new VBox(logoRow, navScroll, bottomSection);
        VBox.setVgrow(navScroll, Priority.ALWAYS);
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);
        sidebar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent " + BORDER
                + " transparent transparent; -fx-border-width:1px;");

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
        label.setStyle(
                "-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + SECONDARY_TEXT + "; -fx-padding:6 8 2 8;");
        return label;
    }

    private HBox createSidebarItem(String icon, String text) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size:14px;");
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size:13px; -fx-text-fill:" + TEXT + ";");

        HBox item = new HBox(10, iconLabel, textLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setMaxWidth(Double.MAX_VALUE);
        item.setStyle("-fx-background-color:transparent; -fx-cursor:hand;");
        return item;
    }

    private void setActiveItem(HBox activeItem, List<HBox> allItems) {
        for (HBox item : allItems) {
            boolean active = item == activeItem;
            item.setStyle(active ? "-fx-background-color:" + ACTIVE_BG + "; -fx-background-radius:10px;"
                    : "-fx-background-color:transparent; -fx-cursor:hand;");
            Label textLabel = (Label) item.getChildren().get(1);
            textLabel.setStyle("-fx-font-size:13px; -fx-font-weight:" + (active ? "bold" : "normal")
                    + "; -fx-text-fill:" + (active ? ACCENT : TEXT) + ";");
        }
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(String adminName, Runnable callbackLogout) {
        topBarTitleLabel = new Label(currentTitle);
        topBarTitleLabel.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Global platform search...");
        searchField.setPrefWidth(320);
        searchField.setStyle("-fx-background-color:" + CHIP_BG
                + "; -fx-font-size:13px; -fx-background-radius:10px; -fx-padding:8 14;");
        searchField.setOnAction(e -> openGlobalSearchModal(searchField.getText().trim()));

        Label bellIcon = iconButton("🔔");
        Label notifBadge = new Label("3");
        notifBadge.setStyle(
                "-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:white; -fx-background-color:#ef4444; -fx-background-radius:20px; -fx-padding:1 5;");
        StackPane bellStack = new StackPane(bellIcon, notifBadge);
        StackPane.setAlignment(notifBadge, Pos.TOP_RIGHT);
        bellStack.setOnMouseClicked(e -> openNotificationDrawer());

        Label helpBtn = iconButton("❓");
        helpBtn.setOnMouseClicked(e -> showHelpModal());

        Label avatar = avatar("AD", 36);
        Label nameLabel = new Label(adminName + " (Admin) ▾");
        nameLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + "; -fx-cursor:hand;");
        nameLabel.setOnMouseClicked(event -> callbackLogout.run());

        HBox profileBox = new HBox(10, avatar, nameLabel);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        HBox topBar = new HBox(20, topBarTitleLabel, hgrow(), searchField, bellStack, helpBtn, profileBox);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16, 40, 16, 40));
        topBar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent transparent " + BORDER
                + " transparent; -fx-border-width:1px;");
        return topBar;
    }

    private Label iconButton(String icon) {
        Label label = new Label(icon);
        label.setStyle("-fx-font-size:15px; -fx-padding:7; -fx-background-color:" + CHIP_BG
                + "; -fx-background-radius:50%; -fx-cursor:hand;");
        return label;
    }

    private void openNotificationDrawer() {
        VBox list = new VBox(10,
                notifRow("🚨 Direct Safety Complaint", "Unsafe equipment report at FitZone Gym B", "12 mins ago"),
                notifRow("💳 Razorpay Dispute Flagged", "Dispute #DSP-9041 requires admin decision", "45 mins ago"),
                notifRow("⏳ New Owner Verification", "Rajesh Sharma submitted UrbanFit Group docs", "2 hours ago"));
        VBox body = new VBox(16, heading("Admin Action Alerts (3)", 18), list);
        body.setPadding(new Insets(20));
        showModal("Platform Alerts", body);
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
                heading("Global Search Results for: \"" + query + "\"", 16),
                searchResultRow("User Record", "Aarav Mehta · aarav.mehta@example.com (USR-1001)"),
                searchResultRow("Space Branch", "Pixel Photography Studio - Satellite (SPC-101)"),
                searchResultRow("Booking Entry", "Booking #SPF-2852 · Content Shoot Floor"),
                searchResultRow("Complaint File", "Complaint #CMP-901 · Safety Incident"));
        results.setPadding(new Insets(20));
        showModal("Platform Search", results);
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
                heading("SpotFit Admin Governance Manual", 18),
                muted("Governing Principles:"),
                muted("1. Admin verifies, moderates, governs, investigates, and intervenes at the platform level."),
                muted("2. Owner retains responsibility for business identity, space content, offerings, pricing, operations, and manager assignments."),
                muted("3. Exceptional Admin actions require a mandatory reason, preserve historical records, and produce an immutable audit entry."),
                primaryButton("Close Help"));
        helpBody.setPadding(new Insets(20));
        showModal("Admin Governance Help", helpBody);
    }
}

