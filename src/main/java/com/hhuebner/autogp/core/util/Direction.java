package com.hhuebner.autogp.core.util;

public enum Direction {
    NORTH(0, -1, 0),
    SOUTH(0, 1, 180),
    EAST(1, 0, 90),
    WEST(-1, 0, 270);

    public final int dx;
    public final int dy;
    public final double angle;

    Direction(int dx, int dy, double angle) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
    }

    public Direction getOpposite() {
        return switch(this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public Direction rotateCW() {
        return switch(this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case EAST -> SOUTH;
            case WEST -> NORTH;
        };
    }

    public Direction rotateCCW() {
        return switch(this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case EAST -> NORTH;
            case WEST -> SOUTH;
        };
    }

    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }
}
