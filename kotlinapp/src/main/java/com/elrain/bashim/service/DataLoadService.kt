package com.elrain.bashim.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.text.format.DateUtils
import android.util.Log
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.helpers.QuotesTableHelper
import com.elrain.bashim.utils.parser.XmlParser
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataLoadService : Service() {

    companion object {
        val ACTION_LOADED = "loaded"
    }

    private val TAG = DataLoadService::class.java.simpleName
    private val mBinder: LocalBinder by lazy { LocalBinder() }

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mDownloader = Runnable {
        Log.i(TAG, "loading")
        val urlsTypes: Array<Urls> = arrayOf(Urls.QUOTES, Urls.COMICS)
        for (urlType in urlsTypes) {
            val url: URL = URL(urlType.getUrl())
            val urlConnection = url.openConnection()
            try {
                urlConnection.connectTimeout = (30 * DateUtils.SECOND_IN_MILLIS).toInt()
                val bashItemsList = XmlParser.parseStream(urlConnection.inputStream).sortedBy { it.pubDate }
                QuotesTableHelper.saveNewQuotes(DBHelper.getInstance(this).writableDatabase,
                        bashItemsList)
                LocalBroadcastManager.getInstance(this)
                        .sendBroadcast(object : Intent(urlType.getAction()) {})
                Thread.sleep(1500)
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "TIMEOUT", e)
            }
        }

        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(object : Intent(ACTION_LOADED) {})
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mExecutor.execute(mDownloader)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    inner class LocalBinder : Binder() {

        val service: DataLoadService
            get() = this@DataLoadService
    }

    enum class Urls(url: Array<String>) {
        QUOTES(arrayOf("http://bash.im/rss", "quotes_loaded")),
        COMICS(arrayOf("http://bash.im/rss/comics.xml", "comics_loaded")),
        BEST(arrayOf("http://bash.im/best", "best_loaded"));

        private val mData = url

        fun getUrl(): String {
            return mData[0]
        }

        fun getAction(): String {
            return mData[1]
        }
    }
}