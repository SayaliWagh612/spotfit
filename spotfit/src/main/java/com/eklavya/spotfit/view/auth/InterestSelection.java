package com.eklavya.spotfit.view.auth;

import com.eklavya.spotfit.controller.user.UserProfileController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.eklavya.spotfit.util.ResponsiveUtil.*;
import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

public class InterestSelection {

    private static final String[][] INTERESTS = {
            { "📷", "Photography" },
            { "🎬", "Content Creation" },
            { "☕", "Aesthetic Cafés" },
            { "🎙", "Podcasting" },
            { "🏺", "Pottery" },
            { "🎨", "Painting" },
            { "✏️", "Drawing" },
            { "✂️", "Craft" },
            { "🎵", "Music" },
            { "🎸", "Music Practice" },
            { "🎧", "Recording Studios" },
            { "💡", "Creative Workshops" },
            { "🍳", "Cooking Workshops" },
            { "📹", "Camera Rental" },
            { "🏛", "Creator Studios" }
    };

    public static Scene getInterestScene(Runnable onBack, Runnable onContinue) {

        ObservableSet<String> selected = FXCollections.observableSet(
                "Photography", "Podcasting", "Creator Studios"
        );

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        root.setTop(buildTopBar());

        VBox stepper = com.eklavya.spotfit.view.common.StepperBar.build(
                new String[] { "Persona", "Interests", "Location" }, 2);

        Text heading = new Text("Select Your Interests");
        heading.setStyle(heading(30));
        HBox headingRow = new HBox(heading);
        headingRow.setAlignment(Pos.CENTER);

        Text subtitle = new Text("Select activities, spaces, and experiences you are interested in exploring on SpotFit.");
        subtitle.setStyle(body(14));
        subtitle.setWrappingWidth(760);
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        HBox subtitleRow = new HBox(subtitle);
        subtitleRow.setAlignment(Pos.CENTER);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(600);
        statusLabel.setStyle("-fx-font-size: 13px;");
        HBox statusRow = new HBox(statusLabel);
        statusRow.setAlignment(Pos.CENTER);

        VBox summaryBox = new VBox();
        GridPane grid = buildInterestGrid(selected, summaryBox, statusLabel);
        refreshSummary(summaryBox, selected);

        VBox center = new VBox(24, stepper, headingRow, subtitleRow, statusRow, grid, summaryBox);
        center.setPadding(new Insets(30, 80, 20, 80));
        center.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PAGE + "; -fx-background-color: " + BG_PAGE + ";");
        root.setCenter(scroll);

        root.setBottom(buildBottomBar(onBack, onContinue, selected, statusLabel));

        Rectangle2D screen = screenBounds();
        return new Scene(root, screen.getWidth(), screen.getHeight());
    }

    private static HBox buildTopBar() {
        Label logo = new Label("SpotFit");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY + ";");

        Label progress = new Label("3 / 4 Completed");
        progress.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                        "-fx-text-fill: " + PRIMARY + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 6 14 6 14;" +
                        "-fx-font-size: 12px; -fx-font-weight: bold;");
        Label help = new Label("?");
        help.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px; -fx-cursor: hand;");
        Label close = new Label("✕");
        close.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(16, logo, spacer, progress, help, close);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(20, 40, 20, 40));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");
        return bar;
    }

    private static GridPane buildInterestGrid(ObservableSet<String> selected, VBox summaryBox, Label statusLabel) {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setAlignment(Pos.CENTER);

        Map<String, VBox> cardMap = new LinkedHashMap<>();

        int col = 0, row = 0;
        for (String[] interest : INTERESTS) {
            String emoji = interest[0];
            String name = interest[1];
            VBox cardNode = interestCard(emoji, name);
            cardMap.put(name, cardNode);
            cardNode.setOnMouseClicked(e -> {
                statusLabel.setText("");
                if (selected.contains(name)) {
                    selected.remove(name);
                } else {
                    selected.add(name);
                }
                styleInterestCard(cardNode, selected.contains(name));
                refreshSummary(summaryBox, selected);
            });
            styleInterestCard(cardNode, selected.contains(name));
            grid.add(cardNode, col, row);
            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    private static VBox interestCard(String emoji, String name) {
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 24px;");
        Label label = new Label(name);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        VBox card = new VBox(10, icon, label);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(170, 110);
        card.setStyle("-fx-cursor: hand;");
        return card;
    }

    private static void styleInterestCard(VBox card, boolean selected) {
        card.setStyle(
                "-fx-cursor: hand;" +
                        "-fx-background-color: " + (selected ? PILL_BG : "white") + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: " + (selected ? PRIMARY : BORDER) + ";" +
                        "-fx-border-width: " + (selected ? "2" : "1") + ";");
    }

    private static void refreshSummary(VBox summaryBox, ObservableSet<String> selected) {
        summaryBox.getChildren().clear();

        Label label = new Label("Selected Interests (" + selected.size() + ")");
        label.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        FlowPane chips = new FlowPane(8, 8);
        for (String name : selected) {
            Label chip = new Label(name);
            chip.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-text-fill: " + TEXT_DARK + ";" +
                            "-fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 5 12 5 12; -fx-font-size: 12px;");
            chips.getChildren().add(chip);
        }

        VBox contentBox = new VBox(10, label, chips);
        contentBox.setPadding(new Insets(16));
        contentBox.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                        "-fx-background-radius: 14;");

        summaryBox.getChildren().add(contentBox);
    }

    private static HBox buildBottomBar(Runnable onBack, Runnable onContinue, ObservableSet<String> selected, Label statusLabel) {
        var backBtn = outlineButton("←  Back");
        backBtn.setPrefWidth(140);
        backBtn.setOnAction(e -> onBack.run());

        var continueBtn = primaryButton("Continue  →");
        continueBtn.setPrefWidth(160);
        continueBtn.setOnAction(e -> {
            if (selected.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                statusLabel.setText("Please select at least one interest to continue.");
                return;
            }

            statusLabel.setText("");
            continueBtn.setDisable(true);
            continueBtn.setText("Saving Interests...");

            UserProfileController profileController = new UserProfileController();
            profileController.saveInterests(new ArrayList<>(selected))
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        if (throwable != null) {
                            continueBtn.setDisable(false);
                            continueBtn.setText("Continue  →");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Failed to save interests. Please try again.");
                            return;
                        }

                        if (result.isSuccess()) {
                            continueBtn.setDisable(false);
                            continueBtn.setText("Continue  →");
                            onContinue.run();
                        } else {
                            continueBtn.setDisable(false);
                            continueBtn.setText("Continue  →");
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(backBtn, spacer, continueBtn);
        bar.setPadding(new Insets(20, 80, 20, 80));
        bar.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");
        return bar;
    }
}
