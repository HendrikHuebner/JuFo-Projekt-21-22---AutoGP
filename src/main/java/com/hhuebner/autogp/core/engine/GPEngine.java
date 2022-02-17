package com.hhuebner.autogp.core.engine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.core.component.*;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.util.Direction;
import com.hhuebner.autogp.core.util.Utility;
import com.hhuebner.autogp.options.OptionsHandler;
import com.hhuebner.autogp.ui.widgets.GroundPlanTab;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GPEngine {

    public static final int CELL_SIZE = 150;

    private final Supplier<MainSceneController> mainController;
    public final Map<Integer, GroundPlan> groundPlanMap = new HashMap<>();
    public final ObservableList<Room> rooms = FXCollections.observableArrayList();

    public GPEngine(Supplier<MainSceneController> mainController) {
        this.mainController = mainController;

        //test room config
        this.rooms.add(new Room.Builder().setType(RoomType.HALLWAY).build());

        this.rooms.add(new Room.Builder().setType(RoomType.LIVING_ROOM).build());

        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BED_ROOM).build());

        this.rooms.add(new Room.Builder().setType(RoomType.BATH_ROOM).build());
        this.rooms.add(new Room.Builder().setType(RoomType.BATH_ROOM).build());

        this.rooms.add(new Room.Builder().setType(RoomType.KITCHEN).build());
    }

    /**
     * Attempts to generate a ground plan, return null if unsuccessful.
     *
     * @param seed
     * @return
     */
    public GroundPlan generate(int id, String name, double gpSize, long seed) {
        Random rand = new Random(seed);
        
        GroundPlan gp = new GroundPlan(name, id, gpSize);

        List<Room> roomsLeft = new ArrayList<>(this.rooms);

        //Generate root
        Room root = roomsLeft.get(0);
        roomsLeft.remove(0);
        RoomComponent rootComponent = new RoomComponent(root,
                getRandomBB(rand, root.type.minRatio, root.size), gp.getNextID());

        gp.components.add(rootComponent);

        BoundingBox graphBB = new BoundingBox(rootComponent.getBoundingBox());
        final double maxGraphSide = Math.sqrt(gp.gpSize) * OptionsHandler.INSTANCE.graphSizeLimitFactor.get();

        roomAdd:
        for(Room room : roomsLeft) {
            double minLength = Math.max(OptionsHandler.INSTANCE.minimumRoomWidth.get(),
                    Math.sqrt(room.size * room.type.minRatio));
            double maxLength = room.size / minLength;

            //gather anchor points
            List<AnchorPoint> anchors = new ArrayList<>();
            int cornerCount = 0;

            for(RoomComponent component : gp.components) {
                for(AnchorPoint a : component.getAnchors(gp.components)) {
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
                double xLimit = ((a.side.dx + a.directionFacing.dx > 0) ? graphBB.x + maxGraphSide - a.getX() : a.getX() - graphBB.x2 + maxGraphSide);
                double yLimit = ((a.side.dy + a.directionFacing.dy > 0) ? graphBB.y + maxGraphSide - a.getY() : a.getY() - graphBB.y2 + maxGraphSide);
                if(xLimit < minLength || yLimit < minLength || xLimit * yLimit < room.size) continue;

                //test placement
                if (!gp.intersectsAnyPlacedRoom(maxBB)) {
                    //BoundingBox can be a random size
                    BoundingBox boundingBox = getRandomBB(rand, minLength, room.size, a.getX(), a.getY(), xLimit, yLimit);
                    if(boundingBox == null) continue;

                    if(a.directionFacing.dx + a.side.dx < 0) boundingBox.move(-boundingBox.getWidth(), 0);
                    if(a.directionFacing.dy + a.side.dy < 0) boundingBox.move(0, -boundingBox.getHeight());

                    boundingBox.roundToAdjacentBB(a.directionFacing, a.room.getBoundingBox());

                    a.neighborTopLeft.ifPresent(n -> {
                        boundingBox.roundToAdjacentBB(a.side, n.getBoundingBox());
                    });

                    gp.components.add(new RoomComponent(room, boundingBox, gp.getNextID()));
                    graphBB.encompass(boundingBox);
                    continue roomAdd;
                }
            }
        }

        //generate interior & furniture
        ListMultimap<RoomComponent, Connection> connections = ArrayListMultimap.create();
        for(RoomComponent r : gp.components) {
            for (RoomComponent r2 : gp.components) {
                if(r != r2) {
                    Connection c = Connection.getConnection(r, r2);
                    if(c != null) connections.put(r, c);
                }
            }
        }

        List<RoomComponent> hallways = gp.components.stream().filter(c -> c.room.type == RoomType.HALLWAY).collect(Collectors.toList());
        Optional<RoomComponent> optStart = hallways.stream().findFirst();
        List<RoomComponent> connected = new ArrayList<>();
        List<RoomComponent> toConnect = new ArrayList<>(gp.components);

        for(RoomComponent room : gp.components) {
            room.addChild(WallComponent.create(room, connections, "wall", gp.getNextID()));
        }

        //Try connect all hallways
        if(optStart.isPresent()) {
            connected.add(optStart.get());
            toConnect.remove(optStart.get());
            hallways.remove(optStart.get());

            boolean found = true;

            while(found) {
                found = false;
                for(int i = 0; i < connected.size(); i++) {
                    RoomComponent h = connected.get(i);
                    for(Connection c : connections.get(h)) {
                        if(Math.abs(c.start() - c.end()) < 1) continue;

                        if(hallways.contains(c.roomComponent())) {
                            connected.add(c.roomComponent());
                            toConnect.remove(c.roomComponent());
                            hallways.remove(c.roomComponent());
                            createDoor(gp, h, c);
                            found = true;
                        }
                    }
                }
            }

            if(hallways.size() != 0) { //if not all hallways could be connected, generate new
                return null;
            }

        } else {
            optStart = gp.components.stream().filter(c -> c.room.type == RoomType.LIVING_ROOM).findFirst();

            if(optStart.isPresent()) {
                connected.add(optStart.get());
                toConnect.remove(optStart.get());
            } else {
                connected.add(toConnect.get(0));
                toConnect.remove(0);
            }
        }

        //connect living room and bathroom
        for(int i = 0; i < toConnect.size(); i++) {
            RoomComponent r = toConnect.get(i);
            if(r.room.type == RoomType.LIVING_ROOM || r.room.type == RoomType.BATH_ROOM) {
                boolean found = false;
                for(Connection c : connections.get(r)) {
                    if(Math.abs(c.start() - c.end()) < 1) continue;

                    if(c.roomComponent().room.type == RoomType.HALLWAY) {
                        connected.add(r);
                        toConnect.remove(r);
                        createDoor(gp, r, c);
                        found = true;
                        break;
                    }
                }

                //room couldn't be connected!
                if(!found) {
                    return null;
                }
            }
        }

        //connect remaining rooms

        boolean generateDoors = OptionsHandler.INSTANCE.generateDoors.get();

        for(int i = 0; i < toConnect.size(); i++) {
            RoomComponent r = toConnect.get(i);
            boolean found = false;
            for(Connection c : connections.get(r)) {
                if(Math.abs(c.start() - c.end()) < 1) continue;

                if(c.roomComponent().room.type == RoomType.HALLWAY || c.roomComponent().room.type == RoomType.LIVING_ROOM) {
                    connected.add(r);
                    if(generateDoors) createDoor(gp, r, c);
                    found = true;
                    break;
                }
            }

            //room couldn't be connected!
            if(!found) {
                return null;
            }

        }

        for(RoomComponent roomComponent : gp.components) {
            roomComponent.getWallComponent().getConnections().sort(Comparator.comparingDouble(c -> c.start()));
        }

        if(OptionsHandler.INSTANCE.generateFurniture.get()) {
            this.generateFurniture(gp, rand);
        }

        return gp; //yay
    }

    private void generateWindows(GroundPlan gp, Random rand, ListMultimap<RoomComponent, Connection> connections) {
        final double windowHeight = OptionsHandler.INSTANCE.windowHeight.get();
        final double wallWidth = OptionsHandler.INSTANCE.innerWallWidth.get();

        for (RoomComponent roomComponent : gp.components) {
            final double totalWindowWidth = roomComponent.room.type == RoomType.BATH_ROOM ?
                    OptionsHandler.INSTANCE.bathroomWindowWidth.get() : Utility.getRoomArea(roomComponent.getBoundingBox(), wallWidth) / windowHeight;

            List<Direction> freeSides = new ArrayList<>(List.of(Direction.values()));

            for(Connection c : connections.get(roomComponent)) {
                freeSides.remove(c.side());
            }

            if(freeSides.size() == 0) return;

        }
    }

    private void generateFurniture(GroundPlan gp, Random rand) {
        final int furnitureSpawnTries = OptionsHandler.INSTANCE.furnitureSpawnTries.get();
        final double INNER_WALL_THICKNESS = OptionsHandler.INSTANCE.innerWallWidth.get();

        for(RoomComponent roomComponent : gp.components) {
            roomComponent.getWallComponent().getConnections().sort(Comparator.comparingDouble(c -> c.start()));

            List<FurnitureItem> sortedFurniture = new ArrayList<>(roomComponent.room.furniture);
            sortedFurniture.sort(Comparator.comparing(f -> !f.isCornerGenerating()));

            //add furniture
            furnitureLoop:
            for (FurnitureItem item : sortedFurniture) {
                List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
                Collections.shuffle(directions, rand);

                directionLoop:
                for (Direction side : directions) {
                    BoundingBox bb = null;
                    double a = side == Direction.EAST ? roomComponent.getBoundingBox().x2 - INNER_WALL_THICKNESS :
                            roomComponent.getBoundingBox().x + INNER_WALL_THICKNESS;
                    double b = side == Direction.SOUTH ? roomComponent.getBoundingBox().y2 - INNER_WALL_THICKNESS :
                            roomComponent.getBoundingBox().y + INNER_WALL_THICKNESS;

                    if (item.isCornerGenerating()) {
                        bb = new BoundingBox(a, b, a + (side.isHorizontal() ? item.getHeight() : item.getWidth()),
                                b + (side.isHorizontal() ? item.getWidth() : item.getHeight()));
                        if (side == Direction.SOUTH) bb.move(0, -bb.getHeight());
                        if (side == Direction.EAST) bb.move(-bb.getWidth(), 0);

                        //check collisions
                        for (PlanComponent c : roomComponent.getChildren()) {
                            if (c instanceof InteractableComponent) {
                                if (((InteractableComponent) c).getBoundingBox().intersects(bb)) {
                                    continue directionLoop;
                                }
                            }
                        }

                    } else {
                        furnitureSpawnTries:
                        for (int i = 0; i < furnitureSpawnTries; i++) {
                            if (side.isHorizontal()) {
                                double d = rand.nextDouble() * (roomComponent.getBoundingBox().getHeight() - item.getWidth() - 3 * INNER_WALL_THICKNESS);
                                bb = new BoundingBox(a, b + d, a + item.getHeight(), b + d + item.getWidth());

                                if (side == Direction.EAST) bb.move(-bb.getWidth(), 0);
                            } else {
                                double d = rand.nextDouble() * (roomComponent.getBoundingBox().getWidth() - item.getWidth() - 3 * INNER_WALL_THICKNESS);
                                bb = new BoundingBox(a + d, b, a + d + item.getWidth(), b + item.getHeight());

                                if (side == Direction.SOUTH) bb.move(0, -bb.getHeight());
                            }

                            //check collisions
                            for (PlanComponent c : roomComponent.getChildren()) {
                                if (c instanceof InteractableComponent) {
                                    if (((InteractableComponent) c).getBoundingBox().intersects(bb)) {
                                        bb = null;
                                        continue furnitureSpawnTries; //continue spawn tries
                                    }
                                }
                            }

                            break;
                        }
                    }

                    if (bb != null) {
                        FurnitureComponent furnitureComponent = new FurnitureComponent(bb, side, item, gp.getNextID());
                        roomComponent.addChild(furnitureComponent);
                        continue furnitureLoop;
                    }
                }
            }
        }
    }

    private void createDoor(GroundPlan gp, RoomComponent component, Connection c) {
        final double prefWallDistance = OptionsHandler.INSTANCE.doorPrefWallDistance.get()
                + OptionsHandler.INSTANCE.innerWallWidth.get();

        final double doorWidth = OptionsHandler.INSTANCE.doorSize.get();
        double start, end;

        double d1 = c.start();
        double d2 = c.side().isHorizontal() ? component.getBoundingBox().getHeight() - c.end() : component.getBoundingBox().getWidth() - c.end();

        if(d1 <= d2) {
            start = d1 + prefWallDistance;
            end = d1 + doorWidth + prefWallDistance;
        } else {
            start = (c.side().isHorizontal() ? component.getBoundingBox().getHeight() : component.getBoundingBox().getWidth()) - d2 - doorWidth - prefWallDistance;
            end = (c.side().isHorizontal() ? component.getBoundingBox().getHeight() : component.getBoundingBox().getWidth()) - d2 - prefWallDistance;
        }

        component.addChild(DoorComponent.create(component, start, end, c.side(), "door", gp.getNextID()));
    }

    private BoundingBox getRandomBB(Random rand, float minRatio, double size) {
        double randRatio = 1 - rand.nextDouble() * (1 - minRatio);
        if(rand.nextBoolean()) randRatio = 1 / randRatio;

        double width = Math.round(10 * Math.sqrt(size * randRatio)) / 10.0;
        double height = Math.round(10 * Math.sqrt(size / randRatio)) / 10.0;

        return new BoundingBox(0, 0, width, height);
    }

    public void calculateRoomSizes(double gpSize) {
        double sum = 0;
        int count = 0;

        for(Room room : this.rooms) {
            if (room.size == 0) {
                count++;
                sum += room.type.defaultSize;
            } else {
                sum += room.size;
            }
        }

        for(Room room : this.rooms) {
            if(room.size == 0) {
                room.size = Math.round(10 * (room.type.defaultSize + (gpSize - sum) / count)) / 10;

                this.mainController.get().roomsOverviewTable.refresh();
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

    public void onSceneLoad() {
        //mainController.get().roomsOverviewTable.getItems().addAll(gp.rooms);
    }

    public GroundPlan getSelectedGP() {
        GroundPlanTab t = ((GroundPlanTab)mainController.get().tabPane.getSelectionModel().getSelectedItem());
        return t == null ? null : this.groundPlanMap.get(t.getID());
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

}
