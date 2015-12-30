package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.BashContentProvider;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;

import de.greenrobot.event.EventBus;

public class MainFragment extends Fragment implements ServiceConnection,
        LoaderManager.LoaderCallbacks<Cursor>, BashPreferences.OnFilterChanged {

    private boolean isBound = false;
    private BashService mBashService;
    private CommonAdapter mQuotesCursorAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isFirstSynced;
    private boolean isLoadingInProcess;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_STARTED))
                    onDownloadStarted();
                else if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_FINISHED))
                    onDownloadFinished();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().startService(new Intent(getActivity(), BashService.class));
        mQuotesCursorAdapter = new CommonAdapter(getActivity());
        RecyclerView lvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        lvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (!isLoadingInProcess && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 3)) {
                    getLoaderManager().restartLoader(Constants.ID_LOADER, null, MainFragment.this);
                    isLoadingInProcess = true;
                }
            }
        });
        if (!isFirstSynced) initRssDownloading();
        getLoaderManager().initLoader(Constants.ID_LOADER, null, MainFragment.this);
    }

    private void initRssDownloading() {
        NetworkUtil.isDeviceOnline(getActivity(), new NetworkUtil.OnDeviceOnlineListener() {
            @Override
            public void connected() {
                if (isBound) mBashService.downloadXml();
                else downloadRss();
            }

            @Override
            public void disconnected() {
                DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initRssDownloading();
                    }
                }).show();
            }

            @Override
            public void onlyWiFiPossible() {
                DialogsHelper.noInternetByPreferencesDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initRssDownloading();
                    }
                }).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchHelper(getActivity()));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aRefresh:
                initRssDownloading();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadRss() {
        Intent intent = new Intent(getActivity(), BashService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void onDownloadStarted() {
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, this));
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_STARTED));
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_FINISHED));
        BashPreferences.getInstance(getActivity()).setFilterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getActivity().unbindService(this);
            isBound = false;
        }
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, this));
        getActivity().unregisterReceiver(mBroadcastReceiver);
        BashPreferences.getInstance(getActivity()).removeFilterListener();
    }

    private void onDownloadFinished() {
        if (isBound && null != getActivity()) {
            getActivity().unbindService(this);
            isBound = false;
        }
        if (null != getActivity())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getLoaderManager().restartLoader(Constants.ID_LOADER, null, MainFragment.this);
                    EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED,
                            MainFragment.this));
                }
            });
        isFirstSynced = true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BashService.LocalBinder binder = (BashService.LocalBinder) service;
        mBashService = binder.getService();
        mBashService.downloadXml();
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String filter = BashPreferences.getInstance(getActivity().getApplicationContext()).getSearchFilter();
        if (TextUtils.isEmpty(filter))
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NULL ", null,
                    QuotesTableHelper.PUB_DATE + " DESC, ROWID LIMIT " + (mQuotesCursorAdapter.getItemCount() + 10));
        else return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NULL AND "
                + QuotesTableHelper.DESCRIPTION + " LIKE '%" + filter + "%'", null,
                QuotesTableHelper.PUB_DATE + " DESC, ROWID LIMIT " + (mQuotesCursorAdapter.getItemCount() + 10));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuotesCursorAdapter.swapCursor(data);
        isLoadingInProcess = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuotesCursorAdapter.swapCursor(null);
    }

    @Override
    public void onFilterChange() {
        getLoaderManager().restartLoader(Constants.ID_LOADER, null, MainFragment.this);
    }
}
