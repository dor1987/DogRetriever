package dtg.dogretriever.Presenter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.FakeDataBaseGenerator;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.Presenter.Fragments.AboutFragment;
import dtg.dogretriever.Presenter.Fragments.ProfileFragment;
import dtg.dogretriever.Presenter.Fragments.SettingFragment;
import dtg.dogretriever.Presenter.LearningAlgoTemp.LearningAlgo;
import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener, MyLocationService.LocationListener {
    private static final String PREDICT_ALGO_KEY = "predictAlgo";
    private static final String LEARNING_ALGO_KEY = "learningAlgo";

    TextView profile_textview;
    FragmentManager fm;
    FrameLayout frameLayout;
    AlgorithmFragment algorithmFragment;
    SettingFragment settingFragment;
    AboutFragment aboutFragment;
    ProfileFragment profileFragment;

    FakeDataBaseGenerator fakeDataBaseGenerator = new FakeDataBaseGenerator(2); //dont forget to remove

    FirebaseAdapter firebaseAdapter;
    LearningAlgo learningAlgo;
    //private FusedLocationProviderClient fusedLocationClient;

    final Bundle bundle = new Bundle();
    //final Map<String,Scan> mapOfScans = new HashMap<>();

    private View mProgressView;
    private View mFragmentContinerView;

    //Location Service
    boolean isBound = false;
    private MyLocationService.MylocalBinder mBinder;
    private Location userCurrentLocation;
    private MyLocationService myLocationService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        Bundle extras = getIntent().getExtras();
       // userCurrentLocation = new Location("");
        userCurrentLocation = getIntent().getExtras().getParcelable("currentLocation");
        Log.d("DorCheck","Location At ToolBar OnCreate: "+ userCurrentLocation+"");

        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mProgressView = findViewById(R.id.loading_progress_toolbar);

        settingFragment = new SettingFragment();
        algorithmFragment = new AlgorithmFragment();
        aboutFragment = new AboutFragment();
        profileFragment = new ProfileFragment();

        mFragmentContinerView = findViewById(R.id.fragment_container);

        showProgress(true);

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        learningAlgo = new LearningAlgo();
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();



        int fragmentToOpen = getIntent().getIntExtra("fragmentToOpen",0);

        //which fragment to show depends on what is on the getExtra
        switch (fragmentToOpen){
            case 0: // Open algo fragment

                String dogId;
                if (savedInstanceState == null) {
                    extras = getIntent().getExtras();
                    if (extras == null) {
                        dogId = null;
                    } else {
                        dogId = extras.getString("DOG_ID");
                    }
                }

                else {
                    dogId = (String) savedInstanceState.getSerializable("DOG_ID");
                }

                startAlgoMapFragment(dogId);

                break;
            case 1:
                //open Setting fragment
                startSettingFragment();
                break;
            case 2:
                //open about fragment
                startAboutFragment();
                break;

            case 3:
                //open about fragment
                startProfileFragment();
                break;


        }




        /*
        String dogId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                dogId = null;
            } else {
                dogId = extras.getString("DOG_ID");
            }
        }
*/
        /*
        else {
            dogId = (String) savedInstanceState.getSerializable("DOG_ID");
        }

        startAlgoMapFragment(dogId);
        */
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            mBinder.DeleteLocationListener(ToolbarActivity.this);
            unbindService(mConnection);
            isBound = false;
        }
    }
    private void startProfileFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, profileFragment);
        fragmentTransaction.commit();
     }

    private void startAboutFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, aboutFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AlgorithmFragment.MY_CODE_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                algorithmFragment.updateMapUI();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_container:
                break;
            case R.id.home_container:
                break;
            case R.id.settings_container:
                break;
            case R.id.profile_container:
                break;

            default:
        }
    }
/*
    public void startAlgoFragmentDefault(String dogId){
        //init the algo fragment with a list of specif dogs scan
        ArrayList<Coordinate> temp = new ArrayList<>();
        mapOfScans.clear();
        mapOfScans.putAll(firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(dogId)));

        for (Scan scan : mapOfScans.values()) {
            temp.add(scan.getCoordinate());
        }

        bundle.putParcelableArrayList(LEARNING_ALGO_KEY,temp);
        algorithmFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, algorithmFragment);
        fragmentTransaction.commit();
    }
*/
    public void startAlgoMapFragment(String dogId){
        //Testing new way of starting the algo fragment
        //we pass dogId and the algo fragment get the data needed directly from Firebase

        bundle.putString("dogId",dogId);
        algorithmFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, algorithmFragment);
        fragmentTransaction.commit();
    }
    public void startSettingFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, settingFragment);
        fragmentTransaction.commit();
    }

/*
    public void startAlgoFragmentLearningAlgo(final float radius){
        //init the algo fragment with a list of specif dogs scan

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mapOfScans.clear();
                            mapOfScans.putAll(firebaseAdapter.getAllScanOfAllDogsInNamedRadius(location,radius));

                        }
                        bundle.putParcelableArrayList(LEARNING_ALGO_KEY,learningAlgo.learningAlgo(mapOfScans));
                        algorithmFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.fragment_container, algorithmFragment);
                        fragmentTransaction.commit();
                    }
                });
    }
*/
    private void checkForPermissions() {

    }

    public void OnClickHomeButton(View view) {
       // Toast.makeText(this, "clicked Home Button", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);

    }

    public void OnClickAboutButton(View view) {
       // Toast.makeText(this, "clicked About Button", Toast.LENGTH_SHORT).show();
        startAboutFragment();
    }

    public void OnClickSettingsButton(View view) {
        //Toast.makeText(this, "clicked Settings Button", Toast.LENGTH_SHORT).show();
        startSettingFragment();

    }

    public void OnClickProfileButton(View view) {
       // Toast.makeText(this, "clicked Profile Button", Toast.LENGTH_SHORT).show();
        if(firebaseAdapter.isUserConnected()){
            if(firebaseAdapter.isUserDataReadyNow()){
                startProfileFragment();
            }

            else {
                showProgress(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showProgress(false);


                        firebaseAdapter.removeProfileDataListener();
                        startProfileFragment();

                    }
                });
            }

        }
        else{
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(i);        }


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFragmentContinerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFragmentContinerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFragmentContinerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFragmentContinerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void locationChanged(Location location) {
       // Toast.makeText(this, "Location Updated At ToolBarActivity", Toast.LENGTH_SHORT).show();
        if(location!=null)
            if(location.getLongitude()!=0 && location.getLatitude()!=0)
                showProgress(false);

        userCurrentLocation = location;
        algorithmFragment.locationChanged(userCurrentLocation);

    }
    public Location getCurrentLocation(){
        //used for fragment to get inital value
        return userCurrentLocation;
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBinder = (MyLocationService.MylocalBinder) service;
            mBinder.registerLocationListener(ToolbarActivity.this);
            myLocationService = mBinder.getMyLocationService();
            isBound = true;

            if(myLocationService.isFirstTimeRuning()){
                showProgress(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

}
