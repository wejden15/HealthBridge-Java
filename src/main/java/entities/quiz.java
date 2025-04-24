package entities;

import java.sql.Date;

public class quiz {


    private int id;
    private String name;
    private String type;
    private Date date;

    // Default constructor
    public quiz() {
    }

    // Constructor with all fields
    public quiz(int id, String name, String type, Date date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
    }

    // Constructor without id (for new quizzes)
    public quiz(String name, String type, Date date) {
        this.name = name;
        this.type = type;
        this.date = date;
    }

    // Constructor with just name and type (will set current date automatically)
    public quiz(String name, String type) {
        this.name = name;
        this.type = type;
        this.date = new Date(System.currentTimeMillis());
    }

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

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }
}
