package dtg.dogretriever.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.PredictionRequestClass;
import dtg.dogretriever.Model.PredictionResponseClass;
import dtg.dogretriever.Model.RequestClass;
import dtg.dogretriever.Model.ResponseClass;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.Model.Weather;
import dtg.dogretriever.Model.Cluster;
import dtg.dogretriever.Model.Point;
import dtg.dogretriever.R;
import com.google.android.material.tabs.TabLayout;


public class AlgorithmFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {
    private static final String TAG = "AlgorithmFragment";
    public static final int MY_CODE_REQUEST = 123;
   // private enum algoType {DEFUALT, PREDICTION, LEARNING}
    private final int ALL_MY_DOG_SCANS = 0;
    private final int HOT_ZONE_ALGO = 1;
    private final int PREDICTION_ALGO = 2;

    private final int ALL_MY_DOG_SCANS_ZOOM = 8;
    private final int HOT_ZONE_ALGO_ZOOM = 13;
    private final int PREDICTION_ALGO_ZOOM = 17;

    private final int RADIUS_OFFSET = 2;

    //private ArrayList<Color> colors;
    //private algoType currentAlgoShown = algoType.DEFUALT;
    //private Button predictAlgoBtn;
    //private Button learningAlgoBtn;
    private GoogleMap mMap;
    private SupportMapFragment smFragment;
    private FragmentTabHost mTabHost;
    private Location currentLocation;
    private ArrayList<Coordinate> hotZonesAlgoResult;
    private ArrayList<Cluster> hotZonesAlgoResultAsCluster;
    private Coordinate predictionAlgoResult;
    private OnFragmentInteractionListener mListener;
    private final Map<String, Scan> mapOfScans = new HashMap<>();
    private FirebaseAdapter firebaseAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    LocationManager mLocationManager;
    TabLayout tabLayout;
    private boolean isMapReady;
    private boolean isFirstTimeLocationSet;
    private PopupWindow notEnoughScansPopupWindow;
    //weather
    private Weather.weather currentWeather;
    private Weather weather;

    SharedPreferences sharedPreferences;

    private int currentTab;
    private SimpleDateFormat sdf = new SimpleDateFormat("'Date:' dd.MM.yy ' Time:'HH:mm:ss");
    private LinearLayout colorExplainLayout;
    private boolean gotHotZoneAnswer;
    private boolean gotPredictAlgoAnswer;

    public AlgorithmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkForPermissions();
        displayLocationSettingsRequest(getContext());
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setPadding(0,0,0,150);
        currentLocation = ((ToolbarActivity)getActivity()).getCurrentLocation();
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
        if(currentTab==ALL_MY_DOG_SCANS) {
            if(mapOfScans!= null &&!mapOfScans.isEmpty()){
                for(Scan scan : mapOfScans.values()){
                    createMarker(scan.getCoordinate().getLatitude(),scan.getCoordinate().getLongitude(),sdf.format(scan.getTimeStamp()),scan.getCoordinate().getLatitude()+","+scan.getCoordinate().getLongitude());
                }
            }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.686378,35.017207),ALL_MY_DOG_SCANS_ZOOM));
        }
        else if (currentTab==HOT_ZONE_ALGO){
                showRadiusArea(hotZonesAlgoResultAsCluster);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),HOT_ZONE_ALGO_ZOOM));
        }
        else if(currentTab ==PREDICTION_ALGO) {
            if(predictionAlgoResult!=null) {
                showRadiusArea(predictionAlgoResult);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(predictionAlgoResult.getLatitude(), predictionAlgoResult.getLongitude()), PREDICTION_ALGO_ZOOM));
            }
        }
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

/*
    public static AlgorithmFragment newInstance(String param1, String param2) {
        AlgorithmFragment fragment = new AlgorithmFragment();

        return fragment;
    }
*/

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

        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        View view = inflater.inflate(R.layout.fragment_algorithm, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tablayout);
        colorExplainLayout = view.findViewById(R.id.algo_fragment_color_explain);
        //coordinatesToShow = new ArrayList<>();
        hotZonesAlgoResult = new ArrayList<>();
        hotZonesAlgoResultAsCluster = new ArrayList<>();
        //learningAlgo = new LearningAlgo();
        gotHotZoneAnswer = false;
        gotPredictAlgoAnswer = false;

        hotZonesAlgo();
        predicationAlgo();
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
                        //if dog got no scans than get empty map else use map from firebase
                        mapOfScans.putAll(firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(dogId))==null ?
                                new HashMap<String,Scan>() :
                        firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(dogId)) );
                        colorExplainLayout.setVisibility(View.INVISIBLE);
                        updateMapUI();
                        break;

                    case 1:
                        //show algo1 result for selected dog
                        showHotZonesAlgoMarkersOnMap();
                        colorExplainLayout.setVisibility(View.VISIBLE);

                        break;

                    case 2:
                        //show algo2 result for selected dog
                        //Right now will show ll scans of all dogs
                        ((ToolbarActivity)getActivity()).showSmalProgressBar(false);
                        colorExplainLayout.setVisibility(View.INVISIBLE);
                        showPredictAlgoMarkersOnMap();
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
/*
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
*/

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
        void onFragmentInteraction(Uri uri);
    }

    private void showRadiusArea(Coordinate coord) {
        double radius = 50;
        mMap.addCircle(new CircleOptions().center(new LatLng(coord.getLatitude(),coord.getLongitude())).radius(radius).fillColor(0x22249ACF));
    }

    private void showRadiusArea(ArrayList<Cluster> arrayOfClusters){
        int totalAmountOfPoints = 0;

        for(Cluster cluster : arrayOfClusters){
            totalAmountOfPoints+= cluster.getNumOfPoints();
        }
        for(Cluster cluster : arrayOfClusters){
            mMap.addCircle(new CircleOptions().center(new LatLng(cluster.getCenterLat(),cluster.getCenterLong())).radius(cluster.getDiameter()/RADIUS_OFFSET).fillColor(getColorOfCircle(totalAmountOfPoints,cluster.getNumOfPoints())));
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



    public void locationChanged(Location location) {
        //activates from toolbar activity
        if(location!=null)
            currentLocation = location;
        Log.i(TAG, "Current location: Lat - " + currentLocation.getLatitude() + "Long - " + currentLocation.getLongitude());
        if(isMapReady) {
            if (isFirstTimeLocationSet) {
                isFirstTimeLocationSet = false;
            }
        }
    }

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
                gotHotZoneAnswer = true;
                if (result == null) {
                    return;
                }
               hotZonesAlgoResultAsCluster.addAll(result.getClustersList());

                if(currentTab==HOT_ZONE_ALGO) {
                   tabLayout.selectTab(tabLayout.getTabAt(1));
                }
            }
        }.execute(request);

        //End lambda
}
catch (Exception e){
    e.printStackTrace();
}
    }

    @SuppressLint("StaticFieldLeak")
    public void predicationAlgo(){

        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                this.getActivity().getApplicationContext(), "us-east-1:f03c6ed8-afad-4497-9a9b-a4024e891a21", Regions.US_EAST_1);
// Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.

        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getActivity().getApplicationContext(),
                Regions.US_EAST_1, cognitoProvider);

// Create the Lambda proxy object with a default Json data binder.
// You can provide your own data binder by implementing
// LambdaDataBinder.
        final PredicationAlgoInterface predicationAlgoInterface = factory.build(PredicationAlgoInterface.class);
       // RequestClass request = new RequestClass(convretMapOfScansToPoint(firebaseAdapter.getAllScanOfAllDogsInNamedRadius(currentLocation, radius)),currentWeather.name(),firebaseAdapter.getPlacesHistogram());
        String dogId = getArguments().getString("dogId");
        ArrayList<Point> points = convretMapOfScansToPoint(firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(dogId)));

        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point point1, Point point2) {
                return point1.getTimeStamp() > point2.getTimeStamp() ? 1 :point1.getTimeStamp() < point2.getTimeStamp()? -1:0;
            }
        });

        final PredictionRequestClass request = new PredictionRequestClass(points,currentWeather.name());


// The Lambda function invocation results in a network call.
// Make sure it is not called from the main thread.
        try {


            new AsyncTask<PredictionRequestClass, Void, PredictionResponseClass>() {
                @Override
                protected PredictionResponseClass doInBackground(PredictionRequestClass... params) {
                    // invoke "echo" method. In case it fails, it will throw a
                    // LambdaFunctionException.
                    try {
                        return predicationAlgoInterface.LambdaPrediction(params[0]);
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
                protected void onPostExecute(PredictionResponseClass result) {
                    gotPredictAlgoAnswer = true;
                    if (result == null) {
                        /*
                        if(currentTab==2)
                            tabLayout.selectTab(tabLayout.getTabAt(2));
                        */
                        return;
                    }

                    predictionAlgoResult = new Coordinate(result.getY(),result.getX());
                    if(currentTab==2) {
                        tabLayout.selectTab(tabLayout.getTabAt(2));
                    }
                }
            }.execute(request);

            //End lambda
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Point> convretMapOfScansToPoint(Map<String,Scan> scansMap){
        ArrayList<Point> pointsArrayList = new ArrayList<>();
    try {
        for (Scan scan : scansMap.values()) {
            pointsArrayList.add(new Point(-1, scan.getCoordinate().getLatitude(),
                    scan.getCoordinate().getLongitude(),
                    scan.getCurrentWeather() == null ? Weather.weather.UNKNOWN.name() : scan.getCurrentWeather().name(),
                    scan.getTimeStamp().getTime(),
                    scan.getPlaces()));
        }
    }catch (NullPointerException e){
        return pointsArrayList;
    }
        return pointsArrayList;
    }
/*
    public ArrayList<Coordinate> convertClusterArrayListToCoordiante(ArrayList<Cluster> cluserArrayList){
        ArrayList<Coordinate> coordinatesArrayList = new ArrayList<>();

        for (Cluster cluser : cluserArrayList){
            coordinatesArrayList.add(new Coordinate(cluser.getCenterLat(),cluser.getCenterLong()));
        }
        return coordinatesArrayList;
    }
*/

    public void showHotZonesAlgoMarkersOnMap(){
        updateMapUI();
        if(hotZonesAlgoResultAsCluster.size() == 0 && !gotHotZoneAnswer){
            ((ToolbarActivity)getActivity()).showSmalProgressBar(true);
        }
        else if(hotZonesAlgoResultAsCluster.size() != 0){
            ((ToolbarActivity)getActivity()).showSmalProgressBar(false);
        }
        else if(hotZonesAlgoResultAsCluster.size() == 0 && gotHotZoneAnswer){
            startNotificationPopUp();
        }
    }

    public void showPredictAlgoMarkersOnMap(){
        updateMapUI();

        if(predictionAlgoResult == null && !gotPredictAlgoAnswer){
            ((ToolbarActivity)getActivity()).showSmalProgressBar(true);
        }
        else if(predictionAlgoResult != null){
            ((ToolbarActivity)getActivity()).showSmalProgressBar(false);
        }
        else if(predictionAlgoResult == null && gotPredictAlgoAnswer){
            startNotificationPopUp();
        }
    }
    private void startNotificationPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.not_enough_scans_pop_up, null);

        Button closeBtn = layout.findViewById(R.id.not_enough_scans_popup_layout_close_button);

        closeBtn.setOnClickListener(this);

        notEnoughScansPopupWindow = new PopupWindow(this.getActivity());
        notEnoughScansPopupWindow.setContentView(layout);
        notEnoughScansPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        notEnoughScansPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        notEnoughScansPopupWindow.setClippingEnabled(true);
        notEnoughScansPopupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        notEnoughScansPopupWindow.setFocusable(true);
        notEnoughScansPopupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.not_enough_scans_popup_layout_close_button:
                notEnoughScansPopupWindow.dismiss();
                tabLayout.selectTab(tabLayout.getTabAt(0));
                break;
        }
    }
}
