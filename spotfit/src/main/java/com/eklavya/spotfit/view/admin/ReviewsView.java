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

public class ReviewsView {

    public VBox getContent() {
        Label title = heading("Review Moderation", 24);
        Label subtitle = muted("Govern user reviews across all spaces. Actions: Keep, Hide, Remove, Restore. Hidden & removed reviews are preserved in audit trail.");

        TextField search = searchField("🔍 Search by reviewer, space, review text, or ID...");
        search.setPrefWidth(380);

        HBox filtersRow = new HBox(12,
                search,
                hgrow(),
                outlineChip("All Reviews (1,240)"),
                outlineChip("Reported (6)"),
                outlineChip("Flagged (4)"),
                outlineChip("Hidden (12)"),
                outlineChip("Removed (8)")
        );

        VBox reviewList = new VBox(12,
                createReviewRow("REV-401", "Aarav Mehta", "★ 5.0", "Pixel Photography Studio", "Exceptional space! Highly professional equipment and seamless check-in.", "PUBLISHED", GREEN_BG, GREEN_TEXT),
                createReviewRow("REV-402", "Siddharth Rao", "★ 1.0", "FitZone Gym Branch B", "Extremely unsafe equipment and rude staff behavior! Fraudulent charges.", "REPORTED", RED_BG, RED_TEXT),
                createReviewRow("REV-403", "Karan Verma", "★ 1.0", "Aura Yoga Studio", "Contains inappropriate language and spam marketing text.", "FLAGGED", AMBER_BG, AMBER_TEXT),
                createReviewRow("REV-404", "Anonymous User", "★ 2.0", "PowerGym Network", "Hidden by moderation policy due to ongoing dispute investigation.", "HIDDEN", PURPLE_BG, PURPLE_TEXT)
        );

        VBox contentCard = card(filtersRow, reviewList);

        VBox root = new VBox(24, title, subtitle, contentCard);
        root.setPadding(new Insets(32, 40, 40, 40));
        return root;
    }

    private HBox createReviewRow(String id, String reviewer, String rating, String space, String text, String status, String bg, String fg) {
        Label avatarLbl = avatar(initialsOf(reviewer), 42);

        VBox info = new VBox(2,
                new HBox(8, heading(reviewer + " (" + rating + ")", 15), muted("on " + space + " (" + id + ")")),
                muted("\"" + text + "\"")
        );

        Label statusChip = chip(status, bg, fg);

        Button moderateBtn = secondaryButton("Moderate Review");
        moderateBtn.setOnAction(e -> showReviewModerationModal(id, reviewer, rating, space, text, status));

        HBox row = new HBox(16, avatarLbl, info, hgrow(), statusChip, moderateBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px;");
        return row;
    }

    private void showReviewModerationModal(String id, String reviewer, String rating, String space, String text, String currentStatus) {
        Label info = muted("Review ID: " + id + " | Reviewer: " + reviewer + " | Rating: " + rating + " | Space: " + space);

        VBox textBox = new VBox(6,
                heading("Review Content:", 13),
                muted("\"" + text + "\""),
                muted("Policy Note: Hidden or Removed reviews are NEVER deleted permanently. Restoration is logged in audit log.")
        );
        textBox.setPadding(new Insets(12));
        textBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");

        Button btnKeep = primaryButton("✔ Keep (Mark Valid)");
        Button btnHide = secondaryButton("👁 Hide from Public");
        Button btnRemove = dangerButton("🗑 Remove Review");
        Button btnRestore = secondaryButton("↺ Restore Review");

        HBox btns = new HBox(10, btnKeep, btnHide, btnRemove, btnRestore);

        VBox body = new VBox(16, info, textBox, btns);
        body.setPadding(new Insets(20));

        ModalDialog dialog = showModal("Review Moderation - " + id, body);

        btnKeep.setOnAction(e -> { dialog.close(); showNotice("Review Maintained", "Review " + id + " approved and kept active."); });
        btnHide.setOnAction(e -> { dialog.close(); showNotice("Review Hidden", "Review " + id + " hidden from space discovery. Preserved in database."); });
        btnRemove.setOnAction(e -> { dialog.close(); showNotice("Review Removed", "Review " + id + " removed from public view. Recorded in audit trail."); });
        btnRestore.setOnAction(e -> { dialog.close(); showNotice("Review Restored", "Review " + id + " restored to published state and recorded in audit log."); });
    }

    private void showNotice(String title, String msg) {
        VBox notice = new VBox(14,
                heading(title, 18),
                muted(msg),
                primaryButton("Done")
        );
        notice.setPadding(new Insets(20));
        showModal("Review Governance Completed", notice);
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String init = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) init += parts[1].substring(0, 1).toUpperCase();
        return init;
    }
}

