package controllers;

import entities.Appointment;
import entities.Prescription;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.PrescriptionService;

import java.sql.SQLException;

public class AddPrescriptionController {

    @FXML
    private TextArea medicalDetailsArea;

    @FXML
    private TextArea doctorNotesArea;

    private Appointment appointment;
    private PrescriptionService prescriptionService;

    public void initialize() {
        prescriptionService = new PrescriptionService();
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @FXML
    private void savePrescription() {
        if (medicalDetailsArea.getText().isEmpty()) {
            showAlert("Error", "Medical details cannot be empty");
            return;
        }

        try {
            Prescription prescription = new Prescription(
                appointment.getId(),
                medicalDetailsArea.getText(),
                doctorNotesArea.getText()
            );
            prescriptionService.ajouter(prescription);
            showAlert("Success", "Prescription added successfully");
            closeWindow();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add prescription: " + e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) medicalDetailsArea.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 