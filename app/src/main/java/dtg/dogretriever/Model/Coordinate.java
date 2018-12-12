package dtg.dogretriever.Model;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class Coordinate {
    LatLng location;
    Date timeStamp;
    Float errorApproximation;

    public Coordinate(LatLng location, Date timeStamp, Float errorApproximation) {
        this.location = location;
        this.timeStamp = timeStamp;
        this.errorApproximation = errorApproximation;
    }

    public Coordinate(LatLng location, Date timeStamp) {
        this.location = location;
        this.timeStamp = timeStamp;
    }


    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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
}
