package gui;

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
            
            // Set minimum date to today
            dpAppointmentDate.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
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
            Parent root = FXMLLoader.load(getClass().getResource("AfficherAppointment.fxml"));
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
            Parent root = FXMLLoader.load(getClass().getResource("MainAppointment.fxml"));
            Stage stage = (Stage) tfClientName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Appointment Management System");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void AjouterAppointment(ActionEvent actionEvent) {
        // Validate client name
        if (tfClientName.getText().trim().length() < 3) {
            showAlert("Error", "Client name must be at least 3 characters long");
            return;
        }

        // Validate doctor selection
        if (cbDoctors.getValue() == null) {
            showAlert("Error", "Please select a doctor");
            return;
        }

        // Validate appointment date
        if (dpAppointmentDate.getValue() == null) {
            showAlert("Error", "Please select an appointment date");
            return;
        }

        // Validate that date is not in the past
        if (dpAppointmentDate.getValue().isBefore(LocalDate.now())) {
            showAlert("Error", "Appointment date cannot be in the past");
            return;
        }

        AppointmentService appointmentService = new AppointmentService();
        try {
            LocalDate date = dpAppointmentDate.getValue();
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(9, 0)); // Default to 9 AM
            
            appointmentService.ajouter(
                    new Appointment(
                            cbDoctors.getValue().getId(),
                            tfClientName.getText().trim(),
                            dateTime
                    ));
            
            showAlert("Success", "Appointment added successfully");
            
            // Clear fields
            tfClientName.clear();
            cbDoctors.getSelectionModel().clearSelection();
            dpAppointmentDate.setValue(null);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            showAlert("Error", "Error adding appointment: " + e.getMessage());
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