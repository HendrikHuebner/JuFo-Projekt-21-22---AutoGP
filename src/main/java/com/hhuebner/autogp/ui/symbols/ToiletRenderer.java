package com.hhuebner.autogp.ui.symbols;

import javafx.scene.canvas.GraphicsContext;

public class ToiletRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.beginPath();
        ctx.moveTo(3, 0);
        ctx.lineTo(32, 0);
        ctx.bezierCurveTo(45, 70, -10, 70, 3, 0);
        ctx.stroke();

        ctx.strokeOval(6, 10, 23, 34);
        ctx.strokeOval(13, 14, 9, 9);
        ctx.strokeLine(7, 5, 28, 5);
    }
}
