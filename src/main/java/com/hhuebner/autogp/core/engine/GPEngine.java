package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.util.Direction;

import java.util.*;
import java.util.function.Supplier;

public class GPEngine {

    private static final Random seedGen = new Random();
    public static final int CELL_SIZE = 150;
    private static final double ROOM_SIZE_DEVIANCE = 0.05;
    private static final double GRAPH_SIZE_LIMIT_FACTOR = 1.5;

    private List<PlanComponent> components = new ArrayList<>(); //TODO (MAYBE): Quadtree optimization
    private List<Room> rooms = new ArrayList<>();
    private Supplier<MainSceneController> mainController;
    private Supplier<InputHandler> inputHandler;

    private List<Room> graph = new ArrayList<>(); //TEMPORARY

    public GPEngine(Supplier<MainSceneController> mainController, Supplier<InputHandler> inputHandler) {
        this.mainController = mainController;
        this.inputHandler = inputHandler;

        //test room config
        this.rooms.add(new Room.Builder().setType(RoomType.LIVING_ROOM).setSize(25.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).setSize(20.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).setSize(20.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).setSize(15.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BATH_ROOM).setSize(10.0).build());
        this.rooms.add(new Room.Builder().setType(RoomType.KITCHEN).setSize(10.0).build());

    }

    public void generate() {
        long seed = seedGen.nextLong(); //7612126103766120639
        Random rand = new Random(seed);
        AutoGP.log("Seed: " + seed);

        //MutableGraph<Room> graph = GraphBuilder.undirected().build();
        List<Room> roomsLeft = new ArrayList<>(this.rooms);

        //Generate root
        Room root = roomsLeft.get(0);
        roomsLeft.remove(0);
        root.boundingBox = getRandomBB(rand, root.type.minRatio, root.size, 0, 0);

        this.graph.clear();
        this.graph.add(root);
        BoundingBox graphBB = new BoundingBox(root.boundingBox);

        roomAdd:
        for(Room room : roomsLeft) {
            double minLength = Math.sqrt(room.size * room.type.minRatio);
            double maxLength = Math.sqrt(room.size / room.type.minRatio);

            //gather anchor points
            List<AnchorPoint> anchors = new ArrayList<>();
            int cornerCount = 0;
            for(Room placedRoom : this.graph) {
                for(AnchorPoint a : placedRoom.getAnchors(graph)) {
                    anchors.add(a);
                    if(a.neighborTopRight.isPresent()) cornerCount++;
                }
            }

            Collections.shuffle(anchors, rand);
            if(cornerCount >= 2) anchors.sort(Comparator.comparing(a -> !a.neighborTopLeft.isPresent()));

            for(AnchorPoint a : anchors) {
                if(!a.neighborTopLeft.isPresent() && a.neighborBottomLeft.isPresent()) continue;

                //find minimal Bounding Boxes
                BoundingBox maxBB = new BoundingBox(a.getX(), a.getY(), a.getX() + maxLength, a.getY() + maxLength);

                if(a.directionFacing.dx + a.side.dx < 0) {
                    maxBB.move(-maxBB.getWidth(), 0);
                }

                if(a.directionFacing.dy + a.side.dy < 0) {
                    maxBB.move(0, -maxBB.getHeight());
                }

                //test if within graph limit
                if(Math.abs(maxBB.x2 - graphBB.x) > 15 ||
                        Math.abs(maxBB.x - graphBB.x2) > 15 ||
                        Math.abs(maxBB.y2 - graphBB.y) > 15 ||
                        Math.abs(maxBB.y - graphBB.y2) > 15 ) {
                    AutoGP.log("out of bounds! ", graphBB);
                    continue;
                }

                //test placement
                if (!this.intersectsAnyPlacedRoom(maxBB)) {
                    //Boundingbox can be a random size
                    room.boundingBox = getRandomBB(rand, room.type.minRatio, room.size, a.getX(), a.getY());
                    if(a.directionFacing.dx + a.side.dx < 0) room.boundingBox.move(-room.boundingBox.getWidth(), 0);
                    if(a.directionFacing.dy + a.side.dy < 0) room.boundingBox.move(0, -room.boundingBox.getHeight());

                    this.roundToAdjacentBB(room.boundingBox, a.side, a.directionFacing, a.room.boundingBox);
                    a.neighborTopLeft.ifPresent(n -> {
                        AutoGP.log("Rounded: ", room.name);
                        GPEngine.this.roundToAdjacentBB(room.boundingBox, a.directionFacing.getOpposite(), a.side, n.boundingBox);
                    });

                    graph.add(room);
                    continue roomAdd;
                }
            }
        }

        for(Room r : graph) {
            AutoGP.log(r.boundingBox);
        }
    }

    private void roundToAdjacentBB(BoundingBox toRound, Direction side, Direction facing, BoundingBox adjacent) {
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

    private BoundingBox getRandomBB(Random rand, double minRatio, double roomSize, double x, double y) {
        double randRatio = 1 - rand.nextDouble() * (1 - minRatio);
        if(rand.nextBoolean()) randRatio = 1 / randRatio;

        double width = Math.round(10 * Math.sqrt(roomSize * randRatio * (1 - rand.nextDouble() * ROOM_SIZE_DEVIANCE))) / 10.0;
        double height = Math.round(10 * Math.sqrt(roomSize / randRatio * (1 - rand.nextDouble() * ROOM_SIZE_DEVIANCE))) / 10.0;

        return new BoundingBox(x , y, x + width, y + height);
    }

    private boolean intersectsAnyPlacedRoom(BoundingBox bb) {
        for(Room r : this.graph) {
            if(r.boundingBox.intersects(bb)) return true;
        }

        return false;
    }

    public void onSceneLoad() {
        mainController.get().roomsOverviewTable.getItems().addAll(this.rooms);
    }

    public List<PlanComponent> getComponents() {
        return this.components;
    }

    public List<Room> getGraph() { return this.graph;}

    public void addRoom(Room room) {
        this.rooms.add(room);
    }
}
