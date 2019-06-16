package dtg.dogretriever.Controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import dtg.dogretriever.Model.Coordinate;
import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.Model.Scan;
import dtg.dogretriever.R;
import dtg.dogretriever.View.DogNamesAdapter;
import dtg.dogretriever.View.DogScanListAdapter;

import static dtg.dogretriever.Controller.MyMessagingService.SHARED_PREFS;

public class MainActivity extends AppCompatActivity implements MyLocationService.LocationListener,
        DogScanListFunctionalityInterface, BeaconScannerService.OnBeaconEventListener {
    private static final String TAG = "MainActivity";

    private View mSmallProgressBarView;
   // private View mProgressView, mSmallProgressBarView;
    //private View mMainMenuFormView;
    private TextView userWelcomeTextView;
    private CircleImageView profilePicView;

    private PopupWindow popupWindow = null;
    private PopupWindow fakeScanPopUp = null;
    private PopupWindow notificationPopUp = null;
    private PopupWindow ownerInfoPopUp = null;

    private EditText dogIdFromFakeScanTextView;

    private PopupWindow scanPopUp = null;
    private RecyclerView.Adapter dogScanListAdapter;
    private ArrayList<Dog> listOfDogScanned;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TextView ownerPhoneTextView;

    //Location Service
    boolean isBound = false;
    private MyLocationService.MylocalBinder mBinder;
    private Location userCurrentLocation;
    private MyLocationService myLocationService;

    //notification pop up
    private Double latitude;
    private Double longitude;
    private boolean isNotifcationPopShowenBefore;
    private Bundle bundle;

    //Bluetooth
    private BeaconScannerService mBeaconService;
    private boolean isBoundBeaconService = false;

    //firebase

    private FirebaseAdapter firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        //mProgressView = findViewById(R.id.loading_progress);
        mSmallProgressBarView = findViewById(R.id.main_menu_small_progres_bar);
        //mMainMenuFormView = findViewById(R.id.mainMenuForm);
        userWelcomeTextView = findViewById(R.id.userWelcome);
        profilePicView = findViewById(R.id.profile_image);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
        initWelcomeTextview();
        setTokenListener();
        userCurrentLocation = new Location("");
        //start the service
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        isNotifcationPopShowenBefore = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start the service
        Intent intent = new Intent(this, MyLocationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        showSmallProgressBar(false);
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
        createPopUpScan();
        //scanDogForDebug(view);
    }

    public void initWelcomeTextview() {
        if (firebaseAdapter.isUserConnected()) {
            if (firebaseAdapter.isUserDataReadyNow()) {
                userWelcomeTextView.setText(getString(R.string.hello) + " " + firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                initProfilePic();
            } else {
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        firebaseAdapter.removeProfileDataListener();
                        userWelcomeTextView.setText(getString(R.string.hello) + " " + firebaseAdapter.getCurrentUserProfileFromFireBase().getFullName());
                        initProfilePic();
                    }
                });
            }
        } else {
            userWelcomeTextView.setText("Hello Guest");
        }
    }

    public void clickFindMyDog(View view) {
        if (firebaseAdapter.isUserConnected()) {

            if (firebaseAdapter.isUserDataReadyNow()) {
                createPopUpChooseDogName();
            } else {
                showSmallProgressBar(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showSmallProgressBar(false);
                        createPopUpChooseDogName();
                        firebaseAdapter.removeProfileDataListener();
                    }
                });
            }
        } else {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
            showSmallProgressBar(false);
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
                showSmallProgressBar(true);
                firebaseAdapter.registerProfileDataListener(new FirebaseAdapter.ProfileDataListener() {
                    @Override
                    public void onDataReady() {
                        showSmallProgressBar(false);
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
        TextView errorMessage = layout.findViewById(R.id.popup_layout_errorMessage);

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(createDogsList(), this);
        listView.setAdapter(dogNamesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                showSmallProgressBar(true);
                popupWindow.dismiss();
                AsyncToolBarActivityStart asyncToolBarActivityStart = new AsyncToolBarActivityStart(createDogsList().get(i).getCollarId());
                asyncToolBarActivityStart.execute();
            }
        });

        if (dogNamesAdapter.getCount() == 0) {
            errorMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        popupWindow = new PopupWindow(this);
        initPopUpGraphics(layout,popupWindow);

        /*
        popupWindow.setContentView(layout);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 1, 1);
        */
    }


    private ArrayList<Dog> createDogsList() {
        firebaseAdapter.getCurrentUserProfileFromFireBase();
        return firebaseAdapter.getListOfDogsOwnedByCurrentUser();
    }


    public void cancelPopUp(View view) {
        popupWindow.dismiss();
    }
/*
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
*/

    private void startNotificationPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.notification_pop_up, null);
        notificationPopUp = new PopupWindow(this);
        initPopUpGraphics(layout,notificationPopUp);
        /*
        notificationPopUp.setContentView(layout);
        notificationPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        notificationPopUp.setClippingEnabled(true);
        notificationPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        notificationPopUp.setFocusable(true);
        notificationPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
        */
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

    public void cancelScanPopUp(View view) {
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
            startActivityForResult(enableBtIntent,0);

            return false;
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
           createPopUpScan();
        }else{
            // show error
        }
    }

    private void createPopUpScan() {
        if(checkBluetoothStatus()) {
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
            initPopUpGraphics(layout,scanPopUp);
            scanPopUp.setOutsideTouchable(false);

            /*
            scanPopUp.setContentView(layout);
            scanPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            scanPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            scanPopUp.setClippingEnabled(true);
            scanPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            scanPopUp.setFocusable(true);
            scanPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
            */

            Log.i(TAG, "Start scanning...");
            bindToServiceScan();

        }
    }

    private void bindToServiceScan(){
        final Intent intent = new Intent(this, BeaconScannerService.class);
        isBoundBeaconService =  getApplicationContext().bindService(intent, mBeaconConnection, BIND_AUTO_CREATE);
    }
    public void scanRefresh(View view) {
        listOfDogScanned.clear();
        dogScanListAdapter.notifyDataSetChanged();

        if(isBoundBeaconService){
            getApplicationContext().unbindService(mBeaconConnection);
            bindToServiceScan();
        }

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

        initPopUpGraphics(layout,ownerInfoPopUp);
        /*
        ownerInfoPopUp.setContentView(layout);
        ownerInfoPopUp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ownerInfoPopUp.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ownerInfoPopUp.setClippingEnabled(true);
        ownerInfoPopUp.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        ownerInfoPopUp.setFocusable(true);
        ownerInfoPopUp.showAtLocation(layout, Gravity.CENTER, 1, 1);
        */

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

    @Override
    public void locationChanged(Location location) {
        //showProgress(false);
        userCurrentLocation = location;
      //  if(mProgressView.getVisibility()!=View.VISIBLE) {
        if (!isNotifcationPopShowenBefore)
            showNotificationPop();
        //}
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

    public void initPopUpGraphics(View layout, PopupWindow window){
        window.setContentView(layout);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setClippingEnabled(true);
        window.setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        window.setFocusable(true);
        window.showAtLocation(layout, Gravity.CENTER, 1, 1);
    }

    public void showSmallProgressBar(final Boolean toShow){

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

//Debug Functions Start
    public LatLng getRandomLocation(LatLng point, int radius) {
    //For debug
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
        tempDog = firebaseAdapter.getDogByCollarIdFromFireBase("5556");



        if (tempDog != null) {
            final Dog finalTempDog = tempDog;
            new Thread(new Runnable() {
                public void run(){
                    for (int i = 0; i < 1; i++) {
                        LatLng locationToReturn = getRandomLocation((new LatLng(32.113885, 34.818631)), 10);
                        try {
                            Scan tempScan = new Scan(new Coordinate(locationToReturn.latitude, locationToReturn.longitude), new Date(generateDate()));
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

    public long generateDate(){
        //for debug
        SimpleDateFormat dfDateTime  = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        int year = randBetween(2019, 2019);// Here you can set Range of years you need
        int month = randBetween(5, 6);
        int hour = randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);


        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));

        gc.set(year, month, day, hour, min,sec);

        System.out.println(dfDateTime.format(gc.getTime()));
        return gc.getTimeInMillis();
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

//Debug Functions End

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
        startActivity(intent);
    }
}

}
