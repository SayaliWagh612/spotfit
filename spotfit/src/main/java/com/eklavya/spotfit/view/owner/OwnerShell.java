package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/**
 * Single persistent shell for the Owner dashboard - same architecture as
 * spaceManager/Dashboard.java.
 *
 * Before: every PageRegistry.go(key) call ran OwnerUi.pageShell(...), which
 * rebuilt the sidebar AND the top bar (space-scope ComboBox, its item list,
 * its listeners - everything) from scratch, wrapped in a brand new Scene.
 * PageRegistry then had to cache those Scenes (sceneCache) just to avoid
 * doing that full rebuild on every single click.
 *
 * Now: the top bar is built exactly ONCE, in build() below, the first time
 * any Owner page is opened. Every later navigation just swaps the center of
 * `rightPane` for the new page's content - nothing about the shell itself
 * is touched, so there is nothing left to cache.
 *
 * The sidebar is still rebuilt on every navigation (via OwnerUi.sidebar),
 * because that's the cheapest way to keep its "active item" highlight in
 * sync with `activeKey` - it's 14 Labels, not the whole page.
 */
public class OwnerShell {

    private static OwnerShell instance;

    private final BorderPane rightPane = new BorderPane();
    private Label titleLabel;
    private Scene shellScene;
    private String activeKey = "dashboard";

    private OwnerShell() {
        build();
    }

    private static OwnerShell instance() {
        if (instance == null) {
            instance = new OwnerShell();
        }
        return instance;
    }

    // =========================================================
    // ENTRY POINTS (called from PageRegistry - see that file)
    // =========================================================

    /** The shell's one and only Scene. Every "back to shell" link from an
     *  unconverted detail page (e.g. AccountSettingsPage) should navigate
     *  to this, not to a Scene it built for itself. */
    public static Scene getScene() {
        return instance().shellScene;
    }

    /**
     * Navigate to `key`. First call ever also puts the shell Scene on the
     * Stage; every call after that just swaps rightPane's center, so the
     * sidebar + top bar are never rebuilt from scratch again.
     */
    public static void show(String key) {
        OwnerShell shell = instance();
        if (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getScene() != shell.shellScene) {
            OwnerUi.navigate(shell.shellScene);
        }
        shell.navigateTo(key);
    }

    /** Rebuilds the CURRENT page's content in place - e.g. after the space
     *  scope changes, or after an in-place edit (add/remove a manager). */
    public static void refresh() {
        instance().navigateTo(instance().activeKey);
    }

    // =========================================================
    // BUILD (runs exactly once, lazily, on first navigation)
    // =========================================================

    private void build() {
        rightPane.setTop(createTopBar());

        VBox sidebar = OwnerUi.sidebar(activeKey);

        HBox root = new HBox(sidebar, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.prefWidthProperty().bind(root.widthProperty().subtract(sidebar.widthProperty()));

        shellScene = OwnerUi.sizedScene(root);
    }

    private ScrollPane createFitScroll(Node content) {
        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + OwnerUi.BACKGROUND + ";");
        if (content instanceof Region) {
            ((Region) content).setMaxWidth(Double.MAX_VALUE);
        }
        return scroller;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void navigateTo(String key) {
        activeKey = key;

        Node content = buildContent(key);
        VBox page = new VBox(24, content);
        page.setPadding(new Insets(24, 32, 32, 32));
        rightPane.setCenter(createFitScroll(page));

        if (titleLabel != null) {
            titleLabel.setText(titleFor(key));
        }

        swapSidebar(key);
    }

    private void swapSidebar(String key) {
        HBox root = (HBox) rightPane.getParent();
        if (root == null) return;
        VBox freshSidebar = OwnerUi.sidebar(key);
        root.getChildren().set(0, freshSidebar);
        rightPane.prefWidthProperty().bind(root.widthProperty().subtract(freshSidebar.widthProperty()));
    }

    public static void showCustomPage(String title, Node content) {
        showCustomPage("businesses", title, content);
    }

    public static void showCustomPage(String sidebarKey, String title, Node content) {
        OwnerShell shell = instance();
        if (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getScene() != shell.shellScene) {
            OwnerUi.navigate(shell.shellScene);
        }
        shell.renderCustomPage(sidebarKey, title, content);
    }

    private void renderCustomPage(String sidebarKey, String title, Node content) {
        String key = (sidebarKey != null && !sidebarKey.isBlank()) ? sidebarKey : "myspaces";
        activeKey = key;
        VBox page = new VBox(24, content);
        page.setPadding(new Insets(24, 32, 32, 32));
        rightPane.setCenter(createFitScroll(page));
        if (titleLabel != null) {
            titleLabel.setText(title != null ? title : ("myspaces".equals(key) ? "My Spaces" : "My Businesses"));
        }
        swapSidebar(key);
    }

    private Node buildContent(String key) {
        switch (key) {
            case "dashboard":
                return new OwnerDashboardFirstSpacePage().getContent(rightPane);
            case "businesses":
                return new OwnerBusinessesPage().getContent();
            case "myspaces":
                return new MySpacesPage().getContent(rightPane);
            case "bookings":
                return new BookingManagementPage().getContent(rightPane);
            case "complaints":
                return new OwnerComplaintsPage().getContent(rightPane);
            case "crm":
                return new CustomerCRMPage().getContent(rightPane);
            case "analytics":
                return new AnalyticsDashboardPage().getContent(rightPane);
            case "revenue":
                return new RevenuePayoutsPage().getContent(rightPane);
            case "staff":
                return new StaffDetailsPage().getContent(rightPane);
            case "reviews":
                return new ReviewsFeedbackPage().getContent(rightPane);
            case "promotions":
                return new PromotionsOffersPage().getContent(rightPane);
            case "notifications":
                return new NotificationsCenterPage().getContent(rightPane);
            case "settings":
                return new BusinessSettingsPage().getContent(rightPane);
            case "profile":
                return new OwnerProfilePage().getContent(rightPane);
            case "help":
                return new HelpSupportPage().getContent(rightPane);
            default:
                System.out.println("[OwnerShell] Unknown key: " + key);
                return new VBox();
        }
    }

    private String titleFor(String key) {
        switch (key) {
            case "dashboard": return "Owner Business Dashboard";
            case "businesses": return "My Businesses";
            case "myspaces": return "My Spaces Portfolio";
         //   case "bookings": return "Booking Management";
            case "complaints": return "Complaints & Escalations";
            case "crm": return "Customer Management";
            case "analytics": return "Business Analytics";
            case "revenue": return "Revenue & Payouts";
            case "staff": return "Space Managers";
            case "reviews": return "Reviews & Feedback";
            case "promotions": return "Promotions & Offers";
            case "notifications": return "Notifications Center";
            case "settings": return "Business Settings";
            case "profile": return "Owner Profile";
            case "help": return "Help & Support Center";
            default: return "Dashboard";
        }
    }

    // =========================================================
    // TOP BAR (built once - space scope combo, bell, gear, profile)
    // =========================================================

    public static void resetInstance() {
        instance = null;
    }

    public static String getInitials(String name) {
        if (name == null || name.isBlank()) return "O";
        String clean = name.trim();
        String[] parts = clean.split("\\s+");
        if (parts.length == 1) {
            return clean.substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    private VBox createTopBar() {
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = OwnerUi.heading(titleFor(activeKey), 22);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ComboBox<String> spaceScopeCombo = new ComboBox<>();
        spaceScopeCombo.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-background-color:" + OwnerUi.SURFACE + ";"
                + " -fx-border-color:" + OwnerUi.CARD_BORDER + "; -fx-border-radius:8;");

        spaceScopeCombo.getItems().add("All Spaces Scope");
        for (Space space : MockDataStore.getInstance().getSpaces()) {
            spaceScopeCombo.getItems().add(space.getName());
        }

        String currentScope = MockDataStore.getInstance().getSelectedSpaceId();
        if (currentScope == null || "ALL".equalsIgnoreCase(currentScope)) {
            spaceScopeCombo.getSelectionModel().select("All Spaces Scope");
        } else {
            Space selectedSpace = MockDataStore.getInstance().getSelectedSpace();
            if (selectedSpace != null) {
                spaceScopeCombo.getSelectionModel().select(selectedSpace.getName());
            } else {
                spaceScopeCombo.getSelectionModel().select("All Spaces Scope");
            }
        }

        spaceScopeCombo.setOnAction(e -> {
            String selectedText = spaceScopeCombo.getSelectionModel().getSelectedItem();
            if (selectedText == null || "All Spaces Scope".equals(selectedText)) {
                MockDataStore.getInstance().setSelectedSpaceId("ALL");
            } else {
                MockDataStore.getInstance().getSpaces().stream()
                        .filter(space -> space.getName().equals(selectedText))
                        .findFirst()
                        .ifPresent(space -> MockDataStore.getInstance().setSelectedSpaceId(space.getSpaceId()));
            }
            refresh();
        });

        Label bell = new Label("\uD83D\uDD14");
        Label gear = new Label("\u2699");
        bell.setStyle("-fx-font-size:16px; -fx-cursor:hand;");
        gear.setStyle("-fx-font-size:16px; -fx-cursor:hand;");
        gear.setOnMouseClicked(e -> PageRegistry.go("settings"));

        String ownerNameText = "Owner";
        if (com.eklavya.spotfit.session.AppSession.isAuthenticated()
                && com.eklavya.spotfit.session.AppSession.getName() != null
                && !com.eklavya.spotfit.session.AppSession.getName().isBlank()) {
            ownerNameText = com.eklavya.spotfit.session.AppSession.getName();
        } else if (com.eklavya.spotfit.session.AppSession.getEmail() != null
                && !com.eklavya.spotfit.session.AppSession.getEmail().isBlank()) {
            ownerNameText = com.eklavya.spotfit.session.AppSession.getEmail().split("@")[0];
        }

        Label avatar = new Label(getInitials(ownerNameText));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(34, 34);
        avatar.setStyle("-fx-background-color:" + OwnerUi.PRIMARY_LIGHT + "; -fx-background-radius:999; -fx-font-weight:bold;"
                + " -fx-text-fill:" + OwnerUi.PRIMARY + "; -fx-cursor:hand;");
        Label ownerName = OwnerUi.body(ownerNameText + "  \u25be", 13, OwnerUi.TEXT_DARK);
        ownerName.setStyle(ownerName.getStyle() + " -fx-cursor:hand; -fx-font-weight:bold;");
        avatar.setOnMouseClicked(e -> PageRegistry.go("profile"));
        ownerName.setOnMouseClicked(e -> PageRegistry.go("profile"));
        HBox profile = new HBox(8, avatar, ownerName);
        profile.setAlignment(Pos.CENTER_LEFT);

        HBox rightIcons = new HBox(16, spaceScopeCombo, bell, gear, profile);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);
        topRow.getChildren().addAll(spacer, rightIcons);
        topRow.setSpacing(12);

        VBox topBar = new VBox(topRow);
        topBar.setPadding(new Insets(18, 32, 18, 32));
        topBar.setStyle("-fx-background-color:" + OwnerUi.SURFACE + "; -fx-border-color:transparent transparent "
                + OwnerUi.CARD_BORDER + " transparent; -fx-border-width:0 0 1 0;");
        return topBar;
    }
}

