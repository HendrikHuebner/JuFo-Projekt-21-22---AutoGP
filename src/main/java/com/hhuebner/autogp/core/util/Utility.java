package com.hhuebner.autogp.core.util;

import com.hhuebner.autogp.core.InputHandler;

public class Utility {

    public static Pair<String, String> splitFilename(String filename) {
        int i = filename.lastIndexOf(".");
        return new Pair(filename.substring(0, i), filename.substring((i + 1)));
    }

    public static double convertUnit(double n, Unit input, Unit output) {
        return n * input.factor / output.factor;
    }

    public static double calcPixels(double value, InputHandler inputHandler) {
        return value * inputHandler.displayUnit.factor * inputHandler.globalScale;
    }
}
