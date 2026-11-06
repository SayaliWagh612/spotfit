package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
//import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerAnalyticsController;
import com.eklavya.spotfit.controller.owner.OwnerDashboardController;
import com.eklavya.spotfit.session.AppSession;
import java.time.LocalTime;
import com.eklavya.spotfit.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.List;
import com.eklavya.spotfit.controller.owner.OwnerBookingController;
import com.eklavya.spotfit.controller.owner.OwnerManagerController;

import javafx.application.Platform;

import java.util.Comparator;

public class OwnerDashboardFirstSpacePage {
    private final OwnerDashboardController controller = new OwnerDashboardController();

    public VBox getContent(BorderPane rightPane) {
        // Owner owner = controller.getOwner();
        String scopeId = controller.getSelectedSpaceScopeId();
        Space selected = "ALL".equalsIgnoreCase(scopeId) ? null
                : controller.getOwnedSpaces().stream()
                        .filter(s -> s.getSpaceId().equals(scopeId)).findFirst().orElse(null);
        String scopeName = selected == null ? "All Spaces" : selected.getName();

        // VBox greeting = new VBox(4, OwnerUi.heading("Good morning, " +
        // firstName(owner.getName()) + " 👋", 27),
        // OwnerUi.body("Business overview for " + scopeName + ".", 13,
        // OwnerUi.TEXT_MUTED));
        String ownerName = getAuthenticatedOwnerName();

        VBox greeting = new VBox(
                4,
                OwnerUi.heading(
                        getGreeting() + ", " + firstName(ownerName),
                        27),
                OwnerUi.body(
                        "Business overview for " + scopeName + ".",
                        13,
                        OwnerUi.TEXT_MUTED));
        VBox totalBusinessesCard = OwnerUi.statCard("Total Businesses", "...", "Real Firestore Data");

        HBox stats = new HBox(16,
                totalBusinessesCard,
                OwnerUi.statCard(selected == null ? "Active Spaces" : "Space Status",
                        selected == null ? String.valueOf(controller.getActiveSpacesCount()) : selected.getStatus(),
                        "Space Data"),
                OwnerUi.statCard("Bookings", String.valueOf(controller.getTotalBookingsCount()), "Current scope"),
                OwnerUi.statCard("Captured Revenue", money(controller.getTotalRevenue()), "Successful payments"),
                OwnerUi.statCard("Average Rating", selected == null ? averageRating(controller.getOwnedSpaces())
                        : String.format("%.1f ★", selected.getRating()), null));

        new com.eklavya.spotfit.controller.owner.BusinessController().loadCurrentOwnerBusinesses()
                .whenComplete((res, err) -> javafx.application.Platform.runLater(() -> {
                    if (res != null && res.isSuccess() && res.getData() != null) {
                        totalBusinessesCard.getChildren().clear();
                        totalBusinessesCard.getChildren().addAll(
                                OwnerUi.body("Total Businesses", 12.5, OwnerUi.TEXT_MUTED),
                                OwnerUi.heading(String.valueOf(res.getData().size()), 24),
                                OwnerUi.body("Real Firestore Data", 11.5, OwnerUi.PRIMARY_CONTAINER));
                    } else {
                        totalBusinessesCard.getChildren().clear();
                        totalBusinessesCard.getChildren().addAll(
                                OwnerUi.body("Total Businesses", 12.5, OwnerUi.TEXT_MUTED),
                                OwnerUi.heading("0", 24),
                                OwnerUi.body("Real Firestore Data", 11.5, OwnerUi.PRIMARY_CONTAINER));
                    }
                }));
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // ============================================================
        // REAL FIRESTORE RECENT BOOKINGS
        // ============================================================

        VBox bookings = OwnerUi.card();
        bookings.setSpacing(12);
        bookings.setMinHeight(310);

        bookings.getChildren().addAll(
                OwnerUi.heading(
                        "Recent Bookings",
                        18),
                OwnerUi.body(
                        "Latest customer booking activity",
                        12,
                        OwnerUi.TEXT_MUTED));

        String bookingSpaceFilter = scopeId == null
                || scopeId.isBlank()
                || "ALL".equalsIgnoreCase(scopeId)
                        ? null
                        : scopeId;

        OwnerBookingController bookingController = new OwnerBookingController();

        bookingController
                .getBookings(
                        bookingSpaceFilter,
                        null,
                        null)
                .whenComplete((liveBookings, error) -> Platform.runLater(() -> {

                    bookings.getChildren().clear();

                    bookings.getChildren().add(
                            OwnerUi.heading(
                                    "Recent Bookings",
                                    17));

                    if (error != null) {
                        bookings.getChildren().add(
                                OwnerUi.body(
                                        "Could not load recent bookings.",
                                        13,
                                        "#C62828"));
                        return;
                    }

                    if (liveBookings == null
                            || liveBookings.isEmpty()) {

                        bookings.getChildren().add(
                                OwnerUi.body(
                                        "No real bookings in this scope.",
                                        13,
                                        OwnerUi.TEXT_MUTED));
                        return;
                    }

                    List<Booking> recentBookings = liveBookings.stream()
                            .sorted(
                                    Comparator.comparingLong(
                                            (Booking booking) -> Math.max(
                                                    booking.getUpdatedAt(),
                                                    booking.getCreatedAt()))
                                            .reversed())
                            .limit(5)
                            .toList();

                    for (Booking booking : recentBookings) {

                        // String bookingId = valueOrFallback(
                        // booking.getBookingId(),
                        // "Booking");

                        // String customerName = valueOrFallback(
                        // booking.getCustomerName(),
                        // "SpotFit Customer");

                        // String offeringName = valueOrFallback(
                        // booking.getOfferingName(),
                        // valueOrFallback(
                        // booking.getBookingType(),
                        // "Session"));

                        // String bookingStatus = valueOrFallback(
                        // booking.getStatus(),
                        // "UNKNOWN");

                        // double amount = booking.getTotalAmountPaise() > 0
                        // ? booking.getTotalAmountPaise() / 100.0
                        // : booking.getAmount();

                        // bookings.getChildren().add(
                        // infoRow(
                        // bookingId + " · " + customerName,
                        // offeringName
                        // + " · "
                        // + bookingStatus
                        // + " · "
                        // + money(amount)));

                        bookings.getChildren().add(
                                createBookingRow(booking));
                    }
                }));

        // ============================================================
        // REAL FIRESTORE SPACE MANAGERS
        // ============================================================

        VBox managerCard = OwnerUi.card();
        managerCard.setSpacing(12);
        managerCard.setMinHeight(190);

        managerCard.getChildren().addAll(
                OwnerUi.heading(
                        bookingSpaceFilter == null
                                ? "Space Managers"
                                : "Assigned Manager",
                        18),
                OwnerUi.body(
                        "Your active operations team",
                        12,
                        OwnerUi.TEXT_MUTED));

        OwnerManagerController managerController = new OwnerManagerController();

        managerController
                .getManagers()
                .whenComplete((result, error) -> Platform.runLater(() -> {

                    managerCard.getChildren().clear();

                    managerCard.getChildren().add(
                            OwnerUi.heading(
                                    bookingSpaceFilter == null
                                            ? "Space Managers"
                                            : "Assigned Manager",
                                    17));

                    if (error != null
                            || result == null
                            || result.isFailure()) {

                        managerCard.getChildren().add(
                                OwnerUi.body(
                                        "Could not load assigned Managers.",
                                        13,
                                        "#C62828"));
                        return;
                    }

                    List<Manager> managers = result.getData() == null
                            ? List.of()
                            : result.getData();

                    List<Manager> scopedManagers = managers.stream()
                            .filter(manager -> {
                                if (bookingSpaceFilter == null) {
                                    return true;
                                }

                                return manager.getAssignedSpaceIds() != null
                                        && manager
                                                .getAssignedSpaceIds()
                                                .contains(
                                                        bookingSpaceFilter);
                            })
                            .toList();

                    if (scopedManagers.isEmpty()) {
                        managerCard.getChildren().add(
                                OwnerUi.body(
                                        bookingSpaceFilter == null
                                                ? "No registered Managers found."
                                                : "No Manager is assigned to this Space.",
                                        13,
                                        OwnerUi.TEXT_MUTED));
                        return;
                    }

                    // for (Manager manager : scopedManagers) {

                    // String managerName = valueOrFallback(
                    // manager.getName(),
                    // "Space Manager");

                    // String managerEmail = valueOrFallback(
                    // manager.getEmail(),
                    // "No email available");

                    // String status = manager.isActive()
                    // ? "ACTIVE"
                    // : "DEACTIVATED";

                    // int assignedSpaces = manager.getAssignedSpaceIds() == null
                    // ? 0
                    // : manager
                    // .getAssignedSpaceIds()
                    // .size();

                    // String details;

                    // if (bookingSpaceFilter == null) {
                    // details = managerEmail
                    // + " · "
                    // + status
                    // + " · "
                    // + assignedSpaces
                    // + (assignedSpaces == 1
                    // ? " Space"
                    // : " Spaces");
                    // } else {
                    // details = managerEmail
                    // + " · "
                    // + status;
                    // }

                    // managerCard.getChildren().add(
                    // infoRow(
                    // managerName,
                    // details));
                    // }
                    for (Manager manager : scopedManagers) {
                        managerCard.getChildren().add(
                                createManagerRow(manager));
                    }
                }));

        VBox spaces = OwnerUi.card();
        spaces.getChildren().add(OwnerUi.heading("Space Performance", 17));
        for (Space space : controller.getOwnedSpaces()) {
            if (selected == null || selected.getSpaceId().equals(space.getSpaceId()))
                spaces.getChildren().add(infoRow(space.getName(), space.getTotalBookings() + " bookings · "
                        + money(space.getMonthlyRevenue()) + " · " + space.getRating() + " ★"));
        }

        VBox side = new VBox(18, managerCard, spaces);
        HBox lower = new HBox(18, bookings, side);
        HBox.setHgrow(bookings, Priority.ALWAYS);
        HBox.setHgrow(side, Priority.ALWAYS);
        VBox content = new VBox(20, greeting, stats, lower);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }

    private HBox infoRow(String left, String right) {
        HBox row = new HBox(10, OwnerUi.body(left, 13, OwnerUi.TEXT_DARK), spacer(),
                OwnerUi.body(right, 12, OwnerUi.TEXT_MUTED));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        return row;
    }

    private HBox createBookingRow(Booking booking) {

        String customerName = valueOrFallback(
                booking.getCustomerName(),
                "SpotFit Customer");

        String offeringName = valueOrFallback(
                booking.getOfferingName(),
                valueOrFallback(
                        booking.getBookingType(),
                        "Workshop"));

        String readableOffering = formatLabel(offeringName);

        String bookingStatus = valueOrFallback(
                booking.getStatus(),
                "UNKNOWN").toUpperCase();

        double amount = booking.getTotalAmountPaise() > 0
                ? booking.getTotalAmountPaise() / 100.0
                : booking.getAmount();

        // Customer initials
        StackPane avatar = new StackPane();
        avatar.setMinSize(44, 44);
        avatar.setPrefSize(44, 44);
        avatar.setMaxSize(44, 44);

        avatar.setStyle(
                "-fx-background-color: #F8E7D5;" +
                        "-fx-background-radius: 22;");

        Label initials = new Label(
                getInitials(customerName));

        initials.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + OwnerUi.PRIMARY + ";");

        avatar.getChildren().add(initials);

        // Booking information
        Label name = OwnerUi.heading(
                customerName,
                14);

        Label offering = OwnerUi.body(
                readableOffering,
                12,
                OwnerUi.TEXT_MUTED);

        VBox details = new VBox(
                3,
                name,
                offering);

        Region space = new Region();
        HBox.setHgrow(
                space,
                Priority.ALWAYS);

        Label status = new Label(
                formatLabel(bookingStatus));

        boolean confirmed = "CONFIRMED".equalsIgnoreCase(bookingStatus)
                || "COMPLETED".equalsIgnoreCase(bookingStatus)
                || "CHECKED_IN".equalsIgnoreCase(bookingStatus);

        status.setStyle(
                "-fx-background-color: "
                        + (confirmed ? "#E8F7EE" : "#FFF3CD") + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: "
                        + (confirmed ? "#27864A" : "#856404") + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10;");

        Label price = OwnerUi.heading(
                money(amount),
                14);

        VBox right = new VBox(
                6,
                status,
                price);

        right.setAlignment(
                Pos.CENTER_RIGHT);

        HBox row = new HBox(
                12,
                avatar,
                details,
                space,
                right);

        row.setAlignment(
                Pos.CENTER_LEFT);

        row.setPadding(
                new Insets(13, 15, 13, 15));

        row.setStyle(
                "-fx-background-color: " + OwnerUi.BACKGROUND + ";" +
                        "-fx-background-radius: 13;" +
                        "-fx-border-color: " + "#E6CDBA" + ";" +
                        "-fx-border-radius: 13;");

        return row;
    }

    private HBox createManagerRow(
            Manager manager) {

        String managerName = valueOrFallback(
                manager.getName(),
                "Space Manager");

        StackPane avatar = new StackPane();
        avatar.setMinSize(46, 46);
        avatar.setPrefSize(46, 46);
        avatar.setMaxSize(46, 46);

        avatar.setStyle(
                "-fx-background-color: #F8E7D5;" +
                        "-fx-background-radius: 23;");

        Label initials = new Label(
                getInitials(managerName));

        initials.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + OwnerUi.PRIMARY + ";");

        avatar.getChildren().add(initials);

        Label name = OwnerUi.heading(
                managerName,
                14);

        Label role = OwnerUi.body(
                "Space Manager",
                11,
                OwnerUi.TEXT_MUTED);

        VBox identity = new VBox(
                2,
                name,
                role);

        Region space = new Region();
        HBox.setHgrow(
                space,
                Priority.ALWAYS);

        Label status = new Label(
                manager.isActive()
                        ? "ACTIVE"
                        : "INACTIVE");

        status.setStyle(
                "-fx-background-color: "
                        + (manager.isActive()
                                ? "#E8F7EE"
                                : "#F2F2F2")
                        + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: "
                        + (manager.isActive()
                                ? "#27864A"
                                : OwnerUi.TEXT_MUTED)
                        + ";" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 10;");

        HBox row = new HBox(
                12,
                avatar,
                identity,
                space,
                status);

        row.setAlignment(
                Pos.CENTER_LEFT);

        row.setPadding(
                new Insets(13, 15, 13, 15));

        row.setStyle(
                "-fx-background-color: " + OwnerUi.BACKGROUND + ";" +
                        "-fx-background-radius: 13;" +
                        "-fx-border-color: " + "#E6CDBA" + ";" +
                        "-fx-border-radius: 13;");

        return row;
    }

    private String getInitials(
            String name) {

        if (name == null || name.isBlank()) {
            return "SM";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0]
                    .substring(
                            0,
                            Math.min(2, parts[0].length()))
                    .toUpperCase();
        }

        return (parts[0].substring(0, 1)
                + parts[parts.length - 1]
                        .substring(0, 1))
                .toUpperCase();
    }

    private String formatLabel(
            String value) {

        if (value == null || value.isBlank()) {
            return "Session";
        }

        String normalized = value.trim()
                .replace("_", " ")
                .toLowerCase();

        StringBuilder result = new StringBuilder();

        for (String word : normalized.split("\\s+")) {

            if (word.isBlank()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)));

            if (word.length() > 1) {
                result.append(
                        word.substring(1));
            }
        }

        return result.toString();
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private String firstName(String name) {
        return name == null || name.isBlank() ? "Owner" : name.trim().split("\\s+")[0];
    }

    private String money(double amount) {
        return "₹" + String.format("%,.0f", amount);
    }

    private String valueOrFallback(
            String value,
            String fallback) {

        return value != null && !value.isBlank()
                ? value
                : fallback;
    }

    private String averageRating(List<Space> spaces) {
        return spaces.isEmpty() ? "0.0 ★"
                : String.format("%.1f ★", spaces.stream().mapToDouble(Space::getRating).average().orElse(0));
    }

    private String getAuthenticatedOwnerName() {

        String sessionName = AppSession.getName();

        if (AppSession.isAuthenticated()
                && sessionName != null
                && !sessionName.isBlank()) {
            return sessionName.trim();
        }

        String email = AppSession.getEmail();

        if (email != null && !email.isBlank()) {
            return email.split("@")[0];
        }

        return "Owner";
    }

    private String getGreeting() {

        int hour = LocalTime.now().getHour();

        if (hour < 12) {
            return "Good morning";
        }

        if (hour < 17) {
            return "Good afternoon";
        }

        return "Good evening";
    }
}
