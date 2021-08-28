package com.hhuebner.autogp.core.util;

public class Vec2d {

    public double x;
    public double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2d vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vec2d add(Vec2d vec) {
        return new Vec2d(this.x + vec.x, this.y + vec.y);
    }

    public Vec2d sub(Vec2d vec) {
        return new Vec2d(this.x - vec.x, this.y - vec.y);
    }

    public Vec2d scale(double s) {
        return new Vec2d(this.x * s, this.y * s);
    }
}
