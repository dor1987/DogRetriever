package dtg.dogretriever.Presenter.LearningAlgoTemp;


import java.util.ArrayList;
import java.util.Date;

public class Point {

    private int clusterId;
    private Double mLatitude;
    private Double mLongitude;
    private String weather;
    private long timeStamp;
    private ArrayList<String> places;

    public Point(int clusterId, double mLatitude, double mLongitude, String weather,long timeStamp,ArrayList<String> places) {
        this.clusterId = clusterId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.weather = weather;
        this.timeStamp = timeStamp;
        this.places = places;

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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public ArrayList<String> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<String> places) {
        this.places = places;
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
