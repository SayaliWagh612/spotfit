package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class OwnerAnalyticsController {

    private final MockDataStore store = MockDataStore.getInstance();

    public AnalyticsSummary getAnalyticsSummary(String spaceIdScope) {
        List<Booking> bookings = store.getBookingsForScope(spaceIdScope);
        double totalRevenue = bookings.stream()
                .filter(b -> "SUCCESSFUL".equals(b.getPaymentStatus()))
                .mapToDouble(Booking::getAmount)
                .sum();

        int totalBookings = bookings.size();
        double occupancyRate = totalBookings > 0 ? 78.5 : 0.0;
        double customerSatisfaction = 4.8;

        return new AnalyticsSummary(spaceIdScope, totalRevenue, totalBookings, occupancyRate, customerSatisfaction);
    }
}

