package entities;

import java.util.Date;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private String type;
    private Date date;
    private float prix;
    private String image;

    public Produit(int id, String nom, String description, String type, Date date, float prix, String image) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.type = type;
        this.date = date;
        this.prix = prix;
        this.image = image;
    }

    public Produit(String nom, String description, String type, Date date, float prix, String image) {
        this.nom = nom;
        this.description = description;
        this.type = type;
        this.date = date;
        this.prix = prix;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
