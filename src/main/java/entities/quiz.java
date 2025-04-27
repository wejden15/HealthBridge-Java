package entities;

public class quiz {


    private int id;
    private String name;
    private String type;

    // No-args constructor
    public quiz() {
    }

    public quiz(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public quiz(String name, String type) {
        this.name = name;
        this.type = type;
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

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
