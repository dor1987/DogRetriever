package dtg.dogretriever.Presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow = null;
    private FirebaseAdapter firebaseAdapter;
    private View mProgressView;
    private View mMainMenuFormView;

    //debug
    private PopupWindow fakeScanPopUp = null;
    private EditText dogIdFromFakeScanTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        mProgressView = findViewById(R.id.loading_progress);
        mMainMenuFormView = findViewById(R.id.mainMenuForm);



    }



    public void clickScanner(View view) {
        //temp implementation for debugging
        createPopUpFakeScan();

    }

    public void clickFindMyDog(View view) {
        if(firebaseAdapter.isUserConnected()) {

            if(firebaseAdapter.isUserDataReadyNow()){
                createPopUpChooseDogName();
            }
            else {
                showProgress(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showProgress(false);
                        createPopUpChooseDogName();
                        firebaseAdapter.removeProfileDataListener();
                    }
                });
            }
        }
        else {
            Intent i = new Intent(getBaseContext(), SigninActivity.class);
            startActivity(i);
        }
    }

    public void clickSettings(View view) {

    }

    public void clickProfile(View view) {
        //check if user logged in
        //if user logged in send to profile
        //else send to login activity


        if(firebaseAdapter.isUserConnected()){
            if(firebaseAdapter.isUserDataReadyNow()){
                Intent i = new Intent(getBaseContext(),ProfileActivity.class);
            }

            else {
                showProgress(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showProgress(false);
                        Intent i = new Intent(getBaseContext(),ProfileActivity.class);
                        firebaseAdapter.removeProfileDataListener();
                        startActivity(i);

                    }
                });
            }

        }
        else{
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(i);
        }
    }

    public void clickAbout(View view) {

    }


    private void createPopUpChooseDogName(){

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.choose_dog_popup, null);

        ListView listView = layout.findViewById(R.id.popup_dog_name_list_view);
        Button cancelBtn = layout.findViewById(R.id.popup_layout_cancel);
        TextView errorMessage = layout.findViewById(R.id.popup_layout_errorMessage);

            DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(), this);
            listView.setAdapter(dogNamesAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                    intent.putExtra("TAG", "AlgorithmFragment");
                    intent.putExtra("DOG_ID", createDogsList().get(i).getCollarId());
                    startActivity(intent);

                }
            });

            if(dogNamesAdapter.getCount()==0){
                errorMessage.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(layout);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);



    }


    private ArrayList<Dog> createDogsList(){
        firebaseAdapter.getCurrentUserProfileFromFireBase();
        return firebaseAdapter.getListOfDogsOwnedByCurrentUser();
    }


    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainMenuFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void createPopUpFakeScan(){
        //for debug

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.fake_scan_debug, null);

        //Button cancelBtn = layout.findViewById(R.id.fake_scan__popup_layout_cancel);
        //Button scanBtn = layout.findViewById(R.id.fake_scan__popup_layout_add_dog_button);



        fakeScanPopUp = new PopupWindow(this);
        fakeScanPopUp.setContentView(layout);
        fakeScanPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        fakeScanPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        fakeScanPopUp.setFocusable(true);
        fakeScanPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
        dogIdFromFakeScanTextView = layout.findViewById(R.id.fake_scan__popup_layout_dog_id);

    }

    public void scanDog(View view) {
        //for debug
        String collarId = dogIdFromFakeScanTextView.getText().toString();

        Dog tempDog = firebaseAdapter.getDogByCollarIdFromFireBase(collarId);
        if(tempDog!= null) {
            LatLng locationToReturn = getRandomLocation((new LatLng(32.30613403, 35.00500989)), 2000);

            try {
                Scan tempScan = new Scan(new Coordinate(locationToReturn.latitude, locationToReturn.longitude));

                firebaseAdapter.addScanToDog(tempDog, tempScan);
            } catch (IOException e) {
                e.printStackTrace();
            }

            fakeScanPopUp.dismiss();

        }

        else{
            dogIdFromFakeScanTextView.setText("Id not found in Database");
        }

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
}