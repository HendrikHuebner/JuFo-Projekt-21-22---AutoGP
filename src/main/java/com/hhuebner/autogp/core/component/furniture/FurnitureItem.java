package com.hhuebner.autogp.core.component.furniture;

import com.hhuebner.autogp.ui.symbols.SymbolRenderer;

public class FurnitureItem {

    private final String name;
    private final boolean cornerGenerating;
    private final SymbolRenderer renderer;
    private final double width;
    private final double height;

    public FurnitureItem(String name, double width, double height, boolean cornerGenerating, SymbolRenderer renderer) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.cornerGenerating = cornerGenerating;
        this.renderer = renderer;

    }

    public FurnitureItem(String name, double width, double height, SymbolRenderer renderer) {
        this(name, width, height, false, renderer);
    }

    public boolean isCornerGenerating() {
        return cornerGenerating;
    }

    public String getName() {
        return name;
    }

    public SymbolRenderer getRenderer() {
        return this.renderer;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
