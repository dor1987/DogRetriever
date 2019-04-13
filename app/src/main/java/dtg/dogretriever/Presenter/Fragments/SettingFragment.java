package dtg.dogretriever.Presenter.Fragments;


import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import dtg.dogretriever.R;


public class SettingFragment extends PreferenceFragmentCompat{


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);

    }
*/
}
