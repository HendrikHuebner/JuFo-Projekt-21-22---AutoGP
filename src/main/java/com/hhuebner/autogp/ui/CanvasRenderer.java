package com.hhuebner.autogp.ui;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.Options;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.GPEngine;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasRenderer extends AnimationTimer {

    private final Camera cam;
    private final GPEngine engine;
    private final Canvas canvas;
    private final InputHandler inputHandler;

    public CanvasRenderer(Canvas canvas, InputHandler inputHandler, GPEngine engine, Camera cam) {
        this.canvas = canvas;
        this.inputHandler = inputHandler;
        this.engine = engine;
        this.cam = cam;
    }

    @Override
    public void handle(long time) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, w, h);

        ctx.save();
        ctx.transform(this.cam.getTransform());

        if(Options.showGrid) {
            drawGrid(ctx, w, h);
        }

        for(PlanComponent component : engine.getComponents()) {
            component.render(ctx);
        }

        this.inputHandler.getSelectedComponent().ifPresent(component -> {
           if(component instanceof InteractableComponent) {
               ((InteractableComponent)component).renderInteractionBox(ctx, this.cam);
           }
        });

        ctx.restore();

        if(inputHandler.hasSelection()) {
            this.drawSelectionBox(ctx, inputHandler.getSelection());
        }
    }

    private void drawSelectionBox(GraphicsContext ctx, double[] selection) {
        ctx.save();
        ctx.setStroke(Color.DARKBLUE);
        ctx.setFill(Color.STEELBLUE);
        ctx.setLineWidth(2.0);
        ctx.beginPath();
        ctx.rect(selection[0], selection[1], selection[2] - selection[0], selection[3] - selection[1]);
        ctx.stroke();
        ctx.setGlobalAlpha(0.3);
        ctx.fill();
        ctx.closePath();
        ctx.restore();
    }

    public void drawGrid(GraphicsContext ctx, double w, double h) {
        final int cellCountY = 10;
        double cellsize = h / (cellCountY + 1);
        final int cellCountX = (int) (w / (cellsize + 1));
        double offsetX = w - cellCountX * cellsize;
        double offsetY = h - cellCountY * cellsize;

        ctx.save();
        ctx.setStroke(Color.GRAY);
        ctx.setLineWidth(1.0/this.cam.getScaleX());

        for(int i = 0; i < cellCountX; i++) {
            ctx.strokeLine(i * cellsize + offsetX, offsetY, i * cellsize + offsetX,
                    cellCountY * cellsize - cellsize + offsetY);
        }

        for(int j = 0; j < cellCountY; j++) {
            ctx.strokeLine(offsetX, j * cellsize + offsetY,
                    cellCountX * cellsize + offsetX - cellsize, j * cellsize + offsetY);
        }

        ctx.restore();
    }
}
