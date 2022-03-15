package com.hhuebner.autogp.core.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.options.OptionsHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DoorComponent extends InteractableComponent {

    private final Direction side;
    private final boolean openingLeft;
    private final double clearance;

    @JsonCreator
    private DoorComponent(@JsonProperty("boundingBox") BoundingBox boundingBox,
                          @JsonProperty("side") Direction side,
                          @JsonProperty("openingLeft") boolean openingLeft,
                          @JsonProperty("clearance") double clearance,
                          @JsonProperty("name") String name,
                          @JsonProperty("id") long id) {
        super(boundingBox, name, id);
        
        this.side = side;
        this.openingLeft = openingLeft;
        this.clearance = clearance;
    }

    public static DoorComponent create(RoomComponent component, double start, double end, Direction side, String name, long id) {
        double a = side == Direction.EAST ? component.bb.x2 : component.bb.x;
        double b = side == Direction.SOUTH ? component.bb.y2 : component.bb.y;
        double clearance = (end - start) * OptionsHandler.INSTANCE.doorClearanceFactor.get();

        BoundingBox bb;
        boolean openingLeft;

        if(side.isHorizontal()) {
            openingLeft = start < component.bb.getHeight() - end ^ side == Direction.WEST;
            bb = new BoundingBox(a, b + start, a + clearance,b + end);
        } else {
            openingLeft = start < component.bb.getWidth() - end ^ side == Direction.SOUTH;
            bb = new BoundingBox(a + start, b , a + end, b + clearance);
        }

        if(side == Direction.EAST) bb.move(-bb.getWidth(), 0);
        if(side == Direction.SOUTH) bb.move(0, -bb.getHeight());

        switch(side) {
            case NORTH -> bb.y -= clearance;
            case SOUTH -> bb.y2 += clearance;
            case WEST -> bb.x -= clearance;
            case EAST -> bb.x2 += clearance;
        }

        return new DoorComponent(bb, side, openingLeft, clearance, name, id);
    }


    @Override
    public void render(GraphicsContext ctx, InputHandler handler) {
        double width = side.isHorizontal() ? this.bb.y2 - this.bb.y : this.bb.x2 - this.bb.x;

        ctx.save();
        double scaledX = bb.x * CELL_SIZE;
        double scaledY = bb.y * CELL_SIZE;
        double scaledW = this.bb.getWidth() * CELL_SIZE;
        double scaledH = this.bb.getHeight() * CELL_SIZE;
        double scaledC = this.clearance* CELL_SIZE;

        if(OptionsHandler.INSTANCE.DEBUG) {
            ctx.setStroke(Color.BLUE);
            ctx.strokeRect(scaledX, scaledY, scaledW, scaledH);
            ctx.setStroke(Color.BLACK);
        }

        if(OptionsHandler.INSTANCE.showDoorHitBoxes.get()) {
            ctx.setStroke(Color.RED);
            ctx.strokeRect(scaledX, scaledY, scaledW, scaledH);
        }

        if(this.side == Direction.EAST)
            scaledX += scaledW;

        if(this.side == Direction.SOUTH) {
            scaledX += scaledW;
            scaledY += scaledH;
        }

        ctx.setStroke(Color.BLACK);
        ctx.setLineWidth(4);


        ctx.translate(scaledX, scaledY);
        ctx.rotate(side.angle);
        ctx.translate(0, scaledC);

        if(!openingLeft) {
            if(side != Direction.WEST) {
                ctx.translate(width* CELL_SIZE, 0);
            }
        } else {
            if (side == Direction.WEST) {
                ctx.translate(- width* CELL_SIZE, 0);
            }
        }

        if(!openingLeft) ctx.scale(-1, 1);

        double scaledR = 2 * width* CELL_SIZE;

        ctx.strokeArc(side.isHorizontal() ? -scaledH : -scaledW, (side.isHorizontal() ? -scaledH : -scaledW)
                + OptionsHandler.INSTANCE.innerWallWidth.get()* CELL_SIZE,

                scaledR, scaledR, 270, 90, ArcType.ROUND);
        ctx.restore();
    }

    public Direction getSide() {
        return side;
    }

    public double getClearance() {
        return clearance;
    }

    public boolean isOpeningLeft() {
        return openingLeft;
    }
}
