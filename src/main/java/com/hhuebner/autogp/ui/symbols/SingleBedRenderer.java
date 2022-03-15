package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
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
