package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.engine.RoomType;
import com.hhuebner.autogp.core.util.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RoomEditorController {

    @FXML
    public ChoiceBox<Unit> sizeUnitChoice;
    @FXML
    public ChoiceBox<RoomType> roomTypeChoice;
    @FXML
    public ListView furnitureListDisplay;
    @FXML
    public TextField nameTextField;

    private Room.Builder roomBuilder;
    private final GPEngine engine;
    

    public RoomEditorController(GPEngine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        this.roomBuilder = new Room.Builder();

        //Choice boxes
        ObservableList<Unit> units = FXCollections.observableArrayList(Unit.values());
        sizeUnitChoice.setItems(units);
        sizeUnitChoice.setValue(Unit.METRES);
        sizeUnitChoice.setOnAction((event) ->  this.roomBuilder.setUnit(sizeUnitChoice.getValue()));

        ObservableList<RoomType> roomTypes = FXCollections.observableArrayList(RoomType.values());
        roomTypeChoice.setItems(roomTypes);

        roomTypeChoice.setOnAction((event) ->  {
            this.nameTextField.setText(roomTypeChoice.getValue().name);
            this.furnitureListDisplay.getItems().clear();

            this.roomBuilder.setType(roomTypeChoice.getValue())
                .setName(roomTypeChoice.getValue().name);

            for(FurnitureItem item : this.roomBuilder.type.defaultFurniture) {
                this.roomBuilder.addFurnitureItem(item);
                this.furnitureListDisplay.getItems().add(item.getName());
            }
        });
    }

    @FXML
    public void onFinish(ActionEvent event) {
        if(this.roomBuilder.canBuild()) {
            //Add room
            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
            this.engine.addRoom(this.roomBuilder.build());
        } else {
            AutoGP.log("Can't add room, not all elements have been added", roomBuilder.type, roomBuilder.size);
        }
    }

    @FXML
    public void onSetSize(KeyEvent event) {
        String string = ((TextField)event.getSource()).getText();
        double value = 1.0;
        try {
            value = Double.parseDouble(string);
        }
        catch(NumberFormatException e) {}
        finally {
            this.roomBuilder.setSize(value);
        }
    }


    @FXML
    public void onSetName(KeyEvent event) {
        String string = ((TextField)event.getSource()).getText();
        this.roomBuilder.setName(string);
    }

    @FXML
    public void onCancel(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }
}
