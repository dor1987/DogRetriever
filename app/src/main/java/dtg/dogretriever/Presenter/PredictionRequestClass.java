package dtg.dogretriever.Presenter;

import java.util.ArrayList;
import dtg.dogretriever.Presenter.LearningAlgoTemp.Point;

public class PredictionRequestClass {
    ArrayList<Point> pointsList;
    String weather;

    public PredictionRequestClass() {
    }

    public PredictionRequestClass(ArrayList<Point> pointsList, String weather) {
        this.pointsList = pointsList;
        this.weather = weather;
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
}
