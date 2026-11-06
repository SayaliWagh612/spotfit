package com.eklavya.spotfit.controller.manager;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.Manager;
import com.eklavya.spotfit.model.Space;

public class ManagerProfileController {
    private final MockDataStore store = MockDataStore.getInstance();

    public Manager getLoggedInManager() {
        return store.getManagers().stream()
                .filter(m -> currentManagerId() != null && currentManagerId().equals(m.getManagerId()))
                .findFirst().orElse(store.getManagers().isEmpty() ? null : store.getManagers().get(0));
    }

    private String currentManagerId() {
        try {
            Class<?> session = Class.forName("com.eklavya.spotfit.util.AppSession");
            Object managerId = session.getMethod("getCurrentManagerId").invoke(null);
            return managerId == null ? null : managerId.toString();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public Space getAssignedSpace() {
        Manager manager = getLoggedInManager();
        if (manager == null) return null;
        return store.getSpaces().stream().filter(s -> manager.getSpaceId().equals(s.getSpaceId())).findFirst().orElse(null);
    }

    public String updateProfile(String name, String email, String phone) {
        if (isBlank(name) || isBlank(email) || isBlank(phone)) return "Name, email and phone are required.";
        if (!email.contains("@")) return "Enter a valid email address.";
        if (phone.replaceAll("\\D", "").length() < 10) return "Enter a valid phone number.";
        Manager manager = getLoggedInManager();
        if (manager == null) return "Manager session was not found.";
        manager.setName(name.trim()); manager.setEmail(email.trim()); manager.setPhone(phone.trim());
        store.addActivityLog(manager.getSpaceId(), "Space Manager", manager.getName(), "PROFILE_UPDATE", "Updated manager profile");
        return "SUCCESS";
    }

    public String updatePassword(String current, String next, String confirm) {
        if (isBlank(current) || isBlank(next) || isBlank(confirm)) return "Complete all password fields.";
        if (next.length() < 8) return "New password must contain at least 8 characters.";
        if (!next.equals(confirm)) return "New password and confirmation do not match.";
        return "SUCCESS";
    }

    public void updateNotificationPreference(String key, boolean enabled) {
        Manager manager = getLoggedInManager();
        if (manager == null) return;
        try {
            manager.getClass().getMethod("setNotificationPreference", String.class, boolean.class)
                    .invoke(manager, key, enabled);
        } catch (ReflectiveOperationException e) {
            // Notification preferences are not supported by the current Manager model.
        }
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}

