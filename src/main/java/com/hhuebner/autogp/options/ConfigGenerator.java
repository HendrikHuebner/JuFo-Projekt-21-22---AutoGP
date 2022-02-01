package com.hhuebner.autogp.options;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ConfigGenerator {

    public static void main(String[] args) {
        List<Option<?>> options = OptionsHandler.INSTANCE.getOptions();
        Properties prop = new Properties();

        try(FileOutputStream fileOutputStream = new FileOutputStream("config.properties")){
            for(Option<?> o : options) {
                prop.setProperty(o.getKey(), o.getDefaultValue().toString());
            }

            prop.store(fileOutputStream, "AutoGP options");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
