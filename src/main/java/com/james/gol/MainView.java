package com.james.gol;

import com.james.gol.model.Board;
import com.james.gol.model.BoundedBoard;
import com.james.gol.model.CellState;
import com.james.gol.model.StandardRule;
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

    public static final int EDITING = 0;
    public static final int SIMULATING = 1;

    private CellState drawMode = CellState.ALIVE;
    private int applicationState = EDITING;

    private Canvas canvas;
    private Affine affine;

    private InfoBar infoBar;

    private Simulation simulation;
    private Board initialBoard;

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
        this.initialBoard = new BoundedBoard(10, 10);
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
            drawMode = CellState.ALIVE;
        } else if(keyEvent.getCode() == KeyCode.E) {
            drawMode = CellState.DEAD;
        }
    }

    private void handleDraw(MouseEvent mouseEvent) {
        if (this.applicationState == SIMULATING) {
            return;
        }

        Point2D simCoord = getSimulationCoordinates(mouseEvent);
        int simX = (int) simCoord.getX();
        int simY = (int) simCoord.getY();

        this.initialBoard.setState(simX, simY, drawMode);
        this.draw();
    }

    public void draw() {
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setTransform(this.affine);
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0, 0, 400, 400);

        if (this.applicationState == EDITING) {
            drawSimulation(this.initialBoard);
        } else {
            drawSimulation(this.simulation.getBoard());
        }

        // draw gridlines
        g.setStroke(Color.GRAY);
        g.setLineWidth(0.05f);
        for (int x = 0; x <= this.initialBoard.getWidth(); x++) {
            g.strokeLine(x, 0, x, 10);
        }
        for (int y = 0; y <= this.initialBoard.getHeight(); y++) {
            g.strokeLine(0, y, 10, y);

        }
    }

    private void drawSimulation(Board simulationToDraw) {
        GraphicsContext g = this.canvas.getGraphicsContext2D();

        g.setFill(Color.BLACK);
        for (int x = 0; x < simulationToDraw.getWidth(); x++) {
            for (int y = 0; y < simulationToDraw.getHeight(); y++) {
                if (simulationToDraw.getState(x, y) == CellState.ALIVE) {
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public void setDrawMode(CellState value) {
        this.drawMode = value;
        this.infoBar.setDrawMode(value);
    }

    public void setApplicationState(int applicationState) {
        if (applicationState == this.applicationState) {
            return;
        }

        if (applicationState == SIMULATING) {
            this.simulation = new Simulation(this.initialBoard, new StandardRule());
        }
        this.applicationState = applicationState;
    }

    public int getApplicationState() {
        return applicationState;
    }
}
