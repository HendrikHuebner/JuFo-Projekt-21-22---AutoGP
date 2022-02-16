package com.hhuebner.autogp.ui.widgets;

import com.hhuebner.autogp.core.InputHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;

public class GroundPlanTab extends Tab {

    private final int id;

    public GroundPlanTab(int id, String name, InputHandler inputHandler) {
        super(name);
        this.id = id;

        this.setOnCloseRequest(e -> {
            inputHandler.closeTab(this);
        });
    }

    public final int getID() {
        return id;
    }

    @Override
    public void setOnCloseRequest(EventHandler<Event> eventHandler) {
        super.setOnCloseRequest(eventHandler);
    }
}

