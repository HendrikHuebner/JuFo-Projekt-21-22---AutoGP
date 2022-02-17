package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.GroundPlan;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.UnitSq;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.options.OptionsHandler;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.widgets.GroundPlanTab;
import com.hhuebner.autogp.ui.widgets.InformationLabel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;


public class MainSceneController {

    private int groundPlanId = 0;
    private final Supplier<Scene> roomEditorScene;

    @FXML public TabPane tabPane;

    @FXML public ChoiceBox<Unit> inputUnitChoice;
    @FXML public ChoiceBox<UnitSq> inputUnitChoiceBaseArea;
    @FXML public ChoiceBox<Unit> outputUnitChoice;

    @FXML public TableView<Room> roomsOverviewTable;
    @FXML private TableColumn<Room, String> nameCol;
    @FXML private TableColumn<Room, String> sizeCol;
    @FXML private TableColumn<Room, String> furnitureCol;

    @FXML public Canvas canvas;
    @FXML public InformationLabel infoLabel;

    private final Camera cam;
    private InputHandler inputHandler;
    private GPEngine engine;
    private Point2D prevMousePos = new Point2D(0, 0);


    public MainSceneController(InputHandler inputHandler, Supplier<Scene> roomEditorScene, GPEngine engine, Camera cam) {
        this.inputHandler = inputHandler;
        this.roomEditorScene = roomEditorScene;
        this.engine = engine;
        this.cam = cam;
    }

    @FXML
    public void initialize() {
        //TableView
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory("size"));
        furnitureCol.setCellValueFactory((param -> {
            StringBuilder sb = new StringBuilder();
            for(FurnitureItem fi : param.getValue().furniture) {
                sb.append(fi.getName());
                sb.append(" ");
            }

            return new ReadOnlyStringWrapper(sb.toString());
        }));

        roomsOverviewTable.setItems(engine.rooms);

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
            this.infoLabel.update(this.inputHandler);
        });

        inputUnitChoice.setOnAction((event) ->  {
            this.infoLabel.update(this.inputHandler);
        });

    }

    @FXML
    public void onAddRoom(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.setResizable(false);
        dialog.setTitle("Raum Hinzufügen");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(this.roomEditorScene.get());
        dialog.showAndWait();
    }

    @FXML
    public void onGenerate(ActionEvent event) {
        Random seedGen = new Random();

        int limit = OptionsHandler.INSTANCE.generationTryLimit.get();
        long start = System.currentTimeMillis();
        int tries = 0;
        long seed = 0;
        int id = this.groundPlanId++;

        this.engine.calculateRoomSizes(inputHandler.gpSize);

        for (int i = 0; i < limit; i++) {
            seed = seedGen.nextLong();
            GroundPlan gp = this.engine.generate(id,"gp", inputHandler.gpSize, 235621015467243245l);

            if (gp != null) {
                tries += i + 1;
                this.engine.groundPlanMap.put(id, gp);
                //AutoGP.logf("Successfully generated after %d iterations using %d as seed", i, seed);
                break;
            }
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        AutoGP.log("seed:", seed, "avg time: ", timeElapsed / 300.0, "avg tries: ", tries / 300.0);

        Tab tab = new GroundPlanTab(id, "Grundriss Nr. " + id, inputHandler);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        inputHandler.clearSelectedComponent();
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

    public void onSetArea(KeyEvent event) {
        String string = ((TextField)event.getSource()).getText();
        double value = 100.0;
        try {
            value = Double.parseDouble(string);
        }
        catch(NumberFormatException e) {}
        finally {
            value = Utility.convertUnitSq(value, this.inputUnitChoiceBaseArea.getValue(), UnitSq.METRES); //internally, everything is in metres
            this.inputHandler.gpSize = value;
        }
    }

    //CANVAS


    @FXML
    public void onCanvasMousePressed(MouseEvent event) throws NonInvertibleTransformException {
        inputHandler.dragStart = Optional.of(new Point2D(event.getX(), event.getY()));
        Point2D mouse = this.cam.getTransform().inverseTransform(event.getX(), event.getY());

        if (this.inputHandler.getTool() == InputHandler.Tool.CURSOR) {
            inputHandler.onCursorClick(mouse.getX(), mouse.getY());

            //update information bar
            if(inputHandler.selectedRoom.isPresent()) {
                InteractableComponent component = inputHandler.selectedComponent.orElse(inputHandler.selectedRoom.get());
                infoLabel.setInformation(component, this.inputHandler);
            } else {
                infoLabel.clear();
            }
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
            inputHandler.clearSelectionBox();
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
        inputHandler.clearSelectionBox();

        double multiplier = 1 + event.getDeltaY() / 1000.0; //event.getDeltaY() = mousewheel scroll

        Affine affine = this.cam.getTransform();
        affine.prependTranslation(-event.getX(), -event.getY());
        affine.prependScale(multiplier, multiplier);
        affine.prependTranslation(event.getX(), event.getY());
    }
}
