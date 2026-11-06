package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.WorkshopSessionStatus;
import com.eklavya.spotfit.controller.owner.WorkshopSessionController;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.WorkshopSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Owner UI Page for managing Workshop Sessions in a V2 WORKSHOP Space.
 */
public class ManageWorkshopSessionsPage {

    private final Space space;
    private final WorkshopSessionController sessionController;
    private final VBox container;
    private final VBox sessionsListBox;

    public ManageWorkshopSessionsPage(Space space) {
        this.space = space;
        this.sessionController = new WorkshopSessionController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(24));
        this.container.setStyle("-fx-background-color: " + OwnerUi.BACKGROUND + ";");
        this.sessionsListBox = new VBox(12);
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

        Label titleLbl = OwnerUi.heading("Manage Workshop Sessions — " + (space != null ? space.getSpaceName() : ""), 22);
        Label subtitleLbl = OwnerUi.body("Schedule and manage upcoming sessions for this workshop space.", 14, OwnerUi.TEXT_MUTED);

        container.getChildren().addAll(topRow, titleLbl, subtitleLbl);

        if (space == null || space.getSchemaVersion() != 2 || !"WORKSHOP".equalsIgnoreCase(space.getBookingType())) {
            Label errLbl = OwnerUi.body("Workshop Session management is only applicable for V2 WORKSHOP Spaces.", 14, OwnerUi.ERROR);
            container.getChildren().add(errLbl);
            return;
        }

        Button addSessionBtn = OwnerUi.primaryButton("+ Add Session");
        addSessionBtn.setOnAction(e -> showSessionFormDialog(null));

        HBox actionRow = new HBox(16, addSessionBtn);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().addAll(actionRow, sessionsListBox);

        loadSessions();
    }

    private void loadSessions() {
        sessionsListBox.getChildren().clear();
        Label loadingLbl = OwnerUi.body("Loading workshop sessions...", 14, OwnerUi.TEXT_MUTED);
        sessionsListBox.getChildren().add(loadingLbl);

        sessionController.getSessionsForSpace(space.getSpaceId())
                .thenAccept(res -> Platform.runLater(() -> {
                    sessionsListBox.getChildren().clear();
                    if (res.isFailure()) {
                        Label errLbl = OwnerUi.body("Error loading sessions: " + res.getMessage(), 14, OwnerUi.ERROR);
                        Button retryBtn = OwnerUi.secondaryButton("Retry");
                        retryBtn.setOnAction(e -> loadSessions());
                        sessionsListBox.getChildren().addAll(errLbl, retryBtn);
                        return;
                    }

                    List<WorkshopSession> sessions = res.getData();
                    if (sessions == null || sessions.isEmpty()) {
                        VBox emptyBox = OwnerUi.card();
                        Label emptyTitle = OwnerUi.heading("No Sessions Scheduled", 16);
                        Label emptyMsg = OwnerUi.body("Click '+ Add Session' above to schedule new workshop sessions.", 13, OwnerUi.TEXT_MUTED);
                        emptyBox.getChildren().addAll(emptyTitle, emptyMsg);
                        sessionsListBox.getChildren().add(emptyBox);
                        return;
                    }

                    for (WorkshopSession session : sessions) {
                        sessionsListBox.getChildren().add(buildSessionCard(session));
                    }
                }));
    }

    private VBox buildSessionCard(WorkshopSession session) {
        VBox card = OwnerUi.card();

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        ZoneId spaceZone = getSpaceZoneId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        ZonedDateTime startDt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(session.getStartAt()), spaceZone);
        ZonedDateTime endDt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(session.getEndAt()), spaceZone);

        Label timeLbl = OwnerUi.heading(startDt.format(formatter) + " – " + endDt.format(formatter), 16);
        Label statusBadge = OwnerUi.badge(session.getStatus());

        header.getChildren().addAll(timeLbl, statusBadge);

        String details = String.format("Capacity: %d | Booked Count: %d | Timezone: %s",
                session.getCapacity(), session.getBookedCount(), spaceZone.getId());
        Label detailsLbl = OwnerUi.body(details, 13, OwnerUi.TEXT_MUTED);

        String priceStr = session.getPriceOverridePaise() > 0
                ? "Price Override: ₹" + new BigDecimal(session.getPriceOverridePaise()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                : "Price: Standard Workshop Base Price";
        Label priceLbl = OwnerUi.body(priceStr, 13, OwnerUi.PRIMARY);

        HBox actions = new HBox(12);
        boolean isScheduled = WorkshopSessionStatus.SCHEDULED.equalsIgnoreCase(session.getStatus());

        if (isScheduled) {
            Button editBtn = OwnerUi.secondaryButton("Edit Session");
            editBtn.setOnAction(e -> showSessionFormDialog(session));

            Button cancelBtn = OwnerUi.secondaryButton("Cancel Session");
            cancelBtn.setOnAction(e -> {
                cancelBtn.setDisable(true);
                sessionController.cancelSession(space.getSpaceId(), session.getSessionId())
                        .thenAccept(res -> Platform.runLater(() -> {
                            if (res.isFailure()) {
                                showToast("Failed to cancel session: " + res.getMessage(), true);
                            } else {
                                loadSessions();
                            }
                        }));
            });
            actions.getChildren().addAll(editBtn, cancelBtn);
        } else {
            Label disabledMsg = OwnerUi.body("Session is " + session.getStatus().toLowerCase() + " and cannot be modified.", 12, OwnerUi.TEXT_MUTED);
            actions.getChildren().add(disabledMsg);
        }

        card.getChildren().addAll(header, detailsLbl, priceLbl, actions);
        return card;
    }

    private void showSessionFormDialog(WorkshopSession existingSession) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existingSession == null ? "Add Workshop Session" : "Edit Workshop Session");

        VBox form = new VBox(12);
        form.setPadding(new Insets(16));

        ZoneId spaceZone = getSpaceZoneId();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String defaultStartDate = "";
        String defaultStartTime = "10:00";
        String defaultEndDate = "";
        String defaultEndTime = "12:00";

        if (existingSession != null) {
            ZonedDateTime startZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(existingSession.getStartAt()), spaceZone);
            ZonedDateTime endZdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(existingSession.getEndAt()), spaceZone);
            defaultStartDate = startZdt.format(dateFormatter);
            defaultStartTime = startZdt.format(timeFormatter);
            defaultEndDate = endZdt.format(dateFormatter);
            defaultEndTime = endZdt.format(timeFormatter);
        } else {
            LocalDateTime nowPlusDay = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0);
            defaultStartDate = nowPlusDay.format(dateFormatter);
            defaultStartTime = "10:00";
            defaultEndDate = nowPlusDay.format(dateFormatter);
            defaultEndTime = "12:00";
        }

        TextField startDateField = OwnerUi.textField(defaultStartDate);
        TextField startTimeField = OwnerUi.textField(defaultStartTime);
        TextField endDateField = OwnerUi.textField(defaultEndDate);
        TextField endTimeField = OwnerUi.textField(defaultEndTime);
        TextField capField = OwnerUi.textField(existingSession != null ? String.valueOf(existingSession.getCapacity()) : "10");

        String existingPriceRupees = (existingSession != null && existingSession.getPriceOverridePaise() > 0)
                ? new BigDecimal(existingSession.getPriceOverridePaise()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString()
                : "";
        TextField priceField = OwnerUi.textField(existingPriceRupees);

        HBox startRow = new HBox(12,
                new VBox(4, OwnerUi.fieldLabel("Start Date (yyyy-MM-dd)*"), startDateField),
                new VBox(4, OwnerUi.fieldLabel("Start Time (HH:mm)*"), startTimeField)
        );
        HBox endRow = new HBox(12,
                new VBox(4, OwnerUi.fieldLabel("End Date (yyyy-MM-dd)*"), endDateField),
                new VBox(4, OwnerUi.fieldLabel("End Time (HH:mm)*"), endTimeField)
        );

        form.getChildren().addAll(
                startRow,
                endRow,
                OwnerUi.fieldLabel("Session Capacity*"), capField,
                OwnerUi.fieldLabel("Price Override (₹ Rupees, optional)"), priceField
        );

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    String startIso = startDateField.getText().trim() + "T" + startTimeField.getText().trim() + ":00";
                    String endIso = endDateField.getText().trim() + "T" + endTimeField.getText().trim() + ":00";

                    LocalDateTime startLdt = LocalDateTime.parse(startIso);
                    LocalDateTime endLdt = LocalDateTime.parse(endIso);

                    long startEpoch = startLdt.atZone(spaceZone).toInstant().toEpochMilli();
                    long endEpoch = endLdt.atZone(spaceZone).toInstant().toEpochMilli();
                    int capacity = Integer.parseInt(capField.getText().trim());

                    long pricePaise = 0L;
                    if (!priceField.getText().isBlank()) {
                        pricePaise = new BigDecimal(priceField.getText().trim()).multiply(new BigDecimal(100)).longValue();
                    }

                    WorkshopSession session = (existingSession != null) ? existingSession : new WorkshopSession();
                    session.setSpaceId(space.getSpaceId());
                    session.setStartAt(startEpoch);
                    session.setEndAt(endEpoch);
                    session.setCapacity(capacity);
                    session.setPriceOverridePaise(pricePaise);

                    if (existingSession == null) {
                        sessionController.createSession(space.getSpaceId(), session)
                                .thenAccept(res -> Platform.runLater(() -> {
                                    if (res.isFailure()) {
                                        showToast("Failed to create session: " + res.getMessage(), true);
                                    } else {
                                        loadSessions();
                                    }
                                }));
                    } else {
                        sessionController.updateSession(space.getSpaceId(), session)
                                .thenAccept(res -> Platform.runLater(() -> {
                                    if (res.isFailure()) {
                                        showToast("Failed to update session: " + res.getMessage(), true);
                                    } else {
                                        loadSessions();
                                    }
                                }));
                    }
                } catch (Exception ex) {
                    showToast("Invalid format or numbers. Please check inputs: " + ex.getMessage(), true);
                }
            }
        });
    }

    private void showToast(String message, boolean isError) {
        Label toast = OwnerUi.body(message, 13, isError ? OwnerUi.ERROR : OwnerUi.SUCCESS);
        container.getChildren().add(0, toast);
    }

    private ZoneId getSpaceZoneId() {
        if (space != null && space.getTimeZoneId() != null && !space.getTimeZoneId().isBlank()) {
            try {
                return ZoneId.of(space.getTimeZoneId().trim());
            } catch (Exception ignored) {
            }
        }
        return ZoneId.systemDefault();
    }
}
