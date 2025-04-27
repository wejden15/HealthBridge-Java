module gui.pijava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;
    requires twilio;
    requires com.google.zxing;
    requires com.google.zxing.javase;


    opens gui to javafx.fxml;
    opens entities to javafx.base;
    exports gui;
    exports entities;
}