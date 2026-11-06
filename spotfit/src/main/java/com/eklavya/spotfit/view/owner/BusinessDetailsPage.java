package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.BusinessStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.controller.owner.SubmissionController;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Space;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * View presenting details of a single Business organization, submission readiness, and associated Spaces.
 */
public class BusinessDetailsPage {

    private final Business business;
    private final SpaceController spaceController;
    private final BusinessController businessController;
    private final SubmissionController submissionController;

    public BusinessDetailsPage(Business business) {
        this.business = business;
        this.spaceController = new SpaceController();
        this.businessController = new BusinessController();
        this.submissionController = new SubmissionController();
    }

    public Node getContent() {
        VBox card = OwnerUi.card();
        card.setSpacing(24);

        // Header Row
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = OwnerUi.heading(business.getBusinessName(), 24);
        Label badge = createStatusBadge(business.getVerificationStatus());

        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = OwnerUi.secondaryButton("Back to Businesses");
        backBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessList());

        header.getChildren().addAll(title, badge, spacer, backBtn);

        // Submission & Verification Banner Card
        VBox submissionCard = buildSubmissionBannerCard();

        // Grid of details
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(12);

        int row = 0;
        addDetailRow(grid, row++, "Legal Name:", val(business.getLegalName()));
        addDetailRow(grid, row++, "Business Email:", val(business.getBusinessEmail()));
        addDetailRow(grid, row++, "Business Phone:", val(business.getBusinessPhone()));

        if (business.getSchemaVersion() == 2) {
            addDetailRow(grid, row++, "Category:", val(business.getCategory()));
            String groupsStr = (business.getActivityGroups() != null && !business.getActivityGroups().isEmpty())
                    ? String.join(", ", business.getActivityGroups())
                    : "None";
            addDetailRow(grid, row++, "Activity Groups:", groupsStr);
            if (business.getWebsite() != null && !business.getWebsite().isBlank()) {
                addDetailRow(grid, row++, "Website:", business.getWebsite());
            }
        }

        addDetailRow(grid, row++, "Address:", val(business.getAddressLine()));
        addDetailRow(grid, row++, "Landmark:", val(business.getLandmark()));
        addDetailRow(grid, row++, "City / State:", val(business.getCity()) + ", " + val(business.getState()));
        addDetailRow(grid, row++, "Postal Code:", val(business.getPostalCode()));
        addDetailRow(grid, row++, "Registration No:", val(business.getRegistrationNumber()));

        String createdStr = business.getCreatedAt() > 0
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(business.getCreatedAt()))
                : "N/A";
        String updatedStr = business.getUpdatedAt() > 0
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(business.getUpdatedAt()))
                : "N/A";

        addDetailRow(grid, row++, "Schema Version:", "V" + business.getSchemaVersion());
        addDetailRow(grid, row++, "Created At:", createdStr);
        addDetailRow(grid, row++, "Updated At:", updatedStr);

        VBox descBox = new VBox(6);
        Label descHead = OwnerUi.body("Description:", 13, OwnerUi.TEXT_MUTED);
        Label descText = OwnerUi.body(val(business.getDescription()), 14, OwnerUi.TEXT_DARK);
        descText.setWrapText(true);
        descBox.getChildren().addAll(descHead, descText);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        String st = business.getVerificationStatus() != null ? business.getVerificationStatus().toUpperCase() : BusinessStatus.DRAFT;
        if (BusinessStatus.DRAFT.equals(st) || BusinessStatus.REJECTED.equals(st)) {
            Button editBtn = OwnerUi.primaryButton("Edit Business");
            editBtn.setOnAction(e -> {
                // Safeguard: Fetch fresh Business from Firestore before edit
                businessController.loadBusiness(business.getBusinessId()).thenAccept(freshRes -> Platform.runLater(() -> {
                    if (freshRes.isSuccess() && freshRes.getData() != null) {
                        BusinessFlowRouter.goToEditBusiness(freshRes.getData());
                    } else {
                        BusinessFlowRouter.goToEditBusiness(business);
                    }
                }));
            });
            actions.getChildren().add(editBtn);
        }

        // Spaces Section
        VBox spacesSection = buildSpacesSection();

        card.getChildren().addAll(header, submissionCard, grid, descBox, actions, spacesSection);
        return card;
    }

    private VBox buildSubmissionBannerCard() {
        VBox card = OwnerUi.card();
        card.setSpacing(12);

        String status = business.getVerificationStatus() != null ? business.getVerificationStatus().trim().toUpperCase() : BusinessStatus.DRAFT;

        Label header = OwnerUi.heading("Verification & Submission Status", 16);
        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size:13px;");

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Label feedbackLbl = new Label();
        feedbackLbl.setWrapText(true);
        feedbackLbl.setStyle("-fx-font-size:13px;");
        feedbackLbl.setVisible(false);

        if (BusinessStatus.DRAFT.equalsIgnoreCase(status)) {
            msgLbl.setText("This Business is currently in DRAFT status. Complete the business profile details and submit for developer verification.");
            msgLbl.setStyle("-fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");

            Button checkBtn = OwnerUi.secondaryButton("Check Readiness");
            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkBusinessReadiness(business.getBusinessId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Business is 100% ready for submission.");
                        } else {
                            feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                            feedbackLbl.setText("Missing Requirements:\n- " + String.join("\n- ", res.getData().getMissingRequirements()));
                        }
                    } else {
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            Button submitBtn = OwnerUi.primaryButton("Submit for Verification");
            submitBtn.setOnAction(e -> {
                submitBtn.setDisable(true);
                submissionController.submitBusiness(business.getBusinessId()).thenAccept(res -> Platform.runLater(() -> {
                    submitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        BusinessFlowRouter.goToBusinessDetails(res.getData());
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, submitBtn);

        } else if (BusinessStatus.SUBMITTED.equalsIgnoreCase(status)) {
            msgLbl.setText("Submitted on " + formatDate(business.getSubmittedAt()) + ". Pending developer review in Firebase Console.");
            msgLbl.setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");

        } else if (BusinessStatus.UNDER_REVIEW.equalsIgnoreCase(status)) {
            msgLbl.setText("Under Review. Your business verification is currently being reviewed by developers in Firebase Console.");
            msgLbl.setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");

        } else if (BusinessStatus.APPROVED.equalsIgnoreCase(status)) {
            msgLbl.setText("Status: Approved. Verification complete. You can create and manage Spaces under this Business.");
            msgLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");

            Button addSpaceCardBtn = OwnerUi.primaryButton("+ Add Space to this Business");
            addSpaceCardBtn.setOnAction(e -> SpaceFlowRouter.goToCreateSpace(business.getBusinessId()));

            Button viewBusinessesBtn = OwnerUi.secondaryButton("View My Businesses");
            viewBusinessesBtn.setOnAction(e -> BusinessFlowRouter.goToBusinessList());

            btnBox.getChildren().addAll(addSpaceCardBtn, viewBusinessesBtn);

        } else if (BusinessStatus.SUSPENDED.equalsIgnoreCase(status)) {
            msgLbl.setText("Status: Suspended. This business has been suspended. New Space creation is disabled for suspended businesses.");
            msgLbl.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

        } else if (BusinessStatus.REJECTED.equalsIgnoreCase(status)) {
            String reason = (business.getRejectionReason() != null && !business.getRejectionReason().isBlank())
                    ? business.getRejectionReason()
                    : "No specific reason provided.";
            msgLbl.setText("Status: Rejected. Reason: " + reason + "\nUpdate business details below and resubmit for verification.");
            msgLbl.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

            Button checkBtn = OwnerUi.secondaryButton("Check Readiness");
            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkBusinessReadiness(business.getBusinessId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Business is 100% ready for resubmission.");
                        } else {
                            feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                            feedbackLbl.setText("Missing Requirements:\n- " + String.join("\n- ", res.getData().getMissingRequirements()));
                        }
                    } else {
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            Button resubmitBtn = OwnerUi.primaryButton("Resubmit for Verification");
            resubmitBtn.setOnAction(e -> {
                resubmitBtn.setDisable(true);
                submissionController.submitBusiness(business.getBusinessId()).thenAccept(res -> Platform.runLater(() -> {
                    resubmitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        BusinessFlowRouter.goToBusinessDetails(res.getData());
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, resubmitBtn);

        } else {
            msgLbl.setText("Status: " + status + ". Business verification details loaded.");
            msgLbl.setStyle("-fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");
        }

        card.getChildren().addAll(header, msgLbl);
        if (!btnBox.getChildren().isEmpty()) {
            card.getChildren().add(btnBox);
        }
        card.getChildren().add(feedbackLbl);
        return card;
    }

    private String formatDate(long millis) {
        return millis > 0 ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(millis)) : "N/A";
    }

    private VBox buildSpacesSection() {
        VBox box = new VBox(16);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label heading = OwnerUi.heading("Spaces in this Business", 18);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isEligibleForSpace = BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus()) && business.isActive();

        Button addSpaceBtn = OwnerUi.primaryButton("+ Add Space");
        addSpaceBtn.setVisible(isEligibleForSpace);
        addSpaceBtn.setOnAction(e -> SpaceFlowRouter.goToCreateSpace(business.getBusinessId()));

        headerRow.getChildren().addAll(heading, spacer);
        if (isEligibleForSpace) {
            headerRow.getChildren().add(addSpaceBtn);
        }

        VBox spaceListContainer = new VBox(12);
        loadBusinessSpaces(spaceListContainer);

        box.getChildren().addAll(headerRow, spaceListContainer);
        return box;
    }

    private void loadBusinessSpaces(VBox spaceListContainer) {
        spaceListContainer.getChildren().clear();
        Label loadingLbl = OwnerUi.body("Loading spaces for this business...", 13, OwnerUi.TEXT_MUTED);
        spaceListContainer.getChildren().add(loadingLbl);

        spaceController.loadSpacesForBusiness(business.getBusinessId()).thenAccept(sRes -> Platform.runLater(() -> {
            spaceListContainer.getChildren().clear();

            if (sRes.isSuccess() && sRes.getData() != null) {
                String currentOwnerId = com.eklavya.spotfit.session.AppSession.getUserId();
                List<Space> spaces = sRes.getData().stream()
                        .filter(s -> business.getBusinessId().equals(s.getBusinessId()))
                        .filter(s -> currentOwnerId == null || currentOwnerId.equals(s.getOwnerId()))
                        .toList();

                if (!spaces.isEmpty()) {
                    FlowPane grid = new FlowPane();
                    grid.setHgap(16);
                    grid.setVgap(16);

                    for (Space s : spaces) {
                        grid.getChildren().add(createSpaceCard(s));
                    }
                    spaceListContainer.getChildren().add(grid);
                } else {
                    renderEmptySpacesCard(spaceListContainer);
                }
            } else {
                VBox errCard = OwnerUi.card();
                errCard.setSpacing(10);
                Label errLbl = OwnerUi.body("Failed to load spaces: " + (sRes != null ? sRes.getMessage() : "Unknown error"), 13, OwnerUi.ERROR);
                Button retryBtn = OwnerUi.secondaryButton("Retry");
                retryBtn.setOnAction(e -> loadBusinessSpaces(spaceListContainer));
                errCard.getChildren().addAll(errLbl, retryBtn);
                spaceListContainer.getChildren().add(errCard);
            }
        }));
    }

    private void renderEmptySpacesCard(VBox spaceListContainer) {
        VBox emptyCard = OwnerUi.card();
        emptyCard.setAlignment(Pos.CENTER);
        emptyCard.setSpacing(12);
        emptyCard.setPadding(new Insets(24));

        Label emptyHead = OwnerUi.heading("No Spaces Found", 15);
        Label emptyBody = OwnerUi.body("No physical spaces have been added to this business yet.", 13, OwnerUi.TEXT_MUTED);

        emptyCard.getChildren().addAll(emptyHead, emptyBody);

        if (BusinessStatus.APPROVED.equalsIgnoreCase(business.getVerificationStatus()) && business.isActive()) {
            Button createSpaceBtn = OwnerUi.primaryButton("+ List New Space");
            createSpaceBtn.setOnAction(e -> SpaceFlowRouter.goToCreateSpace(business.getBusinessId()));
            emptyCard.getChildren().add(createSpaceBtn);
        }

        spaceListContainer.getChildren().add(emptyCard);
    }

    private VBox createSpaceCard(Space space) {
        VBox card = OwnerUi.card();
        card.setPrefWidth(300);
        card.setSpacing(10);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = OwnerUi.heading(space.getSpaceName(), 15);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = OwnerUi.badge(space.getStatus() != null ? space.getStatus() : SpaceStatus.DRAFT);
        top.getChildren().addAll(nameLbl, spacer, statusBadge);

        String categoryText = (space.getCategory() != null ? space.getCategory() : "") +
                (space.getActivityGroup() != null ? " • " + space.getActivityGroup() : "");
        Label catLabel = OwnerUi.body(categoryText, 12, OwnerUi.TEXT_MUTED);
        Label locLabel = OwnerUi.body("Location: " + (space.getCity() != null ? space.getCity() : "N/A"), 12, OwnerUi.TEXT_MUTED);
        Label capLabel = OwnerUi.body("Capacity: " + space.getCapacity() + " guests", 12, OwnerUi.TEXT_MUTED);

        Button viewBtn = OwnerUi.primaryButton("View Space");
        viewBtn.setOnAction(event -> SpaceFlowRouter.goToSpaceDetails(space));

        HBox actions = new HBox(8, viewBtn);

        String st = space.getStatus() != null ? space.getStatus().toUpperCase() : SpaceStatus.DRAFT;
        if (SpaceStatus.DRAFT.equals(st) || SpaceStatus.REJECTED.equals(st)) {
            Button editBtn = OwnerUi.secondaryButton("Edit");
            editBtn.setOnAction(event -> SpaceFlowRouter.goToEditSpace(space));
            actions.getChildren().add(editBtn);
        }

        card.getChildren().addAll(top, catLabel, locLabel, capLabel, actions);
        return card;
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lbl = OwnerUi.body(label, 13, OwnerUi.TEXT_MUTED);
        lbl.setStyle("-fx-font-weight:700;");
        Label val = OwnerUi.body(value, 14, OwnerUi.TEXT_DARK);
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private String val(String str) {
        return (str != null && !str.isBlank()) ? str : "N/A";
    }

    private Label createStatusBadge(String status) {
        String st = status != null ? status.trim().toUpperCase() : BusinessStatus.DRAFT;
        Label badge = new Label(st);

        String bg = "#FFF3E0";
        String color = "#E65100";

        if (BusinessStatus.APPROVED.equalsIgnoreCase(st)) {
            bg = "#E8F5E9";
            color = "#2E7D32";
        } else if (BusinessStatus.REJECTED.equalsIgnoreCase(st) || BusinessStatus.SUSPENDED.equalsIgnoreCase(st)) {
            bg = "#FFEBEE";
            color = "#C62828";
        } else if (BusinessStatus.SUBMITTED.equalsIgnoreCase(st) || BusinessStatus.UNDER_REVIEW.equalsIgnoreCase(st)) {
            bg = "#E3F2FD";
            color = "#1565C0";
        }

        badge.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + color + ";"
                + " -fx-font-size:11px; -fx-font-weight:800; -fx-padding:4 10; -fx-background-radius:12;");
        return badge;
    }
}
