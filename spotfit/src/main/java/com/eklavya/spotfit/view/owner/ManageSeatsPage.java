package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.SeatSelectionMode;
import com.eklavya.spotfit.constants.SeatStatus;
import com.eklavya.spotfit.controller.owner.SeatController;
import com.eklavya.spotfit.model.Seat;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.bookingconfig.SeatBookingConfig;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Owner UI Page for managing Seats in a V2 SEAT Space.
 */
public class ManageSeatsPage {

    private final Space space;
    private final SeatController seatController;
    private final VBox container;
    private final VBox seatsListBox;

    public ManageSeatsPage(Space space) {
        this.space = space;
        this.seatController = new SeatController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(24));
        this.container.setStyle("-fx-background-color: " + OwnerUi.BACKGROUND + ";");
        this.seatsListBox = new VBox(12);
        buildUi();
    }

    public Parent getContent() {
        return container;
    }

    private void buildUi() {
        container.getChildren().clear();

        // Header Navigation & Title
        Button backBtn = OwnerUi.secondaryButton("← Back to Space Details");
        backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceDetails(space));

        HBox topRow = new HBox(16, backBtn);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = OwnerUi.heading("Manage Seats — " + (space != null ? space.getSpaceName() : ""), 22);
        Label subtitleLbl = OwnerUi.body("Configure and update seat units for this space.", 14, OwnerUi.TEXT_MUTED);

        container.getChildren().addAll(topRow, titleLbl, subtitleLbl);

        if (space == null || space.getSchemaVersion() != 2 || !"SEAT".equalsIgnoreCase(space.getBookingType())) {
            Label errLbl = OwnerUi.body("Seat management is only applicable for V2 SEAT Spaces.", 14, OwnerUi.ERROR);
            container.getChildren().add(errLbl);
            return;
        }

        SeatBookingConfig config = (space.getBookingConfig() instanceof SeatBookingConfig)
                ? (SeatBookingConfig) space.getBookingConfig()
                : null;

        String mode = (config != null && config.getSelectionMode() != null)
                ? config.getSelectionMode().toUpperCase()
                : SeatSelectionMode.AUTO_ASSIGN;

        if (SeatSelectionMode.AUTO_ASSIGN.equals(mode)) {
            VBox autoBox = OwnerUi.card();
            Label autoTitle = OwnerUi.heading("Automatic Seat Assignment Mode", 16);
            Label autoMsg = OwnerUi.body("Seats are assigned automatically for this space based on capacity ("
                    + space.getCapacity()
                    + "). Individual seat setup is not required.", 14, OwnerUi.TEXT_MUTED);
            autoBox.getChildren().addAll(autoTitle, autoMsg);
            container.getChildren().add(autoBox);
            return;
        }

        // Action Row for manual seat modes (VISUAL_MAP, SEAT_LIST, TABLE_LIST)
        Button addSeatBtn = OwnerUi.primaryButton("+ Add Seat");
        addSeatBtn.setOnAction(e -> showSeatFormDialog(null, mode));

        HBox actionRow = new HBox(16, addSeatBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(actionRow, seatsListBox);

        loadSeats();
    }

    private void loadSeats() {
        seatsListBox.getChildren().clear();
        Label loadingLbl = OwnerUi.body("Loading seats...", 14, OwnerUi.TEXT_MUTED);
        seatsListBox.getChildren().add(loadingLbl);

        seatController.getSeatsForSpace(space.getSpaceId())
                .thenAccept(res -> Platform.runLater(() -> {
                    seatsListBox.getChildren().clear();
                    if (res.isFailure()) {
                        Label errLbl = OwnerUi.body("Error loading seats: " + res.getMessage(), 14, OwnerUi.ERROR);
                        Button retryBtn = OwnerUi.secondaryButton("Retry");
                        retryBtn.setOnAction(e -> loadSeats());
                        seatsListBox.getChildren().addAll(errLbl, retryBtn);
                        return;
                    }

                    List<Seat> seats = res.getData();
                    if (seats == null || seats.isEmpty()) {
                        VBox emptyBox = OwnerUi.card();
                        Label emptyTitle = OwnerUi.heading("No Seats Defined", 16);
                        Label emptyMsg = OwnerUi.body("Click '+ Add Seat' above to configure seat units for this space.", 13, OwnerUi.TEXT_MUTED);
                        emptyBox.getChildren().addAll(emptyTitle, emptyMsg);
                        seatsListBox.getChildren().add(emptyBox);
                        return;
                    }

                    SeatBookingConfig config = (space.getBookingConfig() instanceof SeatBookingConfig)
                            ? (SeatBookingConfig) space.getBookingConfig()
                            : null;
                    String mode = (config != null && config.getSelectionMode() != null)
                            ? config.getSelectionMode().toUpperCase()
                            : "";

                    for (Seat seat : seats) {
                        seatsListBox.getChildren().add(buildSeatCard(seat, mode));
                    }
                }));
    }

    private VBox buildSeatCard(Seat seat, String mode) {
        VBox card = OwnerUi.card();

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = OwnerUi.heading(seat.getSeatName() + " (" + seat.getSeatCode() + ")", 16);
        Label statusBadge = OwnerUi.badge(seat.getStatus());
        Label activeBadge = OwnerUi.badge(seat.isActive() ? "ACTIVE" : "INACTIVE");

        header.getChildren().addAll(nameLbl, statusBadge, activeBadge);

        String details = String.format("Section: %s | Row: %s | Col: %d | Capacity: %d",
                val(seat.getSection()), val(seat.getRowLabel()), seat.getColumnNumber(), seat.getCapacity());
        Label detailsLbl = OwnerUi.body(details, 13, OwnerUi.TEXT_MUTED);

        String priceStr = seat.getPriceOverridePaise() > 0
                ? "Price Override: ₹" + new BigDecimal(seat.getPriceOverridePaise()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                : "Price: Standard Space Base Price";
        Label priceLbl = OwnerUi.body(priceStr, 13, OwnerUi.PRIMARY);

        HBox actions = new HBox(12);
        Button editBtn = OwnerUi.secondaryButton("Edit");
        editBtn.setOnAction(e -> showSeatFormDialog(seat, mode));

        Button toggleActiveBtn = OwnerUi.secondaryButton(seat.isActive() ? "Deactivate" : "Reactivate");
        toggleActiveBtn.setOnAction(e -> {
            toggleActiveBtn.setDisable(true);
            seatController.setSeatActive(space.getSpaceId(), seat.getSeatId(), !seat.isActive())
                    .thenAccept(res -> Platform.runLater(() -> {
                        if (res.isFailure()) {
                            showToast("Failed to update seat status: " + res.getMessage(), true);
                        } else {
                            loadSeats();
                        }
                    }));
        });

        actions.getChildren().addAll(editBtn, toggleActiveBtn);

        card.getChildren().addAll(header, detailsLbl, priceLbl, actions);
        return card;
    }

    private void showSeatFormDialog(Seat existingSeat, String mode) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existingSeat == null ? "Add Seat" : "Edit Seat");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));

        TextField nameField = OwnerUi.textField(existingSeat != null ? existingSeat.getSeatName() : "");
        TextField codeField = OwnerUi.textField(existingSeat != null ? existingSeat.getSeatCode() : "");
        TextField sectionField = OwnerUi.textField(existingSeat != null ? existingSeat.getSection() : "");
        TextField rowField = OwnerUi.textField(existingSeat != null ? existingSeat.getRowLabel() : "");
        TextField colField = OwnerUi.textField(existingSeat != null ? String.valueOf(existingSeat.getColumnNumber()) : "1");
        TextField capField = OwnerUi.textField(existingSeat != null ? String.valueOf(existingSeat.getCapacity()) : "1");

        String existingPriceRupees = (existingSeat != null && existingSeat.getPriceOverridePaise() > 0)
                ? new BigDecimal(existingSeat.getPriceOverridePaise()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString()
                : "";
        TextField priceField = OwnerUi.textField(existingPriceRupees);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(SeatStatus.AVAILABLE, SeatStatus.BLOCKED, SeatStatus.MAINTENANCE);
        statusCombo.setValue(existingSeat != null ? existingSeat.getStatus() : SeatStatus.AVAILABLE);

        if (!SeatSelectionMode.TABLE_LIST.equals(mode)) {
            capField.setDisable(true);
        }

        form.getChildren().addAll(
                OwnerUi.fieldLabel("Seat Name*"), nameField,
                OwnerUi.fieldLabel("Seat Code*"), codeField,
                OwnerUi.fieldLabel("Section"), sectionField,
                OwnerUi.fieldLabel("Row Label"), rowField,
                OwnerUi.fieldLabel("Column Number"), colField,
                OwnerUi.fieldLabel("Capacity (Table List Mode only)"), capField,
                OwnerUi.fieldLabel("Price Override (₹ Rupees, optional)"), priceField,
                OwnerUi.fieldLabel("Status"), statusCombo
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String code = codeField.getText().trim();
                    String section = sectionField.getText().trim();
                    String row = rowField.getText().trim();
                    int col = Integer.parseInt(colField.getText().trim());
                    int cap = Integer.parseInt(capField.getText().trim());
                    long pricePaise = 0L;
                    if (!priceField.getText().isBlank()) {
                        pricePaise = new BigDecimal(priceField.getText().trim()).multiply(new BigDecimal(100)).longValue();
                    }

                    Seat seat = (existingSeat != null) ? existingSeat : new Seat();
                    seat.setSpaceId(space.getSpaceId());
                    seat.setSeatName(name);
                    seat.setSeatCode(code);
                    seat.setSection(section);
                    seat.setRowLabel(row);
                    seat.setColumnNumber(col);
                    seat.setCapacity(cap);
                    seat.setPriceOverridePaise(pricePaise);
                    seat.setStatus(statusCombo.getValue());

                    if (existingSeat == null) {
                        seatController.createSeat(space.getSpaceId(), seat)
                                .thenAccept(res -> Platform.runLater(() -> {
                                    if (res.isFailure()) {
                                        showToast("Failed to create seat: " + res.getMessage(), true);
                                    } else {
                                        loadSeats();
                                    }
                                }));
                    } else {
                        seatController.updateSeat(space.getSpaceId(), seat)
                                .thenAccept(res -> Platform.runLater(() -> {
                                    if (res.isFailure()) {
                                        showToast("Failed to update seat: " + res.getMessage(), true);
                                    } else {
                                        loadSeats();
                                    }
                                }));
                    }
                } catch (Exception ex) {
                    showToast("Invalid inputs: " + ex.getMessage(), true);
                }
            }
        });
    }

    private void showToast(String message, boolean isError) {
        Label toast = OwnerUi.body(message, 13, isError ? OwnerUi.ERROR : OwnerUi.SUCCESS);
        container.getChildren().add(0, toast);
    }

    private String val(String val) {
        return (val != null && !val.isBlank()) ? val : "N/A";
    }
}
