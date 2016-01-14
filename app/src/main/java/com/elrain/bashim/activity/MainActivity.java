package com.elrain.bashim.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.elrain.bashim.R;
import com.elrain.bashim.fragment.BestRandomFragment;
import com.elrain.bashim.fragment.ComicsFragment;
import com.elrain.bashim.fragment.FavoriteFragment;
import com.elrain.bashim.fragment.MainFragment;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.util.AlarmUtil;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.ScreenUtil;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private String mLastTag;
    private HashMap<String, Fragment> mFragmentMap;
    private FragmentManager mFragmentManager;
    private boolean isTablet = false;
    private DrawerLayout drawer;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BashPreferences.getInstance(this).setSearchFilter(null);
//        Fabric.with(this, new Crashlytics());
//        Fabric.with(this, new Answers(), new Crashlytics());
        if (!ScreenUtil.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        if (null != savedInstanceState && ScreenUtil.isTablet(this))
            mLastTag = BashPreferences.getInstance(this).getLastTag();
        else mLastTag = getString(R.string.action_main);
        EventBus.getDefault().register(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        if (null != findViewById(R.id.vDivider))
            isTablet = true;
        mFragmentManager = getFragmentManager();
        initFragmentMap();
        initActionBar();
        if (null != savedInstanceState && ScreenUtil.isTablet(this))
            changeFragment(null == mLastTag ? getString(R.string.action_main) : mLastTag, null);
        else changeFragment(getString(R.string.action_main), null);
        setActionBarTitle();
        if (getIntent().getBooleanExtra(Constants.KEY_OPEN_MAIN_ACTIVITY, false))
            BashPreferences.getInstance(this).resetQuotesCounter();
    }

    private void setActionBarTitle() {
        if (null != getSupportActionBar())
            if (getString(R.string.action_comics).equals(mLastTag))
                getSupportActionBar().setTitle(R.string.action_comics);
            else if (getString(R.string.action_favorite).equals(mLastTag))
                getSupportActionBar().setTitle(R.string.action_favorite);
            else if (getString(R.string.action_random).equals(mLastTag))
                getSupportActionBar().setTitle(R.string.action_random);
            else if (getString(R.string.action_best).equals(mLastTag))
                getSupportActionBar().setTitle(R.string.action_best);
            else if (getString(R.string.action_main).equals(mLastTag))
                getSupportActionBar().setTitle(R.string.action_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmUtil.getInstance(this).unsubscribeListener();
        EventBus.getDefault().unregister(this);
        BashPreferences.getInstance(this).setSearchFilter(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTablet) drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void initFragmentMap() {
        mFragmentMap = new HashMap<>();
        mFragmentMap.put(getString(R.string.action_favorite), new FavoriteFragment());
        mFragmentMap.put(getString(R.string.action_main), new MainFragment());
        mFragmentMap.put(getString(R.string.action_comics), new ComicsFragment());
        mFragmentMap.put(getString(R.string.action_random), new BestRandomFragment());
        mFragmentMap.put(getString(R.string.action_best), new BestRandomFragment());
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(getSelectedItem()).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!isTablet) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        } else drawer.setDrawerListener(null);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START) && !isTablet) {
            drawer.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.aMain) {
            if (null != getSupportActionBar()) getSupportActionBar().setTitle(R.string.action_main);
            changeFragment(getString(R.string.action_main), null);
        } else if (id == R.id.aFavorite) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.action_favorite);
            changeFragment(getString(R.string.action_favorite), null);
        } else if (id == R.id.aComics) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.action_comics);
            changeFragment(getString(R.string.action_comics), null);
        } else if (id == R.id.aPreferences) {
            Intent in = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(in);
        } else if (id == R.id.aRandom) {
            if (null != getSupportActionBar())
                getSupportActionBar().setTitle(R.string.action_random);
            Bundle b = new Bundle();
            b.putString(Constants.PARSE, Constants.RANDOM_URL);
            changeFragment(getString(R.string.action_random), b);
        } else if (id == R.id.aBest) {
            if (null != getSupportActionBar()) getSupportActionBar().setTitle(R.string.action_best);
            Bundle b = new Bundle();
            b.putString(Constants.PARSE, Constants.BEST_URL);
            changeFragment(getString(R.string.action_best), b);
        }
        BashPreferences.getInstance(this).setSearchFilter(null);
        if (!isTablet) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(@NonNull String tag, Bundle bundle) {
        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.flContent);
        Fragment newFragment = mFragmentMap.get(tag);
        if (null != bundle) {
            if (null == newFragment.getArguments())
                newFragment.setArguments(bundle);
            else
                newFragment.getArguments().putAll(bundle);
        }
        if (newFragment != currentFragment) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (null != newFragment) {
                if (null != currentFragment) ft.detach(currentFragment);
                if (null == mFragmentManager.findFragmentByTag(tag))
                    ft.add(R.id.flContent, newFragment, tag);
                else ft.attach(mFragmentManager.findFragmentByTag(tag));
            }
            ft.commit();
            BashPreferences.getInstance(this).setLastTag(tag);
            mLastTag = tag;
        }
    }

    private int getSelectedItem() {
        if (null == mLastTag || getString(R.string.action_main).equals(mLastTag)) return 0;
        else if (getString(R.string.action_random).equals(mLastTag)) return 1;
        else if (getString(R.string.action_best).equals(mLastTag)) return 2;
        else if (getString(R.string.action_comics).equals(mLastTag)) return 3;
        else if (getString(R.string.action_favorite).equals(mLastTag)) return 4;
        else return 0;
    }

    @Override
    public void onRefresh() {
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, null));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(RefreshMessage message) {
        if (message.mState == RefreshMessage.State.FINISHED && null != message.mFrom) {
            mSwipeRefreshLayout.setRefreshing(false);
        } else if (message.mState == RefreshMessage.State.STARTED && null != message.mFrom) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
}
