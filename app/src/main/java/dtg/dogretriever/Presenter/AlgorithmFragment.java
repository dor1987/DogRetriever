package dtg.dogretriever.Presenter;

import android.annotation.SuppressLint;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.R;


public class AlgorithmFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener{
    private static final String TAG = "AlgorithmFragment";

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
        mMap.setOnMarkerClickListener(this);
        updateLocationUI();

    }


    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (checkLocationPermissionAndEnabled()){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            showRadiusArea(getCoordinatesToShow(),getRandomRadius(getCoordinatesToShow().size()));

        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private boolean checkLocationPermissionAndEnabled (){
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else{
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(!gps_enabled && !network_enabled) {
                return false;
            }
            return true;
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


        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
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
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(),currentLocation.getAltitude())));



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
}
