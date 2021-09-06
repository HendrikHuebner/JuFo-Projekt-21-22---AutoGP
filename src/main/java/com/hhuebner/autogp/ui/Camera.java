package com.hhuebner.autogp.ui;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;

public class Camera {

    private Affine transform;

    public Camera() {
        this.transform = new Affine();
    }

    public void move(Point2D p) {
        this.transform.appendTranslation(p.getX(), p.getY());
    }

    public void setPos(Point2D p) {
        this.transform.setTx(p.getX() * getScaleX());
        this.transform.setTy(p.getY() * getScaleY());
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
