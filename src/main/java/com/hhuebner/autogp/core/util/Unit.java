package com.hhuebner.autogp.core.util;

public enum Unit {
    CENTIMETRES("cm", 0.01),
    DECIMETRES("dm", 0.1),
    METRES("m", 1.0),
    OCTOMETRE("om", 0.125),
    INCHES("in", 0.0254),
    FEET("ft", 0.32),
    YARDS("yd", 0.81);

    public final String name;
    public final double factor;

    Unit(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public static Unit getFromName(String name) {
        for(Unit u : Unit.values()) {
            if(name.equals(u.name))
                return u;
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
