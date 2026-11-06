package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View listing real Firestore Spaces for the authenticated Space Owner.
 * Handles both legacy V1 spaces and single-unit V2 spaces.
 */
public class MySpacesPage {

    private final SpaceController spaceController;
    private final BusinessController businessController;
    private final VBox container;

    private final Map<String, String> businessNameMap = new HashMap<>();

    public MySpacesPage() {
        this.spaceController = new SpaceController();
        this.businessController = new BusinessController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
    }

    public VBox getContent() {
        loadData();
        return container;
    }

    public VBox getContent(BorderPane rightPane) {
        return getContent();
    }

    private void loadData() {
        showLoadingState();

        // 1. Load Owner Businesses first to map businessId -> businessName
        businessController.loadCurrentOwnerBusinesses().thenAccept(bRes -> {
            if (bRes.isSuccess() && bRes.getData() != null) {
                List<Business> businesses = bRes.getData();
                businessNameMap.clear();
                for (Business b : businesses) {
                    businessNameMap.put(b.getBusinessId(), b.getBusinessName());
                }

                if (businesses.isEmpty()) {
                    Platform.runLater(this::showNoBusinessState);
                    return;
                }

                // 2. Load Owner Spaces from Firestore
                spaceController.loadCurrentOwnerSpaces().thenAccept(sRes -> {
                    Platform.runLater(() -> {
                        if (sRes.isSuccess()) {
                            List<Space> spaces = sRes.getData();
                            if (spaces == null || spaces.isEmpty()) {
                                showEmptySpaceState();
                            } else {
                                renderSpacesView(spaces);
                            }
                        } else {
                            showErrorState(sRes.getMessage());
                        }
                    });
                }).exceptionally(ex -> {
                    Platform.runLater(() -> showErrorState("Failed to load spaces: " + ex.getMessage()));
                    return null;
                });

            } else {
                Platform.runLater(() -> showErrorState("Failed to load business profiles: " + bRes.getMessage()));
            }
        }).exceptionally(ex -> {
            Platform.runLater(() -> showErrorState("Error fetching business data: " + ex.getMessage()));
            return null;
        });
    }

    private void showLoadingState() {
        container.getChildren().clear();
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(40, 40);

        Label loadingLbl = OwnerUi.body("Loading your spaces portfolio...", 14, OwnerUi.TEXT_MUTED);

        VBox box = new VBox(16, progress, loadingLbl);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(60, 0, 60, 0));
        container.getChildren().add(box);
    }

    private void showNoBusinessState() {
        container.getChildren().clear();
        VBox card = OwnerUi.card();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(14);
        card.setPadding(new Insets(40, 24, 40, 24));

        Label title = OwnerUi.heading("No Business Profile Found", 20);
        Label msg = OwnerUi.body("You need to register a Business before creating a Space.", 14, OwnerUi.TEXT_MUTED);

        Button addBizBtn = OwnerUi.primaryButton("+ Add Business");
        addBizBtn.setMaxWidth(200);
        addBizBtn.setOnAction(e -> BusinessFlowRouter.goToCreateBusiness());

        card.getChildren().addAll(title, msg, addBizBtn);
        container.getChildren().add(card);
    }

    private void showEmptySpaceState() {
        container.getChildren().clear();

        var addBtn = OwnerUi.primaryButton("+ List New Space");
        addBtn.setMaxWidth(220);
        addBtn.setOnAction(event -> SpaceFlowRouter.goToCreateSpace());

        VBox card = OwnerUi.card();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(14);
        card.setPadding(new Insets(40, 24, 40, 24));

        Label title = OwnerUi.heading("No spaces added yet", 20);
        Label msg = OwnerUi.body("List your first physical Space under your Business profile.", 14, OwnerUi.TEXT_MUTED);

        Button listSpaceBtn = OwnerUi.primaryButton("List Your First Space");
        listSpaceBtn.setMaxWidth(220);
        listSpaceBtn.setOnAction(e -> SpaceFlowRouter.goToCreateSpace());

        card.getChildren().addAll(title, msg, listSpaceBtn);

        container.getChildren().addAll(addBtn, card);
    }

    private void showErrorState(String errorMsg) {
        container.getChildren().clear();
        VBox card = OwnerUi.card();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(14);
        card.setPadding(new Insets(30));

        Label errLbl = OwnerUi.body("Unable to load spaces: " + errorMsg, 14, OwnerUi.ERROR);
        Button retryBtn = OwnerUi.secondaryButton("Retry");
        retryBtn.setOnAction(e -> loadData());

        card.getChildren().addAll(errLbl, retryBtn);
        container.getChildren().add(card);
    }

    private void renderSpacesView(List<Space> spaces) {
        container.getChildren().clear();

        long total = spaces.size();
        long draft = spaces.stream().filter(s -> {
            if (s.getSchemaVersion() == 2) {
                return "DRAFT".equalsIgnoreCase(s.getApprovalStatus());
            }
            return SpaceStatus.DRAFT.equalsIgnoreCase(s.getStatus());
        }).count();

        long submitted = spaces.stream().filter(s -> {
            if (s.getSchemaVersion() == 2) {
                String st = val(s.getApprovalStatus()).toUpperCase();
                return "SUBMITTED".equals(st) || "UNDER_REVIEW".equals(st);
            }
            return SpaceStatus.SUBMITTED.equalsIgnoreCase(s.getStatus()) || SpaceStatus.UNDER_REVIEW.equalsIgnoreCase(s.getStatus());
        }).count();

        long live = spaces.stream().filter(s -> {
            if (s.getSchemaVersion() == 2) {
                return "APPROVED".equalsIgnoreCase(s.getApprovalStatus());
            }
            return SpaceStatus.LIVE.equalsIgnoreCase(s.getStatus()) || SpaceStatus.APPROVED.equalsIgnoreCase(s.getStatus());
        }).count();

        HBox stats = new HBox(16,
                OwnerUi.statCard("Total Spaces", String.valueOf(total), null),
                OwnerUi.statCard("Draft Spaces", String.valueOf(draft), null),
                OwnerUi.statCard("Submitted / Under Review", String.valueOf(submitted), null),
                OwnerUi.statCard("Live / Approved", String.valueOf(live), null));

        var addBtn = OwnerUi.primaryButton("+ List New Space");
        addBtn.setMaxWidth(220);
        addBtn.setOnAction(event -> SpaceFlowRouter.goToCreateSpace());

        FlowPane grid = new FlowPane();
        grid.setHgap(16);
        grid.setVgap(16);

        for (Space s : spaces) {
            grid.getChildren().add(createSpaceCard(s));
        }

        container.getChildren().addAll(stats, addBtn, grid);
    }

    private VBox createSpaceCard(Space space) {
        VBox card = OwnerUi.card();
        card.setPrefWidth(320);
        card.setSpacing(10);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String badgeText = space.getSchemaVersion() == 2 ? formatV2Status(space) : (space.getStatus() != null ? space.getStatus() : SpaceStatus.DRAFT);
        Label statusBadge = OwnerUi.badge(badgeText);

        top.getChildren().addAll(OwnerUi.heading(space.getSpaceName(), 15), spacer, statusBadge);

        String businessName = businessNameMap.getOrDefault(space.getBusinessId(), "Business");
        Label bizLabel = OwnerUi.body("Business: " + businessName, 12, OwnerUi.ACCENT);
        bizLabel.setStyle("-fx-font-weight: bold;");

        String categoryText = val(space.getCategory()) +
                (space.getActivityGroup() != null ? " • " + space.getActivityGroup() : "") +
                (space.getActivityType() != null && !space.getActivityType().isBlank() ? " (" + space.getActivityType() + ")" : "");
        Label catLabel = OwnerUi.body(categoryText, 12, OwnerUi.TEXT_MUTED);

        String locStr = getFormattedLocation(space);
        Label locLabel = OwnerUi.body("Location: " + locStr, 12, OwnerUi.TEXT_MUTED);
        Label capLabel = OwnerUi.body("Capacity: " + space.getCapacity() + " guests", 12, OwnerUi.TEXT_MUTED);

        VBox extraV2Info = new VBox(2);
        if (space.getSchemaVersion() == 2) {
            String priceStr = "₹" + BigDecimal.valueOf(space.getBasePricePaise(), 2).toPlainString() + " / " + val(space.getPricingUnit());
            Label priceLbl = OwnerUi.body("Pricing: " + priceStr + " (" + val(space.getBookingType()) + ")", 12, OwnerUi.TEXT_DARK);
            extraV2Info.getChildren().add(priceLbl);
        }

        String dateStr = space.getUpdatedAt() > 0 ?
                new SimpleDateFormat("yyyy-MM-dd").format(new Date(space.getUpdatedAt())) : "Recent";
        Label updatedLabel = OwnerUi.body("Updated: " + dateStr, 11, OwnerUi.TEXT_MUTED);

        Button viewBtn = OwnerUi.secondaryButton("View Details");
        viewBtn.setOnAction(event -> SpaceFlowRouter.goToSpaceDetails(space));

        HBox actions = new HBox(8, viewBtn);

        boolean isEditable = false;
        if (space.getSchemaVersion() == 2) {
            String st = val(space.getApprovalStatus()).toUpperCase();
            isEditable = "DRAFT".equals(st) || "REJECTED".equals(st) || "APPROVED".equals(st);
        } else {
            isEditable = SpaceStatus.DRAFT.equalsIgnoreCase(space.getStatus()) || SpaceStatus.REJECTED.equalsIgnoreCase(space.getStatus());
        }

        if (isEditable) {
            Button editBtn = OwnerUi.secondaryButton("Edit");
            editBtn.setOnAction(event -> SpaceFlowRouter.goToEditSpace(space));
            actions.getChildren().add(editBtn);
        }

        card.getChildren().addAll(top, bizLabel, catLabel, locLabel, capLabel);
        if (!extraV2Info.getChildren().isEmpty()) {
            card.getChildren().add(extraV2Info);
        }
        card.getChildren().addAll(updatedLabel, actions);
        return card;
    }

    private String getFormattedLocation(Space s) {
        if (s.getSchemaVersion() == 2 && s.getSpaceLocation() != null) {
            SpaceLocation loc = s.getSpaceLocation();
            String area = val(loc.getArea());
            String city = val(loc.getCity());
            if (!area.isBlank() && !city.isBlank()) {
                return area + ", " + city;
            } else if (!city.isBlank()) {
                return city;
            } else if (!area.isBlank()) {
                return area;
            }
        }
        return s.getCity() != null ? s.getCity() : "N/A";
    }

    private static String formatV2Status(Space s) {
        if (s == null) return "DRAFT";
        String app = s.getApprovalStatus();
        if (app == null || app.isBlank()) return s.getStatus() != null ? s.getStatus() : "DRAFT";
        app = app.toUpperCase();
        if ("APPROVED".equals(app)) {
            String op = s.getOperationalStatus() != null ? s.getOperationalStatus().toUpperCase() : "INACTIVE";
            if ("ACTIVE".equals(op)) return "LIVE";
            if ("INACTIVE".equals(op)) return "APPROVED — INACTIVE";
            if ("MAINTENANCE".equals(op)) return "MAINTENANCE";
            if ("CLOSED".equals(op)) return "CLOSED";
            return "APPROVED (" + op + ")";
        }
        if ("UNDER_REVIEW".equals(app)) return "UNDER REVIEW";
        return app;
    }

    private static String val(String str) {
        return str != null ? str : "";
    }
}
