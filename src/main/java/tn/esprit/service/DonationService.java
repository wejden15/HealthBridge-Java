package tn.esprit.service;

import tn.esprit.entity.Donation;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationService {

    private final Connection connection = DBConnection.getConnection();

    public void addDonation(Donation donation) {
        String query = "INSERT INTO donation (evenement_id, donor_name, amount, comment) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, donation.getEvenementId());
            ps.setString(2, donation.getDonorName());
            ps.setDouble(3, donation.getAmount());
            ps.setString(4, donation.getComment());
            ps.executeUpdate();

            System.out.println("✅ Donation enregistrée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Donation d = new Donation();
                d.setId(rs.getInt("id"));
                d.setEvenementId(rs.getInt("evenement_id"));
                d.setDonorName(rs.getString("donor_name"));
                d.setAmount(rs.getDouble("amount"));
                d.setComment(rs.getString("comment"));
                d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                donations.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    public List<Donation> getDonationsByEvent(int evenementId) {
        List<Donation> donations = new ArrayList<>();
        String query = "SELECT * FROM donation WHERE evenement_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, evenementId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Donation d = new Donation();
                d.setId(rs.getInt("id"));
                d.setEvenementId(rs.getInt("evenement_id"));
                d.setDonorName(rs.getString("donor_name"));
                d.setAmount(rs.getDouble("amount"));
                d.setComment(rs.getString("comment"));
                d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                donations.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }
}
