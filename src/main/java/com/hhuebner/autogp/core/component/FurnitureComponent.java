package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class FurnitureComponent extends InteractableComponent {

    private final FurnitureItem item;
    private final Direction facing;
    private boolean isValid = false; //unused. Indicates that this FC is in a correct position

    public FurnitureComponent(BoundingBox bb, Direction facing, FurnitureItem item, long id) {
        super(bb, item.getName(), id);
        this.item = item;
        this.facing = facing;
    }

    @Override
    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        ctx.save();

        double scaledX = Utility.calcPixels(bb.x, inputHandler) * CELL_SIZE;
        double scaledY = Utility.calcPixels(bb.y, inputHandler) * CELL_SIZE;
        double scaledW = Utility.calcPixels(this.item.getWidth() , inputHandler) * CELL_SIZE;
        double scaledH = Utility.calcPixels(this.item.getHeight(), inputHandler) * CELL_SIZE;

        if(this.facing == Direction.EAST)
            scaledX += scaledH;

        if(this.facing == Direction.WEST)
            scaledY += scaledW;

        if(this.facing == Direction.SOUTH) {
            scaledX += scaledW;
            scaledY += scaledH;
        }

        ctx.translate(scaledX, scaledY);

        ctx.rotate(facing.angle);
        this.item.getRenderer().render(ctx, item,0, 0, scaledW, scaledH);
        //ctx.drawImage(this.item.getImage(), 0, 0, scaledW, scaledH);
        ctx.restore();
    }

    @Override
    public List<? extends PlanComponent> getChildren() {
        return null;
    }

    public boolean validate(List<RoomComponent> components) {
        return isValid;
    }

    public boolean isValid() {
        return isValid;
    }
}
