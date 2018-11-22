package dtg.dogretriever.Presenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.R;

public class ProfileActivity extends AppCompatActivity {
    private ArrayList<Dog> dogsList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        listView = findViewById(R.id.dogs_list);

    }


}
