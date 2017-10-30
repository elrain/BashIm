package com.elrain.bashim.service.runnablesfactory

import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import com.elrain.bashim.App
import com.elrain.bashim.AppDatabase

abstract class BaseRunnable(private val context: Context) : Runnable {

    protected val mLocalBroadcastManager: LocalBroadcastManager
            by lazy { LocalBroadcastManager.getInstance(context) }
    protected val db: AppDatabase by lazy { (context.applicationContext as App).getAppDb() }

    abstract override fun run()

    protected fun getContext() = context

}
