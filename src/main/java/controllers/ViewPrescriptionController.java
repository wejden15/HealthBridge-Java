package controllers;

import entities.Prescription;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ViewPrescriptionController {

    @FXML
    private TextArea medicalDetailsArea;

    @FXML
    private TextArea doctorNotesArea;

    public void setPrescription(Prescription prescription) {
        medicalDetailsArea.setText(prescription.getMedical_details());
        doctorNotesArea.setText(prescription.getDoctor_notes());
    }

    @FXML
    private void close() {
        Stage stage = (Stage) medicalDetailsArea.getScene().getWindow();
        stage.close();
    }
} 