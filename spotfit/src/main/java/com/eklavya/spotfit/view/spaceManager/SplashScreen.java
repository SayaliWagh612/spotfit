package com.eklavya.spotfit.view.spaceManager;

import com.eklavya.spotfit.Main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class SplashScreen extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {

        System.out.println("Starting SpotFit Splash Screen...");

        // =========================================================
        // LOAD SPLASH VIDEO
        // =========================================================

        URL videoURL = getClass()
                .getResource("/video/SplashScreen.mp4");

        if (videoURL == null) {

            System.err.println("======================================");
            System.err.println("SPLASH VIDEO NOT FOUND!");
            System.err.println("Expected location:");
            System.err.println(
                    "src/main/resources/video/.mp4"
            );
            System.err.println("======================================");

            // If video is not found, directly open Welcome Page
            Main.showWelcomePage(stage);

            return;
        }

        System.out.println("Splash video found:");
        System.out.println(videoURL);

        // =========================================================
        // CREATE MEDIA
        // =========================================================

        Media media;

        try {

            media = new Media(
                    videoURL.toExternalForm()
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to load splash video."
            );

            e.printStackTrace();

            Main.showWelcomePage(stage);

            return;
        }

        // =========================================================
        // MEDIA PLAYER
        // =========================================================

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setAutoPlay(false);

        // =========================================================
        // MEDIA VIEW
        // =========================================================

        MediaView mediaView =
                new MediaView(mediaPlayer);

        /*
         * Automatically resize the video
         * according to the maximized window.
         */

        mediaView.fitWidthProperty().bind(
                stage.widthProperty()
        );

        mediaView.fitHeightProperty().bind(
                stage.heightProperty()
        );

        mediaView.setPreserveRatio(false);

        // =========================================================
        // ROOT
        // =========================================================

        StackPane root = new StackPane();

        root.setStyle(
                "-fx-background-color: black;"
        );

        root.getChildren().add(mediaView);

        // =========================================================
        // SCENE
        // =========================================================

        Scene scene = new Scene(root);

        // =========================================================
        // STAGE
        // =========================================================

        stage.setTitle("SpotFit");

        stage.setScene(scene);

        stage.setResizable(true);

        /*
         * Keep splash maximized.
         */
        stage.setMaximized(true);

        stage.show();

        // =========================================================
        // VIDEO READY
        // =========================================================

        mediaPlayer.setOnReady(() -> {

            System.out.println(
                    "Splash video is ready."
            );

            mediaPlayer.play();

            System.out.println(
                    "Splash video started."
            );
        });

        // =========================================================
        // VIDEO FINISHED
        // =========================================================

        mediaPlayer.setOnEndOfMedia(() -> {

            System.out.println(
                    "Splash video finished."
            );

            /*
             * Smooth fade-out of splash.
             */

            FadeTransition fadeOut =
                    new FadeTransition(
                            Duration.millis(700),
                            root
                    );

            fadeOut.setFromValue(1.0);

            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {

                mediaPlayer.stop();

                /*
                 * Open maximized Welcome Page.
                 */
                Main.showWelcomePage(stage);
            });

            fadeOut.play();
        });

        // =========================================================
        // VIDEO ERROR
        // =========================================================

        mediaPlayer.setOnError(() -> {

            System.err.println(
                    "======================================"
            );

            System.err.println(
                    "SPLASH VIDEO PLAYBACK ERROR"
            );

            System.err.println(
                    "======================================"
            );

            if (mediaPlayer.getError() != null) {

                mediaPlayer.getError()
                        .printStackTrace();
            }

            /*
             * If video cannot play,
             * continue to Welcome Page.
             */

            Main.showWelcomePage(stage);
        });
    }

    public static void main(String[] args) {

        launch(args);
    }
}