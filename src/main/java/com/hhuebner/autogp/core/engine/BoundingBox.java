package com.hhuebner.autogp.core.engine;

public class BoundingBox {

    public static final BoundingBox EMPTY = new BoundingBox(0, 0, 0, 0);

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

    public BoundingBox(BoundingBox bb) {
        this.x = bb.x;
        this.y = bb.y;
        this.x2 = bb.x2;
        this.y2 = bb.y2;
    }

    /**
     * Returns true if BoundingBox intersects with parameter bb. Returns false if they are adjacent.
     * @param bb
     * @return
     */
    public boolean intersects(BoundingBox bb) {
        //check for intersection
        return ((this.x > bb.x && this.x < bb.x2 || bb.x > this.x && bb.x < this.x2) ||
                //check x for adjacency
                (this.x == bb.x && this.x2 > this.x == bb.x2 > bb.x) ||
                (this.x2 == bb.x && this.x > this.x2 == bb.x2 > bb.x) ||
                (this.x == bb.x2 && this.x2 > this.x == bb.x > bb.x2) ||
                (this.x2 == bb.x2 && this.x > this.x2 == bb.x > bb.x2)) &&
                //check y for adjacency
                ((this.y > bb.y && this.y < bb.y2 || bb.y > this.y && bb.y < this.y2) ||
                (this.y == bb.y && this.y2 > this.y == bb.y2 > bb.y) ||
                (this.y2 == bb.y && this.y > this.y2 == bb.y2 > bb.y) ||
                (this.y == bb.y2 && this.y2 > this.y == bb.y > bb.y2) ||
                (this.y2 == bb.y2 && this.y > this.y2 == bb.y > bb.y2));
    }

    /**
     * Returns true if BoundingBox contains the point at (x, y)
     * @param x
     * @param y
     * @return
     */
    public boolean containsPoint(double x, double y) {
        return this.y <= y && this.y2 >= y && this.x <= x && this.x2 >= x;
    }

    /**
     * Moves BoundingBox by parameters dx and dy
     * @param dx
     * @param dy
     */
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

    @Override
    public String toString() {
        return "BoundingBox{" +
                "x=" + x +
                ", y=" + y +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    /**
     * If this BoundingBox does not contain bb, scale it up just enough, so it contains bb
     * @param bb
     */
    public void encompass(BoundingBox bb) {
        this.x = Math.min(this.x, bb.x);
        this.x2 = Math.max(this.x2, bb.x2);
        this.y = Math.min(this.y, bb.y);
        this.y2 = Math.max(this.y2, bb.y2);
    }
}