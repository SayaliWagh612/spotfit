package com.eklavya.spotfit.constants;

/**
 * Controlled application role constants for SpotFit.
 */
public final class UserRole {

    public static final String USER = "USER";
    public static final String OWNER = "OWNER";
    public static final String MANAGER = "MANAGER";
    public static final String ADMIN = "ADMIN";

    private UserRole() {
    }

    public static boolean isValid(String role) {
        if (role == null) {
            return false;
        }

        return USER.equals(role)
                || OWNER.equals(role)
                || MANAGER.equals(role)
                || ADMIN.equals(role);
    }

    public static boolean isBusinessRole(String role) {
        if (role == null) {
            return false;
        }

        return OWNER.equals(role) || MANAGER.equals(role);
    }
}
