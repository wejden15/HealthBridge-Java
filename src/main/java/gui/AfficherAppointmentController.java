package gui;

import entities.Appointment;
import entities.Doctor;
import entities.Prescription;
import javafx.event.ActionEvent;
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
import services.DoctorService;
import services.PrescriptionService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfficherAppointmentController {
    @FXML
    private VBox appointmentsContainer;

    @FXML
    private Button backButton;

    @FXML
    private Button bookAppointmentButton;

    private Map<Integer, Doctor> doctorMap = new HashMap<>();
    private AppointmentService appointmentService;
    private PrescriptionService prescriptionService;

    public void initialize() {
        try {
            // Load all doctors into a map for quick lookup
            DoctorService doctorService = new DoctorService();
            for (Doctor doctor : doctorService.recuperer()) {
                doctorMap.put(doctor.getId(), doctor);
            }

            appointmentService = new AppointmentService();
            prescriptionService = new PrescriptionService();
            loadAppointments();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error loading appointments: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadAppointments() {
        appointmentsContainer.getChildren().clear();
        try {
            List<Appointment> appointments = appointmentService.recuperer();
            for (Appointment appointment : appointments) {
                appointmentsContainer.getChildren().add(createAppointmentCard(appointment));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    private AnchorPane createAppointmentCard(Appointment appointment) {
        Doctor doctor = doctorMap.get(appointment.getDoctor_id());
        String doctorName = doctor != null ? doctor.getName() : "Unknown Doctor";
        String doctorSpecialty = doctor != null ? doctor.getSpecialty() : "Unknown";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = appointment.getAppointment_date().format(formatter);

        AnchorPane card = new AnchorPane();
        card.setPrefWidth(680.0);
        card.setPrefHeight(120.0);
        card.setStyle("-fx-background-color: white; -fx-border-color: #1e90ff; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Client Name
        Text clientName = new Text("Client: " + appointment.getClient_name());
        clientName.setLayoutX(20.0);
        clientName.setLayoutY(30.0);
        clientName.setFont(new Font(14.0));

        // Doctor Info
        Text doctorInfo = new Text("Doctor: " + doctorName + " (" + doctorSpecialty + ")");
        doctorInfo.setLayoutX(20.0);
        doctorInfo.setLayoutY(60.0);
        doctorInfo.setFont(new Font(14.0));

        // Date and Time
        Text dateTime = new Text("Date: " + formattedDate);
        dateTime.setLayoutX(20.0);
        dateTime.setLayoutY(90.0);
        dateTime.setFont(new Font(14.0));

        // Prescription Button
        Button prescriptionButton = new Button();
        try {
            Prescription prescription = prescriptionService.getByAppointmentId(appointment.getId());
            if (prescription == null) {
                prescriptionButton.setText("Add Prescription");
                prescriptionButton.setStyle("-fx-background-color: #1e90ff; -fx-text-fill: white;");
                prescriptionButton.setOnAction(e -> addPrescription(appointment));
            } else {
                prescriptionButton.setText("View Prescription");
                prescriptionButton.setStyle("-fx-background-color: #32cd32; -fx-text-fill: white;");
                prescriptionButton.setOnAction(e -> viewPrescription(prescription));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to check prescription: " + e.getMessage());
        }
        prescriptionButton.setLayoutX(550.0);
        prescriptionButton.setLayoutY(30.0);
        prescriptionButton.setPrefHeight(40.0);
        prescriptionButton.setPrefWidth(120.0);

        // Cancel Button
        Button cancelButton = new Button("Cancel Appointment");
        cancelButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        cancelButton.setLayoutX(550.0);
        cancelButton.setLayoutY(80.0);
        cancelButton.setPrefHeight(40.0);
        cancelButton.setPrefWidth(120.0);
        cancelButton.setOnAction(e -> cancelAppointment(appointment, card));

        card.getChildren().addAll(clientName, doctorInfo, dateTime, prescriptionButton, cancelButton);
        return card;
    }

    private void addPrescription(Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddPrescription.fxml"));
            Parent root = loader.load();
            AddPrescriptionController controller = loader.getController();
            controller.setAppointment(appointment);
            
            Stage stage = new Stage();
            stage.setTitle("Add Prescription");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open prescription form: " + e.getMessage());
        }
    }

    private void viewPrescription(Prescription prescription) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewPrescription.fxml"));
            Parent root = loader.load();
            ViewPrescriptionController controller = loader.getController();
            controller.setPrescription(prescription);
            
            Stage stage = new Stage();
            stage.setTitle("View Prescription");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open prescription view: " + e.getMessage());
        }
    }

    private void cancelAppointment(Appointment appointment, AnchorPane card) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Appointment");
        confirmAlert.setHeaderText("Are you sure you want to cancel this appointment?");
        confirmAlert.setContentText("This action cannot be undone. Any associated prescription will also be deleted.");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                // First, try to delete the prescription if it exists
                Prescription prescription = prescriptionService.getByAppointmentId(appointment.getId());
                if (prescription != null) {
                    prescriptionService.supprimer(prescription);
                }
                
                // Then delete the appointment
                appointmentService.supprimer(appointment);
                appointmentsContainer.getChildren().remove(card);
                showAlert("Success", "Appointment and associated prescription cancelled successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to cancel appointment: " + e.getMessage());
            }
        }
    }

    @FXML
    private void GoToMain() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainAppointment.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Failed to return to main menu: " + e.getMessage());
        }
    }

    @FXML
    private void BookAppointment() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AjouterAppointment.fxml"));
            Stage stage = (Stage) bookAppointmentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Error", "Failed to open appointment form: " + e.getMessage());
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