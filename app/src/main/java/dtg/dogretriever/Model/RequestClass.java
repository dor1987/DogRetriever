package dtg.dogretriever.Model;

import java.util.ArrayList;
import java.util.Map;

import dtg.dogretriever.Model.Point;

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

}
