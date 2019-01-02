package dtg.dogretriever.Model;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class Coordinate {
    //LatLng location;
    double latitude;
    double longitude;




    public Coordinate(){
        //an empty constructor must be define for Firebase
    }

    public Coordinate(Double latitude , Double longitude) {
       // this.location = location;
        this.latitude =latitude;
        this.longitude =longitude;

    }



/*
    public LatLng getLocation() {
        return new LatLng(latitude,longitude);
    //    return location;
    }
*/


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude, double longitude) {
       this.latitude = latitude;
       this.longitude = longitude;
        //this.location = location;
    }

/*
    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
*/

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
