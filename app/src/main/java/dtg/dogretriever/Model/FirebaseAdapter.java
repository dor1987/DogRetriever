package dtg.dogretriever.Model;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseAdapter {


    //Firebase refrences
    FirebaseDatabase dataBase;
    DatabaseReference dogTableRef;
    DatabaseReference usersTableRef;


    ArrayList<Dog> DogsFromDataBaseList;
    ArrayList<Profile> ProfilesFromDataBaseList;

    public FirebaseAdapter() {
        //Init FireBase

        dataBase = FirebaseDatabase.getInstance();
        //Referenecs for both tables
        dogTableRef = dataBase.getReference().child("Dogs");
        usersTableRef = dataBase.getReference().child("Users");

        DogsFromDataBaseList = new ArrayList<Dog>(); //This is where the list of dog will be after it get it from firebase
        ProfilesFromDataBaseList = new ArrayList<Profile>(); //This is where the list of profiles will be after it get it from firebase


        //Firebase Listeners
        dogTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This event happens when app start and when something change on the dog table
                DogsFromDataBaseList.clear();
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Dog dog = postSnapshot.getValue(Dog.class);
                    Log.d("onChildAdded", "dog:" + dog.getName());
                    DogsFromDataBaseList.add(dog);
                }
                ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
                for (int i = 0; i < DogsFromDataBaseList.size(); i++) {
                    if (DogsFromDataBaseList.get(i).getScannedCoords() != null) {
                        coordinates.addAll(DogsFromDataBaseList.get(i).getScannedCoords());
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        usersTableRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfilesFromDataBaseList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Profile profile = postSnapshot.getValue(Profile.class);
                    ProfilesFromDataBaseList.add(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }


    public void addUserToDataBase(Profile user){
        //Gets user Profile and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
        usersTableRef.child(user.getId()).setValue(user);
    }


    public void addDogToDataBase(Dog dog){
        //Gets dog and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
        dogTableRef.child(dog.getCollarId()+"").setValue(dog);
    }

    public Dog getDogByCollarIdFromFireBase(String collarId){
     //return dog from database by id if dog not found return null

        for (Dog dog : DogsFromDataBaseList){
            if(dog.getCollarId().equals(collarId)){
                return dog;
            }
        }
        return null;
    }
/*
    public Profile getProfileByIdFromFireBase(String Id){
        //return profile from database by id if profile not found return null
        for (Profile profile : ProfilesFromDataBaseList){
            if(profile.getId().equals(Id)){
                return profile;
            }
        }
        return null;
    }
    */
}