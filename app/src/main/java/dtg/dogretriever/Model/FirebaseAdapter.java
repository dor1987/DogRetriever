package dtg.dogretriever.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;


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
    FirebaseAuth.AuthStateListener mAuthListener;
    String userID;
    final DataSnapshot[] mainDataSnapshot= new DataSnapshot[1];


    //Listeners
    private ProfileDataListener profileDataListener;
    private ImageUploadListener imageUploadListener;

    //For Type histogram
    Map<String,Integer> histogramOfPlacesMap;
    boolean isHistogramReady;

    //Firebase StorageRefs
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private StorageReference oldImageUrl;

    private FirebaseAdapter() {
        //listeners
        this.profileDataListener = null;
        this.imageUploadListener = null;


        //Init FireBase
        dataBase = FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Referenecs for both tables
        dogTableRef = dataBase.getReference().child("Dogs");
        usersTableRef = dataBase.getReference().child("Users");

        DogsFromDataBaseList = new ArrayList<Dog>(); //This is where the list of dog will be after it get it from firebase
        ProfilesFromDataBaseList = new ArrayList<Profile>(); //This is where the list of profiles will be after it get it from firebase
        profile = new Profile();
        mAuth = FirebaseAuth.getInstance();

        //init histogram array
        histogramOfPlacesMap = new HashMap<String,Integer>();
        isHistogramReady = false;

        //Firebase Listeners

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() != null){
                    userID = mAuth.getCurrentUser().getUid();
                }
            }
        };

        dogTableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This event happens when app start and when something change on the dog table
                DogsFromDataBaseList.clear();
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Dog dog = postSnapshot.getValue(Dog.class);
                    Log.d("onChildAdded", "dog:" + dog.getName());

                    if (postSnapshot.hasChild("image"))
                        dog.setmImageUrl((String) postSnapshot.child("image").child("mImageUrl").getValue());

                    DogsFromDataBaseList.add(dog);
                }
                generatePlacesHistogram();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






            usersTableRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mainDataSnapshot[0] = dataSnapshot;

                    if(profileDataListener != null){ //let main menu know that the profile data is ready
                        profileDataListener.onDataReady();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


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

    public void addScanToDog(Dog dog,Scan scan){
        //Get dog and scan and add the scan to the dog scan map at firebase
        DatabaseReference currentDogRef = dogTableRef.child(dog.getCollarId()).child("scans");
        String hashCode = currentDogRef.push().getKey();

        currentDogRef.child(hashCode).setValue(scan);
    }


    public Map<String,Scan> getAllScanOfSpecificDog(Dog dog){
        //Get dog and give back all his scans as map
        return getDogByCollarIdFromFireBase(dog.getCollarId()).getScans();
    }

    public Map<String,Scan> getAllScanOfAllDogs(){
        //give back Map of all the scans of all the dogs
        Map<String,Scan> tempMapOfScans = new HashMap<String,Scan>();

        for (Dog dog : DogsFromDataBaseList){
            if(dog.getScans()!=null) {
                tempMapOfScans.putAll(dog.getScans());
            }
        }
        return tempMapOfScans;
    }

    public ArrayList<Dog> getAllDogs(){
        //just for debug
        ArrayList<Dog> arrayOfDogs = new ArrayList<>();

        for(Dog dog : DogsFromDataBaseList){
            if(dog !=null){
                arrayOfDogs.add(dog);
            }
        }
        return arrayOfDogs;
    }

    public Profile getUserById(String userId){
        Profile owner = new Profile();
        try {
            if (mainDataSnapshot[0].child(userId).hasChild("fullName"))
                owner.setFullName((String) mainDataSnapshot[0].child(userId).child("fullName").getValue());
            if (mainDataSnapshot[0].child(userId).hasChild("phoneNumber"))
                owner.setPhoneNumber((String) mainDataSnapshot[0].child(userId).child("phoneNumber").getValue());
            if (mainDataSnapshot[0].child(userId).hasChild("address"))
                owner.setAddress((String) mainDataSnapshot[0].child(userId).child("address").getValue());
            if (mainDataSnapshot[0].child(userId).hasChild("eMail"))
                owner.seteMail((String) mainDataSnapshot[0].child(userId).child("eMail").getValue());
            if (mainDataSnapshot[0].child(userId).hasChild("image"))
                owner.setmImageUrl((String) mainDataSnapshot[0].child(userId).child("image").child("mImageUrl").getValue());

        }

        catch (NullPointerException e){
            return null;
        }

        return owner;
    }

    public Map<String,Scan> getAllScanOfAllDogsInNamedRadius(Location currentLocation,float radius){
        //give back Map of all the scans of all the dogs within given location and radius
        Map<String,Scan> tempMapOfScans = new HashMap<String,Scan>();

        for (Dog dog : DogsFromDataBaseList){
            if(dog.getScans()!=null) {
                for (Map.Entry<String, Scan> pair : dog.getScans().entrySet()){
                    Location tempLocation = new Location("");
                    tempLocation.setLatitude(pair.getValue().getCoordinate().getLatitude());
                    tempLocation.setLongitude(pair.getValue().getCoordinate().getLongitude());

                    if(currentLocation.distanceTo(tempLocation)< radius){
                        tempMapOfScans.put(pair.getKey(),pair.getValue());
                    }
                }
            }
        }
        return tempMapOfScans;
    }

    public void generatePlacesHistogram(){
        final Map<String,Scan> tempMapOfScans = getAllScanOfAllDogs();

        new Thread(new Runnable() {
            @Override
            public void run() {

                for(Scan scan : tempMapOfScans.values()){
                    if(scan.getPlaces()!=null) {
                        for (String place : scan.getPlaces()) {
                            if (histogramOfPlacesMap.get(place) != null) {
                                histogramOfPlacesMap.put(place, histogramOfPlacesMap.get(place) + 1);
                            } else {
                                histogramOfPlacesMap.put(place, 1);
                            }
                        }
                    }
                }
                isHistogramReady = true;
            }
        })
.start();


    }

    public boolean isHistogramReady(){
        return isHistogramReady;
    }

    public Map<String,Integer> getPlacesHistogram(){
        return histogramOfPlacesMap;
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

        userID = mAuth.getCurrentUser().getUid();//maybe redundant, need 2 check if already init in constructor
    try {
        if (mainDataSnapshot[0].child(userID).hasChild("id")) ;
        profile.setId((String) mainDataSnapshot[0].child(userID).child("id").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("fullName"))
            profile.setFullName((String) mainDataSnapshot[0].child(userID).child("fullName").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("phoneNumber"))
            profile.setPhoneNumber((String) mainDataSnapshot[0].child(userID).child("phoneNumber").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("address"))
            profile.setAddress((String) mainDataSnapshot[0].child(userID).child("address").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("eMail"))
            profile.seteMail((String) mainDataSnapshot[0].child(userID).child("eMail").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("dogsIDArrayList"))
            profile.setDogsIDMap((Map<String, String>) mainDataSnapshot[0].child(userID).child("dogsIDArrayList").getValue());
        if (mainDataSnapshot[0].child(userID).hasChild("image"))
            profile.setmImageUrl((String) mainDataSnapshot[0].child(userID).child("image").child("mImageUrl").getValue());

    }

    catch (NullPointerException e){
        return null;
    }


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

    public void writeNewTokenToFireBase(String token) {
            DatabaseReference currentUserRef = usersTableRef.child(mAuth.getUid()).child("token");
            currentUserRef.setValue(token);
    }


    public void registerProfileDataListener(ProfileDataListener profileDataListener) {
        this.profileDataListener = profileDataListener;
    }

    public void removeProfileDataListener(){
        this.profileDataListener = null;
    }

    public void registerImageUploadListener(ImageUploadListener imageUploadListener){
        this.imageUploadListener =  imageUploadListener;
    }

    public void removeImageUploadListener(){
        this.imageUploadListener = null;
    }

    public Boolean isUserDataReadyNow(){
        if(mainDataSnapshot[0]!=null){
            return true;
        }
        else
            return false;
    }

    public Boolean isDogsDataReadyNow(){
        if(DogsFromDataBaseList!=null && DogsFromDataBaseList.isEmpty()){
            return true;
        }
        else return false;
    }
    public interface ImageUploadListener{
        public void onUploadFinish(String url);
        public void onUploadProgress(double progress);
        public void onDogImageUploadFinish(String url);
        public void onDogImageUploadProgress(double progress);
    }

    public interface ProfileDataListener {
        public void onDataReady();
    }

    //Image upload download Functions

    public String getFileExtension(Context context, Uri uri){
        //Used to get the file extension (jpg, bitmap, etc..)

        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public boolean isThereAnOnGoingImageUploadTask(){
        if(mUploadTask !=null && mUploadTask.isInProgress()){
            return true;
        }
        return false;
    }

    public void uploadFile(Context context, Uri mImageUri, final String imageName){
        if(mImageUri != null){
            oldImageUrl = getOldImageUrl();

          final StorageReference fileRefrence = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(context,mImageUri));

            try {//Compress Image before upload
                Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(),mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
                byte[] data = baos.toByteArray();




            mUploadTask = fileRefrence.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Upload upload = new Upload(imageName.trim(),uri.toString());
                                userID = mAuth.getCurrentUser().getUid();

                                DatabaseReference currentUserRef = usersTableRef.child(userID).child("image");
                                currentUserRef.setValue(upload);
                                Log.e("UPLOADSUCCESS"," UPLOADSUCCESS");
                                imageUploadListener.onUploadProgress(0.0);
                                imageUploadListener.onUploadFinish(uri.toString());
                                profile.setmImageUrl(uri.toString());
                                deleteOldPicture();
                            }
                        });

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UPLOADFAIL"," UPLOADFAIL");

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        imageUploadListener.onUploadProgress(progress);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private StorageReference getOldImageUrl() {
        if(profile.getmImageUrl()!=null && !profile.getmImageUrl().trim().isEmpty()) {
            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(profile.getmImageUrl());
        return oldImageRef;
        }
        return null;
    }

    public void cancelAnUpload(){
        if(mUploadTask!=null) {
            if (!mUploadTask.isComplete()) {
                //Upload is not complete yet, let's cancel
                mUploadTask.cancel();
            }
        }
    }

    public void deleteOldPicture(){
       if(oldImageUrl!=null && !oldImageUrl.toString().trim().isEmpty()) {
           oldImageUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   oldImageUrl = null;
               }
           });
       }
    }

    public void uploadDogFile(Context context, Uri mImageUri,final String imageName,final String myDogId){
        if(mImageUri != null){
            oldImageUrl = getOldDogImageUrl(myDogId);

            final StorageReference fileRefrence = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(context,mImageUri));

            try {//Compress Image before upload
                Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(),mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG,25,baos);
                byte[] data = baos.toByteArray();

                mUploadTask = fileRefrence.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Upload upload = new Upload(imageName.trim(),uri.toString());

                                        DatabaseReference currentDogRef = dogTableRef.child(myDogId).child("image");
                                        currentDogRef.setValue(upload);
                                        Log.e("UPLOADSUCCESS"," UPLOADSUCCESS");
                                        imageUploadListener.onDogImageUploadProgress(0.0);
                                        imageUploadListener.onDogImageUploadFinish(uri.toString());
                                        deleteOldDogPicture();
                                    }
                                });

                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("UPLOADFAIL"," UPLOADFAIL");

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                imageUploadListener.onDogImageUploadProgress(progress);
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private StorageReference getOldDogImageUrl(String myDogId) {
        StorageReference oldImageRef = null;
        try {
            oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getDogByCollarIdFromFireBase(myDogId).getmImageUrl());
        }
        catch (IllegalArgumentException e){
            return null;
        }
       return oldImageRef;
    }

    public void cancelAnUploadDogImage(){
        if(mUploadTask!=null) {
            if (!mUploadTask.isComplete()) {
                //Upload is not complete yet, let's cancel
                mUploadTask.cancel();
            }
        }
    }

    public void deleteOldDogPicture(){
        if(oldImageUrl!=null && !oldImageUrl.toString().trim().isEmpty()) {
            oldImageUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    oldImageUrl = null;
                }
            });
        }
    }

    public void logOut(){
        writeNewTokenToFireBase("");
        mAuth.signOut();
    }
}
