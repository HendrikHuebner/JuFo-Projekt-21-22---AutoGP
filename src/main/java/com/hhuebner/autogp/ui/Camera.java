package com.hhuebner.autogp.ui;

import com.hhuebner.autogp.core.util.Vec2d;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;

public class Camera {

    private Affine transform;
    private double zoom = 1.0;

    public Camera() {
        this.transform = new Affine();
    }

    public void move(Vec2d vec) {
        this.transform.appendTranslation(vec.x, vec.y);
    }

    public void setPos(Vec2d vec) {
        this.transform.setTx(vec.x * zoom);
        this.transform.setTy(vec.y * zoom);
    }

    /**
     * multiplier must be greater than 0 for zooming in and smaller than 0 for zooming out
     * @param multiplier
     */
    public void zoom(double multiplier) {
        this.zoom *= multiplier;
        this.transform.appendScale(multiplier, multiplier);
    }

    public Affine getTransform() {
        return this.transform;
    }
}
