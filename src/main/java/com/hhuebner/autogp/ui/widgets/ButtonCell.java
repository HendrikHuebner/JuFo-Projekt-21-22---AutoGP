package com.hhuebner.autogp.ui.widgets;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

public class ButtonCell<S, T> extends TableCell<S, T> {

        private final Button btn = new Button("X");

        public ButtonCell(TableView table) {

            btn.setOnAction((ActionEvent event) -> {
                table.getItems()
                        .remove(this.getTableRow().getIndex());
            });
        }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            setGraphic(null);
        } else {
            setGraphic(btn);
        }
    }
}
