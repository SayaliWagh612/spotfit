package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerSpaceController;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class ReviewSubmitPage {

    private final OwnerSpaceController controller;
    private final List<Offering> offerings;
    private final List<Resource> resources;

    public ReviewSubmitPage() {
        this(new OwnerSpaceController(), null, null);
    }

    public ReviewSubmitPage(OwnerSpaceController controller, List<Offering> offerings, List<Resource> resources) {
        this.controller = controller;
        this.offerings = offerings;
        this.resources = resources;
    }

    Scene getScene(Runnable callback) {
        Label title = OwnerUi.heading("Step 7: Review & Submit Space Listing", 22);
        Label sub = OwnerUi.body("Verify all space classification, details, offerings, and resources before submitting.", 13, OwnerUi.TEXT_MUTED);

        VBox summary = OwnerUi.card();
        summary.getChildren().addAll(
                OwnerUi.badge("PENDING APPROVAL"),
                OwnerUi.heading("New SpotFit Space Submission", 18),
                reviewRow("Status", "PENDING_APPROVAL (Will be listed on My Spaces immediately)"),
                reviewRow("Manager Status", "Unassigned (Active Manager ID empty - Invite Manager available later)"),
                reviewRow("Offering", offerings != null && !offerings.isEmpty() ? offerings.get(0).getName() + " (\u20b9" + offerings.get(0).getPrice() + "/hr)" : "Standard Studio Booking"),
                reviewRow("Resource", resources != null && !resources.isEmpty() ? resources.get(0).getName() + " (" + resources.get(0).getType() + ")" : "Studio Room 1")
        );

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            if (callback != null) callback.run();
        });

        var submitBtn = OwnerUi.primaryButton("Submit Space Listing  \u2192");
        submitBtn.setOnAction(event -> {
            Space createdSpace = controller.submitNewSpace(offerings, resources);
            OwnerUi.navigate(new SpaceSubmittedPage().getScene(createdSpace));
        });

        HBox actions = new HBox(12, backBtn, submitBtn);
        HBox.setHgrow(submitBtn, Priority.ALWAYS);

        VBox form = new VBox(16, title, sub, summary, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(6));
        return OwnerUi.sizedScene(OwnerUi.wizardShell(6, formCard, sidePanel));
    }

    private HBox reviewRow(String label, String value) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label l = OwnerUi.fieldLabel(label + ":");
        l.setMinWidth(110);
        Label v = OwnerUi.body(value, 12.5, OwnerUi.TEXT_DARK);
        v.setWrapText(true);
        row.getChildren().addAll(l, v);
        return row;
    }
}

