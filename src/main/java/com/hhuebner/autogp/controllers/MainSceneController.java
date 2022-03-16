package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.GroundPlan;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.UnitSq;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.options.OptionsHandler;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.widgets.ButtonCell;
import com.hhuebner.autogp.ui.widgets.GroundPlanTab;
import com.hhuebner.autogp.ui.widgets.InformationLabel;
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
    private final Supplier<Scene> furnitureSelectionScene;
    private final MenuBarHandler menuBarhandler;

    @FXML public TabPane tabPane;

    @FXML public ChoiceBox<UnitSq> inputUnitChoiceBaseArea;
    @FXML public ChoiceBox<Unit> outputUnitChoice;

    @FXML private ScrollPane tableScrollPane;

    public TableView<Room> roomsOverviewTable = new TableView<>();
    private TableColumn<Room, String> nameCol = new TableColumn<>("Name");
    private TableColumn<Room, String> typeCol = new TableColumn<>("Typ");
    private TableColumn<Room, String> sizeCol = new TableColumn<>("Fläche");
    private TableColumn<Room, Void> removeButtonCol = new TableColumn<>("Löschen");

    public TableView<FurnitureItem> furnitureOverviewTable = new TableView<>();
    private TableColumn<FurnitureItem, String> fSymbolCol = new TableColumn<>("Typ");
    private TableColumn<FurnitureItem, String> fNameCol = new TableColumn<>("Name");
    private TableColumn<FurnitureItem, String> fSizeCol = new TableColumn<>("Größe");
    private TableColumn<FurnitureItem, Void> fRemoveButtonCol = new TableColumn<>("Löschen");

    @FXML public Canvas canvas;
    @FXML public InformationLabel infoLabel;
    @FXML public MenuBar menuBar;

    private final Camera cam;
    private InputHandler inputHandler;
    private GPEngine engine;
    private Point2D prevMousePos = new Point2D(0, 0);


    public MainSceneController(InputHandler inputHandler, Supplier<Scene> roomEditorScene, Supplier<Scene> furnitureSelectionScene, GPEngine engine, Camera cam, MenuBarHandler menuBarHandler) {
        this.inputHandler = inputHandler;
        this.roomEditorScene = roomEditorScene;
        this.furnitureSelectionScene = furnitureSelectionScene;
        this.engine = engine;
        this.cam = cam;
        this.menuBarhandler = menuBarHandler;
    }

    @FXML
    public void initialize() {
        //TableViews
        nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        sizeCol.setCellValueFactory(new PropertyValueFactory("size"));
        typeCol.setCellValueFactory(new PropertyValueFactory("type"));
        removeButtonCol.setCellFactory(p -> new ButtonCell<>(roomsOverviewTable));
        roomsOverviewTable.getColumns().addAll(nameCol, typeCol, sizeCol, removeButtonCol);

        roomsOverviewTable.setItems(engine.rooms);
        tableScrollPane.setContent(roomsOverviewTable);

        fNameCol.setCellValueFactory(new PropertyValueFactory("displayName"));
        fSizeCol.setCellValueFactory(new PropertyValueFactory("displaySize"));
        fSymbolCol.setCellValueFactory(new PropertyValueFactory("displayType"));
        fRemoveButtonCol.setCellFactory(p -> new ButtonCell<>(furnitureOverviewTable));
        furnitureOverviewTable.getColumns().addAll(fNameCol, fSymbolCol, fSizeCol, fRemoveButtonCol);

        //Choice boxes
        ObservableList<Unit> units = FXCollections.observableArrayList(Unit.values());
        ObservableList<UnitSq> unitsSq = FXCollections.observableArrayList(UnitSq.values());
        outputUnitChoice.setItems(units);
        inputUnitChoiceBaseArea.setItems(unitsSq);

        outputUnitChoice.setValue(Unit.METRES);
        inputUnitChoiceBaseArea.setValue(UnitSq.METRES);
        
        outputUnitChoice.setOnAction((event) ->  {
            this.inputHandler.displayUnit = outputUnitChoice.getValue();
            this.infoLabel.update(this.inputHandler);
        });
        inputUnitChoiceBaseArea.setOnAction(event -> {
            this.inputHandler.gpSizeUnit = inputUnitChoiceBaseArea.getValue();
            this.engine.updateSizes(inputHandler.gpSize, inputHandler.gpSizeUnit);
        });

        this.menuBarhandler.initialize(menuBar);
    }

    @FXML
    public void onAddRoom(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);

        //select scene to show
        if(this.inputHandler.selectedRoom.isPresent()) {
            dialog.setTitle("Gegenstand Hinzufügen");
            dialog.setScene(this.furnitureSelectionScene.get());
        } else {
            dialog.setTitle("Raum Hinzufügen");
            dialog.setScene(this.roomEditorScene.get());
        }

        dialog.showAndWait();
    }

    @FXML
    public void onGenerate(ActionEvent event) {
        Random seedGen = new Random();

        int limit = 10000;
        OptionsHandler.INSTANCE.generationTryLimit.get();
        long start = System.currentTimeMillis();
        int tries = 0;
        long seed = 0;
        int id = this.getNextGPID();

        this.engine.calculateRoomSizes(inputHandler.gpSize, inputHandler.gpSizeUnit);

        GroundPlan gp = null;

        for (int i = 0; i < limit; i++) {
            seed = seedGen.nextLong();
            gp = this.engine.generate(id, "gp", inputHandler.gpSize, seed + 1);

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

        addGroundPlanTab(gp);
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
            value = Utility.convertUnit(value, this.outputUnitChoice.getValue(), Unit.METRES); //internally, everything is in metres
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
            this.engine.updateSizes(inputHandler.gpSize, inputHandler.gpSizeUnit);
        }
    }


    //CANVAS


    @FXML
    public void onCanvasMousePressed(MouseEvent event) throws NonInvertibleTransformException {
        inputHandler.dragStart = Optional.of(new Point2D(event.getX(), event.getY()));
        Point2D mouse = this.cam.getTransform().inverseTransform(event.getX(), event.getY());

        if (this.inputHandler.getTool() == InputHandler.Tool.CURSOR) {
            inputHandler.onCursorPress(mouse.getX(), mouse.getY());

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
        affine.prependScale(multiplier,  multiplier);
        affine.prependTranslation(event.getX(), event.getY());
    }

    public void onSelectRoom(RoomComponent component) {
        this.furnitureOverviewTable.setItems((ObservableList<FurnitureItem>) component.room.furniture);
        this.tableScrollPane.setContent(this.furnitureOverviewTable);
    }

    public void onDeselectRoom() {
        this.tableScrollPane.setContent(this.roomsOverviewTable);
    }

    public void addGroundPlanTab(GroundPlan groundPlan) {
        Tab tab = new GroundPlanTab(groundPlan.getGroundPlanID(), "Grundriss Nr. " + groundPlan.getGroundPlanID(), this.inputHandler);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public int getNextGPID() {
        return ++this.groundPlanId;
    }
}
