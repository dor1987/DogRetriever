package dtg.dogretriever.Presenter;

import java.util.ArrayList;
import java.util.Map;

import dtg.dogretriever.Presenter.LearningAlgoTemp.Point;

public class RequestClass {
    ArrayList<Point> pointsList;
    String weather;
    Map<String,Integer> placesHistogram;

    public RequestClass() {
    }

    public RequestClass(ArrayList<Point> pointsList, String weather,Map<String,Integer> placesHistogram) {
        this.pointsList = pointsList;
        this.weather = weather;
        this.placesHistogram = placesHistogram;

    }

    public ArrayList<Point> getPointsList() {
        return pointsList;
    }

    public void setPointsList(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
    }
    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Map<String, Integer> getPlacesHistogram() {
        return placesHistogram;
    }

    public void setPlacesHistogram(Map<String, Integer> placesHistogram) {
        this.placesHistogram = placesHistogram;
    }
    /*
    String firstName;
    String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RequestClass(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public RequestClass() {
    }

    */
}
