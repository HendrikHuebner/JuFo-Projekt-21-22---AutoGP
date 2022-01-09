package com.hhuebner.autogp.core.util;

import com.hhuebner.autogp.core.InputHandler;

public class Utility {

    public static Pair<String, String> splitFilename(String filename) {
        int i = filename.lastIndexOf(".");
        return new Pair(filename.substring(0, i), filename.substring((i + 1)));
    }

    public static double pixelsToUnit(double px, Unit input, Unit output, double globalScale) {
        return px * globalScale * input.factor / output.factor;
    }

    public static double pixelsToUnit(double px, InputHandler inputHandler) {
        return pixelsToUnit(px, inputHandler.scalingUnit.first,
                inputHandler.scalingUnit.second, inputHandler.globalScale);
    }

    public double convertUnit(double n, Unit input, Unit output) {
        return n * input.factor / output.factor;
    }
}
