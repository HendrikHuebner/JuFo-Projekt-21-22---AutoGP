package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class FurnitureComponent extends InteractableComponent {

    private final FurnitureItem item;
    private final Direction facing;

    public FurnitureComponent(BoundingBox bb, Direction facing, FurnitureItem item, long id) {
        super(bb, item.getName(), id);
        this.item = item;
        this.facing = facing;
    }

    @Override
    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        ctx.save();

        double scaledX = Utility.calcPixels( bb.x, inputHandler) * CELL_SIZE;
        double scaledY = Utility.calcPixels(bb.y, inputHandler) * CELL_SIZE;
        double scaledW = Utility.calcPixels(bb.getWidth() , inputHandler) * CELL_SIZE;
        double scaledH = Utility.calcPixels(bb.getHeight(), inputHandler) * CELL_SIZE;

        ctx.translate(scaledX + (this.facing == Direction.SOUTH ? scaledW : 0), scaledY + (this.facing == Direction.WEST ? scaledH : 0));
        ctx.rotate(facing.angle);

        ctx.drawImage(this.item.getImage(), 0, 0, scaledW, scaledH);
        ctx.restore();
    }

    @Override
    public List<? extends PlanComponent> getChildren() {
        return null;
    }
}
