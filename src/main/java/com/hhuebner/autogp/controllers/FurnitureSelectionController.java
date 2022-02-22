package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.component.furniture.FurnitureItems;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class FurnitureSelectionController {

    @FXML private ChoiceBox<FurnitureItem> furnitureChoice;
    @FXML private Canvas previewCanvas;
    @FXML private TextField nameTextField;

    private InputHandler inputHandler;

    public FurnitureSelectionController(InputHandler handler) {
        this.inputHandler = handler;
    }

    @FXML
    public void initialize() {
        this.furnitureChoice.setItems(FXCollections.observableList(FurnitureItems.getItems()));
        this.furnitureChoice.setValue(FurnitureItems.ARMCHAIR);
        this.updateCanvas(FurnitureItems.ARMCHAIR);

        this.furnitureChoice.setOnAction(e -> {
            FurnitureItem fi = furnitureChoice.getValue();
            this.nameTextField.setText(fi.toString());
            FurnitureSelectionController.this.updateCanvas(fi);
        });
    }

    private void updateCanvas(FurnitureItem fi) {
        final double margin = 15.0;
        GraphicsContext ctx = this.previewCanvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, this.previewCanvas.getWidth(), this.previewCanvas.getHeight());

        double scale = Math.min(this.previewCanvas.getWidth() / fi.getWidth(),
                this.previewCanvas.getHeight() / fi.getHeight());

        fi.getRenderer().render(ctx, fi,
                margin + (this.previewCanvas.getWidth() - scale * fi.getWidth()) / 2,
                margin + (this.previewCanvas.getHeight() - scale * fi.getHeight()) / 2,
                scale * fi.getWidth() - 2 * margin, scale * fi.getHeight() - 2 * margin);
    }

    @FXML
    public void onFinish(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        this.inputHandler.selectedRoom.ifPresent(r ->
            r.room.furniture.add(this.furnitureChoice.getValue()));
    }


    @FXML
    public void onCancel(ActionEvent event) {
        ((Stage)((Button)event.getSource()).getScene().getWindow()).close();
    }

    public void onSetName(KeyEvent event) {
        //String string = ((TextField)event.getSource()).getText();
    }
}
