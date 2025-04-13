package entities;

public class Prescription {
    private int id;
    private int appointment_id;
    private String medical_details;
    private String doctor_notes;

    public Prescription() {
    }

    public Prescription(int id, int appointment_id, String medical_details, String doctor_notes) {
        this.id = id;
        this.appointment_id = appointment_id;
        this.medical_details = medical_details;
        this.doctor_notes = doctor_notes;
    }

    public Prescription(int appointment_id, String medical_details, String doctor_notes) {
        this.appointment_id = appointment_id;
        this.medical_details = medical_details;
        this.doctor_notes = doctor_notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getMedical_details() {
        return medical_details;
    }

    public void setMedical_details(String medical_details) {
        this.medical_details = medical_details;
    }

    public String getDoctor_notes() {
        return doctor_notes;
    }

    public void setDoctor_notes(String doctor_notes) {
        this.doctor_notes = doctor_notes;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", appointment_id=" + appointment_id +
                ", medical_details='" + medical_details + '\'' +
                ", doctor_notes='" + doctor_notes + '\'' +
                '}';
    }
} 