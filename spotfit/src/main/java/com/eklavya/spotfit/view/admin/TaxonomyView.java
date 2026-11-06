package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class TaxonomyView {

    private String currentTab = "Categories";

    public VBox getContent(String initialTab) {
        if (initialTab != null) {
            this.currentTab = initialTab;
        }

        Label title = heading("Catalogue & Taxonomy Governance", 24);
        Label subtitle = muted("Govern Categories, Activities, Personas, and Interests. Rule: Deactivate rather than delete referenced items to maintain historical references.");

        Button btnCategories = secondaryButton("Categories");
        Button btnActivities = secondaryButton("Activities");
        Button btnPersonas = secondaryButton("Personas");
        Button btnInterests = secondaryButton("Interests");

        updateTabButtonStyles(btnCategories, btnActivities, btnPersonas, btnInterests);

        Button addBtn = primaryButton("+ Add New " + currentTab.substring(0, currentTab.length() - 1));
        addBtn.setOnAction(e -> showAddTaxonomyModal());

        HBox topBar = new HBox(12, btnCategories, btnActivities, btnPersonas, btnInterests, hgrow(), addBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox taxonomyList = new VBox(12);
        populateList(taxonomyList);

        btnCategories.setOnAction(e -> { currentTab = "Categories"; updateTabButtonStyles(btnCategories, btnActivities, btnPersonas, btnInterests); populateList(taxonomyList); });
        btnActivities.setOnAction(e -> { currentTab = "Activities"; updateTabButtonStyles(btnCategories, btnActivities, btnPersonas, btnInterests); populateList(taxonomyList); });
        btnPersonas.setOnAction(e -> { currentTab = "Personas"; updateTabButtonStyles(btnCategories, btnActivities, btnPersonas, btnInterests); populateList(taxonomyList); });
        btnInterests.setOnAction(e -> { currentTab = "Interests"; updateTabButtonStyles(btnCategories, btnActivities, btnPersonas, btnInterests); populateList(taxonomyList); });

        VBox contentCard = card(topBar, taxonomyList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private void updateTabButtonStyles(Button b1, Button b2, Button b3, Button b4) {
        b1.setStyle(currentTab.equals("Categories") ? activeTabStyle() : inactiveTabStyle());
        b2.setStyle(currentTab.equals("Activities") ? activeTabStyle() : inactiveTabStyle());
        b3.setStyle(currentTab.equals("Personas") ? activeTabStyle() : inactiveTabStyle());
        b4.setStyle(currentTab.equals("Interests") ? activeTabStyle() : inactiveTabStyle());
    }

    private String activeTabStyle() {
        return "-fx-background-color:" + ACCENT + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10px;";
    }

    private String inactiveTabStyle() {
        return "-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT + "; -fx-border-color:" + BORDER + "; -fx-border-radius:10px;";
    }

    private void populateList(VBox list) {
        list.getChildren().clear();
        if (currentTab.equals("Categories")) {
            list.getChildren().addAll(
                    createTaxonomyItem("CAT-101", "Photography & Film", "Studio spaces for photo shoots, video creation", "184 Spaces", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("CAT-102", "Podcast & Audio Production", "Acoustically treated podcast booths", "92 Spaces", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("CAT-103", "Fitness & Gym", "Equipped workout spaces and personal training floors", "140 Spaces", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("CAT-104", "Yoga & Wellness", "Meditation and holistic wellness spaces", "88 Spaces", "ACTIVE", GREEN_BG, GREEN_TEXT)
            );
        } else if (currentTab.equals("Activities")) {
            list.getChildren().addAll(
                    createTaxonomyItem("ACT-201", "Portrait Photography", "Single/Group headshots and fashion shoots", "210 Offerings", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("ACT-202", "Video Podcast Recording", "Multi-cam podcasting & streaming", "95 Offerings", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("ACT-203", "CrossFit HIIT Training", "High intensity functional workouts", "64 Offerings", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("ACT-204", "Legacy Aerobics (Obsolete)", "Deactivated legacy activity category", "12 Offerings", "DEACTIVATED", CHIP_BG, TEXT)
            );
        } else if (currentTab.equals("Personas")) {
            list.getChildren().addAll(
                    createTaxonomyItem("PRS-301", "Content Creator", "Independent YouTubers, Influencers, Podcasters", "4,200 Users", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("PRS-302", "Fitness Enthusiast", "Gymgoers, athletes, wellness seekers", "6,100 Users", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("PRS-303", "Corporate Team Lead", "Organizers of team workshops and offsites", "1,800 Users", "ACTIVE", GREEN_BG, GREEN_TEXT)
            );
        } else { // Interests
            list.getChildren().addAll(
                    createTaxonomyItem("INT-401", "Cinematography", "Lighting setups, cameras, color grading", "3,400 Users", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("INT-402", "Calisthenics & Strength", "Bodyweight and heavy strength training", "2,900 Users", "ACTIVE", GREEN_BG, GREEN_TEXT),
                    createTaxonomyItem("INT-403", "Audio Engineering", "Sound mixing, vocal recording", "1,500 Users", "ACTIVE", GREEN_BG, GREEN_TEXT)
            );
        }
    }

    private HBox createTaxonomyItem(String id, String name, String desc, String usage, String status, String bg, String fg) {
        Label iconLbl = new Label("🏷");
        iconLbl.setStyle("-fx-font-size:18px; -fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px; -fx-padding:8;");

        VBox info = new VBox(2,
                heading(name + " (" + id + ")", 15),
                muted(desc + " · Usage: " + usage)
        );

        Label statusChip = chip(status, bg, fg);

        Button editBtn = secondaryButton("Edit / Deactivate");
        editBtn.setOnAction(e -> showEditTaxonomyModal(id, name, desc, status));

        HBox row = new HBox(16, iconLbl, info, hgrow(), statusChip, editBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showAddTaxonomyModal() {
        Label title = heading("Add New " + currentTab.substring(0, currentTab.length() - 1), 18);

        Label nameLbl = heading("Item Name:", 14);
        TextField nameField = searchField("e.g. Pilates Studio");

        Label descLbl = heading("Description:", 14);
        TextField descField = searchField("Brief description...");

        Button saveBtn = primaryButton("Add to Approved Catalogue");

        VBox body = new VBox(14, title, nameLbl, nameField, descLbl, descField, saveBtn);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Add Taxonomy Item", body);

        saveBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()) return;
            dialog.close();
            showNotice("Taxonomy Item Added", nameField.getText() + " added to " + currentTab + " catalogue.");
        });
    }

    private void showEditTaxonomyModal(String id, String name, String desc, String currentStatus) {
        Label title = heading("Taxonomy Governance - " + name + " (" + id + ")", 18);

        Label nameLbl = heading("Display Name (Renaming preserves internal ID):", 14);
        TextField nameField = searchField(name);
        nameField.setText(name);

        VBox warningBox = new VBox(6,
                heading("Governance Rule:", 13),
                muted("Once referenced by Spaces or Users, items should be DEACTIVATED instead of deleted to preserve historical associations.")
        );
        warningBox.setPadding(new Insets(10));
        warningBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:8px;");

        Button btnSave = primaryButton("Save Changes");
        Button btnToggleStatus = secondaryButton(currentStatus.equals("DEACTIVATED") ? "Reactivate Item" : "Deactivate Item (Soft Disable)");

        HBox btns = new HBox(10, btnSave, btnToggleStatus);

        VBox body = new VBox(14, title, nameLbl, nameField, warningBox, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Edit Catalogue Item", body);

        btnSave.setOnAction(e -> {
            dialog.close();
            showNotice("Taxonomy Updated", name + " display name updated. ID " + id + " preserved.");
        });

        btnToggleStatus.setOnAction(e -> {
            dialog.close();
            showNotice("Status Updated", name + " status set to " + (currentStatus.equals("DEACTIVATED") ? "ACTIVE" : "DEACTIVATED") + ".");
        });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Taxonomy Action", notice);
    }
}

