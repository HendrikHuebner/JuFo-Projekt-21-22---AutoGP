package com.hhuebner.autogp.core.component;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.Connection;
import com.hhuebner.autogp.core.engine.RoomType;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.options.OptionsHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class WallComponent extends PlanComponent {

    private static Map<RoomType, Color> typeColors = new HashMap<>();

    static {
        typeColors.put(RoomType.LIVING_ROOM, Color.YELLOW);
        typeColors.put(RoomType.BED_ROOM, Color.GREEN);
        typeColors.put(RoomType.KITCHEN, Color.ORANGE);
        typeColors.put(RoomType.BATH_ROOM, Color.LIGHTBLUE);
        typeColors.put(RoomType.HALLWAY, Color.LIGHTGREY);
    }

    private final RoomComponent roomComponent;
    @JsonManagedReference
    private List<Connection> connections = new ArrayList<>();
    private List<WallPosition> windows = new ArrayList<>();

    public WallComponent(RoomComponent component, String name, int id) {
        super(name, id);

        this.roomComponent = component;
    }

    @Override
    public void render(GraphicsContext ctx, InputHandler inputHandler) {
        ctx.save();

        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(4);
        BoundingBox bb = this.roomComponent.bb;
        drawInnerWall(ctx, this.roomComponent, inputHandler);

        for(Direction side : Direction.values()) {
            double x1 = side == Direction.EAST ? bb.x2 : bb.x;
            double y1 = side == Direction.SOUTH ? bb.y2 : bb.y;
            double x2 = side == Direction.WEST ? bb.x : bb.x2;
            double y2 = side == Direction.NORTH ? bb.y : bb.y2;
            double start = 0.0;

            for(Connection p : this.connections) {
                if (p.side() != side) continue;

                if(p.start() > start) {
                    outerWallLine(ctx, inputHandler, x1, x2, y1, y2, side, start, p.start());
                }

                start = p.end();
            }

            double end = side.isHorizontal() ? bb.getHeight() :  bb.getWidth();

            if(start < end) {
                outerWallLine(ctx, inputHandler, x1, x2, y1, y2, side, start, end);
            }
        }

        ctx.restore();
    }

    private void outerWallLine(GraphicsContext ctx, InputHandler handler, double x1, double x2, double y1, double y2, Direction side, double start, double end) {
        double d = side.isHorizontal() ? side.dx : side.dy;
        boolean shortenL = false;
        boolean lengthenL = true;
        boolean shortenR = false;
        boolean lengthenR = true;

        for(Connection c : this.connections) {
            if(side.isHorizontal()) {
                if (c.roomComponent().bb.containsPoint(x1 + side.dx * 0.01, y1 + start - 0.01)) {
                    shortenL = true;
                    break;
                }
                if (c.roomComponent().bb.containsPoint(x1 - side.dx * 0.01, y1 + start - 0.01)) {
                    lengthenL = false;
                }
                if (c.roomComponent().bb.containsPoint(x1 + side.dx * 0.01, y1 + end + 0.01)) {
                    shortenR = true;
                    break;
                }
                if (c.roomComponent().bb.containsPoint(x1 - side.dx * 0.01, y1 + end + 0.01)) {
                    lengthenR = false;
                }
            } else {
                if (c.roomComponent().bb.containsPoint(x1 + start - 0.01, y1 + side.dy * 0.01)) {
                    shortenL = true;
                    break;
                }
                if (c.roomComponent().bb.containsPoint(x1 + start - 0.01, y1 - side.dy * 0.01)) {
                    lengthenL = false;
                }
                if (c.roomComponent().bb.containsPoint(x1 + end + 0.01, y1 + side.dy * 0.01)) {
                    shortenR = true;
                    break;
                }
                if (c.roomComponent().bb.containsPoint(x1 + end + 0.01, y1 - side.dy * 0.01)) {
                    lengthenR = false;
                }
            }
        }

        final double OUTER_WALL_THICKNESS = OptionsHandler.INSTANCE.outerWallWidth.get();

        double l = 0.0;
        if(shortenL) {
            l -= OUTER_WALL_THICKNESS;
        } else if(lengthenL) {
            l += OUTER_WALL_THICKNESS;
        }

        double r = 0.0;
        if(shortenR) {
            r += OUTER_WALL_THICKNESS;
        } else if(lengthenR) {
            r -= OUTER_WALL_THICKNESS;
        }

        if(side.isHorizontal()) {
            drawScaledLine(ctx, x1 + d * OUTER_WALL_THICKNESS, y1 + start - l, x2 + d * OUTER_WALL_THICKNESS, y1 + end - r, handler);
        } else {
            drawScaledLine(ctx, x1 + start - l, y1 + d * OUTER_WALL_THICKNESS, x1 + end - r, y2 + d * OUTER_WALL_THICKNESS, handler);
        }
    }

    private void drawInnerWall(GraphicsContext ctx, RoomComponent component, InputHandler handler) {
        final double INNER_WALL_THICKNESS = OptionsHandler.INSTANCE.innerWallWidth.get();
        BoundingBox bb = component.getBoundingBox();

        double scaledX = ( bb.x + INNER_WALL_THICKNESS) * CELL_SIZE;
        double scaledY = (bb.y + INNER_WALL_THICKNESS) * CELL_SIZE;
        double scaledW = (bb.getWidth() - 2 * INNER_WALL_THICKNESS) * CELL_SIZE;
        double scaledH = (bb.getHeight() - 2 * INNER_WALL_THICKNESS) * CELL_SIZE;


        if(OptionsHandler.INSTANCE.colorRooms.get()) {
            ctx.save();
            if (handler.selectedRoom.isPresent()) {
                ctx.setGlobalAlpha(handler.selectedRoom.get().equals(this.roomComponent) ? 0.5 : 0.2);
            } else {
                ctx.setGlobalAlpha(0.2);
            }

            ctx.setFill(typeColors.get(component.room.type));
            ctx.fillRect(scaledX, scaledY, scaledW, scaledH);
            ctx.restore();
        }
        ctx.strokeRect(scaledX, scaledY, scaledW, scaledH);
    }

    private void drawScaledLine(GraphicsContext ctx, double x, double y, double x2, double y2, InputHandler handler) {
        double scaledX = x * CELL_SIZE;
        double scaledY = y * CELL_SIZE;
        double scaledX2 = x2 * CELL_SIZE;
        double scaledY2 = y2 * CELL_SIZE;
        ctx.strokeLine(scaledX, scaledY, scaledX2, scaledY2);
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public List<Connection> getConnections() {
        return this.connections;
    }

    /**
     * marks out an interval on the given side of a room.
     */
    public record WallPosition(Direction side, double start, double end) {}
}
