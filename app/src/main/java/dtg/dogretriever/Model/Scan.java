package dtg.dogretriever.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scan {
    private final int NEARBY_PLACES_RADIUS = 30;

    private Coordinate coordinate;
    private Date timeStamp;
    private Weather.weather currentWeather;
    private ArrayList<String> places;

    public Scan() {}

    public Scan(Coordinate coordinate) throws IOException {
        this.coordinate = coordinate;
        setTimeStamp();
        Weather weather = new Weather(coordinate.toString());
        setCurrentWeather(weather.getCurrentWeather());

        Place place  = new Place(coordinate.toString());
        if(place.getPlaceType()!=null)
            setPlaces(new ArrayList<>(place.getPlaceType()));

    }

    public Scan(Coordinate coordinate, Date timeStamp) throws IOException {
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



/*
    public void getPlaceType(double latitide, double longitude){
        //Getting Lat Long and get type list from google api
        Object transferData[] = new Object[1];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();

        String url = getUrl(latitide, longitude,"30");
        transferData[0] = url;
        getNearbyPlaces.execute(transferData);
    }
*/
//    public void fakeGetNearbyPlaces(double latitide, double longitude){
//        //just for debugging
//        //generate random array of places to simulate google response
//        ArrayList<String> fakePlacesResult = new ArrayList<>();
//        ArrayList<String> fakePlaceArrayListDataBase = new ArrayList<>(
//                Arrays.asList(
//        "accounting","airport","amusement_park","aquarium","art_gallery",
//                "atm","bakery","bank","bar","beauty_salon","bicycle_store","book_store",
//                        "bowling_alley","bus_station","cafe","campground","car_dealer","car_rental",
//                        "car_repair","car_wash","casino","cemetery","church","city_hall",
//                        "clothing_store","convenience_store","courthouse","dentist",
//                        "department_store","doctor","electrician","electronics_store",
//                        "embassy","fire_station","florist","funeral_home",
//                        "furniture_store","gas_station","gym","hair_care",
//                        "hardware_store","hindu_temple","home_goods_store","hospital",
//                        "insurance_agency","jewelry_store","laundry",
//                        "lawyer","library","liquor_store","local_government_office","locksmith",
//                        "lodging","meal_delivery","meal_takeaway","mosque","movie_rental",
//                        "movie_theater","moving_company","museum","night_club","painter",
//                        "park","parking","pet_store","pharmacy","physiotherapist",
//                        "plumber","police","post_office","real_estate_agency","restaurant",
//                        "roofing_contractor","rv_park","school","shoe_store","shopping_mall",
//                        "spa","stadium","storage","store","subway_station",
//                        "supermarket","synagogue","taxi_stand","train_station","transit_station",
//                        "travel_agency","veterinary_care","zoo"));
//
//        Random random = new Random();
//        int n = random.nextInt(15);
//
//        for(int i = 0; i < n ; i++){
//            fakePlacesResult.add(fakePlaceArrayListDataBase.get(random.nextInt(fakePlaceArrayListDataBase.size())));
//        }
//    places = fakePlacesResult;
//    }
//    private String getUrl(double latitide, double longitude, String ProximityRadius)
//    {
//        //Assist function for "getPlaceType", building Url to fit the Search
//        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googleURL.append("location=" + latitide + "," + longitude);
//        googleURL.append("&radius=" + ProximityRadius);
//        //googleURL.append("&type=" + nearbyPlace); not a specific type
//        googleURL.append("&sensor=true"); //not a must
//        googleURL.append("&key=" + "AIzaSyDTDmMNFTekqcFEWeK9yAJYzxdaM-IU8Wk");
//
//        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());
//
//        return googleURL.toString();
//    }

//    @SuppressLint("StaticFieldLeak")
//    public void getNearbyPlaces(double latitide, double longitude) {
//        Object transferData[] = new Object[1];
//        //GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
//
//        String url = getUrl(latitide, longitude,NEARBY_PLACES_RADIUS+"");
//        transferData[0] = url;
//        //getNearbyPlaces.execute(transferData);
//
//
//        new AsyncTask<Object, String, String>() {
//            private String googleplaceData, url;
//
//            @Override
//            protected String doInBackground(Object... objects) {
//                url = (String) objects[0];
//
//                DownloadUrl downloadUrl = new DownloadUrl();
//                try {
//                    googleplaceData = downloadUrl.ReadTheURL(url);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                return googleplaceData;
//            }
//
//
//            @Override
//            protected void onPostExecute(String s) {
//                List<String> nearByPlacesList = null;
//                DataParser dataParser = new DataParser();
//                nearByPlacesList = dataParser.parse(s);
//                places.addAll(nearByPlacesList);
//            }
//
//        }.execute(transferData);
//    }
}
