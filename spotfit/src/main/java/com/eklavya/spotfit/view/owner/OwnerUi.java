package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.Owner;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;

/**
 * Shared UI file for the Owner module.
 *
 * Contains:
 * - Owner colors/styles
 * - Navigation
 * - Sidebar
 * - Page shell
 * - Wizard shell
 * - Common UI controls
 */
public class OwnerUi {

    // =========================================================
    // COLORS
    // =========================================================

    public static final String BACKGROUND = "#FFF8F5";
    public static final String SURFACE = "#FFFFFF";
    public static final String SURFACE_LOW = "#FFF1EA";
    public static final String SURFACE_HIGH = "#FFE3D3";

    public static final String CARD_BORDER = "#D9C2B4";
    public static final String INPUT_BORDER = "#D8C3AF";

    public static final String PRIMARY = "#884600";
    public static final String PRIMARY_CONTAINER = "#A65E1B";
    public static final String PRIMARY_LIGHT = "#F8E6D4";
    public static final String ACCENT = "#C97A2B";

    public static final String TEXT_DARK = "#2E1503";
    public static final String TEXT_MAIN = "#2E1503";
    public static final String TEXT_MUTED = "#6B5A4A";

    public static final String ERROR = "#BA1A1A";
    public static final String SUCCESS = "#2E7D32";

    // =========================================================
    // HEADING
    // =========================================================

    public static Label heading(String text, double size) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size:" + size + "px; -fx-font-weight:700; -fx-text-fill:" + TEXT_DARK + ";");
        return label;
    }

    // =========================================================
    // BODY TEXT
    // =========================================================

    public static Label body(String text, double size, String color) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-font-size:" + size + "px; -fx-text-fill:" + color + ";");
        return label;
    }

    // =========================================================
    // FIELD LABEL
    // =========================================================

    public static Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:12px; -fx-font-weight:700; -fx-text-fill:" + TEXT_MUTED + ";");
        return label;
    }

    // =========================================================
    // BADGE
    // =========================================================

    public static Label badge(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:11px; -fx-font-weight:700; -fx-text-fill:" + PRIMARY_CONTAINER + ";"
                + " -fx-background-color:" + PRIMARY_LIGHT + "; -fx-background-radius:999; -fx-padding:6 14;");
        return label;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        String base = "-fx-background-color:" + PRIMARY_CONTAINER + "; -fx-text-fill:white; -fx-background-radius:14;"
                + " -fx-font-size:14px; -fx-font-weight:700; -fx-padding:14 26; -fx-cursor:hand;";
        button.setStyle(base);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnMouseEntered(e -> button.setStyle(base.replace(PRIMARY_CONTAINER, PRIMARY)));
        button.setOnMouseExited(e -> button.setStyle(base));
        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        String base = "-fx-background-color:white; -fx-text-fill:" + TEXT_DARK + "; -fx-background-radius:14;"
                + " -fx-border-radius:14; -fx-border-color:" + INPUT_BORDER + "; -fx-font-size:14px;"
                + " -fx-font-weight:700; -fx-padding:14 26; -fx-cursor:hand;";
        button.setStyle(base);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnMouseEntered(e -> button.setStyle(base.replace("white", SURFACE_LOW)));
        button.setOnMouseExited(e -> button.setStyle(base));
        return button;
    }

    public static Button dangerButton(String text) {
        Button button = new Button(text);
        String base = "-fx-background-color:" + ERROR + "; -fx-text-fill:white; -fx-background-radius:14;"
                + " -fx-font-size:14px; -fx-font-weight:700; -fx-padding:14 26; -fx-cursor:hand;";
        button.setStyle(base);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnMouseEntered(e -> button.setStyle(base.replace(ERROR, "#9A0000")));
        button.setOnMouseExited(e -> button.setStyle(base));
        return button;
    }

    // =========================================================
    // LINK BUTTON
    // =========================================================

    public static Button linkButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color:transparent; -fx-text-fill:" + PRIMARY_CONTAINER + "; -fx-font-size:13px;"
                + " -fx-font-weight:700; -fx-cursor:hand; -fx-underline:true; -fx-padding:0;");
        return button;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    public static TextField textField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(48);
        field.setStyle(inputStyle());
        return field;
    }

    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    public static PasswordField passwordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.setPrefHeight(48);
        field.setStyle(inputStyle());
        return field;
    }

    // =========================================================
    // TEXT AREA
    // =========================================================

    public static TextArea textArea(String placeholder, int rows) {
        TextArea area = new TextArea();
        area.setPromptText(placeholder);
        area.setPrefRowCount(rows);
        area.setWrapText(true);
        area.setStyle(inputStyle());
        return area;
    }

    public static <T> ComboBox<T> comboBox(String placeholder) {
        ComboBox<T> box = new ComboBox<>();
        box.setPromptText(placeholder);
        box.setPrefHeight(48);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(inputStyle());
        return box;
    }

    public static VBox fieldGroup(String labelText, Control inputControl) {
        Label label = body(labelText, 13, TEXT_DARK);
        label.setStyle("-fx-font-weight:600; -fx-text-fill:" + TEXT_DARK + ";");
        VBox box = new VBox(6, label, inputControl);
        return box;
    }

    // =========================================================
    // INPUT STYLE
    // =========================================================

    private static String inputStyle() {
        return "-fx-background-color:#FFFDFB; -fx-background-radius:14; -fx-border-radius:14;"
                + " -fx-border-color:" + INPUT_BORDER + "; -fx-padding:0 16; -fx-font-size:14px;"
                + " -fx-text-fill:" + TEXT_DARK + "; -fx-prompt-text-fill:" + TEXT_MUTED + ";";
    }

    // =========================================================
    // CARD
    // =========================================================

    public static VBox card() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:24; -fx-border-radius:24;"
                + " -fx-border-color:" + CARD_BORDER + "; -fx-effect:dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);");
        box.setCache(true);
        box.setCacheHint(CacheHint.SPEED);
        return box;
    }

    // =========================================================
    // IMAGE / PLACEHOLDER
    // =========================================================

    public static Node imageOrPlaceholder(String url, double width, double height, double radius) {
        if (url != null && !url.isBlank()) {
            try {
                Image image = new Image(url, width, height, false, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(width, height);
                clip.setArcWidth(radius * 2);
                clip.setArcHeight(radius * 2);
                imageView.setClip(clip);

                return imageView;
            } catch (Exception ignored) {
                // If image fails, placeholder will be shown.
            }
        }

        Region placeholder = new Region();
        placeholder.setPrefSize(width, height);
        placeholder.setStyle("-fx-background-color:" + SURFACE_HIGH + "; -fx-background-radius:" + radius + ";");
        return placeholder;
    }

    // =========================================================
    // SIZED SCENE HELPER
    // =========================================================

    /**
     * Builds a Scene that is already sized to match the current window.
     *
     * Every owner-module page used to call `new Scene(root)` with no
     * width/height. JavaFX then sizes that Scene to the ROOT'S PREFERRED
     * (small) size. When navigate() immediately swaps that undersized
     * Scene onto the already-maximized Stage, the window visibly SHRINKS
     * for a frame, then navigate() forces it back to full screen — this
     * is the shrink/expand "lag" seen when switching pages (e.g. after
     * "Continue as Space Owner").
     *
     * Using this helper everywhere instead of `new Scene(root)` means the
     * Scene already matches the Stage's current bounds, so swapping it in
     * causes no resize at all and no visible flicker.
     */
    public static Scene sizedScene(Parent root) {
        double w = 1280;
        double h = 800;

        if (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null
                && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getWidth() > 0
                && com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getHeight() > 0) {
            w = com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getWidth();
            h = com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getHeight();
        } else {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            w = bounds.getWidth();
            h = bounds.getHeight();
        }

        return new Scene(root, w, h);
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    /**
     * Changes the Scene using AppNavigator.
     *
     * Important:
     *
     * We do NOT call:
     *
     * stage.setMaximized(false);
     * stage.setMaximized(true);
     *
     * on every page.
     *
     * That could cause visible shrink-expand flashing.
     *
     * Instead:
     *
     * 1. Change the Scene.
     * 2. Check whether the Stage is already maximized and
     *    matches the available monitor size.
     * 3. Only restore the window bounds when needed.
     */
    public static void navigate(Scene scene) {
        if (scene == null) {
            return;
        }

        if (com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() == null) {
            System.out.println("Navigation Error: AppNavigator.getPrimaryStage() is null.");
            return;
        }

        // CHANGE PAGE
        com.eklavya.spotfit.navigation.AppNavigator.setScene(scene);

        // GET SCREEN BOUNDS
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        // CHECK CURRENT WINDOW SIZE
        boolean alreadyFullSize = com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().isMaximized()
                && Math.abs(com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getWidth() - bounds.getWidth()) < 2
                && Math.abs(com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getHeight() - bounds.getHeight()) < 2;

        // FIX WINDOW ONLY IF NECESSARY
        if (!alreadyFullSize) {
            com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().setX(bounds.getMinX());
            com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().setY(bounds.getMinY());
            com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().setWidth(bounds.getWidth());
            com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().setHeight(bounds.getHeight());
            com.eklavya.spotfit.navigation.AppNavigator.setMaximized(true);
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    public static VBox sidebar(String activeKey) {
        VBox root = new VBox();
        root.setPrefWidth(250);
        root.setMinWidth(250);
        root.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent " + CARD_BORDER
                + " transparent transparent; -fx-border-width:0 1 0 0;");

        // BRAND
        HBox brand = new HBox(8);
        brand.setPadding(new Insets(24, 20, 20, 20));
        brand.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("SpotFit Owner");
        logo.setStyle("-fx-font-size:20px; -fx-font-weight:800; -fx-text-fill:" + PRIMARY_CONTAINER + ";");
        brand.getChildren().add(logo);

        // NAV ITEMS
        VBox nav = new VBox(4);
        nav.setPadding(new Insets(4, 12, 12, 12));

        String[][] items = {
                {"Dashboard", "dashboard"},
                {"My Businesses", "businesses"},
                {"My Spaces", "myspaces"},
              //  {"Bookings", "bookings"},
                {"Complaints", "complaints"},
                {"Customers", "crm"},
                {"Analytics", "analytics"},
                {"Revenue & Payouts", "revenue"},
                {"Staff / Managers", "staff"},
                {"Reviews", "reviews"},
                {"Promotions", "promotions"},
                {"Notifications", "notifications"},
                {"My Profile", "profile"},
                {"Settings", "settings"},
                {"Help Center", "help"}
        };

        for (String[] item : items) {
            nav.getChildren().add(sidebarNavItem(item[0], item[1], item[1].equals(activeKey)));
        }

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // LOGOUT
        Label logout = new Label("  Log Out");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setPrefHeight(44);
        logout.setStyle("-fx-font-size:14px; -fx-font-weight:700; -fx-text-fill:" + ERROR + ";"
                + " -fx-padding:0 16; -fx-cursor:hand;");
        logout.setOnMouseClicked(e -> {
            com.eklavya.spotfit.session.AppSession.clearSession();
            OwnerFlowRouter.goToLogin();
        });

        VBox bottom = new VBox(logout);
        bottom.setPadding(new Insets(12));

        root.getChildren().addAll(brand, nav, spacer, bottom);
        return root;
    }

    // =========================================================
    // SIDEBAR ITEM
    // =========================================================

    private static Label sidebarNavItem(String text, String key, boolean active) {
        Label label = new Label("  " + text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPrefHeight(42);

        String base = "-fx-font-size:13.5px; -fx-font-weight:700; -fx-background-radius:12; -fx-padding:0 14; -fx-cursor:hand;";

        if (active) {
            label.setStyle(base + " -fx-background-color:" + PRIMARY_LIGHT + "; -fx-text-fill:" + PRIMARY_CONTAINER + ";");
        } else {
            label.setStyle(base + " -fx-text-fill:" + TEXT_MUTED + ";");
        }

        label.setOnMouseClicked(e -> PageRegistry.go(key));
        return label;
    }

    // =========================================================
    // PAGE SHELL
    // =========================================================

    public static Scene pageShell(String activeKey, String breadcrumb, String pageTitle, String pageSubtitle, Node content, Runnable onBack) {
        Label crumb = body(breadcrumb, 12.5, TEXT_MUTED);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        // BACK
        if (onBack != null) {
            Button backButton = secondaryButton("← Back");
            backButton.setOnAction(event -> onBack.run());
            topRow.getChildren().add(backButton);
        }

        topRow.getChildren().add(crumb);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // SPACE SCOPE SELECTOR
        ComboBox<String> spaceScopeCombo = new ComboBox<>();
        spaceScopeCombo.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-background-color:" + SURFACE + ";"
                + " -fx-border-color:" + CARD_BORDER + "; -fx-border-radius:8;");

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

            // Scope changed -> this page's cached scene is stale, drop it
            // so PageRegistry rebuilds fresh instead of reusing old data.
            PageRegistry.invalidate(activeKey);
            PageRegistry.go(activeKey);
        });

        // HEADER ICONS
        Label bell = new Label("🔔");
        Label gear = new Label("⚙");
        bell.setStyle("-fx-font-size:16px; -fx-cursor:hand;");
        gear.setStyle("-fx-font-size:16px; -fx-cursor:hand;");
        gear.setOnMouseClicked(e -> PageRegistry.go("settings"));

        // OWNER
        String ownerNameText = "Owner";
        if (com.eklavya.spotfit.session.AppSession.isAuthenticated()
                && com.eklavya.spotfit.session.AppSession.getName() != null
                && !com.eklavya.spotfit.session.AppSession.getName().isBlank()) {
            ownerNameText = com.eklavya.spotfit.session.AppSession.getName();
        } else if (com.eklavya.spotfit.session.AppSession.getEmail() != null
                && !com.eklavya.spotfit.session.AppSession.getEmail().isBlank()) {
            ownerNameText = com.eklavya.spotfit.session.AppSession.getEmail().split("@")[0];
        }

        Label avatar = new Label(OwnerShell.getInitials(ownerNameText));
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(34, 34);
        avatar.setStyle("-fx-background-color:" + PRIMARY_LIGHT + "; -fx-background-radius:999; -fx-font-weight:bold;"
                + " -fx-text-fill:" + PRIMARY + "; -fx-cursor:hand;");

        Label ownerName = body(ownerNameText + "  ▾", 13, TEXT_DARK);
        ownerName.setStyle(ownerName.getStyle() + " -fx-cursor:hand; -fx-font-weight:bold;");

        avatar.setOnMouseClicked(e -> PageRegistry.go("profile"));
        ownerName.setOnMouseClicked(e -> PageRegistry.go("profile"));

        HBox profile = new HBox(8, avatar, ownerName);
        profile.setAlignment(Pos.CENTER_LEFT);

        HBox rightIcons = new HBox(16, spaceScopeCombo, bell, gear, profile);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        topRow.getChildren().addAll(spacer, rightIcons);
        topRow.setSpacing(12);

        // TOP BAR
        VBox topBar = new VBox(topRow);
        topBar.setPadding(new Insets(18, 32, 12, 32));
        topBar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent transparent " + CARD_BORDER
                + " transparent; -fx-border-width:0 0 1 0;");

        // PAGE TITLE
        Label title = heading(pageTitle, 26);
        VBox titleBox = new VBox(4, title);

        if (pageSubtitle != null) {
            titleBox.getChildren().add(body(pageSubtitle, 13, TEXT_MUTED));
        }

        titleBox.setPadding(new Insets(24, 32, 8, 32));

        // PAGE CONTENT
        VBox page = new VBox(titleBox, content);
        page.setPadding(new Insets(0, 32, 32, 32));

        ScrollPane scroller = new ScrollPane(page);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + BACKGROUND + ";");

        VBox center = new VBox(topBar, scroller);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        center.setStyle("-fx-background-color:" + BACKGROUND + ";");
        HBox.setHgrow(center, Priority.ALWAYS);

        HBox root = new HBox(sidebar(activeKey), center);
        return sizedScene(root);
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    public static VBox statCard(String label, String value, String trend) {
        VBox card = card();
        card.setPrefWidth(220);

        Label valueLabel = heading(value, 24);
        Label labelText = body(label, 12, TEXT_MUTED);
        card.getChildren().addAll(labelText,valueLabel);

        if (trend != null) {
            Label trendLabel = body(trend, 11.5, SUCCESS);
            card.getChildren().add(trendLabel);
        }

        return card;
    }

    // =========================================================
    // WIZARD STEPS
    // =========================================================

    public static final String[] WIZARD_STEP_NAMES = {
            "Category Selection",
            "Space Information",
            "Amenities & Facilities",
            "Gallery Upload",
            "Pricing & Availability",
            "Documents & Verification",
            "Review & Submit"
    };

    public static final String[] V2_WIZARD_STEP_NAMES = {
            "Business & Classification",
            "Space Information & Location",
            "Booking Type & Pricing",
            "Booking Configuration",
            "Operations",
            "Review & Create"
    };

    // =========================================================
    // WIZARD SHELL
    // =========================================================

    public static HBox wizardShell(int stepIndex, Node formContent, Node sidePanel) {
        Label breadcrumb = body("Dashboard  ›  My Spaces  ›  Create New Space", 12.5, TEXT_MUTED);

        HBox topBar = new HBox(breadcrumb);
        topBar.setPadding(new Insets(20, 32, 12, 32));
        topBar.setStyle("-fx-background-color:" + SURFACE + "; -fx-border-color:transparent transparent " + CARD_BORDER
                + " transparent; -fx-border-width:0 0 1 0;");

        // STEP
        Label stepBadge = badge("STEP " + (stepIndex + 1) + " OF 7");
        Label stepTitle = heading(WIZARD_STEP_NAMES[stepIndex], 26);

        HBox header = new HBox(12, stepBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        // LEFT COLUMN
        VBox mainColumn = new VBox(10, header, stepTitle, formContent);
        mainColumn.setPadding(new Insets(28, 20, 28, 32));
        HBox.setHgrow(mainColumn, Priority.ALWAYS);

        // RIGHT COLUMN
        VBox rightColumn = new VBox(20, sidePanel);
        rightColumn.setPadding(new Insets(28, 32, 28, 20));
        rightColumn.setPrefWidth(320);
        rightColumn.setMinWidth(320);

        HBox body = new HBox(mainColumn, rightColumn);

        // SCROLL
        ScrollPane scroller = new ScrollPane(body);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + BACKGROUND + ";");

        VBox center = new VBox(topBar, scroller);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        center.setStyle("-fx-background-color:" + BACKGROUND + ";");
        HBox.setHgrow(center, Priority.ALWAYS);

        return new HBox(sidebar("myspaces"), center);
    }

    // =========================================================
    // WIZARD PROGRESS
    // =========================================================

    public static VBox wizardProgressCard(int activeIndex) {
        Label title = heading("Space Setup Progress", 16);
        VBox list = new VBox(10);

        for (int i = 0; i < WIZARD_STEP_NAMES.length; i++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            String mark;
            if (i < activeIndex) {
                mark = "✓";
            } else if (i == activeIndex) {
                mark = "●";
            } else {
                mark = "○";
            }

            String color = i <= activeIndex ? ACCENT : TEXT_MUTED;

            Label dot = new Label(mark);
            dot.setStyle("-fx-text-fill:" + color + "; -fx-font-size:12px; -fx-font-weight:800;");

            Label text = body(WIZARD_STEP_NAMES[i], 12.5, i == activeIndex ? TEXT_DARK : TEXT_MUTED);

            row.getChildren().addAll(dot, text);
            list.getChildren().add(row);
        }

        VBox card = card();
        card.getChildren().addAll(title, list);
        return card;
    }

    public static VBox v2WizardProgressCard(int activeIndex) {
        Label title = heading("V2 Space Setup Progress", 16);
        VBox list = new VBox(10);

        for (int i = 0; i < V2_WIZARD_STEP_NAMES.length; i++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            String mark;
            if (i < activeIndex) {
                mark = "✓";
            } else if (i == activeIndex) {
                mark = "●";
            } else {
                mark = "○";
            }

            String color = i <= activeIndex ? ACCENT : TEXT_MUTED;

            Label dot = new Label(mark);
            dot.setStyle("-fx-text-fill:" + color + "; -fx-font-size:12px; -fx-font-weight:800;");

            Label text = body(V2_WIZARD_STEP_NAMES[i], 12.5, i == activeIndex ? TEXT_DARK : TEXT_MUTED);

            row.getChildren().addAll(dot, text);
            list.getChildren().add(row);
        }

        VBox card = card();
        card.getChildren().addAll(title, list);
        return card;
    }

    // =========================================================
    // SCROLL WRAPPER (used by OwnerShell.java and by content pages
    // that swap rightPane's center directly for a drill-down page,
    // e.g. BookingManagementPage -> BookingDetailsPage)
    // =========================================================

    public static ScrollPane scroll(Node content) {
        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + BACKGROUND + ";");
        if (content instanceof Region) {
            ((Region) content).setMaxWidth(Double.MAX_VALUE);
        }
        return scroller;
    }
}
