package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.RoomComponent;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Direction;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GPEngine {

    private static final Random seedGen = new Random();
    public static final int CELL_SIZE = 150;
    private static final double ROOM_SIZE_DEVIANCE = 0.05;
    private static final double GRAPH_SIZE_LIMIT_FACTOR = 1.4;

    private int componentIdCounter = 0;
    private final List<RoomComponent> components = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();
    private final Supplier<MainSceneController> mainController;

    public GPEngine(Supplier<MainSceneController> mainController) {
        this.mainController = mainController;

        //test room config
        this.rooms.add(new Room.Builder().setType(RoomType.HALLWAY).setSize(15.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.LIVING_ROOM).setSize(25.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).setSize(20.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).setSize(20.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BATH_ROOM).setSize(10.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.KITCHEN).setSize(10.0).build());
    }

    public void generate() {
        long seed = 6293675520558343120l;//seedGen.nextLong();
        Random rand = new Random(seed);
        AutoGP.log("Seed: " + seed);
        this.components.clear();
        this.componentIdCounter = 0;

        List<Room> roomsLeft = new ArrayList<>(this.rooms);

        //Generate root
        Room root = roomsLeft.get(0);
        roomsLeft.remove(0);
        RoomComponent rootComponent = new RoomComponent(root,
                getRandomBB(rand, root.type.minRatio, root.size), ++componentIdCounter);

        this.components.add(rootComponent);

        BoundingBox graphBB = new BoundingBox(rootComponent.getBoundingBox());

        roomAdd:
        for(Room room : roomsLeft) {
            double maxLength = Math.sqrt(room.size / room.type.minRatio);
            double minLength = Math.sqrt(room.size * room.type.minRatio);

            //gather anchor points
            List<AnchorPoint> anchors = new ArrayList<>();
            int cornerCount = 0;

            for(RoomComponent component : this.components) {
                for(AnchorPoint a : component.getAnchors(this.components)) {
                    anchors.add(a);
                    if(a.neighborTopRight.isPresent())
                        cornerCount++;
                }
            }

            Collections.shuffle(anchors, rand);
            if(cornerCount >= 2) anchors.sort(Comparator.comparing(a -> a.neighborTopLeft.isEmpty()));

            for(AnchorPoint a : anchors) {
                if(a.neighborTopLeft.isEmpty() && a.neighborBottomLeft.isPresent()) continue;

                //find minimal Bounding Boxes
                BoundingBox maxBB = new BoundingBox(a.getX(), a.getY(), a.getX() + maxLength, a.getY() + maxLength);

                if(a.directionFacing.dx + a.side.dx < 0) {
                    maxBB.move(-maxBB.getWidth(), 0);
                }

                if(a.directionFacing.dy + a.side.dy < 0) {
                    maxBB.move(0, -maxBB.getHeight());
                }

                //test if within graph limit
                double xLimit = ((a.side.dx + a.directionFacing.dx > 0) ? graphBB.x + 15 - a.getX() : a.getX() - graphBB.x2 + 15);
                double yLimit = ((a.side.dy + a.directionFacing.dy > 0) ? graphBB.y + 15 - a.getY() : a.getY() - graphBB.y2 + 15);
                if(xLimit < minLength || yLimit < minLength || xLimit * yLimit < room.size) continue;

                //test placement
                if (!this.intersectsAnyPlacedRoom(maxBB)) {
                    //BoundingBox can be a random size
                    BoundingBox boundingBox = getRandomBB(rand, minLength, room.size, a.getX(), a.getY(), xLimit, yLimit);
                    if(boundingBox == null) continue;

                    if(a.directionFacing.dx + a.side.dx < 0) boundingBox.move(-boundingBox.getWidth(), 0);
                    if(a.directionFacing.dy + a.side.dy < 0) boundingBox.move(0, -boundingBox.getHeight());

                    this.roundToAdjacentBB(boundingBox, a.directionFacing, a.room.getBoundingBox());
                    a.neighborTopLeft.ifPresent(n -> {
                        AutoGP.log("Rounded: ", room.name);
                        GPEngine.this.roundToAdjacentBB(boundingBox, a.side, n.getBoundingBox());
                    });

                    components.add(new RoomComponent(room, boundingBox, ++componentIdCounter));
                    graphBB.encompass(boundingBox);
                    continue roomAdd;
                }
            }
        }

        for(RoomComponent r : components) {
            AutoGP.log(r.getName(), r.getBoundingBox());
        }

        //generate interior & furniture

        List<RoomComponent> hallways = this.components.stream().filter(c -> c.room.type == RoomType.HALLWAY)
                .collect(Collectors.toList());


        /*
        for(int i = 0; i < this.components.size(); i++) {
            List<Connection> connections = new ArrayList<>();
            RoomComponent roomComponent = this.components.get(i);

            for (RoomComponent r2 : this.components) {
                if(roomComponent != r2) {
                    connections.add(Connection.getConnection(roomComponent, r2));
                }
            }

            if(i == 0) {
                //place entrance door
                Direction freeSide = null;
                for(Direction d : Direction.values()) {
                    if(connections.stream().noneMatch(c -> c.getSide() == d)) {
                        freeSide = d;
                    }
                }

                if(freeSide == null) {
                    //regenerate
                } else {
                    //add door
                }
            }
        }*/
    }

    private BoundingBox getRandomBB(Random rand, float minRatio, double size) {
        double randRatio = 1 - rand.nextDouble() * (1 - minRatio);
        if(rand.nextBoolean()) randRatio = 1 / randRatio;

        double width = Math.round(10 * Math.sqrt(size * randRatio * (1 - rand.nextDouble() * ROOM_SIZE_DEVIANCE))) / 10.0;
        double height = Math.round(10 * Math.sqrt(size / randRatio * (1 - rand.nextDouble() * ROOM_SIZE_DEVIANCE))) / 10.0;

        return new BoundingBox(0, 0, width, height);    }

    private void roundToAdjacentBB(BoundingBox toRound, Direction facing, BoundingBox adjacent) {
        //find adjacent side
        double d = 0.0;
        switch(facing) {
            case NORTH -> d = adjacent.y - toRound.y;
            case SOUTH -> d = adjacent.y2 - toRound.y2;
            case EAST -> d = adjacent.x2 - toRound.x2;
            case WEST -> d = adjacent.x - toRound.x;
        }

        if(Math.abs(d) < 1) { //FIXME: 1 meter
            switch(facing) {
                case NORTH -> toRound.y = adjacent.y;
                case SOUTH -> toRound.y2 = adjacent.y2;
                case EAST -> toRound.x2 = adjacent.x2;
                case WEST -> toRound.x = adjacent.x;
            }
        }
    }

    private BoundingBox getRandomBB(Random rand, double minLength, double roomSize, double x, double y, double xLim, double yLim) {
        for(int i = 0; i < 10; i++) {
            double rx = minLength + rand.nextDouble() * (xLim - minLength);
            double ry = roomSize / rx;


            if (ry >= minLength && ry <= yLim)  {
                return new BoundingBox(x , y, x + rx, y + ry);
            }
        }

        return null;
    }

    private boolean intersectsAnyPlacedRoom(BoundingBox bb) {
        for(RoomComponent r : this.components) {
            if(r.getBoundingBox().intersects(bb)) return true;
        }

        return false;
    }

    public void onSceneLoad() {
        mainController.get().roomsOverviewTable.getItems().addAll(this.rooms);
    }

    public List<RoomComponent> getComponents() {
        return this.components;
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }
}
