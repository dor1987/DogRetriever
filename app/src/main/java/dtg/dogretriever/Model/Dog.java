package dtg.dogretriever.Model;

public class Dog {
    private enum enumSize {TINY , SMALL, MEDIUM, LARGE};

    private int collarId;
    private String name;
    private String breed;
    private String color;
    private enumSize size;
    private String notes;


    public Dog(){}

    private Dog(DogBuilder dogBuilder){
        this.collarId = dogBuilder.collarId;

        setName(dogBuilder.name);
        setBreed(dogBuilder.breed);
        setColor(dogBuilder.color);
        setSize(dogBuilder.size);
        setNotes(dogBuilder.notes);

    }

    public Dog(int collarId, String name, String breed, String color, enumSize size, String notes) {
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

    public enumSize getSize() {
        return size;
    }

    public void setSize(enumSize size) {
        this.size = size;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public static class DogBuilder {
        //required
        private int collarId;


        //optional
        private String name;
        private String breed;
        private String color;
        private enumSize size;
        private String notes;


        public DogBuilder(int collarId) {
            this.collarId = collarId;
        }


        public DogBuilder setCollarId(int collarId) {
            this.collarId = collarId;
            return this;
        }

        public DogBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DogBuilder setBreed(String breed) {
            this.breed = breed;
            return this;
        }

        public DogBuilder setColor(String color) {
            this.color = color;
            return this;
        }

        public DogBuilder setSize(enumSize size) {
            this.size = size;
            return this;
        }

        public DogBuilder setNotes(String notes) {
            this.notes = notes;
            return this;
        }


        public Dog build() {
            return new Dog(this);
        }
    }
}