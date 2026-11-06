package com.eklavya.spotfit.view.common;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import static com.eklavya.spotfit.util.StyleConstants.*;

public class StepperBar {

    /** currentStep is 1-indexed. Steps before it are marked complete (checkmark). */
    public static VBox build(String[] stepLabels, int currentStep) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < stepLabels.length; i++) {
            int stepNumber = i + 1;
            int circleCol = i * 2;

            Label circle = buildCircle(stepNumber, currentStep);
            grid.add(circle, circleCol, 0);
            GridPane.setHalignment(circle, HPos.CENTER);

            Label text = buildLabel(stepLabels[i], stepNumber, currentStep);
            grid.add(text, circleCol, 1);
            GridPane.setHalignment(text, HPos.CENTER);

            ColumnConstraints circleConstraint = new ColumnConstraints();
            circleConstraint.setHgrow(Priority.NEVER);
            grid.getColumnConstraints().add(circleConstraint);

            if (i < stepLabels.length - 1) {
                Region line = new Region();
                line.setPrefHeight(2);
                line.setMinHeight(2);
                line.setMaxHeight(2); // <- the fix: without this the line fills the whole row height
                line.setStyle("-fx-background-color: " + (stepNumber < currentStep ? PRIMARY : BORDER) + ";");

                int lineCol = circleCol + 1;
                grid.add(line, lineCol, 0);
                GridPane.setValignment(line, VPos.CENTER);
                GridPane.setHgrow(line, Priority.ALWAYS);

                ColumnConstraints lineConstraint = new ColumnConstraints();
                lineConstraint.setHgrow(Priority.ALWAYS);
                lineConstraint.setMinWidth(40);
                grid.getColumnConstraints().add(lineConstraint);
            }
        }

        Label stepOf = new Label("STEP " + currentStep + " OF " + stepLabels.length);
        stepOf.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        HBox stepOfRow = new HBox(stepOf);
        stepOfRow.setAlignment(Pos.CENTER);

        VBox wrapper = new VBox(14, grid, stepOfRow);
        wrapper.setMaxWidth(900);
        return wrapper;
    }

    private static Label buildCircle(int stepNumber, int currentStep) {
        Label circle = new Label();
        circle.setPrefSize(32, 32);
        circle.setMinSize(32, 32);
        circle.setMaxSize(32, 32);
        circle.setAlignment(Pos.CENTER);

        boolean complete = stepNumber < currentStep;
        boolean current = stepNumber == currentStep;

        if (complete) {
            circle.setText("✓");
            circle.setStyle(
                    "-fx-background-color: " + PRIMARY + ";" +
                    "-fx-text-fill: white; -fx-font-weight: bold;" +
                    "-fx-background-radius: 50%;"
            );
        } else if (current) {
            circle.setText(String.valueOf(stepNumber));
            circle.setStyle(
                    "-fx-background-color: " + PRIMARY + ";" +
                    "-fx-text-fill: white; -fx-font-weight: bold;" +
                    "-fx-background-radius: 50%;"
            );
        } else {
            circle.setText(String.valueOf(stepNumber));
            circle.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-text-fill: " + TEXT_MUTED + ";" +
                    "-fx-border-color: " + BORDER + "; -fx-border-width: 1.5; -fx-border-radius: 50%;" +
                    "-fx-background-radius: 50%;"
            );
        }
        return circle;
    }

    private static Label buildLabel(String labelText, int stepNumber, int currentStep) {
        boolean complete = stepNumber < currentStep;
        boolean current = stepNumber == currentStep;
        Label text = new Label(labelText);
        text.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: " + (current ? "bold" : "normal") + ";" +
                "-fx-text-fill: " + (current || complete ? PRIMARY : TEXT_MUTED) + ";" +
                "-fx-padding: 6 0 0 0;"
        );
        return text;
    }
}