package services;

import entities.User;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    Connection cnx;

    public UserService() {
        cnx = MyDB.getInstance().getConx();
    }
    public void ajouter(User u) {
        String sql = "INSERT INTO user(email, roles, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, u.getUsername());  // store username in email
            ps.setString(2, "[\"" + u.getRole() + "\"]"); // store as JSON array string
            ps.setString(3, u.getPassword());
            ps.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }



    public boolean login(String username, String password) {
        String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // login successful if user exists
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
    public List<User> afficher() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return list;
    }
}

