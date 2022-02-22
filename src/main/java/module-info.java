module com.hhuebner.autogp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;


    opens com.hhuebner.autogp to javafx.fxml;
    opens com.hhuebner.autogp.core.engine to javafx.base;
    opens com.hhuebner.autogp.core.component.furniture to javafx.base;
    opens com.hhuebner.autogp.controllers to javafx.fxml;

    exports com.hhuebner.autogp;
    exports com.hhuebner.autogp.controllers;
    exports com.hhuebner.autogp.core to javafx.fxml;
    exports com.hhuebner.autogp.options to javafx.fxml;
    exports com.hhuebner.autogp.ui to javafx.fxml;
    exports com.hhuebner.autogp.ui.widgets to javafx.fxml;
}