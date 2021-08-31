package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.engine.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ImageComponent extends InteractableComponent {

    protected final String path;

    public ImageComponent(BoundingBox bb, String name, String imagePath, long id) {
        super(bb, name, id);

        this.path = imagePath;
    }

    @Override
    public void render(GraphicsContext ctx) {
        ctx.save();
        ctx.setFill(Color.SALMON); //DEBUG
        ctx.fillRect(this.bb.x, this.bb.y, this.bb.x2 - this.bb.x, this.bb.y2 - this.bb.y);
        ctx.restore();
    }
}
