package com.example.cse110.teamproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.ListPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        final String LIST_PREF_KEY = "dir_mode";
        final String SHARED_PREF_KEY = "shared_dir_mode";
        SharedPreferences.Editor editor;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // find our list preference
            ListPreference listPreference = findPreference(LIST_PREF_KEY);

            // SharedPreferences is used as a global variable as a flag to let the program know
            // which direction mode is currently selected
            SharedPreferences preferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
            editor = preferences.edit();

//            editor.putString(SHARED_PREF_KEY,"brief_dir");
//            editor.apply();

            // listen to change in list preference
            preferenceChangeListener(listPreference);
        }

        private void preferenceChangeListener(ListPreference listPreference) {
            listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // newValue gives "detailed_dir" when user clicks on "Detailed Direction"
                // or "brief_dir" when user clicks on "Brief Direction"

                // place holder
                Log.d("CHANGED TO", newValue.toString());

                // preferences is set to "detailed_dir when user clicks on "Detailed Direction"
                // or "brief_dir" when user clicks on "Brief Direction"
                editor.putString(SHARED_PREF_KEY, newValue.toString());
                editor.apply();

                return true;
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}