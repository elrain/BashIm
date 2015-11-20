package com.elrain.bashim.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class NetworkUtil {
    /**
     * Check does WI-FI or mobile network enabled and connected.
     *
     * @param context application context
     * @return <code>true</code> if enabled.
     */

    public static boolean isDeviceOnline(@NonNull Context context) {
        boolean status = false;
        try {
            ConnectivityManager cv = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cv.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                networkInfo = cv.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
