package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.PlanComponent;

import java.util.ArrayList;
import java.util.List;

public class GPEngine {

    private List<PlanComponent> components = new ArrayList<>(); //TODO (MAYBE): Quadtree optimization

    public GPEngine() {}

    public List<PlanComponent> getComponents() {
        return this.components;
    }
}
