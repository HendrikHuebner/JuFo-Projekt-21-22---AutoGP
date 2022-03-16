package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class StoveRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeRoundRect(0, 0, 80, 80, 5, 5);
        ctx.strokeLine(8, 7, 72, 7);

        ctx.strokeOval(10, 15, 24, 24);
        ctx.strokeOval(44, 15, 24, 24);
        ctx.strokeOval(10, 47, 24, 24);
        ctx.strokeOval(44, 47, 24, 24);

    }
}
