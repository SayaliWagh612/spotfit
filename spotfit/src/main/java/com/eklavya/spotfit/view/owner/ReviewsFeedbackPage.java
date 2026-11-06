package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.controller.owner.OwnerReviewController;
import com.eklavya.spotfit.model.Review;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class ReviewsFeedbackPage {
    private final OwnerReviewController controller = new OwnerReviewController();

    public VBox getContent(BorderPane rightPane) {
        String scope = MockDataStore.getInstance().getSelectedSpaceId();
        List<Review> reviews = controller.getReviews(scope);
        double rating = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        long replied = reviews.stream().filter(r -> r.getOwnerReply() != null || r.getManagerReply() != null).count();

        VBox note = new VBox(OwnerUi.body("Reply as the business or report a review to Admin. Reviews cannot be hidden or deleted here.", 13, OwnerUi.TEXT_MUTED));

        HBox stats = new HBox(16,
                OwnerUi.statCard("Average Rating", String.format("%.1f ★", rating), null),
                OwnerUi.statCard("Reviews", String.valueOf(reviews.size()), "Current scope"),
                OwnerUi.statCard("Responses", String.valueOf(replied), null),
                OwnerUi.statCard("Moderation", "Admin", "Owner may reply/report"));
        VBox list = new VBox(14);
        for (Review review : reviews) list.getChildren().add(card(review));
        if (reviews.isEmpty()) list.getChildren().add(OwnerUi.body("No reviews for this space scope.", 13, OwnerUi.TEXT_MUTED));
        VBox content = new VBox(20, note, stats, list); content.setPadding(new Insets(0,0,0,0));
        return content;
    }
    private VBox card(Review r) {
        VBox box = OwnerUi.card();
        HBox top = new HBox(OwnerUi.heading(r.getCustomerName(), 15), spacer(), OwnerUi.body("★".repeat(r.getRating()), 14, OwnerUi.ACCENT), OwnerUi.badge(r.getStatus())); top.setAlignment(Pos.CENTER_LEFT);
        TextField reply = OwnerUi.textField("Write an official business reply...");
        Button send = OwnerUi.primaryButton("Reply"); Button report = OwnerUi.secondaryButton("Report to Admin");
        send.setOnAction(e -> { if (!reply.getText().isBlank()) { controller.replyToReview(r.getReviewId(), reply.getText()); reply.setText("Reply saved"); reply.setDisable(true); } });
        report.setOnAction(e -> controller.reportReview(r.getReviewId(), "Reported by Owner for Admin review"));
        box.getChildren().addAll(top, OwnerUi.body(r.getComment(), 13, OwnerUi.TEXT_DARK));
        if (r.getManagerReply() != null) box.getChildren().add(OwnerUi.body("Manager reply: " + r.getManagerReply(), 12, OwnerUi.TEXT_MUTED));
        if (r.getOwnerReply() != null) box.getChildren().add(OwnerUi.body("Business reply: " + r.getOwnerReply(), 12, OwnerUi.SUCCESS));
        box.getChildren().addAll(reply, new HBox(10, send, report)); return box;
    }
    private Region spacer() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }
}

