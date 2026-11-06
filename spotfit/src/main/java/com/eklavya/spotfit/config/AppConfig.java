package com.eklavya.spotfit.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads desktop application configuration properties for SpotFit.
 */
public final class AppConfig {

    private static final String SYS_PROP_API_KEY =
            "spotfit.firebase.apiKey";

    private static final String SYS_PROP_PROJECT_ID =
            "spotfit.firebase.projectId";

    private static final String ENV_API_KEY =
            "SPOTFIT_FIREBASE_API_KEY";

    private static final String ENV_PROJECT_ID =
            "SPOTFIT_FIREBASE_PROJECT_ID";

    private static final String FILE_PROP_API_KEY =
            "firebase.api.key";

    private static final String FILE_PROP_PROJECT_ID =
            "firebase.project.id";

    private static final String SYS_PROP_PAYMENT_MODE =
            "spotfit.payment.mode";

    private static final String ENV_PAYMENT_MODE =
            "SPOTFIT_PAYMENT_MODE";

    private static final String FILE_PROP_PAYMENT_MODE =
            "payment.mode";

    private static final Properties LOCAL_PROPERTIES =
            loadLocalProperties();

    private AppConfig() {
    }

    private static Properties loadLocalProperties() {

        Properties props = new Properties();

        File file = new File("firebase.properties");

        if (file.exists() && file.isFile()) {

            try (InputStream input =
                         new FileInputStream(file)) {

                props.load(input);

            } catch (Exception e) {

                System.out.println(
                        "Could not load firebase.properties: "
                                + e.getMessage()
                );
            }
        }

        return props;
    }

    private static String resolveValue(
            String sysProp,
            String envVar,
            String fileKey) {

        // 1. System property
        String sysVal = System.getProperty(sysProp);

        if (sysVal != null && !sysVal.trim().isEmpty()) {
            return sysVal.trim();
        }

        // 2. Environment variable
        String envVal = System.getenv(envVar);

        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }

        // 3. firebase.properties
        String fileVal =
                LOCAL_PROPERTIES.getProperty(fileKey);

        if (fileVal != null && !fileVal.trim().isEmpty()) {
            return fileVal.trim();
        }

        return null;
    }

    public static String getFirebaseApiKey() {

        return resolveValue(
                SYS_PROP_API_KEY,
                ENV_API_KEY,
                FILE_PROP_API_KEY
        );
    }

    public static String getFirebaseProjectId() {

        return resolveValue(
                SYS_PROP_PROJECT_ID,
                ENV_PROJECT_ID,
                FILE_PROP_PROJECT_ID
        );
    }

    public static boolean hasFirebaseApiKey() {

        return getFirebaseApiKey() != null;
    }

    public static boolean hasFirebaseProjectId() {

        return getFirebaseProjectId() != null;
    }

    public static boolean isFirebaseConfigured() {

        return hasFirebaseApiKey()
                && hasFirebaseProjectId();
    }

    public static String getPaymentMode() {

        String mode = resolveValue(
                SYS_PROP_PAYMENT_MODE,
                ENV_PAYMENT_MODE,
                FILE_PROP_PAYMENT_MODE
        );

        if (mode == null || mode.trim().isEmpty()) {
            return "DEMO";
        }

        return mode.toUpperCase();
    }
}