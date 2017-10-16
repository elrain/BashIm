package com.elrain.bashim.main

import android.app.LoaderManager
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.MenuItem
import android.view.View
import com.elrain.bashim.BaseActivity
import com.elrain.bashim.R
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.ItemsLoader
import com.elrain.bashim.dal.helpers.BashItemType
import com.elrain.bashim.dal.helpers.TempTableHelper
import com.elrain.bashim.dao.BashItem
import com.elrain.bashim.main.adapter.BaseAdapter
import com.elrain.bashim.main.adapter.ItemsAdapter
import com.elrain.bashim.service.DataLoadService
import com.elrain.bashim.service.runnablesfactory.DownloadRunnableFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>, BaseAdapter.OnItemAction {

    private var mAdapter: ItemsAdapter? = null
    private var mLastSelected: Int = BashItemType.QUOTE.getId()

    companion object {
        fun launch(context: Context) {
            context.startActivity(object : Intent(context, MainActivity::class.java) {})
        }
    }

    override fun doOnReceive(intent: Intent) {
        splashUpdating.visibility = View.GONE
        rvQuotes.visibility = View.VISIBLE
        restartLoaderWithNewType(BashItemType.OTHER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBarAndDrawer()

        mAdapter = ItemsAdapter(this, null)

        loaderManager.initLoader(mLastSelected, null, this)

        rvQuotes.layoutManager = LinearLayoutManager(this)
        rvQuotes.adapter = mAdapter
    }

    private fun initToolBarAndDrawer() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            R.id.nav_quotes -> restartLoaderWithNewType(BashItemType.QUOTE)
            R.id.nav_comics -> restartLoaderWithNewType(BashItemType.COMICS)
            R.id.nav_random -> {
                splashUpdating.visibility = View.VISIBLE
                rvQuotes.visibility = View.GONE
                val intent = Intent(this, DataLoadService::class.java)
                intent.putExtra(DataLoadService.EXTRA_WHAT_TO_LOAD,
                        DownloadRunnableFactory.DownloadRunnableTypes.OTHER)
                startService(intent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun restartLoaderWithNewType(type: BashItemType) {
        mLastSelected = type.getId()
        loaderManager.restartLoader(mLastSelected, null, this)
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> =
            ItemsLoader(this, DBHelper.getInstance(this))

    override fun onLoadFinished(p0: Loader<Cursor>?, cursor: Cursor?) {
        if (cursor != null) {
            rvQuotes.layoutManager.scrollToPosition(0)
            mAdapter?.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter?.swapCursor(null)
    }

    override fun openInTab(url: String) {
        val intentOnCustomTabBuilder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        intentOnCustomTabBuilder.setToolbarColor(resources.getColor(R.color.colorPrimary))
        intentOnCustomTabBuilder.build().launchUrl(this, Uri.parse(url))
    }

    override fun shareItem(item: BashItem) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val text = Html.fromHtml("${item.description} <br/><br/>")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "$text ${item.link}")
        startActivity(shareIntent)
    }

}
