package com.elrain.bashim.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

class NetworkUtils {
    companion object {
        private val TAG = NetworkUtils::class.java.simpleName

        fun isNetworkAvailable(context: Context, available: () -> Unit,
                               onFail: () -> Unit = {
                                   Log.e(TAG, "No internet connection")
                               }) {
            val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val ni = cm.activeNetworkInfo
            if (ni == null) {
                onFail.invoke()
            } else {
                when (ni.type) {
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_WIFI -> {
                        if (ni.isConnectedOrConnecting) {
                            available.invoke()
                        } else {
                            onFail.invoke()
                        }
                    }
                    else -> onFail.invoke()
                }
            }
        }
    }
}