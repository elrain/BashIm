package com.elrain.bashim.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elrain.bashim.util.BashPreferences;

/**
 * Created by denys.husher on 16.12.2015.
 */
public class CancelNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        BashPreferences.getInstance(context).resetQuotesCounter();
    }
}
