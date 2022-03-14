package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.util.Direction;

import java.util.List;
import java.util.Optional;

public class AnchorPoint {

    public RoomComponent room;
    public Direction directionFacing;
    public Direction side;

    public Optional<RoomComponent> neighborTopLeft = Optional.empty();
    public Optional<RoomComponent> neighborTopRight = Optional.empty();
    public Optional<RoomComponent> neighborBottomLeft = Optional.empty();
    public Optional<RoomComponent> neighborBottomRight = Optional.empty();

    public AnchorPoint(RoomComponent room, Direction side, Direction directionFacing) {
        this.room = room;
        this.side = side;
        this.directionFacing = directionFacing;
    }

    public static AnchorPoint create(List<RoomComponent> graph, RoomComponent room, Direction side, Direction directionFacing) {
        AnchorPoint a = new AnchorPoint(room, side, directionFacing);
        boolean obstructed = a.calcNeighbors(graph);
        if(obstructed) return null;
        else return a;
    }

    private boolean calcNeighbors(List<RoomComponent> graph) {
        final double margin = 0.01;
        double s = directionFacing.isHorizontal() ? room.getBoundingBox().getWidth() : room.getBoundingBox().getHeight();
        double lx = this.getX() - this.directionFacing.dx * margin;
        double ly = this.getY() - this.directionFacing.dy * margin;
        double rx = lx + this.directionFacing.dx * (s + margin);
        double ry = ly + this.directionFacing.dy * (s + margin);

        for(RoomComponent r : graph) {
            BoundingBox bb = r.getBoundingBox();
            if(bb.containsPoint(lx + (this.side.dx + 2 * this.directionFacing.dx) * margin, ly + (this.side.dy + 2 * this.directionFacing.dy) * margin)) {
                return true;
            } else if(bb.containsPoint(lx + this.side.dx * margin, ly + this.side.dy * margin)) {
                this.neighborTopLeft = Optional.of(r);
            } else if (bb.containsPoint(lx - this.side.dx * margin, ly - this.side.dy * margin)) {
                this.neighborBottomLeft = Optional.of(r);
            } else if(bb.containsPoint(rx + this.side.dx * margin, ry + this.side.dy * margin)) {
                this.neighborTopRight = Optional.of(r);
            } else if (bb.containsPoint(rx - this.side.dx * margin, ry - this.side.dy * margin)) {
                this.neighborBottomRight = Optional.of(r);
            }
        }

        return false;
    }

    public double getX() {
        return room.getBoundingBox().x + ((side.dx + directionFacing.getOpposite().dx > 0) ? room.getBoundingBox().getWidth() : 0);
    }

    public double getY() {
        return room.getBoundingBox().y + ((side.dy + directionFacing.getOpposite().dy > 0) ? room.getBoundingBox().getHeight() : 0);
    }
}