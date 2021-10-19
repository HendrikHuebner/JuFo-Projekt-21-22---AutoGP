package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.util.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RoomEditorController {

    @FXML
    public TextField sizeField;
    @FXML
    public ChoiceBox<Unit> sizeUnitChoice;
    private double roomSize = 0.0;

    @FXML
    public void initialize() {
        ObservableList<Unit> items = FXCollections.observableArrayList(Unit.values());
        sizeUnitChoice.setItems(items);
        sizeUnitChoice.setValue(Unit.METRES);
        this.roomSize = 0.0;
    }

    @FXML
    public void onFinish(ActionEvent event) {
        //DO STUFF
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
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
            this.roomSize = value;
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }
}
