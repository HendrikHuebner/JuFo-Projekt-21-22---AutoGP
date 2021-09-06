package com.hhuebner.autogp.ui;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.Options;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.GPEngine;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CanvasRenderer extends AnimationTimer {

    private final Camera cam;
    private final GPEngine engine;
    private final Canvas canvas;
    private final InputHandler inputHandler;

    private final int CELL_COUNT_Y = 10;
    private final int textAxisDist = 8;

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

        if(Options.showGrid)
            drawGrid(ctx);

        for(PlanComponent component : engine.getComponents()) {
            component.render(ctx);
        }

        if(inputHandler.getSelectedComponent().isPresent()) {
            PlanComponent component = inputHandler.getSelectedComponent().get();
            if(component instanceof InteractableComponent) {
                ((InteractableComponent)component).renderSelectionOutline(ctx, this.cam);

                if(this.inputHandler.getTool() == InputHandler.Tool.CURSOR)
                    ((InteractableComponent)component).renderInteractionBox(ctx, this.cam);
            }

            if(this.inputHandler.getTool() == InputHandler.Tool.RULER)
                this.drawMeasurements(ctx, component);
        }

        ctx.restore();

        if(inputHandler.getTool() == InputHandler.Tool.SELECTION && inputHandler.hasSelection()) {
            this.drawSelectionBox(ctx, inputHandler.getSelection());
        }
    }

    private void drawMeasurements(GraphicsContext ctx, PlanComponent component) {
        BoundingBox bb = component.getBoundingBox();
        final int textOffset = 6;
        double cellsize = canvas.getHeight() / (CELL_COUNT_Y + 1);

        ctx.save();
        ctx.setStroke(Color.BLUE);
        ctx.setLineWidth(1.0 / cam.getScaleX());
        ctx.strokeLine(bb.x, bb.y, 0, bb.y);
        ctx.strokeLine(bb.x, bb.y2, 0, bb.y2);
        ctx.strokeLine(bb.x, bb.y2, bb.x, this.canvas.getHeight());
        ctx.strokeLine(bb.x2, bb.y2, bb.x2, this.canvas.getHeight());

        ctx.setLineWidth(4.0/cam.getScaleX());
        ctx.strokeLine(0, bb.y,0, bb.y2);
        ctx.strokeLine(bb.x, this.canvas.getHeight(), bb.x2, this.canvas.getHeight());

        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.TOP);

        ctx.fillText(String.format("%.2f", (bb.x2 - bb.x) / cellsize * this.inputHandler.globalScale),
                bb.x / 2 + bb.x2 / 2, this.canvas.getHeight() + textOffset);

        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);

        ctx.fillText(String.format("%.2f", (bb.y2 - bb.y) / cellsize * this.inputHandler.globalScale),
                -textOffset, bb.y / 2 + bb.y2 / 2);

        ctx.restore();
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

    public void drawGrid(GraphicsContext ctx) {

        double cellsize = canvas.getHeight() / (CELL_COUNT_Y + 1);
        final int cellCountX = (int) (canvas.getWidth() / (cellsize + 1));
        double offsetX = canvas.getWidth() - cellCountX * cellsize;
        double offsetY = canvas.getHeight() - CELL_COUNT_Y * cellsize;

        ctx.save();
        ctx.setStroke(Color.GRAY);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setLineWidth(1.0/this.cam.getScaleX());
        ctx.setFont(new Font("Arial", 9));

        //GRID AND NUMBERS
        for(int j = 0; j < CELL_COUNT_Y; j++) {
            //HORIZONTAL LINES
            ctx.strokeLine(offsetX, j * cellsize + offsetY,
                    cellCountX * cellsize + offsetX - cellsize, j * cellsize + offsetY);
            ctx.fillText(String.format("%.1f%s", (CELL_COUNT_Y - j - 1) * this.inputHandler.globalScale,
                            this.inputHandler.scalingUnit.name), offsetX - textAxisDist, j * cellsize + offsetY);

        }

        ctx.setTextBaseline(VPos.TOP);
        ctx.setTextAlign(TextAlignment.CENTER);

        for(int i = 0; i < cellCountX; i++) {
            //VERTICAL LINES
            ctx.strokeLine(i * cellsize + offsetX, offsetY, i * cellsize + offsetX,
                    CELL_COUNT_Y * cellsize - cellsize + offsetY);
            ctx.fillText(String.format("%.1f%s", i * this.inputHandler.globalScale, this.inputHandler.scalingUnit.name),
                    i * cellsize + offsetX, CELL_COUNT_Y * cellsize - cellsize + offsetY + textAxisDist);
        }

        //AXES
        ctx.setStroke(Color.BLACK);
        ctx.strokeLine(offsetX, offsetY, offsetX, CELL_COUNT_Y * cellsize - cellsize + offsetY);
        ctx.strokeLine(offsetX, (CELL_COUNT_Y - 1) * cellsize + offsetY,
                cellCountX * cellsize + offsetX - cellsize, (CELL_COUNT_Y - 1) * cellsize + offsetY);

        ctx.restore();
    }
}
