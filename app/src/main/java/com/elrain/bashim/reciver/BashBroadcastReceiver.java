package com.elrain.bashim.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class BashBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.INTENT_DOWNLOAD)
                && NetworkUtil.isDeviceOnline(context)) {
            Log.e("onReceive", "download");
            Intent intent1 = new Intent(context, BashService.class);
            intent1.putExtra(Constants.INTENT_DOWNLOAD, true);
            context.startService(intent1);
        }
    }
}
