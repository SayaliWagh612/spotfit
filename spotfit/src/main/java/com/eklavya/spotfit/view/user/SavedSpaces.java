package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.controller.user.CustomerFavoriteController;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.util.AuthenticatedExecutor;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.user.CustomerUi.createFavoriteButton;
import static com.eklavya.spotfit.view.user.CustomerUi.resolveSpaceCover;

public class SavedSpaces {

    private static CustomerFavoriteController favController;

    private static CustomerFavoriteController getFavController() {
        if (favController == null) {
            favController = new CustomerFavoriteController();
        }
        return favController;
    }

    public static Scene getSavedSpacesScene(Runnable backAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        HBox topBar = buildTopBar(backAction);
        root.setTop(topBar);

        VBox content = new VBox(25);
        content.setFillWidth(true);
        content.setPadding(new Insets(35, 55, 60, 55));

        VBox hero = new VBox(7);
        Label title = new Label("Saved Spaces");
        title.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 29px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");
        Label subtitle = new Label("Your favorite spaces across SpotFit.");
        subtitle.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");
        hero.getChildren().addAll(title, subtitle);

        VBox spacesContainer = new VBox(18);

        content.getChildren().addAll(hero, spacesContainer);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        root.setCenter(scroll);

        loadSavedSpaces(spacesContainer, backAction);

        return new Scene(root, 1280, 800);
    }

    private static HBox buildTopBar(Runnable backAction) {
        HBox bar = new HBox(20);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15, 42, 15, 42));
        bar.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 0 0 1 0;");

        Button back = new Button("←  Back");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_MUTED + "; -fx-font-family: " + FONT
                + "; -fx-font-size: 12px; -fx-cursor: hand;");
        back.setOnAction(e -> backAction.run());

        Label title = new Label("Favorites");
        title.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");

        bar.getChildren().addAll(back, title);
        return bar;
    }

    private static void loadSavedSpaces(VBox spacesContainer, Runnable backAction) {
        spacesContainer.getChildren().clear();

        VBox loadingBox = new VBox(12);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(40));

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40, 40);

        Label loadingLabel = new Label("Loading your saved spaces...");
        loadingLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MUTED + ";");

        loadingBox.getChildren().addAll(spinner, loadingLabel);
        spacesContainer.getChildren().add(loadingBox);

        getFavController().loadSavedVisibleSpaces().thenAccept(res -> {
            Platform.runLater(() -> {
                spacesContainer.getChildren().clear();

                if (res.isSuccess()) {
                    List<Space> spaces = res.getData();
                    if (spaces == null || spaces.isEmpty()) {
                        VBox emptyBox = new VBox(12);
                        emptyBox.setAlignment(Pos.CENTER);
                        emptyBox.setPadding(new Insets(50));
                        emptyBox.setStyle("-fx-background-color: " + CARD_WHITE
                                + "; -fx-background-radius: 16; -fx-border-color: " + BORDER
                                + "; -fx-border-radius: 16;");

                        Label emptyTitle = new Label("No Saved Spaces");
                        emptyTitle.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

                        Label emptySub = new Label("You haven't saved any spaces yet.");
                        emptySub.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px; -fx-text-fill: "
                                + TEXT_MUTED + ";");

                        emptyBox.getChildren().addAll(emptyTitle, emptySub);
                        spacesContainer.getChildren().add(emptyBox);
                    } else {
                        for (Space space : spaces) {
                            spacesContainer.getChildren().add(spaceCard(space, backAction));
                        }
                    }
                } else {
                    VBox errorBox = new VBox(15);
                    errorBox.setAlignment(Pos.CENTER);
                    errorBox.setPadding(new Insets(40));
                    errorBox.setStyle("-fx-background-color: " + CARD_WHITE
                            + "; -fx-background-radius: 16; -fx-border-color: " + BORDER + "; -fx-border-radius: 16;");

                    Label errLabel = new Label("Could not load saved spaces right now.");
                    errLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK
                            + "; -fx-font-weight: bold;");

                    Label errDetail = new Label("(" + res.getErrorCode() + ")");
                    errDetail.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px; -fx-text-fill: #e74c3c;");

                    Button retryBtn = new Button("Retry");
                    retryBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-family: "
                            + FONT
                            + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand;");

                    retryBtn.setOnAction(e -> {
                        retryBtn.setDisable(true);
                        AuthenticatedExecutor.execute(token -> {
                            Platform.runLater(() -> loadSavedSpaces(spacesContainer, backAction));
                            return CompletableFuture
                                    .completedFuture(com.eklavya.spotfit.model.OperationResult.success(null));
                        });
                    });

                    errorBox.getChildren().addAll(errLabel, errDetail, retryBtn);
                    spacesContainer.getChildren().add(errorBox);
                }
            });
        });
    }

    private static HBox spaceCard(Space space, Runnable parentBackAction) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setMinHeight(180);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-background-radius: 16; -fx-border-color: " + BORDER
                + "; -fx-border-radius: 16; -fx-cursor: hand;");

        javafx.scene.Node imageNode = resolveSpaceCover(space.getCoverPhotoUrl(), null);
        if (imageNode instanceof ImageView) {
            ((ImageView) imageNode).setFitWidth(250);
            ((ImageView) imageNode).setFitHeight(145);
            Rectangle clip = new Rectangle(250, 145);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            imageNode.setClip(clip);
        }

        VBox details = new VBox(8);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label name = new Label(space.getName());
        name.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");

        String area = space.getSpaceLocation().getArea();
        String city = space.getSpaceLocation().getCity();
        String locStr = (area != null && !area.isBlank() ? area + ", " : "") + (city != null ? city : "");
        Label location = new Label(locStr.isBlank() ? "Location details available" : locStr);
        location.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        String bookingType = space.getBookingType() != null && !space.getBookingType().isBlank()
                ? space.getBookingType().substring(0, 1).toUpperCase()
                        + space.getBookingType().substring(1).toLowerCase()
                : "Booking";
        if (bookingType.equalsIgnoreCase("Practice_room"))
            bookingType = "Practice Room";
        Label typeLabel = new Label(bookingType + " · Capacity " + space.getCapacity());
        typeLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        String amStr = (space.getAmenities() != null && !space.getAmenities().isEmpty())
                ? String.join(" · ", space.getAmenities())
                : "Standard amenities included";
        Label amenities = new Label(amStr);
        amenities.setWrapText(true);
        amenities.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        double basePrice = space.getBasePricePaise() / 100.0;
        String priceText = basePrice > 0
                ? "₹" + String.format("%.0f", basePrice) + " / " + space.getPricingUnit().toLowerCase()
                : "Free";
        Label price = new Label(priceText);
        price.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: "
                + PRIMARY + ";");

        details.getChildren().addAll(name, location, typeLabel, amenities, spacer, price);

        Button favorite = createFavoriteButton(space.getSpaceId(), getFavController(), null, 40, 18);

        card.getChildren().addAll(imageNode, details, favorite);

        card.setOnMouseClicked(e -> {
            if (e.getTarget() instanceof javafx.scene.Node) {
                javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
                while (target != null && target != card) {
                    if (target instanceof Button)
                        return; // ignore clicks on favorite button
                    target = target.getParent();
                }
            }
            AppNavigator.setScene(SpaceDetails.getSpaceDetailsScene(space,
                    () -> AppNavigator.setScene(getSavedSpacesScene(parentBackAction))));
        });

        return card;
    }
}