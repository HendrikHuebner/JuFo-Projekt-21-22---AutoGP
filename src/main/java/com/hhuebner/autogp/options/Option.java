package com.hhuebner.autogp.options;

import javafx.scene.control.MenuItem;

public abstract class Option<T> {

    protected final String parentMenu;
    protected final String key;
    protected final T defaultValue;
    protected T value;

    public Option(String parentMenu, String key, T defaultValue) {
        this.parentMenu = parentMenu;
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getParentMenu() {
        return parentMenu;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void parse(String value) {
        if(value == null || value.isEmpty())
            this.value = this.defaultValue;
        else
            this.setValue(value);
    };

    public abstract void setValue(String value);

    public abstract MenuItem createMenuItem();

    public T get() {
        return value;
    }

}
