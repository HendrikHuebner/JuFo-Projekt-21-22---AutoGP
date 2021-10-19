package com.hhuebner.autogp.core.component;

import javafx.scene.canvas.GraphicsContext;

public class WallComponent extends PlanComponent {

    public WallComponent(long id) {
        super("wall" + id, id);
    }

    @Override
    public void render(GraphicsContext ctx) {

    }
}
