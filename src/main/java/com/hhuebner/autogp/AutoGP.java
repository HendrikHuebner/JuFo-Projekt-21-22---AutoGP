package com.hhuebner.autogp;

import com.hhuebner.autogp.controllers.FurnitureSelectionController;
import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.controllers.MenuBarHandler;
import com.hhuebner.autogp.controllers.RoomEditorController;
import com.hhuebner.autogp.core.ControllerFactory;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AutoGP extends Application {

    private Scene roomEditorScene = null;
    private Scene furnitureSelectionScene;
    private FXMLLoader mainLoader = null;

    @Override
    public void start(Stage stage) throws IOException {
        Camera camera = new Camera();
        GPEngine engine = new GPEngine(() -> AutoGP.this.mainLoader.getController());
        InputHandler inputHandler = new InputHandler(() -> AutoGP.this.mainLoader.getController(), engine, camera);
        MenuBarHandler menuBarHandler = new MenuBarHandler(stage, engine);

        //main scene
        this.mainLoader = getFXMLLoader("auto_gp.fxml", MainSceneController.class,
                new MainSceneController(inputHandler, () -> this.roomEditorScene, () -> this.furnitureSelectionScene, engine, camera, menuBarHandler));

        //room editor
        FXMLLoader roomEditorLoader = getFXMLLoader("room_editor.fxml", RoomEditorController.class, new RoomEditorController(engine));

        //furniture selector
        FXMLLoader furnitureSelectionLoader = getFXMLLoader("furniture_selector.fxml",
                FurnitureSelectionController.class, new FurnitureSelectionController(inputHandler));

        Scene main = new Scene(mainLoader.load(), 834, 555);
        this.roomEditorScene = new Scene(roomEditorLoader.load(), 293, 340);
        this.furnitureSelectionScene = new Scene(furnitureSelectionLoader.load(),269, 346);

        stage.setTitle("AutoGP");
        stage.setResizable(false);
        stage.setScene(main);
        stage.show();

        CanvasRenderer canvasRenderer = new CanvasRenderer(((MainSceneController)mainLoader.getController()).canvas, inputHandler, engine, camera);
        canvasRenderer.start();
    }

    public static <T> FXMLLoader getFXMLLoader(String path, Class clazz, T controller) {
        FXMLLoader loader = new FXMLLoader(AutoGP.class.getResource(path));
        loader.setControllerFactory((c) -> {
            if(c == clazz) {
                return controller;
            } else {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        return loader;
    }

    public static void main(String[] args) {
        launch();
    }

    //LOGGER
    public static void log(Object... params) {
        StringBuilder s = new StringBuilder();
        for(Object o : params) {
            if (o == null)
                s.append("null");
            else
                s.append(o.toString()).append(" ");
        }
        System.out.println(s);
    }

    public static void warn(Object... params) {
        StringBuilder s = new StringBuilder("\\u001B[33m");
        for(Object o : params) {
            if (o == null)
                s.append("null");
            else
                s.append(o.toString()).append(" ");
        }
        System.out.println(s);
    }

    public static void logf(String s, Object... params) {
        System.out.println(String.format(s, params));
    }
}