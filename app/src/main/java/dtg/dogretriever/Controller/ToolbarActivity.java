package dtg.dogretriever.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Controller.Fragments.AboutFragment;
import dtg.dogretriever.Controller.Fragments.NotificationFragment;
import dtg.dogretriever.Controller.Fragments.ProfileFragment;
import dtg.dogretriever.Controller.Fragments.SettingFragment;
import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements MyLocationService.LocationListener {

    //Fragments
    private AlgorithmFragment algorithmFragment;
    private SettingFragment settingFragment;
    private AboutFragment aboutFragment;
    private ProfileFragment profileFragment;
    private NotificationFragment notificationFragment;


    //Location Service
    boolean isBound = false;
    private MyLocationService.MylocalBinder mBinder;
    private Location userCurrentLocation;
    private MyLocationService myLocationService;
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    private View mFragmentContainerView;
    private View smallProgressBar;
    private View mProgressView;
    private FirebaseAdapter firebaseAdapter;
    final Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_toolbar);
        Bundle extras;
        userCurrentLocation = getIntent().getExtras().getParcelable("currentLocation");
        latitude = getIntent().getExtras().getDouble("latitude");
        longitude = getIntent().getExtras().getDouble("longitude");
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mProgressView = findViewById(R.id.loading_progress_toolbar);
        smallProgressBar = findViewById(R.id.tool_bar_activity_small_progres_bar);
        settingFragment = new SettingFragment();
        algorithmFragment = new AlgorithmFragment();
        aboutFragment = new AboutFragment();
        profileFragment = new ProfileFragment();
        notificationFragment = new NotificationFragment();
        mFragmentContainerView = findViewById(R.id.fragment_container);
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

            case 4:
                //open notification fragment
                startNotificationFragment(latitude,longitude);
        }

    }

    private void startNotificationFragment(Double latitude, Double longitude) {
        bundle.putDouble("lat",latitude);
        bundle.putDouble("lng",longitude);
        notificationFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, notificationFragment);
        fragmentTransaction.commit();
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



    public void startAlgoMapFragment(String dogId){
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


    public void OnClickHomeButton(View view) {
        showSmalProgressBar(false);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void OnClickAboutButton(View view) {
        showSmalProgressBar(false);
        startAboutFragment();
    }

    public void OnClickSettingsButton(View view) {
        showSmalProgressBar(false);
        startSettingFragment();
    }

    public void OnClickProfileButton(View view) {
        showSmalProgressBar(false);
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
            startActivity(i);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFragmentContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFragmentContainerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFragmentContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFragmentContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void locationChanged(Location location) {
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

            if(!myLocationService.isFirstTimeRuning()){
                userCurrentLocation = myLocationService.getUserCurrentLocation();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };


    public void showSmalProgressBar(final Boolean toShow){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            smallProgressBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
            smallProgressBar.animate().setDuration(shortAnimTime).alpha(
                    toShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    smallProgressBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            smallProgressBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
        }
    }
}
