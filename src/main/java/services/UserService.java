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
            ps.setString(1, u.getUsername()); // Store username in the email column
            ps.setString(2, "[\"" + u.getRole() + "\"]"); // Format role as JSON array
            ps.setString(3, u.getPassword());

            ps.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM user WHERE email = ? AND password = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String rolesJson = rs.getString("roles");
                String role = parseRoleFromJson(rolesJson);
                return new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),  // username is stored in email column
                        rs.getString("password"),
                        role
                );
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    private String parseRoleFromJson(String rolesJson) {
        if (rolesJson == null || rolesJson.isEmpty()) return "USER";
        // Remove the brackets and quotes but keep the ROLE_ prefix
        return rolesJson.replaceAll("[\\[\\]\"]", "");
    }

    public List<User> afficher() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String rolesJson = rs.getString("roles");
                String role = parseRoleFromJson(rolesJson); // Parse role from JSON

                User u = new User(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"), // Use 'email' column as username
                        rs.getString("password"),
                        role
                );
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return list;
    }

}

