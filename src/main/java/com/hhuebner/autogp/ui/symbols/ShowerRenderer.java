package com.hhuebner.autogp.ui.symbols;

import javafx.scene.canvas.GraphicsContext;

public class ShowerRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRoundRect(0, 0, 100, 100, 15, 15);
        ctx.strokeRoundRect(8, 8, 84, 84, 15, 15);

        ctx.strokeOval(16, 16, 8, 8);
        ctx.strokeLine(28, 28, 80, 80);
        ctx.strokeLine(32, 20, 84, 20);
        ctx.strokeLine(20, 32, 20, 84);
    }
}
