package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.core.component.FurnitureComponent;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.component.furniture.FurnitureItems;

import java.util.ArrayList;
import java.util.List;

public class GPEngine {

    public static final int CELL_SIZE = 30;

    private List<PlanComponent> components = new ArrayList<>(); //TODO (MAYBE): Quadtree optimization

    public GPEngine() {
        this.components.add(new FurnitureComponent(new BoundingBox(100, 200, 300, 400), FurnitureItems.TOILET, 1l));
    }

    public List<PlanComponent> getComponents() {
        return this.components;
    }
}
