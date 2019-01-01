package dtg.dogretriever.Presenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogsListAdapter;

public class ProfileActivity extends AppCompatActivity {
    private ArrayList<Dog> dogsList;
    private ListView listView;
    private DogsListAdapter dogsListAdapter;
    private FirebaseAdapter firebaseAdapter;
    private Profile profile;


    private EditText profileName;
    private EditText phoneNumber;
    private EditText address;
    private EditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        profile = firebaseAdapter.getCurrentUserProfileFromFireBase();
        profileName = findViewById(R.id.profileName);
        phoneNumber = findViewById(R.id.profileEditPhoneNumber);
        address = findViewById(R.id.profileEditAddress);
        email = findViewById(R.id.profileEditEmail);
        dogsList = new ArrayList<Dog>();

        updateProfileViews();


        dogsListAdapter = new DogsListAdapter(dogsList,this);

        listView = findViewById(R.id.profile_dogs_list);
        listView.setAdapter(dogsListAdapter);


       // dogsListAdapter.notifyDataSetChanged();
    }

    private void updateProfileViews() {
        //update the user information on the views
        profileName.setText(profile.getFullName());
        phoneNumber.setText(profile.getPhoneNumber());
        address.setText(profile.getAddress());
        email.setText(profile.geteMail());
        initListToShow();
    }

    private void initListToShow() {
        if(profile.getDogsIDMap() != null) {
            for (Map.Entry<String, String> entry : profile.getDogsIDMap().entrySet()) {
                dogsList.add( firebaseAdapter.getDogByCollarIdFromFireBase(entry.getValue()));
            }
        }
    }

    public void onClickAdd(View view) {
        //TODO Popup of dog register need to be implmented this method will draw info from there
        dogsList.add(new Dog("test"));
        dogsListAdapter.notifyDataSetChanged();
    }
}
