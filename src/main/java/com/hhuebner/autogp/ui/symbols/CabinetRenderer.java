package com.hhuebner.autogp.ui.symbols;

import javafx.scene.canvas.GraphicsContext;

public class CabinetRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRect(0, 0, 100, 60);
        ctx.strokeLine(0, 0, 100, 60);
        ctx.strokeLine(0, 60, 100, 0);
    }
}
