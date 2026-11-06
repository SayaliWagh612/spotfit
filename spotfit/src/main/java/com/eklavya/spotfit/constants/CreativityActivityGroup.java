package com.eklavya.spotfit.constants;

/**
 * Controlled activity group constants for Creativity category in SpotFit.
 */
public final class CreativityActivityGroup {

    public static final String ART = "ART";
    public static final String CONTENT_CREATION = "CONTENT_CREATION";
    public static final String PODCASTS = "PODCASTS";
    public static final String MUSIC = "MUSIC";

    private CreativityActivityGroup() {
    }

    public static boolean isValid(String activityGroup) {
        if (activityGroup == null) {
            return false;
        }
        return ART.equalsIgnoreCase(activityGroup)
                || CONTENT_CREATION.equalsIgnoreCase(activityGroup)
                || PODCASTS.equalsIgnoreCase(activityGroup)
                || MUSIC.equalsIgnoreCase(activityGroup);
    }
}
