package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.model.Offering;
import com.eklavya.spotfit.model.Space;

/**
 * Router managing view transitions for Owner Offering flows.
 */
public class OfferingFlowRouter {

    public static void goToCreateOffering(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("Add Offering — " + space.getSpaceName(), new CreateOfferingPage(space).getContent());
        } else {
            SpaceFlowRouter.goToSpaceList();
        }
    }

    public static void goToEditOffering(Space space, Offering offering) {
        if (space != null && offering != null) {
            OwnerShell.showCustomPage("Edit Offering — " + offering.getOfferingName(), new EditOfferingPage(space, offering).getContent());
        } else {
            SpaceFlowRouter.goToSpaceDetails(space);
        }
    }

    public static void goToSpaceDetails(Space space) {
        SpaceFlowRouter.goToSpaceDetails(space);
    }
}
