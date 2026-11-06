package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class ManagerReviewController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }
    private String managerName() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "Space Manager" : m.getName(); }

    public List<Review> getReviews() {
        return store.getReviewsForScope(assignedSpaceId());
    }

    public boolean replyToReview(String reviewId, String replyText) {
        List<Review> list = store.getReviewsForScope(assignedSpaceId());
        for (Review r : list) {
            if (r.getReviewId().equals(reviewId)) {
                r.setManagerReply(replyText);
                r.setStatus("REPLIED");
                store.addActivityLog(assignedSpaceId(), "Space Manager", managerName(), "REPLY_REVIEW", "Replied to customer review by " + r.getCustomerName());
                return true;
            }
        }
        return false;
    }

    public boolean reportReview(String reviewId, String reason) {
        List<Review> list = store.getReviewsForScope(assignedSpaceId());
        for (Review r : list) {
            if (r.getReviewId().equals(reviewId)) {
                r.setStatus("REPORTED");
                r.setReportedReason(reason);
                store.addActivityLog(assignedSpaceId(), "Space Manager", managerName(), "REPORT_REVIEW", "Reported review to SpotFit Admin: " + reason);
                return true;
            }
        }
        return false;
    }
}

