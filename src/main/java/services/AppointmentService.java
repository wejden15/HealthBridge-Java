package services;

import entities.Appointment;
import utils.MyDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService implements Service<Appointment> {

    private Connection cnx;

    public AppointmentService() {
        cnx = MyDB.getInstance().getConx();
    }

    @Override
    public void ajouter(Appointment appointment) throws SQLException {
        String sql = "insert into appointment(doctor_id, client_name, appointment_date) " +
                "values(?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, appointment.getDoctor_id());
        ps.setString(2, appointment.getClient_name());
        ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointment_date()));
        ps.executeUpdate();
    }

    @Override
    public void modifier(Appointment appointment) throws SQLException {
        String sql = "update appointment set doctor_id = ?, client_name = ?, appointment_date = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, appointment.getDoctor_id());
        ps.setString(2, appointment.getClient_name());
        ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointment_date()));
        ps.setInt(4, appointment.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Appointment appointment) throws SQLException {
        String sql = "delete from appointment where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, appointment.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Appointment> recuperer() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "select * from appointment";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            int doctor_id = rs.getInt("doctor_id");
            String client_name = rs.getString("client_name");
            LocalDateTime appointment_date = rs.getTimestamp("appointment_date").toLocalDateTime();
            Appointment a = new Appointment(id, doctor_id, client_name, appointment_date);
            appointments.add(a);
        }
        return appointments;
    }

    public Appointment getById(int id) throws SQLException {
        String sql = "select * from appointment where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Appointment(
                rs.getInt("id"),
                rs.getInt("doctor_id"),
                rs.getString("client_name"),
                rs.getTimestamp("appointment_date").toLocalDateTime()
            );
        }
        return null;
    }
} 