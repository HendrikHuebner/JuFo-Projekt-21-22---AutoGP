package com.hhuebner.autogp.core.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /**
     *
     * @return BoundingBox
     */
    public BoundingBox getBB() {
        return this.bb;
    }

    public void renderSelectionOutline(GraphicsContext ctx, Camera cam, InputHandler inputHandler) {
        double x = this.bb.x * CELL_SIZE;
        double x2 = this.bb.x2 * CELL_SIZE;
        double y = this.bb.y * CELL_SIZE;
        double y2 = this.bb.y2 * CELL_SIZE;

        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(x, y, x2 - x, y2 - y);
        ctx.restore();
    }

    public void renderInteractionBox(GraphicsContext ctx, Camera cam, InputHandler inputHandler) {
        double boxSize = 10.0 / cam.getScaleX();
        double width = this.bb.getWidth() * CELL_SIZE;
        double height = this.bb.getHeight() * CELL_SIZE;

        double x = this.bb.x * CELL_SIZE;
        double x2 = this.bb.x2 * CELL_SIZE;
        double y = this.bb.y * CELL_SIZE;
        double y2 = this.bb.y2 * CELL_SIZE;

        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(x - boxSize / 2, y + height / 2 - boxSize / 2 , boxSize, boxSize);
        ctx.strokeRect(x2 - boxSize / 2, y + height / 2 - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(x + width / 2 - boxSize / 2, y - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(x + width / 2 - boxSize / 2, y2 - boxSize / 2, boxSize, boxSize);
        ctx.restore();
    }

    @JsonIgnore
    public String getDescription(InputHandler handler) {
        return String.format("Objektname: %s  Breite: %.2f%s  Höhe %.2f%s", this.name,
                Utility.convertUnit(bb.getWidth(), Unit.METRES, handler.displayUnit), handler.displayUnit,
                Utility.convertUnit(bb.getHeight(), Unit.METRES, handler.displayUnit), handler.displayUnit);
    }

    @JsonIgnore
    public boolean isClickable() {
        return true;
    }
}
