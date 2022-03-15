module com.hhuebner.autogp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.hhuebner.autogp to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.hhuebner.autogp.core.component to com.fasterxml.jackson.databind;
    opens com.hhuebner.autogp.ui.symbols to com.fasterxml.jackson.databind;
    opens com.hhuebner.autogp.core.util to com.fasterxml.jackson.databind;
    opens com.hhuebner.autogp.core.component.furniture to javafx.base, com.fasterxml.jackson.databind;
    opens com.hhuebner.autogp.controllers to javafx.fxml;

    exports com.hhuebner.autogp;
    exports com.hhuebner.autogp.controllers;
    exports com.hhuebner.autogp.options to javafx.fxml;
    exports com.hhuebner.autogp.ui to javafx.fxml;
    exports com.hhuebner.autogp.ui.widgets to javafx.fxml;
    exports com.hhuebner.autogp.core;
    opens com.hhuebner.autogp.core to javafx.fxml;
    exports com.hhuebner.autogp.core.engine;
    opens com.hhuebner.autogp.core.engine to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
}