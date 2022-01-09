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
        long seed = seedGen.nextLong(); //
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
                BoundingBox minBBX = new BoundingBox(a.getX(), a.getY(), a.getX() + maxLength, a.getY() + minLength);
                BoundingBox minBBY = new BoundingBox(a.getX(), a.getY(), a.getX() + minLength, a.getY() + maxLength);

                if(a.directionFacing.dx + a.side.dx < 0) {
                    minBBX.move(-minBBX.getWidth(), 0);
                    minBBY.move(-minBBY.getWidth(), 0);
                }

                if(a.directionFacing.dy + a.side.dy < 0) {
                    minBBX.move(0, -minBBX.getHeight());
                    minBBY.move(0, -minBBY.getHeight());
                }

                //test if within graph limit
                if(Math.abs(minBBX.x2 - graphBB.x) > 15 || Math.abs(minBBY.x2 - graphBB.x) > 15 ||
                        Math.abs(minBBX.x - graphBB.x2) > 15 || Math.abs(minBBY.x - graphBB.x2) > 15 ||
                        Math.abs(minBBX.y2 - graphBB.y) > 15 || Math.abs(minBBY.y2 - graphBB.y) > 15 ||
                        Math.abs(minBBX.y - graphBB.y2) > 15 || Math.abs(minBBY.y - graphBB.y2) > 15) {
                    AutoGP.log("out of bounds! ", graphBB);
                    continue;
                }

                //test placement
                if (!this.intersectsAnyPlacedRoom(minBBX)) {
                    if (!this.intersectsAnyPlacedRoom(minBBY)) {
                        //Boundingbox can be a random size
                        room.boundingBox = getRandomBB(rand, room.type.minRatio, room.size, a.getX(), a.getY());
                        if(a.directionFacing.dx + a.side.dx < 0) room.boundingBox.move(-room.boundingBox.getWidth(), 0);
                        if(a.directionFacing.dy + a.side.dy < 0) room.boundingBox.move(0, -room.boundingBox.getHeight());

                        this.roundToAdjacentBB(room.boundingBox, a.side, a.room.boundingBox);
                        a.neighborTopLeft.ifPresent(n -> {
                            AutoGP.log("Rounded: ", room.name);
                            //GPEngine.this.roundToAdjacentBB(room.boundingBox, a.directionFacing.getOpposite(), n.boundingBox);
                        });

                        graph.add(room);
                        graphBB.encompass(room.boundingBox);
                        continue roomAdd;
                    } else {
                        //BoundingBox is restricted to height
                    }
                } else if (!this.intersectsAnyPlacedRoom(minBBY)) {
                    //BoundingBox is restricted to width

                }
            }
        }

        for(Room r : graph) {
            AutoGP.log(r.boundingBox);
        }
    }

    private void roundToAdjacentBB(BoundingBox toRound, Direction side, BoundingBox adjacent) {
        //find adjacent side
        double len1 = side.isHorizontal() ? adjacent.getHeight() : adjacent.getWidth();
        double len2 = side.isHorizontal() ? toRound.getHeight() : toRound.getWidth();

        if(Math.abs(len1 - len2) < 1) { //FIXME: 1 meter
            if(side.isHorizontal()) {
                toRound.y = adjacent.y;
                toRound.y2 = adjacent.y2;
            } else {
                toRound.x = adjacent.x;
                toRound.x2 = adjacent.x2;
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
