package dtg.dogretriever.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.MapView;

import java.util.zip.Inflater;

import dtg.dogretriever.R;


public class AlgorithmFragment extends Fragment {

    private enum algoType {PREDICTION, LEARNING}
    private algoType currentAlgoShown = algoType.PREDICTION;
    private Button predictAlgoBtn;
    private Button learningAlgoBtn;
    private MapView mapView;

    private OnFragmentInteractionListener mListener;

    public AlgorithmFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AlgorithmFragment newInstance(String param1, String param2) {
        AlgorithmFragment fragment = new AlgorithmFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_algorithm, container, false);

        predictAlgoBtn = view.findViewById(R.id.predictionAlgo_btn);
        learningAlgoBtn = view.findViewById(R.id.learningAlgo_btn);

        mapView = view.findViewById(R.id.mapView);

        predictAlgoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPredictAlgoON();

            }
        });


        learningAlgoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLearningAlgoON();

            }
        });


        return view;
    }

    private void setPredictAlgoON(){
        predictAlgoBtn.setBackgroundResource(R.color.dark_grey);
        learningAlgoBtn.setBackgroundResource(R.color.light_grey);
        currentAlgoShown = algoType.PREDICTION;
    }

    private void setLearningAlgoON(){
        predictAlgoBtn.setBackgroundResource(R.color.dark_grey);
        learningAlgoBtn.setBackgroundResource(R.color.light_grey);
        currentAlgoShown = algoType.LEARNING;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
