package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.Space;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.user.CustomerUi.*;

public class SpaceDetails {

        private static com.eklavya.spotfit.controller.user.CustomerFavoriteController favController;

        private static com.eklavya.spotfit.controller.user.CustomerFavoriteController getFavController() {
                if (favController == null) {
                        favController = new com.eklavya.spotfit.controller.user.CustomerFavoriteController();
                }
                return favController;
        }

        // ============================================================
        // IMAGE PATH
        // ============================================================

        // Image must be inside:
        // src/main/resources/images/UserDashImg/Photography.png

        private static final String SPACE_IMAGE = "/images/UserDashImg/Studio.png";

        // Fallback image
        private static final String FALLBACK_IMAGE = "/images/UserDashImg/Sports.png";

        // Content column never grows wider than this, no matter how wide
        // the window is.
        private static final double CONTENT_MAX_WIDTH = 1180;

        // ============================================================
        // MAIN SCENE
        // ============================================================

        public static Scene getSpaceDetailsScene(
                        Space space,
                        Runnable callBackAction) {

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                root.setTop(
                                buildTopBar(callBackAction));

                VBox content = new VBox();
                content.setMaxWidth(CONTENT_MAX_WIDTH);
                // Keep the details column at its computed content height. Without this,
                // StackPane may compress the VBox to the viewport height and the
                // ScrollPane will not detect content added asynchronously below.
                content.setMinHeight(Region.USE_PREF_SIZE);

                StackPane centeredWrapper = new StackPane(content);
                centeredWrapper.setMinHeight(Region.USE_PREF_SIZE);

                centeredWrapper.setAlignment(
                                Pos.TOP_CENTER);

                ScrollPane scroll = new ScrollPane(centeredWrapper);

                scroll.setFitToWidth(true);
                scroll.setFitToHeight(false);
                scroll.setPannable(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.ALWAYS);

                scroll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                scroll.skinProperty().addListener(
                                (obs, oldSkin, newSkin) -> {

                                        if (newSkin == null) {
                                                return;
                                        }

                                        javafx.application.Platform.runLater(
                                                        () -> {

                                                                javafx.scene.Node vbar = scroll.lookup(
                                                                                ".scroll-bar:vertical");

                                                                if (vbar != null) {

                                                                        double vbarWidth = vbar
                                                                                        .getBoundsInParent()
                                                                                        .getWidth();

                                                                        centeredWrapper.setTranslateX(
                                                                                        vbarWidth / 2.0);
                                                                }
                                                        });
                                });

                root.setCenter(scroll);

                if (space != null && space.getSchemaVersion() == 2) {
                        VBox loadingBox = new VBox(12);
                        loadingBox.setAlignment(Pos.CENTER);
                        loadingBox.setPadding(new Insets(60));
                        ProgressIndicator spinner = new ProgressIndicator();
                        spinner.setMaxSize(40, 40);
                        Label loadingLabel = new Label("Loading space details...");
                        loadingLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");
                        loadingBox.getChildren().addAll(spinner, loadingLabel);
                        content.getChildren().add(loadingBox);

                        com.eklavya.spotfit.controller.user.CustomerMarketplaceController controller = new com.eklavya.spotfit.controller.user.CustomerMarketplaceController();

                        controller.loadVisibleV2SpaceDetails(space.getSpaceId()).thenAccept(res -> {
                                javafx.application.Platform.runLater(() -> {
                                        content.getChildren().clear();
                                        if (res.isSuccess() && res.getData() != null) {
                                                // Keep the V2 wrapper itself. Extracting only its children discarded
                                                // the page padding and vertical spacing, which made the page look
                                                // cramped and visually unstructured.
                                                content.getChildren().add(
                                                                buildContentV2(res.getData(), callBackAction));
                                        } else {
                                                VBox errorBox = new VBox(16);
                                                errorBox.setAlignment(Pos.CENTER);
                                                errorBox.setPadding(new Insets(60));
                                                errorBox.setStyle("-fx-background-color: " + CARD_WHITE
                                                                + "; -fx-background-radius: 16; -fx-border-color: "
                                                                + BORDER + "; -fx-border-radius: 16;");
                                                Label errLabel = new Label(
                                                                "This space is no longer available in the marketplace.");
                                                errLabel.setStyle("-fx-font-family: " + FONT
                                                                + "; -fx-font-size: 15px; -fx-text-fill: " + TEXT_DARK
                                                                + "; -fx-font-weight: bold;");
                                                Button backBtn = new Button("Back to Marketplace");
                                                backBtn.setStyle("-fx-background-color: " + PRIMARY
                                                                + "; -fx-text-fill: white; -fx-font-family: " + FONT
                                                                + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand;");
                                                backBtn.setOnAction(e -> callBackAction.run());
                                                errorBox.getChildren().addAll(errLabel, backBtn);
                                                content.getChildren().add(errorBox);
                                        }
                                        content.requestLayout();
                                        centeredWrapper.requestLayout();
                                        scroll.requestLayout();
                                        scroll.setVvalue(0.0);
                                });
                        });
                } else {
                        content.getChildren().addAll(buildContent(space).getChildren());
                }

                return new Scene(
                                root,
                                1280,
                                800);
        }

        // ============================================================
        // TOP BAR
        // ============================================================

        private static HBox buildTopBar(
                        Runnable callBackAction) {

                HBox bar = new HBox(18);

                bar.setAlignment(
                                Pos.CENTER_LEFT);

                bar.setPadding(
                                new Insets(14, 42, 14, 42));

                bar.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-width: 0 0 1 0;");

                Button back = new Button(
                                "←  Back");

                back.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-cursor: hand;");

                back.setOnAction(
                                e -> callBackAction.run());

                Label title = new Label(
                                "Space Details");

                title.setStyle(
                                heading(19));

                bar.getChildren().addAll(
                                back,
                                title);

                return bar;
        }

        // ============================================================
        // MAIN CONTENT
        // ============================================================

        private static VBox buildContent(
                        Space space) {
                VBox content = new VBox(25);

                content.setAlignment(
                                Pos.TOP_CENTER);

                content.setPadding(
                                new Insets(32, 40, 50, 40));

                content.getChildren().addAll(
                                buildHero(space),
                                buildMainInfo(space),
                                buildOfferings(space),
                                buildAbout(space));

                return content;
        }

        // ============================================================
        // HERO IMAGE
        // ============================================================

        private static StackPane buildHero(Space space) {

                StackPane hero = new StackPane();

                hero.setPrefHeight(300);

                hero.setMinHeight(300);

                hero.setMaxWidth(
                                Double.MAX_VALUE);

                // ----------------------------------------------------
                // LOAD IMAGE FROM RESOURCES FOLDER
                // ----------------------------------------------------

                Image image = loadImage(
                                space.getImagePath());

                ImageView imageView = new ImageView(
                                image);

                imageView.setFitHeight(300);

                imageView.setPreserveRatio(false);

                imageView.setSmooth(true);

                imageView.setCache(true);

                imageView.fitWidthProperty().bind(
                                hero.widthProperty());

                // ----------------------------------------------------
                // ROUNDED CORNERS
                // ----------------------------------------------------

                Rectangle clip = new Rectangle();

                clip.setArcWidth(24);

                clip.setArcHeight(24);

                clip.widthProperty().bind(
                                hero.widthProperty());

                clip.setHeight(300);

                imageView.setClip(clip);

                // ----------------------------------------------------
                // FAVORITE BUTTON
                // ----------------------------------------------------

                Button favourite = new Button("♡");
                favourite.setPrefSize(44, 44);
                favourite.setStyle("-fx-background-color: white; -fx-background-radius: 22; -fx-text-fill: "
                                + TEXT_MUTED + "; -fx-font-size: 22px; -fx-cursor: default;");
                favourite.setDisable(true);
                javafx.scene.control.Tooltip t = new javafx.scene.control.Tooltip("Not eligible for saving");
                t.setStyle("-fx-font-size: 12px;");
                favourite.setTooltip(t);

                StackPane.setAlignment(favourite, Pos.TOP_RIGHT);
                StackPane.setMargin(favourite, new Insets(16));

                hero.getChildren().addAll(imageView, favourite);

                return hero;
        }

        // ============================================================
        // MAIN INFORMATION
        // ============================================================

        private static VBox buildMainInfo(Space space) {

                VBox section = new VBox(9);

                HBox titleRow = new HBox();

                titleRow.setAlignment(
                                Pos.CENTER_LEFT);

                VBox titleBox = new VBox(5);

                HBox.setHgrow(
                                titleBox,
                                Priority.ALWAYS);

                // ----------------------------------------------------
                // SPACE NAME
                // ----------------------------------------------------

                Label title = new Label(
                                space.getName());

                title.setStyle(
                                heading(25));

                // ----------------------------------------------------
                // LOCATION
                // ----------------------------------------------------

                Label location = new Label(
                                "📍 " + space.getLocation());

                location.setStyle(
                                body(11));

                titleBox.getChildren().addAll(
                                title,
                                location);

                // ----------------------------------------------------
                // RATING
                // ----------------------------------------------------

                Label rating = new Label(
                                "★ " + space.getRating() + "  •  " + space.getReviews() + " reviews");

                rating.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 14;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 7 11;");

                titleRow.getChildren().addAll(
                                titleBox,
                                rating);

                // ----------------------------------------------------
                // DESCRIPTION
                // ----------------------------------------------------

                Label description = new Label(
                                "A comfortable creative space designed for " +
                                                "photography, content creation and small productions.");

                description.setWrapText(true);

                // No fixed maxWidth here — it inherits the 650px cap from
                // the parent content column, so it can never stretch wider
                // than the rest of the page like it could before.

                description.setStyle(
                                body(12));

                section.getChildren().addAll(
                                titleRow,
                                description);

                return section;
        }

        // ============================================================
        // OFFERINGS
        // ============================================================

        private static VBox buildOfferings(Space space) {

                VBox section = new VBox(13);

                Label title = new Label(
                                "What would you like to do?");

                title.setStyle(
                                heading(18));

                Label subtitle = new Label(
                                "Choose an offering and continue with your booking.");

                subtitle.setStyle(
                                body(11));

                VBox cards = new VBox(10);

                if (space.getOfferings() != null) {
                        for (Offering offering : space.getOfferings()) {
                                cards.getChildren().add(offeringCard(offering, space));
                        }
                }

                section.getChildren().addAll(
                                title,
                                subtitle,
                                cards);

                return section;
        }

        // ============================================================
        // OFFERING CARD
        // ============================================================

        private static HBox offeringCard(
                        Offering offering, Space space) {

                HBox card = new HBox(16);

                card.setAlignment(
                                Pos.CENTER_LEFT);

                card.setPadding(
                                new Insets(16));

                card.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 15;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 15;");

                VBox details = new VBox(5);

                HBox.setHgrow(
                                details,
                                Priority.ALWAYS);

                Label name = new Label(
                                offering.getName());

                name.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label info = new Label(
                                offering.getDescription());

                info.setWrapText(true);

                info.setStyle(
                                body(10));

                Label amount = new Label(
                                "₹" + String.format(
                                                "%.0f",
                                                offering.getPrice())
                                                + " / "
                                                + offering.getPricingUnit()
                                                                .toLowerCase());

                amount.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + PRIMARY + ";");

                Label type = new Label(
                                getBookingTypeText(
                                                offering.getBookingType()));

                type.setStyle(
                                "-fx-background-color: " + PILL_BG + ";" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-text-fill: " + PRIMARY + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 9px;" +
                                                "-fx-padding: 5 9;");

                details.getChildren().addAll(
                                name,
                                info,
                                amount,
                                type);

                Button select = new Button(
                                getBookingButtonText(
                                                offering.getBookingType()));

                select.setPrefHeight(36);

                select.setStyle(
                                "-fx-background-color: " + PRIMARY + ";" +
                                                "-fx-background-radius: 18;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-padding: 0 17;" +
                                                "-fx-cursor: hand;");

                select.setOnAction(
                                e -> openBooking(
                                                space, offering));

                card.getChildren().addAll(
                                details,
                                select);

                return card;
        }

        private static String getBookingTypeText(String bookingType) {
                if (bookingType == null)
                        return "Booking";
                switch (bookingType.toUpperCase()) {
                        case "SEAT":
                                return "Seat Booking";
                        case "SLOT":
                                return "Time Slot";
                        case "WORKSHOP":
                                return "Workshop";
                        case "RENTAL":
                                return "Rental";
                        case "VENUE":
                                return "Venue Request";
                        case "PRACTICE_ROOM":
                                return "Practice Room";
                        default:
                                return "Booking";
                }
        }

        private static String getBookingButtonText(String bookingType) {
                if (bookingType == null)
                        return "Continue";
                switch (bookingType.toUpperCase()) {
                        case "SEAT":
                                return "Reserve";
                        case "SLOT":
                                return "Book";
                        case "WORKSHOP":
                                return "Join";
                        case "RENTAL":
                                return "Rent";
                        case "VENUE":
                                return "Request";
                        case "PRACTICE_ROOM":
                                return "Book";
                        default:
                                return "Continue";
                }
        }

        private static void openBooking(
                        Space space, Offering offering) {

                if (!com.eklavya.spotfit.session.AppSession.isAuthenticated()) {
                        com.eklavya.spotfit.view.user.CustomerLoginPrompt.show(
                                        "Sign in to book this space",
                                        () -> com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin());
                        return;
                }

                com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                Booking.getBookingScene(
                                                space,
                                                offering,
                                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                                SpaceDetails.getSpaceDetailsScene(
                                                                                space,
                                                                                () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                                .setScene(
                                                                                                                Explore.getExploreScene(
                                                                                                                                () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                                                                                .getPrimaryStage()
                                                                                                                                                .setScene(
                                                                                                                                                                UserDash.getUserDashScene())))))));
        }

        // ============================================================
        // ABOUT
        // ============================================================

        private static VBox buildAbout(Space space) {

                VBox section = new VBox(10);

                Label title = new Label(
                                "About this space");

                title.setStyle(
                                heading(18));

                Label description = new Label(
                                "Creator Studio is a flexible creative environment " +
                                                "for photographers, content creators and small teams. " +
                                                "The space provides a clean setup with useful equipment " +
                                                "and flexible hourly booking.");

                description.setWrapText(true);

                // No fixed maxWidth here either — same reasoning as the
                // main description above, it inherits the 650px column cap.

                description.setStyle(
                                body(12));

                HBox facilities = new HBox(10);

                facilities.getChildren().addAll(

                                facility("📶 Wi-Fi"),

                                facility("💡 Lighting"),

                                facility("❄ AC"),

                                facility("🔌 Power"),

                                facility("🚗 Parking"));

                section.getChildren().addAll(
                                title,
                                description,
                                facilities);

                return section;
        }

        // ============================================================
        // FACILITY
        // ============================================================

        private static Label facility(
                        String text) {

                Label label = new Label(
                                text);

                label.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 14;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 14;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-padding: 7 11;");

                return label;
        }

        // ============================================================
        // V2 CONTENT BUILDERS
        // ============================================================

        private static VBox buildContentV2(Space space, Runnable callBackAction) {
                VBox content = new VBox(20);
                content.setAlignment(Pos.TOP_CENTER);
                content.setFillWidth(true);
                content.setPadding(new Insets(24, 40, 56, 40));

                VBox pricing = buildPricingV2(space);
                VBox amenities = buildAmenitiesV2(space);
                pricing.setPrefWidth(340);
                amenities.setPrefWidth(820);
                HBox.setHgrow(pricing, Priority.ALWAYS);
                HBox.setHgrow(amenities, Priority.ALWAYS);
                HBox essentialsRow = new HBox(18, pricing, amenities);
                essentialsRow.setAlignment(Pos.TOP_LEFT);
                essentialsRow.setFillHeight(true);

                VBox policies = buildPoliciesV2(space);
                VBox location = buildLocationV2(space);
                policies.setPrefWidth(680);
                location.setPrefWidth(480);
                HBox.setHgrow(policies, Priority.ALWAYS);
                HBox.setHgrow(location, Priority.ALWAYS);
                HBox practicalRow = new HBox(18, policies, location);
                practicalRow.setAlignment(Pos.TOP_LEFT);
                practicalRow.setFillHeight(true);

                content.getChildren().addAll(
                                buildHeroV2(space),
                                buildMainInfoV2(space),
                                essentialsRow,
                                practicalRow,
                                buildWorkshopsV2(space, callBackAction));

                return content;
        }

        private static StackPane buildHeroV2(Space space) {
                StackPane hero = new StackPane();
                hero.setPrefHeight(360);
                hero.setMinHeight(360);
                hero.setMaxWidth(CONTENT_MAX_WIDTH);
                hero.setStyle("-fx-background-radius: 22;"
                                + " -fx-effect: dropshadow(gaussian, rgba(59,36,23,0.20), 24, 0.14, 0, 8);");

                javafx.scene.Node imageNode = resolveSpaceCover(space.getCoverPhotoUrl(), space.getActivityGroup());

                if (imageNode instanceof ImageView) {
                        ImageView imageView = (ImageView) imageNode;
                        imageView.setFitHeight(360);
                        imageView.setPreserveRatio(false);
                        imageView.setSmooth(true);
                        imageView.setCache(true);
                        imageView.fitWidthProperty().bind(hero.widthProperty());

                        Rectangle clip = new Rectangle();
                        clip.setArcWidth(44);
                        clip.setArcHeight(44);
                        clip.widthProperty().bind(hero.widthProperty());
                        clip.setHeight(360);
                        imageView.setClip(clip);
                }

                Region shade = new Region();
                shade.setMouseTransparent(true);
                shade.setStyle("-fx-background-color: linear-gradient(to bottom, transparent 58%, rgba(59,36,23,0.22));"
                                + " -fx-background-radius: 22;");
                shade.prefWidthProperty().bind(hero.widthProperty());
                shade.prefHeightProperty().bind(hero.heightProperty());

                Button favourite;
                if (space.getSpaceId() != null) {
                        favourite = createFavoriteButton(space.getSpaceId(), getFavController(), null, 44, 22);
                } else {
                        favourite = new Button("♡");
                        favourite.setStyle("-fx-background-color: white; -fx-background-radius: 22; -fx-text-fill: "
                                        + TEXT_MUTED + "; -fx-font-size: 22px; -fx-cursor: default;");
                        favourite.setDisable(true);
                }
                StackPane.setAlignment(favourite, Pos.TOP_RIGHT);
                StackPane.setMargin(favourite, new Insets(20));

                hero.getChildren().addAll(imageNode, shade, favourite);
                return hero;
        }

        private static VBox buildMainInfoV2(Space space) {
                VBox section = new VBox(18);
                styleDetailsCard(section);

                HBox titleRow = new HBox(15);
                titleRow.setAlignment(Pos.CENTER_LEFT);

                VBox titleBox = new VBox(7);
                HBox.setHgrow(titleBox, Priority.ALWAYS);

                Label title = new Label(space.getName());
                title.setStyle(heading(30));

                String area = space.getSpaceLocation().getArea();
                String city = space.getSpaceLocation().getCity();
                String locStr = (area != null && !area.isBlank() ? area + ", " : "") + (city != null ? city : "");
                Label location = new Label("Location  ·  "
                                + (locStr.isBlank() ? "Details available after booking" : locStr));
                location.setStyle(body(14));

                titleBox.getChildren().addAll(title, location);

                String btType = space.getBookingType() != null ? space.getBookingType() : "Space";
                String tag = btType.substring(0, 1).toUpperCase() + btType.substring(1).toLowerCase();
                if (tag.equalsIgnoreCase("Practice_room"))
                        tag = "Practice Room";

                Label typeBadge = new Label(tag);
                typeBadge.setStyle("-fx-background-color: " + PILL_BG + "; -fx-background-radius: 14; -fx-text-fill: "
                                + PRIMARY + "; -fx-font-family: " + FONT
                                + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 7 11;");

                titleRow.getChildren().addAll(titleBox, typeBadge);

                String activityLabelText = "Activity: " + space.getActivityType();
                if ("WORKSHOP".equalsIgnoreCase(space.getBookingType())) {
                        String groupStr = space.getActivityGroup() != null ? space.getActivityGroup() : "";
                        String formatGroup = groupStr.substring(0, 1).toUpperCase()
                                        + groupStr.substring(1).toLowerCase();
                        activityLabelText = formatGroup + " Workshop";
                }
                FlowPane facts = new FlowPane();
                facts.setHgap(8);
                facts.setVgap(8);
                facts.getChildren().addAll(
                                detailChip(activityLabelText),
                                detailChip("Group · " + space.getActivityGroup()),
                                detailChip("Up to " + space.getCapacity() + " people"),
                                detailChip(space.getTimeZoneId() != null ? space.getTimeZoneId() : "Asia/Kolkata"));

                Label aboutTitle = new Label("About this space");
                aboutTitle.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

                Label description = new Label(space.getDescription() != null && !space.getDescription().isBlank()
                                ? space.getDescription()
                                : "No description provided.");
                description.setWrapText(true);
                description.setStyle(body(14));

                section.getChildren().addAll(titleRow, facts, aboutTitle, description);
                return section;
        }

        private static VBox buildPricingV2(Space space) {
                VBox section = new VBox(10);
                styleDetailsCard(section);
                Label title = new Label("Pricing");
                title.setStyle(heading(18));

                double basePrice = space.getBasePricePaise() / 100.0;
                String pricingUnit = space.getPricingUnit() != null && !space.getPricingUnit().isBlank()
                                ? space.getPricingUnit().toLowerCase()
                                : "booking";
                String priceText = basePrice > 0
                                ? "₹" + String.format("%.0f", basePrice) + " / " + pricingUnit
                                : "Free";

                Label priceLabel = new Label(priceText);
                priceLabel.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");

                Label helper = new Label("Final total is calculated before confirmation.");
                helper.setWrapText(true);
                helper.setStyle(body(11));

                section.getChildren().addAll(title, priceLabel, helper);
                return section;
        }

        private static VBox buildAmenitiesV2(Space space) {
                VBox section = new VBox(10);
                styleDetailsCard(section);
                Label title = new Label("Amenities & Facilities");
                title.setStyle(heading(18));

                javafx.scene.layout.FlowPane amenitiesBox = new javafx.scene.layout.FlowPane();
                amenitiesBox.setHgap(8);
                amenitiesBox.setVgap(8);

                if (space.getAmenities() != null && !space.getAmenities().isEmpty()) {
                        for (String am : space.getAmenities()) {
                                amenitiesBox.getChildren().add(facility(am));
                        }
                }
                if (space.getFacilities() != null && !space.getFacilities().isEmpty()) {
                        for (String fac : space.getFacilities()) {
                                amenitiesBox.getChildren().add(facility(fac));
                        }
                }

                if (amenitiesBox.getChildren().isEmpty()) {
                        Label none = new Label("No specific amenities listed.");
                        none.setStyle(body(12));
                        section.getChildren().addAll(title, none);
                } else {
                        section.getChildren().addAll(title, amenitiesBox);
                }
                return section;
        }

        private static VBox buildPoliciesV2(Space space) {
                VBox section = new VBox(10);
                styleDetailsCard(section);
                Label title = new Label("Policies & Rules");
                title.setStyle(heading(18));

                VBox content = new VBox(6);
                if (space.getRules() != null && !space.getRules().isEmpty()) {
                        Label rulesTitle = new Label("House Rules:");
                        rulesTitle.setStyle(sectionLabelStyle());
                        content.getChildren().add(rulesTitle);
                        for (String rule : space.getRules()) {
                                Label r = new Label("• " + rule);
                                r.setStyle(body(12));
                                r.setWrapText(true);
                                content.getChildren().add(r);
                        }
                }

                if (space.getCancellationPolicy() != null && !space.getCancellationPolicy().isBlank()) {
                        Label cancelTitle = new Label("Cancellation Policy:");
                        cancelTitle.setStyle(sectionLabelStyle() + " -fx-padding: 8 0 0 0;");
                        Label cancel = new Label(space.getCancellationPolicy());
                        cancel.setStyle(body(12));
                        cancel.setWrapText(true);
                        content.getChildren().addAll(cancelTitle, cancel);
                }

                if (space.getOpeningTime() != null && space.getClosingTime() != null) {
                        Label hoursTitle = new Label("Operating Hours:");
                        hoursTitle.setStyle(sectionLabelStyle() + " -fx-padding: 8 0 0 0;");
                        Label hours = new Label(space.getOpeningTime() + " - " + space.getClosingTime() + " "
                                        + (space.getOperatingDays() != null
                                                        ? String.join(", ", space.getOperatingDays())
                                                        : ""));
                        hours.setStyle(body(12));
                        content.getChildren().addAll(hoursTitle, hours);
                }

                if (content.getChildren().isEmpty()) {
                        Label none = new Label("No specific policies listed.");
                        none.setStyle(body(12));
                        section.getChildren().addAll(title, none);
                } else {
                        section.getChildren().addAll(title, content);
                }
                return section;
        }

        private static VBox buildLocationV2(Space space) {
                VBox section = new VBox(10);
                styleDetailsCard(section);
                Label title = new Label("Location");
                title.setStyle(heading(18));

                com.eklavya.spotfit.model.SpaceLocation loc = space.getSpaceLocation();
                java.util.List<String> parts = new java.util.ArrayList<>();
                if (loc.getAddressLine() != null && !loc.getAddressLine().isBlank())
                        parts.add(loc.getAddressLine());
                if (loc.getArea() != null && !loc.getArea().isBlank())
                        parts.add(loc.getArea());
                if (loc.getLandmark() != null && !loc.getLandmark().isBlank())
                        parts.add(loc.getLandmark());
                if (loc.getCity() != null && !loc.getCity().isBlank())
                        parts.add(loc.getCity());
                if (loc.getState() != null && !loc.getState().isBlank())
                        parts.add(loc.getState());
                if (loc.getPostalCode() != null && !loc.getPostalCode().isBlank())
                        parts.add(loc.getPostalCode());

                Label address = new Label(parts.isEmpty() ? "Address not provided" : String.join(", ", parts));
                address.setStyle(body(14));
                address.setWrapText(true);

                Label privacy = new Label("Exact directions remain available here for planning your visit.");
                privacy.setStyle(body(11));
                privacy.setWrapText(true);

                section.getChildren().addAll(title, address, privacy);
                return section;
        }

        private static void styleDetailsCard(VBox card) {
                card.setPadding(new Insets(22));
                card.setMaxWidth(Double.MAX_VALUE);
                card.setStyle("-fx-background-color: " + CARD_WHITE + ";"
                                + " -fx-background-radius: 18;"
                                + " -fx-border-color: " + BORDER + ";"
                                + " -fx-border-radius: 18;"
                                + " -fx-effect: dropshadow(gaussian, rgba(59,36,23,0.07), 14, 0.10, 0, 4);");
        }

        private static Label detailChip(String text) {
                Label chip = new Label(text == null ? "" : text);
                chip.setStyle("-fx-background-color: " + PILL_BG + ";"
                                + " -fx-background-radius: 14;"
                                + " -fx-text-fill: " + TEXT_DARK + ";"
                                + " -fx-font-family: " + FONT + ";"
                                + " -fx-font-size: 11px;"
                                + " -fx-padding: 7 11;");
                return chip;
        }

        private static String sectionLabelStyle() {
                return "-fx-font-family: " + FONT + "; -fx-font-size: 12px;"
                                + " -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";";
        }

        private static VBox buildWorkshopsV2(Space space, Runnable callBackAction) {
                VBox section = new VBox(15);
                styleDetailsCard(section);
                Label title = new Label(
                                space.getBookingType() != null && space.getBookingType().equalsIgnoreCase("WORKSHOP")
                                                ? "Upcoming Workshops"
                                                : "Booking Options");
                title.setStyle(heading(18));

                VBox workshopsBox = new VBox(12);
                workshopsBox.getChildren().add(new Label("Loading sessions..."));

                // Guests may browse every Space Details page, but availability and
                // booking actions require a Customer account.
                if (isGuestSession()) {
                        workshopsBox.getChildren().clear();
                        workshopsBox.getChildren().add(buildGuestBookingCard(space));
                        section.getChildren().addAll(title, workshopsBox);
                        return section;
                }

                if ("WORKSHOP".equalsIgnoreCase(space.getBookingType())) {
                        if (isGuestSession()) {
                                VBox signInCard = new VBox(12);
                                signInCard.setAlignment(Pos.CENTER_LEFT);
                                signInCard.setPadding(new Insets(20));
                                signInCard.setStyle(
                                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                                "-fx-background-radius: 14;" +
                                                                "-fx-border-color: " + BORDER + ";" +
                                                                "-fx-border-radius: 14;");

                                Label signInTitle = new Label("Ready to join this workshop?");
                                signInTitle.setStyle(
                                                "-fx-font-family: " + FONT + ";" +
                                                                "-fx-font-size: 16px;" +
                                                                "-fx-font-weight: bold;" +
                                                                "-fx-text-fill: " + TEXT_DARK + ";");

                                Label signInMessage = new Label(
                                                "Sign in to view available sessions and book your spot.");
                                signInMessage.setWrapText(true);
                                signInMessage.setStyle(body(13));

                                Button signInButton = new Button("Sign In to Book");
                                signInButton.setStyle(
                                                "-fx-background-color: " + PRIMARY + ";" +
                                                                "-fx-text-fill: white;" +
                                                                "-fx-font-family: " + FONT + ";" +
                                                                "-fx-font-size: 13px;" +
                                                                "-fx-font-weight: bold;" +
                                                                "-fx-background-radius: 20;" +
                                                                "-fx-padding: 9 20;" +
                                                                "-fx-cursor: hand;");
                                signInButton.setOnAction(e -> CustomerLoginPrompt.show(
                                                "Sign in to book this workshop",
                                                () -> UserFlowRouter.goToLogin()));

                                workshopsBox.getChildren().clear();
                                signInCard.getChildren().addAll(signInTitle, signInMessage, signInButton);
                                workshopsBox.getChildren().add(signInCard);
                                section.getChildren().addAll(title, workshopsBox);
                                return section;
                        }

                        com.eklavya.spotfit.controller.user.CustomerMarketplaceController controller = new com.eklavya.spotfit.controller.user.CustomerMarketplaceController();
                        controller.loadCustomerBookingOptions(space.getSpaceId(), null).thenAccept(res -> {
                                javafx.application.Platform.runLater(() -> {
                                        workshopsBox.getChildren().clear();
                                        if (res.isSuccess() && res.getData() != null) {
                                                com.eklavya.spotfit.model.CustomerBookingOptions options = res
                                                                .getData();
                                                java.util.List<com.eklavya.spotfit.model.WorkshopSession> sessions = options
                                                                .getWorkshopSessions();
                                                if (sessions == null || sessions.isEmpty()) {
                                                        Label empty = new Label(
                                                                        "No upcoming Workshop Sessions are currently available.");
                                                        empty.setStyle(body(13));
                                                        workshopsBox.getChildren().add(empty);
                                                } else {
                                                        for (com.eklavya.spotfit.model.WorkshopSession session : sessions) {
                                                                workshopsBox.getChildren().add(
                                                                                buildSessionCardV2(space, session));
                                                        }
                                                }
                                        } else {
                                                Label error = new Label("Unable to load sessions.");
                                                error.setStyle("-fx-text-fill: red;");
                                                workshopsBox.getChildren().add(error);
                                        }
                                });
                        });
                } else {
                        Label placeholder = new Label(
                                        "Booking flow for " + space.getBookingType() + " is currently in development.");
                        placeholder.setStyle(body(13));
                        workshopsBox.getChildren().add(placeholder);
                }

                section.getChildren().addAll(title, workshopsBox);
                return section;
        }

        private static VBox buildGuestBookingCard(Space space) {
                boolean workshop = "WORKSHOP".equalsIgnoreCase(space.getBookingType());

                VBox card = new VBox(12);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(20));
                card.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 14;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 14;");

                Label cardTitle = new Label(
                                workshop ? "Ready to join this workshop?" : "Ready to book this space?");
                cardTitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 16px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label message = new Label(
                                workshop
                                                ? "Sign in to view available sessions and book your spot."
                                                : "Sign in to view availability and continue with your booking.");
                message.setWrapText(true);
                message.setStyle(body(13));

                Button signInButton = new Button("Sign In to Book");
                signInButton.setStyle(
                                "-fx-background-color: " + PRIMARY + ";" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-background-radius: 20;" +
                                                "-fx-padding: 9 20;" +
                                                "-fx-cursor: hand;");
                signInButton.setOnAction(e -> CustomerLoginPrompt.show(
                                workshop ? "Sign in to book this workshop" : "Sign in to book this space",
                                () -> UserFlowRouter.goToLogin()));

                card.getChildren().addAll(cardTitle, message, signInButton);
                return card;
        }

        private static VBox buildSessionCardV2(Space space, com.eklavya.spotfit.model.WorkshopSession session) {
                VBox card = new VBox(10);
                card.setPadding(new Insets(16));
                card.setStyle("-fx-background-color: " + CARD_WHITE + "; -fx-background-radius: 12; -fx-border-color: "
                                + BORDER + "; -fx-border-radius: 12;");

                HBox topRow = new HBox(15);
                topRow.setAlignment(Pos.CENTER_LEFT);

                String tz = space.getTimeZoneId() != null ? space.getTimeZoneId() : "Asia/Kolkata";
                java.time.ZoneId zoneId = java.time.ZoneId.of(tz);
                java.time.ZonedDateTime start = java.time.Instant.ofEpochMilli(session.getStartAt()).atZone(zoneId);
                java.time.ZonedDateTime end = java.time.Instant.ofEpochMilli(session.getEndAt()).atZone(zoneId);

                java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter
                                .ofPattern("EEE, MMM d, yyyy");
                java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter
                                .ofPattern("h:mm a");

                Label dateLabel = new Label(start.format(dateFormatter));
                dateLabel.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

                Label timeLabel = new Label(
                                start.format(timeFormatter) + " - " + end.format(timeFormatter) + " (" + tz + ")");
                timeLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED
                                + ";");

                VBox dtBox = new VBox(4);
                dtBox.getChildren().addAll(dateLabel, timeLabel);
                HBox.setHgrow(dtBox, Priority.ALWAYS);

                int remaining = session.getCapacity() - session.getBookedCount();
                Label capacityLabel = new Label(remaining + " spot" + (remaining > 1 ? "s" : "") + " left");
                capacityLabel.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-font-family: " + FONT
                                + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 8;");

                topRow.getChildren().addAll(dtBox, capacityLabel);

                double price = session.getPriceOverridePaise() > 0
                                ? session.getPriceOverridePaise() / 100.0
                                : space.getBasePricePaise() / 100.0;
                Label priceLabel = new Label("₹" + String.format("%.0f", price) + " / person");
                priceLabel.setStyle("-fx-font-family: " + FONT
                                + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");

                HBox bottomRow = new HBox(15);
                bottomRow.setAlignment(Pos.CENTER_LEFT);

                int minParticipants = 1;
                int maxParticipants = 100;
                if (space.getBookingConfig() instanceof com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig) {
                        com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig wCfg = (com.eklavya.spotfit.model.bookingconfig.WorkshopBookingConfig) space
                                        .getBookingConfig();
                        minParticipants = wCfg.getMinimumParticipantsPerBooking();
                        maxParticipants = wCfg.getMaximumParticipantsPerBooking();
                }

                int maximumSelectable = Math.min(remaining, maxParticipants);

                Label participantLabel = new Label("Participants:");
                participantLabel.setStyle(body(12));
                ComboBox<Integer> countCombo = new ComboBox<>();
                for (int i = minParticipants; i <= maximumSelectable; i++)
                        countCombo.getItems().add(i);

                if (!countCombo.getItems().isEmpty()) {
                        countCombo.getSelectionModel().selectFirst();
                }
                countCombo.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button bookBtn = new Button("Book Now");
                bookBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-family: " + FONT
                                + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 6 16; -fx-cursor: hand;");

                if (remaining < minParticipants) {
                        bookBtn.setDisable(true);
                        bookBtn.setText("Sold Out");
                        Label errorLabel = new Label("Not enough remaining capacity for the minimum booking size.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
                        bottomRow.getChildren().addAll(participantLabel, countCombo, spacer, priceLabel, errorLabel,
                                        bookBtn);
                } else {
                        bottomRow.getChildren().addAll(participantLabel, countCombo, spacer, priceLabel, bookBtn);
                }

                bookBtn.setOnAction(e -> {
                        if (!com.eklavya.spotfit.session.AppSession.isAuthenticated()) {
                                com.eklavya.spotfit.view.user.CustomerLoginPrompt.show(
                                                "Sign in to book this session",
                                                () -> com.eklavya.spotfit.view.user.UserFlowRouter.goToLogin());
                                return;
                        }

                        int participants = countCombo.getValue() != null ? countCombo.getValue() : 1;
                        bookBtn.setDisable(true);
                        bookBtn.setText("Booking...");

                        com.eklavya.spotfit.model.BookingRequest req = new com.eklavya.spotfit.model.BookingRequest(
                                        space.getSpaceId(), space.getBookingType());
                        req.setSelectedDate(start.toLocalDate());
                        req.setStartTime(start.toLocalTime());
                        req.setEndTime(end.toLocalTime());
                        req.setDurationHours((int) java.time.Duration.between(start, end).toHours());
                        req.setParticipantCount(participants);
                        req.setQuantity(1);
                        req.setSessionId(session.getSessionId());

                        com.eklavya.spotfit.controller.user.CustomerMarketplaceController controller = new com.eklavya.spotfit.controller.user.CustomerMarketplaceController();
                        controller.previewV2Booking(req).thenAccept(previewRes -> {
                                javafx.application.Platform.runLater(() -> {
                                        if (previewRes.isSuccess() && previewRes.getData() != null) {
                                                com.eklavya.spotfit.model.BookingPreviewResult preview = previewRes
                                                                .getData();

                                                boolean confirmed = CustomerBookingDialogs.confirmBooking(
                                                                preview.getTotalQuotePaise() / 100.0);
                                                if (confirmed) {
                                                        bookBtn.setText("Creating Intent...");
                                                        controller.createV2Booking(req).thenAccept(createRes -> {
                                                                javafx.application.Platform.runLater(() -> {
                                                                        bookBtn.setDisable(false);
                                                                        bookBtn.setText("Book Now");
                                                                        if (createRes.isSuccess() && createRes
                                                                                        .getData() != null) {
                                                                                com.eklavya.spotfit.model.Booking b = createRes
                                                                                                .getData();
                                                                                boolean viewBookings = CustomerBookingDialogs
                                                                                                .showBookingCreated(b);
                                                                                if (viewBookings) {
                                                                                        com.eklavya.spotfit.navigation.AppNavigator
                                                                                                        .setScene(
                                                                                                                        MyBookings.getMyBookingsScene(
                                                                                                                                        () -> com.eklavya.spotfit.navigation.AppNavigator
                                                                                                                                                        .getPrimaryStage()
                                                                                                                                                        .setScene(UserDash
                                                                                                                                                                        .getUserDashScene())));
                                                                                }
                                                                        } else {
                                                                                Alert err = new Alert(
                                                                                                Alert.AlertType.ERROR);
                                                                                err.setTitle("Booking Failed");
                                                                                err.setHeaderText(
                                                                                                "Unable to create booking intent");
                                                                                err.setContentText(
                                                                                                createRes.getMessage());
                                                                                err.showAndWait();
                                                                        }
                                                                });
                                                        });
                                                } else {
                                                        bookBtn.setDisable(false);
                                                        bookBtn.setText("Book Now");
                                                }
                                        } else {
                                                bookBtn.setDisable(false);
                                                bookBtn.setText("Book Now");
                                                Alert err = new Alert(Alert.AlertType.ERROR);
                                                err.setTitle("Preview Failed");
                                                err.setHeaderText("Unable to generate quote");
                                                err.setContentText(previewRes.getMessage());
                                                err.showAndWait();
                                        }
                                });
                        });
                });

                card.getChildren().addAll(topRow, bottomRow);
                return card;
        }

        // ============================================================
        // LOAD IMAGE FROM RESOURCES
        // ============================================================

        private static Image loadImage(
                        String classpathPath) {

                java.io.InputStream stream = SpaceDetails.class.getResourceAsStream(
                                classpathPath);

                if (stream == null) {

                        System.err.println(
                                        "[SpaceDetails] Missing image resource: "
                                                        + classpathPath);

                        stream = SpaceDetails.class.getResourceAsStream(
                                        FALLBACK_IMAGE);

                        if (stream == null) {

                                throw new IllegalStateException(
                                                "Image not found: "
                                                                + classpathPath
                                                                + "\nFallback image also missing: "
                                                                + FALLBACK_IMAGE
                                                                + "\nMake sure the images are inside:"
                                                                + "\nsrc/main/resources/images/UserDashImg/");
                        }
                }

                return new Image(
                                stream);
        }
}