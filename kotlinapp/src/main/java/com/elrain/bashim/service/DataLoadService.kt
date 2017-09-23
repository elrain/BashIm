package com.elrain.bashim.service

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import com.elrain.bashim.service.runnablesfactory.DownloadRunnableFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataLoadService : IntentService("DownloadQuotes") {

    companion object {
        val ACTION_LOADED = "loaded"
        val EXTRA_USER_TEXT = "userText"
        val EXTRA_WHAT_TO_LOAD = "whatToLoad"
    }

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    override fun onHandleIntent(intent: Intent?) {
        val type = intent?.getSerializableExtra(EXTRA_WHAT_TO_LOAD)
                as DownloadRunnableFactory.DownloadRunnableTypes
        mExecutor.execute(DownloadRunnableFactory(this)
                .getRunnable(type))
    }

    override fun onBind(intent: Intent?): IBinder? = null
}