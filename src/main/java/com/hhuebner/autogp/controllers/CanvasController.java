package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.util.Vec2d;
import com.hhuebner.autogp.ui.Camera;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.Optional;

public class CanvasController {

    private final Camera cam;
    @FXML public Canvas canvas;
    private InputHandler inputHandler;
    private GPEngine engine;

    public CanvasController(Camera cam, InputHandler inputHandler, GPEngine engine) {
        this.cam = cam;
        this.inputHandler = inputHandler;
        this.engine = engine;
    }

    @FXML
    public void OnCanvasMousePressed(MouseEvent event) throws NonInvertibleTransformException {
        switch(this.inputHandler.getTool()) {
            case SELECTION: {
                inputHandler.dragStart = Optional.of(new Vec2d(event.getX(), event.getY()));
                break;
            }
            case MOVE: {
                inputHandler.prevMousePos = new Vec2d(event.getX(), event.getY());
                break;
            }
            case CURSOR:
                Point2D mouse = this.cam.getTransform().inverseTransform(event.getX(), event.getY());
                inputHandler.onCursorClick(mouse.getX(), mouse.getY());
                break;
        }
    }

    @FXML
    public void OnCanvasMouseReleased(MouseEvent event) {
        switch(this.inputHandler.getTool()) {
            case SELECTION: {
                AutoGP.log("release", event.getX(), event.getY());
                inputHandler.dragEnd = Optional.of(new Vec2d(event.getX(), event.getY()));
                inputHandler.handleSelection();
                inputHandler.clearSelection();
                break;
            }
        }
    }

    @FXML
    public void OnCanvasMouseDrag(MouseEvent event) {
        switch(this.inputHandler.getTool()) {
            case SELECTION: {
                inputHandler.dragStart.ifPresent(start -> {
                    inputHandler.dragEnd = Optional.of(new Vec2d(event.getX(), event.getY()));
                });
                break;
            }
            case MOVE: {
                Vec2d currentMousePos = new Vec2d(event.getX(), event.getY());
                this.cam.move(currentMousePos.sub(inputHandler.prevMousePos).scale(1.0 / this.cam.getScaleX()));
                inputHandler.prevMousePos = currentMousePos;
                break;
            }
        }
    }

    @FXML 
    public void OnCanvasScroll(ScrollEvent event) throws NonInvertibleTransformException {
        //remove selection box
        inputHandler.clearSelection();

        double multiplier = event.getDeltaY() / 1000.0;

        this.cam.move(new Vec2d(-event.getX() * multiplier, -event.getY() * multiplier));
        this.cam.zoom(1 + multiplier);
    }
}
