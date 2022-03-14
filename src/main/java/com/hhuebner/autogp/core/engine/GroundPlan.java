package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.RoomComponent;

import java.util.ArrayList;
import java.util.List;

public class GroundPlan {

    private final String name;
    private final int groundPlanID;

    public double gpSize;
    private int componentIdCounter;
    public final List<RoomComponent> components = new ArrayList<>();

    public GroundPlan(String name, int groundPlanID, double gpSize) {
        this(name, groundPlanID, gpSize, 0);
    }

    public GroundPlan(String name, int groundPlanID, double gpSize, int componentIdCounter) {
        this.name = name;
        this.groundPlanID = groundPlanID;
        this.gpSize = gpSize;
        this.componentIdCounter = componentIdCounter;
    }

    boolean intersectsAnyPlacedRoom(BoundingBox bb) {
        for(RoomComponent r : this.components) {
            if(r.getBoundingBox().intersects(bb)) return true;
        }

        return false;
    }

    public int getNextID() {
        return this.componentIdCounter++;
    }

    public int getID() {
        return groundPlanID;
    }
}
