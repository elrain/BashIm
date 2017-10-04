package com.elrain.bashim

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.helpers.TempTableHelper
import com.elrain.bashim.service.DataLoadService

abstract class BaseActivity : AppCompatActivity() {

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                doOnReceive(intent)
            }
        }
    }

    protected abstract fun doOnReceive(intent: Intent)

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                object : IntentFilter(DataLoadService.ACTION_LOADED) {})
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
        super.onStop()
    }
}