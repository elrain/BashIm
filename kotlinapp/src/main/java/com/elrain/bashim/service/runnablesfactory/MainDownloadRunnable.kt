package com.elrain.bashim.service.runnablesfactory

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import com.elrain.bashim.service.DataLoadService
import com.elrain.bashim.service.loaddataitems.BaseDataLoadItem
import com.elrain.bashim.service.loaddataitems.CommicsDataLoadItem
import com.elrain.bashim.service.loaddataitems.QuoteDataLoadItem
import com.elrain.bashim.utils.parser.XmlParser
import java.net.SocketTimeoutException
import java.net.URL

class MainDownloadRunnable(context: Context) : BaseRunnable(context) {

    private val TAG = MainDownloadRunnable::class.java.simpleName
    private val mDataLoadItems by lazy { arrayOf(QuoteDataLoadItem(), CommicsDataLoadItem()) }
    private lateinit var mParser: XmlParser
    private val quotesDao by lazy{ db.quotesDao() }

    override fun run() {
        for (dataLoadItem in mDataLoadItems) {
            try {
                loadBashItems(dataLoadItem)
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "TIMEOUT", e)
            }
        }

        mLocalBroadcastManager.sendBroadcast(object : Intent(DataLoadService.ACTION_LOADED) {})
    }

    private fun loadBashItems(dataLoadItem: BaseDataLoadItem) {
        val url = URL(dataLoadItem.getUrl())
        val urlConnection = url.openConnection()
        urlConnection.connectTimeout = (30 * DateUtils.SECOND_IN_MILLIS).toInt()

        mParser = XmlParser(urlConnection.inputStream)

        val bashItemsList = mParser.parse().sortedBy { it.pubDate }
        quotesDao.saveNewQuotes(bashItemsList)

        val intent = Intent(DataLoadService.ACTION_LOADED)
        intent.putExtra(DataLoadService.EXTRA_USER_TEXT, dataLoadItem.getUserStringId())
        mLocalBroadcastManager.sendBroadcast(intent)

        Thread.sleep(1200)
    }
}