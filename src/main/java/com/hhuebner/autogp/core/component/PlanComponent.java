package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.engine.BoundingBox;

public abstract class PlanComponent {

    private final BoundingBox bb;
    private final String name;
    private final long id;

    public PlanComponent(BoundingBox bb, String name, long id) {
        this.bb = bb;
        this.name = name;
        this.id = id;
    }

    public BoundingBox getBoundingBox() {
        return this.bb;
    }

    public abstract void render();
}
