package dtg.dogretriever.Presenter.LearningAlgoTemp;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Point {

    private int clusterId;
    private Double mLatitude;
    private Double mLongitude;


    public Point(int clusterId, double mLatitude, double mLongitude) {
        this.clusterId = clusterId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }
    public Point() {
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    /*
    private int clusterId;
    private Double mLatitude;
    private Double mLongitude;


    public Point(int clusterId, double mLatitude, double mLongitude) {
        this.clusterId = clusterId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }
    */
}
