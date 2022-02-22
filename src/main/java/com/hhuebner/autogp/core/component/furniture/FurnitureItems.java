package com.hhuebner.autogp.core.component.furniture;

import com.hhuebner.autogp.ui.symbols.*;

import java.util.List;

public class FurnitureItems {

    public static final FurnitureItem TOILET = new FurnitureItem("Toilette", 0.35, 0.58, new ToiletRenderer());
    public static final FurnitureItem BATH_TUB = new FurnitureItem("Badewanne", 0, 0, new CabinetRenderer());
    public static final FurnitureItem SINK = new FurnitureItem("Waschbecken", 0.5, 0.35, new SinkRenderer());
    public static final FurnitureItem SHOWER = new FurnitureItem("Dusche", 1.0, 1.0, true, new ShowerRenderer());
    
    public static final FurnitureItem ARMCHAIR = new FurnitureItem("Sessel", 0.95, 0.8, new ArmchairRenderer());
    public static final FurnitureItem SINGLE_BED = new FurnitureItem("Einzelbett", 1.0, 2.0, true, new SingleBedRenderer());
    public static final FurnitureItem DOUBLE_BED = new FurnitureItem("Doppelbett", 2.0, 2.0, new CabinetRenderer());
    public static final FurnitureItem DINING_TABLE = new FurnitureItem("Esstisch", 1.90, 1.20, new TableRenderer());
    public static final FurnitureItem CABINET = new FurnitureItem("Schrank", 1.0, 0.60, new CabinetRenderer());
    public static final FurnitureItem COUCH = new FurnitureItem("Sofa", 1.95, 0.8, new CouchRenderer());

    public static List<FurnitureItem> getItems() {
        return List.of(TOILET, BATH_TUB, SINK, SHOWER, ARMCHAIR, SINK, DOUBLE_BED, DINING_TABLE, CABINET, COUCH);
    }
}
