package dtg.dogretriever.Presenter.Fragments;


import android.os.Bundle;
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
}
