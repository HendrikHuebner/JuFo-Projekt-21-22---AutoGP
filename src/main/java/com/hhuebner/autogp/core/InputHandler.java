package com.hhuebner.autogp.core;

import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.DragMode;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.UnitSq;
import com.hhuebner.autogp.options.OptionsHandler;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.widgets.GroundPlanTab;
import javafx.geometry.Point2D;

import java.util.Optional;
import java.util.function.Supplier;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class InputHandler {

    private final GPEngine engine;
    private final Supplier<MainSceneController> scene;
    private final Camera camera;

    public Optional<Point2D> dragStart = Optional.empty();
    public Optional<Point2D> dragEnd = Optional.empty();
    public Unit displayUnit = Unit.METRES;
    public double globalScale = 1.0;
    public double gpSize = OptionsHandler.INSTANCE.defaultGPSize.get();
    public UnitSq gpSizeUnit = UnitSq.METRES;

    private Tool tool = Tool.MOVE;
    public Optional<RoomComponent> selectedRoom = Optional.empty();
    public Optional<InteractableComponent> selectedComponent = Optional.empty();

    private Optional<DragMode> selectedDragMode = Optional.empty();


    public InputHandler(Supplier<MainSceneController> scene, GPEngine engine, Camera camera) {
        this.scene = scene;
        this.engine = engine;
        this.camera = camera;
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

    public void onCursorPress(double mouseXAbsolute, double mouseYAbsolute) {
        //Check if a resize box has been clicked
        BoundingBox bb = null;

        if(this.selectedComponent.isPresent()) {
            bb = this.selectedComponent.get().getBB();
        }else if(this.selectedRoom.isPresent()) {
            bb = this.selectedRoom.get().getBB();
        }

        if(bb != null) {
            final double ox = 12 / this.camera.getScaleX(); //half of the side length of the clickable box
            final double oy = 12 / this.camera.getScaleY(); //half of the side length of the clickable box

            System.out.println(this.camera.getScaleY());

            double x = bb.x * CELL_SIZE;
            double x2 = bb.x2 * CELL_SIZE;
            double y = bb.y * CELL_SIZE;
            double y2 = bb.y2 * CELL_SIZE;
            double w = (x2 - x) / 2; //half width of bb
            double h = (y2 - y) / 2; //half height of bb

            if(new BoundingBox(x + w - ox, y - oy, x + w + ox, y + oy)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.NORTH); return;
            } else if(new BoundingBox(x + w - ox, y2 - oy, x + w + ox, y2 + oy)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.SOUTH); return;
            } else if(new BoundingBox(x - ox, y + h - oy, x + ox, y + h + oy)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.WEST); return;
            } else if(new BoundingBox(x2 - ox, y + h - oy, x2 + ox, y + h + oy)
                    .containsPoint(mouseXAbsolute, mouseYAbsolute)) {
                this.selectedDragMode = Optional.of(DragMode.EAST); return;
            }
        }

        double mouseX = mouseXAbsolute / GPEngine.CELL_SIZE;
        double mouseY = mouseYAbsolute / GPEngine.CELL_SIZE;

        if(this.selectedRoom.isEmpty()) {
            //select room
            for(RoomComponent roomComponent : this.engine.getSelectedGP().components) {
                if (selectComponentAtPoint(roomComponent, mouseX, mouseY)) return;
            }
        } else {
            //first, try to select components within the room
            RoomComponent roomComponent = this.selectedRoom.get();
            for(PlanComponent component : roomComponent.getChildren()) {
                if(component instanceof InteractableComponent) {
                    if(!((InteractableComponent) component).isClickable()) continue;
                    if(selectComponentAtPoint((InteractableComponent) component, mouseX, mouseY)) return;
                }
            }

            //check if the click was even inside the room (important for moving)
            if(selectComponentAtPoint(roomComponent, mouseX, mouseY)) return;

            //check remaining rooms
            for(RoomComponent r : this.engine.getSelectedGP().components) {
                if (selectComponentAtPoint(r, mouseX, mouseY)) return;
            }

            this.clearSelectedComponent();
        }
    }

    public void onCursorRealease(double mouseXAbsolute, double mouseYAbsolute) {

    }

    private boolean selectComponentAtPoint(InteractableComponent component, double mouseX, double mouseY) {
        if (component.getBB().containsPoint(mouseX, mouseY)) {
            if(component instanceof RoomComponent) {
                this.selectedComponent = Optional.empty();

                if (this.selectedRoom.isPresent() && this.selectedRoom.get().equals(component)) {
                    //move
                    this.selectedDragMode = Optional.of(DragMode.MOVE);

                } else {
                    //select
                    this.selectedRoom = Optional.of((RoomComponent) component);
                    this.scene.get().onSelectRoom((RoomComponent) component);
                }
            } else {
                //room must be selected already
                if (this.selectedComponent.isPresent() && this.selectedComponent.get().equals(component)) {
                    this.selectedDragMode = Optional.of(DragMode.MOVE);
                } else {
                    this.selectedComponent = Optional.of(component);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void clearSelectedComponent() {
        this.selectedComponent = Optional.empty();
        this.selectedRoom = Optional.empty();
        this.scene.get().onDeselectRoom();
    }

    public void handleCursorDrag(double startX, double startY, double currentX, double currentY) {
        if(this.selectedRoom.isPresent() && this.selectedDragMode.isPresent()) {
            DragMode dragMode = this.selectedDragMode.get();
            //if component is selected, move it, otherwise move room
            InteractableComponent component = this.selectedComponent.orElse(this.selectedRoom.get());
            double dxPX = currentX - startX; //in pixels
            double dyPX = currentY - startY;

            //pixels to meters
            double dx = dxPX / CELL_SIZE / this.globalScale;
            double dy = dyPX / CELL_SIZE / this.globalScale;

            switch(dragMode) {
                case MOVE -> component.getBB().move(dx, dy);
                case NORTH -> component.getBB().y += dy;
                case SOUTH -> component.getBB().y2 += dy;
                case WEST -> component.getBB().x += dx;
                case EAST -> component.getBB().x2 += dx;
            }

            engine.updateConnections();
        }
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

    public void clearSelectionBox() {
        dragEnd = Optional.empty();
        dragStart = Optional.empty();
    }

    public double[] getSelection() {
        return new double[]{this.dragStart.get().getX(), this.dragStart.get().getY(),
                this.dragEnd.get().getX(), this.dragEnd.get().getY()};
    }

    public void closeTab(GroundPlanTab tab) {
        this.clearSelectedComponent();
        this.engine.groundPlanMap.remove(tab.getID());
    }

    public static enum Tool {
        CURSOR,
        MOVE,
        SELECTION,
    }
}

