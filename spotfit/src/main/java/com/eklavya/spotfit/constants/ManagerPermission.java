package com.eklavya.spotfit.constants;

import java.util.List;

/**
 * Controlled Manager permission string constants for SpotFit.
 */
public final class ManagerPermission {

    public static final String VIEW_ASSIGNED_SPACES = "VIEW_ASSIGNED_SPACES";
    public static final String EDIT_SPACE_OPERATIONS = "EDIT_SPACE_OPERATIONS";
    public static final String MANAGE_RESOURCES = "MANAGE_RESOURCES";
    public static final String MANAGE_AVAILABILITY = "MANAGE_AVAILABILITY";
    public static final String MANAGE_SEATS = "MANAGE_SEATS";
    public static final String MANAGE_SESSIONS = "MANAGE_SESSIONS";
    public static final String MANAGE_SPACE_STATUS = "MANAGE_SPACE_STATUS";
    public static final String VIEW_BOOKINGS = "VIEW_BOOKINGS";
    public static final String MANAGE_BOOKINGS = "MANAGE_BOOKINGS";
    public static final String CHECK_IN_OUT = "CHECK_IN_OUT";
    public static final String HANDLE_CUSTOMERS = "HANDLE_CUSTOMERS";
    public static final String HANDLE_COMPLAINTS = "HANDLE_COMPLAINTS";

    private ManagerPermission() {
    }

    /**
     * Returns sensible default permissions assigned to a newly invited Manager.
     */
    public static List<String> getDefaultPermissions() {
        return List.of(
                VIEW_ASSIGNED_SPACES,
                EDIT_SPACE_OPERATIONS,
                MANAGE_RESOURCES,
                MANAGE_AVAILABILITY,
                MANAGE_SEATS,
                MANAGE_SESSIONS,
                MANAGE_SPACE_STATUS,
                VIEW_BOOKINGS,
                MANAGE_BOOKINGS,
                CHECK_IN_OUT,
                HANDLE_CUSTOMERS,
                HANDLE_COMPLAINTS
        );
    }

    public static boolean isValid(String permission) {
        if (permission == null) {
            return false;
        }
        return VIEW_ASSIGNED_SPACES.equals(permission)
                || EDIT_SPACE_OPERATIONS.equals(permission)
                || MANAGE_RESOURCES.equals(permission)
                || MANAGE_AVAILABILITY.equals(permission)
                || MANAGE_SEATS.equals(permission)
                || MANAGE_SESSIONS.equals(permission)
                || MANAGE_SPACE_STATUS.equals(permission)
                || VIEW_BOOKINGS.equals(permission)
                || MANAGE_BOOKINGS.equals(permission)
                || CHECK_IN_OUT.equals(permission)
                || HANDLE_CUSTOMERS.equals(permission)
                || HANDLE_COMPLAINTS.equals(permission);
    }

    public static String getDisplayLabel(String permission) {
        if (permission == null) return "";
        switch (permission) {
            case VIEW_ASSIGNED_SPACES: return "View Assigned Spaces";
            case EDIT_SPACE_OPERATIONS: return "Edit Space Operations";
            case MANAGE_RESOURCES: return "Manage Resources";
            case MANAGE_AVAILABILITY: return "Manage Availability";
            case MANAGE_SEATS: return "Manage Seats";
            case MANAGE_SESSIONS: return "Manage Sessions";
            case MANAGE_SPACE_STATUS: return "Manage Space Status";
            case VIEW_BOOKINGS: return "View Bookings";
            case MANAGE_BOOKINGS: return "Manage Bookings";
            case CHECK_IN_OUT: return "Check In / Out";
            case HANDLE_CUSTOMERS: return "Handle Customers";
            case HANDLE_COMPLAINTS: return "Handle Complaints";
            default: return permission;
        }
    }
}
