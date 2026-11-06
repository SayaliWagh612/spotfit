package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.Owner;

public class OwnerProfileController {
    private final MockDataStore store = MockDataStore.getInstance();

    public Owner getCurrentOwner() {
        return store.getCurrentOwner();
    }

    public String updatePersonalProfile(String name, String email, String phone, String city, String language) {
        if (isBlank(name) || isBlank(email) || isBlank(phone)) return "Name, email and phone are required.";
        if (!email.contains("@") || !email.contains(".")) return "Enter a valid email address.";
        if (phone.replaceAll("\\D", "").length() < 10) return "Enter a valid phone number.";
        Owner owner = getCurrentOwner();
        owner.setName(name.trim()); owner.setEmail(email.trim()); owner.setPhone(phone.trim());
        owner.setCity(city == null ? "" : city.trim());
        owner.setPreferredLanguage(isBlank(language) ? "English" : language.trim());
        store.addActivityLog("ALL", "Business Owner", owner.getName(), "PROFILE_UPDATE", "Updated personal profile");
        return "SUCCESS";
    }

    public String updateBusinessProfile(String name, String email, String phone, String address) {
        if (isBlank(name) || isBlank(email) || isBlank(phone) || isBlank(address)) return "Complete all business fields.";
        if (!email.contains("@")) return "Enter a valid business email.";
        Owner owner = getCurrentOwner();
        owner.setBusinessName(name.trim()); owner.setBusinessEmail(email.trim());
        owner.setBusinessPhone(phone.trim()); owner.setBusinessAddress(address.trim());
        store.addActivityLog("ALL", "Business Owner", owner.getName(), "BUSINESS_PROFILE_UPDATE", "Updated business profile");
        return "SUCCESS";
    }

    public String updateProfileImage(String imagePath) {
        getCurrentOwner().setProfileImagePath(imagePath);
        return "SUCCESS";
    }

    public String updatePassword(String current, String next, String confirm) {
        if (isBlank(current) || isBlank(next) || isBlank(confirm)) return "Complete all password fields.";
        if (next.length() < 8) return "New password must contain at least 8 characters.";
        if (!next.equals(confirm)) return "New password and confirmation do not match.";
        return "SUCCESS";
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}

