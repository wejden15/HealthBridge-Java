package tn.esprit.service;

import tn.esprit.entity.Evenement;
import tn.esprit.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements IEvenementService {

    private final Connection connection;
    private String generatePassCode() {
        int randomNumber = (int)(Math.random() * 9000) + 1000; // Generate 4 digits
        return "PASS-" + randomNumber;
    }

    public EvenementService() {
        connection = DBConnection.getConnection();
    }

    @Override
    public void ajouterEvenement(Evenement evenement) {
        String query = "INSERT INTO evenement (nom, categorie, date, pass_code) VALUES (?, ?, ?, ?)"; // 🔥 add pass_code

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            evenement.setPassCode(generatePassCode()); // 🔥 Generate BEFORE inserting

            ps.setString(1, evenement.getNom());
            ps.setString(2, evenement.getCategorie());
            ps.setDate(3, java.sql.Date.valueOf(evenement.getDate()));
            ps.setString(4, evenement.getPassCode()); // 🔥 Insert the generated pass

            ps.executeUpdate();

            System.out.println("✅ Événement ajouté avec passcode: " + evenement.getPassCode());

        } catch (SQLException e) {
            System.out.println("❌ Erreur d'insertion : " + e.getMessage());
        }
    }


    @Override
    public void supprimerEvenement(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Événement supprimé.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression : " + e.getMessage());
        }
    }

    @Override
    public void modifierEvenement(Evenement evenement) {
        String query = "UPDATE evenement SET nom = ?, categorie = ?, date = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, evenement.getNom());
            ps.setString(2, evenement.getCategorie());
            ps.setDate(3, java.sql.Date.valueOf(evenement.getDate()));
            ps.setInt(4, evenement.getId());
            ps.executeUpdate();
            System.out.println("✅ Événement modifié.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur modification : " + e.getMessage());
        }
    }

    @Override
    public List<Evenement> rechercherParCategorie(String categorie) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE categorie = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categorie);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getDate("date").toLocalDate()
                );
                evenements.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur recherche : " + e.getMessage());
        }

        return evenements;
    }

    @Override
    public List<Evenement> getAllEvenements() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getDate("date").toLocalDate()
                );
                e.setPassCode(rs.getString("pass_code")); // 🔥 add this line

                evenements.add(e);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur récupération : " + e.getMessage());
        }

        return evenements;
    }
}
