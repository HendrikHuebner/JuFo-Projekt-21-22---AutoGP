package com.hhuebner.autogp.core.engine;

public class BoundingBox {

    private final double x;
    private final double y;
    private final double x2;
    private final double y2;

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
}