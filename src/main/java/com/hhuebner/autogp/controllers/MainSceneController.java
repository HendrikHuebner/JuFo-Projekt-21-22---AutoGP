package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class MainSceneController {

    @FXML
    public CanvasController canvasController;
    private InputHandler inputHandler;

    public MainSceneController(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @FXML
    public void OnGenerate(ActionEvent event) {
    }

    @FXML
    public void OnClickMoveTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.MOVE);
    }

    @FXML
    public void OnClickCursorTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.CURSOR);
    }

    @FXML
    public void OnClickSelectTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.SELECTION);
    }
}
