package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class OwnerRevenueController {

    private final MockDataStore store = MockDataStore.getInstance();

    public List<Payment> getPayments(String spaceIdScope) {
        return store.getPaymentsForScope(spaceIdScope);
    }

    public double getTotalEarnings(String spaceIdScope) {
        return store.getPaymentsForScope(spaceIdScope).stream()
                .filter(p -> "SUCCESSFUL".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double getPendingPayout(String spaceIdScope) {
        return getTotalEarnings(spaceIdScope) * 0.85; // 85% payout after platform fee
    }
}

