package dtg.dogretriever.Presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;

import static dtg.dogretriever.Presenter.MyMessagingService.SHARED_PREFS;

//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MyLocationService.LocationListener {

    private PopupWindow popupWindow = null;
    private FirebaseAdapter firebaseAdapter;
    private View mProgressView,mSmallProgressBarView;
    private View mMainMenuFormView;
    private TextView userWelcomeTextView;
    private CircleImageView profilePicView;
    //debug
    private PopupWindow fakeScanPopUp = null;
    private PopupWindow notificationPopUp = null;
    private EditText dogIdFromFakeScanTextView;

    //use current location
   // private FusedLocationProviderClient fusedLocationClient;
   // private LocationManager locationManager;

    //Location Service
    boolean isBound = false;
    private MyLocationService.MylocalBinder mBinder;
    private Location userCurrentLocation;
    private MyLocationService myLocationService;

    //notification pop up
    Double latitude;
    Double longitude;
    boolean isNotifcationPopShowenBefore;
    //Intent extras;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        mProgressView = findViewById(R.id.loading_progress);
        mSmallProgressBarView = findViewById(R.id.main_menu_small_progres_bar);
        mMainMenuFormView = findViewById(R.id.mainMenuForm);
        userWelcomeTextView = findViewById(R.id.userWelcome);
        profilePicView = findViewById(R.id.profile_image);


        //permissionCheck();//If permission is ok it will start a get current location sequence


        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        initWelcomeTextview();
        setTokenListener();
       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userCurrentLocation = new Location("");


        //start the service
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        isNotifcationPopShowenBefore = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        //start the service
        showProgress(true);
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            mBinder.DeleteLocationListener(MainActivity.this);
            unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            mBinder.DeleteLocationListener(MainActivity.this);
            unbindService(mConnection);
            isBound = false;
        }
    }

    /*
    private void getUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            userCurrentLocation.setLatitude(location.getLatitude());
                            userCurrentLocation.setLongitude(location.getLongitude());
                        }
                        showProgress(false);
                    }
                });

    }
*/

/*
    private void permissionCheck() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }
*/
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        permissionCheck();
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50, this);




                } else {
                    // permission denied, boo!
                    //closing app
                    System.exit(0);

                }
                return;
            }
            // other 'case' lines to check for other

        }
    }
*/
    public void clickScanner(View view) {
        //temp implementation for debugging
        createPopUpFakeScan();

    }
    public void initWelcomeTextview(){
        if(firebaseAdapter.isUserConnected()) {

            if(firebaseAdapter.isUserDataReadyNow()){
                userWelcomeTextView.setText("Hello "+firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                initProfilePic();

            }
            else {

                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        firebaseAdapter.removeProfileDataListener();
                        userWelcomeTextView.setText("Hello "+firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                        initProfilePic();
                    }
                });
            }
        }
        else {

            userWelcomeTextView.setText("Hello Guest");
        }
    }
    public void clickFindMyDog(View view) {
        if(firebaseAdapter.isUserConnected()) {

            if(firebaseAdapter.isUserDataReadyNow()){
                createPopUpChooseDogName();
            }
            else {
                //showProgress(true);
                showSmalProgressBar(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        //showProgress(false);
                        showSmalProgressBar(false);
                        createPopUpChooseDogName();
                        firebaseAdapter.removeProfileDataListener();
                    }
                });
            }
        }
        else {

            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    public void clickSettings(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("fragmentToOpen", 1);
        startActivity(intent);
    }

    public void clickProfile(View view) {
        //check if user logged in
        //if user logged in send to profile
        //else send to login activity

        Toast.makeText(this, "Clicked Profile", Toast.LENGTH_SHORT).show();

        if(firebaseAdapter.isUserConnected()){
            if(firebaseAdapter.isUserDataReadyNow()){
                Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                intent.putExtra("fragmentToOpen", 3);
                startActivity(intent);
            }

            else {
                showProgress(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showProgress(false);
                        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                        intent.putExtra("fragmentToOpen", 3);
                        firebaseAdapter.removeProfileDataListener();
                        startActivity(intent);

                    }
                });
            }

        }
        else{
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(i);
        }

        /*
        if(firebaseAdapter.isUserConnected()){
            if(firebaseAdapter.isUserDataReadyNow()){
                Intent i = new Intent(getBaseContext(),ProfileActivity.class);
                startActivity(i);
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

        */
    }

    public void clickAbout(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("fragmentToOpen", 2);
        startActivity(intent);
    }


    private void createPopUpChooseDogName(){
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.choose_dog_popup, null);

        ListView listView = layout.findViewById(R.id.popup_dog_name_list_view);
        Button cancelBtn = layout.findViewById(R.id.popup_layout_cancel);
        TextView errorMessage = layout.findViewById(R.id.popup_layout_errorMessage);

            DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(), this);
            listView.setAdapter(dogNamesAdapter);
        Log.d("DorCheck","Location At MainMenu: "+ userCurrentLocation+"");

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                  //  showProgress(true);
                    showSmalProgressBar(true);
                    popupWindow.dismiss();



                new AsyncTask<Void,Void,Intent>(){

                    @Override
                    protected Intent doInBackground(Void... voids) {
                        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                        intent.putExtra("TAG", "AlgorithmFragment");
                        intent.putExtra("fragmentToOpen", "0");
                        intent.putExtra("DOG_ID", createDogsList().get(i).getCollarId());
                        intent.putExtra("currentLocation", userCurrentLocation);
                    return intent;
                    }

                    @Override
                    protected void onPostExecute(Intent intent) {
                        super.onPostExecute(intent);
                        startActivity(intent);
                    }
                }.execute();
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

    private void startNotificationPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.notification_pop_up, null);
        notificationPopUp = new PopupWindow(this);
        notificationPopUp.setContentView(layout);
        notificationPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setFocusable(true);
        notificationPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);

    }
    public void openMapWithNotificationCords(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("TAG", "notificatonFragment");
        intent.putExtra("fragmentToOpen", 4);
        intent.putExtra("latitude",latitude );
        intent.putExtra("longitude", longitude);
        intent.putExtra("currentLocation", userCurrentLocation);
        startActivity(intent);

    }

    public void closeNotificationPopUp(View view) {
        notificationPopUp.dismiss();
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
        Dog tempDog = null;
        String collarId = dogIdFromFakeScanTextView.getText().toString();

        if(!collarId.equals(""))
            tempDog  = firebaseAdapter.getDogByCollarIdFromFireBase(collarId);

        if(tempDog!= null) {
                //LatLng locationToReturn = getRandomLocation((new LatLng(32.30613403, 35.00500989)), 2000);
            final Dog finalTempDog = tempDog;

            myLocationService.addScanToDataBase(finalTempDog);

            fakeScanPopUp.dismiss();

        }

        else{
            dogIdFromFakeScanTextView.setText("Id not found in Database");
        }

    }

    public void cancelScanPopUp(View view) {
        //for debug
        fakeScanPopUp.dismiss();
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
/*
    public void getPlaceType(double latitide, double longitude){
        //Getting Lat Long and get type list from google api
        Object transferData[] = new Object[1];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        String url = getUrl(latitide, longitude,"30");
        transferData[0] = url;
        getNearbyPlaces.execute(transferData);
    }

    private String getUrl(double latitide, double longitude, String ProximityRadius)
    {
        //Assist function for "getPlaceType", building Url to fit the Search
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        //googleURL.append("&type=" + nearbyPlace); not a specific type
        googleURL.append("&sensor=true"); //not a must
        googleURL.append("&key=" + "AIzaSyDTDmMNFTekqcFEWeK9yAJYzxdaM-IU8Wk");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }
*/
    @Override
    public void locationChanged(Location location) {
        //Toast.makeText(this, "Location Updated At MainActivity", Toast.LENGTH_SHORT).show();

        showProgress(false);

        userCurrentLocation = location;
        Log.d("DorCheck","Location At MainActivity: userCurrentLocation: "+ userCurrentLocation+" location from function "+location );

        if(mProgressView.getVisibility()!=View.VISIBLE) {
            if (!isNotifcationPopShowenBefore)
                showNotificationPop();
        }
    }

    public void showNotificationPop(){
        isNotifcationPopShowenBefore = true;
        //Intent extras = getIntent();
        if(bundle!=null){
           for(String key : bundle.keySet()){
               if(key.equals("lat"))
                   latitude = Double.parseDouble(bundle.getString(key));

               else if (key.equals("long"))
                   longitude = Double.parseDouble(bundle.getString(key));
           }
           if(latitude!=null && latitude!=0.0
                   && longitude != null && longitude != 0.0)
                startNotificationPopUp();

            /*
        if(extras.getStringExtra("latitude") != null) {

            latitude = Double.valueOf(extras.getStringExtra("latitude"));
            longitude = Double.valueOf(extras.getStringExtra("longitude"));

            startNotificationPopUp();
        }
        */
        }
/*
        else if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                if(bundle.getString("latitude") != null) {
                    latitude = Double.valueOf(bundle.getString("latitude"));
                    longitude = Double.valueOf(bundle.getString("longitude"));
                    startNotificationPopUp();
                }
            }
        }
  */
        else{
            Toast.makeText(this, "Got Nothing", Toast.LENGTH_SHORT).show();
        }
    }



    /*
        @Override
        public void onLocationChanged(Location location) {
            //getUserCurrentLocation();
            userCurrentLocation.setLatitude(location.getLatitude());
            userCurrentLocation.setLongitude(location.getLongitude());
            showProgress(false);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
        */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBinder = (MyLocationService.MylocalBinder) service;
            mBinder.registerLocationListener(MainActivity.this);
            myLocationService = mBinder.getMyLocationService();
            isBound = true;
/*
            if(myLocationService.isFirstTimeRuning()){
                showProgress(true);
            }
*/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public void setTokenListener() {
        if (firebaseAdapter.isUserConnected()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", instanceIdResult.getToken() + "");
                    editor.commit();
                    firebaseAdapter.writeNewTokenToFireBase(instanceIdResult.getToken() + "");
                }
            });
        }
    }

    public void initProfilePic(){
        if(firebaseAdapter.getCurrentUserProfileFromFireBase().getmImageUrl()!=null &&
                !firebaseAdapter.getCurrentUserProfileFromFireBase().getmImageUrl().trim().isEmpty())
        Picasso.get()
                .load(firebaseAdapter.getCurrentUserProfileFromFireBase()
                .getmImageUrl())
                .placeholder(R.drawable.asset6h)
                .error(R.drawable.asset6h)
                .into(profilePicView);

    }

    public void showSmalProgressBar(final Boolean toShow){

        if(toShow){ //disable/enable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
            mSmallProgressBarView.animate().setDuration(shortAnimTime).alpha(
                    toShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
            //mMainMenuFormView.setVisibility(toShow ? View.GONE : View.VISIBLE);
        }
    }

}