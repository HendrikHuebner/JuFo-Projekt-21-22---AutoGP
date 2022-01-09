package com.hhuebner.autogp.core;

import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.control.Label;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class InformationLabel extends Label {

    private double width = 0.0;
    private double height = 0.0;
    private String name = "";
    private Unit unit = Unit.METRES;

    public void setInformation(InputHandler handler, double width, double height, String name) {
        this.width = width;
        this.height = height; 
        this.name = name;
        update(handler);
    }

    public void clear() {
        this.setText("");
    }


    public void update(InputHandler handler) {
        Unit unit = handler.scalingUnit.second;
        this.setText(String.format("Object name: %s  width: %.2f%s  height %.2f%s",
                name, Utility.pixelsToUnit(width, handler) / CELL_SIZE, unit.toString(), Utility.pixelsToUnit(height, handler) / CELL_SIZE, unit.toString()));
    }

}
