package com.elrain.bashim.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.elrain.bashim.R;
import com.elrain.bashim.receiver.BashBroadcastReceiver;

/**
 * Created by denys.husher on 17.11.2015.
 * This class provides access to the alarm. These allow you
 * to change frequency of quotes update.
 */
public final class AlarmUtil implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static AlarmUtil mInstance;
    private final AlarmManager mAlarmManager;
    private final PendingIntent mAlarmPIntent;
    private final PendingIntent mCancelPIntent;
    private final SharedPreferences mSharedPref;
    private final Context mContext;

    private AlarmUtil(Context context) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPref.registerOnSharedPreferenceChangeListener(this);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, BashBroadcastReceiver.class);
        alarmIntent.setAction(Constants.INTENT_DOWNLOAD);
        mAlarmPIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        mCancelPIntent = PendingIntent.getBroadcast(context, 0, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Return Instance of AlarmUtil class
     * @param context application context
     * @return instance of AlarmUtil
     */
    public static AlarmUtil getInstance(Context context) {
        if (null == mInstance)
            mInstance = new AlarmUtil(context);
        return mInstance;
    }

    /**
     * Set inexact repeating of alarm manager for checking of new quotes.
     *
     * @param repeatTime amount of time interval between checking.
     *                   Pass 0 to use default frequency in 30 minutes
     */
    private void setAlarm(int repeatTime) {
        if (repeatTime == 0 || repeatTime == 1800000) {
            cancelAlarm();
            mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HALF_HOUR, mAlarmPIntent);
        } else {
            cancelAlarm();
            mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), repeatTime, mAlarmPIntent);
        }
    }

    /**
     * This method allows you to setup alarm manager with inexact repeating
     *
     * @see AlarmManager#setInexactRepeating(int, long, long, PendingIntent)
     */
    public void setAlarm() {
        if (!"0".equals(mSharedPref.getString(
                mContext.getString(R.string.preferences_key_alarm_frequency), null)))
            mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HALF_HOUR, mAlarmPIntent);
    }

    private void cancelAlarm() {
        mAlarmManager.cancel(mCancelPIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mContext.getString(R.string.preferences_key_alarm_frequency).equals(key)) {
            String repeatTime = sharedPreferences.getString(key, Constants.PREFERENCES_UPDATE_DEF_VALUE);
            if ("0".equals(repeatTime)) cancelAlarm();
            else setAlarm(Integer.parseInt(repeatTime));
        }
    }

    /**
     * Unsubscribe listener on changing preferences
     */
    public void unsubscribeListener() {
        if (null != mSharedPref) mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }
}
