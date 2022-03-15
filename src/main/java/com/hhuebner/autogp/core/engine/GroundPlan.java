package com.hhuebner.autogp.core.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hhuebner.autogp.core.component.RoomComponent;

import java.util.ArrayList;
import java.util.List;

public class GroundPlan {

    private final String name;
    private int groundPlanID;
    private int componentIdCounter;
    public double gpSize;

    public final List<RoomComponent> components = new ArrayList<>();

    public GroundPlan(String name, int groundPlanID, double gpSize) {
        this(name, groundPlanID, gpSize, 0);
    }

    @JsonCreator
    public GroundPlan(@JsonProperty("name") String name, @JsonProperty("groundPlanID") int groundPlanID,
                      @JsonProperty("gpSize") double gpSize, @JsonProperty("componentIdCounter") int componentIdCounter) {
        this.name = name;
        this.groundPlanID = groundPlanID;
        this.gpSize = gpSize;
        this.componentIdCounter = componentIdCounter;
    }

    boolean intersectsAnyPlacedRoom(BoundingBox bb) {
        for(RoomComponent r : this.components) {
            if(r.getBB().intersects(bb)) return true;
        }

        return false;
    }

    @JsonIgnore
    public int getNextID() {
        return this.componentIdCounter++;
    }

    public int getComponentIdCounter() {
        return this.componentIdCounter;
    }

    public int getGroundPlanID() {
        return groundPlanID;
    }

    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        this.groundPlanID = id;
    }
}
