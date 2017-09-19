package com.elrain.bashim.service.runablesfactory

import android.content.Context
import android.support.v4.content.LocalBroadcastManager

abstract class BaseRunnable(private val context: Context) : Runnable {

    protected val mLocalBroadcastManager: LocalBroadcastManager
            by lazy { LocalBroadcastManager.getInstance(context) }

    abstract override fun run()

    protected fun getContext() = context

}
