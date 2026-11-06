package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.model.Business;

/**
 * Router managing view transitions for the Owner Business module.
 */
public class BusinessFlowRouter {

    public static void goToBusinessList() {
        PageRegistry.go("businesses");
    }

    public static void goToCreateBusiness() {
        OwnerShell.showCustomPage("businesses", "Create New Business", new CreateBusinessPage().getContent());
    }

    public static void goToBusinessDetails(Business business) {
        if (business != null) {
            OwnerShell.showCustomPage("businesses", "Business Details", new BusinessDetailsPage(business).getContent());
        } else {
            goToBusinessList();
        }
    }

    public static void goToEditBusiness(Business business) {
        if (business != null) {
            OwnerShell.showCustomPage("businesses", "Edit Business Profile", new EditBusinessPage(business).getContent());
        } else {
            goToBusinessList();
        }
    }

    public static void goToOwnerDashboard() {
        OwnerFlowRouter.goToDashboard();
    }
}
