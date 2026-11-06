package com.eklavya.spotfit.view.user;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import com.eklavya.spotfit.controller.user.CustomerFavoriteController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.eklavya.spotfit.util.StyleConstants.*;

import com.eklavya.spotfit.session.AppSession;

public class CustomerUi {

    private static final Map<String, Image> imageCache = new HashMap<>();

    public static boolean isGuestSession() {
        return !AppSession.isAuthenticated()
                || AppSession.getUserId() == null || AppSession.getUserId().isBlank()
                || AppSession.getIdToken() == null || AppSession.getIdToken().isBlank();
    }

    public static Label pageTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");
        return l;
    }

    public static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");
        return l;
    }

    public static Label cardTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: "
                + TEXT_DARK + ";");
        return l;
    }

    public static Label primaryBody(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 15px; -fx-text-fill: " + TEXT_DARK + ";");
        l.setWrapText(true);
        return l;
    }

    public static Label secondaryBody(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 14px; -fx-text-fill: " + TEXT_MUTED + ";");
        l.setWrapText(true);
        return l;
    }

    public static Label labelChip(String text, String bgColor, String textColor) {
        Label l = new Label(text);
        l.setStyle(
                "-fx-font-family: " + FONT + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + textColor
                        + "; -fx-background-color: " + bgColor + "; -fx-padding: 4 10; -fx-background-radius: 12px;");
        return l;
    }

    public static Button createFavoriteButton(
            String spaceId,
            CustomerFavoriteController controller,
            Runnable onFavoriteRemoved,
            int size,
            int fontSize) {

        final String EMPTY_HEART = "\u2661"; // ♡
        final String FILLED_HEART = "\u2665"; // ♥

        Button btn = new Button(EMPTY_HEART);

        btn.setPrefSize(size, size);
        btn.setMinSize(size, size);
        btn.setMaxSize(size, size);
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(javafx.geometry.Insets.EMPTY);
        btn.setMnemonicParsing(false);
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setFocusTraversable(false);

        Tooltip tooltip = new Tooltip("Save space");
        tooltip.setStyle("-fx-font-family: " + FONT + "; -fx-font-size: 12px;");
        btn.setTooltip(tooltip);

        Runnable updateStyle = () -> {
            boolean saved = FILLED_HEART.equals(btn.getText());
            String color = saved ? PRIMARY : TEXT_MUTED;

            btn.setStyle(
                    "-fx-font-family: 'Segoe UI Symbol';" +
                            "-fx-font-size: " + fontSize + "px;" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-background-color: " + CARD_WHITE + ";" +
                            "-fx-background-radius: " + (size / 2.0) + ";" +
                            "-fx-border-color: " + BORDER + ";" +
                            "-fx-border-radius: " + (size / 2.0) + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 0;" +
                            "-fx-alignment: center;" +
                            "-fx-cursor: hand;");
        };

        updateStyle.run();
        btn.setDisable(true);

        btn.setOnMouseEntered(event -> {
            boolean saved = FILLED_HEART.equals(btn.getText());
            String color = saved ? PRIMARY : PRIMARY_DARK;

            btn.setStyle(
                    "-fx-font-family: 'Segoe UI Symbol';" +
                            "-fx-font-size: " + fontSize + "px;" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-background-color: " + PILL_BG + ";" +
                            "-fx-background-radius: " + (size / 2.0) + ";" +
                            "-fx-border-color: " + BORDER_STRONG + ";" +
                            "-fx-border-radius: " + (size / 2.0) + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-padding: 0;" +
                            "-fx-alignment: center;" +
                            "-fx-cursor: hand;");
        });

        btn.setOnMouseExited(event -> updateStyle.run());

        controller.isFavorite(spaceId).thenAccept(result -> Platform.runLater(() -> {
            if (result.isSuccess() && Boolean.TRUE.equals(result.getData())) {
                btn.setText(FILLED_HEART);
                tooltip.setText("Remove from saved spaces");
            } else {
                btn.setText(EMPTY_HEART);
                tooltip.setText("Save space");
            }

            updateStyle.run();
            btn.setDisable(false);
        })).exceptionally(error -> {
            Platform.runLater(() -> {
                tooltip.setText("Could not check saved status");
                btn.setDisable(false);
                updateStyle.run();
            });
            return null;
        });

        btn.setOnAction(event -> {
            event.consume();
            btn.setDisable(true);
            tooltip.setText("Updating...");

            controller.toggleFavorite(spaceId).thenAccept(result -> Platform.runLater(() -> {
                if (result.isSuccess()) {
                    boolean nowSaved = Boolean.TRUE.equals(result.getData());

                    btn.setText(nowSaved ? FILLED_HEART : EMPTY_HEART);
                    tooltip.setText(
                            nowSaved
                                    ? "Remove from saved spaces"
                                    : "Save space");

                    if (!nowSaved && onFavoriteRemoved != null) {
                        onFavoriteRemoved.run();
                    }
                } else {
                    tooltip.setText(
                            result.getMessage() != null
                                    ? result.getMessage()
                                    : "Could not update saved space");
                }

                updateStyle.run();
                btn.setDisable(false);
            })).exceptionally(error -> {
                Platform.runLater(() -> {
                    tooltip.setText("Could not update saved space");
                    btn.setDisable(false);
                    updateStyle.run();
                });
                return null;
            });
        });

        return btn;
    }

    public static ImageView resolveSpaceCover(String coverPhotoUrl, String activityGroup) {
        ImageView iv = new ImageView();
        iv.setPreserveRatio(false);

        if (coverPhotoUrl != null && !coverPhotoUrl.isBlank() && coverPhotoUrl.startsWith("http")) {
            Image webImg = new Image(coverPhotoUrl, true);
            iv.setImage(webImg);
            webImg.errorProperty().addListener((obs, oldVal, isError) -> {
                if (isError) {
                    iv.setImage(getLocalFallback(activityGroup));
                }
            });
        } else {
            iv.setImage(getLocalFallback(activityGroup));
        }

        return iv;
    }

    private static Image getLocalFallback(String activityGroup) {
        if (activityGroup == null)
            return getNeutralPlaceholder();
        String path = switch (activityGroup.toUpperCase()) {
            case "ART" -> "/images/user/creativity/art.png";
            case "MUSIC" -> "/images/user/creativity/music.png";
            case "PODCASTS" -> "/images/user/creativity/podcasts.png";
            case "CONTENT_CREATION" -> "/images/user/creativity/content-creation.png";
            default -> null;
        };

        if (path == null)
            return getNeutralPlaceholder();

        if (imageCache.containsKey(path))
            return imageCache.get(path);

        try {
            InputStream is = CustomerUi.class.getResourceAsStream(path);
            if (is != null) {
                Image img = new Image(is);
                if (!img.isError()) {
                    imageCache.put(path, img);
                    return img;
                }
            }
        } catch (Exception ignored) {
        }

        return getNeutralPlaceholder();
    }

    private static Image getNeutralPlaceholder() {
        String path = "/images/UserDashImg/Creativity.png"; // existing fallback or generic
        if (imageCache.containsKey(path))
            return imageCache.get(path);
        try {
            InputStream is = CustomerUi.class.getResourceAsStream(path);
            if (is != null) {
                Image img = new Image(is);
                imageCache.put(path, img);
                return img;
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
