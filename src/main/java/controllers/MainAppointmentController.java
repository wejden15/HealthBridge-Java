package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAppointmentController {

    @FXML
    private Button addAppointmentButton;

    @FXML
    private Button showAppointmentsButton;

    @FXML
    private void GoToAddAppointment() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterAppointment.fxml"));
            Stage stage = (Stage) addAppointmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Book Appointment");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void GoToShowAppointments() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherAppointment.fxml"));
            Stage stage = (Stage) showAppointmentsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("My Appointments");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
} 