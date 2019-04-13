package dtg.dogretriever.Presenter.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

import androidx.fragment.app.Fragment;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String SHARED_PREFS = "sharedPrefs";

    private ArrayList<Dog> dogsList;
    private ListView listView;
    private DogsListAdapter dogsListAdapter;
    private FirebaseAdapter firebaseAdapter;
    private Profile profile;


    private EditText profileName;
    private EditText phoneNumber;
    private EditText address;
    private EditText email;



    //popup window for add new dog
    private PopupWindow popupWindow = null;
    private int popupWidth ;
    private int popupHeight;

    //popup window data to save
    private Dog dog;
    private Dog.enumSize size;
    private EditText dogNameTextView;
    private EditText colorTextView;
    private EditText breedTextView;
    private EditText notesTextView;
    private EditText collarIdTextView;




    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //setContentView(R.layout.activity_profile);
        //for pop up window
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        popupWidth = displayMetrics.widthPixels ;
        popupHeight = displayMetrics.heightPixels ;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        profile = firebaseAdapter.getCurrentUserProfileFromFireBase();
        profileName = view.findViewById(R.id.profileFragmentName);
        phoneNumber = view.findViewById(R.id.profileFragmentEditPhoneNumber);
        address = view.findViewById(R.id.profileFragmentEditAddress);
        email = view.findViewById(R.id.profileFragmentEditEmail);
        dogsList = new ArrayList<Dog>();

        updateProfileViews();


        dogsListAdapter = new DogsListAdapter(dogsList,this.getActivity());

        listView = view.findViewById(R.id.profile_Fragment_dogs_list);
        listView.setAdapter(dogsListAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,getActivity().MODE_PRIVATE);
        firebaseAdapter.writeNewTokenToFireBase(sharedPreferences.getString("token",""));

        ImageButton addDogToListButton =(ImageButton) view.findViewById(R.id.profileFramentAddButton);

        addDogToListButton.setOnClickListener(this);


        // Inflate the layout for this fragment
        return view;
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


    private void createPopUpAddNewDog(){

        LayoutInflater layoutInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.add_dog_popup, null);

        Spinner spinner = layout.findViewById(R.id.add_dog_popup_layout_size_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.size, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button addDogButton = layout.findViewById(R.id.add_dog_popup_layout_add_dog_button);
        Button addDogPopUpcancel = layout.findViewById(R.id.add_dog_popup_layout_cancel);

        addDogButton.setOnClickListener(this);
        addDogPopUpcancel.setOnClickListener(this);

        popupWindow = new PopupWindow(this.getActivity());
        popupWindow.setContentView(layout);
        popupWindow.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(1);
        popupWindow.setWidth(1);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);

        //popup  data
        dogNameTextView = layout.findViewById(R.id.add_dog_popup_layout_dog_name);
        colorTextView = layout.findViewById(R.id.add_dog_popup_layout_color);
        breedTextView = layout.findViewById(R.id.add_dog_popup_layout_breed);
        notesTextView = layout.findViewById(R.id.add_dog_popup_layout_notes);
        collarIdTextView = layout.findViewById(R.id.add_dog_popup_layout_collarid);



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Convert the value from the spinner from string to enum and store it
        String sizeAsString = adapterView.getItemAtPosition(i).toString();
        if(sizeAsString.equals("TINY"))
            size = Dog.enumSize.TINY;

        else if(sizeAsString.equals("SMALL"))
            size = Dog.enumSize.SMALL;

        else if(sizeAsString.equals("MEDIUM"))
            size = Dog.enumSize.MEDIUM;

        else if(sizeAsString.equals("LARGE"))
            size = Dog.enumSize.LARGE;

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void saveDog(View view) {
        String collarId = collarIdTextView.getText().toString();
        String profileId = profile.getId();

        Dog.DogBuilder dogBuilder = new Dog.DogBuilder(collarId,profileId);

        dogBuilder.setName(dogNameTextView.getText().toString());

        if(!colorTextView.getText().toString().equals(""))
            dogBuilder.setColor(colorTextView.getText().toString());
        if(!breedTextView.getText().toString().equals(""))
            dogBuilder.setBreed(breedTextView.getText().toString());
        if(!notesTextView.getText().toString().equals(""))
            dogBuilder.setNotes(notesTextView.getText().toString());
        if(size!=null)
            dogBuilder.setSize(size);

        Dog tempDog = dogBuilder.build();
        firebaseAdapter.addDogToDataBase(tempDog);
        dogsList.add(tempDog);
        dogsListAdapter.notifyDataSetChanged();
        popupWindow.dismiss();
    }

    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        case R.id.add_dog_popup_layout_add_dog_button:
            saveDog(view);
            break;

        case R.id.add_dog_popup_layout_cancel:
            cancelPopUp(view);
            break;

        case R.id.profileFramentAddButton:
            createPopUpAddNewDog();
            break;
        }

    }
}
