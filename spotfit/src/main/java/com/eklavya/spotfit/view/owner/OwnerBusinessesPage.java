package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.model.Business;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * View listing all businesses registered under the authenticated Space Owner.
 */
public class OwnerBusinessesPage {

    private final BusinessController businessController;
    private final VBox container;

    public OwnerBusinessesPage() {
        this.businessController = new BusinessController();
        this.container = new VBox(20);
    }

    public Node getContent() {
        loadBusinesses();
        return container;
    }

    private void loadBusinesses() {
        container.getChildren().clear();

        // Top Action Bar
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label heading = OwnerUi.heading("My Businesses", 24);
        Label sub = OwnerUi.body("Manage the organizations connected to your SpotFit Owner account.", 13, OwnerUi.TEXT_MUTED);
        titleBox.getChildren().addAll(heading, sub);

        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = OwnerUi.primaryButton("+ Add Business");
        addBtn.setOnAction(e -> BusinessFlowRouter.goToCreateBusiness());

        topBar.getChildren().addAll(titleBox, spacer, addBtn);
        container.getChildren().add(topBar);

        // Loading Indicator State
        VBox loadingBox = new VBox(16);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPadding(new Insets(40));
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(40, 40);
        Label loadingLabel = OwnerUi.body("Loading your businesses...", 14, OwnerUi.TEXT_MUTED);
        loadingBox.getChildren().addAll(progress, loadingLabel);
        container.getChildren().add(loadingBox);

        businessController.loadCurrentOwnerBusinesses()
                .whenComplete((result, throwable) -> Platform.runLater(() -> {
                    container.getChildren().remove(loadingBox);

                    if (throwable != null) {
                        renderErrorState("Failed to load businesses: " + throwable.getMessage());
                    } else if (result != null && result.isFailure()) {
                        renderErrorState(result.getMessage());
                    } else if (result != null && result.getData() != null) {
                        List<Business> list = result.getData();
                        if (list.isEmpty()) {
                            renderEmptyState();
                        } else {
                            renderBusinessList(list);
                        }
                    } else {
                        renderEmptyState();
                    }
                }));
    }

    private void renderEmptyState() {
        VBox emptyCard = OwnerUi.card();
        emptyCard.setAlignment(Pos.CENTER);
        emptyCard.setPadding(new Insets(48, 32, 48, 32));

        Label icon = new Label("\uD83C\uDFE2");
        icon.setStyle("-fx-font-size:48px;");

        Label emptyTitle = OwnerUi.heading("No businesses added yet", 20);
        Label emptySub = OwnerUi.body("Create your first Business before adding Spaces.", 13.5, OwnerUi.TEXT_MUTED);
        emptySub.setWrapText(true);

        Button createFirstBtn = OwnerUi.primaryButton("+ Add Your First Business");
        createFirstBtn.setOnAction(e -> BusinessFlowRouter.goToCreateBusiness());

        emptyCard.getChildren().addAll(icon, emptyTitle, emptySub, createFirstBtn);
        container.getChildren().add(emptyCard);
    }

    private void renderErrorState(String message) {
        VBox errorCard = OwnerUi.card();
        errorCard.setAlignment(Pos.CENTER);
        errorCard.setPadding(new Insets(32));

        Label errLabel = OwnerUi.body(message != null ? message : "An error occurred while loading businesses.", 14, "#D32F2F");
        errLabel.setWrapText(true);

        Button retryBtn = OwnerUi.secondaryButton("Retry");
        retryBtn.setOnAction(e -> loadBusinesses());

        errorCard.getChildren().addAll(errLabel, retryBtn);
        container.getChildren().add(errorCard);
    }

    private void renderBusinessList(List<Business> businesses) {
        VBox listContainer = new VBox(16);

        for (Business b : businesses) {
            VBox card = OwnerUi.card();

            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = OwnerUi.heading(b.getBusinessName(), 18);
            Label statusBadge = createStatusBadge(b.getVerificationStatus());

            VBox headSpacer = new VBox();
            HBox.setHgrow(headSpacer, Priority.ALWAYS);

            header.getChildren().add(nameLabel);
            if (b.getSchemaVersion() == 2) {
                Label v2Badge = new Label("V2");
                v2Badge.setStyle("-fx-background-color:#EDE7F6; -fx-text-fill:#512DA8; -fx-font-size:11px; -fx-font-weight:800; -fx-padding:3 8; -fx-background-radius:12;");
                header.getChildren().add(v2Badge);
            }
            header.getChildren().addAll(statusBadge, headSpacer);

            String locationStr = (b.getCity() != null ? b.getCity() : "")
                    + ((b.getState() != null && !b.getState().isBlank()) ? ", " + b.getState() : "");
            Label locationLabel = OwnerUi.body("\uD83D\uDCCD " + locationStr, 13, OwnerUi.TEXT_MUTED);

            VBox extraV2Box = new VBox(4);
            if (b.getSchemaVersion() == 2) {
                String catStr = (b.getCategory() != null ? b.getCategory() : "CREATIVITY");
                String groupsStr = (b.getActivityGroups() != null && !b.getActivityGroups().isEmpty())
                        ? String.join(", ", b.getActivityGroups())
                        : "None";
                Label catGroupLabel = OwnerUi.body("Category: " + catStr + " • Activities: " + groupsStr, 12.5, OwnerUi.TEXT_MUTED);
                extraV2Box.getChildren().add(catGroupLabel);
            }

            HBox contactRow = new HBox(20);
            Label emailLabel = OwnerUi.body("✉ " + (b.getBusinessEmail() != null ? b.getBusinessEmail() : "N/A"), 13, OwnerUi.TEXT_DARK);
            Label phoneLabel = OwnerUi.body("📞 " + (b.getBusinessPhone() != null ? b.getBusinessPhone() : "N/A"), 13, OwnerUi.TEXT_DARK);
            contactRow.getChildren().addAll(emailLabel, phoneLabel);

            String updatedStr = b.getUpdatedAt() > 0
                    ? new SimpleDateFormat("MMM dd, yyyy").format(new Date(b.getUpdatedAt()))
                    : "N/A";
            Label updatedLabel = OwnerUi.body("Updated: " + updatedStr, 12, OwnerUi.TEXT_MUTED);

            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER_LEFT);

            Button viewBtn = OwnerUi.secondaryButton("View Details");
            viewBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessDetails(b));
            actions.getChildren().add(viewBtn);

            String st = b.getVerificationStatus() != null ? b.getVerificationStatus().toUpperCase() : BusinessStatus.DRAFT;
            if (BusinessStatus.DRAFT.equals(st) || BusinessStatus.REJECTED.equals(st)) {
                Button editBtn = OwnerUi.primaryButton("Edit Business");
                editBtn.setOnAction(e -> BusinessFlowRouter.goToEditBusiness(b));
                actions.getChildren().add(editBtn);
            }

            card.getChildren().addAll(header, locationLabel);
            if (!extraV2Box.getChildren().isEmpty()) {
                card.getChildren().add(extraV2Box);
            }
            card.getChildren().addAll(contactRow, updatedLabel, actions);
            listContainer.getChildren().add(card);
        }

        container.getChildren().add(listContainer);
    }

    private Label createStatusBadge(String status) {
        String st = status != null ? status.toUpperCase() : BusinessStatus.DRAFT;
        Label badge = new Label(st);

        String bg = "#FFF3E0";
        String color = "#E65100";

        if (BusinessStatus.APPROVED.equals(st)) {
            bg = "#E8F5E9";
            color = "#2E7D32";
        } else if (BusinessStatus.REJECTED.equals(st) || BusinessStatus.SUSPENDED.equals(st)) {
            bg = "#FFEBEE";
            color = "#C62828";
        } else if (BusinessStatus.SUBMITTED.equals(st) || BusinessStatus.UNDER_REVIEW.equals(st)) {
            bg = "#E3F2FD";
            color = "#1565C0";
        }

        badge.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + color + ";"
                + " -fx-font-size:11px; -fx-font-weight:800; -fx-padding:4 10; -fx-background-radius:12;");
        return badge;
    }
}
