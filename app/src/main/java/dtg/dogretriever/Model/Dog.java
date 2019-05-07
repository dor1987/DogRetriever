package dtg.dogretriever.Model;

import java.util.ArrayList;
import java.util.Map;

public class Dog {
    public enum enumSize {TINY , SMALL, MEDIUM, LARGE};

    private String collarId;
    private String name;
    private String breed;
    private String color;
    private enumSize size;
    private String notes;
    private String ownerId;
    //private ArrayList<Coordinate> scannedCoords;
    private String hashCode;  //Because the dog id is save at profile inside hashmap , i saved the hash related with this dog to be able to identify it later
    private Map<String,Scan> scans;
    private String mImageUrl;

    public Dog(){}


    private Dog(DogBuilder dogBuilder){
        setCollarId(dogBuilder.collarId);
        setOwnerId(dogBuilder.ownerId);
        setName(dogBuilder.name);
        setBreed(dogBuilder.breed);
        setColor(dogBuilder.color);
        setSize(dogBuilder.size);
        setNotes(dogBuilder.notes);
        setScans(scans);
        setmImageUrl(dogBuilder.imageUrl);

    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getCollarId() {
        return collarId;
    }

    public void setCollarId(String collarId) {
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
/*
    public ArrayList<Coordinate> getScannedCoords() {
        return scannedCoords;
    }
*/
/*
    public void setScannedCoords(ArrayList<Coordinate> scannedCoords) {
        this.scannedCoords = scannedCoords;
    }
*/
/*
    public Map<String, Coordinate> getScannedCoords() {
        return scannedCoords;
    }

    public void setScannedCoords(Map<String, Coordinate> scannedCoords) {
        this.scannedCoords = scannedCoords;
    }
*/

    public Map<String, Scan> getScans() {
        return scans;
    }

    public void setScans(Map<String, Scan> scans) {
        this.scans = scans;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public static class DogBuilder {
        //required
        private String collarId;
        private String ownerId;


        //optional
        private String name;
        private String breed;
        private String color;
        private enumSize size;
        private String notes;
        //private ArrayList<Coordinate> scannedCoords;
        //private Map<String,Coordinate> scannedCoords;
        private Map<String,Scan> scans;
        private String imageUrl;

        public DogBuilder(String collarId, String ownerId) {
            this.collarId = collarId;
            this.ownerId = ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public DogBuilder setCollarId(String collarId) {
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

        public DogBuilder setImageUrl(String mImageUrl) {
            this.imageUrl = mImageUrl;
            return this;
        }

        /*
        public void setScannedCoords(ArrayList<Coordinate> scannedCoords) {
            this.scannedCoords = scannedCoords;
        }
        */
/*
        public void setScannedCoords(Map<String,Coordinate> scannedCoords){
            this.scannedCoords = scannedCoords;
        }
  */

        public void setScans(Map<String, Scan> scans) {
            this.scans = scans;
        }

        public Dog build() {
            return new Dog(this);
        }
    }
}