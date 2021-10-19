package com.hhuebner.autogp.core.engine;

public class BoundingBox {

    public double x;
    public double y;
    public double x2;
    public double y2;

    public BoundingBox(double x, double y, double x2, double y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean intersects(BoundingBox bb) {
        return (this.x >= bb.x && this.x <= bb.x2 || bb.x >= this.x && bb.x <= this.x2) &&
                (this.y >= bb.y && this.y <= bb.y2 || bb.y >= this.y && bb.y <= this.y2);
    }

    public boolean containsPoint(double x, double y) {
        return this.y <= y && this.y2 >= y && this.x <= x && this.x2 >= x;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        this.x2 += dx;
        this.y2 += dy;
    }

    public double getWidth() {
        return this.x2 - this.x;
    }

    public double getHeight() {
        return this.y2 - this.y;
    }
}