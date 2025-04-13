package gui;

import entities.Prescription;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import services.PrescriptionService;

import java.sql.SQLException;

public class ViewPrescriptionController {

    @FXML
    private TextArea medicalDetailsArea;

    @FXML
    private TextArea doctorNotesArea;

    private Prescription prescription;
    private PrescriptionService prescriptionService;

    public void initialize() {
        prescriptionService = new PrescriptionService();
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
        medicalDetailsArea.setText(prescription.getMedical_details());
        doctorNotesArea.setText(prescription.getDoctor_notes());
    }

    @FXML
    private void savePrescription() {
        if (medicalDetailsArea.getText().isEmpty()) {
            showAlert("Error", "Medical details cannot be empty");
            return;
        }

        try {
            prescription.setMedical_details(medicalDetailsArea.getText());
            prescription.setDoctor_notes(doctorNotesArea.getText());
            prescriptionService.modifier(prescription);
            showAlert("Success", "Prescription updated successfully");
            close();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update prescription: " + e.getMessage());
        }
    }

    @FXML
    private void close() {
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