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

public class PersonaSelection {

    private static final String[][] PERSONAS = {
            { "🎓", "Student" }, { "💻", "Freelancer" }, { "🎨", "Artist" }, { "📷", "Photographer" },
            { "🎬", "Content Creator" }, { "🎙", "Podcaster" }, { "🎵", "Musician" }, { "💼", "Working Professional" },
            { "🚀", "Entrepreneur" }, { "✍️", "Writer" }, { "🔬", "Researcher" }, { "🏃", "Fitness Enthusiast" }
    };

    public static Scene getPersonaScene(Runnable onBack, Runnable onContinue) {

        ObservableSet<String> selected = FXCollections.observableSet("Student", "Content Creator", "Photographer");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        root.setTop(buildTopBar());

        VBox stepper = com.eklavya.spotfit.view.common.StepperBar.build(
                new String[] { "Complete Profile", "Select Persona", "Location" }, 2);

        Text heading = new Text("What best describes you?");
        heading.setStyle(heading(30));
        HBox headingRow = new HBox(heading);
        headingRow.setAlignment(Pos.CENTER);

        Text subtitle = new Text("Choose one or more personas to help us personalize your SpotFit experience. "
                + "We'll recommend spaces, activities, workshops, and events based on your interests.");
        subtitle.setStyle(body(14));
        subtitle.setWrappingWidth(760);
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        HBox subtitleRow = new HBox(subtitle);
        subtitleRow.setAlignment(Pos.CENTER);

        VBox summaryBox = new VBox();
        GridPane grid = buildPersonaGrid(selected, summaryBox);
        refreshSummary(summaryBox, selected);

        VBox center = new VBox(28, stepper, headingRow, subtitleRow, grid, summaryBox);
        center.setPadding(new Insets(40, 80, 20, 80));
        center.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG_PAGE + "; -fx-background-color: " + BG_PAGE + ";");
        root.setCenter(scroll);

        root.setBottom(buildBottomBar(onBack, onContinue, selected));

        Rectangle2D screen = screenBounds();
        return new Scene(root, screen.getWidth(), screen.getHeight());
    }

    private static HBox buildTopBar() {
        Label logo = new Label("SpotFit");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY + ";");

        Label progress = new Label("2 / 4 Completed");
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

    private static GridPane buildPersonaGrid(ObservableSet<String> selected, VBox summaryBox) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setAlignment(Pos.CENTER);

        Map<String, VBox> cardMap = new LinkedHashMap<>();

        int col = 0, row = 0;
        for (String[] persona : PERSONAS) {
            String emoji = persona[0];
            String name = persona[1];
            VBox cardNode = personaCard(emoji, name);
            cardMap.put(name, cardNode);
            cardNode.setOnMouseClicked(e -> {
                if (selected.contains(name)) {
                    selected.remove(name);
                } else {
                    selected.add(name);
                }
                stylePersonaCard(cardNode, selected.contains(name));
                refreshSummary(summaryBox, selected);
            });
            stylePersonaCard(cardNode, selected.contains(name));
            grid.add(cardNode, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    private static VBox personaCard(String emoji, String name) {
        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 26px;");
        Label label = new Label(name);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        VBox card = new VBox(12, icon, label);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 130);
        card.setStyle("-fx-cursor: hand;");
        return card;
    }

    private static void stylePersonaCard(VBox card, boolean selected) {
        card.setStyle(
                "-fx-cursor: hand;" +
                        "-fx-background-color: " + (selected ? PILL_BG : "white") + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: " + (selected ? PRIMARY : BORDER) + ";" +
                        "-fx-border-width: " + (selected ? "2" : "1") + ";");
    }

    private static void refreshSummary(VBox summaryBox, ObservableSet<String> selected) {
        summaryBox.getChildren().clear();

        Label label = new Label("Selected Personas");
        label.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");

        FlowPane chips = new FlowPane(10, 10);
        for (String name : selected) {
            Label chip = new Label(name + "  ✕");
            chip.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-text-fill: " + TEXT_DARK + ";" +
                            "-fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-border-radius: 16;" +
                            "-fx-background-radius: 16;" +
                            "-fx-padding: 6 12 6 12; -fx-font-size: 12px;");
            chips.getChildren().add(chip);
        }

        Label recommendLabel = new Label("Based on your selection, you may like:\n"
                + "🎙 Podcast Studios, 📷 Photography Studios, ☕ Creative Cafes, 🎨 Workshops");
        recommendLabel.setWrapText(true);
        recommendLabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 16;" +
                        "-fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");
        recommendLabel.setMaxWidth(420);

        HBox topRow = new HBox(30, new VBox(10, label, chips), recommendLabel);
        HBox.setHgrow(topRow.getChildren().get(0), Priority.ALWAYS);

        VBox box = new VBox(16, topRow);
        box.setPadding(new Insets(20));
        box.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                        "-fx-background-radius: 16;");

        summaryBox.getChildren().add(box);
    }

    private static HBox buildBottomBar(Runnable onBack, Runnable onContinue, ObservableSet<String> selected) {
        var backBtn = outlineButton("←  Back");
        backBtn.setPrefWidth(140);
        backBtn.setOnAction(e -> onBack.run());

        var continueBtn = primaryButton("Continue  →");
        continueBtn.setPrefWidth(160);
        continueBtn.setOnAction(e -> {
            continueBtn.setDisable(true);
            continueBtn.setText("Saving...");

            UserProfileController profileController = new UserProfileController();
            profileController.savePersonas(new ArrayList<>(selected))
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        continueBtn.setDisable(false);
                        continueBtn.setText("Continue  →");
                        onContinue.run();
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
