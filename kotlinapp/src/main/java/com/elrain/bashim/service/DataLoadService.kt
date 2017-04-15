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
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataLoadService : Service() {

    companion object {
        val ACTION_LOADED = "loaded"
        val ACTION_QUOTES_LOADED = "quotes_loaded"
        val ACTION_COMICS_LOADED = "comics_loaded"
        val ACTION_BEST_LOADED = "best_loaded"
    }

    private val TAG = DataLoadService::class.java.simpleName
    private val mBinder: LocalBinder by lazy { LocalBinder() }

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mDownloader = Runnable {
        Log.i(TAG, "loading")
        val urls: Array<String> = arrayOf(Urls.QUOTES.getUrl(), Urls.COMICS.getUrl())
        for (urlString in urls) {
            val url: URL = URL(urlString)
            val urlConnection = url.openConnection()
            urlConnection.connectTimeout = (DateUtils.SECOND_IN_MILLIS * 30).toInt()
            val bashItemsList = XmlParser.parseStream(urlConnection.inputStream).sortedBy { it.pubDate }
            QuotesTableHelper.saveNewQuotes(DBHelper.getInstance(this).writableDatabase,
                    bashItemsList)
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(object : Intent(ACTION_QUOTES_LOADED) {})
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

    enum class Urls(url: String) {
        QUOTES("http://bash.im/rss"), COMICS("http://bash.im/rss/comics.xml"), BEST("http://bash.im/best");

        private val mUrl = url

        fun getUrl(): String {
            return mUrl
        }
    }
}