package com.hhuebner.autogp.core;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.util.Vec2d;
import javafx.scene.Scene;

import java.util.Optional;
import java.util.function.Supplier;

public class InputHandler {

    private final double RESIZE_BOX_SIZE = 20;

    private final GPEngine engine;
    private Supplier<Scene> scene;
    public Optional<Vec2d> dragStart = Optional.empty();
    public Optional<Vec2d> dragEnd = Optional.empty();
    public Vec2d prevMousePos = new Vec2d(0, 0);
    private Tool tool = Tool.MOVE;
    private Optional<PlanComponent> selected = Optional.empty();
    private Optional<DragMode> selectedDragDirection = Optional.empty();

    public InputHandler(Supplier<Scene> scene, GPEngine engine) {
        this.scene = scene;
        this.engine = engine;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        System.out.println(this.scene.get());
    }

    public Tool getTool() {
        return this.tool;
    }

    public void onCursorClick(double mouseXAbsolute, double mouseYAbsolute) {
        AutoGP.log("selected:", this.selectedDragDirection);

        //Check if a resize box has been clicked
        if(this.selected.isPresent()) {
            BoundingBox bb = this.selected.get().getBoundingBox();
            double w = (bb.x2 - bb.x) / 2; //half width of bb
            double h = (bb.y2 - bb.y) / 2; //half height of bb
            double o = RESIZE_BOX_SIZE / 2; //half resize box offset
            
            if(new BoundingBox(bb.x + w - o, bb.y - o, bb.x + w + o, bb.y + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragDirection = Optional.of(DragMode.NORTH); return;
            } else if(new BoundingBox(bb.x + w - o, bb.y2 - o, bb.x + w + o, bb.y2 + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragDirection = Optional.of(DragMode.SOUTH); return;
            } else if(new BoundingBox(bb.x - o, bb.y + h - o, bb.x + o, bb.y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragDirection = Optional.of(DragMode.WEST); return;
            } else if(new BoundingBox(bb.x2 - o, bb.y + h - o, bb.x2 + o, bb.y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragDirection = Optional.of(DragMode.EAST); return;
            }
        }
        
        for(PlanComponent component : this.engine.getComponents()) {
            if(component.getBoundingBox().containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                if(this.selected.isPresent() && this.selected.get().equals(component)) {
                    //if selected component was clicked again, set drag mode to MOVE
                    this.selectedDragDirection = Optional.of(DragMode.MOVE);
                } else
                    this.selected = Optional.of(component);
            } else
                this.selected = Optional.empty();
        }
    }

    public Optional<PlanComponent> getSelectedComponent() {
        return this.selected;
    }

    //Selection Tool
    public boolean hasSelection() {
        return this.dragStart.isPresent() && this.dragEnd.isPresent();
    }

    public void handleSelection() {
    }

    public void clearSelection() {
        dragEnd = Optional.empty();
        dragStart = Optional.empty();
    }

    public double[] getSelection() {
        return new double[]{this.dragStart.get().x, this.dragStart.get().y, this.dragEnd.get().x, this.dragEnd.get().y};
    }

    public static enum Tool {
        CURSOR,
        MOVE,
        SELECTION;
    }
}

