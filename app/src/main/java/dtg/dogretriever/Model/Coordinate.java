package dtg.dogretriever.Model;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class Coordinate {
    //LatLng location;
    double latitude;
    double longitude;

    Date timeStamp;
    Float errorApproximation;

    public Coordinate(){
        //an empty constructor must be define for Firebase
    }

    public Coordinate(Double latitude , Double longitude, Date timeStamp, Float errorApproximation) {
       // this.location = location;
        this.latitude =latitude;
        this.longitude =longitude;
        this.timeStamp = timeStamp;
        this.errorApproximation = errorApproximation;
    }

    public Coordinate(Double latitude , Double longitude, Date timeStamp) {
       // this.location = location;
        this.latitude =latitude;
        this.longitude =longitude;
        this.timeStamp = timeStamp;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Float getErrorApproximation() {
        return errorApproximation;
    }

    public void setErrorApproximation(Float errorApproximation) {
        this.errorApproximation = errorApproximation;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
}
