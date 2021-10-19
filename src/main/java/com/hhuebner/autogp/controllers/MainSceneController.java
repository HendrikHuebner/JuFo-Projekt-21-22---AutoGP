package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.util.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.Supplier;


public class MainSceneController {

    private final Supplier<Scene> roomEditorScene;
    @FXML
    public CanvasController canvasController;
    @FXML
    public ChoiceBox<Unit> inputUnitChoice;
    @FXML
    public ChoiceBox<Unit> outputUnitChoice;
    private final InputHandler inputHandler;

    public MainSceneController(InputHandler inputHandler, Supplier<Scene> roomEditorScene) {
        this.inputHandler = inputHandler;
        this.roomEditorScene = roomEditorScene;
    }

    @FXML
    public void initialize() {
        ObservableList<Unit> items = FXCollections.observableArrayList(Unit.values());
        inputUnitChoice.setItems(items);
        outputUnitChoice.setItems(items);

        inputUnitChoice.setValue(Unit.METRES);
        outputUnitChoice.setValue(Unit.METRES);

        inputUnitChoice.setOnAction((event) ->  this.inputHandler.scalingUnit.first = inputUnitChoice.getValue());
        outputUnitChoice.setOnAction((event) ->  this.inputHandler.scalingUnit.second = outputUnitChoice.getValue());
    }

    @FXML
    public void onAddRoom(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        //dialog.initOwner(parent);
        dialog.setScene(this.roomEditorScene.get());
        dialog.showAndWait();
    }

    @FXML
    public void onGenerate(ActionEvent event) {
    }

    @FXML
    public void onSetScale(KeyEvent event) {
        String string = ((TextField)event.getSource()).getText();
        double value = 1.0;
        try {
            value = Double.parseDouble(string);
        }
        catch(NumberFormatException e) {}
        finally {
            this.inputHandler.globalScale = value;
        }
    }

    @FXML
    public void onClickMoveTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.MOVE);
    }

    @FXML
    public void onClickCursorTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.CURSOR);
    }

    @FXML
    public void onClickSelectTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.SELECTION);
    }

    public void onClickRulerTool(ActionEvent actionEvent) {
        this.inputHandler.setTool(InputHandler.Tool.RULER);
    }
}
