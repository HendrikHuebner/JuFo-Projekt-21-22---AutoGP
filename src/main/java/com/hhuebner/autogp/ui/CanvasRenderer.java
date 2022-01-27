package com.hhuebner.autogp.ui;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.Options;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.ImageLoader;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.core.util.Utility;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class CanvasRenderer extends AnimationTimer {

    private final Camera cam;
    private final GPEngine engine;
    private final Canvas canvas;
    private final InputHandler inputHandler;

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

        for(RoomComponent roomComponent : engine.getComponents()) {
            roomComponent.render(ctx, inputHandler);
        }

        if(inputHandler.getSelectedComponent().isPresent()) {
            PlanComponent component = inputHandler.getSelectedComponent().get();
            if(component instanceof InteractableComponent) {
                ((InteractableComponent) component).renderSelectionOutline(ctx, this.cam, this.inputHandler);

                if (this.inputHandler.getTool() == InputHandler.Tool.CURSOR)
                    ((InteractableComponent) component).renderInteractionBox(ctx, this.cam, this.inputHandler);
            }
        }

        ctx.restore();
        drawGridNumbers(ctx);

        if(inputHandler.getTool() == InputHandler.Tool.SELECTION && inputHandler.hasSelection()) {
            this.drawSelectionBox(ctx, inputHandler.getSelection());
        }
    }

    /*
    @Deprecated
    private void drawMeasurements(GraphicsContext ctx, InteractableComponent component) {
        BoundingBox bb = component.getBoundingBox();
        final int textOffset = 6;

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

        ctx.fillText(String.format("%.2f%s", Utility.pixelsToUnit(bb.getWidth(), this.inputHandler) / CELL_SIZE,
                this.inputHandler.scalingUnit.second.name),bb.x / 2 + bb.x2 / 2, this.canvas.getHeight() + textOffset);

        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);

        ctx.fillText(String.format("%.2f%s", Utility.pixelsToUnit(bb.getHeight(), this.inputHandler) / CELL_SIZE,
                        this.inputHandler.scalingUnit.second.name), -textOffset, bb.y / 2 + bb.y2 / 2);

        ctx.restore();
    }*/

    @Deprecated
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

    public void drawGridNumbers(GraphicsContext ctx) {
        ctx.save();
        ctx.setFill(Color.GRAY);

        final int cellCountX = (int) (canvas.getWidth() / this.cam.getScaleX() / CELL_SIZE) + 3;
        final int cellCountY = (int) (canvas.getHeight() / this.cam.getScaleY() / CELL_SIZE) + 3;
        double startX = -this.cam.getX() + this.cam.getX() % CELL_SIZE;
        double startY = -this.cam.getY() + this.cam.getY() % CELL_SIZE;

        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.RIGHT);

        for(int i = 0; i < cellCountX; i++) {
            ctx.fillText(String.format("%.1f", Utility.calcPixels(startX/CELL_SIZE + i, this.inputHandler)),
                    (this.cam.getX() % CELL_SIZE + i * CELL_SIZE) * cam.getScaleX(), canvas.getHeight() - 10);
        }

        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.CENTER);

        for(int i = 0; i < cellCountY; i++) {
            ctx.fillText(String.format("%.1f", Utility.calcPixels(startY/CELL_SIZE + i, this.inputHandler)), 10,
                    (this.cam.getY() % CELL_SIZE + i * CELL_SIZE) * cam.getScaleY());
        }

        ctx.restore();
    }

    public void drawGrid(GraphicsContext ctx) {
        final int cellCountX = (int) (canvas.getWidth() / this.cam.getScaleX() / CELL_SIZE) + 3;
        final int cellCountY = (int) (canvas.getHeight() / this.cam.getScaleY() / CELL_SIZE) + 3;
        double lengthX = canvas.getWidth() / this.cam.getScaleX() + 2 * CELL_SIZE;
        double lengthY = canvas.getHeight() / this.cam.getScaleY() +  2 * CELL_SIZE;
        double startX = -this.cam.getX() + this.cam.getX() % CELL_SIZE - CELL_SIZE;
        double startY = -this.cam.getY() + this.cam.getY() % CELL_SIZE - CELL_SIZE;

        ctx.save();
        ctx.setStroke(Color.GRAY);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setLineWidth(1.0/this.cam.getScaleX());
        ctx.setFont(new Font("Arial", 9));

        //Thick lines and measurements
        for(int i = 0; i < cellCountX; i++) {
            ctx.strokeLine(startX + i * CELL_SIZE, startY, startX + i * CELL_SIZE, startY + lengthY);
        }
        for(int i = 0; i < cellCountY; i++) {
            ctx.strokeLine(startX, startY + i * CELL_SIZE, startX + lengthX, startY  + i * CELL_SIZE);
        }

        ctx.setStroke(Color.LIGHTGRAY);
        for(int i = 0; i < cellCountX; i++) {
            for (int j = 1; j < 5; j++) {
                double x = startX + i * CELL_SIZE + j * CELL_SIZE / 5;
                ctx.strokeLine(x, startY, x, startY + lengthY);
            }
        }
        for(int i = 0; i < cellCountY; i++) {
            for (int j = 1; j < 5; j++) {
                double y = startY + i * CELL_SIZE + j * CELL_SIZE / 5;
                ctx.strokeLine(startX, y, startX + lengthX, y);
            }
        }
        ctx.restore();
    }
}
