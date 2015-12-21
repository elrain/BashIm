package com.elrain.bashim.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class BashPreferences {

    private static final String BASH_SHARED_PREF = "BashSharedPref";
    private static final String KEY_FIRST_START = "firstStart";
    private static final String KEY_LAST_TAG = "lastTag";
    private static final String KEY_NEW_QUOTES_COUNTER_TAG = "newQuotes";
    public static final String KEY_SEARCH_FILTER = "searchFilter";
    private static BashPreferences mInstance;
    private static SharedPreferences mPreferences;

    private BashPreferences(Context context) {
        mPreferences = context.getSharedPreferences(BASH_SHARED_PREF, Context.MODE_PRIVATE);
    }

    public static BashPreferences getInstance(@NonNull Context context) {
        if (null == mInstance) mInstance = new BashPreferences(context);
        return mInstance;
    }

    public boolean isFirstStart() {
        boolean result = mPreferences.getBoolean(KEY_FIRST_START, true);
        if (result) {
            if (getQuotesCounter() != 0)
                resetQuotesCounter();
            mPreferences.edit().putBoolean(KEY_FIRST_START, false).apply();
        }
        return result;
    }

    public String getLastTag() {
        return mPreferences.getString(KEY_LAST_TAG, null);
    }

    public void setLastTag(String lastTag) {
        mPreferences.edit().putString(KEY_LAST_TAG, lastTag).apply();
    }

    public int getQuotesCounter() {
        return mPreferences.getInt(KEY_NEW_QUOTES_COUNTER_TAG, 0);
    }

    public void addIncreaseQuotCounter() {
        int count = mPreferences.getInt(KEY_NEW_QUOTES_COUNTER_TAG, 0);
        ++count;
        mPreferences.edit().putInt(KEY_NEW_QUOTES_COUNTER_TAG, count).apply();
    }

    public void resetQuotesCounter() {
        mPreferences.edit().putInt(KEY_NEW_QUOTES_COUNTER_TAG, 0).apply();
    }

    public void setSearchFilter(String filter) {
        mPreferences.edit().putString(KEY_SEARCH_FILTER, filter).apply();
    }

    public String getSearchFilter() {
        return mPreferences.getString(KEY_SEARCH_FILTER, null);
    }
}
