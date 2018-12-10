package dtg.dogretriever.View;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dtg.dogretriever.Model.Dog;
import dtg.dogretriever.R;

public class DogNamesAdapter extends BaseAdapter {
    ArrayList<Dog> dogsList;
    private LayoutInflater inflater;
    private Context context;

    public DogNamesAdapter(ArrayList<Dog> dogsList, Context context){
        this.dogsList = dogsList;
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.dogs_name_item,
                viewGroup, false);

        TextView dogName = view.findViewById(R.id.dog_name_text);
        dogName.setText(dogsList.get(i).getName());

        return view;
    }

}
