package com.eklavya.spotfit.view.user;

import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.Space;
import java.util.Arrays;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.user.CustomerUi.*;

public class SpaceListing {

        private static com.eklavya.spotfit.controller.user.CustomerFavoriteController favController;

        private static com.eklavya.spotfit.controller.user.CustomerFavoriteController getFavController() {
                if (favController == null) {
                        favController = new com.eklavya.spotfit.controller.user.CustomerFavoriteController();
                }
                return favController;
        }

        // ============================================================
        // MAIN SCENE
        // ============================================================

        public static Scene getSpaceListingScene(
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction) {

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                HBox topBar = buildTopBar(
                                activityName,
                                backAction);

                VBox content = buildContent(
                                domainName,
                                subdomainName,
                                activityName,
                                backAction);

                ScrollPane scroll = new ScrollPane(content);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setVbarPolicy(
                                ScrollPane.ScrollBarPolicy.AS_NEEDED);

                scroll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                root.setTop(topBar);
                root.setCenter(scroll);

                return new Scene(root, 1280, 800);
        }

        // ============================================================
        // TOP BAR
        // ============================================================

        private static HBox buildTopBar(
                        String activityName,
                        Runnable backAction) {

                HBox bar = new HBox(20);

                bar.setAlignment(
                                Pos.CENTER_LEFT);

                bar.setPadding(
                                new Insets(15, 42, 15, 42));

                bar.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-width: 0 0 1 0;");

                Button back = new Button("←  Back");

                back.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;");

                back.setOnAction(
                                e -> backAction.run());

                Label title = new Label(
                                activityName);

                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 20px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // Button profile = new Button("●");

                // profile.setStyle(
                //                 "-fx-background-color: " + PILL_BG + ";" +
                //                                 "-fx-background-radius: 20;" +
                //                                 "-fx-font-size: 13px;" +
                //                                 "-fx-text-fill: " + TEXT_DARK + ";" +
                //                                 "-fx-cursor: hand;");

                bar.getChildren().addAll(
                                back,
                                title,
                                spacer);

                return bar;
        }

        // ============================================================
        // CONTENT
        // ============================================================

        private static VBox buildContent(
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction) {

                VBox content = new VBox(25);

                content.setFillWidth(true);

                content.setPadding(
                                new Insets(35, 55, 60, 55));

                // --------------------------------------------------------
                // HERO
                // --------------------------------------------------------

                VBox hero = new VBox(7);

                Label title = new Label(
                                "Spaces for " + activityName);

                title.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 29px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                Label subtitle = new Label(
                                "Discover spaces that are suited for your activity.");

                subtitle.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 13px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                hero.getChildren().addAll(
                                title,
                                subtitle);

                // --------------------------------------------------------
                // BREADCRUMB
                // --------------------------------------------------------

                Label breadcrumb = new Label(
                                domainName
                                                + "  /  "
                                                + subdomainName
                                                + "  /  "
                                                + activityName);

                breadcrumb.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";");

                // --------------------------------------------------------
                // SPACE LIST
                // --------------------------------------------------------

                VBox spaces = new VBox(18);

                if (domainName != null && domainName.equalsIgnoreCase("Creativity")) {
                        loadLiveCreativitySpaces(spaces, domainName, subdomainName, activityName, backAction);
                } else {
                        for (Space space : getMockSpaces(activityName)) {
                                spaces.getChildren().add(
                                                spaceCard(space, domainName, subdomainName, activityName, backAction,
                                                                false));
                        }
                }

                content.getChildren().addAll(
                                hero,
                                breadcrumb,
                                spaces);

                return content;
        }

        private static int currentRequestId = 0;

        private static void loadLiveCreativitySpaces(
                        VBox spacesContainer,
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction) {

                spacesContainer.getChildren().clear();

                VBox loadingBox = new VBox(12);
                loadingBox.setAlignment(Pos.CENTER);
                loadingBox.setPadding(new Insets(40));

                javafx.scene.control.ProgressIndicator spinner = new javafx.scene.control.ProgressIndicator();
                spinner.setMaxSize(40, 40);

                Label loadingLabel = new Label("Loading live Creativity spaces...");
                loadingLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MUTED
                                + ";");

                loadingBox.getChildren().addAll(spinner, loadingLabel);
                spacesContainer.getChildren().add(loadingBox);

                final int requestId = ++currentRequestId;

                com.eklavya.spotfit.controller.user.CustomerMarketplaceController controller = new com.eklavya.spotfit.controller.user.CustomerMarketplaceController();

                String navTarget = (subdomainName != null && !subdomainName.isBlank()) ? subdomainName : activityName;

                controller.loadVisibleV2CreativitySpaces(navTarget, activityName).thenAccept(res -> {
                        javafx.application.Platform.runLater(() -> {
                                if (requestId != currentRequestId) {
                                        return;
                                }

                                spacesContainer.getChildren().clear();

                                if (res.isSuccess()) {
                                        java.util.List<Space> liveSpaces = res.getData();
                                        if (liveSpaces == null || liveSpaces.isEmpty()) {
                                                VBox emptyBox = new VBox(12);
                                                emptyBox.setAlignment(Pos.CENTER);
                                                emptyBox.setPadding(new Insets(50));
                                                emptyBox.setStyle("-fx-background-color: " + CARD_WHITE
                                                                + "; -fx-background-radius: 16; -fx-border-color: "
                                                                + BORDER + "; -fx-border-radius: 16;");

                                                Label emptyTitle = new Label("No Live Spaces Available");
                                                emptyTitle.setStyle("-fx-font-family: " + FONT
                                                                + "; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: "
                                                                + TEXT_DARK + ";");

                                                Label emptySub = new Label("No live spaces are available for "
                                                                + activityName + " yet.");
                                                emptySub.setStyle("-fx-font-family: " + FONT
                                                                + "; -fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED
                                                                + ";");

                                                emptyBox.getChildren().addAll(emptyTitle, emptySub);
                                                spacesContainer.getChildren().add(emptyBox);
                                        } else {
                                                for (Space space : liveSpaces) {
                                                        spacesContainer.getChildren().add(
                                                                        spaceCard(space, domainName, subdomainName,
                                                                                        activityName, backAction,
                                                                                        true));
                                                }
                                        }
                                } else {
                                        VBox errorBox = new VBox(15);
                                        errorBox.setAlignment(Pos.CENTER);
                                        errorBox.setPadding(new Insets(40));
                                        errorBox.setStyle("-fx-background-color: " + CARD_WHITE
                                                        + "; -fx-background-radius: 16; -fx-border-color: " + BORDER
                                                        + "; -fx-border-radius: 16;");

                                        Label errLabel = new Label("We couldn’t load spaces right now.");
                                        errLabel.setStyle("-fx-font-family: " + FONT
                                                        + "; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK
                                                        + "; -fx-font-weight: bold;");

                                        Label errDetail = new Label("(" + res.getErrorCode() + ")");
                                        errDetail.setStyle("-fx-font-family: " + FONT
                                                        + "; -fx-font-size: 11px; -fx-text-fill: #e74c3c;");

                                        Button retryBtn = new Button("Retry");
                                        retryBtn.setStyle("-fx-background-color: " + PRIMARY
                                                        + "; -fx-text-fill: white; -fx-font-family: " + FONT
                                                        + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand;");

                                        retryBtn.setOnAction(e -> {
                                                retryBtn.setDisable(true);
                                                com.eklavya.spotfit.util.AuthenticatedExecutor.execute(token -> {
                                                        javafx.application.Platform.runLater(
                                                                        () -> loadLiveCreativitySpaces(spacesContainer,
                                                                                        domainName, subdomainName,
                                                                                        activityName, backAction));
                                                        return java.util.concurrent.CompletableFuture.completedFuture(
                                                                        com.eklavya.spotfit.model.OperationResult
                                                                                        .success(null));
                                                });
                                        });

                                        errorBox.getChildren().addAll(errLabel, errDetail, retryBtn);
                                        spacesContainer.getChildren().add(errorBox);
                                }
                        });
                });
        }

        // ============================================================
        // SPACE CARD
        // ============================================================

        private static HBox spaceCard(
                        Space space,
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction) {
                return spaceCard(space, domainName, subdomainName, activityName, backAction, false);
        }

        private static HBox spaceCard(
                        Space space,
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction,
                        boolean isRealFirestoreSpace) {

                HBox card = new HBox(20);

                card.setAlignment(
                                Pos.CENTER_LEFT);

                card.setPadding(
                                new Insets(16));

                card.setMinHeight(180);

                card.setMaxWidth(
                                Double.MAX_VALUE);

                card.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 16;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 16;" +
                                                "-fx-cursor: hand;");

                // --------------------------------------------------------
                // IMAGE
                // --------------------------------------------------------

                javafx.scene.Node imageNode = resolveSpaceCover(
                                isRealFirestoreSpace ? space.getCoverPhotoUrl() : null,
                                "Creativity".equalsIgnoreCase(domainName) ? activityName : null);

                if (imageNode instanceof ImageView) {
                        ((ImageView) imageNode).setFitWidth(250);
                        ((ImageView) imageNode).setFitHeight(145);
                        Rectangle clip = new Rectangle(250, 145);
                        clip.setArcWidth(16);
                        clip.setArcHeight(16);
                        imageNode.setClip(clip);
                }

                // --------------------------------------------------------
                // DETAILS
                // --------------------------------------------------------

                VBox details = new VBox(8);

                HBox.setHgrow(
                                details,
                                Priority.ALWAYS);

                Label name = new Label(
                                space.getName());

                name.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 20px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                if (isRealFirestoreSpace) {
                        String area = space.getSpaceLocation().getArea();
                        String city = space.getSpaceLocation().getCity();
                        String locStr = (area != null && !area.isBlank() ? area + ", " : "")
                                        + (city != null ? city : "");
                        Label location = new Label(locStr.isBlank() ? "Location details available" : locStr);
                        location.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");

                        String bookingType = space.getBookingType() != null && !space.getBookingType().isBlank()
                                        ? space.getBookingType().substring(0, 1).toUpperCase()
                                                        + space.getBookingType().substring(1).toLowerCase()
                                        : "Booking";
                        if (bookingType.equalsIgnoreCase("Practice_room"))
                                bookingType = "Practice Room";
                        Label typeLabel = new Label(bookingType + " · Capacity " + space.getCapacity());
                        typeLabel.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");

                        String amStr = (space.getAmenities() != null && !space.getAmenities().isEmpty())
                                        ? String.join(" · ", space.getAmenities())
                                        : "Standard amenities included";
                        Label amenities = new Label(amStr);
                        amenities.setWrapText(true);
                        amenities.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");

                        Region spacer = new Region();
                        VBox.setVgrow(spacer, Priority.ALWAYS);

                        double basePrice = space.getBasePricePaise() / 100.0;
                        String priceText = basePrice > 0
                                        ? "₹" + String.format("%.0f", basePrice) + " / "
                                                        + space.getPricingUnit().toLowerCase()
                                        : "Free";
                        Label price = new Label(priceText);
                        price.setStyle("-fx-font-family: " + FONT
                                        + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY
                                        + ";");

                        details.getChildren().addAll(name, location, typeLabel, amenities, spacer, price);
                } else {
                        Label rating = new Label("Prototype Preview");
                        rating.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: "
                                        + TEXT_DARK
                                        + "; -fx-background-color: #fef3c7; -fx-padding: 4 8; -fx-background-radius: 8;");

                        Label location = new Label(space.getLocation());
                        location.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");

                        Label amenities = new Label(String.join(" • ", space.getAmenities()));
                        amenities.setWrapText(true);
                        amenities.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px; -fx-text-fill: "
                                        + TEXT_MUTED + ";");

                        Region spacer = new Region();
                        VBox.setVgrow(spacer, Priority.ALWAYS);

                        Label price = new Label("From ₹" + space.getPrice() + " / " + space.getPricingUnit());
                        price.setStyle("-fx-font-family: " + FONT
                                        + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY
                                        + ";");

                        details.getChildren().addAll(name, rating, location, amenities, spacer, price);
                }

                // --------------------------------------------------------
                // FAVORITE
                // --------------------------------------------------------

                Button favorite;
                if (isRealFirestoreSpace && space.getSpaceId() != null) {
                        favorite = createFavoriteButton(space.getSpaceId(), getFavController(), null, 40, 18);
                } else {
                        favorite = new Button("♡");
                        favorite.setStyle("-fx-background-color: " + PILL_BG
                                        + "; -fx-background-radius: 20; -fx-font-size: 18px; -fx-text-fill: "
                                        + TEXT_MUTED + "; -fx-cursor: default;");
                        favorite.setDisable(true);
                        javafx.scene.control.Tooltip t = new javafx.scene.control.Tooltip("Not eligible for saving");
                        t.setStyle("-fx-font-size: 12px;");
                        favorite.setTooltip(t);
                }

                card.getChildren().addAll(
                                imageNode,
                                details,
                                favorite);

                // --------------------------------------------------------
                // CLICK
                // --------------------------------------------------------

                card.setOnMouseClicked(
                                e -> {

                                        if (e.getTarget() != favorite) {

                                                openSpaceDetails(
                                                                space,
                                                                domainName,
                                                                subdomainName,
                                                                activityName,
                                                                backAction);
                                        }
                                });

                return card;
        }

        // ============================================================
        // MOCK DATA
        // ============================================================

        public static Space[] getMockSpaces(
                        String activityName) {

                /*
                 * TEMPORARY DATA ONLY.
                 *
                 * Firebase will eventually provide these values.
                 */

                if (activityName.equals(
                                "Creator / Photography Studios")) {

                        return new Space[] {

                                        new Space(
                                                        "Rhythm Creator Studio",
                                                        "4.8",
                                                        126,
                                                        "Baner, Pune",
                                                        "3.2 km",
                                                        800,
                                                        "hour",
                                                        "WiFi • AC • Lighting • Tripods",
                                                        "/images/UserDashImg/Creativity.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_001",
                                                                                        "Photography Session",
                                                                                        "Access to studio setup, lighting and backdrop.",
                                                                                        "SLOT", 800, "HOUR"),
                                                                        new Offering("offering_002", "Content Creation",
                                                                                        "Ideal for reels, product shoots and creator content.",
                                                                                        "SLOT", 1000, "HOUR"),
                                                                        new Offering("offering_003",
                                                                                        "Podcast Recording",
                                                                                        "Audio-ready setup with microphones and recording equipment.",
                                                                                        "SLOT", 1200,
                                                                                        "HOUR"))),

                                        new Space(
                                                        "FrameLab Studio",
                                                        "4.6",
                                                        89,
                                                        "Kothrud, Pune",
                                                        "5.1 km",
                                                        650,
                                                        "hour",
                                                        "AC • Cameras • Backdrops • Editing Desk",
                                                        "/images/UserDashImg/Creativity.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_004", "Standard Shoot",
                                                                                        "Access to basic backdrops and lighting.",
                                                                                        "SLOT", 650, "HOUR"),
                                                                        new Offering("offering_005", "Camera Rental",
                                                                                        "Rent a DSLR camera alongside the studio.",
                                                                                        "RENTAL", 250,
                                                                                        "HOUR"))),

                                        new Space(
                                                        "Creator Hub Pune",
                                                        "4.7",
                                                        74,
                                                        "Viman Nagar, Pune",
                                                        "7.4 km",
                                                        900,
                                                        "hour",
                                                        "WiFi • Lighting • Green Screen • Parking",
                                                        "/images/UserDashImg/Creativity.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_006",
                                                                                        "Green Screen Access",
                                                                                        "Includes parking and green screen setup.",
                                                                                        "SLOT", 900, "HOUR")))
                        };
                } else if (activityName.equalsIgnoreCase("Sports")) {
                        return new Space[] {
                                        new Space(
                                                        "Active Arena",
                                                        "4.9",
                                                        110,
                                                        "Viman Nagar, Pune",
                                                        "4.2 km",
                                                        500,
                                                        "hour",
                                                        "AC • Parking • Badminton • Gym Weights",
                                                        "/images/UserDashImg/Sports.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_sports_001",
                                                                                        "Badminton Court",
                                                                                        "Standard synthetic court.",
                                                                                        "PRACTICE_ROOM", 300,
                                                                                        "HOUR"),
                                                                        new Offering("offering_sports_002",
                                                                                        "Yoga Class",
                                                                                        "Group yoga session.",
                                                                                        "WORKSHOP", 200,
                                                                                        "SESSION")))
                        };
                } else if (activityName.equalsIgnoreCase("Wellness")) {
                        return new Space[] {
                                        new Space(
                                                        "Zen Wellness Center",
                                                        "4.8",
                                                        60,
                                                        "Baner, Pune",
                                                        "2.5 km",
                                                        1500,
                                                        "session",
                                                        "AC • Peaceful environment • Spa Facilities",
                                                        "/images/UserDashImg/Wellness.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_well_001",
                                                                                        "Massage Therapy",
                                                                                        "Relaxing massage slot.",
                                                                                        "SLOT", 1500,
                                                                                        "SESSION"),
                                                                        new Offering("offering_well_002",
                                                                                        "Meditation Seat",
                                                                                        "Reserved spot in the quiet room.",
                                                                                        "SEAT", 500, "DAY")))
                        };
                } else if (activityName.equalsIgnoreCase("Workplaces")) {
                        return new Space[] {
                                        new Space(
                                                        "Tech Hub Co-Working",
                                                        "4.5",
                                                        200,
                                                        "Kharadi, Pune",
                                                        "8.0 km",
                                                        6000,
                                                        "month",
                                                        "WiFi • AC • Coffee • Meeting Rooms",
                                                        "/images/UserDashImg/Workplaces.png",
                                                        Arrays.asList(
                                                                        new Offering("offering_work_001", "Hot Desk",
                                                                                        "Daily pass for any available desk.",
                                                                                        "SEAT", 500, "DAY"),
                                                                        new Offering("offering_work_002",
                                                                                        "Conference Room",
                                                                                        "Book the 10-seater meeting room.",
                                                                                        "VENUE", 1500,
                                                                                        "HOUR")))
                        };
                }

                // --------------------------------------------------------
                // GENERIC FALLBACK
                // --------------------------------------------------------

                return new Space[] {

                                new Space("Creative Space", "4.5", 42, "Pune", "4.0 km", 500, "hour",
                                                "WiFi • AC • Comfortable workspace",
                                                "/images/UserDashImg/Creativity.png",
                                                Arrays.asList(new Offering("offering_007", "General Access",
                                                                "A simple and comfortable creative setting.",
                                                                "SEAT", 500, "HOUR"))) };
        }

        // ============================================================
        // SPACE DETAILS NAVIGATION
        // ============================================================

        private static void openSpaceDetails(
                        Space space,
                        String domainName,
                        String subdomainName,
                        String activityName,
                        Runnable backAction) {

                com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                SpaceDetails.getSpaceDetailsScene(
                                                space,
                                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                                                SpaceListing.getSpaceListingScene(
                                                                                domainName,
                                                                                subdomainName,
                                                                                activityName,
                                                                                backAction))));
        }

        // ============================================================
        // IMAGE LOADER
        // ============================================================

        private static Image loadImage(
                        String classpathPath) {

                java.io.InputStream stream = SpaceListing.class
                                .getResourceAsStream(
                                                classpathPath);

                if (stream == null) {

                        System.err.println(
                                        "[SpaceListing] Missing image: "
                                                        + classpathPath);

                        stream = SpaceListing.class
                                        .getResourceAsStream(
                                                        "/images/UserDashImg/Sports.png");
                }

                if (stream == null) {

                        throw new IllegalStateException(
                                        "Fallback image is also missing.");
                }

                return new Image(stream);
        }
}
