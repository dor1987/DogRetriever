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

import dtg.dogretriever.Model.FakeDataBaseGenerator;
import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String PREDICT_ALGO_KEY= "predictAlgo";
    private static final String LEARNING_ALGO_KEY= "learningAlgo";

    TextView profile_textview;
    FragmentManager fm;
    FrameLayout frameLayout;
    AlgorithmFragment algorithmFragment;
    FakeDataBaseGenerator fakeDataBaseGenerator = new FakeDataBaseGenerator(2); //dont forget to remove


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        algorithmFragment = new AlgorithmFragment();

        profile_textview = findViewById(R.id.profile_toolbar_text);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LEARNING_ALGO_KEY,fakeDataBaseGenerator.getRandomScanCoordsArray());
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
