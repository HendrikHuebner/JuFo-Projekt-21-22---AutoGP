package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.ui.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    public void renderSelectionOutline(GraphicsContext ctx, Camera cam) {
        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(this.bb.x, this.bb.y, this.bb.getWidth(), this.bb.getHeight());
        ctx.restore();
    }

    public void renderInteractionBox(GraphicsContext ctx, Camera cam) {
        double boxSize = 10.0/cam.getScaleX();
        double width = this.bb.getWidth();
        double height = this.bb.getHeight();

        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(this.bb.x - boxSize / 2, this.bb.y + height / 2 - boxSize / 2 , boxSize, boxSize);
        ctx.strokeRect(this.bb.x2 - boxSize / 2, this.bb.y + height / 2 - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(this.bb.x + width / 2 - boxSize / 2, this.bb.y - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(this.bb.x + width / 2 - boxSize / 2, this.bb.y2 - boxSize / 2, boxSize, boxSize);
        ctx.restore();
    }
}
