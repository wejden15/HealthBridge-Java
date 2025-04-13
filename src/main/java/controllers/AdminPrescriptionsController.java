package controllers;

import entities.Appointment;
import entities.Prescription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.AppointmentService;
import services.PrescriptionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminPrescriptionsController {
    @FXML
    private VBox prescriptionsContainer;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteAllButton;

    private PrescriptionService prescriptionService;
    private AppointmentService appointmentService;

    public void initialize() {
        prescriptionService = new PrescriptionService();
        appointmentService = new AppointmentService();
        loadPrescriptions();
    }

    private void loadPrescriptions() {
        prescriptionsContainer.getChildren().clear();
        try {
            List<Prescription> prescriptions = prescriptionService.recuperer();
            for (Prescription prescription : prescriptions) {
                prescriptionsContainer.getChildren().add(createPrescriptionCard(prescription));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load prescriptions: " + e.getMessage());
        }
    }

    private AnchorPane createPrescriptionCard(Prescription prescription) {
        AnchorPane card = new AnchorPane();
        card.setPrefWidth(680.0);
        card.setPrefHeight(150.0);
        card.setStyle("-fx-background-color: white; -fx-border-color: #1e90ff; -fx-border-radius: 5; -fx-background-radius: 5;");

        try {
            Appointment appointment = appointmentService.getById(prescription.getAppointment_id());
            if (appointment != null) {
                // Appointment Info
                Text appointmentInfo = new Text("Appointment: " + appointment.getClient_name() + " - " + 
                        appointment.getAppointment_date().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                appointmentInfo.setLayoutX(20.0);
                appointmentInfo.setLayoutY(30.0);
                appointmentInfo.setFont(new Font(14.0));
                card.getChildren().add(appointmentInfo);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load appointment details: " + e.getMessage());
        }

        // Medical Details
        Text medicalDetails = new Text("Medical Details: " + prescription.getMedical_details());
        medicalDetails.setLayoutX(20.0);
        medicalDetails.setLayoutY(60.0);
        medicalDetails.setFont(new Font(14.0));
        medicalDetails.setWrappingWidth(600.0);

        // Doctor's Notes
        Text doctorNotes = new Text("Doctor's Notes: " + prescription.getDoctor_notes());
        doctorNotes.setLayoutX(20.0);
        doctorNotes.setLayoutY(90.0);
        doctorNotes.setFont(new Font(14.0));
        doctorNotes.setWrappingWidth(600.0);

        // Delete Button
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        deleteButton.setLayoutX(550.0);
        deleteButton.setLayoutY(30.0);
        deleteButton.setPrefHeight(40.0);
        deleteButton.setPrefWidth(120.0);
        deleteButton.setOnAction(e -> deletePrescription(prescription, card));

        card.getChildren().addAll(medicalDetails, doctorNotes, deleteButton);
        return card;
    }

    private void deletePrescription(Prescription prescription, AnchorPane card) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Prescription");
        confirmAlert.setHeaderText("Are you sure you want to delete this prescription?");
        confirmAlert.setContentText("This action cannot be undone.");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                prescriptionService.supprimer(prescription);
                prescriptionsContainer.getChildren().remove(card);
                showAlert("Success", "Prescription deleted successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete prescription: " + e.getMessage());
            }
        }
    }

    @FXML
    private void DeleteAllPrescriptions() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete All Prescriptions");
        confirmAlert.setHeaderText("Are you sure you want to delete ALL prescriptions?");
        confirmAlert.setContentText("This action cannot be undone. All prescriptions will be permanently deleted.");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                List<Prescription> prescriptions = prescriptionService.recuperer();
                for (Prescription prescription : prescriptions) {
                    prescriptionService.supprimer(prescription);
                }
                
                prescriptionsContainer.getChildren().clear();
                showAlert("Success", "All prescriptions have been deleted");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete prescriptions: " + e.getMessage());
            }
        }
    }

    @FXML
    private void GoToAdminMain() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminMain.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Failed to return to admin dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 