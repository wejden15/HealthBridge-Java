package tn.esprit.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BackupService {

    public static void backupEvenementTable(String backupFilePath) {
        Connection connection = DBConnection.getConnection();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM evenement");

            StringBuilder sqlBackup = new StringBuilder();
            sqlBackup.append("USE healthbridge;\n");
            sqlBackup.append("DELETE FROM evenement;\n");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom").replace("'", "\\'");
                String categorie = rs.getString("categorie").replace("'", "\\'");
                String date = rs.getDate("date").toString();

                sqlBackup.append(String.format(
                        "INSERT INTO evenement (id, nom, categorie, date) VALUES (%d, '%s', '%s', '%s');\n",
                        id, nom, categorie, date
                ));
            }

            try (FileWriter writer = new FileWriter(backupFilePath)) {
                writer.write(sqlBackup.toString());
                System.out.println("✅ Sauvegarde réussie vers : " + backupFilePath);
            } catch (IOException e) {
                System.out.println("❌ Erreur lors de l'écriture du fichier backup : " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la récupération des données : " + e.getMessage());
        }
    }
}
