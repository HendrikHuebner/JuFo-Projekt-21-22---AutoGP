package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.AnchorPoint;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.engine.RoomType;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class RoomComponent extends InteractableComponent {

    public final Room room;
    public List<WallPosition> doors = new ArrayList<>();
    public List<WallPosition> windows = new ArrayList<>();
    private static Map<RoomType, Color> typeColors = new HashMap<>();

    private final List<PlanComponent> children = new ArrayList<>();

    static {
        typeColors.put(RoomType.LIVING_ROOM, Color.YELLOW);
        typeColors.put(RoomType.BED_ROOM, Color.GREEN);
        typeColors.put(RoomType.KITCHEN, Color.ORANGE);
        typeColors.put(RoomType.BATH_ROOM, Color.LIGHTBLUE);
        typeColors.put(RoomType.HALLWAY, Color.LIGHTGREY);
    }

    public RoomComponent(Room room, BoundingBox bb, long id) {
        super(bb, "room" + id, id);
        this.room = room;
    }

    @Override
    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setFill(this.typeColors.get(this.room.type));
        ctx.setLineWidth(4);
        double scaledX = Utility.calcPixels(bb.x, inputHandler) * CELL_SIZE;
        double scaledY = Utility.calcPixels(bb.y, inputHandler) * CELL_SIZE;
        double scaledW = Utility.calcPixels(bb.getWidth(), inputHandler) * CELL_SIZE;
        double scaledH = Utility.calcPixels(bb.getHeight(), inputHandler) * CELL_SIZE;
        ctx.save();
        ctx.setGlobalAlpha(0.4);
        ctx.fillRect(scaledX, scaledY, scaledW, scaledH);
        ctx.restore();
        ctx.strokeRect(scaledX, scaledY, scaledW, scaledH);
        ctx.restore();
    }

    public List<AnchorPoint> getAnchors(List<RoomComponent> graph) {
        List<AnchorPoint> list = new ArrayList<>();
        for(Direction facing : Direction.values()) {
            list.add(new AnchorPoint(graph, this, facing.rotateCW(), facing));
            list.add(new AnchorPoint(graph,this, facing.rotateCCW(), facing));
        }

        return list;
    }

    public List<PlanComponent> getChildren() {
        return this.children;
    }

    private static record WallPosition(Direction side, double position, boolean facingInwards) {

    }
}
