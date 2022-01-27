package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.ui.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public abstract class InteractableComponent extends PlanComponent {

    public boolean locked = false;
    protected BoundingBox bb;

    public InteractableComponent(BoundingBox bb, String name, long id) {
        super(name, id);
        this.bb = bb;
    }

    public void moveBound(DragMode dragMode, double dist) {
        switch (dragMode) {
            case EAST -> this.bb.x2 += dist;
            case WEST -> this.bb.x += dist;
            case NORTH -> this.bb.y += dist;
            case SOUTH -> this.bb.y2 += dist;
        }
    }

    public BoundingBox getBoundingBox() {
        return this.bb;
    }

    public void renderSelectionOutline(GraphicsContext ctx, Camera cam, InputHandler inputHandler) {
        double x = Utility.calcPixels(this.bb.x, inputHandler) * CELL_SIZE;
        double x2 = Utility.calcPixels(this.bb.x2, inputHandler) * CELL_SIZE;
        double y = Utility.calcPixels(this.bb.y, inputHandler) * CELL_SIZE;
        double y2 = Utility.calcPixels(this.bb.y2, inputHandler) * CELL_SIZE;

        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(x, y, x2 - x, y2 - y);
        ctx.restore();
    }

    public void renderInteractionBox(GraphicsContext ctx, Camera cam, InputHandler inputHandler) {
        double boxSize = 10.0 / cam.getScaleX();
        double width = Utility.calcPixels(this.bb.getWidth(), inputHandler) * CELL_SIZE;
        double height = Utility.calcPixels(this.bb.getHeight(), inputHandler) * CELL_SIZE;

        double x = Utility.calcPixels(this.bb.x, inputHandler) * CELL_SIZE;
        double x2 = Utility.calcPixels(this.bb.x2, inputHandler) * CELL_SIZE;
        double y = Utility.calcPixels(this.bb.y, inputHandler) * CELL_SIZE;
        double y2 = Utility.calcPixels(this.bb.y2, inputHandler) * CELL_SIZE;

        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(x - boxSize / 2, y + height / 2 - boxSize / 2 , boxSize, boxSize);
        ctx.strokeRect(x2 - boxSize / 2, y + height / 2 - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(x + width / 2 - boxSize / 2, y - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(x + width / 2 - boxSize / 2, y2 - boxSize / 2, boxSize, boxSize);
        ctx.restore();
    }

    public String getDescription(InputHandler handler) {
        return String.format("Object name: %s  width: %.2f%s  height %.2f%s", this.name,
                Utility.convertUnit(bb.getWidth(), Unit.METRES, handler.displayUnit), handler.displayUnit,
                Utility.convertUnit(bb.getHeight(), Unit.METRES, handler.displayUnit), handler.displayUnit);
    }
}
