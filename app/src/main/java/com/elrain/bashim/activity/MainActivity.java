package com.elrain.bashim.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.elrain.bashim.R;
import com.elrain.bashim.fragment.CommicsFragment;
import com.elrain.bashim.fragment.FavoriteFragment;
import com.elrain.bashim.fragment.MainFragment;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_MAIN = "main";
    private static final String TAG_COMICS = "comics";
    public static final String KEY_LAST_TAG = "lastTag";

    private String mLastTag;
    private HashMap<String, Fragment> mFragmentMap;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        initActionBar();
        initFragmentMap();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        mFragmentManager = getFragmentManager();
        if (null == savedInstanceState)
            changeFragment(TAG_MAIN);
        else {
            mLastTag = savedInstanceState.getString(KEY_LAST_TAG);
            changeFragment(mLastTag);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(KEY_LAST_TAG, mLastTag);
    }

    private void initFragmentMap() {
        mFragmentMap = new HashMap<>();
        mFragmentMap.put(TAG_FAVORITE, new FavoriteFragment());
        mFragmentMap.put(TAG_MAIN, new MainFragment());
        mFragmentMap.put(TAG_COMICS, new CommicsFragment());
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.aMain) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.fragment_main);
            changeFragment(TAG_MAIN);
        } else if (id == R.id.aFavorite) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.fragment_favorite);
            changeFragment(TAG_FAVORITE);
        } else if (id == R.id.aComics) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.fragment_comics);
            changeFragment(TAG_COMICS);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(@NonNull String tag) {
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.flContent);
        Fragment newFragment = mFragmentMap.get(tag);
        if (newFragment != currentFragment) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (null != newFragment) {
                if (null != currentFragment)
                    ft.detach(currentFragment);
                if (null == mFragmentManager.findFragmentByTag(tag))
                    ft.add(R.id.flContent, newFragment, tag);
                else
                    ft.attach(mFragmentManager.findFragmentByTag(tag));
            }
            ft.addToBackStack(tag);
            ft.commit();
            mLastTag = tag;
        }
    }
}
