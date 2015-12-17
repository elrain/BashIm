package com.elrain.bashim.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.elrain.bashim.activity.helper.NotificationHelper;
import com.elrain.bashim.util.AlarmUtil;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.webutil.XmlWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class BashService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private ExecutorService executor;

    @Override
    public void onCreate() {
        super.onCreate();
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && intent.getBooleanExtra(Constants.INTENT_DOWNLOAD, false))
            downloadXml();
        else AlarmUtil.getInstance(getApplicationContext()).setAlarm();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void downloadXml() {
        sendBroadcast(Constants.ACTION_DOWNLOAD_STARTED);
        executor.execute(new DownloadTask());
    }

    private void sendBroadcast(String actionDownloadStarted) {
        Intent downloadStartIntent = new Intent();
        downloadStartIntent.setAction(actionDownloadStarted);
        sendBroadcast(downloadStartIntent);
    }

    public class LocalBinder extends Binder {
        public BashService getService() {
            return BashService.this;
        }
    }

    private class DownloadTask implements Runnable {
        @Override
        public void run() {
            XmlWorker.getStreamAndParse(getApplicationContext());
            if (!BashPreferences.getInstance(getApplicationContext()).isFirstStart()
                    && BashPreferences.getInstance(getApplicationContext()).getQuotesCounter() != 0)
                NotificationHelper.showNotification(getApplicationContext());
            sendBroadcast(Constants.ACTION_DOWNLOAD_FINISHED);
            stopSelf();
        }
    }
}
