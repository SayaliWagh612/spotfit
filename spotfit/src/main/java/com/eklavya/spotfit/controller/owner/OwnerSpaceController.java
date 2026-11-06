package com.eklavya.spotfit.controller.owner;

import com.eklavya.spotfit.controller.admin.MockDataStore;
import com.eklavya.spotfit.model.*;
import java.util.List;

public class OwnerSpaceController {

    private final MockDataStore store = MockDataStore.getInstance();

    // Wizard Draft State
    private String draftCategory;
    private String draftActivityGroup;
    private String draftSpaceType;
    private String draftName;
    private String draftDescription;
    private String draftAddress;
    private String draftCity;
    private int draftCapacity = 20;
    private String draftEmail;
    private String draftPhone;
    private String draftOperatingDays = "Mon - Sat";
    private String draftOpeningTime = "08:00 AM";
    private String draftClosingTime = "09:00 PM";
    private String draftRules = "Standard rules apply.";
    private String draftCancellationPolicy = "Standard 24h cancellation.";
    private List<String> draftAmenities;

    public List<String> getCategories() {
        return List.of("Creativity", "Sports", "Wellness", "Study & Workspace");
    }

    public List<ActivityType> getActivityGroupsForCategory(String category) {
        return store.getActivityTypesForCategory(category);
    }

    public List<SpaceType> getSpaceTypesForActivityGroup(String activityTypeId) {
        return store.getSpaceTypesForActivityType(activityTypeId);
    }

    // Step 1 - Step 3
    public void setClassification(String category, String activityGroup, String spaceType) {
        this.draftCategory = category;
        this.draftActivityGroup = activityGroup;
        this.draftSpaceType = spaceType;
    }

    // Step 4
    public void setSpaceDetails(String name, String description, String address, String city, int capacity,
                                String email, String phone, String days, String openTime, String closeTime,
                                String rules, String cancellationPolicy, List<String> amenities) {
        this.draftName = name;
        this.draftDescription = description;
        this.draftAddress = address;
        this.draftCity = city;
        this.draftCapacity = capacity;
        this.draftEmail = email;
        this.draftPhone = phone;
        this.draftOperatingDays = days;
        this.draftOpeningTime = openTime;
        this.draftClosingTime = closeTime;
        this.draftRules = rules;
        this.draftCancellationPolicy = cancellationPolicy;
        this.draftAmenities = amenities;
    }

    // Step 7: Submission
    public Space submitNewSpace(List<Offering> offerings, List<Resource> resources) {
        String newSpaceId = "space-" + (store.getSpaces().size() + 1);

        Space space = new Space(
                newSpaceId, store.getCurrentOwner().getOwnerId(),
                draftName != null && !draftName.isBlank() ? draftName : "New SpotFit Studio",
                draftCategory != null ? draftCategory : "Creativity",
                draftActivityGroup != null ? draftActivityGroup : "Content Creation",
                draftSpaceType != null ? draftSpaceType : "Creator Studio",
                draftDescription != null ? draftDescription : "Newly registered space",
                draftAddress != null ? draftAddress : "City Center",
                draftCity != null ? draftCity : "Ahmedabad",
                draftCapacity, draftEmail, draftPhone,
                draftOperatingDays, draftOpeningTime, draftClosingTime,
                draftRules, draftCancellationPolicy, draftAmenities,
                "PENDING_APPROVAL", null, 0.0, 0, 0.0
        );

        store.addSpace(space);

        if (offerings != null) {
            for (Offering off : offerings) {
                off.setSpaceId(newSpaceId);
                store.addOffering(off);
            }
        }

        if (resources != null) {
            for (Resource res : resources) {
                res.setSpaceId(newSpaceId);
                store.addResource(res);
            }
        }

        store.addActivityLog(newSpaceId, "Business Owner", store.getCurrentOwner().getName(), "CREATE_SPACE", "Submitted new space for approval: " + space.getName());
        return space;
    }

    public List<Space> getAllSpaces() {
        return store.getSpaces();
    }

    public Space getSpaceById(String spaceId) {
        return store.getSpaces().stream().filter(s -> s.getSpaceId().equals(spaceId)).findFirst().orElse(null);
    }
}

