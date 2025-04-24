package services;

import entities.Doctor;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorService implements Service<Doctor> {

    private Connection cnx;

    public DoctorService() {
        cnx = MyDB.getInstance().getConx();
    }

    public void addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctor (name, specialty, picture) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialty());
            ps.setString(3, doctor.getPicture());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding doctor: " + e.getMessage());
        }
    }

    public Doctor getDoctorByName(String name) {
        String sql = "SELECT * FROM doctor WHERE name = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialty"),
                        rs.getString("picture")
                );
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Error fetching doctor: " + e.getMessage());
            return null;
        }
    }
    @Override
    public void ajouter(Doctor doctor) throws SQLException {
        String sql = "insert into doctor(name, specialty, picture) " +
                "values(?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, doctor.getName());
        ps.setString(2, doctor.getSpecialty());
        ps.setString(3, doctor.getPicture());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Doctor doctor) throws SQLException {
        String sql = "update doctor set name = ?, specialty = ?, picture = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, doctor.getName());
        ps.setString(2, doctor.getSpecialty());
        ps.setString(3, doctor.getPicture());
        ps.setInt(4, doctor.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Doctor doctor) throws SQLException {
        String sql = "delete from doctor where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, doctor.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Doctor> recuperer() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "select * from doctor";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String specialty = rs.getString("specialty");
            String picture = rs.getString("picture");
            Doctor d = new Doctor(id, name, specialty, picture);
            doctors.add(d);
        }
        return doctors;
    }
} 