package dtg.dogretriever.Model;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class Weather {
    private static final String TAG = "Weather";
    private static final String API = "https://api.forecast.io/forecast/dc740664c686a65b34462152a32c898c/%s";


    public enum weather {UNKNOWN,SNOWY,COLD,WARM,HOT,VERY_HOT}

    private weather currentWeather;

    public Weather() { }

    public Weather(final String coord)  {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    renderTemperature(getJSON(coord));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
            thread.start();


    }


    public static JSONObject getJSON(String coord) throws IOException {
        //get json from weather API

        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(String.format((API), coord));

            connection =(HttpsURLConnection)url.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            Log.i(TAG, "connection status: "+ connection.getResponseMessage());
            reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));

            StringBuilder json = new StringBuilder(1024);
            String tmp;

            while((tmp=reader.readLine())!=null) {
                json.append(tmp).append("\n");
            }

            JSONObject data = new JSONObject(json.toString());

            return data;
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }finally {
            if(reader != null)
            reader.close();
            if(connection != null)
            connection.disconnect();
        }
    }

    private void renderTemperature(JSONObject json){

        double temperatureF = 0;
        try{

         String temperature = json.getJSONObject("currently").getString("temperature");
            temperatureF = Double.valueOf(temperature);
            setCurrentWeather(fahrenheitToCelsius(temperatureF));

        }catch(Exception e){
            currentWeather = weather.UNKNOWN;
            Log.e(TAG, "One or more fields not found in the JSON data");
        }



    }


    private double fahrenheitToCelsius(double fDegree){

        return  (( 5 *(fDegree - 32.0)) / 9.0);

    }

    public void setCurrentWeather(Double temperature) {
        if (temperature <= 0 )
            currentWeather = weather.SNOWY;
        else if (temperature > 0 && temperature <= 20)
            currentWeather = weather.COLD;
        else if (temperature > 20 && temperature <= 30)
            currentWeather = weather.WARM;
        else if (temperature > 30 && temperature < 35)
            currentWeather = weather.HOT;
        else
            currentWeather = weather.VERY_HOT;


    }

    public weather getCurrentWeather() {

        return currentWeather;
    }
}
