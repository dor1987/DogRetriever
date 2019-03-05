package dtg.dogretriever.Presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import dtg.dogretriever.Presenter.LearningAlgoTemp.LearningAlgo;
import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREDICT_ALGO_KEY = "predictAlgo";
    private static final String LEARNING_ALGO_KEY = "learningAlgo";

    TextView profile_textview;
    FragmentManager fm;
    FrameLayout frameLayout;
    AlgorithmFragment algorithmFragment;
    FakeDataBaseGenerator fakeDataBaseGenerator = new FakeDataBaseGenerator(2); //dont forget to remove

    FirebaseAdapter firebaseAdapter;
    LearningAlgo learningAlgo;
    private FusedLocationProviderClient fusedLocationClient;

    final Bundle bundle = new Bundle();
    //final Map<String,Scan> mapOfScans = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        algorithmFragment = new AlgorithmFragment();

        profile_textview = findViewById(R.id.profile_toolbar_text);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        learningAlgo = new LearningAlgo();
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        String dogId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                dogId = null;
            } else {
                dogId = extras.getString("DOG_ID");
            }
        }

        else {
            dogId = (String) savedInstanceState.getSerializable("DOG_ID");
        }

        //startAlgoFragmentDefault(dogId);
        //startAlgoFragmentLearningAlgo(500);
        startAlgoMapFragment(dogId);
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
        fragmentTransaction.add(R.id.fragment_container, algorithmFragment);
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
}
