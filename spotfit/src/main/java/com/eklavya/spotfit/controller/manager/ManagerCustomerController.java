package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerCustomerController {

    private final MockDataStore store = MockDataStore.getInstance();
    private String assignedSpaceId() { Manager m = new ManagerProfileController().getLoggedInManager(); return m == null ? "space-1" : m.getSpaceId(); }

    public List<Customer> getSpaceCustomers() {
        List<String> spaceCustomerIds = store.getBookingsForScope(assignedSpaceId()).stream()
                .map(Booking::getCustomerId)
                .distinct()
                .collect(Collectors.toList());

        return store.getCustomers().stream()
                .filter(c -> spaceCustomerIds.contains(c.getCustomerId()))
                .collect(Collectors.toList());
    }

    public List<Booking> getCustomerBookingsForSpace(String customerId) {
        return store.getBookingsForScope(assignedSpaceId()).stream()
                .filter(b -> customerId.equals(b.getCustomerId()))
                .collect(Collectors.toList());
    }
}

