package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.List;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class DoorComponent extends InteractableComponent {

    private final Direction side;
    private final boolean openingLeft;

    private DoorComponent(BoundingBox bb, Direction side, boolean openingLeft, String name, long id) {
        super(bb, name, id);
        
        this.side = side;
        this.openingLeft = openingLeft;
    }

    public static DoorComponent create(RoomComponent component, double start, double end, Direction side, String name, long id) {
        double a = side == Direction.EAST ? component.bb.x2 : component.bb.x;
        double b = side == Direction.SOUTH ? component.bb.y2 : component.bb.y;
        double clearance = (end - start) * 1.5;

        BoundingBox bb;
        boolean openingLeft;

        if(side.isHorizontal()) {
            openingLeft = start < component.bb.getHeight() - end ^ side == Direction.WEST;
            bb = new BoundingBox(a, b + start, a + clearance,b + end);
        } else {
            openingLeft = start < component.bb.getWidth() - end ^ side == Direction.SOUTH;
            bb = new BoundingBox(a + start, b + clearance, a + end, b);
        }

        return new DoorComponent(bb, side, openingLeft, name, id);
    }


    @Override
    public void render(GraphicsContext ctx, InputHandler handler) {
        double width = side.isHorizontal() ? this.bb.y2 - this.bb.y : this.bb.x2 - this.bb.x;

        ctx.save();
        ctx.setStroke(Color.RED);
        double sx = Utility.calcPixels(bb.x, handler) * CELL_SIZE;
        double sy = Utility.calcPixels(bb.y, handler) * CELL_SIZE;
        double scaledW = Utility.calcPixels(this.bb.getWidth() , handler) * CELL_SIZE;
        double scaledH = Utility.calcPixels(this.bb.getHeight(), handler) * CELL_SIZE;
        ctx.strokeRect(sx, sy, scaledW, scaledH);

        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(4);
        ctx.translate(Utility.calcPixels(side == Direction.SOUTH ? this.bb.x2 : this.bb.x, handler) * CELL_SIZE, Utility.calcPixels(this.bb.y, handler) * CELL_SIZE);
        ctx.rotate(side.angle);

        if(!openingLeft) {
            if(!side.isHorizontal())
                ctx.translate(Utility.calcPixels(width, handler) * CELL_SIZE, 0);
            else if (side == Direction.EAST){
                ctx.translate(Utility.calcPixels(width, handler) * CELL_SIZE, 0);
            }
        } else {
            if (side == Direction.WEST) {
                ctx.translate(- Utility.calcPixels(width, handler) * CELL_SIZE, 0);
            }
        }

        if(!openingLeft) ctx.scale(-1, 1);
        double scaledX = Utility.calcPixels(- width, handler) * CELL_SIZE;
        double scaledY = Utility.calcPixels(WallComponent.INNER_WALL_THICKNESS - width, handler) * CELL_SIZE;
        double scaledR = Utility.calcPixels(2 * width, handler) * CELL_SIZE;

        ctx.strokeArc(scaledX, scaledY, scaledR, scaledR, 270, 90, ArcType.ROUND);
        ctx.restore();
    }

    @Override
    public List<? extends PlanComponent> getChildren() {
        return null;
    }
}
