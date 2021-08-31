package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.ui.Camera;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class InteractableComponent extends PlanComponent {

    public boolean locked = false;

    public InteractableComponent(BoundingBox bb, String name, long id) {
        super(bb, name, id);
    }

    public void moveBound(DragMode dragMode, double dist) {
        switch(dragMode) {
            case EAST: {
                this.bb.x2 += dist; break;
            }
            case WEST: {
                this.bb.x += dist; break;
            }
            case NORTH: {
                this.bb.y += dist; break;
            }
            case SOUTH: {
                this.bb.y2 += dist; break;
            }
        }
    }

    public void renderInteractionBox(GraphicsContext ctx, Camera cam) {
        double boxSize = 10.0/cam.getScaleX();
        double width = this.bb.x2 - this.bb.x;
        double height = this.bb.y2 - this.bb.y;
        ctx.save();
        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(1.0/cam.getScaleX());
        ctx.strokeRect(this.bb.x, this.bb.y, width, height);
        ctx.strokeRect(this.bb.x - boxSize / 2, this.bb.y + height / 2 - boxSize / 2 , boxSize, boxSize);
        ctx.strokeRect(this.bb.x2 - boxSize / 2, this.bb.y + height / 2 - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(this.bb.x + width / 2 - boxSize / 2, this.bb.y - boxSize / 2, boxSize, boxSize);
        ctx.strokeRect(this.bb.x + width / 2 - boxSize / 2, this.bb.y2 - boxSize / 2, boxSize, boxSize);

        ctx.restore();
    }
}
