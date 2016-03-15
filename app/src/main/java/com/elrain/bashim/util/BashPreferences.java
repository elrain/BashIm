package com.elrain.bashim.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by denys.husher on 04.11.2015.
 * Application preferences manager
 */
public class BashPreferences {

    private static final String BASH_SHARED_PREF = "BashSharedPref";
    private static final String KEY_FIRST_START = "firstStart";
    private static final String KEY_LAST_TAG = "lastTag";
    private static final String KEY_NEW_QUOTES_COUNTER_TAG = "newQuotes";
    private static final String KEY_SEARCH_FILTER = "searchFilter";
    private static BashPreferences mInstance;
    private static SharedPreferences mPreferences;
    private OnFilterChanged mFilterListener;

    private BashPreferences(Context context) {
        mPreferences = context.getSharedPreferences(BASH_SHARED_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Notify if search string was changed. If it occurs {@link OnFilterChanged#onFilterChange} will be called.
     * <p>Also to be notified you need to set this listener by calling {@link #setFilterListener(OnFilterChanged)}</p>
     */
    public interface OnFilterChanged {
        void onFilterChange();
    }

    /**
     * Setter for search string change listener.
     *
     * @param listener {@link com.elrain.bashim.util.BashPreferences.OnFilterChanged}
     */
    public void setFilterListener(OnFilterChanged listener) {
        mFilterListener = listener;
    }

    /**
     * Remove search string change listener.
     *
     * @see com.elrain.bashim.util.BashPreferences.OnFilterChanged
     */
    public void removeFilterListener() {
        mFilterListener = null;
    }

    /**
     * Return instance of {@link BashPreferences} class.
     *
     * @param context application context
     * @return {@link BashPreferences}
     * @see Context
     */
    public static BashPreferences getInstance(@NonNull Context context) {
        if (null == mInstance) mInstance = new BashPreferences(context);
        return mInstance;
    }

    /**
     * @return <code>true</code> on first call and after it will change value to <code>false</code>
     * if {@link BashPreferences#getQuotesCounter()} won't return zero
     */
    public boolean isFirstStart() {
        boolean result = mPreferences.getBoolean(KEY_FIRST_START, true);
        if (result) {
            if (getQuotesCounter() != 0)
                resetQuotesCounter();
            mPreferences.edit().putBoolean(KEY_FIRST_START, false).apply();
        }
        return result;
    }

    /**
     * Reset key {@link BashPreferences#KEY_FIRST_START} to true
     */
    public void resetFirstStart() {
        mPreferences.edit().putBoolean(KEY_FIRST_START, true).apply();
    }

    /**
     * Return tag of last opened fragment.
     * @return last fragment tag or <code>null</code> if it wasn't set before
     */
    public String getLastTag() {
        return mPreferences.getString(KEY_LAST_TAG, null);
    }

    /**
     * Save last opened fragment tag to shared preferences
     * @param lastTag last fragment tag
     */
    public void setLastTag(String lastTag) {
        mPreferences.edit().putString(KEY_LAST_TAG, lastTag).apply();
    }

    /**
     * Return quantity of new quotes which user didn't see
     * @return quantity of new quotes which user didn't see
     */
    public int getQuotesCounter() {
        return mPreferences.getInt(KEY_NEW_QUOTES_COUNTER_TAG, 0);
    }

    /**
     * Increase by 1 quantity of new quotes which user didn't see
     */
    public void increaseQuotCounter() {
        int count = mPreferences.getInt(KEY_NEW_QUOTES_COUNTER_TAG, 0);
        ++count;
        mPreferences.edit().putInt(KEY_NEW_QUOTES_COUNTER_TAG, count).apply();
    }

    /**
     * Set quantity of new quotes which user didn't see to zero
     */
    public void resetQuotesCounter() {
        mPreferences.edit().putInt(KEY_NEW_QUOTES_COUNTER_TAG, 0).apply();
    }

    /**
     * Setter for search string. If {@link com.elrain.bashim.util.BashPreferences.OnFilterChanged}
     * not null will call {@link OnFilterChanged#onFilterChange()}
     * @param filter search string
     */
    public void setSearchFilter(String filter) {
        mPreferences.edit().putString(KEY_SEARCH_FILTER, filter).apply();
        if (null != mFilterListener) mFilterListener.onFilterChange();
    }

    /**
     * Return search string
     * @return search string or null if it wasn't set before
     */
    public String getSearchFilter() {
        return mPreferences.getString(KEY_SEARCH_FILTER, null);
    }
}
