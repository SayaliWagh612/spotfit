package com.eklavya.spotfit.session;

import com.eklavya.spotfit.constants.UserRole;

/**
 * Manages the current in-memory authenticated session for SpotFit.
 */
public final class AppSession {

    private static String userId;
    private static String email;
    private static String name;
    private static String role;
    private static String idToken;
    private static String refreshToken;
    private static long tokenExpiresAt;
    private static boolean authenticated;

    private static final long TOKEN_REFRESH_BUFFER_MILLIS = 5 * 60 * 1000L;

    private AppSession() {
    }

    public static void startSession(
            String newUserId,
            String newEmail,
            String newName,
            String newRole,
            String newIdToken,
            String newRefreshToken,
            long expiresInSeconds) {

        if (newUserId == null || newUserId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        if (!UserRole.isValid(newRole)) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }

        userId = newUserId.trim();
        email = newEmail.trim();
        name = (newName != null && !newName.isBlank()) ? newName.trim() : email.split("@")[0];
        role = newRole;
        idToken = newIdToken;
        refreshToken = newRefreshToken;

        if (expiresInSeconds > 0) {
            tokenExpiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000L);
        } else {
            tokenExpiresAt = 0L;
        }

        authenticated = true;
    }

    public static void updateTokens(String newIdToken, String newRefreshToken, long expiresInSeconds) {
        if (!authenticated) {
            throw new IllegalStateException("Cannot update tokens: No active session.");
        }
        if (newIdToken == null || newIdToken.isBlank()) {
            throw new IllegalArgumentException("New ID token cannot be null or blank.");
        }

        idToken = newIdToken;
        if (newRefreshToken != null && !newRefreshToken.isBlank()) {
            refreshToken = newRefreshToken;
        }

        if (expiresInSeconds > 0) {
            tokenExpiresAt = System.currentTimeMillis() + (expiresInSeconds * 1000L);
        } else {
            tokenExpiresAt = 0L;
        }
    }

    public static void updateProfile(String updatedName, String updatedEmail) {
        if (updatedName != null && !updatedName.isBlank()) {
            name = updatedName.trim();
        }
        if (updatedEmail != null && !updatedEmail.isBlank()) {
            email = updatedEmail.trim();
        }
    }

    public static void clearSession() {
        userId = null;
        email = null;
        name = null;
        role = null;
        idToken = null;
        refreshToken = null;
        tokenExpiresAt = 0L;
        authenticated = false;
    }

    public static boolean isAuthenticated() {
        return authenticated;
    }

    public static boolean hasIdToken() {
        return idToken != null && !idToken.isBlank();
    }

    public static boolean isTokenExpired() {
        if (tokenExpiresAt <= 0) {
            return true;
        }
        return System.currentTimeMillis() >= tokenExpiresAt;
    }

    public static boolean shouldRefreshToken() {
        if (!authenticated) {
            return false;
        }
        if (!hasIdToken() || tokenExpiresAt <= 0) {
            return true;
        }
        return (tokenExpiresAt - System.currentTimeMillis()) <= TOKEN_REFRESH_BUFFER_MILLIS;
    }

    public static boolean isUser() {
        return UserRole.USER.equals(role);
    }

    public static boolean isOwner() {
        return UserRole.OWNER.equals(role);
    }

    public static boolean isManager() {
        return UserRole.MANAGER.equals(role);
    }

    public static boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }

    public static String getUserId() {
        return userId;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getRole() {
        return role;
    }

    public static String getIdToken() {
        return idToken;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static long getTokenExpiresAt() {
        return tokenExpiresAt;
    }
}
