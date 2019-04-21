package dtg.dogretriever.Presenter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
/*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
*/
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.Model.Weather;
import dtg.dogretriever.Presenter.LearningAlgoTemp.Cluster;
import dtg.dogretriever.Presenter.LearningAlgoTemp.LearningAlgo;
import dtg.dogretriever.Presenter.LearningAlgoTemp.Point;
import dtg.dogretriever.R;
import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.google.android.material.tabs.TabLayout;

//import static android.support.v4.content.ContextCompat.getSystemService;


public class AlgorithmFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "AlgorithmFragment";
    public static final int MY_CODE_REQUEST = 123;



    private enum algoType {DEFUALT, PREDICTION, LEARNING}

    private final double minRadius = 1000.0;
    private final double maxRadius = 5000.0;
    private ArrayList<Color> colors;
    private algoType currentAlgoShown = algoType.DEFUALT;
    private Button predictAlgoBtn;
    private Button learningAlgoBtn;
    private GoogleMap mMap;
    private SupportMapFragment smFragment;
    private FragmentTabHost mTabHost;
    private ArrayList<Coordinate> coordinatesToShow;
    private Location currentLocation;
    private ArrayList<Coordinate> hotZonesAlgoResult;
    private ArrayList<Cluster> hotZonesAlgoResultAsCluster;

    private OnFragmentInteractionListener mListener;
    final Map<String, Scan> mapOfScans = new HashMap<>();
    FirebaseAdapter firebaseAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    LocationManager mLocationManager;
    LearningAlgo learningAlgo;
    TabLayout tabLayout;
    boolean isMapReady;
    boolean isFirstTimeLocationSet;

    //weather
    private Weather.weather currentWeather;
    private Weather weather;

    SharedPreferences sharedPreferences;

    private int currentTab;

    public AlgorithmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkForPermissions();
        displayLocationSettingsRequest(getContext());
        mMap.setOnMarkerClickListener(this);

        //currentLocation = getLastKnownLocation();
        //currentLocation = ((ToolbarActivity) getActivity()).getUserCurrentLocation();
        currentLocation = ((ToolbarActivity)getActivity()).getCurrentLocation();
        Log.d("DorCheck","Location At AlgoFragment onMapReady: "+ currentLocation+"");

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),15));
        tabLayout.getTabAt(0).select();
        isMapReady= true;
    }

    private Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet));

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), MY_CODE_REQUEST);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    private void checkForPermissions() {
        if (mMap == null) {
            Log.d(TAG, "map in null");
            return;
        }

        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            this.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_CODE_REQUEST);


        } else {
            updateMapUI();
            Log.d(TAG, "Location permission granted from manifest");
        }

    }

    @SuppressLint("MissingPermission")
    public void updateMapUI() {
        mMap.clear();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

//        ArrayList<Coordinate> coordinates = getLearningAlgoCoordsList();

        if(currentTab==0) {
            if (!coordinatesToShow.isEmpty()) {
                for (Coordinate coordinate : coordinatesToShow) {
                    createMarker(coordinate.getLatitude(), coordinate.getLongitude(), "bla bla", "bla bla");
                }
            }
        }
        else if (currentTab==1){
                showRadiusArea(hotZonesAlgoResultAsCluster);
        }
        // showRadiusArea(getCoordinatesToShow(), getRandomRadius(getCoordinatesToShow().size()));
     /*
        for (Scan scan : mapOfScans.values()) {
            createMarker(scan.getCoordinate().getLatitude(), scan.getCoordinate().getLongitude(), "Time", scan.getTimeStamp() + "");
        }
    */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_CODE_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateMapUI();
                    Log.d(TAG, "Location permission granted");
                } else {
                    Log.d(TAG, "Location permission denied");
                }


        }


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    // TODO: Rename and change types and number of parameters
    public static AlgorithmFragment newInstance(String param1, String param2) {
        AlgorithmFragment fragment = new AlgorithmFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isMapReady = false;
        isFirstTimeLocationSet = true;
        currentLocation = ((ToolbarActivity)getActivity()).getCurrentLocation();
        weather = new Weather(new Coordinate(currentLocation.getLatitude(), currentLocation.getLongitude()).toString());
        currentWeather = weather.getCurrentWeather();

        Log.d("DorCheck","Location At AlgoFragment OnCreateView: "+ currentLocation+"");

     /*
        currentLocation = new Location("");
        currentLocation.setLongitude(0);
        currentLocation.setLatitude(0);
       */
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        View view = inflater.inflate(R.layout.fragment_algorithm, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tablayout);
        coordinatesToShow = new ArrayList<>();
        hotZonesAlgoResult = new ArrayList<>();
        hotZonesAlgoResultAsCluster = new ArrayList<>();
        //learningAlgo = new LearningAlgo();
        hotZonesAlgo();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FragmentManager fm = getActivity().getSupportFragmentManager();
        smFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
        if (smFragment == null) {
            smFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapView, smFragment).commit();
        }



        smFragment.getMapAsync(this);


    }

    @Override
    public void onStart() {
        super.onStart();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                String dogId = getArguments().getString("dogId");
                switch (tab.getPosition()) {
                    case 0:
                        //Show all scan of selected dog
                        ((ToolbarActivity)getActivity()).showSmalProgressBar(false);
                        mapOfScans.clear();
                        mapOfScans.putAll(firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(dogId)));

                        coordinatesToShow.clear();

                        for (Scan scan : mapOfScans.values())
                            coordinatesToShow.add(scan.getCoordinate());


                        updateMapUI();
                        break;

                    case 1:
                        //show algo1 result for selected dog
                        showHotZonesAlgoMarkersOnMap();

                        /*
                        coordinatesToShow.clear();
                       // coordinatesToShow.addAll(learningAlgo.learningAlgo(firebaseAdapter.getAllScanOfAllDogsInNamedRadius(currentLocation, 2000)));
                        coordinatesToShow.addAll(hotZonesAlgoResult);
                        updateMapUI();
                        if(coordinatesToShow.size() == 0){
                            Toast.makeText(getContext(), "Not enough information", Toast.LENGTH_SHORT).show();
                        }
                        */
                        break;

                    case 2:
                        //show algo2 result for selected dog
                        //Right now will show ll scans of all dogs
                        ((ToolbarActivity)getActivity()).showSmalProgressBar(false);

                        mapOfScans.clear();
                        mapOfScans.putAll(firebaseAdapter.getAllScanOfAllDogs());
                        coordinatesToShow.clear();

                        for (Scan scan : mapOfScans.values())
                            coordinatesToShow.add(scan.getCoordinate());

                        updateMapUI();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

    }

    private void setPredictAlgoON() {
        predictAlgoBtn.setBackgroundResource(R.color.colorPrimaryDark);
        learningAlgoBtn.setBackgroundResource(R.color.colorPrimary);
        currentAlgoShown = algoType.PREDICTION;
    }

    private void setLearningAlgoON() {
        predictAlgoBtn.setBackgroundResource(R.color.colorPrimary);
        learningAlgoBtn.setBackgroundResource(R.color.colorPrimaryDark);
        currentAlgoShown = algoType.LEARNING;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void showRadiusArea(ArrayList<Coordinate> centerList, ArrayList<Double> radiusList) {

        for (int i = 0; i < centerList.size(); i++) {

                  //mMap.addCircle(new CircleOptions().center(centerList.get(i).get)
                    //      .radius(radiusList.get(i)).fillColor(getRandomColor()));
        }
    }

    private void showRadiusArea(ArrayList<Cluster> arrayOfClusters){
        int totalAmountOfPoints = 0;

        for(Cluster cluster : arrayOfClusters){
            totalAmountOfPoints+= cluster.getNumOfPoints();
        }
        for(Cluster cluster : arrayOfClusters){
            mMap.addCircle(new CircleOptions().center(new LatLng(cluster.getCenterLat(),cluster.getCenterLong())).radius(cluster.getDiameter()/6).fillColor(getColorOfCircle(totalAmountOfPoints,cluster.getNumOfPoints())));
        }

    }
    private int getColorOfCircle(int totalAmountOfPoints,int clusterAmountOfPoints){
    double ratio = (double)clusterAmountOfPoints/(double)totalAmountOfPoints;

        if(ratio<=0.1){
            return 0x22ff0000;
        }
        else if(ratio<=0.6){
            return 0x22FFFF00;
        }
        else{
        return 0x2200FF00;
        }

    }
    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


    private ArrayList getRandomRadius(int size) {
        Random rnd = new Random();
        ArrayList radiusList = new ArrayList<>();

        for (int i = 0; i < size; i++)
            radiusList.add(minRadius + (maxRadius - minRadius) * rnd.nextDouble());

        return radiusList;
    }

    public void locationChanged(Location location) {
        //activates from toolbar activity
        if(location!=null)
            currentLocation = location;
        //currentLocation.setLatitude(location.getLatitude());
        //currentLocation.setLongitude(location.getLongitude());

        Log.i(TAG, "Current location: Lat - " + currentLocation.getLatitude() + "Long - " + currentLocation.getLongitude());
        if(isMapReady) {

            if (isFirstTimeLocationSet) {


                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));

                isFirstTimeLocationSet = false;

        }
          /*
                else
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        */
        }
    }
    /*
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Log.i(TAG, "Current location: Lat - " + location.getLatitude() + "Long - " + location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getAltitude())));


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getAltitude())));

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {


    }
    */
/*
    private ArrayList<Coordinate> getLearningAlgoCoordsList() {
        return getArguments().getParcelableArrayList("learningAlgo");
    }
*/
/*
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    */


    @SuppressLint("StaticFieldLeak")
    public void hotZonesAlgo(){
        //testing Lambda

        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                this.getActivity().getApplicationContext(), "us-east-1:0843e7a0-4eaa-4d94-8450-29790fb2faf0", Regions.US_EAST_1);
// Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.

        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getActivity().getApplicationContext(),
                Regions.US_EAST_1, cognitoProvider);

// Create the Lambda proxy object with a default Json data binder.
// You can provide your own data binder by implementing
// LambdaDataBinder.
        final MyInterface myInterface = factory.build(MyInterface.class);
        int radius = Integer.parseInt(sharedPreferences.getString("hot_zones_algo_radius","4000"));
        RequestClass request = new RequestClass(convretMapOfScansToPoint(firebaseAdapter.getAllScanOfAllDogsInNamedRadius(currentLocation, radius)),currentWeather.name(),firebaseAdapter.getPlacesHistogram());
// The Lambda function invocation results in a network call.
// Make sure it is not called from the main thread.
try {


        new AsyncTask<RequestClass, Void, ResponseClass>() {
            @Override
            protected ResponseClass doInBackground(RequestClass... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return myInterface.AndroidBackendLambdaFunction(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e("Tag", "Failed to invoke echo", lfe);
                    return null;
                }
                catch (AmazonServiceException e){
                    Log.e("Tag", "request time out",e);
                    return null;
                }
                catch (AmazonClientException e){
                    Log.e("Tag","request time out");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseClass result) {
                if (result == null) {
                    return;
                }

                // Do a toast
                //Toast.makeText(MainActivity.this, result.getGreetings(), Toast.LENGTH_LONG).show();
               // hotZonesAlgoResult.addAll(convertClusterArrayListToCoordiante(result.getClustersList()));
               hotZonesAlgoResultAsCluster.addAll(result.getClustersList());

                //Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                if(currentTab==1) {
                   tabLayout.selectTab(tabLayout.getTabAt(1));
                }
            }
        }.execute(request);

        //End testing lambda
}
catch (Exception e){
    e.printStackTrace();
}
    }
    public ArrayList<Point> convretMapOfScansToPoint(Map<String,Scan> scansMap){
        ArrayList<Point> pointsArrayList = new ArrayList<>();

        for(Scan scan : scansMap.values()){
            pointsArrayList.add(new Point(-1,scan.getCoordinate().getLatitude(),
                    scan.getCoordinate().getLongitude(),
                    scan.getCurrentWeather() == null ? Weather.weather.UNKNOWN.name() : scan.getCurrentWeather().name(),
                    scan.getTimeStamp().getTime(),
                    scan.getPlaces()));
        }

        return pointsArrayList;
    }

    public ArrayList<Coordinate> convertClusterArrayListToCoordiante(ArrayList<Cluster> cluserArrayList){
        ArrayList<Coordinate> coordinatesArrayList = new ArrayList<>();

        for (Cluster cluser : cluserArrayList){
            coordinatesArrayList.add(new Coordinate(cluser.getCenterLat(),cluser.getCenterLong()));
        }
        return coordinatesArrayList;
    }

    public void showHotZonesAlgoMarkersOnMap(){
        coordinatesToShow.clear();
        // coordinatesToShow.addAll(learningAlgo.learningAlgo(firebaseAdapter.getAllScanOfAllDogsInNamedRadius(currentLocation, 2000)));

        //coordinatesToShow.addAll(hotZonesAlgoResult);
        updateMapUI();
        if(hotZonesAlgoResultAsCluster.size() == 0){
            ((ToolbarActivity)getActivity()).showSmalProgressBar(true);
            Toast.makeText(getContext(), "Not enough information", Toast.LENGTH_SHORT).show();
        }
        else{
            ((ToolbarActivity)getActivity()).showSmalProgressBar(false);

        }
    }



}
