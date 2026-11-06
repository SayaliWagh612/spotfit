package com.eklavya.spotfit.view.owner;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class BusinessSettingsPage {

    public VBox getContent(BorderPane rightPane) {
        TextField businessName = OwnerUi.textField("Acme Studios");
        TextArea address = OwnerUi.textArea("Business address", 3);

        VBox statusCard = OwnerUi.card();
        statusCard.getChildren().addAll(OwnerUi.heading("Business Status", 15), OwnerUi.badge("Verified"),
                OwnerUi.body("Verification: Approved", 12, OwnerUi.TEXT_MUTED));

        var cancelBtn = OwnerUi.secondaryButton("Cancel Changes");
        cancelBtn.setOnAction(event -> {
            System.out.println("back");
            PageRegistry.go("dashboard");
        });
        var saveBtn = OwnerUi.primaryButton("Save Business Settings");
        saveBtn.setOnAction(event -> {
            System.out.println("saved");
            PageRegistry.go("dashboard");
        });
        HBox actions = new HBox(12, cancelBtn, saveBtn);
        HBox.setHgrow(saveBtn, Priority.ALWAYS);

        VBox form = new VBox(14,
                OwnerUi.fieldLabel("Business Name"), businessName,
                OwnerUi.fieldLabel("Business Address"), address,
                statusCard, actions);
        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);
        formCard.setMaxWidth(600);

        // Related settings pages still build their own full Scene (they
        // aren't sidebar destinations). "Back" from them now restores the
        // shell's one persistent Scene, whose rightPane is still showing
        // this exact Business Settings content underneath - no captured
        // Scene field needed anymore.
        var accountBtn = OwnerUi.secondaryButton("Account & Security \u2192");
        accountBtn.setOnAction(event -> {
            System.out.println("account settings");
            OwnerUi.navigate(new AccountSettingsPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });

        var payoutBtn = OwnerUi.secondaryButton("Payout Settings \u2192");
        payoutBtn.setOnAction(event -> {
            System.out.println("payout settings");
            OwnerUi.navigate(new PayoutSettingsPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });

        var billingBtn = OwnerUi.secondaryButton("Subscription & Billing \u2192");
        billingBtn.setOnAction(event -> {
            System.out.println("subscription billing");
            OwnerUi.navigate(new SubscriptionBillingPage().getScene(() -> OwnerUi.navigate(OwnerShell.getScene())));
        });

        VBox relatedCard = OwnerUi.card();
        relatedCard.getChildren().addAll(OwnerUi.heading("Related Settings", 15),
                accountBtn, payoutBtn, billingBtn);

        VBox content = new VBox(20, formCard, relatedCard);
        content.setPadding(new Insets(0, 0, 0, 0));
        return content;
    }
}

