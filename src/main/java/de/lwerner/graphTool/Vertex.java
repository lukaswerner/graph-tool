package de.lwerner.graphTool;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Set;

import static de.lwerner.graphTool.GraphTool.unitSize;

public class Vertex {

    public static final double CIRCLE_RADIUS = 1d;
    private static final Color FILL_COLOR = Color.DARKSLATEBLUE;
    private static final Color STROKE_COLOR = Color.WHITE;
    private static final Color SELECTED_STROKE_COLOR = Color.LIGHTCORAL;

    private static int vertexCount;

    // UI
    private final StackPane ui;
    private final Circle circle;
    private final Text text;

    // Event helpers
    private double orgSceneX;
    private double orgSceneY;
    private double orgLayoutX;
    private double orgLayoutY;

    // Vertex properties
    private final Set<Edge> edges;

    public Vertex(double unitsX, double unitsY) {
        this.circle = new Circle(CIRCLE_RADIUS * unitSize, FILL_COLOR);
        this.text = new Text("" + ++vertexCount);
        this.ui = new StackPane();

        edges = new HashSet<>();

        buildUi(unitsX, unitsY);
    }

    private void buildUi(double unitsX, double unitsY) {
        ui.setLayoutX(unitsX * unitSize);
        ui.setLayoutY(unitsY * unitSize);

        circle.setStroke(STROKE_COLOR);
        circle.setStrokeWidth(1);

        text.setFont(Font.font(0.5 * unitSize));
        text.setFill(Color.WHITE);

        circle.setCursor(Cursor.HAND);
        text.setCursor(Cursor.HAND);

        text.setOnMousePressed(this::mousePressed);
        text.setOnMouseDragged(this::mouseDragged);
        text.setOnMouseEntered(this::mouseEntered);
        text.setOnMouseExited(this::mouseExited);
        circle.setOnMousePressed(this::mousePressed);
        circle.setOnMouseDragged(this::mouseDragged);
        circle.setOnMouseEntered(this::mouseEntered);
        circle.setOnMouseExited(this::mouseExited);

        ui.getChildren().addAll(circle, text);
    }

    public StackPane getUi() {
        return ui;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            circle.setStroke(SELECTED_STROKE_COLOR);
        } else {
            circle.setStroke(STROKE_COLOR);
        }
    }

    public void setDashed(boolean dashed) {
        if (dashed) {
            circle.getStrokeDashArray().addAll(3d);
        } else {
            circle.getStrokeDashArray().clear();
        }
    }

    private void mouseEntered(MouseEvent event) {
        if (GraphTool.getState() == GraphTool.ApplicationState.NEW_VERTEX && GraphTool.getNewVertex() != this) {
            setSelected(true);
        }
    }

    private void mouseExited(MouseEvent event) {
        if (GraphTool.getState() == GraphTool.ApplicationState.NEW_VERTEX && GraphTool.getNewVertex() != this) {
            setSelected(false);
        }
    }

    private void mousePressed(MouseEvent event) {
        // TODO: Connect two vertices
        if (GraphTool.getState() == GraphTool.ApplicationState.IDLE) {
            orgSceneX = event.getSceneX();
            orgSceneY = event.getSceneY();
            orgLayoutX = ui.getLayoutX();
            orgLayoutY = ui.getLayoutY();

            ui.toFront();
        } else if (GraphTool.getState() == GraphTool.ApplicationState.NEW_VERTEX && GraphTool.getNewVertex() != this) {
            GraphTool.connectNewVertex(this);
            setSelected(false);
        }
    }

    private void mouseDragged(MouseEvent event) {
        double offsetX = event.getSceneX() - orgSceneX;
        double offsetY = event.getSceneY() - orgSceneY;

        double newX = orgLayoutX + offsetX;
        double newY = orgLayoutY + offsetY;

        if (event.isShiftDown()) {
            double newXUnits = Math.round(newX / unitSize);
            double newYUnits = Math.round(newY / unitSize);
            newX = newXUnits * unitSize;
            newY = newYUnits * unitSize;
        }

        ui.relocate(newX, newY);

        ui.toFront();
    }

    public static void decrement() {
        vertexCount--;
    }

}
