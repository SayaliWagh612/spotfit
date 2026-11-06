package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.constants.OfferingStatus;
import com.eklavya.spotfit.constants.SpaceStatus;
import com.eklavya.spotfit.controller.owner.BusinessController;
import com.eklavya.spotfit.controller.owner.OfferingController;
import com.eklavya.spotfit.controller.owner.ResourceController;
import com.eklavya.spotfit.controller.owner.SpaceController;
import com.eklavya.spotfit.controller.owner.SubmissionController;
import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.OperatingDayConfig;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.model.SpaceLocation;
import com.eklavya.spotfit.model.bookingconfig.*;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Details view displaying real Firestore Space attributes.
 * Dynamically toggles between V1 (Resources/Offerings) and V2 (Single Unit BookingConfig) layouts.
 */
public class SpaceDetailsPage {

    private final Space initialSpace;
    private Space activeSpace;
    private final BusinessController businessController;
    private final SpaceController spaceController;
    private final ResourceController resourceController;
    private final OfferingController offeringController;
    private final SubmissionController submissionController;
    private final VBox container;

    public SpaceDetailsPage(Space space) {
        this.initialSpace = space;
        this.activeSpace = space;
        this.businessController = new BusinessController();
        this.spaceController = new SpaceController();
        this.resourceController = new ResourceController();
        this.offeringController = new OfferingController();
        this.submissionController = new SubmissionController();
        this.container = new VBox(20);
        this.container.setPadding(new Insets(16, 0, 0, 0));
    }

    public VBox getContent() {
        if (initialSpace != null && initialSpace.getSchemaVersion() == 2) {
            loadV2SpaceAndRender();
        } else {
            renderV1Details();
        }
        return container;
    }

    public Scene getScene(Runnable callback) {
        String nameStr = initialSpace != null ? val(initialSpace.getSpaceName()) : "Space Details";
        return OwnerUi.pageShell("myspaces", "My Spaces / " + nameStr, nameStr,
                "Operational details for space", getContent(), callback);
    }

    // ==================== V2 DETAILS RENDERING ====================

    private void loadV2SpaceAndRender() {
        container.getChildren().clear();
        spaceController.loadV2Space(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
            if (res.isSuccess() && res.getData() != null) {
                this.activeSpace = res.getData();
            } else {
                this.activeSpace = initialSpace;
            }
            renderV2Details();
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                this.activeSpace = initialSpace;
                renderV2Details();
            });
            return null;
        });
    }

    private void renderV2Details() {
        container.getChildren().clear();

        if (activeSpace == null) {
            container.getChildren().add(OwnerUi.body("No space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title & Status Badge
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = OwnerUi.heading(val(activeSpace.getSpaceName()), 24);
        Label badge = OwnerUi.badge(formatV2Status(activeSpace));
        titleRow.getChildren().addAll(nameLbl, badge);

        // Actions: Back and Edit (if DRAFT, REJECTED, or APPROVED)
        Button backBtn = OwnerUi.secondaryButton("← Back to Spaces");
        backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceList());

        HBox actionBox = new HBox(12, backBtn);

        String bType = activeSpace.getBookingType() != null ? activeSpace.getBookingType().toUpperCase() : "";
        if ("SLOT".equals(bType) || "VENUE".equals(bType) || "PRACTICE_ROOM".equals(bType)) {
            Button availBtn = OwnerUi.secondaryButton("Manage Availability");
            availBtn.setOnAction(e -> SpaceFlowRouter.goToManageAvailability(activeSpace));
            actionBox.getChildren().add(availBtn);
        } else if ("SEAT".equals(bType)) {
            Button seatsBtn = OwnerUi.secondaryButton("Manage Seats");
            seatsBtn.setOnAction(e -> SpaceFlowRouter.goToManageSeats(activeSpace));
            actionBox.getChildren().add(seatsBtn);
        } else if ("WORKSHOP".equals(bType)) {
            Button sessionsBtn = OwnerUi.secondaryButton("Manage Sessions");
            sessionsBtn.setOnAction(e -> SpaceFlowRouter.goToManageWorkshopSessions(activeSpace));
            actionBox.getChildren().add(sessionsBtn);
        }

        String approvalStatus = val(activeSpace.getApprovalStatus()).toUpperCase();
        if ("DRAFT".equals(approvalStatus) || "REJECTED".equals(approvalStatus) || "APPROVED".equals(approvalStatus)) {
            Button editBtn = OwnerUi.primaryButton("Edit Space");
            editBtn.setOnAction(e -> SpaceFlowRouter.goToEditSpace(activeSpace));
            actionBox.getChildren().add(editBtn);
        }

        // Submission & Verification Banner Card
        VBox submissionCard = buildV2SubmissionBannerCard();

        // Business Name card
        VBox bizCard = OwnerUi.card();
        Label bizLabel = OwnerUi.body("Loading Business Information...", 13, OwnerUi.TEXT_MUTED);
        bizCard.getChildren().addAll(OwnerUi.heading("Business Association", 16), bizLabel);

        if (activeSpace.getBusinessId() != null) {
            businessController.loadCurrentOwnerBusinesses().thenAccept(bRes -> {
                if (bRes.isSuccess() && bRes.getData() != null) {
                    for (Business b : bRes.getData()) {
                        if (b.getBusinessId().equals(activeSpace.getBusinessId())) {
                            Platform.runLater(() -> bizLabel.setText("Business Name: " + b.getBusinessName() + " (" + b.getCity() + ") | Verification: " + b.getVerificationStatus()));
                            return;
                        }
                    }
                    Platform.runLater(() -> bizLabel.setText("Business ID: " + activeSpace.getBusinessId()));
                }
            });
        }

        // Classification Card
        VBox classificationCard = OwnerUi.card();
        classificationCard.setSpacing(8);
        classificationCard.getChildren().addAll(
                OwnerUi.heading("Classification & Overview", 16),
                OwnerUi.body("Category: " + val(activeSpace.getCategory()), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Activity Group: " + val(activeSpace.getActivityGroup()), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Activity Type: " + val(activeSpace.getActivityType()), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Capacity: " + activeSpace.getCapacity() + " guests", 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Description: " + (activeSpace.getDescription() != null && !activeSpace.getDescription().isBlank() ? activeSpace.getDescription() : "No description provided."), 13, OwnerUi.TEXT_MUTED)
        );

        // Location & Area Card
        VBox locationCard = OwnerUi.card();
        locationCard.setSpacing(8);

        SpaceLocation loc = activeSpace.getSpaceLocation();
        String addressLine = loc != null ? val(loc.getAddressLine()) : val(activeSpace.getAddressLine());
        String area = loc != null ? val(loc.getArea()) : "";
        String landmark = loc != null ? val(loc.getLandmark()) : val(activeSpace.getLandmark());
        String city = loc != null ? val(loc.getCity()) : val(activeSpace.getCity());
        String state = loc != null ? val(loc.getState()) : val(activeSpace.getState());
        String postal = loc != null ? val(loc.getPostalCode()) : val(activeSpace.getPostalCode());

        String fullAddress = addressLine +
                (!area.isBlank() ? ", Area: " + area : "") +
                (!landmark.isBlank() ? ", Near " + landmark : "") +
                (!city.isBlank() ? ", " + city : "") +
                (!state.isBlank() ? ", " + state : "") +
                (!postal.isBlank() ? " - " + postal : "");

        locationCard.getChildren().addAll(
                OwnerUi.heading("Location & Timezone", 16),
                OwnerUi.body("Address: " + fullAddress, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Area / Locality: " + (!area.isBlank() ? area : "N/A"), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Time Zone: " + (activeSpace.getTimeZoneId() != null ? activeSpace.getTimeZoneId() : "Asia/Kolkata"), 13, OwnerUi.TEXT_MUTED)
        );

        // Booking Model & Pricing Card
        VBox pricingCard = OwnerUi.card();
        pricingCard.setSpacing(8);

        String rupeePriceDisplay = "₹" + BigDecimal.valueOf(activeSpace.getBasePricePaise(), 2).toPlainString();
        pricingCard.getChildren().addAll(
                OwnerUi.heading("Booking Model & Pricing", 16),
                OwnerUi.body("Booking Type: " + val(activeSpace.getBookingType()), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Base Price: " + rupeePriceDisplay + " per " + val(activeSpace.getPricingUnit()), 13, OwnerUi.TEXT_DARK)
        );

        // Booking Configuration Card
        VBox configCard = buildV2BookingConfigSummaryCard(activeSpace.getBookingConfig());

        // Operating Hours Card
        VBox scheduleCard = buildV2OperatingHoursCard(activeSpace.getOperatingHours());

        // Operations & Policies Card
        VBox opsCard = OwnerUi.card();
        opsCard.setSpacing(8);

        String amenitiesStr = activeSpace.getAmenities() != null && !activeSpace.getAmenities().isEmpty() ?
                String.join(", ", activeSpace.getAmenities()) : "None specified";
        String facilitiesStr = activeSpace.getFacilities() != null && !activeSpace.getFacilities().isEmpty() ?
                String.join(", ", activeSpace.getFacilities()) : "None specified";
        String rulesStr = activeSpace.getRules() != null && !activeSpace.getRules().isEmpty() ?
                String.join("; ", activeSpace.getRules()) : "Standard venue rules apply.";
        String cancelStr = activeSpace.getCancellationPolicy() != null && !activeSpace.getCancellationPolicy().isBlank() ?
                activeSpace.getCancellationPolicy() : "Standard cancellation policy applies.";

        opsCard.getChildren().addAll(
                OwnerUi.heading("Amenities, Facilities & Rules", 16),
                OwnerUi.body("Amenities: " + amenitiesStr, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Facilities: " + facilitiesStr, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("House Rules: " + rulesStr, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Cancellation Policy: " + cancelStr, 13, OwnerUi.TEXT_MUTED)
        );

        // Record Metadata Card
        VBox metaCard = OwnerUi.card();
        metaCard.setSpacing(8);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String createdStr = activeSpace.getCreatedAt() > 0 ? sdf.format(new Date(activeSpace.getCreatedAt())) : "N/A";
        String updatedStr = activeSpace.getUpdatedAt() > 0 ? sdf.format(new Date(activeSpace.getUpdatedAt())) : "N/A";

        metaCard.getChildren().addAll(
                OwnerUi.heading("Record Metadata", 16),
                OwnerUi.body("Schema Version: 2 (Single-unit V2 Space)", 12, OwnerUi.ACCENT),
                OwnerUi.body("Created Date: " + createdStr, 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Last Updated: " + updatedStr, 12, OwnerUi.TEXT_MUTED)
        );

        // Note: Resources & Offerings are strictly omitted for V2 spaces!
        container.getChildren().addAll(
                titleRow, actionBox, submissionCard, bizCard, classificationCard,
                locationCard, pricingCard, configCard, scheduleCard, opsCard, metaCard
        );
    }

    private VBox buildV2SubmissionBannerCard() {
        VBox card = OwnerUi.card();
        card.setSpacing(12);

        Label header = OwnerUi.heading("V2 Space Review & Submission Status", 16);
        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size:13px;");

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Label feedbackLbl = new Label();
        feedbackLbl.setWrapText(true);
        feedbackLbl.setStyle("-fx-font-size:13px;");
        feedbackLbl.setVisible(false);

        String approvalStatus = activeSpace.getApprovalStatus() != null ? activeSpace.getApprovalStatus().toUpperCase() : "DRAFT";
        String opStatus = activeSpace.getOperationalStatus() != null ? activeSpace.getOperationalStatus().toUpperCase() : "INACTIVE";

        if ("DRAFT".equals(approvalStatus)) {
            msgLbl.setText("This V2 Space is currently in DRAFT status. Perform a readiness check before submitting for developer review.");
            msgLbl.setStyle("-fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");

            Button checkBtn = OwnerUi.secondaryButton("Check Readiness");
            Button submitBtn = OwnerUi.primaryButton("Submit for Review");
            submitBtn.setDisable(true);

            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkV2SpaceReadiness(activeSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Space is 100% ready for submission.");
                            submitBtn.setDisable(false);
                        } else {
                            feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                            feedbackLbl.setText("Missing Requirements:\n- " + String.join("\n- ", res.getData().getMissingRequirements()));
                            submitBtn.setDisable(true);
                        }
                    } else {
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                        submitBtn.setDisable(true);
                    }
                }));
            });

            submitBtn.setOnAction(e -> {
                submitBtn.setDisable(true);
                submissionController.submitV2Space(activeSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    submitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        this.activeSpace = res.getData();
                        renderV2Details();
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, submitBtn);

        } else if ("SUBMITTED".equals(approvalStatus)) {
            msgLbl.setText("Submitted on " + formatDate(activeSpace.getSubmittedAt()) + ". Waiting for developer review.");
            msgLbl.setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");

        } else if ("UNDER_REVIEW".equals(approvalStatus)) {
            msgLbl.setText("Under developer review.");
            msgLbl.setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");

        } else if ("REJECTED".equals(approvalStatus)) {
            String reason = (activeSpace.getRejectionReason() != null && !activeSpace.getRejectionReason().isBlank())
                    ? activeSpace.getRejectionReason()
                    : "No specific reason provided.";
            msgLbl.setText("Status: Rejected. Reason: " + reason + "\nUpdate space details/configurations and resubmit for review.");
            msgLbl.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

            Button checkBtn = OwnerUi.secondaryButton("Check Readiness");
            Button resubmitBtn = OwnerUi.primaryButton("Resubmit Space");
            resubmitBtn.setDisable(true);

            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkV2SpaceReadiness(activeSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Space is 100% ready for resubmission.");
                            resubmitBtn.setDisable(false);
                        } else {
                            feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                            feedbackLbl.setText("Missing Requirements:\n- " + String.join("\n- ", res.getData().getMissingRequirements()));
                            resubmitBtn.setDisable(true);
                        }
                    } else {
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                        resubmitBtn.setDisable(true);
                    }
                }));
            });

            resubmitBtn.setOnAction(e -> {
                resubmitBtn.setDisable(true);
                submissionController.submitV2Space(activeSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    resubmitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        this.activeSpace = res.getData();
                        renderV2Details();
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, resubmitBtn);

        } else if ("APPROVED".equals(approvalStatus)) {
            if ("ACTIVE".equals(opStatus)) {
                msgLbl.setText("Live and available for future customer discovery.");
                msgLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");

                Button inactBtn = OwnerUi.secondaryButton("Deactivate");
                Button maintBtn = OwnerUi.secondaryButton("Set Maintenance");
                Button closeBtn = OwnerUi.dangerButton("Close");

                inactBtn.setOnAction(e -> changeOperationalStatus("INACTIVE", feedbackLbl));
                maintBtn.setOnAction(e -> changeOperationalStatus("MAINTENANCE", feedbackLbl));
                closeBtn.setOnAction(e -> changeOperationalStatus("CLOSED", feedbackLbl));

                btnBox.getChildren().addAll(inactBtn, maintBtn, closeBtn);

            } else if ("INACTIVE".equals(opStatus)) {
                msgLbl.setText("Approved by Developer. Space is currently INACTIVE.");
                msgLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");

                Button actBtn = OwnerUi.primaryButton("Activate Space");
                actBtn.setOnAction(e -> changeOperationalStatus("ACTIVE", feedbackLbl));
                btnBox.getChildren().add(actBtn);

            } else if ("MAINTENANCE".equals(opStatus)) {
                msgLbl.setText("Space is currently under MAINTENANCE.");
                msgLbl.setStyle("-fx-text-fill:#E65100; -fx-font-weight:bold;");

                Button actBtn = OwnerUi.primaryButton("Activate Space");
                Button inactBtn = OwnerUi.secondaryButton("Deactivate");
                actBtn.setOnAction(e -> changeOperationalStatus("ACTIVE", feedbackLbl));
                inactBtn.setOnAction(e -> changeOperationalStatus("INACTIVE", feedbackLbl));
                btnBox.getChildren().addAll(actBtn, inactBtn);

            } else if ("CLOSED".equals(opStatus)) {
                msgLbl.setText("Space is currently CLOSED.");
                msgLbl.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

                Button actBtn = OwnerUi.primaryButton("Re-open (Activate)");
                Button inactBtn = OwnerUi.secondaryButton("Set Inactive");
                actBtn.setOnAction(e -> changeOperationalStatus("ACTIVE", feedbackLbl));
                inactBtn.setOnAction(e -> changeOperationalStatus("INACTIVE", feedbackLbl));
                btnBox.getChildren().addAll(actBtn, inactBtn);
            }
        }

        card.getChildren().addAll(header, msgLbl);
        if (!btnBox.getChildren().isEmpty()) {
            card.getChildren().add(btnBox);
        }
        card.getChildren().add(feedbackLbl);
        return card;
    }

    private void changeOperationalStatus(String newStatus, Label feedbackLbl) {
        submissionController.setV2OperationalStatus(activeSpace.getSpaceId(), newStatus).thenAccept(res -> Platform.runLater(() -> {
            if (res.isSuccess() && res.getData() != null) {
                this.activeSpace = res.getData();
                renderV2Details();
            } else {
                feedbackLbl.setVisible(true);
                feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                feedbackLbl.setText(res.getMessage());
            }
        }));
    }

    private VBox buildV2BookingConfigSummaryCard(BookingConfig config) {
        VBox card = OwnerUi.card();
        card.setSpacing(8);
        card.getChildren().add(OwnerUi.heading("Booking Configuration Summary", 16));

        if (config instanceof SeatBookingConfig s) {
            card.getChildren().addAll(
                    OwnerUi.body("Selection Mode: " + s.getSelectionMode(), 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Duration: Min " + s.getMinimumDurationMinutes() + " mins / Max " + s.getMaximumDurationMinutes() + " mins (Step: " + s.getDurationStepMinutes() + " mins)", 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Max Guests Per Booking: " + s.getMaxGuestsPerBooking(), 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Seat Price Mode: " + s.getSeatPriceMode() + " | Allow Multiple Seats: " + s.isAllowMultipleSeats(), 13, OwnerUi.TEXT_MUTED)
            );
        } else if (config instanceof SlotBookingConfig s) {
            card.getChildren().addAll(
                    OwnerUi.body("Duration: Min " + s.getMinimumDurationMinutes() + " mins / Max " + s.getMaximumDurationMinutes() + " mins (Step: " + s.getDurationStepMinutes() + " mins)", 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Slot Interval: " + s.getSlotIntervalMinutes() + " mins | Buffer: Before " + s.getBufferBeforeMinutes() + " mins / After " + s.getBufferAfterMinutes() + " mins", 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Max Capacity: " + s.getMaxPeople() + " people", 13, OwnerUi.TEXT_MUTED)
            );
        } else if (config instanceof WorkshopBookingConfig w) {
            card.getChildren().addAll(
                    OwnerUi.body("Participants: Min " + w.getMinimumParticipantsPerBooking() + " / Max " + w.getMaximumParticipantsPerBooking(), 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Cutoffs: Booking " + w.getBookingCutoffHours() + "h prior / Cancellation " + w.getCancellationCutoffHours() + "h prior", 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Instructor: " + (w.getInstructorName() != null ? w.getInstructorName() : "N/A"), 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Materials: " + (w.getMaterialsIncluded() != null ? String.join(", ", w.getMaterialsIncluded()) : "None"), 13, OwnerUi.TEXT_MUTED)
            );
        } else if (config instanceof RentalBookingConfig r) {
            card.getChildren().addAll(
                    OwnerUi.body("Available Quantity: " + r.getAvailableQuantity() + " units", 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Duration: Min " + r.getMinimumRentalDurationMinutes() + " mins / Max " + r.getMaximumRentalDurationMinutes() + " mins", 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Pickup: " + r.isPickupAvailable() + " | Delivery: " + r.isDeliveryAvailable() + " (Radius: " + r.getDeliveryRadiusKm() + " Km)", 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Security Deposit: ₹" + BigDecimal.valueOf(r.getSecurityDepositPaise(), 2).toPlainString(), 13, OwnerUi.TEXT_MUTED)
            );
        } else if (config instanceof VenueBookingConfig v) {
            card.getChildren().addAll(
                    OwnerUi.body("Duration: Min " + v.getMinimumBookingHours() + " hours / Max " + v.getMaximumBookingHours() + " hours", 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Requires Owner Approval: " + v.isRequiresApproval(), 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Allowed Event Types: " + (v.getAllowedEventTypes() != null ? String.join(", ", v.getAllowedEventTypes()) : "All"), 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Facilities: Sound: " + v.isSoundSystemAvailable() + ", Lighting: " + v.isLightingAvailable() + ", Stage: " + v.isStageAvailable() + ", Green Room: " + v.isGreenRoomAvailable(), 13, OwnerUi.TEXT_MUTED)
            );
        } else if (config instanceof PracticeRoomBookingConfig p) {
            card.getChildren().addAll(
                    OwnerUi.body("Duration: Min " + p.getMinimumDurationMinutes() + " mins / Max " + p.getMaximumDurationMinutes() + " mins (Step: " + p.getDurationStepMinutes() + " mins)", 13, OwnerUi.TEXT_DARK),
                    OwnerUi.body("Equipment Selection Enabled: " + p.isEquipmentSelectionEnabled(), 13, OwnerUi.TEXT_MUTED),
                    OwnerUi.body("Included Equipment: " + (p.getIncludedEquipment() != null ? String.join(", ", p.getIncludedEquipment()) : "None"), 13, OwnerUi.TEXT_MUTED)
            );
        } else {
            card.getChildren().add(OwnerUi.body("Standard single-unit configuration.", 13, OwnerUi.TEXT_MUTED));
        }

        return card;
    }

    private VBox buildV2OperatingHoursCard(Map<String, OperatingDayConfig> hours) {
        VBox card = OwnerUi.card();
        card.setSpacing(8);
        card.getChildren().add(OwnerUi.heading("Operating Schedule", 16));

        if (hours != null && !hours.isEmpty()) {
            for (Map.Entry<String, OperatingDayConfig> entry : hours.entrySet()) {
                OperatingDayConfig cfg = entry.getValue();
                String timeStr = (cfg != null && cfg.isActive()) ?
                        (cfg.getOpeningTime() + " - " + cfg.getClosingTime()) : "CLOSED";
                card.getChildren().add(OwnerUi.body(entry.getKey() + ": " + timeStr, 13, cfg != null && cfg.isActive() ? OwnerUi.TEXT_DARK : OwnerUi.TEXT_MUTED));
            }
        } else {
            card.getChildren().add(OwnerUi.body("No specific schedule set.", 13, OwnerUi.TEXT_MUTED));
        }

        return card;
    }

    // ==================== V1 DETAILS RENDERING ====================

    private void renderV1Details() {
        container.getChildren().clear();

        if (initialSpace == null) {
            container.getChildren().add(OwnerUi.body("No space selected.", 14, OwnerUi.ERROR));
            return;
        }

        // Header Title & Status Badge
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = OwnerUi.heading(initialSpace.getSpaceName() != null ? initialSpace.getSpaceName() : "Space Details", 24);
        Label badge = OwnerUi.badge(initialSpace.getStatus() != null ? initialSpace.getStatus() : SpaceStatus.DRAFT);
        titleRow.getChildren().addAll(nameLbl, badge);

        Button backBtn = OwnerUi.secondaryButton("← Back to Spaces");
        backBtn.setOnAction(e -> SpaceFlowRouter.goToSpaceList());

        HBox actionBox = new HBox(12, backBtn);

        String statusStr = initialSpace.getStatus() != null ? initialSpace.getStatus().toUpperCase() : SpaceStatus.DRAFT;
        if (SpaceStatus.DRAFT.equals(statusStr) || SpaceStatus.REJECTED.equals(statusStr)) {
            Button editBtn = OwnerUi.primaryButton("Edit Space");
            editBtn.setOnAction(e -> {
                spaceController.loadSpace(initialSpace.getSpaceId()).thenAccept(freshRes -> Platform.runLater(() -> {
                    if (freshRes.isSuccess() && freshRes.getData() != null) {
                        SpaceFlowRouter.goToEditSpace(freshRes.getData());
                    } else {
                        SpaceFlowRouter.goToEditSpace(initialSpace);
                    }
                }));
            });
            actionBox.getChildren().add(editBtn);
        }

        VBox submissionCard = buildV1SubmissionBannerCard();

        VBox bizCard = OwnerUi.card();
        Label bizLabel = OwnerUi.body("Loading Business Information...", 13, OwnerUi.TEXT_MUTED);
        bizCard.getChildren().addAll(OwnerUi.heading("Business Association", 16), bizLabel);

        if (initialSpace.getBusinessId() != null) {
            businessController.loadCurrentOwnerBusinesses().thenAccept(bRes -> {
                if (bRes.isSuccess() && bRes.getData() != null) {
                    for (Business b : bRes.getData()) {
                        if (b.getBusinessId().equals(initialSpace.getBusinessId())) {
                            Platform.runLater(() -> bizLabel.setText("Business Name: " + b.getBusinessName() + " (" + b.getCity() + ") | Verification: " + b.getVerificationStatus()));
                            return;
                        }
                    }
                    Platform.runLater(() -> bizLabel.setText("Business ID: " + initialSpace.getBusinessId()));
                }
            });
        }

        VBox classificationCard = OwnerUi.card();
        classificationCard.setSpacing(8);
        classificationCard.getChildren().addAll(
                OwnerUi.heading("Classification & Overview", 16),
                OwnerUi.body("Category: " + (initialSpace.getCategory() != null ? initialSpace.getCategory() : "N/A"), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Activity Group: " + (initialSpace.getActivityGroup() != null ? initialSpace.getActivityGroup() : "N/A"), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Space Type: " + (initialSpace.getSpaceType() != null ? initialSpace.getSpaceType() : "N/A"), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Capacity: " + initialSpace.getCapacity() + " guests", 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Description: " + (initialSpace.getDescription() != null ? initialSpace.getDescription() : "No description provided."), 13, OwnerUi.TEXT_MUTED)
        );

        VBox locationCard = OwnerUi.card();
        locationCard.setSpacing(8);

        String fullAddress = (initialSpace.getAddressLine() != null ? initialSpace.getAddressLine() : "") +
                (initialSpace.getLandmark() != null && !initialSpace.getLandmark().isBlank() ? ", Near " + initialSpace.getLandmark() : "") +
                (initialSpace.getCity() != null ? ", " + initialSpace.getCity() : "") +
                (initialSpace.getState() != null ? ", " + initialSpace.getState() : "") +
                (initialSpace.getPostalCode() != null ? " - " + initialSpace.getPostalCode() : "");

        locationCard.getChildren().addAll(
                OwnerUi.heading("Location & Operating Hours", 16),
                OwnerUi.body("Address: " + fullAddress, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Operating Days: " + initialSpace.getOperatingDaysDisplay(), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Operating Hours: " + (initialSpace.getOpeningTime() != null ? initialSpace.getOpeningTime() : "") + " - " + (initialSpace.getClosingTime() != null ? initialSpace.getClosingTime() : ""), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Time Zone: " + initialSpace.getTimeZoneId(), 13, OwnerUi.TEXT_MUTED)
        );

        VBox amenitiesCard = OwnerUi.card();
        amenitiesCard.setSpacing(8);

        String amenitiesStr = initialSpace.getAmenities() != null && !initialSpace.getAmenities().isEmpty() ?
                String.join(", ", initialSpace.getAmenities()) : "None specified";
        String activitiesStr = initialSpace.getSuitableActivities() != null && !initialSpace.getSuitableActivities().isEmpty() ?
                String.join(", ", initialSpace.getSuitableActivities()) : "None specified";
        String rulesStr = initialSpace.getRules() != null && !initialSpace.getRules().isEmpty() ?
                String.join("; ", initialSpace.getRules()) : "Standard venue rules apply.";

        amenitiesCard.getChildren().addAll(
                OwnerUi.heading("Amenities & Venue Rules", 16),
                OwnerUi.body("Amenities: " + amenitiesStr, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Suitable Activities: " + activitiesStr, 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("House Rules: " + rulesStr, 13, OwnerUi.TEXT_DARK)
        );

        VBox resourcesSection = buildV1ResourcesSection();
        VBox offeringsSection = buildV1OfferingsSection();

        VBox metaCard = OwnerUi.card();
        metaCard.setSpacing(8);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String createdStr = initialSpace.getCreatedAt() > 0 ? sdf.format(new Date(initialSpace.getCreatedAt())) : "N/A";
        String updatedStr = initialSpace.getUpdatedAt() > 0 ? sdf.format(new Date(initialSpace.getUpdatedAt())) : "N/A";

        metaCard.getChildren().addAll(
                OwnerUi.heading("Record Metadata", 16),
                OwnerUi.body("Created Date: " + createdStr, 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Last Updated: " + updatedStr, 12, OwnerUi.TEXT_MUTED)
        );

        container.getChildren().addAll(
                titleRow, actionBox, submissionCard, bizCard, classificationCard,
                locationCard, amenitiesCard, resourcesSection, offeringsSection, metaCard
        );
    }

    private VBox buildV1SubmissionBannerCard() {
        VBox card = OwnerUi.card();
        card.setSpacing(12);

        String status = initialSpace.getStatus() != null ? initialSpace.getStatus().toUpperCase() : SpaceStatus.DRAFT;

        Label header = OwnerUi.heading("Space Submission Readiness & Review Status", 16);
        Label msgLbl = new Label();
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size:13px;");

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_LEFT);

        Label feedbackLbl = new Label();
        feedbackLbl.setWrapText(true);
        feedbackLbl.setStyle("-fx-font-size:13px;");
        feedbackLbl.setVisible(false);

        if (SpaceStatus.DRAFT.equals(status)) {
            msgLbl.setText("This Space is currently in DRAFT status. Verify parent Business approval, active Resources, Offerings, and Availability rules before submitting for review.");
            msgLbl.setStyle("-fx-text-fill:" + OwnerUi.TEXT_MUTED + ";");

            Button checkBtn = OwnerUi.secondaryButton("Check Submission Readiness");
            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkSpaceReadiness(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Space is 100% ready for submission.");
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

            Button submitBtn = OwnerUi.primaryButton("Submit Space");
            submitBtn.setOnAction(e -> {
                submitBtn.setDisable(true);
                submissionController.submitSpace(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    submitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        SpaceFlowRouter.goToSpaceDetails(res.getData());
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, submitBtn);

        } else if (SpaceStatus.SUBMITTED.equals(status)) {
            msgLbl.setText("Submitted on " + formatDate(initialSpace.getSubmittedAt()) + ". Pending developer review in Firebase Console.");
            msgLbl.setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");

        } else if (SpaceStatus.LIVE.equals(status)) {
            msgLbl.setText("This Space is live and ready for customer discovery.");
            msgLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");

        } else if (SpaceStatus.REJECTED.equals(status)) {
            String reason = (initialSpace.getRejectionReason() != null && !initialSpace.getRejectionReason().isBlank())
                    ? initialSpace.getRejectionReason()
                    : "No specific reason provided.";
            msgLbl.setText("Status: Rejected. Reason: " + reason + "\nUpdate space details/configurations below and resubmit for review.");
            msgLbl.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

            Button checkBtn = OwnerUi.secondaryButton("Check Readiness");
            checkBtn.setOnAction(e -> {
                checkBtn.setDisable(true);
                submissionController.checkSpaceReadiness(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    checkBtn.setDisable(false);
                    feedbackLbl.setVisible(true);
                    if (res.isSuccess() && res.getData() != null) {
                        if (res.getData().isReady()) {
                            feedbackLbl.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");
                            feedbackLbl.setText("✔ Space is 100% ready for resubmission.");
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

            Button resubmitBtn = OwnerUi.primaryButton("Resubmit Space");
            resubmitBtn.setOnAction(e -> {
                resubmitBtn.setDisable(true);
                submissionController.submitSpace(initialSpace.getSpaceId()).thenAccept(res -> Platform.runLater(() -> {
                    resubmitBtn.setDisable(false);
                    if (res.isSuccess() && res.getData() != null) {
                        SpaceFlowRouter.goToSpaceDetails(res.getData());
                    } else {
                        feedbackLbl.setVisible(true);
                        feedbackLbl.setStyle("-fx-text-fill:#C62828;");
                        feedbackLbl.setText(res.getMessage());
                    }
                }));
            });

            btnBox.getChildren().addAll(checkBtn, resubmitBtn);
        }

        card.getChildren().addAll(header, msgLbl);
        if (!btnBox.getChildren().isEmpty()) {
            card.getChildren().add(btnBox);
        }
        card.getChildren().add(feedbackLbl);
        return card;
    }

    private VBox buildV1ResourcesSection() {
        VBox box = new VBox(12);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label heading = OwnerUi.heading("Space Resources", 18);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addResBtn = OwnerUi.primaryButton("+ Add Resource");
        addResBtn.setOnAction(e -> ResourceFlowRouter.goToCreateResource(initialSpace));

        headerRow.getChildren().addAll(heading, spacer, addResBtn);

        VBox resourceListContainer = new VBox(12);
        Label loadingLbl = OwnerUi.body("Loading resources...", 13, OwnerUi.TEXT_MUTED);
        resourceListContainer.getChildren().add(loadingLbl);

        resourceController.loadResourcesForSpace(initialSpace.getSpaceId()).thenAccept(resResult -> Platform.runLater(() -> {
            resourceListContainer.getChildren().clear();

            if (resResult.isSuccess() && resResult.getData() != null && !resResult.getData().isEmpty()) {
                List<Resource> resources = resResult.getData();
                for (Resource r : resources) {
                    resourceListContainer.getChildren().add(buildV1ResourceCard(r));
                }
            } else {
                VBox emptyCard = OwnerUi.card();
                emptyCard.getChildren().addAll(
                        OwnerUi.heading("No Resources Configured", 15),
                        OwnerUi.body("No Resources configured. Add the physical rooms, seats, equipment, or venue areas available in this Space.", 13, OwnerUi.TEXT_MUTED)
                );
                resourceListContainer.getChildren().add(emptyCard);
            }
        }));

        box.getChildren().addAll(headerRow, resourceListContainer);
        return box;
    }

    private VBox buildV1ResourceCard(Resource r) {
        VBox card = OwnerUi.card();
        card.setSpacing(6);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = OwnerUi.heading(r.getResourceName(), 16);
        Label codeLbl = OwnerUi.body("[" + r.getResourceCode() + "]", 13, OwnerUi.TEXT_MUTED);
        Label badge = OwnerUi.badge(r.getStatus() != null ? r.getStatus() : "AVAILABLE");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button availBtn = OwnerUi.primaryButton("Manage Availability");
        availBtn.setOnAction(e -> ResourceFlowRouter.goToManageAvailability(initialSpace, r));

        Button editBtn = OwnerUi.secondaryButton("Edit");
        editBtn.setOnAction(e -> ResourceFlowRouter.goToEditResource(initialSpace, r));

        topRow.getChildren().addAll(nameLbl, codeLbl, badge, spacer, availBtn, editBtn);

        String eqStr = r.getEquipment() != null && !r.getEquipment().isEmpty() ? String.join(", ", r.getEquipment()) : "None";
        String amStr = r.getAmenities() != null && !r.getAmenities().isEmpty() ? String.join(", ", r.getAmenities()) : "None";

        card.getChildren().addAll(
                topRow,
                OwnerUi.body("Type: " + r.getResourceType() + " | Capacity: " + r.getCapacity() + " unit(s)", 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Equipment: " + eqStr, 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body("Amenities: " + amStr, 12, OwnerUi.TEXT_MUTED),
                OwnerUi.body(r.getDescription() != null ? r.getDescription() : "", 12, OwnerUi.TEXT_MUTED)
        );

        return card;
    }

    private VBox buildV1OfferingsSection() {
        VBox box = new VBox(12);

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label heading = OwnerUi.heading("Space Offerings", 18);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addOffBtn = OwnerUi.primaryButton("+ Add Offering");
        addOffBtn.setOnAction(e -> OfferingFlowRouter.goToCreateOffering(initialSpace));

        headerRow.getChildren().addAll(heading, spacer, addOffBtn);

        VBox offeringListContainer = new VBox(12);
        Label loadingLbl = OwnerUi.body("Loading offerings...", 13, OwnerUi.TEXT_MUTED);
        offeringListContainer.getChildren().add(loadingLbl);

        offeringController.loadOfferingsForSpace(initialSpace.getSpaceId()).thenAccept(offResult -> Platform.runLater(() -> {
            offeringListContainer.getChildren().clear();

            if (offResult.isSuccess() && offResult.getData() != null && !offResult.getData().isEmpty()) {
                List<Offering> offerings = offResult.getData();
                for (Offering o : offerings) {
                    offeringListContainer.getChildren().add(buildV1OfferingCard(o));
                }
            } else {
                VBox emptyCard = OwnerUi.card();
                emptyCard.getChildren().addAll(
                        OwnerUi.heading("No Offerings Configured", 15),
                        OwnerUi.body("No Offerings configured. Create bookable packages for your customer.", 13, OwnerUi.TEXT_MUTED)
                );
                offeringListContainer.getChildren().add(emptyCard);
            }
        }));

        box.getChildren().addAll(headerRow, offeringListContainer);
        return box;
    }

    private VBox buildV1OfferingCard(Offering o) {
        VBox card = OwnerUi.card();
        card.setSpacing(6);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = OwnerUi.heading(o.getOfferingName(), 16);
        Label badge = OwnerUi.badge(o.getStatus() != null ? o.getStatus() : OfferingStatus.DRAFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = OwnerUi.secondaryButton("Edit");
        editBtn.setOnAction(e -> OfferingFlowRouter.goToEditOffering(initialSpace, o));
        if (!OfferingStatus.DRAFT.equalsIgnoreCase(o.getStatus())) {
            editBtn.setDisable(true);
        }

        topRow.getChildren().addAll(nameLbl, badge, spacer, editBtn);

        String priceDisplay = "₹" + o.getPriceRupeesString() + " (" + o.getPricingType() + ")";
        String durationSummary = "Duration: " + o.getDefaultDurationMinutes() + " mins (Min " + o.getMinimumDurationMinutes() + " / Max " + o.getMaximumDurationMinutes() + ")";

        card.getChildren().addAll(
                topRow,
                OwnerUi.body("Price: " + priceDisplay + " | Booking Type: " + o.getBookingType(), 13, OwnerUi.TEXT_DARK),
                OwnerUi.body("Required Resource: " + o.getRequiredResourceType() + " | " + durationSummary, 12, OwnerUi.TEXT_DARK),
                OwnerUi.body(o.getDescription() != null ? o.getDescription() : "", 12, OwnerUi.TEXT_MUTED)
        );

        return card;
    }

    // ==================== HELPER METHODS ====================

    private static String formatDate(long millis) {
        return millis > 0 ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(millis)) : "N/A";
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
