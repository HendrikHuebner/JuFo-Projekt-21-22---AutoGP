package com.hhuebner.autogp;

import com.hhuebner.autogp.controllers.CanvasController;
import com.hhuebner.autogp.controllers.MainSceneController;
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
import java.net.URISyntaxException;

public class AutoGP extends Application {
    private Scene mainScene = null;
    private Scene roomEditorScene = null;


    @Override
    public void start(Stage stage) throws IOException, URISyntaxException { //TODO: FIX THIS SHITTY CODE
        Camera camera = new Camera();
        GPEngine engine = new GPEngine();
        InputHandler inputHandler = new InputHandler(() -> this.mainScene, engine);
        ControllerFactory mainCF = new ControllerFactory();

        mainCF.registerController(MainSceneController.class, new MainSceneController(inputHandler, () -> this.roomEditorScene));
        CanvasController canvasController = mainCF.registerController(CanvasController.class,
                new CanvasController(camera, inputHandler, engine));

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("auto_gp.fxml"));
        mainCF.setControllers(mainLoader);

        ControllerFactory roomEditorCF = new ControllerFactory();
        FXMLLoader roomEditorLoader = new FXMLLoader(getClass().getResource("room_editor.fxml"));
        roomEditorCF.setControllers(roomEditorLoader);

        Parent mainRoot = mainLoader.load();
        Scene main = new Scene(mainRoot, 819, 415);
        this.mainScene = main;

        Parent roomEditorRoot = roomEditorLoader.load();
        Scene roomEditor = new Scene(roomEditorRoot, 340, 377);
        this.roomEditorScene = roomEditor;

        stage.setTitle("AutoGP");
        stage.setResizable(false);
        stage.setScene(main);
        stage.show();

        CanvasRenderer canvasRenderer = new CanvasRenderer(canvasController.canvas, inputHandler, engine, camera);
        canvasRenderer.start();
    }

    public static void main(String[] args) {
        launch();
    }

    //LOGGER
    public static void log(Object... params) {
        StringBuilder s = new StringBuilder();
        for(Object o : params)
            s.append(o.toString()).append(" ");

        System.out.println(s);
    }

    public static void warn(Object... params) {
        StringBuilder s = new StringBuilder("\\u001B[33m");
        for(Object o : params)
            s.append(o.toString()).append(" ");

        System.out.println(s);
    }

    public static void logf(String s, Object... params) {
        System.out.println(String.format(s, params));
    }
}