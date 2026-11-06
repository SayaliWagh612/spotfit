package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerComplaintController;
import com.eklavya.spotfit.model.Complaint;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class OwnerComplaintsPage {

    private final OwnerComplaintController controller = new OwnerComplaintController();
    private String currentFilter = null;
    private VBox listContainer;

    public VBox getContent(BorderPane rightPane) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        // Filter Bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        Button allBtn = OwnerUi.secondaryButton("All Complaints");
        Button newBtn = OwnerUi.secondaryButton("New");
        Button escBtn = OwnerUi.primaryButton("Escalated to Owner");
        Button resBtn = OwnerUi.secondaryButton("Resolved");

        allBtn.setOnAction(e -> { currentFilter = null; refreshList(); });
        newBtn.setOnAction(e -> { currentFilter = "NEW"; refreshList(); });
        escBtn.setOnAction(e -> { currentFilter = "ESCALATED"; refreshList(); });
        resBtn.setOnAction(e -> { currentFilter = "RESOLVED"; refreshList(); });

        filterBar.getChildren().addAll(allBtn, newBtn, escBtn, resBtn);

        listContainer = new VBox(14);
        refreshList();

        root.getChildren().addAll(filterBar, listContainer);
        return root;
    }

    private void refreshList() {
        listContainer.getChildren().clear();
        String currentScope = MockDataStore.getInstance().getSelectedSpaceId();
        List<Complaint> complaints = controller.getComplaints(currentScope, currentFilter);

        if (complaints.isEmpty()) {
            VBox emptyBox = OwnerUi.card();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            emptyBox.getChildren().add(OwnerUi.body("No complaints found for the selected space scope.", 14, OwnerUi.TEXT_MUTED));
            listContainer.getChildren().add(emptyBox);
            return;
        }

        for (Complaint c : complaints) {
            VBox card = OwnerUi.card();
            card.setSpacing(10);

            HBox header = new HBox(12);
            header.setAlignment(Pos.CENTER_LEFT);
            Label idLabel = OwnerUi.heading(c.getComplaintId(), 15);
            Label custLabel = OwnerUi.body("Customer: " + c.getCustomerName(), 13, OwnerUi.TEXT_MAIN);
            Label typeBadge = new Label(c.getType());
            typeBadge.setStyle("-fx-background-color:#EBF5FF; -fx-text-fill:#1E40AF; -fx-padding:4 8; -fx-background-radius:6; -fx-font-weight:bold; -fx-font-size:11px;");

            Label statusBadge = new Label(c.getStatus());
            statusBadge.setStyle("-fx-background-color:" + ("RESOLVED".equals(c.getStatus()) ? "#DEF7EC" : "#FDE8E8") +
                    "; -fx-text-fill:" + ("RESOLVED".equals(c.getStatus()) ? "#03543F" : "#9B1C1C") +
                    "; -fx-padding:4 8; -fx-background-radius:6; -fx-font-weight:bold; -fx-font-size:11px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            header.getChildren().addAll(idLabel, custLabel, typeBadge, spacer, statusBadge);

            Label subj = OwnerUi.heading(c.getSubject(), 14);
            Label desc = OwnerUi.body(c.getDescription(), 13, OwnerUi.TEXT_MUTED);

            VBox mgrNotesBox = new VBox(4);
            mgrNotesBox.setStyle("-fx-background-color:#F8FAFC; -fx-padding:10; -fx-background-radius:8; -fx-border-color:#E2E8F0; -fx-border-radius:8;");
            Label mgrTitle = OwnerUi.body("Space Manager Operational Notes:", 12, OwnerUi.TEXT_MAIN);
            mgrTitle.setStyle("-fx-font-weight:bold;");
            Label mgrContent = OwnerUi.body(c.getManagerNotes() != null && !c.getManagerNotes().isBlank() ? c.getManagerNotes() : "No manager notes recorded yet.", 12, OwnerUi.TEXT_MUTED);
            mgrNotesBox.getChildren().addAll(mgrTitle, mgrContent);

            HBox actions = new HBox(10);
            if (!"RESOLVED".equals(c.getStatus())) {
                Button resolveBtn = OwnerUi.primaryButton("Resolve Complaint");
                resolveBtn.setOnAction(e -> showResolveDialog(c));

                Button escalateBtn = OwnerUi.secondaryButton("Escalate to Admin");
                escalateBtn.setOnAction(e -> {
                    controller.escalateToAdmin(c.getComplaintId(), "Owner requested platform admin review.");
                    refreshList();
                });

                actions.getChildren().addAll(resolveBtn, escalateBtn);
            } else {
                Label resNote = OwnerUi.body("Resolution Note: " + (c.getOwnerNotes() != null ? c.getOwnerNotes() : "Resolved"), 12, OwnerUi.SUCCESS);
                actions.getChildren().add(resNote);
            }

            card.getChildren().addAll(header, subj, desc, mgrNotesBox, actions);
            listContainer.getChildren().add(card);
        }
    }

    private void showResolveDialog(Complaint c) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Resolve Complaint - " + c.getComplaintId());
        dialog.setHeaderText("Enter business resolution notes for customer " + c.getCustomerName());

        ButtonType resolveButtonType = new ButtonType("Confirm Resolution", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resolveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Type resolution details or refund confirmation...");
        content.getChildren().add(notesArea);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resolveButtonType) {
                return notesArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(notes -> {
            if (!notes.isBlank()) {
                controller.resolveComplaint(c.getComplaintId(), notes);
                refreshList();
            }
        });
    }
}

