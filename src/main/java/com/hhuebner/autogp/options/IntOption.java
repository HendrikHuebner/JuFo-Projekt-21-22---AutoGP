package com.hhuebner.autogp.options;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

public class IntOption extends Option<Integer> {

    public IntOption(String parentMenu, String key, int defaultValue) {
        super(parentMenu, key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Integer.valueOf(value);
        }
    }

    @Override
    public MenuItem createMenuItem() {
        return new CheckMenuItem(this.key);
    }
}
