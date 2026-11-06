package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;
import java.util.stream.Collectors;

public class OwnerDashboardController {

    private final MockDataStore store = MockDataStore.getInstance();

    public Owner getOwner() {
        return store.getCurrentOwner();
    }

    public String getSelectedSpaceScopeId() {
        return store.getSelectedSpaceId();
    }

    public void setSelectedSpaceScopeId(String spaceId) {
        store.setSelectedSpaceId(spaceId);
    }

    public List<Space> getOwnedSpaces() {
        return store.getSpacesForOwner();
    }

    public double getTotalRevenue() {
        String scope = store.getSelectedSpaceId();
        return store.getBookingsForScope(scope).stream()
                .filter(b -> "SUCCESSFUL".equals(b.getPaymentStatus()))
                .mapToDouble(Booking::getAmount)
                .sum();
    }

    public int getTotalBookingsCount() {
        String scope = store.getSelectedSpaceId();
        return store.getBookingsForScope(scope).size();
    }

    public int getActiveSpacesCount() {
        return (int) store.getSpacesForOwner().stream()
                .filter(s -> "ACTIVE".equals(s.getStatus()))
                .count();
    }

    public List<Booking> getRecentBookings() {
        String scope = store.getSelectedSpaceId();
        List<Booking> list = store.getBookingsForScope(scope);
        return list.stream().limit(5).collect(Collectors.toList());
    }

    public Manager getActiveManagerForSelectedSpace() {
        Space selected = store.getSelectedSpace();
        if (selected == null) return null;
        return store.getManagerForSpace(selected.getSpaceId());
    }
}

