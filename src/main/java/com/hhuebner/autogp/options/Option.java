package com.hhuebner.autogp.options;

public abstract class Option<T> {

    protected final String key;
    protected final T defaultValue;
    protected T value;

    public Option(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getKey() {
        return key;
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

    public T get() {
        return value;
    }

}
