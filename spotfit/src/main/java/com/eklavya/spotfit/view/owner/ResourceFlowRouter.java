package com.eklavya.spotfit.view.owner;

import com.eklavya.spotfit.model.Resource;
import com.eklavya.spotfit.model.Space;

/**
 * Router managing view transitions for Owner Resource flows.
 */
public class ResourceFlowRouter {

    public static void goToCreateResource(Space space) {
        if (space != null) {
            OwnerShell.showCustomPage("myspaces", "Add Resource — " + space.getSpaceName(), new CreateResourcePage(space).getContent());
        } else {
            SpaceFlowRouter.goToSpaceList();
        }
    }

    public static void goToEditResource(Space space, Resource resource) {
        if (space != null && resource != null) {
            OwnerShell.showCustomPage("myspaces", "Edit Resource — " + resource.getResourceName(), new EditResourcePage(space, resource).getContent());
        } else {
            SpaceFlowRouter.goToSpaceDetails(space);
        }
    }

    public static void goToManageAvailability(Space space, Resource resource) {
        if (space != null && resource != null) {
            OwnerShell.showCustomPage("myspaces", "Manage Availability — " + resource.getResourceName(), new ManageAvailabilityPage(space, resource).getContent());
        } else {
            SpaceFlowRouter.goToSpaceDetails(space);
        }
    }

    public static void goToSpaceDetails(Space space) {
        SpaceFlowRouter.goToSpaceDetails(space);
    }
}
