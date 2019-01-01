package dtg.dogretriever.Model;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseAdapter {
    //singleton
    private static FirebaseAdapter instance;

    //Firebase refrences
    FirebaseDatabase dataBase;
    DatabaseReference dogTableRef;
    DatabaseReference usersTableRef;
    FirebaseAuth mAuth;


    ArrayList<Dog> DogsFromDataBaseList;
    ArrayList<Profile> ProfilesFromDataBaseList;
    Profile profile;

    private FirebaseAdapter() {
        //Init FireBase
        dataBase = FirebaseDatabase.getInstance();
        //Referenecs for both tables
        dogTableRef = dataBase.getReference().child("Dogs");
        usersTableRef = dataBase.getReference().child("Users");

        DogsFromDataBaseList = new ArrayList<Dog>(); //This is where the list of dog will be after it get it from firebase
        ProfilesFromDataBaseList = new ArrayList<Profile>(); //This is where the list of profiles will be after it get it from firebase
        profile = new Profile();
        mAuth = FirebaseAuth.getInstance();

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

        if(isUserConnected()) {
            usersTableRef.orderByKey().equalTo(mAuth.getUid()).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   // ProfilesFromDataBaseList.clear();
                    //profile = dataSnapshot.getValue(Profile.class);

                    for (DataSnapshot temp: dataSnapshot.getChildren()){
                        if(temp.hasChild("fullName"))
                            profile.setFullName((String)temp.child("fullName").getValue());
                        if(temp.hasChild("phoneNumber"))
                            profile.setPhoneNumber((String)temp.child("phoneNumber").getValue());
                        if(temp.hasChild("address"))
                            profile.setAddress((String)temp.child("address").getValue());
                        if(temp.hasChild("eMail"))
                            profile.seteMail((String)temp.child("eMail").getValue());
                        if(temp.hasChild("dogsIDArrayList"))
                            profile.setDogsIDMap((Map<String,String>) temp.child("dogsIDArrayList").getValue());
                    }

                    /*
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ProfilesFromDataBaseList.add(profile);
                    }
  */
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static FirebaseAdapter getInstanceOfFireBaseAdapter(){
        if(instance == null){
            instance = new FirebaseAdapter();
        }
        return instance;
    }

    public void addUserToDataBase(Profile user){
        //Gets user Profile and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
        usersTableRef.child(user.getId()).setValue(user);
    }

    public void addDogToDataBase(Dog dog){
        //Gets dog and save it to FireBase by ID
        //This method would overwrite database child if the user have the same id
        //This methood will add the dogId to the current user profile

        DatabaseReference currentUserRef = usersTableRef.child(mAuth.getUid()).child("dogsIDArrayList");
        String hashCode = currentUserRef.push().getKey();

        currentUserRef.child(hashCode).setValue(dog.getCollarId());

        dog.setHashCode(hashCode);

        dogTableRef.child(dog.getCollarId()+"").setValue(dog);

    }

    public void removeDogFromDataBase(Dog dog){
        //Delete the dogs and it's scans from database
        dogTableRef.child(dog.getCollarId()).removeValue();

        //Delete the dog Id from the user profile
        DatabaseReference currentUserRef = usersTableRef.child(mAuth.getUid()).child("dogsIDArrayList").child(dog.getHashCode());
        currentUserRef.removeValue();
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

    public void addCoordToDog(Dog dog,Coordinate coordinate){
        //Get dog and coordinate and add the coordinate to the dog coordinate list at firebase
        DatabaseReference currentDogRef = dogTableRef.child(dog.getCollarId()).child("scannedCoords");
        currentDogRef.push();
    }

    public boolean isUserConnected(){
        //check if user is connected right now to firebase
        if(mAuth.getCurrentUser()!=null){
            return true;
        }
        else{
            return false;
        }
    }

    public Profile getCurrentUserProfileFromFireBase(){
        return profile;
    }

    public ArrayList<Dog> getListOfDogsOwnedByCurrentUser(){
        //give an Arraylist of dogs owned by the current user
        //if no dogs found will return null

        ArrayList<Dog> dogArrayList = new ArrayList<>();

        try {
            for (String dogId : profile.getDogIDAsArrayList()) {
                dogArrayList.add(getDogByCollarIdFromFireBase(dogId));
            }
        }catch (NullPointerException e){
            return null;
        }
        return  dogArrayList;
    }
}