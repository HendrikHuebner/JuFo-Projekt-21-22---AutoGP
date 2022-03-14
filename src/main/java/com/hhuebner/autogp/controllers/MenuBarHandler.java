package com.hhuebner.autogp.controllers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hhuebner.autogp.core.component.*;
import com.hhuebner.autogp.core.component.furniture.FurnitureItem;
import com.hhuebner.autogp.core.engine.BoundingBox;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.core.engine.GroundPlan;
import com.hhuebner.autogp.core.engine.Room;
import com.hhuebner.autogp.options.Option;
import com.hhuebner.autogp.options.OptionsHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBarHandler {

    private final Stage stage;
    private final OptionsHandler optHandler = OptionsHandler.INSTANCE;
    private final GPEngine engine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MenuBarHandler(Stage stage, GPEngine engine) {
        this.stage = stage;
        this.engine = engine;

        this.objectMapper.registerSubtypes(PlanComponent.class, FurnitureComponent.class, DoorComponent.class, WallComponent.class);

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(PlanComponent.class)
                .build();

        this.objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public void initialize(MenuBar menuBar) {
        //first gather all sub menus
        Map<String, Menu> menuMap = new HashMap<>();
        menuBar.getMenus().forEach(m -> getSubMenu(menuMap, m));

        //add options
        for (Option<?> o : this.optHandler.getOptions()) {
            Menu m = menuMap.get(o.getParentMenu());
            if (m != null)
                m.getItems().add(o.createMenuItem());
        }

        MenuItem saveMenuItem = new MenuItem("Speichern");
        saveMenuItem.setOnAction(e -> openSaveDialogue());
        menuMap.get("Datei").getItems().add(saveMenuItem);

        MenuItem openMenuItem = new MenuItem("Öffnen");
        openMenuItem.setOnAction(e -> openDialogue());
        menuMap.get("Datei").getItems().add(openMenuItem);
    }

    private void getSubMenu(Map<String, Menu> map, Menu menu) {
        map.put(menu.getText(), menu);

        for (MenuItem i : menu.getItems()) {
            if (i instanceof Menu m) {
                getSubMenu(map, m);
                map.put(m.getText(), m);
            }
        }
    }


    private void openSaveDialogue() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Grundriss Speichern");
        File file = fileChooser.showSaveDialog(this.stage);

        if(file == null) return;

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.engine.getSelectedGP()));
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDialogue() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Grundriss Öffnen");
        File file = fileChooser.showOpenDialog(this.stage);

        try {
            FileReader reader = new FileReader(file);
            GroundPlan groundPlan = objectMapper.readValue(reader, GroundPlan.class);

            if(groundPlan != null) {
                this.engine.addGroundPlan(groundPlan);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
