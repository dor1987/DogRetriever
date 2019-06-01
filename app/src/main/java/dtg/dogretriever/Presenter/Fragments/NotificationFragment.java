package dtg.dogretriever.Presenter.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Weather;
import dtg.dogretriever.Presenter.ToolbarActivity;
import dtg.dogretriever.R;

public class NotificationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {
    private static final String TAG = "notification fragment";
    public static final int MY_CODE_REQUEST = 123;

    private GoogleMap mMap;
    private SupportMapFragment smFragment;
    private Location currentLocation;
    boolean isMapReady;
    SharedPreferences sharedPreferences;
    Coordinate coordinate;

    public NotificationFragment() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkForPermissions();
        displayLocationSettingsRequest(getContext());
        mMap.setOnMarkerClickListener(this);
        currentLocation = ((ToolbarActivity)getActivity()).getCurrentLocation();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinate.getLatitude(),coordinate.getLongitude()),15));
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

        createMarker(coordinate.getLatitude(), coordinate.getLongitude(), "bla bla", "bla bla");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinate.getLatitude(),coordinate.getLongitude()),15));
        Toast.makeText(getContext(), "Created marker with cords: "+ coordinate.getLatitude()+" "+coordinate.getLongitude()+" ", Toast.LENGTH_SHORT).show();
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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        Double lat = getArguments().getDouble("lat");
        Double lng = getArguments().getDouble("lng");

        coordinate = new Coordinate(lat,lng);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isMapReady = false;
        currentLocation = ((ToolbarActivity)getActivity()).getCurrentLocation();
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FragmentManager fm = getActivity().getSupportFragmentManager();
        smFragment = (SupportMapFragment) fm.findFragmentById(R.id.notificationMapView);
        if (smFragment == null) {
            smFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.notificationMapView, smFragment).commit();
        }

        smFragment.getMapAsync(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
