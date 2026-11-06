package com.eklavya.spotfit.controller.admin;

import com.eklavya.spotfit.navigation.AppNavigator;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationController {

    private static NavigationController instance;

    private NavigationController() {}

    public static synchronized NavigationController getInstance() {
        if (instance == null) {
            instance = new NavigationController();
        }
        return instance;
    }

    public Stage getPrimaryStage() {
        return AppNavigator.getPrimaryStage();
    }

    public void navigateTo(Parent root, String title) {
        Scene scene = AppNavigator.getPrimaryStage().getScene();
        if (scene == null) {
            scene = new Scene(root, 1280, 800);
            AppNavigator.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        if (title != null && !title.isBlank()) {
            AppNavigator.setTitle("SpotFit - " + title);
        }
        AppNavigator.show();
    }
}
