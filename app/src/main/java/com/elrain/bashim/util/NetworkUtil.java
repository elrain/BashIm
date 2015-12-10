package com.elrain.bashim.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.elrain.bashim.R;

/**
 * Created by denys.husher on 03.11.2015.
 */
public final class NetworkUtil {
    /**
     * Check does WI-FI or mobile network enabled and connected.
     *
     * @param context application context
     * @return <code>true</code> if enabled.
     */

    public static boolean isDeviceOnline(@NonNull Context context) {
        try {
            ConnectivityManager cv = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cv.getActiveNetworkInfo();
            boolean isOnlyWifi = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getString(R.string.preferences_key_only_wifi), false);
            if (isOnlyWifi && ni.getType() == ConnectivityManager.TYPE_WIFI)
                return ni.isConnectedOrConnecting();
            else return ni.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
