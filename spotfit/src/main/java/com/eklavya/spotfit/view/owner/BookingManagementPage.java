package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerBookingController;
import com.eklavya.spotfit.model.Booking;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class BookingManagementPage {

    public VBox getContent(BorderPane rightPane) {
        String scope = MockDataStore.getInstance().getSelectedSpaceId();
        OwnerBookingController controller = new OwnerBookingController();
        HBox stats = new HBox(16,
                OwnerUi.statCard("Bookings", "0", "Current scope"),
                OwnerUi.statCard("Confirmed", "0", null),
                OwnerUi.statCard("Checked In", "0", null),
                OwnerUi.statCard("Completed", "0", null));
        TextField search = OwnerUi.textField("Search booking ID or customer...");
        ComboBox<String> status = new ComboBox<>(); status.getItems().addAll("All", "PENDING_PAYMENT", "CONFIRMED", "CHECKED_IN", "COMPLETED", "CANCELLED", "REFUNDED"); status.setValue("All");
        TableView<Booking> table = new TableView<>();
        table.getColumns().addAll(col("Booking ID","bookingId"), col("Customer","customerName"), col("Offering","offeringName"), col("Date","bookingDate"), col("Time","slotTime"), col("Status","status"));
        
        Runnable reload = () -> {
            controller.getBookings(scope, "All".equals(status.getValue()) ? null : status.getValue(), search.getText())
                .thenAccept(bookings -> javafx.application.Platform.runLater(() -> {
                    fill(table, bookings);
                    ((Label)((VBox)stats.getChildren().get(0)).getChildren().get(1)).setText(String.valueOf(bookings.size()));
                    ((Label)((VBox)stats.getChildren().get(1)).getChildren().get(1)).setText(String.valueOf(count(bookings,"CONFIRMED")));
                    ((Label)((VBox)stats.getChildren().get(2)).getChildren().get(1)).setText(String.valueOf(count(bookings,"CHECKED_IN")));
                    ((Label)((VBox)stats.getChildren().get(3)).getChildren().get(1)).setText(String.valueOf(count(bookings,"COMPLETED")));
                }));
        };

        search.textProperty().addListener((o,a,b) -> reload.run());
        status.setOnAction(e -> reload.run());
        reload.run();
        table.setPrefHeight(360);
        // Space Manager pattern: clicking a row swaps rightPane's own center
        // for the detail page's content - no captured Scene field needed.
        table.setRowFactory(tv -> {
            TableRow<Booking> row = new TableRow<>();
            row.setStyle("-fx-cursor:hand;");
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    rightPane.setCenter(OwnerUi.scroll(new BookingDetailsPage().getContent(rightPane)));
                }
            });
            return row;
        });
        VBox card = OwnerUi.card(); card.getChildren().addAll(new HBox(12, search, status), table);
        VBox content = new VBox(20, stats, card); content.setPadding(new Insets(0,0,0,0));
        return content;
    }
    private long count(List<Booking> list, String status) { return list.stream().filter(b -> status.equals(b.getStatus())).count(); }
    private void fill(TableView<Booking> table, List<Booking> values) { table.getItems().setAll(values); }
    private TableColumn<Booking,String> col(String title, String property) { TableColumn<Booking,String> c = new TableColumn<>(title); c.setCellValueFactory(new PropertyValueFactory<>(property)); return c; }
}

