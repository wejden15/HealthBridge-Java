package controllers;

import entities.Appointment;
import entities.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AppointmentService;
import services.DoctorService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AjouterAppointmentController {
    @FXML
    private TextField tfClientName;
    @FXML
    private ComboBox<Doctor> cbDoctors;
    @FXML
    private DatePicker dpAppointmentDate;

    @FXML
    void initialize() {
        try {
            DoctorService doctorService = new DoctorService();
            ObservableList<Doctor> doctors = FXCollections.observableArrayList(doctorService.recuperer());
            
            // Set up the ComboBox
            cbDoctors.setItems(doctors);
            
            // Set how to display doctors in the dropdown
            cbDoctors.setCellFactory(param -> new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " - " + item.getSpecialty());
                    }
                }
            });
            
            // Set how to display the selected doctor
            cbDoctors.setButtonCell(new ListCell<Doctor>() {
                @Override
                protected void updateItem(Doctor item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Doctor");
                    } else {
                        setText(item.getName() + " - " + item.getSpecialty());
                    }
                }
            });
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error loading doctors: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void AfficherAppointments(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherAppointment.fxml"));
            Stage stage = (Stage) tfClientName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Show Appointments");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void GoToMain(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainAppointment.fxml"));
            Stage stage = (Stage) tfClientName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointment Management System");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void AjouterAppointment(ActionEvent actionEvent) {
        if (tfClientName.getText().isEmpty() || cbDoctors.getValue() == null || dpAppointmentDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please fill all fields");
            alert.showAndWait();
            return;
        }

        AppointmentService appointmentService = new AppointmentService();
        try {
            LocalDate date = dpAppointmentDate.getValue();
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(9, 0)); // Default to 9 AM
            
            appointmentService.ajouter(
                    new Appointment(
                            cbDoctors.getValue().getId(),
                            tfClientName.getText(),
                            dateTime
                    ));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Appointment added successfully");
            alert.showAndWait();
            
            // Clear fields
            tfClientName.clear();
            cbDoctors.getSelectionModel().clearSelection();
            dpAppointmentDate.setValue(null);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding appointment: " + e.getMessage());
            alert.showAndWait();
        }
    }
} 