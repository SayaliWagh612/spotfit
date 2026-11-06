package com.eklavya.spotfit.view.user;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.geometry.Side;

import com.eklavya.spotfit.Main;
import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.view.user.UserFlowRouter;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class UserDash {

        // ============================================================
        // IMAGE PATHS
        // ============================================================

        // Recommendation images
        private static final String STUDIO_IMAGE = "/images/UserDashImg/Studio.png";

        private static final String PODCAST_IMAGE = "/images/UserDashImg/Podcast.png";

        private static final String PHOTOGRAPHY_IMAGE = "/images/UserDashImg/Photography.png";

        // Domain tiles shown in "Explore by activity"
        private static final String CREATIVITY_IMAGE = "/images/UserDashImg/Creativity.png";

        private static final String SPORTS_IMAGE = "/images/UserDashImg/Sports.png";

        private static final String WELLNESS_IMAGE = "/images/UserDashImg/Wellness.png";

        private static final String WORKSPACES_IMAGE = "/images/UserDashImg/Workplaces.png";

        private static final String RHYTHM_IMAGE = "/images/UserDashImg/Rhythm.png";

        // Fallback image
        private static final String FALLBACK_IMAGE = "/images/UserDashImg/Sports.png";

        // ============================================================
        // MAIN SCENE
        // ============================================================

        public static Scene getUserDashScene() {

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                HBox navigation = buildTopNavigation();

                VBox mainContent = buildMainContent();

                ScrollPane scrollPane = new ScrollPane(mainContent);

                scrollPane.setFitToWidth(true);

                scrollPane.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scrollPane.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scrollPane.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                root.setTop(navigation);

                root.setCenter(scrollPane);

                Scene scene = new Scene(root, 1280, 800);

                updateResponsiveLayout(
                                scene,
                                mainContent,
                                navigation);

                scene.widthProperty().addListener(
                                (obs, oldValue, newValue) -> updateResponsiveLayout(
                                                scene,
                                                mainContent,
                                                navigation));

                return scene;
        }

        // ============================================================
        // RESPONSIVE LAYOUT
        // ============================================================

        private static void updateResponsiveLayout(
                        Scene scene,
                        VBox mainContent,
                        HBox navigation) {

                double width = scene.getWidth();

                if (width < 750) {

                        mainContent.setPadding(
                                        new Insets(24, 20, 35, 20));

                        navigation.setPadding(
                                        new Insets(10, 18, 10, 18));

                } else if (width < 1050) {

                        mainContent.setPadding(
                                        new Insets(28, 30, 40, 30));

                        navigation.setPadding(
                                        new Insets(11, 28, 11, 28));

                } else {

                        mainContent.setPadding(
                                        new Insets(32, 48, 45, 48));

                        navigation.setPadding(
                                        new Insets(12, 42, 12, 42));
                }
        }

        // ============================================================
        // TOP NAVIGATION
        // ============================================================

        private static HBox buildTopNavigation() {

                HBox navigation = new HBox(20);

                navigation.setAlignment(
                                Pos.CENTER_LEFT);

                navigation.setMinHeight(64);

                navigation.setPrefHeight(64);

                navigation.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-width: 0 0 1 0;");

                HBox brand = buildBrand();

                HBox.setHgrow(
                                brand,
                                Priority.NEVER);

                HBox navLinks = new HBox(6);

                navLinks.setAlignment(
                                Pos.CENTER);

                Button home = navButton(
                                "Home",
                                true);

                Button explore = navButton(
                                "Explore",
                                false);

                explore.setOnAction(
                                e -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                Explore.getExploreScene(
                                                                () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                .setScene(
                                                                                                UserDash.getUserDashScene()))));

                Button bookings = navButton(
                                "My Bookings",
                                false);

                bookings.setOnAction(e -> {
                        if (!com.eklavya.spotfit.session.AppSession.isAuthenticated()) {
                                com.eklavya.spotfit.view.user.CustomerLoginPrompt.show(
                                                "Sign in to view your bookings",
                                                () -> com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin());
                                return;
                        }
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        MyBookings.getMyBookingsScene(
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(
                                                                                        UserDash.getUserDashScene())));
                });

                Button aboutUs = navButton(
                                "About Us",
                                false);

                aboutUs.setOnAction(e -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                new com.eklavya.spotfit.view.common.AboutUs().getAboutUsScene(
                                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                                UserDash.getUserDashScene()))));

                navLinks.getChildren().addAll(
                                home,
                                explore,
                                bookings,
                                aboutUs);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                HBox actions = new HBox(8);

                actions.setAlignment(
                                Pos.CENTER_RIGHT);

                Button saved = roundActionButton("♡");

                saved.setOnAction(e -> {
                        if (!com.eklavya.spotfit.session.AppSession.isAuthenticated()) {
                                com.eklavya.spotfit.view.user.CustomerLoginPrompt.show(
                                                "Sign in to view your saved spaces",
                                                () -> com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin());
                                return;
                        }
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        com.eklavya.spotfit.view.user.SavedSpaces.getSavedSpacesScene(
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(
                                                                                        UserDash.getUserDashScene())));
                });

                boolean isGuest = !com.eklavya.spotfit.session.AppSession.isAuthenticated();
                String userName = isGuest ? "Guest" : com.eklavya.spotfit.session.AppSession.getName();

                Button profile = new Button(userName);

                profile.setPrefHeight(36);

                profile.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 18;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 0 15;" +
                                                "-fx-cursor: hand;");

                Button authAction = new Button(isGuest ? "Sign In" : "Logout");

                authAction.setPrefHeight(36);

                authAction.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 18;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-padding: 0 13;" +
                                                "-fx-cursor: hand;");

                if (isGuest) {
                        authAction.setOnAction(e -> {
                                com.eklavya.spotfit.view.user.CustomerLoginPrompt.show(
                                                "Sign in to access your SpotFit account",
                                                () -> com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin());
                        });
                } else {
                        authAction.setOnAction(
                                        e -> showLogoutConfirmation());
                }

                actions.getChildren().addAll(
                                saved,
                                profile,
                                authAction);

                navigation.getChildren().addAll(
                                brand,
                                navLinks,
                                spacer,
                                actions);

                return navigation;
        }

        // ============================================================
        // BRAND
        // ============================================================

        private static HBox buildBrand() {

                HBox brand = new HBox(8);
                brand.setAlignment(Pos.CENTER_LEFT);

                ImageView logoView = new ImageView(
                                new Image(UserDash.class.getResourceAsStream("/images/logo.png")));

                logoView.setFitWidth(30);
                logoView.setFitHeight(30);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);

                Label name = new Label("SpotFit");
                name.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 20px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + PRIMARY + ";");

                brand.getChildren().addAll(logoView, name);

                return brand;
        }

        // ============================================================
        // NAV BUTTON
        // ============================================================

        private static Button navButton(
                        String text,
                        boolean active) {

                Button button = new Button(text);

                button.setPrefHeight(38);

                applyNavButtonStyle(
                                button,
                                active);

                button.setOnMouseEntered(
                                e -> {

                                        if (!active) {

                                                button.setStyle(
                                                                "-fx-background-color: " + PILL_BG + ";" +
                                                                                "-fx-background-radius: 19;" +
                                                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                                                "-fx-font-family: " + FONT + ";" +
                                                                                "-fx-font-size: 11px;" +
                                                                                "-fx-font-weight: bold;" +
                                                                                "-fx-padding: 0 15;" +
                                                                                "-fx-cursor: hand;");
                                        }
                                });

                button.setOnMouseExited(
                                e -> applyNavButtonStyle(
                                                button,
                                                active));

                return button;
        }

        // ============================================================
        // NAV BUTTON STYLE
        // ============================================================

        private static void applyNavButtonStyle(
                        Button button,
                        boolean active) {

                if (active) {

                        button.setStyle(
                                        "-fx-background-color: " + PILL_BG + ";" +
                                                        "-fx-background-radius: 19;" +
                                                        "-fx-text-fill: " + PRIMARY + ";" +
                                                        "-fx-font-family: " + FONT + ";" +
                                                        "-fx-font-size: 11px;" +
                                                        "-fx-font-weight: bold;" +
                                                        "-fx-padding: 0 15;" +
                                                        "-fx-cursor: hand;");

                } else {

                        button.setStyle(
                                        "-fx-background-color: transparent;" +
                                                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                        "-fx-font-family: " + FONT + ";" +
                                                        "-fx-font-size: 11px;" +
                                                        "-fx-padding: 0 15;" +
                                                        "-fx-cursor: hand;");
                }
        }

        // ============================================================
        // ROUND ACTION BUTTON
        // ============================================================

        private static Button roundActionButton(
                        String symbol) {

                Button button = new Button(symbol);

                button.setPrefSize(
                                36,
                                36);

                button.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background-radius: 18;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-size: 17px;" +
                                                "-fx-cursor: hand;");

                button.setOnMouseEntered(
                                e -> button.setStyle(
                                                "-fx-background-color: " + PILL_BG + ";" +
                                                                "-fx-background-radius: 18;" +
                                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                                "-fx-font-size: 17px;" +
                                                                "-fx-cursor: hand;"));

                button.setOnMouseExited(
                                e -> button.setStyle(
                                                "-fx-background-color: transparent;" +
                                                                "-fx-background-radius: 18;" +
                                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                                "-fx-font-size: 17px;" +
                                                                "-fx-cursor: hand;"));

                return button;
        }

        // ============================================================
        // LOGOUT
        // ============================================================

        private static void showLogoutConfirmation() {
                String accountName = com.eklavya.spotfit.session.AppSession.getName();
                boolean confirmed = CustomerBookingDialogs.confirmLogout(accountName);

                if (confirmed) {
                        com.eklavya.spotfit.session.AppSession.clearSession();
                        com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin();
                }
        }
        // ============================================================
        // MAIN CONTENT
        // ============================================================

        private static VBox buildMainContent() {

                VBox main = new VBox(34);

                main.setFillWidth(true);

                main.getChildren().addAll(
                                buildWelcomeSection(),
                                buildSearchHero(),
                                buildQuickCategories(),
                                buildRecommendedSection(),
                                buildUpcomingBooking());

                return main;
        }

        // ============================================================
        // WELCOME SECTION
        // ============================================================

        private static VBox buildWelcomeSection() {

                VBox section = new VBox(5);

                String userName = com.eklavya.spotfit.session.AppSession.getName();
                if (userName == null || userName.isBlank()) {
                        userName = "Guest";
                }

                Label greeting = new Label(
                                "Welcome, " + userName);

                greeting.setStyle(
                                heading(30));

                Label subtitle = new Label(
                                "Find a space that fits what you want to do.");

                subtitle.setStyle(
                                body(14));

                section.getChildren().addAll(
                                greeting,
                                subtitle);

                return section;
        }

        // ============================================================
        // SEARCH HERO
        // ============================================================

        private static StackPane buildSearchHero() {

                StackPane hero = new StackPane();
                hero.setMinHeight(190);
                hero.setPrefHeight(205);
                hero.setMaxWidth(Double.MAX_VALUE);

                javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
                                0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                                new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.web(PRIMARY)),
                                new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.web(PRIMARY).darker()));

                hero.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(22), Insets.EMPTY)));
                addShadow(hero, 0.12);

                VBox content = new VBox(15);
                content.setAlignment(Pos.CENTER_LEFT);
                content.setPadding(new Insets(28, 34, 28, 34));

                Label title = new Label("What do you want to do today?");
                title.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");

                Label subtitle = new Label("Search by activity, space, or experience.");
                subtitle.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.82);");

                HBox searchBox = new HBox(10);
                searchBox.setAlignment(Pos.CENTER_LEFT);
                searchBox.setMaxWidth(720);

                TextField searchField = new TextField();
                searchField.setPromptText("Search activities, spaces, locations or amenities...");
                searchField.setPrefHeight(48);
                searchField.setStyle(
                                "-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: transparent; -fx-font-family: "
                                                + FONT + "; -fx-font-size: 12px; -fx-padding: 0 16px;");
                HBox.setHgrow(searchField, Priority.ALWAYS);

                Button search = new Button("Search");
                search.setPrefHeight(48);
                search.setPrefWidth(100);
                search.setStyle("-fx-background-color: " + TEXT_DARK
                                + "; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-family: " + FONT
                                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");

                search.setOnMouseEntered(e -> search.setStyle("-fx-background-color: derive(" + TEXT_DARK
                                + ", 15%); -fx-background-radius: 12; -fx-text-fill: white; -fx-font-family: " + FONT
                                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;"));
                search.setOnMouseExited(e -> search.setStyle("-fx-background-color: " + TEXT_DARK
                                + "; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-family: " + FONT
                                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;"));

                Runnable doSearch = () -> {
                        String query = searchField.getText().trim();
                        if (query.isEmpty()) {
                                searchField.requestFocus();
                                searchField.setPromptText("Type something to search...");
                                return;
                        }
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(SearchResults.getSearchResultsScene(query,
                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                        .setScene(getUserDashScene())));
                };

                search.setOnAction(e -> doSearch.run());
                searchField.setOnAction(e -> doSearch.run());

                // ============================================================
                // THEMED SEARCH SUGGESTIONS
                // ============================================================

                ContextMenu suggestionsMenu = new ContextMenu();
                suggestionsMenu.setId("spotfit-search-suggestions");
                suggestionsMenu.setAutoHide(true);
                suggestionsMenu.setHideOnEscape(true);

                suggestionsMenu.setOnShowing(event -> {
                        var stylesheet = UserDash.class.getResource(
                                        "/styles/customer-search-suggestions.css");

                        if (stylesheet != null) {
                                String stylesheetUrl = stylesheet.toExternalForm();

                                if (!suggestionsMenu.getScene()
                                                .getStylesheets()
                                                .contains(stylesheetUrl)) {

                                        suggestionsMenu.getScene()
                                                        .getStylesheets()
                                                        .add(stylesheetUrl);
                                }
                        }
                });

                javafx.animation.PauseTransition debounce = new javafx.animation.PauseTransition(
                                javafx.util.Duration.millis(300));

                searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                        debounce.stop();

                        if (newValue == null || newValue.trim().isEmpty()) {
                                suggestionsMenu.hide();
                                suggestionsMenu.getItems().clear();
                                return;
                        }

                        debounce.setOnFinished(event -> {
                                java.util.List<com.eklavya.spotfit.model.search.SearchNavigationMatch> suggestions = com.eklavya.spotfit.model.search.CustomerSearchCatalog
                                                .getSuggestions(newValue);

                                suggestionsMenu.getItems().clear();

                                if (suggestions.isEmpty() || !searchField.isFocused()) {
                                        suggestionsMenu.hide();
                                        return;
                                }

                                double suggestionWidth = Math.max(
                                                320,
                                                searchField.getWidth() - 34);

                                for (com.eklavya.spotfit.model.search.SearchNavigationMatch match : suggestions) {

                                        HBox graphic = createSearchSuggestionGraphic(
                                                        match,
                                                        suggestionWidth);

                                        MenuItem item = new MenuItem();
                                        item.setGraphic(graphic);
                                        item.getStyleClass().add("spotfit-suggestion-item");

                                        item.setOnAction(actionEvent -> {
                                                debounce.stop();
                                                suggestionsMenu.hide();

                                                searchField.setText(match.getDisplayLabel());
                                                doSearch.run();
                                        });

                                        suggestionsMenu.getItems().add(item);
                                }

                                if (!suggestionsMenu.getItems().isEmpty()
                                                && searchField.getScene() != null
                                                && searchField.getScene().getWindow() != null) {

                                        if (suggestionsMenu.isShowing()) {
                                                suggestionsMenu.hide();
                                        }

                                        suggestionsMenu.show(
                                                        searchField,
                                                        Side.BOTTOM,
                                                        0,
                                                        7);
                                }
                        });

                        debounce.playFromStart();
                });

                searchField.focusedProperty().addListener(
                                (obs, wasFocused, isFocused) -> {

                                        if (!isFocused && !suggestionsMenu.isShowing()) {
                                                debounce.stop();
                                        }
                                });

                searchBox.getChildren().addAll(searchField, search);
                content.getChildren().addAll(title, subtitle, searchBox);
                StackPane.setAlignment(content, Pos.CENTER_LEFT);
                hero.getChildren().add(content);

                return hero;
        }

        private static HBox createSearchSuggestionGraphic(
                        com.eklavya.spotfit.model.search.SearchNavigationMatch match,
                        double preferredWidth) {

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPrefWidth(preferredWidth);
                row.setMinHeight(54);
                row.getStyleClass().add("spotfit-suggestion-content");

                Label title = new Label(match.getDisplayLabel());
                title.getStyleClass().add("spotfit-suggestion-title");

                Label type = new Label(getSearchSuggestionType(match));
                type.getStyleClass().add("spotfit-suggestion-type");

                VBox textContent = new VBox(3, title, type);
                textContent.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(textContent, Priority.ALWAYS);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label arrow = new Label("›");
                arrow.getStyleClass().add("spotfit-suggestion-arrow");

                row.getChildren().addAll(textContent, spacer);

                if (match.isPrototype()) {
                        Label prototypeBadge = new Label("PROTOTYPE");
                        prototypeBadge.getStyleClass().add(
                                        "spotfit-suggestion-prototype");

                        row.getChildren().add(prototypeBadge);
                }

                row.getChildren().add(arrow);

                return row;
        }

        private static String getSearchSuggestionType(
                        com.eklavya.spotfit.model.search.SearchNavigationMatch match) {

                return switch (match.getResultType()) {
                        case DOMAIN -> match.isPrototype()
                                        ? "Explore domain"
                                        : "Explore domain";

                        case ACTIVITY_GROUP -> "Browse activity group";

                        case ACTIVITY -> "Find available spaces";
                };
        }

        private static String getSearchSuggestionIcon(
                        com.eklavya.spotfit.model.search.SearchNavigationMatch match) {

                return switch (match.getResultType()) {
                        case DOMAIN -> "◆";
                        case ACTIVITY_GROUP -> "●";
                        case ACTIVITY -> "↗";
                };
        }

        // ============================================================
        // SEARCH MESSAGE
        // ============================================================

        private static void showSearchMessage(
                        String query) {

                if (query == null ||
                                query.trim().isEmpty()) {

                        return;
                }

                Alert alert = new Alert(
                                Alert.AlertType.INFORMATION);

                alert.setTitle("Search");

                alert.setHeaderText(
                                "Searching SpotFit");

                alert.setContentText(
                                "Looking for: " +
                                                query.trim());

                alert.showAndWait();
        }

        // ============================================================
        // QUICK CATEGORIES
        // ============================================================

        private static VBox buildQuickCategories() {

                VBox section = new VBox(14);

                HBox headingRow = sectionHeadingRow(
                                "Explore by activity",
                                "See all");

                HBox cards = new HBox(14);

                cards.setAlignment(
                                Pos.CENTER_LEFT);

                cards.setFillHeight(true);

                StackPane creativity = domainCard(
                                CREATIVITY_IMAGE,
                                "Creativity",
                                "Art, music, podcasts & more");

                StackPane sports = domainCard(
                                SPORTS_IMAGE,
                                "Sports",
                                "Turfs, courts & gaming zones");

                StackPane wellness = domainCard(
                                WELLNESS_IMAGE,
                                "Wellness",
                                "Yoga, gyms & meditation");

                StackPane workspaces = domainCard(
                                WORKSPACES_IMAGE,
                                "Workspaces",
                                "Study spots & co-working");

                for (StackPane card : new StackPane[] {
                                creativity,
                                sports,
                                wellness,
                                workspaces }) {

                        HBox.setHgrow(
                                        card,
                                        Priority.ALWAYS);
                }

                creativity.setOnMouseClicked(e -> {
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        DomainPage.getDomainScene("Creativity",
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(UserDash.getUserDashScene())));
                });

                sports.setOnMouseClicked(e -> {
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        DomainPage.getDomainScene("Sports",
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(UserDash.getUserDashScene())));
                });

                wellness.setOnMouseClicked(e -> {
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        DomainPage.getDomainScene("Wellness",
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(UserDash.getUserDashScene())));
                });

                workspaces.setOnMouseClicked(e -> {
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                        DomainPage.getDomainScene("Workspaces",
                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                        .setScene(UserDash.getUserDashScene())));
                });

                cards.getChildren().addAll(
                                creativity,
                                sports,
                                wellness,
                                workspaces);

                section.getChildren().addAll(
                                headingRow,
                                cards);

                return section;
        }

        // ============================================================
        // DOMAIN CARD
        // ============================================================

        private static StackPane domainCard(
                        String imagePath,
                        String title,
                        String subtitle) {

                double cardHeight = 200;

                StackPane card = new StackPane();

                card.setMinWidth(150);

                card.setPrefWidth(220);

                card.setMaxWidth(
                                Double.MAX_VALUE);

                card.setPrefHeight(cardHeight);

                card.setMinHeight(cardHeight);

                card.setStyle(
                                "-fx-cursor: hand;" +
                                                "-fx-background-radius: 18;");

                ImageView image = new ImageView(
                                loadImage(imagePath));

                image.setPreserveRatio(false);

                image.setFitHeight(cardHeight);

                image.fitWidthProperty().bind(
                                card.widthProperty());

                Rectangle clip = new Rectangle();

                clip.setArcWidth(18);

                clip.setArcHeight(18);

                clip.widthProperty().bind(
                                card.widthProperty());

                clip.setHeight(cardHeight);

                image.setClip(clip);

                Region overlay = new Region();

                overlay.setPrefHeight(cardHeight);

                overlay.setBackground(
                                new Background(
                                                new BackgroundFill(
                                                                new LinearGradient(
                                                                                0,
                                                                                0,
                                                                                0,
                                                                                1,
                                                                                true,
                                                                                CycleMethod.NO_CYCLE,
                                                                                new Stop(
                                                                                                0,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0)),
                                                                                new Stop(
                                                                                                0.55,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0)),
                                                                                new Stop(
                                                                                                1,
                                                                                                Color.rgb(
                                                                                                                0,
                                                                                                                0,
                                                                                                                0,
                                                                                                                0.72))),
                                                                new CornerRadii(18),
                                                                Insets.EMPTY)));

                VBox textBox = new VBox(3);

                textBox.setAlignment(
                                Pos.BOTTOM_LEFT);

                textBox.setPadding(
                                new Insets(
                                                0,
                                                16,
                                                16,
                                                16));

                Label titleLabel = new Label(title);

                titleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 24px;" +
                                                "-fx-font-weight: 800;" +
                                                "-fx-text-fill: white;");

                Label subtitleLabel = new Label(subtitle);

                subtitleLabel.setWrapText(true);

                subtitleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-text-fill: rgba(255,255,255,0.85);");

                textBox.getChildren().addAll(
                                titleLabel,
                                subtitleLabel);

                StackPane.setAlignment(
                                textBox,
                                Pos.BOTTOM_LEFT);

                card.getChildren().addAll(
                                image,
                                overlay,
                                textBox);

                addShadow(
                                card,
                                0.10);

                card.setOnMouseEntered(
                                e -> liftCard(
                                                card,
                                                true));

                card.setOnMouseExited(
                                e -> liftCard(
                                                card,
                                                false));

                return card;
        }

        // ============================================================
        // RECOMMENDED SECTION
        // ============================================================

        private static VBox buildRecommendedSection() {

                VBox section = new VBox(15);

                HBox header = sectionHeader(
                                "Recommended for you",
                                "Based on your interests");

                HBox cards = new HBox(17);

                cards.setAlignment(
                                Pos.CENTER_LEFT);

                cards.setFillHeight(true);

                cards.getChildren().addAll(

                                spaceCard(
                                                STUDIO_IMAGE,
                                                "Creator Studio",
                                                "Content Creation",
                                                "Baner",
                                                "2.4 km",
                                                "4.8",
                                                "124",
                                                "₹800 / hour",
                                                "Available"),

                                spaceCard(
                                                RHYTHM_IMAGE,
                                                "Rhythm House",
                                                "Music",
                                                "Kothrud",
                                                "3.1 km",
                                                "4.7",
                                                "98",
                                                "₹600 / hour",
                                                "Available"),

                                spaceCard(
                                                PHOTOGRAPHY_IMAGE,
                                                "Photography Studio",
                                                "Photography",
                                                "Aundh",
                                                "4.2 km",
                                                "4.9",
                                                "76",
                                                "₹1,000 / hour",
                                                "Available"));

                section.getChildren().addAll(
                                header,
                                cards);

                return section;
        }

        // ============================================================
        // RECOMMENDATION SECTION HEADER
        // ============================================================

        private static HBox sectionHeader(
                        String title,
                        String subtitle) {

                HBox header = new HBox();

                header.setAlignment(
                                Pos.CENTER_LEFT);

                VBox text = new VBox(3);

                Label titleLabel = new Label(
                                title);

                titleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 17px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label subtitleLabel = new Label(
                                subtitle);

                subtitleLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                text.getChildren().addAll(
                                titleLabel,
                                subtitleLabel);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Button seeAll = new Button(
                                "See all →");

                seeAll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-cursor: hand;");

                header.getChildren().addAll(
                                text,
                                spacer,
                                seeAll);

                return header;
        }

        // ============================================================
        // RECOMMENDATION SPACE CARD
        // ============================================================

        private static VBox spaceCard(
                        String imagePath,
                        String title,
                        String activity,
                        String location,
                        String distance,
                        String rating,
                        String reviews,
                        String price,
                        String availability) {

                VBox card = new VBox();

                card.setPrefWidth(270);

                card.setMinWidth(250);

                card.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 16;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 16;" +
                                                "-fx-cursor: hand;");

                // ========================================================
                // IMAGE AREA
                // ========================================================

                StackPane imageArea = new StackPane();

                imageArea.setPrefHeight(145);

                imageArea.setMinHeight(145);

                ImageView recommendationImage = new ImageView(
                                loadImage(imagePath));

                recommendationImage.setFitWidth(270);

                recommendationImage.setFitHeight(145);

                recommendationImage.setPreserveRatio(false);

                Rectangle imageClip = new Rectangle(
                                270,
                                145);

                imageClip.setArcWidth(22);

                imageClip.setArcHeight(22);

                recommendationImage.setClip(
                                imageClip);

                // ========================================================
                // FAVORITE BUTTON
                // ========================================================

                Button favorite = new Button("♡");

                favorite.setPrefSize(35, 35);

                favorite.setStyle("-fx-background-color: " + CARD_WHITE
                                + "; -fx-background-radius: 20; -fx-font-size: 18px; -fx-text-fill: " + TEXT_MUTED
                                + "; -fx-cursor: default;");
                favorite.setDisable(true);
                javafx.scene.control.Tooltip t = new javafx.scene.control.Tooltip("Not eligible for saving");
                t.setStyle("-fx-font-size: 12px;");
                favorite.setTooltip(t);

                StackPane.setAlignment(
                                favorite,
                                Pos.TOP_RIGHT);

                StackPane.setMargin(
                                favorite,
                                new Insets(10));

                imageArea.getChildren().addAll(
                                recommendationImage,
                                favorite);

                // ========================================================
                // CARD INFORMATION
                // ========================================================

                VBox info = new VBox(8);

                info.setPadding(
                                new Insets(14));

                // ========================================================
                // NAME
                // ========================================================

                Label name = new Label(
                                title);

                name.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 14px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                // ========================================================
                // RATING
                // ========================================================

                HBox ratingRow = new HBox(5);

                ratingRow.setAlignment(
                                Pos.CENTER_LEFT);

                Label star = new Label(
                                "★");

                star.setStyle(
                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-size: 11px;");

                Label ratingText = new Label(
                                rating + "  ·  " +
                                                reviews +
                                                " reviews");

                ratingText.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                ratingRow.getChildren().addAll(
                                star,
                                ratingText);

                // ========================================================
                // LOCATION
                // ========================================================

                Label locationLabel = new Label(
                                "📍 " +
                                                location +
                                                "  ·  " +
                                                distance);

                locationLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                // ========================================================
                // ACTIVITY PILL
                // ========================================================

                Label activityLabel = new Label(
                                activity);

                activityLabel.setPadding(
                                new Insets(
                                                5,
                                                9,
                                                5,
                                                9));

                activityLabel.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 8;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 9px;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                // ========================================================
                // BOTTOM ROW
                // ========================================================

                HBox bottom = new HBox();

                bottom.setAlignment(
                                Pos.CENTER_LEFT);

                Label priceLabel = new Label(
                                price);

                priceLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + PRIMARY + ";");

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Label availabilityLabel = new Label(
                                "● " +
                                                availability);

                availabilityLabel.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 9px;" +
                                                "-fx-text-fill: " + PRIMARY + ";");

                bottom.getChildren().addAll(
                                priceLabel,
                                spacer,
                                availabilityLabel);

                // ========================================================
                // ADD INFORMATION
                // ========================================================

                info.getChildren().addAll(
                                name,
                                ratingRow,
                                locationLabel,
                                activityLabel,
                                bottom);

                card.getChildren().addAll(
                                imageArea,
                                info);

                // ========================================================
                // HOVER EFFECT
                // ========================================================

                card.setOnMouseEntered(
                                e -> {

                                        card.setTranslateY(-4);

                                        card.setStyle(
                                                        "-fx-background-color: " + CARD_WHITE + ";" +
                                                                        "-fx-background-radius: 16;" +
                                                                        "-fx-border-color: " + PRIMARY + ";" +
                                                                        "-fx-border-radius: 16;" +
                                                                        "-fx-cursor: hand;");
                                });

                card.setOnMouseExited(
                                e -> {

                                        card.setTranslateY(0);

                                        card.setStyle(
                                                        "-fx-background-color: " + CARD_WHITE + ";" +
                                                                        "-fx-background-radius: 16;" +
                                                                        "-fx-border-color: " + BORDER + ";" +
                                                                        "-fx-border-radius: 16;" +
                                                                        "-fx-cursor: hand;");
                                });

                // ========================================================
                // CLICK → SPACE DETAILS
                // ========================================================

                card.setOnMouseClicked(
                                e -> {
                                        com.eklavya.spotfit.model.Space mockSpace = com.eklavya.spotfit.view.user.SpaceListing
                                                        .getMockSpaces("")[0];
                                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                        SpaceDetails.getSpaceDetailsScene(
                                                                        mockSpace,
                                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                        .setScene(
                                                                                                        Explore.getExploreScene(
                                                                                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                                                                        .getPrimaryStage()
                                                                                                                                        .setScene(
                                                                                                                                                        UserDash.getUserDashScene())))));
                                });

                // ========================================================
                // FAVORITE BUTTON
                // ========================================================

                // Disabled, no logic needed

                return card;
        }

        // ============================================================
        // UPCOMING BOOKING
        // ============================================================

        private static VBox buildUpcomingBooking() {

                VBox section = new VBox(14);

                // HBox headingRow = sectionHeadingRow(
                // "Your next booking",
                // "View all");

                HBox bookingCard = new HBox(18);

                bookingCard.setAlignment(
                                Pos.CENTER_LEFT);

                bookingCard.setPadding(
                                new Insets(17));

                bookingCard.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 16;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 16;");

                addShadow(
                                bookingCard,
                                0.05);

                Label date = new Label(
                                "20\nAUG");

                date.setAlignment(
                                Pos.CENTER);

                date.setPrefWidth(58);

                date.setPrefHeight(58);

                date.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-font-weight: bold;");

                VBox details = new VBox(5);

                HBox.setHgrow(
                                details,
                                Priority.ALWAYS);

                Label title = new Label(
                                "Podcast Studio Session");

                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label time = new Label(
                                "Tomorrow • 4:00 PM – 6:00 PM");

                time.setStyle(
                                body(10));

                Label location = new Label(
                                "Rhythm Creative Studio • Baner");

                location.setStyle(
                                body(10));

                details.getChildren().addAll(
                                title,
                                time,
                                location);

                Button view = new Button(
                                "View booking");

                view.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 18;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 9 13;" +
                                                "-fx-cursor: hand;");

                bookingCard.getChildren().addAll(
                                date,
                                details,
                                view);

                section.getChildren().addAll(
                                bookingCard);

                return section;
        }

        // ============================================================
        // EXISTING SECTION HEADING ROW
        // ============================================================

        private static HBox sectionHeadingRow(
                        String title,
                        String actionText) {

                HBox row = new HBox();

                row.setAlignment(
                                Pos.CENTER_LEFT);

                Label heading = new Label(
                                title);

                heading.setStyle(
                                heading(17));

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Button action = new Button(
                                actionText + "  →");

                action.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;");

                row.getChildren().addAll(
                                heading,
                                spacer,
                                action);

                return row;
        }

        // ============================================================
        // IMAGE VIEW
        // ============================================================

        private static ImageView createImageView(
                        String classpathPath,
                        double width,
                        double height) {

                Image image = loadImage(
                                classpathPath);

                ImageView imageView = new ImageView(
                                image);

                imageView.setFitWidth(width);

                imageView.setFitHeight(height);

                imageView.setPreserveRatio(false);

                return imageView;
        }

        // ============================================================
        // LOAD IMAGE
        // ============================================================

        private static Image loadImage(
                        String classpathPath) {

                java.io.InputStream stream = UserDash.class.getResourceAsStream(
                                classpathPath);

                if (stream == null) {

                        System.err.println(
                                        "[UserDash] Missing image resource: " +
                                                        classpathPath +
                                                        " — falling back to placeholder.");

                        stream = UserDash.class.getResourceAsStream(
                                        FALLBACK_IMAGE);

                        if (stream == null) {

                                throw new IllegalStateException(
                                                "Fallback placeholder image is also missing: " +
                                                                FALLBACK_IMAGE +
                                                                ". Add it to src/main/resources" +
                                                                FALLBACK_IMAGE);
                        }
                }

                return new Image(stream);
        }

        // ============================================================
        // SHADOW
        // ============================================================

        private static void addShadow(
                        Region node) {

                addShadow(
                                node,
                                0.07);
        }

        private static void addShadow(
                        Region node,
                        double opacity) {

                DropShadow shadow = new DropShadow();

                shadow.setRadius(14);

                shadow.setOffsetY(4);

                shadow.setColor(
                                Color.rgb(
                                                0,
                                                0,
                                                0,
                                                opacity));

                node.setEffect(shadow);
        }

        // ============================================================
        // CARD LIFT EFFECT
        // ============================================================

        private static void liftCard(
                        Region card,
                        boolean hovered) {

                ScaleTransition scale = new ScaleTransition(
                                Duration.millis(120),
                                card);

                if (hovered) {

                        scale.setToX(1.015);

                        scale.setToY(1.015);

                        addShadow(
                                        card,
                                        0.13);

                } else {

                        scale.setToX(1.0);

                        scale.setToY(1.0);

                        addShadow(
                                        card,
                                        0.06);
                }

                scale.play();
        }
}