package com.hhuebner.autogp;

import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.controllers.RoomEditorController;
import com.hhuebner.autogp.core.ControllerFactory;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.core.engine.GPEngine;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AutoGP extends Application {

    private Scene mainScene = null;
    private Scene roomEditorScene = null;
    private FXMLLoader mainLoader = null;

    @Override
    public void start(Stage stage) throws IOException { //TODO: refactor
        Camera camera = new Camera();
        GPEngine engine = new GPEngine(() -> AutoGP.this.mainLoader.getController());
        InputHandler inputHandler = new InputHandler(() -> this.mainScene, engine);
        ControllerFactory mainCF = new ControllerFactory();

        mainCF.registerController(MainSceneController.class, new MainSceneController(inputHandler, () -> this.roomEditorScene, engine, camera));

        mainLoader = new FXMLLoader(getClass().getResource("auto_gp.fxml"));
        mainCF.setControllers(mainLoader);

        ControllerFactory roomEditorCF = new ControllerFactory();
        roomEditorCF.registerController(RoomEditorController.class, new RoomEditorController(engine, () -> AutoGP.this.mainLoader.getController()));
        FXMLLoader roomEditorLoader = new FXMLLoader(getClass().getResource("room_editor.fxml"));
        roomEditorCF.setControllers(roomEditorLoader);

        Parent mainRoot = mainLoader.load();
        Scene main = new Scene(mainRoot, 834, 555);
        this.mainScene = main;

        Parent roomEditorRoot = roomEditorLoader.load();
        Scene roomEditor = new Scene(roomEditorRoot, 340, 377);
        this.roomEditorScene = roomEditor;

        stage.setTitle("AutoGP");
        stage.setResizable(false);
        stage.setScene(main);
        stage.show();

        engine.onSceneLoad();

        CanvasRenderer canvasRenderer = new CanvasRenderer(((MainSceneController)mainLoader.getController()).canvas, inputHandler, engine, camera);
        canvasRenderer.start();
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