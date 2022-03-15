package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TableRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRect(0, 10, 45, 45);
        ctx.strokeLine(8, 10, 8, 55);
        ctx.strokeRect(0, 65, 45, 45);
        ctx.strokeLine(8, 65, 8, 110);

        ctx.strokeRect(55, 0, 80, 120);

        ctx.strokeRect(145, 10, 45, 45);
        ctx.strokeLine(182, 10, 182, 55);
        ctx.strokeRect(145, 65, 45, 45);
        ctx.strokeLine(182, 65, 182, 110);
    }
}
