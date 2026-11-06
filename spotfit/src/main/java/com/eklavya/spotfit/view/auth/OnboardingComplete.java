package com.eklavya.spotfit.view.auth;

import com.eklavya.spotfit.controller.user.UserProfileController;
import com.eklavya.spotfit.model.User;
import com.eklavya.spotfit.session.AppSession;
import javafx.application.Platform;
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
import java.util.List;

import static com.eklavya.spotfit.util.ResponsiveUtil.*;
import static com.eklavya.spotfit.util.StyleConstants.*;
import static com.eklavya.spotfit.view.common.UIComponents.*;

public class OnboardingComplete {

    public static Scene getCompleteScene(String userName, Runnable onStartExploring, Runnable onCustomize) {
        return getCompleteScene((User) null, onStartExploring, onCustomize);
    }

    public static Scene getCompleteScene(User userParam, Runnable onStartExploring, Runnable onCustomize) {

        Label pin = new Label("📍");
        Label logoText = new Label("SpotFit");
        logoText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY + ";");
        HBox logoRow = new HBox(6, pin, logoText);
        logoRow.setAlignment(Pos.CENTER);

        Label checkCircleOuter = new Label("✓");
        checkCircleOuter.setPrefSize(120, 120);
        checkCircleOuter.setAlignment(Pos.CENTER);
        checkCircleOuter.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 44px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-border-color: " + PILL_BG + "; -fx-border-width: 8; -fx-border-radius: 50%;");

        String initialName = (userParam != null && userParam.getName() != null && !userParam.getName().isBlank())
                ? userParam.getName()
                : AppSession.getName();
        if (initialName == null || initialName.isBlank()) {
            initialName = "there";
        }

        Text welcome = new Text("Welcome to SpotFit, " + initialName + "! 🎉");
        welcome.setStyle(heading(22));
        Text subtitle = new Text("Your personalized profile is saved and ready to be explored.");
        subtitle.setStyle(body(14));

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(440);
        statusLabel.setStyle("-fx-font-size: 13px;");

        VBox summaryContainer = new VBox();
        summaryContainer.setAlignment(Pos.CENTER);

        if (userParam != null) {
            summaryContainer.getChildren().add(buildSummaryCard(userParam));
        } else {
            Label loadingLabel = new Label("Loading your profile summary...");
            loadingLabel.setStyle(body(13));
            summaryContainer.getChildren().add(loadingLabel);

            UserProfileController profileController = new UserProfileController();
            profileController.loadCurrentUser().whenComplete((result, throwable) -> Platform.runLater(() -> {
                summaryContainer.getChildren().clear();
                if (result != null && result.isSuccess() && result.getData() != null) {
                    User loadedUser = result.getData();
                    welcome.setText("Welcome to SpotFit, " + loadedUser.getName() + "! 🎉");
                    summaryContainer.getChildren().add(buildSummaryCard(loadedUser));
                } else {
                    summaryContainer.getChildren().add(buildSummaryCard(null));
                }
            }));
        }

        HBox statsRow = buildPreviewRow();

        Button startBtn = primaryButton("Start Exploring");
        startBtn.setMaxWidth(300);
        startBtn.setOnAction(e -> {
            statusLabel.setText("");
            startBtn.setDisable(true);
            startBtn.setText("Finalizing Setup...");

            UserProfileController profileController = new UserProfileController();
            profileController.completeOnboarding()
                    .whenComplete((result, throwable) -> Platform.runLater(() -> {
                        startBtn.setDisable(false);
                        startBtn.setText("Start Exploring");

                        if (throwable != null) {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText("Something went wrong while contacting Firebase. Please try again.");
                            return;
                        }

                        if (result.isSuccess()) {
                            onStartExploring.run();
                        } else {
                            statusLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13px;");
                            statusLabel.setText(result.getMessage());
                        }
                    }));
        });

        Label customize = new Label("Customize Preferences");
        customize.setStyle(
                "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;");
        customize.setOnMouseClicked(e -> onCustomize.run());

        VBox content = new VBox(20, logoRow, checkCircleOuter, welcome, subtitle, statusLabel, summaryContainer, statsRow, startBtn,
                customize);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(920);
        content.setPadding(new Insets(60));

        VBox root = new VBox(content);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + BG_PAGE + ";");

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: " + BG_PAGE + "; -fx-background-color: " + BG_PAGE + ";");

        Rectangle2D screen = screenBounds();
        return new Scene(scroll, screen.getWidth(), screen.getHeight());
    }

    private static VBox buildSummaryCard(User user) {
        List<String> personas = (user != null && user.getPersonas() != null) ? user.getPersonas() : new ArrayList<>();
        VBox personaCol = summaryColumn("PERSONAS", personas.isEmpty() ? List.of("No personas selected") : personas);

        List<String> interests = (user != null && user.getInterests() != null) ? user.getInterests() : new ArrayList<>();
        VBox interestsCol = summaryColumn("INTERESTS", interests.isEmpty() ? List.of("No interests selected") : interests);

        Label locationTitle = new Label("LOCATION & PREFERENCES");
        locationTitle.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        String city = (user != null && user.getCity() != null && !user.getCity().isBlank()) ? user.getCity() : null;
        double dist = (user != null) ? user.getPreferredDistanceKm() : 0.0;

        String locationText;
        if (city != null && dist > 0) {
            locationText = "📍 " + city + " • Within " + (int) dist + " km";
        } else if (city != null) {
            locationText = "📍 " + city;
        } else {
            locationText = "📍 Location not specified";
        }

        Label locationVal = new Label(locationText);
        locationVal.setStyle(body(13));
        locationVal.setWrapText(true);

        long budgetPaise = (user != null) ? user.getBudgetPreferencePaise() : 0L;
        String budgetText;
        if (budgetPaise > 0) {
            budgetText = "💳 Budget up to ₹" + String.format("%,d", budgetPaise / 100);
        } else {
            budgetText = "💳 Budget not specified";
        }
        Label budgetVal = new Label(budgetText);
        budgetVal.setStyle(body(13));

        String lang = (user != null && user.getPreferredLanguage() != null && !user.getPreferredLanguage().isBlank())
                ? user.getPreferredLanguage()
                : "en";
        boolean notify = (user != null) ? user.isNotificationsEnabled() : true;
        Label prefVal = new Label("🗣 " + lang.toUpperCase() + " • " + (notify ? "Notifications On" : "Notifications Off"));
        prefVal.setStyle(body(12));

        VBox locationCol = new VBox(8, locationTitle, locationVal, budgetVal, prefVal);
        locationCol.setMaxWidth(300);

        HBox row = new HBox(40, personaCol, interestsCol, locationCol);
        row.setAlignment(Pos.TOP_LEFT);

        VBox card = card(880);
        card.getChildren().add(row);
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    private static VBox summaryColumn(String title, List<String> chips) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        FlowPane chipPane = new FlowPane(8, 8);
        for (String chip : chips) {
            Label chipLabel = new Label(chip);
            chipLabel.setStyle(
                    "-fx-background-color: " + PILL_BG + ";" +
                            "-fx-text-fill: " + TEXT_DARK + ";" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 6 12 6 12; -fx-font-size: 12px;");
            chipPane.getChildren().add(chipLabel);
        }
        VBox col = new VBox(10, titleLabel, chipPane);
        col.setMaxWidth(260);
        return col;
    }

    private static HBox buildPreviewRow() {
        Label statsTitle = new Label("Explore Categories (Preview)");
        statsTitle.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label statsSub = new Label("Explore space categories tailored to your profile");
        statsSub.setStyle(body(12));
        VBox statsCard = new VBox(8, statsTitle, statsSub);
        statsCard.setPadding(new Insets(20));
        statsCard.setStyle("-fx-background-color: " + PILL_BG + "; -fx-background-radius: 16;");
        statsCard.setPrefWidth(280);

        GridPane categoryGrid = new GridPane();
        categoryGrid.setHgap(12);
        categoryGrid.setVgap(12);
        categoryGrid.add(categoryTile("🎨", "Creativity"), 0, 0);
        categoryGrid.add(categoryTile("💻", "Study & Work"), 1, 0);
        categoryGrid.add(categoryTile("🎙", "Content"), 0, 1);
        categoryGrid.add(categoryTile("📷", "Photography"), 1, 1);

        HBox row = new HBox(20, statsCard, categoryGrid);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private static VBox categoryTile(String icon, String label) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px;");
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        VBox tile = new VBox(10, iconLabel, textLabel);
        tile.setAlignment(Pos.CENTER);
        tile.setPrefSize(140, 100);
        tile.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-border-radius: 14;");
        return tile;
    }
}
