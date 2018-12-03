package dtg.dogretriever.Model;

public class Dog {
    private int collarId;
    private String name;
    private String breed;
    private String color;
    private String size;
    private String notes;


    public Dog(int collarId, String name, String breed, String color, String size, String notes) {
        this.collarId = collarId;
        this.name = name;
        this.breed = breed;
        this.color = color;
        this.size = size;
        this.notes = notes;
    }

    public int getCollarId() {
        return collarId;
    }

    public void setCollarId(int collarId) {
        this.collarId = collarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
