package com.hhuebner.autogp.ui.widgets;

import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.component.InteractableComponent;
import com.hhuebner.autogp.core.engine.ImageLoader;
import com.hhuebner.autogp.core.util.Unit;
import com.hhuebner.autogp.core.util.Utility;
import javafx.scene.control.Label;

import static com.hhuebner.autogp.core.engine.GPEngine.CELL_SIZE;

public class InformationLabel extends Label {

    private InteractableComponent component;

    public void setInformation(InteractableComponent component, InputHandler handler) {
        this.component = component;
        update(handler);
    }

    public void clear() {
        this.setText("");
    }


    public void update(InputHandler handler) {
        if(this.component != null)
            this.setText(this.component.getDescription(handler));
    }
}
