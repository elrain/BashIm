package com.elrain.bashim.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

class NetworkUtils {
    companion object {
        private val TAG = NetworkUtils::class.java.simpleName

        fun isNetworkAvailable(context: Context, availableDoNext: () -> Unit,
                               noInternetDoNext: () -> Unit = {
                                   Log.e(TAG, "No internet connection")
                               }) {
            val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val ni = cm.activeNetworkInfo
            if (ni == null) {
                noInternetDoNext.invoke()
            } else {
                when (ni.type) {
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_WIFI -> {
                        if (ni.isConnectedOrConnecting) {
                            availableDoNext.invoke()
                        } else {
                            noInternetDoNext.invoke()
                        }
                    }
                    else -> noInternetDoNext.invoke()
                }
            }
        }
    }
}