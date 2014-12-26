package com.android.jota.eurofootball;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {

    public static String getPreferredLeague(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.pref_league_key),
                context.getString(R.string.pref_league_default));
    }

}
