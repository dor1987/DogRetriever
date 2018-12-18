package dtg.dogretriever.Presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow = null;
    private int popupWidth ;
    private int popupHeight;

    //for database testing
    public int profileIdCounter = 0;
    public int dogIdCounter = 0;

    //Firebase
    FirebaseDatabase dataBase;
    DatabaseReference dogTableRef;
    DatabaseReference oneDogRef;
    DatabaseReference usersTableRef;
    ArrayList<Dog> DogsFromDataBaseList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        popupWidth = displayMetrics.widthPixels ;
        popupHeight = displayMetrics.heightPixels ;

        //Init FireBase
        dataBase = FirebaseDatabase.getInstance();
        //Referenecs for both tables
        dogTableRef = dataBase.getReference().child("Dogs");
        usersTableRef = dataBase.getReference("Users");

        //InitDataBase(); //Uncomment only if database need to be rebuilt


        DogsFromDataBaseList = new ArrayList<Dog>(); //This is where the list of dog will be after it get it from firebase

        //Firebase Listeners
        dogTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This event happens when app start and when somthing change on the dog table
                DogsFromDataBaseList.clear();
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Dog dog = postSnapshot.getValue(Dog.class);
                    Log.d("onChildAdded", "dog:" + dog.getName());
                    DogsFromDataBaseList.add(dog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    public void clickScanner(View view) {

    }

    public void clickFindMyDog(View view) {
       createPopUpChooseDogName();

    }

    public void clickSettings(View view) {

    }

    public void clickProfile(View view) {
        Intent i = new Intent(getBaseContext(),ProfileActivity.class);
        startActivity(i);
    }

    public void clickAbout(View view) {

    }


    private void createPopUpChooseDogName(){

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.choose_dog_popup, null);

        ListView listView = layout.findViewById(R.id.popup_dog_name_list_view);
        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(),this);
        listView.setAdapter(dogNamesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(),ToolbarActivity.class);
                intent.putExtra("DOG_ID",createDogsList().get(i).getCollarId());
                startActivity(intent);

            }
        });

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(layout);
        popupWindow.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(1);
        popupWindow.setWidth(1);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);





    }


    private ArrayList<Dog> createDogsList(){

        ArrayList<Dog> dogs = new ArrayList<>();

        dogs.add(new Dog("Luka"));
        dogs.add(new Dog("Nala"));

        return dogs;
    }


    public void addUserToDataBase(Profile user){
        //Gets user Profile and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
        usersTableRef.child(user.getId()).setValue(user);
    }


    public void addDogToDataBase(Dog dog){
        //Gets dog and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id

        //ToDo need to define getters at the dog class
         dogTableRef.child(dog.getCollarId()+"").setValue(dog);
    }

    //Assist functions to create random database
    public void InitDataBase(){
        for(int i =0 ; i<50;i++){
            addUserToDataBase(generateRandomProfile(profileIdCounter));
            profileIdCounter+=1;

        }
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
        addDogToDataBase(dog);

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

            scannedCoords.add(new Coordinate(locationToReturn.latitude,locationToReturn.longitude,getRandomTimeStamp()));

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