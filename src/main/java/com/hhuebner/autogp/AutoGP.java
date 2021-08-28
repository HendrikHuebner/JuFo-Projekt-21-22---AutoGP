package com.hhuebner.autogp;

import com.hhuebner.autogp.controllers.CanvasController;
import com.hhuebner.autogp.controllers.MainSceneController;
import com.hhuebner.autogp.core.ControllerFactory;
import com.hhuebner.autogp.core.InputHandler;
import com.hhuebner.autogp.ui.Camera;
import com.hhuebner.autogp.ui.CanvasRenderer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.function.Supplier;

public class AutoGP extends Application {
    private Scene scene = null;

    //public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws IOException {

        Camera camera = new Camera();
        CanvasRenderer canvasRenderer = new CanvasRenderer(camera);
        InputHandler inputHandler = new InputHandler(() -> this.scene);
        ControllerFactory controllerFactory = new ControllerFactory();
        controllerFactory.registerController(MainSceneController.class, new MainSceneController(inputHandler));
        CanvasController canvasController = controllerFactory.registerController(CanvasController.class,
                 new CanvasController(canvasRenderer, camera, inputHandler));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("auto_gp.fxml"));
        controllerFactory.setControllers(loader);

        Parent root = loader.load();
        Scene main = new Scene(root, 819, 415);
        this.scene = main;

        stage.setTitle("AutoGP");
        stage.setResizable(false);
        stage.setScene(main);
        stage.show();

        canvasRenderer.render(canvasController.canvas, inputHandler);
    }

    public static void main(String[] args) {
        launch();
    }

    //LOGGER
    public static void log(Object... params) {
        String s = "";
        for(Object o : params)
            s += o.toString() + " ";

        System.out.println(s);
    }

    public static void logf(String s, Object... params) {
        System.out.println(String.format(s, params));
    }
}