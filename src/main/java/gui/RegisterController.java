package gui;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;

public class RegisterController {


    @FXML
    private TextField fullnameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;



    @FXML
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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        roleComboBox.getSelectionModel().selectFirst(); // Select default value
    }

}
