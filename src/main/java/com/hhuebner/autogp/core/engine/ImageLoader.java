package com.hhuebner.autogp.core.engine;

import com.hhuebner.autogp.AutoGP;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    public static final ImageLoader INSTANCE = new ImageLoader("/com/hhuebner/autogp/symbols");

    private final String rootFolder;
    private Map<String, Image> images = new HashMap<>();

    public ImageLoader(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * Gets image or loads it if not already loaded. Returns null if image doesn't exit.
     *
     * @param name
     * @return
     */
    public Image getOrLoad(String name) {
        if(this.images.containsKey(name))
            return this.images.get(name);

        Image img = null;

        try {
            img = new Image(AutoGP.class.getResourceAsStream(this.rootFolder + "/" + name));
        } catch(NullPointerException e) {
            AutoGP.warn("Couldn't find image: " + this.rootFolder + "/" + name);
        }

        if(img != null) {
            this.images.put(name, img);
        }

        return img;
    }
}
