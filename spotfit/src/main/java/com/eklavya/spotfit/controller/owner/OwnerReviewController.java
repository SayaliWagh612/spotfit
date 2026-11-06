package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class OwnerReviewController {

    private final MockDataStore store = MockDataStore.getInstance();

    public List<Review> getReviews(String spaceIdScope) {
        return store.getReviewsForScope(spaceIdScope);
    }

    public boolean replyToReview(String reviewId, String replyText) {
        List<Review> list = store.getReviewsForScope("ALL");
        for (Review r : list) {
            if (r.getReviewId().equals(reviewId)) {
                r.setOwnerReply(replyText);
                r.setStatus("REPLIED");
                store.addActivityLog(r.getSpaceId(), "Business Owner", store.getCurrentOwner().getName(), "REPLY_REVIEW", "Replied to review by " + r.getCustomerName());
                return true;
            }
        }
        return false;
    }

    public boolean reportReview(String reviewId, String reason) {
        List<Review> list = store.getReviewsForScope("ALL");
        for (Review r : list) {
            if (r.getReviewId().equals(reviewId)) {
                r.setStatus("REPORTED");
                r.setReportedReason(reason);
                store.addActivityLog(r.getSpaceId(), "Business Owner", store.getCurrentOwner().getName(), "REPORT_REVIEW", "Reported review to SpotFit Admin: " + reason);
                return true;
            }
        }
        return false;
    }
}

