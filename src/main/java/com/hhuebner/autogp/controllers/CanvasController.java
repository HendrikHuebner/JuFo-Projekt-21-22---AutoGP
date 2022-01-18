package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.InformationLabel;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.ui.Camera;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.Optional;

public class CanvasController {

    private final Camera cam;
    @FXML public Canvas canvas;
    @FXML public InformationLabel infoLabel;
    private InputHandler inputHandler;
    private GPEngine engine;
    private Point2D prevMousePos = new Point2D(0, 0);

    public CanvasController(Camera cam, InputHandler inputHandler, GPEngine engine) {
        this.cam = cam;
        this.inputHandler = inputHandler;
        this.engine = engine;
    }

    @FXML
    public void onCanvasMousePressed(MouseEvent event) throws NonInvertibleTransformException {
        inputHandler.dragStart = Optional.of(new Point2D(event.getX(), event.getY()));
        Point2D mouse = this.cam.getTransform().inverseTransform(event.getX(), event.getY());

        if (this.inputHandler.getTool() == InputHandler.Tool.CURSOR) {
            inputHandler.onCursorClick(mouse.getX(), mouse.getY());

            //update information bar
            Optional<InteractableComponent> component = inputHandler.getSelectedComponent();
            component.ifPresent(c -> infoLabel.setInformation(c, this.inputHandler));

            if(!component.isPresent()) infoLabel.clear();
        }

        this.prevMousePos = mouse;
    }

    @FXML
    public void onCanvasMouseReleased(MouseEvent event) throws NonInvertibleTransformException {
        if (this.inputHandler.getTool() == InputHandler.Tool.SELECTION) {
            inputHandler.dragEnd = Optional.of(new Point2D(event.getX(), event.getY()));

            Point2D startAbs = this.cam.getTransform().inverseTransform(inputHandler.dragStart.get());
            Point2D endAbs = this.cam.getTransform().inverseTransform(inputHandler.dragEnd.get());
            inputHandler.handleSelection(startAbs.getX(), startAbs.getY(), endAbs.getX(), endAbs.getY());
            inputHandler.clearSelection();
        }
     }

    @FXML
    public void onCanvasMouseDrag(MouseEvent event) throws NonInvertibleTransformException {
        Point2D mouse = this.cam.getTransform().inverseTransform(event.getX(), event.getY());

        inputHandler.dragStart.ifPresent(start -> 
            inputHandler.dragEnd = Optional.of(new Point2D(event.getX(), event.getY())));
        
        switch (this.inputHandler.getTool()) {
            case MOVE ->
                this.cam.move(mouse.subtract(this.prevMousePos));
            case CURSOR ->
                this.inputHandler.handleCursorDrag(this.prevMousePos.getX(), prevMousePos.getY(), mouse.getX(), mouse.getY());
        }

        this.prevMousePos = this.cam.getTransform().inverseTransform(event.getX(), event.getY());
    }

    @FXML 
    public void onCanvasScroll(ScrollEvent event) {
        //remove selection box
        inputHandler.clearSelection();

        double multiplier = 1 + event.getDeltaY() / 1000.0; //event.getDeltaY() = mousewheel scroll

        Affine affine = this.cam.getTransform();
        affine.prependTranslation(-event.getX(), -event.getY());
        affine.prependScale(multiplier, multiplier);
        affine.prependTranslation(event.getX(), event.getY());
    }
}
