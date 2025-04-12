package entities;

import java.util.Date;

public class Commande {
    private int id;
    private int produits_id;
    private Date date;
    private float totale;
    private String statut;

    public Commande(int id, int produits_id, Date date, float totale, String statut) {
        this.id = id;
        this.produits_id = produits_id;
        this.date = date;
        this.totale = totale;
        this.statut = statut;
    }

    public Commande(int produits_id, Date date, float totale, String statut) {
        this.produits_id = produits_id;
        this.date = date;
        this.totale = totale;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduits_id() {
        return produits_id;
    }

    public void setProduits_id(int produits_id) {
        this.produits_id = produits_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTotale() {
        return totale;
    }

    public void setTotale(float totale) {
        this.totale = totale;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
