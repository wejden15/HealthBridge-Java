package services;

import entities.Commande;
import entities.CrudCommande;
import entities.Produit;
import utils.MyDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande implements CrudCommande<Commande> {

    public Connection conx;
    public Statement stm;


    public ServiceCommande() {
        conx = MyDB.getInstance().getConx();
    }

    @Override
    public void ajouter(Commande c) {
        String req =
                "INSERT INTO commande"
                        + "(produits_id,date,totale,statut)"
                        + "VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = conx.prepareStatement(req);
            ps.setInt(1, c.getProduits_id());
            ps.setDate(2, new java.sql.Date(c.getDate().getTime()));
            ps.setFloat(3, c.getTotale());
            ps.setString(4, c.getStatut());
            ps.executeUpdate();
            System.out.println("Commande Ajoutée !!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Commande c) {
        try {
            String req = "UPDATE commande SET produits_id=?, date=?, totale=?, statut=? WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(5, c.getId());
            pst.setInt(1, c.getProduits_id());
            pst.setDate(2, new java.sql.Date(c.getDate().getTime()));
            pst.setFloat(3, c.getTotale());
            pst.setString(4, c.getStatut());

            pst.executeUpdate();
            System.out.println("Commande Modifiée !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM commande WHERE id=?";
        try {
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Commande suprimée !");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Commande> Show() {
        List<Commande> list = new ArrayList<>();

        try {
            String req = "SELECT * from commande";
            Statement st = conx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Commande(rs.getInt("id"), rs.getInt("produits_id"),
                        rs.getDate("date"), rs.getFloat("totale"),
                        rs.getString("statut")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public Commande getById(int id) throws SQLException {
        Commande cmd = null;
        String sql = "SELECT * FROM commande WHERE id = ?";
        try {
            PreparedStatement pst = conx.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                cmd=new Commande(rs.getInt("id"), rs.getInt("produits_id"),
                        rs.getDate("date"), rs.getFloat("totale"),
                        rs.getString("statut"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cmd;
    }
}
