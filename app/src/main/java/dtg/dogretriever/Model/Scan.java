package dtg.dogretriever.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scan {
    private Coordinate coordinate;
    private Date timeStamp;
    private Weather.weather currentWeather;
    private ArrayList<String> places;

    public Scan() {}

    public Scan(Coordinate coordinate){
        this.coordinate = coordinate;
        setTimeStamp();
        Weather weather = new Weather(coordinate.toString());
        setCurrentWeather(weather.getCurrentWeather());

        Place place  = new Place(coordinate.toString());
        if(place.getPlaceType()!=null)
            setPlaces(new ArrayList<>(place.getPlaceType()));
    }

    public Scan(Coordinate coordinate, Date timeStamp){
        this.coordinate = coordinate;
        this.timeStamp = timeStamp;
        Weather weather = new Weather(coordinate.toString());
        setCurrentWeather(weather.getCurrentWeather());

        Place place  = new Place(coordinate.toString());
        if(place.getPlaceType()!=null)
            setPlaces(new ArrayList<>(place.getPlaceType()));
    }


    public ArrayList<String> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<String> places) {
        this.places = places;
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

    public Weather.weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather.weather currentWeather) {
        this.currentWeather = currentWeather;
    }
}
