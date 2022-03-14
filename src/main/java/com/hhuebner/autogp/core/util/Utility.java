package com.hhuebner.autogp.core.util;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;

public class Utility {

    public static Pair<String, String> splitFilename(String filename) {
        int i = filename.lastIndexOf(".");
        return new Pair(filename.substring(0, i), filename.substring((i + 1)));

    }

    public static double convertUnit(double n, Unit input, Unit output) {
        return n * input.factor / output.factor;
    }

    public static double convertUnitSq(double n, UnitSq input, UnitSq output) {
        return n * input.factor / output.factor;
    }


    public static boolean epsEquals(double a, double b, double eps) {
        return a == b ? true : Math.abs(a - b) < eps;
    }

    /**
     * Returns the area of a room.
     * @param bb BoundingBox of room
     * @param ww inner wall width of room
     * @return
     */
    public static double getRoomArea(BoundingBox bb, double ww) {
        return (bb.getHeight() - 2 * ww) * (bb.getWidth() - 2 * ww);
    }
}
