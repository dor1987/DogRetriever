package dtg.dogretriever.Controller.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogsListAdapter;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_DOG_IMAGE_REQUEST = 2;


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
    private TextView errorTextView;
    private ImageView dogProfileImage;

    private ImageButton editImageButton;
    private ImageView profilePicture;

    //edit image pop up
    private Uri mImageUri;
    private ImageView imageBeforeUploadView;
    private EditText imageNameEditText;
    private ProgressBar imageUploadProgressBar;


    //edit dog image popup
    private ImageView dogImageBeforeUploadView;
    private EditText dogImageNameEditText;
    private ProgressBar dogImageUploadProgressBar;
    private String currentDogId;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
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
        editImageButton = view.findViewById(R.id.profileFragmentImageEditButton);
        profilePicture = view.findViewById(R.id.profileFragmentImage);
        dogsList = new ArrayList<Dog>();
        updateProfileViews();


        dogsListAdapter = new DogsListAdapter(dogsList,this.getActivity());

        listView = view.findViewById(R.id.profile_Fragment_dogs_list);
        listView.setAdapter(dogsListAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,getActivity().MODE_PRIVATE);
        firebaseAdapter.writeNewTokenToFireBase(sharedPreferences.getString("token",""));

        ImageButton addDogToListButton =(ImageButton) view.findViewById(R.id.profileFramentAddButton);

        addDogToListButton.setOnClickListener(this);
        editImageButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }


    private void updateProfileViews() {
        //update the user information on the views
        if(profile.getmImageUrl()!=null && !profile.getmImageUrl().isEmpty()) {
            String temp = profile.getmImageUrl();
            Picasso.get().load(temp).into(profilePicture);
        }
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
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);



        //popup  data
        dogNameTextView = layout.findViewById(R.id.add_dog_popup_layout_dog_name);
        colorTextView = layout.findViewById(R.id.add_dog_popup_layout_color);
        breedTextView = layout.findViewById(R.id.add_dog_popup_layout_breed);
        notesTextView = layout.findViewById(R.id.add_dog_popup_layout_notes);
        collarIdTextView = layout.findViewById(R.id.add_dog_popup_layout_collarid);
        errorTextView = layout.findViewById(R.id.add_dog_popup_layout_error_message);
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
        createPopEditDogImage();
        currentDogId = collarId;//used to temp save the dog id for image upload
    }

    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){

        case R.id.add_dog_popup_layout_add_dog_button:
            if(validateNewDogInput())
                saveDog(view);
            break;

        case R.id.add_dog_popup_layout_cancel:
            cancelPopUp(view);
            break;

        case R.id.profileFramentAddButton:
            createPopUpAddNewDog();
            break;

            case R.id.profileFragmentImageEditButton:
            createPopEditImage();
            break;

            case R.id.image_upload_popup_layout_upload_button:
               if(firebaseAdapter.isThereAnOnGoingImageUploadTask()){
                   Toast.makeText(getActivity(), "Please wait until upload is done", Toast.LENGTH_SHORT).show();
               }
               else {
                   firebaseAdapter.uploadFile(this.getActivity().getBaseContext(), mImageUri, imageNameEditText.getText().toString());

                   firebaseAdapter.registerImageUploadListener(new FirebaseAdapter.ImageUploadListener() {

                       @Override
                       public void onUploadFinish(String url) {
                           firebaseAdapter.removeImageUploadListener();
                           cancelPopUp(view);
                           Picasso.get()
                                   .load(url)
                                   .placeholder(R.drawable.asset6h)
                                   .error(R.drawable.asset6h)
                                   .into(profilePicture);
                       }
                       @Override
                   public void onUploadProgress(double progress) {
                       imageUploadProgressBar.setProgress((int) progress);
                   }

                       @Override
                       public void onDogImageUploadFinish(String url) {
                           //ignore
                       }

                       @Override
                       public void onDogImageUploadProgress(double progress) {
                           //ignore
                       }
                   });
               }
                break;

            case R.id.image_upload_popup_layout_cancel_button:
                firebaseAdapter.cancelAnUpload();
                cancelPopUp(view);
                break;

            case R.id.image_upload_layout_choose_file_button:
                openFileChooser(PICK_IMAGE_REQUEST);
                break;

            case R.id.dog_image_upload_popup_layout_next_button:
                firebaseAdapter.cancelAnUpload();
                cancelPopUp(view);
                break;

            case R.id.dog_image_upload_layout_choose_file_button:
                openFileChooser(PICK_DOG_IMAGE_REQUEST);
                break;

            case R.id.dog_image_upload_popup_layout_upload_button:
                if(firebaseAdapter.isThereAnOnGoingImageUploadTask()){
                    Toast.makeText(getActivity(), "Please wait until upload is done", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAdapter.uploadDogFile(this.getActivity().getBaseContext(), mImageUri, dogImageNameEditText.getText().toString(),currentDogId);

                    firebaseAdapter.registerImageUploadListener(new FirebaseAdapter.ImageUploadListener() {

                        @Override
                        public void onUploadFinish(String url) {
                            //ignore
                        }

                        @Override
                        public void onUploadProgress(double progress) {
                            //ignore
                        }

                        @Override
                        public void onDogImageUploadFinish(String url) {
                            firebaseAdapter.removeImageUploadListener();
                            cancelPopUp(view);
                        }

                        @Override
                        public void onDogImageUploadProgress(double progress) {
                            dogImageUploadProgressBar.setProgress((int) progress);
                        }
                    });
                }
                break;
        }
    }

    private boolean validateNewDogInput() {
        boolean result = true;
        if(collarIdTextView.getText()==null || collarIdTextView.getText().toString().trim().isEmpty()){
            result = false;
            errorTextView.setText("Id is needed");
        }
        else if(firebaseAdapter.getDogByCollarIdFromFireBase(collarIdTextView.getText().toString())!=null){
            result = false;
            errorTextView.setText("Id is taken");
        }

    return result;
    }

    private void createPopEditImage() {
        LayoutInflater layoutInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.image_upload_pop_up, null);

        Button uploadImageButton = layout.findViewById(R.id.image_upload_popup_layout_upload_button);
        Button cancelImageUploadButton = layout.findViewById(R.id.image_upload_popup_layout_cancel_button);
        Button chooseImageUploadButton = layout.findViewById(R.id.image_upload_layout_choose_file_button);
        imageBeforeUploadView = layout.findViewById(R.id.image_upload_popup_layout_imageviewbeforeupload);
        imageNameEditText = layout.findViewById(R.id.image_upload_layout_choose_file_edittext);
        imageUploadProgressBar =layout.findViewById(R.id.image_upload_progressbar);

        uploadImageButton.setOnClickListener(this);
        cancelImageUploadButton.setOnClickListener(this);
        chooseImageUploadButton.setOnClickListener(this);

        initPopUpsGraphics(layout);
    }

    private void openFileChooser(int src){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, src);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

                mImageUri = data.getData();
                Picasso.get().load(mImageUri).fit().into(imageBeforeUploadView);

                String uriString = mImageUri.toString();
                File myFile = new File(uriString);

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getActivity().getContentResolver().query(mImageUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            imageNameEditText.setText(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    imageNameEditText.setText(myFile.getName());
                }
        }

        else if(requestCode == PICK_DOG_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(dogImageBeforeUploadView);

            String uriString = mImageUri.toString();
            File myFile = new File(uriString);

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(mImageUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        dogImageNameEditText.setText(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                dogImageNameEditText.setText(myFile.getName());
            }
        }

    }
    private void createPopEditDogImage() {
        LayoutInflater layoutInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dog_image_upload_pop_up, null);

        Button uploadDogImageButton = layout.findViewById(R.id.dog_image_upload_popup_layout_upload_button);
        Button NextImageUploadButton = layout.findViewById(R.id.dog_image_upload_popup_layout_next_button);
        Button chooseDogImageUploadButton = layout.findViewById(R.id.dog_image_upload_layout_choose_file_button);
        dogImageBeforeUploadView = layout.findViewById(R.id.dog_image_upload_popup_layout_imageviewbeforeupload);
        dogImageNameEditText = layout.findViewById(R.id.dog_image_upload_layout_choose_file_edittext);
        dogImageUploadProgressBar =layout.findViewById(R.id.dog_image_upload_progressbar);

        uploadDogImageButton.setOnClickListener(this);
        NextImageUploadButton.setOnClickListener(this);
        chooseDogImageUploadButton.setOnClickListener(this);

        initPopUpsGraphics(layout);
    }

    public void initPopUpsGraphics(View layout){
        popupWindow = new PopupWindow(this.getActivity());
        popupWindow.setContentView(layout);
        popupWindow.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(1);
        popupWindow.setWidth(1);
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);

    }

}
