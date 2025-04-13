package controllers;

import entities.Appointment;
import entities.Doctor;
import entities.Prescription;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.AppointmentService;
import services.DoctorService;
import services.PrescriptionService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAppointmentsController {
    @FXML
    private TableView<Appointment> appointmentsTable;
    
    @FXML
    private TableColumn<Appointment, String> clientNameColumn;
    
    @FXML
    private TableColumn<Appointment, String> doctorNameColumn;
    
    @FXML
    private TableColumn<Appointment, String> specialtyColumn;
    
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    
    @FXML
    private TableColumn<Appointment, Void> actionsColumn;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteAllButton;

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
            
            // Set up table columns
            clientNameColumn.setCellValueFactory(cellData -> cellData.getValue().clientNameProperty());
            
            doctorNameColumn.setCellValueFactory(cellData -> {
                Doctor doctor = doctorMap.get(cellData.getValue().getDoctor_id());
                return doctor != null ? doctor.nameProperty() : null;
            });
            
            specialtyColumn.setCellValueFactory(cellData -> {
                Doctor doctor = doctorMap.get(cellData.getValue().getDoctor_id());
                return doctor != null ? doctor.specialtyProperty() : null;
            });
            
            dateColumn.setCellValueFactory(cellData -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new SimpleStringProperty(cellData.getValue().getAppointment_date().format(formatter));
            });
            
            // Set up actions column
            actionsColumn.setCellFactory(createActionsCellFactory());
            
            loadAppointments();

        } catch (SQLException e) {
            showAlert("Error", "Error loading appointments: " + e.getMessage());
        }
    }

    private Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> createActionsCellFactory() {
        return new Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                return new TableCell<Appointment, Void>() {
                    private final Button deleteButton = new Button("Delete");
                    {
                        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            deleteAppointment(appointment);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };
    }

    private void loadAppointments() {
        try {
            List<Appointment> appointments = appointmentService.recuperer();
            appointmentsTable.getItems().setAll(appointments);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    private void deleteAppointment(Appointment appointment) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Appointment");
        confirmAlert.setHeaderText("Are you sure you want to delete this appointment?");
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
                appointmentsTable.getItems().remove(appointment);
                showAlert("Success", "Appointment and associated prescription deleted successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete appointment: " + e.getMessage());
            }
        }
    }

    @FXML
    private void DeleteAllAppointments() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete All Appointments");
        confirmAlert.setHeaderText("Are you sure you want to delete ALL appointments?");
        confirmAlert.setContentText("This action cannot be undone. All appointments and their associated prescriptions will be permanently deleted.");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                // First delete all prescriptions
                List<Prescription> prescriptions = prescriptionService.recuperer();
                for (Prescription prescription : prescriptions) {
                    prescriptionService.supprimer(prescription);
                }
                
                // Then delete all appointments
                List<Appointment> appointments = appointmentService.recuperer();
                for (Appointment appointment : appointments) {
                    appointmentService.supprimer(appointment);
                }
                
                appointmentsTable.getItems().clear();
                showAlert("Success", "All appointments and prescriptions have been deleted");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete appointments: " + e.getMessage());
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