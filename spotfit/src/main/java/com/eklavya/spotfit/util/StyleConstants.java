package com.eklavya.spotfit.util;

public class StyleConstants {

    // backgrounds
    public static final String BG_PAGE = "#FBEDE0";
    public static final String BG_PANEL_GRADIENT_TOP = "#FCE9D8";
    public static final String CARD_WHITE = "#FFFFFF";
    public static final String INPUT_BG = "#FCEEE2";
    public static final String PILL_BG = "#F5E2D0";

    // text
    public static final String TEXT_DARK = "#3B2417";
    public static final String TEXT_MUTED = "#8A7565";
    public static final String TEXT_PLACEHOLDER = "#B3A093";

    // brand
    public static final String PRIMARY = "#A8571F";
    public static final String PRIMARY_DARK = "#8A4718";
    public static final String PRIMARY_LIGHT = "#C97C3D";

    // borders
    public static final String BORDER = "#EDD9C3";
    public static final String BORDER_STRONG = "#D8B99A";

    public static final String FONT = "'Segoe UI'";

    public static String heading(int size) {
        return "-fx-font-family: " + FONT + "; -fx-font-weight: 800; -fx-font-size: " + size + "px; -fx-text-fill: " + TEXT_DARK + ";";
    }

    public static String body(int size) {
        return "-fx-font-family: " + FONT + "; -fx-font-size: " + size + "px; -fx-text-fill: " + TEXT_MUTED + ";";
    }
}