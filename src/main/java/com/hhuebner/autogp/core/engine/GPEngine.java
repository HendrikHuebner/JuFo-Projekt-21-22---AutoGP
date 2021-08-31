package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.ImageComponent;
import com.hhuebner.autogp.core.component.PlanComponent;

import java.util.ArrayList;
import java.util.List;

public class GPEngine {

    private List<PlanComponent> components = new ArrayList<>(); //TODO (MAYBE): Quadtree optimization

    public GPEngine() {
        //DEBUG
        this.components.add(new ImageComponent(new BoundingBox(100, 100, 200, 300), "testbox", "", 0l));
    }

    public List<PlanComponent> getComponents() {
        return this.components;
    }
}
