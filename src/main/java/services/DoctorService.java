package services;

import entities.Doctor;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorService implements Service<Doctor> {

    private Connection cnx;

    public DoctorService() {
        cnx = MyDatabase.getInstance().getCnx();
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