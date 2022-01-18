package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.InputHandler;
import javafx.scene.canvas.GraphicsContext;

public abstract class PlanComponent {

    protected final String name;
    protected final long id;

    public PlanComponent(String name, long id) {
        this.name = name;
        this.id = id;
    }


    public abstract void render(GraphicsContext ctx, InputHandler inputHandler);

    public String getName() {
        return this.name;
    }
}
