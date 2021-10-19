package com.hhuebner.autogp.core.component.furniture;

import com.hhuebner.autogp.AutoGP;
import com.hhuebner.autogp.core.component.PlanComponent;
import com.hhuebner.autogp.core.engine.ImageLoader;
import javafx.scene.image.Image;

import java.util.List;

public class FurnitureItem {

    private final String name;
    private final Image image;
    private boolean isValid = false;

    public FurnitureItem(String name) {
        this.name = name;

        //Load image
        this.image = ImageLoader.INSTANCE.getOrLoad(name + ".png");
        if(this.image.isError()) {
            AutoGP.warn("Failed to load image for ", this.name, "!");
        }
    }

    public boolean validate(List<PlanComponent> components) {
        return isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }
}
