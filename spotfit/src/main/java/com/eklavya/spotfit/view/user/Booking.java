package com.eklavya.spotfit.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import static com.eklavya.spotfit.util.StyleConstants.*;
import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.model.Offering;

public class Booking {

        public static Scene getBookingScene(
                        com.eklavya.spotfit.model.Space space, Offering offering,
                        Runnable callBackAction) {

                BorderPane root = new BorderPane();

                root.setStyle(
                                "-fx-background-color: " + BG_PAGE + ";");

                root.setTop(
                                buildTopBar(
                                                space, offering,
                                                callBackAction));

                VBox content = buildContent(
                                space, offering);

                ScrollPane scroll = new ScrollPane(content);

                scroll.setFitToWidth(true);

                scroll.setHbarPolicy(
                                ScrollPane.ScrollBarPolicy.NEVER);

                scroll.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-background: transparent;" +
                                                "-fx-border-color: transparent;");

                root.setCenter(scroll);

                return new Scene(
                                root,
                                1280,
                                800);
        }

        private static HBox buildTopBar(
                        com.eklavya.spotfit.model.Space space, Offering offering,
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

                Button back = new Button("←  Space Details");

                back.setStyle(
                                "-fx-background-color: transparent;" +
                                                "-fx-text-fill: " + TEXT_MUTED + ";" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-cursor: hand;");

                back.setOnAction(
                                e -> callBackAction.run());

                Label title = new Label("Book your session");

                title.setStyle(
                                heading(19));

                bar.getChildren().addAll(
                                back,
                                title);

                return bar;
        }

        private static VBox buildContent(
                        com.eklavya.spotfit.model.Space space, Offering offering) {

                VBox content = new VBox(24);

                content.setPadding(
                                new Insets(32, 48, 50, 48));

                content.getChildren().add(buildHeader(offering));

                String btType = offering.getBookingType() != null ? offering.getBookingType().toUpperCase() : "";
                switch (btType) {
                        case "SLOT":
                        case "PRACTICE_ROOM":
                                content.getChildren().addAll(
                                                buildDateSection(),
                                                buildDurationSection(),
                                                buildTimeSection());
                                break;
                        case "SEAT":
                                content.getChildren().addAll(
                                                buildDateSection(),
                                                buildSeatSelectionSection());
                                break;
                        case "WORKSHOP":
                                content.getChildren().addAll(
                                                buildDateSection(),
                                                buildParticipantsSection());
                                break;
                        case "RENTAL":
                                content.getChildren().addAll(
                                                buildDateSection(),
                                                buildRentalDurationSection());
                                break;
                        case "VENUE":
                                content.getChildren().addAll(
                                                buildDateSection(),
                                                buildGuestCountSection(),
                                                buildRequirementsSection());
                                break;
                }

                content.getChildren().add(buildSummary(space, offering));

                return content;
        }

        private static VBox buildHeader(
                        Offering offering) {

                VBox header = new VBox(5);

                Label title = new Label(
                                "Book " + offering.getName());

                title.setStyle(
                                heading(27));

                Label subtitle = new Label(
                                offering.getDescription());

                subtitle.setWrapText(true);

                subtitle.setStyle(
                                body(12));

                header.getChildren().addAll(
                                title,
                                subtitle);

                return header;
        }

        private static VBox buildDateSection() {

                VBox section = new VBox(11);

                Label title = new Label(
                                "1. Choose a date");

                title.setStyle(
                                heading(17));

                DatePicker datePicker = new DatePicker();

                datePicker.setValue(
                                java.time.LocalDate.now()
                                                .plusDays(1));

                datePicker.setPrefWidth(260);
                datePicker.setPrefHeight(44);

                datePicker.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;");

                section.getChildren().addAll(
                                title,
                                datePicker);

                return section;
        }

        private static VBox buildDurationSection() {

                VBox section = new VBox(11);

                Label title = new Label(
                                "2. Choose duration");

                title.setStyle(
                                heading(17));

                ToggleGroup group = new ToggleGroup();

                HBox options = new HBox(10);

                options.getChildren().addAll(
                                durationButton(
                                                "1 hour",
                                                group,
                                                false),
                                durationButton(
                                                "2 hours",
                                                group,
                                                true),
                                durationButton(
                                                "3 hours",
                                                group,
                                                false));

                section.getChildren().addAll(
                                title,
                                options);

                return section;
        }

        private static RadioButton durationButton(
                        String text,
                        ToggleGroup group,
                        boolean selected) {

                RadioButton button = new RadioButton(text);

                button.setToggleGroup(group);

                button.setSelected(selected);

                button.setPrefHeight(42);

                button.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 12;" +
                                                "-fx-padding: 0 16;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";" +
                                                "-fx-cursor: hand;");

                return button;
        }

        private static VBox buildTimeSection() {

                VBox section = new VBox(11);

                Label title = new Label(
                                "3. Choose an available time");

                title.setStyle(
                                heading(17));

                Label subtitle = new Label(
                                "Available slots for your selected date.");

                subtitle.setStyle(
                                body(10));

                FlowPane slots = new FlowPane();

                slots.setHgap(10);
                slots.setVgap(10);

                ToggleGroup timeGroup = new ToggleGroup();

                slots.getChildren().addAll(
                                timeButton(
                                                "10:00 AM – 12:00 PM",
                                                timeGroup,
                                                false),
                                timeButton(
                                                "12:00 PM – 2:00 PM",
                                                timeGroup,
                                                false),
                                timeButton(
                                                "2:00 PM – 4:00 PM",
                                                timeGroup,
                                                false),
                                timeButton(
                                                "4:00 PM – 6:00 PM",
                                                timeGroup,
                                                true),
                                timeButton(
                                                "6:00 PM – 8:00 PM",
                                                timeGroup,
                                                false));

                section.getChildren().addAll(
                                title,
                                subtitle,
                                slots);

                return section;
        }

        private static RadioButton timeButton(
                        String text,
                        ToggleGroup group,
                        boolean selected) {

                RadioButton button = new RadioButton(text);

                button.setToggleGroup(group);

                button.setSelected(selected);

                button.setPrefHeight(44);

                button.setStyle(
                                "-fx-background-color: " +
                                                (selected
                                                                ? PILL_BG
                                                                : CARD_WHITE)
                                                +
                                                ";" +
                                                "-fx-background-radius: 12;" +
                                                "-fx-border-color: " +
                                                (selected
                                                                ? PRIMARY
                                                                : BORDER)
                                                +
                                                ";" +
                                                "-fx-border-radius: 12;" +
                                                "-fx-padding: 0 14;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 10px;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";" +
                                                "-fx-cursor: hand;");

                return button;
        }

        private static VBox buildSeatSelectionSection() {
                VBox section = new VBox(11);
                Label title = new Label("2. Choose number of seats");
                title.setStyle(heading(17));
                ComboBox<Integer> seats = new ComboBox<>();
                seats.getItems().addAll(1, 2, 3, 4, 5, 10);
                seats.getSelectionModel().selectFirst();
                seats.setPrefHeight(44);
                seats.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px;");
                section.getChildren().addAll(title, seats);
                return section;
        }

        private static VBox buildParticipantsSection() {
                VBox section = new VBox(11);
                Label title = new Label("2. Number of Participants");
                title.setStyle(heading(17));
                ComboBox<Integer> count = new ComboBox<>();
                count.getItems().addAll(1, 2, 3, 4, 5);
                count.getSelectionModel().selectFirst();
                count.setPrefHeight(44);
                count.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 11px;");
                section.getChildren().addAll(title, count);
                return section;
        }

        private static VBox buildRentalDurationSection() {
                VBox section = new VBox(11);
                Label title = new Label("2. Rental Duration");
                title.setStyle(heading(17));
                ToggleGroup group = new ToggleGroup();
                HBox options = new HBox(10);
                options.getChildren().addAll(
                                durationButton("1 Day", group, true),
                                durationButton("3 Days", group, false),
                                durationButton("1 Week", group, false));
                section.getChildren().addAll(title, options);
                return section;
        }

        private static VBox buildGuestCountSection() {
                VBox section = new VBox(11);
                Label title = new Label("2. Guest Count");
                title.setStyle(heading(17));
                TextField guests = new TextField("50");
                guests.setPrefHeight(44);
                guests.setMaxWidth(200);
                guests.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 13px;");
                section.getChildren().addAll(title, guests);
                return section;
        }

        private static VBox buildRequirementsSection() {
                VBox section = new VBox(11);
                Label title = new Label("3. Additional Requirements");
                title.setStyle(heading(17));
                TextArea reqs = new TextArea();
                reqs.setPromptText("Enter catering, seating arrangement requests...");
                reqs.setPrefHeight(100);
                reqs.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px;");
                section.getChildren().addAll(title, reqs);
                return section;
        }

        private static VBox buildSummary(
                        com.eklavya.spotfit.model.Space space, Offering offering) {

                VBox section = new VBox(14);

                section.setPadding(
                                new Insets(18));

                section.setStyle(
                                "-fx-background-color: " + CARD_WHITE + ";" +
                                                "-fx-background-radius: 16;" +
                                                "-fx-border-color: " + BORDER + ";" +
                                                "-fx-border-radius: 16;");

                Label title = new Label(
                                "Booking summary");

                title.setStyle(
                                heading(17));

                HBox spaceRow = summaryRow(
                                "Space",
                                space.getName());

                HBox price = summaryRow(
                                "Total",
                                "₹" + offering.getPrice());

                String btName = offering.getBookingType() != null ? offering.getBookingType().toUpperCase() : "";
                if ("SEAT".equals(btName)) {
                        section.getChildren().addAll(title, spaceRow, summaryRow("Seats", "1"), price);
                } else if ("WORKSHOP".equals(btName)) {
                        section.getChildren().addAll(title, spaceRow, summaryRow("Participants", "1"), price);
                } else if ("RENTAL".equals(btName)) {
                        section.getChildren().addAll(title, spaceRow, summaryRow("Duration", "1 Day"), price);
                } else if ("VENUE".equals(btName)) {
                        section.getChildren().addAll(title, spaceRow, summaryRow("Guests", "50"), price);
                } else {
                        section.getChildren().addAll(title, spaceRow, summaryRow("Date", "Tomorrow"),
                                        summaryRow("Time", "4:00 PM – 6:00 PM"), price);
                }

                Separator separator = new Separator();

                String btnText = "Continue to payment  →";
                switch (btName) {
                        case "SEAT":
                                btnText = "Reserve Seats  →";
                                break;
                        case "WORKSHOP":
                                btnText = "Join Workshop  →";
                                break;
                        case "RENTAL":
                                btnText = "Rent Item  →";
                                break;
                        case "VENUE":
                                btnText = "Submit Request  →";
                                break;
                        default:
                                break;
                }

                Button continueButton = new Button(
                                btnText);

                continueButton.setPrefHeight(46);

                continueButton.setMaxWidth(
                                Double.MAX_VALUE);

                continueButton.setStyle(
                                "-fx-background-color: " + PRIMARY + ";" +
                                                "-fx-background-radius: 23;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-cursor: hand;");

                continueButton.setOnAction(e -> {
                    if (space != null && space.getSchemaVersion() == 2) {
                        continueButton.setDisable(true);
                        continueButton.setText("Creating Booking Intent...");

                        com.eklavya.spotfit.model.BookingRequest req = new com.eklavya.spotfit.model.BookingRequest(
                                space.getSpaceId(), space.getBookingType()
                        );
                        req.setSelectedDate(java.time.LocalDate.now().plusDays(1));
                        req.setStartTime(java.time.LocalTime.of(10, 0));
                        req.setEndTime(java.time.LocalTime.of(11, 0));
                        req.setDurationHours(1);
                        req.setParticipantCount(1);
                        req.setQuantity(1);

                        com.eklavya.spotfit.controller.user.CustomerMarketplaceController controller =
                                new com.eklavya.spotfit.controller.user.CustomerMarketplaceController();

                        controller.createV2Booking(req).thenAccept(res -> {
                            javafx.application.Platform.runLater(() -> {
                                continueButton.setDisable(false);
                                continueButton.setText("Continue to Payment");

                                Alert alert = new Alert(res.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                                if (res.isSuccess() && res.getData() != null) {
                                    com.eklavya.spotfit.model.Booking b = res.getData();
                                    alert.setTitle("Booking Request Created");
                                    alert.setHeaderText("Status: PENDING_PAYMENT (Provisional Quote: Rs." +  String.format("%.2f", b.getAmount()) + ")");
                                    alert.setContentText("Booking ID: " + b.getBookingId() + "\n\n" +
                                            "Booking request created. Payment is pending. Availability is not reserved until payment is successfully verified.");
                                    alert.showAndWait();

                                    com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                            MyBookings.getMyBookingsScene(() -> com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().setScene(UserDash.getUserDashScene()))
                                    );
                                } else {
                                    alert.setTitle("Booking Creation Failed");
                                    alert.setHeaderText("Unable to create booking intent");
                                    alert.setContentText(res.getMessage() != null ? res.getMessage() : "Unknown error occurred.");
                                    alert.showAndWait();
                                }
                            });
                        });
                    } else {
                        com.eklavya.spotfit.model.Booking dummyBooking = new com.eklavya.spotfit.model.Booking(
                                space, offering, "Tomorrow", "4:00 PM – 6:00 PM", offering.getPrice(), "0000"
                        );
                        com.eklavya.spotfit.navigation.AppNavigator.setScene(
                            Payment.getPaymentScene(dummyBooking,
                                () -> com.eklavya.spotfit.navigation.AppNavigator.setScene(
                                    Booking.getBookingScene(space, offering,
                                        () -> com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage()
                                                .setScene(SpaceDetails.getSpaceDetailsScene(space,
                                                    () -> com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage()
                                                            .setScene(Explore.getExploreScene(
                                                                () -> com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage()
                                                                        .setScene(UserDash.getUserDashScene())))))))));
                    }
                });

                section.getChildren().addAll(
                                separator,
                                continueButton);

                return section;
        }

        private static HBox summaryRow(
                        String label,
                        String value) {

                HBox row = new HBox();

                row.setAlignment(
                                Pos.CENTER_LEFT);

                Label left = new Label(label);

                left.setStyle(
                                body(11));

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                Label right = new Label(value);

                right.setStyle(
                                "-fx-font-family: " + FONT + ";" +
                                                "-fx-font-size: 11px;" +
                                                "-fx-font-weight: bold;" +
                                                "-fx-text-fill: " + TEXT_DARK + ";");

                row.getChildren().addAll(
                                left,
                                spacer,
                                right);

                return row;
        }

}
