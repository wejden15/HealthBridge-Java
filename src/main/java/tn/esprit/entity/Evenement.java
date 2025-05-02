package tn.esprit.entity;

import java.time.LocalDate;

public class Evenement {
    private int id;
    private String nom;
    private String categorie;
    private LocalDate date;
    private String passCode;

    public Evenement() {}

    public Evenement(int id, String nom, String categorie, LocalDate date) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.date = date;
    }
    private String generatePassCode() {
        return "PASS-" + (int)(Math.random() * 10000);
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPassCode() {
        return passCode;
    }
    public boolean isExpired() {
        return date != null && date.isBefore(LocalDate.now());
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
