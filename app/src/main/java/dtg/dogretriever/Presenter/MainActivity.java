package dtg.dogretriever.Presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow = null;
    private int popupWidth ;
    private int popupHeight;

    //for database testing
    public int profileIdCounter;
    public int dogdCounter;

    //Firebase
    FirebaseDatabase dataBase;
    DatabaseReference dogTableRef;
    DatabaseReference usersTableRef;

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
        /*
        dogs.add(new Dog("Luka"));
        dogs.add(new Dog("Nala"));
*/
        return dogs;
    }


    public void addUserToDataBase(Profile user){
        //Gets user Profile and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
      //  usersTableRef.child(user.getId()).setValue(user);
    }


    public void addDogToDataBase(Dog dog){
        //Gets dog and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id

        //ToDo need to define getters at the dog class
         dogTableRef.child(dog.getCollarId()+"").setValue(dog);
    }

    //Assist functions to create random profile

    public Profile GenerateRandomProfile(int id){
         String fullName= getRandomName();
         String userName = fullName.replaceAll("\\s+","");
         String password = getRandomNumber()+"";
         String eMail = userName+"@gmail.com";
         String phoneNumber = getRandomNumber()+"";
         String address ="Random Address";
         ArrayList dogIdArrayList;
         Bitmap randomIamge =null;



        Profile profile = new Profile(id,userName,fullName,password,eMail,phoneNumber,address,dogIdArrayList,randomIamge);
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

        int n = rand.nextInt(50) + 1;

        return namesList.get(n);
    }

    public int getRandomNumber(){
        //an asisst function to create database
        Random rand = new Random();
       return ( 100000000 + rand.nextInt(900000000));
    }



    //Assist functions to create random Dog

}