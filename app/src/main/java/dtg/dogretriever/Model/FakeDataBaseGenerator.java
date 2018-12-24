package dtg.dogretriever.Model;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FakeDataBaseGenerator {
    //Assist functions to create random database

    private ArrayList<Dog> fakeDogsDataBase;
    private ArrayList<Profile> fakeProfilesDataBase;
    private int profileIdCounter = 0;
    private int dogIdCounter = 0;


    public FakeDataBaseGenerator(int amountOfProfiles) {
        this.fakeDogsDataBase = new ArrayList<>();
        this.fakeProfilesDataBase = new ArrayList<>();
        this.profileIdCounter = 0;
        this.dogIdCounter = 0;


        //Generate the database
        for(int i =0 ; i<amountOfProfiles;i++){
            fakeProfilesDataBase.add(generateRandomProfile(profileIdCounter));
            profileIdCounter+=1;
        }

    }


    public ArrayList<Profile> getFakeProfilesDataBase() {
        //return the full data bases (profile and dogs)
        return fakeProfilesDataBase;
    }

    public ArrayList<Dog> getFakeDogsDataBase(){
        //return only dog database
        return fakeDogsDataBase;
    }

    //Assist functions to create random profile
    public Profile generateRandomProfile(int id){
        String fullName= getRandomName();
        String userName = fullName.replaceAll("\\s+","");
        String password = getRandomNumber()+"";
        String eMail = userName+"@gmail.com";
        String phoneNumber = getRandomNumber()+"";
        String address ="Random Address";
        ArrayList dogIdArrayList = new ArrayList();
        Bitmap randomIamge =null;

        for(int i = 0; (id%2)+1>i;i++){
            Dog tempDog = GenerateRandomDog(dogIdCounter,id);
            dogIdArrayList.add(tempDog.getCollarId());
            dogIdCounter+=1;
        }

        Profile profile = new Profile();
        profile.setId(id+"");
        profile.setUserName(userName);
        profile.setFullName(fullName);
        profile.setPassword(password);
        profile.seteMail(eMail);
        profile.setPhoneNumber(phoneNumber);
        profile.setAddress(address);
        profile.setDogsIDArrayList(dogIdArrayList);
        profile.setProfileImage(randomIamge);

        return profile;
    }
    public String getRandomName() {
        //Generate random name for Database testing

        ArrayList<String> namesList = new ArrayList();
        namesList.addAll(Arrays.asList("Donovan Eber",
                "Dana Penny",
                "Carie Saar",
                "Lynn Philpot",
                "Ignacio Hennig",
                "Marci Rachal",
                "Rosamaria Gallow",
                "delina Turcios",
                "Susanna Riding",
                "Jerlene Henkle",
                "Dorla Hydrick",
                "Edyth Funnell",
                "Fredia Wissing",
                "Margret Harley",
                "Augustina Lomax",
                "Sylvie Roof",
                "Edison Whobrey",
                "Marlene Burgamy",
                "Gerri Fiscus",
                "Kathlene Weatherhead",
                "Wendy Tedrow",
                "Monte Mcgeehan",
                "Maura Woodson",
                "Ellena Embry",
                "Migdalia Dupuis",
                "Stephen Prager",
                "Caprice Mitchener",
                "Orval Lauber",
                "Alesia Wilbanks",
                "Kacie Reep",
                "Brianne Cushman",
                "Lela Mchaney",
                "Tangela Stumbo",
                "Melany Romano",
                "Adah Stellhorn",
                "Meda Shin",
                "Janett Kempton",
                "Arianne Pinkley",
                "Jackelyn Ellman",
                "Beckie Stotz",
                "Kemberly Alligood",
                "Brittaney Saleem",
                "Anton Rolfes",
                "Marth Bluhm",
                "Lorina Crapo",
                "Casimira Sowers",
                "Marine Bramblett",
                "Burl Martinelli",
                "Johanna Fullbright",
                "Avril Pusey"));

        Random rand = new Random();

        int n = rand.nextInt(namesList.size());

        return namesList.get(n);
    }
    public int getRandomNumber(){
        //an asisst function to create database
        Random rand = new Random();
        return ( 100000000 + rand.nextInt(900000000));
    }

    //Assist functions to create random Dog
    public Dog GenerateRandomDog(int dogId,int ownerId){
        String collarId = dogId+"";
        String name = getOneWordName();
        String breed = getRandomBreed();
        String color = getRandomColor();
        Dog.enumSize size = getRandomSize();
        String notes = "Random Note";
        ArrayList<Coordinate> scannedCoords = getRandomScanCoordsArray();

        Dog dog = new Dog();
        dog.setCollarId(collarId);
        dog.setName(name);
        dog.setBreed(breed);
        dog.setColor(color);
        dog.setSize(size);
        dog.setNotes(notes);
        dog.setScannedCoords(scannedCoords);
        dog.setOwnerId(ownerId+"");
        //addDogToDataBase(dog);
        fakeDogsDataBase.add(dog);

        return dog;
    }
    public String getOneWordName(){
        String input = getRandomName();
        int i = input.indexOf(' ');
        String name = input.substring(0, i);

        return name;
    }
    public String getRandomBreed() {
        //Generate random breed for Database testing

        ArrayList<String> breedList = new ArrayList();
        breedList.addAll(Arrays.asList("Affenpinscher","Afghan Hound","Akita","Bulldog",
                "Australian Shepherd","Basenji","Border Terrier","Canaan Dog",
                "Dalmatian","English Setter","French Bulldog","Golden Retriever",
                "Great Dane","Labrador Retriever","Maltese"));


        Random rand = new Random();

        int n = rand.nextInt( breedList.size());

        return breedList.get(n);
    }
    public String getRandomColor() {
        //Generate random breed for Database testing

        ArrayList<String> colorList = new ArrayList();
        colorList.addAll(Arrays.asList("White","Black","Brown","Grey","Yellow"));


        Random rand = new Random();

        int n = rand.nextInt(colorList.size());

        return colorList.get(n);
    }
    public Dog.enumSize getRandomSize() {
        //Generate random size for Database testing
        Random rand = new Random();
        int n = rand.nextInt(4);

        switch (n){

            case 0:
                return Dog.enumSize.TINY;

            case 1:
                return Dog.enumSize.SMALL;

            case 2:
                return Dog.enumSize.MEDIUM;

            default:
                return Dog.enumSize.LARGE;
        }

    }
    public ArrayList<Coordinate> getRandomScanCoordsArray(){
        //create an array of random locations
        Random rand = new Random();
        int numOfScans = rand.nextInt(3);
        int scanArea = rand.nextInt(3);
        LatLng locationToReturn;
        ArrayList<Coordinate> scannedCoords = new ArrayList<Coordinate>();

        for(int i = 0; i<numOfScans;i++){
            switch (scanArea){
                case 0: //Dor's house
                    locationToReturn = getRandomLocation((new LatLng(32.30613403,35.00500989)),2000);
                    break;

                case 1: //Afeka
                    locationToReturn = getRandomLocation((new LatLng(32.1224552,34.8068643)),2000);
                    break;

                default: //Tal's House
                    locationToReturn = getRandomLocation((new LatLng(32.0820469,34.811154)),2000);
                    break;
            }


            scannedCoords.add(new Coordinate(locationToReturn.latitude,locationToReturn.longitude));

        }
        return scannedCoords;
    }
    public LatLng getRandomLocation(LatLng point, int radius) {
        //get random location in a predefined radius
        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }
    public Date getRandomTimeStamp(){
        //get Random time stamps
        Date currentTime = Calendar.getInstance().getTime();
        Random rand = new Random();
        int timeShift = rand.nextInt(8);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, timeShift);

        currentTime = calendar.getTime();

        return currentTime;
    }

}
