package com.hhuebner.autogp.options;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

public class DoubleOption extends Option<Double> {

    public DoubleOption(String parentMenu, String key, double defaultValue) {
        super(parentMenu, key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Double.valueOf(value);
        }
    }

    @Override
    public MenuItem createMenuItem() {
        return new CheckMenuItem(this.key);
    }
}
