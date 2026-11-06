package com.eklavya.spotfit.view.common;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class UIComponents {

    public static TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(48);
        field.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-prompt-text-fill: " + TEXT_PLACEHOLDER + ";" +
                "-fx-padding: 0 14 0 14;"
        );
        return field;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(48);
        field.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-prompt-text-fill: " + TEXT_PLACEHOLDER + ";" +
                "-fx-padding: 0 14 0 14;"
        );
        return field;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(48);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + PRIMARY_DARK + ";" +
                "-fx-background-radius: 10; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-background-radius: 10; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand;"));
        return btn;
    }

    public static Button outlineButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(48);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: " + BORDER_STRONG + ";" +
                "-fx-border-width: 1;" +
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-font-size: 14px;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    public static Label pillBadge(String text) {
        Label pill = new Label(text);
        pill.setStyle(
                "-fx-background-color: " + PILL_BG + ";" +
                "-fx-background-radius: 20;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16 8 16;"
        );
        return pill;
    }

    public static VBox card(double width) {
        VBox card = new VBox(18);
        card.setPrefWidth(width);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: " + CARD_WHITE + ";" +
                "-fx-background-radius: 24;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(30);
        shadow.setOffsetY(10);
        card.setEffect(shadow);
        return card;
    }

    public static HBox divider(String middleText) {
        HBox line1 = new HBox();
        HBox.setHgrow(line1, Priority.ALWAYS);
        line1.setStyle("-fx-background-color: " + BORDER + ";");
        line1.setPrefHeight(1);

        HBox line2 = new HBox();
        HBox.setHgrow(line2, Priority.ALWAYS);
        line2.setStyle("-fx-background-color: " + BORDER + ";");
        line2.setPrefHeight(1);

        Label label = new Label(middleText);
        label.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        HBox row = new HBox(12, line1, label, line2);
        row.setStyle("-fx-alignment: center;");
        return row;
    }

    public static CheckBox checkbox(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setStyle("-fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 13px;");
        return cb;
    }
}
