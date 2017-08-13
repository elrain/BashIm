package com.elrain.bashim.splash

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.elrain.bashim.R
import com.elrain.bashim.main.MainActivity
import com.elrain.bashim.service.DataLoadService
import com.elrain.bashim.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_splash.view.*

class SplashActivity : AppCompatActivity(), ServiceConnection {

    private val TAG = SplashActivity::class.java.simpleName
    private var mIsBound = false
    private var mService: DataLoadService? = null
    private val tvDownloadStatus by lazy { activity_splash.tvDownloadStatus }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val action = intent.action
                if (action == DataLoadService.ACTION_LOADED) {
                    runOnUiThread {
                        tvDownloadStatus.text = resources.getString(R.string.splash_updated)
                    }
                    Handler().postDelayed({
                        launchMain()
                    }, 2000)
                } else {
                    val urlsType: Array<DataLoadService.Urls> =
                            arrayOf(DataLoadService.Urls.QUOTES, DataLoadService.Urls.COMICS)

                    urlsType.map { it.getAction() }.filter { it == action }.first {
                        var text = ""
                        if (it == DataLoadService.Urls.QUOTES.getAction()) {
                            text = getString(R.string.splash_quotes_downloaded)
                        } else if (it == DataLoadService.Urls.COMICS.getAction()) {
                            text = getString(R.string.splash_commics_downloaded)
                        }
                        runOnUiThread { tvDownloadStatus.text = text }
                        return
                    }
                }
            }
        }
    }

    private fun launchMain() {
        MainActivity.launch(this@SplashActivity)
        this@SplashActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        NetworkUtils.isNetworkAvailable(this, available = {
            startService(object : Intent(this, DataLoadService::class.java) {})
        }, onFail = {
            Log.e(TAG, "No Internet connection::Redirecting on main")
            launchMain()
        })
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                object : IntentFilter(DataLoadService.ACTION_LOADED) {})

        val urlsType: Array<DataLoadService.Urls> =
                arrayOf(DataLoadService.Urls.QUOTES, DataLoadService.Urls.COMICS)
        urlsType.map { it.getAction() }.forEach {
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                    object : IntentFilter(it) {})
        }

        bindService(object : Intent(this, DataLoadService::class.java) {},
                this, Context.BIND_AUTO_CREATE)

    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
        unbindService(this)
        super.onStop()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder: DataLoadService.LocalBinder = service as DataLoadService.LocalBinder
        mService = binder.service
        mIsBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        if (mIsBound) {
            mService = null
        }
    }
}
