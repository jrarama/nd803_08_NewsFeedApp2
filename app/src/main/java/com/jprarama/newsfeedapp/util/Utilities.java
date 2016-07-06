package com.jprarama.newsfeedapp.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by joshua on 6/7/16.
 */
public class Utilities {

    public static String getPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }
}
