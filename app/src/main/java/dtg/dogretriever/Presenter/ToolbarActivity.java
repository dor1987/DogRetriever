package dtg.dogretriever.Presenter;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.FakeDataBaseGenerator;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.Presenter.LearningAlgoTemp.LearningAlgo;
import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREDICT_ALGO_KEY= "predictAlgo";
    private static final String LEARNING_ALGO_KEY= "learningAlgo";

    TextView profile_textview;
    FragmentManager fm;
    FrameLayout frameLayout;
    AlgorithmFragment algorithmFragment;
    FakeDataBaseGenerator fakeDataBaseGenerator = new FakeDataBaseGenerator(2); //dont forget to remove

    FirebaseAdapter firebaseAdapter;
    LearningAlgo learningAlgo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        algorithmFragment = new AlgorithmFragment();

        profile_textview = findViewById(R.id.profile_toolbar_text);
        Bundle bundle = new Bundle();

        learningAlgo = new LearningAlgo();


        //1.get the dog Id from bundle
        //2.and create a array of cords
        //3.put array of cords in bundle to send to algo fragment
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("DOG_ID");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("DOG_ID");
        }

       // Map<String,Scan> mapOfScans =  firebaseAdapter.getAllScanOfSpecificDog(firebaseAdapter.getDogByCollarIdFromFireBase(newString));
        Map<String,Scan> mapOfScans =  firebaseAdapter.getAllScanOfAllDogs();

        bundle.putParcelableArrayList(LEARNING_ALGO_KEY,learningAlgo.learningAlgo(mapOfScans));
        //

        //bundle.putParcelableArrayList(LEARNING_ALGO_KEY,fakeDataBaseGenerator.getRandomScanCoordsArray());
        algorithmFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, algorithmFragment);
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
}
