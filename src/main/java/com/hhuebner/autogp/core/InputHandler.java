package com.hhuebner.autogp.core;

import com.hhuebner.autogp.core.util.Vec2d;
import javafx.scene.Scene;

import java.util.Optional;
import java.util.function.Supplier;

public class InputHandler {

    private Supplier<Scene> scene;
    public Optional<Vec2d> dragStart = Optional.empty();
    public Optional<Vec2d> dragEnd = Optional.empty();
    public Vec2d prevMousePos = new Vec2d(0, 0);
    private Tool tool = Tool.MOVE;

    public InputHandler(Supplier<Scene> scene) {
        this.scene = scene;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        System.out.println(this.scene.get());
    }

    public Tool getTool() {
        return this.tool;
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

