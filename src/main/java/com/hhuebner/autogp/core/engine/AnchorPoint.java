package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.util.Direction;

import java.util.List;
import java.util.Optional;

public class AnchorPoint {

    public Room room;
    public Direction directionFacing;
    public Direction side;

    public Optional<Room> neighborTopLeft = Optional.empty();
    public Optional<Room> neighborTopRight = Optional.empty();
    public Optional<Room> neighborBottomLeft = Optional.empty();
    public Optional<Room> neighborBottomRight = Optional.empty();

    public AnchorPoint(List<Room> graph, Room room, Direction side, Direction directionFacing) {
        this.room = room;
        this.side = side;
        this.directionFacing = directionFacing;

        this.calcNeighbors(graph);
    }

    private void calcNeighbors(List<Room> graph) {
        final double margin = 0.01;
        double s = directionFacing.isHorizontal() ? room.boundingBox.getWidth() : room.boundingBox.getHeight();
        double lx = this.getX() - this.directionFacing.dx * margin;
        double ly = this.getY() - this.directionFacing.dy * margin;
        double rx = lx + this.directionFacing.dx * (s + margin);
        double ry = ly + this.directionFacing.dy * (s + margin);

        for(Room r : graph) {
            if(r.boundingBox.containsPoint(lx + this.side.dx * margin, ly + this.side.dy * margin)) {
                this.neighborTopLeft = Optional.of(r);
            } else if (r.boundingBox.containsPoint(lx - this.side.dx * margin, ly - this.side.dy * margin)) {
                this.neighborBottomLeft = Optional.of(r);
            } else if(r.boundingBox.containsPoint(rx + this.side.dx * margin, ry + this.side.dy * margin)) {
                this.neighborTopRight = Optional.of(r);
            } else if (r.boundingBox.containsPoint(rx - this.side.dx * margin, ry - this.side.dy * margin)) {
                this.neighborBottomRight = Optional.of(r);
            }
        }
    }

    public double getX() {
        return room.boundingBox.x + ((side.dx + directionFacing.getOpposite().dx > 0) ? room.boundingBox.getWidth() : 0);
    }

    public double getY() {
        return room.boundingBox.y + ((side.dy + directionFacing.getOpposite().dy > 0) ? room.boundingBox.getHeight() : 0);
    }
}