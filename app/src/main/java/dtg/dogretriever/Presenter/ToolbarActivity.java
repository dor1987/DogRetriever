package dtg.dogretriever.Presenter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import dtg.dogretriever.R;

public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener{
    TextView profile_textview;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        AlgorithmFragment algorithmFragment = new AlgorithmFragment();

        profile_textview = findViewById(R.id.profile_toolbar_text);

        profile_textview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        profile_textview.setTypeface(profile_textview.getTypeface(), Typeface.BOLD);
                        break;
                    case MotionEvent.ACTION_UP:
                        profile_textview.setTypeface(profile_textview.getTypeface(), Typeface.NORMAL);
                        break;
                }
                return false;

            }
        });

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
