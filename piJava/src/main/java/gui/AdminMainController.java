package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMainController {

    @FXML
    private Button manageAppointmentsButton;

    @FXML
    private Button managePrescriptionsButton;

    @FXML
    private void GoToManageAppointments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminAppointments.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) manageAppointmentsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Appointments");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open appointments management: " + e.getMessage());
        }
    }

    @FXML
    private void GoToManagePrescriptions() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminPrescriptions.fxml"));
            Stage stage = (Stage) managePrescriptionsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Prescriptions");
        } catch (IOException e) {
            showAlert("Error", "Failed to open prescriptions management: " + e.getMessage());
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