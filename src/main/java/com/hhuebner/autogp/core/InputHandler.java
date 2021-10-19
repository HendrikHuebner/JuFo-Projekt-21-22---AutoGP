package com.hhuebner.autogp.core;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.util.Pair;
import com.hhuebner.autogp.core.util.Unit;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

import java.util.Optional;
import java.util.function.Supplier;

public class InputHandler {

    private final GPEngine engine;
    private final Supplier<Scene> scene;
    public Optional<Point2D> dragStart = Optional.empty();
    public Optional<Point2D> dragEnd = Optional.empty();
    public Pair<Unit, Unit> scalingUnit = new Pair(Unit.METRES, Unit.METRES);
    private Tool tool = Tool.MOVE;
    private Optional<InteractableComponent> selected = Optional.empty();
    private Optional<DragMode> selectedDragMode = Optional.empty();
    public double globalScale = 1.0;

    public InputHandler(Supplier<Scene> scene, GPEngine engine) {
        this.scene = scene;
        this.engine = engine;
    }

    public double calcMeasuredDistance(double cells) {
        return cells * this.globalScale * this.scalingUnit.first.factor / this.scalingUnit.second.factor;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        this.dragStart = Optional.empty();
        this.dragEnd = Optional.empty();
    }

    public Tool getTool() {
        return this.tool;
    }

    // ***** Cursor tool *****

    public void onCursorClick(double mouseXAbsolute, double mouseYAbsolute) {
        AutoGP.log("selected:", this.selectedDragMode);

        //Check if a resize box has been clicked
        if(this.selected.isPresent()) {
            BoundingBox bb = this.selected.get().getBoundingBox();
            double w = bb.getWidth() / 2; //half width of bb
            double h = bb.getHeight() / 2; //half height of bb
            final double o = 12; //half of the side length of the clickable box
            
            if(new BoundingBox(bb.x + w - o, bb.y - o, bb.x + w + o, bb.y + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.NORTH); return;
            } else if(new BoundingBox(bb.x + w - o, bb.y2 - o, bb.x + w + o, bb.y2 + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.SOUTH); return;
            } else if(new BoundingBox(bb.x - o, bb.y + h - o, bb.x + o, bb.y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.WEST); return;
            } else if(new BoundingBox(bb.x2 - o, bb.y + h - o, bb.x2 + o, bb.y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.EAST); return;
            }
        }
        
        for(PlanComponent component : this.engine.getComponents()) {
            if(component instanceof InteractableComponent) {
                if (((InteractableComponent)component).getBoundingBox().containsPoint(mouseXAbsolute, mouseYAbsolute)) {

                    if (this.selected.isPresent() && this.selected.get().equals(component)) {
                        //if selected component was clicked again, set drag mode to MOVE
                        this.selectedDragMode = Optional.of(DragMode.MOVE);

                    } else
                        this.selected = Optional.of((InteractableComponent) component);
                } else
                    this.selected = Optional.empty();
            }
        }
    }

    public void handleCursorDrag(double startX, double startY, double currentX, double currentY) {
        if(this.selected.isPresent() && this.selectedDragMode.isPresent()) {
            DragMode dragMode = this.selectedDragMode.get();
            InteractableComponent component = this.selected.get();
            double dx = currentX - startX;
            double dy = currentY - startY;

            switch(dragMode) {
                case MOVE -> component.getBoundingBox().move(dx, dy);
                case NORTH -> component.getBoundingBox().y += dy;
                case SOUTH -> component.getBoundingBox().y2 += dy;
                case WEST -> component.getBoundingBox().x += dx;
                case EAST -> component.getBoundingBox().x2 += dx;
            }
        }
    }

    public Optional<InteractableComponent> getSelectedComponent() {
        return this.selected;
    }

    // ***** Selection Tool *****

    public boolean hasSelection() {
        return this.dragStart.isPresent() && this.dragEnd.isPresent();
    }

    /**
     * Called when the selection box is released
     */
    public void handleSelection(double x1, double y1, double x2, double y2) {
    }

    public void clearSelection() {
        dragEnd = Optional.empty();
        dragStart = Optional.empty();
    }

    public double[] getSelection() {
        return new double[]{this.dragStart.get().getX(), this.dragStart.get().getY(),
                this.dragEnd.get().getX(), this.dragEnd.get().getY()};
    }

    public static enum Tool {
        CURSOR,
        MOVE,
        SELECTION,
        RULER;
    }
}

