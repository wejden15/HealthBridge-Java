package services;

import entities.Prescription;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService implements Service<Prescription> {

    private Connection cnx;

    public PrescriptionService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Prescription prescription) throws SQLException {
        String sql = "insert into prescription(appointment_id, medical_details, doctor_notes) " +
                "values(?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, prescription.getAppointment_id());
        ps.setString(2, prescription.getMedical_details());
        ps.setString(3, prescription.getDoctor_notes());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Prescription prescription) throws SQLException {
        String sql = "update prescription set appointment_id = ?, medical_details = ?, doctor_notes = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, prescription.getAppointment_id());
        ps.setString(2, prescription.getMedical_details());
        ps.setString(3, prescription.getDoctor_notes());
        ps.setInt(4, prescription.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Prescription prescription) throws SQLException {
        String sql = "delete from prescription where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, prescription.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Prescription> recuperer() throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "select * from prescription";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            int appointment_id = rs.getInt("appointment_id");
            String medical_details = rs.getString("medical_details");
            String doctor_notes = rs.getString("doctor_notes");
            Prescription p = new Prescription(id, appointment_id, medical_details, doctor_notes);
            prescriptions.add(p);
        }
        return prescriptions;
    }

    public Prescription getByAppointmentId(int appointmentId) throws SQLException {
        String sql = "select * from prescription where appointment_id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, appointmentId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("id");
            String medical_details = rs.getString("medical_details");
            String doctor_notes = rs.getString("doctor_notes");
            return new Prescription(id, appointmentId, medical_details, doctor_notes);
        }
        return null;
    }
} 