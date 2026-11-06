package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboardController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }

    public Manager getActiveManager() {
        return new ManagerProfileController().getLoggedInManager();
    }

    public Space getAssignedSpace() {
        return store.getSpaces().stream().filter(s -> s.getSpaceId().equals(assignedSpaceId())).findFirst().orElse(null);
    }

    public List<Booking> getTodaysBookings() {
        return store.getBookingsForScope(assignedSpaceId()).stream()
                .filter(b -> "Today".equalsIgnoreCase(b.getBookingDate()))
                .collect(Collectors.toList());
    }

    public List<Booking> getActiveSessions() {
        return store.getBookingsForScope(assignedSpaceId()).stream()
                .filter(b -> "CHECKED_IN".equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Resource> getResources() {
        return store.getResourcesForSpace(assignedSpaceId());
    }

    public List<ActivityLog> getRecentActivity() {
        return store.getActivityLogsForScope(assignedSpaceId());
    }
}

