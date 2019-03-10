package dtg.dogretriever.Presenter.GooglePlaces;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private ArrayList<String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        //HashMap<String, String> googlePlaceMap = new HashMap<>();

        ArrayList<String> types = new ArrayList<>();


            if (!googlePlaceJSON.isNull("types"))
            {
                try {
                    JSONArray jr = googlePlaceJSON.getJSONArray("types");
                    JSONObject jb = (JSONObject)jr.getJSONObject(0);
                    JSONArray st = jb.getJSONArray("types");
                    for(int i=0;i<st.length();i++)
                    {
                        String type = st.getString(i);
                        types.add(type);
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

        return types;
    }



    private List<String> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        ArrayList<String> NearbyPlacesList = new ArrayList<>();

        //ArrayList<String> NearbyPlaceList = null;

        for (int i=0; i<counter; i++)
        {
            try
            {
                NearbyPlacesList.addAll(getSingleNearbyPlace( (JSONObject) jsonArray.get(i) ));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
    }



    public List<String> parse(String jSONdata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
