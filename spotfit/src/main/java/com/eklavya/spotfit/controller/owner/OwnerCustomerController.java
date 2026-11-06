package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class OwnerCustomerController {

    private final MockDataStore store = MockDataStore.getInstance();

    public List<Customer> getCustomers() {
        return store.getCustomers();
    }

    public List<Booking> getCustomerBookings(String customerId, String spaceIdScope) {
        return store.getBookingsForScope(spaceIdScope).stream()
                .filter(b -> customerId.equals(b.getCustomerId()))
                .collect(Collectors.toList());
    }
}

