package com.elrain.bashim.service.runnablesfactory

import android.content.Context
import android.content.Intent
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.helpers.QuotesTableHelper
import com.elrain.bashim.service.DataLoadService
import com.elrain.bashim.utils.parser.HtmlParser
import org.jsoup.Jsoup

class OtherDownloadRunnable(context: Context) : BaseRunnable(context) {

    override fun run() {
        val document = Jsoup.connect("http://bash.im/random").get()
        val bashItems = HtmlParser(document).parse()
        QuotesTableHelper.deleteOldAndSaveNewTempQuotes(DBHelper.getInstance(getContext()).writableDatabase,
                bashItems)

        val intent = Intent(DataLoadService.ACTION_LOADED)
        mLocalBroadcastManager.sendBroadcast(intent)
    }
}