package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Set the stage title
        stage.setTitle("HealthBridge - 2025");

        // Set the scene
        stage.setScene(scene);


        // Show the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
