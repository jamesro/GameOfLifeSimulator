package com.james;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MainView extends VBox {

    private Canvas canvas;
    private Simulation simulation;
    private InfoBar infoBar;

    private Affine affine;
    
    private int drawMode = Simulation.ALIVE;

    public MainView() {
        this.canvas = new Canvas(400, 400);
        this.canvas.setOnMousePressed(this::handleDraw);
        this.canvas.setOnMouseDragged(this::handleDraw);
        this.canvas.setOnMouseMoved(this::handleMoved);
        
        Toolbar toolbar = new Toolbar(this);

        this.infoBar = new InfoBar();
        this.infoBar.setDrawMode(this.drawMode);
        this.infoBar.setCursorPosition(0, 0);

        Pane spacer = new Pane();
        spacer.setMinSize(0, 0);
        spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(toolbar, this.canvas, spacer, infoBar);

        this.affine = new Affine();
        this.affine.appendScale(400 / 10, 400 /10);
        this.simulation = new Simulation(10, 10);

        this.setOnKeyPressed(this::onKeyPressed);
    }

    private void handleMoved(MouseEvent mouseEvent) {
        Point2D simCoord = getSimulationCoordinates(mouseEvent);
        int simX = (int) simCoord.getX();
        int simY = (int) simCoord.getY();

        this.infoBar.setCursorPosition(simX, simY);
    }

    private Point2D getSimulationCoordinates(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        try {
            Point2D simCoord = this.affine.inverseTransform(mouseX, mouseY);
            return simCoord;
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException("Non-invertible transform");
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.D) {
            drawMode = Simulation.ALIVE;
        } else if(keyEvent.getCode() == KeyCode.E) {
            drawMode = Simulation.DEAD;
        }
    }

    private void handleDraw(MouseEvent mouseEvent) {

        Point2D simCoord = getSimulationCoordinates(mouseEvent);
        int simX = (int) simCoord.getX();
        int simY = (int) simCoord.getY();

        this.simulation.setState(simX, simY, drawMode);
        this.draw();
    }

    public void draw() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setTransform(this.affine);
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0, 0, 400, 400);

        g.setFill(Color.BLACK);
        for (int x = 0; x < this.simulation.width; x++) {
            for (int y = 0; y < this.simulation.height; y++) {
                if (this.simulation.getState(x, y) == Simulation.ALIVE) {
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        // draw gridlines
        g.setStroke(Color.GRAY);
        g.setLineWidth(0.05f);
        for (int x = 0; x <= this.simulation.width; x++) {
            g.strokeLine(x, 0, x, 10);
        }
        for (int y = 0; y <= this.simulation.height; y++) {
            g.strokeLine(0, y, 10, y);

        }
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public void setDrawMode(int value) {
        this.drawMode = value;
        this.infoBar.setDrawMode(value);
    }
}
