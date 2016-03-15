package com.elrain.bashim.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;

/**
 * Created by denys.husher on 04.11.2015.
 * This receiver will receive download intent({@link Constants#INTENT_DOWNLOAD}) and notify service to download new quotes
 */
public class BashBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.INTENT_DOWNLOAD.equals(intent.getAction())
                && NetworkUtil.isDeviceOnline(context)) {
            Intent downloadIntent = new Intent(context, BashService.class);
            downloadIntent.putExtra(Constants.INTENT_DOWNLOAD, true);
            context.startService(downloadIntent);
        } else if (Constants.INTENT_CANCEL.equals(intent.getAction()))
            BashPreferences.getInstance(context).resetQuotesCounter();
    }
}
