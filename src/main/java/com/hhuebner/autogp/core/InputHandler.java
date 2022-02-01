package com.hhuebner.autogp.core;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.util.Pair;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.Utility;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

import java.util.Optional;
import java.util.function.Supplier;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class InputHandler {

    private final GPEngine engine;
    private final Supplier<Scene> scene;
    public Optional<Point2D> dragStart = Optional.empty();
    public Optional<Point2D> dragEnd = Optional.empty();
    public Unit displayUnit = Unit.METRES;
    public double globalScale = 1.0;
    private Tool tool = Tool.MOVE;
    private Optional<InteractableComponent> selected = Optional.empty();
    private Optional<DragMode> selectedDragMode = Optional.empty();


    public InputHandler(Supplier<Scene> scene, GPEngine engine) {
        this.scene = scene;
        this.engine = engine;
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
        //Check if a resize box has been clicked
        if(this.selected.isPresent()) {
            BoundingBox bb = this.selected.get().getBoundingBox();
            final double o = 12; //half of the side length of the clickable box

            double x = Utility.calcPixels(bb.x, this) * CELL_SIZE;
            double x2 = Utility.calcPixels(bb.x2, this) * CELL_SIZE;
            double y = Utility.calcPixels(bb.y, this) * CELL_SIZE;
            double y2 = Utility.calcPixels(bb.y2, this) * CELL_SIZE;
            double w = (x2 - x) / 2; //half width of bb
            double h = (y2 - y) / 2; //half height of bb

            if(new BoundingBox(x + w - o, y - o, x + w + o, y + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.NORTH); return;
            } else if(new BoundingBox(x + w - o, y2 - o, x + w + o, y2 + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.SOUTH); return;
            } else if(new BoundingBox(x - o, y + h - o, x + o, y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.WEST); return;
            } else if(new BoundingBox(x2 - o, y + h - o, x2 + o, y + h + o)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.EAST); return;
            }
        }

        double mouseX = mouseXAbsolute / (this.displayUnit.factor * GPEngine.CELL_SIZE * globalScale);
        double mouseY = mouseYAbsolute / (this.displayUnit.factor * GPEngine.CELL_SIZE * globalScale);

        for(RoomComponent roomComponent : this.engine.getComponents()) {
            AutoGP.log(roomComponent.getName(), roomComponent.getBoundingBox());
            for(PlanComponent component : roomComponent.getChildren()) {
                if(component instanceof InteractableComponent) {
                    if(selectComponentAtPoint(roomComponent, mouseX, mouseY)) return;
                }
            }

            if(selectComponentAtPoint(roomComponent, mouseX, mouseY)) return;
        }
    }

    private boolean selectComponentAtPoint(InteractableComponent component, double mouseX, double mouseY) {
        if (component.getBoundingBox().containsPoint(mouseX, mouseY)) {
            if (this.selected.isPresent() && this.selected.get().equals(component)) {
                //if selected component was clicked again, set drag mode to MOVE
                this.selectedDragMode = Optional.of(DragMode.MOVE);

            } else {
                this.selected = Optional.of(component);
            }

            return true;
        } else {
            this.selected = Optional.empty();
            return false;
        }
    }

    public void handleCursorDrag(double startX, double startY, double currentX, double currentY) {
        if(this.selected.isPresent() && this.selectedDragMode.isPresent()) {
            DragMode dragMode = this.selectedDragMode.get();
            InteractableComponent component = this.selected.get();
            double dxPX = currentX - startX; //in pixels
            double dyPX = currentY - startY;

            //pixels to meters
            double dx = dxPX / CELL_SIZE / this.globalScale;
            double dy = dyPX / CELL_SIZE / this.globalScale;

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

