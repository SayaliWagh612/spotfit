package com.eklavya.spotfit;

import com.eklavya.spotfit.navigation.AppNavigator;
import com.eklavya.spotfit.view.common.WelcomePage;
import com.eklavya.spotfit.view.spaceManager.SplashScreen;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Start splash screen
        SplashScreen splashScreen =
                new SplashScreen();

        splashScreen.start(primaryStage);
    }

    /**
     * Opens Welcome Page after splash screen.
     */
    public static void showWelcomePage(Stage primaryStage) {

        try {

            System.out.println(
                    "Opening SpotFit Welcome Page..."
            );

            // =====================================================
            // INITIALIZE NAVIGATOR
            // =====================================================

            AppNavigator.initialize(primaryStage);

            // =====================================================
            // CREATE WELCOME PAGE
            // =====================================================

            WelcomePage welcomePage =
                    new WelcomePage();

            Scene welcomeScene =
                    welcomePage.getScene();

            // =====================================================
            // SET TITLE
            // =====================================================

            AppNavigator.setTitle("SpotFit");

            primaryStage.setTitle("SpotFit");

            // =====================================================
            // SET SCENE
            // =====================================================

            AppNavigator.setScene(welcomeScene);

            // =====================================================
            // KEEP MAXIMIZED
            // =====================================================

            AppNavigator.setMaximized(true);

            primaryStage.setMaximized(true);

            primaryStage.show();

            // =====================================================
            // SMOOTH WELCOME PAGE FADE-IN
            // =====================================================

            welcomeScene.getRoot().setOpacity(0);

            FadeTransition fadeIn =
                    new FadeTransition(
                            Duration.millis(700),
                            welcomeScene.getRoot()
                    );

            fadeIn.setFromValue(0.0);

            fadeIn.setToValue(1.0);

            fadeIn.play();

            System.out.println(
                    "Welcome Page opened successfully."
            );

        } catch (Exception e) {

            System.err.println(
                    "Error opening Welcome Page:"
            );

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}