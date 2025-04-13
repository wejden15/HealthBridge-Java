package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreenController {

    @FXML
    private Button frontOfficeButton;

    @FXML
    private Button backOfficeButton;


    @FXML
    private void GoToFrontOffice() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainAppointment.fxml"));
            Stage stage = (Stage) frontOfficeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Front Office - Appointment Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void GoToBackOffice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminMain.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backOfficeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Back Office - Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open back office: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 