package com.hhuebner.autogp.core.engine;


import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Unit;

import java.util.ArrayList;
import java.util.List;

public class Room {

    public RoomType type;
    public List<FurnitureItem> furniture;
    public double size;
    public String name;

    //DEBUG
    public Room(String name, RoomType type, double size, List<FurnitureItem> furniture) {
        this.type = type;
        this.size = size;
        this.name = type.name;
        this.furniture = furniture;
    }

    public String getName() {
        return this.name;
    }

    public String getSize() {
        return String.valueOf(this.size);
    }

    public static class Builder {
        public RoomType type = null;
        public List<FurnitureItem> furniture = new ArrayList<>();
        public double size = 0.0;
        public Unit unit = Unit.METRES;
        public String name = "";

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

            return new Room(name, type, size, furniture);
        }
    }


}
