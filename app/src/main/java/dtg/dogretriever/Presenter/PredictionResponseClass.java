package dtg.dogretriever.Presenter;

import java.util.ArrayList;

import dtg.dogretriever.Presenter.LearningAlgoTemp.Cluster;

public class PredictionResponseClass {

    Double latitude;
    Double longitude;


    public PredictionResponseClass() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

/*
    String greetings;

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(String greetings) {
        this.greetings = greetings;
    }

    public ResponseClass(String greetings) {
        this.greetings = greetings;
    }

    public ResponseClass() {
    }
    */
}
