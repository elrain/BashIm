package com.elrain.bashim.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.elrain.bashim.R;
import com.elrain.bashim.util.Constants;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(R.string.activity_preferences);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().add(R.id.flSettings, new PreferencesFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                PreferencesActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class PreferencesFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference mLpUpdateFrequency;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            mLpUpdateFrequency = (ListPreference) findPreference(
                    getActivity().getString(R.string.preferences_key_alarm_frequency));
            String summary = getPreferenceManager().getSharedPreferences().getString(
                    getActivity().getString(R.string.preferences_key_alarm_frequency),
                    Constants.PREFERENCES_UPDATE_DEF_VALUE);
            mLpUpdateFrequency.setSummary(generateFrequencySummary(summary));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (getActivity().getString(R.string.preferences_key_alarm_frequency).equals(key)) {
                mLpUpdateFrequency.setSummary(generateFrequencySummary(
                        sharedPreferences.getString(key, Constants.PREFERENCES_UPDATE_DEF_VALUE)));
            }
        }

        private String generateFrequencySummary(String value) {
            String[] possibleValues = getActivity().getResources().getStringArray(R.array.listValues);
            String[] resultValues = getActivity().getResources().getStringArray(R.array.listArray);
            for (int index = 0; index < possibleValues.length; ++index) {
                if (possibleValues[index].equals(value))
                    return resultValues[index];
            }
            return resultValues[1];
        }
    }
}
