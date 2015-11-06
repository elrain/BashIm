package com.elrain.bashim.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.elrain.bashim.activity.helper.NotificationHelper;
import com.elrain.bashim.reciver.BashBroadcastReceiver;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NewQuotesCounter;
import com.elrain.bashim.webutil.DownloadXML;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class BashService extends Service {

    private static final int THIRTY_MINUTES = 30 * 60 * 1000;
    private final IBinder mBinder = new LocalBinder();
    private DownloadListener mDownloadListener;
    private AlarmManager mAlarmMgr;

    public interface DownloadListener {
        void onDownloadStarted();

        void onDownloadFinished();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && intent.getBooleanExtra(Constants.INTENT_DOWNLOAD, false))
            downloadXml(false);
        else if (null == mAlarmMgr) {
            PendingIntent alarmPIntent;
            mAlarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, BashBroadcastReceiver.class);
            alarmIntent.setAction(Constants.INTENT_DOWNLOAD);
            alarmPIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

            mAlarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), THIRTY_MINUTES, alarmPIntent);

        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BashService getService() {
            return BashService.this;
        }
    }

    public void setListener(DownloadListener listener) {
        mDownloadListener = listener;
    }

    public void downloadXml(boolean isDialogNeeded) {
        if (isDialogNeeded && null != mDownloadListener)
            mDownloadListener.onDownloadStarted();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(r);
    }

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            DownloadXML.downloadFile(getApplicationContext());
            if (null != mDownloadListener)
                mDownloadListener.onDownloadFinished();
            else if (!BashPreferences.getInstance(getApplicationContext()).isFirstStart()
                    && NewQuotesCounter.getInstance().getCounter() != 0)
                NotificationHelper.showNotification(getApplicationContext());
        }
    };
}
