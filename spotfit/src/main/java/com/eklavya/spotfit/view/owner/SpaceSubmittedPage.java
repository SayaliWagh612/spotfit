package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.model.Space;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class SpaceSubmittedPage {

    public Scene getScene() {
        return getScene(null);
    }

    public Scene getScene(Space space) {
        Label check = new Label("\u2713");
        check.setStyle("-fx-font-size:36px; -fx-text-fill:white; -fx-background-color:" + OwnerUi.SUCCESS
                + "; -fx-background-radius:999; -fx-padding:14 20; -fx-font-weight:800;");

        Label title = OwnerUi.heading("Space Submitted Successfully \uD83C\uDF89", 24);
        Label status = OwnerUi.badge("\uD83D\uDFE1 Pending Approval");

        String spaceNameStr = space != null ? space.getName() : "New SpotFit Studio";
        String spaceIdStr = space != null ? space.getSpaceId() : "space-new";

        VBox refCard = OwnerUi.card();
        refCard.getChildren().addAll(
                detailRow("Space ID:", spaceIdStr),
                detailRow("Verification Status:", "Pending Approval"),
                detailRow("Space Name:", spaceNameStr),
                detailRow("Manager Status:", "Unassigned (Invite Manager anytime from My Spaces)")
        );
        refCard.setMaxWidth(Double.MAX_VALUE);

        Label quickActionsTitle = OwnerUi.heading("Quick Actions", 15);
        // A new space was just submitted, so any previously cached dashboard/
        // My Spaces scenes are now stale - clear them before navigating so
        // they rebuild with the new space included.
        PageRegistry.invalidate("dashboard");
        PageRegistry.invalidate("myspaces");

        var mySpacesBtn = OwnerUi.secondaryButton("Go to My Spaces");
        mySpacesBtn.setOnAction(e -> PageRegistry.go("myspaces"));

        var inviteBtn = OwnerUi.secondaryButton("Invite Manager");
        inviteBtn.setOnAction(e -> PageRegistry.go("staff"));

        var dashboardBtn = OwnerUi.primaryButton("Go to Owner Dashboard  \u2192");
        dashboardBtn.setOnAction(e -> PageRegistry.go("dashboard"));

        HBox quickActions = new HBox(10, mySpacesBtn, inviteBtn, dashboardBtn);
        VBox actionsBox = new VBox(10, quickActionsTitle, quickActions);
        actionsBox.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(20, check, title, status, refCard, actionsBox);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(32));

        ScrollPane scroller = new ScrollPane(content);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent; -fx-background:" + OwnerUi.BACKGROUND + ";");

        VBox root = new VBox(scroller);
        VBox.setVgrow(scroller, Priority.ALWAYS);
        root.setStyle("-fx-background-color:" + OwnerUi.BACKGROUND + ";");

        return OwnerUi.sizedScene(root);
    }

    private HBox detailRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(OwnerUi.fieldLabel(label), spacer, OwnerUi.body(value, 13, OwnerUi.TEXT_DARK));
        return row;
    }
}
