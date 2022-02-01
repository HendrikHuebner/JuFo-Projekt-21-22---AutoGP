package com.hhuebner.autogp.ui.symbols;

import javafx.scene.canvas.GraphicsContext;

public class DoubleBedRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRect(0, 0, 200, 200);
        ctx.strokeRect(20, 15, 60, 50);
        ctx.strokeRect(120, 15, 60, 50);

        ctx.strokeLine(0, 75, 150, 75);
        ctx.strokeLine(150, 75, 200, 125);
        ctx.strokeLine(150, 75, 150, 125);
        ctx.strokeLine(150, 125, 200, 125);
    }
}
