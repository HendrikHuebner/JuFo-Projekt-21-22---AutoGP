package com.hhuebner.autogp.ui;

import com.hhuebner.autogp.core.util.Vec2d;
import javafx.scene.transform.Affine;

public class Camera {

    private Affine transform;

    public Camera() {
        this.transform = new Affine();
    }

    public void move(Vec2d vec) {
        this.transform.prependTranslation(vec.x, vec.y);
    }

    public void setPos(Vec2d vec) {
        this.transform.setTx(vec.x * getScaleX());
        this.transform.setTy(vec.y * getScaleY());
    }

    /**
     * multiplier must be greater than 0 for zooming in and smaller than 0 for zooming out
     * @param multiplier
     */
    public void zoom(double multiplier) {
        this.transform.appendScale(multiplier, multiplier);
    }

    public Affine getTransform() {
        return this.transform;
    }

    public double getY() {
        return this.transform.getTy() / this.transform.getMyy();
    }

    public double getX() {
        return this.transform.getTx() / this.transform.getMxx();
    }

    public double getScaleX() {
        return this.transform.getMxx();
    }

    public double getScaleY() {
        return this.transform.getMyy();
    }
}
