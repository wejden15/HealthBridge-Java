package gui;

import entities.Doctor;
import entities.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import services.DoctorService;
import services.UserService;
import services.UserSession;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private void initialize() {
        // Clear any residual data when returning to login
        usernameField.clear();
        passwordField.clear();

        // Optional: focus on username field
        Platform.runLater(() -> usernameField.requestFocus());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        UserService userService = new UserService();
        User user = userService.login(username, password);

        if (user != null) {
            UserSession.login(user); // Set session as logged in

            try {
                String fxmlFile;
                Doctor doctor = null;

                if (user.getRole().contains("ROLE_ADMIN")) {
                    fxmlFile = "hello-view.fxml";
                } else if (user.getRole().equalsIgnoreCase("Doctor")) {
                    DoctorService doctorService = new DoctorService();
                    doctor = doctorService.getDoctorByName(user.getUsername());
                    fxmlFile = "dashboard_doctor.fxml";
                } else {
                    fxmlFile = "dashboard_patient.fxml";
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                if (loader.getController() instanceof DoctorDashboardController) {
                    DoctorDashboardController controller = loader.getController();
                    controller.setDoctorData(doctor);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load dashboard: " + e.getMessage());
            }
        } else {
            showAlert("Login Failed", "Invalid username or password");
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
