// package com.eklavya.spotfit.view.owner;

// import com.eklavya.spotfit.controller.admin.MockDataStore;
// import com.eklavya.spotfit.controller.owner.OwnerCustomerController;
// import com.eklavya.spotfit.model.Booking;
// import com.eklavya.spotfit.model.Customer;
// import javafx.geometry.Insets;
// import javafx.scene.control.*;
// import javafx.scene.control.cell.PropertyValueFactory;
// import javafx.scene.layout.*;
// import java.util.*;

// public class CustomerCRMPage {
//     public static class CustomerRow {
//         private final String name, contact, activity, spending;
//         CustomerRow(String n,String c,String a,String s){name=n;contact=c;activity=a;spending=s;}
//         public String getName(){return name;} public String getContact(){return contact;} public String getActivity(){return activity;} public String getSpending(){return spending;}
//     }
//     public VBox getContent(BorderPane rightPane) {
//         String scope = MockDataStore.getInstance().getSelectedSpaceId();
//         OwnerCustomerController controller = new OwnerCustomerController();
//         Map<String,CustomerRow> rows = new LinkedHashMap<>();
//         for (Customer customer : controller.getCustomers()) {
//             List<Booking> history = controller.getCustomerBookings(customer.getCustomerId(), scope);
//             if (!history.isEmpty()) rows.put(customer.getCustomerId(), new CustomerRow(customer.getName(), customer.getEmail(), history.size()+" bookings", "₹"+String.format("%,.0f", history.stream().mapToDouble(Booking::getAmount).sum())));
//         }
//         HBox stats = new HBox(16, OwnerUi.statCard("Customers", String.valueOf(rows.size()), "Current space scope"), OwnerUi.statCard("Privacy", "Protected", "Phone numbers hidden"), OwnerUi.statCard("Communication", "SpotFit", "In-app channel"));
//         TableView<CustomerRow> table = new TableView<>(); table.getItems().setAll(rows.values());
//         table.getColumns().addAll(col("Customer","name"), col("Contact","contact"), col("Activity","activity"), col("Booking Value","spending")); table.setPrefHeight(360);
//         VBox card = OwnerUi.card(); card.getChildren().addAll(OwnerUi.heading("Space Customers",16), table);
//         VBox content = new VBox(20,stats,card); content.setPadding(new Insets(0,0,0,0));
//         return content;
//     }
//     private TableColumn<CustomerRow,String> col(String title,String property){TableColumn<CustomerRow,String> c=new TableColumn<>(title);c.setCellValueFactory(new PropertyValueFactory<>(property));return c;}
// }

package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.PaymentStatus;
import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerBookingController;
import com.eklavya.spotfit.model.Booking;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerCRMPage {

    public static class CustomerRow {

        private final String name;
        private final String contact;
        private final String activity;
        private final String spending;

        public CustomerRow(
                String name,
                String contact,
                String activity,
                String spending) {

            this.name = name;
            this.contact = contact;
            this.activity = activity;
            this.spending = spending;
        }

        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }

        public String getActivity() {
            return activity;
        }

        public String getSpending() {
            return spending;
        }
    }

    public VBox getContent(BorderPane rightPane) {

        String selectedScope =
                MockDataStore.getInstance()
                        .getSelectedSpaceId();

        /*
         * "ALL" means fetch every booking belonging
         * to the currently authenticated Owner.
         */
        String spaceFilter =
                selectedScope == null
                        || selectedScope.isBlank()
                        || "ALL".equalsIgnoreCase(selectedScope)
                        ? null
                        : selectedScope;

        OwnerBookingController controller =
                new OwnerBookingController();

        // -----------------------------------------------------
        // STAT CARDS
        // -----------------------------------------------------

        VBox customersStat =
                OwnerUi.statCard(
                        "Customers",
                        "...",
                        "Loading Firestore data");

        VBox privacyStat =
                OwnerUi.statCard(
                        "Privacy",
                        "Protected",
                        "Phone numbers hidden");

        VBox communicationStat =
                OwnerUi.statCard(
                        "Communication",
                        "SpotFit",
                        "Registered customers");

        HBox stats = new HBox(
                16,
                customersStat,
                privacyStat,
                communicationStat);

        for (javafx.scene.Node node : stats.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        // -----------------------------------------------------
        // CUSTOMER TABLE
        // -----------------------------------------------------

        TableView<CustomerRow> table =
                new TableView<>();

        TableColumn<CustomerRow, String> customerColumn =
                column("Customer", "name");

        TableColumn<CustomerRow, String> contactColumn =
                column("Contact", "contact");

        TableColumn<CustomerRow, String> activityColumn =
                column("Paid Bookings", "activity");

        TableColumn<CustomerRow, String> spendingColumn =
                column("Booking Value", "spending");

        customerColumn.setPrefWidth(210);
        contactColumn.setPrefWidth(280);
        activityColumn.setPrefWidth(180);
        spendingColumn.setPrefWidth(180);

        table.getColumns().addAll(
                customerColumn,
                contactColumn,
                activityColumn,
                spendingColumn);

        table.setPrefHeight(430);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        Label loading =
                new Label("Loading paid customers from Firestore...");

        loading.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + OwnerUi.TEXT_MUTED + ";");

        table.setPlaceholder(loading);

        // -----------------------------------------------------
        // TABLE CARD
        // -----------------------------------------------------

        VBox tableCard = OwnerUi.card();
        tableCard.setSpacing(15);

        Label tableTitle =
                OwnerUi.heading(
                        "Paid Customers",
                        18);

        Label subtitle =
                OwnerUi.body(
                        "Registered customers with successfully paid bookings for your Spaces.",
                        12,
                        OwnerUi.TEXT_MUTED);

        tableCard.getChildren().addAll(
                tableTitle,
                subtitle,
                table);

        VBox content = new VBox(
                20,
                stats,
                tableCard);

        content.setPadding(
                new Insets(0, 0, 0, 0));

        // -----------------------------------------------------
        // FETCH REAL FIRESTORE BOOKINGS
        // -----------------------------------------------------

        controller
                .getBookings(
                        spaceFilter,
                        null,
                        null)
                .whenComplete((bookings, error) ->
                        Platform.runLater(() -> {

                            if (error != null) {
                                Label errorLabel =
                                        new Label(
                                                "Could not load customer bookings.");

                                errorLabel.setStyle(
                                        "-fx-font-size: 13px;" +
                                        "-fx-text-fill: #C62828;");

                                table.setPlaceholder(errorLabel);

                                replaceCustomersStat(
                                        stats,
                                        "0",
                                        "Unable to load Firestore data");

                                return;
                            }

                            renderPaidCustomers(
                                    table,
                                    stats,
                                    bookings);
                        }));

        return content;
    }

    private void renderPaidCustomers(
            TableView<CustomerRow> table,
            HBox stats,
            List<Booking> bookings) {

        Map<String, List<Booking>> customerBookings =
                new LinkedHashMap<>();

        if (bookings != null) {
            for (Booking booking : bookings) {

                if (booking == null) {
                    continue;
                }

                /*
                 * Only include bookings whose demo/real
                 * payment was successfully completed.
                 */
                if (!PaymentStatus.SUCCESSFUL.equalsIgnoreCase(
                        booking.getPaymentStatus())) {
                    continue;
                }

                String customerKey =
                        getCustomerKey(booking);

                customerBookings
                        .computeIfAbsent(
                                customerKey,
                                key -> new java.util.ArrayList<>())
                        .add(booking);
            }
        }

        table.getItems().clear();

        for (List<Booking> history :
                customerBookings.values()) {

            if (history.isEmpty()) {
                continue;
            }

            Booking firstBooking =
                    history.get(0);

            String customerName =
                    valueOrFallback(
                            firstBooking.getCustomerName(),
                            "SpotFit Customer");

            String customerEmail =
                    valueOrFallback(
                            firstBooking.getCustomerEmail(),
                            "Registered customer");

            double totalSpending =
                    history.stream()
                            .mapToDouble(
                                    this::getBookingAmount)
                            .sum();

            int bookingCount =
                    history.size();

            String bookingText =
                    bookingCount == 1
                            ? "1 paid booking"
                            : bookingCount + " paid bookings";

            CustomerRow row =
                    new CustomerRow(
                            customerName,
                            customerEmail,
                            bookingText,
                            "₹" + String.format(
                                    "%,.2f",
                                    totalSpending));

            table.getItems().add(row);
        }

        if (table.getItems().isEmpty()) {
            Label emptyLabel =
                    new Label(
                            "No successfully paid customers yet.");

            emptyLabel.setStyle(
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: " +
                    OwnerUi.TEXT_MUTED + ";");

            table.setPlaceholder(emptyLabel);
        }

        replaceCustomersStat(
                stats,
                String.valueOf(
                        customerBookings.size()),
                "Real Firestore bookings");
    }

    private String getCustomerKey(
            Booking booking) {

        if (booking.getCustomerId() != null
                && !booking.getCustomerId().isBlank()) {

            return booking
                    .getCustomerId()
                    .trim();
        }

        if (booking.getCustomerEmail() != null
                && !booking.getCustomerEmail().isBlank()) {

            return booking
                    .getCustomerEmail()
                    .trim()
                    .toLowerCase();
        }

        return booking.getBookingId();
    }

    private double getBookingAmount(
            Booking booking) {

        if (booking.getTotalAmountPaise() > 0) {
            return booking.getTotalAmountPaise()
                    / 100.0;
        }

        return booking.getAmount();
    }

    private String valueOrFallback(
            String value,
            String fallback) {

        return value != null
                && !value.isBlank()
                ? value
                : fallback;
    }

    private void replaceCustomersStat(
            HBox stats,
            String value,
            String description) {

        VBox updatedStat =
                OwnerUi.statCard(
                        "Customers",
                        value,
                        description);

        HBox.setHgrow(
                updatedStat,
                Priority.ALWAYS);

        stats.getChildren().set(
                0,
                updatedStat);
    }

    private TableColumn<CustomerRow, String> column(
            String title,
            String property) {

        TableColumn<CustomerRow, String> column =
                new TableColumn<>(title);

        column.setCellValueFactory(
                new PropertyValueFactory<>(property));

        return column;
    }
}