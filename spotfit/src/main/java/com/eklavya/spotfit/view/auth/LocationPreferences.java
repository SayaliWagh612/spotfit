package com.eklavya.spotfit.view.auth;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import netscape.javascript.JSObject;

import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.util.ResponsiveUtil.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

public class LocationPreferences {

    // ============================================================
    // DEFAULT LOCATION
    // ============================================================

    private static final double DEFAULT_LAT = 18.5204;
    private static final double DEFAULT_LNG = 73.8567;

    private static double selectedLatitude = DEFAULT_LAT;
    private static double selectedLongitude = DEFAULT_LNG;

    private static int selectedRadiusKm = 10;

    private static WebEngine mapEngine;

    private static Label selectedLocationLabel;

    // ============================================================
    // MAIN SCENE
    // ============================================================

    public static Scene getLocationScene(
            Runnable onBack,
            java.util.function.Consumer<com.eklavya.spotfit.model.User> onFinishSetup) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + BG_PAGE + ";"
        );

        // --------------------------------------------------------
        // STEPPER
        // --------------------------------------------------------

        VBox stepper = com.eklavya.spotfit.view.common.StepperBar.build(
                new String[]{
                        "Profile",
                        "Persona",
                        "Location"
                },
                3
        );

        VBox stepperWrapper = new VBox(stepper);

        stepperWrapper.setAlignment(Pos.CENTER);

        stepperWrapper.setPadding(
                new Insets(25, 0, 10, 0)
        );

        // --------------------------------------------------------
        // HEADER
        // --------------------------------------------------------

        Label heading = new Label(
                "Set Your Location Preferences"
        );

        heading.setStyle(
                heading(24)
        );

        Label subtitle = new Label(
                "Choose where you want to explore and how far you're willing to travel."
        );

        subtitle.setStyle(
                body(14)
        );

        subtitle.setWrapText(true);

        VBox headingBox = new VBox(
                6,
                heading,
                subtitle
        );

        headingBox.setPadding(
                new Insets(0, 0, 10, 0)
        );

        // --------------------------------------------------------
        // CONTENT
        // --------------------------------------------------------

        VBox leftForm = buildLeftForm();

        VBox rightMap = buildRightMap();

        HBox.setHgrow(
                rightMap,
                Priority.ALWAYS
        );

        HBox contentRow = new HBox(
                24,
                leftForm,
                rightMap
        );

        contentRow.setAlignment(
                Pos.TOP_LEFT
        );

        // --------------------------------------------------------
        // CENTER
        // --------------------------------------------------------

        VBox center = new VBox(
                18,
                stepperWrapper,
                headingBox,
                contentRow
        );

        center.setPadding(
                new Insets(
                        15,
                        50,
                        40,
                        50
                )
        );

        // --------------------------------------------------------
        // SCROLL
        // --------------------------------------------------------

        ScrollPane scroll = new ScrollPane(
                center
        );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background: " + BG_PAGE + ";" +
                "-fx-background-color: " + BG_PAGE + ";"
        );

        root.setCenter(scroll);

        // --------------------------------------------------------
        // BOTTOM BAR
        // --------------------------------------------------------

        root.setBottom(
                buildBottomBar(
                        onBack,
                        onFinishSetup
                )
        );

        // --------------------------------------------------------
        // SCENE
        // --------------------------------------------------------

        Rectangle2D screen = screenBounds();

        Scene scene = new Scene(
                root,
                screen.getWidth(),
                screen.getHeight()
        );

        // Left panel = approximately 30%
        bindWidthPercent(
                leftForm,
                scene,
                0.30,
                360,
                500
        );

        return scene;
    }

    // ============================================================
    // LEFT PANEL
    // ============================================================

    private static VBox buildLeftForm() {

        // --------------------------------------------------------
        // LOCATION CARD
        // --------------------------------------------------------

        Label locationTitle =
                sectionLabel("📍 Base Location");

        Label locationHelp =
                new Label(
                        "Select a point on the map or choose a city."
                );

        locationHelp.setWrapText(true);
        locationHelp.setStyle(body(12));

        // Current location button

        var currentLocationButton =
                primaryButton(
                        "🧭  Use Current Location"
                );

        currentLocationButton.setMaxWidth(
                Double.MAX_VALUE
        );

        currentLocationButton.setOnAction(e -> {

            if (mapEngine != null) {

                try {

                    mapEngine.executeScript(
                            "getCurrentLocation();"
                    );

                } catch (Exception ex) {

                    System.out.println(
                            "Could not access browser geolocation."
                    );
                }
            }
        });

        HBox divider =
                divider("OR MANUAL ENTRY");

        Label cityLabel =
                new Label("City / Area");

        cityLabel.setStyle(
                body(13)
        );

        ComboBox<String> cityBox =
                new ComboBox<>();

        cityBox.getItems().addAll(
                "Pune, Maharashtra",
                "Mumbai, Maharashtra",
                "Bengaluru, Karnataka",
                "Delhi, India",
                "Hyderabad, Telangana"
        );

        cityBox.setValue(
                "Pune, Maharashtra"
        );

        cityBox.setMaxWidth(
                Double.MAX_VALUE
        );

        cityBox.setPrefHeight(46);

        cityBox.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;"
        );

        cityBox.setOnAction(e -> {

            String city =
                    cityBox.getValue();

            if (city == null)
                return;

            switch (city) {

                case "Pune, Maharashtra":

                    moveMapTo(
                            18.5204,
                            73.8567
                    );

                    break;

                case "Mumbai, Maharashtra":

                    moveMapTo(
                            19.0760,
                            72.8777
                    );

                    break;

                case "Bengaluru, Karnataka":

                    moveMapTo(
                            12.9716,
                            77.5946
                    );

                    break;

                case "Delhi, India":

                    moveMapTo(
                            28.6139,
                            77.2090
                    );

                    break;

                case "Hyderabad, Telangana":

                    moveMapTo(
                            17.3850,
                            78.4867
                    );

                    break;
            }
        });

        // Selected location display

        selectedLocationLabel =
                new Label(
                        "📍 Pune, Maharashtra\n" +
                        "18.52040, 73.85670"
                );

        selectedLocationLabel.setWrapText(
                true
        );

        selectedLocationLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        selectedLocationLabel.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12;" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + TEXT_DARK + ";"
        );

        VBox locationCard =
                sectionCard(
                        locationTitle,
                        locationHelp,
                        currentLocationButton,
                        divider,
                        cityLabel,
                        cityBox,
                        selectedLocationLabel
                );

        // ========================================================
        // RADIUS CARD
        // ========================================================

        Label radiusTitle =
                sectionLabel(
                        "🥾 Travel Radius"
                );

        Label radiusValue =
                new Label("10 km");

        radiusValue.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 4 12 4 12;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        HBox radiusHeader =
                new HBox(
                        radiusTitle,
                        spacer(),
                        radiusValue
                );

        radiusHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        Slider radiusSlider =
                new Slider(
                        1,
                        50,
                        10
                );

        radiusSlider.setShowTickMarks(false);
        radiusSlider.setShowTickLabels(false);

        radiusSlider.setMaxWidth(
                Double.MAX_VALUE
        );

        radiusSlider.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            selectedRadiusKm =
                                    newValue.intValue();

                            radiusValue.setText(
                                    selectedRadiusKm +
                                    " km"
                            );

                            if (mapEngine != null) {

                                mapEngine.executeScript(
                                        "updateRadius(" +
                                        selectedRadiusKm +
                                        ");"
                                );
                            }
                        }
                );

        Label minLabel =
                new Label("1 km");

        minLabel.setStyle(
                body(12)
        );

        Label maxLabel =
                new Label("50 km");

        maxLabel.setStyle(
                body(12)
        );

        HBox rangeLabels =
                new HBox(
                        minLabel,
                        spacer(),
                        maxLabel
                );

        VBox radiusCard =
                sectionCard(
                        radiusHeader,
                        radiusSlider,
                        rangeLabels
                );

        // ========================================================
        // DISCOVERY CARD
        // ========================================================

        Label discoveryTitle =
                sectionLabel(
                        "🧭 Discovery Preference"
                );

        ToggleButton nearbyOnly =
                new ToggleButton(
                        "Nearby Only"
                );

        ToggleButton openToTravel =
                new ToggleButton(
                        "Open to Travel"
                );

        openToTravel.setSelected(
                true
        );

        styleToggle(
                nearbyOnly
        );

        styleToggle(
                openToTravel
        );

        nearbyOnly.setOnAction(e -> {

            nearbyOnly.setSelected(true);
            openToTravel.setSelected(false);

        });

        openToTravel.setOnAction(e -> {

            openToTravel.setSelected(true);
            nearbyOnly.setSelected(false);

        });

        HBox toggleRow =
                new HBox(
                        10,
                        nearbyOnly,
                        openToTravel
                );

        HBox.setHgrow(
                nearbyOnly,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                openToTravel,
                Priority.ALWAYS
        );

        nearbyOnly.setMaxWidth(
                Double.MAX_VALUE
        );

        openToTravel.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox discoveryCard =
                sectionCard(
                        discoveryTitle,
                        toggleRow
                );

        // ========================================================
        // FORM
        // ========================================================

        VBox form =
                new VBox(
                        18,
                        locationCard,
                        radiusCard,
                        discoveryCard
                );

        form.setMaxWidth(
                Double.MAX_VALUE
        );

        return form;
    }

    // ============================================================
    // TOGGLE STYLE
    // ============================================================

    private static void styleToggle(
            ToggleButton button) {

        button.setPrefHeight(42);

        String base =
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;";

        button.setStyle(
                base +
                "-fx-background-color: " +
                (button.isSelected()
                        ? PILL_BG
                        : "white") +
                ";" +
                "-fx-border-color: " +
                (button.isSelected()
                        ? PRIMARY
                        : BORDER) +
                ";" +
                "-fx-text-fill: " +
                TEXT_DARK +
                ";"
        );

        button.selectedProperty()
                .addListener(
                        (obs, oldValue, selected) -> {

                            button.setStyle(
                                    base +
                                    "-fx-background-color: " +
                                    (selected
                                            ? PILL_BG
                                            : "white") +
                                    ";" +
                                    "-fx-border-color: " +
                                    (selected
                                            ? PRIMARY
                                            : BORDER) +
                                    ";" +
                                    "-fx-text-fill: " +
                                    TEXT_DARK +
                                    ";"
                            );
                        }
                );
    }

    // ============================================================
    // RIGHT MAP
    // ============================================================

    private static VBox buildRightMap() {

        WebView webView =
                new WebView();

        webView.setPrefHeight(
                570
        );

        webView.setMinHeight(
                500
        );

        webView.setMaxWidth(
                Double.MAX_VALUE
        );

        // Important for JavaFX WebView
        webView.setContextMenuEnabled(
                false
        );

        mapEngine =
                webView.getEngine();

        mapEngine.setJavaScriptEnabled(
                true
        );

        // --------------------------------------------------------
        // JavaScript loaded
        // --------------------------------------------------------

        mapEngine.getLoadWorker()
                .stateProperty()
                .addListener(
                        (obs, oldState, newState) -> {

                            if (newState ==
                                    Worker.State.SUCCEEDED) {

                                try {

                                    JSObject window =
                                            (JSObject)
                                                    mapEngine.executeScript(
                                                            "window"
                                                    );

                                    window.setMember(
                                            "javaBridge",
                                            new JavaBridge()
                                    );

                                    mapEngine.executeScript(
                                            "initializeMap();"
                                    );

                                } catch (Exception ex) {

                                    ex.printStackTrace();
                                }
                            }
                        }
                );

        // --------------------------------------------------------
        // Load map
        // --------------------------------------------------------

        mapEngine.loadContent(
                getMapHtml()
        );

        // --------------------------------------------------------
        // Map container
        // --------------------------------------------------------

        StackPane mapContainer =
                new StackPane(
                        webView
                );

        mapContainer.setPrefHeight(
                570
        );

        mapContainer.setMinHeight(
                500
        );

        mapContainer.setMaxWidth(
                Double.MAX_VALUE
        );

        mapContainer.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;"
        );

        // --------------------------------------------------------
        // Map title overlay
        // --------------------------------------------------------

        Label mapHint =
                new Label(
                        "Click anywhere on the map to select a location"
                );

        mapHint.setStyle(
                "-fx-background-color: rgba(255,255,255,0.94);" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 9 14;" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " +
                TEXT_DARK +
                ";"
        );

        StackPane.setAlignment(
                mapHint,
                Pos.TOP_CENTER
        );

        StackPane.setMargin(
                mapHint,
                new Insets(14)
        );

        // --------------------------------------------------------
        // Map stack
        // --------------------------------------------------------

        StackPane mapStack =
                new StackPane(
                        mapContainer,
                        mapHint
                );

        // --------------------------------------------------------
        // Profile summary
        // --------------------------------------------------------

        Label profileTitle =
                new Label(
                        "YOUR SETUP PROFILE"
                );

        profileTitle.setStyle(
                "-fx-text-fill: " +
                TEXT_MUTED +
                ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Label profileDesc =
                new Label(
                        "Student · Photographer · Content Creator — " +
                        "Podcasting, Book Cafes, Workshops"
                );

        profileDesc.setWrapText(
                true
        );

        profileDesc.setStyle(
                body(12)
        );

        VBox profileSummary =
                new VBox(
                        4,
                        profileTitle,
                        profileDesc
                );

        profileSummary.setPadding(
                new Insets(14)
        );

        profileSummary.setStyle(
                "-fx-background-color: " +
                PILL_BG +
                ";" +
                "-fx-background-radius: 12;"
        );

        // --------------------------------------------------------
        // Right column
        // --------------------------------------------------------

        VBox rightColumn =
                new VBox(
                        14,
                        mapStack,
                        profileSummary
                );

        rightColumn.setMaxWidth(
                Double.MAX_VALUE
        );

        return rightColumn;
    }

    // ============================================================
    // MAP HTML
    // ============================================================

    private static String getMapHtml() {

        return """
                <!DOCTYPE html>

                <html>

                <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width,
                               initial-scale=1.0">

                <link
                    rel="stylesheet"
                    href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                />

                <script
                    src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js">
                </script>

                <style>

                html,
                body {

                    margin: 0;
                    padding: 0;

                    width: 100%;
                    height: 100%;

                    overflow: hidden;

                }

                #map {

                    width: 100%;
                    height: 100%;

                }

                .leaflet-container {

                    font-family: "Segoe UI", Arial, sans-serif;

                    background: #F5F0EB;

                }

                </style>

                </head>

                <body>

                <div id="map"></div>

                <script>

                let map = null;

                let marker = null;

                let radiusCircle = null;

                const defaultLat = 18.5204;

                const defaultLng = 73.8567;

                const defaultRadius = 10000;


                // =================================================
                // INITIALIZE MAP
                // =================================================

                function initializeMap() {

                    map = L.map(
                        'map',
                        {
                            zoomControl: true,
                            attributionControl: true
                        }
                    ).setView(
                        [
                            defaultLat,
                            defaultLng
                        ],
                        13
                    );


                    // OpenStreetMap tiles

                    L.tileLayer(
                        'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                        {
                            maxZoom: 19,

                            attribution:
                                '&copy; OpenStreetMap contributors'
                        }
                    ).addTo(map);


                    // Marker

                    marker =
                        L.marker(
                            [
                                defaultLat,
                                defaultLng
                            ],
                            {
                                draggable: true
                            }
                        ).addTo(map);


                    // Popup

                    marker.bindPopup(
                        '<b>Selected Location</b><br>' +
                        'Drag this marker or click the map.'
                    );


                    // Radius

                    radiusCircle =
                        L.circle(
                            [
                                defaultLat,
                                defaultLng
                            ],
                            {
                                radius:
                                    defaultRadius,

                                color:
                                    '#A8571F',

                                weight: 2,

                                fillColor:
                                    '#A8571F',

                                fillOpacity:
                                    0.08
                            }
                        ).addTo(map);


                    // Map click

                    map.on(
                        'click',
                        function(event) {

                            setLocation(
                                event.latlng.lat,
                                event.latlng.lng
                            );

                        }
                    );


                    // Marker drag

                    marker.on(
                        'dragend',
                        function() {

                            const position =
                                marker.getLatLng();

                            setLocation(
                                position.lat,
                                position.lng
                            );

                        }
                    );


                    // Make sure tiles render correctly

                    setTimeout(
                        function() {

                            map.invalidateSize();

                        },
                        300
                    );

                }


                // =================================================
                // SELECT LOCATION
                // =================================================

                function setLocation(
                    lat,
                    lng
                ) {

                    if (!map ||
                        !marker ||
                        !radiusCircle) {

                        return;

                    }


                    marker.setLatLng(
                        [
                            lat,
                            lng
                        ]
                    );


                    radiusCircle.setLatLng(
                        [
                            lat,
                            lng
                        ]
                    );


                    map.panTo(
                        [
                            lat,
                            lng
                        ]
                    );


                    if (window.javaBridge) {

                        window.javaBridge.locationSelected(
                            lat,
                            lng
                        );

                    }

                }


                // =================================================
                // MOVE MAP
                // =================================================

                function moveTo(
                    lat,
                    lng
                ) {

                    if (!map)
                        return;


                    setLocation(
                        lat,
                        lng
                    );


                    map.setView(
                        [
                            lat,
                            lng
                        ],
                        14,
                        {
                            animate: true
                        }
                    );

                }


                // =================================================
                // RADIUS
                // =================================================

                function updateRadius(
                    km
                ) {

                    if (!radiusCircle)
                        return;


                    radiusCircle.setRadius(
                        km * 1000
                    );

                }


                // =================================================
                // CURRENT LOCATION
                // =================================================

                function getCurrentLocation() {

                    if (!navigator.geolocation) {

                        if (window.javaBridge) {

                            window.javaBridge.locationError(
                                "Geolocation is not supported."
                            );

                        }

                        return;

                    }


                    navigator.geolocation.getCurrentPosition(

                        function(position) {

                            const lat =
                                position.coords.latitude;

                            const lng =
                                position.coords.longitude;


                            moveTo(
                                lat,
                                lng
                            );

                        },

                        function(error) {

                            if (window.javaBridge) {

                                window.javaBridge.locationError(
                                    "Unable to access your current location."
                                );

                            }

                        },

                        {
                            enableHighAccuracy: true,

                            timeout: 10000,

                            maximumAge: 0
                        }

                    );

                }

                </script>

                </body>

                </html>
                """;
    }

    // ============================================================
    // JAVA ↔ JAVASCRIPT BRIDGE
    // ============================================================

    public static class JavaBridge {

        public void locationSelected(
                double latitude,
                double longitude) {

            selectedLatitude =
                    latitude;

            selectedLongitude =
                    longitude;

            System.out.println(
                    "Selected location: " +
                    latitude +
                    ", " +
                    longitude
            );

            Platform.runLater(
                    () -> {

                        if (selectedLocationLabel
                                != null) {

                            selectedLocationLabel.setText(

                                    String.format(
                                            "📍 Selected Location%n" +
                                            "Latitude: %.5f%n" +
                                            "Longitude: %.5f",
                                            latitude,
                                            longitude
                                    )
                            );
                        }

                    }
            );
        }


        public void locationError(
                String message) {

            Platform.runLater(
                    () -> {

                        if (selectedLocationLabel
                                != null) {

                            selectedLocationLabel.setText(
                                    "⚠ " + message
                            );

                        }

                    }
            );

            System.out.println(
                    "Location error: " +
                    message
            );
        }
    }

    // ============================================================
    // MOVE MAP FROM JAVA
    // ============================================================

    private static void moveMapTo(
            double latitude,
            double longitude) {

        selectedLatitude =
                latitude;

        selectedLongitude =
                longitude;

        if (mapEngine == null)
            return;

        try {

            mapEngine.executeScript(
                    "moveTo(" +
                    latitude +
                    "," +
                    longitude +
                    ");"
            );

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    // ============================================================
    // SECTION LABEL
    // ============================================================

    private static Label sectionLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: " +
                TEXT_DARK +
                ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // ============================================================
    // CARD
    // ============================================================

    private static VBox sectionCard(
            javafx.scene.Node... children) {

        VBox box =
                new VBox(
                        12,
                        children
                );

        box.setPadding(
                new Insets(18)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " +
                BORDER +
                ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
        );

        return box;
    }

    // ============================================================
    // SPACER
    // ============================================================

    private static Region spacer() {

        Region region =
                new Region();

        HBox.setHgrow(
                region,
                Priority.ALWAYS
        );

        return region;
    }

    // ============================================================
    // BOTTOM BAR
    // ============================================================

    private static HBox buildBottomBar(
            Runnable onBack,
            java.util.function.Consumer<com.eklavya.spotfit.model.User> onFinishSetup) {

        var backButton =
                outlineButton(
                        "←  Back"
                );

        backButton.setPrefWidth(
                140
        );

        backButton.setOnAction(
                e -> onBack.run()
        );


        var finishButton =
                primaryButton(
                        "Finish Setup  →"
                );

        finishButton.setPrefWidth(
                180
        );

        finishButton.setOnAction(
                e -> {
                    finishButton.setDisable(true);
                    finishButton.setText("Finishing...");

                    com.eklavya.spotfit.controller.user.UserProfileController profileController =
                            new com.eklavya.spotfit.controller.user.UserProfileController();
                    profileController.saveLocationPreferences(
                            "Pune, Maharashtra",
                            selectedRadiusKm,
                            0L,
                            "en",
                            true
                    ).whenComplete((result, throwable) -> Platform.runLater(() -> {
                        finishButton.setDisable(false);
                        finishButton.setText("Finish Setup  →");
                        if (result != null && result.isSuccess() && result.getData() != null) {
                            onFinishSetup.accept(result.getData());
                        } else {
                            onFinishSetup.accept(null);
                        }
                    }));
                }
        );


        Region spacerRegion =
                new Region();

        HBox.setHgrow(
                spacerRegion,
                Priority.ALWAYS
        );


        HBox bar =
                new HBox(
                        backButton,
                        spacerRegion,
                        finishButton
                );

        bar.setAlignment(
                Pos.CENTER
        );

        bar.setPadding(
                new Insets(
                        16,
                        50,
                        16,
                        50
                )
        );

        bar.setStyle(
                "-fx-background-color: " +
                BG_PAGE +
                ";" +
                "-fx-border-color: " +
                BORDER +
                ";" +
                "-fx-border-width: 1 0 0 0;"
        );

        return bar;
    }
}