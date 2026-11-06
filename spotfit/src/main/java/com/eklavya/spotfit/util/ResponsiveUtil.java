package com.eklavya.spotfit.util;

import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class ResponsiveUtil {

    /** Returns the usable screen size (minus taskbars etc.) of the primary monitor. */
    public static Rectangle2D screenBounds() {
        return Screen.getPrimary().getVisualBounds();
    }

    /** Binds a region's preferred width to a percentage of the scene width, clamped between min and max. */
    public static void bindWidthPercent(Region region, Scene scene, double percent, double min, double max) {
        region.prefWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> clamp(scene.getWidth() * percent, min, max),
                        scene.widthProperty()
                )
        );
    }

    /** Binds a Text/Label-like wrapping width to a percentage of the scene width, clamped between min and max. */
    public static javafx.beans.binding.DoubleBinding widthPercentBinding(Scene scene, double percent, double min, double max) {
        return Bindings.createDoubleBinding(
                () -> clamp(scene.getWidth() * percent, min, max),
                scene.widthProperty()
        );
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
