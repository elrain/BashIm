package com.elrain.bashim.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class BashPreferences {

    private static final String BASH_SHARED_PREF = "BashSharedPref";
    private static final String KEY_FIRST_START = "firstStart";
    private static BashPreferences mInstance;
    private static SharedPreferences mPreferences;

    private BashPreferences(Context context) {
        mPreferences = context.getSharedPreferences(BASH_SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static BashPreferences getInstance(Context context) {
        if (null == mInstance)
            mInstance = new BashPreferences(context);
        return mInstance;
    }

    public boolean isFirstStart() {
        boolean result = mPreferences.getBoolean(KEY_FIRST_START, true);
        mPreferences.edit().putBoolean(KEY_FIRST_START, false).apply();
        return result;
    }

}
