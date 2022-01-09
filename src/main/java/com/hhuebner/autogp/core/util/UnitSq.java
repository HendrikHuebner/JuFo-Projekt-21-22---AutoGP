package com.hhuebner.autogp.core.util;

public enum UnitSq {
    CENTIMETRES("cm²", 0.0001),
    DECIMETRES("dm²", 0.1),
    METRES("m²", 1.0),
    OCTOMETRE("om²", 0.0156), //apparently architects use this sometimes..?
    INCHES("in²", 0.0006),
    FEET("ft²", 0.0929),
    YARDS("yd²", 0.8361);

    public final String name;
    public final double factor;

    UnitSq(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public static com.hhuebner.autogp.core.util.Unit getFromName(String name) {
        for(com.hhuebner.autogp.core.util.Unit u : com.hhuebner.autogp.core.util.Unit.values()) {
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
