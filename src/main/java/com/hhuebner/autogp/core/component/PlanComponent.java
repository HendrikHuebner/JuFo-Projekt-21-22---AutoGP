package com.hhuebner.autogp.core.component;

import javafx.scene.canvas.GraphicsContext;

public abstract class PlanComponent {

    protected final String name;
    protected final long id;

    public PlanComponent(String name, long id) {
        this.name = name;
        this.id = id;
    }


    public abstract void render(GraphicsContext ctx);
}
