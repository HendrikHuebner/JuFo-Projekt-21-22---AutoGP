package com.hhuebner.autogp.core.util;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;

public class Utility {

    private static final double EPSILON = 0.000001;

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

    public static double calcPixels(double value, InputHandler inputHandler) {
        return value * inputHandler.displayUnit.factor * inputHandler.globalScale;
    }

    public static boolean epsEquals(double a, double b) {
        return a == b ? true : Math.abs(a - b) < EPSILON;
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
