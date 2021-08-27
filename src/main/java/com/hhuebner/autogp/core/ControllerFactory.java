package com.hhuebner.autogp.core;

import com.hhuebner.autogp.controllers.MainSceneController;
import javafx.fxml.FXMLLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ControllerFactory {

    private Map<Class<?>, Object> controllers = new HashMap<>();

    /**
     * Sets the controller factory. Must be called before FXML is loaded.
     * @param loader
     */
    public void setControllers(FXMLLoader loader) {
        loader.setControllerFactory((clazz) -> {
            if(this.controllers.containsKey(clazz)) {
                return this.controllers.get(clazz);
            } else {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    public <T> T registerController(Class<T> controllerClazz, T controller) {
        this.controllers.put(controllerClazz, controller);
        return controller;
    }
}
