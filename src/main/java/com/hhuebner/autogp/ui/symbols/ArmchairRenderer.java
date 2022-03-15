package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class ArmchairRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.strokeLine(0, 0, 95, 0);
        ctx.strokeLine(0, 0, 0, 65);
        ctx.strokeLine(0, 65, 15, 65);
        ctx.strokeLine(95, 0, 95, 65);
        ctx.strokeLine(95, 65, 80, 65);

        ctx.strokeRect(15, 20, 65, 60);
    }
}
