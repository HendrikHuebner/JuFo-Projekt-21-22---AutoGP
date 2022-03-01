package com.hhuebner.autogp.options;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

public class BoolOption extends Option<Boolean> {

    public BoolOption(String parentMenu, String key, boolean defaultValue) {
        super(parentMenu, key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Boolean.valueOf(value);
        }
    }

    @Override
    public MenuItem createMenuItem() {
        CheckMenuItem menuItem = new CheckMenuItem(this.key);
        menuItem.setSelected(this.value);
        menuItem.setOnAction(e -> this.value = menuItem.isSelected());
        return menuItem;
    }
}
