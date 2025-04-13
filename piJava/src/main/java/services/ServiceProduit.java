package services;

import entities.CrudProduit;
import entities.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.MyDB;

public class ServiceProduit implements CrudProduit<Produit> {

    public Connection conx;
    public Statement stm;


    public ServiceProduit() {
        conx = MyDB.getInstance().getConx();
    }

    @Override
    public void ajouter(Produit p) {
        String req =
                "INSERT INTO produit"
                        + "(nom,description,type,date,prix,image)"
                        + "VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conx.prepareStatement(req);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getType());
            ps.setDate(4, new java.sql.Date(p.getDate().getTime()));
            ps.setFloat(5, p.getPrix());
            ps.setString(6, p.getImage());
            ps.executeUpdate();
            System.out.println("Produit Ajoutée !!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Produit p) {
        try {
            String req = "UPDATE produit SET nom=?, description=?, type=?, date=?, prix=?, image=? WHERE id=?";
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(7, p.getId());
            pst.setString(1, p.getNom());
            pst.setString(2, p.getDescription());
            pst.setString(3, p.getType());
            pst.setDate(4, new java.sql.Date(p.getDate().getTime()));
            pst.setFloat(5, p.getPrix());
            pst.setString(6, p.getImage());

            pst.executeUpdate();
            System.out.println("Produit Modifiée !");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id=?";
        try {
            PreparedStatement pst = conx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Produit suprimée !");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Produit> Show() {
        List<Produit> list = new ArrayList<>();

        try {
            String req = "SELECT * from produit";
            Statement st = conx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("description"), rs.getString("type"),
                        rs.getDate("date"), rs.getFloat("prix"),
                        rs.getString("image")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public Produit getById(int id) throws SQLException {
        Produit prod = null;
        String sql = "SELECT * FROM produit WHERE id = ?";
        try {
            PreparedStatement pst = conx.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                prod=new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("description"), rs.getString("type"),
                        rs.getDate("date"), rs.getFloat("prix"),
                        rs.getString("image"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prod;
    }
}
