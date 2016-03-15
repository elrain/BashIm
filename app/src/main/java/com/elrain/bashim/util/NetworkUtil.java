package com.elrain.bashim.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.elrain.bashim.R;

public final class NetworkUtil {

    /**
     * Check does WI-FI or mobile network enabled and connected.
     *
     * @param context  application context
     * @param listener which would notify about checking results
     * @see com.elrain.bashim.util.NetworkUtil.OnDeviceOnlineListener
     */
    public static void isDeviceOnline(@NonNull Context context, @NonNull OnDeviceOnlineListener listener) {
        try {
            ConnectivityManager cv = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cv.getActiveNetworkInfo();
            boolean isOnlyWifi = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getString(R.string.preferences_key_only_wifi), false);
            if (isOnlyWifi) {
                if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (ni.isConnectedOrConnecting()) listener.connected();
                    else listener.disconnected();
                } else listener.onlyWiFiPossible();
            } else {
                if (ni.isConnectedOrConnecting()) listener.connected();
                else listener.disconnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.disconnected();
        }
    }

    /**
     * Check does WI-FI or mobile network enabled and connected.
     *
     * @param context application context
     * @return <code>true</code> if enabled. It returns <code>false</code> in case when selected
     * use only WiFi and device has mobile connection.
     */
    public static boolean isDeviceOnline(@NonNull Context context) {
        try {
            ConnectivityManager cv = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cv.getActiveNetworkInfo();
            boolean isOnlyWifi = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(context.getString(R.string.preferences_key_only_wifi), false);
            if (isOnlyWifi)
                return ni.getType() == ConnectivityManager.TYPE_WIFI && ni.isConnectedOrConnecting();
            else return ni.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Interface for notifying of network connection checking results
     *
     * @see OnDeviceOnlineListener#connected()
     * @see OnDeviceOnlineListener#disconnected()
     * @see OnDeviceOnlineListener#onlyWiFiPossible()
     */
    public interface OnDeviceOnlineListener {
        /**
         * Called if device connected to the Internet
         */
        void connected();

        /**
         * Called if device has not connected to the Internet
         */
        void disconnected();

        /**
         * Called if mobile connection is active but in application setting was selected
         * "use only WiFi connection"
         */
        void onlyWiFiPossible();
    }

}
