package dtg.dogretriever.Controller.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Controller.MainActivity;
import dtg.dogretriever.R;

import static dtg.dogretriever.Controller.MyMessagingService.SHARED_PREFS;


public class SettingFragment extends PreferenceFragmentCompat{

    private Preference logoutBtn;
    private FirebaseAdapter firebaseAdapter;
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        logoutBtn = findPreference(getString(R.string.log_out));
        if(firebaseAdapter.isUserConnected())
            logoutBtn.setVisible(true);
        else
            logoutBtn.setVisible(false);

        logoutBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                firebaseAdapter.logOut();
                SharedPreferences sh = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                if(sh != null)
                    sh.edit().clear().apply();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

                
                return true;
            }
        });


    }
}
