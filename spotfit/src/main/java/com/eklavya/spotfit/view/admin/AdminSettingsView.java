package com.eklavya.spotfit.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.admin.AdminDashboardUi.*;

public class AdminSettingsView {

    private String currentSection = "Audit Log";

    public VBox getContent(String initialSection) {
        if (initialSection != null) {
            this.currentSection = initialSection;
        }

        Label title = heading("Admin Settings & Platform Configuration", 24);
        Label subtitle = muted("Governs immutable Audit Logs, Recommendation parameters, Announcements, Featured spaces, and Platform policies.");

        Button btnAudit = secondaryButton("Immutable Audit Log");
        Button btnRecs = secondaryButton("Recommendations");
        Button btnAnnouncements = secondaryButton("Announcements");
        Button btnFeatured = secondaryButton("Featured Spaces");
        Button btnPolicies = secondaryButton("Platform Policies");

        updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies);

        HBox navRow = new HBox(12, btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies);

        VBox dynamicContainer = new VBox();
        renderSection(dynamicContainer);

        btnAudit.setOnAction(e -> { currentSection = "Audit Log"; updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies); renderSection(dynamicContainer); });
        btnRecs.setOnAction(e -> { currentSection = "Recommendations"; updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies); renderSection(dynamicContainer); });
        btnAnnouncements.setOnAction(e -> { currentSection = "Announcements"; updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies); renderSection(dynamicContainer); });
        btnFeatured.setOnAction(e -> { currentSection = "Featured Spaces"; updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies); renderSection(dynamicContainer); });
        btnPolicies.setOnAction(e -> { currentSection = "Platform Policies"; updateNavStyles(btnAudit, btnRecs, btnAnnouncements, btnFeatured, btnPolicies); renderSection(dynamicContainer); });

        VBox root = new VBox(24, title, subtitle, navRow, dynamicContainer);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private void updateNavStyles(Button b1, Button b2, Button b3, Button b4, Button b5) {
        b1.setStyle(currentSection.equals("Audit Log") ? activeBtnStyle() : inactiveBtnStyle());
        b2.setStyle(currentSection.equals("Recommendations") ? activeBtnStyle() : inactiveBtnStyle());
        b3.setStyle(currentSection.equals("Announcements") ? activeBtnStyle() : inactiveBtnStyle());
        b4.setStyle(currentSection.equals("Featured Spaces") ? activeBtnStyle() : inactiveBtnStyle());
        b5.setStyle(currentSection.equals("Platform Policies") ? activeBtnStyle() : inactiveBtnStyle());
    }

    private String activeBtnStyle() {
        return "-fx-background-color:" + ACCENT + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10px;";
    }

    private String inactiveBtnStyle() {
        return "-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT + "; -fx-border-color:" + BORDER + "; -fx-border-radius:10px;";
    }

    private void renderSection(VBox container) {
        container.getChildren().clear();

        if (currentSection.equals("Audit Log")) {
            container.getChildren().add(renderAuditLogSection());
        } else if (currentSection.equals("Recommendations")) {
            container.getChildren().add(renderRecommendationsSection());
        } else if (currentSection.equals("Announcements")) {
            container.getChildren().add(renderAnnouncementsSection());
        } else if (currentSection.equals("Featured Spaces")) {
            container.getChildren().add(renderFeaturedSpacesSection());
        } else {
            container.getChildren().add(renderPoliciesSection());
        }
    }

    // =========================================================
    // SECTION 1: IMMUTABLE AUDIT LOG
    // =========================================================

    private VBox renderAuditLogSection() {
        Label secTitle = heading("Immutable Platform Audit Log", 18);
        Label secSub = muted("Read-only platform activity trail. Admin cannot edit or delete audit entries. Supports filtering & export.");

        TextField search = searchField("🔍 Filter by Actor, Action, Entity ID, or Reason...");
        search.setPrefWidth(340);

        Button exportBtn = primaryButton("Export Audit Log (CSV/PDF)");
        exportBtn.setOnAction(e -> showNotice("Audit Log Exported", "Immutable audit log exported to file successfully."));

        HBox top = new HBox(12, search, hgrow(), exportBtn);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox logsList = new VBox(10,
                auditLogRow("LOG-8841", "Admin (admin@spotfit.com)", "SPACE_SUSPENDED", "Space #SPC-301", "APPROVED -> SUSPENDED", "Safety incident report #CMP-901", "13 Aug 2026 14:32"),
                auditLogRow("LOG-8840", "Admin (admin@spotfit.com)", "REFUND_OVERRIDE", "Booking #SPF-2650", "SUCCESSFUL -> REFUNDED", "Dispute resolution override", "13 Aug 2026 12:15"),
                auditLogRow("LOG-8839", "Admin (admin@spotfit.com)", "OWNER_VERIFIED", "Owner #OWN-201", "PENDING -> VERIFIED", "Completed GST & ID audit", "13 Aug 2026 11:04"),
                auditLogRow("LOG-8838", "Admin (admin@spotfit.com)", "REVIEW_HIDDEN", "Review #REV-404", "PUBLISHED -> HIDDEN", "Policy violation (Profanity)", "12 Aug 2026 16:45")
        );

        return card(secTitle, secSub, top, logsList);
    }

    private HBox auditLogRow(String logId, String actor, String action, String entity, String change, String reason, String time) {
        VBox info = new VBox(2,
                heading(logId + " · " + action + " by " + actor, 14),
                muted(entity + " · " + change + " · Reason: " + reason + " · " + time)
        );

        Button viewBtn = secondaryButton("View Detail");
        viewBtn.setOnAction(e -> showAuditDetailModal(logId, actor, action, entity, change, reason, time));

        HBox r = new HBox(info, hgrow(), viewBtn);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(10, 14, 10, 14));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    private void showAuditDetailModal(String logId, String actor, String action, String entity, String change, String reason, String time) {
        VBox body = new VBox(12,
                heading("Audit Record Detail (" + logId + ")", 18),
                detailRow("Actor & Role:", actor),
                detailRow("Action Performed:", action),
                detailRow("Affected Entity:", entity),
                detailRow("Previous -> New Values:", change),
                detailRow("Mandatory Reason:", reason),
                detailRow("Timestamp:", time),
                detailRow("Immutability:", "Read-Only / Cryptographically Hashed"),
                primaryButton("Close")
        );
        body.setPadding(new Insets(20));
        showModal("Audit Entry - " + logId, body);
    }

    // =========================================================
    // SECTION 2: RECOMMENDATION PARAMETERS
    // =========================================================

    private VBox renderRecommendationsSection() {
        Label secTitle = heading("Backend Recommendation Engine Parameters", 18);
        Label secSub = muted("Configure algorithm weights used by the backend engine to calculate personalized user recommendations.");

        VBox sliders = new VBox(14,
                weightSlider("Personalized User Relevance Weight", 35),
                weightSlider("Space Popularity & Booking Velocity Weight", 25),
                weightSlider("Rating & Review Score Weight", 20),
                weightSlider("Geographic Distance Proximity Weight", 10),
                weightSlider("Competitive Price Weight", 10)
        );

        Button saveBtn = primaryButton("Save Recommendation Rules");
        saveBtn.setOnAction(e -> showNotice("Recommendation Rules Saved", "Updated algorithm weights saved for backend recommendation engine."));

        return card(secTitle, secSub, sliders, saveBtn);
    }

    private VBox weightSlider(String labelText, double defaultVal) {
        Label l = heading(labelText + " (" + (int) defaultVal + "%)", 14);
        Slider slider = new Slider(0, 100, defaultVal);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            l.setText(labelText + " (" + newVal.intValue() + "%)");
        });

        VBox b = new VBox(6, l, slider);
        b.setPadding(new Insets(8, 12, 8, 12));
        b.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:8px;");
        return b;
    }

    // =========================================================
    // SECTION 3: ANNOUNCEMENTS
    // =========================================================

    private VBox renderAnnouncementsSection() {
        Label secTitle = heading("Platform Announcements Manager", 18);
        Label secSub = muted("Create lightweight broadcast announcements. Target: All Users, All Owners, All Managers, or Specific Role.");

        Button createBtn = primaryButton("+ Create New Announcement");
        createBtn.setOnAction(e -> showCreateAnnouncementModal());

        VBox announcementsList = new VBox(10,
                announcementRow("ANC-101", "Platform Maintenance Scheduled", "System upgrade on Sunday 2:00 AM - 4:00 AM", "Target: All Users & Owners", "PUBLISHED", GREEN_BG, GREEN_TEXT),
                announcementRow("ANC-102", "Updated Cancellation Policy Policy V2.1", "New refund window guidelines for space managers", "Target: Space Managers", "PUBLISHED", GREEN_BG, GREEN_TEXT),
                announcementRow("ANC-103", "Diwali Booking Offer Banner", "Festive promotion guidelines for space listings", "Target: All Owners", "DRAFT", AMBER_BG, AMBER_TEXT)
        );

        HBox annHeader = new HBox(heading("Platform Announcements", 18), hgrow(), createBtn);
        annHeader.setAlignment(Pos.CENTER_LEFT);

        return card(annHeader, secSub, announcementsList);
    }

    private HBox announcementRow(String id, String title, String bodyText, String target, String status, String bg, String fg) {
        VBox info = new VBox(2,
                heading(id + " · " + title, 15),
                muted(bodyText + " · " + target)
        );

        Label statusChip = chip(status, bg, fg);

        HBox r = new HBox(info, hgrow(), statusChip);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(10, 14, 10, 14));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    private void showCreateAnnouncementModal() {
        Label title = heading("Create Platform Announcement", 18);

        Label titleLbl = heading("Announcement Title:", 14);
        TextField titleField = searchField("e.g. Scheduled System Maintenance");

        Label bodyLbl = heading("Announcement Message Body:", 14);
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Enter broadcast message...");
        bodyArea.setPrefRowCount(3);

        Label targetLbl = heading("Target Audience:", 14);
        ComboBox<String> targetCombo = new ComboBox<>();
        targetCombo.getItems().addAll("All Users", "All Owners", "All Managers", "All Roles");
        targetCombo.getSelectionModel().selectFirst();
        targetCombo.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:8px;");

        Button btnDraft = secondaryButton("Save as Draft");
        Button btnPublish = primaryButton("Publish Announcement");

        HBox btns = new HBox(10, btnDraft, btnPublish);

        VBox body = new VBox(14, title, titleLbl, titleField, bodyLbl, bodyArea, targetLbl, targetCombo, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("New Announcement", body);

        btnDraft.setOnAction(e -> { dialog.close(); showNotice("Saved as Draft", "Announcement saved as Draft."); });
        btnPublish.setOnAction(e -> { dialog.close(); showNotice("Announcement Published", "Announcement broadcasted to " + targetCombo.getValue() + "."); });
    }

    // =========================================================
    // SECTION 4: FEATURED SPACES
    // =========================================================

    private VBox renderFeaturedSpacesSection() {
        Label secTitle = heading("Featured Spaces Management", 18);
        Label secSub = muted("Manually assign spaces to be highlighted on user discovery homepage.");

        VBox featuredList = new VBox(10,
                featuredRow("Pixel Photography Studio - Satellite", "Ahmedabad", "★ 4.9", "FEATURED"),
                featuredRow("Aura Yoga Studio - Bodakdev", "Ahmedabad", "★ 4.8", "FEATURED"),
                featuredRow("FitZone Gym Suite - Branch B", "Surat", "★ 4.7", "FEATURED")
        );

        return card(secTitle, secSub, featuredList);
    }

    private HBox featuredRow(String space, String city, String rating, String status) {
        VBox info = new VBox(2, heading(space, 15), muted(city + " · " + rating));
        Button unfeatureBtn = dangerButton("Unmark Featured");
        unfeatureBtn.setOnAction(e -> showNotice("Featured Status Removed", space + " removed from featured spaces list."));

        HBox r = new HBox(info, hgrow(), unfeatureBtn);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(10, 14, 10, 14));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    // =========================================================
    // SECTION 5: PLATFORM POLICIES
    // =========================================================

    private VBox renderPoliciesSection() {
        Label secTitle = heading("Platform Policies & Governance Rules", 18);
        Label secSub = muted("Configure system policy rules, cancellation fee percentages, and safety guidelines.");

        VBox policies = new VBox(12,
                policyRow("Standard Cancellation Refund Policy", "100% refund if cancelled > 24 hrs prior; 50% if < 24 hrs."),
                policyRow("Platform Commission Rate", "15% fixed take-rate on completed gross booking value."),
                policyRow("Review Moderation Protocol", "Automated flagging for profane words; manual admin audit for reported items.")
        );

        return card(secTitle, secSub, policies);
    }

    private HBox policyRow(String title, String desc) {
        VBox info = new VBox(2, heading(title, 15), muted(desc));
        Button editBtn = secondaryButton("Edit Policy");
        editBtn.setOnAction(e -> showNotice("Policy Updated", title + " updated and logged to audit trail."));

        HBox r = new HBox(info, hgrow(), editBtn);
        r.setAlignment(Pos.CENTER_LEFT);
        r.setPadding(new Insets(10, 14, 10, 14));
        r.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:10px;");
        return r;
    }

    private HBox detailRow(String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");
        Label v = new Label(value);
        v.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        HBox r = new HBox(l, hgrow(), v);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Configuration Complete", notice);
    }
}

