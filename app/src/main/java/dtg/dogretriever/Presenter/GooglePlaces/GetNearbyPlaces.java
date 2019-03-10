package dtg.dogretriever.Presenter.GooglePlaces;
import android.os.AsyncTask;



import java.io.IOException;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private String googleplaceData, url;

    @Override
    protected String doInBackground(Object... objects)
    {
        url = (String) objects[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try
        {
            googleplaceData = downloadUrl.ReadTheURL(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return googleplaceData;
    }


    @Override
    protected void onPostExecute(String s)
    {
        List<String> nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse(s);
    }



}
