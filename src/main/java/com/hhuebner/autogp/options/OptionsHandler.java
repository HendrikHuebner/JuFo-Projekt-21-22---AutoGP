package com.hhuebner.autogp.options;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class OptionsHandler {

    public static final OptionsHandler INSTANCE = new OptionsHandler();

    private List<Option<?>> options = new ArrayList<>();

    //CANVAS UI
    public Option<Boolean> showDoorHitBoxes = register(new BoolOption("showDoorHitBoxes", false));
    public Option<Boolean> showGrid = register(new BoolOption("showGrid", true));
    public Option<Boolean> showNumbers = register(new BoolOption("showNumbers", true));
    public Option<Double> graphSizeLimitFactor = register(new DoubleOption("graphSizeLimitFactor", 1.4));
    public Option<Integer> furnitureLineWidth = register(new IntOption("furnitureLineWidth", 2));

    //WALLS, DOORS, WINDOWS
    public Option<Double> innerWallWidth = register(new DoubleOption("innerWallWidth", 0.1));
    public Option<Double> outerWallWidth = register(new DoubleOption("outerWallWidth", 0.2));
    public Option<Double> doorSize = register(new DoubleOption("doorSize", 0.75));
    public Option<Double> doorPrefWallDistance = register(new DoubleOption("doorPrefWallDistance", 0.25));
    public Option<Double> doorClearanceFactor = register(new DoubleOption("doorClearanceFactor", 1.5));

    //GENERATION ALGORITHM
    public Option<Integer> generationTryLimit = register(new IntOption("generationTryLimit", 5000));
    public Option<Boolean> generateFurniture = register(new BoolOption("generateFurniture", true));
    public Option<Boolean> generateDoors = register(new BoolOption("generateDoors", true));
    public Option<Boolean> generateWindows = register(new BoolOption("generateWindows", true));
    public Option<Double> minimumRoomWidth = register(new DoubleOption("minimumRoomWidth", 1.4));

    public Option<Double> roomSizeRoundingThreshold = register(new DoubleOption("roomSizeRoundingThreshold", 0.75));
    public Option<Integer> furnitureSpawnTries = register(new IntOption("furnitureSpawnTries", 10));

    //DEFAULT PARAMETERS
    public Option<Double> defaultGPSize = register(new DoubleOption("defaultGPSize", 100.0));

    private OptionsHandler() {
        loadConfig();
    }

    private void loadConfig() {
        Properties prop = new Properties();

        for(Option<?> o : this.options) {
            String value = prop.getProperty(o.getKey());
            o.parse(value);
        }

        try (InputStream inputStream = new FileInputStream("config.properties")) {
            if(inputStream != null)
                prop.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> Option<T> register(Option<T> o) {
        this.options.add(o);
        return o;
    }

    public List<Option<?>> getOptions() {
        return this.options;
    }
}
