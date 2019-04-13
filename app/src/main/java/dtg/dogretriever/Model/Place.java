package dtg.dogretriever.Model;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

public class Place {
    private final int MAX_RADIUS = 100;
    private final int LIMIT = 10;
    private final static String TAG = "Place";
    private final String CLIENT_ID = "DYSSILD5DEHVLDFII0BELZ4QHOTS4PEEPNAP5UXEZTTTRC3F";
    private final String CLIENT_SECRET = "HRXKJAWTGVUP130AP5Y1G1IA1J35NOSGDVFGKLODTFMGXUWA";
    private final String API = "https://api.foursquare.com/v2/venues/search?client_id=";

    private HashSet<String> placeType;

    public Place(final String coord) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getPlaceDetailFromJson(getJsonFromFourquare(coord));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonFromFourquare(String coord){
        BufferedReader reader = null;

        HttpsURLConnection httpsURLConnection=null;
        String url = API + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
                + "&v=20130815&intent=checkin&ll=" + coord + "&radius=" + MAX_RADIUS + "&limit=" + LIMIT;
        try {
            URL urlConnection = new URL(url);

            // instanciate an HttpClient
            httpsURLConnection = (HttpsURLConnection)urlConnection.openConnection();
            // instanciate an HttpGet
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setConnectTimeout(10000);


            Log.i(TAG, "connection status: "+ httpsURLConnection.getResponseMessage());
            reader = new BufferedReader( new InputStreamReader(httpsURLConnection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;

            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }

            JSONObject data = new JSONObject(json.toString());

            return data;
        } catch (MalformedURLException e ) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpsURLConnection.disconnect();
        }
}

    private void getPlaceDetailFromJson(JSONObject json){

        HashSet<String> venuesType = new HashSet<>();

        try{
            JSONArray venues =json.getJSONObject("response").getJSONArray("venues");

            for (int i=0 ; i< venues.length(); i++) {
                if (json.getJSONObject("response").getJSONArray("venues").getJSONObject(i) != null) {
                        JSONArray jsonArray = json.getJSONObject("response").getJSONArray("venues").getJSONObject(i).
                                getJSONArray("categories");
                        if (!jsonArray.isNull(0)) {
                            String place = jsonArray.getJSONObject(0).
                                    getString("shortName");
                            venuesType.add(place);
                            Log.d(TAG, "place type found : " + place);

                    }


                }
            }

            this.placeType = venuesType;


        }catch(Exception e){

            Log.e(TAG, "One or more fields not found in the JSON data");
        }



    }

    public HashSet<String> getPlaceType() {
        return placeType;
    }



}

