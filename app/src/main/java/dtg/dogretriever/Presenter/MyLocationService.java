package dtg.dogretriever.Presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


public class MyLocationService extends Service implements LocationListener {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private final IBinder binder = new MylocalBinder();
    private LocationListener mLocationListener;
    private Location userCurrentLocation = new Location("");
    private LocationManager locationManager;
    private boolean askedForPremmisions = false;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private boolean isFirstTimeRuning = true;

    public MyLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

       // Toast.makeText(this, "new Service Created", Toast.LENGTH_SHORT).show();



        //mLocationListener.locationChanged(userCurrentLocation);


    }

    public Boolean isFirstTimeRuning(){
        return isFirstTimeRuning;
    }

    @Override
    public void onLocationChanged(Location location) {
        isFirstTimeRuning = false;

        if (isBetterLocation(location, userCurrentLocation)) {
            userCurrentLocation = location;
            mLocationListener.locationChanged(userCurrentLocation);

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    public class MylocalBinder extends Binder {
        MyLocationService getMyLocationService(){
            return MyLocationService.this;
        }

        void registerLocationListener(LocationListener listener) {
            mLocationListener = listener;
            while(ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyLocationService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (!askedForPremmisions) {
                        ActivityCompat.requestPermissions((Activity) listener,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                100);
                        askedForPremmisions = true;


                    }
                //return;
                }
            }



            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }
            if (!gps_enabled && !network_enabled) {
                //User game permission but gps and netword are disabled
                //close the app
                System.exit(1);
                return;
            }
            if (gps_enabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, MyLocationService.this);
            }

            if(network_enabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, MyLocationService.this);
            }

            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
            userCurrentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(crit, true));
            if(userCurrentLocation!=null)
                if(userCurrentLocation.getLongitude()!=0 && userCurrentLocation.getLatitude()!=0)
                    mLocationListener.locationChanged(userCurrentLocation);


        }

        void DeleteLocationListener(LocationListener listener){
            locationManager.removeUpdates(MyLocationService.this);
            mLocationListener = null;
        }


    }

    public interface LocationListener{
        void locationChanged(Location location);
    }


}
/*


  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                    return;
                } else {
                    // permission denied, boo!
                    //closing app
                    System.exit(0);
                }
                break;

        }
 */