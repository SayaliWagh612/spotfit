package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class ManagerAvailabilityController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }

    public List<Resource> getResources() {
        return store.getResourcesForSpace(assignedSpaceId());
    }

    public boolean updateResourceStatus(String resourceId, String newStatus) {
        Resource r = store.getResourcesForSpace(assignedSpaceId()).stream()
                .filter(res -> res.getResourceId().equals(resourceId))
                .findFirst()
                .orElse(null);

        if (r != null) {
            r.setStatus(newStatus);
            Manager manager = new ManagerProfileController().getLoggedInManager();
            store.addActivityLog(assignedSpaceId(), "Space Manager", manager == null ? "Space Manager" : manager.getName(), "RESOURCE_STATUS_CHANGE", "Updated " + r.getName() + " status to " + newStatus);
            return true;
        }
        return false;
    }
}

