package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int doctor_id;
    private String client_name;
    private LocalDateTime appointment_date;
    
    // JavaFX Properties
    private final StringProperty clientNameProperty = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> appointmentDateProperty = new SimpleObjectProperty<>();

    public Appointment() {
        // Initialize properties
        clientNameProperty.set("");
        appointmentDateProperty.set(LocalDateTime.now());
    }

    public Appointment(int id, int doctor_id, String client_name, LocalDateTime appointment_date) {
        this.id = id;
        this.doctor_id = doctor_id;
        this.client_name = client_name;
        this.appointment_date = appointment_date;
        
        // Initialize properties
        clientNameProperty.set(client_name);
        appointmentDateProperty.set(appointment_date);
    }

    public Appointment(int doctor_id, String client_name, LocalDateTime appointment_date) {
        this.doctor_id = doctor_id;
        this.client_name = client_name;
        this.appointment_date = appointment_date;
        
        // Initialize properties
        clientNameProperty.set(client_name);
        appointmentDateProperty.set(appointment_date);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
        this.clientNameProperty.set(client_name);
    }

    public LocalDateTime getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(LocalDateTime appointment_date) {
        this.appointment_date = appointment_date;
        this.appointmentDateProperty.set(appointment_date);
    }

    // Property getters
    public StringProperty clientNameProperty() {
        return clientNameProperty;
    }

    public ObjectProperty<LocalDateTime> appointmentDateProperty() {
        return appointmentDateProperty;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", doctor_id=" + doctor_id +
                ", client_name='" + client_name + '\'' +
                ", appointment_date=" + appointment_date +
                '}';
    }
} 