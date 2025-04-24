module gui.pijava {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    
    // Database
    requires java.sql;
    
    // Google API dependencies
    requires google.api.client;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.calendar;
    
    // System dependencies
    requires jdk.httpserver;

    // Add automatic modules
    requires commons.email;
    requires org.apache.pdfbox;

    // Open packages to JavaFX
    opens gui to javafx.fxml, javafx.graphics, javafx.base, javafx.controls;
    opens entities to javafx.base, javafx.controls;
    opens services to javafx.base;
    
    // Export your packages
    exports gui;
    exports entities;
    exports services;
}