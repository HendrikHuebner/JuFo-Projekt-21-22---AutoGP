package com.hhuebner.autogp.core.component;

import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.BoundingBox;
import javafx.scene.canvas.GraphicsContext;

public class FurnitureComponent extends InteractableComponent {

    private final FurnitureItem item;

    public FurnitureComponent(BoundingBox bb, FurnitureItem item, long id) {
        super(bb, item.getName(), id);
        this.item = item;
    }

    @Override
    public void render(GraphicsContext ctx) {
        ctx.save();
        ctx.drawImage(this.item.getImage(), this.bb.x, this.bb.y, this.bb.getWidth(), this.bb.getHeight());
        ctx.restore();
    }
}
