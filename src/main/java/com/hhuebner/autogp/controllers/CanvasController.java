package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.util.Vec2d;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Optional;

public class CanvasController {

    private final CanvasRenderer canvasRenderer;
    private final Camera cam;
    @FXML public Canvas canvas;

    private Optional<Vec2d> dragStart = Optional.empty();
    private Optional<Vec2d> dragEnd = Optional.empty();

    public CanvasController(CanvasRenderer canvasRenderer, Camera cam) {
        this.canvasRenderer = canvasRenderer;
        this.cam = cam;
    }

    @FXML
    public void OnCanvasMousePressed(MouseEvent event) {
        this.dragStart = Optional.of(new Vec2d(event.getX(), event.getY()));
    }

    @FXML
    public void OnCanvasMouseReleased(MouseEvent event) {
        AutoGP.log("release", event.getX(), event.getY());
        if(dragStart.isPresent() && dragEnd.isPresent()) {
            AutoGP.logf("Selected area: x1: %f, y1: %f, x2: %f, y2: %f",
                    dragStart.get().x, dragStart.get().y, dragEnd.get().x, dragEnd.get().y);
        }
        this.dragStart = Optional.empty();
        this.dragEnd = Optional.empty();
    }

    @FXML
    public void OnCanvasMouseDrag(MouseEvent event) {
        dragStart.ifPresent(start -> {
            this.dragEnd = Optional.of(new Vec2d(event.getX(), event.getY()));
            this.canvasRenderer.render(this.canvas, this);
        });
    }

    @FXML 
    public void OnCanvasScroll(ScrollEvent event) {
        //remove selection box
        this.dragStart = Optional.empty();
        this.dragEnd = Optional.empty();

        double multiplier = event.getDeltaY() / 1000.0;

        this.cam.move(new Vec2d(- event.getX() * multiplier,- event.getY() * multiplier));
        this.cam.zoom(1 + multiplier);
        this.canvasRenderer.render(this.canvas, this);
    }

    public boolean hasSelection() {
        return this.dragStart.isPresent() && this.dragEnd.isPresent();
    }

    public double[] getSelection() {
        return new double[]{this.dragStart.get().x, this.dragStart.get().y, this.dragEnd.get().x, this.dragEnd.get().y};
    }
}
