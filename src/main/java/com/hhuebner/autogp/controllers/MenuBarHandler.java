package com.hhuebner.autogp.controllers;

import com.hhuebner.autogp.options.Option;
import com.hhuebner.autogp.options.OptionsHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MenuBarHandler {

    private final Stage stage;
    private final OptionsHandler optHandler = OptionsHandler.INSTANCE;

    public MenuBarHandler(Stage stage) {
        this.stage = stage;
    }

    public void initialize(MenuBar menuBar) {
        //first gather all sub menus
        Map<String, Menu> menuMap = new HashMap<>();
        menuBar.getMenus().forEach(m -> getSubMenu(menuMap, m));

        //add options
        for(Option<?> o : this.optHandler.getOptions()) {
            Menu m = menuMap.get(o.getParentMenu());
            if(m != null)
                m.getItems().add(o.createMenuItem());
        }
    }

    private void getSubMenu(Map<String, Menu> map, Menu menu) {
        map.put(menu.getText(), menu);

        for(MenuItem i : menu.getItems()) {
            if(i instanceof Menu m) {
                getSubMenu(map, m);
                map.put(m.getText(), m);
            }
        }
    }
}
