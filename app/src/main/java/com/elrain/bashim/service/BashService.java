package com.elrain.bashim.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.BuildConfig;
import com.elrain.bashim.activity.helper.NotificationHelper;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.AlarmUtil;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.XmlParser;
import com.elrain.bashim.webutil.XmlWorker;
import com.squareup.sqlbrite.BriteDatabase;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by denys.husher on 03.11.2015.
 * Service for downloading quotes and comics
 */
public class BashService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private ExecutorService mExecutor;
    @Inject
    BashPreferences mBashPreferences;
    @Inject
    AlarmUtil mAlarmUtil;
    @Inject
    BriteDatabase mDb;

    @Override
    public void onCreate() {
        super.onCreate();
        ((BashApp) getApplicationContext()).getComponent().inject(this);
        mExecutor = Executors.newFixedThreadPool(1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && intent.getBooleanExtra(Constants.INTENT_DOWNLOAD, false))
            downloadXml();
        else mAlarmUtil.setAlarm();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * This method notify that the downloading was started and run executor to download quotes and comics
     */
    public void downloadXml() {
        sendBroadcast(Constants.ACTION_DOWNLOAD_STARTED);
        mExecutor.execute(new DownloadTask());
    }

    private void sendBroadcast(String actionDownloadStarted) {
        Intent downloadStartIntent = new Intent();
        downloadStartIntent.setAction(actionDownloadStarted);
        sendBroadcast(downloadStartIntent);
    }

    /**
     * Binder class
     */
    public class LocalBinder extends Binder {
        public BashService getService() {
            return BashService.this;
        }
    }

    private class DownloadTask implements Runnable {

        private URL[] urls;

        public DownloadTask() {
            try {
                urls = new URL[]{new URL(Constants.COMMICS_RSS_URL), new URL(BuildConfig.RSS_URL)};
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            for (URL url : urls) {
                Observable<List<BashItem>> items;
                try {
                    items = XmlParser.parseXml(XmlWorker.getStream(url));
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                    sendBroadcast(Constants.ACTION_DOWNLOAD_ABORTED);
                    stopSelf();
                    break;
                }
                Date lastPubTime = QuotesTableHelper.getLastQuotePubTime(mDb);
                if (null != lastPubTime)
                    items.flatMap(Observable::from)
                            .filter(bashItem -> bashItem.getPubDate().after(lastPubTime))
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(item -> QuotesTableHelper.saveQuot(mDb, item));
                else items.flatMap(Observable::from)
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(item -> QuotesTableHelper.saveQuot(mDb, item));
            }
            if (!mBashPreferences.isFirstStart() && mBashPreferences.getQuotesCounter() != 0)
                NotificationHelper.showNotification(getApplicationContext());
            sendBroadcast(Constants.ACTION_DOWNLOAD_FINISHED);
            stopSelf();
        }
    }
}
