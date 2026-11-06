package com.eklavya.spotfit.navigation;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AppNavigator {

    private static Stage primaryStage;

    private AppNavigator() {
    }

    public static void initialize(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException(
                    "Primary stage cannot be null."
            );
        }

        if (primaryStage != null && primaryStage != stage) {
            throw new IllegalStateException(
                    "AppNavigator has already been initialized with another stage."
            );
        }

        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        ensureInitialized();
        return primaryStage;
    }

    public static void setScene(Scene scene) {
        ensureInitialized();

        if (scene == null) {
            throw new IllegalArgumentException(
                    "Scene cannot be null."
            );
        }

        primaryStage.setScene(scene);

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }

    public static void setTitle(String title) {
        ensureInitialized();
        primaryStage.setTitle(title);
    }

    public static void setMaximized(boolean maximized) {
        ensureInitialized();
        primaryStage.setMaximized(maximized);
    }

    public static void show() {
        ensureInitialized();

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }

    private static void ensureInitialized() {
        if (primaryStage == null) {
            throw new IllegalStateException(
                    "AppNavigator has not been initialized."
            );
        }
    }
}
