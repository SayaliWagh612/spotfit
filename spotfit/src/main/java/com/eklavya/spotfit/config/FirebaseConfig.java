package com.eklavya.spotfit.config;

import com.eklavya.spotfit.model.OperationResult;

/**
 * Provides REST API configuration and URLs for Firebase services in SpotFit.
 */
public final class FirebaseConfig {

    private static final String AUTH_BASE_URL = "https://identitytoolkit.googleapis.com/v1";
    private static final String SECURE_TOKEN_BASE_URL = "https://securetoken.googleapis.com/v1";
    private static final String FIRESTORE_BASE_URL = "https://firestore.googleapis.com/v1";

    private FirebaseConfig() {
    }

    public static String getApiKey() {
        return AppConfig.getFirebaseApiKey();
    }

    public static String getProjectId() {
        return AppConfig.getFirebaseProjectId();
    }

    public static String getAuthBaseUrl() {
        return AUTH_BASE_URL;
    }

    public static String getSecureTokenBaseUrl() {
        return SECURE_TOKEN_BASE_URL;
    }

    public static String getFirestoreBaseUrl() {
        return FIRESTORE_BASE_URL;
    }

    public static String getFirestoreDocumentsUrl() {
        String projectId = getProjectId();
        if (projectId == null || projectId.isBlank()) {
            return null;
        }
        return FIRESTORE_BASE_URL + "/projects/" + projectId + "/databases/(default)/documents";
    }

    public static boolean isConfigured() {
        return AppConfig.isFirebaseConfigured();
    }

    public static OperationResult<Void> validateConfiguration() {
        if (!AppConfig.hasFirebaseApiKey()) {
            return OperationResult.failure("FIREBASE_API_KEY_MISSING", "Firebase Web API key is not configured.");
        }
        if (!AppConfig.hasFirebaseProjectId()) {
            return OperationResult.failure("FIREBASE_PROJECT_ID_MISSING", "Firebase project ID is not configured.");
        }
        return OperationResult.success(null, "Firebase configuration is available.");
    }
}
