package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class CouchRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeLine(0, 0, 195, 0);
        ctx.strokeLine(0, 0, 0, 65);
        ctx.strokeLine(0, 65, 15, 65);
        ctx.strokeLine(195, 0, 195, 65);
        ctx.strokeLine(195, 65, 180, 65);

        ctx.strokeRect(15, 20, 165, 60);
        ctx.strokeLine(70, 20, 70, 80);
        ctx.strokeLine(125, 20, 125, 80);
    }
}
