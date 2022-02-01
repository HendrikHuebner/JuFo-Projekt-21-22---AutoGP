package com.hhuebner.autogp.options;

public class DoubleOption extends Option<Double> {

    public DoubleOption(String key, double defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public void setValue(String value) {
        if(value.isEmpty()) {
            this.value = this.defaultValue;
        } else {
            this.value = Double.valueOf(value);
        }
    }
}
