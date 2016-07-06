package com.jprarama.newsfeedapp.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jprarama.newsfeedapp.R;

/**
 * Created by joshua on 6/7/16.
 */
public class SettingsFragment extends PreferenceFragment {

    private EditTextPreference prefQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        prefQuery = (EditTextPreference) findPreference(getString(R.string.pref_feed_query_key));
        prefQuery.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                updateQuery(o.toString());
                return true;
            }
        });
        updateQuery(prefQuery.getText());
    }

    private void updateQuery(String value) {
        if (value == null || value.length() == 0) {
            prefQuery.setSummary(getString(R.string.pref_feed_query_summary));
            return;
        }

        prefQuery.setText(value);
        prefQuery.setSummary(value);
    }

    
}
