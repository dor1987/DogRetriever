package dtg.dogretriever.Presenter.LearningAlgoTemp;


import java.util.Date;


public class Point {

    private int clusterId;
    private Double mLatitude;
    private Double mLongitude;
    private String weather;
    private long timeStamp;

    public Point(int clusterId, double mLatitude, double mLongitude, String weather,long timeStamp) {
        this.clusterId = clusterId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.weather = weather;
        this.timeStamp = timeStamp;

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
