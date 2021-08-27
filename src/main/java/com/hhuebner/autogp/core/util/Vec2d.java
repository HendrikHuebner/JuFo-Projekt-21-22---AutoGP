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

    public void add(Vec2d vec) {
        this.x += vec.x;
        this.y += vec.y;
    }

    public void sub(Vec2d vec) {
        this.x -= vec.x;
        this.y -= vec.y;
    }

    public void scale(double s) {
        this.x *= s;
        this.y *= s;
    }
}
