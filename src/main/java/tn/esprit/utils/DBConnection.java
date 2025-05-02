package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/healthbridge"; // Ta BDD
    private static final String USER = "root"; // Ton username MySQL
    private static final String PASSWORD = ""; // Ton password (vide si par défaut)

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion à la base de données établie !");
            } catch (SQLException e) {
                System.out.println("❌ Erreur de connexion à la base : " + e.getMessage());
            }
        }
        return connection;
    }
}
