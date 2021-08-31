package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.engine.BoundingBox;
import javafx.scene.canvas.GraphicsContext;

public abstract class PlanComponent {

    protected final BoundingBox bb;
    protected final String name;
    protected final long id;

    public PlanComponent(BoundingBox bb, String name, long id) {
        this.bb = bb;
        this.name = name;
        this.id = id;
    }

    public BoundingBox getBoundingBox() {
        return this.bb;
    }

    public abstract void render(GraphicsContext ctx);
}
