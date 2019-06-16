package dtg.dogretriever.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class Coordinate implements Parcelable {
    //LatLng location;
    double latitude;
    double longitude;




    public Coordinate(){
        //an empty constructor must be define for Firebase
    }

    public Coordinate(Double latitude , Double longitude) {
        this.latitude =latitude;
        this.longitude =longitude;

    }


    protected Coordinate(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel in) {
            return new Coordinate(in);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude, double longitude) {
       this.latitude = latitude;
       this.longitude = longitude;
    }


    @Override
    public String toString() {
        return latitude + "," + longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
