package com.hhuebner.autogp.options;

public class IntOption extends Option<Integer> {

    public IntOption(String key, int defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Integer.valueOf(value);
        }
    }
}
