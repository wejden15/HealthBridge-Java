package gui;

import entities.Doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class DoctorAppointmentsController implements Initializable {

    @FXML private Label doctorNameLabel;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> patientColumn;
    @FXML private TableColumn<Appointment, LocalDate> dateColumn;
    @FXML private TableColumn<Appointment, LocalTime> timeColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;

    private Doctor doctor;

    public void setDoctorData(Doctor doctor) {
        this.doctor = doctor;
        doctorNameLabel.setText("Dr. " + doctor.getName() + "'s Appointments");
        loadAppointments();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(
                new Appointment("John Smith", LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Confirmed"),
                new Appointment("Sarah Johnson", LocalDate.now().plusDays(2), LocalTime.of(10, 30), "Confirmed"),
                new Appointment("Michael Brown", LocalDate.now().plusDays(3), LocalTime.of(14, 0), "Pending")
        );
        appointmentsTable.setItems(appointments);
    }

    public static class Appointment {
        private final String patientName;
        private final LocalDate date;
        private final LocalTime time;
        private final String status;

        public Appointment(String patientName, LocalDate date, LocalTime time, String status) {
            this.patientName = patientName;
            this.date = date;
            this.time = time;
            this.status = status;
        }

        public String getPatientName() { return patientName; }
        public LocalDate getDate() { return date; }
        public LocalTime getTime() { return time; }
        public String getStatus() { return status; }
    }
}