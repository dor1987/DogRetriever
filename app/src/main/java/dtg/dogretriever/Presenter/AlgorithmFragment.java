package dtg.dogretriever.Presenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.R;


public class AlgorithmFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = "AlgorithmFragment";
    public static final int MY_CODE_REQUEST = 123;
    private enum algoType {PREDICTION, LEARNING}

    private final double minRadius = 1000.0;
    private final double maxRadius = 5000.0;
    private ArrayList<Color> colors;
    private algoType currentAlgoShown = algoType.PREDICTION;
    private Button predictAlgoBtn;
    private Button learningAlgoBtn;
    private GoogleMap mMap;
    private SupportMapFragment smFragment;
    private FragmentTabHost mTabHost;
    private ArrayList<Coordinate> coordinatesToShow;
    private Location currentLocation;

    private OnFragmentInteractionListener mListener;

    public AlgorithmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkForPermissions();
        displayLocationSettingsRequest(getContext());
        mMap.setOnMarkerClickListener(this);


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

        if (getContext().checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            this.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_CODE_REQUEST);


        }else{
            updateMapUI();
            Log.d(TAG,"Location permission granted from manifest");
        }

    }

    @SuppressLint("MissingPermission")
    public void updateMapUI(){
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        showRadiusArea(getCoordinatesToShow(), getRandomRadius(getCoordinatesToShow().size()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch(requestCode){
            case MY_CODE_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateMapUI();
                    Log.d(TAG,"Location permission granted");
                }
                else{
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_algorithm, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);




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

    private void setPredictAlgoON(){
        predictAlgoBtn.setBackgroundResource(R.color.colorPrimaryDark);
        learningAlgoBtn.setBackgroundResource(R.color.colorPrimary);
        currentAlgoShown = algoType.PREDICTION;
    }

    private void setLearningAlgoON(){
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


    private void showRadiusArea(ArrayList<Coordinate> centerList, ArrayList<Double> radiusList){

        for(int i=0 ; i<centerList.size() ; i++){
            mMap.addCircle(new CircleOptions().center(centerList.get(i).getLocation())
                    .radius(radiusList.get(i)).fillColor(getRandomColor()));
        }
    }

    private int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private ArrayList<Coordinate> getCoordinatesToShow(){

        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate((new LatLng(32.0820469,34.811154)),new Date(System.currentTimeMillis())));
        coordinates.add(new Coordinate((new LatLng(32.08167461,34.81057763)),new Date(System.currentTimeMillis())));
        coordinates.add(new Coordinate((new LatLng(32.08202004,34.8135817)),new Date(System.currentTimeMillis())));
        coordinates.add(new Coordinate((new LatLng(32.08049284,34.81220841)),new Date(System.currentTimeMillis())));
        coordinates.add(new Coordinate((new LatLng(32.08156552,34.80885386)),new Date(System.currentTimeMillis())));

        return coordinates;

    }

    private ArrayList getRandomRadius(int size){
        Random rnd = new Random();
        ArrayList radiusList = new ArrayList<>();

        for(int i=0; i<size ; i++)
        radiusList.add(minRadius + (maxRadius - minRadius) * rnd.nextDouble());

        return radiusList;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        Log.i(TAG, "Current location: Lat - " + location.getLatitude() + "Long - " +location.getLongitude() );
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getAltitude())));



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getAltitude())));

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {


    }
}
