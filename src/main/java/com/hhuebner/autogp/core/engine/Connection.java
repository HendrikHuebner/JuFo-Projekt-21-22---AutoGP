package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.util.Direction;

public class Connection {

    private final RoomComponent roomComponent;
    private final Direction side;
    private final double end;
    private final double start;

    public Connection(RoomComponent roomComponent, Direction side, double start, double end) {
        this.roomComponent = roomComponent;
        this.side = side;
        this.start = start;
        this.end = end;
    }

    public static Connection getConnection(RoomComponent r1, RoomComponent r2) {
        BoundingBox bb1 = r1.getBoundingBox();
        BoundingBox bb2 = r2.getBoundingBox();

        boolean xIntersection1 = bb1.x >= bb2.x && bb1.x < bb2.x2;
        boolean xIntersection2 = bb2.x >= bb1.x && bb2.x < bb1.x2;
        boolean yIntersection1 = bb1.y >= bb2.y && bb1.y < bb2.y2;
        boolean yIntersection2 = bb2.y >= bb1.y && bb2.y < bb1.y2;


        Direction direction = null;
        if(bb1.x == bb2.x2) direction = Direction.WEST;
        if(bb1.x2 == bb2.x ) direction = Direction.EAST;
        if(bb1.y == bb2.y2) direction = Direction.NORTH;
        if(bb1.y2 == bb2.y) direction = Direction.SOUTH;

        if(direction != null) {
            if(direction.isHorizontal()) {
                if(yIntersection1) {
                    return new Connection(r2, direction, bb2.y2, bb1.y);
                } else if(yIntersection2) {
                    return new Connection(r2, direction, bb2.y, bb1.y2);
                }
            } else {
                if(xIntersection1) {
                    return new Connection(r2, direction, bb2.x2, bb1.x);
                } else if(xIntersection2) {
                    return new Connection(r2, direction, bb2.x, bb1.x2);
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "roomComponent=" + roomComponent.getName() +
                ", side=" + side +
                ", end=" + end +
                ", start=" + start +
                '}';
    }

    public Direction getSide() {
        return side;
    }
}
