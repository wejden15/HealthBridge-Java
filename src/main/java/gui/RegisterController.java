package gui;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import services.UserService;

public class RegisterController {


    @FXML
    private TextField fullnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;


    private ComboBox<String> roleComboBox;

    @FXML
    void handleRegister(ActionEvent event) {
        String fullname = fullnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        User u = new User(fullname, username, password, role);
        UserService us = new UserService();
        us.ajouter(u);
    }

    @FXML
    void switchToLogin(ActionEvent event) {
        // TODO: Logic to switch to login scene
        System.out.println("Switching to login...");
    }
}
