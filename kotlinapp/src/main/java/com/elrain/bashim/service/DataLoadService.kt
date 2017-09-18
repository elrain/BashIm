package com.elrain.bashim.service

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.text.format.DateUtils
import android.util.Log
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.helpers.QuotesTableHelper
import com.elrain.bashim.service.loaddataitems.BaseDataLoadItem
import com.elrain.bashim.service.loaddataitems.CommicsDataLoadItem
import com.elrain.bashim.service.loaddataitems.QuoteDataLoadItem
import com.elrain.bashim.utils.parser.XmlParser
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataLoadService : IntentService("DownloadQuotes") {

    companion object {
        val ACTION_LOADED = "loaded"
        val EXTRA_USER_TEXT = "userText"
    }

    private val TAG = DataLoadService::class.java.simpleName

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mDownloader = Runnable {
        val dataLoadItems: Array<BaseDataLoadItem> = arrayOf(QuoteDataLoadItem(), CommicsDataLoadItem())
        val lbm = LocalBroadcastManager.getInstance(this)

        for (dataLoadItem in dataLoadItems) {
            val url = URL(dataLoadItem.getUrl())
            val urlConnection = url.openConnection()
            try {
                urlConnection.connectTimeout = (30 * DateUtils.SECOND_IN_MILLIS).toInt()
                val bashItemsList = XmlParser.parseStream(urlConnection.inputStream).sortedBy { it.pubDate }
                QuotesTableHelper.saveNewQuotes(DBHelper.getInstance(this).writableDatabase,
                        bashItemsList)

                val intent = Intent(ACTION_LOADED)
                intent.putExtra(EXTRA_USER_TEXT, dataLoadItem.getUserStringId())
                lbm.sendBroadcast(intent)

                Thread.sleep(1200)
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "TIMEOUT", e)
            }
        }

        lbm.sendBroadcast(object : Intent(ACTION_LOADED) {})
    }

    override fun onHandleIntent(intent: Intent?) {
        mExecutor.execute(mDownloader)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}