package dtg.dogretriever.Presenter.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Presenter.MainActivity;
import dtg.dogretriever.R;

import static dtg.dogretriever.Presenter.MyMessagingService.SHARED_PREFS;


public class SettingFragment extends PreferenceFragmentCompat{

    private Preference logoutBtn;
    //private FirebaseAuth mAuth;
    private FirebaseAdapter firebaseAdapter;
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

        //mAuth = FirebaseAuth.getInstance();
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

//                mAuth.signOut();
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                SharedPreferences sh = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//
//                if(sh != null) {
//                    sh.edit().clear().apply();
//                }
//                startActivity(intent);
                
                return true;
            }
        });


    }
}
