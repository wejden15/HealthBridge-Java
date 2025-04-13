package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;



    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        UserService us = new UserService();
        boolean success = us.login(username, password);

        if (success) {
            System.out.println("Login successful!");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml")); // or any other view
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Dashboard");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Login failed!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }
    }



    @FXML
    void switchToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
