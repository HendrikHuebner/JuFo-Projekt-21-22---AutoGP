package com.hhuebner.autogp.core.engine;


import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Room {

    public RoomType type;
    public ObservableList<FurnitureItem> furniture;
    public double size;
    public String name;
    public final int generationPriority;

    //DEBUG
    public Room(String name, RoomType type, double size, List<FurnitureItem> furniture, int generationPriority) {
        this.type = type;
        this.size = size;
        this.name = type.name;
        this.furniture = FXCollections.observableArrayList(furniture);
        this.generationPriority = generationPriority;
    }

    public String getName() {
        return this.name;
    }

    public String getSize() {
        return String.valueOf(this.size);
    }

    public String getType() {
        return this.type.toString();
    }

    public static class Builder {
        public RoomType type = null;
        public List<FurnitureItem> furniture = new ArrayList<>();
        public double size = 0.0;
        public Unit unit = Unit.METRES;
        public String name = "";
        public int generationPriority = 0;

        public Builder setType(RoomType type) {
            this.type = type;
            this.furniture = new ArrayList<>(type.defaultFurniture);
            return this;
        }

        public Builder setSize(double size) {
            this.size = size;
            return this;
        }

        public Builder setUnit(Unit unit) {
            this.unit = unit;
            return this;
        }
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPriority(int generationPriority) {
            this.generationPriority = generationPriority;
            return this;
        }

        public Builder addFurnitureItem(FurnitureItem fi) {
            this.furniture.add(fi);
            return this;
        }

        public boolean canBuild() {
            return type != null;
        }

        public Room build() {
            if(name.equals(""))
                name = this.type.name;

            return new Room(name, type, size, furniture, generationPriority);
        }
    }


}
