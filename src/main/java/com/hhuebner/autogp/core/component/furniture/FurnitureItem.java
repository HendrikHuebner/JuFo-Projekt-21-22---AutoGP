package com.hhuebner.autogp.core.component.furniture;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hhuebner.autogp.ui.symbols.SymbolRenderer;

public class FurnitureItem {

    public String name;
    public double width;
    public double height;
    public boolean cornerGenerating;
    public SymbolRenderer renderer;


    @JsonCreator
    public FurnitureItem(@JsonProperty("name") String name,
                         @JsonProperty("width") double width,
                         @JsonProperty("height") double height,
                         @JsonProperty("cornerGenerating") boolean cornerGenerating,
                         @JsonProperty("renderer") SymbolRenderer renderer) {

        this.name = name;
        this.width = width;
        this.height = height;
        this.cornerGenerating = cornerGenerating;
        this.renderer = renderer;

    }

    public FurnitureItem(String name, double width, double height, SymbolRenderer renderer) {
        this(name, width, height, false, renderer);
    }

    public String getName() {
        return name;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public boolean isCornerGenerating() {
        return cornerGenerating;
    }

    public SymbolRenderer getRenderer() {
        return this.renderer;
    }

    //TableView
    @JsonIgnore
    public String getDisplayName() {
        return this.name;
    }

    @JsonIgnore
    public String getDisplaySize() {
        return String.format("%.2fm x %.2fm", this.getWidth(), this.getHeight());
    }

    @JsonIgnore
    public String getDisplayType() {
        return this.getName();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
