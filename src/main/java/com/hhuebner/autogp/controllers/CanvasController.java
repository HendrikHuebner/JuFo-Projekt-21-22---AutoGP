package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.util.Vec2d;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Optional;

public class CanvasController {

    private final CanvasRenderer canvasRenderer;
    private final Camera cam;
    @FXML public Canvas canvas;
    private InputHandler inputHandler;

    public CanvasController(CanvasRenderer canvasRenderer, Camera cam, InputHandler inputHandler) {
        this.canvasRenderer = canvasRenderer;
        this.cam = cam;
        this.inputHandler = inputHandler;
    }

    @FXML
    public void OnCanvasMousePressed(MouseEvent event) {
        switch(this.inputHandler.getTool()) {
            case SELECTION: {
                inputHandler.dragStart = Optional.of(new Vec2d(event.getX(), event.getY()));
                break;
            }
            case MOVE: {
                inputHandler.prevMousePos = new Vec2d(event.getX(), event.getY());
                break;
            }
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
                    this.canvasRenderer.render(this.canvas, this.inputHandler);
                });
                break;
            }
            case MOVE: {
                Vec2d currentMousePos = new Vec2d(event.getX(), event.getY());
                this.cam.move(currentMousePos.sub(inputHandler.prevMousePos).scale(1.0 / this.cam.getZoom()));
                inputHandler.prevMousePos = currentMousePos;
                this.canvasRenderer.render(this.canvas, this.inputHandler);
                break;
            }
        }
    }

    @FXML 
    public void OnCanvasScroll(ScrollEvent event) {
        //remove selection box
        inputHandler.clearSelection();

        double multiplier = event.getDeltaY() / 1000.0;

        this.cam.move(new Vec2d(-event.getX() * multiplier, -event.getY() * multiplier));
        this.cam.zoom(1 + multiplier);
        this.canvasRenderer.render(this.canvas, this.inputHandler);
    }
}
