package dtg.dogretriever.Presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import dtg.dogretriever.Model.Beacon;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;
import dtg.dogretriever.View.DogScanListAdapter;

import static dtg.dogretriever.Presenter.MyMessagingService.SHARED_PREFS;

public class MainActivity extends AppCompatActivity implements MyLocationService.LocationListener,
        DogScanListFunctionalityInterface, BeaconScannerService.OnBeaconEventListener {
    private static final String TAG = "MainActivity";

    private PopupWindow popupWindow = null;
    private View mProgressView, mSmallProgressBarView;
    private View mMainMenuFormView;
    private TextView userWelcomeTextView;
    private CircleImageView profilePicView;
    //debug
    private PopupWindow fakeScanPopUp = null;
    private PopupWindow notificationPopUp = null;
    private EditText dogIdFromFakeScanTextView;

    //real scan debug

    private PopupWindow scanPopUp = null;
    private RecyclerView.Adapter dogScanListAdapter;
    private ArrayList<Dog> listOfDogScanned;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PopupWindow ownerInfoPopUp = null;
    private TextView ownerPhoneTextView;

    //Location Service
    boolean isBound = false;
    private MyLocationService.MylocalBinder mBinder;
    private Location userCurrentLocation;
    private MyLocationService myLocationService;

    //notification pop up
    Double latitude;
    Double longitude;
    boolean isNotifcationPopShowenBefore;
    Bundle bundle;

    //Bluetooth
    private static final int BT_EXPIRE_TIMEOUT = 5000;
    private static final int BT_EXPIRE_TASK_PERIOD = 1000;
    private BeaconScannerService mBeaconService;
    private DogScanListAdapter DogScanListAdapter;
    private ArrayList <Beacon> mBeaconAdapterItems;
    private boolean isBoundBeaconService = false;

    //firebase

    private FirebaseAdapter firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        mProgressView = findViewById(R.id.loading_progress);
        mSmallProgressBarView = findViewById(R.id.main_menu_small_progres_bar);
        mMainMenuFormView = findViewById(R.id.mainMenuForm);
        userWelcomeTextView = findViewById(R.id.userWelcome);
        profilePicView = findViewById(R.id.profile_image);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        initWelcomeTextview();
        setTokenListener();
        userCurrentLocation = new Location("");
        //start the service
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        isNotifcationPopShowenBefore = false;
        mBeaconAdapterItems = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start the service
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        showSmalProgressBar(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            mBinder.DeleteLocationListener(MainActivity.this);
            unbindService(mConnection);
            isBound = false;
        }
        if(isBoundBeaconService) {
            getApplicationContext().unbindService(mBeaconConnection);
            isBoundBeaconService = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            mBinder.DeleteLocationListener(MainActivity.this);
            unbindService(mConnection);
            isBound = false;
        }
    }

    public void clickScanner(View view) {
        //temp implementation for debugging
        createPopUpScan();
    }

    public void initWelcomeTextview() {
        if (firebaseAdapter.isUserConnected()) {
            if (firebaseAdapter.isUserDataReadyNow()) {
                userWelcomeTextView.setText("Hello " + firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                initProfilePic();
            } else {
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        firebaseAdapter.removeProfileDataListener();
                        userWelcomeTextView.setText("Hello " + firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                        initProfilePic();
                    }
                });
            }
        } else {
            removeProfilePic();
            userWelcomeTextView.setText("Hello Guest");
        }
    }

    public void clickFindMyDog(View view) {
        if (firebaseAdapter.isUserConnected()) {

            if (firebaseAdapter.isUserDataReadyNow()) {
                createPopUpChooseDogName();
            } else {
                showSmalProgressBar(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showSmalProgressBar(false);
                        createPopUpChooseDogName();
                        firebaseAdapter.removeProfileDataListener();
                    }
                });
            }
        } else {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
            showSmalProgressBar(false);
        }
    }

    public void clickSettings(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("fragmentToOpen", 1);
        startActivity(intent);
    }

    public void clickProfile(View view) {
        //check if user logged in
        //if user logged in send to profile
        //else send to login activity
        if (firebaseAdapter.isUserConnected()) {
            if (firebaseAdapter.isUserDataReadyNow()) {
                Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                intent.putExtra("fragmentToOpen", 3);
                startActivity(intent);
            } else {
                showSmalProgressBar(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showSmalProgressBar(false);
                        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                        intent.putExtra("fragmentToOpen", 3);
                        firebaseAdapter.removeProfileDataListener();
                        startActivity(intent);

                    }
                });
            }

        } else {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        }
    }

    public void clickAbout(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("fragmentToOpen", 2);
        startActivity(intent);
    }


    private void createPopUpChooseDogName() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.choose_dog_popup, null);

        ListView listView = layout.findViewById(R.id.popup_dog_name_list_view);
        Button cancelBtn = layout.findViewById(R.id.popup_layout_cancel);
        TextView errorMessage = layout.findViewById(R.id.popup_layout_errorMessage);

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(), this);
        listView.setAdapter(dogNamesAdapter);
        Log.d("DorCheck", "Location At MainMenu: " + userCurrentLocation + "");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                showSmalProgressBar(true);
                popupWindow.dismiss();
                new AsyncToolBarActivityStart(createDogsList().get(i).getCollarId()).execute();
            }
        });

        if (dogNamesAdapter.getCount() == 0) {
            errorMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(layout);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);
    }


    private ArrayList<Dog> createDogsList() {
        firebaseAdapter.getCurrentUserProfileFromFireBase();
        return firebaseAdapter.getListOfDogsOwnedByCurrentUser();
    }


    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainMenuFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainMenuFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void startNotificationPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.notification_pop_up, null);
        notificationPopUp = new PopupWindow(this);
        notificationPopUp.setContentView(layout);
        notificationPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setClippingEnabled(true);
        notificationPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        notificationPopUp.setFocusable(true);
        notificationPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);

    }

    public void openMapWithNotificationCords(View view) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("TAG", "notificatonFragment");
        intent.putExtra("fragmentToOpen", 4);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("currentLocation", userCurrentLocation);
        startActivity(intent);

    }

    public void closeNotificationPopUp(View view) {
        notificationPopUp.dismiss();
    }

    private void createPopUpFakeScan() {
        //for debug

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.fake_scan_debug, null);
        fakeScanPopUp = new PopupWindow(this);
        fakeScanPopUp.setContentView(layout);
        fakeScanPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        fakeScanPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        fakeScanPopUp.setClippingEnabled(true);
        fakeScanPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        fakeScanPopUp.setFocusable(true);
        fakeScanPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
        dogIdFromFakeScanTextView = layout.findViewById(R.id.fake_scan__popup_layout_dog_id);

    }

    public void scanDogForDebug(View view) {
        //Used to put data in data base, dont use without asking dor
        Dog tempDog = null;
       // String collarId = dogIdFromFakeScanTextView.getText().toString();

        //if (!collarId.equals(""))
            tempDog = firebaseAdapter.getDogByCollarIdFromFireBase("7777777");

        if (tempDog != null) {
            final Dog finalTempDog = tempDog;
            new Thread(new Runnable() {
                public void run(){
                    for (int i = 0; i < 20; i++) {
                        LatLng locationToReturn = getRandomLocation((new LatLng(32.083729, 34.811020)), 2000);
                        try {
                            Scan tempScan = new Scan(new Coordinate(locationToReturn.latitude, locationToReturn.longitude), new Date(1558372715000L + i*1000));
                            firebaseAdapter.addScanToDog(finalTempDog, tempScan);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();



            //fakeScanPopUp.dismiss();
            Toast.makeText(myLocationService, "Scan accepted", Toast.LENGTH_SHORT).show();

        } else {
            dogIdFromFakeScanTextView.setText("Id not found in Database");
        }

    }

    public void cancelScanPopUp(View view) {
        //for debug
        if(isBoundBeaconService) {
            getApplicationContext().unbindService(mBeaconConnection);
            isBoundBeaconService = false;
        }
        scanPopUp.dismiss();
    }

    private boolean checkBluetoothStatus() {
        BluetoothManager manager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (adapter == null || !adapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);

            return false;
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    private void createPopUpScan() {
        //real implementation
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.scan_popup, null);
        recyclerView = layout.findViewById(R.id.scan_popup_layout_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //scan pop up
        listOfDogScanned = new ArrayList<>();

        dogScanListAdapter = new DogScanListAdapter(listOfDogScanned, MainActivity.this, this);
        recyclerView.setAdapter(dogScanListAdapter);

            scanPopUp = new PopupWindow(this);
            scanPopUp.setContentView(layout);
            scanPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            scanPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            scanPopUp.setClippingEnabled(true);
            scanPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            scanPopUp.setFocusable(true);
            scanPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
            scanPopUp.setOutsideTouchable(false);


        if(checkBluetoothStatus()) {
            Log.i(TAG, "Start scanning...");
            final Intent intent = new Intent(this, BeaconScannerService.class);
            isBoundBeaconService =  getApplicationContext().bindService(intent, mBeaconConnection, BIND_AUTO_CREATE);

        }
    }

    public void scanRefresh(View view) {
        //TODO add bluetooth refresh functionality
        listOfDogScanned.clear();
        dogScanListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOwnerInformation(String ownerId) {
        showInfo(firebaseAdapter.getUserById(ownerId));
    }

    @Override
    public void scanDog(Dog dog) {
    //Called from the dogscanlist adapter
      myLocationService.addScanToDataBase(dog);
    }

    public void showInfo(Profile ownerProfile) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.scan_owner_information_popup, null);
        ownerInfoPopUp = new PopupWindow(this);
        ownerInfoPopUp.setContentView(layout);
        ownerInfoPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ownerInfoPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ownerInfoPopUp.setClippingEnabled(true);
        ownerInfoPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        ownerInfoPopUp.setFocusable(true);
        ownerInfoPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
        TextView ownerNameTextView = layout.findViewById(R.id.scan_pop_up_owner_info_name);
        ownerPhoneTextView = layout.findViewById(R.id.scan_pop_up_owner_info_number);
        TextView ownerAddressTextView = layout.findViewById(R.id.scan_pop_up_owner_info_address);
        TextView ownerMailTextView = layout.findViewById(R.id.scan_pop_up_owner_info_mail);
        CircleImageView ownerPicView = layout.findViewById(R.id.scan_pop_up_owner_info_image);
        Button ownerInfoCallButton = layout.findViewById(R.id.scan_pop_up_owner_info_call_button);
        ownerNameTextView.setText(ownerProfile.getFullName() == null ? "Not available" : ownerProfile.getFullName());
        ownerPhoneTextView.setText(ownerProfile.getPhoneNumber()== null ? "Not available" : ownerProfile.getPhoneNumber());
        ownerAddressTextView.setText(ownerProfile.getAddress()== null ? "Not available" : ownerProfile.getAddress());
        ownerMailTextView.setText(ownerProfile.geteMail()== null ? "Not available" : ownerProfile.geteMail());
        if(ownerPhoneTextView.getText().toString().equals("Not available")){
            ownerInfoCallButton.setVisibility(View.INVISIBLE);
        }

        Picasso.get()
                .load(ownerProfile.getmImageUrl())
                .placeholder(R.drawable.asset6h)
                .error(R.drawable.asset6h)
                .into(ownerPicView);
    }


    public void onCallOwnerClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ownerPhoneTextView.getText().toString()));
        startActivity(intent);
    }

    public void onClosedOwnerInfoClicked(View view) {
        ownerInfoPopUp.dismiss();
    }

    public LatLng getRandomLocation(LatLng point, int radius) {
        //get random location in a predefined radius
        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);
            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }

    @Override
    public void locationChanged(Location location) {
        showProgress(false);
        userCurrentLocation = location;
        Log.d("DorCheck","Location At MainActivity: userCurrentLocation: "+ userCurrentLocation+" location from function "+location );
        if(mProgressView.getVisibility()!=View.VISIBLE) {
            if (!isNotifcationPopShowenBefore)
                showNotificationPop();
        }
    }

    public void showNotificationPop(){
        isNotifcationPopShowenBefore = true;
        if(bundle!=null){
           for(String key : bundle.keySet()){
               if(key.equals("lat"))
                   latitude = Double.parseDouble(bundle.getString(key));

               else if (key.equals("long"))
                   longitude = Double.parseDouble(bundle.getString(key));
           }
           if(latitude!=null && latitude!=0.0
                   && longitude != null && longitude != 0.0)
                startNotificationPopUp();
        }
        else{
            //debug
          //  Toast.makeText(this, "Got Nothing", Toast.LENGTH_SHORT).show();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBinder = (MyLocationService.MylocalBinder) service;
            mBinder.registerLocationListener(MainActivity.this);
            myLocationService = mBinder.getMyLocationService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public void setTokenListener() {
        if (firebaseAdapter.isUserConnected()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", instanceIdResult.getToken() + "");
                    editor.commit();
                    firebaseAdapter.writeNewTokenToFireBase(instanceIdResult.getToken() + "");
                }
            });
        }
    }


    public void initProfilePic(){
        if(firebaseAdapter.getCurrentUserProfileFromFireBase().getmImageUrl()!=null &&
                !firebaseAdapter.getCurrentUserProfileFromFireBase().getmImageUrl().trim().isEmpty())
        Picasso.get()
                .load(firebaseAdapter.getCurrentUserProfileFromFireBase()
                .getmImageUrl())
                .placeholder(R.drawable.asset6h)
                .error(R.drawable.asset6h)
                .into(profilePicView);

    }

    private void removeProfilePic(){
        Picasso.get()
                .load(R.drawable.asset6h)
                .placeholder(R.drawable.asset6h)
                .error(R.drawable.asset6h)
                .into(profilePicView);
    }

    public void showSmalProgressBar(final Boolean toShow){

        if(toShow){ //disable/enable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
            mSmallProgressBarView.animate().setDuration(shortAnimTime).alpha(
                    toShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSmallProgressBarView.setVisibility(toShow ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onBeaconIdentifier(String deviceAddress, int rssi, String instanceId) {
        Dog foundDog = firebaseAdapter.getDogByCollarIdFromFireBase(instanceId);
        Log.i(TAG, "beacon with id " + instanceId + " was found");
        if(foundDog != null) {
            if (!listOfDogScanned.contains(foundDog)) {
                Log.i(TAG, foundDog.getName() + "'s beacon was found");
                listOfDogScanned.add(foundDog);
                dogScanListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBeaconTelemetry(String deviceAddress, float battery, float temperature) {

    }


    private ServiceConnection mBeaconConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "Connected to scanner service");
            mBeaconService = ((BeaconScannerService.LocalBinder)iBinder).getService();
            mBeaconService.setBeaconEventListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Disconnected from scanner service");
            mBeaconService = null;
        }
    };




    private class AsyncToolBarActivityStart extends AsyncTask<Void, Void, Intent>{
    private String collarId;
    private AsyncToolBarActivityStart(String collarId) {
        this.collarId = collarId;
    }

    @Override
    protected Intent doInBackground(Void... voids) {
        Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
        intent.putExtra("TAG", "AlgorithmFragment");
        intent.putExtra("fragmentToOpen", "0");
        intent.putExtra("DOG_ID", collarId);
        intent.putExtra("currentLocation", userCurrentLocation);
        return intent;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);
        Toast.makeText(getBaseContext(), "on post execute", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}

}
