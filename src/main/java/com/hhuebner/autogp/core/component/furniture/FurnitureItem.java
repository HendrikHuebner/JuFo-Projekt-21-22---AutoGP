package com.hhuebner.autogp.core.component.furniture;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.ImageLoader;
import javafx.scene.image.Image;

import java.util.List;

public class FurnitureItem {

    private final String name;
    private final Image image;
    private final String path;
    private final boolean cornerGenerating;

    public FurnitureItem(String name, String path, boolean cornerGenerating) {
        this.name = name;
        this.path = path;
        this.cornerGenerating = cornerGenerating;

        //Load image
        this.image = ImageLoader.INSTANCE.getOrLoad(path + ".png");
        if(this.image == null || this.image.isError()) {
            AutoGP.warn("Failed to load image for ", this.path, "!");
        }
    }

    public FurnitureItem(String name, String path) {
        this(name, path, false);
    }

    public boolean isCornerGenerating() {
        return cornerGenerating;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public double getWidth() {
        return this.image.getWidth() / 100.0;
    }

    public double getHeight() {
        return this.image.getHeight() / 100.0;
    }
}
