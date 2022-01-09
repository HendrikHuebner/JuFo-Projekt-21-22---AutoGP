package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class Room {

    public RoomType type;
    public List<FurnitureItem> furniture;
    public double size;
    public Unit unit;
    public String name;

    public BoundingBox boundingBox = BoundingBox.EMPTY; //for now, we just render rooms separately from components
    public List<WallPosition> doors = new ArrayList<>();
    public List<WallPosition> windows = new ArrayList<>();

    private static Map<RoomType, Color> typeColors = new HashMap<>();

    static {
        typeColors.put(RoomType.LIVING_ROOM, Color.YELLOW);
        typeColors.put(RoomType.BED_ROOM, Color.GREEN);
        typeColors.put(RoomType.KITCHEN, Color.ORANGE);
        typeColors.put(RoomType.BATH_ROOM, Color.LIGHTBLUE);
        typeColors.put(RoomType.HALLWAY, Color.LIGHTGREY);
    }

    //DEBUG
    public Room(String name, RoomType type, double size, Unit unit, List<FurnitureItem> furniture) {
        this.type = type;
        this.size = size;
        this.name = type.name;
        this.unit = unit;
        this.furniture = furniture;
    }

    public String getName() {
        return this.name;
    }

    public String getSize() {
        return String.valueOf(this.size) + " " + this.unit.name;
    }

    public List<AnchorPoint> getAnchors(List<Room> graph) {
        List<AnchorPoint> list = new ArrayList<>();
        for(Direction faceing : Direction.values()) {
            list.add(new AnchorPoint(graph, this, faceing.rotateCW(), faceing));
            list.add(new AnchorPoint(graph,this, faceing.rotateCCW(), faceing));
        }
        
        return list;
    }

    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setFill(typeColors.get(this.type));
        ctx.setLineWidth(4);
        double scaledX = Utility.pixelsToUnit(boundingBox.x, unit, inputHandler.scalingUnit.second, inputHandler.globalScale) * CELL_SIZE;
        double scaledY = Utility.pixelsToUnit(boundingBox.y, unit, inputHandler.scalingUnit.second, inputHandler.globalScale) * CELL_SIZE;
        double scaledW = Utility.pixelsToUnit(boundingBox.getWidth(), unit, inputHandler.scalingUnit.second, inputHandler.globalScale) * CELL_SIZE;
        double scaledH = Utility.pixelsToUnit(boundingBox.getHeight(), unit, inputHandler.scalingUnit.second, inputHandler.globalScale) * CELL_SIZE;
        ctx.save();
        ctx.setGlobalAlpha(0.4);
        ctx.fillRect(scaledX, scaledY, scaledW, scaledH);
        ctx.restore();
        ctx.strokeRect(scaledX, scaledY, scaledW, scaledH);
        ctx.restore();
    }

    public static class Builder {
        public RoomType type = null;
        public List<FurnitureItem> furniture = new ArrayList<>();
        public double size = 0.0;
        public Unit unit = Unit.METRES;
        public String name = "";

        public Builder setType(RoomType type) {
            this.type = type;
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
            return type != null && size != 0;
        }

        public Room build() {
            if(name.equals(""))
                name = this.type.name;

            return new Room(name, type, size, unit, furniture);
        }
    }

    private static record WallPosition(Direction side, double position, boolean facingInwards) {

    }
}
