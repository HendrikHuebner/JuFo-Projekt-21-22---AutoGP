package com.hhuebner.autogp.core.util;

public class Utility {

    public static Pair<String, String> splitFilename(String filename) {
        int i = filename.lastIndexOf(".");
        return new Pair(filename.substring(0, i), filename.substring((i + 1)));
    }
}
