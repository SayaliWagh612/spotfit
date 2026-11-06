package com.eklavya.spotfit.constants;

/**
 * Controlled space category constants for SpotFit.
 */
public final class SpaceCategory {

    public static final String CREATIVITY = "CREATIVITY";
    public static final String WELLNESS = "WELLNESS";
    public static final String SPORTS = "SPORTS";
    public static final String STUDY_WORKSPACE = "STUDY_WORKSPACE";

    private SpaceCategory() {
    }

    public static boolean isValid(String category) {
        if (category == null) {
            return false;
        }
        return CREATIVITY.equalsIgnoreCase(category)
                || WELLNESS.equalsIgnoreCase(category)
                || SPORTS.equalsIgnoreCase(category)
                || STUDY_WORKSPACE.equalsIgnoreCase(category);
    }
}
