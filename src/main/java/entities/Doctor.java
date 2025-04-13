package entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Doctor {
    private int id;
    private String name;
    private String specialty;
    private String picture; // URL as varchar
    
    // JavaFX Properties
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty specialtyProperty = new SimpleStringProperty();

    public Doctor() {
        // Initialize properties
        nameProperty.set("");
        specialtyProperty.set("");
    }

    public Doctor(int id, String name, String specialty, String picture) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.picture = picture;
        
        // Initialize properties
        nameProperty.set(name);
        specialtyProperty.set(specialty);
    }

    public Doctor(String name, String specialty, String picture) {
        this.name = name;
        this.specialty = specialty;
        this.picture = picture;
        
        // Initialize properties
        nameProperty.set(name);
        specialtyProperty.set(specialty);
    }

    public Doctor(int id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        
        // Initialize properties
        nameProperty.set(name);
        specialtyProperty.set(specialty);
    }

    public Doctor(String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
        
        // Initialize properties
        nameProperty.set(name);
        specialtyProperty.set(specialty);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameProperty.set(name);
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
        this.specialtyProperty.set(specialty);
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    // Property getters
    public StringProperty nameProperty() {
        return nameProperty;
    }

    public StringProperty specialtyProperty() {
        return specialtyProperty;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialty='" + specialty + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
} 