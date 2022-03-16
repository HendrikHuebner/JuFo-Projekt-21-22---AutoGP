package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.component.furniture.FurnitureItems;
import com.hhuebner.autogp.options.OptionsHandler;

import java.util.List;

public enum RoomType {
    LIVING_ROOM("Wohnzimmer", 1.0f/2.0f, true,
            List.of(FurnitureItems.ARMCHAIR, FurnitureItems.COUCH, FurnitureItems.DINING_TABLE, FurnitureItems.CABINET), 15),
    BATH_ROOM("Badezimmer", 2.0f/5.0f, false, List.of(FurnitureItems.SINK, FurnitureItems.SHOWER, FurnitureItems.TOILET), 5),
    KITCHEN("Küche", 1.0f/2.0f, false, List.of(FurnitureItems.SINK, FurnitureItems.STOVE, FurnitureItems.CABINET), 10),
    BED_ROOM("Schlafzimmer", 1.0f/2.0f, true, List.of(FurnitureItems.SINGLE_BED, FurnitureItems.CABINET), 15),
    HALLWAY("Flur", 1.0f/3.0f, false, List.of(), 8),
    GENERIC_HABITABLE("Aufenthaltsraum", 1.0f/2.0f, true, List.of(), 10);

    public final String name;
    public final float minRatio;
    public final boolean isHabitable;
    public final List<FurnitureItem> defaultFurniture;
    public double defaultSize;

    RoomType(String name, float minRatio, boolean isHabitable, List<FurnitureItem> defaultFurniture, double defaultSize) {
        this.name = name;
        this.minRatio =  minRatio;
        this.isHabitable = isHabitable;
        this.defaultFurniture = defaultFurniture;
        this.defaultSize = defaultSize;
    }
}
