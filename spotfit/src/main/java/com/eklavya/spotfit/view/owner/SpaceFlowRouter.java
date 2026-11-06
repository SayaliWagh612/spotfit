package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.model.Business;
import com.eklavya.spotfit.model.Space;

/**
 * Router managing view transitions for the Owner Space module using AppNavigator and OwnerShell.
 */
public class SpaceFlowRouter {

    public static void goToSpaceList() {
        OwnerShell.show("myspaces");
    }

    public static void goToCreateSpace() {
        OwnerShell.showCustomPage("myspaces", "List New Space", new ListNewSpaceWizardPage().getContent());
    }

    public static void goToCreateSpace(String businessId) {
        OwnerShell.showCustomPage("myspaces", "List New Space", new ListNewSpaceWizardPage(businessId).getContent());
    }

    public static void goToSpaceDetails(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Space Details", new SpaceDetailsPage(space).getContent());
        } else {
            goToSpaceList();
        }
    }

    public static void goToEditSpace(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Edit Space", new EditSpacePage(space).getContent());
        } else {
            goToSpaceList();
        }
    }

    public static void goToBusinessDetails(Business business) {
        BusinessFlowRouter.goToBusinessDetails(business);
    }

    public static void goToOwnerDashboard() {
        OwnerFlowRouter.goToDashboard();
    }

    public static void goToManageAvailability(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Manage Availability", new ManageAvailabilityPage(space).getContent());
        } else {
            goToSpaceList();
        }
    }

    public static void goToManageSeats(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Manage Seats", new ManageSeatsPage(space).getContent());
        } else {
            goToSpaceList();
        }
    }

    public static void goToManageWorkshopSessions(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Manage Workshop Sessions", new ManageWorkshopSessionsPage(space).getContent());
        } else {
            goToSpaceList();
        }
    }
}
