package com.hhuebner.autogp.ui.symbols;

import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.options.OptionsHandler;
import javafx.scene.canvas.GraphicsContext;

public abstract class SymbolRenderer {

    public void render(GraphicsContext ctx, FurnitureItem item, double x, double y, double w, double h) {
        ctx.save();
        ctx.translate(x, y);
        ctx.scale(w / item.getWidth() / 100, h / item.getHeight() / 100);
        ctx.setLineWidth(1);//OptionsHandler.INSTANCE.furnitureLineWidth.get());
        this.drawSymbol(ctx);
        ctx.restore();
    }

    /**
     * Draws the symbol at (0, 0). 100px equal one Meter.
     * @param ctx
     */
    protected abstract void drawSymbol(GraphicsContext ctx);
}
