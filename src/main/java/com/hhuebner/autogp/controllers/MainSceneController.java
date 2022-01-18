package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.UnitSq;
import com.hhuebner.autogp.core.util.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.Supplier;


public class MainSceneController {

    private final Supplier<Scene> roomEditorScene;

    @FXML public CanvasController canvasController;
    @FXML public ChoiceBox<Unit> inputUnitChoice;
    @FXML public ChoiceBox<UnitSq> inputUnitChoiceBaseArea;
    @FXML public ChoiceBox<Unit> outputUnitChoice;
    @FXML public TableView roomsOverviewTable;
    @FXML private TableColumn<Room, String> nameCol;
    @FXML private TableColumn<Room, String> sizeCol;
    @FXML private TableColumn<Room, String> furnitureCol;

    private final InputHandler inputHandler;
    private final GPEngine engine;

    public MainSceneController(InputHandler inputHandler, Supplier<Scene> roomEditorScene, GPEngine engine) {
        this.inputHandler = inputHandler;
        this.roomEditorScene = roomEditorScene;
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        //TableView
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory("size"));

        //Choice boxes
        ObservableList<Unit> units = FXCollections.observableArrayList(Unit.values());
        ObservableList<UnitSq> unitsSq = FXCollections.observableArrayList(UnitSq.values());
        inputUnitChoice.setItems(units);
        outputUnitChoice.setItems(units);
        inputUnitChoiceBaseArea.setItems(unitsSq);

        inputUnitChoice.setValue(Unit.METRES);
        outputUnitChoice.setValue(Unit.METRES);
        inputUnitChoiceBaseArea.setValue(UnitSq.METRES);
        
        outputUnitChoice.setOnAction((event) ->  {
            this.inputHandler.displayUnit = outputUnitChoice.getValue();
            this.canvasController.infoLabel.update(this.inputHandler);
        });

        inputUnitChoice.setOnAction((event) ->  {
            this.canvasController.infoLabel.update(this.inputHandler);
        });
    }

    @FXML
    public void onAddRoom(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(this.roomEditorScene.get());
        dialog.showAndWait();
    }

    @FXML
    public void onGenerate(ActionEvent event) {
        this.engine.generate();
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
            value = Utility.convertUnit(value, this.inputUnitChoice.getValue(), Unit.METRES); //internally, everything is in metres 
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
