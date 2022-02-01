package com.hhuebner.autogp.ui.symbols;

import javafx.scene.canvas.GraphicsContext;

public class SingleBedRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRect(0, 0, 100, 200);
        ctx.strokeRect(20, 15, 60, 50);
        ctx.strokeLine(0, 75, 75, 75);
        ctx.strokeLine(75, 75, 100, 100);
        ctx.strokeLine(75, 75, 75, 100);
        ctx.strokeLine(75, 100, 100, 100);
    }
}
