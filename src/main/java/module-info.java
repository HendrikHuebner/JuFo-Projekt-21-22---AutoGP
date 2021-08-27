module com.hhuebner.autogp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hhuebner.autogp to javafx.fxml;
    exports com.hhuebner.autogp;
    exports com.hhuebner.autogp.controllers;
    opens com.hhuebner.autogp.controllers to javafx.fxml;
}