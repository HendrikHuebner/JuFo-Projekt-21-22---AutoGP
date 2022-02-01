package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.component.furniture.FurnitureItems;

import java.util.List;

public enum RoomType {
    LIVING_ROOM("Wohnzimmer", 1.0f/2.0f, true, List.of(FurnitureItems.ARMCHAIR, FurnitureItems.COUCH, FurnitureItems.DINING_TABLE, FurnitureItems.CABINET)),
    BATH_ROOM("Badezimmer", 2.0f/5.0f, false, List.of(FurnitureItems.SINK, FurnitureItems.SHOWER, FurnitureItems.TOILET)),
    KITCHEN("Küche", 1.0f/2.0f, false, List.of(FurnitureItems.SINK)),
    BED_ROOM("Schlafzimmer", 1.0f/2.0f, true, List.of(FurnitureItems.SINGLE_BED, FurnitureItems.CABINET)),
    HALLWAY("Flur", 1.0f/3.0f, false, List.of()),
    GENERIC_HABITABLE("Aufenthaltsraum", 1.0f/2.0f, true, List.of());

    public final String name;
    public final float minRatio;
    public final boolean isHabitable;
    public final List<FurnitureItem> defaultFurniture;

    RoomType(String name, float minRatio, boolean isHabitable, List<FurnitureItem> defaultFurniture) {
        this.name = name;
        this.minRatio =  minRatio;
        this.isHabitable = isHabitable;
        this.defaultFurniture = defaultFurniture;
    }

    @Override
    public String toString() {
        return name;
    }
}
