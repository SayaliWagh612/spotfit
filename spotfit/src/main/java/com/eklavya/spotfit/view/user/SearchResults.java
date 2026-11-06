package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.controller.user.CustomerMarketplaceController;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.search.CustomerSearchCatalog;
import com.eklavya.spotfit.model.search.SearchNavigationMatch;
import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import java.util.List;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class SearchResults {

    private static final CustomerMarketplaceController searchController = new CustomerMarketplaceController();
    private static com.eklavya.spotfit.controller.user.CustomerFavoriteController favController;

    private static com.eklavya.spotfit.controller.user.CustomerFavoriteController getFavController() {
        if (favController == null) {
            favController = new com.eklavya.spotfit.controller.user.CustomerFavoriteController();
        }
        return favController;
    }

    public static Scene getSearchResultsScene(String initialQuery, Runnable backAction) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        // Top Navigation
        HBox topNav = new HBox(15);
        topNav.setPadding(new Insets(20, 30, 20, 30));
        topNav.setAlignment(Pos.CENTER_LEFT);
        topNav.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 0 0 1 0;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY
                + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (backAction != null) {
                backAction.run();
            } else {
                AppNavigator.setScene(UserDash.getUserDashScene());
            }
        });

        TextField searchField = new TextField(initialQuery);
        searchField.setPromptText("Search activities, spaces, locations or amenities...");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-color: " + INPUT_BG + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 15; -fx-text-fill: " + TEXT_DARK
                + ";");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: " + PRIMARY
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        Label titleLabel = new Label("Search results");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        HBox.setMargin(titleLabel, new Insets(0, 0, 0, 20));

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        topNav.getChildren().addAll(backBtn, titleLabel, navSpacer, searchField, searchBtn);

        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_PAGE + ";");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(topNav, scrollPane);

        Runnable performSearch = () -> {
            String q = searchField.getText();
            if (q == null || q.trim().isEmpty()) {
                contentArea.getChildren().clear();
                Label errorLabel = new Label("Please enter a search term.");
                errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 14px;");
                contentArea.getChildren().add(errorLabel);
                return;
            }
            contentArea.getChildren().clear();

            // Loading state
            VBox loadingBox = new VBox(15);
            loadingBox.setAlignment(Pos.CENTER);
            loadingBox.setPadding(new Insets(50));
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            Label loadingText = new Label("Searching available Spaces...");
            loadingText.setStyle("-fx-text-fill: " + TEXT_MUTED + ";");
            loadingBox.getChildren().addAll(loadingIndicator, loadingText);
            contentArea.getChildren().add(loadingBox);

            searchField.setDisable(true);
            searchBtn.setDisable(true);

            // Fetch Local Matches
            List<SearchNavigationMatch> localMatches = CustomerSearchCatalog.getNavigationMatches(q);

            Runnable reloadAction = () -> searchBtn.fire();

            // Fetch Real Spaces
            searchController.searchVisibleMarketplaceSpaces(q).thenAccept(res -> {
                Platform.runLater(() -> {
                    searchField.setDisable(false);
                    searchBtn.setDisable(false);
                    contentArea.getChildren().clear();

                    if (res.isFailure()) {
                        VBox errorBox = new VBox(15);
                        errorBox.setAlignment(Pos.CENTER);
                        errorBox.setPadding(new Insets(50));
                        Label errorLabel = new Label("Failed to load results: " + res.getMessage());
                        errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 16px;");

                        Button retryBtn = new Button("Retry");
                        retryBtn.setStyle("-fx-background-color: " + PRIMARY
                                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                        retryBtn.setOnAction(evt -> reloadAction.run());

                        errorBox.getChildren().addAll(errorLabel, retryBtn);
                        contentArea.getChildren().add(errorBox);
                        return;
                    }

                    List<Space> realSpaces = res.getData();

                    if (localMatches.isEmpty() && (realSpaces == null || realSpaces.isEmpty())) {
                        VBox emptyBox = new VBox(15);
                        emptyBox.setAlignment(Pos.CENTER);
                        emptyBox.setPadding(new Insets(50));
                        Label emptyLabel = new Label("No available Spaces matched \"" + q + "\".");
                        emptyLabel.setStyle(
                                "-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 18px; -fx-font-weight: bold;");
                        Label suggestionLabel = new Label("Try browsing Creativity groups or using a different term.");
                        suggestionLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
                        emptyBox.getChildren().addAll(emptyLabel, suggestionLabel);
                        contentArea.getChildren().add(emptyBox);
                        return;
                    }

                    // Render Local Matches
                    if (!localMatches.isEmpty()) {
                        Label navHeading = new Label("Categories & Activities");
                        navHeading.setStyle(
                                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
                        contentArea.getChildren().add(navHeading);

                        FlowPane navFlow = new FlowPane(15, 15);
                        for (SearchNavigationMatch match : localMatches) {
                            navFlow.getChildren().add(createNavigationCard(match, backAction));
                        }
                        contentArea.getChildren().add(navFlow);
                    }

                    // Render Real Spaces
                    if (realSpaces != null && !realSpaces.isEmpty()) {
                        Label spacesHeading = new Label("Spaces");
                        spacesHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: "
                                + TEXT_DARK + "; -fx-padding: 20 0 0 0;");
                        contentArea.getChildren().add(spacesHeading);

                        GridPane spacesGrid = new GridPane();
                        spacesGrid.setHgap(20);
                        spacesGrid.setVgap(20);

                        int col = 0;
                        int row = 0;
                        for (Space s : realSpaces) {
                            spacesGrid.add(createSpaceCard(s, backAction), col, row);
                            col++;
                            if (col >= 3) {
                                col = 0;
                                row++;
                            }
                        }
                        contentArea.getChildren().add(spacesGrid);
                    }
                });
            });
        };

        searchBtn.setOnAction(e -> performSearch.run());
        searchField.setOnAction(e -> performSearch.run());

        // auto-execute if initialQuery is not empty
        if (initialQuery != null && !initialQuery.trim().isEmpty()) {
            Platform.runLater(performSearch);
        }

        return new Scene(root, 1280, 800);
    }

    private static VBox createNavigationCard(SearchNavigationMatch match, Runnable searchResultsBackAction) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        card.setPrefWidth(250);

        Label title = new Label(match.getDisplayLabel());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        String typeLabelText = "";
        switch (match.getResultType()) {
            case DOMAIN:
                typeLabelText = "Domain";
                break;
            case ACTIVITY_GROUP:
                typeLabelText = "Activity Group";
                break;
            case ACTIVITY:
                typeLabelText = "Activity";
                break;
        }
        if (match.isPrototype()) {
            typeLabelText += " • Prototype Preview";
        }

        Label typeLabel = new Label(typeLabelText);
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        card.getChildren().addAll(title, typeLabel);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: "
                + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));

        card.setOnMouseClicked(e -> {
            Runnable backToSearch = () -> AppNavigator
                    .setScene(getSearchResultsScene(match.getDisplayLabel(), searchResultsBackAction));
            switch (match.getResultType()) {
                case DOMAIN:
                    AppNavigator.setScene(DomainPage.getDomainScene(match.getCanonicalDomain(), backToSearch));
                    break;
                case ACTIVITY_GROUP:
                    AppNavigator.setScene(ActivityPage.getActivityScene(match.getCanonicalDomain(),
                            match.getSubdomainOrGroup(), backToSearch));
                    break;
                case ACTIVITY:
                    AppNavigator.setScene(SpaceListing.getSpaceListingScene(match.getCanonicalDomain(),
                            match.getSubdomainOrGroup(), match.getActivityName(), backToSearch));
                    break;
            }
        });

        return card;
    }

    private static VBox createSpaceCard(Space space, Runnable searchResultsBackAction) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        card.setPrefWidth(300);

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(space.getSpaceName() != null ? space.getSpaceName() : "Unnamed Space");
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button favorite = com.eklavya.spotfit.view.user.CustomerUi.createFavoriteButton(space.getSpaceId(),
                getFavController(), null, 32, 16);
        topRow.getChildren().addAll(name, spacer, favorite);

        String locStr = space.getSpaceLocation() != null
                ? (space.getSpaceLocation().getAddressLine() != null ? space.getSpaceLocation().getAddressLine() : "") +
                        (space.getSpaceLocation().getCity() != null ? ", " + space.getSpaceLocation().getCity() : "")
                : "Location Unknown";

        Label locLabel = new Label(locStr);
        locLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        String subLabelTxt = "";
        if (space.getActivityType() != null) {
            subLabelTxt += space.getActivityType() + " • ";
        }
        subLabelTxt += "Cap: " + space.getCapacity();

        if (space.getBasePricePaise() > 0 && space.getPricingUnit() != null) {
            subLabelTxt += " • ₹" + (space.getBasePricePaise() / 100) + "/" + space.getPricingUnit().toLowerCase();
        }

        Label subLabel = new Label(subLabelTxt);
        subLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        card.getChildren().addAll(topRow, locLabel, subLabel);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: "
                + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));

        card.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof javafx.scene.Node) {
                javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
                while (target != null && target != card) {
                    if (target instanceof Button)
                        return; // ignore clicks on favorite button
                    target = target.getParent();
                }
            }
            Runnable backToSearch = () -> AppNavigator
                    .setScene(getSearchResultsScene(space.getSpaceName(), searchResultsBackAction));
            AppNavigator.setScene(SpaceDetails.getSpaceDetailsScene(space, backToSearch));
        });

        return card;
    }
}
