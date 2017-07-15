package com.elrain.bashim.activities

import android.app.LoaderManager
import android.content.Context
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.elrain.bashim.R
import com.elrain.bashim.adapter.ItemsAdapter
import com.elrain.bashim.dal.DBHelper
import com.elrain.bashim.dal.ItemsLoader
import com.elrain.bashim.dal.helpers.BashItemType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private val mRvQuotes: RecyclerView by lazy { rvQuotes }
    private val mDrawer: DrawerLayout by lazy { drawer_layout }
    private var mAdapter: ItemsAdapter? = null
    private var mLastSelected: Int = BashItemType.QUOTE.getId()

    companion object {
        fun launch(context: Context) {
            context.startActivity(object : Intent(context, MainActivity::class.java) {})
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolBarAndDrawer()

        mAdapter = ItemsAdapter(this, null)

        loaderManager.initLoader(mLastSelected, null, this)

        mRvQuotes.layoutManager = LinearLayoutManager(this)
        mRvQuotes.adapter = mAdapter
    }

    private fun initToolBarAndDrawer() {
        val toolbar = toolbar
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = nav_view
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_quotes) {
            mLastSelected = BashItemType.QUOTE.getId()
            loaderManager.restartLoader(mLastSelected, null, this)
        } else if (id == R.id.nav_comics) {
            mLastSelected = BashItemType.COMICS.getId()
            loaderManager.restartLoader(mLastSelected, null, this)
        }
        mDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        return ItemsLoader(this, DBHelper.getInstance(this))
    }

    override fun onLoadFinished(p0: Loader<Cursor>?, cursor: Cursor?) {
        if (cursor != null) {
            mRvQuotes.layoutManager.scrollToPosition(0)
            mAdapter?.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter?.swapCursor(null)
    }
}
