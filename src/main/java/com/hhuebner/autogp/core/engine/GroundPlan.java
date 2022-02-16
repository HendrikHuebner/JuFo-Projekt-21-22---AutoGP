package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.RoomComponent;

import java.util.ArrayList;
import java.util.List;

public class GroundPlan {

    private final String name;
    private final int groundPlanID;

    public double gpSize;
    private int componentIdCounter = 0;
    public final List<RoomComponent> components = new ArrayList<>();

    public GroundPlan(String name, int id, double gpSize) {
        this.name = name;
        this.groundPlanID = id;
        this.gpSize = gpSize;
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
}
