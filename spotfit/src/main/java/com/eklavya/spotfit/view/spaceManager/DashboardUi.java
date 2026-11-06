package com.eklavya.spotfit.view.spaceManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// Shared colors + reusable UI builders for Dashboard/Bookings/Calendar/Check-In/My Space/Analytics/Reviews/Complaints/Settings.
public class DashboardUi {

    public static final String PRIMARY = "#884600";
    public static final String BACKGROUND = "#f8efe6";
    public static final String SURFACE = "#ffffff";
    public static final String TEXT = "#1e1b17";
    public static final String SECONDARY_TEXT = "#6b5a4a";
    public static final String BORDER = "#e3d5c5";
    public static final String CHIP_BG = "#ffeade";
    public static final String ACTIVE_BG = "#ffe3cf";
    public static final String GREEN_TEXT = "#1a7f37";
    public static final String GREEN_BG = "#e6f4ea";
    public static final String RED_TEXT = "#ba1a1a";
    public static final String RED_BG = "#ffdad6";
    public static final String BLUE_TEXT = "#1a56a7";
    public static final String BLUE_BG = "#e3edfa";

    public static VBox card(Node... children) {
        VBox card = new VBox(16, children);
        card.setPadding(new Insets(24));
        card.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:20px; -fx-border-color:" + BORDER
                + "; -fx-border-radius:20px; -fx-border-width:1px;");
        return card;
    }

    public static Label heading(String text, double size) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:" + size + "px; -fx-font-weight:bold; -fx-text-fill:" + TEXT + ";");
        label.setWrapText(true);
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:14px; -fx-text-fill:" + SECONDARY_TEXT + ";");
        label.setWrapText(true);
        return label;
    }

    public static Label chip(String text, String bg, String fg) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + fg + "; -fx-background-color:" + bg
                + "; -fx-background-radius:20px; -fx-padding:6 14;");
        return label;
    }

    public static Label outlineChip(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:13px; -fx-text-fill:" + TEXT + "; -fx-background-color:" + SURFACE
                + "; -fx-border-color:"
                + BORDER + "; -fx-border-radius:20px; -fx-background-radius:20px; -fx-padding:8 16;");
        return label;
    }

    public static Label avatar(String initials, double size) {
        Label label = new Label(initials);
        label.setStyle("-fx-font-size:" + (size * 0.35) + "px; -fx-font-weight:bold; -fx-text-fill:" + TEXT
                + "; -fx-background-color:" + CHIP_BG
                + "; -fx-background-radius:50%; -fx-min-width:" + size + "px; -fx-min-height:" + size
                + "px; -fx-max-width:" + size
                + "px; -fx-max-height:" + size + "px; -fx-alignment:center;");
        return label;
    }

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(44);
        button.setStyle("-fx-background-color:" + PRIMARY
                + "; -fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold; -fx-background-radius:12px; -fx-cursor:hand;");
        return button;
    }

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(44);
        button.setStyle("-fx-background-color:" + SURFACE + "; -fx-text-fill:" + TEXT
                + "; -fx-font-size:14px; -fx-font-weight:bold; "
                + "-fx-border-color:" + BORDER + "; -fx-border-width:1px; -fx-border-radius:12px; -fx-cursor:hand;");
        return button;
    }

    public static TextField searchField(String promptText) {
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setStyle("-fx-background-color:" + SURFACE
                + "; -fx-font-size:14px; -fx-background-radius:12px; -fx-border-color:" + BORDER
                + "; -fx-border-radius:12px; -fx-padding:10 14;");
        return tf;
    }

    public static Label link(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + PRIMARY + "; -fx-cursor:hand;");
        return label;
    }

    public static HBox sectionHeader(String title, Label rightLink) {
        Label titleLabel = heading(title, 18);
        HBox header = rightLink != null ? new HBox(titleLabel, hgrow(), rightLink) : new HBox(titleLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    public static Region hgrow() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    public static ScrollPane scroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color:" + BACKGROUND + "; -fx-background:" + BACKGROUND + ";");
        return scrollPane;
    }

    public static class ModalDialog {
        private final StackPane overlay;
        private final StackPane stackRoot;

        public ModalDialog(StackPane overlay, StackPane stackRoot) {
            this.overlay = overlay;
            this.stackRoot = stackRoot;
        }

        public void close() {
            if (stackRoot != null && overlay != null) {
                stackRoot.getChildren().remove(overlay);
            }
        }
    }

    public static ModalDialog showModal(String title, Node body) {
        Scene scene = com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage() != null ? com.eklavya.spotfit.navigation.AppNavigator.getPrimaryStage().getScene() : null;
        if (scene == null) {
            return new ModalDialog(null, null);
        }

        Parent root = scene.getRoot();
        StackPane stackRoot;

        if (root instanceof StackPane) {
            stackRoot = (StackPane) root;
        } else {
            stackRoot = new StackPane(root);
            scene.setRoot(stackRoot);
        }

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");

        Label headerTitle = heading(title, 20);
        Label closeBtn = new Label("✕");
        closeBtn.setStyle(
                "-fx-font-size:16px; -fx-font-weight:bold; -fx-cursor:hand; -fx-text-fill:" + SECONDARY_TEXT + ";");

        HBox top = new HBox(headerTitle, hgrow(), closeBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(16, 24, 16, 24));
        top.setStyle("-fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1px;");

        VBox modalContent = new VBox(top, body);
        modalContent.setStyle("-fx-background-color:" + SURFACE + "; -fx-background-radius:16px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);");
        modalContent.setMaxWidth(600);
        modalContent.setMaxHeight(Region.USE_PREF_SIZE);
        modalContent.setPadding(new Insets(0, 0, 16, 0));

        modalContent.setOnMouseClicked(javafx.event.Event::consume);

        overlay.getChildren().add(modalContent);
        overlay.setAlignment(Pos.CENTER);

        ModalDialog dialog = new ModalDialog(overlay, stackRoot);
        closeBtn.setOnMouseClicked(e -> dialog.close());
        overlay.setOnMouseClicked(e -> dialog.close());

        stackRoot.getChildren().add(overlay);
        return dialog;
    }
}
