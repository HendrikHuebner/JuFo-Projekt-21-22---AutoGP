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
    private final List<PlanComponent> children = new ArrayList<>();


    public RoomComponent(Room room, BoundingBox bb, long id) {
        super(bb, "room" + id, id);
        this.room = room;
    }

    @Override
    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        for(PlanComponent component : this.children) {
            component.render(ctx, inputHandler);
        }
    }

    public List<AnchorPoint> getAnchors(List<RoomComponent> graph) {
        List<AnchorPoint> list = new ArrayList<>();
        for(Direction facing : Direction.values()) {
            list.add(new AnchorPoint(graph, this, facing.rotateCW(), facing));
            list.add(new AnchorPoint(graph,this, facing.rotateCCW(), facing));
        }

        return list;
    }

    public void addChild(PlanComponent component) {
        this.children.add(component);
    }

    public List<PlanComponent> getChildren() {
        return this.children;
    }


    public WallComponent getWallComponent() {
        for(PlanComponent c : children) {
            if(c instanceof  WallComponent)
                return (WallComponent) c;
        }
        throw new ExceptionInInitializerError();
    }
}
