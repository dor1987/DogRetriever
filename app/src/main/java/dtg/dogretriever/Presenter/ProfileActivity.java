package dtg.dogretriever.Presenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.ArrayList;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogsListAdapter;

public class ProfileActivity extends AppCompatActivity {
    private ArrayList<Dog> dogsList;
    private ListView listView;
    private DogsListAdapter dogsListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dogsList = new ArrayList<Dog>();
        initListToShow();

        dogsListAdapter = new DogsListAdapter(dogsList,this);

        listView = findViewById(R.id.dogs_list);
        listView.setAdapter(dogsListAdapter);


       // dogsListAdapter.notifyDataSetChanged();
    }

    private void initListToShow() {
        dogsList.add(new Dog("1","Luka","Pitbull","White",Dog.enumSize.SMALL,"Insane"));
        dogsList.add(new Dog("2","Nala","Labrador","White",Dog.enumSize.LARGE,"Fat"));
        dogsList.add(new Dog("3","kc","Pincher","Brown",Dog.enumSize.TINY,"Good girl"));
    }

}
