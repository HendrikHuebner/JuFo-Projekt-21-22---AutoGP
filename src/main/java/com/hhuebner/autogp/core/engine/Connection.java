package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.util.Direction;

import java.util.Arrays;

import static com.hhuebner.autogp.core.util.Utility.epsEquals;

public record Connection(RoomComponent roomComponent, Direction side, double start, double end) {


    public static Connection getConnection(RoomComponent r1, RoomComponent r2) {
        BoundingBox bb1 = new BoundingBox(r1.getBoundingBox());
        BoundingBox bb2 = new BoundingBox(r2.getBoundingBox());

        Direction direction = null;
        if (epsEquals(bb1.x, bb2.x2, 0.05)) direction = Direction.WEST;
        if (epsEquals(bb1.x2, bb2.x, 0.05)) direction = Direction.EAST;
        if (epsEquals(bb1.y, bb2.y2, 0.05)) direction = Direction.NORTH;
        if (epsEquals(bb1.y2, bb2.y, 0.05)) direction = Direction.SOUTH;

        if (direction != null) {
            double start;
            double end;
            double[] arr;

            if (direction.isHorizontal()) {
                if(bb1.y2 < bb2.y || bb2.y2 < bb1.y) return null;
                arr = new double[]{bb1.y, bb1.y2, bb2.y, bb2.y2};
            } else {
                if(bb1.x2 < bb2.x || bb2.x2 < bb1.x) return null;
                arr = new double[]{bb1.x, bb1.x2, bb2.x, bb2.x2};
            }

            Arrays.sort(arr);
            start = arr[1] - (direction.isHorizontal() ? bb1.y : bb1.x);
            end = arr[2] - (direction.isHorizontal() ? bb1.y : bb1.x);

            if(end - start > 0)
                return new Connection(r2, direction, start, end);
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
