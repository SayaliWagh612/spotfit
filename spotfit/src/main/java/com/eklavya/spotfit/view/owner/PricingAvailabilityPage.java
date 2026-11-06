package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.owner.OwnerSpaceController;
import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.view.spaceManager.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class PricingAvailabilityPage {

    private final OwnerSpaceController controller;
    private Scene pricingScene;

    public PricingAvailabilityPage() {
        this(new OwnerSpaceController());
    }

    public PricingAvailabilityPage(OwnerSpaceController controller) {
        this.controller = controller;
    }

    Scene getScene(Runnable callback) {
        Label headerTitle = OwnerUi.heading("Step 5 & 6: Define Offerings & Resources", 18);
        Label sub = OwnerUi.body("Add offerings (pricing/services) and bookable resources for this space.", 13, OwnerUi.TEXT_MUTED);

        // Step 5: Offering Form
        Label offLabel = OwnerUi.heading("Primary Offering", 15);
        TextField offName = OwnerUi.textField("e.g. Podcast Recording Session");
        TextField offPrice = OwnerUi.textField("1500");
        TextField offDuration = OwnerUi.textField("90");

        // Step 6: Resource Form
        Label resLabel = OwnerUi.heading("Bookable Resources", 15);
        TextField resName = OwnerUi.textField("e.g. Podcast Room A");
        TextField resType = OwnerUi.textField("Audio Studio");

        var backBtn = OwnerUi.secondaryButton("\u2190 Back");
        backBtn.setOnAction(event -> {
            if (callback != null) callback.run();
        });

        var continueBtn = OwnerUi.primaryButton("Review & Submit Listing \u2192");
        continueBtn.setOnAction(event -> {
            double price = 1500.0;
            try { price = Double.parseDouble(offPrice.getText().trim()); } catch (Exception ignored) {}

            int dur = 90;
            try { dur = Integer.parseInt(offDuration.getText().trim()); } catch (Exception ignored) {}

            List<Offering> offerings = new ArrayList<>();
            Offering off = new Offering();
            off.setOfferingId("off-temp-1");
            off.setOfferingName(offName.getText() != null && !offName.getText().isBlank() ? offName.getText() : "Standard Studio Booking");
            off.setBookingType("SLOT");
            off.setPricingType("PER_HOUR");
            off.setPricePaise((long)(price * 100));
            off.setDefaultDurationMinutes(dur);
            offerings.add(off);

            List<Resource> resources = new ArrayList<>();
            Resource res = new Resource();
            res.setResourceId("res-temp-1");
            res.setResourceName(resName.getText() != null && !resName.getText().isBlank() ? resName.getText() : "Studio Room 1");
            res.setResourceType(resType.getText() != null && !resType.getText().isBlank() ? resType.getText() : "ROOM");
            res.setStatus("AVAILABLE");
            resources.add(res);

            Runnable ran = () -> OwnerUi.navigate(pricingScene);
            BusinessVerificationPage next = new BusinessVerificationPage();
            OwnerUi.navigate(next.getScene(ran));
        });

        HBox actions = new HBox(12, backBtn, continueBtn);
        HBox.setHgrow(continueBtn, Priority.ALWAYS);
        HBox.setHgrow(backBtn, Priority.ALWAYS);

        VBox form = new VBox(14, headerTitle, sub,
                offLabel,
                OwnerUi.fieldLabel("Offering Name *"), offName,
                OwnerUi.fieldLabel("Price per Hour (\u20b9) *"), offPrice,
                OwnerUi.fieldLabel("Duration (Minutes) *"), offDuration,
                resLabel,
                OwnerUi.fieldLabel("Resource Name *"), resName,
                OwnerUi.fieldLabel("Resource Type *"), resType,
                actions);

        VBox formCard = OwnerUi.card();
        formCard.getChildren().add(form);

        VBox sidePanel = new VBox(20, OwnerUi.wizardProgressCard(4));

        Scene sc = OwnerUi.sizedScene(OwnerUi.wizardShell(4, formCard, sidePanel));
        pricingScene = sc;
        return pricingScene;
    }
}

