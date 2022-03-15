package com.hhuebner.autogp.ui.symbols;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SinkRenderer extends SymbolRenderer {

    @Override
    protected void drawSymbol(GraphicsContext ctx) {
        ctx.beginPath();
        ctx.lineTo(50, 0);
        ctx.lineTo(50, 30);
        ctx.bezierCurveTo(50, 35, 50, 35, 45, 35);
        ctx.lineTo(5, 35);
        ctx.bezierCurveTo(0, 35, 0, 35, 0, 30);
        ctx.lineTo(0, 0);
        ctx.stroke();

        ctx.strokeArc(22, 9, 6, 6, 0, 360, ArcType.OPEN);
        ctx.strokeArc(6, 6, 38, 23, 0, 360, ArcType.OPEN);
    }
}
