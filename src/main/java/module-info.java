module gui.pijava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens gui to javafx.fxml;
    opens entities to javafx.base;
    exports gui;
    exports entities;
}