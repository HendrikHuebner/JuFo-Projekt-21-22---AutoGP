package com.hhuebner.autogp.options;

public class BoolOption extends Option<Boolean> {

    public BoolOption(String key, boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Boolean.valueOf(value);
        }
    }
}
