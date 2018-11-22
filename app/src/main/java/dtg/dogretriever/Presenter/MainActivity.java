package dtg.dogretriever.Presenter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import dtg.dogretriever.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickScanner(View view) {

    }

    public void clickFindMyDog(View view) {

    }

    public void clickSettings(View view) {

    }

    public void clickProfile(View view) {
        Intent i = new Intent(getBaseContext(),ProfileActivity.class);
        startActivity(i);
    }

    public void clickAbout(View view) {

    }
}