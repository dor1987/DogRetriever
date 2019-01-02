package dtg.dogretriever.Model;

import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Scan {


    private Coordinate coordinate;
    private Date timeStamp;
    private Float errorApproximation;
    private Weather.weather currentWeather;

    public Scan() {}

    public Scan(Coordinate coordinate) throws IOException {
        this.coordinate = coordinate;
        setTimeStamp();
        this.errorApproximation = errorApproximation;
        Weather weather = new Weather(coordinate.toString());
        setCurrentWeather(weather.getCurrentWeather());
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = Calendar.getInstance().getTime();
    }

    public Float getErrorApproximation() {
        return errorApproximation;
    }

    public void setErrorApproximation(Float errorApproximation) {
        this.errorApproximation = errorApproximation;
    }

    public Weather.weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather.weather currentWeather) {
        this.currentWeather = currentWeather;
    }


}
