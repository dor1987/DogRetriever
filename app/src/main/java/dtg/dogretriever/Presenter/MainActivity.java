package dtg.dogretriever.Presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;


import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popupWindow = null;
    private int popupWidth ;
    private int popupHeight;
    private FirebaseAdapter firebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        popupWidth = displayMetrics.widthPixels ;
        popupHeight = displayMetrics.heightPixels ;

        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

    }



    public void clickScanner(View view) {
        //temp implementation for debugging

    }

    public void clickFindMyDog(View view) {
        if(firebaseAdapter.isUserConnected())
            createPopUpChooseDogName();
        else {
            Intent i = new Intent(getBaseContext(), SigninActivity.class);
            startActivity(i);
        }
    }

    public void clickSettings(View view) {

    }

    public void clickProfile(View view) {
        //check if user logged in
        //if user logged in send to profile
        //else send to login activity

        Intent i;
        if(firebaseAdapter.isUserConnected()){
            i = new Intent(getBaseContext(),ProfileActivity.class);
        }
        else{
            i = new Intent(getBaseContext(),LoginActivity.class);
        }

        startActivity(i);

    }

    public void clickAbout(View view) {

    }


    private void createPopUpChooseDogName(){

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.choose_dog_popup, null);

        ListView listView = layout.findViewById(R.id.popup_dog_name_list_view);
        Button cancelBtn = layout.findViewById(R.id.popup_layout_cancel);
        TextView errorMessage = layout.findViewById(R.id.popup_layout_errorMessage);

            DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(), this);
            listView.setAdapter(dogNamesAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                    intent.putExtra("TAG", "AlgorithmFragment");
                    intent.putExtra("DOG_ID", createDogsList().get(i).getCollarId());
                    startActivity(intent);

                }
            });

            if(dogNamesAdapter.getCount()==0){
                errorMessage.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(layout);
        popupWindow.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(1);
        popupWindow.setWidth(1);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);

    }


    private ArrayList<Dog> createDogsList(){
        return firebaseAdapter.getListOfDogsOwnedByCurrentUser();
    }


    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }
}