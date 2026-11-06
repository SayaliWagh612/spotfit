package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.view.spaceManager.DashboardUi.*;

public class ReviewsView {

    private VBox reviewsContainer;

    public VBox getContent(BorderPane rightPane) {

        Label title = heading("Customer Reviews & Feedback", 30);
        Label subtitle = muted("Monitor, respond to, and moderate customer reviews for Pixel Photography Studio.");
        Label auditNote = chip("ℹ Moderation Notice: Soft-hide only (Records retained for audit trail)", CHIP_BG, PRIMARY);

        HBox header = new HBox(new VBox(4, title, subtitle), hgrow(), auditNote);
        header.setAlignment(Pos.CENTER_LEFT);

        reviewsContainer = new VBox(14,
                reviewCard("Aarav Mehta", "★★★★★", "12 Aug 2026", "Video Podcast Suite", "Fantastic studio equipment and extremely helpful manager! Sound isolation was top notch.", "PUBLISHED", "Thank you Aarav! Glad you had a great recording session."),
                reviewCard("Riya Kapoor", "★★★★☆", "10 Aug 2026", "Portrait Session", "Great lighting setup. Wi-Fi was a bit slow in the corner floor but overall very good experience.", "PUBLISHED", null),
                reviewCard("Dev Patel", "★★★★★", "05 Aug 2026", "Content Shoot", "Super clean space, seamless OTP check-in experience. Will definitely book again.", "PUBLISHED", null),
                reviewCard("Spam User", "★☆☆☆☆", "01 Aug 2026", "Unverified Visit", "Irrelevant commercial promotional spam comment.", "HIDDEN", null)
        );

        VBox content = new VBox(20, header, card(heading("Space Customer Reviews (4.9 Rating Avg)", 18), reviewsContainer));
        content.setPadding(new Insets(32, 40, 40, 40));
        return content;
    }

    private HBox reviewCard(String name, String rating, String date, String offering, String text, String status, String replyText) {

        Label avatarLabel = avatar(initialsOf(name), 44);

        Label rLabel = new Label(rating);
        rLabel.setStyle("-fx-font-size:16px; -fx-text-fill:" + PRIMARY + ";");

        Label stChip = chip(status, status.equals("PUBLISHED") ? GREEN_BG : RED_BG, status.equals("PUBLISHED") ? GREEN_TEXT : RED_TEXT);

        HBox topRow = new HBox(10, heading(name, 16), rLabel, muted("· " + date + " · " + offering), hgrow(), stChip);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label commentLabel = new Label("\"" + text + "\"");
        commentLabel.setWrapText(true);
        commentLabel.setStyle("-fx-font-size:14px; -fx-text-fill:" + TEXT + "; -fx-padding:4 0;");

        VBox replyBox = new VBox(6);
        if (replyText != null) {
            Label replyTitle = new Label("Manager Response (Rahul Sharma):");
            replyTitle.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + ";");
            Label replyContent = new Label(replyText);
            replyContent.setWrapText(true);
            replyContent.setStyle("-fx-font-size:13px; -fx-text-fill:" + SECONDARY_TEXT + ";");

            replyBox.getChildren().addAll(replyTitle, replyContent);
            replyBox.setPadding(new Insets(12));
            replyBox.setStyle("-fx-background-color:" + CHIP_BG + "; -fx-background-radius:10px;");
        }

        Button replyBtn = primaryButton("💬 Reply");
        replyBtn.setOnAction(e -> openReplyModal(name, commentLabel));

        Button hideBtn = secondaryButton(status.equals("PUBLISHED") ? "Hide Review" : "Unhide Review");
        hideBtn.setOnAction(e -> {
            String nStatus = status.equals("PUBLISHED") ? "HIDDEN" : "PUBLISHED";
            AuditLogManager.addLog("Rahul Sharma", "Moderated Review", "Changed review status for " + name + " to " + nStatus, "REVIEWS");
            showModal("Review Moderated", new VBox(16, heading("Review status changed to " + nStatus, 16), muted("Underlying review record is retained in Firestore audit trail.")));
        });

        Button reportBtn = secondaryButton("⚠ Report to SpotFit");
        reportBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Reported Review", "Reported problematic review from " + name, "REVIEWS");
            showModal("Report Submitted", new VBox(16, heading("Report sent to SpotFit Support", 16), muted("Our team will review this feedback for community guidelines violations.")));
        });

        HBox actions = new HBox(10, replyBtn, hideBtn, reportBtn);

        VBox cardContent = new VBox(10, topRow, commentLabel, replyBox, actions);
        if (replyText == null) {
            cardContent.getChildren().remove(replyBox);
        }

        HBox row = new HBox(avatarLabel, cardContent);
        row.setAlignment(Pos.TOP_LEFT);
        row.setSpacing(16);
        row.setPadding(new Insets(16));
        row.setStyle("-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:14px;");
        HBox.setHgrow(cardContent, Priority.ALWAYS);
        return row;
    }

    private void openReplyModal(String name, Label commentLabel) {
        TextArea replyInput = new TextArea();
        replyInput.setPromptText("Type official manager reply...");
        replyInput.setPrefRowCount(3);
        replyInput.setStyle("-fx-background-color:" + SURFACE + "; -fx-font-size:14px; -fx-border-color:" + BORDER + "; -fx-border-radius:12px;");

        Button submitBtn = primaryButton("Post Public Response");
        submitBtn.setOnAction(e -> {
            AuditLogManager.addLog("Rahul Sharma", "Replied to Review", "Posted public reply to " + name, "REVIEWS");
            showModal("Response Posted", new VBox(16, heading("✔ Response Posted", 16), muted("Your reply is now visible on the SpotFit customer app.")));
        });

        VBox body = new VBox(16,
                heading("Respond to " + name + "'s Review", 18),
                commentLabel,
                replyInput,
                submitBtn
        );
        body.setPadding(new Insets(20));
        showModal("Manager Review Response", body);
    }

    private String initialsOf(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
        return initials;
    }
}

