package com.elrain.bashim.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.elrain.bashim.BaseActivity
import com.elrain.bashim.R
import com.elrain.bashim.main.MainActivity
import com.elrain.bashim.service.DataLoadService
import com.elrain.bashim.service.runnablesfactory.DownloadRunnableFactory
import com.elrain.bashim.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.view.*

class SplashActivity : BaseActivity() {

    private val TAG = SplashActivity::class.java.simpleName
    private val tvDownloadStatus by lazy { activity_splash.tvDownloadStatus }

    override fun doOnReceive(intent: Intent) {
        val userStringId =
                intent.getIntExtra(DataLoadService.EXTRA_USER_TEXT, R.string.splash_updated)

        changeTextForUser(userStringId)

        if (userStringId == R.string.splash_updated) {
            Handler().postDelayed({
                launchMain()
            }, 1200)
        }
    }

    private fun changeTextForUser(textId: Int) {
        runOnUiThread {
            tvDownloadStatus.text = resources.getString(textId)
        }
    }

    private fun launchMain() {
        MainActivity.launch(this@SplashActivity)
        this@SplashActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        NetworkUtils.isNetworkAvailable(this, availableDoNext = {
            val serviceIntent = Intent(this, DataLoadService::class.java)
            serviceIntent.putExtra(DataLoadService.EXTRA_WHAT_TO_LOAD,
                    DownloadRunnableFactory.DownloadRunnableTypes.MAIN)
            startService(serviceIntent)
        }, noInternetDoNext = {
            Log.i(TAG, "No Internet connection::Redirecting on main")
            launchMain()
        })
    }
}
