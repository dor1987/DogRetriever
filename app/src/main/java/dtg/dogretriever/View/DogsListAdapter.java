package dtg.dogretriever.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.R;

public class DogsListAdapter extends BaseAdapter {
    private ArrayList<Dog> dogsList;
    private ImageView deleteMeal;
    private Context context;
    private LayoutInflater inflater;


    public DogsListAdapter(ArrayList<Dog> dogs, Context context){
        this.dogsList = dogs;
        this.context = context;

    }

    @Override
    public int getCount() {
        return dogsList.size();
    }

    @Override
    public Object getItem(int i) {
        return dogsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        return null;
    }
}
